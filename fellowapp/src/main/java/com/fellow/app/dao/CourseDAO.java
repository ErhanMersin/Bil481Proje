package com.fellow.app.dao;

import com.fellow.app.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    // Fetches all courses for a specific user
    public List<Course> getCoursesByUserId(int userId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("color_hex"),
                            rs.getInt("user_id"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
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
                    return new Course(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("color_hex"),
                            rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Returns null if course does not exist
    }

    // Adds a new course and sets its generated ID
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (name, color_hex, user_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getColorHex());
            pstmt.setInt(3, course.getUserId());

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
}