package com.company;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

public class Display extends JComponent implements MouseListener, MouseMotionListener {
    static final int ROWS = 80;
    static final int COLS = 135;
    static Cell[][] cell = new Cell[ROWS][COLS];
    private final int X_GRID_OFFSET = 25; // 25 pixels from left
    private final int Y_GRID_OFFSET = 40; // 40 pixels from top
    private final int CELL_WIDTH = 8;
    private final int CELL_HEIGHT = 8;
    private int timeInt = 100;
    static boolean boundBool = true;

    private final int DISPLAY_WIDTH;
    private final int DISPLAY_HEIGHT;
    private StartButton startStop;
    private StepButton step;
    private GliderGunButton gliderGun;
    private SpaceshipButton spaceShip;
    private toggleSpeed speedButton;
    private boolean paintloop = false;


    Display(int width, int height) {
        DISPLAY_WIDTH = width;
        DISPLAY_HEIGHT = height;
        init();
    }

    /**
     * Initializes all the necessary buttons and also set the size of the display. It also adds the
     * MouseListener and MotionListener into the display so that it can sense where the mouse is
     * clicked on and light up a certain cell.
     */
    private void init() {
        setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        final int X_LOC = DISPLAY_WIDTH/50;
        final int Y_LOC = (int)(DISPLAY_HEIGHT/10*8.2);
        initCells();

        addMouseListener(this);
        addMouseMotionListener(this);

        startStop = new StartButton();
        step = new StepButton();
        gliderGun = new GliderGunButton();
        spaceShip = new SpaceshipButton();
        speedButton = new toggleSpeed();
        exploderButton exploder = new exploderButton();
        cellRowButton complexCells = new cellRowButton();
        clearButton clear = new clearButton();
        closeButton quit = new closeButton();
        boundButton bound = new boundButton();
        startStop.setBounds(X_LOC, Y_LOC, 75, 36);
        step.setBounds(X_LOC+25*4,Y_LOC,75,36);
        gliderGun.setBounds(X_LOC+25*8,Y_LOC,150,36);
        spaceShip.setBounds(X_LOC+25*15,Y_LOC,100, 36);
        exploder.setBounds(X_LOC+25*20,Y_LOC,100,36);
        complexCells.setBounds(X_LOC+25*25,Y_LOC,100,36);
        speedButton.setBounds(X_LOC+25*30,Y_LOC,125,36);
        clear.setBounds(X_LOC+25*36,Y_LOC,75,36);
        quit.setBounds(X_LOC+25*40,Y_LOC,75,36);
        bound.setBounds(X_LOC+25*44,Y_LOC,100,36);
        add(startStop);
        startStop.setVisible(true);
        add(step);
        step.setVisible(true);
        add(gliderGun);
        gliderGun.setVisible(true);
        add(spaceShip);
        spaceShip.setVisible(true);
        add(speedButton);
        speedButton.setVisible(true);
        add(exploder);
        exploder.setVisible(true);
        add(complexCells);
        complexCells.setVisible(true);
        add(clear);
        clear.setVisible(true);
        add(quit);
        quit.setVisible(true);
        add(bound);
        bound.setVisible(true);
    }

    /**
     * Paint all the cell, grid, and buttons at the beginning. Then if paint loop is set, the method
     * would update the cells in a defined time interval that can be modified by the speed button.
     * The default sleep time is 100ms, which is 10 updates per second. But the speed can be updated
     * all the way up to 5ms, which is 20x faster and at 200 updates per second, or it can be slowed
     * down to 400ms, which is 2.5 updates per second and 4x slower than the default speed. If the
     * paint loop is off then it's executed in a separate method that only calculates the next step.
     * @param g Graphics input
     */
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        drawGrid(g);
        drawCells(g);
        drawButtons();

        if (paintloop) {
            try {
                Thread.sleep(timeInt);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            nextGeneration();
            repaint();
        }
    }

