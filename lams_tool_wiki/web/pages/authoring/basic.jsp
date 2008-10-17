<%@ include file="/common/taglibs.jsp"%>

<c:set var="formBean" value="<%=request.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />

<!-- ========== Basic Tab ========== -->

<div id="wikimenu" style="margin-right:75px;">

	<div id="breadcrumb" style="float: left; width: 30%;">
		<c:if test="${currentWikiPage.title != mainWikiPage.title}">
			<a href='javascript:changeWikiPage("${mainWikiPage.javaScriptTitle}")'>${mainWikiPage.title}</a> :
		</c:if> 
		<a href='javascript:changeWikiPage("${currentWikiPage.javaScriptTitle}")'>${currentWikiPage.title}</a>
	</div>
	
	<div id="buttons" style="float: right; width: 70%; text-align:right; " >
		<a href="javascript:changeDiv('view');" title='<fmt:message key="label.wiki.view.toolTip"></fmt:message>'>
			<fmt:message key="label.wiki.view"></fmt:message>
		</a> 
		&nbsp;
		<a href="javascript:changeDiv('edit');" title='<fmt:message key="label.wiki.edit.toolTip"></fmt:message>'>
			<fmt:message key="label.wiki.edit"></fmt:message>
		</a> 
		&nbsp;
		<a href="javascript:cancelAdd();changeDiv('add');" title='<fmt:message key="label.wiki.add.toolTip"></fmt:message>'>
			<fmt:message key="label.wiki.add"></fmt:message>
		</a> 
		&nbsp;
		<a href="javascript:changeDiv('history');" title='<fmt:message key="label.wiki.history.toolTip"></fmt:message>'>
			<fmt:message key="label.wiki.history"></fmt:message>
		</a> 
		<c:if test="${currentWikiPage.title != mainWikiPage.title}">
			&nbsp;
			<a href="javascript:doRemove(&#39;<fmt:message key="label.wiki.remove.confirm"></fmt:message>&#39;)"
				title='<fmt:message key="label.wiki.remove.toolTip"></fmt:message>'
			>
				<fmt:message key="label.wiki.remove"></fmt:message>
			</a>
		</c:if>
	</div>
</div>

<br />
<br />
<hr />
<br />

<div id="view" style="margin-right:75px;">
	<h1>
		${currentWikiPage.title}
	</h1>
	
	<br />
	
	<i> 
		<fmt:message key="label.wiki.last.edit">
			<fmt:param>
				<c:choose>
					<c:when test="${currentWikiPage.currentWikiContentDTO.editorDTO == null}">
						<fmt:message key="label.wiki.history.editor.author"></fmt:message>
					</c:when>
					<c:otherwise>
						${currentWikiPage.currentWikiContentDTO.editorDTO.firstName} ${currentWikiPage.currentWikiContentDTO.editorDTO.lastName}
					</c:otherwise>
				</c:choose>
			</fmt:param>
			<fmt:param>
				<lams:Date value="${currentWikiPage.currentWikiContentDTO.editDate}" />
			</fmt:param>
		</fmt:message> 
	</i> 
	
	<br />
	<br />
	
	<div id="viewBody">
		${currentWikiPage.currentWikiContentDTO.body}
	</div>
</div>

<div id="history" style="display: none;">

	<h1>
	<fmt:message key="label.wiki.history" /> - ${currentWikiPage.title}
	</h1>
	
	<br />
	
	<c:choose>
		<c:when test="${not empty wikiPageContentHistory}">
			<table class="alternative-color">
				<tr>
					<th>
						<fmt:message key="label.wiki.history.version"></fmt:message>
					</th>
					<th>
						<fmt:message key="label.wiki.history.date"></fmt:message>
					</th>
					<th>
						<fmt:message key="label.wiki.history.editor"></fmt:message>
					</th>
					<th>
						<fmt:message key="label.wiki.history.actions"></fmt:message>
					</th>
				</tr>
				
				<c:forEach var="wikiContentPageVersion"items="${wikiPageContentHistory}">
					<c:if test="${wikiContentPageVersion.version != currentWikiPage.currentWikiContentDTO.version}">
						<tr>
							<td>
								${wikiContentPageVersion.version}
							</td>
							<td>
								<lams:Date value="${wikiContentPageVersion.editDate}" />
							</td>
							<td>
								<c:choose>
									<c:when test="${wikiContentPageVersion.editorDTO != null}">
										${wikiContentPageVersion.editorDTO.firstName} ${wikiContentPageVersion.editorDTO.firstName}
									</c:when>
									<c:otherwise>
										<fmt:message key="label.wiki.history.editor.author"></fmt:message>
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<a href="javascript:doRevert('${wikiContentPageVersion.uid}');" title='<fmt:message key="label.wiki.history.actions.compare.tooltip" />'>
									<fmt:message key="label.wiki.history.actions.revert" /> 
								</a>
								&nbsp; 
								<a href="javascript:doCompareOrView('<lams:WebAppURL/>', '${wikiContentPageVersion.uid}', '${currentWikiPage.uid}', 'comparePage');" title='<fmt:message key="label.wiki.history.actions.compare.tooltip" />'>
									<fmt:message key="label.wiki.history.actions.compare" /> 
								</a>
								&nbsp;
								<a href="javascript:doCompareOrView('<lams:WebAppURL/>', '${wikiContentPageVersion.uid}', '${currentWikiPage.uid}', 'viewPage');" title='<fmt:message key="label.wiki.history.actions.compare.tooltip" />'>
									<fmt:message key="label.wiki.history.actions.view" /> 
								</a>
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<fmt:message key="label.wiki.history.empty" />
		</c:otherwise>
	</c:choose>
