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


<%@ taglib uri="tags-bean" prefix="bean"%> 
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.Set" %>

    <% 
		Set tabs = new LinkedHashSet();
		tabs.add("label.summary");
		tabs.add("label.instructions");
		tabs.add("label.editActivity");
		tabs.add("label.stats");
		pageContext.setAttribute("tabs", tabs);
	%>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
	<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>

	<html:html locale="true">
	<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<lams:headItems/>
	<title> <bean:message key="label.monitoring"/> </title>

	<script type="text/javascript">
		var imgRoot="${lams}images/";
		var themeName="aqua";
	
        function init(){
        
            initTabSize(4);
            
            var tag = document.getElementById("currentTab");
	    	if(tag.value != "")
	    		selectTab(tag.value);
            else
                selectTab(1); //select the default tab;
                

            initEditor("title");
            initEditor("instructions");
            
            initEditor("optionContent0");
            <c:set var="optIndex" scope="session" value="1"/>
            <c:forEach var="questionEntry" items="${sessionScope.mapOptionsContent}">
                <c:set var="optIndex" scope="session" value="${optIndex +1}"/>
                initEditor("<c:out value="optionContent${optIndex-1}"/>");
            </c:forEach>

        }     
        
        function doSelectTab(tabId) {
        	// start optional tab controller stuff
        	var tag = document.getElementById("currentTab");
	    	tag.value = tabId;
	    	// end optional tab controller stuff
	    	selectTab(tabId);
        } 
        
        function doSubmit(method) {
        	document.VoteMonitoringForm.method.value=method;
        	document.VoteMonitoringForm.submit();
        }

		function submitMonitoringMethod(actionMethod) 
		{
			document.VoteMonitoringForm.method.value=actionMethod; 
			document.VoteMonitoringForm.submit();
		}
		
		function submitAuthoringMethod(actionMethod) {
			document.VoteAuthoringForm.dispatch.value=actionMethod; 
			document.VoteAuthoringForm.submit();
		}
		
		function submitModifyQuestion(questionIndexValue, actionMethod) 
		{
			document.VoteMonitoringForm.questionIndex.value=questionIndexValue; 
			submitMethod(actionMethod);
		}
		
		function submitEditResponse(responseId, actionMethod) 
		{
			document.VoteMonitoringForm.responseId.value=responseId; 
			submitMethod(actionMethod);
		}
		
		function submitMethod(actionMethod) 
		{
			submitMonitoringMethod(actionMethod);
		}
		
		function deleteOption(optIndex, actionMethod) {
			document.VoteMonitoringForm.optIndex.value=optIndex; 
			submitMethod(actionMethod);
		}
		
		function submitSession(selectedToolSessionId, actionMethod) {
			document.VoteMonitoringForm.selectedToolSessionId.value=selectedToolSessionId; 
			submitMonitoringMethod(actionMethod);
		}
		
		
		function submitModifyOption(optionIndexValue, actionMethod) 
		{
			document.VoteMonitoringForm.optIndex.value=optionIndexValue; 
			submitMethod(actionMethod);
		}

		function submitModifyNomination(optionIndexValue, actionMethod) 
		{
			document.VoteMonitoringForm.optIndex.value=optionIndexValue; 
			submitMethod(actionMethod);
		}
		
		function submitOpenVote(currentUid, actionMethod)
		{
			document.VoteMonitoringForm.currentUid.value=currentUid;
	        submitMethod(actionMethod);
		}
	
	</script>
	
</head>
<body onLoad="init();">
	
	<c:if test="${(isPortfolioExport != 'true') }"> 	
		<b> <font size=2> <bean:message key="label.monitoring"/> </font></b>
	</c:if> 				
				
    <html:form  action="/monitoring?validate=false" enctype="multipart/form-data" method="POST" target="_self">		
	<html:hidden property="method"/>
	<html:hidden property="toolContentID"/>
	<html:hidden property="currentTab" styleId="currentTab" />

	<html:hidden property="responseId"/>	 
	<html:hidden property="currentUid"/>
	<html:hidden property="selectedToolSessionId"/>							
	<input type="hidden" name="isToolSessionChanged"/>	

		<lams:Tabs collection="${tabs}" useKey="true" control="true"/>
		<div class="tabbody">
		<lams:TabBody id="1" titleKey="label.summary" page="SummaryContent.jsp"/>
		<lams:TabBody id="2" titleKey="label.instructions" page="Instructions.jsp" />
		<lams:TabBody id="3" titleKey="label.editActivity" page="Edit.jsp" />
		<lams:TabBody id="4" titleKey="label.stats" page="Stats.jsp" />
	</html:form>
	
	<lams:HTMLEditor/>		
</body>
</html:html>
