-- Fellow Akademik Çalışma Yönetim Sistemi
-- Veritabanı Şeması (SQLite)

-- Kullanıcılar tablosu
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE,
    department TEXT,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Dersler tablosu
CREATE TABLE IF NOT EXISTS courses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    color_hex TEXT DEFAULT '#6366f1',
    user_id INTEGER NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Etkinlikler tablosu (Ders, Quiz, Ödev, Proje, Sınav)
CREATE TABLE IF NOT EXISTS events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    type TEXT NOT NULL CHECK(type IN ('LECTURE','QUIZ','HOMEWORK','PROJECT','EXAM')),
    course_id INTEGER NOT NULL,
    event_date DATE NOT NULL,
    event_time TEXT,
    description TEXT,
    user_id INTEGER NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Çalışma seansları tablosu (Pomodoro)
CREATE TABLE IF NOT EXISTS study_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_id INTEGER NOT NULL,
    topic TEXT,
    duration_seconds INTEGER NOT NULL,
    pomodoro_count INTEGER DEFAULT 0,
    session_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Çalışılacaklar tablosu (Todo)
CREATE TABLE IF NOT EXISTS todo_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_id INTEGER NOT NULL,
    topic TEXT NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT 0,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- İndeksler (Performans için)
CREATE INDEX IF NOT EXISTS idx_events_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_sessions_date ON study_sessions(session_date);
CREATE INDEX IF NOT EXISTS idx_sessions_course ON study_sessions(course_id);
CREATE INDEX IF NOT EXISTS idx_todos_course ON todo_items(course_id);