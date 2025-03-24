import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

public class FileModeController {

    public Button encryptButton;
    public Button decryptButton;
    @FXML
    private TextField keyInput;
    @FXML
    private ComboBox<String> keySizeCombo;
    private String loadedFilePath;

    /**
     * Opens a file chooser to select a file for encryption or decryption.
     */
    @FXML
    private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.dat", "*.bin", "*.*"));
        fileChooser.setTitle("Select a File");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            loadedFilePath = file.getPath();
            showAlert(Alert.AlertType.INFORMATION, "File Loaded", "Loaded file: " + loadedFilePath);
        }
    }

    /**
     * Encrypts the selected file and saves it to a new location.
     */
    @FXML
    private void saveToFile() {
        if (loadedFilePath == null) {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "Please load a file first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Encrypted File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.dat", "*.bin", "*.*"));
        fileChooser.setInitialFileName("encrypted_file.dat");

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                byte[] originalData = FileHandler.loadFileAsBytes(loadedFilePath);
                String keyText = keyInput.getText();

                if (keyText.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Missing Key", "Please enter an encryption key.");
                    return;
                }

                byte[] keyBytes = prepareKey(keyText);
                Aes aes = new Aes(keyBytes);
                byte[] encryptedData = aes.encode(originalData);

                FileHandler.saveBytesToFile(encryptedData, file.getPath());

                showAlert(Alert.AlertType.INFORMATION, "Success", "File saved successfully: " + file.getPath());

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "File Error", "Error while saving file: " + e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Encryption Error", "An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Decrypts the selected file and saves it to a new location.
     */
    @FXML
    private void decryptFile() {
        if (loadedFilePath == null) {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "Please load a file first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Decrypted File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.dat", "*.bin", "*.*"));
        fileChooser.setInitialFileName("decrypted_file.dat");

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                byte[] encryptedData = FileHandler.loadFileAsBytes(loadedFilePath);
                String keyText = keyInput.getText();

                if (keyText.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Missing Key", "Please enter a decryption key.");
                    return;
                }

                byte[] keyBytes = prepareKey(keyText);
                Aes aes = new Aes(keyBytes);
                byte[] decryptedData = aes.decode(encryptedData);

                FileHandler.saveBytesToFile(decryptedData, file.getPath());

                showAlert(Alert.AlertType.INFORMATION, "Success", "File decrypted and saved: " + file.getPath());

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "File Error", "Error while decrypting file: " + e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Decryption Error", "An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Prepares the encryption key to the required length (128/192/256 bits).
     *
     * @param key The user-provided key string.
     * @return A byte array representing the encryption key.
     */
    private byte[] prepareKey(String key) {
        int keySize = Integer.parseInt(keySizeCombo.getValue()) / 8;
        byte[] keyBytes = key.getBytes();
        byte[] preparedKey = new byte[keySize];
        System.arraycopy(keyBytes, 0, preparedKey, 0, Math.min(keyBytes.length, keySize));
        return preparedKey;
    }

    /**
     * Displays an alert dialog to the user.
     *
     * @param alertType The type of alert (INFORMATION, WARNING, ERROR).
     * @param title     The title of the alert window.
     * @param message   The message to display.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
