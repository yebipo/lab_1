CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL,
                                     level INT DEFAULT 1,
                                     level_url VARCHAR(255),
                                     daily_goal_minutes INT NOT NULL,
                                     avatar_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL,
                                          color VARCHAR(50),
                                          description TEXT,
                                          icon_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS skills (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL,
                                      icon_url VARCHAR(255),
                                      category_id BIGINT REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS tasks (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
                                     description TEXT,
                                     focus_score INT DEFAULT 0,
                                     status VARCHAR(50) DEFAULT 'TODO',
                                     user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS task_skills (
                                           task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
                                           skill_id BIGINT REFERENCES skills(id) ON DELETE CASCADE,
                                           PRIMARY KEY (task_id, skill_id)
);

CREATE TABLE IF NOT EXISTS work_logs (
                                         id BIGSERIAL PRIMARY KEY,
                                         duration_minutes INT NOT NULL,
                                         comment TEXT,
                                         interruption_count INT DEFAULT 0,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE
);