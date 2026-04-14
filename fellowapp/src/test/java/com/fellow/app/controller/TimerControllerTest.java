package com.fellow.app.controller;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.model.Course;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import javafx.scene.control.ComboBox;
import javafx.application.Platform;

@ExtendWith(JavaFXTestSetup.class)
class TimerControllerTest {

    @Mock
    private CourseDAO courseDAO;

    private TimerController timerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        timerController = new TimerController();
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field courseDAOField = TimerController.class.getDeclaredField("courseDAO");
            courseDAOField.setAccessible(true);
            courseDAOField.set(timerController, courseDAO);

            java.lang.reflect.Field cmbCourseField = TimerController.class.getDeclaredField("cmbCourse");
            cmbCourseField.setAccessible(true);
            cmbCourseField.set(timerController, new ComboBox<Course>());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRefreshLoadsCourses() {
        // Arrange
        Course course1 = new Course(1, "Math", "#ff0000", 1);
        Course course2 = new Course(2, "Physics", "#00ff00", 1);
        List<Course> courses = Arrays.asList(course1, course2);
        when(courseDAO.getCoursesByUserId(1)).thenReturn(courses);
        when(courseDAO.getOrCreateDefaultCourse(1)).thenReturn(course1);

        // Act
        timerController.refresh();

        // Assert
        verify(courseDAO).getCoursesByUserId(1);
        // Note: Since cmbCourse is FXML injected, we can't easily test UI updates
        // without TestFX
    }

    // @Test
    // void testSaveSessionToDatabase() {
    // // Arrange
    // Course course = new Course(1, "Math", "#ff0000", 1);
    // when(courseDAO.getOrCreateDefaultCourse(1)).thenReturn(course);

    // // Act
    // // This method is private, so we can't directly test it
    // // We would need to make it package-private or use reflection

    // // Assert
    // // verify(studySessionDAO).addSession(eq(1), eq(1), anyInt(), anyInt());
    // }
}