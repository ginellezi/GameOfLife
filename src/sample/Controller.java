/**
 * Controller is located at the bottom of the game window.
 * It contains all the buttons and sliders that the user will
 * use to manipulate the game.
 *
 * @author Ginelle Ignacio
 * @author Rudi André Dahle
 * @author Olav Smevoll
 */

package sample;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.Color;


public class Controller implements Initializable {

    //Data field related to FXML
    @FXML private Canvas canvas;
    @FXML private Canvas canvasGrid;
    @FXML private Canvas canvasBG;
    @FXML private ColorPicker colorPicker;
    @FXML private ColorPicker backgroundColor;
    @FXML private Slider speedSlider;
    @FXML private Button playPause;
    @FXML private Label genCounter;
    @FXML private Label fpsCount;
    @FXML private ToggleButton gridToggle;
    @FXML private TextArea tipField;
    @FXML private ComboBox cellShapeBox;

    //Data field
    private GraphicsContext gc;
    private GraphicsContext gcGrid;
    private GraphicsContext gcBG;
    private Timeline timeline;
    private boolean running = false;
    private boolean showGrid = false;
    private boolean shape = false;
    private double FPS;
    private double xCoord;
    private double yCoord;
    private long meanTime;


    //Objects
    Grid grid;
    Board gameBoard;
    Graphics graphics;
    FileHandler reader;
    Stage helpWindow;
    Stage readWeb;
    Alerts alerts;
    WorkerPool workerPool;
    DynamicBoard dynamicBoard;


