-- Initial schema for learning_goals to match JPA entity
CREATE TABLE IF NOT EXISTS learning_goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    daily_target_minutes INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Minimal users table placeholder if not exists (foreign key optional depending on existing schema)
-- ALTER TABLE learning_goals ADD CONSTRAINT fk_learning_goals_user FOREIGN KEY (user_id) REFERENCES users(id);


