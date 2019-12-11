package sample;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Random;

public class Board {

    Cell[][] cells;

    public Board(int rows, int columns) {
        cells = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell();
                cells[i][j].setId(0);
            }
        }
    }

    public int getCellId(int i, int j) {
        return cells[i][j].getId();
    }

    public void fillCellId(int i, int j, int id) {
        cells[i][j].setId(id);
    }


    public boolean getCellIsRecrystalised(int i, int j){
        return cells[i][j].isRecrystalised();
    }

    public void setCellIsRecrystilised(int i, int j, boolean isRecrystalised){
        cells[i][j].setRecrystalised(isRecrystalised);
    }

    public boolean isBoardFull(int rows, int columns){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(getCellId(i,j) == 0){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isCellOnBorder(int x, int y,int ROWS,int COLUMNS, boolean periodic) {
        int id = getCellId(x,y);
        int[] is = new int[]{x - 1, x, x, x + 1};
        int[] js = new int[]{y, y - 1, y + 1, y};
        for (int i = 0; i < 4; i++) {
            int kk = is[i];
            int ll = js[i];
            if (periodic) {
                if (is[i] == -1) kk = ROWS - 1;
                if (is[i] == ROWS) kk = 0;
                if (js[i] == -1) ll = COLUMNS - 1;
                if (js[i] == COLUMNS) ll = 0;
            }

            try{
                int id2 = getCellId(kk,ll);
                if(id != id2){
                    return true;
                }
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }

        }




        return false;
    }

    public boolean isBoardEmpty(int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(getCellId(i,j) != 0){
                    return false;
                }
            }
        }
        return true;
    }
}
