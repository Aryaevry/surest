-- V1__init.sql

-- 1️⃣ Role table
CREATE TABLE IF NOT EXISTS role (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
    );

-- 2️⃣ User table
CREATE TABLE IF NOT EXISTS user_table (
                                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id UUID NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id)
    );

-- 3️⃣ Member table
CREATE TABLE IF NOT EXISTS member (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
    );
