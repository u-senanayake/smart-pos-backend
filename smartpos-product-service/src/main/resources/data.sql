--Category table 
INSERT INTO Category (
    name, description, cat_prefix, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Electronics', 'Category for electronic devices and gadgets', 'E', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);

--Brand table
INSERT INTO Brand (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Samsung', 'A leading brand in electronics and home appliances', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);

--Distributor table
INSERT INTO Distributor (
    company_name, email, phone_no_1, phone_no_2, address, enabled, 
    created_at, updated_at, created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Tech Distributors Ltd.', 'info@techdistributors.com', '123-456-7890', '098-765-4321', 
    '456 Business Park, Tokyo, Japan', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, FALSE, NULL, NULL
);

--product table
INSERT INTO Product (
    product_id, sku, product_name, description, category_id, distributor_id, 
    price, cost_price, min_price, manufacture_date, expire_date, 
    enabled, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES 
(
    'E0001', 'SKU-1001', 'Samsung Galaxy S24', 'Latest Samsung smartphone', 1, 1, 
    120000.00, 95000.00, 110000.00, '2024-01-10', '2027-01-10', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 
    FALSE, NULL, NULL
);
INSERT INTO Product (
    product_id, sku, product_name, description, category_id, distributor_id, 
    price, cost_price, min_price, manufacture_date, expire_date, 
    enabled, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES
(
    'E0002', 'SKU-1002', 'Sony WH-1000XM5', 'Noise-canceling wireless headphones', 1, 1, 
    45000.00, 35000.00, 40000.00, '2023-12-01', '2026-12-01', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 
    FALSE, NULL, NULL
);
INSERT INTO Product (
    product_id, sku, product_name, description, category_id, distributor_id, 
    price, cost_price, min_price, manufacture_date, expire_date, 
    enabled, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES
(
    'E0003', 'SKU-1003', 'Apple MacBook Pro 14"', 'Apple M3 Pro 14-inch laptop', 1, 1, 
    280000.00, 230000.00, 260000.00, '2024-02-15', '2028-02-15', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 
    FALSE, NULL, NULL
);
INSERT INTO Product (
    product_id, sku, product_name, description, category_id, distributor_id, 
    price, cost_price, min_price, manufacture_date, expire_date, 
    enabled, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES
(
    'E0004', 'SKU-1004', 'Logitech MX Master 3', 'Ergonomic wireless mouse', 1, 1, 
    15000.00, 10000.00, 12000.00, '2023-10-05', '2026-10-05', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 
    FALSE, NULL, NULL
);
INSERT INTO Product (
    product_id, sku, product_name, description, category_id, distributor_id, 
    price, cost_price, min_price, manufacture_date, expire_date, 
    enabled, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES
(
    'E0005', 'SKU-1005', 'Dell UltraSharp 27"', 'High-resolution 4K monitor', 1, 1, 
    80000.00, 65000.00, 75000.00, '2024-01-20', '2027-01-20', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 
    FALSE, NULL, NULL
);

--Inventory table
INSERT INTO Inventory (product_id, quantity, stock_alert_level, stock_warning_level, last_updated) 
VALUES (1, 50, 10, 20, CURRENT_TIMESTAMP);

INSERT INTO Inventory( product_id, quantity, stock_alert_level, stock_warning_level, last_updated
) VALUES (2, 30, 5, 10, CURRENT_TIMESTAMP );

INSERT INTO Inventory( product_id, quantity, stock_alert_level, stock_warning_level, last_updated
) VALUES (3, 20, 3, 7, CURRENT_TIMESTAMP );

INSERT INTO Inventory(product_id, quantity, stock_alert_level, stock_warning_level, last_updated
) VALUES (4, 100, 15, 30, CURRENT_TIMESTAMP);

INSERT INTO Inventory(product_id, quantity, stock_alert_level, stock_warning_level, last_updated
) VALUES (5, 40, 8, 15, CURRENT_TIMESTAMP );
