INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');
INSERT INTO users (id, user_name, password, role_id)
VALUES (1, 'admin', '$2a$12$KtR5X8nZ6pW3vJ9mQ4sF2eQzL7uY0iB1oTjHdGfA8rNxM2wKpVcSa', 1);
-- password: admin123
ALTER TABLE users AUTO_INCREMENT = 2;