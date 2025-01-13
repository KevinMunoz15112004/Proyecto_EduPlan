package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Sorts.descending;

public class register {
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton iniciarSesiónButton;
    private JButton registrarButton;
    public JPanel registerPanel;
    private JTextField rolField;
    private JLabel rolButton;
    private JTextField cedulaField;

    public register() {
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText();
                char[] password = passwordField1.getPassword();
                String rol = rolField.getText().trim(); // Eliminar espacios extra
                String cedula = cedulaField.getText().trim(); // Obtener cédula

                String passwordString = new String(password);

                // Validar que los campos no estén vacíos
                if (username.isEmpty() || passwordString.isEmpty() || rol.isEmpty() || cedula.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
                    return;
                }

                // Validar que el rol sea "Estudiante" o "Profesor"
                if (!rol.equalsIgnoreCase("Estudiante") && !rol.equalsIgnoreCase("Profesor")) {
                    JOptionPane.showMessageDialog(null, "El rol solo puede ser 'Estudiante' o 'Profesor'.");
                    return;
                }

                // Validar que la cédula tenga exactamente 10 dígitos
                if (cedula.length() != 10 || !cedula.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(null, "La cédula debe tener exactamente 10 dígitos.");
                    return;
                }

                // Conectar a MongoDB para registrar al usuario
                try (var mongoClient = MongoClients.create("")) {
                    // Obtener la base de datos y la colección
                    MongoDatabase database = mongoClient.getDatabase("conection");
                    MongoCollection<Document> collection = database.getCollection("registers");

                    // Verificar si la cédula ya existe en la base de datos
                    Document existingUser = collection.find(new Document("cedula", cedula)).first();
                    if (existingUser != null) {
                        JOptionPane.showMessageDialog(null, "El número de cédula ya está registrado.");
                        return;
                    }

                    // Buscar el último user_id generado en la colección
                    Document lastUser = collection.find().sort(descending("user_id")).first();
                    int newId = (lastUser != null) ? lastUser.getInteger("user_id") + 1 : 1;

                    // Crear un documento con los datos del usuario
                    Document newUser = new Document("user_id", newId) // Usar "user_id" en lugar de "_id"
                            .append("username", username)
                            .append("password", passwordString)
                            .append("rol", rol)
                            .append("cedula", cedula);  // Agregar la cédula al documento

                    // Insertar el nuevo usuario en la colección
                    collection.insertOne(newUser);

                    // Mostrar mensaje de éxito
                    JOptionPane.showMessageDialog(null, "Usuario registrado con éxito. ID asignado: " + newId);
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
    //1765476789
}
