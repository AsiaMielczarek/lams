<%@ include file="/common/taglibs.jsp"%>

<h1><c:out value="${content.title}" escapeXml="true"/></h1>

<div class="instructions space-top">
	<c:out value="${content.instructions}" escapeXml="false"/>
</div>

<c:if test="${empty listAllGroupsDTO}">
	<div align="center">
		<b> <fmt:message key="error.noLearnerActivity"/> </b>
	</div>
</c:if>

<c:if test="${content.useSelectLeaderToolOuput && not empty listAllGroupsDTO}">
	<div class="info">
		<fmt:message key="label.info.use.select.leader.outputs" />
	</div>
</c:if>

<c:forEach var="groupDto" items="${listAllGroupsDTO}">
			  	 		
	<c:if test="${isGroupedActivity}">
		<h1 class="group-name-title">
			<fmt:message key="group.label" />: <c:out value="${groupDto.sessionName}"/>
		</h1>
	</c:if>

	<c:forEach var="question" items="${questionDTOs}">
		
		<div class="tablesorter-container">
		<div class="question-title">
			<c:out value="${question.question}" escapeXml="false"/>
		</div>	
		
		<table class="tablesorter" data-question-uid="${question.uid}" data-session-id="${groupDto.sessionId}">
		
			<thead>
				<tr>
					<th title="<fmt:message key='label.sort.by.answer'/>" >
						<fmt:message key="label.learning.answer" />
					</th>
					
					<c:if test="${content.allowRateAnswers}">
						<th title="<fmt:message key='label.sort.by.rating'/>" width="130px">
							<fmt:message key="label.learning.rating" />
						</th>
					</c:if>
				</tr>
			</thead>
			
			<tbody>
			</tbody>
		</table>
		
		<!-- pager -->
		<div class="pager">
			<form>
				<img class="tablesorter-first"/>
				<img class="tablesorter-prev"/>
				<span class="pagedisplay"></span> <!-- this can be any element, including an input -->
				<img class="tablesorter-next"/>
				<img class="tablesorter-last"/>
				<select class="pagesize">
					<option selected="selected" value="10">10</option>
					<option value="20">20</option>
					<option value="30">30</option>
					<option value="40">40</option>
					<option value="50">50</option>
					<option value="100">100</option>
				</select>
			</form>
		</div>
		</div>
		
	</c:forEach>
			  	
</c:forEach>
		
<c:if test="${content.reflect && not empty reflectionsContainerDTO}"> 							
	<jsp:include page="/monitoring/Reflections.jsp" />
</c:if>

<%@include file="AdvanceOptions.jsp"%>

<%@include file="dateRestriction.jsp"%>
