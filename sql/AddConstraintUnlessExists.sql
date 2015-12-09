DELIMITER //
CREATE PROCEDURE AddConstraintUnlessExists (
	IN tableName tinytext,
    IN constraintName tinytext,
    IN constraintType tinytext,
    IN constraintDef text)
begin
    IF NOT EXISTS (
		SELECT NULL FROM information_schema.TABLE_CONSTRAINTS WHERE
		CONSTRAINT_SCHEMA = 'dealersocket' AND
        CONSTRAINT_NAME = constraintName AND
        CONSTRAINT_TYPE = constraintType) 
	THEN
		set @ddl = CONCAT('ALTER TABLE dealersocket.', tableName, ' ADD CONSTRAINT ', constraintName, ' ', constraintType, ' ', constraintDef);
        prepare stmt from @ddl;
        execute stmt;
	END IF;
end