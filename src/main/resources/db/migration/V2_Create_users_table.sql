CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20),
    PRIMARY KEY (id),
    UNIQUE KEY (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;