# This file contains a complete database schema for all the 
# tables used by this module, written in SQL

# It may also contain INSERT statements for particular data 
# that may be used, especially new entries in the table log_display

CREATE TABLE prefix_lamstwo (
  id int(10) unsigned NOT NULL auto_increment,
  course int(10) unsigned NOT NULL default '0',
  name varchar(255) NOT NULL default '',
  intro text NOT NULL default '',
  timemodified int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (id),
  KEY course (course)
)COMMENT='LAMS2 activity';  

CREATE TABLE prefix_lamstwo_lesson (
  id int(10) unsigned NOT NULL auto_increment,
  course int(10) unsigned NOT NULL default '0',
  lamstwo int(10) unsigned NOT NULL default '0',
  name varchar(255) NOT NULL default '',
  intro text NOT NULL default '',
  groupid bigint(10) NOT NULL default '-1',
  sequence_id bigint(20) default '0',
  lesson_id bigint(20) default '0',
  timemodified int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (id)
); 

CREATE TABLE mdl_lamstwo_grade (
  id int(10) unsigned NOT NULL auto_increment,
  lamstwolesson int(10) unsigned NOT NULL default '0',
  user int(10) unsigned NOT NULL default '0',
  completed int(1) unsigned NOT NULL default '0',
  PRIMARY KEY (id)
);
