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

<c:set scope="request" var="lams"><lams:LAMSURL/></c:set>
<c:set scope="request" var="tool"><lams:WebAppURL/></c:set>

 		<c:set var="statsTabActive" scope="request" value="true"/>
 		
		<c:if test="${(voteGeneralMonitoringDTO.userExceptionNoToolSessions == 'true')}"> 	
				<table align="center">
					<tr> 
						<td NOWRAP valign=top align=center> 
							<b>  <bean:message key="error.noLearnerActivity"/> </b>
						</td> 
					<tr>
				</table>
		</c:if>			


		<c:if test="${(voteGeneralMonitoringDTO.userExceptionNoToolSessions != 'true') }"> 	
			<c:if test="${voteGeneralMonitoringDTO.currentMonitoredToolSession != 'All'}"> 							
					<jsp:include page="/monitoring/IndividualSessionSummary.jsp" />					
			</c:if> 	    	  

			<c:if test="${voteGeneralMonitoringDTO.currentMonitoredToolSession == 'All'}"> 							
					<jsp:include page="/monitoring/AllSessionsSummary.jsp" />								
			</c:if> 	    	  
		</c:if>						
 		

