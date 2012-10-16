<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>
	<%-- param has higher level for request attribute --%>
	<c:if test="${not empty param.sessionMapID}">
		<c:set var="sessionMapID" value="${param.sessionMapID}" />
	</c:if>
	<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />

	<c:set var="mode" value="${sessionMap.mode}" />
	<c:set var="toolSessionID" value="${sessionMap.toolSessionID}" />
	<c:set var="scratchie" value="${sessionMap.scratchie}" />
	<c:set var="isUserLeader" value="${sessionMap.user.leader}" />
	<c:set var="isScratchingFinished" value="${sessionMap.isScratchingFinished}" />

<lams:html>
<lams:head>
	<title><fmt:message key="label.learning.title" /></title>
	<%@ include file="/common/header.jsp"%>
	<style type="text/css">
    	#scratches {margin: 40px 0px; border-spacing: 0;}
    	#scratches tr td { padding: 12px 15px; }
    	#scratches a, #scratches a:hover {border-bottom: none;}
    	.scartchie-image {border: 0;}
    </style>

	<script language="JavaScript" type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery-ui.js"></script>
	<script type="text/javascript">
	<!--

		function scratchItem(itemUid, answerUid){
			var id = '-' + itemUid + '-' + answerUid;
			
	        $.ajax({
	        	async: false,
	            url: '<c:url value="/learning/scratchItem.do"/>',
	            data: 'sessionMapID=${sessionMapID}&answerUid=' + answerUid,
	            dataType: 'json',
	            type: 'post',
	            success: function (json) {
	            	if (json.answerCorrect) {
	            		//show animation
	            		$('#image' + id).attr("src", "<html:rewrite page='/includes/images/scratchie-correct-animation.gif'/>?reqID=" + (new Date()).getTime());
	            		
	            		//disable scratching
	            		$("[id^=imageLink-" + itemUid + "]").removeAttr('onclick'); 
	            		$("[id^=imageLink-" + itemUid + "]").css('cursor','default');
	            		$("[id^=image-" + itemUid + "]").not("img[src*='scratchie-correct-animation.gif']").not("img[src*='scratchie-correct.gif']").fadeTo(1300, 0.3);

	            	} else {
	            		
	            		//show animation, disable onclick
	            		$('#image' + id).attr("src", "<html:rewrite page='/includes/images/scratchie-wrong-animation.gif'/>?reqID=" + (new Date()).getTime());
	            		$('#imageLink' + id).removeAttr('onclick');
	            		$('#imageLink' + id).css('cursor','default');
	            	}
	            }
	       	});
		}

		function finish(isShowResultsPage){
			var numberOfAvailableScratches = $("[id^=imageLink-][onclick]").length;
			var	finishConfirmed = (numberOfAvailableScratches > 0) ? confirm("<fmt:message key="label.one.or.more.questions.not.completed"></fmt:message>") : true;
			
			if (finishConfirmed) {
				document.getElementById("finishButton").disabled = true;
				if (isShowResultsPage) {
					document.location.href ='<c:url value="/learning/showResults.do?sessionMapID=${sessionMapID}"/>';
				} else {
					document.location.href ='<c:url value="/learning/finish.do?sessionMapID=${sessionMapID}"/>';
				}
				
				return false;
			}
		}
		function continueReflect(){
			document.location.href='<c:url value="/learning/newReflection.do?sessionMapID=${sessionMapID}"/>';
		}
		
		if (${!isUserLeader && mode != "teacher"}) {
			setInterval("refreshQuestionList();",10000);// Auto-Refresh every 30 seconds
		}
		
		function refreshQuestionList() {
			var url = "<c:url value="/learning/refreshQuestionList.do"/>";
			$("#questionListArea").load(
				url,
				{
					sessionMapID: "${sessionMapID}"
				},
				function(){
					//reinitializePassingMarkSelect(false);
					//refreshThickbox();
				}
			);
		}
		
	-->        
    </script>
</lams:head>
<body class="stripes">

	<div id="content">
		<h1>
			${scratchie.title}
		</h1>
		
		<h4>
			<fmt:message key="label.group.leader" >
				<fmt:param>${sessionMap.groupLeader.firstName} ${sessionMap.groupLeader.lastName}</fmt:param>
			</fmt:message>
		</h4>

		<p style="font-style: italic;">
			${scratchie.instructions}
		</p>

		<%@ include file="/common/messages.jsp"%>

		<div id="questionListArea">
			<%@ include file="questionlist.jsp"%>
		</div>

	</div>
	<!--closes content-->

	<div id="footer">
	</div>
	<!--closes footer-->

</body>
</lams:html>
