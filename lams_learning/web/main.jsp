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

<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-lams" prefix="lams"%>

<!DOCTYPE html>

<lams:html>
<lams:head>
	<lams:css />
	<link href="css/main.css" rel="stylesheet" type="text/css" />

	<title><fmt:message key="learner.title" /></title>

	<c:if test="${not empty param.notifyCloseURL}">
		<c:set var="notifyCloseURL" value="${param.notifyCloseURL}" scope="request" />
	</c:if>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.js"></script>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.form.js"></script>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.layout.js"></script>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/raphael/raphael.js"></script>
	<script type="text/javascript" src="includes/javascript/main.js"></script>
	<script type="text/javascript">
		var APP_URL = '<lams:WebAppURL />';
		var LABEL_CURRENT_ACTIVITY = '<fmt:message key="label.learner.progress.activity.current.tooltip"/>';
		var LABEL_COMPLETED_ACTIVITY = '<fmt:message key="label.learner.progress.activity.completed.tooltip"/>';
		var LABEL_ATTEMPTED_ACTIVITY = '<fmt:message key="label.learner.progress.activity.attempted.tooltip"/>';
		var LABEL_TOSTART_ACTIVITY = '<fmt:message key="label.learner.progress.activity.tostart.tooltip"/>';
		var LABEL_SUPPORT_ACTIVITY = '<fmt:message key="label.learner.progress.activity.support.tooltip"/>';

		var tooltipDiv = null;
		var supportSeparatorRow = null;
		var supportPart = null;
		
		var parentURL = "${notifyCloseURL}";
		var lessonId = '${param.lessonID}';
		var progressPanelEnabled = '<lams:Configuration key="LearnerCollapsProgressPanel" />' != 'false';
		var presenceEnabled = '${param.presenceEnabledPatch}' != 'false';
		
		$(document).ready(function() {
			window.onresize = resizeElements;
			
			// show if panel is not disabled in LAMS Configuration
			if (progressPanelEnabled) {
				// these DOM elements are accessed often, so cache reference to them
				tooltipDiv = $("#tooltip");
				supportSeparatorRow = $('#supportSeparatorRow');
				supportPart = $('#supportPart');
				
				$('body').layout({
					west : {
						applyDefaultStyles : true,
						initClosed : false,
						resizable : false,
						slidable : false,
						size : 160,
						spacing_open : 10,
						spacing_closed : 10,
						togglerContent_open : '<fmt:message key="label.learner.progress.open"/>',
						togglerContent_closed : '<fmt:message key="label.learner.progress.closed"/>',
						togglerLength_open : 80,
						togglerLength_closed : 130,
						togglerTip_open : '<fmt:message key="label.learner.progress.open.tooltip"/>',
						togglerTip_closed : '<fmt:message key="label.learner.progress.closed.tooltip"/>',
						onopen_start : function() {$('#controlFrame').css('visibility','visible');}
					}
				});
				
				$('#notebookForm').ajaxForm({
				    'clearForm' : true,
				    'beforeSubmit' : validateNotebookForm
				});
			}
		});
	</script>
</lams:head>

<body class="stripes">
	<div id="controlFrame" class="ui-layout-west">
		<table id="logoTable" class="progressStaticHeight" cellspacing="0">
			<tr>
				<td rowspan="2">
					<img id="logoImage" src="<lams:LAMSURL />images/css/lams_logo.gif" />
				</td>
				<td id="exitButtonCell">
					<input onClick="javascript:closeWindow()" type="button" class="button progressButton" value='<fmt:message key="button.exit" />' />
				</td>
			</tr>
			<tr>
				<td id="exportButtonCell">
					<input onClick="javascript:exportPortfolio()" type="button" class="button progressButton" value='<fmt:message key="button.export" />' />
				</td>
			</tr>
		</table>
		
		<div id="lessonTitleRow" class="separatorRow progressStaticHeight"><c:out value="${param.title}" /></div>
		
		<div id="progressBarDiv"></div>
		
		<div id="supportSeparatorRow" class="separatorRow progressStaticHeight"
		     onClick="javascript:toggleBarPart('support')">
			<span><fmt:message key="label.learner.progress.support" /></span>
			<span id="supportTogglerCell" class="togglerCell" />▼</span>
		</div>
		<div id="supportPart" class="progressStaticHeight"></div>
		
		<div id="notebookSeparatorRow" class="separatorRow progressStaticHeight" 
		     onClick="javascript:toggleBarPart('notebook')">
			<span><fmt:message key="label.learner.progress.notebook" /></span>
			<span id="notebookTogglerCell" class="togglerCell"/>▲</span>
		</div>
		<form id="notebookForm" action="<lams:WebAppURL />notebook.do?method=processNewEntry" method="post">
			<input type="hidden" name="lessonID" value="${param.lessonID}" />
			<input type="hidden" name="signature" value="SCRATCHPAD" />
			<input type="hidden" name="skipViewAll" value="true" />
			
			<table id="notebookPart" class="progressStaticHeight">
				<tr>
					<td id="notebookTitleCell">
						<fmt:message key="mynotes.entry.title.label" />
					</td>
					<td>
						<input name="title" class="notebookInput" type="text" />
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<textarea id="entry" name="entry" rows="4" class="notebookInput"></textarea>
					</td>
				</tr>
				<tr>
					<td id="viewAllButtonCell">
						<input type="button" onClick="javascript:viewNotebookEntries()" 
						       class="button progressButton" value='<fmt:message key="mynotes.view.all.button" />' />
					</td>
					<td id="saveButtonCell">
						<input type="submit" class="button progressButton" value='<fmt:message key="button.save" />' />
					</td>
				</tr>
			</table>
		</form>
	</div>

	<c:if test="${param.presenceEnabledPatch}">
		<%@ include file="presenceChat.jsp"%>
	</c:if>
	
	<iframe id="contentFrame" name="contentFrame" onload="javascript:fillProgressBar()" class="ui-layout-center"
		src="content.do?lessonID=<c:out value="${param.lessonID}"/>"> </iframe>
		
	<div id="tooltip"></div>
	
</body>

</lams:html>