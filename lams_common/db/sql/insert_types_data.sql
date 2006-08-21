-- CVS ID: $Id$
INSERT INTO lams_privilege VALUES (1,'Z','do anything');
INSERT INTO lams_privilege VALUES (2,'A','add/remove/modify classes within the course');
INSERT INTO lams_privilege VALUES (3,'B','create running instances of sequences and assign those to a class');
INSERT INTO lams_privilege VALUES (4,'C','stop/start running sequences');
INSERT INTO lams_privilege VALUES (5,'D','monitor the progress of learners');
INSERT INTO lams_privilege VALUES (6,'E','participates in sequences');
INSERT INTO lams_privilege VALUES (7,'F','export their progress on each running sequence');
INSERT INTO lams_privilege VALUES (8,'G','write/create/delete permissions in course content folder');
INSERT INTO lams_privilege VALUES (9,'H','read course content folder');
INSERT INTO lams_privilege VALUES (10,'I','create new users');
INSERT INTO lams_privilege VALUES (11,'J','create guest users');
INSERT INTO lams_privilege VALUES (12,'K','change status of course');
INSERT INTO lams_privilege VALUES (13,'L','browse all users in the system');

INSERT INTO lams_role VALUES (1, 'SYSADMIN', 'LAMS System Adminstrator', NOW());
INSERT INTO lams_role VALUES (2, 'COURSE MANAGER', 'Course Manager', NOW());
INSERT INTO lams_role VALUES (3, 'AUTHOR', 'Authors Learning Designs', NOW());
INSERT INTO lams_role VALUES (4, 'STAFF', 'Member of Staff', NOW());
INSERT INTO lams_role VALUES (5, 'LEARNER', 'Student', NOW());
INSERT INTO lams_role VALUES (6, 'COURSE ADMIN', 'Course Administrator', NOW());

INSERT INTO lams_role_privilege VALUES (1,1,1);
INSERT INTO lams_role_privilege VALUES (2,2,2);
INSERT INTO lams_role_privilege VALUES (3,2,3);
INSERT INTO lams_role_privilege VALUES (4,2,4);
INSERT INTO lams_role_privilege VALUES (5,2,5);
INSERT INTO lams_role_privilege VALUES (6,2,8);
INSERT INTO lams_role_privilege VALUES (7,2,9);
INSERT INTO lams_role_privilege VALUES (8,2,10);
INSERT INTO lams_role_privilege VALUES (9,2,12);
INSERT INTO lams_role_privilege VALUES (10,2,13);
INSERT INTO lams_role_privilege VALUES (11,3,8);
INSERT INTO lams_role_privilege VALUES (12,3,9);
INSERT INTO lams_role_privilege VALUES (13,4,3);
INSERT INTO lams_role_privilege VALUES (14,4,4);
INSERT INTO lams_role_privilege VALUES (15,4,5);
INSERT INTO lams_role_privilege VALUES (16,5,6);
INSERT INTO lams_role_privilege VALUES (17,5,7);
INSERT INTO lams_role_privilege VALUES (18,6,2);
INSERT INTO lams_role_privilege VALUES (19,6,10);
INSERT INTO lams_role_privilege VALUES (20,6,12);
INSERT INTO lams_role_privilege VALUES (21,6,13);


INSERT INTO lams_auth_method_type VALUES(1, 'LAMS');
INSERT INTO lams_auth_method_type VALUES(2, 'WEB_AUTH');
INSERT INTO lams_auth_method_type VALUES(3, 'LDAP');

INSERT INTO lams_organisation_type VALUES(1, 'ROOT ORGANISATION', 'root all other organisations: controlled by Sysadmin');
INSERT INTO lams_organisation_type VALUES(2, 'COURSE ORGANISATION', 'main organisation level - equivalent to an entire course.');
INSERT INTO lams_organisation_type VALUES(3, 'CLASS', 'runtime organisation level - lessons are run for classes.');

INSERT INTO lams_organisation_state VALUES (1, 'ACTIVE');
INSERT INTO lams_organisation_state VALUES (2, 'HIDDEN');
INSERT INTO lams_organisation_state VALUES (3, 'ARCHIVED');
INSERT INTO lams_organisation_state VALUES (4, 'REMOVED');

INSERT INTO lams_grouping_type VALUES (1, 'RANDOM_GROUPING');
INSERT INTO lams_grouping_type VALUES (2, 'CHOSEN_GROUPING');
INSERT INTO lams_grouping_type VALUES (3, 'CLASS_GROUPING');

INSERT INTO lams_tool_session_type VALUES (1, 'NON_GROUPED');
INSERT INTO lams_tool_session_type VALUES (2, 'GROUPED');


