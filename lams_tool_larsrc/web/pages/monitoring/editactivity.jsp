<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
<c:set var="resource" value="${sessionMap.resource}"/>

<table cellpadding="0">
	<tr>
		<td>
			<fmt:message key="label.authoring.basic.title" />
			:
		</td>
		<td>
			<c:out value="${resource.title}" escapeXml="false" />
		</td>
	</tr>

	<tr>
		<td>
			<fmt:message key="label.authoring.basic.instruction" />
			:
		</td>
		<td>
			<c:out value="${resource.instructions}" escapeXml="false" />
		</td>
	</tr>

	<tr>
		<td colspan="2">
			<c:set var="isPageEditable" value="${sessionMap.isPageEditable}" />
			<c:choose>
				<c:when test='${isPageEditable == "true"}'>
					<c:url  var="authoringUrl" value="/definelater.do">
						<c:param name="toolContentID" value="${sessionMap.toolContentID}" />
						<c:param name="contentFolderID" value="${sessionMap.contentFolderID}" />
					</c:url>
					<html:link href="javascript:;" onclick="launchPopup('${authoringUrl}','definelater')" styleClass="button">
						<fmt:message key="label.monitoring.edit.activity.edit" />
					</html:link>
				</c:when>
				<c:otherwise>
					<div align="center">
						<b> <fmt:message key="message.monitoring.edit.activity.not.editable" /> </b>
					</div>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
</table>
