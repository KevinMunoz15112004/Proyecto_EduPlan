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
                String cedula = cedulaText.getText();
                char[] password = passwordField1.getPassword();

                // Convertir la contraseña a un String
                String passwordString = new String(password);

                // Validar que los campos no estén vacíos
                if (cedula.isEmpty() || passwordString.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese tanto la cédula como la contraseña.");
                    return;
                }

                // Conectar a MongoDB para verificar las credenciales
                try (var mongoClient = MongoClients.create("")) {
                    // Obtener la base de datos "conection" y la colección "registers"
                    MongoDatabase database = mongoClient.getDatabase("conection");
                    MongoCollection<Document> collection = database.getCollection("registers");

                    // Buscar el usuario en la base de datos usando la cédula
                    Document user = collection.find(new Document("cedula", cedula)).first();

                    // Verificar si el usuario existe
                    if (user != null) {
                        // Comparar la contraseña ingresada con la almacenada en la base de datos
                        String storedPassword = user.getString("password");
                        if (storedPassword.equals(passwordString)) {
                            // Mostrar mensaje de bienvenida con el nombre y rol del usuario
                            String username = user.getString("username");
                            String rol = user.getString("rol");
                            JOptionPane.showMessageDialog(null, "Bienvenido, " + username + ". Rol: " + rol);

                            // Ingreso de ventana correspondiente (puedes redirigir a la ventana principal o el dashboard aquí)

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
