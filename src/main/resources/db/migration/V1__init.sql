-- Roles
CREATE TABLE role (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      name VARCHAR(50) UNIQUE NOT NULL
);

-- Users
CREATE TABLE "user_table" (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              username VARCHAR(50) UNIQUE NOT NULL,
                              password_hash VARCHAR(255) NOT NULL,
                              role_id UUID NOT NULL REFERENCES role(id)
);

-- Members
CREATE TABLE member (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        first_name VARCHAR(100) NOT NULL,
                        last_name VARCHAR(100) NOT NULL,
                        date_of_birth DATE NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        created_at TIMESTAMP DEFAULT now() NOT NULL,
                        updated_at TIMESTAMP DEFAULT now() NOT NULL
);
