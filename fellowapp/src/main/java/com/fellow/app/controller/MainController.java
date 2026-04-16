package com.fellow.app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Main controller — manages the top navigation bar and tab switching.
 */
public class MainController {

    // ── Tab pane ──────────────────────────────────────────────────────────────
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab tabHome;
    @FXML
    private Tab tabCalendar;
    @FXML
    private Tab tabCourse;
    @FXML
    private Tab tabPomodoro;
    @FXML
    private Tab tabStats;

    @FXML
    private Tab tabTodo;

    // Injected by fx:include (JavaFX names them <fx:id>Controller)
    @FXML
    private HomeController homeViewController;
    @FXML
    private StatisticsController statisticsViewController;

    // ── Navigation buttons ────────────────────────────────────────────────────
    @FXML
    private Button themeToggleButton;
    @FXML
    private Button navButtonHome;
    @FXML
    private Button navButtonCalendar;
    @FXML
    private Button navButtonCourse;
    @FXML
    private Button navButtonPomodoro;
    @FXML
    private Button navButtonTodo;
    @FXML
    private Button navButtonStats;

    private boolean darkTheme = true;
    private Button selectedNavButton;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Open on Home tab
        mainTabPane.getSelectionModel().select(tabHome);

        // Refresh sub-controllers when their tab is selected
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    if (newTab == null)
                        return;
                    if ("tabHome".equals(newTab.getId()) && homeViewController != null) {
                        homeViewController.refresh();
                    }
                    if ("tabStats".equals(newTab.getId()) && statisticsViewController != null) {
                        statisticsViewController.refresh();
                    }
                });

        if (themeToggleButton != null) {
            themeToggleButton.setText(darkTheme ? "Light Mode" : "Dark Mode");
        }

        setSelectedNavButton(navButtonHome);
        System.out.println("MainController initialized.");
    }

    // ── Navigation handlers ───────────────────────────────────────────────────

    @FXML
    public void showHome(ActionEvent event) {
        setSelectedNavButton(navButtonHome);
        mainTabPane.getSelectionModel().select(tabHome);
    }

    @FXML
    public void showCalendar(ActionEvent event) {
        setSelectedNavButton(navButtonCalendar);
        mainTabPane.getSelectionModel().select(tabCalendar);
    }

    @FXML
    public void showCourse(ActionEvent event) {
        setSelectedNavButton(navButtonCourse);
        mainTabPane.getSelectionModel().select(tabCourse);
    }

    @FXML
    public void showPomodoro(ActionEvent event) {
        setSelectedNavButton(navButtonPomodoro);
        mainTabPane.getSelectionModel().select(tabPomodoro);
    }

    @FXML
    public void showTodo(ActionEvent event) {
        setSelectedNavButton(navButtonTodo);
        mainTabPane.getSelectionModel().select(tabTodo);
    }

    @FXML
    public void showStatistics(ActionEvent event) {
        setSelectedNavButton(navButtonStats);
        mainTabPane.getSelectionModel().select(tabStats);
    }

    // ── Theme toggle ──────────────────────────────────────────────────────────

    @FXML
    public void handleThemeToggle(ActionEvent event) {
        Scene scene = themeToggleButton.getScene();
        if (scene == null)
            return;

        scene.getStylesheets().clear();
        String stylesheet = darkTheme ? "/css/light-theme.css" : "/css/dark-theme.css";
        scene.getStylesheets().add(getClass().getResource(stylesheet).toExternalForm());
        darkTheme = !darkTheme;
        themeToggleButton.setText(darkTheme ? "Light Mode" : "Dark Mode");
        System.out.println("Theme switched to: " + (darkTheme ? "dark" : "light"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setSelectedNavButton(Button button) {
        if (selectedNavButton != null) {
            selectedNavButton.getStyleClass().remove("selected-nav-button");
        }
        selectedNavButton = button;
        if (selectedNavButton != null
                && !selectedNavButton.getStyleClass().contains("selected-nav-button")) {
            selectedNavButton.getStyleClass().add("selected-nav-button");
        }
    }
}