package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        // Llamada a la ventana principal del Login
        JFrame frame = new JFrame("Login");
        frame.setContentPane(new login().logPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setPreferredSize(new Dimension(600, 600));
        frame.pack();
        frame.setVisible(true);
    }
}
