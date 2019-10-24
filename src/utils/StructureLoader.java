package utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import model.Cell;
import model.Structure;
import org.omg.CORBA.INTERNAL;
import sun.applet.Main;
import view.MainController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StructureLoader {

    public static void saveStructureToText(Structure structure, String filename) {
        StringBuilder structureToText = new StringBuilder();
        structureToText.append(structure.getRows());
        structureToText.append(";");
        structureToText.append(structure.getColumns());
        structureToText.append(";");
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

        try (PrintStream out = new PrintStream(new FileOutputStream(filename + ".txt"))) {
            out.print(structureToText.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static Structure loadStructureFromText(String filename) {
        Structure structure = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename + ".txt"));
            String size = reader.readLine();
            System.out.println(size);
            List<Integer> sizeParameters = new ArrayList<>();
            String tmp = "";
            for (int i = 0; i < size.length(); i++) {
                if (size.charAt(i) != ';')
                    tmp += size.charAt(i);
                else {
                    sizeParameters.add(Integer.parseInt(tmp));
                    tmp = "";
                }
            }

            structure = new Structure(sizeParameters.get(0), sizeParameters.get(1));
            List<Integer> parameters = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {

                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) != ';')
                        tmp += line.charAt(i);
                    else {
                        parameters.add(Integer.parseInt(tmp));
                        tmp = "";
                    }
                }
                structure.fillCellWithId(parameters.get(0), parameters.get(1), parameters.get(2));
                MainController.simulationProperties.addColor(parameters.get(2));
                System.out.println("reading lines 2 ");
                line = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return structure;
    }
//
//    public static void exportBMP(File file) throws NullPointerException {
//        Grid grid = DataModel.getGrid();
//
//        int width = grid.getWidth();
//        int height = grid.getHeight();
//
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        grid.getGrid().forEach(c -> {
//            int x = c.getX();
//            int y = c.getY();
//            int color = GraphicCalculator.RGBtoINT(DataModel.getColorForGrain(c.getState()));
//
//            image.setRGB(x, y, color);
//
//        });
//
//        try{
//            ImageIO.write(image, "bmp", file);
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    public static void importBMP(File file) throws NullPointerException{
//        try {
//            //TODO jezeli te smae ziarna sie nie stykaja to nie periodyczna
//            BufferedImage image = ImageIO.read(file);
//            Grid grid = new Grid(image.getWidth(), image.getHeight(), false);
//
//            DataModel.cleanDataModel();
//
//            HashMap<Color, Integer> grainsColors = new HashMap<>();
//            grainsColors.put(Color.WHITE, 0);
//            grainsColors.put(Color.BLACK, 1);
//
//            int numberOfGrains = 1;
//
//            for (int y = 0; y < image.getHeight(); y++) {
//                for (int x = 0; x < image.getWidth(); x++) {
//                    Color color = GraphicCalculator.INTtoRGB(image.getRGB(x, y));
//                    Cell cell = new Cell(x, y);
//                    if (color.equals(Color.WHITE)) {
//                        cell.setState(0);
//                    }
//                    else if(color.equals((Color.BLACK))) {
//                        DataModel.setNumberOfInclusions(1);
//                        cell.setState(1);
//                    }
//                    else if (grainsColors.containsKey(color)) {
//                        cell.setState(grainsColors.get(color));
//                    }
//                    else {
//                        numberOfGrains++;
//                        grainsColors.put(color, numberOfGrains);
//                        cell.setState(numberOfGrains);
//                    }
//                    grid.setCell(cell.getX(), cell.getY(), cell);
//                }
//            }
//
//            DataModel.setGrid(grid);
//            DataModel.setNumberOfGrains(numberOfGrains - 1);
//
//            Map<Integer, Color> colorsInversed = grainsColors.entrySet()
//                    .stream()
//                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
//            DataModel.setGrainsColors(colorsInversed);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
