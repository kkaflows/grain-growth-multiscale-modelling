package view;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.SimulationProperties;
import model.Structure;
import utils.Drawing;
import utils.Simulation;
import utils.StructureLoader;

import java.util.Random;

public class MainController {

    SimulationProperties simulationProperties;
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
    private void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();
        xBoardSizeTextField.setText("600");
        yBoardSizeTextField.setText("600");
        sizeOfGrainTextField.setText("5");

        initializeSimulationProperties();

        buttonsListeners();

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
                StructureLoader.loadStructureFromText("structure");
            }
        });
        surroundingTypeChoiceBox.setItems(FXCollections.observableArrayList(
                "Moore"));

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


}
