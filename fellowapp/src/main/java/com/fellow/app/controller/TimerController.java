package com.fellow.app.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.util.Duration;
import com.fellow.app.dao.StudySessionDAO;
import com.fellow.app.dao.CourseDAO;
import com.fellow.app.model.Course;

public class TimerController {

    @FXML
    private Label timerLabel;
    @FXML
    private Button startButton;
    @FXML
    private ComboBox<Course> cmbCourse;

    private CourseDAO courseDAO = new CourseDAO();
    private final int DEMO_USER_ID = 1;

    private Timeline timeline;
    private int timeSeconds = 25 * 60;
    private int defaultTimeSeconds = 25 * 60;
    private boolean isRunning = false;

    @FXML
    public void initialize() {
        System.out.println("TimerController initialized with smart save feature.");
        refresh();
    }

    public void refresh() {
        if (cmbCourse == null) {
            return;
        }
        Course previous = cmbCourse.getValue();
        var courses = courseDAO.getCoursesByUserId(DEMO_USER_ID);
        cmbCourse.getItems().setAll(courses);
        if (previous != null && courses.contains(previous)) {
            cmbCourse.getSelectionModel().select(previous);
        } else {
            Course defaultCourse = courseDAO.getOrCreateDefaultCourse(DEMO_USER_ID);
            if (defaultCourse != null && courses.contains(defaultCourse)) {
                cmbCourse.getSelectionModel().select(defaultCourse);
            } else if (!courses.isEmpty()) {
                cmbCourse.getSelectionModel().selectFirst();
            }
        }
    }

    @FXML
    public void handleStart(ActionEvent event) {
        if (isRunning)
            return;

        isRunning = true;
        startButton.setText("Running...");
        System.out.println("Timer started.");

        if (timeline == null) {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                timeSeconds--;
                updateLabel();

                // When timer naturally reaches 0
                if (timeSeconds <= 0) {
                    stopAndReset(true); // Stop and save the FULL time
                    System.out.println("Timer finished naturally.");
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
            System.out.println("Timer paused.");
        }
    }

    @FXML
    public void handleReset(ActionEvent event) {
        // If user manually clicks reset, save whatever time elapsed so far!
        stopAndReset(true);
        System.out.println("Timer reset manually.");
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        // Cancel: reset timer without saving any progress
        stopAndReset(false);
        System.out.println("Timer cancelled.");
    }

    /**
     * Core logic for stopping the timer and saving partial/full sessions.
     */
    private void stopAndReset(boolean saveProgress) {
        if (timeline != null)
            timeline.stop();

        if (saveProgress) {
            int elapsedSeconds = defaultTimeSeconds - timeSeconds;

            // Only save if they actually studied for at least 1 second
            if (elapsedSeconds > 0) {
                saveSessionToDatabase(elapsedSeconds);
            }
        }

        isRunning = false;
        timeSeconds = defaultTimeSeconds;
        updateLabel();
        startButton.setText("Start");
    }

    private void saveSessionToDatabase(int durationSeconds) {
        Course selectedCourse = cmbCourse != null ? cmbCourse.getValue() : null;
        if (selectedCourse == null) {
            selectedCourse = courseDAO.getOrCreateDefaultCourse(DEMO_USER_ID);
        }

        // If they finished the whole time, it counts as 1 full pomodoro, else 0.
        int pomodoroCount = (durationSeconds >= defaultTimeSeconds) ? 1 : 0;

        // Save the study session
        new StudySessionDAO().addSession(selectedCourse.getId(), DEMO_USER_ID, durationSeconds, pomodoroCount);
        System.out.println(
                "Session saved successfully: " + durationSeconds + " seconds for " + selectedCourse.getCourseName());
    }

    @FXML
    public void handleIncrementTime() {
        // Only allow changing time if timer is completely fresh (not paused midway)
        if (!isRunning && timeSeconds == defaultTimeSeconds) {
            defaultTimeSeconds += 300; // Add 5 minutes
            timeSeconds = defaultTimeSeconds;
            updateLabel();
        }
    }

    @FXML
    public void handleDecrementTime() {
        // Only allow changing time if timer is completely fresh and > 5 mins
        if (!isRunning && timeSeconds == defaultTimeSeconds && defaultTimeSeconds > 300) {
            defaultTimeSeconds -= 300; // Subtract 5 minutes
            timeSeconds = defaultTimeSeconds;
            updateLabel();
        }
    }

    private void updateLabel() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(formattedTime);

        // Sync with the Top Bar mini widget if running
        if (timerLabel.getScene() != null) {
            Label miniTimerLbl = (Label) timerLabel.getScene().lookup("#miniTimerLabel");
            if (miniTimerLbl != null) {
                miniTimerLbl.setText(formattedTime);
            }
        }
    }

    @FXML
    public void handleMiniMode() {
        if (timerLabel.getScene() == null)
            return;

        // Find and display the widget box securely in top bar
        javafx.scene.layout.HBox widget = (javafx.scene.layout.HBox) timerLabel.getScene().lookup("#miniTimerWidget");
        if (widget != null) {
            widget.setVisible(true);
            widget.setManaged(true);
        }

        TabPane tabPane = (TabPane) timerLabel.getScene().lookup("#mainTabPane");
        if (tabPane != null)
            tabPane.getSelectionModel().select(0);
    }
}