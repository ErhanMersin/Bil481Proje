package com.fellow.app.dao;

import com.fellow.app.model.TodoItem;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TodoItemDAO {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean addTodo(TodoItem item) {
        String sql = "INSERT INTO todo_items (course_id, user_id, topic, description, completed, created_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, item.getCourseId());
            pstmt.setInt(2, item.getUserId());
            pstmt.setString(3, item.getTopic());
            pstmt.setString(4, item.getDescription() == null ? "" : item.getDescription());
            pstmt.setInt(5, item.isCompleted() ? 1 : 0);
            
            String now = LocalDateTime.now().format(formatter);
            pstmt.setString(6, now);
            item.setCreatedDate(LocalDateTime.now());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        item.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<TodoItem> getTodosByUserAndCourse(int userId, int courseId) {
        List<TodoItem> list = new ArrayList<>();
        String sql = "SELECT * FROM todo_items WHERE user_id = ? AND course_id = ? ORDER BY completed ASC, id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TodoItem t = new TodoItem(rs.getInt("course_id"), rs.getInt("user_id"), rs.getString("topic"), rs.getString("description"));
                    t.setId(rs.getInt("id"));
                    t.setCompleted(rs.getInt("completed") == 1);
                    String cd = rs.getString("created_date");
                    if (cd != null && !cd.isEmpty()) {
                        try {
                            t.setCreatedDate(LocalDateTime.parse(cd.replace("T", " ").substring(0, 19), formatter));
                        } catch (Exception ex) {}
                    }
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateCompleted(int id, boolean completed) {
        String sql = "UPDATE todo_items SET completed = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, completed ? 1 : 0);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTodo(int id) {
        String sql = "DELETE FROM todo_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getActiveTodoCount(int userId) {
        String sql = "SELECT COUNT(*) FROM todo_items WHERE user_id = ? AND completed = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
