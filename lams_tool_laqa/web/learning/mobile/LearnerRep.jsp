<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>
<c:set var="sessionMap" value="${sessionScope[generalLearnerFlowDTO.httpSessionID]}" />

<lams:html>
<lams:head>
	<html:base />
	<title><fmt:message key="activity.title" /></title>

	<link rel="stylesheet" href="${lams}css/defaultHTML_learner_mobile.css" />
	<link rel="stylesheet" href="${lams}css/jquery.mobile.css" />
	<link rel="stylesheet" href="<html:rewrite page='/includes/css/jRating.jquery.css'/>"  type="text/css" />
	<link rel="stylesheet" href="<html:rewrite page='/includes/css/ratingStars.css'/>"  type="text/css" />

	<script type="text/javascript"> 
		var pathToImageFolder = "<html:rewrite page='/images/'/>"; 
	</script>
	<script src="${lams}includes/javascript/jquery.js"></script>
	<script type="text/javascript" src="<html:rewrite page='/includes/javascript/jRating.jquery.js'/>"></script>
	<script src="${lams}includes/javascript/jquery.mobile.js"></script>	
	<script language="JavaScript" type="text/JavaScript">
		function submitLearningMethod(actionMethod) {	
			if (actionMethod == 'endLearning') {
				$("#finishButton").attr("disabled", true);
			}
			document.QaLearningForm.method.value=actionMethod; 
			document.QaLearningForm.submit();
		}
		
		function submitMethod(actionMethod) {
			submitLearningMethod(actionMethod);
		}
	</script>
</lams:head>

