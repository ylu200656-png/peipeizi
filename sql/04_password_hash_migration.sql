USE yaojie;

UPDATE sys_user
SET password_hash = '$2a$10$dPpBEtp.icfkXCIFwrmIFO5d9u4V5EWYlqCIh5wZonKao8mV2HXOO'
WHERE password_hash = '123456';
