<%@ include file="/includes/taglibs.jsp"%>

<table cellpadding="0">
	<tr>
		<td>
			<span  class="field-name"><fmt:message key="message.label.subject" /></span><BR>		
			<html:text size="30" tabindex="1" property="message.subject" />
			<br>
			<html:errors property="message.subject" />
		</td>
	</tr>
	<tr>
		<td>
			<span  class="field-name"><fmt:message key="message.label.body" />*</span><BR>
			<%@include file="bodyarea.jsp"%>
		</td>
	</tr>
	<c:if test="${sessionMap.allowUpload}">
		<tr>
			<td>
				<span class="field-name"><fmt:message key="message.label.attachment" /></span>
				<html:file tabindex="3" property="attachmentFile" />
				<html:errors property="message.attachment" />
			</td>
		</tr>
	</c:if>

	<div class="right-buttons">
	<tr>
		<td>
		<html:submit styleClass="button">
			<fmt:message key="button.submit" />
		</html:submit>
		<c:set var="backToForum">
			<html:rewrite page="/learning/viewForum.do?toolSessionID=${sessionMap.toolSessionID}" />
		</c:set>
		<html:button property="goback" onclick="javascript:location.href='${backToForum}';" styleClass="button">
			<fmt:message key="button.cancel" />
		</html:button>
		</td>
	</tr>
	</div>

</table>

<div class="space-bottom"></div>
