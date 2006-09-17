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
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>
 
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="tags-bean" prefix="bean"%> 
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<%
String protocol = request.getProtocol();
if(protocol.startsWith("HTTPS")){
	protocol = "https://";
}else{
	protocol = "http://";
}
String pathToRoot = protocol+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
String pathToShare = protocol+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/../..";

%>

<lams:Passon id="${activityForm.lessonID}" progress="${activityForm.progressSummary}" redirect="true"/>

<h1 class="no-tabs-below">&nbsp;</h1>
<div id="header-no-tabs-learner">

</div><!--closes header-->

<div id="content-learner">

	<c:if test="${!empty activityForm.activityURLs}">
		
		<script language="JavaScript" type="text/JavaScript"><!--
			// onload method call commented out as the Flash passon movie calls doRedirect
			function redirectPage() {
				setTimeout("doRedirect()", 1000);
			}
			function doRedirect() {
				var url = "<c:out value='${activityForm.activityURLs[0].url}' escapeXml="false" />";
				if ( url.substring(0,4)!="http" ) {
					if ( url.substring(0,1)=="/" ) {
						url = "<%=pathToRoot%>.."+url;
					} else {
						url = "<%=pathToRoot%>../"+url;
					}
				}
				window.location.href = url;
			}
			
				//window.onload = redirectPage;
			//-->
		</script>
		
		<p>&nbsp;</p>
		<p><fmt:message key="message.activity.loading"/></p>

	<!-- <p>Tool summary is <c:out value="${activityForm.progressSummary}" escapeXml="false" /></p> -->

	</c:if>

</div>  <!--closes content-->


<div id="footer-learner">
</div><!--closes footer-->


			
