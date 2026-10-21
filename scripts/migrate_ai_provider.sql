-- ==========================================================
-- Migration Script: Add AI Provider Tracking
-- Backward-compatible: existing records will have provider = NULL
-- ==========================================================
USE `learning_platform`;

-- Safely add provider column if not exists
SET @dbname = DATABASE();
SET @tablename = "ai_messages";
SET @columnname = "provider";
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_SCHEMA = @dbname
      AND TABLE_NAME = @tablename
      AND COLUMN_NAME = @columnname
  ) > 0,
  "SELECT 1",
  CONCAT("ALTER TABLE ", @tablename, " ADD COLUMN ", @columnname, " VARCHAR(50) NULL AFTER teaching_style;")
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;
