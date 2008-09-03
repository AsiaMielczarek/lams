SET FOREIGN_KEY_CHECKS=0;
drop table if exists tl_lasurv11_answer;
drop table if exists tl_lasurv11_attachment;
drop table if exists tl_lasurv11_option;
drop table if exists tl_lasurv11_question;
drop table if exists tl_lasurv11_session;
drop table if exists tl_lasurv11_survey;
drop table if exists tl_lasurv11_user;


create table tl_lasurv11_survey (
uid bigint not null auto_increment, 
title varchar(255), 
run_offline smallint, 
lock_on_finished smallint, 
instructions text, 
online_instructions text, 
offline_instructions text, 
content_in_use smallint, 
define_later smallint, 
content_id bigint unique, 
reflect_instructions varchar(255), 
reflect_on_activity smallint, 
show_questions_on_one_page smallint, 
create_date datetime, 
update_date datetime, 
create_by bigint,
answer_submit_notify tinyint DEFAULT 0,
primary key (uid)
)type=innodb;


create table tl_lasurv11_answer (
uid bigint not null auto_increment, 
question_uid bigint, 
user_uid bigint, 
answer_choices varchar(255), 
udpate_date datetime, 
answer_text text, 
primary key (uid)
)type=innodb;

create table tl_lasurv11_attachment (
uid bigint not null auto_increment, 
file_version_id bigint, 
file_type varchar(255), 
file_name varchar(255), 
file_uuid bigint, 
create_date datetime, 
survey_uid bigint, 
primary key (uid)
)type=innodb;

create table tl_lasurv11_option (
uid bigint not null auto_increment, 
description text,
sequence_id integer, 
question_uid bigint, 
primary key (uid)
)type=innodb;

create table tl_lasurv11_question (
uid bigint not null auto_increment, 
sequence_id integer,
description text,
create_by bigint, 
create_date datetime, 
question_type smallint, 
append_text smallint, 
optional smallint, 
allow_multiple_answer smallint,
survey_uid bigint, 
primary key (uid)
)type=innodb;

create table tl_lasurv11_session (
uid bigint not null auto_increment, 
session_end_date datetime, 
session_start_date datetime, 
survey_uid bigint, 
session_id bigint, 
session_name varchar(250), 
primary key (uid)
)type=innodb;

create table tl_lasurv11_user (
uid bigint not null auto_increment, 
user_id bigint, 
last_name varchar(255), 
first_name varchar(255), 
login_name varchar(255), 
session_uid bigint, 
survey_uid bigint, 
session_finished smallint, 
primary key (uid)
)type=innodb;

alter table tl_lasurv11_answer add index FK6DAAFE3BB1423DC1 (user_uid), add constraint FK6DAAFE3BB1423DC1 foreign key (user_uid) references tl_lasurv11_user (uid);
alter table tl_lasurv11_answer add index FK6DAAFE3B25F3BB77 (question_uid), add constraint FK6DAAFE3B25F3BB77 foreign key (question_uid) references tl_lasurv11_question (uid);
alter table tl_lasurv11_attachment add index FKD92A9120D14146E5 (survey_uid), add constraint FKD92A9120D14146E5 foreign key (survey_uid) references tl_lasurv11_survey (uid);
alter table tl_lasurv11_option add index FK85AB46F26966134F (question_uid), add constraint FK85AB46F26966134F foreign key (question_uid) references tl_lasurv11_question (uid);
alter table tl_lasurv11_question add index FK872D4F23D14146E5 (survey_uid), add constraint FK872D4F23D14146E5 foreign key (survey_uid) references tl_lasurv11_survey (uid);
alter table tl_lasurv11_question add index FK872D4F23E4C99A5F (create_by), add constraint FK872D4F23E4C99A5F foreign key (create_by) references tl_lasurv11_user (uid);
alter table tl_lasurv11_session add index FKF08793B9D14146E5 (survey_uid), add constraint FKF08793B9D14146E5 foreign key (survey_uid) references tl_lasurv11_survey (uid);
alter table tl_lasurv11_survey add index FK8CC465D7E4C99A5F (create_by), add constraint FK8CC465D7E4C99A5F foreign key (create_by) references tl_lasurv11_user (uid);
alter table tl_lasurv11_user add index FK633F25884F803F63 (session_uid), add constraint FK633F25884F803F63 foreign key (session_uid) references tl_lasurv11_session (uid);
alter table tl_lasurv11_user add index FK633F2588D14146E5 (survey_uid), add constraint FK633F2588D14146E5 foreign key (survey_uid) references tl_lasurv11_survey (uid);



INSERT INTO `tl_lasurv11_survey` (`uid`, `create_date`, `update_date`, `create_by`, `title`, 
  `run_offline`, `lock_on_finished`, `instructions`, `online_instructions`, `offline_instructions`,
 `content_in_use`, `define_later`, `content_id`,`show_questions_on_one_page`,`reflect_on_activity`) VALUES
  (1,NULL,NULL,NULL,'Survey','0','1','Instructions',null,null,0,0,${default_content_id},1,0);
  
INSERT INTO `tl_lasurv11_question` (`uid`, `sequence_id`, `description`, `create_by`, `create_date`, `question_type`, `append_text`, `optional`, `allow_multiple_answer`, `survey_uid`) VALUES 
  (1,1,'Sample Multiple choice - only one response allowed?',null,NOW(),1,0,0,0,1);
INSERT INTO `tl_lasurv11_question` (`uid`, `sequence_id`, `description`, `create_by`, `create_date`, `question_type`, `append_text`, `optional`, `allow_multiple_answer`, `survey_uid`) VALUES   
  (2,2,'Sample Multiple choice - multiple response allowed?',null,NOW(),2,0,0,1,1);
INSERT INTO `tl_lasurv11_question` (`uid`, `sequence_id`, `description`, `create_by`, `create_date`, `question_type`, `append_text`, `optional`, `allow_multiple_answer`, `survey_uid`) VALUES   
  (3,3,'Sample Free text question?',null,NOW(),3,0,0,0,1);
  

INSERT INTO `tl_lasurv11_option` (`uid`, `description`, `sequence_id`, `question_uid`) VALUES 
  (1,'Option 1',0,1);
INSERT INTO `tl_lasurv11_option` (`uid`, `description`, `sequence_id`, `question_uid`) VALUES   
  (2,'Option 2',1,1);
INSERT INTO `tl_lasurv11_option` (`uid`, `description`, `sequence_id`, `question_uid`) VALUES 
  (3,'Option 3',2,1);
INSERT INTO `tl_lasurv11_option` (`uid`, `description`, `sequence_id`, `question_uid`) VALUES 
  (4,'Option 1',0,2);
INSERT INTO `tl_lasurv11_option` (`uid`, `description`, `sequence_id`, `question_uid`) VALUES 
  (5,'Option 2',1,2);
INSERT INTO `tl_lasurv11_option` (`uid`, `description`, `sequence_id`, `question_uid`) VALUES 
  (6,'Option 3',2,2);  
SET FOREIGN_KEY_CHECKS=1;
