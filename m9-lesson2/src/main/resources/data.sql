insert into Privilege (name, id) values ('READ_PRIVILEGE', 1);
insert into Privilege (name, id) values ('WRITE_PRIVILEGE', 2);
insert into Role (name, id) values ('ROLE_ADMIN', 3);
insert into Role (name, id) values ('ROLE_USER', 4);
insert into roles_privileges (role_id, privilege_id) values (3, 1);
insert into roles_privileges (role_id, privilege_id) values (3, 2);
insert into roles_privileges (role_id, privilege_id) values (4, 1);
-- test@email.com/pass
insert into User (id, created, email, password) values (1, NOW(), 'test@email.com', '$2a$04$kqRvgmJBlWZQQ2c9NT9IH.ZhxFY07Y2xE73vmLHxBq2hNTvGvUc5m');
insert into users_roles (user_id, role_id) values (1, 3);