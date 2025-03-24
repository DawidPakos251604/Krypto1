import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import java.io.File;

public class FileModeController {

    @FXML
    private TextField keyInput;
    @FXML
    private ComboBox<String> keySizeCombo;
    @FXML
    private String loadedFilePath;

    @FXML
    private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki", "*.dat", "*.bin", "*.*"));
        fileChooser.setTitle("Wybierz plik");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            loadedFilePath = file.getPath(); // Zapisujemy ścieżkę wczytanego pliku
            System.out.println("Załadowano plik: " + loadedFilePath);
        }
    }

    @FXML
    private void saveToFile() {
        if (loadedFilePath == null) {
            System.out.println("Najpierw załaduj plik!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz zaszyfrowany plik");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki binarne", "*.dat", "*.bin", "*.*"));
        fileChooser.setInitialFileName("zaszyfrowany_plik.dat");

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                byte[] originalData = FileHandler.loadFileAsBytes(loadedFilePath);
                String keyText = keyInput.getText();
                if (keyText.isEmpty()) {
                    System.out.println("Brak klucza do szyfrowania!");
                    return;
                }

                // Przygotowanie klucza zgodnie z wybranym rozmiarem
                byte[] keyBytes = prepareKey(keyText);

                Aes aes = new Aes(keyBytes);
                byte[] encryptedData = aes.encode(originalData);
                FileHandler.saveBytesToFile(encryptedData, file.getPath());

                System.out.println("Plik zapisano pomyślnie: " + file.getPath());

            } catch (Exception e) {
                System.err.println("Błąd podczas zapisu pliku: " + e.getMessage());
            }
        }
    }

    @FXML
    private void decryptFile() {
        if (loadedFilePath == null) {
            System.out.println("Najpierw załaduj plik!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz odszyfrowany plik");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki binarne", "*.dat", "*.bin", "*.*"));
        fileChooser.setInitialFileName("odszyfrowany_plik.dat");

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                byte[] encryptedData = FileHandler.loadFileAsBytes(loadedFilePath);
                String keyText = keyInput.getText();
                if (keyText.isEmpty()) {
                    System.out.println("Brak klucza do deszyfrowania!");
                    return;
                }

                byte[] keyBytes = prepareKey(keyText);

                Aes aes = new Aes(keyBytes);
                byte[] decryptedData = aes.decode(encryptedData);
                FileHandler.saveBytesToFile(decryptedData, file.getPath());

                System.out.println("Plik odszyfrowano i zapisano pomyślnie: " + file.getPath());

            } catch (Exception e) {
                System.err.println("Błąd podczas odszyfrowywania pliku: " + e.getMessage());
            }
        }
    }

    // Przygotowanie klucza do wymaganej długości (128/192/256 bitów)
    private byte[] prepareKey(String key) {
        int keySize = Integer.parseInt(keySizeCombo.getValue()) / 8;
        byte[] keyBytes = key.getBytes();
        byte[] preparedKey = new byte[keySize];
        System.arraycopy(keyBytes, 0, preparedKey, 0, Math.min(keyBytes.length, keySize));
        return preparedKey;
    }
}
