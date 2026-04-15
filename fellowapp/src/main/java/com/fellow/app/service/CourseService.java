package com.fellow.app.service;

import com.fellow.app.dao.CourseDAO;
import com.fellow.app.model.Course;
import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO = new CourseDAO();

    public List<Course> getCoursesByUserId(int userId) {
        return courseDAO.getCoursesByUserId(userId);
    }

    public Course getCourseById(int courseId) {
        return courseDAO.getCourseById(courseId);
    }

    public Course getCourseByName(int userId, String courseName) {
        return courseDAO.getCourseByName(userId, courseName);
    }

    public Course getOrCreateDefaultCourse(int userId) {
        return courseDAO.getOrCreateDefaultCourse(userId);
    }

    public boolean addCourse(Course course) {
        return courseDAO.addCourse(course);
    }

    public boolean deleteCourse(int courseId) {
        return courseDAO.deleteCourse(courseId);
    }
}
