package com.fellow.app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {

    // ── Tab pane ──────────────────────────────────────────────────────────────
    @FXML private TabPane mainTabPane;
    @FXML private Tab tabHome;
    @FXML private Tab tabPomodoro;

    // Injected by fx:include — JavaFX names it <fx:id>Controller
    @FXML private HomeController homeViewController;

    // ── UI Elements ───────────────────────────────────────────────────────────
    @FXML private Button themeToggleButton;
    @FXML private Button navButtonHome;
    @FXML private Button navButtonCalendar;
    @FXML private Button navButtonPomodoro;
    @FXML private Button navButtonTodo;
    @FXML private Button navButtonStats;

    private boolean darkTheme = true;
    private Button selectedNavButton;

    // ── Initialization ────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Select Home tab on startup
        mainTabPane.getSelectionModel().select(tabHome);

        mainTabPane.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldTab, newTab) -> {
                if (newTab != null && "tabHome".equals(newTab.getId()) && homeViewController != null) {
                    homeViewController.refresh();
                }
            }
        );

        if (themeToggleButton != null) {
            themeToggleButton.setText(darkTheme ? "Light Mode" : "Dark Mode");
        }

        setSelectedNavButton(navButtonHome);
    }

    private void setSelectedNavButton(Button button) {
        if (selectedNavButton != null) {
            selectedNavButton.getStyleClass().remove("selected-nav-button");
        }
        selectedNavButton = button;
        if (selectedNavButton != null && !selectedNavButton.getStyleClass().contains("selected-nav-button")) {
            selectedNavButton.getStyleClass().add("selected-nav-button");
        }
    }

    // ── Navigation Handlers ───────────────────────────────────────────────────

    @FXML
    public void showHome(ActionEvent event) {
        setSelectedNavButton(navButtonHome);
        mainTabPane.getSelectionModel().select(tabHome);
    }

    @FXML
    public void showCalendar(ActionEvent event) {
        setSelectedNavButton(navButtonCalendar);
        mainTabPane.getSelectionModel().select(1);
    }

    @FXML
    public void showPomodoro(ActionEvent event) {
        setSelectedNavButton(navButtonPomodoro);
        mainTabPane.getSelectionModel().select(tabPomodoro);
    }

    @FXML
    public void showTodo(ActionEvent event) {
        setSelectedNavButton(navButtonTodo);
        mainTabPane.getSelectionModel().select(3);
    }

    @FXML
    public void showStatistics(ActionEvent event) {
        setSelectedNavButton(navButtonStats);
        mainTabPane.getSelectionModel().select(4);
    }

    @FXML
    public void handleThemeToggle(ActionEvent event) {
        Scene scene = themeToggleButton.getScene();
        if (scene == null) {
            return;
        }

        scene.getStylesheets().clear();
        String stylesheet = darkTheme ? "/css/light-theme.css" : "/css/dark-theme.css";
        scene.getStylesheets().add(getClass().getResource(stylesheet).toExternalForm());
        darkTheme = !darkTheme;
        themeToggleButton.setText(darkTheme ? "Light Mode" : "Dark Mode");
    }
}