package com.fellow.app.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    
    // Common date/time formatters
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * Formats LocalDateTime to string using standard format
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Formats LocalDate to string using standard format
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return null;
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Formats LocalTime to string using standard format
     */
    public static String formatTime(LocalTime time) {
        if (time == null) return null;
        return time.format(TIME_FORMATTER);
    }
    
    /**
     * Parses string to LocalDateTime using standard format
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Parses string to LocalDate using standard format
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Parses string to LocalTime using standard format
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return null;
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Validates if a date string is in valid format
     */
    public static boolean isValidDate(String dateStr) {
        return parseDate(dateStr) != null;
    }
    
    /**
     * Validates if a time string is in valid format (HH:mm)
     */
    public static boolean isValidTime(String timeStr) {
        return parseTime(timeStr) != null;
    }
    
    /**
     * Validates if a date-time string is in valid format
     */
    public static boolean isValidDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr) != null;
    }
    
    /**
     * Gets current date and time
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * Gets current date
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Checks if a date is in the past
     */
    public static boolean isPastDate(LocalDate date) {
        if (date == null) return false;
        return date.isBefore(today());
    }
    
    /**
     * Checks if a date is today
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) return false;
        return date.equals(today());
    }
    
    /**
     * Checks if a date is in the future
     */
    public static boolean isFutureDate(LocalDate date) {
        if (date == null) return false;
        return date.isAfter(today());
    }
}