    /**
     * Controller class has a default constructor that
     * receives no argument.
     */
    public Controller(){

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Fixes a bug problem within Java when using combo box
        System.setProperty("glass.accessible.force", "false");

        //Objects
        gc = canvas.getGraphicsContext2D();
        gcGrid = canvasGrid.getGraphicsContext2D();
        gcBG = canvasBG.getGraphicsContext2D();
        graphics = new Graphics(gc);
        grid = new Grid(gcGrid);
        reader = new FileHandler(graphics, gameBoard);
        helpWindow = new Stage();
        readWeb = new Stage();
        alerts = new Alerts();
        workerPool = new WorkerPool();
        dynamicBoard = DynamicBoard.getInstance();

        //Grid properties
        graphics.setCellHeight(dynamicBoard.cellsWide);
        graphics.setCellWidth(dynamicBoard.cellsWide);
        graphics.setXCell(xCoord);
        graphics.setYCell(yCoord);
        grid.setCanvasHeight(gcGrid.getCanvas().heightProperty().intValue());
        grid.setCanvasWidth(gcGrid.getCanvas().widthProperty().intValue());
        grid.setCellWidth(graphics.getCellWidth());
        grid.setCellHeight(graphics.getCellHeight());


        //Combo box list
        cellShapeBox.getItems().addAll(
                "Square",
                "Circle"
        );

        //Initial properties in the GUI
        tipField.setText("Welcome to Game of \nLife! You can draw your \nown pattern, " +
                "upload \na file, or read a file from \nweb to begin the game!");
        genCounter.setText(Integer.toString(dynamicBoard.getGenCounter()));
        graphics.gc.setFill(Color.rgb(26, 0, 104));
        colorPicker.setValue(Color.rgb(26, 0, 104));
        speedSlider.setValue(10.0);
        speedSlider.setShowTickMarks(true);
        FPS = speedSlider.getValue();
        fpsCount.setText((Integer.toString((int) speedSlider.getValue())+" fps"));
        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                fpsCount.setText(Math.round(newValue.intValue()) + " fps");
                tipField.setText("FPS is frames per second. \n");
            }
        });

        dynamicBoard.createArray();



        //Time properties responsible for the animation
        Duration duration = Duration.millis(1000);
        KeyFrame keyframe = new KeyFrame(duration, (ActionEvent e) -> {


            dynamicBoard.checkForBoardIncrease();

            dynamicBoard.setBoardSplit();

            workerPool.setTask(() -> {dynamicBoard.nextGeneration(); /*System.out.println("Trådid: " + Thread.currentThread().getId());*/});
            try {
                workerPool.runWorkers();
                workerPool.clearWorkers();
            }catch (InterruptedException ee) {
                workerPool.clearWorkers();
            }

            graphics.clearDynamicBoard();

            graphics.setCellHeight(dynamicBoard.cellsHigh);
            graphics.setCellWidth(dynamicBoard.cellsWide);
            grid.setCellHeight(graphics.getCellHeight());
            grid.setCellWidth(graphics.getCellWidth());

            dynamicBoard.rules();

            grid.clearGrid();
            if(showGrid) {
                grid.draw();
            }


            graphics.drawDynamic(dynamicBoard.getGameBoard());


            genCounter.setText(Integer.toString(dynamicBoard.getGenCounter()));

        });
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyframe);
        timeline.rateProperty().bind(speedSlider.valueProperty());
    }


    /**
     * Method called when user drags cells to input into
     * the canvas area
     *
     * @author Rudi André Dahle
     * @author Ginelle Ignacio
     * @param event Represents a mouse event used when
     *              the user interacts with the GUI.
     */
    public void selectDragCell(MouseEvent event){
        int canvasH = canvasGrid.heightProperty().intValue();
        int canvasW = canvasGrid.widthProperty().intValue();

        xCoord = event.getX();
        yCoord = event.getY();

        //fix for handling index out of bounds exception
        if(xCoord > 0 && yCoord > 0 && xCoord < canvasH && yCoord < canvasW) {
            graphics.setYCell(yCoord);
            graphics.setXCell(xCoord);
            graphics.drawDynamicCell(dynamicBoard.getGameBoard());
        }

        tipField.setText("Draw your own pattern!");
    }


    /**
     * Method called when the user selects either "Square"
     * or "Circle" as the shape of the cells.
     *
     * @author Ginelle Ignacio
     * @author Rudi André Dahle
     * @param actionEvent
     */
    public void cellShapeEvent(ActionEvent actionEvent){
         if (cellShapeBox.getValue() == "Square") {
             shape = true;
             tipField.setText("You can also choose \n" +
                     "circle as the shape \nof the cells!");
         } else if (cellShapeBox.getValue() == "Circle") {
             shape = false;
             tipField.setText("You can also choose \n" +
                     "square as the shape \nof the cells!");
         }
        graphics.setShape(shape);
        graphics.drawDynamic(dynamicBoard.getGameBoard());
    }


    /**
     * Method called when user plays or pauses the button
     * for the animation
     *
     * @author Rudi André Dahle
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    @FXML
    public void OnStartClick(ActionEvent actionEvent) {
        if(timeline.getStatus() == Status.RUNNING) {
            timeline.pause();
            running = false;
            tipField.setText("Click 'Play' to watch \nthe animation!");
        } else {
            timeline.play();
            running = true;
            tipField.setText("Enjoy playing!");
        }
    }



    /**
     * Method called when user press play/pause button
     * to change the button text.
     *
     * @author Ginelle Ignacio
     */
    public void playPauseEvent(){
        if(running){
            playPause.setText("Pause");
        } else {
            playPause.setText("Play");
        }
    }


    /**
     * Clear button to clear the cells in the canvas area
     *
     * @author Ginelle Ignacio
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    public void clearEvent(ActionEvent actionEvent){
        timeline.stop();
        playPause.setText("Play");
        dynamicBoard.resetGenCount();
        dynamicBoard.clearDynBoard();
        dynamicBoard.clearByteBoard();
        grid.clearGrid();


        //Resets to the original size of the board
        //dynamicBoard.setBoardSize(30);
        graphics.setCellHeight(dynamicBoard.getCellsHigh());
        graphics.setCellWidth(dynamicBoard.getCellsWide());
        graphics.drawDynamic(dynamicBoard.getGameBoard());

        //Plays the new cells drawn
        grid.setCellHeight(graphics.getCellHeight());
        grid.setCellWidth(graphics.getCellWidth());
        if(showGrid) {
            grid.draw();
        }

    }


    /**
     * Color picker changes the colors of the cells
     *
     * @author Ginelle Ignacio
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    public void colorChanged(ActionEvent actionEvent){
        graphics.gc.setFill(colorPicker.getValue());
        tipField.setText("Choose different colors \nand" +
                " have fun!");
        graphics.drawDynamic(dynamicBoard.getGameBoard());
    }


    /**
     * Change background color of the game.
     * @author Rudi André Dahle
     * @param actionEvent represents an Action Event used to
     *                    when a button has been fired.
     */
    public void backgroundChanged(ActionEvent actionEvent){
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        gcBG.setFill(backgroundColor.getValue());
        gcBG.fillRect(0, 0, canvasWidth, canvasHeight);

    }


    /**
     * Grid toggle to make the grid visible or invisible
     *
     * @author Rudi André Dahle
     * @param actionEvent Represents an Action Event used
     *                    when a button has been fired.
     */
    public void gridEvent(ActionEvent actionEvent) {
        if (!showGrid) {
            showGrid = true;
            gridToggle.setSelected(true);
            grid.draw();
        }else {
            showGrid = false;
            grid.clearGrid();
            gridToggle.setSelected(false);
        }
    }

    /**
     * Method called when user select on "Guidelines" on Menu.
     * Method contains information about the rules of the game,
     * and details about the controllers.
     *
     * @author Ginelle Ignacio
     * @param ae represents an Action Event used
     *           when a menu item has been clicked
     * @throws Exception if an alerts occurs while opening help window
     */
    public void helpEvent (ActionEvent ae) throws Exception {
        try{
            Parent helpRoot = FXMLLoader.load(getClass().getClassLoader()
                    .getResource("Rules/Guide.fxml"));
            helpWindow.setTitle("Guidelines");
            helpWindow.setScene(new Scene(helpRoot));
            helpWindow.show();
        } catch (IOException io){
            alerts.notLoading();
        }
    }


    /**
     * "Open File..." menu item set to open FileChooser window
     * Method also includes exceptions.
     *
     * @author Rudi Andre Dahle
     * @author Ginelle Ignacio
     * @param ae represents an Action Event used
     *           when a menu item has been clicked
     * @throws PatternFormatExceptions if an alerts occurs while
     *         opening file chooser window
     */
    public void openFiles(ActionEvent ae)throws PatternFormatExceptions {
        try {
            reader.chooseFile();

            grid.setCellHeight(graphics.getCellHeight());
            grid.setCellWidth(graphics.getCellWidth());
            grid.clearGrid();

            if(showGrid) {
                grid.draw();
            }

            errorContent();
            dynamicBoard.resetGenCount();
            graphics.drawDynamic(dynamicBoard.getGameBoard());
            tipField.setText("Great choice of pattern!\nClick 'Play'" +
                    "to watch the \nanimation!");
        } catch (FileNotFoundException fe){
            alerts.fileNotFound();
        } catch (IOException e) {
            alerts.errorOpeningfile();
        } catch (NoSuchElementException ne){
            alerts.incorrectMatch();
        } catch (IllegalStateException ie) {
            alerts.errorReading();
        }
    }

    /**
     * Helping method to get the error when the program
     * reads a wrong file format
     *
     * @author Rudi André Dahle
     */
    public void errorContent(){
        boolean error = reader.getError();
        if(error){
            alerts.errorReading();
        }
    }

    /**
     * Method called when the user selects "Read Web File.."
     * on the menu list under "File". This method will read
     * a web address and convert it into a pattern.
     *
     * @author Ginelle Ignacio
     * @param ae represents an Action Event used to
     *           when a menu item has been clicked
     * @throws Exception if an alerts occurs while opening URL window
     */

    public void webFile(ActionEvent ae) throws Exception {
        try {
            Parent webRoot = FXMLLoader.load(getClass().getClassLoader()
                    .getResource("WebFile/Webfile.fxml"));
            readWeb.setTitle("Read web file");
            readWeb.setScene(new Scene(webRoot));
            readWeb.show();
        } catch (IOException io){
            alerts.notLoading();
        }
        tipField.setText("After submitting a URL, \nClick 'Play' to view the " +
                "\npattern! ");
    }

    /**
     * Method called when the user clicks on the "Close"
     * item located in Menu under File to close whole window
     *
     * @author Ginelle Ignacio
     * @param ae represents an Action Event used to
     *           when a menu item has been clicked
     */
    public void closeWindow (ActionEvent ae){
        System.exit(0);
    }
}