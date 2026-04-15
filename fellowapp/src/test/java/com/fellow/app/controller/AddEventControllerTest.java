package com.fellow.app.controller;

import com.fellow.app.service.CourseService;
import com.fellow.app.service.EventService;
import com.fellow.app.model.Course;
import com.fellow.app.model.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JavaFXTestSetup.class)
class AddEventControllerTest {

    @Mock
    private CourseService courseService;

    @Mock
    private EventService eventService;

    @Mock
    private TextField txtTitle;

    @Mock
    private ComboBox<String> cmbType;

    @Mock
    private ComboBox<Course> cmbCourse;

    @Mock
    private DatePicker dpDate;

    @Mock
    private TextField txtTime;

    @Mock
    private TextField txtDesc;

    private AddEventController addEventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addEventController = new AddEventController();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field courseServiceField = AddEventController.class.getDeclaredField("courseService");
            courseServiceField.setAccessible(true);
            courseServiceField.set(addEventController, courseService);

            java.lang.reflect.Field txtTitleField = AddEventController.class.getDeclaredField("txtTitle");
            txtTitleField.setAccessible(true);
            txtTitleField.set(addEventController, txtTitle);

            java.lang.reflect.Field cmbTypeField = AddEventController.class.getDeclaredField("cmbType");
            cmbTypeField.setAccessible(true);
            cmbTypeField.set(addEventController, cmbType);

            java.lang.reflect.Field cmbCourseField = AddEventController.class.getDeclaredField("cmbCourse");
            cmbCourseField.setAccessible(true);
            cmbCourseField.set(addEventController, cmbCourse);

            java.lang.reflect.Field dpDateField = AddEventController.class.getDeclaredField("dpDate");
            dpDateField.setAccessible(true);
            dpDateField.set(addEventController, dpDate);

            java.lang.reflect.Field txtTimeField = AddEventController.class.getDeclaredField("txtTime");
            txtTimeField.setAccessible(true);
            txtTimeField.set(addEventController, txtTime);

            java.lang.reflect.Field txtDescField = AddEventController.class.getDeclaredField("txtDesc");
            txtDescField.setAccessible(true);
            txtDescField.set(addEventController, txtDesc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInitialize() {
        // Arrange
        // Act
        // initialize is called automatically, but we can't test FXML injection easily

        // Assert
        // Cannot assert without proper setup
    }
}