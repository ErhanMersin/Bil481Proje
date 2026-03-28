package com.fellow.app.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Duration;

public class MainController {

    // ── Tab pane ──────────────────────────────────────────────────────────────
    @FXML private TabPane mainTabPane;
    @FXML private Tab tabHome;
    @FXML private Tab tabPomodoro;

    // Injected by fx:include — JavaFX names it <fx:id>Controller
    @FXML private HomeController homeViewController;

    // ── Pomodoro (existing, untouched) ────────────────────────────────────────
    @FXML private Label  timerLabel;
    @FXML private Button startButton;

    private Timeline timeline;
    private int     timeSeconds = 25 * 60;
    private int     defaultTimeSeconds = 25 * 60;
    private boolean isRunning   = false;

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
    }

    // ── Pomodoro handlers ─────────────────────────────────────────────────────

    @FXML
    public void handleIncrementTime(ActionEvent event) {
        if (!isRunning) {
            defaultTimeSeconds += 5 * 60;
            if (defaultTimeSeconds > 120 * 60) defaultTimeSeconds = 120 * 60;
            timeSeconds = defaultTimeSeconds;
            updateLabel();
        }
    }

    @FXML
    public void handleDecrementTime(ActionEvent event) {
        if (!isRunning) {
            defaultTimeSeconds -= 5 * 60;
            if (defaultTimeSeconds < 5 * 60) defaultTimeSeconds = 5 * 60;
            timeSeconds = defaultTimeSeconds;
            updateLabel();
        }
    }

    @FXML
    public void handleStart(ActionEvent event) {
        if (isRunning) {
            System.out.println("Timer is already running!");
            return;
        }
        System.out.println("Pomodoro started!");
        isRunning = true;
        startButton.setText("Running...");

        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                timeSeconds--;
                updateLabel();
                if (timeSeconds <= 0) {
                    new com.fellow.app.dao.StudySessionDAO().addSession(1, 1, defaultTimeSeconds, 1);
                    handleReset(null);
                    System.out.println("Session completed and saved!");
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }
        timeline.play();
        updateLabel();
    }

    @FXML
    public void handlePause(ActionEvent event) {
        if (timeline != null && isRunning) {
            timeline.pause();
            isRunning = false;
            startButton.setText("Resume");
            updateLabel();
            System.out.println("Timer paused at: " + timerLabel.getText());
        }
    }

    @FXML
    public void handleReset(ActionEvent event) {
        if (timeline != null) {
            timeline.stop();
        }
        isRunning   = false;
        timeSeconds = defaultTimeSeconds;
        updateLabel();
        startButton.setText("Start");
        System.out.println("Timer reset to " + (defaultTimeSeconds / 60) + ":00");
    }

    private void updateLabel() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        String text = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(text);
        if (homeViewController != null) {
            homeViewController.updateMiniTimer(text, isRunning);
        }
    }

    @FXML
    public void handleMiniMode(ActionEvent event) {
        // Switch to the Home tab where the embedded mini timer is displayed
        mainTabPane.getSelectionModel().select(tabHome);
    }
}
