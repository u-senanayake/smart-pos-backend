--Sales table
INSERT INTO Sales (
    user_id, total_amount, total_item_count, sale_date_time, customer_id, payment_status, created_at, updated_at
) VALUES 
(
    1, 150000.00, 3, CURRENT_TIMESTAMP, 1, 'FINALIZED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
INSERT INTO Sales (
    user_id, total_amount, total_item_count, sale_date_time, customer_id, payment_status, created_at, updated_at
) VALUES 
(
    1, 80000.00, 2, CURRENT_TIMESTAMP, 2, 'DRAFT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
INSERT INTO Sales (
    user_id, total_amount, total_item_count, sale_date_time, customer_id, payment_status, created_at, updated_at
) VALUES 
(
    1, 50000.00, 1, CURRENT_TIMESTAMP, 3, 'FINALIZED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
INSERT INTO Sales (
    user_id, total_amount, total_item_count, sale_date_time, customer_id, payment_status, created_at, updated_at
) VALUES 
(
    1, 50000.00, 1, CURRENT_TIMESTAMP, 4, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
--Sales items table
INSERT INTO sales_items (
    sale_id, product_id, quantity, returned_quantity, price_per_unit, item_discount_val, item_discount_per, total_price
) VALUES 
(
    1, 1, 1, 0, 120000.00, 5000.00, 5, 115000.00
);
INSERT INTO sales_items (
    sale_id, product_id, quantity, returned_quantity, price_per_unit, item_discount_val, item_discount_per, total_price
) VALUES 
(
    1, 2, 2, 0, 15000.00, 1000.00, 3, 29000.00
);
INSERT INTO sales_items (
    sale_id, product_id, quantity, returned_quantity, price_per_unit, item_discount_val, item_discount_per, total_price
) VALUES 
(
    2, 3, 1, 0, 80000.00, 0, 0, 80000.00
);
INSERT INTO sales_items (
    sale_id, product_id, quantity, returned_quantity, price_per_unit, item_discount_val, item_discount_per, total_price
) VALUES 
(
    3, 4, 1, 0, 50000.00, 0, 0, 50000.00
);

--Payment table
INSERT INTO Payment (
    sale_id, cash_amount, ccard_amount, ccard_ref, qr_amount, qr_ref, cheque_amount, cheque_ref, due_amount
) VALUES 
(
    1, 50000.00, 65000.00, 'CC-REF123', NULL, NULL, 0, NULL, 0
);
INSERT INTO Payment (
    sale_id, cash_amount, ccard_amount, ccard_ref, qr_amount, qr_ref, cheque_amount, cheque_ref, due_amount
) VALUES
(
    2, 20000.00, 40000.00, 'CC-REF456', 20000.00, 'QR-REF789', NULL, NULL, 0
);
INSERT INTO Payment (
    sale_id, cash_amount, ccard_amount, ccard_ref, qr_amount, qr_ref, cheque_amount, cheque_ref, due_amount
) VALUES
(
    3, 50000.00, NULL, NULL, NULL, NULL, NULL, NULL, 0
);
