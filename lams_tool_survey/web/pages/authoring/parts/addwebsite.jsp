<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<%@ include file="/common/header.jsp"%>
		<script type="text/javascript">
	   <%-- user for  surveysurveyitem.js --%>
	   var removeInstructionUrl = "<c:url value='/authoring/removeInstruction.do'/>";
       var addInstructionUrl = "<c:url value='/authoring/newInstruction.do'/>";
       var removeItemAttachmentUrl = "<c:url value='/authoring/removeItemAttachment.do'/>";
       
	</script>
		<script type="text/javascript" src="<html:rewrite page='/includes/javascript/surveysurveyitem.js'/>"></script>
		<style type="text/css">
	<!--
	table { 
		 width:650px;
		 margin-left:0px; 
		 text-align:left; 
		 }
	
	td { 
		padding:4px; 
		font-size:12px;
	}
	hr {
		border: none 0;
		border-top: 1px solid #ccc;
		width: 650px;
		height: 1px;
		margin: 0px 10px 10px 0px;
	}
		
	-->
	</style>
	</head>
	<body class="tabpart">
		<table class="forms">
			<!-- Basic Info Form-->
			<tr>
				<td>
					<%@ include file="/common/messages.jsp"%>
					<html:form action="/authoring/saveOrUpdateItem" method="post" styleId="surveyItemForm" enctype="multipart/form-data">
						<html:hidden property="sessionMapID" />
						<input type="hidden" name="instructionList" id="instructionList" />
						<input type="hidden" name="itemType" id="itemType" value="3" />
						<html:hidden property="itemIndex" />
						<table class="innerforms">
							<tr>
								<td colspan="2">
									<h2>
										<fmt:message key="label.authoring.basic.add.website" />
									</h2>
								</td>
							</tr>
							<tr>
								<td width="130px">
									<fmt:message key="label.authoring.basic.survey.title.input" />
								</td>
								<td>
									<html:text property="title" size="55" tabindex="1" />
								</td>
							</tr>
							  <%--  Remove description in as LDEV-617
							<tr>
								<td width="130px">
									<fmt:message key="label.authoring.basic.survey.description.input" />
								</td>
								<td>
									<lams:STRUTS-textarea rows="5" cols="55" tabindex="2" property="description" />
								</td>
							</tr>
							--%>
							<tr>
								<td width="130px">
									<fmt:message key="label.authoring.basic.survey.zip.file.input" />
								</td>
								<td>
									<c:set var="itemAttachment" value="<%= request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY) %>" />
									<div id="itemAttachmentArea">
										<%@ include file="/pages/authoring/parts/itemattachment.jsp"%>
									</div>
								</td>
							</tr>
						</table>
					</html:form>
				</td>
			</tr>
			<tr>

				<!-- Instructions -->
				<td>
					<%@ include file="instructions.jsp"%>
				</td>
			</tr>
			<tr>
				<td align="center" valign="bottom">
					 <a href="#" onclick="submitSurveyItem()" class="button-add-item"><fmt:message key="label.authoring.basic.add.website" /></a> <a href="javascript:;" onclick="cancelSurveyItem()" class="button"><fmt:message key="label.cancel" /></a>
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;
				</td>
			</tr>
		</table>

	</body>
</html>
