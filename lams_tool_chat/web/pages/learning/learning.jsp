<%@ include file="/common/taglibs.jsp"%>

<!--  JsJaC Library -->
<script type="text/javascript"
	src="${tool}includes/javascript/jsjac-1.3.1/jsjac.js"></script>
<!--  <script language="JavaScript" type="text/javascript" src="Debugger.js"></script> -->

<!--  Chat Config -->
<script type="text/javascript">
	var HTTPBASE = "${tool}JHB/";
	var XMPPDOMAIN = "${XMPPDOMAIN}";
	var USERNAME = "${chatUserDTO.userID}";
	var PASSWORD = "${chatUserDTO.userID}";
	var CONFERENCEROOM = "${CONFERENCEROOM}";
	var NICK = "${chatUserDTO.jabberNickname}";
	var RESOURCE = "lams_chatclient";
	var MODE = "${MODE}";
	var USER_UID = "${chatUserDTO.uid}";	
	var LEARNER_FINISHED = "${chatUserDTO.finishedActivity}";
	var LOCK_ON_FINISHED = "${chatDTO.lockOnFinish}";
	var REFLECT_ON_ACTIVITY = "${chatDTO.reflectOnActivity}";	
</script>

<!--  Chat Client -->
<script type="text/javascript"
	src="${tool}includes/javascript/learning.js"></script>

<div id="content">
	<h1>
		<c:out value="${chatDTO.title}" escapeXml="false" />
	</h1>

	<p>
		<c:out value="${chatDTO.instructions}" escapeXml="false" />
	</p>

   <c:if test="${MODE == 'learner' || MODE == 'author'}">
	<c:if test="${chatDTO.lockOnFinish}"> 
		<div class="info">
			<c:choose>
				<c:when test="${chatUserDTO.finishedActivity}">
					<fmt:message key="message.activityLocked"/> 
				</c:when>
				<c:otherwise>
					<fmt:message key="message.warnLockOnFinish" />
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>
   </c:if>
	&nbsp;	
	
	<div id="chat_content">

		<form name="sendForm" action="" onSubmit="return sendMsg(this);">
			<div>


				<div id="roster"></div>
				<div id="iResp">
					<fmt:message>message.loading</fmt:message>
				</div>

				<br />

				<c:if test="${MODE == 'teacher' }">
					<div class="field-name">
						<fmt:message>label.sendMessageTo</fmt:message>
						<span id="sendToEveryone"><fmt:message>label.everyone</fmt:message>
						</span><span id="sendToUser" style="display: none"></span>
					</div>
				</c:if>

				<div>

					<table cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<textarea id="msgArea" name="msg"
									onKeyPress="return checkEnter(event);" rows="2" cols="2"></textarea>
							</td>

							<td valign="middle" width="105px">
								<input id="sendButton" class="button" type="submit"
									value='<fmt:message>button.send</fmt:message>' />

							</td>
						</tr>

					</table>

				</div>
			</div>
		</form>

		<c:if test="${MODE == 'learner' || MODE == 'author'}">
			<%@ include file="parts/finishButton.jsp"%>
		</c:if>
	</div>
</div>

