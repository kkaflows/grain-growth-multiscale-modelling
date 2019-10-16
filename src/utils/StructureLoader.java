package utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import model.Structure;

import javax.imageio.ImageIO;
import java.io.*;

public class StructureLoader {

    public static void saveStructureToText(Structure structure, String filename){
        StringBuilder structureToText = new StringBuilder();
        for (int i = 0; i < structure.getRows(); i++) {
            for (int j = 0; j < structure.getColumns(); j++) {
                structureToText.append(i);
                structureToText.append(";");
                structureToText.append(j);
                structureToText.append(";");
                structureToText.append(structure.getCells()[i][j].getId());
                structureToText.append(";");
                structureToText.append("\n");
            }
        }

        try (PrintStream out = new PrintStream(new FileOutputStream(filename+".txt"))) {
            String text= "text";
            out.print(structureToText.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
