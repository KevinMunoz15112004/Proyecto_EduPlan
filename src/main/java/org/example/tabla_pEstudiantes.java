package org.example;

import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class tabla_pEstudiantes {
    private JTable table1;
    private JButton elegirEstudianteButton;
    private JButton regresarButton;
    public JPanel interfazTablaP;

    public tabla_pEstudiantes(String nombreProfesor) {
        // Configurar columnas de la tabla
        String[] columnNames = {"ID", "Cédula", "Nombre", "Curso"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);

        // Cargar datos de la base de datos
        cargarDatos(nombreProfesor, tableModel);

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazTablaP);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new interfaz_profesor(nombreProfesor).interfazPPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.setPreferredSize(new Dimension(600, 600));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void cargarDatos(String nombreProfesor, DefaultTableModel tableModel) {
        String url = "mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority";
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");

            // Obtener la colección de profesores
            MongoCollection<Document> profesoresCollection = database.getCollection("profesores");
            Document profesor = profesoresCollection.find(new Document("nombre", nombreProfesor)).first();

            if (profesor != null) {
                // Obtener las materias del profesor
                List<String> listaMateriasProfesor = profesor.getList("materias", String.class);

                // Obtener la colección de estudiantes
                MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");
                MongoCursor<Document> cursor = estudiantesCollection.find().iterator();

                while (cursor.hasNext()) {
                    Document estudiante = cursor.next();
                    List<String> materiasEstudiante = estudiante.getList("materias", String.class);

                    // Verificar si hay materias comunes entre el profesor y el estudiante
                    if (materiasEstudiante != null && listaMateriasProfesor.stream().anyMatch(materiasEstudiante::contains)) {
                        int userId = estudiante.getInteger("user_id");
                        String cedula = estudiante.getString("cedula");
                        String nombre = estudiante.getString("nombre");
                        String curso = estudiante.getString("curso");

                        // Agregar el estudiante a la tabla
                        Object[] row = {userId, cedula, nombre, curso};
                        tableModel.addRow(row);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró al profesor con el nombre: " + nombreProfesor);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
