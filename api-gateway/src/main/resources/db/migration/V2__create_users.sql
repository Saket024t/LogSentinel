CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Passwords are 'password' encoded using BCrypt
INSERT INTO users (username, password_hash, role) VALUES
('viewer', '$2a$10$Oq.qWJ6I40Y3y1oF/45K0ONM1Zp1R1q1.w2r/O2G/G5W7lXW3h/u2', 'ROLE_VIEWER'),
('operator', '$2a$10$Oq.qWJ6I40Y3y1oF/45K0ONM1Zp1R1q1.w2r/O2G/G5W7lXW3h/u2', 'ROLE_OPERATOR'),
('admin', '$2a$10$Oq.qWJ6I40Y3y1oF/45K0ONM1Zp1R1q1.w2r/O2G/G5W7lXW3h/u2', 'ROLE_ADMIN');
