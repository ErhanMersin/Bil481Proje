package com.fellow.app.dao;

import com.fellow.app.model.TodoItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TodoItemDAOTest {

    private static final String TEST_DB_FILE = "target/fellow-test.db";
    private TodoItemDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        // Set the database to test database
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        
        // Remove old test db file if exists
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        
        // Initialize tables and seed dummy user/course (userId=1, courseId=1)
        DatabaseConnection.initializeDatabase();
        
        // Initialize the DAO
        dao = new TodoItemDAO();
    }

    @AfterEach
    void tearDown() {
        DatabaseConnection.closeConnection();
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.resetDatabaseUrl();
    }

    @Test
    void testAddAndGetTodo() {
        // Create a new todo
        TodoItem newItem = new TodoItem(1, 1, "Test Task", "Test Description");
        
        // Save to DB
        boolean isAdded = dao.addTodo(newItem);
        assertTrue(isAdded, "Todo should be successfully added");

        // Read from DB
        List<TodoItem> userTodos = dao.getTodosByUserId(1);
        assertFalse(userTodos.isEmpty(), "User should have at least one todo");

        // Verify content
        TodoItem fetchedItem = userTodos.get(userTodos.size() - 1);
        assertTrue(fetchedItem.getId() > 0, "Todo ID should be auto-generated and positive in DB");
        assertEquals("Test Task", fetchedItem.getTopic());
        assertEquals("Test Description", fetchedItem.getDescription());
        assertEquals(1, fetchedItem.getCourseId());
        assertFalse(fetchedItem.isCompleted(), "New todo should not be completed by default");
    }

    @Test
    void testUpdateCompleted() {
        // Create and add a new todo
        TodoItem newItem = new TodoItem(1, 1, "Update Task", "Update Desc");
        dao.addTodo(newItem);

        // Fetch inserted item to get its real ID
        List<TodoItem> userTodos = dao.getTodosByUserId(1);
        TodoItem insertedItem = userTodos.get(userTodos.size() - 1);
        int actualId = insertedItem.getId();

        // Check active count before update
        int activeCountBefore = dao.getActiveTodoCount(1);
        
        // Mark as completed
        boolean isUpdated = dao.updateCompleted(actualId, true);
        assertTrue(isUpdated, "Todo should be updated successfully");

        // Check active count after update
        int activeCountAfter = dao.getActiveTodoCount(1);
        assertEquals(activeCountBefore - 1, activeCountAfter, "Active count should decrease by 1");

        // Verify status from DB
        List<TodoItem> todos = dao.getTodosByUserId(1);
        boolean foundAndCompleted = false;
        for (TodoItem t : todos) {
            if (t.getId() == actualId && t.isCompleted()) {
                foundAndCompleted = true;
                break;
            }
        }
        assertTrue(foundAndCompleted, "The todo should be found and marked as completed");
    }

    @Test
    void testDeleteTodo() {
        // Create and add a new todo
        TodoItem newItem = new TodoItem(1, 1, "Delete Task", "Delete Desc");
        dao.addTodo(newItem);
        
        // Fetch inserted item to get its real ID
        List<TodoItem> userTodos = dao.getTodosByUserId(1);
        TodoItem insertedItem = userTodos.get(userTodos.size() - 1);
        int idToDelete = insertedItem.getId();

        // Delete from DB
        boolean isDeleted = dao.deleteTodo(idToDelete);
        assertTrue(isDeleted, "Todo should be deleted successfully");

        // Verify it was actually removed
        List<TodoItem> todos = dao.getTodosByUserId(1);
        boolean found = false;
        for (TodoItem t : todos) {
            if (t.getId() == idToDelete) {
                found = true;
                break;
            }
        }
        assertFalse(found, "The deleted todo should not be in the returned list");
    }
}
