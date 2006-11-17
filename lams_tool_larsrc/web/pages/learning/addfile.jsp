<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
		"http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/common/taglibs.jsp"%>
<lams:html>
	<head>
		<%@ include file="/common/header.jsp"%>
	</head>
	<body>
		<html:form action="/learning/saveOrUpdateItem" method="post"
			styleId="resourceItemForm" enctype="multipart/form-data">
			<html:hidden property="itemType" styleId="itemType" value="2" />
			<html:hidden property="mode" />
			<html:hidden property="sessionMapID" />

			<div class="field-name-alternative-color space-top">
				<fmt:message key="label.learning.new.file" />
			</div>

			<%@ include file="/common/messages.jsp"%>

			<div class="field-name space-top">
				<fmt:message key="label.authoring.basic.resource.title.input" />
			</div>
			<html:text property="title" size="40" tabindex="1" />

			<div class="field-name space-top">
				<fmt:message key="label.authoring.basic.resource.file.input" />
			</div>
			<input type="file" name="file" size="25" />

			<div class="field-name space-top">
				<fmt:message key="label.learning.comment.or.instruction" />
			</div>
			<html:textarea rows="5" cols="25" tabindex="2" property="description"
				styleClass="text-area" />

			<div class="space-bottom-top">
				<a href="#"
					onclick="document.getElementById('resourceItemForm').submit()"
					class="button"> <fmt:message key="button.upload" /> </a>
			</div>

		</html:form>
	</body>
</lams:html>
