package com.company;

import javax.swing.JFrame;

public class Main {
    /**
     * This is the method that initializes a window in a resolution of 1280x960, and it also makes
     * the blocks able to draw on it.
     * @param args Command line Arguments
     */
    public static void main(String[] args) {
        final int DISPLAY_WIDTH = 1280;
        final int DISPLAY_HEIGHT = 960;
        JFrame f = new JFrame();
        f.setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        Display display = new Display(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Conway's Game of Life");
        f.add(display);
        f.setVisible(true);
        f.setResizable(false);
    }
}