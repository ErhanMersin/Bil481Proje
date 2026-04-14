package com.fellow.app.integration;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.dao.DatabaseConnection;
import com.fellow.app.dao.EventDAO;
import com.fellow.app.model.Course;
import com.fellow.app.model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Event management workflow
 * Tests CourseDAO + EventDAO + Database interaction
 */
public class EventManagementIntegrationTest {

    private static final String TEST_DB_FILE = "target/fellow-event-integration-test.db";
    private EventDAO eventDAO;
    private CourseDAO courseDAO;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.initializeDatabase();

        eventDAO = new EventDAO();
        courseDAO = new CourseDAO();
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
    void testAddEventToDefaultCourse() {
        // Get or create default course
        Course defaultCourse = courseDAO.getOrCreateDefaultCourse(1);
        assertNotNull(defaultCourse);
        assertTrue(defaultCourse.getId() > 0);

        // Create event for default course
        Event event = new Event();
        event.setTitle("Midterm Exam");
        event.setType("EXAM");
        event.setCourseId(defaultCourse.getId());
        event.setEventDate(LocalDate.now());
        event.setEventTime("10:00");
        event.setDescription("Midterm exam preparation");
        event.setUserId(1);

        // Add event
        boolean isAdded = eventDAO.addEvent(event);
        assertTrue(isAdded, "Event should be added successfully");
        assertTrue(event.getId() > 0, "Event should have generated ID");

        // Verify event persists
        List<Event> currentEvents = eventDAO.getEventsByDate(1, LocalDate.now());
        assertFalse(currentEvents.isEmpty(), "Should have events for today");
        assertTrue(currentEvents.stream().anyMatch(e -> e.getTitle().equals("Midterm Exam")));
    }

    @Test
    void testAddMultipleCoursesAndEvents() {
        // Create multiple courses
        Course course1 = new Course("Algorithms", "Algo description", "#FF0000", 1);
        Course course2 = new Course("Database", "DB description", "#00FF00", 1);
        
        courseDAO.addCourse(course1);
        courseDAO.addCourse(course2);

        // Add events for each course
        Event event1 = new Event();
        event1.setTitle("Algorithm Quiz");
        event1.setType("QUIZ");
        event1.setCourseId(course1.getId());
        event1.setEventDate(LocalDate.now());
        event1.setEventTime("11:00");
        event1.setUserId(1);

        Event event2 = new Event();
        event2.setTitle("Database Project");
        event2.setType("PROJECT");
        event2.setCourseId(course2.getId());
        event2.setEventDate(LocalDate.now());
        event2.setEventTime("14:00");
        event2.setUserId(1);

        eventDAO.addEvent(event1);
        eventDAO.addEvent(event2);

        // Verify both events exist for today
        List<Event> todayEvents = eventDAO.getEventsByDate(1, LocalDate.now());
        assertEquals(2, todayEvents.size(), "Should have 2 events for today");
        
        assertTrue(todayEvents.stream().anyMatch(e -> e.getCourseId() == course1.getId()));
        assertTrue(todayEvents.stream().anyMatch(e -> e.getCourseId() == course2.getId()));
    }

    @Test
    void testGetEventsByMonth() {
        // Add course
        Course course = courseDAO.getOrCreateDefaultCourse(1);

        // Get baseline event count for current month (includes seed data)
        LocalDate today = LocalDate.now();
        List<Event> monthEventsBefore = eventDAO.getEventsByMonth(1, today.getYear(), today.getMonthValue());
        int baselineCount = monthEventsBefore.size();

        // Add events for different dates in current month
        Event event1 = new Event();
        event1.setTitle("New Event 1");
        event1.setType("OTHER");
        event1.setCourseId(course.getId());
        event1.setEventDate(today);
        event1.setEventTime("10:00");
        event1.setUserId(1);

        Event event2 = new Event();
        event2.setTitle("New Event 2");
        event2.setType("OTHER");
        event2.setCourseId(course.getId());
        event2.setEventDate(today.plusDays(5));
        event2.setEventTime("15:00");
        event2.setUserId(1);

        eventDAO.addEvent(event1);
        eventDAO.addEvent(event2);

        // Get events for current month
        List<Event> monthEvents = eventDAO.getEventsByMonth(1, today.getYear(), today.getMonthValue());

        assertEquals(baselineCount + 2, monthEvents.size(), "Should have 2 more events in current month");
    }

