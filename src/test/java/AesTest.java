import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AesTest {

    @Test
    public void testShiftRows() {
        byte[] state = {
                (byte) 0x32, (byte) 0x88, (byte) 0x31, (byte) 0xe0,
                (byte) 0x43, (byte) 0x5a, (byte) 0x31, (byte) 0x37,
                (byte) 0xf6, (byte) 0x30, (byte) 0x98, (byte) 0x07,
                (byte) 0xa8, (byte) 0x8d, (byte) 0xa2, (byte) 0x34
        };

        byte[] expectedState = {
                (byte) 0x32, (byte) 0x88, (byte) 0x31, (byte) 0xe0,
                (byte) 0x5a, (byte) 0x31, (byte) 0x37, (byte) 0x43,
                (byte) 0x98, (byte) 0x07, (byte) 0xf6, (byte) 0x30,
                (byte) 0x34, (byte) 0xa8, (byte) 0x8d, (byte) 0xa2
        };

        byte[] result = Aes.shiftRows(state);

        assertArrayEquals(expectedState, result);
    }

    @Test
    public void testShiftRowsReversed() {
        byte[] state = {
                (byte) 0x32, (byte) 0x88, (byte) 0x31, (byte) 0xe0,
                (byte) 0x43, (byte) 0x5a, (byte) 0x31, (byte) 0x37,
                (byte) 0xf6, (byte) 0x30, (byte) 0x98, (byte) 0x07,
                (byte) 0xa8, (byte) 0x8d, (byte) 0xa2, (byte) 0x34
        };

        byte[] expectedState = {
                (byte) 0x32, (byte) 0x88, (byte) 0x31, (byte) 0xe0,
                (byte) 0x37, (byte) 0x43, (byte) 0x5a, (byte) 0x31,
                (byte) 0x98, (byte) 0x07, (byte) 0xf6, (byte) 0x30,
                (byte) 0x8d, (byte) 0xa2, (byte) 0x34, (byte) 0xa8
        };

        byte[] result = Aes.shiftRowsReversed(state);

        assertArrayEquals(expectedState, result);
    }

    @Test
    public void testMixColumns() throws Exception {
        Aes aes = new Aes(new byte[16]);

        byte[] input = {
                (byte) 0xd4, (byte) 0xbf, (byte) 0x5d, (byte) 0x30,
                (byte) 0xe0, (byte) 0xb4, (byte) 0x52, (byte) 0xae,
                (byte) 0xb8, (byte) 0x41, (byte) 0x11, (byte) 0xf1,
                (byte) 0x1e, (byte) 0x27, (byte) 0x98, (byte) 0xe5
        };

        byte[] expectedOutput = {
                (byte) 0x04, (byte) 0x66, (byte) 0x81, (byte) 0xe5,
                (byte) 0xe0, (byte) 0xcb, (byte) 0x19, (byte) 0x9a,
                (byte) 0x48, (byte) 0xf8, (byte) 0xd3, (byte) 0x7a,
                (byte) 0x28, (byte) 0x06, (byte) 0x26, (byte) 0x4c
        };

        byte[] result = aes.mixColumns(input);
        assertArrayEquals(expectedOutput, result);
    }

    @Test
    public void testMixColumnsReversed() throws Exception {
        Aes aes = new Aes(new byte[16]);

        byte[] input = {
                (byte) 0x04, (byte) 0x66, (byte) 0x81, (byte) 0xe5,
                (byte) 0xe0, (byte) 0xcb, (byte) 0x19, (byte) 0x9a,
                (byte) 0x48, (byte) 0xf8, (byte) 0xd3, (byte) 0x7a,
                (byte) 0x28, (byte) 0x06, (byte) 0x26, (byte) 0x4c
        };

        byte[] expectedOutput = {
                (byte) 0xd4, (byte) 0xbf, (byte) 0x5d, (byte) 0x30,
                (byte) 0xe0, (byte) 0xb4, (byte) 0x52, (byte) 0xae,
                (byte) 0xb8, (byte) 0x41, (byte) 0x11, (byte) 0xf1,
                (byte) 0x1e, (byte) 0x27, (byte) 0x98, (byte) 0xe5
        };

        byte[] result = aes.mixColumnsReversed(input);
        assertArrayEquals(expectedOutput, result);
    }

    @Test
    public void testEncryptionAndDecryption128Bit() throws Exception {
        byte[] key = "1234567890abcdef".getBytes(); // 128-bit key
        byte[] plaintext = "This is a test!!!".getBytes(); // 16 bytes plaintext (for AES-128)

        Aes aes = new Aes(key);

        byte[] ciphertext = aes.encode(plaintext);

        byte[] decryptedText = aes.decode(ciphertext);

        assertArrayEquals(plaintext, decryptedText);
    }

    @Test
    public void testEncryptionAndDecryption192Bit() throws Exception {
        byte[] key = "1234567890abcdef12345678".getBytes(); // 192-bit key
        byte[] plaintext = "This is a 192-bit key test.".getBytes(); // 24 bytes plaintext

        Aes aes = new Aes(key);

        byte[] ciphertext = aes.encode(plaintext);

        byte[] decryptedText = aes.decode(ciphertext);

        assertArrayEquals(plaintext, decryptedText);
    }

    @Test
    public void testEncryptionAndDecryption256Bit() throws Exception {
        byte[] key = "1234567890abcdef1234567890abcdef".getBytes(); // 256-bit key
        byte[] plaintext = "This is a 256-bit key test!!!".getBytes(); // 32 bytes plaintext

        Aes aes = new Aes(key);

        byte[] ciphertext = aes.encode(plaintext);

        byte[] decryptedText = aes.decode(ciphertext);

        assertArrayEquals(plaintext, decryptedText);
    }

    @Test
    public void testPaddingOnEncryption() throws Exception {
        byte[] key = "1234567890abcdef".getBytes(); // 128-bit key
        byte[] plaintext = "Short text.".getBytes(); // Less than 16 bytes

        Aes aes = new Aes(key);

        byte[] ciphertext = aes.encode(plaintext);

        byte[] decryptedText = aes.decode(ciphertext);

        assertArrayEquals(plaintext, decryptedText);
    }

    @Test
    public void testInvalidKeyLength() {
        byte[] invalidKey = "shortkey".getBytes(); // Invalid key (8 bytes)
        Exception exception = assertThrows(Exception.class, () -> {
            new Aes(invalidKey);
        });

        assertEquals("Key has wrong length! Supported lengths: 16, 24, 32 bytes (128, 192, 256 bits)", exception.getMessage());
    }

    @Test
    public void testDecryptionWithNon16ByteInput() {
        byte[] key = "1234567890abcdef".getBytes(); // 128-bit key
        byte[] incorrectCiphertext = new byte[15]; // Not a multiple of 16 bytes

        Aes aes = null;
        try {
            aes = new Aes(key);
        } catch (Exception e) {
            fail("Key initialization failed");
        }

        // Decrypt the ciphertext, which is not a multiple of 16
        byte[] result = aes.decode(incorrectCiphertext);

        // Decrypted result should be null since AES requires 16-byte blocks
        assertNull(result);
    }

    @Test
    public void testGenerateSubKeys128Bit() throws Exception {
        byte[] key = "1234567890abcdef".getBytes(); // 128-bit key
        Aes aes = new Aes(key);

        byte[][] subkeys = aes.generateSubKeys(key);

        // Check that the number of subkeys matches the expected (11 subkeys for AES-128)
        assertEquals(44, subkeys.length); // (rounds + 1) * 4 = (10 + 1) * 4 = 44
    }

    @Test
    public void testGenerateSubKeys192Bit() throws Exception {
        byte[] key = "1234567890abcdef12345678".getBytes(); // 192-bit key (24 bytes)
        Aes aes = new Aes(key);

        byte[][] subkeys = aes.generateSubKeys(key);

        // Check that the number of subkeys matches the expected (13 subkeys for AES-192)
        assertEquals(52, subkeys.length); // (rounds + 1) * 4 = (12 + 1) * 4 = 52
    }

    @Test
    public void testGenerateSubKeys256Bit() throws Exception {
        byte[] key = "1234567890abcdef1234567890abcdef".getBytes(); // 256-bit key (32 bytes)
        Aes aes = new Aes(key);

        byte[][] subkeys = aes.generateSubKeys(key);

        // Check that the number of subkeys matches the expected (15 subkeys for AES-256)
        assertEquals(60, subkeys.length); // (rounds + 1) * 4 = (14 + 1) * 4 = 60
    }

}
