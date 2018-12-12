DROP TRIGGER customer_id ON Customer;
DROP TRIGGER mechanic_id ON Mechanic;
DROP TRIGGER closed_request_id ON Closed_Request;
CREATE SEQUENCE customer_id_seq;
CREATE SEQUENCE mechanic_id_seq;
CREATE SEQUENCE closed_request_id_seq;

SELECT setval('customer_id_seq', (SELECT MAX(id) FROM Customer));
SELECT setval('mechanic_id_seq', (SELECT MAX(id) FROM Mechanic));
SELECT setval('closed_request_id_seq', (SELECT MAX(wid) FROM Closed_Request));

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

CREATE OR REPLACE FUNCTION closed_request_id()
    RETURNS trigger AS
    $BODY$
    BEGIN
        NEW.wid := nextval('closed_request_id');
        return NEW;
    END
    $BODY$
    LANGUAGE plpgsql VOLATILE;
    
CREATE TRIGGER customer_id BEFORE INSERT ON Customer
    FOR EACH ROW EXECUTE PROCEDURE customer_id();

CREATE TRIGGER mechanic_id BEFORE INSERT ON Mechanic
    FOR EACH ROW EXECUTE PROCEDURE mechanic_id();

CREATE TRIGGER closed_request_id BEFORE INSERT ON Closed_Request
    FOR EACH ROW EXECUTE PROCEDURE closed_request_id();
    
