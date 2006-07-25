<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as 
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>

<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="tags-bean" prefix="bean"%> 
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-lams" prefix="lams" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html:html locale="true" xhtml="true">
	
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<lams:css/>
		<title><fmt:message key="learner.title"/></title>
		<script language="JavaScript" type="text/JavaScript">
		<!--
			function doAlert(arg){
				alert(arg);
			}
		//-->
		</script>
	</head>

	<frameset rows="*" cols="160,*">
		<frame src="controlFrame.jsp?lessonID=<c:out value="${param.lessonID}"/><c:if test="${param.mode != null}">&mode=<c:out value="${param.mode}"/></c:if>" name="controlFrame" scrolling="NO">
		<frame src="content.do" name="contentFrame" scrolling="YES">
	</frameset>
	
	<noframes>
		<body>
			<fmt:message key="message.activity.parallel.noFrames" />
		</body>
	</noframes>

</html:html>