INSERT INTO lams_learning_activity_type VALUES (1, 'TOOL');
INSERT INTO lams_learning_activity_type VALUES (2, 'GROUPING');
INSERT INTO lams_learning_activity_type VALUES (3, 'GATE_SYNCH');
INSERT INTO lams_learning_activity_type VALUES (4, 'GATE_SCHEDULE');
INSERT INTO lams_learning_activity_type VALUES (5, 'GATE_PERMISSION');
INSERT INTO lams_learning_activity_type VALUES (6, 'PARALLEL');
INSERT INTO lams_learning_activity_type VALUES (7, 'OPTIONS');
INSERT INTO lams_learning_activity_type VALUES (8, 'SEQUENCE');

INSERT INTO lams_gate_activity_level VALUES (1, 'LEARNER');
INSERT INTO lams_gate_activity_level VALUES (2, 'GROUP');
INSERT INTO lams_gate_activity_level VALUES (3, 'CLASS');

INSERT INTO lams_tool_session_state VALUES  (1, 'STARTED');
INSERT INTO lams_tool_session_state VALUES  (2, 'ENDED');


INSERT INTO lams_lesson_state VALUES (1, 'CREATED');
INSERT INTO lams_lesson_state VALUES (2, 'NOT_STARTED');
INSERT INTO lams_lesson_state VALUES (3, 'STARTED');
INSERT INTO lams_lesson_state VALUES (4, 'SUSPENDED');
INSERT INTO lams_lesson_state VALUES (5, 'FINISHED');
INSERT INTO lams_lesson_state VALUES (6, 'ARCHIVED');
INSERT INTO lams_lesson_state VALUES (7, 'REMOVED');

INSERT into lams_license VALUES (1, 'LAMS Recommended: CC Attribution-Noncommercial-ShareAlike 2.5', 'by-nc-sa', 'http://creativecommons.org/licenses/by-nc-sa/2.5/', 1, '/images/license/byncsa.jpg');
INSERT into lams_license VALUES (2, 'CC Attribution-No Derivatives 2.5', 'by-nd', 'http://creativecommons.org/licenses/by-nd/2.5/',0,'/images/license/bynd.jpg');
INSERT into lams_license VALUES (3, 'CC Attribution-Noncommercial-No Derivatives 2.5', 'by-nc-nd', 'http://creativecommons.org/licenses/by-nc-nd/2.5/',0, '/images/license/byncnd.jpg');
INSERT into lams_license VALUES (4, 'CC Attribution-Noncommercial 2.5', 'by-nc', 'http://creativecommons.org/licenses/by-nc/2.5/',0,'/images/license/bync.jpg');
INSERT into lams_license VALUES (5, 'CC Attribution-ShareAlike 2.5', 'by-sa', 'http://creativecommons.org/licenses/by-sa/2.5/',0,'/images/license/byncsa.jpg'); 
INSERT into lams_license VALUES (6, 'Other Licensing Agreement', 'other', '',0, '');

INSERT into lams_copy_type VALUES(1,'NONE');
INSERT into lams_copy_type VALUES(2,'LESSON');
INSERT into lams_copy_type VALUES(3,'PREVIEW');

INSERT into lams_workspace_folder_type VALUES (1, 'NORMAL');
INSERT into lams_workspace_folder_type VALUES (2, 'RUN SEQUENCES');

INSERT INTO lams_authentication_method VALUES (1, 1, 'LAMS-Database');
INSERT INTO lams_authentication_method VALUES (2, 2, 'Oxford-WebAuth');
INSERT INTO lams_authentication_method VALUES (3, 3, 'MQ-LDAP');

INSERT INTO lams_activity_category VALUES (1 ,'SYSTEM');
INSERT INTO lams_activity_category VALUES (2 ,'COLLABORATION');
INSERT INTO lams_activity_category VALUES (3 ,'ASSESSMENT');
INSERT INTO lams_activity_category VALUES (4 ,'CONTENT');
INSERT INTO lams_activity_category VALUES (5 ,'SPLIT');

INSERT INTO lams_grouping_support_type VALUES (1 ,'NONE');
INSERT INTO lams_grouping_support_type VALUES (2 ,'OPTIONAL');
INSERT INTO lams_grouping_support_type VALUES (3 ,'REQUIRED');

INSERT INTO lams_log_event_type VALUES (1, 'LEARNER_LESSON_START');
INSERT INTO lams_log_event_type VALUES (2, 'LEARNER_LESSON_FINISH');
INSERT INTO lams_log_event_type VALUES (3, 'LEARNER_LESSON_EXIT');
INSERT INTO lams_log_event_type VALUES (4, 'LEARNER_LESSON_RESUME');
INSERT INTO lams_log_event_type VALUES (5, 'LEARNER_ACTIVITY_START');
INSERT INTO lams_log_event_type VALUES (6, 'LEARNER_ACTIVITY_FINISH');

