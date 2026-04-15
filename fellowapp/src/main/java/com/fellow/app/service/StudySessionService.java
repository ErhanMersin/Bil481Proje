package com.fellow.app.service;

import com.fellow.app.dao.StudySessionDAO;

public class StudySessionService {
    private final StudySessionDAO sessionDAO = new StudySessionDAO();

    public boolean addSession(int courseId, int userId, int durationSeconds, int pomodoroCount) {
        return sessionDAO.addSession(courseId, userId, durationSeconds, pomodoroCount);
    }
}
