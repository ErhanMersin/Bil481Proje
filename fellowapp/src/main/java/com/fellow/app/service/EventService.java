package com.fellow.app.service;

import com.fellow.app.dao.EventDAO;
import com.fellow.app.dao.CourseDAO;
import com.fellow.app.model.Event;
import com.fellow.app.model.Course;
import java.time.LocalDate;
import java.util.List;

public class EventService {
    private final EventDAO eventDAO = new EventDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    public List<Event> getUpcomingEvents(int userId, String sortBy) {
        return eventDAO.getUpcomingEvents(userId, sortBy);
    }

    public List<Event> getEventsByMonth(int userId, int year, int month) {
        return eventDAO.getEventsByMonth(userId, year, month);
    }

    public List<Event> getEventsByDate(int userId, LocalDate date) {
        return eventDAO.getEventsByDate(userId, date);
    }

    public boolean addEvent(Event event) {
        return eventDAO.addEvent(event);
    }

    public boolean deleteEvent(int id) {
        return eventDAO.deleteEvent(id);
    }

    public List<Course> getCoursesByUserId(int userId) {
        return courseDAO.getCoursesByUserId(userId);
    }

    public Course getOrCreateDefaultCourse(int userId) {
        return courseDAO.getOrCreateDefaultCourse(userId);
    }

    public Course getCourseById(int courseId) {
        return courseDAO.getCourseById(courseId);
    }
}
