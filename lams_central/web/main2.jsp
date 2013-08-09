<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ page import="org.lamsfoundation.lams.security.JspRedirectStrategy"%>
<%@ page import="org.lamsfoundation.lams.web.util.HttpSessionManager"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration"%>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-function" prefix="fn"%>
<%@ taglib uri="tags-tiles" prefix="tiles" %>

<%-- If you change this file, remember to update the copy made for CNG-21 --%>

<%JspRedirectStrategy.welcomePageStatusUpdate(request, response);%>
<%HttpSessionManager.getInstance().updateHttpSessionByLogin(request.getSession(),request.getRemoteUser());%>
<!DOCTYPE HTML>
<lams:html>
<lams:head>
	<c:choose>
	<c:when test="${page_direction == 'RTL'}">
		<title><fmt:message key="index.welcome" /> :: <fmt:message key="title.lams"/></title>
	</c:when>
	<c:otherwise>
		<title><fmt:message key="title.lams"/> :: <fmt:message key="index.welcome" /></title>
	</c:otherwise>
	</c:choose>
	
	<lams:css style="main" />
	
	<link rel="icon" href="<lams:LAMSURL/>/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="<lams:LAMSURL/>/favicon.ico" type="image/x-icon" />
	<link rel="stylesheet" href="<lams:LAMSURL/>/css/thickbox.css" type="text/css" media="screen">
	<link rel="stylesheet" href="<lams:LAMSURL/>css/jquery-ui-redmond-theme.css" type="text/css" media="screen">
	<link rel="stylesheet" href="<lams:LAMSURL/>css/index.css" type="text/css" media="screen">

	<script language="JavaScript" type="text/javascript" src="includes/javascript/getSysInfo.js"></script>
	<script language="javascript" type="text/javascript" src="loadVars.jsp"></script>
	<script language="JavaScript" type="text/javascript" src="includes/javascript/openUrls.js"></script>
	<script language="JavaScript" type="text/javascript" src="includes/javascript/jquery.js"></script>
	<script language="JavaScript" type="text/javascript" src="includes/javascript/jquery-ui.js"></script>
    <script language="JavaScript" type="text/javascript" src="includes/javascript/thickbox.js"></script>
	<c:if test="${empty tab}">
		<script language="JavaScript" type="text/javascript" src="includes/javascript/groupDisplay.js"></script>	
	</c:if>
	<script language="javascript" type="text/javascript">
			var LAMS_URL = '<lams:LAMSURL/>';
			var LABELS = {
					EMAIL_NOTIFICATIONS_TITLE : '<fmt:message key="index.emailnotifications" />',
					REMOVE_LESSON_CONFIRM1 : '<fmt:message key="index.remove.lesson.confirm1"/>',
					REMOVE_LESSON_CONFIRM2 : '<fmt:message key="index.remove.lesson.confirm2"/>',
					SORTING_ENABLE : '<fmt:message key="label.enable.lesson.sorting"/>',
					SORTING_DISABLE : '<fmt:message key="label.disable.lesson.sorting"/>',
					SINGLE_ACTIVITY_LESSON_TITLE : '<fmt:message key="index.single.activity.lesson.title"/>',
					GRADEBOOK_COURSE_TITLE : '<fmt:message key="index.gradebook.course.title"/>',
					GRADEBOOK_LESSON_TITLE : '<fmt:message key="index.gradebook.lesson.title"/>',
					GRADEBOOK_LEARNER_TITLE : '<fmt:message key="index.gradebook.learner.title"/>',
					CONDITIONS_TITLE : '<fmt:message key="index.conditions.title"/>',
					SEARCH_LESSON_TITLE : '<fmt:message key="index.search.lesson.title"/>'
			}
			
			var tabName = '${tab}';
			var stateId = tabName == 'profile' ? 3 : 1;

			$(document).ready(function(){
				initMainPage();
				
				<%-- If it's the user's first login, display a dialog asking if tutorial videos should be shown --%>
				<c:if test="${firstLogin}">
					<c:url var="disableAllTutorialVideosUrl" value="tutorial.do">
						<c:param name="method" value="disableAllTutorialVideos" />
					</c:url>
					if (!confirm("<fmt:message key='label.tutorial.disable.all' />")){
				 		$.get("${disableAllTutorialVideosUrl}");
					}
				</c:if>
			});

	</script>
