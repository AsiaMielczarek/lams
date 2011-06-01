<%@ include file="/includes/taglibs.jsp"%>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<c:set var="lams">
 		<lams:LAMSURL />
</c:set>

<link type="text/css" href="${lams}/css/jquery-ui-1.8.11.flick-theme.css" rel="stylesheet">
<link type="text/css" href="${lams}/css/jquery-ui-timepicker-addon.css" rel="stylesheet">

<script type="text/javascript">
	//pass settings to monitorToolSummaryAdvanced.js
	var submissionDeadlineSettings = {
		lams: '${lams}',
		submissionDeadline: '${submissionDeadline}',
		setSubmissionDeadlineUrl: '<c:url value="/monitoring/setSubmissionDeadline.do"/>',
		toolContentID: '${param.toolContentID}',
		messageNotification: '<fmt:message key="monitor.summary.notification" />',
		messageRestrictionSet: '<fmt:message key="monitor.summary.date.restriction.set" />',
		messageRestrictionRemoved: '<fmt:message key="monitor.summary.date.restriction.removed" />'
	};
</script>
<script type="text/javascript" src="${lams}includes/javascript/jquery-1.5.1.min.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery-ui-1.8.11.custom.min.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery.blockUI.js"></script>  
<script type="text/javascript" src="${lams}/includes/javascript/monitorToolSummaryAdvanced.js" ></script>

<script type="text/javascript">
<!--

	var messageTargetDiv = "messageArea";
	function releaseMarks(sessionId){
		var url = "<c:url value="/monitoring/releaseMark.do"/>";
	    var reqIDVar = new Date();
		var param = "toolSessionID=" + sessionId +"&reqID="+reqIDVar.getTime();
		messageLoading();
	    var myAjax = new Ajax.Updater(
		    	messageTargetDiv,
		    	url,
		    	{
		    		method:'get',
		    		parameters:param,
		    		onComplete:messageComplete,
		    		evalScripts:true
		    	}
	    );
	}
	
	function showBusy(targetDiv){
		if($(targetDiv+"_Busy") != null){
			Element.show(targetDiv+"_Busy");
		}
	}
	function hideBusy(targetDiv){
		if($(targetDiv+"_Busy") != null){
			Element.hide(targetDiv+"_Busy");
		}				
	}
	function messageLoading(){
		showBusy(messageTargetDiv);
	}
	function messageComplete(){
		hideBusy(messageTargetDiv);
	}
//-->
</script>


<h1 style="padding-bottom: 10px;">
	<img src="<lams:LAMSURL/>/images/tree_closed.gif" id="treeIcon" onclick="javascript:toggleAdvancedOptionsVisibility(document.getElementById('advancedDiv'), document.getElementById('treeIcon'), '<lams:LAMSURL/>');" />

	<a href="javascript:toggleAdvancedOptionsVisibility(document.getElementById('advancedDiv'), document.getElementById('treeIcon'),'<lams:LAMSURL/>');" >
		<fmt:message key="monitor.summary.th.advancedSettings" />
	</a>
</h1>
<br />

<div class="monitoring-advanced" id="advancedDiv" style="display:none">

<table class="alternative-color">	

	<tr>
		<td>
			<fmt:message key="label.authoring.advance.lock.on.finished" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.lockWhenFinished == true}">
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
			<fmt:message key="label.authoring.advance.allow.edit" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.allowEdit == true}">
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
			<fmt:message key="label.authoring.advance.allow.rate.postings" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.allowRateMessages}">
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
			<fmt:message key="label.authoring.advance.allow.upload" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.allowUpload == true}">
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
			<fmt:message key="label.authoring.advance.use.richeditor" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.allowRichEditor == true}">
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
			<fmt:message key="label.authoring.advance.limited.input" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.limitedInput == true}">
					<fmt:message key="label.on" />, ${forum.limitedChar}
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="label.authoring.advance.allow.new.topics" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.allowNewTopic == true}">
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
			<fmt:message key="label.authoring.advance.number.reply" />
		</td>
		
		<td>
			<fmt:message key="label.authoring.advance.minimum.reply" />
			<c:choose>
				<c:when test="${forum.minimumReply != 0}">
					${forum.minimumReply}
				</c:when>
				<c:otherwise>
					<fmt:message key="label.authoring.advance.no.minimum" />
				</c:otherwise>
			</c:choose>
			<br />
			
			<fmt:message key="label.authoring.advance.maximum.reply" />
			<c:choose>
				<c:when test="${forum.maximumReply != 0}">
					${forum.maximumReply}
				</c:when>
				<c:otherwise>
					<fmt:message key="label.authoring.advance.no.maximum" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	
	<tr>
		<td>
			<fmt:message key="label.authoring.advanced.send.emails.to" /> <fmt:message key="label.authoring.advanced.learners" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.notifyLearnersOnForumPosting == true}">
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
			<fmt:message key="label.authoring.advanced.send.emails.to" /> <fmt:message key="label.authoring.advanced.teachers" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.notifyTeachersOnForumPosting == true}">
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
			<fmt:message key="label.authoring.advanced.notify.mark.release" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.notifyLearnersOnMarkRelease == true}">
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
			<fmt:message key="monitor.summary.td.addNotebook" />
		</td>
		
		<td>
			<c:choose>
				<c:when test="${forum.reflectOnActivity == true}">
					<fmt:message key="label.on" />
				</c:when>
				<c:otherwise>
					<fmt:message key="label.off" />
				</c:otherwise>
			</c:choose>	
		</td>
	</tr>
	
	<c:choose>
		<c:when test="${forum.reflectOnActivity == true}">
			<tr>
				<td>
					<fmt:message key="monitor.summary.td.notebookInstructions" />
				</td>
				<td>
					${forum.reflectInstructions}	
				</td>
			</tr>
		</c:when>
	</c:choose>
	
