<%@ include file="/common/taglibs.jsp"%>
<div id="addRecordDiv">

<c:if test="${not empty param.sessionMapID}">
	<c:set var="sessionMapID" value="${param.sessionMapID}" />
</c:if>

<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
<c:set var="daco" value="${sessionMap.daco}" />
<%-- If the view is horizontal or vertical --%>
<c:set var="horizontal" value="${sessionMap.learningView=='horizontal'}" />
<%-- To display A) B) C) in answer options instead of 1) 2) 3) --%>
<c:set var="ordinal"><fmt:message key="label.authoring.basic.answeroption.ordinal"/></c:set>
<c:set var="finishedLock" value="${sessionMap.finishedLock}" />

<%@ include file="/common/messages.jsp"%>
<%-- The status of the last add/edit operation. --%>
<c:if test="${recordOperationSuccess=='add'}">
	<div class="info"><fmt:message key="message.learning.addrecordsuccess" /></div>
</c:if>
<c:if test="${recordOperationSuccess=='edit'}">
	<div class="info"><fmt:message key="message.learning.editrecordsuccess" /></div>
</c:if>
<table>
	<tr>
		<td>
		<h1>${daco.title}</h1>
		</td>
	</tr>
	<tr>
		<td>${daco.instructions}</td>
	</tr>
