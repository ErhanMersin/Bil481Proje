package com.fellow.app.service;

import com.fellow.app.dao.StudySessionDAO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for statistics calculations.
 * Uses the Strategy pattern to support daily / weekly / monthly time ranges.
 */
public class StatisticsService {

    private final StudySessionDAO sessionDAO = new StudySessionDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── Strategy interface ────────────────────────────────────────────────────

    public interface TimeStrategy {
        int    calculateTime(int userId, int courseId, StudySessionDAO dao, DateTimeFormatter fmt);
        String startDate(DateTimeFormatter fmt);
        String endDate(DateTimeFormatter fmt);
        String label(); // "today" / "this week" / "this month"
    }

    // ── Concrete strategies ───────────────────────────────────────────────────

    public static class DailyStrategy implements TimeStrategy {
        @Override
        public int calculateTime(int userId, int courseId, StudySessionDAO dao, DateTimeFormatter fmt) {
            String today = LocalDate.now().format(fmt);
            return dao.getTotalStudyTime(userId, courseId, today, today);
        }
        @Override public String startDate(DateTimeFormatter fmt) { return LocalDate.now().format(fmt); }
        @Override public String endDate(DateTimeFormatter fmt)   { return LocalDate.now().format(fmt); }
        @Override public String label() { return "today"; }
    }

    public static class WeeklyStrategy implements TimeStrategy {
        @Override
        public int calculateTime(int userId, int courseId, StudySessionDAO dao, DateTimeFormatter fmt) {
            LocalDate today = LocalDate.now();
            return dao.getTotalStudyTime(userId, courseId,
                    today.with(DayOfWeek.MONDAY).format(fmt),
                    today.with(DayOfWeek.SUNDAY).format(fmt));
        }
        @Override public String startDate(DateTimeFormatter fmt) { return LocalDate.now().with(DayOfWeek.MONDAY).format(fmt); }
        @Override public String endDate(DateTimeFormatter fmt)   { return LocalDate.now().with(DayOfWeek.SUNDAY).format(fmt); }
        @Override public String label() { return "this week"; }
    }

    public static class MonthlyStrategy implements TimeStrategy {
        @Override
        public int calculateTime(int userId, int courseId, StudySessionDAO dao, DateTimeFormatter fmt) {
            LocalDate today = LocalDate.now();
            return dao.getTotalStudyTime(userId, courseId,
                    today.withDayOfMonth(1).format(fmt),
                    today.withDayOfMonth(today.lengthOfMonth()).format(fmt));
        }
        @Override public String startDate(DateTimeFormatter fmt) { return LocalDate.now().withDayOfMonth(1).format(fmt); }
        @Override public String endDate(DateTimeFormatter fmt)   { return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).format(fmt); }
        @Override public String label() { return "this month"; }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns all distinct course names the user has studied in the given time range.
     * Used to populate the course selector in the Statistics panel.
     */
    public List<String> getStudiedCourseNames(int userId, TimeStrategy strategy) {
        return sessionDAO.getCourseNamesStudiedInRange(
                userId,
                strategy.startDate(formatter),
                strategy.endDate(formatter));
    }

    /**
     * Builds the study time report string for a specific course and time range.
     * Example: "You studied 2 hours 30 minutes for BIL-481 this week."
     */
    public String getStudyTimeReport(int userId, int courseId, String courseName, TimeStrategy strategy) {
        int totalSeconds = strategy.calculateTime(userId, courseId, sessionDAO, formatter);

        if (totalSeconds == 0) {
            return "No study records found for " + courseName + " " + strategy.label() + ".";
        }

        int hours   = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;

        return String.format("You studied %d hour%s %d minute%s for %s %s.",
                hours,   hours   == 1 ? "" : "s",
                minutes, minutes == 1 ? "" : "s",
                courseName,
                strategy.label());
    }

    /**
     * Resolves a course name to its database id for the given user.
     */
    public int getCourseIdByName(int userId, String courseName) {
        return sessionDAO.getCourseIdByName(userId, courseName);
    }
}