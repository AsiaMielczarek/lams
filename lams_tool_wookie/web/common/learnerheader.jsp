<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<lams:head>  
	<title>
		<fmt:message key="activity.title" />
	</title>
	<lams:css/>

	<link href="${lams}css/fckeditor_style.css" rel="stylesheet" type="text/css">

	<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
	<script type="text/javascript" src="${lams}fckeditor/fckeditor.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/fckcontroller.js"></script>
	<script type="text/javascript" src="${tool}includes/javascript/common.js"></script>
	<script type="text/javascript" src="${tool}includes/javascript/learning.js"></script>
</lams:head>
