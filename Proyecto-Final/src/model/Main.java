package model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/vista/VistaHash.fxml"));

        // Crear la escena
        Scene scene = new Scene(root);

        // Establecer la escena en el escenario principal
        primaryStage.setScene(scene);
        primaryStage.setTitle("Excel Proyecto Final");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
