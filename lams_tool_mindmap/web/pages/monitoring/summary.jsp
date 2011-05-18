<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams"><lams:LAMSURL/></c:set>

<link type="text/css" href="${lams}/css/jquery-ui-1.8.11.flick-theme.css" rel="stylesheet">
<link type="text/css" href="${lams}/css/jquery-ui-timepicker-addon.css" rel="stylesheet">

<script type="text/javascript" src="${lams}includes/javascript/jquery-1.5.1.min.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery-ui-1.8.11.custom.min.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery.blockUI.js"></script> 
<script type="text/javascript">
<!--
	var evalcomixWindow = null;
	
	function openEvalcomixWindow(url)
	{
    	evalcomixWindow=window.open(url, 'evalcomixWindow', 'width=800,height=600,scrollbars=yes,resizable=yes');
		if (window.focus) {evalcomixWindow.focus()}
	}
	
	var settings = {
		lams: '${lams}',
		submissionDeadline: '${submissionDeadline}',
		setSubmissionDeadlineUrl: '<c:url value="/monitoring.do?dispatch=setSubmissionDeadline"/>',
		toolContentID: '${param.toolContentID}',
		messageNotification: '<fmt:message key="monitor.summary.notification" />',
		messageRestrictionSet: '<fmt:message key="monitor.summary.date.restriction.set" />',
		messageRestrictionRemoved: '<fmt:message key="monitor.summary.date.restriction.removed" />'
	};
	
//-->
</script>
<script type="text/javascript" src="${lams}/includes/javascript/monitorToolSummaryAdvanced.js" ></script>

<c:set var="dto" value="${mindmapDTO}" />

<h1 style="padding-bottom: 10px;">
	<img src="<lams:LAMSURL/>/images/tree_closed.gif" id="treeIcon" onclick="javascript:toggleAdvancedOptionsVisibility(document.getElementById('advancedDiv'), document.getElementById('treeIcon'), '<lams:LAMSURL/>');" />

	<a href="javascript:toggleAdvancedOptionsVisibility(document.getElementById('advancedDiv'), document.getElementById('treeIcon'),'<lams:LAMSURL/>');" >
		<fmt:message key="monitor.summary.th.advancedSettings" />
	</a>
</h1>

<div class="monitoring-advanced" id="advancedDiv" style="display:none">
<table class="alternative-color">

	<tr>
		<td>
			<fmt:message key="advanced.lockOnFinished" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${dto.lockOnFinish}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="advanced.multiUserMode" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${dto.multiUserMode}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="advanced.reflectOnActivity" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${dto.reflectOnActivity}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="label.notebookInstructions" />
		</td>
		
		<td>
			${dto.reflectInstructions}
		</td>
	</tr>
	
</table>
</div>

<%@include file="dateRestriction.jsp"%>

<c:if test="${empty dto.sessionDTOs}">
	<p class="warning">
		<fmt:message key="label.nogroups" />
	</p>
</c:if>

<c:forEach var="session" items="${dto.sessionDTOs}">

	<c:if test="${isGroupedActivity}">
		<h2>
			${session.sessionName}
		</h2>
	</c:if>

	<table cellpadding="0">
		<tr>
			<td class="field-name" width="30%">
				<fmt:message key="heading.totalLearners" />
			</td>
			<td width="70%">
				${session.numberOfLearners}
			</td>
		</tr>
	</table>

	<table cellpadding="0">

		<tr>
			<th>
				<fmt:message key="heading.learner" />
			</th>
			<th>
				<fmt:message key="heading.mindmapEntry" />
			</th>
			<th>
				<fmt:message key="label.notebookEntry" />
			</th>
		</tr>

		<c:choose>
			<c:when test="${dto.multiUserMode}">
				
				<c:choose>
					<c:when test="${dto.reflectOnActivity == true}">
						<c:forEach var="user" items="${session.userDTOs}">
							<tr>
								<td width="30%">
									${user.firstName} ${user.lastName}
								</td>
						
								<td width="40%">
									<a href="./learning.do?mode=learner&amp;toolSessionID=${session.sessionID}&amp;monitor=true">
										<fmt:message key="label.view" />
									</a>
								</td>
						
								<td width="30%">
									<a href="./monitoring.do?dispatch=reflect&amp;userUID=${user.uid}&amp;toolContentID=${dto.toolContentId}">
										<fmt:message key="label.view" />
									</a>
								</td>
							</tr>
						</c:forEach>
					</c:when>
					
					<c:otherwise>
						<tr>
							<td width="30%">
								<fmt:message key="label.multimode" />
							</td>
					
							<td width="40%">
								<a href="./learning.do?mode=learner&amp;toolSessionID=${session.sessionID}&amp;monitor=true">
									<fmt:message key="label.view" />
								</a>
							</td>
					
							<td width="30%">
								<fmt:message key="label.notAvailable" />
							</td>
						</tr>
					</c:otherwise>
					
				</c:choose>
				
			</c:when>
			
			<c:otherwise>
				<c:forEach var="user" items="${session.userDTOs}">
					<tr>
						<td width="30%">
							${user.firstName} ${user.lastName}
						</td>
						
						<td width="40%">
							<c:choose>
								<c:when test="${user.finishedActivity != true}">
									<fmt:message key="label.notAvailable" />
								</c:when>
								<c:otherwise>
									<a href="./monitoring.do?dispatch=showMindmap&amp;userUID=${user.uid}&amp;toolContentID=${dto.toolContentId}&amp;contentFolderID=${contentFolderID}">
										<fmt:message key="label.view" />
									</a>									
								</c:otherwise>
							</c:choose>
						</td>
						
						<td width="30%">
							<c:choose>
								<c:when test="${dto.reflectOnActivity != true}">
									<fmt:message key="label.notAvailable" />
								</c:when>
								<c:otherwise>
									<a href="./monitoring.do?dispatch=reflect&amp;userUID=${user.uid}&amp;toolContentID=${dto.toolContentId}">
										<fmt:message key="label.view" />
									</a>
								</c:otherwise>
							</c:choose>
						</td>
						
					</tr>
				</c:forEach>
			</c:otherwise>
			
		</c:choose>

	</table>
</c:forEach>
