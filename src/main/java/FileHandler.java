import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for handling file operations (reading and writing binary files).
 */
public class FileHandler {

    /**
     * Reads the contents of a file as a byte array.
     *
     * @param filePath The path to the file to be read.
     * @return A byte array containing the file's data.
     * @throws IOException If an error occurs while reading the file.
     */
    public static byte[] loadFileAsBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    /**
     * Saves the given byte data to a file.
     *
     * @param data     The byte array to be written to the file.
     * @param filePath The path to the file where the data will be saved.
     * @throws IOException If an error occurs while writing to the file.
     */
    public static void saveBytesToFile(byte[] data, String filePath) throws IOException {
        if (data == null || data.length == 0) {
            throw new IOException("No data to save!");
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        } catch (IOException e) {
            throw new IOException("Error while writing file: " + e.getMessage(), e);
        }
    }
}
