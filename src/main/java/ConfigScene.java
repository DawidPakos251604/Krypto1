import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigScene {

    @FXML
    private TextArea inputText, outputText;

    @FXML
    private void encryptText() {
        String text = inputText.getText();
        if (!text.isEmpty()) {
            outputText.setText("Zaszyfrowany tekst: ");
        }
    }

    @FXML
    private void decryptText() {
        String text = inputText.getText();
        if (!text.isEmpty()) {
            outputText.setText("Odszyfrowany tekst: ");
        }
    }

    @FXML
    private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki", "*.dat", "*.bin", "*.*"));
        fileChooser.setTitle("Wybierz plik");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String hexContent = FileHandler.loadFileAsHex(file.getPath());
                inputText.setText(hexContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki", "*.dat", "*.bin", "*.*"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                String hexData = outputText.getText();
                FileHandler.saveHexToFile(hexData, file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
