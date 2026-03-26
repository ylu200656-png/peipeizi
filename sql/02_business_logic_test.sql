USE yaojie;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE operation_log;
TRUNCATE TABLE warning_record;
TRUNCATE TABLE inventory_record;
TRUNCATE TABLE inventory;
TRUNCATE TABLE sale_order_item;
TRUNCATE TABLE sale_order;
TRUNCATE TABLE purchase_order_item;
TRUNCATE TABLE purchase_order;
TRUNCATE TABLE medicine;
TRUNCATE TABLE supplier;
TRUNCATE TABLE medicine_category;
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE sys_user;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO sys_role (role_code, role_name) VALUES
('ADMIN', '系统管理员'),
('PHARMACY_MANAGER', '药房管理员'),
('INVENTORY_MANAGER', '库存管理员'),
('SALES_CLERK', '销售员');

INSERT INTO sys_user (username, password_hash, real_name, status) VALUES
('admin', '$2a$10$dPpBEtp.icfkXCIFwrmIFO5d9u4V5EWYlqCIh5wZonKao8mV2HXOO', '系统管理员', 1),
('manager', '$2a$10$dPpBEtp.icfkXCIFwrmIFO5d9u4V5EWYlqCIh5wZonKao8mV2HXOO', '药房管理员', 1),
('stocker', '$2a$10$dPpBEtp.icfkXCIFwrmIFO5d9u4V5EWYlqCIh5wZonKao8mV2HXOO', '库存管理员', 1),
('clerk', '$2a$10$dPpBEtp.icfkXCIFwrmIFO5d9u4V5EWYlqCIh5wZonKao8mV2HXOO', '销售员', 1);

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r
WHERE (u.username = 'admin' AND r.role_code = 'ADMIN')
   OR (u.username = 'manager' AND r.role_code = 'PHARMACY_MANAGER')
   OR (u.username = 'stocker' AND r.role_code = 'INVENTORY_MANAGER')
   OR (u.username = 'clerk' AND r.role_code = 'SALES_CLERK');

INSERT INTO medicine_category (category_name, category_code, remark) VALUES
('抗生素类', 'ANTIBIOTIC', '普通药品分类'),
('管制药品类', 'CONTROLLED', '管制药品分类');

INSERT INTO supplier (supplier_name, contact_person, phone, address, status) VALUES
('重庆示例药业有限公司', '张敏', '13800000001', '重庆市渝中区示例路 1 号', 1),
('西南管制药品供应中心', '李强', '13800000002', '重庆市沙坪坝区示例路 2 号', 1);

INSERT INTO medicine (
  medicine_code, medicine_name, category_id, specification, unit, manufacturer, supplier_id,
  purchase_price, sale_price, safe_stock, expiry_warning_days, is_controlled, status, remark
) VALUES
(
  'MED-001', '阿莫西林胶囊', 1, '0.25g*24', '盒', '重庆制药厂', 1,
  12.50, 18.00, 20, 30, 0, 1, '普通药品'
),
(
  'MED-002', '吗啡片', 2, '10mg*10', '盒', '西南制药厂', 2,
  40.00, 58.00, 5, 60, 1, 1, '管制药品'
);

CALL sp_purchase_inbound(
  'PO-001', 1, 3, 1, 'AMX-BATCH-001', 50, 12.50,
  DATE_SUB(CURDATE(), INTERVAL 20 DAY),
  DATE_ADD(CURDATE(), INTERVAL 365 DAY),
  'Normal medicine inbound'
);

CALL sp_purchase_inbound(
  'PO-002', 1, 3, 1, 'AMX-BATCH-002', 10, 12.50,
  DATE_SUB(CURDATE(), INTERVAL 60 DAY),
  DATE_ADD(CURDATE(), INTERVAL 15 DAY),
  'Near-expiry batch inbound'
);

