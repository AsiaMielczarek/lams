<%@ page language="java"  pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-core" prefix="c" %>
<%@ taglib uri="tags-lams" prefix="lams" %>
<%@ page import="org.lamsfoundation.lams.security.JspRedirectStrategy" %>
<%@ page import="org.lamsfoundation.lams.util.Configuration" %>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys" %>
<%	
	if (JspRedirectStrategy.loginPageRedirected(request,response))
	{
		return;
	}		

	/*String webAuthUser = (String) session.getAttribute("WEBAUTH_USER");
	if (webAuthUser != null)
	{
		response.sendRedirect("j_security_check?j_username=" + webAuthUser + "&j_password=Dummy");	
	}*/
	
	// for sysadmin to be able to login as someone else
	String login = (String)session.getAttribute("login");
	if (login!=null) { 
		session.removeAttribute("login");
		response.sendRedirect("j_security_check?j_username="+login+"&j_password=dummy");
	} 
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<lams:html>
<!--
flash is searching for this string, so leave it!:
j_security_login_page
-->
<c:set var="encrypt"><%= Configuration.getAsBoolean(ConfigurationKeys.LDAP_ENCRYPT_PASSWORD_FROM_BROWSER) %></c:set>
<lams:head>
	<title><fmt:message key="title.login.window"/></title>
	<lams:css  style="core"/>
	<link rel="icon" href="<lams:LAMSURL/>/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="<lams:LAMSURL/>/favicon.ico" type="image/x-icon" />
	<script language="javascript" type="text/javascript" src="includes/javascript/flash_detect.js">
		<!--
		function getFlashVersion() { return null; };
		//-->
	</script>
	<script language="javascript" type="text/javascript" src="includes/javascript/browser_detect.js"></script>
	<script language="JavaScript" type="text/javascript" src="includes/javascript/sha1.js"></script>
	<script>
		function submitForm(){
			  var password=document.loginForm.j_password.value;
			  <c:if test="${encrypt eq 'true'}">
			  document.loginForm.j_password.value=hex_sha1(password);
			  </c:if>
			  document.loginForm.submit();
		}
		
		function onEnter(event){
			intKeyCode = event.keyCode;
			if (intKeyCode == 13) {
				submitForm();
			}
		}
		
	</script>
</lams:head>

<body class="stripes">
	<div id="login-page"><!--main box 'page'-->
	
		<h1 class="no-tabs-below">&nbsp;</h1>
		
		<div id="login-header">
		
		</div><!--closes header-->
	
	
	
	  <div id="login-content">	
		   
			  <div id="login-left-col">
			    <h1><img src="<lams:LAMSURL/>/images/css/lams_login.gif" alt="LAMS - Learning Activity Management System" /></h1>
			  	<!--Test if the browsers flash player meets requirements-->
			  	<script language="JavaScript" type="text/javascript">
			  		<!--
						var minimumFlashVersion = 7;
						var flashVersion = getFlashVersion();
						if(flashVersion < minimumFlashVersion) {
							// show error message
							document.write('<div class=\"warning\"><fmt:message key="flash.min.error"/>');
							document.write('<br><a href=\"http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash\" target=\"_blank\"><fmt:message key="flash.download.player"/></a></div>');
						}
						
						
						if(isBrowserNotCompatable()) {
							// incompatable browser - show warning message
							document.write('<div class=\"warning\"><fmt:message key="msg.browser.compat"/>');
							document.write('<br></div>');
						}
						
						function isBrowserNotCompatable() {
							if(op) return true;	// Opera
							else if(saf) return true;		// Safari
							else if(ie6 || ie7) return false;	// IE6 IE7
							else if(ie5xwin || ie5 || ie4 || ie5mac || ie5x) return true;
							else if(moz) 
								if((moz_brow.indexOf("Firefox") != -1) && (moz_brow_nu >= 1.5)) return false;	// Firefox 1.5+
							else return true;
						}
						
					//-->
				</script>
 				<%try{%>
				  	<c:set var="url"><lams:LAMSURL/>/www/news.html</c:set>
		  			<c:import url="${url}" charEncoding="utf-8" />
		  		<%}catch(Exception e){}%>
			  </div>
				<!--closes left col-->
				
				<div id="login-right-col">
				<p class="version"><fmt:message key="msg.LAMS.version"/> <%= Configuration.get(ConfigurationKeys.VERSION) %></p>
				 <h2><fmt:message key="button.login"/></h2>
				 <form action="j_security_check" method="post" name="loginForm" id="loginForm">
					<c:if test="${!empty param.failed}">
						<div class="warning-login">
							<fmt:message key="error.login"/>
						</div>
					</c:if>	
				 <p class="first"><fmt:message key="label.username"/>: 
				   <input name="j_username" type="text" size="16" style="width:125px" tabindex="1" onkeypress="onEnter(event)"/>
				  </p>
				 <p><fmt:message key="label.password"/>: 
				   <input name="j_password" type="password" size="16" style="width:125px" autocomplete="off" tabindex="2" onkeypress="onEnter(event)"/>
				  </p>
					<p class="login-button">
					 <a href="javascript:submitForm()" class="button" tabindex="3"/><fmt:message key="button.login"/></a>
					 </p>
				</form>	
				<p class="login-button">
				   <a href="<lams:LAMSURL/>/www/help/troubleshoot-<%=Configuration.get(ConfigurationKeys.SERVER_LANGUAGE)%>.pdf">
				   	<fmt:message key="label.help"/>
				   </a>
				</p>   
				</div><!--closes right col-->
	 
	  <div class="clear"></div><!-- forces the CSS to display the columns-->
	
	  </div>  <!--closes content-->
		
		
		
		
		<div id="footer">
		
		<p>			<fmt:message key="msg.LAMS.version" /> <%=Configuration.get(ConfigurationKeys.VERSION)%>
			<a href="<lams:LAMSURL/>/www/copyright.jsp" target='copyright' onClick="openCopyRight()">
				&copy; <fmt:message key="msg.LAMS.copyright.short" /> 
			</a>
		</p>
		
	  </div><!--closes footer-->
		
	</div><!--closes page-->


</body>


</lams:html>