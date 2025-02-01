package org.example;

import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class gestion_profesores {
    private JTable table1;
    private JButton eliminarButton;
    private JButton actualizarButton;
    public JPanel interfazA2Admin;
    private JButton regresarButton;

    private static final String url = "";

    public gestion_profesores() {
        // Establecer las columnas de la tabla para el JTable
        String[] columnNames = {"ID", "Cédula", "Nombre", "Materia(s)", "Contraseña"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);

        // Cargar los datos de la base de datos
        cargarDatos(tableModel);

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) table1.getValueAt(selectedRow, 0);
                    eliminarUsuario(userId);
                    tableModel.removeRow(selectedRow);
                    reorganizarUserIds(); // Reorganizar el id tras eliminar
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

                    // Validación de cédula (debe tener 10 dígitos numéricos)
                    String nuevaCedula = null;
                    while (nuevaCedula == null || nuevaCedula.trim().isEmpty()) {
                        nuevaCedula = JOptionPane.showInputDialog("Nueva cédula:");
                        if(nuevaCedula == null){
                            return;
                        } else if (nuevaCedula.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar una cédula.");
                        } else if (!nuevaCedula.matches("\\d{10}")) {
                            JOptionPane.showMessageDialog(null, "La cédula debe tener 10 dígitos numéricos.");
                            nuevaCedula = null;
                        }
                    }

                    // Validación de nombre (no debe contener números ni caracteres especiales)
                    String nuevoNombre = null;
                    while (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                        nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:");
                        if(nuevoNombre == null){
                            return;
                        } else if (nuevoNombre.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar un nombre.");
                        } else if (!nuevoNombre.matches("[A-Za-záéíóúÁÉÍÓÚñÑ\\s]+")) {
                            JOptionPane.showMessageDialog(null, "El nombre no debe contener números ni caracteres especiales.");
                            nuevoNombre = null;
                        }
                    }

                    // Se crea un JCheckBox para seleccionar materias
                    String[] materias = {"Matemáticas", "Literatura", "Estudios Sociales", "Biología", "Cultura Física",
                            "Artística", "Física", "Química", "Filosofía", "Historia", "Lectura Crítica", "Economía"};
                    JCheckBox[] checkBoxes = new JCheckBox[materias.length];
                    JPanel panel = new JPanel();

                    for (int i = 0; i < materias.length; i++) {
                        checkBoxes[i] = new JCheckBox(materias[i]);
                        panel.add(checkBoxes[i]);
                    }

                    int result = JOptionPane.showConfirmDialog(null, panel, "Seleccione las materias",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result != JOptionPane.OK_OPTION) {
                        return;
                    }

                    StringBuilder materiasSeleccionadas = new StringBuilder();
                    while (materiasSeleccionadas.length() == 0) {
                        for (JCheckBox checkBox : checkBoxes) {
                            if (checkBox.isSelected()) {
                                if (materiasSeleccionadas.length() > 0) {
                                    materiasSeleccionadas.append(", ");
                                }
                                materiasSeleccionadas.append(checkBox.getText());
                            }
                        }

                        if (materiasSeleccionadas.length() == 0) {
                            JOptionPane.showMessageDialog(null, "Debe seleccionar al menos una materia.");
                            // Repetir la selección de materias hasta que se elija una o más
                            result = JOptionPane.showConfirmDialog(null, panel, "Seleccione las materias",
                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                            if (result != JOptionPane.OK_OPTION) {
                                return;
                            }
                        }
                    }

                    String nuevaMateria = materiasSeleccionadas.toString();

                    // Validación de contraseña (debe tener al menos 6 caracteres, permitiendo letras y números)
                    String nuevoPassword = null;
                    while (nuevoPassword == null || nuevoPassword.trim().isEmpty()) {
                        nuevoPassword = JOptionPane.showInputDialog("Nueva Contraseña:");
                        if(nuevoPassword == null){
                            return;
                        } else if (nuevoPassword.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar una contraseña.");
                        } else if (nuevoPassword.length() < 6) {
                            JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 6 caracteres.");
                            nuevoPassword = null;
                        }
                    }

                    actualizarUsuario(userId, nuevoNombre, nuevaMateria, nuevaCedula, nuevoPassword);
                    table1.setValueAt(nuevaCedula, selectedRow, 1);
                    table1.setValueAt(nuevoNombre, selectedRow, 2);
                    table1.setValueAt(nuevaMateria, selectedRow, 3);
                    table1.setValueAt(nuevoPassword, selectedRow, 4);
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un usuario para actualizar.");
                }
            }
        });

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazA2Admin);
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
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> estudiantesCollection = database.getCollection("profesores");

            MongoCursor<Document> cursor = estudiantesCollection.find().iterator();
            while (cursor.hasNext()) {
                Document profesor = cursor.next();
                int userId = profesor.getInteger("profesor_id");
                String cedula = profesor.getString("cedula");
                String nombre = profesor.getString("nombre");

                // Obtener materias como lista
                Object materiasObject = profesor.get("materias");
                String curso = "";
                if (materiasObject instanceof List) {
                    List<String> materiasList = (List<String>) materiasObject;
                    curso = String.join(", ", materiasList); //esto une las materias en una cadena separada por comas
                } else if (materiasObject instanceof String) {
                    curso = (String) materiasObject;
                }

                String password = profesor.getString("password");

                // Agregar los datos a la tabla
                Object[] row = {userId, cedula, nombre, curso, password};
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void eliminarUsuario(int userId) {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> estudiantesCollection = database.getCollection("profesores");

            // Eliminar el documento con el user_id específico
            estudiantesCollection.deleteOne(new Document("profesor_id", userId));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarUsuario(int userId, String nuevoNombre, String nuevaMateria, String nuevaCedula, String nuevoPassword) {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> estudiantesCollection = database.getCollection("profesores");

            Document updatedDocument = new Document("profesor_id", userId)
                    .append("cedula", nuevaCedula)
                    .append("nombre", nuevoNombre)
                    .append("materia", nuevaMateria)
                    .append("password", nuevoPassword);

            // Actualizar el documento en la base de datos
            estudiantesCollection.updateOne(new Document("profesor_id", userId), new Document("$set", updatedDocument));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void reorganizarUserIds() {
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> profesoresCollection = database.getCollection("profesores");

            MongoCursor<Document> cursor = profesoresCollection.find().sort(new Document("profesor_id", 1)).iterator();
            int newUserId = 1;
            while (cursor.hasNext()) {
                Document estudiante = cursor.next();
                int oldUserId = estudiante.getInteger("profesor_id");
                if (oldUserId != newUserId) {
                    profesoresCollection.updateOne(new Document("profesor_id", oldUserId),
                            new Document("$set", new Document("profesor_id", newUserId)));
                }
                newUserId++;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al reorganizar los user_id: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
