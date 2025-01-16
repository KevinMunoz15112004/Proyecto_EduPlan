package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class rol_eleccion {
    private JTextField rolTextField;
    private JButton aceptarButton;
    public JPanel rolPanel;

    public rol_eleccion() {
        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rol = rolTextField.getText().trim();

                // Validar que el rol sea "Estudiante" o "Profesor"
                if (rol.equalsIgnoreCase("Estudiante")) {
                    // Redirigir a la ventana de registro de estudiantes
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(rolPanel);
                    currentFrame.dispose();

                    JFrame frame = new JFrame("Registro de Estudiante");
                    frame.setContentPane(new register_estudiante().estudiantePanel);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(600, 600);
                    frame.setPreferredSize(new Dimension(600, 600));
                    frame.pack();
                    frame.setVisible(true);

                } else if (rol.equalsIgnoreCase("Profesor")) {
                    // Redirigir a la ventana de registro de profesores
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(rolPanel);
                    currentFrame.dispose();

                    JFrame frame = new JFrame("Registro de Profesor");
                    frame.setContentPane(new register_profesor().profesorPanel);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(600, 600);
                    frame.setPreferredSize(new Dimension(600, 600));
                    frame.pack();
                    frame.setVisible(true);
                } else {
                    // Mostrar mensaje de error si el rol no es válido
                    JOptionPane.showMessageDialog(null, "Por favor, escriba 'Estudiante' o 'Profesor'.");
                }
            }
        });
    }
}
