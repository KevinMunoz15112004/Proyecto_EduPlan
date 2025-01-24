package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class interfaz_estudiante {
    private JButton verCalificacionesButton;
    private JButton verHorarioButton;
    private JButton consultaDeAsistenciasButton;
    private JLabel bienvenidaLabel;
    public JPanel interfazEPanel;
    private JButton descargarReporteButton;
    private JButton cerrarSesionButton;

    public interfaz_estudiante(String nombre) {
        bienvenidaLabel.setText("Bienvenido/a, " + nombre);
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazEPanel);
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

        verCalificacionesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazEPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new notas_estudiante(nombre).notasPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 300);
                frame.setPreferredSize(new Dimension(600, 300));
                frame.pack();
                frame.setVisible(true);
            }
        });

        verHorarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazEPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new horario_estudiante(nombre).horario);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 300);
                frame.setPreferredSize(new Dimension(600, 300));
                frame.pack();
                frame.setVisible(true);
            }
        });

        consultaDeAsistenciasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazEPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new asistencia_estudiante(nombre).asistenciaPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 300);
                frame.setPreferredSize(new Dimension(600, 300));
                frame.pack();
                frame.setVisible(true);
            }
        });

        descargarReporteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

}
