<%@ include file="/common/taglibs.jsp"%>
<c:set var="formBean" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />
<script type="text/javascript">
	function showConditionMessage(url) {
		$.ajaxSetup({ cache: true });
		$("#conditionInputArea").load(url, function() {
			$(this).show();
			$("#saveCancelButtons").hide();
		});
	}
	function hideConditionMessage(){
		$("#conditionInputArea").hide();
		$("#saveCancelButtons").show();
	}
	
	function editCondition(orderId,sessionMapID){
		var url = "<c:url value='/authoring/editCondition.do?orderId='/>"
			  + orderId + "&sessionMapID=" + sessionMapID;
		showConditionMessage(url);
	}
	
	function deleteCondition(orderId, sessionMapID){
		$("#conditionsArea").load("<c:url value='/authoring/removeCondition.do'/>",{
			'orderId' : orderId,
			'sessionMapID' : sessionMapID
		});
	}
	
	function upCondition(orderId,sessionMapID){
		$("#conditionsArea").load("<c:url value='/authoring/upCondition.do'/>",{
			'orderId' : orderId,
			'sessionMapID' : sessionMapID
		});
	}
	function downCondition(orderId,sessionMapID){
		$("#conditionsArea").load("<c:url value='/authoring/downCondition.do'/>",{
			'orderId' : orderId,
			'sessionMapID' : sessionMapID
		});
	}
	
	//Packs additional elements and submits the question form
	function submitCondition(){
		var form = $('#QaConditionForm');
		$('#conditionInputArea').load(form.attr('action'), form.serialize());
	} 

</script>

<!-- Conditions Tab Content -->
<div id="conditionsArea">
	<c:set var="sessionMapID" value="${formBean.httpSessionID}" />
	<%@ include file="/authoring/conditionList.jsp"%>
</div>
 
 <div class="form-inline">
	<a href="javascript:showConditionMessage('<html:rewrite page="/authoring/newConditionInit.do?sessionMapID=${formBean.httpSessionID}"/>');" 
		class="btn btn-default btn-sm"><i class="fa fa-plus"></i>&nbsp;<fmt:message key="label.authoring.conditions.add.condition" /></a> 
</div>

<div id="conditionInputArea" name="conditionInputArea" class="voffset10"></div>