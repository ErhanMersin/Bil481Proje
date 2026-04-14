package com.fellow.app.integration;

import com.fellow.app.dao.DatabaseConnection;
import com.fellow.app.dao.StudySessionDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for StudySessionDAO + Timer/Study Session workflow
 * Tests complete study session tracking and statistics
 */
public class StudySessionIntegrationTest {

    private static final String TEST_DB_FILE = "target/fellow-session-integration-test.db";
    private StudySessionDAO studySessionDAO;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.initializeDatabase();

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
    void testAddStudySessionAndRetrieveStats() {
        // Add study session (25 min = 1500 sec)
        boolean added = studySessionDAO.addSession(1, 1, 1500, 1);
        assertTrue(added, "Study session should be added");

        // Get stats
        int[] stats = studySessionDAO.getUserStats(1);
        assertEquals(1500, stats[0], "Duration should be 1500");
        assertEquals(1, stats[1], "Pomodoro count should be 1");
    }

    @Test
    void testMultipleSessionsAccumulate() {
        // Add multiple sessions
        studySessionDAO.addSession(1, 1, 1500, 1); // 25 min
        studySessionDAO.addSession(1, 1, 3000, 2); // 50 min
        studySessionDAO.addSession(1, 1, 1800, 1); // 30 min

        // Verify accumulation
        int[] stats = studySessionDAO.getUserStats(1);
        assertEquals(6300, stats[0], "Total duration should be 6300");
        assertEquals(4, stats[1], "Total pomodoro count should be 4");
    }

    @Test
    void testTodayStudyTimeCalculation() {
        // Add sessions today
        studySessionDAO.addSession(1, 1, 1800, 1); // 30 min
        studySessionDAO.addSession(1, 1, 1800, 1); // 30 min

        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        int totalTime = studySessionDAO.getTotalStudyTime(1, 1, todayStr, todayStr);
        assertEquals(3600, totalTime, "Today's total should be 3600 (1 hour)");
    }

    @Test
    void testWeeklyStudyTimeAggregation() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        // Add sessions spanning the week
        for (int i = 0; i < 7; i++) {
            studySessionDAO.addSession(1, 1, 1800, 1); // 30 min each day
        }

        int weekTotal = studySessionDAO.getTotalStudyTime(1, 1, weekStart.toString(), weekEnd.toString());
        assertTrue(weekTotal >= 10800, "Week total should be at least 3 hours");
    }

    @Test
    void testDailyChartDataReturnsWeekView() {
        // Add sessions
        studySessionDAO.addSession(1, 1, 1800, 1);
        studySessionDAO.addSession(1, 1, 1200, 1);

        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now().plusDays(3);

        Map<String, Integer> chartData = studySessionDAO.getDailyChartData(1, start.toString(), end.toString());

        assertFalse(chartData.isEmpty(), "Chart data should not be empty");
        assertTrue(chartData.values().stream().anyMatch(v -> v > 0), "Should have at least one non-zero value");
    }

    @Test
    void testMonthlyChartDataAggregation() {
        // Add sessions
        for (int i = 0; i < 3; i++) {
            studySessionDAO.addSession(1, 1, 3600, 2); // 1 hour each
        }

        Map<String, Integer> monthlyData = studySessionDAO.getMonthlyChartData(1);

        assertFalse(monthlyData.isEmpty(), "Monthly data should not be empty");
        int totalSeconds = monthlyData.values().stream().mapToInt(Integer::intValue).sum();
        assertTrue(totalSeconds >= 1800, "Total seconds should be reasonable");
    }

    @Test
    void testGetCourseNamesStudied() {
        // Add sessions for course
        studySessionDAO.addSession(1, 1, 1800, 1);

        LocalDate today = LocalDate.now();
        List<String> courseNames = studySessionDAO.getCourseNamesStudiedInRange(1, 
                today.toString(), today.toString());

        assertFalse(courseNames.isEmpty(), "Should have studied at least one course");
    }

    @Test
    void testCourseIdResolution() {
        int courseId = studySessionDAO.getCourseIdByName(1, "Default");
        assertTrue(courseId > 0, "Should resolve Default course ID");

        int invalidId = studySessionDAO.getCourseIdByName(1, "NonExistent");
        assertEquals(-1, invalidId, "Non-existent course should return -1");
    }

    @Test
    void testCompleteStudySessionWorkflow() {
        // 1. Add multiple study sessions over multiple days
        LocalDate today = LocalDate.now();
        
        // Today: 2 hours
        studySessionDAO.addSession(1, 1, 3600, 2);
        studySessionDAO.addSession(1, 1, 3600, 2);

        // 2. Get today's stats
        int[] todayStats = studySessionDAO.getUserStats(1);
        assertEquals(7200, todayStats[0], "Today should have 2 hours");
        assertEquals(4, todayStats[1], "Today should have 4 pomodoros");

        // 3. Get daily chart for week
        LocalDate weekStart = today.minusDays(3);
        LocalDate weekEnd = today.plusDays(3);
        Map<String, Integer> weekChart = studySessionDAO.getDailyChartData(1, 
                weekStart.toString(), weekEnd.toString());
        
        assertFalse(weekChart.isEmpty(), "Week chart should have data");

        // 4. Get monthly overview
        Map<String, Integer> monthChart = studySessionDAO.getMonthlyChartData(1);
        assertFalse(monthChart.isEmpty(), "Month chart should have data");

        // 5. Get course names
        List<String> courses = studySessionDAO.getCourseNamesStudiedInRange(1,
                weekStart.toString(), weekEnd.toString());
        assertTrue(courses.size() > 0, "Should have at least one course");
    }
}
