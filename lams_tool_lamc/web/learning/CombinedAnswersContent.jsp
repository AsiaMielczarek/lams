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

<c:if test="${mcGeneralLearnerFlowDTO.retries == 'true' && mcGeneralLearnerFlowDTO.passMark != '0'}">

	<p>
		<fmt:message key="label.learner.message" />
		(
		<c:out value="${mcGeneralLearnerFlowDTO.passMark}" />
		)
	</p>

</c:if>

<c:forEach var="dto" varStatus="status" items="${requestScope.learnerAnswersDTOList}">

	<div class="shading-bg">
		<div style="overflow: auto;">
			<span class="float-left space-right">
				${dto.displayOrder})
			</span> 
			<c:out value="${dto.question}" escapeXml="false" />

			<c:if test="${mcGeneralLearnerFlowDTO.showMarks == 'true'}">			
				[
				<strong><fmt:message key="label.mark" /></strong>
				<c:out value="${dto.mark}" />
				]
			</c:if>							
		</div>
	</div>

	<table class="indent">
		<tbody>
			<c:forEach var="option" items="${dto.options}">
				<tr>
		
					<td>
						<input type="radio" name="checkedCa${dto.questionUid}" class="noBorder" value="${dto.questionUid}-${option.uid}"
							<c:if test="${option.selected}">checked="checked"</c:if>>
					</td>
					<td width="100%">
						<c:out value="${option.mcQueOptionText}" escapeXml="false" />
					</td>
					
				</tr>
			</c:forEach>
		</tbody>
	</table>

</c:forEach>

<html:hidden property="donePreview" />

<div class="space-bottom-top align-right">
	<html:hidden property="continueOptionsCombined" value="Continue" />

	<html:button property="continueButton" styleClass="button" onclick="doSubmit();" styleId="continueButton">
		<fmt:message key="button.continue" />
	</html:button>
</div>

<!--options content ends here-->

