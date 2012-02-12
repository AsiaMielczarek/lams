<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
<lams:html>
<lams:head>
	<%@ include file="/common/header.jsp"%>

	<script type="text/javascript">
	<!--
		function finishSession(){
			document.getElementById("finishButton").disabled = true;
			document.location.href ='<c:url value="/learning/finish.do?sessionMapID=${sessionMapID}&runOffline=true"/>';
			return false;
		}
		function continueReflect(){
			document.location.href='<c:url value="/learning/newReflection.do?sessionMapID=${sessionMapID}"/>';
		}
		
	-->        
    </script>
</lams:head>

<body class="stripes">
	<div id="content">
		<h1>
			${sessionMap.title}
		</h1>


		<c:choose>
			<c:when test="${empty sessionMap.submissionDeadline}">
					<p>
					<fmt:message key="run.offline.message" />
					</p>
			</c:when>
			<c:otherwise>
				<div class="warning">
					<fmt:message key="authoring.info.teacher.set.restriction" >
						<fmt:param><lams:Date value="${sessionMap.submissionDeadline}" /></fmt:param>
					</fmt:message>
				</div>
			</c:otherwise>
		</c:choose>									
			
		
		<c:if test="${sessionMap.userFinished and sessionMap.reflectOn}">
			<div class="small-space-top">
				<h2>${sessionMap.reflectInstructions}</h2>
			
				<c:choose>
					<c:when test="${empty sessionMap.reflectEntry}">
						<p>
							<em>
								<fmt:message key="message.no.reflection.available" />
							</em>
						</p>
					</c:when>
					<c:otherwise>
						<p> <lams:out escapeHtml="true" value="${sessionMap.reflectEntry}" />  </p>				
					</c:otherwise>
				</c:choose>
				
				<html:button property="FinishButton"
					onclick="return continueReflect()" styleClass="button">
					<fmt:message key="label.edit" />
				</html:button>											
			</div>
		</c:if>

		<div class="space-bottom-top align-right">
			<c:choose>
				<c:when
					test="${sessionMap.reflectOn && (not sessionMap.userFinished)}">
					<html:link href="#nogo" property="FinishButton"
						onclick="return continueReflect()" styleClass="button">
						<fmt:message key="label.continue" />
					</html:link>
				</c:when>
				<c:otherwise>
					<html:link href="#nogo" property="FinishButton" styleId="finishButton"
						onclick="return finishSession()" styleClass="button">
						<span class="nextActivity"><fmt:message key="label.finished" /></span>
					</html:link>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div id="footer">
	</div>
	<!--closes footer-->

</body>
</lams:html>
