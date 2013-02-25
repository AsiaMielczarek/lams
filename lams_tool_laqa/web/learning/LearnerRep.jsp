<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>
<c:set var="sessionMap" value="${sessionScope[generalLearnerFlowDTO.httpSessionID]}" />

<lams:html>
<lams:head>
	<html:base />
	<title><fmt:message key="activity.title" /></title>
	
	<lams:css />
	<link type="text/css" href="${lams}css/jquery.jRating.css" rel="stylesheet"/>
	<link rel="stylesheet" href="${lams}css/jquery.tablesorter.theme-blue.css">
	<link rel="stylesheet" href="${lams}css/jquery.tablesorter.pager.css">
	<style media="screen,projection" type="text/css">
		.rating-stars-div {margin-top: 8px;}
		.user-answer {padding: 7px 2px;}
		tr.odd:hover .jStar {background-image: url(${lams}images/css/jquery.jRating-stars-grey.png)!important;}
		tr.even:hover .jStar {background-image: url(${lams}images/css/jquery.jRating-stars-light-grey.png)!important;}
		tr.odd .jStar {background-image: url(${lams}images/css/jquery.jRating-stars-light-blue.png)!important;}
		.tablesorter-blue {margin-bottom: 5px;}
		.pager {padding-bottom: 20px;}
	</style>

	<script type="text/javascript"> 
		//var for jquery.jRating.js
		var pathToImageFolder = "${lams}images/css/"; 
	</script>
	<script src="${lams}includes/javascript/jquery.js" type="text/javascript"></script>
	<script src="${lams}includes/javascript/jquery.jRating.js" type="text/javascript"></script>
	<script src="${lams}includes/javascript/jquery.tablesorter.js" type="text/javascript"></script>
	<script src="${lams}includes/javascript/jquery.tablesorter-pager.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function(){

			$(".tablesorter").tablesorter({
				theme: 'blue',
			    widthFixed: true,
			    widgets: ['zebra']
			});
			
			$(".tablesorter").each(function() {
				$(this).tablesorterPager({
				    container: $(this).next(".pager"),
				    output: '{startRow} to {endRow} ({totalRows})',// possible variables: {page}, {totalPages}, {filteredPages}, {startRow}, {endRow}, {filteredRows} and {totalRows}
				    // if true, the table will remain the same height no matter how many records are displayed. The space is made up by an empty
				    // table row set to a height to compensate; default is false
				    fixedHeight: true,
				    // remove rows from the table to speed up the sort of large tables.
				    // setting this to false, only hides the non-visible rows; needed if you plan to add/remove rows with the pager enabled.
				    removeRows: false,
				    // css class names of pager arrows
				    cssNext: '.tablesorter-next', // next page arrow
					cssPrev: '.tablesorter-prev', // previous page arrow
					cssFirst: '.tablesorter-first', // go to first page arrow
					cssLast: '.tablesorter-last', // go to last page arrow
					cssGoto: '.gotoPage', // select dropdown to allow choosing a page
					cssPageDisplay: '.pagedisplay', // location of where the "output" is displayed
					cssPageSize: '.pagesize', // page size selector - select dropdown that sets the "size" option
					// class added to arrows when at the extremes (i.e. prev/first arrows are "disabled" when on the first page)
					cssDisabled: 'disabled' // Note there is no period "." in front of this class name
				});
			});
	  	
		    $(".rating-stars").jRating({
		    	phpPath : "<c:url value='/learning.do'/>?method=rateResponse&toolSessionID=" + $("#toolSessionID").val(),
		    	rateMax : 5,
		    	decimalLength : 1,
			  	onSuccess : function(data, responseUid){
			    	$("#averageRating" + responseUid).html(data.averageRating);
			    	$("#numberOfVotes" + responseUid).html(data.numberOfVotes);
			    	$("#averageRating" + responseUid).parents(".tablesorter").trigger("update");
				},
			  	onError : function(){
			    	jError('Error : please retry');
			  	}
			});
		    $(".rating-stars-disabled").jRating({
		    	rateMax : 5,
		    	isDisabled : true
		    });
		 });
	
		function submitLearningMethod(actionMethod) {	
			if (actionMethod == 'endLearning') {
				$("#finishButton").attr("disabled", true);
			}
			document.QaLearningForm.method.value=actionMethod; 
			document.QaLearningForm.submit();
		}
		
		function submitMethod(actionMethod) {
			submitLearningMethod(actionMethod);
		}
	</script>
