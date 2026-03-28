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
            
            // Seed base data to satisfy NOT NULL constraints without User/Course modules built yet
            seedDummyData(conn);
            
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
    
    /**
     * Seeds dummy data required for other models to function without errors
     */
    private static void seedDummyData(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            
            // Check if dummy user exists
            java.sql.ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users WHERE id = 1");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (id, name, email) VALUES (1, 'Test User', 'test@fellow.app')");
            }
            rs.close();
            
            // Check if dummy course exists
            rs = stmt.executeQuery("SELECT count(*) FROM courses WHERE id = 1");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO courses (id, name, color_hex, user_id) VALUES (1, 'Genel', '#6366f1', 1)");
            }
            rs.close();
            
            // Check if dummy events exist
            rs = stmt.executeQuery("SELECT count(*) FROM events WHERE user_id = 1");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO events (title, type, course_id, event_date, event_time, description, user_id, created_date) VALUES ('C++ Algoritmalar Finali', 'EXAM', 1, '2026-06-15', '14:00', 'Çalış', 1, '2026-03-25 10:00:00')");
                stmt.execute("INSERT INTO events (title, type, course_id, event_date, event_time, description, user_id, created_date) VALUES ('Veri Yapıları Ağaç Projesi', 'PROJECT', 1, '2026-04-10', '23:59', 'Teslim tarihi', 1, '2026-03-28 10:00:00')");
                stmt.execute("INSERT INTO events (title, type, course_id, event_date, event_time, description, user_id, created_date) VALUES ('Matematik Ödevi', 'HOMEWORK', 1, '2026-04-05', '09:00', 'Sayfa 50', 1, '2026-03-27 10:00:00')");
            }
            rs.close();
            
            stmt.close();
        } catch (Exception e) {
            System.err.println("❌ Error seeding database: " + e.getMessage());
        }
    }
}