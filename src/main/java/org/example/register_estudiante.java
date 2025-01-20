package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Sorts.descending;
import org.bson.Document;

public class register_estudiante {
    private JTextField cedulaField;
    private JTextField nombreField;
    private JTextField cursoField;
    private JPasswordField passwordField;
    private JButton registrarButton;
    private JButton iniciarSesionButton;
    public JPanel estudiantePanel;
    private JComboBox cursoComboBox;

    public register_estudiante() {
        // Opciones del JComboBox
        cursoComboBox.addItem("8vo");
        cursoComboBox.addItem("9no");
        cursoComboBox.addItem("10mo");
        cursoComboBox.addItem("1roBAC");
        cursoComboBox.addItem("2doBAC");
        cursoComboBox.addItem("3roBAC");

        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame estudianteframe = (JFrame) SwingUtilities.getWindowAncestor(estudiantePanel);
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

        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = "";
                String cedula = cedulaField.getText().trim();
                String nombre = nombreField.getText().trim();
                String curso = (String) cursoComboBox.getSelectedItem();
                String password = new String(passwordField.getPassword());

                // Validar que todos los campos estén llenos
                if (cedula.isEmpty() || nombre.isEmpty() || curso.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                    return;
                }

                // Validar cédula: debe contener exactamente 10 dígitos
                if (!cedula.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(null, "La cédula debe tener exactamente 10 dígitos.");
                    return;
                }

                // Validar nombre: no debe contener números
                if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                    JOptionPane.showMessageDialog(null, "El nombre no debe contener números ni caracteres especiales.");
                    return;
                }

                // Validar contraseña: debe tener al menos 6 caracteres y solo permitir letras y números
                if (!password.matches("[a-zA-Z0-9]{6,}")) {
                    JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 6 caracteres y solo puede contener letras y números.");
                    return;
                }

                // Se definen las materias según el curso
                List<String> materias = new ArrayList<>();
                switch (curso) {
                    case "8vo":
                    case "9no":
                    case "10mo":
                        materias.add("Matemáticas");
                        materias.add("Literatura");
                        materias.add("Estudios Sociales");
                        materias.add("Biología");
                        materias.add("Cultura Física");
                        materias.add("Artística");
                        break;
                    case "1roBAC":
                    case "2doBAC":
                        materias.add("Matemáticas");
                        materias.add("Física");
                        materias.add("Química");
                        materias.add("Filosofía");
                        materias.add("Literatura");
                        materias.add("Historia");
                        break;
                    case "3roBAC":
                        materias.add("Matemáticas");
                        materias.add("Física");
                        materias.add("Literatura");
                        materias.add("Filosofía");
                        materias.add("Economía");
                        materias.add("Química");
                        materias.add("Historia");
                        materias.add("Lectura Crítica");
                        break;
                }

                // Conectar a la base de datos
                try (MongoClient mongoClient = MongoClients.create(url)) {
                    MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
                    MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

                    // Verificar si la cédula ya existe
                    Document existingStudent = estudiantesCollection.find(new Document("cedula", cedula)).first();
                    if (existingStudent != null) {
                        JOptionPane.showMessageDialog(null, "La cédula ingresada ya está registrada.");
                        return;
                    }

                    // Obtener el último user_id y calcular el siguiente
                    Document lastStudent = estudiantesCollection.find()
                            .sort(descending("user_id"))
                            .first();

                    int nextUserId = (lastStudent == null) ? 1 : lastStudent.getInteger("user_id") + 1;

                    // Crear el documento para insertar
                    Document estudiante = new Document("user_id", nextUserId)
                            .append("cedula", cedula)
                            .append("nombre", nombre)
                            .append("curso", curso)
                            .append("password", password)
                            .append("materias", materias);

                    // Insertar el documento en la colección
                    estudiantesCollection.insertOne(estudiante);
                    JOptionPane.showMessageDialog(null, "Registro de estudiante exitoso. ID: " + nextUserId);

                    // Limpiar los campos después del registro
                    cedulaField.setText("");
                    nombreField.setText("");
                    passwordField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al registrar el estudiante: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }
}
