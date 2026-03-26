USE yaojie;

SET @sql = IF(
    (SELECT COUNT(1) FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'warning_record'
          AND COLUMN_NAME = 'handled_by') = 0,
    'ALTER TABLE warning_record ADD COLUMN handled_by BIGINT UNSIGNED NULL AFTER handled_at',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(1) FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'warning_record'
          AND COLUMN_NAME = 'handle_remark') = 0,
    'ALTER TABLE warning_record ADD COLUMN handle_remark VARCHAR(255) NULL AFTER handled_by',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(1) FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'warning_record'
          AND INDEX_NAME = 'idx_warning_record_handled_by') = 0,
    'ALTER TABLE warning_record ADD KEY idx_warning_record_handled_by (handled_by)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(1) FROM information_schema.REFERENTIAL_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'warning_record'
          AND CONSTRAINT_NAME = 'fk_warning_record_handler') = 0,
    'ALTER TABLE warning_record ADD CONSTRAINT fk_warning_record_handler FOREIGN KEY (handled_by) REFERENCES sys_user(id)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
