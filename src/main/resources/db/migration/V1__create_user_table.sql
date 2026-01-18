-- CREATE TABLE users (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,

--     username VARCHAR(50) NOT NULL,
--     password VARCHAR(255) NOT NULL,

--     email VARCHAR(100),
--     phone VARCHAR(20),

--     role VARCHAR(20) NOT NULL DEFAULT 'USER',

--     token_balance BIGINT NOT NULL DEFAULT 0,

--     status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

--     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

--     CONSTRAINT uk_users_username UNIQUE (username),
--     CONSTRAINT uk_users_email UNIQUE (email)
-- );