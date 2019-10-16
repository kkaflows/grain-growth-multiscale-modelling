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
            out.print(structureToText.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static Structure loadStructureFromText(String filename){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename+".txt"));
            System.out.println(reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Structure(2,2);
    }
}
