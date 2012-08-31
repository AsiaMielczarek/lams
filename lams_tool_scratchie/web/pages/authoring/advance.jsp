<%@ include file="/common/taglibs.jsp"%>
<c:set var="formBean" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />

<!-- Advance Tab Content -->

<p>
	<html:checkbox property="scratchie.extraPoint" styleClass="noBorder" styleId="extraPoint"/>
	<label for="extraPoint">
		<fmt:message key="label.authoring.advanced.give.extra.point" />
	</label>
</p>

<p>
	<html:checkbox property="scratchie.showResultsPage" styleClass="noBorder" styleId="showResultsPage"/>
	<label for="showResultsPage">
		<fmt:message key="label.authoring.advanced.show.results.page" />
	</label>
</p>

<p>
	<html:checkbox property="scratchie.reflectOnActivity" styleClass="noBorder" styleId="reflectOn"/>
	<label for="reflectOn">
		<fmt:message key="label.authoring.advanced.reflectOnActivity" />
	</label>
</p>

<p>
	<html:textarea property="scratchie.reflectInstructions" styleId="reflectInstructions" cols="30" rows="3" />
</p>
<script type="text/javascript">
<!--
//automatically turn on refect option if there are text input in refect instruction area
	var ra = document.getElementById("reflectInstructions");
	var rao = document.getElementById("reflectOn");
	function turnOnRefect(){
		if(isEmpty(ra.value)){
		//turn off	
			rao.checked = false;
		}else{
		//turn on
			rao.checked = true;		
		}
	}

	ra.onkeyup=turnOnRefect;
//-->
</script>
