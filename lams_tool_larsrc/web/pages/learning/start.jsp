<%@ include file="/common/taglibs.jsp"%>
<html>
	<body>

		<c:choose>
			<c:when test="${runAuto}">
				<script type="text/javascript">
					document.location = "<c:url value="/reviewItem.do"/>?mode=${mode}&toolSessionID=${toolSessionID}&itemUid=${itemUid}";
				</script>
			</c:when>
			<c:otherwise>
				<script type="text/javascript">
					document.location = "<c:url value="/pages/learning/learning.jsp?mode=${mode}"/>"
				</script>
			</c:otherwise>
		</c:choose>
	<body>
</html>
