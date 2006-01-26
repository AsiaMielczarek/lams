<%@ taglib uri="tags-html-el" prefix="html" %>
<%@ taglib uri="tags-bean" prefix="bean" %>
<%@ taglib uri="tags-logic-el" prefix="logic-el" %>
<%@ taglib uri="tags-c" prefix="c" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

		<html:form  action="/learning?method=displayMc&validate=false" method="POST" target="_self">
				<table align=center bgcolor="#FFFFFF">
					  <tr>
					  	<td NOWRAP align=left class="input" valign=top bgColor="#333366" colspan=2> 
						  	<font size=2 color="#FFFFFF"> <b>  <bean:message key="label.assessment"/> </b> </font>
					  	</td>
					  </tr>
				
			 		<c:if test="${sessionScope.isRetries == 'true'}"> 		
						  <tr>
						  	<td NOWRAP align=center class="input" valign=top colspan=2> 
							  	<font size=3> <b>  <bean:message key="label.individual.results.withRetries"/> </b> </font>
						  	</td>
						  </tr>
  					</c:if> 			

					<c:if test="${sessionScope.isRetries != 'true'}"> 							  
						  <tr>
						  	<td NOWRAP align=center class="input" valign=top colspan=2> 
							  	<font size=3> <b>  <bean:message key="label.individual.results.withoutRetries"/> </b> </font>
						  	</td>
						  </tr>
					</c:if> 			


					  <tr>
					  	<td NOWRAP align=right class="input" valign=top colspan=2> 
						  	<font size=2 color="#000000"> <b>  <bean:message key="label.mark"/>  
						  	<c:out value="${sessionScope.learnerMark}"/>  &nbsp
							<bean:message key="label.outof"/> &nbsp
						  	<c:out value="${sessionScope.totalQuestionCount}"/>
						  	</b> </font>
					  	</td>
					  </tr>	

					<tr>
						<td NOWRAP align=right class="input" valign=top colspan=2> 
							<hr>
						</td> 
					</tr>
					
					<c:if test="${sessionScope.userPassed != 'true'}">
						 <tr>
						  	<td NOWRAP align=left class="input" valign=top colspan=2> 
							  	<font size=2 color="#FF0000"> <b>  <bean:message key="label.mustGet"/> &nbsp
							  	<c:out value="${sessionScope.learnerMarkAtLeast}"/>  &nbsp
								<bean:message key="label.outof"/> &nbsp
							  	<c:out value="${sessionScope.totalQuestionCount}"/>
								<bean:message key="label.toFinish"/>						  	
							  	</b> </font>
						  	</td>
						  </tr>	
  					</c:if> 			
					
					
  		  	 		<c:set var="mainQueIndex" scope="session" value="0"/>
					<c:forEach var="questionEntry" items="${sessionScope.mapQuestionContentLearner}">
					<c:set var="mainQueIndex" scope="session" value="${mainQueIndex +1}"/>
						  <tr>
						  	<td NOWRAP align=left class="input" valign=top bgColor="#999966" colspan=2> 
							  	<font color="#FFFFFF"> 
								  	<font size=2>
								  		<c:out value="${questionEntry.value}"/> 
								  	</font>
							  	</font> 
						  	</td>
						  </tr>

								  								  
						  <tr>						 
							<td NOWRAP align=left>
							<table align=left>
			  		  	 		<c:set var="queIndex" scope="session" value="0"/>
								<c:forEach var="mainEntry" items="${sessionScope.mapGeneralOptionsContent}">
									<c:set var="queIndex" scope="session" value="${queIndex +1}"/>
										<c:if test="${sessionScope.mainQueIndex == sessionScope.queIndex}"> 		
									  		<c:forEach var="subEntry" items="${mainEntry.value}">
				  								<tr> 
													<td NOWRAP align=left class="input" valign=top> 
					   								    <img src="images/dot.jpg" align=left> &nbsp
														<font size=2 color="#669966">	<c:out value="${subEntry.value}"/> </font>					   								    
													</td> 
												</tr>	
											</c:forEach>

												<tr>												
												<td NOWRAP colspan=2 align=left class="input" valign=top> 
													<font size=2>
					   								    <bean:message key="label.you.answered"/>
				   								    </font>
												</td> 
												</tr>
												
												<tr>
													<td NOWRAP align=left class="input" valign=top> 											
														<table align=left>
													  		<c:forEach var="subEntry" items="${mainEntry.value}">
						  											<c:forEach var="selectedMainEntry" items="${sessionScope.mapGeneralCheckedOptionsContent}">
																				<c:if test="${selectedMainEntry.key == sessionScope.queIndex}"> 		
																			  		<c:forEach var="selectedSubEntry" items="${selectedMainEntry.value}">
																						<c:if test="${subEntry.key == selectedSubEntry.key}"> 		
																								<tr> 
																									<td NOWRAP align=left class="input" valign=top> 
																										<font size=2>
																											<b> <c:out value="${subEntry.value}"/> </b>
																										</font>
																									</td> 
																								</tr>
										  												</c:if> 			
																					</c:forEach>																						
							  													</c:if> 			
																	</c:forEach>		
																</tr>																			
												  			</c:forEach>											
														</table>
													</td>

													<td NOWRAP align=right class="input" valign=top> 											
														<font size=2>
															<c:forEach var="mainEntry" items="${sessionScope.mapLearnerAssessmentResults}">
																	<c:if test="${mainEntry.key == sessionScope.queIndex}"> 		
																		<c:if test="${mainEntry.value == 'true'}"> 		
																			<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackCorrect}">
																				<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																					    <img src="images/tick.gif" align=right width=20 height=20>
																				</c:if> 																																				
																			</c:forEach>											
																		</c:if> 														
																		
																		<c:if test="${mainEntry.value == 'false'}"> 		
																			<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackIncorrect}">
																				<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																					    <img src="images/cross.gif" align=right width=20 height=20>
																				</c:if> 																																				
																			</c:forEach>											
																		</c:if> 														
																	</c:if> 																		
															</c:forEach>		
														</font>
													</td>
												</tr>

												<tr>
												<td NOWRAP bgcolor="#CCCC99" colspan=2 align=left class="input" valign=top> 											
													<font size=2>
														<c:forEach var="mainEntry" items="${sessionScope.mapLearnerAssessmentResults}">
																<c:if test="${mainEntry.key == sessionScope.queIndex}"> 		
																	<c:if test="${mainEntry.value == 'true'}"> 		
																		<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackCorrect}">
																			<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																					<c:out value="${feedbackEntry.value}"/>
																			</c:if> 																																				
																		</c:forEach>											
																	</c:if> 														
																	
																	<c:if test="${mainEntry.value == 'false'}"> 		
																		<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackIncorrect}">
																			<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																					<c:out value="${feedbackEntry.value}"/>
																			</c:if> 																																				
																		</c:forEach>											
																	</c:if> 														
																</c:if> 																		
														</c:forEach>											
													</font>
												</td>
												</tr>
											
										</c:if> 			
								</c:forEach>
							</table>
							</td>
						</tr>
					</c:forEach>

			  	   	<tr> 
				 		<td NOWRAP colspan=2 class="input" valign=top> 
				 		&nbsp
				 		</td>
			  	   </tr>

			  	   

			 		<c:if test="${sessionScope.isRetries == 'true'}"> 					  	   
		  	   		  <tr>
					  	<td NOWRAP colspan=2 align=center class="input" valign=top> 
						  	<font size=2>
					  			<html:submit property="redoQuestions" styleClass="button">
									<bean:message key="label.redo.questions"/>
								</html:submit>	 		
			       
								<c:if test="${sessionScope.userPassed == 'true'}">
							  	   <html:submit property="learnerFinished" styleClass="button">
										<bean:message key="label.finished"/>
								   </html:submit>
						  	   </c:if>
	
		   						<html:submit property="viewSummary" styleClass="button">
									<bean:message key="label.view.summary"/>
								</html:submit>	 				 		  					
							</font>
					  	 </td>
					  </tr>
					</c:if> 																		

					<c:if test="${sessionScope.isRetries != 'true'}"> 							  
		  	   		  <tr>
		  	   		    <td NOWRAP colspan=2 align=right class="input" valign=top>
			  	   		    <font size=2>
				  	   		  	<c:if test="${sessionScope.userPassed == 'true'}">
							  	   <html:submit property="learnerFinished" styleClass="button">
												<bean:message key="label.finished"/>
								   </html:submit>
					  	   		</c:if>
	
		   						<html:submit property="viewSummary" styleClass="button">
									<bean:message key="label.view.summary"/>
								</html:submit>	 				 		  					
							</font>
					  	 </td>
					  </tr>
					</c:if> 																		
					
				</table>
	</html:form>

