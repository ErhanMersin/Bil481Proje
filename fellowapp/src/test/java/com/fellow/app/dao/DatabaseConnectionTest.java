package com.fellow.app.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    private static final String TEST_DB_FILE = "target/fellow-test.db";

    @BeforeEach
    void setUp() {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
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
    void testInitializeDatabaseCreatesFile() throws SQLException {
        DatabaseConnection.initializeDatabase();

        File f = new File(DB_FILE);
        assertTrue(f.exists(), "Database file should be created after initializeDatabase");

        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }
    }

    @Test
    void testGetConnectionReturnsSameConnectionAndClose() throws SQLException {
        Connection conn1 = DatabaseConnection.getConnection();
        assertNotNull(conn1);
        assertFalse(conn1.isClosed());

        Connection conn2 = DatabaseConnection.getConnection();
        assertSame(conn1, conn2, "getConnection should return the same singleton connection instance");

        DatabaseConnection.closeConnection();
        assertTrue(conn1.isClosed(), "Connection should be closed after closeConnection");
    }
}
