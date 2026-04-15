package com.fellow.app.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class NotificationUtil {
    
    /**
     * Shows an error alert with the specified title and message
     */
    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }
    
    /**
     * Shows an information alert with the specified title and message
     */
    public static void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }
    
    /**
     * Shows a warning alert with the specified title and message
     */
    public static void showWarning(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }
    
    /**
     * Shows a confirmation dialog and returns true if user clicks OK
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }
    
    /**
     * Shows a success message (information alert with "Success" title)
     */
    public static void showSuccess(String message) {
        showInfo("Success", message);
    }
    
    /**
     * Shows an error message for validation failures
     */
    public static void showValidationError(String message) {
        showError("Validation Error", message);
    }
    
    /**
     * Shows an error message for database operation failures
     */
    public static void showDatabaseError(String operation) {
        showError("Database Error", "Failed to " + operation + ". Please try again.");
    }
    
    /**
     * Shows an error message for save operation failures
     */
    public static void showSaveError(String itemType) {
        showDatabaseError("save " + itemType);
    }
    
    /**
     * Shows an error message for delete operation failures
     */
    public static void showDeleteError(String itemType) {
        showDatabaseError("delete " + itemType);
    }
    
    /**
     * Shows an error message for load operation failures
     */
    public static void showLoadError(String itemType) {
        showDatabaseError("load " + itemType + "s");
    }
}
