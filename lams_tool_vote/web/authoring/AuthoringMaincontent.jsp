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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>

<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.lamsfoundation.lams.tool.vote.VoteAppConstants"%>

    <% 
		Set tabs = new LinkedHashSet();
		tabs.add("label.basic");
		tabs.add("label.advanced");
		tabs.add("label.instructions");
		pageContext.setAttribute("tabs", tabs);
		
		Set tabsBasic = new LinkedHashSet();
		tabsBasic.add("label.basic");
		pageContext.setAttribute("tabsBasic", tabsBasic);
	%>

	<html:html locale="true">
	<head>
	<title> <bean:message key="activity.title"/>  </title>

	<%@ include file="/common/header.jsp"%>
	<%@ include file="/common/fckeditorheader.jsp"%>


	<script type="text/javascript">
		var imgRoot="${lams}images/";
		var themeName="aqua";

        function init(){
			if (document.VoteAuthoringForm.activeModule.value != 'defineLater')
			{
	            initTabSize(3);
	            
	            var tag = document.getElementById("currentTab");
		    	if(tag.value != "")
		    		selectTab(tag.value);
	            else
	                selectTab(1); //select the default tab;
	            
	            initEditor("richTextOfflineInstructions");                                    
		        initEditor("richTextOnlineInstructions");                                    
			}
			else
			{
	            initTabSize(1);
	            
	            var tag = document.getElementById("currentTab");
		    	if(tag.value != "")
		    		selectTab(tag.value);
	            else
	                selectTab(1); //select the default tab;
			}
			
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
        	var tag = document.getElementById("currentTab");
	    	tag.value = tabId;
	    	selectTab(tabId);
        } 
        
        function doSubmit(method) {
        	document.VoteAuthoringForm.dispatch.value=method;
        	document.VoteAuthoringForm.submit();
        }
        
        
		function submitModifyNomination(optionIndexValue, actionMethod) 
		{
			document.VoteAuthoringForm.optIndex.value=optionIndexValue; 
			submitMethod(actionMethod);
		}
		
		function submitMethod(actionMethod) 
		{
			document.VoteAuthoringForm.dispatch.value=actionMethod; 
			document.VoteAuthoringForm.submit();
		}
		
		function deleteOption(deletableOptionIndex, actionMethod) {
			document.VoteAuthoringForm.deletableOptionIndex.value=deletableOptionIndex; 
			submitMethod(actionMethod);
		}
		
		function submitDeleteFile(uuid, actionMethod) 
		{
			document.VoteAuthoringForm.uuid.value=uuid; 
			submitMethod(actionMethod);
		}

	</script>
	
</head>
<body onLoad="init();">

<div id="page">
	<h1> <bean:message key="label.authoring.vote"/> </h1>
	
	<div id="header">
		<c:if test="${sessionScope.activeModule != 'defineLater' }"> 			
			<lams:Tabs collection="${tabs}" useKey="true" control="true"/>
		</c:if> 					
		<c:if test="${ (sessionScope.activeModule == 'defineLater') && (sessionScope.defineLaterInEditMode != 'true') }"> 					
			<lams:Tabs collection="${tabsBasic}" useKey="true" control="true"/>
		</c:if> 							
		<c:if test="${ (sessionScope.activeModule == 'defineLater') && (sessionScope.defineLaterInEditMode == 'true') }"> 					
			<lams:Tabs collection="${tabsBasic}" useKey="true" control="true"/>
		</c:if> 									
	</div>
	
	<div id="content">	
		<html:form  styleId="authoringForm" action="/authoring?validate=false" enctype="multipart/form-data" method="POST" target="_self">
		<html:hidden property="dispatch" value="submitAllContent"/>
		<html:hidden property="toolContentID"/>
		<html:hidden property="currentTab" styleId="currentTab" />
		<html:hidden property="activeModule"/>
		
		<c:if test="${sessionScope.activeModule != 'defineLater' }"> 			
			<!-- tab content 1 (Basic) -->
			<lams:TabBody id="1" titleKey="label.basic" page="Basic.jsp"/>
			<!-- end of content (Basic) -->
			      
			<!-- tab content 2 (Advanced) -->
			<lams:TabBody id="2" titleKey="label.advanced" page="AdvancedContent.jsp" />
			<!-- end of content (Advanced) -->
			
			<!-- tab content 3 (Instructions) -->
			<lams:TabBody id="3" titleKey="label.instructions" page="InstructionsContent.jsp" />
			<!-- end of content (Instructions) -->
		</c:if> 			
		
		<c:if test="${ (sessionScope.activeModule == 'defineLater') && (sessionScope.defineLaterInEditMode != 'true') }"> 			
			<lams:Tabs collection="${tabsBasic}" useKey="true" control="true"/>
			<!-- end tab buttons -->
			
			<!-- tab content 1 (Basic) -->
			<lams:TabBody id="1" titleKey="label.basic" page="BasicContentViewOnly.jsp"/>
			<!-- end of content (Basic) -->
		</c:if> 			
		
		<c:if test="${ (sessionScope.activeModule == 'defineLater') && (sessionScope.defineLaterInEditMode == 'true') }"> 			
			<lams:Tabs collection="${tabsBasic}" useKey="true" control="true"/>
			<!-- end tab buttons -->
			
			<!-- tab content 1 (Basic) -->
			<lams:TabBody id="1" titleKey="label.basic" page="BasicContent.jsp"/>
			<!-- end of content (Basic) -->
		</c:if> 			
		</html:form>
	</div>

	<div id="footer"></div>
		<lams:HTMLEditor />
	</div>


</body>
</html:html>
