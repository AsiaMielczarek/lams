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
	<title><fmt:message key="activity.title" />	</title>

	<script language="JavaScript" type="text/JavaScript">

	var noSelected = 0;
	var maxVotes = <c:out value="${VoteLearningForm.maxNominationCount}"/>; 
	var minVotes = <c:out value="${VoteLearningForm.minNominationCount}"/>; 
	function updateCount(clickedObj){
		var userEntry = 0;
		<c:if test="${VoteLearningForm.allowTextEntry == true}">	
			if(document.forms[0].userEntry.value != ""){
				userEntry = 1;
			}
		</c:if>
		if(clickedObj.checked){
			noSelected++;
		}else{
			noSelected--;
		}
		
		if((noSelected + userEntry) > maxVotes){
			clickedObj.checked = false;
			noSelected--;
			alertTooManyVotes(maxVotes);
		}
	
	}

	function validate(){
		var error = "";
		var userEntry = 0;
		<c:if test="${VoteLearningForm.allowTextEntry == true}">	
			if(document.forms[0].userEntry.value != ""){
				userEntry = 1;
			}
		</c:if>

		var numberOfVotes = noSelected + userEntry;
		if(numberOfVotes > maxVotes) {
			alertTooManyVotes(maxVotes);
			return false;
		} else if(numberOfVotes < minVotes) {
			var msg = "<fmt:message key="error.minNominationCount.not.reached"/> " + minVotes + " <fmt:message key="label.nominations"/>";
			alert(msg);
			return false;			
		} else if(numberOfVotes == 0) {
			alert("<fmt:message key="error.empty.selection"/>");
			return false;
		} else {
			return true;
		}
	}

	function alertTooManyVotes(maxVotes) {
		var msg = "<fmt:message key="error.maxNominationCount.reached"/> "+maxVotes+" <fmt:message key="label.nominations"/>";
		alert(msg);
	}
	</script>

	<script language="JavaScript" type="text/JavaScript">
		function submitMethod(actionMethod) 
		{
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

		<html:form onsubmit="return validate();"
			action="/learning?validate=false&dispatch=continueOptionsCombined"
			method="POST" target="_self">
			<html:hidden property="dispatch" />
			<html:hidden property="toolSessionID" />
			<html:hidden property="userID" />
			<html:hidden property="revisitingUser" />
			<html:hidden property="previewOnly" />
			<html:hidden property="maxNominationCount" />
			<html:hidden property="minNominationCount" />
			<html:hidden property="allowTextEntry" />
			<html:hidden property="lockOnFinish" />
			<html:hidden property="reportViewOnly" />
			<html:hidden property="showResults" />
			
			
			<c:if test="${not empty voteGeneralLearnerFlowDTO.submissionDeadline}">
				<div class="info">
					<fmt:message key="authoring.info.teacher.set.restriction" >
						<fmt:param><lams:Date value="${voteGeneralLearnerFlowDTO.submissionDeadline}" /></fmt:param>
					</fmt:message>	
				</div>
			</c:if>

			<c:if test="${voteGeneralLearnerFlowDTO.activityRunOffline == 'true'}">
				<div class="warning">
					<fmt:message key="label.learning.forceOfflineMessage" />
				</div>
			</c:if>

			<c:if test="${voteGeneralLearnerFlowDTO.maxNominationCountReached == 'true'}">
				<div class="warning">
					<fmt:message key="error.maxNominationCount.reached" />
					<c:out value="${voteGeneralLearnerFlowDTO.maxNominationCount}" />
					<fmt:message key="label.nominations" />
				</div>
			</c:if>

			<c:if test="${voteGeneralLearnerFlowDTO.activityRunOffline != 'true'}">

				<p>
					<c:out value="${voteGeneralLearnerFlowDTO.activityInstructions}"
						escapeXml="false" />
				</p>

				<c:if test="${voteGeneralLearnerFlowDTO.maxNominationCount > 1}">
					<p>&nbsp;</p>
					<p>
						<fmt:message key="label.nominations.available">
							<fmt:param>
								<c:out value="${voteGeneralLearnerFlowDTO.maxNominationCount}" />
							</fmt:param>
						</fmt:message>
					</p>
				</c:if>

				<table class="shading-bg">

					<c:forEach var="subEntry" varStatus="status"
						items="${requestScope.mapQuestionContentLearner}">

						<tr>
							<td width="50px">

								<input type="checkbox" name="checkedVotes" class="noBorder"
									value="${subEntry.key}" onClick="updateCount(this);">

							</td>

							<td>

								<c:out value="${subEntry.value}" escapeXml="false" />

							</td>
					</c:forEach>
				</table>

				<c:if test="${VoteLearningForm.allowTextEntry == 'true'}">
					<strong> <fmt:message key="label.other" />: </strong>
					<html:text property="userEntry" size="30" maxlength="100" />
				</c:if>

				<html:hidden property="donePreview" />

				<div class="space-bottom-top">
					<html:submit property="continueOptionsCombined" styleClass="button">
						<fmt:message key="label.submit.vote" />
					</html:submit>
				</div>
				
			</c:if>

		</html:form>
	</div>

	<div id="footer"></div>

</body>
</lams:html>
