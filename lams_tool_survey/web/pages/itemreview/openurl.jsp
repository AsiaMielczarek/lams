<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<%@ include file="/common/header.jsp"%>
	</head>
	<body>
		<br>
		<div align="center">
			<p><b>${title}</b></p>
			<a href="javascript:;" onclick="javascipt:launchPopup('${popupUrl}','popupUrl');" style="width:200px;float:none;" class="button">
				<fmt:message key="open.in.new.window" />
			</a>
		<div>
	</body>
</html>
