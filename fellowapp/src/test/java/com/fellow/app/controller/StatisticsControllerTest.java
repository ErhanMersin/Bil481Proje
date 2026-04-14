package com.fellow.app.controller;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.service.StatisticsService;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JavaFXTestSetup.class)
class StatisticsControllerTest {

    @Mock
    private StatisticsService statsService;

    @Mock
    private CourseDAO courseDAO;

    @Mock
    private ComboBox<String> cmbCourse;

    @Mock
    private Label lblResult;

    private StatisticsController statisticsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statisticsController = new StatisticsController();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field statsServiceField = StatisticsController.class.getDeclaredField("statsService");
            statsServiceField.setAccessible(true);
            statsServiceField.set(statisticsController, statsService);

            java.lang.reflect.Field courseDAOField = StatisticsController.class.getDeclaredField("courseDAO");
            courseDAOField.setAccessible(true);
            courseDAOField.set(statisticsController, courseDAO);

            java.lang.reflect.Field cmbCourseField = StatisticsController.class.getDeclaredField("cmbCourse");
            cmbCourseField.setAccessible(true);
            cmbCourseField.set(statisticsController, cmbCourse);

            java.lang.reflect.Field lblResultField = StatisticsController.class.getDeclaredField("lblResult");
            lblResultField.setAccessible(true);
            lblResultField.set(statisticsController, lblResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRefresh() {
        // Arrange
        when(courseDAO.getCoursesByUserId(1)).thenReturn(java.util.Arrays.asList());
        when(statsService.getTotalStudyTime()).thenReturn(0);
        when(statsService.getMostStudiedCourse()).thenReturn(null);
        when(statsService.getAverageSessionDuration()).thenReturn(0);

        // Act
        statisticsController.refresh();

        // Assert
        verify(courseDAO).getCoursesByUserId(1);
    }
}