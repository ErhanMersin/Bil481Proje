package com.fellow.app.controller;

import com.fellow.app.service.TodoService;
import com.fellow.app.service.CourseService;
import com.fellow.app.model.Course;
import com.fellow.app.model.TodoItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import javafx.application.Platform;

@ExtendWith(JavaFXTestSetup.class)
class TodoControllerTest {

    @Mock
    private CourseService courseService;

    @Mock
    private TodoService todoService;

    @Mock
    private TextField txtTopic;

    @Mock
    private TextField txtDescription;

    @Mock
    private ListView<TodoItem> lvTodos;

    private TodoController todoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        todoController = new TodoController();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field courseServiceField = TodoController.class.getDeclaredField("courseService");
            courseServiceField.setAccessible(true);
            courseServiceField.set(todoController, courseService);

            java.lang.reflect.Field todoServiceField = TodoController.class.getDeclaredField("todoService");
            todoServiceField.setAccessible(true);
            todoServiceField.set(todoController, todoService);

            java.lang.reflect.Field cmbCourseField = TodoController.class.getDeclaredField("cmbCourse");
            cmbCourseField.setAccessible(true);
            cmbCourseField.set(todoController, new ComboBox<Course>());

            java.lang.reflect.Field txtTopicField = TodoController.class.getDeclaredField("txtTopic");
            txtTopicField.setAccessible(true);
            txtTopicField.set(todoController, txtTopic);

            java.lang.reflect.Field txtDescriptionField = TodoController.class.getDeclaredField("txtDescription");
            txtDescriptionField.setAccessible(true);
            txtDescriptionField.set(todoController, txtDescription);

            java.lang.reflect.Field lvTodosField = TodoController.class.getDeclaredField("lvTodos");
            lvTodosField.setAccessible(true);
            lvTodosField.set(todoController, lvTodos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHandleAddTask() {
        // Arrange
        when(txtTopic.getText()).thenReturn("Test Topic");
        when(txtDescription.getText()).thenReturn("Test Description");
        Course selectedCourse = new Course(1, "Math", "#ff0000", 1);
        // Since cmbCourse is real object, set value directly
        try {
            java.lang.reflect.Field cmbCourseField = TodoController.class.getDeclaredField("cmbCourse");
            cmbCourseField.setAccessible(true);
            ComboBox<Course> realCmbCourse = (ComboBox<Course>) cmbCourseField.get(todoController);
            realCmbCourse.setValue(selectedCourse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(todoService.addTodo(any(TodoItem.class))).thenReturn(true);

        // Act
        todoController.handleAddTask();

        // Assert
        verify(todoService).addTodo(any(TodoItem.class));
        verify(txtTopic).clear();
        verify(txtDescription).clear();
        // Note: loadTodos() would be called, but since lvTodos is mocked, we can't
        // verify much
    }

    @Test
    void testHandleAddTaskEmptyTopic() {
        // Arrange
        when(txtTopic.getText()).thenReturn("   ");

        // Act
        todoController.handleAddTask();

        // Assert
        verify(todoService, never()).addTodo(any(TodoItem.class));
        verify(txtTopic, never()).clear();
    }
}