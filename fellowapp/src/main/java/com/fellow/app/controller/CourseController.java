package com.fellow.app.controller;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CourseController {

    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private ColorPicker colorPicker;
    @FXML private ListView<Course> lvCourses;

    private final CourseDAO courseDAO = new CourseDAO();
    private final ObservableList<Course> courseItems = FXCollections.observableArrayList();
    private final int DEMO_USER_ID = 1;

    @FXML
    public void initialize() {
        lvCourses.setItems(courseItems);
        lvCourses.setCellFactory(listView -> new ListCell<>() {
            private final HBox cellRoot = new HBox();
            private final VBox textContainer = new VBox();
            private final Label titleLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Button deleteButton = new Button();

            {
                titleLabel.getStyleClass().add("course-title");
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                descriptionLabel.getStyleClass().add("course-description");
                descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
                textContainer.getChildren().addAll(titleLabel, descriptionLabel);
                textContainer.setSpacing(4);
                HBox.setHgrow(textContainer, Priority.ALWAYS);

                deleteButton.setOnAction(e -> {
                    Course course = getItem();
                    if (course != null && !isReservedDefaultCourseName(course.getCourseName())) {
                        courseDAO.deleteCourse(course.getId());
                        loadCourses();
                    }
                });
                deleteButton.getStyleClass().add("btn-secondary");
                deleteButton.setStyle("-fx-cursor: hand;");
                cellRoot.setSpacing(10);
                cellRoot.getChildren().addAll(textContainer, deleteButton);
            }

            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    titleLabel.setText(course.getCourseName());
                    String description = course.getDescription();
                    if (description != null && !description.isBlank()) {
                        descriptionLabel.setText(description);
                        descriptionLabel.setVisible(true);
                        descriptionLabel.setManaged(true);
                    } else {
                        descriptionLabel.setVisible(false);
                        descriptionLabel.setManaged(false);
                    }
                    boolean isDefault = isReservedDefaultCourseName(course.getCourseName());
                    deleteButton.setText(isDefault ? "Default" : "Delete");
                    deleteButton.setDisable(isDefault);
                    String textColor = isDarkHexColor(course.getColorHex()) ? "#FFFFFF" : "#000000";
                    titleLabel.setTextFill(Color.web(textColor));
                    descriptionLabel.setTextFill(Color.web(textColor));
                    deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: " + textColor + ";");
                    cellRoot.setStyle("-fx-background-color: " + course.getColorHex() + "33; -fx-padding: 10px; -fx-background-radius: 10px;");
                    setGraphic(cellRoot);
                }
            }
        });

        colorPicker.setValue(Color.web("#6366f1"));
        loadCourses();
    }

    @FXML
    public void handleAddCourse() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            return;
        }

        String description = txtDescription.getText().trim();
        String colorHex = colorPicker.getValue() != null ? toHexString(colorPicker.getValue()) : "#6366f1";

        Course course = new Course(name, description, colorHex, DEMO_USER_ID);
        if (courseDAO.addCourse(course)) {
            txtName.clear();
            txtDescription.clear();
            colorPicker.setValue(Color.web("#6366f1"));
            loadCourses();
        }
    }

    @FXML
    public void handleDeleteCourse() {
        Course selectedCourse = lvCourses.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            return;
        }
        courseDAO.deleteCourse(selectedCourse.getId());
        loadCourses();
    }

    private void loadCourses() {
        courseItems.setAll(courseDAO.getCoursesByUserId(DEMO_USER_ID));
        if (!courseItems.isEmpty()) {
            lvCourses.getSelectionModel().selectFirst();
        }
    }

    public void refresh() {
        loadCourses();
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private boolean isReservedDefaultCourseName(String courseName) {
        return CourseDAO.DEFAULT_COURSE_NAME.equals(courseName);
    }

    private boolean isDarkHexColor(String hex) {
        if (hex == null || !hex.startsWith("#")) {
            return false;
        }
        try {
            int r = Integer.parseInt(hex.substring(1, 3), 16);
            int g = Integer.parseInt(hex.substring(3, 5), 16);
            int b = Integer.parseInt(hex.substring(5, 7), 16);
            int brightness = (r * 299 + g * 587 + b * 114) / 1000;
            return brightness < 140;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
