DROP TRIGGER customer_id ON Customer;
DROP TRIGGER mechanic_id ON Mechanic;
CREATE SEQUENCE customer_id_seq START WTIH 1;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION customer_id()
    RETURNS trigger AS
    $BODY$
    BEGIN
        NEW.id := nextval('customer_id_seq');
        return NEW;
    END
    $BODY$
    LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION mechanic_id()
    RETURNS trigger AS
    $BODY$
    BEGIN
        NEW.id := nextval('mechanic_id_seq');
        return NEW;
    END
    $BODY$
    LANGUAGE plpgsql VOLATILE;
    
CREATE TRIGGER customer_id BEFORE INSERT ON Customer
    FOR EACH ROW EXECUTE PROCEDURE customer_id();

CREATE TRIGGER mechanic_id BEFORE INSERT ON Mechanic
    FOR EACH ROW EXECUTE PROCEDURE mechanic_id();
