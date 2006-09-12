<%@ include file="/common/taglibs.jsp" %>
<c:set var="formBean" value="<%= request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY) %>" />

<!-- Advance Tab Content -->	
	<table class="forms">
		<!-- Instructions Row -->
		<tr>
			<td colspan="2 class="formcontrol">
				<html:checkbox property="survey.lockWhenFinished" styleClass="noBorder">
					<fmt:message key="label.authoring.advance.lock.on.finished" />
				</html:checkbox>
			</td>
		</tr>
		<tr>
			<td colspan="2 class="formcontrol">
				<html:checkbox property="survey.runAuto" styleClass="noBorder">
					<fmt:message key="label.authoring.advance.run.content.auto" />
				</html:checkbox>
			</td>
		</tr>
		<tr>
			<td class="formcontrol">
				<html:select property="survey.miniViewSurveyNumber" styleId="viewNumList">
					<c:forEach begin="1" end="${fn:length(surveyList)}" varStatus="status">
						<c:choose>
							<c:when test="${formBean.survey.miniViewSurveyNumber == status.index}">
								<option value="${status.index}" selected="true">${status.index}</option>
							</c:when>
							<c:otherwise>
								<option value="${status.index}">${status.index}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</html:select>
			</td>
			<td class="formcontrol"  width="97%">
					<fmt:message key="label.authoring.advance.mini.number.surveys.view" />
			</td>
		</tr>
		<tr>
			<td colspan="2 class="formcontrol">
				<html:checkbox property="survey.allowAddUrls" styleClass="noBorder">
					<fmt:message key="label.authoring.advance.allow.learner.add.urls" />
				</html:checkbox>
			</td>
		</tr>
		<tr>
			<td colspan="2 class="formcontrol">
				<html:checkbox property="survey.allowAddFiles" styleClass="noBorder">
					<fmt:message key="label.authoring.advance.allow.learner.add.files" />
				</html:checkbox>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<html:checkbox property="survey.reflectOnActivity" styleClass="noBorder"  styleId="reflectOn">
					<fmt:message key="label.authoring.advanced.reflectOnActivity" />
				</html:checkbox>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<lams:STRUTS-textarea property="survey.reflectInstructions" styleId="reflectInstructions" cols="30" rows="3" />
			</td>
		</tr>		
	</table>
