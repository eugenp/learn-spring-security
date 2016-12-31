insert into Role (name, id) values ('ROLE_ADMIN', 3);
insert into Role (name, id) values ('ROLE_USER', 4);
insert into User (id, created, email, password) values (1, NOW(), 'test@test.com', 'test');
insert into users_roles (user_id, role_id) values (1, 3);