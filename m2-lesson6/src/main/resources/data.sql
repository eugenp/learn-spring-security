insert into SecurityQuestionDefinition (id, text) values (1, 'What is the last name of the teacher who gave you your first failing grade?');
insert into SecurityQuestionDefinition (id, text) values (2, 'What is the first name of the person you first kissed?');
insert into SecurityQuestionDefinition (id, text) values (3, 'What is the name of the place your wedding reception was held?');
insert into SecurityQuestionDefinition (id, text) values (4, 'When you were young, what did you want to be when you grew up?');
insert into SecurityQuestionDefinition (id, text) values (5, 'Where were you New Year''s 2000?');
insert into SecurityQuestionDefinition (id, text) values (6, 'Who was your childhood hero?');

-- Test User
insert into User (id, email, password, enabled, created) values (1, 'test@email.com', 'pass', true, '2008-08-08 00:00:00');
insert into SecurityQuestion(id, user_id, securityQuestionDefinition_id, answer) values (1, 1, 4, 'Spring Security Expert');