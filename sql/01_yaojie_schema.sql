DROP DATABASE IF EXISTS yaojie;
CREATE DATABASE yaojie CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE yaojie;

CREATE TABLE sys_user (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  real_name VARCHAR(64) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sys_user_username (username)
);

CREATE TABLE sys_role (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(64) NOT NULL,
  role_name VARCHAR(64) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sys_role_code (role_code)
);

CREATE TABLE sys_user_role (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  role_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sys_user_role (user_id, role_id),
  CONSTRAINT fk_sur_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_sur_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
);

CREATE TABLE medicine_category (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  category_name VARCHAR(64) NOT NULL,
  category_code VARCHAR(64) NOT NULL,
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_medicine_category_code (category_code)
);

CREATE TABLE supplier (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  supplier_name VARCHAR(128) NOT NULL,
  contact_person VARCHAR(64) NULL,
  phone VARCHAR(32) NULL,
  address VARCHAR(255) NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_supplier_name (supplier_name)
);

CREATE TABLE medicine (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  medicine_code VARCHAR(64) NOT NULL,
  medicine_name VARCHAR(128) NOT NULL,
  category_id BIGINT UNSIGNED NOT NULL,
  specification VARCHAR(128) NULL,
  unit VARCHAR(32) NOT NULL,
  manufacturer VARCHAR(128) NULL,
  supplier_id BIGINT UNSIGNED NULL,
  purchase_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  sale_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  safe_stock INT NOT NULL DEFAULT 0,
  expiry_warning_days INT NOT NULL DEFAULT 30,
  is_controlled TINYINT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_medicine_code (medicine_code),
  KEY idx_medicine_category_id (category_id),
  KEY idx_medicine_supplier_id (supplier_id),
  CONSTRAINT fk_medicine_category FOREIGN KEY (category_id) REFERENCES medicine_category(id),
  CONSTRAINT fk_medicine_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

CREATE TABLE purchase_order (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  supplier_id BIGINT UNSIGNED NOT NULL,
  operator_id BIGINT UNSIGNED NOT NULL,
  total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
  status VARCHAR(32) NOT NULL DEFAULT 'COMPLETED',
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_purchase_order_no (order_no),
  CONSTRAINT fk_purchase_order_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id),
  CONSTRAINT fk_purchase_order_operator FOREIGN KEY (operator_id) REFERENCES sys_user(id)
);

CREATE TABLE purchase_order_item (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  purchase_order_id BIGINT UNSIGNED NOT NULL,
  medicine_id BIGINT UNSIGNED NOT NULL,
  batch_no VARCHAR(64) NOT NULL,
  quantity INT NOT NULL,
  purchase_price DECIMAL(10, 2) NOT NULL,
  production_date DATE NOT NULL,
  expiry_date DATE NOT NULL,
  subtotal DECIMAL(12, 2) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_purchase_item_order_id (purchase_order_id),
  KEY idx_purchase_item_medicine_batch (medicine_id, batch_no),
  CONSTRAINT fk_purchase_item_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id),
  CONSTRAINT fk_purchase_item_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id)
);

CREATE TABLE sale_order (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  operator_id BIGINT UNSIGNED NOT NULL,
  total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
  status VARCHAR(32) NOT NULL DEFAULT 'COMPLETED',
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sale_order_no (order_no),
  CONSTRAINT fk_sale_order_operator FOREIGN KEY (operator_id) REFERENCES sys_user(id)
);

CREATE TABLE sale_order_item (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  sale_order_id BIGINT UNSIGNED NOT NULL,
  medicine_id BIGINT UNSIGNED NOT NULL,
  batch_no VARCHAR(64) NOT NULL,
  quantity INT NOT NULL,
  sale_price DECIMAL(10, 2) NOT NULL,
  subtotal DECIMAL(12, 2) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_sale_item_order_id (sale_order_id),
  KEY idx_sale_item_medicine_batch (medicine_id, batch_no),
  CONSTRAINT fk_sale_item_order FOREIGN KEY (sale_order_id) REFERENCES sale_order(id),
  CONSTRAINT fk_sale_item_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id)
);

