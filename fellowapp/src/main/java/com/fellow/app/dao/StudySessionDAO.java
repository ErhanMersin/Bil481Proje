package com.fellow.app.dao;

import java.sql.*;

public class StudySessionDAO {
    
    // Returns [total_study_seconds, total_pomodoro_count]
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
        String sql = "INSERT INTO study_sessions (course_id, user_id, duration_seconds, pomodoro_count) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, durationSeconds);
            pstmt.setInt(4, pomodoroCount);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
