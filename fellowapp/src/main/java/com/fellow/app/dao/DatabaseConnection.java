package com.fellow.app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * SQLite veritabanı bağlantısını yönetir
 * Manages SQLite database connection
 */
public class DatabaseConnection {
    
    private static final String DB_URL = "jdbc:sqlite:fellow.db";
    private static Connection connection;
    
    /**
     * Veritabanı bağlantısını döndürür (Singleton pattern)
     * Returns database connection (Singleton pattern)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }
    
    /**
     * Veritabanını başlatır - tabloları oluşturur
     * Initializes database - creates tables
     */
    public static void initializeDatabase() throws SQLException {
        Connection conn = getConnection();
        
        try {
            // schema.sql dosyasını oku
            InputStream inputStream = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("database/schema.sql");
            
            if (inputStream == null) {
                throw new RuntimeException("schema.sql file not found!");
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sql = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Yorum satırlarını atla
                if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                    sql.append(line).append(" ");
                }
            }
            
            reader.close();
            
            // SQL komutlarını çalıştır
            Statement statement = conn.createStatement();
            String[] sqlCommands = sql.toString().split(";");
            
            for (String command : sqlCommands) {
                if (!command.trim().isEmpty()) {
                    statement.execute(command.trim());
                }
            }
            
            statement.close();
            System.out.println("✅ Database created successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
            throw new SQLException(e);
        }
    }
    
    /**
     * Bağlantıyı kapatır
     * Closes the connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}