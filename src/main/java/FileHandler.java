import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {

    // Wczytywanie pliku binarnego i konwersja na hex (do wyświetlania w GUI)
    public static String loadFileAsHex(String filePath) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(filePath));
        StringBuilder hexString = new StringBuilder();
        for (byte b : content) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    // Zapis danych heksadecymalnych (np. zaszyfrowanych) do pliku binarnego
    public static void saveHexToFile(String hexData, String filePath) throws IOException {
        byte[] byteArray = new byte[hexData.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) Integer.parseInt(hexData.substring(i * 2, i * 2 + 2), 16);
        }
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(byteArray);
        }
    }

    // Wczytywanie pliku binarnego jako byte[] (dla operacji na danych)
    public static byte[] loadFileAsBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    // Zapis danych binarnych do pliku
    public static void saveBytesToFile(byte[] data, String filePath) throws IOException {
        if (data == null || data.length == 0) {
            System.out.println("Brak danych do zapisania!");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
            System.out.println("Dane zapisane pomyślnie do: " + filePath);
        } catch (IOException e) {
            System.err.println("Błąd podczas zapisu: " + e.getMessage());
            throw e;
        }
    }
}