    /**
     * Initializes the cell board to a certain dimension with all empty cells
     */
    private void initCells() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cell[row][col] = new Cell(row, col);
            }
        }
    }

    /**
     * This method is designed for the step button, when the paintloop toggle is set to false, the
     * software won't automatically draw the next generation, but instead when you click the step
     * button it would draw the next generation manually so the user can pause whenever they want
     * or see the detailed process of a certain generation of cells.
     */
    private void togglePaintLoop() {
        paintloop = !paintloop;
    }

    /**
     * Draw the gridlines that fills the gap between the cells to make the board look better
     * @param g Graphics input
     */
    private void drawGrid(Graphics g) {
        for (int row = 0; row <= ROWS; row++) {
            g.drawLine(X_GRID_OFFSET,
                    Y_GRID_OFFSET + (row * (CELL_HEIGHT + 1)), X_GRID_OFFSET
                            + COLS * (CELL_WIDTH + 1), Y_GRID_OFFSET
                            + (row * (CELL_HEIGHT + 1)));
        }
        for (int col = 0; col <= COLS; col++) {
            g.drawLine(X_GRID_OFFSET + (col * (CELL_WIDTH + 1)), Y_GRID_OFFSET,
                    X_GRID_OFFSET + (col * (CELL_WIDTH + 1)), Y_GRID_OFFSET
                            + ROWS * (CELL_HEIGHT + 1));
        }
    }

    /**
     * Cycles through each individual cell and draw them as alive or not alive accordingly
     * following the dimension set at the beginning of the class.
     * @param g Graphics input
     */
    private void drawCells(Graphics g) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cell[row][col].draw(X_GRID_OFFSET, Y_GRID_OFFSET, CELL_WIDTH,
                        CELL_HEIGHT, g);
            }
        }
    }

    /**
     * Repaint all the buttons that needs a repaint, for example, all the button that does
     * the work to toggle something (due to the text needing to be updated).
     */
    private void drawButtons() {
        startStop.repaint();
        gliderGun.repaint();
        spaceShip.repaint();
        step.repaint();
        speedButton.repaint();
    }

    /**
     * Computes what will happen in the next update, and then repaint the board so
     * that the update actually shows up.
     */
    private void nextGeneration() {
        Cell.calcNeighbors(cell);
        repaint();
    }


    /**
     * When mouse is clicked at one place, the coordinate is registered and the cell
     * at that coordinate would light up and shown as alive. Then if the mouse clicks
     * on an alive cell the cell will be flagged not alive and will be greyed out.
     * @param arg0 Mouse Event
     */
    public void mouseClicked(MouseEvent arg0) {
        int col = (arg0.getX()-X_GRID_OFFSET)/(CELL_WIDTH+1);
        int row = (arg0.getY()-Y_GRID_OFFSET)/(CELL_HEIGHT+1);
        if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
            Cell obj = cell[row][col];
            obj.setAlive(!obj.getAlive());
            repaint();
        }
    }


    public void mouseEntered(MouseEvent arg0) {

    }


    public void mouseExited(MouseEvent arg0) {

    }


    public void mousePressed(MouseEvent arg0) {

    }


    public void mouseReleased(MouseEvent arg0) {

    }


    public void mouseDragged(MouseEvent arg0) {

    }


    public void mouseMoved(MouseEvent arg0) {

    }

    /**
     * Clear the current board so that no alive cell is on there.
     */
    private void clearBoard() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cell[i][j].setAlive(false);
                cell[i][j].setAliveNextTurn(false);
            }
        }
    }

    /**
     * Button to start the simulation
     */
    private class StartButton extends JButton implements ActionListener {
        StartButton() {
            super("Start");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            // nextGeneration(); // test the start button
            if (this.getText().equals("Start")) {
                togglePaintLoop();
                setText("Stop");
            } else {
                togglePaintLoop();
                setText("Start");
            }
            nextGeneration();
        }
    }

    /**
     * Button to go step by step without the program automatically calculating the future generations
     */
    private class StepButton extends JButton implements ActionListener {
        StepButton() {
            super("Step");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            nextGeneration();
        }
    }

    /**
     * Toggles the speed all the way down from the default 1x (100ms) to 0.25x (400ms) or all the way
     * up to 20x (5ms).
     */
    private class toggleSpeed extends JButton implements ActionListener {
        toggleSpeed() {
            super("Speed=1x");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            switch (this.getText()) {
                case "Speed=0.25x":
                    setText("Speed=0.5x");
                    timeInt = 200;
                    break;
                case "Speed=0.5x":
                    setText("Speed=1x");
                    timeInt = 100;
                    break;
                case "Speed=1x":
                    setText("Speed=2x");
                    timeInt = 50;
                    break;
                case "Speed=2x":
                    setText("Speed=5x");
                    timeInt = 20;
                    break;
                case "Speed=5x":
                    setText("Speed=10x");
                    timeInt = 10;
                    break;
                case "Speed=10x":
                    setText("Speed=20x");
                    timeInt = 5;
                    break;
                case "Speed=20x":
                    setText("Speed=0.25x");
                    timeInt = 400;
                    break;
            }
        }
    }

    /**
     * Several fun presets that is predetermined and the user could choose which one they want to see.
     */
    private class GliderGunButton extends JButton implements ActionListener {
        GliderGunButton() {
            super("Gosper Glider Gun");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            clearBoard();
            cell[19][19].setAlive(true);
            cell[19][20].setAlive(true);
            cell[20][19].setAlive(true);
            cell[20][20].setAlive(true);
            cell[20][27].setAlive(true);
            cell[21][27].setAlive(true);
            cell[19][28].setAlive(true);
            cell[19][29].setAlive(true);
            cell[20][29].setAlive(true);
            cell[21][27].setAlive(true);
            cell[21][28].setAlive(true);
            cell[21][35].setAlive(true);
            cell[21][36].setAlive(true);
            cell[22][35].setAlive(true);
            cell[22][37].setAlive(true);
            cell[23][35].setAlive(true);
            cell[19][41].setAlive(true);
            cell[19][42].setAlive(true);
            cell[18][41].setAlive(true);
            cell[17][42].setAlive(true);
            cell[17][43].setAlive(true);
            cell[18][43].setAlive(true);
            cell[29][43].setAlive(true);
            cell[29][44].setAlive(true);
            cell[29][45].setAlive(true);
            cell[30][43].setAlive(true);
            cell[31][44].setAlive(true);
            cell[17][53].setAlive(true);
            cell[17][54].setAlive(true);
            cell[18][53].setAlive(true);
            cell[18][54].setAlive(true);
            cell[24][54].setAlive(true);
            cell[25][54].setAlive(true);
            cell[26][54].setAlive(true);
            cell[24][55].setAlive(true);
            cell[25][56].setAlive(true);
            Display.this.repaint();
        }
    }

    private class SpaceshipButton extends JButton implements ActionListener {
        SpaceshipButton() {
            super("Spaceship");
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent arg0) {
            clearBoard();
            cell[36][2].setAlive(true);
            cell[38][2].setAlive(true);
            cell[35][3].setAlive(true);
            cell[35][4].setAlive(true);
            cell[35][5].setAlive(true);
            cell[35][6].setAlive(true);
            cell[36][6].setAlive(true);
            cell[37][6].setAlive(true);
            cell[38][5].setAlive(true);
            Display.this.repaint();
        }
    }

    private class exploderButton extends JButton implements ActionListener {
        exploderButton() {
            super("Exploder");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            clearBoard();
            cell[30][35].setAlive(true);
            cell[31][35].setAlive(true);
            cell[32][35].setAlive(true);
            cell[33][35].setAlive(true);
            cell[34][35].setAlive(true);
            cell[30][37].setAlive(true);
            cell[34][37].setAlive(true);
            cell[30][39].setAlive(true);
            cell[31][39].setAlive(true);
            cell[32][39].setAlive(true);
            cell[33][39].setAlive(true);
            cell[34][39].setAlive(true);
            Display.this.repaint();
        }
    }

    private class cellRowButton extends JButton implements ActionListener {
        cellRowButton() {
            setText("Complex");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            clearBoard();
            for (int i = 0; i < COLS; i++) {
                cell[40][i].setAlive(true);
            }
            Display.this.repaint();
        }
    }

    /**
     * Clears the board and set all cell to not alive.
     */
    private class clearButton extends JButton implements ActionListener {
        clearButton() {
            setText("Clear");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            clearBoard();
            Display.this.repaint();
        }
    }

    /**
     * Exits the program
     */
    private class closeButton extends JButton implements ActionListener {
        closeButton() {
            setText("Quit");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            System.exit(0);
        }
    }

    /**
     * Toggle wrap around, which means that when a cell is on the edge of the screen, it can pop
     * up to the other side of the screen and also detect its neighbor on the other side.
     */
    private class boundButton extends JButton implements ActionListener {
        boundButton() {
            super("Bound On");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent arg0) {
            if (this.getText().equals("Bound On")) {
                setText("Bound Off");
                boundBool = false;
            }
            else {
                setText("Bound On");
                boundBool = true;
            }
        }
    }
}

