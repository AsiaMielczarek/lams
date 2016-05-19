<!DOCTYPE html>
		

<%@ include file="/common/taglibs.jsp"%>
<lams:html>
	<lams:head>
		<%@ include file="/common/header.jsp"%>
		<lams:css/>
		<script type="text/javascript">
		   <%-- user for  rsrcresourceitem.js --%>
		   var removeInstructionUrl = "<c:url value='/authoring/removeInstruction.do'/>";
	       var addInstructionUrl = "<c:url value='/authoring/newInstruction.do'/>";
	       var removeItemAttachmentUrl = "<c:url value='/authoring/removeItemAttachment.do'/>";
		</script>
		<script type="text/javascript" src="<html:rewrite page='/includes/javascript/rsrcresourceitem.js'/>"></script>

	</lams:head>
	<body>

		<div class="panel panel-default add-file">
			<div class="panel-heading panel-title">
				<fmt:message key="label.authoring.basic.add.learning.object" />
			</div>
			
			<div class="panel-body">

			<%@ include file="/common/messages.jsp"%>

			<html:form action="/authoring/saveOrUpdateItem" method="post" styleId="resourceItemForm" enctype="multipart/form-data">
				<input type="hidden" name="instructionList" id="instructionList" />
				<html:hidden property="sessionMapID" />
				<input type="hidden" name="itemType" id="itemType" value="4" />
				<html:hidden property="itemIndex" />
	
				<div class="form-group">
				   	<label for="title"><fmt:message key="label.authoring.basic.resource.title.input" /></label>
					<html:text property="title" size="55" styleClass="form-control form-control-inline" />
			  	</div>	
			  

				<div class="form-group">
					<label for="file"><fmt:message key="label.authoring.basic.resource.zip.file.input" /></label>
					<c:set var="itemAttachment" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />
					<span id="itemAttachmentArea">
					<%@ include file="/pages/authoring/parts/itemattachment.jsp"%>
					</span>
					<i class="fa fa-spinner" style="display:none" id="itemAttachmentArea_Busy"></i>
				</div>
	
			</html:form>

			<!-- Instructions -->
			<%@ include file="instructions.jsp"%>
		
			<div class="voffset5">
				<a href="#" onclick="submitResourceItem()" class="btn btn-default btn-sm">
					<i class="fa fa-plus"></i>&nbsp;<fmt:message key="label.authoring.basic.add.learning.object" /> </a>
				<a href="javascript:;" onclick="hideResourceItem()"	class="btn btn-default btn-sm">
					<fmt:message key="label.cancel" /> </a>
			</div>
			
			</div>
		</div>

	</body>
</lams:html>
