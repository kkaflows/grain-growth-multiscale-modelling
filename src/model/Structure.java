package model;

public class Structure {

    Cell[][] cells;
    int rows, columns;

    public Structure(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        cells = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell();
                cells[i][j].fillCellId(0);
            }
        }
        clearBoard();
    }

    public void clearBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j].fillCell("", false, 0);
            }
        }
    }

    public void fillCellWithId(int x, int y, int id){
        cells[x][y].fillCellId(id);
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
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


}
