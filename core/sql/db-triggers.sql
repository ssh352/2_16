/* Header line. Object: cash_balance_after_update. Script date: 23.05.2012 20:55:33. */
CREATE TRIGGER `cash_balance_after_update`
  AFTER UPDATE ON `cash_balance`
  FOR EACH ROW
BEGIN
     IF NOT NEW.AMOUNT = OLD.AMOUNT OR (NEW.AMOUNT IS NULL XOR OLD.AMOUNT IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('cash_balance', NEW.id, NOW(), 'AMOUNT', NEW.AMOUNT);
     END IF;
END;

/* Header line. Object: component_after_update. Script date: 23.05.2012 20:55:33. */
CREATE TRIGGER `component_after_update`
  AFTER UPDATE ON `component`
  FOR EACH ROW
BEGIN
     IF NOT NEW.QUANTITY = OLD.QUANTITY OR (NEW.QUANTITY IS NULL XOR OLD.QUANTITY IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('component', NEW.id, NOW(), 'QUANTITY', NEW.QUANTITY);
     END IF;
END;

/* Header line. Object: position_after_update. Script date: 23.05.2012 20:55:33. */
CREATE TRIGGER `position_after_update`
  AFTER UPDATE ON `position`
  FOR EACH ROW
BEGIN
     IF NOT NEW.QUANTITY = OLD.QUANTITY OR (NEW.QUANTITY IS NULL XOR OLD.QUANTITY IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('position', NEW.id, NOW(), 'QUANTITY', NEW.QUANTITY);
     END IF;
     IF NOT NEW.EXIT_VALUE = OLD.EXIT_VALUE OR (NEW.EXIT_VALUE IS NULL XOR OLD.EXIT_VALUE IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('position', NEW.id, NOW(), 'EXIT_VALUE', NEW.EXIT_VALUE);
     END IF;
     IF NOT NEW.MAINTENANCE_MARGIN = OLD.MAINTENANCE_MARGIN OR (NEW.MAINTENANCE_MARGIN IS NULL XOR OLD.MAINTENANCE_MARGIN IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('position', NEW.id, NOW(), 'MAINTENANCE_MARGIN', NEW.MAINTENANCE_MARGIN);
     END IF;
END;

/* Header line. Object: property_after_upd_tr. Script date: 23.05.2012 20:55:33. */
CREATE TRIGGER `property_after_update`
  AFTER UPDATE ON `property`
  FOR EACH ROW
BEGIN
     IF NOT NEW.INT_VALUE = OLD.INT_VALUE OR (NEW.INT_VALUE IS NULL XOR OLD.INT_VALUE IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('property', NEW.id, NOW(), 'INT_VALUE', NEW.INT_VALUE);
     END IF;
     IF NOT NEW.DOUBLE_VALUE = OLD.DOUBLE_VALUE OR (NEW.DOUBLE_VALUE IS NULL XOR OLD.DOUBLE_VALUE IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('property', NEW.id, NOW(), 'DOUBLE_VALUE', NEW.DOUBLE_VALUE);
     END IF;
     IF NOT NEW.MONEY_VALUE = OLD.MONEY_VALUE OR (NEW.MONEY_VALUE IS NULL XOR OLD.MONEY_VALUE IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('property', NEW.id, NOW(), 'MONEY_VALUE', NEW.MONEY_VALUE);
     END IF;
     IF NOT NEW.TEXT_VALUE = OLD.TEXT_VALUE OR (NEW.TEXT_VALUE IS NULL XOR OLD.TEXT_VALUE IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('property', NEW.id, NOW(), 'TEXT_VALUE', NEW.TEXT_VALUE);
     END IF;
     IF NOT NEW.DATE_VALUE = OLD.DATE_VALUE OR (NEW.DATE_VALUE IS NULL XOR OLD.DATE_VALUE IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('property', NEW.id, NOW(), 'DATE_VALUE', NEW.DATE_VALUE);
     END IF;
     IF NOT NEW.BOOLEAN_VALUE = OLD.BOOLEAN_VALUE OR (NEW.BOOLEAN_VALUE IS NULL XOR OLD.BOOLEAN_VALUE IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('property', NEW.id, NOW(), 'BOOLEAN_VALUE', NEW.BOOLEAN_VALUE);
     END IF;
END;

/* Header line. Object: strategy_after_update. Script date: 23.05.2012 20:55:33. */
CREATE TRIGGER `strategy_after_update`
  AFTER UPDATE ON `strategy`
  FOR EACH ROW
BEGIN
     IF NOT NEW.ALLOCATION = OLD.ALLOCATION OR (NEW.ALLOCATION IS NULL XOR OLD.ALLOCATION IS NULL) THEN
        INSERT INTO history (TBL, REF_ID, TIME, COL, VALUE)
        VALUES ('strategy', NEW.id, NOW(), 'ALLOCATION', NEW.ALLOCATION);
     END IF;
END;