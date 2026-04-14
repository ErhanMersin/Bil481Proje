package com.fellow.app.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StudySessionDAOTest {

    private static final String TEST_DB_FILE = "target/fellow-test.db";
    private StudySessionDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        // Set the database to test database
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        
        // Remove old test db file if exists
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        
        // Initialize tables and seed dummy user/course (userId=1, courseId=1)
        DatabaseConnection.initializeDatabase();
        
        // Initialize the DAO
        dao = new StudySessionDAO();
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
    void testAddSessionAndGetUserStats() {
        // Assert initial stats
        int[] initialStats = dao.getUserStats(1);
        assertEquals(0, initialStats[0], "Initial duration should be 0");
        assertEquals(0, initialStats[1], "Initial pomodoro count should be 0");

        // Add a 25.5 minute (1530 seconds) session with 1 pomodoro
        boolean isAdded = dao.addSession(1, 1, 1530, 1);
        assertTrue(isAdded, "Session should be successfully added");

        // Fetch stats again
        int[] updatedStats = dao.getUserStats(1);
        assertEquals(1530, updatedStats[0], "Duration should be updated to 1530");
        assertEquals(1, updatedStats[1], "Pomodoro count should be 1");
    }

    @Test
    void testGetTotalStudyTime() {
        // Calculate dynamic dates for the query
        String today = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();

        // Initial total study time should be 0
        int initialTotal = dao.getTotalStudyTime(1, 1, yesterday, tomorrow);
        assertEquals(0, initialTotal, "Initial total time should be 0");

        // Let's add multiple sessions
        dao.addSession(1, 1, 1000, 1); // today
        dao.addSession(1, 1, 500, 0); // today

        // Calculate total for today
        int updatedTotal = dao.getTotalStudyTime(1, 1, yesterday, tomorrow);
        assertEquals(1500, updatedTotal, "Total time should equal the sum of durations in the date range");
        
        // If query is totally out of range, should return 0 
        int outOfRangeTotal = dao.getTotalStudyTime(1, 1, "2000-01-01", "2000-01-02");
        assertEquals(0, outOfRangeTotal, "Out of range date should yield 0 duration");
    }

    @Test
    void testGetDailyChartDataFillsEmptyDays() {
        // Add a session today
        dao.addSession(1, 1, 2000, 1);

        // We want a range from 3 days ago to 3 days in the future (total 7 days)
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now().plusDays(3);

        Map<String, Integer> chartData = dao.getDailyChartData(1, start.toString(), end.toString());
        
        // Ensure size is exactly 7 days
        assertEquals(7, chartData.size(), "Chart data should span exactly 7 days");

        // Validate that today has data (2000), and other days map to 0
        LocalDate current = start;
        int sumOfValues = 0;
        
        for (Map.Entry<String, Integer> entry : chartData.entrySet()) {
            sumOfValues += entry.getValue();
            
            // Reconstruct the expected label
            String expectedDayLabel = current.format(DateTimeFormatter.ofPattern("EEE", java.util.Locale.ENGLISH)) + " "
                    + current.format(DateTimeFormatter.ofPattern("MM/dd"));
            
            assertEquals(expectedDayLabel, entry.getKey(), "Label formatting should match strictly");
            
            if (current.isEqual(LocalDate.now())) {
                assertEquals(2000, entry.getValue(), "Today's value should be 2000");
            } else {
                assertEquals(0, entry.getValue(), "Empty days should be filled with 0");
            }
            current = current.plusDays(1);
        }
        
        // Final sanity check
        assertEquals(2000, sumOfValues, "The aggregate across the map should be exactly the 2000 we put in");
    }

    @Test
    void testGetCourseNamesStudiedInRange() {
        // Add sessions for different courses - but since seed only has courseId=1, add multiple sessions
        dao.addSession(1, 1, 1000, 1); // courseId=1
        dao.addSession(1, 1, 1500, 1); // courseId=1 again

        // Get course names studied in a wide date range
        String today = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();

        List<String> courseNames = dao.getCourseNamesStudiedInRange(1, yesterday, tomorrow);

        // Should contain the default course
        assertTrue(courseNames.size() >= 1, "Should have at least 1 course studied");
        assertTrue(courseNames.contains("Default"), "Should contain Default course");
    }

    @Test
    void testGetCourseIdByName() {
        // Test getting course ID by name - use "Default" which is seeded
        int courseId = dao.getCourseIdByName(1, "Default");
        assertTrue(courseId > 0, "Should return a valid course ID for existing course");

        // Test non-existing course
        int invalidCourseId = dao.getCourseIdByName(1, "Non-existing Course");
        assertEquals(-1, invalidCourseId, "Should return -1 for non-existing course");
    }

    @Test
    void testGetMonthlyChartData() {
        // Add a session this month
        dao.addSession(1, 1, 3000, 2);

        // Get monthly chart data for user
        Map<String, Integer> monthlyData = dao.getMonthlyChartData(1);

        // Should have at least current month
        assertFalse(monthlyData.isEmpty(), "Monthly data should not be empty");

        // Get monthly chart data for specific course
        Map<String, Integer> courseMonthlyData = dao.getMonthlyChartData(1, 1);
        assertFalse(courseMonthlyData.isEmpty(), "Course-specific monthly data should not be empty");
    }
}
