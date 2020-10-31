delete from users_roles;
delete from role;
delete from user;

insert into role (name, id) values ('ROLE_ADMIN', 3);
insert into role (name, id) values ('ROLE_USER', 4);
insert into user (id, email, password, created) values (1, 'test@email.com', 'pass', now());
insert into users_roles (user_id, role_id) values (1, 3);
