package view;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import model.SimulationProperties;
import model.Structure;
import sun.applet.Main;
import utils.Drawing;
import utils.Simulation;
import utils.StructureLoader;

import javax.imageio.ImageIO;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MainController {

    public static SimulationProperties simulationProperties;
    Structure structure;
    Drawing drawing;

    @FXML
    Canvas canvas;
    private GraphicsContext graphicsContext;

    @FXML
    TextField xBoardSizeTextField;

    @FXML
    TextField yBoardSizeTextField;

    @FXML
    TextField sizeOfGrainTextField;

    @FXML
    Button runSimulation;

    @FXML
    Button addRandomGrain;

    @FXML
    ChoiceBox surroundingTypeChoiceBox;

    @FXML
    Button addRandomInclusions;

    @FXML
    TextField numberOfInclusionsTextField;
    @FXML
    TextField sizeOfInclusionTextField;

    @FXML
    Button getAddRandomInclusionsOnEdgeOfGrain;

    @FXML
    Button saveStructureToBmp;


    @FXML
    private void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();
        xBoardSizeTextField.setText("600");
        yBoardSizeTextField.setText("600");
        sizeOfGrainTextField.setText("5");

        initializeSimulationProperties();

        buttonsListeners();

        drawOnCanvas();

    }

    private void buttonsListeners() {
        runSimulation.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Run simulation clicked");
                String surroundingType = (String) surroundingTypeChoiceBox.getValue();

                while (!Simulation.isSimulationOver(structure)) {
                    structure = Simulation.calculateGrains(structure, surroundingType);
                }

                drawing.drawOnCanvasFromId(graphicsContext, simulationProperties, structure);
                System.out.println("Drawed on Canvas");
            }
        });

        addRandomGrain.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("add random grain");
                addRandomGrains();
                drawing.drawOnCanvasFromId(graphicsContext, simulationProperties, structure);
//                StructureLoader.saveStructureToText(structure, "structure");
//                structure = StructureLoader.loadStructureFromText("structure");
//                drawing.drawOnCanvasFromId(graphicsContext, simulationProperties, structure);
            }
        });
        surroundingTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "Moore",
                "Von Neumann",
                "Moore - 4 rules"));

        addRandomInclusions.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addRandomInclusions(Integer.parseInt(numberOfInclusionsTextField.getText()), Integer.parseInt(sizeOfInclusionTextField.getText()));
                drawing.drawOnCanvasFromId(graphicsContext, simulationProperties, structure);

            }
        });

        getAddRandomInclusionsOnEdgeOfGrain.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addRandomInclusionsOnEdgeOfGrain(Integer.parseInt(numberOfInclusionsTextField.getText()), Integer.parseInt(sizeOfInclusionTextField.getText()));
                drawing.drawOnCanvasFromId(graphicsContext, simulationProperties, structure);
            }
        });

        saveStructureToBmp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveCanvasToBmp(structure);
            }
        });

    }

    private void initializeSimulationProperties() {
        int xBoardSize = Integer.parseInt(xBoardSizeTextField.getText());
        int yBoardSize = Integer.parseInt(yBoardSizeTextField.getText());
        int sizeOfGrain = Integer.parseInt(sizeOfGrainTextField.getText());
        simulationProperties = new SimulationProperties(xBoardSize, yBoardSize, sizeOfGrain);
        structure = new Structure(simulationProperties.getRows(), simulationProperties.getColumns());
        drawing = new Drawing();
        drawing.clearCanvas(graphicsContext, simulationProperties);
    }

    private void addRandomGrains() {
        Random random = new Random();
        for (int i = 0; i < 1; i++) {
            int x = Math.abs(random.nextInt() % (simulationProperties.getRows() - 2) + 1);
            int y = Math.abs(random.nextInt() % (simulationProperties.getColumns() - 2) + 1);

            int id = (Math.abs(random.nextInt() % 10) + 1);
            System.out.println("x = " + x + "y = " + y + "id = " + id);

            simulationProperties.addColor(id);

            structure.fillCellWithId(x, y, id);
        }
    }

    private void addRandomInclusions(int numberOfInclusions, int size) {
        Random random = new Random();
        for (int i = 0; i < numberOfInclusions; i++) {
            int x = Math.abs(random.nextInt() % (simulationProperties.getRows() - 2) + 1);
            int y = Math.abs(random.nextInt() % (simulationProperties.getColumns() - 2) + 1);

            int id = -1;
            System.out.println("x = " + x + "y = " + y + "id = " + id);

            for (int j = x; j < x + size; j++) {
                for (int k = y; k < y + size; k++) {
                    structure.fillCellWithId(j, k, -1);
                }
            }
        }
    }

    private void addRandomInclusionsOnEdgeOfGrain(int numberOfInclusions, int size) {
        int addedInclusions = 0;
        Random random = new Random();
        while (addedInclusions < numberOfInclusions) {
            int i = Math.abs(random.nextInt() % (simulationProperties.getRows() - 2) + 1);
            int j = Math.abs(random.nextInt() % (simulationProperties.getColumns() - 2) + 1);
            if (addedInclusions < size) {
                int id1 = structure.getCells()[i][j].getId();
                if (id1 > 0) {
                    boolean putInclusion = false;
                    for (int k = i; k <= i + 1; k++) {
                        for (int l = j; l <= j + 1; l++) {
                            if (id1 != structure.getCells()[k][l].getId() && structure.getCells()[k][l].getId() > 0) {
                                putInclusion = true;
                            }
                        }
                    }

                    if (putInclusion) {
                        for (int k = i; k < i + size; k++) {
                            for (int l = j; l < j + size; l++) {
                                structure.fillCellWithId(k, l, -1);
                            }
                        }
                        addedInclusions++;
                    }
                }
            }

        }
    }

    public void drawOnCanvas() {

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                x = x/Integer.parseInt(sizeOfGrainTextField.getText());
                y = y/Integer.parseInt(sizeOfGrainTextField.getText());
                structure.fillCellWithId(x,y,1);
                drawing.drawOnCanvasFromId(graphicsContext, simulationProperties, structure);

            }
        });
    }

    public void saveCanvasToBmp(Structure structure){

    }


}