</table>
</div>

<%@include file="daterestriction.jsp"%>

<c:forEach var="element" items="${sessionUserMap}">
	<c:set var="toolSessionDto" value="${element.key}" />
	<c:set var="userlist" value="${element.value}" />

	<table cellpadding="0">
		<tr>
			<td colspan="3">
				<img src="${tool}/images/indicator.gif" style="display:none" id="messageArea_Busy" />
				<span id="messageArea"></span>
			</td>
		</tr>
		<c:if test="${isGroupedActivity}">	
			<tr>
				<td colspan="3" >
					<h2>
						<fmt:message key="message.session.name" />:	<c:out value="${toolSessionDto.sessionName}" />
					</h2>
				</td>
			</tr>
		</c:if>
	</table>
	<table cellpadding="0">
		<c:forEach var="user" items="${userlist}" varStatus="status">
			<c:if test="${status.first}">
				<tr>
					<th>
						<fmt:message key="monitoring.user.fullname"/>
					</th>
					<th>
						<fmt:message key="monitoring.user.loginname"/>
					</th>
					<c:if test="${user.hasRefection}">
						<th>
							<fmt:message key="monitoring.user.reflection"/>
						</th>
					</c:if>
					<th>
						<fmt:message key="monitoring.marked.question"/>
					</th>
					<th>&nbsp;</th>
				</tr>
			</c:if>
			<tr>
				<td>
					<c:out value="${user.fullName}" />
				</td>
				<td>
					<c:out value="${user.loginName}" />
				</td>
				<c:if test="${user.hasRefection}">
				<td>
						<c:set var="viewReflection">
							<c:url value="/monitoring/viewReflection.do?toolSessionID=${toolSessionDto.sessionID}&userUid=${user.userUid}"/>
						</c:set>
						<html:link href="javascript:launchPopup('${viewReflection}')">
							<fmt:message key="label.view" />
						</html:link>
				</td>
				</c:if>
				<td>
					<c:choose>
					<c:when test="${user.anyPostsMarked}">
						<fmt:message key="label.yes"/>
					</c:when>
					<c:otherwise>
						<fmt:message key="label.no"/>
					</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:url value="/monitoring/viewUserMark.do" var="viewuserurl">
						<c:param name="userID" value="${user.userUid}" />
						<c:param name="toolSessionID" value="${toolSessionDto.sessionID}" />
					</c:url>
					<html:link href="javascript:launchPopup('${viewuserurl}')" styleClass="button">
						<fmt:message key="lable.topic.title.mark" />
					</html:link>
				</td>
			</tr>
		</c:forEach>
		<c:if test="${empty userlist}">
			<tr>
				<td colspan="3">
					<b><fmt:message key="message.monitoring.summary.no.users" /></b>
				</td>
			</tr>
		</c:if>
 	 </table>

	<table cellpadding="0">
		<tr>
			<td>
				<div style="float:left;padding:5px;margin-left:5px">
					<html:form action="/learning/viewForum.do" target="_blank">
						<html:hidden property="mode" value="teacher"/>
						<html:hidden property="toolSessionID" value="${toolSessionDto.sessionID}" />
						<html:hidden property="hideReflection" value="true"/>
						<html:submit property="viewForum" styleClass="button">
							<fmt:message key="label.monitoring.summary.view.forum" />
						</html:submit>
					</html:form>
				</div>
				<!-- 
				<div style="float:left;padding:5px;margin-left:5px">
					<html:form action="/monitoring/viewAllMarks" target="_blank">
						<html:hidden property="toolSessionID" value="${toolSessionDto.sessionID}" />
						<html:submit property="Mark" styleClass="button">
							<fmt:message key="lable.topic.title.mark" />
						</html:submit>
					</html:form>
				</div>
				 -->
				<div style="float:left;padding:5px;margin-left:5px">
					<html:button property="releaseMarks" onclick="releaseMarks(${toolSessionDto.sessionID})" styleClass="button">
						<fmt:message key="button.release.mark" />
					</html:button>
				</div>
				<div style="float:left;padding:5px;margin-left:5px">
					<html:form action="/monitoring/downloadMarks">
						<html:hidden property="toolSessionID" value="${toolSessionDto.sessionID}" />
						<html:submit property="downloadMarks" styleClass="button">
							<fmt:message key="message.download.marks" />
						</html:submit>
					</html:form>
				</div>
				<div style="float:left;padding:9px">
					<c:url value="/monitoring.do" var="refreshMonitoring">
						<c:param name="contentFolderID" value="${contentFolderID}"/>
						<c:param name="toolContentID" value="${toolContentID}" />
					</c:url>
					<html:link href="${refreshMonitoring}" styleClass="button">
							<fmt:message key="label.refresh" />
					</html:link>
				</div>
			</td>
		</tr>
	</table>
</c:forEach>

<c:if test="${empty sessionUserMap}">
	<p>
		<fmt:message key="message.monitoring.summary.no.session" />
	</p>
</c:if>

