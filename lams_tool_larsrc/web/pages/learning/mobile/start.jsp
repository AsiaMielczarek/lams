<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
		"http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
<html>
	<body>
		<c:choose>
			<c:when test="${sessionMap.runAuto}">
				<script type="text/javascript">
					document.location = "<c:url value="/reviewItem.do"/>?sessionMapID=${sessionMapID}&mode=${mode}&toolSessionID=${toolSessionID}&itemUid=${itemUid}";
				</script>
			</c:when>
			<c:otherwise>
				<script type="text/javascript">
					document.location = "<c:url value="/pages/learning/learning.jsp?sessionMapID=${sessionMapID}&mode=${mode}"/>"
				</script>
			</c:otherwise>
		</c:choose>
	</body>
</html>
