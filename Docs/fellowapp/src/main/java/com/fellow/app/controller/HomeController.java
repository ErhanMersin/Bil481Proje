package com.fellow.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controller for the Home / Dashboard tab.
 * Stats and events are placeholder values for now.
 * Will be connected to real services once they are implemented.
 */
public class HomeController {

    // ── FXML bindings ─────────────────────────────────────────────────────────
    @FXML private Label lblDate;
    @FXML private Label lblStudyTime;
    @FXML private Label lblPomodoros;
    @FXML private Label lblActiveTodos;
    @FXML private ListView<String> listUpcomingEvents;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setTodayDate();
        loadPlaceholderStats();
        loadPlaceholderEvents();
        System.out.println("HomeController initialized.");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Writes today's date in English (e.g. "Friday, March 27"). */
    private void setTodayDate() {
        String formatted = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH));
        lblDate.setText(formatted);
    }

    /**
     * Shows zeroed-out stat cards.
     * Replace with real service calls once services are ready.
     */
    private void loadPlaceholderStats() {
        lblStudyTime.setText("0h 0m");
        lblPomodoros.setText("0");
        lblActiveTodos.setText("0");
    }

    /**
     * Shows a placeholder message in the events list.
     * Replace with real EventService call once it is implemented.
     */
    private void loadPlaceholderEvents() {
        listUpcomingEvents.getItems().clear();
        listUpcomingEvents.getItems().add("No upcoming events — add one from the Calendar tab.");
    }

    // ── Button handlers ───────────────────────────────────────────────────────

    /** Switches to the Pomodoro tab when "Start Pomodoro" is clicked. */
    @FXML
    private void handleStartTimer() {
        switchToTab("tabPomodoro");
    }

    /** Switches to the ToDo tab when "Add ToDo" is clicked. */
    @FXML
    private void handleAddTodo() {
        switchToTab("tabTodo");
    }

    /** Finds a tab by its fx:id and selects it. */
    private void switchToTab(String tabFxId) {
        try {
            TabPane tabPane = (TabPane) lblDate.getScene().lookup("#mainTabPane");
            if (tabPane == null) {
                System.out.println("HomeController - mainTabPane not found.");
                return;
            }
            tabPane.getTabs().stream()
                    .filter(t -> tabFxId.equals(t.getId()))
                    .findFirst()
                    .ifPresent(t -> tabPane.getSelectionModel().select(t));
        } catch (Exception e) {
            System.out.println("HomeController - could not switch to " + tabFxId + ": " + e.getMessage());
        }
    }

    // ── Public API (called by MainController) ─────────────────────────────────

    /**
     * Refreshes the dashboard.
     * Called by MainController whenever the Home tab is re-selected.
     */
    public void refresh() {
        loadPlaceholderStats();
        loadPlaceholderEvents();
        System.out.println("HomeController - dashboard refreshed.");
    }
}