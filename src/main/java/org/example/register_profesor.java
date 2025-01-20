package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

import com.mongodb.client.MongoClient;
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
                String url = "mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority";
                String cedula = cedulaField.getText().trim();
                String nombre = nombreField.getText().trim();
                String password = new String(passwordField.getPassword());

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
                try (MongoClient mongoClient = MongoClients.create(url)) {
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

                    // Crear una instancia para obtener las materias seleccionadas
                    materias_profe materiaFrame = new materias_profe();
                    JDialog materiaDialog = new JDialog();
                    materiaDialog.setContentPane(materiaFrame.materiasPanel);
                    materiaDialog.setSize(300, 400);
                    materiaDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    materiaDialog.setModal(true);
                    materiaDialog.setVisible(true);

                    // Esperar a que el usuario seleccione las materias y cierre la ventana
                    List<String> materiasSeleccionadas = materiaFrame.getMateriasSeleccionadas();
                    if (materiasSeleccionadas.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar al menos una materia.");
                        return;
                    }

                    // Crear el documento con el nuevo profesor_id y materias seleccionadas
                    Document profesor = new Document("profesor_id", nextProfesorId)
                            .append("cedula", cedula)
                            .append("nombre", nombre)
                            .append("password", password)
                            .append("materias", materiasSeleccionadas);  // Agregar las materias seleccionadas

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

    // Clase interna para mostrar las materias
    public class materias_profe {
        public JPanel materiasPanel;
        private List<String> materiasSeleccionadas;

        public materias_profe() {
            materiasPanel = new JPanel();
            materiasPanel.setLayout(new BoxLayout(materiasPanel, BoxLayout.Y_AXIS));

            String[] materias = {"Matemáticas", "Literatura", "Estudios Sociales", "Biología", "Cultura Física",
                    "Artística", "Física", "Química", "Filosofía", "Historia", "Lectura Crítica", "Economía"};
            JCheckBox[] checkBoxes = new JCheckBox[materias.length];

            for (int i = 0; i < materias.length; i++) {
                checkBoxes[i] = new JCheckBox(materias[i]);
                materiasPanel.add(checkBoxes[i]);
            }

            JButton aceptarButton = new JButton("Aceptar");
            aceptarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    materiasSeleccionadas = new ArrayList<>();
                    for (JCheckBox checkBox : checkBoxes) {
                        if (checkBox.isSelected()) {
                            materiasSeleccionadas.add(checkBox.getText());
                        }
                    }
                    // Cerrar la ventana después de seleccionar las materias
                    SwingUtilities.getWindowAncestor(materiasPanel).dispose();
                }
            });
            materiasPanel.add(aceptarButton);
        }

        public List<String> getMateriasSeleccionadas() {
            return materiasSeleccionadas;
        }
    }
}
