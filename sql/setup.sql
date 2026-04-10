CREATE DATABASE IF NOT EXISTS password_db;
USE password_db;

CREATE TABLE IF NOT EXISTS passwords (
    id INT AUTO_INCREMENT PRIMARY KEY,
    website VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    encrypted_password TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Note:
-- This script only creates the table.
-- The app seeds decryptable demo data using your current login master password
-- (see Vault -> "Load Demo: School/TestSite").
