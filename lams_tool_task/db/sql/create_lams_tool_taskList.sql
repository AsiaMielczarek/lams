SET FOREIGN_KEY_CHECKS=0;
drop table if exists tl_latask10_attachment;
drop table if exists tl_latask10_condition;
drop table if exists tl_latask10_condition_tl_item;
drop table if exists tl_latask10_taskList;
drop table if exists tl_latask10_taskList_item;
drop table if exists tl_latask10_taskList_item_visit_log;
drop table if exists tl_latask10_item_attachment;
drop table if exists tl_latask10_item_comment;
drop table if exists tl_latask10_session;
drop table if exists tl_latask10_user;
create table tl_latask10_attachment (
   uid bigint not null auto_increment,
   file_version_id bigint,
   file_type varchar(255),
   file_name varchar(255),
   file_uuid bigint,
   create_date datetime,
   taskList_uid bigint,
   primary key (uid)
)ENGINE=InnoDB;
create table tl_latask10_condition (
   condition_uid bigint not null auto_increment,
   sequence_id integer,
   taskList_uid bigint,
   name varchar(255),
   primary key (condition_uid)
)ENGINE=InnoDB;
create table tl_latask10_condition_tl_item (
   uid bigint not null,
   condition_uid bigint not null,
   primary key (uid, condition_uid)
)ENGINE=InnoDB;
create table tl_latask10_taskList (
   uid bigint not null auto_increment,
   create_date datetime,
   update_date datetime,
   create_by bigint,
   title varchar(255),
   run_offline tinyint,
   instructions text,
   online_instructions text,
   offline_instructions text,
   content_in_use tinyint,
   define_later tinyint,
   content_id bigint unique,
   lock_when_finished tinyint,
   is_sequential_order tinyint,
   minimum_number_tasks integer,
   allow_contribute_tasks tinyint,
   is_monitor_verification_required tinyint,
   reflect_instructions varchar(255), 
   reflect_on_activity smallint, 
   submission_deadline datetime DEFAULT NULL,
   primary key (uid)
)ENGINE=InnoDB;
create table tl_latask10_taskList_item (
   uid bigint not null auto_increment,
   sequence_id integer,
   description text,
   init_item varchar(255),
   organization_xml text,
   title varchar(255),
   create_by bigint,
   create_date datetime,
   create_by_author tinyint,
   is_required tinyint,
   is_comments_allowed tinyint,
   is_comments_required tinyint,
   is_files_allowed tinyint,
   is_files_required tinyint,
   is_comments_files_allowed tinyint,
   show_comments_to_all tinyint,
   is_child_task tinyint,
   parent_task_name varchar(255),
   taskList_uid bigint,
   session_uid bigint,
   primary key (uid)
)ENGINE=InnoDB;
create table tl_latask10_item_log (
   uid bigint not null auto_increment,
   access_date datetime,
   taskList_item_uid bigint,
   user_uid bigint,
   complete tinyint,
   session_id bigint,
   primary key (uid)
)ENGINE=InnoDB;
create table tl_latask10_item_attachment (
   uid bigint not null auto_increment,
   file_version_id bigint,
   file_type varchar(255),
   file_name varchar(255),
   file_uuid bigint,
   create_date datetime,
   taskList_item_uid bigint,
   create_by bigint,
   primary key (uid)
)ENGINE=InnoDB;
create table tl_latask10_item_comment (
   uid bigint not null auto_increment,
   comment text,
   taskList_item_uid bigint,
   create_by bigint,
   create_date datetime,
   primary key (uid)
)ENGINE=InnoDB;
create table tl_latask10_session (
   uid bigint not null auto_increment,
   session_end_date datetime,
   session_start_date datetime,
   status integer,
   taskList_uid bigint,
   session_id bigint,
   session_name varchar(250),
   primary key (uid)
)ENGINE=InnoDB;
create table tl_latask10_user (
   uid bigint not null auto_increment,
   user_id bigint,
   last_name varchar(255),
   first_name varchar(255),
   login_name varchar(255),
   session_finished smallint,
   session_uid bigint,
   taskList_uid bigint,
   is_verified_by_monitor tinyint,
   primary key (uid)
)ENGINE=InnoDB;
alter table tl_latask10_attachment add index FK_NEW_174079138_1E7009430E79035 (taskList_uid), add constraint FK_NEW_174079138_1E7009430E79035 foreign key (taskList_uid) references tl_latask10_taskList (uid);
alter table tl_latask10_condition add index FK_tl_latask10_condition_1 (taskList_uid), add constraint FK_tl_latask10_condition_1 foreign key (taskList_uid) references tl_latask10_taskList (uid);
alter table tl_latask10_condition_tl_item add index FK_tl_latask10_taskList_item_condition_1 (condition_uid), add constraint FK_tl_latask10_taskList_item_condition_1 foreign key (condition_uid) references tl_latask10_condition (condition_uid);
alter table tl_latask10_condition_tl_item add index FK_tl_latask10_taskList_item_condition_2 (uid), add constraint FK_tl_latask10_taskList_item_condition_2 foreign key (uid) references tl_latask10_taskList_item (uid);
alter table tl_latask10_taskList add index FK_NEW_174079138_89093BF758092FB (create_by), add constraint FK_NEW_174079138_89093BF758092FB foreign key (create_by) references tl_latask10_user (uid);
alter table tl_latask10_taskList_item add index FK_NEW_174079138_F52D1F93758092FB (create_by), add constraint FK_NEW_174079138_F52D1F93758092FB foreign key (create_by) references tl_latask10_user (uid);
alter table tl_latask10_taskList_item add index FK_NEW_174079138_F52D1F9330E79035 (taskList_uid), add constraint FK_NEW_174079138_F52D1F9330E79035 foreign key (taskList_uid) references tl_latask10_taskList (uid);
alter table tl_latask10_taskList_item add index FK_NEW_174079138_F52D1F93EC0D3147 (session_uid), add constraint FK_NEW_174079138_F52D1F93EC0D3147 foreign key (session_uid) references tl_latask10_session (uid);
alter table tl_latask10_item_log add index FK_NEW_174079138_693580A438BF8DFE (taskList_item_uid), add constraint FK_NEW_174079138_693580A438BF8DFE foreign key (taskList_item_uid) references tl_latask10_taskList_item (uid);
alter table tl_latask10_item_log add index FK_NEW_174079138_693580A441F9365D (user_uid), add constraint FK_NEW_174079138_693580A441F9365D foreign key (user_uid) references tl_latask10_user (uid);
alter table tl_latask10_item_attachment add index FK_tl_latask10_item_attachment_1 (taskList_item_uid), add constraint FK_tl_latask10_item_attachment_1 foreign key (taskList_item_uid) references tl_latask10_taskList_item (uid);
alter table tl_latask10_item_attachment add index FK_tl_latask10_item_attachment_2 (create_by), add constraint FK_tl_latask10_item_attachment_2 foreign key (create_by) references tl_latask10_user (uid);
alter table tl_latask10_item_comment add index FK_tl_latask10_item_comment_3 (taskList_item_uid), add constraint FK_tl_latask10_item_comment_3 foreign key (taskList_item_uid) references tl_latask10_taskList_item (uid);
alter table tl_latask10_item_comment add index FK_tl_latask10_item_comment_2 (create_by), add constraint FK_tl_latask10_item_comment_2 foreign key (create_by) references tl_latask10_user (uid);
alter table tl_latask10_session add index FK_NEW_174079138_24AA78C530E79035 (taskList_uid), add constraint FK_NEW_174079138_24AA78C530E79035 foreign key (taskList_uid) references tl_latask10_taskList (uid);
alter table tl_latask10_user add index FK_NEW_174079138_30113BFCEC0D3147 (session_uid), add constraint FK_NEW_174079138_30113BFCEC0D3147 foreign key (session_uid) references tl_latask10_session (uid);
alter table tl_latask10_user add index FK_NEW_174079138_30113BFC309ED320 (taskList_uid), add constraint FK_NEW_174079138_30113BFC309ED320 foreign key (taskList_uid) references tl_latask10_taskList (uid);



INSERT INTO `tl_latask10_taskList` (`uid`, `create_date`, `update_date`, `create_by`, `title`, `run_offline`, `instructions`,
	`online_instructions`, `offline_instructions`, `content_in_use`, `define_later`, `content_id`, `lock_when_finished`, 
	`minimum_number_tasks`, `is_sequential_order`, `allow_contribute_tasks`, `is_monitor_verification_required`, 
	`reflect_on_activity`) VALUES
  (1,NULL,NULL,NULL,'Task List','0','Instructions ',null,null,0,0,${default_content_id},0,0,0,0,0,0);
  
INSERT INTO `tl_latask10_taskList_item` (`uid`, `sequence_id`, `description`, `init_item`, `organization_xml`, `title`, `create_by`, `create_date`, `create_by_author`, `is_required`, `is_comments_allowed`, `is_comments_required`, `is_files_allowed`, `is_files_required`, `is_comments_files_allowed`, `show_comments_to_all`, `is_child_task`, `parent_task_name`, `taskList_uid`, `session_uid`) VALUES 
  (1,1,NULL,NULL,NULL,'Task number 1',null,NOW(),1,0,0,0,0,0,0,1,0,NULL,1,NULL);
    
SET FOREIGN_KEY_CHECKS=1;
