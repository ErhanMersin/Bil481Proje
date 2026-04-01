package com.fellow.app.dao;

import com.fellow.app.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public static final String DEFAULT_COURSE_NAME = "Default";
    public static final String DEFAULT_COURSE_DESCRIPTION = "Default course";
    public static final String DEFAULT_COURSE_COLOR = "#6366f1";

    // Fetches all courses for a specific user and ensures a default course exists
    public List<Course> getCoursesByUserId(int userId) {
        getOrCreateDefaultCourse(userId);

        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE user_id = ? " +
                     "ORDER BY CASE WHEN name = ? THEN 0 ELSE 1 END, name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, DEFAULT_COURSE_NAME);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("color_hex"),
                            rs.getInt("user_id"));
                    course.setDescription(rs.getString("description"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("color_hex"),
                            rs.getInt("user_id"));
                    course.setDescription(rs.getString("description"));
                    return course;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fetches a specific course by its name
    public Course getCourseByName(int userId, String courseName) {
        String sql = "SELECT * FROM courses WHERE user_id = ? AND name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, courseName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("color_hex"),
                            rs.getInt("user_id"));
                    course.setDescription(rs.getString("description"));
                    return course;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Returns null if course does not exist
    }

    // Adds a new course and sets its generated ID
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (name, description, color_hex, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getDescription());
            pstmt.setString(3, course.getColorHex());
            pstmt.setInt(4, course.getUserId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next())
                        course.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCourse(int courseId) {
        Course course = getCourseById(courseId);
        if (course == null || isReservedDefaultCourseName(course.getCourseName())) {
            return false;
        }

        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Course getOrCreateDefaultCourse(int userId) {
        Course defaultCourse = getCourseByName(userId, DEFAULT_COURSE_NAME);
        if (defaultCourse != null) {
            return defaultCourse;
        }

        defaultCourse = new Course(DEFAULT_COURSE_NAME, DEFAULT_COURSE_DESCRIPTION, DEFAULT_COURSE_COLOR, userId);
        addCourse(defaultCourse);
        return defaultCourse;
    }

    private boolean isReservedDefaultCourseName(String courseName) {
        return DEFAULT_COURSE_NAME.equals(courseName);
    }
}
