CREATE TABLE reservation (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             book_id BIGINT NOT NULL,
                             status ENUM('RESERVED', 'BORROWED', 'LATE', 'RETURNED') NOT NULL,
                             reservation_date DATETIME NOT NULL,
                             return_date DATETIME,
                             FOREIGN KEY (user_id) REFERENCES users(id),
                             FOREIGN KEY (book_id) REFERENCES books(id)
);