</table>
<c:if test="${not finishedLock }">

	<p class="hint">
		<fmt:message key="label.learning.heading.recordnumber" />
		<span id="displayedRecordNumberSpan" class="hint">${displayedRecordNumber}</span>
	</p>
	<!-- Form to add/edit a record -->
	<html:form action="learning/saveOrUpdateRecord" method="post" styleId="recordForm" enctype="multipart/form-data">
	
	<c:set var="fileNumber" value="0" />
	<c:set var="answerIndex" value="0" />
	
	<html:hidden property="sessionMapID" value="${sessionMapID}" />
	<html:hidden styleId="displayedRecordNumber" property="displayedRecordNumber" value="${displayedRecordNumber}" />
	
		<table cellspacing="0" class="alternative-color">
			<c:forEach var="question" items="${daco.dacoQuestions}" varStatus="questionStatus">
				<tr>
					<td>
					<div class="bigNumber">${questionStatus.index+1}</div>
					${question.description}
					<c:choose>
						<%-- The content varies depending on the question type --%>
						<c:when test="${question.type==1}"><%-- Single line text --%>
							<div class="hint"><fmt:message key="label.learning.textfield.hint" /></div>		
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<c:choose>
								<%-- Textfield entry length is limited 
									depending on the maximum number of characters the teacher provided
								--%>
								<c:when test="${question.max!=null}">
									<html:text property="answer[${answerIndex}]" size="60" maxlength="${question.max}" />
								</c:when>
								<c:otherwise>
									<html:text property="answer[${answerIndex}]" size="60" />
								</c:otherwise>
							</c:choose>
							<c:set var="answerIndex" value="${answerIndex+1}" />
						</c:when>
						<c:when test="${question.type==2}"><%-- Multi-line text --%>
							<div class="hint"><fmt:message key="label.learning.textarea.hint" /></div>
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<html:textarea property="answer[${answerIndex}]" cols="50" rows="3" />
							<c:set var="answerIndex" value="${answerIndex+1}" />
						</c:when>
						<c:when test="${question.type==3}"><%-- Number --%>
							<div class="hint"><fmt:message key="label.learning.number.hint" />
							<c:if test="${not empty question.digitsDecimal}">
								<br />
								<%-- An information for the learner is displayed,
									if the number he provides will be rounded to the number of places after the decimal point,
									as stated by the teacher. --%>
								<fmt:message key="label.learning.number.decimal">
									<fmt:param value="${question.digitsDecimal}" />
								</fmt:message>
							</c:if>
							</div>
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<html:text property="answer[${answerIndex}]" size="10" />
							<c:set var="answerIndex" value="${answerIndex+1}" />
						</c:when>
						<c:when test="${question.type==4}"><%-- Date can be entered in three textfields --%>
							<div class="hint"><fmt:message key="label.learning.date.hint" /></div>
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<label><fmt:message key="label.learning.date.day" /></label>
							<html:text property="answer[${answerIndex}]" size="3" />
							
							<c:set var="answerIndex" value="${answerIndex+1}" />
							<label><fmt:message key="label.learning.date.month" /></label>
							<html:text property="answer[${answerIndex}]" size="3" />
							
							<c:set var="answerIndex" value="${answerIndex+1}" />
							<label><fmt:message key="label.learning.date.year" /></label>
							<html:text property="answer[${answerIndex}]" size="5" />
							
							<c:set var="answerIndex" value="${answerIndex+1}" />
						</c:when>
						<c:when test="${question.type==5}"><%-- File --%>
							<div class="hint"><fmt:message key="label.learning.file.hint" /></div>
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<html:file styleId="file-${fileNumber+1}" property="file[${fileNumber}]" size="50" />
							<c:set var="fileNumber" value="${fileNumber+1}" />
						</c:when>
						<c:when test="${question.type==6}"><%-- Image --%>
							<div class="hint"><fmt:message key="label.learning.image.hint" /></div>
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<html:file styleId="file-${fileNumber+1}" property="file[${fileNumber}]" size="50" />
							<c:set var="fileNumber" value="${fileNumber+1}" />
						</c:when>
						<c:when test="${question.type==7}"><%-- Radio buttons  --%>
							<div class="hint"><fmt:message key="label.learning.radio.hint" /></div>
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<c:forEach var="answerOption" items="${question.answerOptions}" varStatus="status">
							<%-- It displays for example A) instead of 1) --%>
							${fn:substring(ordinal,status.index,status.index+1)}) <html:radio property="answer[${answerIndex}]" value="${status.index+1}">${answerOption.answerOption}</html:radio><br />
							</c:forEach>
							<c:set var="answerIndex" value="${answerIndex+1}" />
						</c:when>
						<c:when test="${question.type==8}"><%-- Dropdown menu --%>
							<div class="hint"><fmt:message key="label.learning.dropdown.hint" /></div>
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<html:select property="answer[${answerIndex}]">
							<html:option value="0"><fmt:message key="label.learning.dropdown.select" /></html:option>
							<c:forEach var="answerOption" items="${question.answerOptions}" varStatus="status">
								<html:option value="${status.index+1}">${answerOption.answerOption}</html:option>
							</c:forEach>
							</html:select>
							<c:set var="answerIndex" value="${answerIndex+1}" />
						</c:when>
						<c:when test="${question.type==9}"><%-- Checkboxes --%>
							<div class="hint"><fmt:message key="label.learning.checkbox.hint" /></div>
							<c:if test="${horizontal}">
								</td><td style="vertical-align: middle;">
							</c:if>
							<html:hidden  styleId="checkbox-${questionStatus.index+1}" property="answer[${answerIndex}]" />
							<c:forEach var="answerOption" items="${question.answerOptions}" varStatus="status">
							${fn:substring(ordinal,status.index,status.index+1)})
							<input type="checkbox" id="checkbox-${questionStatus.index+1}-${status.index+1}" value="${status.index+1}">
								${answerOption.answerOption}
							</input><br />
							</c:forEach>
							<c:set var="answerIndex" value="${answerIndex+1}" />
						</c:when>
						<c:when test="${question.type==10}"><%-- Longitude/latitude --%>
							<div class="hint"><fmt:message key="label.learning.longlat.hint" /></div>
							<c:if test="${horizontal}">
								</td>
								<td>
							</c:if>
							<table class="alternative-color-inner-table">
								<tr>
									<td width="80px">
									<label><fmt:message key="label.learning.longlat.longitude" /></label>
									</td>
									<td>
									<html:text property="answer[${answerIndex}]" size="10" />
									<label><fmt:message key="label.learning.longlat.longitude.unit" /></label><br />
									</td>									
								</tr>
								<c:set var="answerIndex" value="${answerIndex+1}" />
								<tr>
									<td>
									<label><fmt:message key="label.learning.longlat.latitude" /></label>
									</td>
									<td>
									<html:text property="answer[${answerIndex}]" size="10" />
									<label><fmt:message key="label.learning.longlat.latitude.unit" /></label><br />
									</td>
								</tr>
							</table>
							<c:set var="answerIndex" value="${answerIndex+1}" />
						</c:when>
					</c:choose>
					</td>
				</tr>
			</c:forEach>
		</table>
		<div class="button-add-div">
		<lams:ImgButtonWrapper>
			<a href="#" onclick="javascript:saveOrUpdateRecord()" class="button-add-item"><fmt:message key="label.learning.add" />
			</a>
		</lams:ImgButtonWrapper>
		</div>
	</html:form>
</c:if>
<c:if test="${sessionMap.userFinished and daco.reflectOnActivity}">
	<%-- Buttons that either move onto the next activity or display the reflection screen --%>
	<p class="small-space-top">
		<h2 style="padding-left: 20px;">${daco.reflectInstructions}</h2>
		<div class="button-add-div">
			<c:choose>
				<c:when test="${empty sessionMap.reflectEntry}">
					<em><fmt:message key="message.no.reflection.available" /></em>
				</c:when>
				<c:otherwise>
					<lams:out escapeHtml="true" value="${sessionMap.reflectEntry}" />
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${mode != 'teacher'}">
			<div class="small-space-top" style="padding-left: 20px;">
			<html:button property="FinishButton" onclick="javascript:continueReflect()" styleClass="button space-left">
				<fmt:message key="label.common.edit" />
			</html:button>
			</div>
		</c:if>
	</p>
</c:if>
</div>