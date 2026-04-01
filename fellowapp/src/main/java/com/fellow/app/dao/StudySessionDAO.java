package com.fellow.app.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public int getTotalStudyTime(int userId, String startDate, String endDate) {
        String sql = "SELECT SUM(duration_seconds) FROM study_sessions " +
                "WHERE user_id = ? AND session_date >= ? AND session_date <= ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, startDate + " 00:00:00");
            pstmt.setString(3, endDate + " 23:59:59");

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

    /**
     * Retrieves daily study time for the past 7 days (including today).
     * Returns: Map<DayLabel, TotalSeconds> where DayLabel is "Mon", "Tue", etc.
     * FILLS EMPTY DAYS WITH 0 SECONDS.
     */
    public Map<String, Integer> getDailyChartData(int userId, int courseId, String startDate, String endDate) {
        Map<String, Integer> data = new LinkedHashMap<>();

        // First, fill all days in the week with 0
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate current = start;

        while (!current.isAfter(end)) {
            String dayLabel = current.format(DateTimeFormatter.ofPattern("EEE", java.util.Locale.ENGLISH));
            data.put(dayLabel + " " + current.format(DateTimeFormatter.ofPattern("MM/dd")), 0);
            current = current.plusDays(1);
        }

        // Then, override with actual data from database
        String sql = "SELECT DATE(session_date) as day, SUM(duration_seconds) as total " +
                "FROM study_sessions " +
                "WHERE user_id = ? AND course_id = ? AND session_date >= ? AND session_date <= ? " +
                "GROUP BY DATE(session_date) " +
                "ORDER BY day ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, startDate + " 00:00:00");
            pstmt.setString(4, endDate + " 23:59:59");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String day = rs.getString("day");
                    int total = rs.getInt("total");
                    LocalDate dateObj = LocalDate.parse(day);
                    String dayLabel = dateObj.format(DateTimeFormatter.ofPattern("EEE", java.util.Locale.ENGLISH)) + " "
                            +
                            dateObj.format(DateTimeFormatter.ofPattern("MM/dd"));
                    data.put(dayLabel, total);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Retrieves daily study time for specific date range without course filter.
     * For "All" courses selection.
     * FILLS EMPTY DAYS WITH 0 SECONDS.
     */
    public Map<String, Integer> getDailyChartData(int userId, String startDate, String endDate) {
        Map<String, Integer> data = new LinkedHashMap<>();

        // First, fill all days with 0
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate current = start;

        while (!current.isAfter(end)) {
            String dayLabel = current.format(DateTimeFormatter.ofPattern("EEE", java.util.Locale.ENGLISH));
            data.put(dayLabel + " " + current.format(DateTimeFormatter.ofPattern("MM/dd")), 0);
            current = current.plusDays(1);
        }

        // Then, override with actual data from database
        String sql = "SELECT DATE(session_date) as day, SUM(duration_seconds) as total " +
                "FROM study_sessions " +
                "WHERE user_id = ? AND session_date >= ? AND session_date <= ? " +
                "GROUP BY DATE(session_date) " +
                "ORDER BY day ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, startDate + " 00:00:00");
            pstmt.setString(3, endDate + " 23:59:59");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String day = rs.getString("day");
                    int total = rs.getInt("total");
                    LocalDate dateObj = LocalDate.parse(day);
                    String dayLabel = dateObj.format(DateTimeFormatter.ofPattern("EEE", java.util.Locale.ENGLISH)) + " "
                            +
                            dateObj.format(DateTimeFormatter.ofPattern("MM/dd"));
                    data.put(dayLabel, total);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Retrieves monthly study time for all time.
     * Returns: Map<MonthLabel, TotalSeconds> where MonthLabel is "2025-01", etc.
     */
    public Map<String, Integer> getMonthlyChartData(int userId, int courseId) {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT STRFTIME('%Y-%m', session_date) as month, SUM(duration_seconds) as total " +
                "FROM study_sessions " +
                "WHERE user_id = ? AND course_id = ? " +
                "GROUP BY STRFTIME('%Y-%m', session_date) " +
                "ORDER BY month ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String month = rs.getString("month");
                    int total = rs.getInt("total");
                    data.put(month, total);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Retrieves monthly study time for all courses.
     */
    public Map<String, Integer> getMonthlyChartData(int userId) {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT STRFTIME('%Y-%m', session_date) as month, SUM(duration_seconds) as total " +
                "FROM study_sessions " +
                "WHERE user_id = ? " +
                "GROUP BY STRFTIME('%Y-%m', session_date) " +
                "ORDER BY month ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String month = rs.getString("month");
                    int total = rs.getInt("total");
                    data.put(month, total);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}