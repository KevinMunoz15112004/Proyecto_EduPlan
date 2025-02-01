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

public class register_estudianteAdmin {
    private JTextField cedulaField;
    private JTextField nombreField;
    private JComboBox cursoComboBox;
    private JPasswordField passwordField;
    private JButton registrarButton;
    private JButton iniciarSesionButton;
    public JPanel estudianteAdminPanel;
    private JButton regresarButton;

    private static final String url = "";
    private static final String db = "EduPlan";

    public register_estudianteAdmin() {
        // Opciones del JComboBox
        cursoComboBox.addItem("8vo");
        cursoComboBox.addItem("9no");
        cursoComboBox.addItem("10mo");
        cursoComboBox.addItem("1roBAC");
        cursoComboBox.addItem("2doBAC");
        cursoComboBox.addItem("3roBAC");

        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cedula = cedulaField.getText().trim();
                String nombre = nombreField.getText().trim();
                String curso = (String) cursoComboBox.getSelectedItem();
                String password = new String(passwordField.getPassword());

                // Validar que todos los campos estén llenos
                if (cedula.isEmpty() || nombre.isEmpty() || curso.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                    return;
                }

                // Validar cédula (debe contener exactamente 10 dígitos)
                if (!cedula.matches("\\d{10}")) {
                    JOptionPane.showMessageDialog(null, "La cédula debe tener exactamente 10 dígitos.");
                    return;
                }

                // Validar nombre (no debe contener números)
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

                List<String> horario = generarHorario(materias, curso);

                List<Double> notas = new ArrayList<>();
                List<Integer> asistencias = new ArrayList<>();
                for (int i = 0; i < materias.size(); i++) {
                    notas.add(0.0);
                    asistencias.add(0);
                }

                try (MongoClient mongoClient = MongoClients.create(url)) {
                    MongoDatabase database = mongoClient.getDatabase(db);
                    MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

                    // Verificar si la cédula ya existe
                    Document existingStudent = estudiantesCollection.find(new Document("cedula", cedula)).first();
                    if (existingStudent != null) {
                        JOptionPane.showMessageDialog(null, "La cédula ingresada ya está registrada.");
                        return;
                    }

                    Document lastStudent = estudiantesCollection.find()
                            .sort(descending("user_id"))
                            .first();
                    int nextUserId = (lastStudent == null) ? 1 : lastStudent.getInteger("user_id") + 1;

                    Document estudiante = new Document("user_id", nextUserId)
                            .append("cedula", cedula)
                            .append("nombre", nombre)
                            .append("curso", curso)
                            .append("password", password)
                            .append("materias", materias)
                            .append("notas", notas)
                            .append("asistencias", asistencias)
                            .append("horario", horario);

                    estudiantesCollection.insertOne(estudiante);
                    JOptionPane.showMessageDialog(null, "Registro de estudiante exitoso. ID: " + nextUserId);

                    cedulaField.setText("");
                    nombreField.setText("");
                    passwordField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al registrar el estudiante: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame estudianteframe = (JFrame) SwingUtilities.getWindowAncestor(estudianteAdminPanel);
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

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame estudianteframe = (JFrame) SwingUtilities.getWindowAncestor(estudianteAdminPanel);
                estudianteframe.dispose();

                JFrame frame = new JFrame("Administrador");
                frame.setContentPane(new interfaz_admin().interfaz_Admin);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(500, 500);
                frame.setPreferredSize(new Dimension(500, 500));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    // Metodo para generar el horario de forma automática
    private List<String> generarHorario(List<String> materias, String curso) {
        // Definir las horas
        String[] horas = {"7:00-8:30", "8:30-10:00", "10:00-11:30", "11:30-13:00", "14:00-15:30", "15:30-17:00", "17:00-18:30"};
        List<String> horario = new ArrayList<>();

        String[] dias = {"1", "2", "3", "4", "5"};

        // Crear una estructura para los horarios
        for (String dia : dias) {
            StringBuilder diaHorario = new StringBuilder(dia + ": ");

            for (String materia : materias) {
                String hora = horas[(int) (Math.random() * horas.length)];
                diaHorario.append(materia + " (" + hora + "), ");
            }

            horario.add(diaHorario.toString().replaceAll(", $", "")); // Eliminar la coma final
        }
        return horario;
    }
}
