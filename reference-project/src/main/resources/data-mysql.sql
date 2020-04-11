insert into security_question_definition (id, text) values (1, 'What is the last name of the teacher who gave you your first failing grade?');
insert into security_question_definition (id, text) values (2, 'What is the first name of the person you first kissed?');
insert into security_question_definition (id, text) values (3, 'What is the name of the place your wedding reception was held?');
insert into security_question_definition (id, text) values (4, 'When you were young, what did you want to be when you grew up?');
insert into security_question_definition (id, text) values (5, 'Where were you New Year''s 2000?');
insert into security_question_definition (id, text) values (6, 'Who was your childhood hero?');

insert into role (name, id) values ('ADMIN', 1);
insert into role (name, id) values ('USER', 2);

-- test@email.com/pass
insert into user (id, email, password, enabled, created) values (1, 'test@email.com', '$2a$04$kqRvgmJBlWZQQ2c9NT9IH.ZhxFY07Y2xE73vmLHxBq2hNTvGvUc5m', true,  NOW());
insert into security_question(id, user_id, security_question_definition_id, answer) values (1, 1, 4, 'Spring Security Expert');
insert into users_roles (user_id, role_id) values (1, 1);

insert into user (id, email, password, enabled, created) values (2, 'user@email.com', '$2a$04$kqRvgmJBlWZQQ2c9NT9IH.ZhxFY07Y2xE73vmLHxBq2hNTvGvUc5m', true,  NOW());
insert into security_question(id, user_id, security_question_definition_id, answer) values (2, 2, 4, 'Spring Security Expert');
insert into users_roles (user_id, role_id) values (2, 2);
