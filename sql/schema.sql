CREATE DATABASE IF NOT EXISTS dropx DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dropx;

CREATE TABLE dx_content
(
    content_id INT AUTO_INCREMENT NOT NULL,
    extract_code CHAR(5) NOT NULL,
    content_type VARCHAR(10) NOT NULL,
    object_name VARCHAR(255) NOT NULL,
    status CHAR(1) NOT NULL DEFAULT 'Y',
    expire_at DATETIME NOT NULL,
    create_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (content_id)
);

CREATE INDEX dx_content_extract_code ON dx_content(extract_code);
CREATE INDEX dx_content_expire_at ON dx_content(expire_at);
