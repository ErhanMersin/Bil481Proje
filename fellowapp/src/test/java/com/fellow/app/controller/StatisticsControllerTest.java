package com.fellow.app.controller;

import com.fellow.app.service.CourseService;
import com.fellow.app.service.StatisticsService;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
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
    private CourseService courseService;

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

            java.lang.reflect.Field courseServiceField = StatisticsController.class.getDeclaredField("courseService");
            courseServiceField.setAccessible(true);
            courseServiceField.set(statisticsController, courseService);

            java.lang.reflect.Field cmbCourseField = StatisticsController.class.getDeclaredField("cmbCourse");
            cmbCourseField.setAccessible(true);
            cmbCourseField.set(statisticsController, new ComboBox<String>());

            java.lang.reflect.Field lblResultField = StatisticsController.class.getDeclaredField("lblResult");
            lblResultField.setAccessible(true);
            lblResultField.set(statisticsController, lblResult);

            java.lang.reflect.Field chartStudyTimeField = StatisticsController.class.getDeclaredField("chartStudyTime");
            chartStudyTimeField.setAccessible(true);
            chartStudyTimeField.set(statisticsController, new LineChart<>(new CategoryAxis(), new NumberAxis()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRefresh() {
        // Arrange
        when(courseService.getCoursesByUserId(1)).thenReturn(java.util.Arrays.asList());
        // Set currentStrategy to avoid early return
        try {
            java.lang.reflect.Field currentStrategyField = StatisticsController.class
                    .getDeclaredField("currentStrategy");
            currentStrategyField.setAccessible(true);
            currentStrategyField.set(statisticsController,
                    new com.fellow.app.service.StatisticsService.DailyStrategy());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Act
        statisticsController.refresh();

        // Assert
        verify(courseService).getCoursesByUserId(1);
    }
}