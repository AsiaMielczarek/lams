<table class="forum" cellspacing="0">
	<tbody>
		<tr>
			<th class="first" scope="col">
				<c:out value="${topic.message.subject}" />
			</th>

		</tr>

		<tr>
			<td class="first posted-by">
				<fmt:message key="lable.topic.subject.by" />
				<c:out value="${topic.author}" />
				-
				<fmt:formatDate value="${topic.message.created}" type="time" timeStyle="short" />
				<fmt:formatDate value="${topic.message.created}" type="date" dateStyle="full" />

			</td>
		</tr>
		<tr>
			<td class="first">
				<c:out value="${topic.message.body}" escapeXml="false" />
			</td>
		</tr>
		<tr>
			<td align="right">
				<c:forEach var="file" items="${topic.message.attachments}">
					<c:set var="downloadURL">
						<html:rewrite page="/download/?uuid=${file.fileUuid}&versionID=${file.fileVersionId}&preferDownload=true" />
					</c:set>
					<a href="<c:out value='${downloadURL}' escapeXml='false'/>"> <c:out value="${file.fileName}" /> </a>
				</c:forEach>
			</td>
		</tr>
	</tbody>
</table>
