<%@ include file="/common/taglibs.jsp"%>

<c:set var="dto" value="${gmapDTO}" />
<c:forEach var="session" items="${dto.sessionDTOs}">

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
			<td class="field-name" width="30%">
				<fmt:message key="heading.totalLearners" />
			</td>
			<td width="70%">
				${session.numberOfLearners}
			</td>
		</tr>
	</table>

	<table cellpadding="0">

		<tr>
			<th>
				<fmt:message key="heading.learner" />
			</th>
			<th>
				<fmt:message key="heading.notebookEntry" />
			</th>
		</tr>


		<c:forEach var="user" items="${session.userDTOs}">
			<tr>
				<td width="30%">
					${user.firstName} ${user.lastName}
				</td>
				<td width="70%">
					<c:choose>
						<c:when test="${user.entryUID == null}">
							<fmt:message key="label.notAvailable" />
						</c:when>

						<c:otherwise>
							<a
								href="./monitoring.do?dispatch=showGmap&amp;userUID=${user.uid}">
								<fmt:message key="label.view" /> </a>
						</c:otherwise>
					</c:choose>

				</td>
			</tr>
		</c:forEach>
	</table>
</c:forEach>
