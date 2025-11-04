CREATE TABLE user_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    type_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO user_type (type_name) VALUES ('Admin'), ('User');