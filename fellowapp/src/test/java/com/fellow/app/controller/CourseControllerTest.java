package com.fellow.app.controller;

import com.fellow.app.service.CourseService;
import com.fellow.app.model.Course;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.ColorPicker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JavaFXTestSetup.class)
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @Mock
    private TextField txtName;

    @Mock
    private TextField txtDescription;

    @Mock
    private ListView<Course> lvCourses;

    private CourseController courseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        courseController = new CourseController();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field courseServiceField = CourseController.class.getDeclaredField("courseService");
            courseServiceField.setAccessible(true);
            courseServiceField.set(courseController, courseService);

            java.lang.reflect.Field txtNameField = CourseController.class.getDeclaredField("txtName");
            txtNameField.setAccessible(true);
            txtNameField.set(courseController, txtName);

            java.lang.reflect.Field txtDescriptionField = CourseController.class.getDeclaredField("txtDescription");
            txtDescriptionField.setAccessible(true);
            txtDescriptionField.set(courseController, txtDescription);

            java.lang.reflect.Field lvCoursesField = CourseController.class.getDeclaredField("lvCourses");
            lvCoursesField.setAccessible(true);
            lvCoursesField.set(courseController, lvCourses);

            java.lang.reflect.Field colorPickerField = CourseController.class.getDeclaredField("colorPicker");
            colorPickerField.setAccessible(true);
            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setValue(Color.web("#6366f1"));
            colorPickerField.set(courseController, colorPicker);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHandleAddCourse() {
        // Arrange
        when(txtName.getText()).thenReturn("New Course");
        when(txtDescription.getText()).thenReturn("Description");
        when(courseService.addCourse(any(Course.class))).thenReturn(true);

        // Act
        courseController.handleAddCourse();

        // Assert
        verify(courseService).addCourse(any(Course.class));
        verify(txtName).clear();
        verify(txtDescription).clear();
    }

    @Test
    void testHandleAddCourseEmptyName() {
        // Arrange
        when(txtName.getText()).thenReturn("   ");

        // Act
        courseController.handleAddCourse();

        // Assert
        verify(courseService, never()).addCourse(any(Course.class));
    }
}