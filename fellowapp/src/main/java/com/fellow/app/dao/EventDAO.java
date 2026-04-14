package com.fellow.app.dao;

import com.fellow.app.model.Event;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Event> getUpcomingEvents(int userId, String sortBy) {
        List<Event> events = new ArrayList<>();
        String orderClause = "event_date ASC"; // default
        if ("created_date DESC".equals(sortBy)) {
            orderClause = "created_date DESC";
        }

        String sql = "SELECT * FROM events WHERE user_id = ? AND event_date >= CURRENT_DATE ORDER BY " + orderClause
                + ", id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public boolean addEvent(Event event) {
        String sql = "INSERT INTO events (title, type, course_id, event_date, event_time, description, user_id, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getType());
            pstmt.setInt(3, event.getCourseId());
            pstmt.setString(4, event.getEventDate() != null ? event.getEventDate().toString() : null);
            pstmt.setString(5, event.getEventTime());
            pstmt.setString(6, event.getDescription());
            pstmt.setInt(7, event.getUserId());

            String now = LocalDateTime.now().format(dtFormatter);
            pstmt.setString(8, now);
            event.setCreatedDate(LocalDateTime.now());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        event.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Event> getEventsByMonth(int userId, int year, int month) {
        List<Event> events = new ArrayList<>();
        String monthStr = String.format("%04d-%02d", year, month);
        String sql = "SELECT * FROM events WHERE user_id = ? AND event_date LIKE ? ORDER BY event_date ASC, event_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, monthStr + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public List<Event> getEventsByDate(int userId, LocalDate date) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE user_id = ? AND event_date = ? ORDER BY event_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, date.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(rs.getInt("id"));
        e.setTitle(rs.getString("title"));
        e.setType(rs.getString("type"));
        e.setCourseId(rs.getInt("course_id"));
        String d = rs.getString("event_date");
        if (d != null && !d.isEmpty()) {
            e.setEventDate(LocalDate.parse(d));
        }
        e.setEventTime(rs.getString("event_time"));
        e.setDescription(rs.getString("description"));
        e.setUserId(rs.getInt("user_id"));
        String cd = rs.getString("created_date");
        if (cd != null && !cd.isEmpty()) {
            try {
                e.setCreatedDate(LocalDateTime.parse(cd.replace("T", " ").substring(0, 19), dtFormatter));
            } catch (Exception ex) {
            }
        }
        return e;
    }
}
