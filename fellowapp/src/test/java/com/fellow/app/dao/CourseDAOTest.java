package com.fellow.app.dao;

import com.fellow.app.model.Course;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseDAOTest {

    private static final String TEST_DB_FILE = "target/fellow-test.db";
    private CourseDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:" + TEST_DB_FILE);
        File f = new File(TEST_DB_FILE);
        if (f.exists()) {
            f.delete();
        }
        DatabaseConnection.initializeDatabase();
        dao = new CourseDAO();
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
    void testGetOrCreateDefaultCourse() {
        Course defaultCourse = dao.getOrCreateDefaultCourse(1);

        assertNotNull(defaultCourse);
        assertEquals(CourseDAO.DEFAULT_COURSE_NAME, defaultCourse.getCourseName());
        assertEquals(CourseDAO.DEFAULT_COURSE_DESCRIPTION, defaultCourse.getDescription());
        assertEquals(1, defaultCourse.getUserId());
        assertTrue(defaultCourse.getId() > 0);
    }

    @Test
    void testAddCourseAndGetCourseByIdAndName() {
        Course newCourse = new Course("Algorithms", "Algorithms course", "#ff0000", 1);
        assertTrue(dao.addCourse(newCourse), "Yeni ders veritabanına eklenmeli");
        assertTrue(newCourse.getId() > 0, "Eklenen dersin ID'si atanmalı");

        Course foundById = dao.getCourseById(newCourse.getId());
        assertNotNull(foundById);
        assertEquals("Algorithms", foundById.getCourseName());
        assertEquals("Algorithms course", foundById.getDescription());

        Course foundByName = dao.getCourseByName(1, "Algorithms");
        assertNotNull(foundByName);
        assertEquals(newCourse.getId(), foundByName.getId());
        assertEquals("#ff0000", foundByName.getColorHex());
    }

    @Test
    void testGetCoursesByUserIdIncludesDefaultAndSorted() {
        dao.getOrCreateDefaultCourse(1);
        Course newCourse = new Course("Biology", "Bio course", "#00ff00", 1);
        assertTrue(dao.addCourse(newCourse), "Yeni ders ekleme başarılı olmalı");

        List<Course> courses = dao.getCoursesByUserId(1);
        assertFalse(courses.isEmpty(), "Kullanıcının ders listesi boş olmamalı");
        assertEquals(CourseDAO.DEFAULT_COURSE_NAME, courses.get(0).getCourseName(), "Varsayılan ders ilk sırada olmalı");
        assertTrue(courses.stream().anyMatch(c -> "Biology".equals(c.getCourseName())), "Biology dersi listede olmalı");
    }

    @Test
    void testDeleteCourse() {
        Course newCourse = new Course("Chemistry", "Chemistry course", "#00ffff", 1);
        assertTrue(dao.addCourse(newCourse));

        assertTrue(dao.deleteCourse(newCourse.getId()), "Eklenen ders silinebilmeli");
        assertNull(dao.getCourseById(newCourse.getId()), "Silinen ders veritabanında bulunmamalı");
    }

    @Test
    void testDeleteDefaultCourseReturnsFalse() {
        Course defaultCourse = dao.getOrCreateDefaultCourse(1);
        assertFalse(dao.deleteCourse(defaultCourse.getId()), "Varsayılan ders silinmemeli");
    }
}
