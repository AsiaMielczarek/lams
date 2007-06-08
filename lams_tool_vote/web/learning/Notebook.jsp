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
	<title><fmt:message key="activity.title" />
	</title>

	<script language="JavaScript" type="text/JavaScript">
		function submitMethod(actionMethod) 
		{
			document.VoteLearningForm.dispatch.value=actionMethod; 
			document.VoteLearningForm.submit();
		}
	</script>

</lams:head>

<body class="stripes">
	<html:form action="/learning?validate=false"
		enctype="multipart/form-data" method="POST" target="_self">

		<html:hidden property="dispatch" />
		<html:hidden property="toolSessionID" />
		<html:hidden property="userID" />

		<div id="content">
			<h1>
				<c:out value="${voteGeneralLearnerFlowDTO.activityTitle}"
					escapeXml="false" />
			</h1>

			<p>
				<c:out value="${voteGeneralLearnerFlowDTO.reflectionSubject}"
					escapeXml="false" />
			</p>

			<html:textarea cols="60" rows="8" property="entryText"
				styleClass="text-area"></html:textarea>

			<div class="space-bottom-top align-right">
				<html:submit property="submitReflection"
					onclick="javascript:submitMethod('submitReflection');"
					styleClass="button">
					<fmt:message key="button.endLearning" />
				</html:submit>
			</div>

		</div>
	</html:form>
</body>
</lams:html>