<body>
<div data-role="page" data-cache="false">

	<div data-role="header" data-theme="b" data-nobackbtn="true">
		<h1>
			<c:out value="${generalLearnerFlowDTO.activityTitle}" escapeXml="false" />
		</h1>
	</div><!-- /header -->

	<div data-role="content">	
	
		<c:if test="${not empty sessionMap.submissionDeadline}">
			<div class="info">
				<fmt:message key="authoring.info.teacher.set.restriction" >
					<fmt:param><lams:Date value="${sessionMap.submissionDeadline}" /></fmt:param>
				</fmt:message>
			</div>
		</c:if>			
	
		<html:form action="/learning?validate=false" enctype="multipart/form-data" method="POST" target="_self">
			<html:hidden property="toolSessionID" styleId="toolSessionID"/>
			<html:hidden property="userID" />
			<html:hidden property="httpSessionID" />
			<html:hidden property="totalQuestionCount"/>
			
			<c:if test="${generalLearnerFlowDTO.requestLearningReportProgress != 'true'}">
				<c:choose>
					<c:when test="${generalLearnerFlowDTO.reflection != 'true'}">
						<html:hidden property="method" value="endLearning"/>
					</c:when>
					<c:otherwise>
						<html:hidden property="method" value="forwardtoReflection"/>
					</c:otherwise>
				</c:choose>
			</c:if>

			<c:if test="${generalLearnerFlowDTO.requestLearningReportProgress == 'true'}">
				<h2 class="space-bottom">
					<fmt:message key="label.learnerReport" />
				</h2>
				<html:hidden property="method" />
			</c:if>
			
			<ul data-role="listview" data-theme="c" id="qaQuestions" style="padding-top: 0;">
				<c:forEach var="currentDto"	items="${generalLearnerFlowDTO.listMonitoredAnswersContainerDTO}">
					<c:set var="currentQuestionId" scope="request" value="${currentDto.questionUid}" />

					<li>
						<p class="space-top">
							<strong> <fmt:message key="label.question" /> : </strong>
							<c:out value="${currentDto.question}" escapeXml="false" />
						</p>

						<c:forEach var="questionAttemptData" items="${currentDto.questionAttempts}">
							<c:forEach var="sData" items="${questionAttemptData.value}">
								<c:set var="userData" scope="request" value="${sData.value}" />
								<c:set var="responseUid" scope="request" value="${userData.uid}" />

								<c:if test="${generalLearnerFlowDTO.userUid == userData.queUsrId}">
									<c:if test="${currentQuestionId == userData.questionUid}">
										<p class="small-space-top">
											<span class="field-name"> <c:out value="${userData.userName}" /> </span> -
											<lams:Date value="${userData.attemptTime}" style="short"/>
										</p>
										<p class="small-space-bottom">
											<c:out value="${userData.responsePresentable}" escapeXml="false" />
										</p>
										<jsp:include page="parts/ratingStarsDisabled.jsp" />
									</c:if>
								</c:if>								
							</c:forEach>
						</c:forEach>
					</li>
				</c:forEach>				
			</ul>
		</html:form>
		
			<c:if test="${generalLearnerFlowDTO.existMultipleUserResponses == 'true'}">				
				<h2 style="padding-top: 20px; padding-bottom: 0;">
					<fmt:message key="label.other.answers" />
				</h2>
					
				<ul data-role="listview" data-theme="c" id="qaAnswers">
					<c:forEach var="currentDto"	items="${generalLearnerFlowDTO.listMonitoredAnswersContainerDTO}">
						<c:set var="currentQuestionId" scope="request"	value="${currentDto.questionUid}" />
			
						<li>
							<p class="space-top">
								<strong> <fmt:message key="label.question" /> : </strong>
								<c:out value="${currentDto.question}" escapeXml="false" />
							</p>
			
							<c:forEach var="questionAttemptData" items="${currentDto.questionAttempts}">
								<c:forEach var="sData" items="${questionAttemptData.value}">
									<c:set var="userData" scope="request" value="${sData.value}" />
									<c:set var="responseUid" scope="request" value="${userData.uid}" />
		
									<c:if test="${generalLearnerFlowDTO.userUid != userData.queUsrId}">	
										<c:if test="${currentQuestionId == userData.questionUid}">
											<p class="small-space-top">
												<span class="field-name"> <c:out value="${userData.userName}" /> </span>
												<c:if test="${generalLearnerFlowDTO.userNameVisible == 'true'}">																			
													<lams:Date value="${userData.attemptTime}" style="short"/>
												</c:if>																						
											</p>
											<p class="space-bottom">
												<c:choose>
													<c:when test="${userData.visible == 'true'}">
														<c:out value="${userData.responsePresentable}"	escapeXml="false" />
														<jsp:include page="parts/ratingStars.jsp" />
													</c:when>
													<c:otherwise>
														<i><fmt:message key="label.hidden"/></i>
													</c:otherwise>
												</c:choose>												
											</p>
										</c:if>
									</c:if>									
								</c:forEach>
							</c:forEach>
						</li>
					</c:forEach>
				</ul>
			</c:if>	
		
		<c:if test="${generalLearnerFlowDTO.requestLearningReportViewOnly != 'true' }">
			<c:if test="${generalLearnerFlowDTO.teacherViewOnly != 'true' }">
				<div class="button-inside">
					<button name="refreshAnswers" onclick="submitMethod('refreshAllResults');" data-icon="refresh">
						<fmt:message key="label.refresh" />
					</button>
	
					<c:if test="${generalLearnerFlowDTO.lockWhenFinished != 'true'}">
						<button name="redoQuestions" onclick="submitMethod('redoQuestions');" data-icon="back">
							<fmt:message key="label.redo" />
						</button>
					</c:if>
				</div>
			</c:if>	
		</c:if>
	</div>
	
	<div data-role="footer" data-theme="b" class="ui-bar">
		<c:if test="${generalLearnerFlowDTO.requestLearningReportViewOnly != 'true' }">
			<c:if test="${generalLearnerFlowDTO.teacherViewOnly != 'true' }">
				<span class="ui-finishbtn-right">
					<c:if test="${generalLearnerFlowDTO.reflection != 'true'}">
						<a href="#nogo" name="endLearning" id="finishButton" onclick="javascript:submitMethod('endLearning');" data-role="button" data-icon="arrow-r" data-theme="b">
							<span class="nextActivity"><fmt:message key="button.endLearning" /></span>
						</a>
					</c:if>

					<c:if test="${generalLearnerFlowDTO.reflection == 'true'}">
						<button name="forwardtoReflection"	onclick="javascript:submitMethod('forwardtoReflection');" data-icon="arrow-r" data-theme="b">
							<fmt:message key="label.continue" />
						</button>
					</c:if>
				</span>
			</c:if>
		</c:if>
		<c:if test="${generalLearnerFlowDTO.requestLearningReportViewOnly == 'true' }">
			<h2>&nbsp;</h2>
		</c:if>
	</div><!-- /footer -->

</div>
</body>
</lams:html>
