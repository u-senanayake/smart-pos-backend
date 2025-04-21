--Category table 
INSERT INTO Category (
    name, description, cat_prefix, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Electronics', 'Electronic items', 'E', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO Category (
    name, description, cat_prefix, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Clothing', 'Clothing items', 'C', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO Category (
    name, description, cat_prefix, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'DeletedCategory', 'Soft deleted category', 'D', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, TRUE, NULL, NULL
);

--Brand table
INSERT INTO Brand (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Nike', 'Sportswear brand', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO Brand (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Adidas', 'Another sportswear brand', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, FALSE, NULL, NULL
);
INSERT INTO Brand (
    name, description, enabled, created_at, updated_at, 
    created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'DeletedBrand', 'Soft deleted brand', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, TRUE, NULL, NULL
);

--Distributor table
INSERT INTO Distributor (
    company_name, email, phone_no_1, phone_no_2, address, enabled, 
    created_at, updated_at, created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Company A', 'companya@example.com', '1234567890', null, 
    'Address A', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, FALSE, NULL, NULL
);
INSERT INTO Distributor (
    company_name, email, phone_no_1, phone_no_2, address, enabled, 
    created_at, updated_at, created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Company B', 'companyb@example.com', '0987654321', null, 
    'Address B', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, FALSE, NULL, NULL
);
INSERT INTO Distributor (
    company_name, email, phone_no_1, phone_no_2, address, enabled, 
    created_at, updated_at, created_user_id, updated_user_id, deleted, deleted_at, deleted_user_id
) VALUES (
    'Company C', 'companyc@example.com', '0987654321', null, 
    'Address C', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
    1, 1, TRUE, NULL, NULL
);

--product table
INSERT INTO Product (
    product_id, sku, product_name, description, category_id, distributor_id, 
    price, cost_price, min_price, manufacture_date, expire_date, 
    enabled, created_at, updated_at, created_user_id, updated_user_id, 
    deleted, deleted_at, deleted_user_id
) VALUES 
(
    'P001', 'SKU001', 'Product 1', 'Description 1', 1, 1, 
    200.00, 150.00, 180.00, '2024-01-10', '2027-01-10', 
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
    'P002', 'SKU002', 'Product 2', 'Description 2', 1, 1, 
    200.00, 150.00, 180.00, '2024-01-10', '2027-01-10', 
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
    'P003', 'SKU003', 'Deleted Product', 'Deleted Description', 1, 1, 
    300.00, 250.00, 280.00, '2024-01-10', '2027-01-10', 
    TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 
    TRUE, NULL, NULL
);