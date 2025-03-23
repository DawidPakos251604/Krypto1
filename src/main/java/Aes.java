public class Aes {

    byte[][] keyWords;
    byte[][] keyWordsReversed;
    byte[] entranceKey;
    int keySize;
    int rounds;

    public Aes(byte[] originalKey) throws Exception {
        if (originalKey.length != 16 && originalKey.length != 24 && originalKey.length != 32) {
            throw new Exception("Key has wrong length! Supported lengths: 16, 24, 32 bytes (128, 192, 256 bits)");
        }
        this.entranceKey = originalKey;
        this.keySize = originalKey.length;
        this.rounds = (keySize == 16) ? 10 : (keySize == 24) ? 12 : 14;
        this.keyWords = generateSubKeys(entranceKey);
        this.keyWordsReversed = generateReversedSubKeys(keyWords);
    }

    public byte[] encode(byte[] message) {

        int wholeBlocksCount = message.length / 16;
        int charactersToEncodeCount;
        if (wholeBlocksCount == 0) {
            charactersToEncodeCount = 16;
        } else if (message.length % 16 != 0) {
            charactersToEncodeCount = (wholeBlocksCount + 1) * 16;
        } else {
            charactersToEncodeCount = wholeBlocksCount * 16;
        }

        byte[] result = new byte[charactersToEncodeCount];
        byte[] temp = new byte[charactersToEncodeCount];
        byte[] blok = new byte[16];


        // rewrite message to temporary array + append 0s
        for (int i = 0; i < charactersToEncodeCount; ++i) {
            if (i < message.length) {
                temp[i] = message[i];
            } else {
                temp[i] = 0;
            }
        }

        // construct output array...
        int i = 0;
        while (i < temp.length) {
            for (int j = 0; j < 16; ++j) {
                blok[j] = temp[i++];
            }

            blok = this.encrypt(blok);
            System.arraycopy(blok, 0, result, i - 16, blok.length);
        }

        return result;
    }

    public byte[] decode(byte[] message) {
        if (message.length % 16 != 0) {
            return null;
        }

        int blocksCount = message.length / 16;
        byte[][] dataAsBlocks = new byte[blocksCount][16];

        // load data as blocks:

        int i = 0;
        for (int block = 0; block < blocksCount; block++) {
            for (int b = 0; b < 16; b++) {
                dataAsBlocks[block][b] = message[i];
                i++;
            }
        }


        i = 0;

        byte[] tmp = new byte[message.length];
        for (int block = 0; block < blocksCount; block++) {
            for (int b = 0; b < 16; b++) {
                tmp[i] = decrypt(dataAsBlocks[block])[b];
                i++;
            }
        }

        // count trailing zeros in tmp...
        int zeros = 0;
        for (int j = 0; j < 16; j++) {
            if (tmp[tmp.length - (j + 1)] == '\0') {
                zeros++;
            } else {
                break;
            }
        }

        byte[] output = new byte[blocksCount * 16 - zeros];
        System.arraycopy(tmp, 0, output, 0, blocksCount * 16 - zeros);


        return output;
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] state = plaintext.clone();
        state = addRoundKey(state, 0);
        for (int round = 1; round < rounds; round++) {
            state = subBytes(state);
            state = shiftRows(state);
            state = mixColumns(state);
            state = addRoundKey(state, round);
        }
        state = subBytes(state);
        state = shiftRows(state);
        state = addRoundKey(state, rounds);
        return state;
    }

    public byte[] decrypt(byte[] ciphertext) {
        byte[] state = ciphertext.clone();
        state = addRoundKey(state, rounds);
        state = shiftRowsReversed(state);
        state = subBytesReversed(state);
        for (int round = rounds - 1; round > 0; round--) {
            state = addRoundKey(state, round);
            state = mixColumnsReversed(state);
            state = shiftRowsReversed(state);
            state = subBytesReversed(state);
        }
        state = addRoundKey(state, 0);
        return state;
    }

    public byte[][] generateSubKeys(byte[] keyInput) {
        int keyWordsCount = (rounds + 1) * 4;
        byte[][] tmp = new byte[keyWordsCount][4];

        int j = 0;
        for (int i = 0; i < keySize / 4; i++) {
            for (int k = 0; k < 4; k++) {
                tmp[i][k] = keyInput[j++];
            }
        }

        for (int i = keySize / 4; i < keyWordsCount; i++) {
            byte[] temp = tmp[i - 1];

            if (i % (keySize / 4) == 0) {
                temp = g(temp, i / (keySize / 4));
            }

            // Apply SubWord transformation only for every 8th word (for AES-256 only)
            if (keySize == 32 && i % 8 == 0) {
                for (int k = 0; k < 4; k++) {
                    temp[k] = SBox.translateS_Box(temp[k]);
                }
            }

            tmp[i] = xorWords(tmp[i - (keySize / 4)], temp);
        }

        return tmp;
    }

    public byte[][] generateSubKeys1(byte[] keyInput) {
        int keyWordsCount = (rounds + 1) * 4;
        byte[][] tmp = new byte[keyWordsCount][4];

        int j = 0;
        for (int i = 0; i < keySize / 4; i++) {
            for (int k = 0; k < 4; k++) {
                tmp[i][k] = keyInput[j++];
            }
        }

        for (int i = keySize / 4; i < keyWordsCount; i++) {
            byte[] temp = tmp[i - 1];
            if (i % (keySize / 4) == 0) {
                temp = g(temp, i / (keySize / 4)); // Apply g transformation
            }
            tmp[i] = xorWords(tmp[i - (keySize / 4)], temp);
        }
        return tmp;
    }

    public byte[][] generateReversedSubKeys(byte[][] keyWords) {
        int k = 0;
        byte[][] tmp = new byte[(rounds + 1) * 4][4];
        for (int i = rounds; i >= 0; i--) {
            for (int j = 0; j < 4; j++) {
                tmp[k] = keyWords[i * 4 + j];
                k++;
            }
        }
        return tmp;
    }

    public byte[] addRoundKey(byte[] state, int round) {
        byte[] tmp = new byte[state.length];
        int start = round * 4;
        int k = 0;
        for (int i = start; i < start + 4; i++) {
            for (int j = 0; j < 4; j++) {
                tmp[k] = (byte) (state[k] ^ keyWords[i][j]);
                k++;
            }
        }
        return tmp;
    }


    public byte[] xorWords(byte[] word1, byte[] word2) {
        byte[] tmp = new byte[word1.length];
        for (int i = 0; i < word1.length; i++) {
            tmp[i] = (byte) (word1[i] ^ word2[i]);
        }
        return tmp;
    }

    public byte[] g(byte[] word, int round) {
        byte[] result = new byte[4];
        // RotWord
        result[0] = word[1];
        result[1] = word[2];
        result[2] = word[3];
        result[3] = word[0];

        // SubBytes
        for (int i = 0; i < 4; i++) {
            result[i] = SBox.translateS_Box(result[i]);
        }

        // XOR with Rcon
        result[0] ^= Rcon[round - 1];
        return result;
    }

    public static final byte[] Rcon = {
            (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10,
            (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x1B, (byte) 0x36
    };

    public byte[] mixColumns(byte[] state) {
        byte[] tmp = new byte[16];
        for (int i = 0; i < 4; i++) {
            int col = i * 4;
            tmp[col] = (byte) (mul(0x02, state[col]) ^ mul(0x03, state[col + 1]) ^ state[col + 2] ^ state[col + 3]);
            tmp[col + 1] = (byte) (state[col] ^ mul(0x02, state[col + 1]) ^ mul(0x03, state[col + 2]) ^ state[col + 3]);
            tmp[col + 2] = (byte) (state[col] ^ state[col + 1] ^ mul(0x02, state[col + 2]) ^ mul(0x03, state[col + 3]));
            tmp[col + 3] = (byte) (mul(0x03, state[col]) ^ state[col + 1] ^ state[col + 2] ^ mul(0x02, state[col + 3]));
        }
        return tmp;
    }

    public byte[] mixColumnsReversed(byte[] state) {
        byte[] tmp = new byte[16];
        for (int i = 0; i < 4; i++) {
            int col = i * 4;
            tmp[col] = (byte) (mul(0x0E, state[col]) ^ mul(0x0B, state[col + 1]) ^ mul(0x0D, state[col + 2]) ^ mul(0x09, state[col + 3]));
            tmp[col + 1] = (byte) (mul(0x09, state[col]) ^ mul(0x0E, state[col + 1]) ^ mul(0x0B, state[col + 2]) ^ mul(0x0D, state[col + 3]));
            tmp[col + 2] = (byte) (mul(0x0D, state[col]) ^ mul(0x09, state[col + 1]) ^ mul(0x0E, state[col + 2]) ^ mul(0x0B, state[col + 3]));
            tmp[col + 3] = (byte) (mul(0x0B, state[col]) ^ mul(0x0D, state[col + 1]) ^ mul(0x09, state[col + 2]) ^ mul(0x0E, state[col + 3]));
        }
        return tmp;
    }

    private byte mul(int a, byte b) {
        byte result = 0;
        for (int i = 0; i < 8; i++) {
            if ((a & 1) != 0) {
                result ^= b;
            }
            boolean hiBitSet = (b & 0x80) != 0;
            b <<= 1;
            if (hiBitSet) {
                b ^= 0x1b;
            }
            a >>= 1;
        }
        return result;
    }

    /**
     * Each byte of the state is substituted with the corresponding byte from the S-Box.
     *
     * @param state An array of bytes representing the state to be transformed.
     * @return A new array of bytes after applying the substitution using the S-Box.
     */
    public byte[] subBytes(byte[] state) {
        byte[] tmp = new byte[state.length];
        for (int i = 0; i < state.length; i++) {
            tmp[i] = SBox.translateS_Box(state[i]);
        }
        return tmp;
    }

    public byte[] subBytesReversed(byte[] state) {
        byte[] tmp = new byte[state.length];
        for (int i = 0; i < state.length; i++) {
            tmp[i] = SBox.translateInv_S_Box(state[i]);
        }
        return tmp;
    }

    /**
     * The block is a 4x4 matrix. The first row remains unchanged, for the second row
     * a left rotation (ROL) by one position is applied, for the third row a left rotation
     * by two positions is applied, and for the fourth row a left rotation by three positions
     * is applied.
     *
     * @param state A byte array representing the 4x4 block (state) to be shifted.
     * @return A new byte array representing the state after performing the row shifts.
     */
    public static byte[] shiftRows(byte[] state) {
        // Create two-dimensional array for easier shifting
        byte[][] tmp = new byte[4][4];
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tmp[i][j] = state[k];
                k++;
            }
        }

        // Row 1 stays the same (no shift)

        // Row 2 is shifted by 1 position to the left
        byte temp1 = tmp[1][0];
        tmp[1][0] = tmp[1][1];
        tmp[1][1] = tmp[1][2];
        tmp[1][2] = tmp[1][3];
        tmp[1][3] = temp1;

        // Row 3 is shifted by 2 positions to the left
        byte temp2 = tmp[2][0];
        byte temp3 = tmp[2][1];
        tmp[2][0] = tmp[2][2];
        tmp[2][1] = tmp[2][3];
        tmp[2][2] = temp2;
        tmp[2][3] = temp3;

        // Row 4 is shifted by 3 positions to the left
        byte temp4 = tmp[3][0];
        byte temp5 = tmp[3][1];
        byte temp6 = tmp[3][2];
        tmp[3][0] = tmp[3][3];
        tmp[3][1] = temp4;
        tmp[3][2] = temp5;
        tmp[3][3] = temp6;

        // Create one dimensional array to return output
        byte[] newState = new byte[16];
        k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                newState[k] = tmp[i][j];
                k++;
            }
        }

        return newState;
    }

    /**
     * The block is a 4x4 matrix. The first row remains unchanged, for the second row
     * a right rotation (ROL) by one position is applied, for the third row a right rotation
     * by two positions is applied, and for the fourth row a right rotation by three positions
     * is applied.
     *
     * @param state A byte array representing the 4x4 block (state) to be shifted.
     * @return A new byte array representing the state after performing the row shifts.
     */
    public static byte[] shiftRowsReversed(byte[] state) {
        // Create two-dimensional array for easier shifting
        byte[][] tmp = new byte[4][4];
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tmp[i][j] = state[k];
                k++;
            }
        }

        // Row 1 stays the same (no shift)

        // Row 2 is shifted by 1 position to the right
        byte temp1 = tmp[1][3];
        tmp[1][3] = tmp[1][2];
        tmp[1][2] = tmp[1][1];
        tmp[1][1] = tmp[1][0];
        tmp[1][0] = temp1;

        // Row 3 is shifted by 2 positions to the right
        byte temp2 = tmp[2][3];
        byte temp3 = tmp[2][2];
        tmp[2][3] = tmp[2][1];
        tmp[2][2] = tmp[2][0];
        tmp[2][1] = temp2;
        tmp[2][0] = temp3;

        // Row 4 is shifted by 3 positions to the right
        byte temp4 = tmp[3][3];
        byte temp5 = tmp[3][2];
        byte temp6 = tmp[3][1];
        tmp[3][3] = tmp[3][0];
        tmp[3][2] = temp4;
        tmp[3][1] = temp5;
        tmp[3][0] = temp6;

        // Create one-dimensional array to return output
        byte[] newState = new byte[16];
        k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                newState[k] = tmp[i][j];
                k++;
            }
        }

        return newState;
    }

}
