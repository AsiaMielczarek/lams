<%@ include file="/common/taglibs.jsp"%>

<div id="header">

	<!--  TITLE KEY PAGE GOES HERE -->
	<lams:Tabs control="true">
		<lams:Tab id="1" key="button.basic" />
		<lams:Tab id="2" key="button.advanced" />
		<lams:Tab id="3" key="button.instructions" />
	</lams:Tabs>

</div>
<!--closes header-->

<div id="content">
	<html:form action="/authoring" styleId="authoringForm" method="post" enctype="multipart/form-data">
		<html:hidden property="toolContentID" />
		<html:hidden property="currentTab" styleId="currentTab" />
		<html:hidden property="dispatch" value="updateContent" />
		<html:hidden property="authSessionId" />

		<div id="message" align="center">
			<c:if test="${updateContentSuccess}">
				<img src="${tool}images/good.png">
				<bean:message key="message.updateSuccess" />
				</img>
			</c:if>

			<c:if test="${unsavedChanges}">
				<img src="${tool}images/warning.png" />
				<bean:message key="message.unsavedChanges" />
			</c:if>
		</div>

		<%-- Page tabs --%>
		<lams:TabBody id="1" titleKey="button.basic" page="basic.jsp" />
		<lams:TabBody id="2" titleKey="button.advanced" page="advanced.jsp" />
		<lams:TabBody id="3" titleKey="button.instructions" page="instructions.jsp" />



		<%-- Form Controls --%>
		<!-- Button Row -->
		<c:set var="formBean" value="<%= request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY) %>" />
		<lams:AuthoringButton formID="authoringForm" clearSessionActionUrl="/clearsession.do" toolSignature="lachat11" cancelButtonLabelKey="button.cancel" saveButtonLabelKey="button.save" toolContentID="${formBean.toolContentID}" />
	</html:form>
</div>

<div id="footer"></div>

<lams:HTMLEditor />