</lams:head>
<body class="my-courses">
<div id="page-mycourses">
	<div id="header-my-courses">
		<div id="nav-right">
			<div class="nav-box-right">
				<c:choose>
					<c:when test="${empty tab}">
						<div class="tab-left-selected"></div>
						<div class="tab-middle-selected"><a class="tab-middle-link-selected" style="border:0;" href="index.do"><fmt:message key="index.mycourses"/> </a></div>
						<div class="tab-right-selected"></div>
					</c:when>
					<c:otherwise>
						<div class="tab-left"></div>
						<div class="tab-middle"><a class="tab-middle-link" style="border:0;" href="index.do"><fmt:message key="index.mycourses"/> </a></div>
						<div class="tab-right"></div>
					</c:otherwise>
				</c:choose>
			</div>
			<c:forEach var="headerlink" items="${headerLinks}">
			<div class="nav-box-right">
				
				<c:set var="tabLeft" value="tab-left"/>
				<c:set var="tabMiddle" value="tab-middle"/>
				<c:set var="tabRight" value="tab-right"/>	
				<c:set var="highlight" value="false" />
				<c:if test="${tab eq 'profile'}">
					<c:if test="${headerlink.name eq 'index.myprofile'}">
						<c:set var="tabLeft" value="tab-left-selected"/>
						<c:set var="tabMiddle" value="tab-middle-selected"/>
						<c:set var="tabRight" value="tab-right-selected"/>	
						<c:set var="highlight" value="false" />
					</c:if>
				</c:if>
				<c:if test="${tab eq 'community'}">
					<c:if test="${headerlink.name eq 'index.community'}">
						<c:set var="tabLeft" value="tab-left-selected"/>
						<c:set var="tabMiddle" value="tab-middle-selected"/>
						<c:set var="tabRight" value="tab-right-selected"/>	
						<c:set var="highlight" value="false" />
					</c:if>
				</c:if>
				<c:if test="${headerlink.name eq 'index.author'}">
					<c:set var="tabLeft" value="tab-left-highlight"/>
					<c:set var="tabMiddle" value="tab-middle-highlight"/>
					<c:set var="tabRight" value="tab-right-highlight"/>	
					<c:set var="highlight" value="true" />					
				</c:if>
				<c:if test="${headerlink.name eq 'index.planner'}">
					<c:set var="tabLeft" value="tab-left-highlight"/>
					<c:set var="tabMiddle" value="tab-middle-highlight"/>
					<c:set var="tabRight" value="tab-right-highlight"/>	
					<c:set var="highlight" value="true" />					
				</c:if>
	
				<div class="${tabLeft}"></div>
				<div class="${tabMiddle}">
						<c:choose>
							<c:when test="${fn:startsWith(headerlink.name,'index')}">
								<lams:TabName url="${headerlink.url}" highlight="${highlight}"><fmt:message key="${headerlink.name}" /></lams:TabName>
							</c:when>
							<c:otherwise>
								<lams:TabName url="${headerlink.url}" highlight="${highlight}"><c:out value="${headerlink.name}" /></lams:TabName>						
							</c:otherwise>
						</c:choose>
				</div>
				<div class="${tabRight}"></div>
			</div>
			</c:forEach>
		</div>
	</div>
	<div id="content-main">
		<table>
			<tr>
				<c:if test="${not empty portraitUuid}">
					<td>
						<img class="img-border" src="download/?uuid=${portraitUuid}&preferDownload=false" />
					</td>
				</c:if>
				<td>
					<fmt:message key="index.welcome" />
					<lams:user property="firstName" />
				</td>
				<td id="messageCell">
					<div id="message">Important annoucements might be posted here...</div>
				</td>
				<td class="linksCell">
					<c:if test="${not empty adminLinks}">
						<c:forEach var="adminlink" items="${adminLinks}">
							<div class="ui-button" title="<fmt:message key="${adminlink.name}"/>" 
							     onClick='<c:out value="${adminlink.url}"/>'>
									<fmt:message key="${adminlink.name}"/>
							</div> 
						</c:forEach>
					</c:if>
				</td>
				<td class="linksCell">
					<div class="ui-button" title="<fmt:message key="index.refresh.hint"/>"
						 onClick="javascript:loadOrgTab(null, true)">
							<fmt:message key="index.refresh" />
					</div>
					<div class="ui-button" onClick="javascript:closeAllChildren();document.location.href='home.do?method=logout'">
							<fmt:message key="index.logout" />
					</div>
				</td>
			</tr>
		</table>
		<c:if test="${empty tab}">
			<table id="mainContentTable" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<table id="orgTabs" cellpadding="0" cellspacing="0">
							<tr>
								<td>
									<ul>
										<c:forEach items="${collapsedOrgDTOs}" var="dto" varStatus="status">
											<li class="orgTabsHeader">
												<a href="#orgTab-${status.index}-org-${dto.orgId}"><c:out value="${dto.orgName}" /></a>
											</li>
										</c:forEach>
									</ul>
								</td>
								<td id="orgTabsPanelCell" class="ui-widget-content ui-corner-all">
									<c:forEach items="${collapsedOrgDTOs}" var="dto" varStatus="status">
										<div id="orgTab-${status.index}-org-${dto.orgId}" class="orgTab"></div>
									</c:forEach>
								</td>
							</tr>
						</table>
					</td>
					<td id="actionAccord">
							<h3>New lessons</h3>
							<div>New lessons content panel<br />
							TEXT TEXT TEXT TEXT<br />
							TEXT TEXT TEXT TEXT<br />
							TEXT TEXT TEXT TEXT<br />
							TEXT TEXT TEXT TEXT<br />
							TEXT TEXT TEXT TEXT<br />
							</div>
							<h3>Recent activity</h3>
							<div>Recent activity content panel</div>
							<h3>Gradebooks</h3>
							<div>Gradebooks content panel</div>
							<h3>Announcements</h3>
							<div>Announcements content panel</div>
						
					</td>
				</tr>
			</table>
		</c:if>
		<c:if test="${tab eq 'profile'}">
			<tiles:insert attribute="profile" />
		</c:if>
		<c:if test="${tab eq 'community'}">
			<tiles:insert attribute="community" />
		</c:if>
	</div>
	<div id="footer">
		<p>
			<a style="color: #999999; text-decoration: none; border: none;" href="index.do?newLayout=false">
				<fmt:message key="msg.LAMS.version" /> <%=Configuration.get(ConfigurationKeys.VERSION)%>
			</a>
			<a href="<lams:LAMSURL/>/www/copyright.jsp" target='copyright' onClick="openCopyRight()">
				&copy; <fmt:message key="msg.LAMS.copyright.short" /> 
			</a>
		</p>
	</div>
</div>

<div id="dialogContainer">
	<iframe id="dialogFrame"></iframe>
</div>

</body>
</lams:html>