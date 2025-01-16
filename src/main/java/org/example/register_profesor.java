package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Sorts.descending;
import org.bson.Document;

public class register_profesor {
    private JTextField cedulaField;
    private JTextField nombreField;
    private JPasswordField passwordField;
    private JButton registrarButton;
    private JButton iniciarSesionButton;
    public JPanel profesorPanel;

    public register_profesor() {
        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame profesorframe = (JFrame) SwingUtilities.getWindowAncestor(profesorPanel);
                profesorframe.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new login().logPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.setPreferredSize(new Dimension(600, 600));
                frame.pack();
                frame.setVisible(true);
            }
        });

        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cedula = cedulaField.getText().trim();
                String nombre = nombreField.getText().trim();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);

                // Validar que todos los campos estén llenos
                if (cedula.isEmpty() || nombre.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                    return;
                }

                // Validar que la cédula tenga exactamente 10 dígitos
                if (!cedula.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(null, "La cédula debe tener exactamente 10 dígitos.");
                    return;
                }

                // Validar que el nombre no incluya números
                if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                    JOptionPane.showMessageDialog(null, "El nombre no puede contener números ni caracteres especiales.");
                    return;
                }

                // Validar que la contraseña tenga al menos 6 caracteres y no contenga caracteres especiales
                if (!password.matches("[a-zA-Z0-9]{6,}")) {
                    JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 6 caracteres y solo puede incluir letras y números.");
                    return;
                }

                // Conectar a la base de datos y registrar al profesor
                try (var mongoClient = MongoClients.create("")) {
                    // Conectar a la base de datos y colección
                    MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
                    MongoCollection<Document> collection = database.getCollection("profesores");

                    // Verificar si la cédula ya está registrada
                    Document existingProfesor = collection.find(new Document("cedula", cedula)).first();
                    if (existingProfesor != null) {
                        JOptionPane.showMessageDialog(null, "La cédula ingresada ya está registrada.");
                        return;
                    }

                    // Obtener el último profesor_id y calcular el siguiente
                    Document lastProfesor = collection.find()
                            .sort(descending("profesor_id"))
                            .first();

                    int nextProfesorId = (lastProfesor == null) ? 1 : lastProfesor.getInteger("profesor_id") + 1;

                    // Crear el documento con el nuevo profesor_id
                    Document profesor = new Document("profesor_id", nextProfesorId)
                            .append("cedula", cedula)
                            .append("nombre", nombre)
                            .append("password", password);

                    // Insertar el documento en la base de datos
                    collection.insertOne(profesor);
                    JOptionPane.showMessageDialog(null, "Registro exitoso. Profesor guardado con ID: " + nextProfesorId);

                    // Limpiar los campos después del registro
                    cedulaField.setText("");
                    nombreField.setText("");
                    passwordField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al guardar en la base de datos: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }
}