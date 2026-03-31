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
import com.fellow.app.dao.StudySessionDAO;

public class TimerController {

    @FXML private Label timerLabel;
    @FXML private Button startButton;
    
    private Timeline timeline;
    private int timeSeconds = 25 * 60;
    private int defaultTimeSeconds = 25 * 60;
    private boolean isRunning = false;

    @FXML
    public void handleStart(ActionEvent event) {
        if (isRunning) return;
        isRunning = true;
        startButton.setText("Running...");

        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                timeSeconds--;
                updateLabel();
                if (timeSeconds <= 0) {
                    new StudySessionDAO().addSession(1, 1, defaultTimeSeconds, 1);
                    handleReset(null);
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }
        timeline.play();
    }

    @FXML
    public void handlePause(ActionEvent event) {
        if (timeline != null && isRunning) {
            timeline.pause();
            isRunning = false;
            startButton.setText("Resume");
        }
    }

    @FXML
    public void handleReset(ActionEvent event) {
        if (timeline != null) timeline.stop();
        isRunning = false;
        timeSeconds = defaultTimeSeconds;
        updateLabel();
        startButton.setText("Start");
    }

    @FXML
    public void handleIncrementTime() {
        if (!isRunning) {
            defaultTimeSeconds += 300;
            timeSeconds = defaultTimeSeconds;
            updateLabel();
        }
    }

    @FXML
    public void handleDecrementTime() {
        if (!isRunning && defaultTimeSeconds > 300) {
            defaultTimeSeconds -= 300;
            timeSeconds = defaultTimeSeconds;
            updateLabel();
        }
    }

    private void updateLabel() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @FXML
    public void handleMiniMode() {
        // TabPane'i bul ve Home sekmesine geç
        TabPane tabPane = (TabPane) timerLabel.getScene().lookup("#mainTabPane");
        if (tabPane != null) tabPane.getSelectionModel().select(0);
    }
}