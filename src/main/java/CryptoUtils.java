public class CryptoUtils {

    /**
     * Prepares an encryption key by converting a hex string into a byte array
     * and adjusting its length to match the required key size.
     *
     * @param hexKey  The encryption key as a hexadecimal string.
     * @param keySize The required key size in bits (e.g., 128, 192, 256).
     * @return A byte array representing the prepared key.
     * @throws IllegalArgumentException If the key is not a valid hex string or has an incorrect length.
     */
    public static byte[] prepareKey(String hexKey, int keySize) throws IllegalArgumentException {
        if (!hexKey.matches("^[0-9A-Fa-f]+$") || hexKey.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex key! Must be even-length and contain only 0-9, A-F.");
        }

        keySize /= 8; // Convert bits to bytes
        byte[] keyBytes = hexToBytes(hexKey);

        byte[] preparedKey = new byte[keySize];
        System.arraycopy(keyBytes, 0, preparedKey, 0, Math.min(keyBytes.length, keySize));

        return preparedKey;
    }

    /**
     * Converts a byte array into a hexadecimal string representation.
     *
     * @param bytes The byte array to convert.
     * @return A string containing the hexadecimal representation of the byte array.
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    /**
     * Converts a hexadecimal string into a byte array.
     *
     * @param hex The hexadecimal string to convert.
     * @return A byte array representing the original data.
     */
    public static byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
