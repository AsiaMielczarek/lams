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
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA

  http://www.gnu.org/licenses/gpl.txt
--%>

<%@ include file="/common/taglibs.jsp" %>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" scope="request"/>
<div id="itemList">
<h2><fmt:message key="label.vote.nominations" />:
<img src="${ctxPath}/includes/images/indicator.gif" style="display:none" id="resourceListArea_Busy" /></h2>

<c:set var="formBean" value="<%= request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY) %>" />
<table id="itemTable" style="align:left;width:650px" >
    <c:set var="queIndex" scope="request" value="0"/>
    
    <c:forEach items="${listNominationContentDTO}" var="currentDTO" varStatus="status">
	<c:set var="queIndex" scope="request" value="${queIndex +1}"/>
	<c:set var="question" scope="request" value="${currentDTO.question}"/>
	<c:set var="feedback" scope="request" value="${currentDTO.feedback}"/>
	<c:set var="displayOrder" scope="request" value="${currentDTO.displayOrder}"/>	

	<tr>
		<td width="10%" align="right" class="field-name">
			<fmt:message key="label.nomination" />:
		</td>

		<td width="60%" align="left">
			<c:out value="${question}" escapeXml="false"/> 
		</td>		



			<td width="10%" align="right">
		 		<c:if test="${totalNominationCount != 1}"> 		
		 		
			 		<c:if test="${queIndex == 1}"> 		
						<a title="<fmt:message key='label.tip.moveNominationDown'/>" href="javascript:;" onclick="javascript:submitModifyAuthoringNomination('<c:out value="${queIndex}"/>','moveNominationDown');">
		                            <img src="<c:out value="${tool}"/>images/down.gif" border="0">
						</a> 
					</c:if> 							
	
			 		<c:if test="${queIndex == totalNominationCount}"> 		
						<a title="<fmt:message key='label.tip.moveNominationUp'/>" href="javascript:;" onclick="javascript:submitModifyAuthoringNomination('<c:out value="${queIndex}"/>','moveNominationUp');">
		                            <img src="<c:out value="${tool}"/>images/up.gif" border="0">
						</a> 
					</c:if> 							
					
					<c:if test="${(queIndex != 1)  && (queIndex != totalNominationCount)}"> 		
						<a title="<fmt:message key='label.tip.moveNominationDown'/>" href="javascript:;" onclick="javascript:submitModifyAuthoringNomination('<c:out value="${queIndex}"/>','moveNominationDown');">
		                            <img src="<c:out value="${tool}"/>images/down.gif" border="0">
						</a> 
	
						<a title="<fmt:message key='label.tip.moveNominationUp'/>" href="javascript:;" onclick="javascript:submitModifyAuthoringNomination('<c:out value="${queIndex}"/>','moveNominationUp');">
		                            <img src="<c:out value="${tool}"/>images/up.gif" border="0">
						</a> 
					</c:if> 							
				
				</c:if> 			
			</td>

		

		<td width="10%" align="right">
				<a title="<fmt:message key='label.tip.editNomination'/>" href="javascript:;" onclick="javascript:showMessage('<html:rewrite page="/authoring.do?dispatch=newEditableNominationBox&questionIndex=${queIndex}&contentFolderID=${voteGeneralAuthoringDTO.contentFolderID}&httpSessionID=${voteGeneralAuthoringDTO.httpSessionID}&toolContentID=${voteGeneralAuthoringDTO.toolContentID}&activeModule=${voteGeneralAuthoringDTO.activeModule}&defaultContentIdStr=${voteGeneralAuthoringDTO.defaultContentIdStr}&voteChangable=${voteGeneralAuthoringDTO.voteChangable}&lockOnFinish=${voteGeneralAuthoringDTO.lockOnFinish}&allowText=${voteGeneralAuthoringDTO.allowText}&maxNominationCount=${voteGeneralAuthoringDTO.maxNominationCount}&reflect=${voteGeneralAuthoringDTO.reflect}&reflectionSubject=${voteGeneralAuthoringDTO.reflectionSubject}"/>');">
	                    <img src="<c:out value="${tool}"/>images/edit.gif" border="0">
				</a> 
		</td>

		<td width="10%" align="right">
				<a title="<fmt:message key='label.tip.deleteNomination'/>" href="javascript:;" onclick="removeNomination(${queIndex});">
	                    <img src="<c:out value="${tool}"/>images/delete.gif" border="0">
				</a> 				
		</td>
	</tr>
</c:forEach>

</table>
</div>
<%-- This script will works when a new resoruce item submit in order to refresh "Resource List" panel. --%>
<script lang="javascript">
 
	if(window.top != null){
		window.top.hideMessage();
		var obj = window.top.document.getElementById('resourceListArea');
		obj.innerHTML= document.getElementById("itemList").innerHTML;
	}
</script>

