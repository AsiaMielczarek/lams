<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
<c:set var="summaryList" value="${sessionMap.summaryList}"/>

<%@ include file="/common/taglibs.jsp"%>


<c:forEach var="session" items="${summaryList}">

	<table>
		<tr>
			<td>
				<h2>
					${session.sessionName}
				</h2>
			</td>
		</tr>
	</table>

	<table cellpadding="0">
		<tr>
			<td class="field-name" width="40%">
				<fmt:message>heading.totalLearnersInGroup</fmt:message>
			</td>
			<td width="70%">
				${session.numberOfLearners}
			</td>
		</tr>
		<tr>
			<td class="field-name" width="40%">
				<fmt:message>heading.totalFinishedLearnersInGroup</fmt:message>
			</td>
			<td width="70%">
				${session.numberOfFinishedLearners}
			</td>
		</tr>
	</table>
</c:forEach>




