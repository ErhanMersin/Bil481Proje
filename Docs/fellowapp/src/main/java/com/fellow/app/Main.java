package com.fellow.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // FXML tasarımlarını buraya bağlayacağız
        // Şimdilik boş bir pencere açıyor
        
        primaryStage.setTitle("Fellow - Akademik Çalışma Yönetim Sistemi");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.show();
        
        System.out.println("✅ Fellow başarıyla başlatıldı!");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}