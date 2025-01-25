package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import com.mongodb.client.*;

public class interfaz_estudiante {
    private JButton verCalificacionesButton;
    private JButton verHorarioButton;
    private JButton consultaDeAsistenciasButton;
    private JLabel bienvenidaLabel;
    public JPanel interfazEPanel;
    private JButton descargarReporteButton;
    private JButton cerrarSesionButton;

    private static final String url = "";


    public interfaz_estudiante(String nombre) {
        bienvenidaLabel.setText("Bienvenido/a, " + nombre);
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazEPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new login().logPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.setPreferredSize(new Dimension(600, 600));
                frame.pack();
                frame.setVisible(true);
            }
        });

        verCalificacionesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazEPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new notas_estudiante(nombre).notasPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 300);
                frame.setPreferredSize(new Dimension(600, 300));
                frame.pack();
                frame.setVisible(true);
            }
        });

        verHorarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazEPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new horario_estudiante(nombre).horario);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 300);
                frame.setPreferredSize(new Dimension(600, 300));
                frame.pack();
                frame.setVisible(true);
            }
        });

        consultaDeAsistenciasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(interfazEPanel);
                currentFrame.dispose();

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new asistencia_estudiante(nombre).asistenciaPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 300);
                frame.setPreferredSize(new Dimension(600, 300));
                frame.pack();
                frame.setVisible(true);
            }
        });

        descargarReporteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Conexión a MongoDB Atlas
                    MongoClient mongoClient = MongoClients.create(url);
                    MongoDatabase database = mongoClient.getDatabase("prueba_alfa");
                    MongoCollection<org.bson.Document> collection = database.getCollection("estudiantes");

                    // Buscar el estudiante por cédula
                    org.bson.Document estudiante = collection.find(new org.bson.Document("nombre", nombre)).first();
                    String cedula = estudiante.getString("cedula");
                    org.bson.Document estudianteCedula = collection.find(new org.bson.Document("cedula", cedula)).first();

                    if (estudiante == null) {
                        JOptionPane.showMessageDialog(null, "No se encontró información del estudiante.");
                        return;
                    }

                    // Extraer los datos necesarios
                    List<Double> notas = estudiante.getList("notas", Double.class);
                    List<Integer> asistencias = estudiante.getList("asistencias", Integer.class);
                    List<String> horario = estudiante.getList("horario", String.class);

                    // Generar el PDF
                    if (estudianteCedula != null) {
                        String pdfFilePath = "C:\\Users\\MI EQUIPO\\Downloads\\reporte " + estudianteCedula.getString("cedula") + ".pdf";
                        try (PdfWriter writer = new PdfWriter(pdfFilePath)) {
                            PdfDocument pdf = new PdfDocument(writer);
                            Document document = new Document(pdf);

                            // Agregar título
                            document.add(new Paragraph("Reporte del Estudiante")
                                    .setFontSize(18)
                                    .setBold());

                            // Agregar información del estudiante
                            document.add(new Paragraph("Nombre: " + estudianteCedula.getString("nombre")));
                            document.add(new Paragraph("Cédula: " + estudianteCedula.getString("cedula")));
                            document.add(new Paragraph("Curso: " + estudianteCedula.getString("curso")));

                            // Agregar notas
                            document.add(new Paragraph("\nNotas:"));
                            for (int i = 0; i < notas.size(); i++) {
                                document.add(new Paragraph("Materia " + (i + 1) + ": " + notas.get(i)));
                            }

                            // Agregar asistencias
                            document.add(new Paragraph("\nAsistencias:"));
                            for (int i = 0; i < asistencias.size(); i++) {
                                document.add(new Paragraph("Materia " + (i + 1) + ": " + asistencias.get(i)));
                            }

                            // Agregar horario
                            document.add(new Paragraph("\nHorario:"));
                            for (String dia : horario) {
                                document.add(new Paragraph(dia));
                            }

                            document.close();
                            JOptionPane.showMessageDialog(null, "El PDF se generó correctamente en: " + pdfFilePath);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + ex.getMessage());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Cédula inexistente");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + ex.getMessage());
                }
            }
        });
    }

}
