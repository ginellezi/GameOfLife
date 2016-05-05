/**
 * Web Controller is responsible for the controllers in
 * the Web File FXML. This is the "Read Web File" under Menu.
 *
 * @author Ginelle Ignacio
 * @author Olav Smevoll
 * @author Rudi André Dahle
 */

package WebFile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import sample.*;

import javax.xml.bind.SchemaOutputResolver;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;




public class WebFileController implements Initializable {

    //Data field
    @FXML private TextField field;
    @FXML private Button submit;
    @FXML private Label label;

    //Objects
    FileHandler fileHandler;
    Alerts error;
    GraphicsContext gc;
    Graphics graphics;
    DynamicBoard dynamicBoard;
    Board gameBoard;

    /**
     * Web File Controller has a default constructor
     * that receives no arguments.
     *
     * @author Ginelle Ignacio
     * @author Olav Smevoll
     * @author Rudi André Dahle
     */
    public WebFileController(){

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dynamicBoard = DynamicBoard.getInstance();
        fileHandler = new FileHandler(graphics, gameBoard);
        error = new Alerts();
        graphics = new Graphics(gc);
    }


    /**
     * Gets the value of the field entered when the user
     * submits a web address.
     *
     * @author Ginelle Ignacio
     * @coauthor Olav Smevoll
     * @coauthor Rudi André Dahle
     * @param actionEvent Represents an action event used
     *                    when a text is entered
     */
    public void fieldEvent(ActionEvent actionEvent){
        field.getText();
    }

    /**
     * Method called when user submits a URL to read.
     * The program can only read RLE file that's within
     * the array range of the static board.
     *
     * @author Olav Smevoll
     * @cauthor Ginelle Ignacio
     * @coauthor Rudi André Dahle
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    @FXML
    public void submitEvent (ActionEvent actionEvent) throws Exception{
        //Changes the label text
        if (field.getText() != null && !field.getText().isEmpty()) {
            label.setText("Reading file from web..");
        } else {
            label.setText("No URL was inserted!");
        }

        //Reads the content of the URL to convert to RLE file
        try {
            URL url = new URL(field.getText());
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(url.openStream()));

            String readFromWeb;
            String rleCode = "";

            while ((readFromWeb = in.readLine()) != null) {
                if ((readFromWeb.matches("[b, o, $, !, 0-9]*"))) {
                    rleCode = rleCode.concat(readFromWeb + "\n");
                }
            }
            fileHandler.fromRleToSimplified(rleCode);
//          graphics.drawDynamic(dynamicBoard.getGameBoard());
        } catch (Exception e) {
            error.invalidURL();
        }

    }


    /**
     * Method called to clear the text in the text field
     * and removes the text at the bottom of the text field.
     *
     * @author Ginelle Ignacio
     * @coauthor Olav Smevoll
     * @coauthor Rudi André Dahle
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    @FXML
    public void clearEvent(ActionEvent actionEvent){
        field.clear();
        label.setText(null);
    }
}
