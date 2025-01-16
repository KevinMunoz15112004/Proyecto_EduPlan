package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public login() {
        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cedula = cedulaText.getText().trim();
                char[] password = passwordField1.getPassword();
                String passwordString = new String(password);

                // Validar que los campos no estén vacíos
                if (cedula.isEmpty() || passwordString.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese tanto la cédula como la contraseña.");
                    return;
                }

                // Conectar a MongoDB y verificar credenciales
                try (var mongoClient = MongoClients.create("")) {
                    MongoDatabase database = mongoClient.getDatabase("prueba_alfa");

                    // Verificar primero en la colección "estudiantes"
                    MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");
                    Document estudiante = estudiantesCollection.find(new Document("cedula", cedula)).first();

                    if (estudiante != null) {
                        // Comparar contraseñas
                        String storedPassword = estudiante.getString("password");
                        if (storedPassword.equals(passwordString)) {
                            String nombre = estudiante.getString("nombre");
                            JOptionPane.showMessageDialog(null, "Bienvenido, " + nombre + ". Rol: Estudiante");
                            return;
                        } else {
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
                            return;
                        }
                    }

                    // Verificar en la colección "profesores"
                    MongoCollection<Document> profesoresCollection = database.getCollection("profesores");
                    Document profesor = profesoresCollection.find(new Document("cedula", cedula)).first();

                    if (profesor != null) {
                        // Comparar contraseñas
                        String storedPassword = profesor.getString("password");
                        if (storedPassword.equals(passwordString)) {
                            String nombre = profesor.getString("nombre");
                            JOptionPane.showMessageDialog(null, "Bienvenido, " + nombre + ". Rol: Profesor");
                            return;
                        } else {
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
                            return;
                        }
                    }

                    // Si no se encuentra el usuario en ninguna colección
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
                frame.setSize(600, 600);
                frame.setPreferredSize(new Dimension(600, 600));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
