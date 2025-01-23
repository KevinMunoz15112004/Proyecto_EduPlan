package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mongodb.client.*;
import static com.mongodb.client.model.Sorts.descending;
import org.bson.Document;

public class register_admin {
    private JTextField cedulaField;
    private JPasswordField passwordField;
    private JButton registrarButton;
    private JTextField nombreField;
    public JPanel adminPanel;
    private JButton iniciarSesionButton;

    public register_admin() {
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = "";
                String cedula = cedulaField.getText();
                String nombre = nombreField.getText();
                String password = new String(passwordField.getPassword());

                // Validar si los campos están vacíos
                if (cedula.isEmpty() || nombre.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
                    return;
                }

                // Validar la cédula (solo números, exactamente 10 dígitos)
                if (!cedula.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(null, "La cédula debe tener exactamente 10 números.");
                    return;
                }

                // Validar el nombre (solo letras y tildes, sin caracteres especiales)
                if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                    JOptionPane.showMessageDialog(null, "El nombre solo puede contener letras y tildes.");
                    return;
                }

                // Validar la contraseña (al menos 6 caracteres, solo letras o números)
                if (password.length() < 6 || !password.matches("[a-zA-Z0-9]+")) {
                    JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 6 caracteres y solo contener letras o números.");
                    return;
                }

                try (MongoClient mongoClient = MongoClients.create(url)) {
                    MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
                    MongoCollection<Document> administradoresCollection = database.getCollection("administradores");

                    Document existingAdmin = administradoresCollection.find(new Document("cedula", cedula)).first();

                    if (existingAdmin != null) {
                        JOptionPane.showMessageDialog(null, "La cédula ya está registrada.");
                        return;
                    }

                    // Buscar el último admin_id asignado para incrementar el nuevo admin_id
                    Document lastAdmin = administradoresCollection.find()
                            .sort(descending("admin_id"))
                            .first();

                    int newAdminId = (lastAdmin == null) ? 1 : lastAdmin.getInteger("admin_id") + 1;

                    // Crear el documento para insertar en la base de datos
                    Document newAdmin = new Document("admin_id", newAdminId)
                            .append("cedula", cedula)
                            .append("nombre", nombre)
                            .append("password", password);

                    // Insertar el nuevo administrador en la base de datos
                    administradoresCollection.insertOne(newAdmin);
                    JOptionPane.showMessageDialog(null, "Administrador registrado con éxito!");

                    // Limpiar los campos después de registrar
                    cedulaField.setText("");
                    nombreField.setText("");
                    passwordField.setText("");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al registrar el administrador: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Frame estudianteframe = (JFrame) SwingUtilities.getWindowAncestor(adminPanel);
                estudianteframe.dispose();

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
