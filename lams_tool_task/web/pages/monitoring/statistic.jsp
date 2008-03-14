<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
<c:set var="summaryList" value="${sessionMap.summaryList}"/>

<table cellspacing="3">
	<c:if test="${empty summaryList}">
		<div align="center">
			<b> <fmt:message key="message.monitoring.summary.no.session" /> </b>
		</div>
	</c:if>
	<c:forEach var="group" items="${summaryList}" varStatus="firstGroup">
		<c:set var="groupSize" value="${fn:length(group)}" />
		<c:forEach var="item" items="${group}" varStatus="status">
			<%-- display group name on first row--%>
			<c:if test="${status.index == 0}">
				<tr>
					<td colspan="4">
						<B><fmt:message key="monitoring.label.group" /> ${item.sessionName}</B> <SPAN style="font-size: 12px;"> <c:if test="${firstGroup.index==0}">
								<fmt:message key="monitoring.summary.note" />
							</c:if> </SPAN>
					</td>
				</tr>
				<tr>
					<th width="45%">
						<fmt:message key="monitoring.label.title" />
					</th>
					<th width="35%">
						<fmt:message key="monitoring.label.suggest" />
					</th>
					<th width="20%" align="center">
						<fmt:message key="monitoring.label.number.learners" />
					</th>
				</tr>
			</c:if>
			<c:if test="${item.itemUid == -1}">
				<tr>
					<td colspan="4">
						<div align="left">
							<b> <fmt:message key="message.monitoring.summary.no.resource.for.group" /> </b>
						</div>
					</td>
				</tr>
			</c:if>
			<c:if test="${item.itemUid != -1}">
				<tr>
					<td>
						${item.itemTitle}
					</td>
					<td>
						<c:if test="${!item.itemCreateByAuthor}">
							${item.username}
						</c:if>
					</td>
					<td align="center">
						<c:choose>
							<c:when test="${item.viewNumber > 0}">
								<c:set var="listUrl">
									<c:url value='/monitoring/listuser.do?toolSessionID=${item.sessionId}&itemUid=${item.itemUid}' />
								</c:set>
								<a href="#" onclick="launchPopup('${listUrl}','listuser')"> ${item.viewNumber}<a>
							</c:when>
							<c:otherwise>
								0
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:if>
		</c:forEach>
	</c:forEach>
</table>
