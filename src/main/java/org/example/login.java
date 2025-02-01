package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class login {
    private JLabel BienvenidaLabel;
    private JTextField cedulaText;
    private JPasswordField passwordField1;
    private JLabel claveLabel;
    private JLabel cedulaLabel;
    private JButton registrarseButton;
    private JLabel loginPanel;
    public JPanel logPanel;
    private JButton ingresarButton;

    private static final String url = "";
    private static final String db = "EduPlan";

    public login() {
        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cedula = cedulaText.getText().trim();
                String passwordString = new String(passwordField1.getPassword());

                // Validar que los campos no estén vacíos
                if (cedula.isEmpty() || passwordString.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese tanto la cédula como la contraseña.");
                    return;
                }

                try (MongoClient mongoClient = MongoClients.create(url)) {
                    MongoDatabase database = mongoClient.getDatabase(db);

                    // Verificación en la colección estudiantes
                    MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");
                    Document estudiante = estudiantesCollection.find(new Document("cedula", cedula)).first();

                    if (estudiante != null) {
                        // Comparar contraseñas
                        String storedPassword = estudiante.getString("password");
                        if (storedPassword.equals(passwordString)) {
                            String nombre = estudiante.getString("nombre");
                            JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(logPanel);
                            loginFrame.dispose();

                            JFrame frame = new JFrame("Interfaz Estudiante");
                            frame.setContentPane(new interfaz_estudiante(nombre).interfazEPanel);
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setSize(400, 400);
                            frame.setPreferredSize(new Dimension(400, 400));
                            frame.pack();
                            frame.setVisible(true);
                            return;
                        } else {
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
                            return;
                        }
                    }

                    MongoCollection<Document> profesoresCollection = database.getCollection("profesores");
                    Document profesor = profesoresCollection.find(new Document("cedula", cedula)).first();

                    if (profesor != null) {
                        // Comparar contraseñas
                        String storedPassword = profesor.getString("password");
                        if (storedPassword.equals(passwordString)) {
                            String nombre = profesor.getString("nombre");
                            JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(logPanel);
                            loginFrame.dispose();

                            JFrame frame = new JFrame("Interfaz Estudiante");
                            frame.setContentPane(new interfaz_profesor(nombre).interfazPPanel);
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setSize(400, 300);
                            frame.setPreferredSize(new Dimension(400, 300));
                            frame.pack();
                            frame.setVisible(true);
                            return;
                        } else {
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
                            return;
                        }
                    }

                    MongoCollection<Document> administradoresCollection = database.getCollection("administradores");
                    Document administrador = administradoresCollection.find(new Document("cedula", cedula)).first();

                    if (administrador != null) {
                        // Comparar contraseñas
                        String storedPassword = administrador.getString("password");
                        if (storedPassword.equals(passwordString)) {
                            String nombre = administrador.getString("nombre");
                            JOptionPane.showMessageDialog(null, "Bienvenido, " + nombre + ". Rol: Administrador");
                            JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(logPanel);
                            loginFrame.dispose();

                            JFrame adminFrame = new JFrame("Interfaz Administrador");
                            adminFrame.setContentPane(new interfaz_admin().interfaz_Admin);
                            adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            adminFrame.setSize(500, 500);
                            adminFrame.setPreferredSize(new Dimension(500, 500));
                            adminFrame.pack();
                            adminFrame.setVisible(true);
                            return;
                        } else {
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
                            return;
                        }
                    }

                    JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al verificar las credenciales: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        registrarseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(logPanel);
                loginFrame.dispose();

                JFrame frame = new JFrame("Elección del Rol");
                frame.setContentPane(new rol_eleccion().rolPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 400);
                frame.setPreferredSize(new Dimension(400, 400));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
