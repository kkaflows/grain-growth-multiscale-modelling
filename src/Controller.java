package sample;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Controller {
    int canvasWidth = 400;
    int canvasHeight = 400;
    int ROWS = 400;
    int COLUMNS = 400;
    int GRAIN_SIZE = 4;

    int GRAINS_COUNT = 0;

    boolean running = false;

    Board board;
    GraphicsContext graphicsContext;
    HashMap<Integer, Color> colorHashMap;
    List<Integer> dualPhaseIds;
    List<Integer> substructureIds;

    double[][] distributeEnergy;
    HashMap<Integer, Color> energyColorHashMap;

    private Stage dialogStage;

    @FXML
    Canvas canvas;
    @FXML
    Button runSimulationButton;
    @FXML
    Button addRandomGrainButton;
    @FXML
    Button exportToBmpButton;
    @FXML
    Button importFromBmpButton;
    @FXML
    Button exportToTxtButton;
    @FXML
    Button importFromTxtButton;
    @FXML
    Button reinitializeBoardButton;
    @FXML
    Button addRandomInclusionButton;
    @FXML
    Button addEdgeInclusionButton;
    @FXML
    Button clearBoardKeepStructureButton;
    @FXML
    Button clearBoardKeepOnlyBoundariesButton;
    @FXML
    Button boundariesOnSelectedGrainsButton;
    @FXML
    Button boundariesOnAllGrainsButton;
    @FXML
    Button stopSimulationButton;
    @FXML
    Button fillWholeBoardWithRandomGrainsButton;
    @FXML
    Button runSimulationMCButton;
    @FXML
    Button distributeEnergyButton;
    @FXML
    Button showDistributedEnergyButton;
    @FXML
    Button showBoardButton;
    @FXML
    Button recrystaliseButton;
    @FXML
    Button addRecrystalisedNucleonsButton;


    @FXML
    RadioButton substructureRadioButton;
    @FXML
    RadioButton dualPhaseRadioButton;
    @FXML
    RadioButton periodicBoundariesRadioButton;
    @FXML
    RadioButton homogenousRadioButton;
    @FXML
    RadioButton heterogenousRadioButton;

    @FXML
    TextField numberOfRandomNucleonsTextField;
    @FXML
    TextField canvasWidthTextField;
    @FXML
    TextField canvasHeightTextField;
    @FXML
    TextField grainSizeTextField;
    @FXML
    TextField numberOfInclusionsTextField;
    @FXML
    TextField inclusionSizeTextField;
    @FXML
    TextField probabilityTextField;
    @FXML
    TextField boundarySizeTextField;
    @FXML
    TextField numberOfRandomNucleonsMCTextField;
    @FXML
    TextField grainBoundaryEnergyTextField;
    @FXML
    TextField numberOfMCSimulationsTextField;
    @FXML
    TextField energyOnGrainsTextField;
    @FXML
    TextField energyOnBoundariesTextField;
    @FXML
    TextField thresholdTextField;
    @FXML
    TextField numberOfNucleonsRecrystalisationTextField;
    @FXML
    TextField numberOfMCRecrystalisationSimulationsTextField;


    @FXML
    ChoiceBox neighbourhoodTypeChoiceBox;
    @FXML
    ChoiceBox inclusionTypeChoiceBox;
    @FXML
    ChoiceBox numberOfNucleonsRecrystalisationTypeChoiceBox;
    @FXML
    ChoiceBox locationOfNucleonsRecrystalisationTypeChoiceBox;


    public void initialize() {
        initializeBoard();
        initializeChoiceBoxes();
        initializeRadioButtons();
        initializeTextFields();
        buttonsListeners();
        buttonsListenersMC();
        canvasOnClickListener();
    }

    private void buttonsListenersMC() {
        fillWholeBoardWithRandomGrainsButton.setOnMouseClicked(event -> fillWholeBoardWithRandomGrains());

        runSimulationMCButton.setOnMouseClicked(event -> runMCSimulation());

        distributeEnergyButton.setOnMouseClicked(event -> distributeEnergy());

        showDistributedEnergyButton.setOnMouseClicked(event -> drawDistributeEnergy());

        showBoardButton.setOnMouseClicked(event -> drawBoard());

        recrystaliseButton.setOnMouseClicked(event -> recrystalise());

        addRecrystalisedNucleonsButton.setOnMouseClicked(event -> addNucleonsRecrystalised());

    }


    private void initializeTextFields() {
        canvasWidthTextField.setText(String.valueOf(canvasWidth));
        canvasHeightTextField.setText(String.valueOf(canvasHeight));
        grainSizeTextField.setText(String.valueOf(GRAIN_SIZE));

        probabilityTextField.setText("100");

        numberOfRandomNucleonsTextField.setText("10");

        numberOfInclusionsTextField.setText("1");
        inclusionSizeTextField.setText("1");

        dualPhaseRadioButton.setSelected(true);

        boundarySizeTextField.setText("2");

//        Monte Carlo

        numberOfRandomNucleonsMCTextField.setText("10");
        grainBoundaryEnergyTextField.setText("1.0");
        numberOfMCSimulationsTextField.setText("1");

        homogenousRadioButton.setSelected(true);

        energyOnGrainsTextField.setText("2");
        energyOnBoundariesTextField.setText("5");

        thresholdTextField.setText("0.2");

        numberOfNucleonsRecrystalisationTextField.setText("10");

        numberOfMCRecrystalisationSimulationsTextField.setText("1");
    }

    private void canvasOnClickListener() {
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int x1 = x / GRAIN_SIZE;
                int y1 = y / GRAIN_SIZE;
                int id = board.getCellId(x1, y1);
                if (substructureRadioButton.isSelected()) {
                    if (substructureIds.contains(id)) {
                        substructureIds.remove(substructureIds.indexOf(id));
                    } else {
                        substructureIds.add(id);
                    }
                }
                if (dualPhaseRadioButton.isSelected()) {
                    if (dualPhaseIds.contains(id)) {
                        dualPhaseIds.remove(dualPhaseIds.indexOf(id));
                    } else {
                        dualPhaseIds.add(id);
                    }
                }

                drawBoard();
            }
        });

    }

    private void initializeRadioButtons() {
        ToggleGroup toggleGroup = new ToggleGroup();
        substructureRadioButton.setToggleGroup(toggleGroup);
        dualPhaseRadioButton.setToggleGroup(toggleGroup);

        ToggleGroup energyToggleGroup = new ToggleGroup();
        homogenousRadioButton.setToggleGroup(energyToggleGroup);
        heterogenousRadioButton.setToggleGroup(energyToggleGroup);

    }

    private void initializeChoiceBoxes() {
        neighbourhoodTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "Moore",
                "Von Neumann",
                "Moore - 4 rules"
        ));
        inclusionTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "Square",
                "Circle"
        ));
        numberOfNucleonsRecrystalisationTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "Constant",
                "Increasing",
                "At the beginning of simulation"
        ));
        locationOfNucleonsRecrystalisationTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "Grain Boundary",
                "Anywhere"
        ));
        neighbourhoodTypeChoiceBox.setValue("Moore");
        inclusionTypeChoiceBox.setValue("Square");
        numberOfNucleonsRecrystalisationTypeChoiceBox.setValue("At the beginning of simulation");
        locationOfNucleonsRecrystalisationTypeChoiceBox.setValue("Anywhere");
    }

    private void buttonsListeners() {
        runSimulationButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        running = true;
                        while (running && !board.isBoardFull(ROWS, COLUMNS)) {
                            System.out.println("run simulation  button");
                            board = calculateCycle(String.valueOf(neighbourhoodTypeChoiceBox.getValue()), Integer.parseInt(probabilityTextField.getText()));

                        }
                        System.out.println("calculated");
                        drawBoard();
                    }
                });
                thread.start();

            }
        });
        addRandomGrainButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("add random grains");
                addRandomGrain(Integer.parseInt(numberOfRandomNucleonsTextField.getText()));
                drawBoard();
            }
        });
        exportToBmpButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                exportToBMP();
            }
        });
        importFromBmpButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                importFromBmp();
            }
        });
        exportToTxtButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                exportToTxt();
            }
        });
        importFromTxtButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                importFromTxt();
            }
        });
        reinitializeBoardButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                canvasWidth = Integer.parseInt(canvasWidthTextField.getText());
                canvasHeight = Integer.parseInt(canvasHeightTextField.getText());
                GRAIN_SIZE = Integer.parseInt(grainSizeTextField.getText());
                initializeBoard();
            }
        });
        addRandomInclusionButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addRandomInclusions(Integer.parseInt(numberOfInclusionsTextField.getText()), String.valueOf(inclusionTypeChoiceBox.getValue()), Integer.parseInt(inclusionSizeTextField.getText()));
                drawBoard();
            }
        });

        addEdgeInclusionButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!board.isBoardEmpty(ROWS, COLUMNS)) {
                    addInclusionOnEdge(Integer.parseInt(numberOfInclusionsTextField.getText()), String.valueOf(inclusionTypeChoiceBox.getValue()), Integer.parseInt(inclusionSizeTextField.getText()));
                    drawBoard();
                } else {
                    System.out.println("board is empty");
                }

            }
        });
        clearBoardKeepStructureButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clearBoard(false);
                drawBoard();
            }
        });
        clearBoardKeepOnlyBoundariesButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clearBoard(true);
                drawBoard();
            }
        });
        boundariesOnSelectedGrainsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int size = Integer.parseInt(boundarySizeTextField.getText());
                if (size < 2) {
                    size = 2;
                    boundarySizeTextField.setText("2");
                }
                for (int i = 0; i < size; i++) {
                    board = drawBoundariesOnSelectedGrains();
                }
                drawBoard();
            }
        });
        boundariesOnAllGrainsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int size = Integer.parseInt(boundarySizeTextField.getText());
                if (size < 2) {
                    size = 2;
                }
                for (int i = 0; i < size; i++) {
                    board = drawBoundariesOnAllGrains();
                }
                drawBoard();
            }
        });
        stopSimulationButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                running = false;
            }
        });
    }

    private Board drawBoundariesOnAllGrains() {
        Board boardTmp = new Board(ROWS, COLUMNS);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (board.isCellOnBorder(i, j, ROWS, COLUMNS, periodicBoundariesRadioButton.isPressed())) {
                    boardTmp.fillCellId(i, j, -1);
                } else {
                    boardTmp.fillCellId(i, j, board.getCellId(i, j));
                }
            }
        }
        return boardTmp;
    }

    private Board drawBoundariesOnSelectedGrains() {
        Board boardTmp = new Board(ROWS, COLUMNS);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                int id = board.getCellId(i, j);
                if (dualPhaseIds.contains(id) || substructureIds.contains(id)) {
                    if (board.isCellOnBorder(i, j, ROWS, COLUMNS, periodicBoundariesRadioButton.isPressed())) {
                        boardTmp.fillCellId(i, j, -1);
                    } else {
                        boardTmp.fillCellId(i, j, id);
                    }
                } else {
                    boardTmp.fillCellId(i, j, id);
                }
            }
        }
        return boardTmp;
    }

    private void clearBoard(boolean keepOnlyBoundaries) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                int id = board.getCellId(i, j);
                if (dualPhaseIds.contains(id) || substructureIds.contains(id)) {
                    if (keepOnlyBoundaries) {
                        board.fillCellId(i, j, 0);
                    }
                } else {
                    if (id == -1) {
                        board.fillCellId(i, j, -1);
                    } else {
                        board.fillCellId(i, j, 0);
                    }
                }
            }
        }
    }

    private void addInclusionOnEdge(int count, String type, int size) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int x = Math.abs(random.nextInt() % ROWS);
            int y = Math.abs(random.nextInt() % COLUMNS);

            if (board.isCellOnBorder(x, y, ROWS, COLUMNS, periodicBoundariesRadioButton.isPressed())) {
                if (size == 1) {
                    board.fillCellId(x, y, -1);
                } else {
                    switch (type) {
                        case "Square": {
                            for (int j = -size / 2; j <= size / 2; j++) {
                                for (int k = -size / 2; k <= size / 2; k++) {
                                    board.fillCellId(x + j, y + k, -1);
                                }
                            }
                            break;
                        }
                        case "Circle": {
                            System.out.println("Circle");
                            for (int k = y - size; k < y + size; k++) {
                                for (int j = x; Math.pow((j - x), 2) + Math.pow((k - y), 2) <= Math.pow(size, 2); j--) {
                                    board.fillCellId(x + k, y + j, -1);
                                }
                                for (int j = x + 1; (j - x) * (j - x) + (k - y) * (k - y) <= size * size; j++) {
                                    board.fillCellId(x + k, y + j, -1);
                                }
                            }

                            break;
                        }
                    }
                }
            } else {
                i--;
            }
        }
    }

    private void addRandomInclusions(int count, String type, int size) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int x = Math.abs(random.nextInt() % ROWS);
            int y = Math.abs(random.nextInt() % COLUMNS);

            if (size == 1) {
                board.fillCellId(x, y, -1);
            } else {
                switch (type) {
                    case "Square": {
                        for (int j = 0; j < size; j++) {
                            for (int k = 0; k < size; k++) {
                                board.fillCellId(x + j, y + k, -1);
                            }
                        }
                        break;
                    }
                    case "Circle": {
                        System.out.println("Circle");
                        for (int k = y - size; k < y + size; k++) {
                            for (int j = x; Math.pow((j - x), 2) + Math.pow((k - y), 2) <= Math.pow(size, 2); j--) {
                                board.fillCellId(x + k, y + j, -1);
                            }
                            for (int j = x + 1; (j - x) * (j - x) + (k - y) * (k - y) <= size * size; j++) {
                                board.fillCellId(x + k, y + j, -1);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public void initializeBoard() {
        dialogStage = Main.getPrimaryStage();

        ROWS = canvasWidth / GRAIN_SIZE;
        COLUMNS = canvasHeight / GRAIN_SIZE;

        board = new Board(ROWS, COLUMNS);
        distributeEnergy = new double[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                distributeEnergy[i][j] = 0.0;
            }
        }

        graphicsContext = canvas.getGraphicsContext2D();
        colorHashMap = new HashMap<>();
        colorHashMap.put(0, Color.rgb(255, 255, 255));
        colorHashMap.put(-1, Color.rgb(0, 0, 0));

        energyColorHashMap = new HashMap<>();
        energyColorHashMap.put(0, Color.RED);
        colorHashMap.put(-1, Color.rgb(0, 0, 0));


        dualPhaseIds = new ArrayList<>();
        substructureIds = new ArrayList<>();

        drawBoard();
    }

    private void drawBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                int id = board.getCellId(i, j);
                if (dualPhaseIds.contains(id)) {
                    graphicsContext.setFill(Color.ROSYBROWN);
                } else {
                    graphicsContext.setFill(colorHashMap.get(id));
                }
                graphicsContext.fillRect(i * GRAIN_SIZE, j * GRAIN_SIZE, GRAIN_SIZE, GRAIN_SIZE);
            }
        }
    }

    private void addRandomGrain(int count) {
        Random random = new Random();
        for (int k = GRAINS_COUNT; k < GRAINS_COUNT + count; k++) {
            int i = Math.abs(random.nextInt() % ROWS);
            int j = Math.abs(random.nextInt() % COLUMNS);
            if (board.getCellId(i, j) != 0) {
                k--;
            } else {
                int id = k;
                board.fillCellId(i, j, id);
                addColor(id);
            }
        }
        GRAINS_COUNT += count;
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

    private Board calculateCycle(String type, int probability) {
        Board boardTmp = new Board(ROWS, COLUMNS);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                int id = board.getCellId(i, j);
                if (id == 0) {
                    int idToFill = 0;
                    switch (type) {
                        case "Moore": {
                            idToFill = checkMooreNeighbourhood(i, j);
                            break;
                        }
                        case "Von Neumann": {
                            idToFill = checkVonNeumannNeighbourhood(i, j);
                            break;
                        }
                        case "Moore - 4 rules": {
                            idToFill = checkMoore4RulesNeighbourhood(i, j, probability);
                            break;
                        }
                    }
                    boardTmp.fillCellId(i, j, idToFill);
                } else {
                    boardTmp.fillCellId(i, j, id);
                }
            }
        }

        return boardTmp;
    }

    private int checkMoore4RulesNeighbourhood(int i, int j, int probability) {
        HashMap<Integer, Integer> neighbours = new HashMap<>();
        for (int k = i - 1; k < i + 1; k++) {
            for (int l = j - 1; l < j + 1; l++) {
                int kk = k;
                int ll = l;
                if (periodicBoundariesRadioButton.isSelected()) {
                    if (k == -1) kk = ROWS - 1;
                    if (k == ROWS) kk = 0;
                    if (l == -1) ll = COLUMNS - 1;
                    if (l == COLUMNS) ll = 0;
                }
                try {
                    int id = board.getCellId(kk, ll);
                    if (id > 0) {
                        if (id > 0 && !dualPhaseIds.contains(id) && !substructureIds.contains(id)) {
                            if (neighbours.containsKey(id)) {
                                int idCount = neighbours.get(id);
                                neighbours.put(id, idCount + 1);
                            } else {
                                neighbours.put(id, 1);
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
        neighbours.put(0, 0);
        Map.Entry<Integer, Integer> maxEntry = getMaxKeyFromHashMap(neighbours);
        if (maxEntry.getValue() > 5) {
            return maxEntry.getKey();
        } else {
            int[] is = new int[]{i - 1, i, i, i + 1};
            int[] js = new int[]{j, j - 1, j + 1, j};
            for (int k = 0; k < 4; k++) {
                int kk = is[k];
                int ll = js[k];
                if (periodicBoundariesRadioButton.isSelected()) {
                    if (is[k] == -1) kk = ROWS - 1;
                    if (is[k] == ROWS) kk = 0;
                    if (js[k] == -1) ll = COLUMNS - 1;
                    if (js[k] == COLUMNS) ll = 0;
                }
                try {
                    int id = board.getCellId(kk, ll);
                    if (id > 0) {
                        if (id > 0 && !dualPhaseIds.contains(id) && !substructureIds.contains(id)) {
                            if (neighbours.containsKey(id)) {
                                int idCount = neighbours.get(id);
                                neighbours.put(id, idCount + 1);
                            } else {
                                neighbours.put(id, 1);
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
            maxEntry = getMaxKeyFromHashMap(neighbours);
            if (maxEntry.getValue() == 3) {
                return maxEntry.getKey();
            } else {
                int[] is2 = new int[]{i - 1, i, i, i + 1};
                int[] js2 = new int[]{j, j - 1, j + 1, j};
                for (int k = 0; k < 4; k++) {
                    int kk = is[k];
                    int ll = js[k];
                    if (periodicBoundariesRadioButton.isSelected()) {
                        if (is[k] == -1) kk = ROWS - 1;
                        if (is[k] == ROWS) kk = 0;
                        if (js[k] == -1) ll = COLUMNS - 1;
                        if (js[k] == COLUMNS) ll = 0;
                    }
                    try {
                        int id = board.getCellId(kk, ll);
                        if (id > 0) {
                            if (id > 0 && !dualPhaseIds.contains(id) && !substructureIds.contains(id)) {
                                if (neighbours.containsKey(id)) {
                                    int idCount = neighbours.get(id);
                                    neighbours.put(id, idCount + 1);
                                } else {
                                    neighbours.put(id, 1);
                                }
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                maxEntry = getMaxKeyFromHashMap(neighbours);
                if (maxEntry.getValue() == 3) {
                    return maxEntry.getKey();
                } else {
                    Random random = new Random();
                    if (random.nextInt() % 100 < probability)
                        return checkMooreNeighbourhood(i, j);
                    else
                        return 0;
                }
            }
        }

    }

    private int checkVonNeumannNeighbourhood(int i, int j) {
        HashMap<Integer, Integer> neighbours = new HashMap<>();
        int[] is = new int[]{i - 1, i, i, i + 1};
        int[] js = new int[]{j, j - 1, j + 1, j};
        for (int k = 0; k < 4; k++) {

            int kk = is[k];
            int ll = js[k];
            if (periodicBoundariesRadioButton.isSelected()) {
                if (is[k] == -1) kk = ROWS - 1;
                if (is[k] == ROWS) kk = 0;
                if (js[k] == -1) ll = COLUMNS - 1;
                if (js[k] == COLUMNS) ll = 0;
            }
            try {
                int id = board.getCellId(kk, ll);
                if (id > 0) {
                    if (id > 0 && !dualPhaseIds.contains(id) && !substructureIds.contains(id)) {
                        if (neighbours.containsKey(id)) {
                            int idCount = neighbours.get(id);
                            neighbours.put(id, idCount + 1);
                        } else {
                            neighbours.put(id, 1);
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        neighbours.put(0, 0);
        Map.Entry<Integer, Integer> maxEntry = getMaxKeyFromHashMap(neighbours);
        return maxEntry.getKey();
    }

    private int checkMooreNeighbourhood(int i, int j) {
        HashMap<Integer, Integer> neighbours = new HashMap<>();
        for (int k = i - 1; k <= i + 1; k++) {
            for (int l = j - 1; l <= j + 1; l++) {
                int kk = k;
                int ll = l;
                if (periodicBoundariesRadioButton.isSelected()) {
                    if (k == -1) kk = ROWS - 1;
                    if (k == ROWS) kk = 0;
                    if (l == -1) ll = COLUMNS - 1;
                    if (l == COLUMNS) ll = 0;
                }
                try {
                    int id = board.getCellId(kk, ll);
                    if (id > 0 && !dualPhaseIds.contains(id) && !substructureIds.contains(id)) {
                        if (neighbours.containsKey(id)) {
                            int idCount = neighbours.get(id);
                            neighbours.put(id, idCount + 1);
                        } else {
                            neighbours.put(id, 1);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }
        }
        neighbours.put(0, 0);
        Map.Entry<Integer, Integer> maxEntry = getMaxKeyFromHashMap(neighbours);
        return maxEntry.getKey();
    }

    private Map.Entry<Integer, Integer> getMaxKeyFromHashMap(HashMap<Integer, Integer> neighbours) {
        Map.Entry<Integer, Integer> maxEntry = null;

        for (Map.Entry<Integer, Integer> entry : neighbours.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) >= 0) {
                maxEntry = entry;
            }
        }
        return maxEntry;
    }

    private int getKeyFromValueHashMap(HashMap<Integer, Color> colorHashMap, Color color) {

        for (Map.Entry<Integer, Color> entry : colorHashMap.entrySet()) {
            if (entry.getValue().equals(color)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public void exportToBMP() {
        // BufferedImage bimage =new BufferedImage() canvas.snapshot(new SnapshotParameters(), null).;
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save canvas");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
        File file = fileChooser.showSaveDialog(dialogStage);
        if (file != null) {
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

                // create a blank, RGB, same width and height, and a white background
                BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                        bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);

                // write to jpeg file
                Boolean isFinish = ImageIO.write(newBufferedImage, "bmp", file);
                System.out.println(isFinish);

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void exportToTxt() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save microstructure to txt file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        File file = fileChooser.showSaveDialog(dialogStage);

        if (file != null) {
            try {
                PrintWriter printWriter;
                printWriter = new PrintWriter(file);
                printWriter.println(canvasWidth + ";" + canvasHeight + ";" + GRAIN_SIZE + ";");
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLUMNS; j++) {
                        String cell = i + ";" + j + ";" + board.getCellId(i, j) + ";";
                        printWriter.println(cell);
                    }
                }
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void importFromTxt() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load microstructure from txt file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        File file = fileChooser.showOpenDialog(dialogStage);

        if (file != null) {
            try {
                Scanner scanner = new Scanner(file);
                String data = scanner.nextLine();
                String[] parts = data.split(";");
                canvasWidth = Integer.parseInt(parts[0]);
                canvasHeight = Integer.parseInt(parts[1]);
                GRAIN_SIZE = Integer.parseInt(parts[2]);
                initialize();

                while (scanner.hasNextLine()) {
                    data = scanner.nextLine();
                    parts = data.split(";");
                    int i = Integer.parseInt(parts[0]);
                    int j = Integer.parseInt(parts[1]);
                    int id = Integer.parseInt(parts[2]);
                    board.fillCellId(i, j, id);
                    addColor(id);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            drawBoard();
        }
    }

    private void importFromBmp() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(dialogStage);

        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);

                canvasWidth = bufferedImage.getWidth();
                canvasHeight = bufferedImage.getHeight();

                canvasWidthTextField.setText(String.valueOf(canvasWidth));
                canvasHeightTextField.setText(String.valueOf(canvasHeight));

                GRAIN_SIZE = Integer.parseInt(grainSizeTextField.getText());

                initialize();
                int id = 1;

                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLUMNS; j++) {
                        int colorRGB = bufferedImage.getRGB(i * GRAIN_SIZE, j * GRAIN_SIZE);
                        int r = (colorRGB & 0x00ff0000) >> 16;
                        int g = (colorRGB & 0x0000ff00) >> 8;
                        int b = colorRGB & 0x000000ff;

                        Color color = Color.rgb(r, g, b);

                        if (!colorHashMap.containsValue(color)) {
                            colorHashMap.put(id, color);
                            board.fillCellId(i, j, id);
                            id++;
                        } else {
                            int idTmp = getKeyFromValueHashMap(colorHashMap, color);
                            board.fillCellId(i, j, idTmp);
                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        drawBoard();
    }


// Monte Carlo ////////////////////////////////////////////////////////////////////////////////////////////////////


    private void fillWholeBoardWithRandomGrains() {
        Random random = new Random();
        int noOfNucleaons = Integer.parseInt(numberOfRandomNucleonsMCTextField.getText());
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (!substructureIds.contains(board.getCellId(i, j)) && !dualPhaseIds.contains(board.getCellId(i, j))) {
                    int id;
                    do {
                        id = Math.abs((random.nextInt() % noOfNucleaons)) + 1;
                    } while (substructureIds.contains(id) || dualPhaseIds.contains(id));
                    System.out.println(id);
                    board.fillCellId(i, j, id);
                    addColor(id);
                }
            }
        }
        drawBoard();
    }

    private void runMCSimulation() {
        int numberOfSimulations = Integer.parseInt(numberOfMCSimulationsTextField.getText());
        double grainBoundaryEnergy = Double.parseDouble(grainBoundaryEnergyTextField.getText());

        for (int i = 0; i < numberOfSimulations; i++) {

            board = calculateCycleMC(grainBoundaryEnergy);


        }
        drawBoard();

    }

    private Board calculateCycleMC(double grainBoundaryEnergy) {
        Random random = new Random();

        Board boardTmp = new Board(ROWS, COLUMNS);
        List<Point> points = generateShuffledPoints();
        double energy, newEnergy;

        for (Point point : points) {

            int initialId = board.getCellId(point.getX(), point.y);
            if (!dualPhaseIds.contains(initialId) && !substructureIds.contains(initialId)) {
                energy = grainBoundaryEnergy * calculateEnergy(point.getX(), point.getY(), initialId);
                int newX, newY;
                int newId;
                do {
                    newX = pickNewPoint(point.getX(), ROWS);
                    newY = pickNewPoint(point.getY(), COLUMNS);
                    newId = board.getCellId(newX, newY);
                }
                while ((newX == point.getX() && point.getY() == newY) || substructureIds.contains(newId) || dualPhaseIds.contains(newId));

                newEnergy = grainBoundaryEnergy * calculateEnergy(point.x, point.y, newId);

                if (newEnergy - energy <= 0) {
                    boardTmp.fillCellId(point.x, point.y, newId);
                } else {
                    boardTmp.fillCellId(point.x, point.y, initialId);
                }
            } else {
                boardTmp.fillCellId(point.x, point.y, initialId);
            }
        }


        return boardTmp;
    }

    private int pickNewPoint(int x, int rows) {
        Random random = new Random();
        int newX;
        do {
            newX = x;
            int add = random.nextInt() % 2;
            newX += add;
        } while (newX < 0 || newX >= rows);
        return newX;
    }

    private List<Point> generateShuffledPoints() {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Point point = new Point(i, j);
                points.add(point);
            }
        }
        Collections.shuffle(points);
        return points;
    }

    private double calculateEnergy(int x, int y, int initialId) {
        double energy = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                int kk = i;
                int ll = j;
                if (periodicBoundariesRadioButton.isSelected()) {
                    if (i == -1) kk = ROWS - 1;
                    if (i == ROWS) kk = 0;
                    if (j == -1) ll = COLUMNS - 1;
                    if (j == COLUMNS) ll = 0;
                }
                try {
                    int foundId = board.getCellId(kk, ll);

                    if (initialId != foundId) {
                        energy++;
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }
        }
        return energy;
    }


    private void recrystalise() {

        int simulations = Integer.parseInt(numberOfMCRecrystalisationSimulationsTextField.getText());
        int numberOfNucleons = Integer.parseInt(numberOfNucleonsRecrystalisationTextField.getText());
        String numberOfNucleonsType = String.valueOf(numberOfNucleonsRecrystalisationTypeChoiceBox.getValue());
        String locationOfNucleonsType = String.valueOf(locationOfNucleonsRecrystalisationTypeChoiceBox.getValue());


        if(numberOfNucleonsType.equals("At the beginning of simulation")){
            if(locationOfNucleonsType.equals("Anywhere")){
                addRandomRecrystalisedGrains(numberOfNucleons);
            }else if(locationOfNucleonsType.equals("Grain Boundary")){
                addOnBoundariesRecrystalisedGrains(numberOfNucleons);
            }
            drawBoard();
        }

        for (int i = 0; i < simulations; i++) {

            switch(numberOfNucleonsType){
                case "Increasing": {
                    numberOfNucleons +=numberOfNucleons;
                    break;
                }
                case "At the beginning of simulation":{
                    numberOfNucleons = 0;
                    break;
                }
            }

            if(locationOfNucleonsType.equals("Anywhere")){
                addRandomRecrystalisedGrains(numberOfNucleons);
            }else if(locationOfNucleonsType.equals("Grain Boundary")){
                addOnBoundariesRecrystalisedGrains(numberOfNucleons);
            }

            board = calculateCycleMCRecrystalisation();

        }
        drawBoard();
    }

    private void addOnBoundariesRecrystalisedGrains(int numberOfNucleons) {
        Random random = new Random();
        for (int k = GRAINS_COUNT; k < GRAINS_COUNT + numberOfNucleons; k++) {
            int i = Math.abs(random.nextInt() % ROWS);
            int j = Math.abs(random.nextInt() % COLUMNS);
            if(board.isCellOnBorder(i,j, ROWS,COLUMNS, periodicBoundariesRadioButton.isPressed())){
                if (board.getCellId(i, j) == -1) {
                    k--;
                } else {
                    int id = k;
                    board.fillCellId(i, j, id);
                    board.setCellIsRecrystilised(i, j, true);
                    distributeEnergy[i][j] = 0;
                    addColorRecrystalised(id);
                }
            }else{
                k--;
            }

        }
        GRAINS_COUNT += numberOfNucleons;
    }

    private void addNucleonsRecrystalised() {
        int numberOfNucleons = Integer.parseInt(numberOfNucleonsRecrystalisationTextField.getText());
        String numberOfNucleonsType = String.valueOf(numberOfNucleonsRecrystalisationTypeChoiceBox.getValue());
        if (numberOfNucleonsType.equals("At the beginning of simulation")) {
            addRandomRecrystalisedGrains(numberOfNucleons);
            drawBoard();
        }
    }

    private Board calculateCycleMCRecrystalisation() {

        Board boardTmp = new Board(ROWS, COLUMNS);
        List<Point> points = generateShuffledPoints();
        double energy, newEnergy;

        double grainBoundaryEnergy = Double.parseDouble(grainBoundaryEnergyTextField.getText());

        for (Point point : points) {
            if (!board.getCellIsRecrystalised(point.x, point.y)) {
                int initialId = board.getCellId(point.x, point.y);
                if (board.getCellId(point.x, point.y) != -1) {
                    energy = (calculateEnergy(point.x, point.y, initialId) * grainBoundaryEnergy) + distributeEnergy[point.x][point.y];

                    Point recrystalisedPoint = findRecrystalisedNeighbour(point.x, point.y);
                    if (recrystalisedPoint == null) {
                        boardTmp.fillCellId(point.x, point.y, initialId);
                        boardTmp.setCellIsRecrystilised(point.x, point.y, false);
                    } else {

                        int newId = board.getCellId(recrystalisedPoint.x, recrystalisedPoint.y);
                        newEnergy = calculateEnergy(point.x, point.y, newId) * grainBoundaryEnergy;

                        if (newEnergy - energy <= 0) {
                            boardTmp.fillCellId(point.x, point.y, newId);
                            boardTmp.setCellIsRecrystilised(point.x, point.y, true);
                            distributeEnergy[point.x][point.y] = 0;
                        }else{
                            boardTmp.fillCellId(point.x, point.y, initialId);
                            boardTmp.setCellIsRecrystilised(point.x, point.y, false);
                        }
                    }
                }
            } else {
                boardTmp.fillCellId(point.x, point.y, board.getCellId(point.x, point.y));
                boardTmp.setCellIsRecrystilised(point.x, point.y, true);
            }
        }
        return boardTmp;
    }

    private Point findRecrystalisedNeighbour(int x, int y) {

        List<Point> recrystalisedNeighbours = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                try {
                    if (board.getCellIsRecrystalised(i, j)) {
                        Point p = new Point(i, j);
                        recrystalisedNeighbours.add(p);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.shuffle(recrystalisedNeighbours);

        if (recrystalisedNeighbours.isEmpty()) {
            return null;
        } else {
            return recrystalisedNeighbours.get(0);
        }
    }

    private void addRandomRecrystalisedGrains(int count) {
        Random random = new Random();
        for (int k = GRAINS_COUNT; k < GRAINS_COUNT + count; k++) {
            int i = Math.abs(random.nextInt() % ROWS);
            int j = Math.abs(random.nextInt() % COLUMNS);
            if (board.getCellId(i, j) == -1) {
                k--;
            } else {
                int id = k;
                board.fillCellId(i, j, id);
                board.setCellIsRecrystilised(i, j, true);
                distributeEnergy[i][j] = 0;
                addColorRecrystalised(id);
            }
        }
        GRAINS_COUNT += count;
    }

    public void addColorRecrystalised(int id) {
        Random random = new Random();

        boolean containsId = colorHashMap.containsKey(id);
        if (!containsId) {
            int r = Math.abs(random.nextInt() % 255);
            Color color = Color.rgb(r, 0, 0);

            colorHashMap.put(id, color);
        }
    }

    private void drawDistributeEnergy() {
        double energyGrain = Double.parseDouble(energyOnGrainsTextField.getText());
        double energyBoundary = Double.parseDouble(energyOnBoundariesTextField.getText());
        double threshold = Double.parseDouble(thresholdTextField.getText());
        double minValueGrain = energyGrain - energyGrain * threshold;
        double maxValueGrain = energyGrain + energyGrain * threshold;
        double minValueBoundary = energyBoundary - energyBoundary * threshold;
        double maxValueBoundary = energyBoundary + energyBoundary * threshold;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {

                double energy = distributeEnergy[i][j];
                double colorValue = 200;
                if (energy >= minValueGrain && energy <= maxValueGrain) {
                    double colorAdd = Math.abs(energy - energyGrain) / (maxValueGrain - energyGrain);
                    colorValue += (55 * colorAdd)%55;
                    if(colorValue > 255)
                        colorValue = 255;
                    if (energyGrain > energyBoundary) {
                        graphicsContext.setFill(Color.rgb((int) colorValue, (int) colorValue, 0));
                    } else {
                        graphicsContext.setFill(Color.rgb(0, 0, (int) colorValue));
                    }

                }
                if (energy >= minValueBoundary && energy <= maxValueBoundary) {
                    double colorAdd = Math.abs(energy - energyBoundary) / (maxValueBoundary - energyBoundary);
                    colorValue += (55 * colorAdd)%55;
                    if(colorValue > 255)
                        colorValue = 255;
                    if (energyGrain < energyBoundary) {
                        graphicsContext.setFill(Color.rgb((int) colorValue, (int) colorValue, 0));
                    } else {
                        graphicsContext.setFill(Color.rgb(0, 0, (int) colorValue));
                    }
                }


                if (energy == 0) {
                    if (board.getCellIsRecrystalised(i, j)) {
                        graphicsContext.setFill(Color.RED);
                    } else {
                        graphicsContext.setFill(Color.WHITE);

                    }
                }

                graphicsContext.fillRect(i * GRAIN_SIZE, j * GRAIN_SIZE, GRAIN_SIZE, GRAIN_SIZE);
            }
        }
    }

    private void distributeEnergy() {
        Random random = new Random();

        distributeEnergy = new double[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                distributeEnergy[i][j] = 0.0;
            }
        }

        double energyGrain = Double.parseDouble(energyOnGrainsTextField.getText());
        double threshold = Double.parseDouble(thresholdTextField.getText());

        if (homogenousRadioButton.isSelected()) {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    double add = random.nextDouble();
                    if (add < threshold) {
                        distributeEnergy[i][j] = energyGrain + (energyGrain * add);
                    } else {
                        distributeEnergy[i][j] = energyGrain;
                    }
                }
            }
        } else if (heterogenousRadioButton.isSelected()) {
            double energyBoundary = Double.parseDouble(energyOnBoundariesTextField.getText());
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    double add = random.nextDouble();
                    if (board.isCellOnBorder(i, j, ROWS, COLUMNS, periodicBoundariesRadioButton.isPressed())) {
                        if (add < threshold) {
                            distributeEnergy[i][j] = energyBoundary + (energyBoundary * add);
                        } else {
                            distributeEnergy[i][j] = energyBoundary;
                        }
                    } else {
                        if (add < threshold) {
                            distributeEnergy[i][j] = energyGrain + (energyGrain * add);
                        } else {
                            distributeEnergy[i][j] = energyGrain;
                        }
                    }
                }
            }
        }
    }

}
