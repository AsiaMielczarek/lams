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
<%@ taglib uri="tags-logic-el" prefix="logic-el" %>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

	                                     
		           <h2>  <bean:message key="label.authoring.vote.basic"/> </h2>
					<table class="forms">
						<tr>   
						<td NOWRAP class=error>
							<c:if test="${sbmtSuccess == 'true' }"> 			
								<img src="<c:out value="${tool}"/>images/success.gif" align="left" width=20 height=20>  <bean:message key="sbmt.successful"/>  </img>
							</c:if> 			

							<c:if test="${userExceptionMaxNominationInvalid == 'true'}"> 										
									<img src="<c:out value="${tool}"/>images/error.jpg" align="left" width=20 height=20>  <bean:message key="error.maxNominationCount.invalid"/>  </img>
							</c:if> 			

							<c:if test="${validationError == 'true'}"> 			
									<img src="<c:out value="${tool}"/>images/error.jpg" align="left" width=20 height=20>  <bean:message key="error.fields.mandatory"/>  </img>
							</c:if> 			
							
							<c:if test="${userExceptionOptionsDuplicate == 'true'}"> 			
									<img src="<c:out value="${tool}"/>images/error.jpg" align="left" width=20 height=20>   <bean:message key="error.duplicate.nomination"/>  </img>
							</c:if> 			
							
						</td>
						</tr> 
					</table>


					<table class="forms">
						<tr> 
							<td colspan=2 NOWRAP valign=top>
								<lams:SetEditor id="title" text="${activityTitle}" small="true" key="label.authoring.title.col"/>								
							</td> 
							<td> </td>
					  	</tr>

					  	<tr> 
							<td colspan=2 NOWRAP valign=top>
								<lams:SetEditor id="instructions" text="${activityInstructions}" small="true" key="label.authoring.instructions.col"/>								
							</td> 
							<td> </td>							
						</tr>
				
			 		<!--default Option content, this entry can not be deleted but can be updated -->
				 		<tr> 
							<td NOWRAP valign=top>
								<lams:SetEditor id="optionContent0" text="${defaultOptionContent}" small="true" key="label.nomination.col"/>								
							</td> 
							
						  	<td NOWRAP valign=middle>			
								  <img src="<c:out value="${tool}"/>images/down.gif" align=left onclick="javascript:submitModifyNomination('1','moveNominationDown');"> 
							</td> 								
							<td> </td>														 
					  	</tr>
		
				  	<!--end of default Option content -->
				  	
			  		<!-- if there is more than just the default content start presenting them -->
			  	 		<c:set var="optIndex" scope="session" value="1"/>
						<c:forEach var="optionEntry" items="${sessionScope.mapOptionsContent}">
					  		<c:if test="${optionEntry.key > 1}"> 			
								<c:set var="optIndex" scope="session" value="${optIndex +1}"/>
								  <tr>
									<td NOWRAP valign=top>
										<lams:SetEditor id="optionContent${optIndex-1}" text="${optionEntry.value}" small="true" key="label.nomination.col"/>								
	                                	<c:if test="${ activeModule != 'monitoring' }"> 			
			 		 						<html:submit property="removeContent" 
	                                                     styleClass="linkbutton"  
	                                                     onclick="removeNomination(${optIndex});">
												<bean:message key="button.delete"/>
											</html:submit>
										</c:if> 			
										
	                                	<c:if test="${ activeModule == 'monitoring' }"> 			
			 		 						<html:submit property="removeContent" 
	                                                     styleClass="linkbutton"  
	                                                     onclick="removeMonitoringNomination(${optIndex});">
												<bean:message key="button.delete"/>
											</html:submit>
										</c:if> 													
                                    </td>
	      						  	<td NOWRAP valign=middle>			
						  				<c:if test="${maxOptionIndex == optIndex}"> 			
		     								  <img src="<c:out value="${tool}"/>images/up.gif" align=left onclick="javascript:submitModifyNomination('<c:out value="${optIndex}"/>','moveNominationUp');"> 
		     							</c:if> 	    
										
		 				  				<c:if test="${maxOptionIndex != optIndex }"> 			
			   								    <img src="<c:out value="${tool}"/>images/down.gif" align=left onclick="javascript:submitModifyNomination('<c:out value="${optIndex}"/>','moveNominationDown');">		  	   								 
			       								<img src="<c:out value="${tool}"/>images/up.gif" align=left onclick="javascript:submitModifyNomination('<c:out value="${optIndex}"/>','moveNominationUp');">		  	
										</c:if> 	           								 
		                         	</td>
								  </tr>
							</c:if> 			
						</c:forEach>
						<html:hidden property="toolContentId" value="${VoteAuthoringForm.toolContentId}"/>
						<html:hidden property="optIndex"/>
                        
                        <tr>
                            <td></td>
                            <td align="right">
                                <html:submit property="addContent" 
                                             styleClass="linkbutton" 
                                             onclick="submitMethod('addNewNomination');">
                                    <bean:message key="button.add"/>
                                </html:submit>
                            </td>
                        </tr>
					
					</table>

      	<c:if test="${ activeModule != 'authoring' }"> 					
			<HR>
			<p align="right">
		        <a href="javascript:submitMethod('submitAllContent')" class="button">
		        	<bean:message key="label.save"/></a>
			</p>
		</c:if> 					


<SCRIPT language="JavaScript"> 

	function removeNomination(optIndex)
	{
		document.VoteAuthoringForm.optIndex.value=optIndex;
        submitMethod('removeNomination');
	}

	function removeMonitoringNomination(optIndex)
	{
		document.VoteMonitoringForm.optIndex.value=optIndex;
        submitMonitoringMethod('removeNomination');
	}
	
 </SCRIPT>
