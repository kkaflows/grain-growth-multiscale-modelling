package utils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Cell;
import model.SimulationProperties;
import model.Structure;

public class Drawing {

    public void clearCanvas(GraphicsContext graphicsContext, SimulationProperties simulationProperties) {
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, simulationProperties.getCanvasWidth(), simulationProperties.getCanvasHeight());
    }

    public void drawOnCanvasFromId(GraphicsContext graphicsContext, SimulationProperties simulationProperties, Structure structure) {
        Cell[][] cells = structure.getCells();
        for (int i = 0; i < structure.getRows(); i++) {
            for (int j = 0; j < structure.getColumns(); j++) {
                int id = cells[i][j].getId();
                graphicsContext.setFill(simulationProperties.getColorHashMap().get(id));

                graphicsContext.fillRect(i * simulationProperties.getSizeOfGrain(),
                        j * simulationProperties.getSizeOfGrain(),
                        simulationProperties.getSizeOfGrain(),
                        simulationProperties.getSizeOfGrain());


            }
        }
    }

}
