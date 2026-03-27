package com.fellow.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.fellow.app.dao.DatabaseConnection;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // 1. Initialize the Database
        try {
            DatabaseConnection.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 2. Load the FXML Design (This is what prevents the white screen!)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();
        
        // 3. Set up the Scene and Show the Window
        primaryStage.setTitle("Fellow - Academic Study Manager");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
        
        System.out.println("✅ Fellow started successfully with UI!");
    }
    
    @Override
    public void stop() throws Exception {
        // Close database connection when application stops
        DatabaseConnection.closeConnection();
        super.stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
/*
┌─────────────────────────────────────────┐
│  VIEW LAYER (Kullanıcı Arayüzü)        │ ← Kullanıcı buraya tıklar
│  - FXML dosyaları                       │
│  - Controller'lar                       │
└──────────────┬──────────────────────────┘
               │ "Buton tıklandı"
               ↓
┌─────────────────────────────────────────┐
│  SERVICE LAYER (İş Mantığı)             │ ← İş kuralları burada
│  - CourseService                        │
│  - PomodoroTimer                        │
│  - StatisticsService                    │
└──────────────┬──────────────────────────┘
               │ "Veri lazım / Kaydet"
               ↓
┌─────────────────────────────────────────┐
│  DAO LAYER (Veri Erişimi)               │ ← SQL komutları burada
│  - CourseDAO                            │
│  - EventDAO                             │
│  - DatabaseConnection                   │
└──────────────┬──────────────────────────┘
               │ "SELECT, INSERT, UPDATE"
               ↓
┌─────────────────────────────────────────┐
│  DATABASE (SQLite)                      │ ← Veriler burada
│  - fellow.db                            │
│  - Tables: courses, events, etc.        │
└─────────────────────────────────────────┘

         MODEL LAYER (Her yerde kullanılır)
    ┌────────────────────────────────┐
    │  - User, Course, Event         │ ← Veri nesneleri
    │  - StudySession, TodoItem      │
    └────────────────────────────────┘

         UTIL LAYER (Yardımcılar)
    ┌────────────────────────────────┐
    │  - DateTimeUtil                │ ← Herkes kullanır
    │  - ValidationUtil              │
    └────────────────────────────────┘
 */