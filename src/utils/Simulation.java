package utils;

import model.Structure;

public class Simulation {


    public static Structure calculateGrains(Structure structure, String surroundingType) {
        Structure newStructure = new Structure(structure.getRows(), structure.getColumns());
        for (int i = 0; i < structure.getRows(); i++) {
            for (int j = 0; j < structure.getColumns(); j++) {
                int id = structure.getCells()[i][j].getId();
                if (id != 0) {
                    newStructure.fillCellWithId(i, j, id);
                    System.out.println("i = " + i + "j= " + j + "calc id = " + id);
                    switch (surroundingType) {
                        case "Moore":
                            mooreSurrounding(structure, newStructure, id, i, j);
                    }
                }

            }
        }
        return newStructure;
    }

    public static void mooreSurrounding(Structure structure, Structure newStructure, int id, int i, int j) {

        int startI = i - 1;
        int startJ = j - 1;
        int endI = i + 1;
        int endJ = j + 1;

        for (int k = startI; k <= endI; k++) {
            for (int l = startJ; l <= endJ; l++) {
                fillSpecificCell(structure, newStructure, id, k, l);
            }
        }

    }

    private static void fillSpecificCell(Structure structure, Structure newStructure, int id, int k, int l) {
        int tmpX;
        int tmpY;
        tmpX = k;
        tmpY = l;

        if (k == -1) tmpX = 0;
        if (k == structure.getColumns()) tmpX = structure.getColumns() - 1;
        if (l == -1) tmpY = 0;
        if (l == structure.getRows()) tmpY = structure.getRows() - 1;
        if (structure.getCells()[tmpX][tmpY].getId() == 0 || structure.getCells()[tmpX][tmpY].getId() == id)
            newStructure.fillCellWithId(tmpX, tmpY, id);

    }

    public static boolean isSimulationOver(Structure structure){
        for (int i = 0; i < structure.getRows(); i++) {
            for (int j = 0; j < structure.getColumns(); j++) {
                if(structure.getCells()[i][j].getId() == 0){
                    return false;
                }
            }
        }
        return true;
    }


}
