<%@ include file="/common/taglibs.jsp"%>

<script type="text/javascript">
	function disableFinishButton() {
		document.getElementById("finishButton").disabled = true;
	}
         function submitForm(methodName){
                var f = document.getElementById('messageForm');
                f.submit();
        }       
</script>

<div id="content">
	<h1>
		${chatDTO.title}
	</h1>

	<html:form action="/learning" method="post" onsubmit="disableFinishButton();" styleId="messageForm">

		<p class="small-space-top">
			<lams:out value="${chatDTO.reflectInstructions}" />
		</p>

		<html:textarea cols="60" rows="8" property="entryText"
			styleClass="text-area"></html:textarea>

		<div class="space-bottom-top align-right">
			<html:hidden property="dispatch" value="submitReflection" />
			<html:hidden property="chatUserUID" />
			<html:link href="#nogo" styleClass="button" styleId="finishButton"  onclick="submitForm('finish')">
				 <span class="nextActivity"><fmt:message>button.finish</fmt:message></span>
			</html:link>
		</div>
	</html:form>
</div>

