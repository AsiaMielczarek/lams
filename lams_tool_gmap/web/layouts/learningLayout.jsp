<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
            "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/common/taglibs.jsp"%>

<lams:html>
	<tiles:insert attribute="header" />
	<body class="stripes" onunload="GUnload()">
		<tiles:insert attribute="body" />
		<div class="footer">
		</div>					
	</body>
</lams:html>
