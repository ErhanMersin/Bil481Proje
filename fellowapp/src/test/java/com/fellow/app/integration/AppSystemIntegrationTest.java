package com.fellow.app.integration;

import com.fellow.app.dao.*;
import com.fellow.app.model.*;
import com.fellow.app.service.StatisticsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End System Integration Test
 * Tests the workflow spanning across all major components: Courses, Events, Todos, Sessions, and Statistics.
 */
public class AppSystemIntegrationTest {

    private static final String TEST_DB_FILE = "target/system-e2e-test.db";
    
    private CourseDAO courseDAO;
    private EventDAO eventDAO;
    private TodoItemDAO todoDAO;
    private StudySessionDAO studySessionDAO;
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.initializeDatabase();

        courseDAO = new CourseDAO();
        eventDAO = new EventDAO();
        todoDAO = new TodoItemDAO();
        studySessionDAO = new StudySessionDAO();
        statisticsService = new StatisticsService();
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
    void testCompleteUserJourneyAcrossAllComponents() {
        int userId = 1;

        // --- 1. Course Management ---
        // Create a new course
        Course course = new Course("System Test Course", "#FFFFFF", userId);
        boolean courseAdded = courseDAO.addCourse(course);
        assertTrue(courseAdded, "Course should be added successfully");
        int courseId = course.getId();
        assertTrue(courseId > 0, "Course ID should be generated");

        // Verify course exists
        List<Course> courses = courseDAO.getCoursesByUserId(userId);
        assertTrue(courses.stream().anyMatch(c -> c.getId() == courseId), "Course should be found in DB");

        // --- 2. Event Management ---
        // User creates an event related to this course
        Event event = new Event();
        event.setUserId(userId);
        event.setTitle("Midterm Exam");
        event.setEventDate(LocalDate.now().plusDays(5));
        event.setEventTime("14:00");
        event.setDescription("Important exam");
        event.setCourseId(courseId);
        event.setType("EXAM");
        boolean eventAdded = eventDAO.addEvent(event);
        assertTrue(eventAdded, "Event should be added successfully");
        
        List<Event> userEvents = eventDAO.getUpcomingEvents(userId, "event_date ASC");
        assertTrue(userEvents.stream().anyMatch(e -> e.getTitle().equals("Midterm Exam")), "Event should be stored");

        // --- 3. Todo Management ---
        // User creates a Todo item to study for the midterm
        TodoItem todo = new TodoItem(courseId, userId, "Study Chapter 1-3", "Read and practice");
        boolean todoAdded = todoDAO.addTodo(todo);
        assertTrue(todoAdded, "Todo should be added");
        int todoId = todo.getId();

        // Check active todos
        int activeTodos = todoDAO.getActiveTodoCount(userId);
        assertEquals(1, activeTodos, "There should be 1 active todo");

        // User completes the Todo
        boolean todoCompleted = todoDAO.updateCompleted(todoId, true);
        assertTrue(todoCompleted, "Todo completion status updated");
        assertEquals(0, todoDAO.getActiveTodoCount(userId), "There should be no active todos now");

        // --- 4. Study Session Management ---
        // User starts and finishes a Pomodoro study session for this course
        // Simulate 2 sessions: one 50 min, one 40 min
        boolean session1Added = studySessionDAO.addSession(courseId, userId, 3000, 1); // 50 mins
        boolean session2Added = studySessionDAO.addSession(courseId, userId, 2400, 1); // 40 mins
        assertTrue(session1Added && session2Added, "Study sessions should be recorded");

        // --- 5. Statistics & Reporting ---
        // User checks their daily statistics
        StatisticsService.DailyStrategy dailyStrategy = new StatisticsService.DailyStrategy();
        
        // Ensure that course is recorded in daily stats
        List<String> studiedCourses = statisticsService.getStudiedCourseNames(userId, dailyStrategy);
        assertTrue(studiedCourses.contains("System Test Course"), "Studied course should be listed in stats");

        // Verify calculated report for today
        String statReport = statisticsService.getStudyTimeReport(userId, courseId, "System Test Course", dailyStrategy);
        assertNotNull(statReport);
        assertTrue(statReport.contains("1 hour 30 minute"), 
                   "Report should reflect 1 hour 30 minutes (5400 seconds) total study time");

        // --- 6. Cleanup & Cascade Verification ---
        // User deletes the course, expecting cleanup (if foreign keys cascade, or handled manually)
        boolean courseDeleted = courseDAO.deleteCourse(courseId);
        assertTrue(courseDeleted, "Course should be deleted");
        
        // Double check course doesn't exist anymore
        List<Course> remainingCourses = courseDAO.getCoursesByUserId(userId);
        assertFalse(remainingCourses.stream().anyMatch(c -> c.getId() == courseId), "Course should be gone");
        
        // Even if components are deleted, let's make sure the backend holds up and doesn't crash on subsequent queries
        List<TodoItem> remainingTodos = todoDAO.getTodosByUserAndCourse(userId, courseId);
        // SQLite doesn't have FK constraints ON DELETE CASCADE by default unless enabled, 
        // Assuming app logic might still keep orphan records or delete them. This test checks stability.
        assertNotNull(remainingTodos); 
    }
}
