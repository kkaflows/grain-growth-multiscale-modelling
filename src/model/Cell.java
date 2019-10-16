package model;

import javafx.scene.paint.Color;


public class Cell {


    boolean isAlive;
    int id=0,size;
    String description;
    Color color;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void fillCell(String stateString, boolean stateBoolean, int stateInt) {
        this.description = stateString;
        this.isAlive = stateBoolean;
        this.id = stateInt;
    }

    public void fillCellId(int id){
        this.id = id;
    }
}
