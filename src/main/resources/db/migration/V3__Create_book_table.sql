CREATE TABLE books (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       publication_year INT,
                       total_copies INT NOT NULL,
                       available_copies INT NOT NULL,
                       picture_url VARCHAR(255)
);