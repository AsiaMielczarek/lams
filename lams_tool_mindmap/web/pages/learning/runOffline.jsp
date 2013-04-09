<%@ include file="/common/taglibs.jsp"%>

<script type="text/javascript">
	function disableFinishButton() {
		document.getElementById("finishButton").disabled = true;
	}
         function submitForm(methodName){
                var f = document.getElementById('Form');
                f.submit();
        }

</script>

<div id="content">
	<h1>
		${mindmapDTO.title}
	</h1>

	<c:choose>
		<c:when test="${empty submissionDeadline}">
			<p>
				<fmt:message key="message.runOfflineSet" />
			</p>			
		</c:when>
		<c:otherwise>
			<div class="warning">
				<fmt:message key="authoring.info.teacher.set.restriction" >
					<fmt:param><lams:Date value="${submissionDeadline}" /></fmt:param>
				</fmt:message>
			</div>
		</c:otherwise>
	</c:choose>

	<c:if test="${mode == 'learner' || mode == 'author'}">
		<html:form action="/learning" method="post" onsubmit="disableFinishButton();" styleId="Form">
			<html:hidden property="dispatch" value="finishActivity" />
			<html:hidden property="toolSessionID" />

			<div align="right" class="space-bottom-top">
				<html:link href="#" styleClass="button" styleId="finishButton" onclick="submitForm('finish')">
					<span class="nextActivity">
						<c:choose>
							<c:when test="${activityPosition.last}">
								<fmt:message key="button.submit" />
							</c:when>
							<c:otherwise>
								<fmt:message key="button.finish" />
							</c:otherwise>
						</c:choose>
					</span>
				</html:link>
			</div>
		</html:form>
	</c:if>
</div>

