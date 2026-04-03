package com.fellow.app.controller;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.dao.TodoItemDAO;
import com.fellow.app.model.Course;
import com.fellow.app.model.TodoItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

public class TodoController {

    @FXML
    private ComboBox<Course> cmbCourse;
    @FXML
    private TextField txtTopic;
    @FXML
    private TextField txtDescription;
    @FXML
    private ListView<TodoItem> lvTodos;

    private CourseDAO courseDAO = new CourseDAO();
    private TodoItemDAO todoDAO = new TodoItemDAO();
    private ObservableList<TodoItem> todoItems = FXCollections.observableArrayList();
    private ObservableList<Course> courseItems = FXCollections.observableArrayList();
    private final Course allCourse = new Course(0, "All", "#eeeeee", 1);

    private final int DEMO_USER_ID = 1;

    @FXML
    public void initialize() {
        lvTodos.setItems(todoItems);
        lvTodos.setCellFactory(param -> new TodoListCell());
        refresh();

        if (cmbCourse != null) {
            cmbCourse.setOnAction(event -> loadTodos());
        }
    }

    public void refresh() {
        loadCourses();
        loadTodos();
    }

    private void loadCourses() {
        courseItems.setAll(courseDAO.getCoursesByUserId(DEMO_USER_ID));
        ObservableList<Course> items = FXCollections.observableArrayList();
        items.add(allCourse);
        items.addAll(courseItems);
        cmbCourse.setItems(items);

        if (!items.isEmpty()) {
            Course current = cmbCourse.getValue();
            if (current != null && items.contains(current)) {
                cmbCourse.getSelectionModel().select(current);
            } else {
                cmbCourse.getSelectionModel().select(allCourse);
            }
        }
    }

    private void loadTodos() {
        todoItems.clear();
        Course selectedCourse = cmbCourse.getValue();
        if (selectedCourse == null) {
            selectedCourse = allCourse;
            cmbCourse.getSelectionModel().select(allCourse);
        }

        List<TodoItem> list;
        if (selectedCourse.getId() == 0) {
            list = todoDAO.getTodosByUserId(DEMO_USER_ID);
        } else {
            list = todoDAO.getTodosByUserAndCourse(DEMO_USER_ID, selectedCourse.getId());
        }
        todoItems.addAll(list);
    }

    private boolean isDarkMode() {
        javafx.scene.Scene scene = lvTodos != null ? lvTodos.getScene() : null;
        if (scene == null) {
            return true;
        }
        return scene.getStylesheets().stream().anyMatch(s -> s.contains("dark-theme.css"));
    }

    @FXML
    public void handleAddTask() {
        String topic = txtTopic.getText().trim();
        if (topic.isEmpty()) {
            return;
        }
        String desc = txtDescription.getText().trim();
        Course selectedCourse = cmbCourse.getValue();
        if (selectedCourse == null || selectedCourse.getId() == 0) {
            selectedCourse = courseDAO.getOrCreateDefaultCourse(DEMO_USER_ID);
        }
        if (selectedCourse == null && !courseItems.isEmpty()) {
            selectedCourse = courseItems.get(0);
            cmbCourse.getSelectionModel().selectFirst();
        }
        if (selectedCourse == null) {
            return;
        }

        TodoItem item = new TodoItem(selectedCourse.getId(), DEMO_USER_ID, topic, desc);
        if (todoDAO.addTodo(item)) {
            txtTopic.clear();
            txtDescription.clear();
            loadTodos();
        }
    }

    private class TodoListCell extends ListCell<TodoItem> {
        private HBox content;
        private CheckBox checkBox;
        private VBox textContainer;
        private Text topicText;
        private Label courseLabel;
        private Label descLabel;
        private Button btnDelete;

        public TodoListCell() {
            super();
            checkBox = new CheckBox();
            checkBox.setOnAction(e -> {
                TodoItem item = getItem();
                if (item != null) {
                    item.setCompleted(checkBox.isSelected());
                    todoDAO.updateCompleted(item.getId(), item.isCompleted());
                    updateItemAppearance(item);
                }
            });

            topicText = new Text();
            topicText.getStyleClass().add("todo-topic-text");
            topicText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            descLabel = new Label();
            descLabel.getStyleClass().add("todo-desc-label");
            descLabel.setStyle("-fx-font-size: 12px;");

            courseLabel = new Label();
            courseLabel.getStyleClass().add("todo-course-label");
            courseLabel.setStyle(
                    "-fx-font-size: 11px; -fx-padding: 2 6 2 6; -fx-background-radius: 4; -fx-background-color: #ddd;");

            textContainer = new VBox(topicText, courseLabel, descLabel);
            HBox.setHgrow(textContainer, Priority.ALWAYS);
            textContainer.setSpacing(2);

            btnDelete = new Button("Delete");
            btnDelete.getStyleClass().add("todo-delete-button");
            btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            btnDelete.setOnAction(e -> {
                TodoItem item = getItem();
                if (item != null) {
                    todoDAO.deleteTodo(item.getId());
                    loadTodos();
                }
            });

            content = new HBox(checkBox, textContainer, btnDelete);
            content.setSpacing(15);
            content.getStyleClass().add("todo-item-cell");
            content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        }

        private void updateItemAppearance(TodoItem item) {
            topicText.setText(item.getTopic());
            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                descLabel.setText(item.getDescription());
                descLabel.setVisible(true);
                descLabel.setManaged(true);
            } else {
                descLabel.setVisible(false);
                descLabel.setManaged(false);
            }

            boolean darkMode = isDarkMode();
            Course course = courseDAO.getCourseById(item.getCourseId());
            if (course != null) {
                courseLabel.setText(course.getCourseName());
                String textColor = darkMode ? "#FFFFFF" : "#222222";
                courseLabel.setStyle(
                        "-fx-font-size: 11px; -fx-padding: 2 6 2 6; -fx-background-radius: 4; -fx-background-color: "
                                + course.getColorHex() + "33; -fx-text-fill: " + textColor + ";");
                courseLabel.setVisible(true);
                courseLabel.setManaged(true);
            } else {
                courseLabel.setText("Unknown course");
                String textColor = darkMode ? "#FFFFFF" : "#222222";
                courseLabel.setStyle(
                        "-fx-font-size: 11px; -fx-padding: 2 6 2 6; -fx-background-radius: 4; -fx-background-color: #dddddd; -fx-text-fill: "
                                + textColor + ";");
                courseLabel.setVisible(true);
                courseLabel.setManaged(true);
            }

            checkBox.setSelected(item.isCompleted());
            if (item.isCompleted()) {
                topicText.setStrikethrough(true);
                topicText.setFill(Color.gray(0.5));
            } else {
                topicText.setStrikethrough(false);
                topicText.setFill(darkMode ? Color.WHITE : Color.web("#222222"));
            }
        }

        @Override
        protected void updateItem(TodoItem item, boolean empty) {
            super.updateItem(item, empty);
            setStyle("-fx-background-color: transparent; -fx-padding: 5px;");
            if (empty || item == null) {
                setGraphic(null);
            } else {
                updateItemAppearance(item);
                setGraphic(content);
            }
        }
    }
}
