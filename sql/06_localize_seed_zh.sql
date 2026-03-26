USE yaojie;

UPDATE sys_role
SET role_name = CASE role_code
    WHEN 'ADMIN' THEN '系统管理员'
    WHEN 'PHARMACY_MANAGER' THEN '药房管理员'
    WHEN 'INVENTORY_MANAGER' THEN '库存管理员'
    WHEN 'SALES_CLERK' THEN '销售员'
    ELSE role_name
END
WHERE role_code IN ('ADMIN', 'PHARMACY_MANAGER', 'INVENTORY_MANAGER', 'SALES_CLERK');

UPDATE sys_user
SET real_name = CASE username
    WHEN 'admin' THEN '系统管理员'
    WHEN 'manager' THEN '药房管理员'
    WHEN 'stocker' THEN '库存管理员'
    WHEN 'clerk' THEN '销售员'
    ELSE real_name
END
WHERE username IN ('admin', 'manager', 'stocker', 'clerk');

UPDATE medicine_category
SET category_name = CASE category_code
    WHEN 'ANTIBIOTIC' THEN '抗生素类'
    WHEN 'CONTROLLED' THEN '管制药品类'
    ELSE category_name
END,
remark = CASE category_code
    WHEN 'ANTIBIOTIC' THEN '普通药品分类'
    WHEN 'CONTROLLED' THEN '管制药品分类'
    ELSE remark
END
WHERE category_code IN ('ANTIBIOTIC', 'CONTROLLED');

UPDATE supplier
SET supplier_name = CASE
    WHEN supplier_name = 'Chongqing Demo Pharma' THEN '重庆示例药业有限公司'
    WHEN supplier_name = 'Southwest Controlled Supply' THEN '西南管制药品供应中心'
    ELSE supplier_name
END,
contact_person = CASE
    WHEN supplier_name IN ('Chongqing Demo Pharma', '重庆示例药业有限公司') THEN '张敏'
    WHEN supplier_name IN ('Southwest Controlled Supply', '西南管制药品供应中心') THEN '李强'
    ELSE contact_person
END,
address = CASE
    WHEN supplier_name IN ('Chongqing Demo Pharma', '重庆示例药业有限公司') THEN '重庆市渝中区示例路 1 号'
    WHEN supplier_name IN ('Southwest Controlled Supply', '西南管制药品供应中心') THEN '重庆市沙坪坝区示例路 2 号'
    ELSE address
END;

UPDATE medicine
SET medicine_name = CASE medicine_code
    WHEN 'MED-001' THEN '阿莫西林胶囊'
    WHEN 'MED-002' THEN '吗啡片'
    ELSE medicine_name
END,
unit = '盒',
manufacturer = CASE medicine_code
    WHEN 'MED-001' THEN '重庆制药厂'
    WHEN 'MED-002' THEN '西南制药厂'
    ELSE manufacturer
END,
remark = CASE medicine_code
    WHEN 'MED-001' THEN '普通药品'
    WHEN 'MED-002' THEN '管制药品'
    ELSE remark
END
WHERE medicine_code IN ('MED-001', 'MED-002');
