package com.fellow.app.controller;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.dao.EventDAO;
import com.fellow.app.model.Course;
import com.fellow.app.model.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class AddEventController {
    @FXML private TextField txtTitle;
    @FXML private ComboBox<String> cmbType;
    @FXML private ComboBox<Course> cmbCourse;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtTime;
    @FXML private TextField txtDesc;

    private boolean isSaved = false;
    private final int DEMO_USER_ID = 1;
    private final CourseDAO courseDAO = new CourseDAO();

    @FXML
    public void initialize() {
        cmbType.getItems().addAll("EXAM", "PROJECT", "HOMEWORK", "QUIZ", "LECTURE");

        cmbCourse.getItems().setAll(courseDAO.getCoursesByUserId(DEMO_USER_ID));
        Course defaultCourse = courseDAO.getOrCreateDefaultCourse(DEMO_USER_ID);
        if (defaultCourse != null && cmbCourse.getItems().contains(defaultCourse)) {
            cmbCourse.getSelectionModel().select(defaultCourse);
        } else if (!cmbCourse.getItems().isEmpty()) {
            cmbCourse.getSelectionModel().selectFirst();
        }

        javafx.util.Callback<ListView<String>, ListCell<String>> cellFactory = lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("");
            }
        };
        cmbType.setCellFactory(cellFactory);
        cmbType.setButtonCell(cellFactory.call(null));

        cmbType.getSelectionModel().selectFirst();
        dpDate.setValue(LocalDate.now());
    }

    public void setDate(LocalDate date) {
        if (dpDate != null) {
            dpDate.setValue(date);
        }
    }

    public boolean isSaved() {
        return isSaved;
    }

    @FXML
    private void handleSave() {
        String title = txtTitle.getText().trim();
        if (title.isEmpty()) {
            showAlert("Error", "Title cannot be empty.");
            return;
        }
        LocalDate date = dpDate.getValue();
        if (date == null) {
            showAlert("Error", "Please select a date.");
            return;
        }

        Event e = new Event();
        e.setTitle(title);
        e.setType(cmbType.getValue());
        Course selectedCourse = cmbCourse.getValue();
        if (selectedCourse != null) {
            e.setCourseId(selectedCourse.getId());
        } else {
            selectedCourse = courseDAO.getOrCreateDefaultCourse(DEMO_USER_ID);
            if (selectedCourse != null) {
                e.setCourseId(selectedCourse.getId());
            }
        }
        e.setUserId(DEMO_USER_ID);
        e.setEventDate(date);
        
        String timeStr = txtTime.getText().trim();
        e.setEventTime(timeStr.isEmpty() ? null : timeStr); // Allow null for optional time
        
        String descStr = txtDesc.getText().trim();
        e.setDescription(descStr.isEmpty() ? null : descStr);

        EventDAO dao = new EventDAO();
        if (dao.addEvent(e)) {
            isSaved = true;
            closeStage();
        } else {
            showAlert("Error", "Could not save event to database.");
        }
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) txtTitle.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
