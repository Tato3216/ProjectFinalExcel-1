/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import model.Hash;

public class VistaHashController implements Initializable {

    @FXML
    private Button btnCrearTablaHash;
    @FXML
    private TextField txtCubetas;
    @FXML
    private TextField txtIngresarDatos;
    @FXML
    private Button btnInsertInTablaHash;
    @FXML
    private TableView<Cubeta> tblHash;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnBorrarTodo;
    @FXML
    private Button btnAbrir;

    private Hash hash;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnCrearTablaHash.setOnAction(event -> crearTablaHash());
        btnInsertInTablaHash.setOnAction(event -> insertarEnTabla());
        btnGuardar.setOnAction(event -> guardarEnJSON());
        btnBorrarTodo.setOnAction(event -> vaciar());
        btnAbrir.setOnAction(event -> abrirJSON());
    }

    private void guardarEnJSON() {
        ObservableList<Cubeta> cubetasList = tblHash.getItems();
        List<List<Integer>> tabla = new ArrayList<>();

        for (Cubeta cubeta : cubetasList) {
            tabla.add(cubeta.getValores());
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = objectMapper.writeValueAsString(tabla);

            Files.write(Paths.get("tabla_hash.json"), json.getBytes());

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tabla guardada");
            alert.setHeaderText("Dato guardados correctamente");
            alert.showAndWait();
        } catch (IOException e) {
            // Mostrar mensaje de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al guardar.");
            alert.setHeaderText("Error al guardar.");
            alert.showAndWait();
        }
    }

    private void crearTablaHash() {
        int cubetas = Integer.parseInt(txtCubetas.getText());
        hash = new Hash(cubetas);

        tblHash.getItems().clear();
        tblHash.getColumns().clear();

        TableColumn<Cubeta, Integer> column = new TableColumn<>("Cubeta");
        column.setCellValueFactory(new PropertyValueFactory<>("valor"));

        tblHash.getColumns().add(column);

        ObservableList<Cubeta> cubetasList = FXCollections.observableArrayList();

        for (int i = 0; i < cubetas; i++) {
            Cubeta cubeta = new Cubeta(null);
            cubetasList.add(cubeta);
        }

        tblHash.setItems(cubetasList);

        txtCubetas.setDisable(true);
        btnCrearTablaHash.setDisable(true);
    }

    private void insertarEnTabla() {
        System.out.println("Insertando dato en la tabla");

        if (hash != null) {
            int numero = Integer.parseInt(txtIngresarDatos.getText());

            if (tablaLlena()) {
                mostrarAlertaTablaLlena();
            } else {
                int index = hash.hashFunction(numero);
                ObservableList<Cubeta> cubetasList = tblHash.getItems();

                if (index - 1 < 0 || index - 1 >= cubetasList.size()) {
                    mostrarAlertaIndiceInvalido();
                } else {
                    Cubeta cubeta = cubetasList.get(index - 1);
                    cubeta.setValor(numero);
                }
            }

            txtIngresarDatos.clear();
        }
    }

    private void abrirJSON() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Tabla Hash");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos JSON", "*.json"));
        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, List.class);
                List<List<Integer>> tabla = objectMapper.readValue(archivo, collectionType);

                tblHash.getItems().clear();
                tblHash.getColumns().clear();

                TableColumn<Cubeta, Integer> column = new TableColumn<>("Cubeta");
                column.setCellValueFactory(new PropertyValueFactory<>("valor"));

                tblHash.getColumns().add(column);

                ObservableList<Cubeta> cubetasList = FXCollections.observableArrayList();

                for (List<Integer> valores : tabla) {
                    if (!valores.isEmpty()) {
                        Integer valor = valores.get(0);
                        Cubeta cubeta = new Cubeta(valor);
                        cubetasList.add(cubeta);
                    }
                }

                tblHash.setItems(cubetasList);

                // Mostrar mensaje de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Tabla cargada");
                alert.setHeaderText("Tabla cargada correctamente");
                alert.showAndWait();
            } catch (IOException e) {
                // Mostrar mensaje de error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al abrir.");
                alert.setHeaderText("Error al abrir el archivo.");
                alert.showAndWait();
            }
        }
    }

    private void vaciar() {
        tblHash.getItems().clear();
        txtCubetas.setDisable(false);
        btnCrearTablaHash.setDisable(false);
    }

    private void mostrarAlertaIndiceInvalido() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText("Ha Ocurrido un Error al Insertar!");
        alert.showAndWait();
    }

    private boolean tablaLlena() {
        for (Cubeta cubeta : tblHash.getItems()) {
            if (cubeta.getValor() == null) {
                return false;
            }
        }
        return true;
    }

    public void mostrarAlertaTablaLlena() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Tabla Hash Llena");
        alert.setHeaderText("Tabla Hash Llena");
        alert.setContentText("La Tabla Hash Esta Llena");
        alert.showAndWait();
    }

    public static class Cubeta {

        private final SimpleObjectProperty<Integer> valor;

        public Cubeta(Integer valor) {
            this.valor = new SimpleObjectProperty<>(valor);
        }

        public Integer getValor() {
            return valor.get();
        }

        public void setValor(Integer valor) {
            this.valor.set(valor);
        }

        public SimpleObjectProperty<Integer> valorProperty() {
            return valor;
        }

        public List<Integer> getValores() {
            List<Integer> valores = new ArrayList<>();
            if (valor != null) {
                valores.add(valor.get());
            }
            return valores;
        }
    }

}
