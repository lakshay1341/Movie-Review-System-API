-- If roles table is empty, insert default roles
INSERT INTO roles (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');
INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

-- If admin user doesn't exist, create it
INSERT INTO users (user_name, password, role_id) 
SELECT 'admin', '$2a$12$ZFqD8mVpocAMPR.Tgrwkee2ChoY8wEpVCCKjOtRo1tX7KggSYb5Iq',
(SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin');

-- username = admin, password = password
