package co.fitlife.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Asegúrate de que la ruta coincida con donde pusiste el archivo FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/co/fitlife/views/MainView.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("FitLife Pro - Gestión de Gimnasio");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}