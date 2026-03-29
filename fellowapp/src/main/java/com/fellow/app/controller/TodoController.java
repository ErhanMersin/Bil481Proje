package com.fellow.app.controller;

import com.fellow.app.dao.TodoItemDAO;
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

    @FXML private TextField txtTopic;
    @FXML private TextField txtDescription;
    @FXML private ListView<TodoItem> lvTodos;

    private TodoItemDAO todoDAO = new TodoItemDAO();
    private ObservableList<TodoItem> todoItems = FXCollections.observableArrayList();

    private final int DEMO_USER_ID = 1;
    private final int DEMO_COURSE_ID = 1;

    @FXML
    public void initialize() {
        lvTodos.setItems(todoItems);
        lvTodos.setCellFactory(param -> new TodoListCell());
        loadTodos();
    }

    private void loadTodos() {
        todoItems.clear();
        List<TodoItem> list = todoDAO.getTodosByUserAndCourse(DEMO_USER_ID, DEMO_COURSE_ID);
        todoItems.addAll(list);
    }

    @FXML
    public void handleAddTask() {
        String topic = txtTopic.getText().trim();
        if (topic.isEmpty()) {
            return;
        }
        String desc = txtDescription.getText().trim();
        
        TodoItem item = new TodoItem(DEMO_COURSE_ID, DEMO_USER_ID, topic, desc);
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

            textContainer = new VBox(topicText, descLabel);
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

            checkBox.setSelected(item.isCompleted());
            if (item.isCompleted()) {
                topicText.setStrikethrough(true);
                topicText.setFill(Color.gray(0.5));
            } else {
                topicText.setStrikethrough(false);
                topicText.setFill(null);
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
