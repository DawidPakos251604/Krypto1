import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/config_scene.fxml"));

        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("AES Encryption Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
