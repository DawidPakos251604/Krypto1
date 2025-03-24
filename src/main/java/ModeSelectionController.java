import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class ModeSelectionController {

    @FXML
    private void openFileMode(ActionEvent event) {
        openScene("/file_mode.fxml", "Tryb Plikowy", event);
    }

    @FXML
    private void openTextMode(ActionEvent event) {
        openScene("/text_mode.fxml", "Tryb Tekstowy", event);
    }

    private void openScene(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            // Zamknięcie starego okna (okno wyboru trybu)
            Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            oldStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
