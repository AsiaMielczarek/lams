<%@ include file="/common/taglibs.jsp"%>

<c:if test="${finishedLock && (fn:length(question.questionResults) > 1) && assessment.allowHistoryResponses}">
	<div style="padding: 2px 15px 10px; font-style: italic; color:#47bc23;">
		<fmt:message key="label.learning.question.summary.history.responces" />
	</div>

	<table class="forum" style="background:none; margin-bottom:60px; margin-bottom: 10px; width: 83%; padding: 0px 15px 0px; font-style: italic; color:#47bc23; border-bottom: none;" cellspacing="0" >
		<tr>
			<th style="width: 30px; border-left: none; padding-top:0px; padding-left:0px; text-align: center;" >
				#
			</th>		
			<th style="width: 120px; padding-top:0px; " >
				<fmt:message key="label.learning.summary.completed.on" />
			</th>
			<th style="padding-top:0px; " >
				<fmt:message key="label.learning.question.summary.response" />
			</th>
			<c:if test="${assessment.allowGradesAfterAttempt}">			
				<th style="width: 70px; padding-top:0px; " >
					<fmt:message key="label.learning.question.summary.grade" />
				</th>	
			</c:if>		
		</tr>
		<c:forEach var="item" items="${question.questionResults}" varStatus="status">
			<c:set var="questionResult" value="${item[0]}" />
			<c:set var="currentAssessmentResult" value="${item[1]}" />
			<tr>
				<td style="padding-left: 15px; vertical-align: middle; background: none; 
						<c:if test='${status.last}'>border-bottom: none;</c:if>	" >
					${status.index + 1} 
				</td>
				<td style="padding-left: 15px; vertical-align: middle; background: none;
						<c:if test='${status.last}'>border-bottom: none;</c:if>	" >
					<div>
						${currentAssessmentResult.finishDate} 
					</div>
				</td>					
				<td style="vertical-align: middle; background: none;
						<c:if test='${status.last}'>border-bottom: none;</c:if>	" >
					<c:choose>
						<c:when test="${question.type == 1}">
							<c:forEach var="questionOption" items="${question.questionOptions}">
								<c:forEach var="optionAnswer" items="${questionResult.optionAnswers}">
									<c:if test="${optionAnswer.answerBoolean && (optionAnswer.questionOptionUid == questionOption.uid)}">
										${questionOption.optionString}
									</c:if>
								</c:forEach>					
							</c:forEach>						
						</c:when>
						<c:when test="${question.type == 2}">
							<table style="padding: 0px; margin: 0px; border: none; " cellspacing="0" cellpadding="0">
								<c:forEach var="questionOption" items="${question.questionOptions}">
									<tr>
										<td style="width:40%; background: none; padding: 0px; margin: 0px; border: none;">
											${questionOption.question}
										</td>
										<td style="background: none; padding: 0px; margin: 0px; border: none; vertical-align: middle;">
											- 
											<c:forEach var="optionAnswer" items="${questionResult.optionAnswers}">
												<c:if test="${questionOption.uid == optionAnswer.questionOptionUid}">
													<c:forEach var="questionOption2" items="${question.questionOptions}">
														<c:if test="${questionOption2.uid == optionAnswer.answerInt}">
															${questionOption2.optionString}
														</c:if>
													</c:forEach>
												</c:if>
											</c:forEach>										
										</td>
									</tr>
								</c:forEach>
							</table>
						</c:when>
						<c:when test="${question.type == 3}">
							${questionResult.answerString}
						</c:when>
						<c:when test="${question.type == 4}">
							${questionResult.answerString}
						</c:when>
						<c:when test="${question.type == 5}">
							<c:if test="${questionResult.answerString != null}">			
								${questionResult.answerBoolean}
							</c:if>
						</c:when>
						<c:when test="${question.type == 6}">
							${questionResult.answerString}
						</c:when>
						<c:when test="${question.type == 7}">
							<c:forEach var="i" begin="0" end="${fn:length(questionResult.optionAnswers) - 1}" step="1">
								<c:forEach var="optionAnswer" items="${questionResult.optionAnswers}">
									<c:if test="${optionAnswer.answerInt == i}">		
										<c:forEach var="questionOption" items="${question.questionOptions}">
											<c:if test="${optionAnswer.questionOptionUid == questionOption.uid}">
												${questionOption.optionString}
											</c:if>
										</c:forEach>
									</c:if>								
								</c:forEach>
							</c:forEach>
						</c:when>
					</c:choose>
				</td>
				<c:if test="${assessment.allowGradesAfterAttempt}">
					<td style="padding-left: 0px; vertical-align: middle; background: none;
							<c:if test='${status.last}'>border-bottom: none;</c:if>	" >
						<div style="text-align: center;">
							<fmt:formatNumber value="${questionResult.mark}" maxFractionDigits="3"/>
						</div>
					</td>
				</c:if>		
			</tr>
		</c:forEach>	
	

	</table>
</c:if>							