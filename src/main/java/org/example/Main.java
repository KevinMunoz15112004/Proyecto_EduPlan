package org.example;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // URL de conexión a MongoDB Atlas (reemplaza con tu propia URL)
        String uri = "mongodb+srv://kevinmunoz07:mNh1sxHdr4BBBdav@cluster0.sj2qy.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            // Conectar a la base de datos
            MongoDatabase database = mongoClient.getDatabase("Prueba_conexion");
            MongoCollection<Document> usersCollection = database.getCollection("users");

            // Solicitar usuario y contraseña desde consola
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese su nombre de usuario: ");
            String username = scanner.nextLine();
            System.out.print("Ingrese su contraseña: ");
            String password = scanner.nextLine();

            // Verificar si el usuario existe en la base de datos
            Document user = usersCollection.find(new Document("username", username).append("password", password)).first();

            if (user != null) {
                System.out.println("¡Login exitoso!");
                // Puedes agregar aquí la lógica posterior que quieras ejecutar tras el login
            } else {
                System.out.println("Nombre de usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
