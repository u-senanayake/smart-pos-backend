--Run
--Role table
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('Admin', 'Administrator role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('User', 'User role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('Sales Associate', 'Sales Associate role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('Inventory Manager', 'Inventory Manager role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('Customer Service', 'Customer Service role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('Deleted', 'Deleted role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP);

--User table
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'admin_user', 'Admin', 'User', 'admin.user@example.com', '123 Main St, City, Country', 
    '1234567890', '0987654321', 1, '$2a$10$abcdefghijklmnopqrstuv', 
    TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('john_doe', 'John', 'Doe', 'john.doe@example.com', '456 Elm St, City, Country', '1112223333', '3332221111', 2, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('jane_smith', 'Jane', 'Smith', 'jane.smith@example.com', '789 Oak St, City, Country', '4445556666', '6665554444', 3, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('peter_parker', 'Peter', 'Parker', 'peter.parker@example.com', '101 Maple St, City, Country', '7778889999', '9998887777', 2, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('bruce_wayne', 'Bruce', 'Wayne', 'bruce.wayne@example.com', '500 Gotham Ave, Gotham City', '2223334444', '4443332222', 1, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('clark_kent', 'Clark', 'Kent', 'clark.kent@example.com', '600 Metropolis St, Metropolis', '5556667777', '7776665555', 3, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('diana_prince', 'Diana', 'Prince', 'diana.prince@example.com', '700 Amazon Ave, Paradise Island', '8889990000', '0009998888', 2, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('barry_allen', 'Barry', 'Allen', 'barry.allen@example.com', '800 Speedster Rd, Central City', '1231231234', '4324324321', 2, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('arthur_curry', 'Arthur', 'Curry', 'arthur.curry@example.com', '900 Ocean St, Atlantis', '9876543210', '0123456789', 3, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('selina_kyle', 'Selina', 'Kyle', 'selina.kyle@example.com', '200 Alley St, Gotham City', '7418529630', '3692581470', 2, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES
('hal_jordan', 'Hal', 'Jordan', 'hal.jordan@example.com', '300 Lantern St, Coast City', '1593574862', '2583691470', 3, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
