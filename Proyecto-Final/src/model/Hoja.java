package model;

import java.util.ArrayList;
import java.util.List;

public class Hoja<T> {

    // Atributos
    private List<List<Celda<T>>> matriz;

    public Hoja() {
        matriz = new ArrayList<>(); // Inicializa la matriz como una instancia de ArrayList vacía
    }

    // Agrega una fila a la matriz de la hoja de cálculo
    public void addRow(List<Celda<T>> fila) {
        matriz.add(fila); // Agrega la fila a la matriz
    }

    // Establece una celda en una posición específica de la matriz
    public void setCelda(int rowIndex, int columnIndex, Celda<T> celda) {
        if (rowIndex >= 0 && rowIndex < matriz.size()) { // Verifica que el índice de fila esté dentro de los límites de la matriz
            List<Celda<T>> row = matriz.get(rowIndex); // Obtiene la fila correspondiente al índice de fila
            if (columnIndex >= 0 && columnIndex < row.size()) { // Verifica que el índice de columna esté dentro de los límites de la fila
                T contenidoCelda = celda.getContenidoCelda(); // Obtiene el contenido de la celda
                if (contenidoCelda != null) { // Verifica que el contenido de la celda no sea nulo
                    String contenido = contenidoCelda.toString(); // Convierte el contenido de la celda a una cadena de texto
                    if (contenido.isEmpty()) { // Verifica si el contenido de la celda es una cadena vacía
                        row.remove(columnIndex); // Si es una cadena vacía, elimina la celda de la fila
                    } else {
                        row.set(columnIndex, celda); // Si no es una cadena vacía, establece la celda en la posición de la fila correspondiente al índice de columna
                    }
                }
            }
        }
    }
}
