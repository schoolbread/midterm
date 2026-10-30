CREATE DATABASE IF NOT EXISTS college_db;
USE college_db;
CREATE TABLE IF NOT EXISTS students (
  id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  course VARCHAR(100) NOT NULL,
  year_level TINYINT NOT NULL DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO students (full_name, email, course, year_level) VALUES
('Juan Dela Cruz','juan@college.edu','BS Information Technology',2),
('Maria Santos','maria@college.edu','BS Education',1);
