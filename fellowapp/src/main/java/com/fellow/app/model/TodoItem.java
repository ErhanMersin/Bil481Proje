package com.fellow.app.model;

import java.time.LocalDateTime;

public class TodoItem {
    private int id;
    private int courseId;
    private int userId;
    private String topic;
    private String description;
    private boolean completed;
    private LocalDateTime createdDate;

    public TodoItem(int courseId, int userId, String topic, String description) {
        this.courseId = courseId;
        this.userId = userId;
        this.topic = topic;
        this.description = description;
        this.completed = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}
