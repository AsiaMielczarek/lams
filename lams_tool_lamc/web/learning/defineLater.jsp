<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<lams:html>
<lams:head>
	<html:base />
	<lams:css />
	<title><fmt:message key="activity.title" /></title>
</lams:head>

<body class="stripes">

	<div id="content">
		<lams:DefineLater defineLaterMessageKey="error.defineLater" />
	</div>

	<div id="footer"></div>

</body>
</lams:html>
