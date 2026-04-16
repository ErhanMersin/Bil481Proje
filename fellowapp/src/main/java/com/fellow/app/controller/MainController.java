package com.fellow.app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

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
    private Tab tabCourses;
    @FXML
    private Tab tabCalendar;
    @FXML
    private Tab tabPomodoro;
    @FXML
    private Tab tabTodo;
    @FXML
    private Tab tabStats;

    // ── Mini Timer Widget ─────────────────────────────────────────────────────
    @FXML
    private VBox miniTimerContainer;
    @FXML
    private Label miniTimerLabel;

    // Injected by fx:include (JavaFX names them <fx:id>Controller)
    @FXML
    private HomeController homeViewController;
    @FXML
    private CourseController coursesViewController;
    @FXML
    private StatisticsController statisticsViewController;
    @FXML
    private TimerController timerViewController;

    // ── Navigation buttons ────────────────────────────────────────────────────
    @FXML
    private Button themeToggleButton;
    @FXML
    private Button navButtonHome;
    @FXML
    private Button navButtonCourses;
    @FXML
    private Button navButtonCalendar;
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

        // Pass MainController reference to TimerController for mini timer management
        if (timerViewController != null) {
            timerViewController.setMainController(this);
        }

        // Refresh sub-controllers when their tab is selected
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    if (newTab == null)
                        return;
                    if ("tabHome".equals(newTab.getId()) && homeViewController != null) {
                        homeViewController.refresh();
                    }
                    if ("tabCourses".equals(newTab.getId()) && coursesViewController != null) {
                        coursesViewController.refresh();
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
    public void showCourses(ActionEvent event) {
        setSelectedNavButton(navButtonCourses);
        mainTabPane.getSelectionModel().select(tabCourses);
    }

    @FXML
    public void showCalendar(ActionEvent event) {
        setSelectedNavButton(navButtonCalendar);
        mainTabPane.getSelectionModel().select(tabCalendar);
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

    // ── Mini Timer Widget Management ──────────────────────────────────────────

    /**
     * Show the mini timer widget in the top bar.
     */
    public void showMiniTimer() {
        if (miniTimerContainer != null) {
            miniTimerContainer.setVisible(true);
        }
    }

    /**
     * Hide the mini timer widget in the top bar.
     */
    public void hideMiniTimer() {
        if (miniTimerContainer != null) {
            miniTimerContainer.setVisible(false);
        }
    }

    /**
     * Update the mini timer display with the current time.
     *
     * @param timeText The formatted time string (e.g., "24:59")
     */
    public void updateMiniTimerDisplay(String timeText) {
        if (miniTimerLabel != null) {
            miniTimerLabel.setText(timeText);
        }
    }
}