USE yaojie;

CREATE TABLE IF NOT EXISTS inventory_check (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  check_no VARCHAR(64) NOT NULL,
  status ENUM('DRAFT', 'EXECUTED', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
  remark VARCHAR(255) NULL,
  created_by BIGINT UNSIGNED NOT NULL,
  executed_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  executed_at DATETIME NULL,
  UNIQUE KEY uk_inventory_check_no (check_no),
  CONSTRAINT fk_inventory_check_created_by FOREIGN KEY (created_by) REFERENCES sys_user(id),
  CONSTRAINT fk_inventory_check_executed_by FOREIGN KEY (executed_by) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS inventory_check_item (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  check_id BIGINT UNSIGNED NOT NULL,
  medicine_id BIGINT UNSIGNED NOT NULL,
  batch_no VARCHAR(64) NOT NULL,
  system_quantity INT NOT NULL,
  actual_quantity INT NOT NULL,
  difference_quantity INT NOT NULL,
  reason VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_inventory_check_item_check_id (check_id),
  CONSTRAINT fk_inventory_check_item_check FOREIGN KEY (check_id) REFERENCES inventory_check(id),
  CONSTRAINT fk_inventory_check_item_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id)
);