CREATE TABLE inventory (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  medicine_id BIGINT UNSIGNED NOT NULL,
  batch_no VARCHAR(64) NOT NULL,
  current_quantity INT NOT NULL DEFAULT 0,
  locked_quantity INT NOT NULL DEFAULT 0,
  production_date DATE NOT NULL,
  expiry_date DATE NOT NULL,
  last_inbound_time DATETIME NULL,
  last_outbound_time DATETIME NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_inventory_medicine_batch (medicine_id, batch_no),
  KEY idx_inventory_expiry_date (expiry_date),
  CONSTRAINT fk_inventory_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id)
);

CREATE TABLE inventory_record (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  medicine_id BIGINT UNSIGNED NOT NULL,
  batch_no VARCHAR(64) NOT NULL,
  change_type ENUM('IN', 'OUT', 'ADJUST') NOT NULL,
  change_quantity INT NOT NULL,
  before_quantity INT NOT NULL,
  after_quantity INT NOT NULL,
  source_type ENUM('PURCHASE', 'SALE', 'CHECK', 'INIT') NOT NULL,
  source_id BIGINT UNSIGNED NULL,
  operator_id BIGINT UNSIGNED NULL,
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_inventory_record_medicine_batch (medicine_id, batch_no),
  KEY idx_inventory_record_source (source_type, source_id),
  CONSTRAINT fk_inventory_record_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id),
  CONSTRAINT fk_inventory_record_operator FOREIGN KEY (operator_id) REFERENCES sys_user(id)
);

CREATE TABLE warning_record (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  medicine_id BIGINT UNSIGNED NOT NULL,
  batch_no VARCHAR(64) NULL,
  warning_type ENUM('LOW_STOCK', 'EXPIRY_SOON', 'EXPIRED') NOT NULL,
  warning_level ENUM('INFO', 'WARN', 'CRITICAL') NOT NULL,
  warning_message VARCHAR(255) NOT NULL,
  status ENUM('OPEN', 'RESOLVED') NOT NULL DEFAULT 'OPEN',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  handled_at DATETIME NULL,
  KEY idx_warning_record_status_type (status, warning_type),
  CONSTRAINT fk_warning_record_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id)
);

