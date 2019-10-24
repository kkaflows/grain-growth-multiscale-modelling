package utils;

import model.Structure;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Simulation {


    public static Structure calculateGrains(Structure structure, String surroundingType) {
        Structure newStructure = new Structure(structure.getRows(), structure.getColumns());
        for (int i = 0; i < structure.getRows(); i++) {
            for (int j = 0; j < structure.getColumns(); j++) {
                int id = structure.getCells()[i][j].getId();
                if (id > 0) {
//                if (id == 0) {
                    newStructure.fillCellWithId(i, j, id);
                    System.out.println("i = " + i + "j= " + j + "calc id = " + id);
                    switch (surroundingType) {
                        case "Moore": {
                            mooreSurrounding(structure, newStructure, id, i, j);
                            break;
                        }
                        case "Von Neumann": {
                            vonNeumannSurrounding(structure, newStructure, id, i, j);
                            break;
                        }
                        case "Moore - 4 rules": {
                            moore4RulesSurrounding(structure, newStructure, id, i, j, 50);
                            break;
                        }

                    }
                }
                if (id == -1) {
                    newStructure.fillCellWithId(i, j, id);
                }

            }
        }
        return newStructure;
    }

    private static void moore4RulesSurrounding(Structure structure, Structure newStructure, int id, int i, int j, int probability) {

        boolean isChanged = moore1rule(structure, newStructure, i, j);
        if (!isChanged) {
            isChanged = moore2rule(structure, newStructure, i, j);
        }
        if (!isChanged) {
            isChanged = moore3rule(structure, newStructure, i, j);
        }
        if (!isChanged) {
            isChanged = moore4rule(structure, newStructure, i, j, probability);
        }

    }

    private static boolean moore4rule(Structure structure, Structure newStructure, int i, int j, int probability) {
        int startI = i - 1;
        int startJ = j - 1;
        int endI = i + 1;
        int endJ = j + 1;

        HashMap<Integer, Integer> idFrequency = new HashMap<>();

        for (int k = startI; k <= endI; k++) {
            for (int l = startJ; l <= endJ; l++) {
                int id = structure.getCells()[i][j].getId();
                if (id > 0) {
                    if (idFrequency.containsKey(id)) {
                        int frequency = idFrequency.get(id);
                        frequency++;
                        idFrequency.put(id, frequency);
                    } else {
                        idFrequency.put(id, 1);
                    }
                }
            }
        }
        Set<Integer> keys = idFrequency.keySet();
        for (Integer key : keys) {
            System.out.println("key= " + key);
            int frequency = idFrequency.get(key);

            if (frequency > 5) {
                Random random = new Random();
                if(Math.abs(random.nextInt()%100) < probability)
                fillSpecificCell(structure, newStructure, key, i, j);
                return true;
            }
        }
        return false;

    }

    private static boolean moore3rule(Structure structure, Structure newStructure, int i, int j) {
        HashMap<Integer, Integer> coordinates = new HashMap<>();
        coordinates.put(i - 1, j - 1);
        coordinates.put(i - 1, j + 1);
        coordinates.put(i + 1, j - 1);
        coordinates.put(i + 1, j + 1);

        HashMap<Integer, Integer> idFrequency = new HashMap<>();

        Set<Integer> keys = coordinates.keySet();
        for (Integer x : keys) {
            int y = coordinates.get(x);
            int id = structure.getCells()[x][y].getId();
            if (id > 0) {
                if (idFrequency.containsKey(id)) {
                    int frequency = idFrequency.get(id);
                    frequency++;
                    idFrequency.put(id, frequency);
                } else {
                    idFrequency.put(id, 1);
                }
            }
        }
        Set<Integer> keysIds = idFrequency.keySet();
        for (Integer ids : keysIds) {
            int frequency = idFrequency.get(ids);
            if (frequency >= 3) {
                fillSpecificCell(structure, newStructure, ids, i, j);
                return true;
            }
        }
        return false;


    }

    private static boolean moore2rule(Structure structure, Structure newStructure, int i, int j) {
        HashMap<Integer, Integer> coordinates = new HashMap<>();
        coordinates.put(i, j - 1);
        coordinates.put(i - 1, j);
        coordinates.put(i, j + 1);
        coordinates.put(i + 1, j);

        HashMap<Integer, Integer> idFrequency = new HashMap<>();

        Set<Integer> keys = coordinates.keySet();
        for (Integer x : keys) {
            int y = coordinates.get(x);
            int id = structure.getCells()[x][y].getId();
            if (id > 0) {
                if (idFrequency.containsKey(id)) {
                    int frequency = idFrequency.get(id);
                    frequency++;
                    idFrequency.put(id, frequency);
                } else {
                    idFrequency.put(id, 1);
                }
            }
        }
        Set<Integer> keysIds = idFrequency.keySet();
        for (Integer ids : keysIds) {
            int frequency = idFrequency.get(ids);
            if (frequency >= 3) {
                fillSpecificCell(structure, newStructure, ids, i, j);
                return true;
            }
        }
        return false;

    }

    private static boolean moore1rule(Structure structure, Structure newStructure, int i, int j) {
        int startI = i - 1;
        int startJ = j - 1;
        int endI = i + 1;
        int endJ = j + 1;

        HashMap<Integer, Integer> idFrequency = new HashMap<>();

        for (int k = startI; k <= endI; k++) {
            for (int l = startJ; l <= endJ; l++) {
                int id = structure.getCells()[i][j].getId();
                if (id > 0) {
                    if (idFrequency.containsKey(id)) {
                        int frequency = idFrequency.get(id);
                        frequency++;
                        idFrequency.put(id, frequency);
                    } else {
                        idFrequency.put(id, 1);
                    }
                }
            }
        }
        Set<Integer> keys = idFrequency.keySet();
        for (Integer key : keys) {
            System.out.println("key= " + key);
            int frequency = idFrequency.get(key);
            if (frequency > 5) {
                fillSpecificCell(structure, newStructure, key, i, j);
                return true;
            }
        }
        return false;
    }

    private static void vonNeumannSurrounding(Structure structure, Structure newStructure, int id, int i, int j) {
        int leftX = i;
        int leftY = j - 1;
        int upX = i - 1;
        int upY = j;
        int rightX = i;
        int rightY = j + 1;
        int downX = i + 1;
        int downY = j;
        try {
            if (structure.getCells()[leftX][leftY].getId() == 0) {
                fillSpecificCell(structure, newStructure, id, leftX, leftY);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            if (structure.getCells()[upX][upY].getId() == 0) {
                fillSpecificCell(structure, newStructure, id, upX, upY);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            if (structure.getCells()[rightX][rightY].getId() == 0) {
                fillSpecificCell(structure, newStructure, id, rightX, rightY);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            if (structure.getCells()[downX][downY].getId() == 0) {
                fillSpecificCell(structure, newStructure, id, downX, downY);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

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

    public static boolean isSimulationOver(Structure structure) {
        for (int i = 0; i < structure.getRows(); i++) {
            for (int j = 0; j < structure.getColumns(); j++) {
                if (structure.getCells()[i][j].getId() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
