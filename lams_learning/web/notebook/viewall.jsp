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
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<fmt:setBundle basename="org.lamsfoundation.lams.learning.ApplicationResources" />
	<c:set var="addnote">
			<html:rewrite page="/notebook/add.do?lessonID=" /><c:out value="${lessonID}"/>
	</c:set> 
	<div id="content">
		<h1><fmt:message key="mynotes.title"/></h1>
	
		<h2><fmt:message key="mynotes.view.all.button"/></h2>
		<p>&nbsp;</p>
		<c:forEach var="entryList" items="${entries}" varStatus="list_status">
			
			<table class="alternative-color" cellspacing="0">
			<c:forEach var="entry" items="${entryList}" varStatus="entry_status">
					<html:link href="#" linkName="lesson${entry.externalID}" />
					
					<!-- set sig check flag -->
					<c:if test="${entry_status.first}">
						<c:set var="sigToCheck">
							<c:if test="${entry.externalSignature != ''}">
								<c:out value="${entry.externalSignature}"/>
							</c:if>
						</c:set>
						
						<c:set var="addnote">
							<html:rewrite page="/notebook/add.do?lessonID=" /><c:out value="${entry.externalID}"/>
						</c:set> 
						
						<h2>
						<c:choose>
							<c:when test="${!empty sigToCheck}">
								<fmt:message key="mynotes.signature.${sigToCheck}.heading"/> - <c:out value="${entry.lessonName}" escapeXml="false" />
							</c:when>
							<c:otherwise>
								<fmt:message key="mynotes.signature.unknown.heading"/> - <c:out value="${entry.lessonName}" escapeXml="false" />
							</c:otherwise>
						</c:choose>
						</h2>	
						
							<tr>
								<th scope="col" width="30%" colspan="2"><fmt:message key="mynotes.entry.title.label"/></th>
								<th scope="col" width="25%" align="center"><fmt:message key="mynotes.entry.create.date.label"/></th>
								<th scope="col" width="25%" align="center"><fmt:message key="mynotes.entry.last.modified.label"/></th>
							</tr>
					</c:if>
					
					<c:if test="${!empty sigToCheck && sigToCheck != entry.externalSignature}">
						<!-- do segment separator -->
						</table>
						
						<c:set var="sigToCheck" value="${entry.externalSignature}"/>
						
						<h2>
						<c:choose>
							<c:when test="${!empty sigToCheck}">
								<fmt:message key="mynotes.signature.${sigToCheck}.heading"/> - <c:out value="${entry.lessonName}" escapeXml="false" />
							</c:when>
							<c:otherwise>
								<fmt:message key="mynotes.signature.unknown.heading"/> - <c:out value="${entry.lessonName}" escapeXml="false" />
							</c:otherwise>
						</c:choose>
						</h2>		
								
						<table class="alternative-color" cellspacing="0">
							<tr>
								<th scope="col" width="30%" colspan="2"><fmt:message key="mynotes.entry.title.label"/></th>
								<th scope="col" width="25%" align="center"><fmt:message key="mynotes.entry.create.date.label"/></th>
								<th scope="col" width="25%" align="center"><fmt:message key="mynotes.entry.last.modified.label"/></th>
							</tr>
					</c:if>
						
					<tr>
						<td class="align-left" width="28%">
							<c:set var="viewnote">
								<html:rewrite page="/notebook.do?method=viewEntry&uid=" /><c:out value="${entry.uid}"/>
							</c:set> 
							<html:link href="${viewnote}">
								<c:choose>
									<c:when test="${empty entry.title}">
										<fmt:message key="mynotes.entry.no.title.label"/>
									</c:when>
									<c:otherwise>
										<c:out value="${entry.title}" escapeXml="false"/>
									</c:otherwise>
								</c:choose>
							</html:link>
						</td><td>&nbsp;</td>
						<td><lams:Date value="${entry.createDate}"/></td>
						<td><lams:Date value="${entry.lastModified}"/></td>
					</tr>
				</c:forEach>
				
				<!--  new add button for lesson -->
				<table>
					<tr>
						<td>
							<div class="right-buttons">
								<a href="${addnote}" class="button" id="addNewBtn"><fmt:message key="mynotes.add.new.button"/></a>
							</div>
						</td>
					</tr>
				</table>
				
			</table>
		</c:forEach>
		
	</div>  <!--closes content-->


	<div id="footer">
	</div><!--closes footer-->

