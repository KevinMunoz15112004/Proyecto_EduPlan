package org.example;

import javax.swing.*;

public class interfaz_estudiante {
    private JButton verCalificacionesButton;
    private JButton verHorarioButton;
    private JButton consultaDeAsistenciasButton;
    private JLabel bienvenidaLabel;
    public JPanel interfazEPanel;
    private JButton descargarReporteButton;

    public interfaz_estudiante(String nombre) {
        bienvenidaLabel.setText("Bienvenido/a, " + nombre);
    }

}
