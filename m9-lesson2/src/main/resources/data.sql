delete from roles_privileges;
delete from users_roles;
delete from role;
delete from privilege;
delete from user;


insert into privilege (name, id) values ('READ_PRIVILEGE', 1);
insert into privilege (name, id) values ('WRITE_PRIVILEGE', 2);

insert into role (name, id) values ('ROLE_ADMIN', 3);
insert into role (name, id) values ('ROLE_USER', 4);

insert into roles_privileges (role_id, privilege_id) values (3, 1);
insert into roles_privileges (role_id, privilege_id) values (3, 2);
insert into roles_privileges (role_id, privilege_id) values (4, 1);

insert into user (id, email, password) values (1,'test@email.com', 'pass');

insert into users_roles (user_id, role_id) values (1, 3);
