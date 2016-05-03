/**
 * File Handler  class is responsible for the input and
 * output of files to read patterns in RLE format via disc
 * and web.
 *
 * @author Rudi André Dahle
 * @author Olav Smevoll
 * @author Ginelle Ignacio
 */

package sample;

import javafx.stage.FileChooser;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileHandler {

    //Data field
    File file;
   // private boolean[][] loadBoard;
    private List<List<Boolean>> loadDynamicBoard = new ArrayList<>();

    public FileHandler(){
    }

    /**
     * File Handler class has a default constructor
     * that receives no arguments.
     */
    public FileHandler(Graphics gc, Board statBoard, Alerts alerts) {
        this.graphics = gc;
        this.gameBoard = statBoard;
        this.alerts = alerts;
    }

    //Object
    Alerts alerts;
    Board gameBoard;
    Graphics graphics;
    DynamicBoard dynamicBoard = DynamicBoard.getInstance();

    public void createLoadBoard(){
        int boardSize = dynamicBoard.getBoardSize();
        for (int x = 0; x < boardSize; x++) {
            List<Boolean> innerArray = new ArrayList<>();
            for(int y = 0; y < boardSize; y++){
                innerArray.add(false);
            }
            loadDynamicBoard.add(innerArray);
        }
    }

    public void addToLoadArray(){
        for(int x = 0; x < 5; x++) {
            List<Boolean> innerArray = new ArrayList<>();
            for(int y = 0; y < dynamicBoard.getBoardSize()-5; y++){
                innerArray.add(false);
            }
            loadDynamicBoard.add(innerArray);
        }
        for(int x = 0; x < dynamicBoard.getBoardSize(); x++){
            for(int y = 0; y < 5; y++) {
                loadDynamicBoard.get(x).add(false);
            }
        }
    }

    /**
     *
     * @param loadBoard
     */
   // public void setLoadBoard(boolean[][] loadBoard){
    //    this.loadBoard = loadBoard;
   // }



    /**
     * Method called to read an RLE file stored in the
     * computer's disk. Program will show a dialog box
     * containing RLE files which the user can choose.
     *
     * @author Rudi André Dahle
     * @throws IOException if an error occurs while opening file
     * @throws PatternFormatExceptions Exceptions related to file handling
     */
    public void chooseFile() throws IOException, PatternFormatExceptions {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"/rle"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Game of Life File ", "*.rle"));
        file = fileChooser.showOpenDialog(null);

        if (file != null) {
            readGameBoardFromFile(file);
        } else {
            alerts.noFile();
            throw new PatternFormatExceptions("No file was chosen");
        }
    }


    /**
     * Method called to read the content of file
     * by using File Reader and Buffered Reader.
     *
     * @author Olav Smevoll
     * @param file Receives a file selected by the user
     *             that the Buffered Reader will read
     * @throws IOException If an error occurs when reading the file
     * @throws PatternFormatExceptions Exceptions related to file handling
     */
    public void readGameBoardFromFile(File file) throws IOException,
            PatternFormatExceptions {

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String line;
        String rleCode = "";
        while ((line = br.readLine()) != null) {
            if ((line.matches("[b, o, $, !, 0-9]*"))) {
                rleCode = rleCode.concat(line + "\n");
            }
        }
        fromRleToSimplified(rleCode);
       // System.out.println("Leser rle: " + rleCode + "\n");
        long stop = System.currentTimeMillis();
   //     System.out.println("readGameBoardFromFile: " + (stop - start)+"ms");
    }


    /**
     * Method called to read the content of an RLE file
     * or URL by using Pattern and Matcher.
     *
     * @author Olav Smevoll
     * @param rle Receives a string representation of
     *            a RLE file
     */
    public String fromRleToSimplified(String rle) {
        StringBuilder finalRle = new StringBuilder();

        Pattern pattern = Pattern.compile("\\d+|[ob]|\\$");
        Matcher matcher = pattern.matcher(rle);

        while (matcher.find()) {
            int num = 1;
            if (matcher.group().matches("\\d+")) {
                num = Integer.parseInt(matcher.group());
                matcher.find();
            }
            for (int i = 0; i < num; i++) {
                finalRle.append(matcher.group());
            }
        }
        finalRle.toString();
        rleToArray(finalRle.toString());
        return finalRle.toString();
    }


    /**
     * This method makes it possible for the content of
     * the RLE file to be visible on the game board.
     *
     * @author Olav Smevoll
     * @param rle Receives a string representation of
     *            a RLE file
     */
    private void rleToArray(String rle) {
        int yCounter = 5;
        int xCounter = 5;
        createLoadBoard();
        dynamicBoard.resetDynamicBoard(loadDynamicBoard);
        //graphics.clearDynamicBoard();

        for (int i = 0; i < rle.length(); i++) {
            if(xCounter > loadDynamicBoard.size()-5 || yCounter > loadDynamicBoard.size()-5){
                dynamicBoard.addToArrayEastSouth();
                addToLoadArray();
                graphics.setCellHeight(dynamicBoard.cellsWide);
                graphics.setCellWidth(dynamicBoard.cellsWide);
            }
            if (rle.charAt(i) == '$') {
                yCounter++;
                xCounter = 5;
            }
            if (rle.charAt(i) == 'b') {
                loadDynamicBoard.get(xCounter).set(yCounter, false);
                xCounter++;
            }
            if (rle.charAt(i) == 'o') {
                loadDynamicBoard.get(xCounter).set(yCounter, true);
                xCounter++;
            }
        }
        dynamicBoard.setDynGameBoard(loadDynamicBoard);
    }
}