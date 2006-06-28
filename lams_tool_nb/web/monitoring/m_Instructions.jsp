<%@ include file="/includes/taglibs.jsp"%>

<table cellpadding="0">
	<tr>
		<td class="field-name" width="30%">
			<fmt:message key="instructions.onlineInstructions" />
		</td>
		<td>
			<c:out value="${sessionScope.onlineInstructions}" escapeXml="false" />
		</td>
	</tr>

	<tr>
		<td class="field-name" width="30%">
			<fmt:message key="instructions.offlineInstructions" />
		</td>
		<td>
			<c:out value="${sessionScope.offlineInstructions}" escapeXml="false" />
		</td>
	</tr>
</table>

<logic:present name="attachmentList">
	<bean:size id="count" name="attachmentList" />
	<logic:notEqual name="count" value="0">
		<hr />

		<h2>
			<fmt:message key="label.attachments" />
		</h2>

		<table>
			<tr>
				<th>
					<fmt:message key="label.filename" />
				</th>
				<th>
					<fmt:message key="label.type" />
				</th>
				<th>
					&nbsp;
				</th>
			</tr>
			<logic:iterate name="attachmentList" id="attachment">
				<bean:define id="view">/download/?uuid=<bean:write name="attachment" property="uuid" />&preferDownload=false</bean:define>
				<bean:define id="download">/download/?uuid=<bean:write name="attachment" property="uuid" />&preferDownload=true</bean:define>
				<bean:define id="uuid" name="attachment" property="uuid" />

				<tr>
					<td>
						<bean:write name="attachment" property="filename" />
					</td>
					<td>
						<c:choose>
							<c:when test="${attachment.onlineFile}">
								<fmt:message key="instructions.type.online" />
							</c:when>
							<c:otherwise>
								<fmt:message key="instructions.type.offline" />
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<a href='javascript:launchInstructionsPopup("<html:rewrite page='<%=view%>'/>")' class="button"> <fmt:message key="link.view" /> </a>
						<html:link page="<%=download%>" styleClass="button">
							<fmt:message key="link.download" />
						</html:link>
					</td>
				</tr>
			</logic:iterate>
		</table>
	</logic:notEqual>
</logic:present>
