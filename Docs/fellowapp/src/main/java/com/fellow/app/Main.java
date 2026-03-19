package com.fellow.app;

import javafx.application.Application;
import javafx.stage.Stage;
import com.fellow.app.dao.DatabaseConnection;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // Veritabanını başlat
        try {
            DatabaseConnection.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Pencereyi aç
        primaryStage.setTitle("Fellow - Academic Study Manager");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.show();
        
        System.out.println("✅ Fellow started successfully!");
    }
    
    @Override
    public void stop() throws Exception {
        // Uygulama kapanırken veritabanı bağlantısını kapat
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