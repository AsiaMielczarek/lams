SET FOREIGN_KEY_CHECKS=0;
drop table if exists tl_larsrc11_attachment;
drop table if exists tl_larsrc11_item_instruction;
drop table if exists tl_larsrc11_resource;
drop table if exists tl_larsrc11_resource_item;
drop table if exists tl_larsrc11_resource_item_visit_log;
drop table if exists tl_larsrc11_session;
drop table if exists tl_larsrc11_user;
create table tl_larsrc11_attachment (
   uid bigint not null auto_increment,
   file_version_id bigint,
   file_type varchar(255),
   file_name varchar(255),
   file_uuid bigint,
   create_date datetime,
   resource_uid bigint,
   primary key (uid)
);
create table tl_larsrc11_item_instruction (
   uid bigint not null auto_increment,
   description varchar(255),
   sequence_id integer,
   item_uid bigint,
   primary key (uid)
);
create table tl_larsrc11_resource (
   uid bigint not null auto_increment,
   create_date datetime,
   update_date datetime,
   create_by bigint,
   title varchar(255),
   run_offline tinyint,
   lock_on_finished tinyint,
   instructions text,
   online_instructions text,
   offline_instructions text,
   content_in_use tinyint,
   define_later tinyint,
   content_id bigint unique,
   allow_add_files tinyint,
   allow_add_urls tinyint,
   mini_view_resource_number integer,
   allow_auto_run tinyint,
   primary key (uid)
);
create table tl_larsrc11_resource_item (
   uid bigint not null auto_increment,
   file_uuid bigint,
   file_version_id bigint,
   description varchar(255),
   ims_schema varchar(255),
   init_item varchar(255),
   organization_xml text,
   title varchar(255),
   url text,
   create_by bigint,
   create_date datetime,
   create_by_author tinyint,
   is_hide tinyint,
   item_type smallint,
   file_type varchar(255),
   file_name varchar(255),
   open_url_new_window tinyint,
   resource_uid bigint,
   session_uid bigint,
   primary key (uid)
);
create table tl_larsrc11_item_log (
   uid bigint not null auto_increment,
   access_date datetime,
   resource_item_uid bigint,
   user_uid bigint,
   complete tinyint,
   session_id bigint,
   primary key (uid)
);
create table tl_larsrc11_session (
   uid bigint not null auto_increment,
   session_end_date datetime,
   session_start_date datetime,
   status integer,
   resource_uid bigint,
   session_id bigint,
   session_name varchar(250),
   primary key (uid)
);
create table tl_larsrc11_user (
   uid bigint not null auto_increment,
   user_id bigint,
   last_name varchar(255),
   first_name varchar(255),
   login_name varchar(255),
   session_id bigint,
   primary key (uid)
);
alter table tl_larsrc11_attachment add index FK1E7009430E79035 (resource_uid), add constraint FK1E7009430E79035 foreign key (resource_uid) references tl_larsrc11_resource (uid);
alter table tl_larsrc11_item_instruction add index FKA5665013980570ED (item_uid), add constraint FKA5665013980570ED foreign key (item_uid) references tl_larsrc11_resource_item (uid);
alter table tl_larsrc11_resource add index FK89093BF758092FB (create_by), add constraint FK89093BF758092FB foreign key (create_by) references tl_larsrc11_user (uid);
alter table tl_larsrc11_resource_item add index FKF52D1F93758092FB (create_by), add constraint FKF52D1F93758092FB foreign key (create_by) references tl_larsrc11_user (uid);
alter table tl_larsrc11_resource_item add index FKF52D1F9330E79035 (resource_uid), add constraint FKF52D1F9330E79035 foreign key (resource_uid) references tl_larsrc11_resource (uid);
alter table tl_larsrc11_resource_item add index FKF52D1F93EC0D3147 (session_uid), add constraint FKF52D1F93EC0D3147 foreign key (session_uid) references tl_larsrc11_session (uid);
alter table tl_larsrc11_item_log add index FK693580A438BF8DFE (resource_item_uid), add constraint FK693580A438BF8DFE foreign key (resource_item_uid) references tl_larsrc11_resource_item (uid);
alter table tl_larsrc11_item_log add index FK693580A441F9365D (user_uid), add constraint FK693580A441F9365D foreign key (user_uid) references tl_larsrc11_user (uid);
alter table tl_larsrc11_session add index FK24AA78C530E79035 (resource_uid), add constraint FK24AA78C530E79035 foreign key (resource_uid) references tl_larsrc11_resource (uid);
alter table tl_larsrc11_user add index FK30113BFC506CD584 (session_id), add constraint FK30113BFC506CD584 foreign key (session_id) references tl_larsrc11_session (uid);



INSERT INTO `tl_larsrc11_resource` (`uid`, `create_date`, `update_date`, `create_by`, `title`, `run_offline`, `lock_on_finished`, `instructions`, `online_instructions`, `offline_instructions`, `content_in_use`, `define_later`, `content_id`, `allow_add_files`, `allow_add_urls`, `mini_view_resource_number`, `allow_auto_run`) VALUES
  (2,NULL,NULL,NULL,'Shared Resources','0','0','Instruction','Online instruction','Offline instruction',0,0,${default_content_id},1,1,0,1);
SET FOREIGN_KEY_CHECKS=1;