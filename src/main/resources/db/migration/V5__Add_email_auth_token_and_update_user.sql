-- 1. Create table email_auth_token
CREATE TABLE email_auth_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_email_auth_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 2. Add 'email' column to 'users' table (nullable for now to populate with random emails)
ALTER TABLE users ADD COLUMN email VARCHAR(255) UNIQUE;

-- 3. Populate 'email' column with unique placeholder values
UPDATE users
SET email = CONCAT(
    'user_', id, '_', FLOOR(RAND() * 100000), '@example.com'
)
WHERE email IS NULL OR email = '';

-- 4. Make 'email' column NOT NULL after population is done
ALTER TABLE users MODIFY email VARCHAR(255) NOT NULL UNIQUE;

-- 5. Add 'enabled' column with default value 'false' to indicate email verification status
ALTER TABLE users ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT FALSE;

-- 6. Drop uniqueness constraint from 'username' column (if it exists and needs to be removed)
ALTER TABLE users DROP INDEX UKr43af9ap4edm43mmtq01oddj6;