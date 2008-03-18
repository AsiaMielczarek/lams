-- Script to be run for LAMS 2.1 release, on LAMS 2.0.3/2.0.4 tables.
-- Adds all the data needed for branching, plus a small change to the tool table needed for tool admin screen.

ALTER TABLE lams_group
ADD COLUMN group_ui_id INT(11);

CREATE TABLE lams_input_activity (
       activity_id BIGINT(20) NOT NULL
     , input_activity_id BIGINT(20) NOT NULL
     , UNIQUE UQ_lams_input_activity_1 (activity_id, input_activity_id)
     , INDEX (activity_id)
     , CONSTRAINT FK_lams_input_activity_1 FOREIGN KEY (activity_id)
                  REFERENCES lams_learning_activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (activity_id)
     , CONSTRAINT FK_lams_input_activity_2 FOREIGN KEY (activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;


CREATE TABLE lams_branch_condition (
       condition_id BIGINT(20) NOT NULL   AUTO_INCREMENT
     , condition_ui_id INT(11)
     , order_id INT(11)
     , name VARCHAR(255) NOT NULL
     , display_name VARCHAR(255)
     , type VARCHAR(255) NOT NULL
     , start_value VARCHAR(255)
     , end_value VARCHAR(255)
     , exact_match_value VARCHAR(255)
     , PRIMARY KEY (condition_id)
)TYPE=InnoDB;

CREATE TABLE lams_branch_activity_entry (
       entry_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , entry_ui_id INT(11)
     , group_id BIGINT(20)
     , sequence_activity_id BIGINT(20) NOT NULL
     , branch_activity_id BIGINT(20) NOT NULL
     , condition_id BIGINT(20)
     , PRIMARY KEY (entry_id)
     , INDEX (group_id)
     , CONSTRAINT FK_lams_group_activity_1 FOREIGN KEY (group_id)
                  REFERENCES lams_group (group_id)
     , INDEX (sequence_activity_id)
     , CONSTRAINT FK_lams_branch_map_sequence FOREIGN KEY (sequence_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
     , INDEX (branch_activity_id)
     , CONSTRAINT FK_lams_branch_map_branch FOREIGN KEY (branch_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
     , INDEX (condition_id)
     , CONSTRAINT FK_lams_branch_activity_entry_4 FOREIGN KEY (condition_id)
                  REFERENCES lams_branch_condition (condition_id)
)TYPE=InnoDB;

ALTER TABLE lams_learning_activity 
ADD COLUMN default_activity_id BIGINT(20)
,ADD COLUMN start_xcoord INT(11)
,ADD COLUMN start_ycoord INT(11)
,ADD COLUMN end_xcoord INT(11)
,ADD COLUMN end_ycoord INT(11)
,ADD COLUMN stop_after_activity TINYINT NOT NULL DEFAULT 0
;
 

INSERT INTO lams_learning_activity_type VALUES (10, 'BRANCHING_CHOSEN');
INSERT INTO lams_learning_activity_type VALUES (11, 'BRANCHING_GROUP');
INSERT INTO lams_learning_activity_type VALUES (12, 'BRANCHING_TOOL');
INSERT INTO lams_learning_activity_type VALUES (13, 'OPTIONS_WITH_SEQUENCES');

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description, 
	learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url, 
	export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (6, 10, 'Monitor Chosen Branching', 'Select between multiple sequence activities, with the branch chosen in monitoring.', 
	'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching', 
	'learning/branching.do?method=viewBranching&mode=teacher', NULL,
	'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/chosenBranching.do?method=assignBranch', 
	'monitoring/chosenBranching.do?method=assignBranch', now());

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description,
        learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url,
        export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (7, 11, 'Group Based Branching', 'Select between multiple sequence activities, with the branch chosen by an existing group.',
        'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching',
        'learning/branching.do?method=viewBranching&mode=teacher', NULL,
        'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/groupedBranching.do?method=viewBranching',
        'monitoring/groupedBranching.do?method=assignBranch', now());

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description,
        learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url,
        export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (8, 12, 'Tool Output Based Branching', 'Select between multiple sequence activities, with the branch chosen on results of another activity.',
        'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching',
        'learning/branching.do?method=viewBranching&mode=teacher', NULL, 
        'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/toolBranching.do?method=viewBranching',
        'monitoring/toolBranching.do?method=viewBranching', now());

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description,
        learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url,
        export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (9, 8, 'Sequence', 'A sequence of activities',
        'learning/SequenceActivity.do', 'learning/SequenceActivity.do',
        NULL, NULL, 
        'monitoring/sequenceExportPortfolio?mode=teacher', 'monitoring/sequence.do?method=viewSequence',
        'monitoring/sequence.do?method=viewSequence', now());


-- support tools having an admin screen

ALTER TABLE lams_tool ADD COLUMN admin_url TEXT;
ALTER TABLE lams_system_tool ADD COLUMN admin_url TEXT;

-- LDEV-1251, sortable lessons
alter table lams_organisation add column ordered_lesson_ids text;

-- LDEV-1252, collapsible courses
CREATE TABLE lams_user_organisation_collapsed (
       user_organisation_id BIGINT(20) NOT NULL
     , collapsed TINYINT(1) NOT NULL DEFAULT 1
     , PRIMARY KEY (user_organisation_id)
     , CONSTRAINT FK_lams_user_organisation_collapsed_1 FOREIGN KEY (user_organisation_id)
                  REFERENCES lams_user_organisation (user_organisation_id)
)TYPE=InnoDB;

-- LDEV-1301
ALTER TABLE lams_configuration 
ADD COLUMN description_key VARCHAR(255)
, ADD COLUMN header_name VARCHAR(50)
, ADD COLUMN format VARCHAR(30)
, ADD COLUMN required TINYINT NOT NULL DEFAULT 0;

delete from lams_configuration where config_key='LamsHome';
delete from lams_configuration where config_key='FileManagerDir';

update lams_configuration set description_key='config.server.url', header_name='config.header.system', format='STRING', required=1 where config_key='ServerURL';
update lams_configuration set description_key='config.server.url.context.path', header_name='config.header.system', format='STRING', required=1 where config_key='ServerURLContextPath';
update lams_configuration set description_key='config.version', header_name='config.header.system', format='STRING', required=1 where config_key='Version';
update lams_configuration set description_key='config.temp.dir', header_name='config.header.system', format='STRING', required=1 where config_key='TempDir';
update lams_configuration set description_key='config.dump.dir', header_name='config.header.system', format='STRING', required=1 where config_key='DumpDir';
update lams_configuration set description_key='config.ear.dir', header_name='config.header.system', format='STRING', required=1 where config_key='EARDir';
update lams_configuration set description_key='config.smtp.server', header_name='config.header.email', format='STRING', required=1 where config_key='SMTPServer';
update lams_configuration set description_key='config.lams.support.email', header_name='config.header.email', format='STRING', required=1 where config_key='LamsSupportEmail';
update lams_configuration set description_key='config.content.repository.path', header_name='config.header.uploads', format='STRING', required=1 where config_key='ContentRepositoryPath';
update lams_configuration set description_key='config.upload.file.max.size', header_name='config.header.uploads', format='LONG', required=1 where config_key='UploadFileMaxSize';
update lams_configuration set description_key='config.upload.large.file.max.size', header_name='config.header.uploads', format='LONG', required=1 where config_key='UploadLargeFileMaxSize';
update lams_configuration set description_key='config.upload.file.max.memory.size', header_name='config.header.uploads', format='LONG', required=1 where config_key='UploadFileMaxMemorySize';
update lams_configuration set description_key='config.executable.extensions', header_name='config.header.uploads', format='STRING', required=1 where config_key='ExecutableExtensions';
update lams_configuration set description_key='config.user.inactive.timeout', header_name='config.header.system', format='LONG', required=1 where config_key='UserInactiveTimeout';
update lams_configuration set description_key='config.use.cache.debug.listener', header_name='config.header.system', format='BOOLEAN', required=1 where config_key='UseCacheDebugListener';
update lams_configuration set description_key='config.cleanup.preview.older.than.days', header_name='config.header.system', format='LONG', required=1 where config_key='CleanupPreviewOlderThanDays';
update lams_configuration set description_key='config.authoring.activities.colour', header_name='config.header.look.feel', format='BOOLEAN', required=1 where config_key='AuthoringActivitiesColour';
update lams_configuration set description_key='config.authoring.client.version', header_name='config.header.versions', format='STRING', required=1 where config_key='AuthoringClientVersion';
update lams_configuration set description_key='config.monitor.client.version', header_name='config.header.versions', format='STRING', required=1 where config_key='MonitorClientVersion';
update lams_configuration set description_key='config.learner.client.version', header_name='config.header.versions', format='STRING', required=1 where config_key='LearnerClientVersion';
update lams_configuration set description_key='config.server.version.number', header_name='config.header.versions', format='STRING', required=1 where config_key='ServerVersionNumber';
update lams_configuration set description_key='config.server.language', header_name='config.header.look.feel', format='STRING', required=1 where config_key='ServerLanguage';
update lams_configuration set description_key='config.server.page.direction', header_name='config.header.look.feel', format='STRING', required=1 where config_key='ServerPageDirection';
update lams_configuration set description_key='config.dictionary.date.created', header_name='config.header.versions', format='STRING', required=1 where config_key='DictionaryDateCreated';
update lams_configuration set description_key='config.help.url', header_name='config.header.system', format='STRING', required=1 where config_key='HelpURL';
update lams_configuration set description_key='config.xmpp.domain', header_name='config.header.chat', format='STRING', required=1 where config_key='XmppDomain';
update lams_configuration set description_key='config.xmpp.conference', header_name='config.header.chat', format='STRING', required=1 where config_key='XmppConference';
update lams_configuration set description_key='config.xmpp.admin', header_name='config.header.chat', format='STRING', required=1 where config_key='XmppAdmin';
update lams_configuration set description_key='config.xmpp.password', header_name='config.header.chat', format='STRING', required=1 where config_key='XmppPassword';
update lams_configuration set description_key='config.default.flash.theme', header_name='config.header.look.feel', format='STRING', required=1 where config_key='DefaultFlashTheme';
update lams_configuration set description_key='config.default.html.theme', header_name='config.header.look.feel', format='STRING', required=1 where config_key='DefaultHTMLTheme';
update lams_configuration set description_key='config.allow.direct.lesson.launch', header_name='config.header.features', format='BOOLEAN', required=1 where config_key='AllowDirectLessonLaunch';
update lams_configuration set description_key='config.community.enable', header_name='config.header.features', format='BOOLEAN', required=1 where config_key='LAMS_Community_enable';
update lams_configuration set description_key='config.allow.live.edit', header_name='config.header.features', format='BOOLEAN', required=1 where config_key='AllowLiveEdit';

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPProvisioningEnabled','false', 'config.ldap.provisioning.enabled', 'config.header.ldap', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPProviderURL','ldap://192.168.111.15', 'config.ldap.provider.url', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPSecurityAuthentication','simple', 'config.ldap.security.authentication', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPPrincipalDNPrefix','cn=', 'config.ldap.principal.dn.prefix', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPPrincipalDNSuffix',',ou=Users,dc=melcoe,dc=mq,dc=edu,dc=au', 'config.ldap.principal.dn.suffix', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPSecurityProtocol','', 'config.ldap.security.protocol', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPTruststorePath','', 'config.ldap.truststore.path', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPTruststorePassword','', 'config.ldap.truststore.password', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPLoginAttr','uid', 'admin.user.login', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPFNameAttr','givenName', 'admin.user.first_name', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPLNameAttr','sn', 'admin.user.last_name', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPEmailAttr','mail', 'admin.user.email', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPAddr1Attr','postalAddress', 'admin.user.address_line_1', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPAddr2Attr','', 'admin.user.address_line_2', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPAddr3Attr','', 'admin.user.address_line_3', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPCityAttr','l', 'admin.user.city', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPStateAttr','st', 'admin.user.state', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPPostcodeAttr','postalCode', 'admin.user.postcode', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPCountryAttr','', 'admin.user.country', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPDayPhoneAttr','telephoneNumber', 'admin.user.day_phone', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPEveningPhoneAttr','homePhone', 'admin.user.evening_phone', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPFaxAttr','facsimileTelephoneNumber', 'admin.user.fax', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPMobileAttr','mobile', 'admin.user.mobile_phone', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPLocaleAttr','preferredLanguage', 'admin.organisation.locale', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPDisabledAttr','!accountStatus', 'sysadmin.disabled', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPOrgAttr','schoolCode', 'admin.course', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPRolesAttr','memberOf', 'admin.user.roles', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPLearnerMap','Student;SchoolSupportStaff;Teacher;SeniorStaff;Principal', 'config.ldap.learner.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPMonitorMap','SchoolSupportStaff;Teacher;SeniorStaff;Principal', 'config.ldap.monitor.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPAuthorMap','Teacher;SeniorStaff;Principal', 'config.ldap.author.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPGroupAdminMap','Teacher;SeniorStaff', 'config.ldap.group.admin.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPGroupManagerMap','Principal', 'config.ldap.group.manager.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPUpdateOnLogin', 'true', 'config.ldap.update.on.login', 'config.header.ldap', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPOrgField', 'code', 'config.ldap.org.field', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPOnlyOneOrg', 'true', 'config.ldap.only.one.org', 'config.header.ldap', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPEncryptPasswordFromBrowser', 'true', 'config.ldap.encrypt.password.from.browser', 'config.header.ldap', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPSearchResultsPageSize', '100', 'config.ldap.search.results.page.size', 'config.header.ldap', 'LONG', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LearnerProgressBatchSize', '10', 'config.learner.progress.batch.size', 'config.header.look.feel', 'LONG', 1);

-- LDEV-1284 integration organisation no longer mandatory
alter table lams_ext_server_org_map modify column orgid bigint(20);

-- LDEV-1473 learner progress url for branching and sequence system tools
update lams_system_tool set learner_progress_url='monitoring/complexProgress.do' where system_tool_id > 5;

-- LDEV-1563 custom tab
insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('CustomTabLink','', 'config.custom.tab.link', 'config.header.look.feel', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('CustomTabTitle','', 'config.custom.tab.title', 'config.header.look.feel', 'STRING', 0);

-- LDEV-1005
alter table lams_user add column enable_flash TINYINT(1) DEFAULT 1;

-- LDEV-1579
alter table lams_tool add column supports_outputs TINYINT(1) DEFAULT 0;