</lams:head>

<body class="stripes">

	<div id="content">
		<h1>
			<c:out value="${generalLearnerFlowDTO.activityTitle}" escapeXml="false" />
		</h1>
		
		<c:if test="${not empty sessionMap.submissionDeadline}">
			<div class="info">
				<fmt:message key="authoring.info.teacher.set.restriction" >
					<fmt:param><lams:Date value="${sessionMap.submissionDeadline}" /></fmt:param>
				</fmt:message>
			</div>
		</c:if>

		<h2>
			<fmt:message key="label.learnerReport" />
		</h2>

		<c:forEach var="currentDto" items="${generalLearnerFlowDTO.listMonitoredAnswersContainerDTO}">
			<c:set var="currentQuestionId" scope="request" value="${currentDto.questionUid}" />
					
			<div class="shading-bg">
				<p>
					<strong> <fmt:message key="label.question" /> : </strong>
					<c:out value="${currentDto.question}" escapeXml="false" />
				</p>

				<c:forEach var="questionAttemptData" items="${currentDto.questionAttempts}">
					<c:forEach var="sData" items="${questionAttemptData.value}">
						<c:set var="userData" scope="request" value="${sData.value}" />
						<c:set var="responseUid" scope="request" value="${userData.uid}" />

						<c:if test="${generalLearnerFlowDTO.userUid == userData.queUsrId}">
		
							<c:if test="${currentQuestionId == userData.questionUid}">
								<p>
									<span class="field-name"> 
										<c:out value="${userData.userName}" /> 
									</span> 
									-
									<lams:Date value="${userData.attemptTime}" />
								</p>
								<p>
									<c:out value="${userData.responsePresentable}" escapeXml="false" />
								</p>
								<jsp:include page="parts/ratingStarsDisabled.jsp" />
							</c:if>
						</c:if>								
					</c:forEach>
				</c:forEach>
			</div>
		</c:forEach>
				
		<c:if test="${generalLearnerFlowDTO.existMultipleUserResponses == 'true'}">				
			<h2>
				<fmt:message key="label.other.answers" />
			</h2>
	
			<c:forEach var="currentDto" items="${generalLearnerFlowDTO.listMonitoredAnswersContainerDTO}">
				<c:set var="currentQuestionId" scope="request" value="${currentDto.questionUid}" />
			
				<p>
					<strong> <fmt:message key="label.question" /> : </strong>
					<c:out value="${currentDto.question}" escapeXml="false" />
				</p>
				
				<table class="tablesorter">
					<thead>
						<tr>
							<th><fmt:message key="label.learning.answer" /></th>
							<c:if test="${generalLearnerFlowDTO.allowRateAnswers == 'true'}">
								<th><fmt:message key="label.learning.rating" /></th>
							</c:if>
						</tr>
					</thead>
					<tbody>

						<c:forEach var="questionAttemptData" items="${currentDto.questionAttempts}">
							<c:forEach var="sData" items="${questionAttemptData.value}">
								<c:set var="userData" scope="request" value="${sData.value}" />
								<c:set var="responseUid" scope="request" value="${userData.uid}" />
				
								<c:if test="${generalLearnerFlowDTO.userUid != userData.queUsrId}">	
									<c:if test="${currentQuestionId == userData.questionUid}">
										<tr>
											<td>
												<div>
													<span class="field-name"> 
														<c:out value="${userData.userName}" /> 
													</span>
																			
													<c:if test="${generalLearnerFlowDTO.userNameVisible == 'true'}">																			
														<lams:Date value="${userData.attemptTime}" />
													</c:if>																						
												</div>
												<div class="user-answer">
													<c:choose>
														<c:when test="${userData.visible == 'true'}">
															<c:out value="${userData.responsePresentable}" escapeXml="false" />
														</c:when>
														<c:otherwise>
															<i><fmt:message key="label.hidden"/></i>
														</c:otherwise>
													</c:choose>												
												</div>
											</td>
											
											<c:if test="${generalLearnerFlowDTO.allowRateAnswers == 'true'}">
												<td style="width:50px;">
													<c:if test="${userData.visible == 'true'}">	
														<jsp:include page="parts/ratingStars.jsp" />
													</c:if>	
												</td>
											</c:if>
										</tr>
									</c:if>
								</c:if>									
							</c:forEach>
						</c:forEach>
					</tbody>
				</table>
					
				<!-- pager -->
				<div class="pager">
					<form>
					   	<img class="tablesorter-first"/>
				    	<img class="tablesorter-prev"/>
				    	<span class="pagedisplay"></span> <!-- this can be any element, including an input -->
				   		<img class="tablesorter-next"/>
				    	<img class="tablesorter-last"/>
				    	<select class="pagesize">
				      		<option selected="selected" value="10">10</option>
				      		<option value="20">20</option>
				      		<option value="30">30</option>
				      		<option value="40">40</option>
				      		<option value="50">50</option>
				      		<option value="100">100</option>
				    	</select>
				  </form>
				</div>	
										
			</c:forEach>
		</c:if>	
			
		<html:form action="/learning?validate=false" enctype="multipart/form-data" method="POST" target="_self">
			<html:hidden property="toolSessionID" styleId="toolSessionID"/>
			<html:hidden property="userID" />
			<html:hidden property="httpSessionID" />
			<html:hidden property="totalQuestionCount" />
				
			<c:choose>
				<c:when test="${generalLearnerFlowDTO.requestLearningReportProgress != 'true'}">
					<c:choose>
						<c:when test="${generalLearnerFlowDTO.reflection != 'true'}">
							<html:hidden property="method" value="endLearning"/>
						</c:when>
						<c:otherwise>
							<html:hidden property="method" value="forwardtoReflection"/>
						</c:otherwise>
					</c:choose>	
				</c:when>
					
				<c:otherwise>
					<html:hidden property="method" />
				</c:otherwise>
			</c:choose>	
				
			<c:if test="${generalLearnerFlowDTO.requestLearningReportViewOnly != 'true' }">
				<c:if test="${generalLearnerFlowDTO.teacherViewOnly != 'true' }">

					<html:button property="refreshAnswers" styleClass="button" onclick="submitMethod('refreshAllResults');">
						<fmt:message key="label.refresh" />
					</html:button>

					<c:if test="${generalLearnerFlowDTO.lockWhenFinished != 'true'}">
						<html:button property="redoQuestions" styleClass="button" onclick="submitMethod('redoQuestions');">
							<fmt:message key="label.redo" />
						</html:button>
					</c:if>	
						
					<div class="space-bottom-top" align="right">
						<c:if test="${generalLearnerFlowDTO.reflection != 'true'}">
							<html:link href="#nogo" property="endLearning" styleId="finishButton"
								onclick="javascript:submitMethod('endLearning'); return false;"
								styleClass="button">
								<span class="nextActivity"><fmt:message key="button.endLearning" /></span>
							</html:link>
						</c:if>

						<c:if test="${generalLearnerFlowDTO.reflection == 'true'}">
							<html:button property="forwardtoReflection"
								onclick="javascript:submitMethod('forwardtoReflection');"
								styleClass="button">
								<fmt:message key="label.continue" />
							</html:button>
						</c:if>
					</div>
				</c:if>
			</c:if>
		
		</html:form>
	
	</div>
	<div id="footer"></div>
</body>
</lams:html>
