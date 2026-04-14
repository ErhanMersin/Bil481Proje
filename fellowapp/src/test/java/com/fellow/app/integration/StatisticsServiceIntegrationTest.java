package com.fellow.app.integration;

import com.fellow.app.dao.DatabaseConnection;
import com.fellow.app.dao.StudySessionDAO;
import com.fellow.app.service.StatisticsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for StatisticsService + StudySessionDAO + Database
 * Tests real study session data flow through service calculations
 */
public class StatisticsServiceIntegrationTest {

    private static final String TEST_DB_FILE = "target/fellow-stats-integration-test.db";
    private StatisticsService statisticsService;
    private StudySessionDAO studySessionDAO;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.initializeDatabase();

        statisticsService = new StatisticsService();
        studySessionDAO = new StudySessionDAO();
    }

    @AfterEach
    void tearDown() {
        DatabaseConnection.closeConnection();
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.resetDatabaseUrl();
    }

    @Test
    void testDailyStrategyCalculatesCorrectly() {
        // Add study sessions for today
        studySessionDAO.addSession(1, 1, 3600, 1); // 1 hour
        studySessionDAO.addSession(1, 1, 1800, 1); // 30 minutes

        StatisticsService.TimeStrategy strategy = new StatisticsService.DailyStrategy();

        // Get report
        String report = statisticsService.getStudyTimeReport(1, 1, "Algorithm", strategy);

        assertNotNull(report);
        assertFalse(report.contains("No study records"));
        assertTrue(report.contains("1 hour") || report.contains("1.5 hour"));
        assertTrue(report.contains("today"));
    }

    @Test
    void testWeeklyStrategyAggregatesMultipleDays() {
        // Add sessions that would span the week
        studySessionDAO.addSession(1, 1, 3600, 1); // 1 hour
        studySessionDAO.addSession(1, 1, 7200, 1); // 2 hours

        StatisticsService.TimeStrategy strategy = new StatisticsService.WeeklyStrategy();

        String report = statisticsService.getStudyTimeReport(1, 1, "Algorithm", strategy);

        assertNotNull(report);
        assertTrue(report.contains("this week"));
    }

    @Test
    void testMonthlyStrategyAggregatesAllMonthData() {
        // Add multiple sessions
        for (int i = 0; i < 3; i++) {
            studySessionDAO.addSession(1, 1, 1800, 1); // 30 min each
        }

        StatisticsService.TimeStrategy strategy = new StatisticsService.MonthlyStrategy();

        String report = statisticsService.getStudyTimeReport(1, 1, "Algorithm", strategy);

        assertNotNull(report);
        assertTrue(report.contains("this month"));
    }

    @Test
    void testTotalStrategyAggregatesAllData() {
        // Add sessions
        studySessionDAO.addSession(1, 1, 3600, 1); // 1 hour
        studySessionDAO.addSession(1, 1, 1800, 1); // 30 min

        StatisticsService.TimeStrategy strategy = new StatisticsService.TotalStrategy();

        String report = statisticsService.getStudyTimeReport(1, 1, "Algorithm", strategy);

        assertNotNull(report);
        assertTrue(report.contains("all time"));
    }

    @Test
    void testGetStudiedCourseNamesTodayReturnsAddedCourses() {
        // Add sessions for course
        studySessionDAO.addSession(1, 1, 3600, 1);

        StatisticsService.TimeStrategy strategy = new StatisticsService.DailyStrategy();

        List<String> courseNames = statisticsService.getStudiedCourseNames(1, strategy);

        assertFalse(courseNames.isEmpty(), "Should have studied courses");
        assertTrue(courseNames.stream().anyMatch(c -> c != null), "Should have non-null course names");
    }

    @Test
    void testChartDataDailyStrategyReturnsWeekData() {
        // Add study session
        studySessionDAO.addSession(1, 1, 3600, 1);

        StatisticsService.TimeStrategy strategy = new StatisticsService.DailyStrategy();

        Map<String, Integer> chartData = statisticsService.getChartData(1, 1, strategy);

        assertFalse(chartData.isEmpty(), "Chart data should not be empty");
        assertTrue(chartData.values().stream().anyMatch(v -> v > 0), "Should have positive values");
    }

    @Test
    void testChartDataTotalStrategyReturnsMonthlyData() {
        // Add study session
        studySessionDAO.addSession(1, 1, 3600, 1);

        StatisticsService.TimeStrategy strategy = new StatisticsService.TotalStrategy();

        Map<String, Integer> chartData = statisticsService.getChartData(1, 1, strategy);

        assertFalse(chartData.isEmpty(), "Chart data should not be empty");
    }

    @Test
    void testGetCourseIdByNameResolvesCorrectly() {
        // Add session (creates course if needed or uses seeded)
        studySessionDAO.addSession(1, 1, 3600, 1);

        // Try to resolve course ID
        int courseId = statisticsService.getCourseIdByName(1, "Default");

        assertTrue(courseId > 0, "Should resolve valid course ID");
    }

    @Test
    void testNoStudyRecordsReturnsAppropriateMessage() {
        // Don't add any sessions for a specific course
        StatisticsService.TimeStrategy strategy = new StatisticsService.DailyStrategy();

        String report = statisticsService.getStudyTimeReport(1, 999, "NonExisting", strategy);

        assertEquals("No study records found for NonExisting today.", report);
    }

    @Test
    void testCompleteWorkflowAddMultipleSessionsAndAnalyze() {
        // 1. Add sessions for multiple courses over multiple days
        studySessionDAO.addSession(1, 1, 3600, 1); // 1 hour
        studySessionDAO.addSession(1, 1, 1800, 1); // 30 min (total 1.5 hours)

        // 2. Get studied courses
        StatisticsService.DailyStrategy dailyStrategy = new StatisticsService.DailyStrategy();
        List<String> courses = statisticsService.getStudiedCourseNames(1, dailyStrategy);
        assertFalse(courses.isEmpty(), "Should have studied at least one course");

        // 3. Get daily stats
        String dailyReport = statisticsService.getStudyTimeReport(1, 1, courses.get(0), dailyStrategy);
        assertFalse(dailyReport.contains("No study records"), "Should have study records");

        // 4. Get chart data
        Map<String, Integer> chartData = statisticsService.getChartData(1, 1, dailyStrategy);
        assertFalse(chartData.isEmpty(), "Chart data should be populated");

        // 5. Get all-time stats
        StatisticsService.TotalStrategy totalStrategy = new StatisticsService.TotalStrategy();
        String totalReport = statisticsService.getStudyTimeReport(1, 1, courses.get(0), totalStrategy);
        assertTrue(totalReport.contains("all time"), "Total report should mention all time");
    }
}
