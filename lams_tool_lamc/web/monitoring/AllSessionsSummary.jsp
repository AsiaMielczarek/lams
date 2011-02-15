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
<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

				<table class="forms">
		  	 		
					<c:forEach var="sessionMarksDto" items="${listMonitoredMarksContainerDto}">
			  	 		<c:set var="currentSessionId" scope="request" value="${sessionMarksDto.sessionId}"/>
			  	 		<c:set var="mapUserMarksDto" scope="request" value="${sessionMarksDto.userMarks}"/>

						<c:if test="${isGroupedActivity}">
							<tr>
						 		<td NOWRAP colspan=2 > <b> <fmt:message key="group.label"/> : </b>
						 		<c:out value="${sessionMarksDto.sessionName}"/>  </td>
							</tr>
						</c:if>

			  	 		<tr>
							 <td NOWRAP valign=top class="align-left"> <b> <fmt:message key="label.user"/> </b> </td>  
							 <td NOWRAP valign=top class="align-left"> <b> <fmt:message key="label.attemptTime"/>  </b> </td>
				  	 		<c:set var="queIndex" scope="request" value="0"/>
							<c:forEach var="currentDto" items="${listMonitoredAnswersContainerDto}">
							<c:set var="queIndex" scope="request" value="${queIndex +1}"/>
								<td NOWRAP valign=top class="align-left"> <b>  <fmt:message key="label.question.only"/> <c:out value="${queIndex}"/></b>
									 &nbsp (<fmt:message key="label.mark"/> <c:out value="${currentDto.mark}"/> )
								</td>
							</c:forEach>		  	
							 
							 <td NOWRAP valign=top class="align-left"> <b> <fmt:message key="label.total"/>  </b> </td>  
			  	 		</tr>						 

									<c:forEach var="markData" items="${mapUserMarksDto}">						
						  	 		<c:set var="data" scope="request" value="${markData.value}"/>
						  	 		<c:set var="currentUserSessionId" scope="request" value="${data.sessionId}"/>

									<c:if test="${currentMonitoredToolSession == 'All'}"> 			
										<c:if test="${currentUserSessionId == currentSessionId}"> 	
											<tr>									  	 		
							  	 				<td NOWRAP valign=top class="align-left"> 
														<c:out value="${data.fullName}"/> 
												</td>	

												<td NOWRAP valign=top class="align-left">
													<lams:Date value="${data.attemptTime}"/>
												</td>	
			
												<c:forEach var="mark" items="${data.marks}">
													<td NOWRAP valign=top class="align-left"> 
															<c:out value="${mark}"/> 								
													</td>
												</c:forEach>		  										
			
												<td NOWRAP valign=top class="align-left"> 
															<c:out value="${data.totalMark}"/> 																
												</td>							
											</tr>													
										</c:if>																
									</c:if>																		
									</c:forEach>		  	
			

									<c:forEach var="markData" items="${mapUserMarksDto}">						
						  	 		<c:set var="data" scope="request" value="${markData.value}"/>
						  	 		<c:set var="currentUserSessionId" scope="request" value="${data.sessionId}"/>							  	 		
									<c:if test="${currentMonitoredToolSession !='All'}"> 			
										<c:if test="${currentMonitoredToolSession == currentUserSessionId}"> 										
											<c:if test="${currentUserSessionId == currentSessionId}"> 									
											<tr>			
												<td NOWRAP valign=top class="align-left"> 
														<c:out value="${data.fullName}"/> 
												</td>	

												<td NOWRAP valign=top class="align-left">
													<lams:Date value="${data.attemptTime}"/>
												</td>	
			
												<c:forEach var="mark" items="${data.marks}">
													<td NOWRAP valign=top class="align-left"> 
															<c:out value="${mark}"/> 								
													</td>
												</c:forEach>		  										
			
												<td NOWRAP valign=top class="align-left"> 
															<c:out value="${data.totalMark}"/> 																
												</td>							
											</tr>														
											</c:if>																
										</c:if>																			
									</c:if>																								
									</c:forEach>		


		 					<tr> <td NOWRAP colspan="<c:out value='${hrColumnCount}'/>">  </td>
							</tr>
		 					<tr> <td NOWRAP colspan="<c:out value='${hrColumnCount}'/>"> <hr size="2"></td>
							</tr>
		 					<tr> <td NOWRAP colspan="<c:out value='${hrColumnCount}'/>">  </td>
							</tr>
					</c:forEach>		  	
			</table>		  	 		

	