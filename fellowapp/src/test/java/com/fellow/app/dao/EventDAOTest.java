package com.fellow.app.dao;

import com.fellow.app.model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventDAOTest {

    private static final String TEST_DB_FILE = "target/fellow-test.db";
    private EventDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.initializeDatabase();
        dao = new EventDAO();
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
    void testAddEventAndGetUpcomingEvents() {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setType("OTHER");
        event.setCourseId(1);
        event.setEventDate(LocalDate.now());
        event.setEventTime("10:00");
        event.setDescription("Unit test event");
        event.setUserId(1);

        assertTrue(dao.addEvent(event), "Etkinlik başarıyla eklenmeli");
        assertTrue(event.getId() > 0, "Eklenen etkinliğin ID'si atanmalı");

        List<Event> events = dao.getUpcomingEvents(1, "event_date ASC");
        assertFalse(events.isEmpty(), "Gelecek etkinlik listesi boş olmamalı");
        assertTrue(events.stream().anyMatch(e -> e.getId() == event.getId()), "Eklenen etkinlik listede olmalı");
    }

    @Test
    void testGetEventsByDate() {
        Event event = new Event();
        event.setTitle("Date Event");
        event.setType("OTHER");
        event.setCourseId(1);
        LocalDate today = LocalDate.now();
        event.setEventDate(today);
        event.setEventTime("11:00");
        event.setDescription("Date query event");
        event.setUserId(1);

        assertTrue(dao.addEvent(event));
        List<Event> events = dao.getEventsByDate(1, today);

        assertEquals(1, events.size(), "Belirtilen tarih için bir etkinlik bulunmalı");
        assertEquals("Date Event", events.get(0).getTitle());
    }

    @Test
    void testGetEventsByMonth() {
        Event event = new Event();
        event.setTitle("Month Event");
        event.setType("OTHER");
        event.setCourseId(1);
        LocalDate today = LocalDate.now();
        event.setEventDate(today);
        event.setEventTime("12:00");
        event.setDescription("Month query event");
        event.setUserId(1);

        assertTrue(dao.addEvent(event));

        List<Event> monthEvents = dao.getEventsByMonth(1, today.getYear(), today.getMonthValue());
        assertFalse(monthEvents.isEmpty(), "Aynı ay için en az bir etkinlik bulunmalı");
        assertTrue(monthEvents.stream().anyMatch(e -> e.getId() == event.getId()));
    }

    @Test
    void testDeleteEvent() {
        Event event = new Event();
        event.setTitle("Delete Event");
        event.setType("OTHER");
        event.setCourseId(1);
        event.setEventDate(LocalDate.now());
        event.setEventTime("13:00");
        event.setDescription("Delete query event");
        event.setUserId(1);

        assertTrue(dao.addEvent(event));
        assertTrue(dao.deleteEvent(event.getId()), "Eklenen etkinlik silinebilmeli");

        List<Event> events = dao.getEventsByDate(1, LocalDate.now());
        assertFalse(events.stream().anyMatch(e -> e.getId() == event.getId()), "Silinen etkinlik artık bulunmamalı");
    }
}
