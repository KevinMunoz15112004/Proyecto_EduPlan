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

public class register {
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton iniciarSesiónButton;
    private JButton registrarButton;
    public JPanel registerPanel;

    public register() {
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText();
                char[] password = passwordField1.getPassword();

                String passwordString = new String(password);

                if (username.isEmpty() || passwordString.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese tanto el nombre de usuario como la contraseña.");
                    return;
                }

                try (var mongoClient = MongoClients.create("mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority")) {
                    // Obtener la base de datos "gestion_academica" y la colección "usuarios"
                    MongoDatabase database = mongoClient.getDatabase("Prueba_conexion");
                    MongoCollection<Document> collection = database.getCollection("Prueba");

                    // Crear un documento con los datos del usuario
                    Document newUser = new Document("username", username)
                            .append("password", passwordString);

                    // Insertar el nuevo usuario en la colección
                    collection.insertOne(newUser);

                    // Mostrar mensaje de éxito
                    JOptionPane.showMessageDialog(null, "Usuario registrado con éxito.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al registrar el usuario: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        iniciarSesiónButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(registerPanel);
                loginFrame.dispose();

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
