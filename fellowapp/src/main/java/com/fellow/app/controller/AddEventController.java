package com.fellow.app.controller;

import com.fellow.app.service.EventService;
import com.fellow.app.service.CourseService;
import com.fellow.app.model.Course;
import com.fellow.app.model.Event;
import com.fellow.app.util.NotificationUtil;
import com.fellow.app.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class AddEventController {
    @FXML
    private TextField txtTitle;
    @FXML
    private ComboBox<String> cmbType;
    @FXML
    private ComboBox<Course> cmbCourse;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField txtTime;
    @FXML
    private TextField txtDesc;

    private boolean isSaved = false;
    private final int DEMO_USER_ID = 1;
    private final EventService eventService = new EventService();
    private final CourseService courseService = new CourseService();

    @FXML
    public void initialize() {
        cmbType.getItems().addAll("EXAM", "PROJECT", "HOMEWORK", "QUIZ", "LECTURE", "OTHER");

        cmbCourse.getItems().setAll(courseService.getCoursesByUserId(DEMO_USER_ID));
        Course defaultCourse = courseService.getOrCreateDefaultCourse(DEMO_USER_ID);
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
        if (!ValidationUtil.isValidEventTitle(title)) {
            NotificationUtil.showValidationError("Title cannot be empty and must be less than 200 characters.");
            return;
        }
        LocalDate date = dpDate.getValue();
        if (date == null) {
            NotificationUtil.showValidationError("Please select a date.");
            return;
        }

        String timeInput = txtTime.getText().trim();
        if (!ValidationUtil.isNullOrEmpty(timeInput) && !ValidationUtil.isValidTimeFormat(timeInput)) {
            NotificationUtil.showValidationError("Time must be in HH:mm format.");
            return;
        }

        Event e = new Event();
        e.setTitle(title);
        e.setType(cmbType.getValue());
        Course selectedCourse = cmbCourse.getValue();
        if (selectedCourse != null) {
            e.setCourseId(selectedCourse.getId());
        } else {
            selectedCourse = courseService.getOrCreateDefaultCourse(DEMO_USER_ID);
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

        if (eventService.addEvent(e)) {
            isSaved = true;
            closeStage();
        } else {
            NotificationUtil.showSaveError("event");
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
}
