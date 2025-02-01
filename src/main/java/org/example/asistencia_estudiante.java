package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class asistencia_estudiante {
    private JTable table1;
    private JButton regresarButton;
    public JPanel asistenciaPanel;

    private static final String url = "";

    public asistencia_estudiante(String nombreEstudiante) {
        // Configuración inicial para el JTable
        String[] columnNames = {"Materias", "Asistencias"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);

        // Cargamos la información desde la base de datos
        cargarAsistencias(nombreEstudiante, tableModel);

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(asistenciaPanel);
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

    private void cargarAsistencias(String nombreEstudiante, DefaultTableModel tableModel) {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> asistencia = database.getCollection("estudiantes");

            // Se busca el documento del estudiante
            Document estudiante = asistencia.find(new Document("nombre", nombreEstudiante)).first();

            if (estudiante != null) {
                // Se obtienen los campos como lista
                List<String> materias = estudiante.getList("materias", String.class);
                List<Integer> asistencias = estudiante.getList("asistencias", Integer.class);

                // Llenamos el JTable con los datos de las asistencias junto a sus materias
                for (int i = 0; i < asistencias.size(); i++) {
                    tableModel.addRow(new Object[]{materias.get(i), asistencias.get(i)});
                }
            } else {
                JOptionPane.showMessageDialog(null, "Estudiante no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}
