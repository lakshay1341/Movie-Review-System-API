-- First insert roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_USER');

-- Then users (with proper role_id reference)
INSERT INTO users (user_name, password, role_id)
VALUES (
    'admin',
    '$2a$12$OClVWq0Kanqu/zdgF.UtnORDOPPVX3N3PtzdkRlpnmQ0ShQXd1TI.',
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
);
-- password: admin123

-- Reset auto-increment after initial data
ALTER TABLE users AUTO_INCREMENT = 2;