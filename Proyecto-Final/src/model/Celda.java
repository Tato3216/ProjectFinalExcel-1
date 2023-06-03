package model;

import java.util.List;

/**
 *
 * @author Oscar
 */
public class Celda<T> {

    //Atributos
    private T contenidoCelda;

    //Constructor
    public Celda(T contenidoCelda) {
        this.contenidoCelda = contenidoCelda;
    }

    //Metodos Get y Set
    public T getContenidoCelda() {
        return contenidoCelda;
    }

    public void setContenidoCelda(T contenidoCelda) {
        this.contenidoCelda = contenidoCelda;
    }

    
}
