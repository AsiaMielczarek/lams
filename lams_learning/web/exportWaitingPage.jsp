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
<%@ taglib uri="tags-core" prefix="c" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-html" prefix="html" %>
<%@ taglib uri="tags-bean" prefix="bean" %>


<%
String protocol = request.getProtocol();
if(protocol.startsWith("HTTPS")){
	protocol = "https://";
}else{
	protocol = "http://";
}
String learning_root = protocol+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
String exportUrl = learning_root + "portfolioExport?" + request.getQueryString();
String downloadServlet = learning_root + "exportDownload?fileLocation=";

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Generating Export</title>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <script type ="text/javascript">
    	var READY_STATE_UNINITIALIZED=0;
		var READY_STATE_LOADING=1;
		var READY_STATE_LOADED=2;
		var READY_STATE_INTERACTIVE=3;
		var READY_STATE_COMPLETE=4;
		var req;
		var updateindicator="";

		function sendRequest(url,HttpMethod)
		{
		
			if ( window.XMLHttpRequest ) {
			   req = new XMLHttpRequest();
		    } else {
			   req = new ActiveXObject("MSXML2.XMLHTTP");
		    }
		    
			if (!HttpMethod){
				HttpMethod="GET";
			}
			
			if (req){
				req.onreadystatechange=exportCallback; //callback handler
				req.open(HttpMethod,url,true);
				req.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
				req.send(null); //sends any parameters along with the request for that url
			}
		}
		
		function exportCallback(){
			
			updateindicator = updateindicator+"... ";
		
			var ready=req.readyState;
			var msg;
		
			if (ready==READY_STATE_COMPLETE){
			
				switch (req.status)
				{
					case 200: // status 200 OK
						var url = "<%=downloadServlet%>"+req.responseText;
						window.location.href = url;
						break;
					case 404: //status 404 Not Found
						msg = "<fmt:message key='error.message.404' />.";
						alert(msg);
						break;
					case 500: //status 500 Internal Server Error
						msg = "<p><fmt:message key='error.system.learner'><fmt:param>Status 500</fmt:param></fmt:message></p>";
						document.getElementById("message").innerHTML = msg;
						break;
				}
				
				
			}else{
				document.getElementById("message").innerHTML = "<p>Loading ...</p>"+updateindicator;
				
				
			}
		
		}
		
		window.onload=function(){
			sendRequest("<%=exportUrl%>");
		}  
	</script>
      <!--
    <link rel="stylesheet" type="text/css" href="styles.css">
    -->
   

  </head>
  
  <body>

	<h1 class="no-tabs-below">&nbsp;</h1>
	<div id="header-no-tabs-learner">

	</div><!--closes header-->

	<div id="content-learner">

	    <div id="message"></div>

	</div>  <!--closes content-->


	<div id="footer-learner">
	</div><!--closes footer-->

  </body>
</html>
