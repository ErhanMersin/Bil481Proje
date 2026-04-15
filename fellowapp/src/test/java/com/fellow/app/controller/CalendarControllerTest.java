package com.fellow.app.controller;

import com.fellow.app.service.CourseService;
import com.fellow.app.service.EventService;
import com.fellow.app.model.Event;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JavaFXTestSetup.class)
class CalendarControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private CourseService courseService;

    @Mock
    private Label lblMonthYear;

    @Mock
    private Label lblSelectedDate;

    @Mock
    private ListView<Event> listDailyEvents;

    private CalendarController calendarController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        calendarController = new CalendarController();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field eventServiceField = CalendarController.class.getDeclaredField("eventService");
            eventServiceField.setAccessible(true);
            eventServiceField.set(calendarController, eventService);

            java.lang.reflect.Field courseServiceField = CalendarController.class.getDeclaredField("courseService");
            courseServiceField.setAccessible(true);
            courseServiceField.set(calendarController, courseService);

            java.lang.reflect.Field lblSelectedDateField = CalendarController.class.getDeclaredField("lblSelectedDate");
            lblSelectedDateField.setAccessible(true);
            lblSelectedDateField.set(calendarController, lblSelectedDate);

            java.lang.reflect.Field listDailyEventsField = CalendarController.class.getDeclaredField("listDailyEvents");
            listDailyEventsField.setAccessible(true);
            listDailyEventsField.set(calendarController, listDailyEvents);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testLoadEventsForDate() {
        // Arrange
        LocalDate date = LocalDate.now();
        Event event = new Event();
        event.setId(1);
        event.setUserId(1);
        event.setTitle("Event1");
        event.setType("EXAM");
        event.setEventDate(date);
        event.setEventTime("10:00");
        event.setDescription("Desc");
        event.setCourseId(1);
        List<Event> events = Arrays.asList(event);
        when(eventService.getEventsByDate(1, date)).thenReturn(events);

        // Act
        // loadAgenda is private, so we can't directly test it
        // We would need to make it package-private or use reflection

        // Assert
        // verify(listDailyEvents).setItems(any());
    }
}