package org.example;

import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class tabla_pEstudiantes {
    private JTable table1;
    private JButton elegirEstudianteButton;
    private JButton regresarButton;
    public JPanel interfazTablaP;

    private static final String url = "";

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
                frame.setSize(400, 400);
                frame.setPreferredSize(new Dimension(400, 400));
                frame.pack();
                frame.setVisible(true);
            }
        });

        elegirEstudianteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el estudiante seleccionado
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    String nombreEstudiante = table1.getValueAt(selectedRow, 2).toString();
                    String cedulaEstudiante = table1.getValueAt(selectedRow, 1).toString();
                    registrarNotas(nombreEstudiante, cedulaEstudiante, nombreProfesor);
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, seleccione un estudiante.");
                }
            }
        });
    }

    private void cargarDatos(String nombreProfesor, DefaultTableModel tableModel) {
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

    private void registrarNotas(String nombreEstudiante, String cedulaEstudiante, String nombreProfesor) {
        // Obtener las materias comunes entre el profesor y el estudiante
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");

            // Obtener la colección de profesores
            MongoCollection<Document> profesoresCollection = database.getCollection("profesores");
            Document profesor = profesoresCollection.find(new Document("nombre", nombreProfesor)).first();

            // Obtener las materias del profesor
            List<String> listaMateriasProfesor = profesor.getList("materias", String.class);

            // Obtener la colección de estudiantes
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");
            Document estudiante = estudiantesCollection.find(new Document("cedula", cedulaEstudiante)).first();

            if (estudiante != null) {
                List<String> materiasEstudiante = estudiante.getList("materias", String.class);

                // Verificar las materias comunes
                List<String> materiasComunes = listaMateriasProfesor.stream()
                        .filter(materiasEstudiante::contains)
                        .toList();

                // Mostrar un cuadro de diálogo para cada materia común
                for (String materia : materiasComunes) {
                    String nota = JOptionPane.showInputDialog(null,
                            "Ingrese la nota para la materia: " + materia,
                            "Registrar Nota", JOptionPane.QUESTION_MESSAGE);

                    if (nota != null && !nota.isEmpty()) {
                        // Validar el formato de la nota (hasta dos decimales)
                        if (NotaValida(nota)) {
                            // Obtener el índice de la materia
                            int materiaIndex = materiasEstudiante.indexOf(materia);

                            // Convertir la nota a tipo Double
                            double notaDouble = Double.parseDouble(nota);

                            // Actualizar el arreglo de notas del estudiante
                            List<Double> notasEstudiante = estudiante.getList("notas", Double.class);
                            notasEstudiante.set(materiaIndex, notaDouble);

                            // Actualizar el documento en la base de datos
                            Document updatedStudent = new Document("notas", notasEstudiante);
                            estudiantesCollection.updateOne(new Document("cedula", cedulaEstudiante), new Document("$set", updatedStudent));

                            JOptionPane.showMessageDialog(null, "Nota registrada correctamente.");
                        } else {
                            JOptionPane.showMessageDialog(null, "La nota no es válida. Debe tener hasta dos decimales.");
                        }
                    } else {
                        // Si se cancela o se cierra el cuadro de entrada, salimos
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Estudiante no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al registrar la nota: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean NotaValida(String nota) {
        try {
            // Verifica si el formato es un número con hasta dos decimales
            return nota.matches("\\d{1,2}\\.\\d{2}");
        } catch (Exception ex) {
            return false;
        }
    }
}
