public class Aes {

    public Aes() {

    }

    /** Każdy bajt bloku jest zamieniany na inny z SBoxa. */
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
