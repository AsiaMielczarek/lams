<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as 
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>

<c:set scope="request" var="lams">
	<lams:LAMSURL />
</c:set>
<c:set scope="request" var="tool">
	<lams:WebAppURL />
</c:set>

<lams:html>
<lams:head>
	<html:base />
	<lams:css />
	<title><fmt:message key="activity.title" />
	</title>

	<script language="JavaScript" type="text/JavaScript">
		function submitMethod(actionMethod) 
		{
			if (actionMethod == 'learnerFinished') {
				document.getElementById("finishButton").disabled = true;
			}
			document.VoteLearningForm.dispatch.value=actionMethod; 
			document.VoteLearningForm.submit();
		}
	</script>
</lams:head>

<body class="stripes">
	<div id="content">
		<h1>
			<c:out value="${voteGeneralLearnerFlowDTO.activityTitle}"
				escapeXml="false" />
		</h1>
		<html:form action="/learning?validate=false"
			enctype="multipart/form-data" method="POST" target="_self">
			<html:hidden property="dispatch" />
			<html:hidden property="toolSessionID" />
			<html:hidden property="userID" />
			<html:hidden property="revisitingUser" />
			<html:hidden property="previewOnly" />
			<html:hidden property="maxNominationCount" />
			<html:hidden property="allowTextEntry" />
			<html:hidden property="lockOnFinish" />
			<html:hidden property="reportViewOnly" />
			<html:hidden property="userEntry" />

			<p>
				<strong> <fmt:message key="label.learning.reportMessage" />
				</strong>
			</p>

			<ul>
				<c:forEach var="entry"
					items="${requestScope.mapGeneralCheckedOptionsContent}">
					<li>
						<c:out value="${entry.value}" escapeXml="false" />
					</li>


				</c:forEach>

				<c:if test="${not empty VoteLearningForm.userEntry}">
					<li>
						<c:out value="${VoteLearningForm.userEntry}" />
					</li>
				</c:if>
			</ul>

			<div class="space-bottom-top">
				<html:submit property="redoQuestions" styleClass="button"
					onclick="submitMethod('redoQuestions');">
					<fmt:message key="label.retake" />
				</html:submit>

				<c:choose>

				<c:when test="${VoteLearningForm.showResults=='true'}">
					<html:submit property="viewAllResults" styleClass="button"
						onclick="submitMethod('viewAllResults');">
						<fmt:message key="label.overAllResults" />
					</html:submit>
				</c:when>
				
				<c:when test="${voteGeneralLearnerFlowDTO.reflection != 'true'}">
					<html:submit property="learnerFinished" styleId="finishButton"
						onclick="javascript:submitMethod('learnerFinished');"
						styleClass="button">
						<fmt:message key="label.finished" />
					</html:submit>
				</c:when>

				<c:otherwise>
					<html:submit property="forwardtoReflection"
						onclick="javascript:submitMethod('forwardtoReflection');"
						styleClass="button">
						<fmt:message key="label.continue" />
					</html:submit>
				</c:otherwise>
				</c:choose>
				
			</div>
		</html:form>


	</div>
	<div id="footer"></div>
</body>
</lams:html>
