package model;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Random;

public class SimulationProperties {

    int canvasHeight, canvasWidth;
    int rows, columns; //calculated from canvasHeight and size of grain
    int sizeOfGrain;
    HashMap<Integer, Color> colorHashMap;

    public SimulationProperties(int xBoardSize, int yBoardSize, int sizeOfGrain) {
        this.canvasWidth = xBoardSize;
        this.canvasHeight = yBoardSize;
        this.sizeOfGrain = sizeOfGrain;
        rows = canvasWidth / sizeOfGrain;
        columns = canvasHeight / sizeOfGrain;
        colorHashMap = new HashMap<>();
        colorHashMap.put(0, Color.rgb(255, 255, 255));
    }


    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getSizeOfGrain() {
        return sizeOfGrain;
    }

    public void setSizeOfGrain(int sizeOfGrain) {
        this.sizeOfGrain = sizeOfGrain;
    }

    public HashMap<Integer, Color> getColorHashMap() {
        return colorHashMap;
    }

    public void setColorHashMap(HashMap<Integer, Color> colorHashMap) {
        this.colorHashMap = colorHashMap;
    }

    public void addColor(int id) {
        Random random = new Random();

        boolean containsId = colorHashMap.containsKey(id);
        if (!containsId) {
            int r = Math.abs(random.nextInt() % 255);
            int g = Math.abs(random.nextInt() % 255);
            int b = Math.abs(random.nextInt() % 255);
            Color color = Color.rgb(r, g, b);

            colorHashMap.put(id, color);
        }
    }
}
