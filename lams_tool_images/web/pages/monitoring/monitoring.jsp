<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.tool.imageGallery.ImageGalleryConstants"%>

<lams:html>
	<lams:head>
		 <%@ include file="/common/tabbedheader.jsp" %>
	 <script>
			var initialTabId = "${initialTabId}";

		   function init(){
			 	if (initialTabId) {
					selectTab(initialTabId);
				} else {
					selectTab(1);
				}
	        }     
	        
	        function doSelectTab(tabId) {
		    	// end optional tab controller stuff
		    	selectTab(tabId);
	        } 
	        
		    function viewItem(imageUid,sessionMapID){
				var myUrl = "<c:url value="/reviewItem.do"/>?mode=teacher&imageUid=" + imageUid + "&sessionMapID="+sessionMapID;
				launchPopup(myUrl,"MonitoringReview");
			}
	    </script>		 
	</lams:head>
	<body class="stripes" onLoad="init()">
	<div id="page">
		<h1>
			<fmt:message key="label.authoring.heading" />
		</h1>
	<div id="header">
		<lams:Tabs>
			<lams:Tab id="1" key="monitoring.tab.summary" />
			<lams:Tab id="2" key="monitoring.tab.instructions" />
			<lams:Tab id="3" key="monitoring.tab.edit.activity" />			
			<lams:Tab id="4" key="monitoring.tab.statistics" />
		</lams:Tabs>
	</div>
	<div id="content">
			<lams:help toolSignature="<%= ImageGalleryConstants.TOOL_SIGNATURE %>" module="monitoring"/>
	
			<lams:TabBody id="1" titleKey="monitoring.tab.summary" page="summary.jsp" />
			<lams:TabBody id="2" titleKey="monitoring.tab.instructions" page="instructions.jsp"/>
			<lams:TabBody id="3" titleKey="monitoring.tab.edit.activity" page="editactivity.jsp" />			
			<lams:TabBody id="4" titleKey="monitoring.tab.statistics" page="statistic.jsp" />
	</div>
	<div id="footer"></div>
	
	</div>
	</body>
</lams:html>
