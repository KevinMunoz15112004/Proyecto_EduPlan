package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class rol_eleccion {
    private JButton aceptarButton;
    public JPanel rolPanel;
    private JComboBox rolBox;
    private JButton regresarButton;

    public rol_eleccion() {
        rolBox.addItem("");
        rolBox.addItem("Estudiante");
        rolBox.addItem("Profesor");
        rolBox.addItem("Administrador");

        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener la opción seleccionada
                String selectedRole = (String) rolBox.getSelectedItem();

                if (selectedRole == null || selectedRole.isEmpty()) {
                    JOptionPane.showMessageDialog(rolPanel, "Por favor, seleccione un rol antes de continuar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }

                if ("Estudiante".equals(selectedRole)) {
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(rolPanel);
                    currentFrame.dispose();

                    JFrame frame = new JFrame("Registro de Estudiante");
                    frame.setContentPane(new register_estudiante().estudiantePanel);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(600, 600);
                    frame.setPreferredSize(new Dimension(600, 600));
                    frame.pack();
                    frame.setVisible(true);

                } else if ("Profesor".equals(selectedRole)) {
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(rolPanel);
                    currentFrame.dispose();

                    JFrame frame = new JFrame("Registro de Profesor");
                    frame.setContentPane(new register_profesor().profesorPanel);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(600, 600);
                    frame.setPreferredSize(new Dimension(600, 600));
                    frame.pack();
                    frame.setVisible(true);

                } else if ("Administrador".equals(selectedRole)) {
                    // Solicitud de la clave de administrador
                    String adminKey = JOptionPane.showInputDialog(rolPanel, "Por favor, ingrese la clave de administrador:");

                    // Verificar si la clave es correcta
                    String correctAdminKey = "admin123";

                    if (adminKey != null && adminKey.equals(correctAdminKey)) {
                        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(rolPanel);
                        currentFrame.dispose();

                        JFrame frame = new JFrame("Registro de Administrador");
                        frame.setContentPane(new register_admin().adminPanel);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setSize(600, 600);
                        frame.setPreferredSize(new Dimension(600, 600));
                        frame.pack();
                        frame.setVisible(true);

                    } else {
                        JOptionPane.showMessageDialog(rolPanel, "Clave incorrecta. Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(rolPanel);
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
    }
}
