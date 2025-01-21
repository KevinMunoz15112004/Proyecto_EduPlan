package org.example;

import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class tabla_pEstudianteA {
    private JTable table1;
    private JButton elegirEstudianteButton;
    private JButton regresarButton;
    public JPanel tablaPEstudianteA;

    public tabla_pEstudianteA(String nombreProfesor) {
        // Configurar columnas de la tabla
        String[] columnNames = {"ID", "Cédula", "Nombre", "Curso"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);

        // Cargar datos de la base de datos
        cargarDatos(nombreProfesor, tableModel);

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(tablaPEstudianteA);
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

        elegirEstudianteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el estudiante seleccionado
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    String nombreEstudiante = table1.getValueAt(selectedRow, 2).toString();
                    String cedulaEstudiante = table1.getValueAt(selectedRow, 1).toString();
                    registrarAsistencia(nombreEstudiante, cedulaEstudiante, nombreProfesor);
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, seleccione un estudiante.");
                }
            }
        });

    }

    private void cargarDatos(String nombreProfesor, DefaultTableModel tableModel) {
        String url = "";
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");

            // Obtener la colección de profesores
            MongoCollection<Document> profesoresCollection = database.getCollection("profesores");
            Document profesor = profesoresCollection.find(new Document("nombre", nombreProfesor)).first();

            if (profesor != null) {
                // Obtener las materias del profesor
                java.util.List<String> listaMateriasProfesor = profesor.getList("materias", String.class);

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

    private void registrarAsistencia(String nombreEstudiante, String cedulaEstudiante, String nombreProfesor) {
        // Obtener las materias comunes entre el profesor y el estudiante
        String url = "";
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
                    String asistencia = JOptionPane.showInputDialog(null,
                            "Ingrese la Asistencia para la materia: " + materia,
                            "Registrar Asistencia", JOptionPane.QUESTION_MESSAGE);

                    if (asistencia != null && !asistencia.isEmpty()) {
                        // Validar el formato de la nota (hasta dos decimales)
                        if (NotaValida(asistencia)) {
                            // Obtener el índice de la materia
                            int materiaIndex = materiasEstudiante.indexOf(materia);

                            // Nota a tipo INT
                            int asistenciaRegistro = Integer.parseInt(asistencia);

                            // Actualizar el arreglo de notas del estudiante
                            List<Integer> notasEstudiante = estudiante.getList("asistencias", Integer.class);
                            notasEstudiante.set(materiaIndex, asistenciaRegistro);

                            // Actualizar el documento en la base de datos
                            Document updatedStudent = new Document("asistencias", notasEstudiante);
                            estudiantesCollection.updateOne(new Document("cedula", cedulaEstudiante), new Document("$set", updatedStudent));

                            JOptionPane.showMessageDialog(null, "Asistencia registrada correctamente.");
                        } else {
                            JOptionPane.showMessageDialog(null, "La Asistencia no es válida. Debe ser un entero.");
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

    private boolean NotaValida(String asistencia) {
        try {
            // Verifica si el número es un entero sin decimales y de hasta 100 dígitos
            return asistencia.matches("\\d{1,100}");
        } catch (Exception ex) {
            return false;
        }
    }
}
