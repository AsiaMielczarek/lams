<%@ include file="/common/taglibs.jsp"%>
<c:forEach var="element" items="${topicList}">
	<c:set var="toolSessionDto" value="${element.key}" />
	<c:set var="sessionTopicList" value="${element.value}" />
	<c:forEach var="totalMsg" items="${totalMessage}">
		<c:if test="${totalMsg.key eq toolSessionDto.sessionID}">
			<c:set var="sessionTotalMessage" value="${totalMsg.value}" />
		</c:if>
	</c:forEach>
	<c:forEach var="avaMark" items="${markAverage}">
		<c:if test="${avaMark.key eq toolSessionDto.sessionID}">
			<c:set var="sessionMarkAverage" value="${avaMark.value}" />
		</c:if>
	</c:forEach>

	<table cellpadding="0">
		<tr>
			<th colspan="2">
				<fmt:message key="message.session.name" />
				:
				<c:out value="${toolSessionDto.sessionName}" />
			</th>
		</tr>

		<tr>
			<td colspan="2">
				<table>
					<tr>
						<th scope="col" width="50%">
							<fmt:message key="label.topic.title.wiki" />
						</th>
						<th scope="col" width="25%">
							<fmt:message key="label.topic.title.message.number" />
						</th>
						<th scope="col" width="25%">
							<fmt:message key="label.topic.title.average.mark" />
						</th>
					</tr>
					<c:forEach items="${sessionTopicList}" var="topic">
						<tr>
							<td valign="MIDDLE" width="48%">
								<c:set var="viewtopic">
									<html:rewrite page="/monitoring/viewTopicTree.do?topicID=${topic.message.uid}&create=${topic.message.created.time}" />
								</c:set>
								<html:link href="javascript:launchPopup('${viewtopic}');">
									<c:out value="${topic.message.subject}" />
								</html:link>
							</td>
							<td>
								<c:out value="${topic.message.replyNumber+1}" />
							</td>
							<td>
								<c:out value="${topic.mark}" />
							</td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>

		<tr>
			<td class="field-name" width="30%">
				<fmt:message key="label.monitoring.statistic.total.message" />
			</td>
			<td>
				<c:out value="${sessionTotalMessage}" />
			</td>
		</tr>
		<tr>
			<td class="field-name" width="30%">
				<fmt:message key="label.monitoring.statistic.average.mark" />
			</td>
			<td>
				<c:out value="${sessionMarkAverage}" />
			</td>
		</tr>
	</table>
</c:forEach>
