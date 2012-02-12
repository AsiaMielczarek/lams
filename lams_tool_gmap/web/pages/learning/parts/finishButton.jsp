<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	function saveAndFinish() {
		 serialiseMarkers();
		 document.learningForm.submit();
		 return false

	}
</script>


<c:if test="${gmapUserDTO.finishedActivity and gmapDTO.reflectOnActivity}">
<html:form action="/learning" method="post" styleId="reflectEditForm">
	<html:hidden property="dispatch" value="openNotebook" />
	<html:hidden property="mode" value="${mode}" />	
	<html:hidden property="toolSessionID" styleId="toolSessionID"/>
	<html:hidden property="markersXML" value="" />
		<div class="space-top">
			<h2>
				${gmapDTO.reflectInstructions}
			</h2>

			<p>
				<c:choose>
					<c:when test="${not empty gmapUserDTO.notebookEntry}">
						<lams:out escapeHtml="true" value="${gmapUserDTO.notebookEntry}" />
					</c:when>

					<c:otherwise>
						<em><fmt:message key="message.no.reflection.available" /> </em>
					</c:otherwise>
				</c:choose>
			</p>

			<html:submit styleClass="button" onclick="javascript:return confirmLeavePage();">
				<fmt:message key="button.edit" />
			</html:submit>
		</div>
	
</html:form>
</c:if>

<html:form action="/learning" method="post" styleId="learningForm">
	<html:hidden property="dispatch" styleId = "dispatch" value="finishActivity" />
	<html:hidden property="toolSessionID" styleId="toolSessionID"/>
	<html:hidden property="markersXML" value="" styleId="markersXML" />
	<html:hidden property="mode" value="${mode}" />	
	<div class="space-bottom-top align-right">
		<html:submit styleClass="button" styleId="saveButton" onclick="javascript:document.getElementById('dispatch').value = 'saveMarkers'; return serialiseMarkers();">
			<fmt:message>button.save</fmt:message>
		</html:submit>
		<c:choose>
			<c:when test="${!gmapUserDTO.finishedActivity and gmapDTO.reflectOnActivity}">
				<html:submit styleClass="button" onclick="javascript:document.getElementById('dispatch').value = 'openNotebook'; return serialiseMarkers();">
					<fmt:message key="button.continue" />
				</html:submit>
			</c:when>
			<c:otherwise>
				<html:hidden property="dispatch" value="finishActivity" />
				<html:link href="#nogo" styleClass="button" styleId="finishButton" onclick="javascript:saveAndFinish();">
					<span class="nextActivity"><fmt:message key="button.finish" /></span>
				</html:link>
			</c:otherwise>
		</c:choose>
	</div>
</html:form>







