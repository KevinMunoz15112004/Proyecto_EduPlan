package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mongodb.client.*;
import org.bson.Document;

public class gestion_estudiantes {
    private JTable table1;
    public JPanel interfazAPanel;
    private JButton eliminarButton;
    private JButton actualizarButton;
    private JButton regresarButton;

    private static final String url = "";
    private static final String db = "EduPlan";

    public gestion_estudiantes() {
        String[] columnNames = {"ID", "Cédula", "Nombre", "Curso", "Contraseña"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);

        //carga de datos desde la base
        cargarDatos(tableModel);

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) table1.getValueAt(selectedRow, 0);
                    eliminarUsuario(userId);
                    tableModel.removeRow(selectedRow);
                    reorganizarUserIds();
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un usuario para eliminar.");
                }
            }
        });

        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) table1.getValueAt(selectedRow, 0);

                    //Ingreso de valores nuevos
                    String nuevaCedula = null;
                    while (nuevaCedula == null || nuevaCedula.trim().isEmpty()) {
                        nuevaCedula = JOptionPane.showInputDialog("Nueva cédula:");
                        if (nuevaCedula == null){
                            return;
                        } else if (nuevaCedula.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar una cédula.");
                        } else if (!nuevaCedula.matches("\\d{10}")) {
                            JOptionPane.showMessageDialog(null, "La cédula debe tener 10 dígitos numéricos.");
                            nuevaCedula = null;
                        }
                    }

                    String nuevoNombre = null;
                    while (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                        nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:");
                        if (nuevoNombre == null){
                            return;
                        } else if (nuevoNombre.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar un nombre.");
                        } else if (!nuevoNombre.matches("[A-Za-záéíóúÁÉÍÓÚñÑ\\s]+")) {
                            JOptionPane.showMessageDialog(null, "El nombre no debe contener números ni caracteres especiales.");
                            nuevoNombre = null;
                        }
                    }

                    // Se crea un JComboBox para elegir el curso
                    String[] cursos = {"8vo", "9no", "10mo", "1roBAC", "2doBAC", "3roBAC"};
                    JComboBox<String> cursoComboBox = new JComboBox<>(cursos);
                    int option = JOptionPane.showConfirmDialog(null, cursoComboBox, "Seleccionar curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    String nuevoCurso = null;
                    if (option == JOptionPane.OK_OPTION) {
                        nuevoCurso = (String) cursoComboBox.getSelectedItem();
                    }
                    if (nuevoCurso == null || nuevoCurso.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un curso.");
                        return;
                    }

                    String nuevoPassword = null;
                    while (nuevoPassword == null || nuevoPassword.trim().isEmpty()) {
                        nuevoPassword = JOptionPane.showInputDialog("Nueva Contraseña:");
                        if (nuevoPassword == null){
                            return;
                        } else if (nuevoPassword.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar una contraseña.");
                        } else if (nuevoPassword.length() < 6) {
                            JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 6 caracteres.");
                            nuevoPassword = null;
                        }
                    }

                    // Si las validaciones son correctas, se actualizará el usuario
                    actualizarUsuario(userId, nuevoNombre, nuevoCurso, nuevaCedula, nuevoPassword);
                    table1.setValueAt(nuevaCedula, selectedRow, 1);
                    table1.setValueAt(nuevoNombre, selectedRow, 2);
                    table1.setValueAt(nuevoCurso, selectedRow, 3);
                    table1.setValueAt(nuevoPassword, selectedRow, 4);
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un usuario para actualizar.");
                }
            }
        });

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazAPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Gestión Estudiantes");
                frame.setContentPane(new interfaz_admin().interfaz_Admin);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 400);
                frame.setPreferredSize(new Dimension(400, 400));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void cargarDatos(DefaultTableModel tableModel) {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            // Obtenemos todos los documentos
            MongoCursor<Document> cursor = estudiantesCollection.find().iterator();
            while (cursor.hasNext()) {
                Document estudiante = cursor.next();
                int userId = estudiante.getInteger("user_id");
                String cedula = estudiante.getString("cedula");
                String nombre = estudiante.getString("nombre");
                String curso = estudiante.getString("curso");
                String contrasena = estudiante.getString("password");

                Object[] row = {userId, cedula, nombre, curso, contrasena};
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void eliminarUsuario(int userId) {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            // Eliminar el documento con el user_id específico
            estudiantesCollection.deleteOne(new Document("user_id", userId));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarUsuario(int userId, String nuevoNombre, String nuevoCurso, String nuevaCedula, String nuevaContraseña) {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            Document updatedDocument = new Document("user_id", userId)
                    .append("cedula", nuevaCedula)
                    .append("nombre", nuevoNombre)
                    .append("curso", nuevoCurso)
                    .append("password", nuevaContraseña);

            estudiantesCollection.updateOne(new Document("user_id", userId), new Document("$set", updatedDocument));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void reorganizarUserIds() {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            MongoCursor<Document> cursor = estudiantesCollection.find().sort(new Document("user_id", 1)).iterator();
            int newUserId = 1;
            while (cursor.hasNext()) {
                Document estudiante = cursor.next();
                int oldUserId = estudiante.getInteger("user_id");
                if (oldUserId != newUserId) {
                    estudiantesCollection.updateOne(new Document("user_id", oldUserId),
                            new Document("$set", new Document("user_id", newUserId)));
                }
                newUserId++;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al reorganizar los user_id: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
