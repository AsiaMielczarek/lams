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
	<script type="text/javascript">
		function disableFinishButton() {
			var elem = document.getElementById("finishButton");
			if (elem != null) {
				elem.disabled = true;
			}
		}
	</script>
</lams:head>

<body class="stripes">

	<div id="content">

		<h1>
			<c:out value="${mcGeneralLearnerFlowDTO.activityTitle}" escapeXml="false" />
		</h1>

		<html:form action="/learning?method=displayMc&validate=false"
			method="POST" target="_self" onsubmit="disableFinishButton();">
			<html:hidden property="toolContentID" />
			<html:hidden property="toolSessionID" />
			<html:hidden property="httpSessionID" />
			<html:hidden property="userID" />
			<html:hidden property="userOverPassMark" />
			<html:hidden property="passMarkApplicable" />
			<html:hidden property="learnerProgress" />
			<html:hidden property="learnerProgressUserId" />
			<html:hidden property="questionListingMode" />


			<c:choose>
			<c:when test="${mcGeneralLearnerFlowDTO.retries == 'true'}">

				<h3>
					<fmt:message key="label.individual.results.withRetries" />
				</h3>

			</c:when>
			<c:when test="${mcGeneralLearnerFlowDTO.retries != 'true'}">

				<h3>
					<fmt:message key="label.individual.results.withoutRetries" />
				</h3>

			</c:when>
			</c:choose>
			
			<c:if test="${(mcGeneralLearnerFlowDTO.retries == 'true') or (mcGeneralLearnerFlowDTO.displayAnswers == 'true')}">
				<p>
					<strong><fmt:message key="label.mark" /> </strong>
					<c:out value="${mcGeneralLearnerFlowDTO.learnerMark}" />
					<fmt:message key="label.outof" />
					<c:out value="${mcGeneralLearnerFlowDTO.totalMarksPossible}" />
				</p>
			</c:if>
			
			<p>
				<c:if test="${mcGeneralLearnerFlowDTO.retries == 'true'}">
					<c:if
						test="${mcGeneralLearnerFlowDTO.userOverPassMark != 'true' && 
									mcGeneralLearnerFlowDTO.passMarkApplicable == 'true' }">

						<fmt:message key="label.notEnoughMarks">
							<fmt:param>
								<c:if test="${mcGeneralLearnerFlowDTO.passMark != mcGeneralLearnerFlowDTO.totalMarksPossible}">
									<fmt:message key="label.atleast" />
								</c:if>
								<c:out value="${mcGeneralLearnerFlowDTO.passMark}" />
								<fmt:message key="label.outof" />
								<c:out value="${mcGeneralLearnerFlowDTO.totalMarksPossible}" />
							</fmt:param>
						</fmt:message>

					</c:if>
				</c:if>
			</p>

			<h4>
				<fmt:message key="label.yourAnswers" />
			</h4>



			<c:forEach var="dto" varStatus="status"
				items="${requestScope.listSelectedQuestionCandidateAnswersDto}">

				<div class="shading-bg">

						<strong> <fmt:message key="label.question.col" /> </strong>
						
						<div style="overflow: auto;">
							<c:out value="${dto.question}" escapeXml="false" />
						</div>									

                    <c:if test="${mcGeneralLearnerFlowDTO.displayAnswers == 'true'}">
						[
						<strong> <fmt:message key="label.mark" /> </strong>
						<c:out value="${dto.mark}" /> 
						]
					</c:if>

						<p>
							<c:forEach var="caText" varStatus="status"
								items="${dto.candidateAnswers}">
								<c:out value="${caText.value}" escapeXml="false" />
							</c:forEach>

                    <c:if test="${mcGeneralLearnerFlowDTO.displayAnswers == 'true'}">
	
								<c:if test="${dto.attemptCorrect == 'true'}">
									<img src="<c:out value="${tool}"/>images/tick.gif" border="0" class="middle">
								</c:if>
								<c:if test="${dto.attemptCorrect != 'true'}">
									<img src="<c:out value="${tool}"/>images/cross.gif" border="0" class="middle">
								</c:if>
	
						</p>

						<c:if test="${(dto.feedback != null) && (dto.feedback != '')}">
							<div style="overflow: auto;">
								<strong> <fmt:message key="label.feedback.simple" /> </strong> <c:out value="${dto.feedback}" escapeXml="false" /> 
							</div>		
						</c:if>												
					</c:if>
				</div>
			</c:forEach>

			<c:if test="${mcGeneralLearnerFlowDTO.showMarks == 'true'}">
				<h4>
					<fmt:message key="label.group.results" />
				</h4>
				
				<table class="alternative-color" cellspacing="0">
				  <tr>
				  	<td width="30%"> 
					  	  <strong> <fmt:message key="label.topMark"/> </strong>
					 </td> 
					 <td>	
						  	 <c:out value="${mcGeneralLearnerFlowDTO.topMark}"/>
				  	</td>
				  </tr>	
	
				  <tr>
				  	<td> 
					  	 <strong><fmt:message key="label.avMark"/> </strong>
				  	</td>
				  	<td>
						  	<c:out value="${mcGeneralLearnerFlowDTO.averageMark}"/>
				  	</td>
				  </tr>	
				</table>
			</c:if>				
			


			<c:if test="${mcGeneralLearnerFlowDTO.retries == 'true'}">
				<p>
					<fmt:message key="label.learner.bestMark"/>
					<c:out value="${mcGeneralLearnerFlowDTO.latestAttemptMark}"/> 
					<fmt:message key="label.outof"/> 
					<c:out value="${mcGeneralLearnerFlowDTO.totalMarksPossible}"/> 
				</p>
				
				<html:submit property="redoQuestionsOk" styleClass="button">
					<fmt:message key="label.redo.questions" />
				</html:submit>

				<div class="space-bottom-top align-right">
					<c:if
						test="${((mcGeneralLearnerFlowDTO.passMarkApplicable == 'true') && (mcGeneralLearnerFlowDTO.userOverPassMark == 'true'))}">
						<c:if test="${mcGeneralLearnerFlowDTO.reflection != 'true'}">
							<html:hidden property="learnerFinished" value="Finished" />
							
							<html:submit styleClass="button" styleId="finishButton">
								<fmt:message key="label.finished" />
							</html:submit>
						</c:if>

						<c:if test="${mcGeneralLearnerFlowDTO.reflection == 'true'}">
							<html:submit property="forwardtoReflection" styleClass="button">
								<fmt:message key="label.continue" />
							</html:submit>
						</c:if>
					</c:if>
				</div>

			</c:if>

			<c:if test="${mcGeneralLearnerFlowDTO.retries != 'true'}">

				<div class="space-bottom-top align-right">
					<c:if test="${mcGeneralLearnerFlowDTO.reflection != 'true'}">
						<html:hidden property="learnerFinished" value="Finished" />
																  			  		
						<html:submit styleClass="button" styleId="finishButton">
							<fmt:message key="label.finished"/>
						</html:submit>
					</c:if>

					<c:if test="${mcGeneralLearnerFlowDTO.reflection == 'true'}">
						<html:submit property="forwardtoReflection" styleClass="button">
							<fmt:message key="label.continue" />
						</html:submit>
					</c:if>
				</div>

			</c:if>

		</html:form>
	</div>
</body>
</lams:html>

