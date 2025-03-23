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

        // Expected output after applying reversed shift rows
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

        // Expected output after applying reversed shift rows
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
    public void testEncryptionAndDecryption128Bit() throws Exception {
        // 128-bit (16 bytes) key and plaintext
        byte[] key = "1234567890abcdef".getBytes(); // 128-bit key
        byte[] plaintext = "This is a test!!!".getBytes(); // 16 bytes plaintext (for AES-128)

        Aes aes = new Aes(key);

        // Encrypt the plaintext
        byte[] ciphertext = aes.encode(plaintext);

        // Decrypt the ciphertext
        byte[] decryptedText = aes.decode(ciphertext);

        // Check that decrypted text is the same as original plaintext
        assertArrayEquals(plaintext, decryptedText);
    }

    // Test case for a valid AES encryption with a 192-bit key
    @Test
    public void testEncryptionAndDecryption192Bit() throws Exception {
        // 192-bit (24 bytes) key and plaintext
        byte[] key = "1234567890abcdef12345678".getBytes(); // 192-bit key
        byte[] plaintext = "This is a 192-bit key test.".getBytes(); // 24 bytes plaintext

        Aes aes = new Aes(key);

        // Encrypt the plaintext
        byte[] ciphertext = aes.encode(plaintext);

        // Decrypt the ciphertext
        byte[] decryptedText = aes.decode(ciphertext);

        // Check that decrypted text is the same as original plaintext
        assertArrayEquals(plaintext, decryptedText);
    }

    // Test case for a valid AES encryption with a 256-bit key
    @Test
    public void testEncryptionAndDecryption256Bit() throws Exception {
        // 256-bit (32 bytes) key and plaintext
        byte[] key = "1234567890abcdef1234567890abcdef".getBytes(); // 256-bit key
        byte[] plaintext = "This is a 256-bit key test!!!".getBytes(); // 32 bytes plaintext

        Aes aes = new Aes(key);

        // Encrypt the plaintext
        byte[] ciphertext = aes.encode(plaintext);

        // Decrypt the ciphertext
        byte[] decryptedText = aes.decode(ciphertext);

        // Check that decrypted text is the same as original plaintext
        assertArrayEquals(plaintext, decryptedText);
    }

    // Test case for padding (ensuring 16-byte blocks for non-multiple of 16 plaintext lengths)
    @Test
    public void testPaddingOnEncryption() throws Exception {
        byte[] key = "1234567890abcdef".getBytes(); // 128-bit key
        byte[] plaintext = "Short text.".getBytes(); // Less than 16 bytes

        Aes aes = new Aes(key);

        // Encrypt the plaintext
        byte[] ciphertext = aes.encode(plaintext);

        // Decrypt the ciphertext
        byte[] decryptedText = aes.decode(ciphertext);

        // The decrypted text should be equal to the original plaintext
        assertArrayEquals(plaintext, decryptedText);
    }

    // Test case for invalid key length
    @Test
    public void testInvalidKeyLength() {
        byte[] invalidKey = "shortkey".getBytes(); // Invalid key (8 bytes)
        Exception exception = assertThrows(Exception.class, () -> {
            new Aes(invalidKey);
        });

        assertEquals("Key has wrong length! Supported lengths: 16, 24, 32 bytes (128, 192, 256 bits)", exception.getMessage());
    }

    // Test case for correct behavior when input size is not a multiple of 16 during decryption
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

    // Test sub-key generation method (Generate subkeys for 128-bit key)
    @Test
    public void testGenerateSubKeys128Bit() throws Exception {
        byte[] key = "1234567890abcdef".getBytes(); // 128-bit key
        Aes aes = new Aes(key);

        byte[][] subkeys = aes.generateSubKeys(key);

        // Check that the number of subkeys matches the expected (11 subkeys for AES-128)
        assertEquals(44, subkeys.length); // (rounds + 1) * 4 = (10 + 1) * 4 = 44
    }
}
