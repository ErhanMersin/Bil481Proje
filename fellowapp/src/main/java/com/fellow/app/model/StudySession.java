package com.fellow.app.model;

import java.time.LocalDateTime;

public class StudySession {
    private int id;
    private int courseId;
    private String topic;
    private int durationSeconds;
    private int pomodoroCount;
    private LocalDateTime sessionDate;
    private int userId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    
    public int getPomodoroCount() { return pomodoroCount; }
    public void setPomodoroCount(int pomodoroCount) { this.pomodoroCount = pomodoroCount; }
    
    public LocalDateTime getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDateTime sessionDate) { this.sessionDate = sessionDate; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
