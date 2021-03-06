<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<lams:html>
<lams:head>
	<lams:css />
	<title><fmt:message key="label.learner.error" /></title>
</lams:head>

<c:set var="title" scope="request">
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.js"></script>
	<fmt:message key="activity.title" />
</c:set>

<body class="stripes">
	<lams:Page type="learner" title="${title}">
		<div class="voffset10 pull-right">
			<%@ include file="/common/messages.jsp"%>
		</div>
		<div id="footer"></div>
	</lams:Page>
</body>
</lams:html>