    @Test
    void testDeleteEventAndVerifyRemoval() {
        // Add course and event
        Course course = courseDAO.getOrCreateDefaultCourse(1);
        
        Event event = new Event();
        event.setTitle("To Be Deleted");
        event.setType("OTHER");
        event.setCourseId(course.getId());
        event.setEventDate(LocalDate.now());
        event.setEventTime("12:00");
        event.setUserId(1);

        eventDAO.addEvent(event);
        int eventId = event.getId();

        // Verify event exists
        List<Event> before = eventDAO.getEventsByDate(1, LocalDate.now());
        assertTrue(before.stream().anyMatch(e -> e.getId() == eventId));

        // Delete event
        boolean isDeleted = eventDAO.deleteEvent(eventId);
        assertTrue(isDeleted, "Event should be deleted");

        // Verify deletion
        List<Event> after = eventDAO.getEventsByDate(1, LocalDate.now());
        assertFalse(after.stream().anyMatch(e -> e.getId() == eventId), 
                   "Deleted event should not exist");
    }

    @Test
    void testGetUpcomingEventsOrderedByDate() {
        Course course = courseDAO.getOrCreateDefaultCourse(1);

        // Add events with future dates
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate nextWeek = LocalDate.now().plusDays(7);

        Event event1 = new Event();
        event1.setTitle("Next Week Event");
        event1.setType("OTHER");
        event1.setCourseId(course.getId());
        event1.setEventDate(nextWeek);
        event1.setEventTime("10:00");
        event1.setUserId(1);

        Event event2 = new Event();
        event2.setTitle("Tomorrow Event");
        event2.setType("OTHER");
        event2.setCourseId(course.getId());
        event2.setEventDate(tomorrow);
        event2.setEventTime("14:00");
        event2.setUserId(1);

        eventDAO.addEvent(event1);
        eventDAO.addEvent(event2);

        // Get upcoming events
        List<Event> upcoming = eventDAO.getUpcomingEvents(1, "event_date ASC");

        assertFalse(upcoming.isEmpty(), "Should have upcoming events");
        // First event should be tomorrow (closer date)
        LocalDate firstEventDate = upcoming.get(0).getEventDate();
        assertFalse(firstEventDate.isBefore(LocalDate.now()), "Upcoming events should not be in past");
    }

    @Test
    void testCompleteEventWorkflowMultipleCourses() {
        // 1. Create courses
        Course course1 = new Course("Math", "Mathematics", "#FF0000", 1);
        Course course2 = new Course("Physics", "Physics", "#00FF00", 1);
        courseDAO.addCourse(course1);
        courseDAO.addCourse(course2);

        // 2. Add multiple events
        for (int i = 0; i < 3; i++) {
            Event event = new Event();
            event.setTitle("Math Event " + i);
            event.setType("HOMEWORK");
            event.setCourseId(course1.getId());
            event.setEventDate(LocalDate.now().plusDays(i));
            event.setEventTime("09:00");
            event.setUserId(1);
            eventDAO.addEvent(event);
        }

        for (int i = 0; i < 2; i++) {
            Event event = new Event();
            event.setTitle("Physics Event " + i);
            event.setType("PROJECT");
            event.setCourseId(course2.getId());
            event.setEventDate(LocalDate.now().plusDays(i));
            event.setEventTime("11:00");
            event.setUserId(1);
            eventDAO.addEvent(event);
        }

        // 3. Get all upcoming events (baseline includes seed data)
        List<Event> upcoming = eventDAO.getUpcomingEvents(1, "event_date ASC");
        assertTrue(upcoming.size() >= 5, "Should have at least 5 upcoming events");

        // 4. Verify course association - count new events we just added
        List<Event> newMathEvents = upcoming.stream()
                .filter(e -> e.getCourseId() == course1.getId() && e.getTitle().startsWith("Math Event"))
                .toList();
        List<Event> newPhysicsEvents = upcoming.stream()
                .filter(e -> e.getCourseId() == course2.getId() && e.getTitle().startsWith("Physics Event"))
                .toList();
        assertEquals(3, newMathEvents.size(), "Should have 3 new Math events");
        assertEquals(2, newPhysicsEvents.size(), "Should have 2 new Physics events");

        // 5. Delete one of our new events and verify
        if (!newMathEvents.isEmpty()) {
            int countBefore = upcoming.size();
            eventDAO.deleteEvent(newMathEvents.get(0).getId());
            List<Event> after = eventDAO.getUpcomingEvents(1, "event_date ASC");
            assertEquals(countBefore - 1, after.size(), "Should have 1 fewer event after deletion");
        }
    }
}
