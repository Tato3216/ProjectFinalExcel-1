package vista;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Celda;
import model.Hoja;

public class VistaExcelPrincipalController<T> implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuBar menuOpcionesTop;
    @FXML
    private TabPane tabPaneMenuBottom;
    @FXML
    private Tab tabNameHoja;
    @FXML
    private Tab tabAddHoja;
    @FXML
    private TableView<ObservableList<Celda<String>>> tblExcel;

    private Hoja<String> hoja;
    @FXML
    private Menu menuUnicoArchivo;
    @FXML
    private MenuItem menuOpcionGuardar;
    @FXML
    private MenuItem menuOpcionCargar;
    @FXML
    private MenuItem menuOpcionHash;
    @FXML
    private Menu menuUnicoAyuda;
    @FXML
    private MenuItem menuOpcionCerrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hoja = new Hoja<>();
        menuOpcionGuardar.setOnAction(event -> guardarArchivo());
        menuOpcionCargar.setOnAction(event -> cargarArchivo());
        menuOpcionHash.setOnAction(event -> abrirVistaHash());
        tblExcel.getSelectionModel().setCellSelectionEnabled(true);

        tabPaneMenuBottom.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab.equals(tabNameHoja)) {
                renombrarHoja();
            }
        });

        crearColumnasDinamicas();
        crearFilasEnumeradas();
        configurarEdicionCeldas();
        menuOpcionCerrar.setOnAction(event -> {
        // Cerrar completamente el programa
        Platform.exit();
        System.exit(0);
        });
    }

    private void crearColumnasDinamicas() {
        for (char c = 'A'; c <= 'Z'; c++) {
            TableColumn<ObservableList<Celda<String>>, String> column = new TableColumn<>(String.valueOf(c));
            final int columnIndex = tblExcel.getColumns().size();
            column.setCellValueFactory(cellData -> {
                ObservableList<Celda<String>> row = cellData.getValue();
                if (columnIndex >= row.size()) {
                    row.addAll(Collections.nCopies(columnIndex - row.size() + 1, new Celda<>("")));
                }
                return new SimpleStringProperty(row.get(columnIndex).getContenidoCelda());
            });
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> {
                ObservableList<Celda<String>> rowValue = event.getTableView().getItems().get(event.getTablePosition().getRow());
                rowValue.get(event.getTablePosition().getColumn()).setContenidoCelda(event.getNewValue());
            });
            column.setEditable(true);
            tblExcel.getColumns().add(column);
        }
    }

    private void crearFilasEnumeradas() {
        for (int i = 1; i <= 100; i++) {
            ObservableList<Celda<String>> row = FXCollections.observableArrayList();
            for (int j = 0; j < tblExcel.getColumns().size(); j++) {
                row.add(new Celda<>(""));
            }
            tblExcel.getItems().add(row);
        }
    }

    private void configurarEdicionCeldas() {
        tblExcel.setEditable(true);
        tblExcel.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ObservableList<Celda<String>> selectedRow = tblExcel.getSelectionModel().getSelectedItem();
                int rowIndex = tblExcel.getSelectionModel().getSelectedIndex();
                int columnIndex = tblExcel.getSelectionModel().getSelectedCells().get(0).getColumn();

                Celda<String> selectedCell = selectedRow.get(columnIndex);
                String formula = selectedCell.getContenidoCelda();

                if (formula.startsWith("=")) {
                    String[] tokens = formula.substring(1).split("\\(");
                    String functionName = tokens[0].toUpperCase();
                    String[] arguments = tokens[1].replaceAll("\\)", "").split(",");

                    if (functionName.equals("SUMA") && arguments.length == 2) {
                        double arg1 = Double.parseDouble(arguments[0]);
                        double arg2 = Double.parseDouble(arguments[1]);
                        double result = arg1 + arg2;
                        selectedCell.setContenidoCelda(String.valueOf(result));
                    } else if (functionName.equals("MULTI") && arguments.length == 2) {
                        double arg1 = Double.parseDouble(arguments[0]);
                        double arg2 = Double.parseDouble(arguments[1]);
                        double result = arg1 * arg2;
                        selectedCell.setContenidoCelda(String.valueOf(result));
                    }
                }
            }
        });
    }

    private void renombrarHoja() {
        TextInputDialog dialog = new TextInputDialog(tabNameHoja.getText());
        dialog.setTitle("Renombrar hoja");
        dialog.setHeaderText(null);
        dialog.setContentText("Ingrese el nuevo nombre de la hoja:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            tabNameHoja.setText(nombre);
        });
    }

    private void guardarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo CSV");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivo CSV", "*.csv"));
        Stage stage = (Stage) rootPane.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Escribir encabezados de columnas
                String headers = tblExcel.getColumns().stream()
                        .map(TableColumn::getText)
                        .collect(Collectors.joining(","));
                writer.println(headers);

                // Escribir datos de celdas
                tblExcel.getItems().forEach(row -> {
                    String rowData = row.stream()
                            .map(Celda::getContenidoCelda)
                            .collect(Collectors.joining(","));
                    writer.println(rowData);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar archivo CSV");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivo CSV", "*.csv"));
        Stage stage = (Stage) rootPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                // Leer encabezados de columnas
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] headers = line.split(",");
                    // Crear columnas dinámicamente
                    for (String header : headers) {
                        TableColumn<ObservableList<Celda<String>>, String> column = new TableColumn<>(header);
                        column.setCellValueFactory(cellData -> {
                            ObservableList<Celda<String>> row = cellData.getValue();
                            int columnIndex = tblExcel.getColumns().indexOf(column);
                            if (columnIndex < row.size()) {
                                return new SimpleStringProperty(row.get(columnIndex).getContenidoCelda());
                            }
                            return new SimpleStringProperty("");
                        });
                        column.setCellFactory(TextFieldTableCell.forTableColumn());
                        column.setOnEditCommit(event -> {
                            ObservableList<Celda<String>> rowValue = event.getTableView().getItems().get(event.getTablePosition().getRow());
                            rowValue.get(event.getTablePosition().getColumn()).setContenidoCelda(event.getNewValue());
                        });
                        column.setEditable(true);
                        tblExcel.getColumns().add(column);
                    }
                }

                // Leer datos de celdas
                tblExcel.getItems().clear();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] rowData = line.split(",");
                    ObservableList<Celda<String>> row = FXCollections.observableArrayList();
                    for (String cellData : rowData) {
                        row.add(new Celda<>(cellData));
                    }
                    tblExcel.getItems().add(row);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirVistaHash() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaHash.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Tabla Hash");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}