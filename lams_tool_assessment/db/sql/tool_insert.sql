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
help_url,
language_file,
create_date_time,
modified_date_time,
supports_outputs
)
VALUES
(
'laasse10',
'assessmentService',
'Shared Assessment',
'Shared Assessment',
'sharedassessment',
'@tool_version@',
NULL,
NULL,
0,
2,
1,
'tool/laasse10/learning/start.do?mode=learner',
'tool/laasse10/learning/start.do?mode=author',
'tool/laasse10/learning/start.do?mode=teacher',
'tool/laasse10/authoring/start.do',
'tool/laasse10/monitoring/summary.do',
'tool/laasse10/definelater.do',
'tool/laasse10/exportPortfolio?mode=learner',
'tool/laasse10/exportPortfolio?mode=teacher',
'tool/laasse10/contribute.do',
'tool/laasse10/moderate.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/laasse10',
'org.lamsfoundation.lams.tool.assessment.ApplicationResources',
NOW(),
NOW(),
1
)
