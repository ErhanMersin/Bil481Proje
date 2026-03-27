package com.fellow.app.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MainController {

    @FXML
    private Label timerLabel;

    @FXML
    private Button startButton;

    private Timeline timeline;
    private int timeSeconds = 25 * 60; // 25 minutes [cite: 21]
    private boolean isRunning = false;

    @FXML
    public void handleStart(ActionEvent event) {
        if (isRunning) {
            System.out.println("Timer is already running!");
            return;
        }

        System.out.println("▶ Pomodoro started!");
        isRunning = true;
        startButton.setText("Running...");

        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                timeSeconds--;
                updateLabel();

                if (timeSeconds <= 0) {
                    handleReset(null);
                    System.out.println("✅ Session completed!");
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
            System.out.println("⏸ Timer paused at: " + timerLabel.getText());
        }
    }

    @FXML
    public void handleReset(ActionEvent event) {
        if (timeline != null) {
            timeline.stop();
        }
        isRunning = false;
        timeSeconds = 25 * 60; // Reset to original duration 
        updateLabel();
        startButton.setText("Start");
        System.out.println("🔄 Timer reset to 25:00");
    }

    private void updateLabel() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
}