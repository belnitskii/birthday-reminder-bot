ALTER TABLE person
ADD COLUMN user_id BIGINT NOT NULL AFTER birthday_date;

ALTER TABLE person
ADD CONSTRAINT fk_person_user
FOREIGN KEY (user_id) REFERENCES users(id);