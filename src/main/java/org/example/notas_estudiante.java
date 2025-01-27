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

public class notas_estudiante {
    private JTable table1;
    private JButton regresarButton;
    public JPanel notasPanel;

    private static final String url = "";
    private static final String db = "notas";
    private static final String cN = "notas";

    public notas_estudiante(String nombre) {
        // Configuraciónd el Jtable
        String[] columnNames = {"Materias", "Nota"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);

        // Cargar los datos desde la base
        cargarNotas(nombre, tableModel);

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame loginFrame = (JFrame) SwingUtilities.getWindowAncestor(notasPanel);
                loginFrame.dispose();

                JFrame frame = new JFrame("Interfaz Estudiante");
                frame.setContentPane(new interfaz_estudiante(nombre).interfazEPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 400);
                frame.setPreferredSize(new Dimension(400, 400));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public void cargarNotas(String nombre, DefaultTableModel tableModel) {
        // Conexión a Mongo
        try(MongoClient mongoClient = MongoClients.create(url)){
            MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
            MongoCollection<Document> nota = database.getCollection("estudiantes");

            // Se busca en el documento del estudiante
            Document estudiante = nota.find(new Document("nombre", nombre)).first();

            if (estudiante != null) {
                List<String> materias = estudiante.getList("materias", String.class);
                List<Double> notas = estudiante.getList("notas", Double.class);

                // Se llena el JTable con la informacion
                for (int i = 0; i < notas.size(); i++) {
                    tableModel.addRow(new Object[]{materias.get(i), notas.get(i)});
                }
            } else{
                JOptionPane.showMessageDialog(null, "Estudiante no encontrado");
            }
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}
