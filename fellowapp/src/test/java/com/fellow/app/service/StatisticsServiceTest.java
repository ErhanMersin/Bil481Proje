package com.fellow.app.service;

import com.fellow.app.dao.StudySessionDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatisticsServiceTest {

    private StatisticsService statisticsService;

    @Mock
    private StudySessionDAO sessionDAOMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        statisticsService = new StatisticsService();
        injectMockDao(statisticsService, sessionDAOMock);
    }

    private void injectMockDao(StatisticsService service, StudySessionDAO mockDao) throws Exception {
        Field field = StatisticsService.class.getDeclaredField("sessionDAO");
        field.setAccessible(true);
        field.set(service, mockDao);
    }

    @Test
    void testGetStudiedCourseNamesUsesStrategyDateRange() {
        StatisticsService.TimeStrategy strategy = new StatisticsService.DailyStrategy();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expectedDate = today.format(formatter);

        when(sessionDAOMock.getCourseNamesStudiedInRange(1, expectedDate, expectedDate))
                .thenReturn(List.of("Default", "Algorithms"));

        List<String> courseNames = statisticsService.getStudiedCourseNames(1, strategy);

        assertEquals(2, courseNames.size());
        assertTrue(courseNames.contains("Default"));
        assertTrue(courseNames.contains("Algorithms"));
        verify(sessionDAOMock).getCourseNamesStudiedInRange(1, expectedDate, expectedDate);
    }

    @Test
    void testGetCourseIdByNameDelegatesToDao() {
        when(sessionDAOMock.getCourseIdByName(1, "Default")).thenReturn(5);

        int courseId = statisticsService.getCourseIdByName(1, "Default");

        assertEquals(5, courseId);
        verify(sessionDAOMock).getCourseIdByName(1, "Default");
    }

    @Test
    void testGetStudyTimeReportReturnsNoRecordsMessage() {
        StatisticsService.TimeStrategy strategy = new StatisticsService.DailyStrategy();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expectedDate = today.format(formatter);

        when(sessionDAOMock.getTotalStudyTime(1, expectedDate, expectedDate)).thenReturn(0);

        String report = statisticsService.getStudyTimeReport(1, -1, "Default", strategy);

        assertEquals("No study records found for Default today.", report);
    }

    @Test
    void testGetStudyTimeReportFormatsHoursAndMinutes() {
        StatisticsService.TimeStrategy strategy = new StatisticsService.DailyStrategy();
        when(sessionDAOMock.getTotalStudyTime(1, 2, strategy.startDate(DateTimeFormatter.ofPattern("yyyy-MM-dd")), strategy.endDate(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .thenReturn(3660);

        String report = statisticsService.getStudyTimeReport(1, 2, "Algorithms", strategy);

        assertEquals("You studied 1 hour 1 minute for Algorithms today.", report);
    }

    @Test
    void testGetChartDataDailyStrategyWithCourseId() {
        StatisticsService.TimeStrategy strategy = new StatisticsService.DailyStrategy();
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String start = monday.format(formatter);
        String end = sunday.format(formatter);

        when(sessionDAOMock.getDailyChartData(1, 2, start, end)).thenReturn(Map.of(start, 120));

        Map<String, Integer> chartData = statisticsService.getChartData(1, 2, strategy);

        assertEquals(1, chartData.size());
        assertEquals(120, chartData.get(start));
        verify(sessionDAOMock).getDailyChartData(1, 2, start, end);
    }

    @Test
    void testGetChartDataTotalStrategyWithoutCourseId() {
        StatisticsService.TimeStrategy strategy = new StatisticsService.TotalStrategy();
        when(sessionDAOMock.getMonthlyChartData(1)).thenReturn(Map.of("Jan 2000", 100));

        Map<String, Integer> chartData = statisticsService.getChartData(1, -1, strategy);

        assertEquals(1, chartData.size());
        assertEquals(100, chartData.get("Jan 2000"));
        verify(sessionDAOMock).getMonthlyChartData(1);
    }
}
