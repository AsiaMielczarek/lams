<%@ include file="/common/taglibs.jsp"%>

<script type="text/javascript">
<!--
function confirmForceComplete() {
		var message = "<fmt:message key='message.confirmForceComplete'/>";
		if (confirm(message)) {
			return true;			
		} else {
			return false;
		}
	}
//-->
</script>

<c:set var="dto" value="${requestScope.monitoringDTO}" />
<c:forEach var="session" items="${dto.sessionDTOs}">
	<h2>
		${session.sessionName}
	</h2>

	<c:choose>
		<c:when
			test="${(not dto.autoSelectScribe) and  session.appointedScribe eq null}">

			<c:choose>
				<c:when test="${not empty session.userDTOs}">
					<html:form action="/monitoring">

						<html:hidden property="toolSessionID" value="${session.sessionID}" />
						<html:hidden property="dispatch" value="appointScribe" />
						<html:hidden property="contentFolderID" />
						<html:hidden property="currentTab" styleId="currentTab" />

						<fmt:message key="heading.selectScribe" />

						<html:select property="appointedScribeUID"
							style="min-width: 150px;">
							<c:forEach var="user" items="${session.userDTOs}">
								<html:option value="${user.uid}">
												${user.firstName} ${user.lastName}
											</html:option>
							</c:forEach>
						</html:select>

						<html:submit styleClass="button">
							<fmt:message key="button.submit" />
						</html:submit>

					</html:form>
				</c:when>

				<c:otherwise>
					<p>
						<fmt:message key="message.noLearners" />
					</p>
				</c:otherwise>
			</c:choose>
		</c:when>

		<c:otherwise>
			<div class="field-name">
				<fmt:message key="heading.appointedScribe" />
			</div>

			<p>
				${session.appointedScribe}
			</p>

			<c:set var="scribeSessionDTO" value="${session}" scope="request">
			</c:set>
			<%@include file="/pages/parts/voteDisplay.jsp"%>

			<div class="field-name">
				<fmt:message key="heading.report" />
			</div>
			<hr />
			<c:forEach var="report" items="${session.reportDTOs}">
				<p>
					<lams:out value="${report.headingDTO.headingText}" />
				</p>
				<p>
					<lams:out value="${report.entryText}" />
				</p>
				<hr />
			</c:forEach>

			<c:if test="${session.forceComplete eq false}">
				<html:form action="monitoring" onsubmit="return confirmForceComplete();">
					<html:hidden property="dispatch" value="forceCompleteActivity" />
					<html:hidden property="toolSessionID" value="${session.sessionID}" />
					<html:hidden property="contentFolderID" />
					<html:submit styleClass="button">
						<fmt:message key="button.forceComplete" />
					</html:submit>
				</html:form>
			</c:if>

		</c:otherwise>
	</c:choose>

	<c:if test="${dto.reflectOnActivity}">
		<div class="field-name">
			<fmt:message key="heading.reflections" />
		</div>
		<table class="alternative-color" cellspacing="0">
			<tr>
				<th class="first">
					<fmt:message>heading.learner</fmt:message>
				</th>

				<th class="first">
					<c:choose>
						<c:when test="${dto.reflectOnActivity}">
							<fmt:message key="heading.reflection" />
						</c:when>
						<c:otherwise>
							&nbsp;
						</c:otherwise>
					</c:choose>
				</th>
			</tr>

			<c:forEach var="user" items="${session.userDTOs}">
				<tr>
					<td>
						${user.firstName} ${user.lastName}
					</td>
					<c:if test="${dto.reflectOnActivity}">
						<td>
							<c:if test="${user.finishedReflection}">
								<c:url value="monitoring.do" var="openNotebook">
									<c:param name="dispatch" value="openNotebook" />
									<c:param name="uid" value="${user.uid}" />
								</c:url>

								<html:link href="${fn:escapeXml(openNotebook)}" target="_blank">
									<fmt:message key="link.view" />
								</html:link>
							</c:if>
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
	</c:if>


</c:forEach>

<html:form action="/monitoring">
	<html:hidden property="toolContentID" value="${dto.toolContentID}" />
	<html:hidden property="contentFolderID" />
	<p>
		<html:submit styleClass="button right-buttons">
			<fmt:message key="button.refresh" />
		</html:submit>
	</p>
</html:form>
