<%@ include file="/common/taglibs.jsp"%>
<c:set var="formBean" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />

<script lang="javascript">
	$(document).ready(function(){
		$("#attemptsAllowedRadio").click(function() {
			$("#passingMark").val("0");
			$("#passingMark").prop("disabled", true);
			$("#attemptsAllowed").prop("disabled", false);
		});
		
		$("#passingMarkRadio").click(function() {
			$("#attemptsAllowed").val("0");
			$("#attemptsAllowed").prop("disabled", true);
			$("#passingMark").prop("disabled", false);
		});
		
		<c:if test="${formBean.assessment.passingMark == 0}">$("#passingMark").prop("disabled", true);</c:if>
		<c:if test="${formBean.assessment.passingMark > 0}">$("#attemptsAllowed").prop("disabled", true);</c:if>
	});

</script>

<!-- Advance Tab Content -->
<p class="small-space-top">
	<html:text property="assessment.timeLimit" size="3" styleId="timeLimit"></html:text>
	<label for="timeLimit">
		<fmt:message key="label.authoring.advance.time.limit" />
	</label>
</p>

<p>
	<html:select property="assessment.questionsPerPage">
		<html:option value="0"><fmt:message key="label.authoring.advance.all.in.one.page" /></html:option>
		<html:option value="10">10</html:option>
		<html:option value="9">9</html:option>
		<html:option value="8">8</html:option>
		<html:option value="7">7</html:option>
		<html:option value="6">6</html:option>
		<html:option value="5">5</html:option>
		<html:option value="4">4</html:option>
		<html:option value="3">3</html:option>
		<html:option value="2">2</html:option>
		<html:option value="1">1</html:option>
	</html:select>
	<fmt:message key="label.authoring.advance.questions.per.page" />
</p>

<p>
	<html:checkbox property="assessment.shuffled" styleClass="noBorder" styleId="shuffled">
	</html:checkbox>
	<label for="shuffled">
		<fmt:message key="label.authoring.advance.shuffle.questions" />
	</label>
</p>

<p>
	<label>
		<fmt:message key="label.authoring.advance.choose.restriction" />
	</label>
	<br><br>
	
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="radio" name="isAttemptsChosen" value="${true}" id="attemptsAllowedRadio"
		<c:if test="${formBean.assessment.passingMark == 0}">checked="checked"</c:if> 
	/>
	<fmt:message key="label.authoring.advance.attempts.allowed" />
	<html:select property="assessment.attemptsAllowed" styleId="attemptsAllowed" >
		<html:option value="0"><fmt:message key="label.authoring.advance.unlimited" /></html:option>
		<html:option value="6">6</html:option>
		<html:option value="5">5</html:option>
		<html:option value="4">4</html:option>
		<html:option value="3">3</html:option>
		<html:option value="2">2</html:option>
		<html:option value="1">1</html:option>
	</html:select>
	<br><br>
	
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="radio" name="isAttemptsChosen" value="${false}" id="passingMarkRadio"	
		<c:if test="${formBean.assessment.passingMark > 0}">checked="checked"</c:if> 
	/>
	<fmt:message key="label.authoring.advance.passing.mark" />
	<html:select property="assessment.passingMark" styleId="passingMark" >
	</html:select>
	
</p>

<p>
	<html:checkbox property="assessment.allowOverallFeedbackAfterQuestion" styleClass="noBorder" styleId="allowOverallFeedbackAfterQuestion">
	</html:checkbox>
	<label for="allowOverallFeedbackAfterQuestion">
		<fmt:message key="label.authoring.advance.allow.students.overall.feedback" />
	</label>
</p>

<p>
	<html:checkbox property="assessment.allowQuestionFeedback" styleClass="noBorder" styleId="allowQuestionFeedback">
	</html:checkbox>
	<label for="allowQuestionFeedback">
		<fmt:message key="label.authoring.advance.allow.students.question.feedback" />
	</label>
</p>

<p>
	<html:checkbox property="assessment.allowRightAnswersAfterQuestion" styleClass="noBorder" styleId="allowRightAnswersAfterQuestion">
	</html:checkbox>
	<label for="allowRightAnswersAfterQuestion">
		<fmt:message key="label.authoring.advance.allow.students.right.answers" />
	</label>
</p>

<p>
	<html:checkbox property="assessment.allowWrongAnswersAfterQuestion" styleClass="noBorder" styleId="allowWrongAnswersAfterQuestion">
	</html:checkbox>
	<label for="allowWrongAnswersAfterQuestion">
		<fmt:message key="label.authoring.advance.allow.students.wrong.answers" />
	</label>
</p>

<p>
	<html:checkbox property="assessment.allowGradesAfterAttempt" styleClass="noBorder" styleId="allowGradesAfterAttempt">
	</html:checkbox>
	<label for="allowGradesAfterAttempt">
		<fmt:message key="label.authoring.advance.allow.students.grades" />
	</label>
</p>

<p>
	<html:checkbox property="assessment.allowHistoryResponses" styleClass="noBorder" styleId="allowHistoryResponsesAfterAttempt">
	</html:checkbox>
	<label for="allowHistoryResponsesAfterAttempt">
		<fmt:message key="label.authoring.advance.allow.students.history.responses" />
	</label>
</p>

<p>
	<html:checkbox property="assessment.displaySummary" styleClass="noBorder" styleId="displaySummary">
	</html:checkbox>
	<label for="displaySummary">
		<fmt:message key="label.authoring.advance.display.summary" />
	</label>
</p>

<p>
	<html:checkbox property="assessment.notifyTeachersOnAttemptCompletion"
		styleClass="noBorder" styleId="notifyTeachersOnAttemptCompletion">
	</html:checkbox>
	<label for="notifyTeachersOnAttemptCompletion">
		<fmt:message key="label.authoring.advanced.notify.on.attempt.completion" />
	</label>
</p>

<p>
	<html:checkbox property="assessment.reflectOnActivity" styleClass="noBorder" styleId="reflectOnActivity"/>
	<label for="reflectOnActivity">
		<fmt:message key="advanced.reflectOnActivity" />
	</label>
</p>

<p>
	<html:textarea property="assessment.reflectInstructions" styleId="reflectInstructions" cols="30" rows="3" />
</p>

<!-- Overall feedback -->
<input type="hidden" name="overallFeedbackList" id="overallFeedbackList" />
<p>
	<iframe 
		id="advancedInputArea" name="advancedInputArea"
		style="width:650px;height:100px;border:0px;display:block;" frameborder="no" scrolling="no" src="<c:url value='/authoring/initOverallFeedback.do'/>?sessionMapID=${formBean.sessionMapID}">
	</iframe>
</p>

<script type="text/javascript">
	<!--
	//automatically turn on refect option if there are text input in refect instruction area
		var ra = document.getElementById("reflectInstructions");
		var rao = document.getElementById("reflectOnActivity");
		function turnOnRefect(){
			if(isEmpty(ra.value)){
			//turn off	
				rao.checked = false;
			}else{
			//turn on
				rao.checked = true;		
			}
		}
	
		ra.onkeyup=turnOnRefect;
	//-->
</script>

