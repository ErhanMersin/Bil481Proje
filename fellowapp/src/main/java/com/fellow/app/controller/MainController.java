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
    private Tab tabCourses;
    @FXML
    private Tab tabPomodoro;
    @FXML
    private Tab tabTodo;
    @FXML
    private Tab tabStats;

    // Injected by fx:include (JavaFX names them <fx:id>Controller)
    @FXML
    private HomeController homeViewController;
    @FXML
    private CalendarController calendarViewController;
    @FXML
    private CourseController courseViewController;
    @FXML
    private TimerController pomodoroViewController;
    @FXML
    private TodoController todoViewController;
    @FXML
    private StatisticsController statisticsViewController;

    // ── Navigation buttons ────────────────────────────────────────────────────
    @FXML
    private Button navButtonHome;
    @FXML
    private Button navButtonCalendar;
    @FXML
    private Button navButtonCourses;
    @FXML
    private Button navButtonPomodoro;
    @FXML
    private Button navButtonTodo;
    @FXML
    private Button navButtonStats;

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
                    if ("tabCalendar".equals(newTab.getId()) && calendarViewController != null) {
                        calendarViewController.refresh();
                    }
                    if ("tabCourses".equals(newTab.getId()) && courseViewController != null) {
                        courseViewController.refresh();
                    }
                    if ("tabPomodoro".equals(newTab.getId()) && pomodoroViewController != null) {
                        pomodoroViewController.refresh();
                    }
                    if ("tabTodo".equals(newTab.getId()) && todoViewController != null) {
                        todoViewController.refresh();
                    }
                    if ("tabStats".equals(newTab.getId()) && statisticsViewController != null) {
                        statisticsViewController.refresh();
                    }
                });

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
    public void showCourses(ActionEvent event) {
        setSelectedNavButton(navButtonCourses);
        mainTabPane.getSelectionModel().select(tabCourses);
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