DELIMITER //
CREATE PROCEDURE AddColumnUnlessExists (
	IN tableName tinytext,
    IN fieldName tinytext,
    IN fieldDef text)
begin
    IF NOT EXISTS (
        SELECT * FROM information_schema.COLUMNS
        WHERE column_name=fieldName
        and table_name=tableName
        and table_schema='dealersocket'
        )
    THEN
        set @ddl = CONCAT('ALTER TABLE dealersocket.', tableName, ' ADD COLUMN ', fieldName, ' ', fieldDef);
        prepare stmt from @ddl;
        execute stmt;
    END IF;
end