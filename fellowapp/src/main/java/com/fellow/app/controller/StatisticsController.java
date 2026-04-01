package com.fellow.app.controller;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.model.Course;
import com.fellow.app.service.StatisticsService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import java.util.List;

public class StatisticsController {

    @FXML
    private ToggleButton btnDay;
    @FXML
    private ToggleButton btnWeek;
    @FXML
    private ToggleButton btnMonth;
    @FXML
    private ComboBox<String> cmbCourse;
    @FXML
    private Label lblResult;

    private StatisticsService statsService = new StatisticsService();
    private CourseDAO courseDAO = new CourseDAO();
    private StatisticsService.TimeStrategy currentStrategy;
    private final int DEMO_USER_ID = 1;

    @FXML
    public void initialize() {
        // Varsayılan olarak "This Week" seçili başlar
        btnWeek.setSelected(true);
        currentStrategy = new StatisticsService.WeeklyStrategy();
        refresh();
    }

    // MAIN CONTROLLER TARAFINDAN ÇAĞIRILACAK YENİLEME METODU
    public void refresh() {
        if (currentStrategy == null)
            return;

        String previousSelection = cmbCourse.getValue();
        cmbCourse.getItems().clear();

        // Load all courses for the user, even if they have no study sessions
        List<Course> courses = courseDAO.getCoursesByUserId(DEMO_USER_ID);
        for (Course course : courses) {
            cmbCourse.getItems().add(course.getCourseName());
        }

        if (cmbCourse.getItems().contains(previousSelection)) {
            cmbCourse.getSelectionModel().select(previousSelection);
        } else if (!cmbCourse.getItems().isEmpty()) {
            cmbCourse.getSelectionModel().selectFirst();
        } else {
            lblResult.setText("No courses available.");
        }
        updateResult();
    }

    @FXML
    public void handleRangeChange(ActionEvent event) {
        ToggleButton source = (ToggleButton) event.getSource();
        if (!source.isSelected()) {
            source.setSelected(true);
            return; // Tıklanan butonun kapanmasını engelle
        }

        if (source == btnDay) {
            btnWeek.setSelected(false);
            btnMonth.setSelected(false);
            currentStrategy = new StatisticsService.DailyStrategy();
        } else if (source == btnWeek) {
            btnDay.setSelected(false);
            btnMonth.setSelected(false);
            currentStrategy = new StatisticsService.WeeklyStrategy();
        } else if (source == btnMonth) {
            btnDay.setSelected(false);
            btnWeek.setSelected(false);
            currentStrategy = new StatisticsService.MonthlyStrategy();
        }

        refresh();
    }

    @FXML
    public void handleCourseChange() {
        updateResult();
    }

    private void updateResult() {
        String selectedCourse = cmbCourse.getValue();
        if (selectedCourse == null) {
            if (cmbCourse.getItems().isEmpty()) {
                lblResult.setText("No study sessions found for " + currentStrategy.label() + ".");
            } else {
                lblResult.setText("Please select a course to see your study time.");
            }
            return;
        }

        int courseId = statsService.getCourseIdByName(DEMO_USER_ID, selectedCourse);
        String report = statsService.getStudyTimeReport(DEMO_USER_ID, courseId, selectedCourse, currentStrategy);
        lblResult.setText(report);
    }
}