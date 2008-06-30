update lams_configuration set config_value='${LAMS_VERSION}' where config_key='Version';
update lams_configuration set config_value='${LAMS_SERVER_VERSION}' where config_key='AuthoringClientVersion' or config_key='MonitorClientVersion' or config_key="LearnerClientVersion" or config_key='ServerVersionNumber';
update lams_configuration set config_value='${LAMS_LANGUAGE_VERSION}' where config_key='DictionaryDateCreated';

-- FOR 2.1 ONLY
update lams_configuration set config_value='' where config_key='SMTPServer';

-- LI-135 Configurations should not be overwritten by updater, commenting out
--update lams_configuration set config_value='${SERVER_URL}' where config_key='ServerURL';
--update lams_configuration set config_value='${TEMP_DIR}' where config_key='TempDir';
--update lams_configuration set config_value='${DUMP_DIR}' where config_key='DumpDir';
--update lams_configuration set config_value='${EAR_DIR}' where config_key='EARDir';
--update lams_configuration set config_value='${REPOSITORY_DIR}' where config_key='ContentRepositoryPath';
--update lams_configuration set config_value='${LOCALE}' where config_key='ServerLanguage';
--update lams_configuration set config_value='${LOCALE_DIRECTION}' where config_key='ServerPageDirection';
--update lams_configuration set config_value='${WILDFIRE_DOMAIN}' where config_key='XmppDomain';
--update lams_configuration set config_value='${WILDFIRE_CONFERENCE}' where config_key='XmppConference';
--update lams_configuration set config_value='${WILDFIRE_USER}' where config_key='XmppAdmin';
--update lams_configuration set config_value='${WILDFIRE_PASS}' where config_key='XmppPassword';
--update lams_user set login='${LAMS_USER}', password=sha1('${LAMS_PASS}') where user_id=1;
--update lams_user set locale_id=(select locale_id from lams_supported_locale where language_iso_code=(SELECT SUBSTRING_INDEX('${LOCALE}', '_', 1)) and country_iso_code=(SELECT SUBSTRING_INDEX('${LOCALE}', '_', -1))) where user_id=1;
