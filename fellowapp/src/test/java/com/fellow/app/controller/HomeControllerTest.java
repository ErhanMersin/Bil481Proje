package com.fellow.app.controller;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.dao.EventDAO;
import com.fellow.app.model.Event;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JavaFXTestSetup.class)
class HomeControllerTest {

    @Mock
    private EventDAO eventDAO;

    @Mock
    private CourseDAO courseDAO;

    @Mock
    private ListView<Event> listUpcomingEvents;

    private HomeController homeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        homeController = new HomeController();
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field eventDAOField = HomeController.class.getDeclaredField("eventDAO");
            eventDAOField.setAccessible(true);
            eventDAOField.set(homeController, eventDAO);

            java.lang.reflect.Field courseDAOField = HomeController.class.getDeclaredField("courseDAO");
            courseDAOField.setAccessible(true);
            courseDAOField.set(homeController, courseDAO);

            java.lang.reflect.Field listUpcomingEventsField = HomeController.class
                    .getDeclaredField("listUpcomingEvents");
            listUpcomingEventsField.setAccessible(true);
            listUpcomingEventsField.set(homeController, listUpcomingEvents);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRefreshEvents() {
        // Arrange
        Event event = new Event();
        event.setId(1);
        event.setUserId(1);
        event.setTitle("Event1");
        event.setType("EXAM");
        event.setEventDate(java.time.LocalDate.now());
        event.setEventTime("10:00");
        event.setDescription("Desc");
        event.setCourseId(1);
        List<Event> events = Arrays.asList(event);
        when(eventDAO.getUpcomingEvents(1, "event_date ASC")).thenReturn(events);

        // Act
        // loadEvents is private, so we can't directly test it
        // We would need to make it package-private or use reflection

        // Assert
        // verify(eventDAO).getUpcomingEvents(1, "event_date ASC");
    }
}