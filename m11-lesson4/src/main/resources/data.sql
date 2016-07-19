INSERT INTO User (id, email, password) VALUES
(1, 'bill@email.com', 'pass'),
(2, 'john@email.com', '123');

INSERT INTO Possession (id, name, owner_id) VALUES
(1, 'Bill Possession', 1),
(2, 'Common Possesion', 1),
(3, 'John Possession', 2);

INSERT INTO acl_sid (id, principal, sid) VALUES
(1, 1, 'bill@email.com'),
(2, 1, 'john@email.com');

INSERT INTO acl_class (id, class) VALUES 
(1, 'com.baeldung.lss.model.Possession');

INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
(1, 1, 1, NULL, 1, 1), -- Bill Possession object identity
(2, 1, 2, NULL, 1, 1), -- Common Possession object identity
(3, 1, 3, NULL, 1, 1); -- John Possession object identity

INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES
(1, 1, 0, 1, 16, 1, 0, 0), -- bill@email.com has Admin permission for Bill Possession 
(2, 2, 0, 1, 16, 1, 0, 0), -- bill@email.com has Admin permission for Common Possession 
(3, 2, 1, 2, 1, 1, 0, 0),  -- john@email.com has Read permission for Common Possession 
(4, 3, 0, 2, 16, 1, 0, 0); -- john@email.com has Admin permission for John Possession 