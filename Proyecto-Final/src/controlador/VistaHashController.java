/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controlador;

import java.net.URL;
import javafx.scene.control.Alert;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.converter.DoubleStringConverter;
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
        System.out.println("Insertando en la tabla");

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

    private void mostrarAlertaIndiceInvalido() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Índice Inválido");
        alert.setHeaderText("El índice calculado está fuera del rango de la lista de cubetas.");
        alert.setContentText("Revise la función hash o el tamaño de la lista de cubetas.");
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

    private void mostrarAlertaTablaLlena() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Tabla Hash Llena");
        alert.setHeaderText("La tabla hash está llena");
        alert.setContentText("No se pueden insertar más elementos en la tabla hash.");
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
    }
}
