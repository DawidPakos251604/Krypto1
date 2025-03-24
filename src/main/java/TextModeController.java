import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
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
    private void initialize() {
        keySizeCombo.getItems().addAll("128", "192", "256");
        keySizeCombo.setValue("128");  // Domyślnie 128-bitowy klucz
    }

    @FXML
    private void encryptText() {
        try {
            String text = inputText.getText();
            String key = keyInput.getText();
            if (text.isEmpty() || key.isEmpty()) {
                outputText.setText("Błąd: Podaj tekst i klucz!");
                return;
            }

            aes = new Aes(prepareKey(key));

            byte[] encrypted = aes.encode(text.getBytes(StandardCharsets.UTF_8));
            String hexResult = bytesToHex(encrypted);

            outputText.setText(hexResult);
        } catch (Exception e) {
            outputText.setText("Błąd szyfrowania: " + e.getMessage());
        }
    }

    @FXML
    private void decryptText() {
        try {
            String hexText = inputText.getText();
            String key = keyInput.getText();
            if (hexText.isEmpty() || key.isEmpty()) {
                outputText.setText("Błąd: Podaj szyfrogram i klucz!");
                return;
            }

            aes = new Aes(prepareKey(key));

            byte[] encryptedBytes = hexToBytes(hexText);
            byte[] decrypted = aes.decode(encryptedBytes);
            String result = new String(decrypted, StandardCharsets.UTF_8);

            outputText.setText(result);
        } catch (Exception e) {
            outputText.setText("Błąd deszyfrowania: " + e.getMessage());
        }
    }

    // Przygotowanie klucza do wymaganej długości (128/192/256 bitów)
    private byte[] prepareKey(String key) {
        int keySize = Integer.parseInt(keySizeCombo.getValue()) / 8;
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] preparedKey = new byte[keySize];

        // Kopiowanie klucza do tablicy o odpowiednim rozmiarze
        System.arraycopy(keyBytes, 0, preparedKey, 0, Math.min(keyBytes.length, keySize));
        return preparedKey;
    }

    // Konwersja bajtów na HEX
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    // Konwersja HEX na bajty
    private byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
