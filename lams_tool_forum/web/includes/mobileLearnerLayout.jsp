<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>

<%@ taglib uri="tags-tiles" prefix="tiles"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>
<lams:html>
	<lams:head>
		<title><fmt:message key="activity.title" /></title>
		
		<link rel="stylesheet" href="${lams}css/defaultHTML_learner_mobile.css" />

		<!-- ********************  javascript from header.jsp ********************** -->
		<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/tabcontroller.js"></script>
		<script type="text/javascript">
			function closeAndRefreshParentMonitoringWindow() {
				refreshParentMonitoringWindow();
				window.close();
			}  				
		</script>
		<!-- End of javascript from header.jsp -->
		
		<script type="text/javascript">
			var removeItemAttachmentUrl = "<html:rewrite page="/learning/deleteAttachment.do" />";
		</script>		
		<script type="text/javascript" src="${tool}includes/javascript/message.js"></script>
		<script type="text/javascript">
			function removeAtt(mapID){
				removeItemAttachmentUrl =  removeItemAttachmentUrl + "?sessionMapID="+ mapID;
				removeItemAttachment();
			}
		</script>	
		
		<link rel="stylesheet" href="${lams}css/jquery.mobile.css" />
		<script src="${lams}includes/javascript/jquery.js"></script>
		<script src="${lams}includes/javascript/jquery.mobile.js"></script>		
		
	</lams:head>
	<body class="large-font">
		<tiles:insert attribute="bodyMobile" />
	</body>
</lams:html>
