package com.fellow.app.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudySessionDAO {

    public int[] getUserStats(int userId) {
        int[] stats = new int[2];
        String sql = "SELECT SUM(duration_seconds), SUM(pomodoro_count) FROM study_sessions WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats[0] = rs.getInt(1);
                    stats[1] = rs.getInt(2);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public boolean addSession(int courseId, int userId, int durationSeconds, int pomodoroCount) {
        // GECE YARISI SORUNUNU ÇÖZEN KISIM: Tarihi SQLite'a bırakmıyoruz, biz yerel
        // saati gönderiyoruz
        String sql = "INSERT INTO study_sessions (course_id, user_id, duration_seconds, pomodoro_count, session_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, durationSeconds);
            pstmt.setInt(4, pomodoroCount);

            // Yerel saati gönder
            String localNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(5, localNow);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalStudyTime(int userId, int courseId, String startDate, String endDate) {
        String sql = "SELECT SUM(duration_seconds) FROM study_sessions " +
                "WHERE user_id = ? AND course_id = ? AND session_date >= ? AND session_date <= ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, startDate + " 00:00:00");
            pstmt.setString(4, endDate + " 23:59:59");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // STATISTICS SERVICE İÇİN GEREKLİ YENİ METOT: Sadece o tarihlerde çalışılan
    // ders adlarını getirir
    public List<String> getCourseNamesStudiedInRange(int userId, String startDate, String endDate) {
        List<String> courseNames = new ArrayList<>();
        String sql = "SELECT DISTINCT c.name FROM courses c " +
                "JOIN study_sessions s ON c.id = s.course_id " +
                "WHERE s.user_id = ? AND s.session_date >= ? AND s.session_date <= ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, startDate + " 00:00:00");
            pstmt.setString(3, endDate + " 23:59:59");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next())
                    courseNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseNames;
    }

    // STATISTICS SERVICE İÇİN GEREKLİ YENİ METOT: Ders adından ID bulur
    public int getCourseIdByName(int userId, String courseName) {
        String sql = "SELECT id FROM courses WHERE user_id = ? AND name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, courseName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}