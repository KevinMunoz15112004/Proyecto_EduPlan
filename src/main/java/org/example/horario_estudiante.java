package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class horario_estudiante {
    private JTable table1;
    private JButton regresarButton;
    public JPanel horario;

    private static final String url = "";
    private static final String db = "EduPlan";

    public horario_estudiante(String nombreEstudiante) {
        // Creación del modelo de la tabla
        DefaultTableModel model = new DefaultTableModel();

        // Se definen las columnas de la tabla
        model.addColumn("Día");
        model.addColumn("Horario");

        table1.setModel(model);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table1.setFillsViewportHeight(true);

        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            // Se busca el estudiante por cédula
            Document estudianteNombre = estudiantesCollection.find(new Document("nombre", nombreEstudiante)).first();
            if (estudianteNombre != null) {
                // Obteneción de la cédula del estudiante
                String cedula = estudianteNombre.getString("cedula");
                Document estudiante = estudiantesCollection.find(new Document("cedula", cedula)).first();
                if (estudiante != null) {
                    // Obtención del horario del estudiante
                    List<String> horario = (List<String>) estudiante.get("horario");

                    String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};

                    // agregamos los días y los horarios en las filas
                    for (int i = 0; i < dias.length; i++) {
                        String dia = dias[i];
                        String diaHorario = horario.get(i);

                        model.addRow(new Object[]{dia, diaHorario});
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Estudiante no encontrado.");
                }
            } else {
                System.out.println("Estudiante no encontrado");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos del horario: " + ex.getMessage());
            ex.printStackTrace();
        }

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(horario);
                loginFrame.dispose();

                JFrame frame = new JFrame("Interfaz Estudiante");
                frame.setContentPane(new interfaz_estudiante(nombreEstudiante).interfazEPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 400);
                frame.setPreferredSize(new Dimension(400, 400));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
