<master>
<property name="title">@title@</property>
<property name="context">@context@</property>

<p>
<a title="Add a LAMS lesson to this course" href="add" class="button"> Add a LAMS Lesson </a>
&nbsp;&nbsp;

<a title="Open LAMS Authoring to create or edit lessons" href="javascript:;" onClick="window.open('@lams_server_url@/LoginRequest?uid=@username@&method=author&ts=@datetime@&sid=@server_id@&hash=@hashauthor@&courseid=@course_id@&country=AU&lang=EN&requestSrc=@requestSrc@&notifyCloseURL=@notifyCloseURL@&coursename=@course_name@&customCSV=@username@,@course_id@,@course_url@','LAMS_Author','height=650,width=996,resizable');" class="button"> LAMS Author </a>
&nbsp;&nbsp;

</p>

<listtemplate name="d_lesson"></listtemplate>