</div>


<div id="edit" style="display: none;">
	<h1>
		<fmt:message key="label.wiki.edit"></fmt:message> - ${currentWikiPage.title}
	</h1>
	
	<table cellpadding="0">
		<tr>
			<td>
				<div class="field-name"><fmt:message key="label.authoring.basic.title"></fmt:message></div>
				<html:text property="title" styleId="title" style="width: 99%;" value="${currentWikiPage.title}"></html:text>
			</td>
		</tr>
		<tr>
			<td>
				<div class="field-name">
					<fmt:message key="label.wiki.body"></fmt:message>
				</div>
				<lams:FCKEditor id="wikiBody"
					value="${currentWikiPage.currentWikiContentDTO.body}"
					contentFolderID="${sessionMap.contentFolderID}"
					toolbarSet="Custom-Wiki" height="400px">
				</lams:FCKEditor>
			</td>
		</tr>
		<tr>
			<td>
				<html:checkbox property="isEditable" value="1" styleClass="noBorder" styleId="isEditable"></html:checkbox>
				&nbsp;
				<fmt:message key="label.authoring.basic.wikipagevisible"></fmt:message>
			</td>
		</tr>
		<tr>
			<td align="right">
				<a href="javascript:doEditOrAdd('editPage');" class="button"><fmt:message key="label.wiki.savechanges"></fmt:message></a>
				<a href="javascript:changeDiv('view');" class="button"><fmt:message key="button.cancel"></fmt:message></a>
			</td>
		</tr>
	</table>
</div>

<div id="add" style="display: none;">

	<h1>
		<fmt:message key="label.wiki.add"></fmt:message>
	</h1>
	<table cellpadding="0">
		<tr>
			<td>
				<div class="field-name">
					<fmt:message key="label.authoring.basic.title"></fmt:message>
				</div>
				<html:text property="newPageTitle" styleId="newPageTitle" style="width: 99%;" value=""></html:text>
			</td>
		</tr>
		<tr>
			<td>
				<div class="field-name">
					<fmt:message key="label.wiki.body"></fmt:message>
				</div>
				<lams:FCKEditor id="newPageWikiBody" value="" height="400px"
					contentFolderID="${sessionMap.contentFolderID}"
					toolbarSet="Custom-Wiki">
				</lams:FCKEditor>
			</td>
		</tr>
		<tr>
			<td>
				<html:checkbox property="newPageIsEditable" styleClass="noBorder" value="1" styleId="newPageIsEditable"></html:checkbox>
				&nbsp;
				<fmt:message key="label.authoring.basic.wikipagevisible"></fmt:message>
			</td>
		</tr>
		<tr>
			<td align="right">
				<a href="javascript:doEditOrAdd('addPage');" class="button"><fmt:message key="label.wiki.savechanges"></fmt:message></a>
				<a href="javascript:cancelAdd();changeDiv('view');" class="button"><fmt:message key="button.cancel"></fmt:message></a>
			</td>
		</tr>
	</table>
</div>

<br />
<hr />
<br />

<div class="field-name">
	<fmt:message key="label.wiki.pages"></fmt:message>
</div>

<img src="<lams:WebAppURL />/images/tree_closed.gif" id="wikiListImage" 
	onclick="javascript:toggleWikiList('<lams:WebAppURL />')" />
&nbsp;
<a href="javascript:changeWikiPage('${mainWikiPage.javaScriptTitle}')">${mainWikiPage.title}</a>
<div id="wikiList" style="display:none;">
	<c:forEach var="wikiPage" items="${wikiPages}">
		<c:if test="${wikiPage.title != mainWikiPage.title}">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="javascript:changeWikiPage('${wikiPage.javaScriptTitle}')">${wikiPage.title}</a>
			<br />
		</c:if> 
	</c:forEach>
</div>

<script type="text/javascript">
<!--
	var wikiLinkArray = new Array();
	populateWikiLinkArray();
	
	
	function populateWikiLinkArray()
	{
		<c:forEach var="wikiPage" items="${wikiPages}">
			wikiLinkArray[wikiLinkArray.length] = '${wikiPage.javaScriptTitle}';
		</c:forEach>
		
		
	}
	

	function doEditOrAdd(dispatch)
	{
		var title="";
		if(dispatch == "editPage")
		{
			title = document.getElementById("title").value;
		}
		else
		{
			title = document.getElementById("newPageTitle").value;
		}
		
		var i;
		
		if (title == null || trim(title).length == 0)
		{
			alert('<fmt:message key="label.wiki.add.title.required"></fmt:message>');
			return;
		}
		
		for (i=0; i<wikiLinkArray.length; i++)
		{
			if(dispatch == "editPage" && wikiLinkArray[i] == '${currentWikiPage.javaScriptTitle}')
			{
				continue;
			}
			
			if (trim(title) == wikiLinkArray[i])
			{
				alert('<fmt:message key="label.wiki.add.title.exists"><fmt:param>' + title + '</fmt:param></fmt:message>');
				return;
			}
		}
		
		// if all validation fulfilled, we can continue
		document.getElementById("title").value = trim(title);
		submitWiki(dispatch);
	}
	
	function submitWiki(dispatch)
	{
		document.getElementById("dispatch").value=dispatch;
		document.getElementById("authoringForm").submit();
	}
	
	function FCKeditor_OnComplete(editorInstance) 
	{ 	
		editorInstance.wikiLinkArray = wikiLinkArray;
	}
	
-->
</script>

