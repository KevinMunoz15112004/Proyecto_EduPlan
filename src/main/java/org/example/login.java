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
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JLabel claveLabel;
    private JLabel userLabel;
    private JButton registrarseButton;
    private JLabel loginPanel;
    public JPanel logPanel;
    private JButton ingresarButton;

    public login() {
        registrarseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(logPanel);
                loginFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new register().registerPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.setPreferredSize(new Dimension(600, 600));
                frame.pack();
                frame.setVisible(true);
            }
        });
        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText();
                char[] password = passwordField1.getPassword();

                // Convertir la contraseña a un String
                String passwordString = new String(password);

                // Validar que los campos no estén vacíos
                if (username.isEmpty() || passwordString.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese tanto el nombre de usuario como la contraseña.");
                    return;
                }

                // Conectar a MongoDB para verificar las credenciales
                try (var mongoClient = MongoClients.create("mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority")) {
                    // Obtener la base de datos "gestion_academica" y la colección "usuarios"
                    MongoDatabase database = mongoClient.getDatabase("Prueba_conexion");
                    MongoCollection<Document> collection = database.getCollection("Prueba");

                    // Buscar el usuario en la base de datos
                    Document user = collection.find(new Document("username", username)).first();

                    // Verificar si el usuario existe
                    if (user != null) {
                        // Comparar la contraseña ingresada con la almacenada en la base de datos
                        String storedPassword = user.getString("password");
                        if (storedPassword.equals(passwordString)) {
                            JOptionPane.showMessageDialog(null, "Ingreso exitoso.");

                            // Ingreso de ventana correspondiente

                        } else {
                            JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al verificar las credenciales: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }
}
