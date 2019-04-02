package com.company;

import java.awt.Color;

import java.awt.Graphics;


class Cell {
    private int myX, myY; // x,y position on grid
    private boolean myAlive; // alive (true) or dead (false)
    private int myNeighbors; // count of neighbors with respect to x,y
    private boolean myAliveNextTurn; // Used for state in next iteration
    private Color myColor; // Based on alive/dead rules
    private final Color DEFAULT_ALIVE = Color.ORANGE;
    private final Color DEFAULT_DEAD = Color.GRAY;

    /**
     * Initialize a cell, set its position on the grid. Its color and whether it's alive or not
     * is set by default to not alive and gray.
     * @param x x position on grid
     * @param y y position on grid
     */
    Cell(int x, int y) {
        this(x, y, false, Color.GRAY);
    }

    /**
     * Initializer that gives the user more flexibility, sets all parameter using input.
     * @param row x position on grid
     * @param col y position on grid
     * @param alive whether the cell is "alive" or not
     * @param color the color of the cell
     */
    private Cell(int row, int col, boolean alive, Color color) {
        myAlive = alive;
        myColor = color;
        myX = col;
        myY = row;
    }

    /**
     * Getter setter block
     */
    boolean getAlive() {
        return myAlive;
    }

    void setAlive(boolean alive) {
        if (alive) {
            setAlive(true, DEFAULT_ALIVE);
        }
        else {
            setAlive(false, DEFAULT_DEAD);
        }
    }

    private void setAlive(boolean alive, Color color) {
        myColor = color;
        myAlive = alive;
    }

    void setAliveNextTurn(boolean alive) {
        myAliveNextTurn = alive;
    }

    private boolean getAliveNextTurn() {
        return myAliveNextTurn;
    }

    /**
     * Calculate the neighbors of the current cell, it looks from the block that's all around it,
     * and counts how many neighbors there are around it, then later it can be determined whether
     * the current cell should be alive or not based on the survival rule and how many neighbors
     * are present around it.
     * @return The number of neighbors around the current cell.
     */
    private int getNeighbors() {
        int leftX = this.myX-1;
        if (leftX < 0) {
            leftX = 0;
        }
        int rightX = this.myX+1;
        if (rightX > Display.COLS-1) {
            rightX = Display.COLS-1;
        }
        int topY = this.myY-1;
        if (topY < 0) {
            topY = 0;
        }
        int bottomY = this.myY+1;
        if (bottomY > Display.ROWS-1) {
            bottomY = Display.ROWS-1;
        }
        int counter = 0;
        for (int i = leftX; i <= rightX; i++) {
            for (int j = topY; j <= bottomY; j++) {
                if (Display.cell[j][i].getAlive()) {
                    counter++;
                }
            }
        }
        if (myAlive) {
            counter--;
        }
        myNeighbors=counter;
        return myNeighbors;
    }

    /**
     * Similar to the getNeighbors() method, but instead it has no bound, meaning that if the cell
     * is at the edge of the screen and on the exact opposite side of the screen there's another
     * cell, then it can essentially "wrap around" the screen to could the cells around that. If
     * this was not implemented and it was done as the first one, the cell on the walls and corners
     * will have less available slots to check whether there are cells or not, then it won't accurately
     * determine if the cell will actually die or not when bounds are turned off.
     * @return The number of neighbors around the current cell but wraps around the screen
     */
    private int getNeighborsNoBound() {
        boolean[][] temp = new boolean[3][3];
        int leftX = this.myX-1;
        if (leftX < 0) {
            leftX = Display.COLS-1;
        }
        int topY = this.myY-1;
        if (topY < 0) {
            topY = Display.ROWS-1;
        }
        temp[0][0] = Display.cell[topY][leftX].getAlive();
        temp[0][1] = Display.cell[topY][myX].getAlive();
        temp[1][0] = Display.cell[myY][leftX].getAlive();
        temp[1][1] = Display.cell[myY][myX].getAlive();
        int rightX = this.myX+1;
        if (rightX > Display.COLS-1) {
            rightX = 0;
        }
        int bottomY = this.myY+1;
        if (bottomY > Display.ROWS-1) {
            bottomY = 0;
        }
        temp[0][2] = Display.cell[topY][rightX].getAlive();
        temp[1][2] = Display.cell[myY][rightX].getAlive();
        temp[2][0] = Display.cell[bottomY][leftX].getAlive();
        temp[2][1] = Display.cell[bottomY][myX].getAlive();
        temp[2][2] = Display.cell[bottomY][rightX].getAlive();
        int counter = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (temp[i][j]) {
                    counter++;
                }
            }
        }
        if (myAlive) {
            counter--;
        }
        myNeighbors=counter;
        return myNeighbors;
    }

    /**
     * Combines both of the method getNeighborsNoBound() and getNeighbors() but it checks whether bound
     * is turned on or turned off to do the operation that fits the mode. Then eventually, using the
     * survival rules predetermined by the software (in this case it is that cells with less than 2
     * neighbors die and with more than 4 neighbors also die), it can set whether in the next round the
     * current cell will be alive or not. This method goes through each individual cell in the Cell[][]
     * parameter and it sets all of the cells that would be alive next round to alive.
     * @param array Input cell list/grid
     */
    static void calcNeighbors(Cell[][] array) {
        int neighbours;
        Cell obj;
        int xmax = array.length-1;
        int ymax = array[0].length-1;
        for (int i = 0; i <= xmax; i++) {
            for (int j = 0; j <= ymax; j++) {
                obj = array[i][j];
                if (Display.boundBool) {
                    neighbours = obj.getNeighbors();
                }
                else {
                    neighbours = obj.getNeighborsNoBound();
                }
                if (neighbours >= 4 || neighbours < 2) {
                    obj.setAliveNextTurn(false);
                }
                else if (neighbours == 3){
                    obj.setAliveNextTurn(true);
                }
                else {
                    obj.setAliveNextTurn(obj.getAlive());
                }
            }
        }
        for (int i = 0; i <= xmax; i++) {
            for (int j = 0; j <= ymax; j++) {
                array[i][j].setAlive(array[i][j].getAliveNextTurn());
            }
        }
    }

    /**
     * Draws each individual cell.
     * @param x_offset gap between the drawing grid and the edge of the window on x-direction
     * @param y_offset gap between the drawing grid and the edge of the window on y-direction
     * @param width width of a cell
     * @param height height of a cell
     * @param g Graphics input
     */
    void draw(int x_offset, int y_offset, int width, int height, Graphics g) {
        int xleft = x_offset + 1 + (myX * (width + 1));
        int xright = x_offset + width + (myX * (width + 1));
        int ytop = y_offset + 1 + (myY * (height + 1));
        int ybottom = y_offset + height + (myY * (height + 1));
        Color temp = g.getColor();
        g.setColor(myColor);
        g.fillRect(xleft, ytop, width, height);
    }
}