# Connection: ROOT LOCAL
# Host: localhost
# Saved: 2005-04-07 10:42:43
# 
INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
learning_library_id,
default_tool_content_id,
valid_flag,
grouping_support_type_id,
supports_run_offline_flag,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
define_later_url,
export_pfolio_learner_url,
export_pfolio_class_url,
contribute_url,
moderation_url,
language_file,
create_date_time
)
VALUES
(
'lasurv11',
'surveyService',
'Shared Surveys',
'Shared Surveys',
'sharedsurveys',
'1.1',
NULL,
NULL,
0,
2,
1,
'tool/lasurv11/learning/start.do?mode=learner',
'tool/lasurv11/learning/start.do?mode=author',
'tool/lasurv11/learning/start.do?mode=teacher',
'tool/lasurv11/authoring/start.do',
'tool/lasurv11/monitoring/summary.do',
'tool/lasurv11/definelater.do',
'tool/lasurv11/exportPortfolio?mode=learner',
'tool/lasurv11/exportPortfolio?mode=teacher',
'tool/lasurv11/contribute.do',
'tool/lasurv11/moderate.do',
'org.lamsfoundation.lams.tool.survey.ApplicationSurveys',
NOW()
)
