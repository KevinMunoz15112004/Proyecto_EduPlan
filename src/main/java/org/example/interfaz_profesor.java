package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class interfaz_profesor {
    private JLabel bienvenidaLabel;
    public JPanel interfazPPanel;
    private JButton registrarNotaButton;
    private JButton registrarAsistenciaButton;
    private JButton cerrarSesionButton;

    public interfaz_profesor(String nombre) {
        bienvenidaLabel.setText("Bienvenido/a, " + nombre);
        registrarNotaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazPPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Tabla de Estudiantes");
                frame.setContentPane(new tabla_pEstudiantes(nombre).interfazTablaP);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.setPreferredSize(new Dimension(600, 600));
                frame.pack();
                frame.setVisible(true);
            }
        });

        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazPPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new login().logPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.setPreferredSize(new Dimension(600, 600));
                frame.pack();
                frame.setVisible(true);
            }
        });

        registrarAsistenciaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame estudianteframe = (JFrame) SwingUtilities.getWindowAncestor(interfazPPanel);
                estudianteframe.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new tabla_pEstudianteA(nombre).tablaPEstudianteA);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.setPreferredSize(new Dimension(600, 600));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
