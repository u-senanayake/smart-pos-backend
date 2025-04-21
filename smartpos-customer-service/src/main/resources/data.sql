--Customer group table
INSERT INTO CustomerGroup (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES 
(
    'VIP Customers', 'Exclusive group for high-value customers', TRUE, 
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO CustomerGroup (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES 
(
    'Wholesale Buyers', 'Customers who buy in bulk with special discounts', TRUE, 
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO CustomerGroup (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES 
(
    'Retail Customers', 'Regular customers purchasing at standard rates', TRUE, 
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO CustomerGroup (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES 
(
    'Loyalty Members', 'Customers enrolled in our loyalty program', TRUE, 
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO CustomerGroup (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES 
(
    'Corporate Clients', 'Businesses with corporate purchase agreements', TRUE, 
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);

--Customer table
INSERT INTO Customer (
    customer_group_id, username, first_name, last_name, email, phone_no_1, address, 
    enabled, locked, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES 
(
    1, 'john_doe', 'John', 'Doe', 'john.doe@example.com', '1234567890', 
    '123 Main St, Tokyo, Japan', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, FALSE, NULL, NULL
);
INSERT INTO Customer (
    customer_group_id, username, first_name, last_name, email, phone_no_1, address, 
    enabled, locked, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES 
(
    2, 'jane_smith', 'Jane', 'Smith', 'jane.smith@example.com', '9876543210', 
    '456 Business Rd, Osaka, Japan', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, FALSE, NULL, NULL
);
INSERT INTO Customer (
    customer_group_id, username, first_name, last_name, email, phone_no_1, address, 
    enabled, locked, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES 
(
    3, 'michael_brown', 'Michael', 'Brown', 'michael.brown@example.com', '5558889999', 
    '789 Residential St, Kyoto, Japan', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, FALSE, NULL, NULL
);
INSERT INTO Customer (
    customer_group_id, username, first_name, last_name, email, phone_no_1, address, 
    enabled, locked, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES 
(
    4, 'emily_white', 'Emily', 'White', 'emily.white@example.com', '2223334444', 
    '101 Luxury Ave, Sapporo, Japan', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, FALSE, NULL, NULL
);
INSERT INTO Customer (
    customer_group_id, username, first_name, last_name, email, phone_no_1, address, 
    enabled, locked, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES 
(
    5, 'william_clark', 'William', 'Clark', 'william.clark@example.com', '7776665555', 
    '202 Corporate Blvd, Nagoya, Japan', TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, FALSE, NULL, NULL
);
