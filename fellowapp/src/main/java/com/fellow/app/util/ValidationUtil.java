package com.fellow.app.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Common validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern TIME_PATTERN = Pattern.compile(
        "^([01]?[0-9]|2[0-3]):[0-5][0-9]$"
    );
    
    /**
     * Checks if a string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Checks if a string is not null and not empty
     */
    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }
    
    /**
     * Validates string length is within range
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) return false;
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Validates string is not null, not empty, and within length range
     */
    public static boolean isValidString(String str, int minLength, int maxLength) {
        return isNotNullOrEmpty(str) && isValidLength(str, minLength, maxLength);
    }
    
    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates time format (HH:mm)
     */
    public static boolean isValidTimeFormat(String time) {
        if (isNullOrEmpty(time)) return false;
        return TIME_PATTERN.matcher(time.trim()).matches();
    }
    
    /**
     * Validates integer is within range
     */
    public static boolean isValidInteger(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates course name (not null, not empty, reasonable length)
     */
    public static boolean isValidCourseName(String courseName) {
        return isValidString(courseName, 1, 100);
    }
    
    /**
     * Validates event title (not null, not empty, reasonable length)
     */
    public static boolean isValidEventTitle(String title) {
        return isValidString(title, 1, 200);
    }
    
    /**
     * Validates todo topic (not null, not empty, reasonable length)
     */
    public static boolean isValidTodoTopic(String topic) {
        return isValidString(topic, 1, 150);
    }
    
    /**
     * Validates description (can be null or empty, but if provided, reasonable length)
     */
    public static boolean isValidDescription(String description) {
        return description == null || description.length() <= 500;
    }
    
    /**
     * Validates user ID (positive integer)
     */
    public static boolean isValidUserId(int userId) {
        return userId > 0;
    }
    
    /**
     * Validates course ID (positive integer)
     */
    public static boolean isValidCourseId(int courseId) {
        return courseId > 0;
    }
    
    /**
     * Validates event type (must be one of predefined types)
     */
    public static boolean isValidEventType(String eventType) {
        if (isNullOrEmpty(eventType)) return false;
        String upperType = eventType.trim().toUpperCase();
        return upperType.equals("EXAM") || upperType.equals("PROJECT") || 
               upperType.equals("HOMEWORK") || upperType.equals("QUIZ") || 
               upperType.equals("LECTURE") || upperType.equals("OTHER");
    }
    
    /**
     * Validates color code (hex format)
     */
    public static boolean isValidColorCode(String colorCode) {
        if (isNullOrEmpty(colorCode)) return false;
        return colorCode.trim().matches("^#[0-9A-Fa-f]{6}$");
    }
}
