import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TextModeController {

    @FXML
    private TextArea inputText, outputText;
    @FXML
    private TextField keyInput;
    @FXML
    private ComboBox<String> keySizeCombo;

    private Aes aes;

    @FXML
    private void encryptText() {
        try {
            String text = inputText.getText();
            String key = keyInput.getText();
            if (text.isEmpty() || key.isEmpty()) {
                outputText.setText("Error: Please enter text and key!");
                return;
            }

            int keySize = Integer.parseInt(keySizeCombo.getValue());
            aes = new Aes(CryptoUtils.prepareKey(key, keySize));

            byte[] encrypted = aes.encode(text.getBytes(StandardCharsets.UTF_8));
            String hexResult = CryptoUtils.bytesToHex(encrypted);

            outputText.setText(hexResult);
        } catch (Exception e) {
            outputText.setText("Encryption error: " + e.getMessage());
        }
    }

    @FXML
    private void decryptText() {
        try {
            String hexText = inputText.getText();
            String key = keyInput.getText();
            if (hexText.isEmpty() || key.isEmpty()) {
                outputText.setText("Error: Please enter ciphertext and key!");
                return;
            }

            int keySize = Integer.parseInt(keySizeCombo.getValue());
            aes = new Aes(CryptoUtils.prepareKey(key, keySize));

            byte[] encryptedBytes = CryptoUtils.hexToBytes(hexText);
            byte[] decrypted = aes.decode(encryptedBytes);
            String result = new String(decrypted, StandardCharsets.UTF_8);

            outputText.setText(result);
        } catch (Exception e) {
            outputText.setText("Decryption error: " + e.getMessage());
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mode_selection.fxml"));
            AnchorPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Wybór Trybu");
            stage.setScene(new Scene(root));
            stage.show();

            Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            oldStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