INSERT INTO lams_wkspc_fld_content_type VALUES (1,'FILE');
INSERT INTO lams_wkspc_fld_content_type VALUES (2,'PACKAGE');

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description, 
	learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url, 
	export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (1, 2, 'Grouping', 'All types of grouping including random and chosen.', 
	'learning/grouping.do?method=performGrouping', 'learning/grouping.do?method=performGrouping', 
	'learning/grouping.do?method=viewGrouping', 'learning/groupingExportPortfolio?mode=learner', 
	'learning/groupingExportPortfolio?mode=teacher', 
	'monitoring/grouping.do?method=startGrouping', 
	'monitoring/grouping.do?method=startGrouping', now() );

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description, 
	learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url, 
	export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (2, 3, 'Sync Gate', 'Gate: Synchronise Learners.', 
	'learning/gate.do?method=knockGate', 'learning/gate.do?method=knockGate', null, null, 
	'monitoring/gateExportPortfolio?mode=teacher', 'monitoring/gate.do?method=viewGate', 
	'monitoring/gate.do?method=viewGate', now()	);

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description, 
	learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url, 
	export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (3, 4, 'Schedule Gate', 'Gate: Opens/shuts at particular times.', 
	'learning/gate.do?method=knockGate', 'learning/gate.do?method=knockGate', null, null, 
	'monitoring/gateExportPortfolio?mode=teacher', 'monitoring/gate.do?method=viewGate', 
	'monitoring/gate.do?method=viewGate', now()	);

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description, 
	learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url, 
	export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (4, 5, 'Permission Gate', 'Gate: Opens under teacher or staff control.', 
	'learning/gate.do?method=knockGate', 'learning/gate.do?method=knockGate', null, null, 
	'monitoring/gateExportPortfolio?mode=teacher', 'monitoring/gate.do?method=viewGate', 
	'monitoring/gate.do?method=viewGate', now()	);

-- Supported Locales
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (1, 'en', 'AU', 'English (Australia)', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (2, 'es', 'ES', 'Español', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (3, 'mi', 'NZ', 'Māori', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (4, 'de', 'DE', 'Deutsch', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (5, 'zh', 'CN', '简体中文', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (6, 'fr', 'FR', 'Français', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (7, 'it', 'IT', 'Italiano', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (8, 'no', 'NO', 'Norsk', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (9, 'sv', 'SE', 'Svenska', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (10, 'ko', 'KR', '한국어', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (11, 'pl', 'PL', 'Polski', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (12, 'pt', 'BR', 'Português (Brasil)', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (13, 'hu', 'HU', 'Magyar', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (14, 'bg', 'BG', 'Български', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (15, 'cy', 'GB', 'Welsh (United Kingdom)', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (16, 'th', 'TH', 'Thai', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (17, 'el', 'GR', 'Ελληνικά', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (18, 'nl', 'BE', 'Nederlands (België)', 'RTL');
INSERT INTO lams_supported_locale (locale_id, language_iso_code, country_iso_code, description, direction) 
VALUES (19, 'ar', 'JO', 'عربي', 'LTR');

-- which current tool supports the old 1.0.x tools?
INSERT INTO lams_tool_import_support VALUES (1, 'lafrum11', 'messageboard');
INSERT INTO lams_tool_import_support VALUES (2, 'lamc11', 'simpleassessment');
INSERT INTO lams_tool_import_support VALUES (3, 'lanb11', 'noticeboard');
INSERT INTO lams_tool_import_support VALUES (4, 'lanb11', 'htmlnb');
INSERT INTO lams_tool_import_support VALUES (5, 'laqa11', 'qa');
INSERT INTO lams_tool_import_support VALUES (6, 'lasbmt11', 'reportsubmission');
INSERT INTO lams_tool_import_support VALUES (7, 'lachat11', 'chat');
INSERT INTO lams_tool_import_support VALUES (8, 'larsrc11', 'urlcontent');
INSERT INTO lams_tool_import_support VALUES (9, 'larsrc11', 'filecontent');
INSERT INTO lams_tool_import_support VALUES (10, 'larsrc11', 'singleresource');
INSERT INTO lams_tool_import_support VALUES (11, 'lavote11', 'ranking');
INSERT INTO lams_tool_import_support VALUES (12, 'lantbk11', 'journal');
--	private static final String TOOLDATA_TAGS_LOMS = "loms";
--	private static final String TOOLDATA_TAGS_RPT_MARK= "reportmarking";
--	private static final String TOOLDATA_TAGS_GROUPING = "group";
--	private static final String TOOLDATA_TAGS_GROUPREPORTING = "groupreporting";
--	private static final String TOOLDATA_TAGS_SIMPLE_ASSESSMENT = ;
--	private static final String TOOLDATA_TAGS_IMAGEGALLERY = "imagegallery"; 
--	private static final String TOOLDATA_TAGS_IMAGERANKING = "imageranking";