CALL sp_purchase_inbound(
  'PO-003', 2, 2, 2, 'MOR-BATCH-001', 8, 40.00,
  DATE_SUB(CURDATE(), INTERVAL 10 DAY),
  DATE_ADD(CURDATE(), INTERVAL 180 DAY),
  'Controlled medicine inbound'
);

CALL sp_purchase_inbound(
  'PO-004', 1, 3, 1, 'AMX-BATCH-003', 3, 12.50,
  DATE_SUB(CURDATE(), INTERVAL 400 DAY),
  DATE_SUB(CURDATE(), INTERVAL 1 DAY),
  'Expired batch inbound'
);

CALL sp_sale_outbound(
  'SO-001', 4, 1, 'AMX-BATCH-001', 35, 18.00, 'Normal medicine sale'
);

CALL sp_sale_outbound(
  'SO-002', 2, 2, 'MOR-BATCH-001', 2, 58.00, 'Controlled medicine sale'
);

CALL sp_sale_outbound(
  'SO-003', 4, 1, 'AMX-BATCH-002', 6, 18.00, 'Near-expiry batch sale'
);

DELIMITER $$

CREATE PROCEDURE sp_test_expected_controlled_sale_denied()
BEGIN
  DECLARE v_had_error TINYINT DEFAULT 0;

  DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
  BEGIN
    SET v_had_error = 1;
    INSERT INTO operation_log (user_id, module_name, operation_type, business_no, content)
    VALUES (4, 'SALE', 'EXPECTED_DENY', 'SO-EXPECTED-DENY', 'Expected denial for controlled medicine sale');
  END;

  CALL sp_sale_outbound(
    'SO-EXPECTED-DENY', 4, 2, 'MOR-BATCH-001', 1, 58.00, 'Sales clerk tries controlled medicine sale'
  );

  IF v_had_error = 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Expected permission denial did not happen';
  END IF;
END $$

DELIMITER ;

CALL sp_test_expected_controlled_sale_denied();
DROP PROCEDURE sp_test_expected_controlled_sale_denied;

CALL sp_refresh_warnings();

SELECT 'USERS' AS section;
SELECT u.id, u.username, u.real_name, GROUP_CONCAT(r.role_code ORDER BY r.role_code) AS roles
FROM sys_user u
LEFT JOIN sys_user_role ur ON ur.user_id = u.id
LEFT JOIN sys_role r ON r.id = ur.role_id
GROUP BY u.id, u.username, u.real_name
ORDER BY u.id;

SELECT 'INVENTORY' AS section;
SELECT
  m.medicine_name,
  m.is_controlled,
  i.batch_no,
  i.current_quantity,
  m.safe_stock,
  i.production_date,
  i.expiry_date
FROM inventory i
INNER JOIN medicine m ON m.id = i.medicine_id
ORDER BY m.id, i.batch_no;

SELECT 'WARNINGS' AS section;
SELECT
  m.medicine_name,
  wr.batch_no,
  wr.warning_type,
  wr.warning_level,
  wr.warning_message,
  wr.status
FROM warning_record wr
INNER JOIN medicine m ON m.id = wr.medicine_id
ORDER BY wr.warning_type, m.id, wr.batch_no;

SELECT 'PURCHASE_ORDERS' AS section;
SELECT order_no, supplier_id, operator_id, total_amount, status, created_at
FROM purchase_order
ORDER BY id;

SELECT 'SALE_ORDERS' AS section;
SELECT order_no, operator_id, total_amount, status, created_at
FROM sale_order
ORDER BY id;

SELECT 'INVENTORY_RECORDS' AS section;
SELECT
  ir.id,
  m.medicine_name,
  ir.batch_no,
  ir.change_type,
  ir.change_quantity,
  ir.before_quantity,
  ir.after_quantity,
  ir.source_type,
  ir.created_at
FROM inventory_record ir
INNER JOIN medicine m ON m.id = ir.medicine_id
ORDER BY ir.id;

SELECT 'OPERATION_LOGS' AS section;
SELECT id, user_id, module_name, operation_type, business_no, content, created_at
FROM operation_log
ORDER BY id;
