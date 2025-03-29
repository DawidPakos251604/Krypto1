import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

                int keySize = Integer.parseInt(keySizeCombo.getValue());
                byte[] keyBytes = CryptoUtils.prepareKey(keyText, keySize);
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

                int keySize = Integer.parseInt(keySizeCombo.getValue());
                byte[] keyBytes = CryptoUtils.prepareKey(keyText, keySize);
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mode_selection.fxml"));
            AnchorPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Mode Selection");
            stage.setScene(new Scene(root));
            stage.show();

            Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            oldStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


