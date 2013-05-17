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
<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<!--options content goes here-->

<p>
	<c:out value="${mcGeneralLearnerFlowDTO.activityInstructions}" escapeXml="false" />
</p>

	<c:if test="${not empty mcLearnerStarterDTO.submissionDeadline}">
		<div class="info">
			<fmt:message key="authoring.info.teacher.set.restriction" >
				<fmt:param><lams:Date value="${mcLearnerStarterDTO.submissionDeadline}" /></fmt:param>
			</fmt:message>	
		</div>
	</c:if>	


<c:if
	test="${mcGeneralLearnerFlowDTO.retries == 'true' && mcGeneralLearnerFlowDTO.passMark != '0'}">

	<p>
		<fmt:message key="label.learner.message" />
		(
		<c:out value="${mcGeneralLearnerFlowDTO.passMark}" />
		)
	</p>


</c:if>



<c:forEach var="dto" varStatus="status" items="${requestScope.listQuestionCandidateAnswersDto}">

	<div class="shading-bg">
		<div style="overflow: auto;">
			<c:out value="${dto.question}" escapeXml="false" />

			<c:if test="${mcGeneralLearnerFlowDTO.showMarks == 'true'}">			
				[
				<strong><fmt:message key="label.mark" /></strong>
				<c:out value="${dto.mark}" />
				]
			</c:if>							
		</div>
	</div>

	<c:forEach var="ca" varStatus="status" items="${dto.candidateAnswerUids}">

		<div class="indent">
			<input type="radio" name="checkedCa${dto.questionUid}" class="noBorder" value="${dto.questionUid}-${ca.value}">

			<c:forEach var="caText" varStatus="status" items="${dto.candidateAnswers}">
				<c:if test="${ca.key == caText.key}">
					<c:out value="${caText.value}" escapeXml="false" />
		</div>
				</c:if>
			</c:forEach>
	</c:forEach>

</c:forEach>


<html:hidden property="donePreview" />

<div class="space-bottom-top align-right">
	<html:hidden property="continueOptionsCombined" value="Continue" />

	<html:button property="continueButton" styleClass="button" onclick="doSubmit();" styleId="continueButton">
		<fmt:message key="button.continue" />
	</html:button>
</div>

<!--options content ends here-->

