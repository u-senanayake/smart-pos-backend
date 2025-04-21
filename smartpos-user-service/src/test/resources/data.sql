--Role table
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('Admin', 'Administrator role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('User', 'User role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('DeletedRole', 'Soft deleted role', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, NULL);

--User table
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'user1', 'User', 'One', 'user1@example.com', 'Address1', 
    '1234567890', null, 1, '$2a$10$abcdefghijklmnopqrstuv', 
    TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'user2', 'User', 'Two', 'user2@example.com', 'Address2', 
    '1234567890', null, 2, '$2a$10$abcdefghijklmnopqrstuv', 
    TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO Users (
    username, first_name, last_name, email, address, phone_no_1, phone_no_2, 
    role_id, password, enabled, locked, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'admin_user', 'Admin', 'User', 'admin@example.com', 'Admin Address', 
    '1234567890', null, 1, '$2a$10$abcdefghijklmnopqrstuv', 
    TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
