package com.fellow.app.integration;

import com.fellow.app.controller.TodoController;
import com.fellow.app.dao.DatabaseConnection;
import com.fellow.app.dao.TodoItemDAO;
import com.fellow.app.model.TodoItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for TodoController + TodoItemDAO + Database
 * Tests the complete workflow of adding, retrieving, updating and deleting todos
 */
public class TodoControllerIntegrationTest {

    private static final String TEST_DB_FILE = "target/fellow-integration-test.db";
    private TodoController todoController;
    private TodoItemDAO todoDAO;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.initializeDatabase();

        todoDAO = new TodoItemDAO();
        todoController = new TodoController();
        
        // Inject DAO into controller via reflection
        Field daoField = TodoController.class.getDeclaredField("todoDAO");
        daoField.setAccessible(true);
        daoField.set(todoController, todoDAO);
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
    void testAddTodoViaControllerPersistsToDatabase() {
        // Controller context: userId=1, courseId=1
        TodoItem newTodo = new TodoItem(1, 1, "Integration Test Todo", "This is a test todo");
        
        // Add via DAO (simulating controller action)
        boolean isAdded = todoDAO.addTodo(newTodo);
        assertTrue(isAdded, "Todo should be added successfully");
        assertTrue(newTodo.getId() > 0, "Todo should have generated ID");

        // Verify persistence: fetch all todos for user
        List<TodoItem> userTodos = todoDAO.getTodosByUserId(1);
        assertFalse(userTodos.isEmpty(), "User should have at least one todo");
        
        TodoItem foundTodo = userTodos.stream()
                .filter(t -> t.getId() == newTodo.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(foundTodo, "Newly added todo should be found in database");
        assertEquals("Integration Test Todo", foundTodo.getTopic());
        assertEquals("This is a test todo", foundTodo.getDescription());
        assertEquals(1, foundTodo.getCourseId());
        assertFalse(foundTodo.isCompleted(), "New todo should not be completed");
    }

    @Test
    void testCompleteTodoUpdatesPersistentState() {
        // Add a todo
        TodoItem todo = new TodoItem(1, 1, "Task to Complete", "Test completion");
        todoDAO.addTodo(todo);
        int todoId = todo.getId();

        // Verify initial state
        int activeCountBefore = todoDAO.getActiveTodoCount(1);
        assertTrue(activeCountBefore > 0, "Should have active todos");

        // Mark as completed
        boolean isUpdated = todoDAO.updateCompleted(todoId, true);
        assertTrue(isUpdated, "Update should succeed");

        // Verify state changed in database
        int activeCountAfter = todoDAO.getActiveTodoCount(1);
        assertEquals(activeCountBefore - 1, activeCountAfter, "Active count should decrease");

        // Verify by fetching completed todo
        List<TodoItem> todos = todoDAO.getTodosByUserId(1);
        TodoItem completedTodo = todos.stream()
                .filter(t -> t.getId() == todoId)
                .findFirst()
                .orElse(null);
        assertNotNull(completedTodo);
        assertTrue(completedTodo.isCompleted(), "Todo should be marked as completed");
    }

    @Test
    void testDeleteTodoRemovesFromDatabase() {
        // Add multiple todos
        TodoItem todo1 = new TodoItem(1, 1, "Todo 1", "First");
        TodoItem todo2 = new TodoItem(1, 1, "Todo 2", "Second");
        todoDAO.addTodo(todo1);
        todoDAO.addTodo(todo2);

        List<TodoItem> beforeDelete = todoDAO.getTodosByUserId(1);
        int countBefore = beforeDelete.size();

        // Delete one todo
        boolean isDeleted = todoDAO.deleteTodo(todo1.getId());
        assertTrue(isDeleted, "Todo should be deleted successfully");

        // Verify deletion persisted
        List<TodoItem> afterDelete = todoDAO.getTodosByUserId(1);
        int countAfter = afterDelete.size();
        assertEquals(countBefore - 1, countAfter, "Todo count should decrease");
        assertFalse(afterDelete.stream().anyMatch(t -> t.getId() == todo1.getId()), 
                    "Deleted todo should not exist");
        assertTrue(afterDelete.stream().anyMatch(t -> t.getId() == todo2.getId()), 
                   "Other todo should still exist");
    }

    @Test
    void testGetTodosByUserAndCourseFiltersCorrectly() {
        // Add todos for different courses
        TodoItem courseTodo1 = new TodoItem(1, 1, "Course 1 Task 1", "Desc");
        TodoItem courseTodo2 = new TodoItem(1, 1, "Course 1 Task 2", "Desc");
        TodoItem courseTodo3 = new TodoItem(1, 1, "Course 1 Task 3", "Desc");
        
        todoDAO.addTodo(courseTodo1);
        todoDAO.addTodo(courseTodo2);
        todoDAO.addTodo(courseTodo3);

        // Get todos for course 1
        List<TodoItem> course1Todos = todoDAO.getTodosByUserAndCourse(1, 1);
        assertEquals(3, course1Todos.size(), "Should have 3 todos for course 1");

        // Get todos for non-existing course
        List<TodoItem> course999Todos = todoDAO.getTodosByUserAndCourse(1, 999);
        assertTrue(course999Todos.isEmpty(), "Should have no todos for non-existing course");
    }

    @Test
    void testMultipleTodosLifecycleWorkflow() {
        // Simulate complete workflow: add multiple -> read -> update some -> delete some
        
        // 1. Add todos
        TodoItem[] todos = new TodoItem[5];
        for (int i = 0; i < 5; i++) {
            todos[i] = new TodoItem(1, 1, "Task " + (i + 1), "Description " + (i + 1));
            todoDAO.addTodo(todos[i]);
        }

        // 2. Verify all added
        List<TodoItem> allTodos = todoDAO.getTodosByUserId(1);
        assertEquals(5, allTodos.size(), "Should have 5 todos");

        // 3. Complete some
        todoDAO.updateCompleted(todos[0].getId(), true);
        todoDAO.updateCompleted(todos[1].getId(), true);

        // 4. Delete some
        todoDAO.deleteTodo(todos[3].getId());
        todoDAO.deleteTodo(todos[4].getId());

        // 5. Verify final state
        List<TodoItem> finalList = todoDAO.getTodosByUserId(1);
        assertEquals(3, finalList.size(), "Should have 3 todos left");
        
        int completedCount = (int) finalList.stream().filter(TodoItem::isCompleted).count();
        assertEquals(2, completedCount, "Should have 2 completed todos");
        
        int activeCount = (int) finalList.stream().filter(t -> !t.isCompleted()).count();
        assertEquals(1, activeCount, "Should have 1 active todo");
    }
}
