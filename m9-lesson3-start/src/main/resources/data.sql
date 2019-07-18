insert into Role (name, id) values ('ROLE_ADMIN', 3);
insert into Role (name, id) values ('ROLE_USER', 4);
-- test@test.com/test
insert into User (id, created, email, password) values (1, NOW(), 'test@test.com', '$2a$10$jYS6pb7hpNlXC3xRJECG..4oiDUtPwl9PPn4nu6NcszgqNi74nor.');
insert into users_roles (user_id, role_id) values (1, 3);