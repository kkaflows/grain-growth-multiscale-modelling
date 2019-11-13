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
import javax.xml.soap.Text;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//TODO: typy inclusions / import, export


public class Controller {
    int canvasWidth = 400;
    int canvasHeight = 400;
    int ROWS = 400;
    int COLUMNS = 400;
    int GRAIN_SIZE = 1;

    int GRAINS_COUNT = 0;

    boolean running = false;

    Board board;
    GraphicsContext graphicsContext;
    HashMap<Integer, Color> colorHashMap;
    List<Integer> dualPhaseIds;
    List<Integer> substructureIds;

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
    RadioButton substructureRadioButton;
    @FXML
    RadioButton dualPhaseRadioButton;
    @FXML
    RadioButton periodicBoundariesRadioButton;

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
    ChoiceBox neighbouthoodTypeChoiceBox;
    @FXML
    ChoiceBox inclusionTypeChoiceBox;

    public void initialize() {
        initializeBoard();
        initializeChoiceBoxes();
        initializeRadioButtons();
        initializeTextFields();
        buttonsListeners();
        canvasOnClickListener();
    }

    private void initializeTextFields() {
//        canvasWidthTextField.setText("400");
//        canvasHeightTextField.setText("400");
//        grainSizeTextField.setText("1");
        canvasWidthTextField.setText(String.valueOf(canvasWidth));
        canvasHeightTextField.setText(String.valueOf(canvasHeight));
        grainSizeTextField.setText(String.valueOf(GRAIN_SIZE));

        probabilityTextField.setText("100");

        numberOfRandomNucleonsTextField.setText("10");

        numberOfInclusionsTextField.setText("1");
        inclusionSizeTextField.setText("1");

        dualPhaseRadioButton.setSelected(true);

        boundarySizeTextField.setText("2");
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
    }

    private void initializeChoiceBoxes() {
        neighbouthoodTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "Moore",
                "Von Neumann",
                "Moore - 4 rules"
        ));
        inclusionTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "Square",
                "Circle"
        ));
        neighbouthoodTypeChoiceBox.setValue("Moore");
        inclusionTypeChoiceBox.setValue("Square");
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
                            board = calculateCycle(String.valueOf(neighbouthoodTypeChoiceBox.getValue()), Integer.parseInt(probabilityTextField.getText()));

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
                            for (int k = y-size; k < y+size; k++) {
                                for (int j = x; Math.pow((j-x),2) + Math.pow((k-y),2) <= Math.pow(size,2); j--) {
                                    board.fillCellId(x + k, y + j, -1);
                                }
                                for (int j = x+1; (j-x)*(j-x) + (k-y)*(k-y) <= size*size; j++) {
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
            }else {
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
        graphicsContext = canvas.getGraphicsContext2D();
        colorHashMap = new HashMap<>();
        colorHashMap.put(0, Color.rgb(255, 255, 255));
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

}
