package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Hash {

    private ObservableList<Integer>[] table;
    private int size;

    public Hash(int size) {
        this.size = size;
        table = new ObservableList[size];
        initializeTable();
    }

    private void initializeTable() {
        for (int i = 0; i < size; i++) {
            table[i] = FXCollections.observableArrayList();
        }
    }

    public void insert(int key, int data) {
        int index = hashFunction(key);
        ObservableList<Integer> list = table[index];
        list.add(data);
    }

    public boolean contains(int key, int data) {
        int index = hashFunction(key);
        ObservableList<Integer> list = table[index];
        return list.contains(data);
    }

    public ObservableList<ObservableList<Integer>> getAllCubetas() {
        ObservableList<ObservableList<Integer>> cubetas = FXCollections.observableArrayList();
        for (ObservableList<Integer> cubeta : table) {
            cubetas.add(cubeta);
        }
        return cubetas;
    }

    public int hashFunction(int key) {
        return key % size;
    }
}
