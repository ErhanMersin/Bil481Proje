package com.fellow.app.service;

import com.fellow.app.dao.TodoItemDAO;
import com.fellow.app.dao.CourseDAO;
import com.fellow.app.model.TodoItem;
import com.fellow.app.model.Course;
import java.util.List;

public class TodoService {
    private final TodoItemDAO todoDAO = new TodoItemDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    public boolean addTodo(TodoItem item) {
        return todoDAO.addTodo(item);
    }

    public List<TodoItem> getTodosByUserId(int userId) {
        return todoDAO.getTodosByUserId(userId);
    }

    public List<TodoItem> getTodosByUserAndCourse(int userId, int courseId) {
        return todoDAO.getTodosByUserAndCourse(userId, courseId);
    }

    public boolean updateCompleted(int id, boolean completed) {
        return todoDAO.updateCompleted(id, completed);
    }

    public boolean deleteTodo(int id) {
        return todoDAO.deleteTodo(id);
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
