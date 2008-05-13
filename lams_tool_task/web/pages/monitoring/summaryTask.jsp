<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>
<lams:html>
	<lams:head>
		<title><fmt:message key="label.learning.title" />
		</title>
		<%@ include file="/common/header.jsp"%>
		<c:set var="item" value="${taskSummary.taskListItem}"/>
	</lams:head>
	
	<body class="stripes">
	
	
		<div id="content">

			<h2>
				<fmt:message key="label.monitoring.tasksummary.task.summary" />
			</h2>
				
			<div>
				<c:out value="${item.title}"></c:out>
				
				<c:if test="${item.required}">
						(<fmt:message key="label.monitoring.tasksummary.task.required.to.finish" />)
				</c:if>				
			</div>
			<br/>
				
			<c:if test="${item.childTask || item.commentsFilesAllowed}">
				<ul>
					<c:if test="${item.childTask}">
						<li>
							(<fmt:message key="label.monitoring.tasksummary.parent.activity" />: ${item.parentTaskName})
						</li>
					</c:if>
						
					<c:if test="${item.commentsFilesAllowed}">
						<li>
							<fmt:message key="label.monitoring.tasksummary.comments.files.enabled" />
						</li>
					</c:if>
				</ul>
			</c:if>
	
			<table cellpadding="0" class="alternative-color" >
			
				<tr>
					<th width="20%">
						<fmt:message key="label.monitoring.tasksummary.user" />
					</th>
					<th width="15%">
						<fmt:message key="label.monitoring.tasksummary.completed" />?
					</th>
					<th width="20%">
						<fmt:message key="label.monitoring.tasksummary.time.and.date" />
					</th>
					
					<c:if test="${item.commentsFilesAllowed}">
						<th width="45%" align="center">
							<fmt:message key="label.monitoring.tasksummary.comments.files" />
						</th>
					</c:if>			
				</tr>
			
			
				<c:forEach var="taskSummaryItem" items="${taskSummary.taskSummaryItems}">
				
					<tr>
						<td>
							${taskSummaryItem.user.loginName}
						</td>
						
						<td align="center">
							<c:choose>
								<c:when test="${taskSummaryItem.completed}">
									<img src="<html:rewrite page='/includes/images/completeitem.gif'/>"	border="0">
								</c:when>
									
								<c:otherwise>
									<img src="<html:rewrite page='/includes/images/incompleteitem.gif'/>" border="0">
								</c:otherwise>
							</c:choose>
						</td>
						
						<td>
							<lams:Date value="${taskSummaryItem.date}" />
						</td>
									
						<c:if test="${item.commentsFilesAllowed}">
							<td>
							
								<ul>
									<c:forEach var="comment" items="${taskSummaryItem.comments}">
										<li>
											<c:out value="${comment.comment}"></c:out>
										</li>
									</c:forEach>
									
									<c:forEach var="attachment" items="${taskSummaryItem.attachments}">
										<li>
											<c:out value="${attachment.fileName}" />

											<c:set var="downloadURL">
												<html:rewrite page="/download/?uuid=${attachment.fileUuid}&versionID=${attachment.fileVersionId}&preferDownload=true" />
											</c:set>
											<html:link href="${downloadURL}">
												<fmt:message key="label.download" />
											</html:link>

										</li>
									</c:forEach>
									
								</ul>
							
							
							
							
							</td>
						</c:if>
							
					</tr>
					
						
				</c:forEach>
			</table>
	
	
	
		</div>
		<!--closes content-->
	
		<div id="footer">
		</div>
		<!--closes footer-->
	
	</body>
</lams:html>
