package org.example;

import com.mongodb.client.*;
import org.bson.Document;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class interfaz_admin {
    private JTable table1;
    public JPanel interfazAPanel;
    private JButton eliminarButton;
    private JButton actualizarButton;

    public interfaz_admin() {
        // Establecer las columnas de la tabla
        String[] columnNames = {"user_id", "cedula", "nombre", "curso"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);

        // Cargar los datos de la base de datos
        cargarDatos(tableModel);

        // Acción para eliminar un usuario
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) table1.getValueAt(selectedRow, 0);
                    eliminarUsuario(userId);
                    tableModel.removeRow(selectedRow);
                    reorganizarUserIds(); // Reorganizar el user_id tras eliminar
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un usuario para eliminar.");
                }
            }
        });

        // Acción para actualizar un usuario
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    int userId = (int) table1.getValueAt(selectedRow, 0);
                    String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:");
                    String nuevoCurso = JOptionPane.showInputDialog("Nuevo curso:");
                    String nuevaCedula = JOptionPane.showInputDialog("Nueva cédula:");
                    actualizarUsuario(userId, nuevoNombre, nuevoCurso, nuevaCedula);
                    table1.setValueAt(nuevoNombre, selectedRow, 2);
                    table1.setValueAt(nuevoCurso, selectedRow, 3);
                    table1.setValueAt(nuevaCedula, selectedRow, 1);
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un usuario para actualizar.");
                }
            }
        });
    }

    private void cargarDatos(DefaultTableModel tableModel) {
        String url = "mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority";
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            // Obtener todos los documentos
            MongoCursor<Document> cursor = estudiantesCollection.find().iterator();
            while (cursor.hasNext()) {
                Document estudiante = cursor.next();
                // Usar user_id en lugar de _id
                int userId = estudiante.getInteger("user_id");
                String cedula = estudiante.getString("cedula");
                String nombre = estudiante.getString("nombre");
                String curso = estudiante.getString("curso");

                // Agregar los datos a la tabla
                Object[] row = {userId, cedula, nombre, curso};
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void eliminarUsuario(int userId) {
        String url = "mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority";
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            // Eliminar el documento con el user_id específico
            estudiantesCollection.deleteOne(new Document("user_id", userId));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void actualizarUsuario(int userId, String nuevoNombre, String nuevoCurso, String nuevaCedula) {
        String url = "mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority";
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            // Crear el nuevo documento con los datos actualizados
            Document updatedDocument = new Document("user_id", userId)
                    .append("cedula", nuevaCedula)
                    .append("nombre", nuevoNombre)
                    .append("curso", nuevoCurso);

            // Actualizar el documento en la base de datos
            estudiantesCollection.updateOne(new Document("user_id", userId), new Document("$set", updatedDocument));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void reorganizarUserIds() {
        String url = "mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority";
        try (MongoClient mongoClient = MongoClients.create(url)) {
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> estudiantesCollection = database.getCollection("estudiantes");

            // Obtener todos los documentos y reorganizar los user_id
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
