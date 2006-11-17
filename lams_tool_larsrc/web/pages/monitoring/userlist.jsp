<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">


<%@ include file="/common/taglibs.jsp"%>
<lams:html>
<head>
	    <%@ include file="/common/header.jsp" %>
</head>
<body class="stripes">


		<div id="content">
		
		<h1>
			<fmt:message key="label.monitoring.heading.access"/>
		</h1>
		
	<table border="0" cellspacing="3" width="98%">
		<tr>
			<th>
				<fmt:message key="monitoring.label.user.loginname" />
			</th>
			<th>
				<fmt:message key="monitoring.label.access.time" />
			</th>
			<th>
				<fmt:message key="monitoring.label.user.name" />
			</th>
		</tr>
		<c:forEach var="user" items="${userList}">
			<tr>
				<td>
					${user.loginName}
				</td>
				<td>
					<lams:Date value="${user.accessDate}"/>
				</td>
				<td>
					${user.firstName},${user.lastName}
				</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="3" class="align-right">
				<a href="javaqscript:;" onclick="window.close()" class="button">Close</a>
			</td>
		</tr>
	</table>
</div>
		<div id="footer"></div>
		</div>
</body>
</lams:html>