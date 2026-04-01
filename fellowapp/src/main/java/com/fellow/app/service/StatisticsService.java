package com.fellow.app.service;

import com.fellow.app.dao.StudySessionDAO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service for statistics calculations.
 * Uses the Strategy pattern to support daily / weekly / monthly time ranges.
 */
public class StatisticsService {

    private final StudySessionDAO sessionDAO = new StudySessionDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── Strategy interface ────────────────────────────────────────────────────

    public interface TimeStrategy {
        int calculateTime(int userId, int courseId, StudySessionDAO dao, DateTimeFormatter fmt);

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

        @Override
        public String startDate(DateTimeFormatter fmt) {
            return LocalDate.now().format(fmt);
        }

        @Override
        public String endDate(DateTimeFormatter fmt) {
            return LocalDate.now().format(fmt);
        }

        @Override
        public String label() {
            return "today";
        }
    }

    public static class WeeklyStrategy implements TimeStrategy {
        @Override
        public int calculateTime(int userId, int courseId, StudySessionDAO dao, DateTimeFormatter fmt) {
            LocalDate today = LocalDate.now();
            return dao.getTotalStudyTime(userId, courseId,
                    today.with(DayOfWeek.MONDAY).format(fmt),
                    today.with(DayOfWeek.SUNDAY).format(fmt));
        }

        @Override
        public String startDate(DateTimeFormatter fmt) {
            return LocalDate.now().with(DayOfWeek.MONDAY).format(fmt);
        }

        @Override
        public String endDate(DateTimeFormatter fmt) {
            return LocalDate.now().with(DayOfWeek.SUNDAY).format(fmt);
        }

        @Override
        public String label() {
            return "this week";
        }
    }

    public static class MonthlyStrategy implements TimeStrategy {
        @Override
        public int calculateTime(int userId, int courseId, StudySessionDAO dao, DateTimeFormatter fmt) {
            LocalDate today = LocalDate.now();
            return dao.getTotalStudyTime(userId, courseId,
                    today.withDayOfMonth(1).format(fmt),
                    today.withDayOfMonth(today.lengthOfMonth()).format(fmt));
        }

        @Override
        public String startDate(DateTimeFormatter fmt) {
            return LocalDate.now().withDayOfMonth(1).format(fmt);
        }

        @Override
        public String endDate(DateTimeFormatter fmt) {
            return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).format(fmt);
        }

        @Override
        public String label() {
            return "this month";
        }
    }

    public static class TotalStrategy implements TimeStrategy {
        @Override
        public int calculateTime(int userId, int courseId, StudySessionDAO dao, DateTimeFormatter fmt) {
            // Get all study sessions without date restriction
            LocalDate epoch = LocalDate.of(2000, 1, 1);
            LocalDate future = LocalDate.of(2099, 12, 31);
            return dao.getTotalStudyTime(userId, courseId,
                    epoch.format(fmt),
                    future.format(fmt));
        }

        @Override
        public String startDate(DateTimeFormatter fmt) {
            return LocalDate.of(2000, 1, 1).format(fmt);
        }

        @Override
        public String endDate(DateTimeFormatter fmt) {
            return LocalDate.of(2099, 12, 31).format(fmt);
        }

        @Override
        public String label() {
            return "all time";
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns all distinct course names the user has studied in the given time
     * range.
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
    private int getEffectiveTotalStudyTime(int userId, int courseId, TimeStrategy strategy) {
        if (courseId < 0) {
            return sessionDAO.getTotalStudyTime(userId,
                    strategy.startDate(formatter),
                    strategy.endDate(formatter));
        }
        return strategy.calculateTime(userId, courseId, sessionDAO, formatter);
    }

    public String getStudyTimeReport(int userId, int courseId, String courseName, TimeStrategy strategy) {
        int totalSeconds = getEffectiveTotalStudyTime(userId, courseId, strategy);

        if (totalSeconds == 0) {
            return "No study records found for " + courseName + " " + strategy.label() + ".";
        }

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;

        return String.format("You studied %d hour%s %d minute%s for %s %s.",
                hours, hours == 1 ? "" : "s",
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

    /**
     * Gets chart data for the current time strategy.
     * DailyStrategy -> daily data from the week
     * WeeklyStrategy -> daily data from the week
     * MonthlyStrategy -> daily data from the month
     * TotalStrategy -> monthly data from all time
     */
    public Map<String, Integer> getChartData(int userId, int courseId, TimeStrategy strategy) {
        if (strategy instanceof DailyStrategy || strategy instanceof WeeklyStrategy) {
            // Show daily view for current week
            LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
            LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);
            if (courseId < 0) {
                return sessionDAO.getDailyChartData(userId,
                        monday.format(formatter),
                        sunday.format(formatter));
            }
            return sessionDAO.getDailyChartData(userId, courseId,
                    monday.format(formatter),
                    sunday.format(formatter));
        } else if (strategy instanceof MonthlyStrategy) {
            // Show daily view for current month
            LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDay = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            if (courseId < 0) {
                return sessionDAO.getDailyChartData(userId,
                        firstDay.format(formatter),
                        lastDay.format(formatter));
            }
            return sessionDAO.getDailyChartData(userId, courseId,
                    firstDay.format(formatter),
                    lastDay.format(formatter));
        } else if (strategy instanceof TotalStrategy) {
            // Show monthly view for all time
            if (courseId < 0) {
                return sessionDAO.getMonthlyChartData(userId);
            }
            return sessionDAO.getMonthlyChartData(userId, courseId);
        }
        return Map.of();
    }
}