CREATE TABLE operation_log (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NULL,
  module_name VARCHAR(64) NOT NULL,
  operation_type VARCHAR(64) NOT NULL,
  business_no VARCHAR(64) NULL,
  content VARCHAR(255) NOT NULL,
  ip VARCHAR(64) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_operation_log_user_id (user_id),
  CONSTRAINT fk_operation_log_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

DELIMITER $$

CREATE PROCEDURE sp_refresh_warnings()
BEGIN
  DELETE FROM warning_record WHERE status = 'OPEN';

  INSERT INTO warning_record (medicine_id, batch_no, warning_type, warning_level, warning_message, status, created_at)
  SELECT
    m.id,
    NULL,
    'LOW_STOCK',
    'WARN',
    CONCAT(m.medicine_name, ' total stock is below safe stock'),
    'OPEN',
    NOW()
  FROM medicine m
  LEFT JOIN (
    SELECT medicine_id, SUM(current_quantity) AS total_quantity
    FROM inventory
    WHERE expiry_date >= CURDATE()
    GROUP BY medicine_id
  ) s ON s.medicine_id = m.id
  WHERE m.status = 1
    AND IFNULL(s.total_quantity, 0) <= m.safe_stock;

  INSERT INTO warning_record (medicine_id, batch_no, warning_type, warning_level, warning_message, status, created_at)
  SELECT
    m.id,
    i.batch_no,
    'EXPIRED',
    'CRITICAL',
    CONCAT(m.medicine_name, ' batch ', i.batch_no, ' is expired'),
    'OPEN',
    NOW()
  FROM inventory i
  INNER JOIN medicine m ON m.id = i.medicine_id
  WHERE i.current_quantity > 0
    AND i.expiry_date < CURDATE();

  INSERT INTO warning_record (medicine_id, batch_no, warning_type, warning_level, warning_message, status, created_at)
  SELECT
    m.id,
    i.batch_no,
    'EXPIRY_SOON',
    'WARN',
    CONCAT(m.medicine_name, ' batch ', i.batch_no, ' will expire soon'),
    'OPEN',
    NOW()
  FROM inventory i
  INNER JOIN medicine m ON m.id = i.medicine_id
  WHERE i.current_quantity > 0
    AND i.expiry_date >= CURDATE()
    AND DATEDIFF(i.expiry_date, CURDATE()) <= m.expiry_warning_days;
END $$

CREATE PROCEDURE sp_purchase_inbound(
  IN p_order_no VARCHAR(64),
  IN p_supplier_id BIGINT UNSIGNED,
  IN p_operator_id BIGINT UNSIGNED,
  IN p_medicine_id BIGINT UNSIGNED,
  IN p_batch_no VARCHAR(64),
  IN p_quantity INT,
  IN p_purchase_price DECIMAL(10, 2),
  IN p_production_date DATE,
  IN p_expiry_date DATE,
  IN p_remark VARCHAR(255)
)
BEGIN
  DECLARE v_order_id BIGINT UNSIGNED;
  DECLARE v_before_quantity INT DEFAULT 0;
  DECLARE v_after_quantity INT DEFAULT 0;
  DECLARE v_is_controlled TINYINT DEFAULT 0;
  DECLARE v_has_permission INT DEFAULT 0;
  DECLARE v_subtotal DECIMAL(12, 2);

  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  IF p_quantity <= 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Inbound quantity must be greater than 0';
  END IF;

  IF p_expiry_date <= p_production_date THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Expiry date must be later than production date';
  END IF;

  SELECT is_controlled INTO v_is_controlled
  FROM medicine
  WHERE id = p_medicine_id;

  IF v_is_controlled IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Medicine does not exist';
  END IF;

  IF v_is_controlled = 1 THEN
    SELECT COUNT(1) INTO v_has_permission
    FROM sys_user_role ur
    INNER JOIN sys_role r ON r.id = ur.role_id
    WHERE ur.user_id = p_operator_id
      AND r.role_code IN ('ADMIN', 'PHARMACY_MANAGER', 'INVENTORY_MANAGER');

    IF v_has_permission = 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Current user cannot inbound controlled medicine';
    END IF;
  END IF;

  SET v_subtotal = p_quantity * p_purchase_price;

  START TRANSACTION;

  INSERT INTO purchase_order (order_no, supplier_id, operator_id, total_amount, status, remark)
  VALUES (p_order_no, p_supplier_id, p_operator_id, v_subtotal, 'COMPLETED', p_remark);

  SET v_order_id = LAST_INSERT_ID();

  INSERT INTO purchase_order_item (
    purchase_order_id, medicine_id, batch_no, quantity, purchase_price, production_date, expiry_date, subtotal
  ) VALUES (
    v_order_id, p_medicine_id, p_batch_no, p_quantity, p_purchase_price, p_production_date, p_expiry_date, v_subtotal
  );

  SELECT IFNULL(current_quantity, 0) INTO v_before_quantity
  FROM inventory
  WHERE medicine_id = p_medicine_id AND batch_no = p_batch_no;

  INSERT INTO inventory (
    medicine_id, batch_no, current_quantity, locked_quantity, production_date, expiry_date, last_inbound_time, updated_at
  ) VALUES (
    p_medicine_id, p_batch_no, p_quantity, 0, p_production_date, p_expiry_date, NOW(), NOW()
  )
  ON DUPLICATE KEY UPDATE
    current_quantity = current_quantity + VALUES(current_quantity),
    production_date = VALUES(production_date),
    expiry_date = VALUES(expiry_date),
    last_inbound_time = NOW(),
    updated_at = NOW();

  SELECT current_quantity INTO v_after_quantity
  FROM inventory
  WHERE medicine_id = p_medicine_id AND batch_no = p_batch_no;

  INSERT INTO inventory_record (
    medicine_id, batch_no, change_type, change_quantity, before_quantity, after_quantity, source_type, source_id, operator_id, remark
  ) VALUES (
    p_medicine_id, p_batch_no, 'IN', p_quantity, v_before_quantity, v_after_quantity, 'PURCHASE', v_order_id, p_operator_id, p_remark
  );

  INSERT INTO operation_log (user_id, module_name, operation_type, business_no, content)
  VALUES (p_operator_id, 'PURCHASE', 'INBOUND', p_order_no, CONCAT('Inbound success, batch=', p_batch_no, ', qty=', p_quantity));

  CALL sp_refresh_warnings();

  COMMIT;
END $$

CREATE PROCEDURE sp_sale_outbound(
  IN p_order_no VARCHAR(64),
  IN p_operator_id BIGINT UNSIGNED,
  IN p_medicine_id BIGINT UNSIGNED,
  IN p_batch_no VARCHAR(64),
  IN p_quantity INT,
  IN p_sale_price DECIMAL(10, 2),
  IN p_remark VARCHAR(255)
)
BEGIN
  DECLARE v_order_id BIGINT UNSIGNED;
  DECLARE v_before_quantity INT DEFAULT 0;
  DECLARE v_after_quantity INT DEFAULT 0;
  DECLARE v_is_controlled TINYINT DEFAULT 0;
  DECLARE v_expiry_date DATE;
  DECLARE v_has_permission INT DEFAULT 0;
  DECLARE v_subtotal DECIMAL(12, 2);

  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  IF p_quantity <= 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Sale quantity must be greater than 0';
  END IF;

  SELECT is_controlled INTO v_is_controlled
  FROM medicine
  WHERE id = p_medicine_id;

  IF v_is_controlled IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Medicine does not exist';
  END IF;

  SELECT current_quantity, expiry_date
    INTO v_before_quantity, v_expiry_date
  FROM inventory
  WHERE medicine_id = p_medicine_id
    AND batch_no = p_batch_no;

  IF v_before_quantity IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Inventory batch does not exist';
  END IF;

  IF v_expiry_date < CURDATE() THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Expired medicine cannot be sold';
  END IF;

  IF v_before_quantity < p_quantity THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient inventory';
  END IF;

  IF v_is_controlled = 1 THEN
    SELECT COUNT(1) INTO v_has_permission
    FROM sys_user_role ur
    INNER JOIN sys_role r ON r.id = ur.role_id
    WHERE ur.user_id = p_operator_id
      AND r.role_code IN ('ADMIN', 'PHARMACY_MANAGER');

    IF v_has_permission = 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Current user cannot sell controlled medicine';
    END IF;
  END IF;

  SET v_subtotal = p_quantity * p_sale_price;

  START TRANSACTION;

  INSERT INTO sale_order (order_no, operator_id, total_amount, status, remark)
  VALUES (p_order_no, p_operator_id, v_subtotal, 'COMPLETED', p_remark);

  SET v_order_id = LAST_INSERT_ID();

  INSERT INTO sale_order_item (
    sale_order_id, medicine_id, batch_no, quantity, sale_price, subtotal
  ) VALUES (
    v_order_id, p_medicine_id, p_batch_no, p_quantity, p_sale_price, v_subtotal
  );

  UPDATE inventory
  SET current_quantity = current_quantity - p_quantity,
      last_outbound_time = NOW(),
      updated_at = NOW()
  WHERE medicine_id = p_medicine_id
    AND batch_no = p_batch_no;

  SELECT current_quantity INTO v_after_quantity
  FROM inventory
  WHERE medicine_id = p_medicine_id
    AND batch_no = p_batch_no;

  INSERT INTO inventory_record (
    medicine_id, batch_no, change_type, change_quantity, before_quantity, after_quantity, source_type, source_id, operator_id, remark
  ) VALUES (
    p_medicine_id, p_batch_no, 'OUT', p_quantity, v_before_quantity, v_after_quantity, 'SALE', v_order_id, p_operator_id, p_remark
  );

  INSERT INTO operation_log (user_id, module_name, operation_type, business_no, content)
  VALUES (p_operator_id, 'SALE', 'OUTBOUND', p_order_no, CONCAT('Sale success, batch=', p_batch_no, ', qty=', p_quantity));

  CALL sp_refresh_warnings();

  COMMIT;
END $$

DELIMITER ;
