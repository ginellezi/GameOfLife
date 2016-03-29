package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;


/**
 * Created by RudiAndre on 08.02.2016.
 */

public class Board {

    /*DATAFELT*/
    //@FXML Canvas canvas;
    protected GraphicsContext gc;
    private int cellsWide = 40; //Number of cells in gameboards width
    private int cellsHigh = 20; //Number of cells in gameboards height
    public boolean[][] gameBoard;


    //Constructor
    public Board(/*int cellsWide, int cellsHigh*/) {
       // this.cellsWide = cellsWide;
       // this.cellsHigh = cellsHigh;
        System.out.println("Antall celler bredde: " + getBoardWidth());
        System.out.println("Antall celler høyde: " + getBoardHeight());

        gameBoard = new boolean[getBoardWidth()][getBoardHeight()];

        gameBoard[0][0] = true;
        gameBoard[2][0] = true;
        gameBoard[4][0] = true;
        gameBoard[6][0] = true;
        gameBoard[8][0] = true;
        gameBoard[10][0] = true;
        gameBoard[12][0] = true;
        gameBoard[14][0] = true;
        gameBoard[16][0] = true;
        gameBoard[18][0] = true;
        gameBoard[20][0] = true;
        gameBoard[22][0] = true;
        gameBoard[24][0] = true;
        gameBoard[26][0] = true;
        gameBoard[28][0] = true;
        gameBoard[30][0] = true;
        gameBoard[32][0] = true;
        gameBoard[34][0] = true;
        gameBoard[36][0] = true;
        gameBoard[38][0] = true;

        gameBoard[0][2] = true;
        gameBoard[0][4] = true;
        gameBoard[0][6] = true;
        gameBoard[0][8] = true;
        gameBoard[0][10] = true;
        gameBoard[0][12] = true;
        gameBoard[0][14] = true;
        gameBoard[0][16] = true;
        gameBoard[0][18] = true;


        gameBoard[3][4] = true;
        gameBoard[4][4] = true;
        gameBoard[5][4] = true;
        gameBoard[5][3] = true;
        gameBoard[4][2] = true;

        gameBoard[30][3] = true;
        gameBoard[30][4] = true;
        gameBoard[30][5] = true;
        gameBoard[30][7] = true;
        gameBoard[30][8] = true;
        gameBoard[30][9] = true;
        gameBoard[29][6] = true;
        gameBoard[28][6] = true;
        gameBoard[27][6] = true;
        gameBoard[31][6] = true;
        gameBoard[32][6] = true;
        gameBoard[33][6] = true;

    }


    /**
     * Created by Olav Smevoll on 08.03.2016.
     * Counts cell neighbours for next generation.
     * @param x
     * @param y
     * @return counter
     */
    protected int countNeighbours(int x, int y) {
        int counter = 0;
        int blx = gameBoard.length - 1;
        int bly = gameBoard[0].length - 1;


        // Tester celle nordvest
        if (x > 0 && y > 0 && gameBoard[x - 1][y - 1]) {
            counter++;
        }

        //Tester celle nord
        if (x > 0 && gameBoard[x - 1][y]) {
            counter++;
        }

        // Tester celle nordøst
        if (x > 0 && y < bly && gameBoard[x - 1][y + 1]) {
            counter++;
        }

        //Tester celle vest
        if (y > 0 && gameBoard[x][y - 1]) {
            counter++;
        }

        //Tester celle øst
        if (y < bly && gameBoard[x][y + 1]) {
            counter++;
        }

        //Tester celle sørvest
        if (x < blx && y > 0 && gameBoard[x + 1][y - 1]) {
            counter++;
        }

        //Tester celle sør
        if (x < blx && gameBoard[x + 1][y]) {
            counter++;
        }

        //Tester celle sørøst
        if (x < blx && y < bly && gameBoard[x + 1][y + 1]) {
            counter++;
        }
        return counter;
    }


    /**
     * Created by RudiAndre on 04.03.2016.
     *  Sets the new values in the array for the next generation.
     */
    public void nextGeneration(Grid grid) {
        boolean[][] newGameBoard = new boolean[getBoardWidth()][getBoardHeight()];
       grid.draw();
        for (int x = 0; x < gameBoard.length; x++) {
            for (int y = 0; y < gameBoard[0].length; y++) {
                if (gameBoard[x][y]) {
                    newGameBoard[x][y] = countNeighbours(x, y) == 2 || countNeighbours(x, y) == 3;
                } else newGameBoard[x][y] = countNeighbours(x, y) == 3;
            }
        }
        gameBoard = newGameBoard;
    }


    /**
     * Gameboards width in number of cells
     * Rudi 24.03.16
     * @return
     */
    public int getBoardWidth() {
        return cellsWide;
    }


    /**
     *
     * @param x
     */
   /* public void setBoardWidth(int x) {
        cellsWide = x;
    }*/


    /**
     * Gameboards height in number of cells
     * Rudi 24.03.16
     * @return
     */
    public int getBoardHeight() {
       // System.out.println(cellshigh);
        return cellsHigh;
    }


    /**
     * Rudi 24.03.16
     * @param y
     */
    public void setBoardHeight(int y) {

        cellsHigh = y;
    }


    /**
     * Created by RudiAndre on 09.03.2016.
     * @return
     */
    /*public int getCanvasHeight() {
        return canvas.heightProperty().intValue();
    }*/


    /**
     * Created by RudiAndre on 09.03.2016.
     * @return
     */
   /* public int getCanvasWidth() {
        return canvas.widthProperty().intValue();
    }*/


    /**
     * Created by RudiAndre on 01.03.2016.
     * @return
     */
    public boolean[][] getGameBoard() {
        return gameBoard;
    }


    /**
     * Created by RudiAndre on 01.03.2016.
     * @param gameBoard
     */
    public void setGameBoard(boolean[][] gameBoard) {
        this.gameBoard = gameBoard;
    }


    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
               for (int x = 0; x < gameBoard.length; x++) {
            for (int y = 0; y < gameBoard.length; y++) {
                if (gameBoard[x][y]) {
                    str.append("1");
                } else {
                    str.append("0");
                }
            }
        }
        return str.toString();
    }
}