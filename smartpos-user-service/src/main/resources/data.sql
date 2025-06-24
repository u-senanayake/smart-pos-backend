--Run
--Role table
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('admin', 'Administrator role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('sales', 'Sale role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('delivery', 'Delivery role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('purchase', 'Purchase role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('accountant', 'Accountant role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('product_manager', 'Accountant role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);
INSERT INTO role (role_name, description, enabled, created_at, updated_at, deleted, deleted_at)
VALUES ('deleted', 'Deleted role', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, CURRENT_TIMESTAMP);

--User table
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('admin_user', 'Admin', 'User', 'admin.user@example.com', '123 Main St, City, Country',
        '1234567890', '0987654321', 1, '$2a$10$xu85axo.lRg.SgTmegtZj.ica94BKMnkvPmlFsmauxYZJ3fMsACCu',
        TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('sale_user', 'Sale', 'User', 'john.doe@example.com', '456 Elm St, City, Country', '1112223333', '3332221111', 2,
        '$2a$10$xu85axo.lRg.SgTmegtZj.ica94BKMnkvPmlFsmauxYZJ3fMsACCu', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('delivery_user', 'Delivery', 'User', 'jane.smith@example.com', '789 Oak St, City, Country', '4445556666',
        '6665554444', 3, '$2a$10$xu85axo.lRg.SgTmegtZj.ica94BKMnkvPmlFsmauxYZJ3fMsACCu', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('purchase_user', 'Purchase', 'User', 'peter.parker@example.com', '101 Maple St, City, Country', '7778889999',
        '9998887777', 4, '$2a$10$xu85axo.lRg.SgTmegtZj.ica94BKMnkvPmlFsmauxYZJ3fMsACCu', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('accountant_user', 'Accountant', 'User', 'bruce.wayne@example.com', '500 Gotham Ave, Gotham City', '2223334444',
        '4443332222', 5, '$2a$10$xu85axo.lRg.SgTmegtZj.ica94BKMnkvPmlFsmauxYZJ3fMsACCu', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('product_manager', 'Product', 'Manager', 'clark.kent@example.com', '600 Metropolis St, Metropolis',
        '5556667777',
        '7776665555', 6, '$2a$10$xu85axo.lRg.SgTmegtZj.ica94BKMnkvPmlFsmauxYZJ3fMsACCu', TRUE, FALSE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('diana_prince', 'Diana', 'Prince', 'diana.prince@example.com', '700 Amazon Ave, Paradise Island', '8889990000',
        '0009998888', 2, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('barry_allen', 'Barry', 'Allen', 'barry.allen@example.com', '800 Speedster Rd, Central City', '1231231234',
        '4324324321', 2, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('arthur_curry', 'Arthur', 'Curry', 'arthur.curry@example.com', '900 Ocean St, Atlantis', '9876543210',
        '0123456789', 3, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('selina_kyle', 'Selina', 'Kyle', 'selina.kyle@example.com', '200 Alley St, Gotham City', '7418529630',
        '3692581470', 2, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
INSERT INTO Users (username, first_name, last_name, email, address, phone_no_1, phone_no_2,
                   role_id, password, enabled, locked, created_at, updated_at,
                   created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id)
VALUES ('hal_jordan', 'Hal', 'Jordan', 'hal.jordan@example.com', '300 Lantern St, Coast City', '1593574862',
        '2583691470', 3, '$2a$10$abcdefghijklmnopqrstuv', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1,
        FALSE, NULL, NULL);
