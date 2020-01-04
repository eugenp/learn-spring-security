-- User john@test.com/123
insert into User (id, created, email, password) values (1, NOW(), 'john@test.com', '$2a$10$BuPWnSrcLPxwMcCwu7eyBu/x7817Mo3MzYbZGVFfgIewEUUXPmpy6');
-- User tom@test.com/abc
insert into User (id, created, email, password) values (2, NOW(), 'tom@test.com', '$2a$10$UvqH9bvt8ESQYYPv/T4dBuxVdI4R5YW1fQ6ER.1upX4fung7WTkkC');
-- User jane@test.com/test
insert into User (id, created, email, password) values (3, NOW(), 'jane@test.com', '$2a$10$ZosuIqHSLwPDl2SYJh8sg.O.8a.LSMRyOuJwzWZQw06jNHrlmJiqi');