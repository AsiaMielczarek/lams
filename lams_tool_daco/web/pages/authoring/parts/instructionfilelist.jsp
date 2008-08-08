<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.lamsfoundation.lams.contentrepository.client.IToolContentHandler"%>
<c:set var="CONS_OFFLINE" value="<%=IToolContentHandler.TYPE_OFFLINE%>" />
<c:choose>
	<c:when test="${fileTypeFlag==CONS_OFFLINE}">
		<c:set var="targetFileType"
			value="<%=IToolContentHandler.TYPE_OFFLINE%>" />
	</c:when>
	<c:otherwise>
		<c:set var="targetFileType"
			value="<%=IToolContentHandler.TYPE_ONLINE%>" />
	</c:otherwise>
</c:choose>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
<c:set var="fileList" value="${sessionMap.instructionAttachmentList}" />

<%-- check whehter has target file type --%>
<c:forEach var="file" items="${fileList}">
	<c:if test="${targetFileType == file.fileType}">
		<c:set var="hasFile" value="${true}" />
	</c:if>
</c:forEach>

<%-- Display target file type --%>
<c:if test="${hasFile}">
	<ul>
		<c:forEach var="file" items="${fileList}">
			<c:if test="${targetFileType == file.fileType}">
				<li>
					<c:out value="${file.fileName}" />
					<c:set var="viewURL">
						<html:rewrite 
							page="/download/?uuid=${file.fileUuid}&versionID=${file.fileVersionId}&preferDownload=false" />
					</c:set>
					<html:link href="javascript:launchPopup('${viewURL}','instructionfile')">
						<fmt:message key="label.common.view"/>
						
					</html:link>
					<c:set var="downloadURL">
						<html:rewrite
							page="/download/?uuid=${file.fileUuid}&versionID=${file.fileVersionId}&preferDownload=true" />
					</c:set>
					<html:link href="${downloadURL}">
						<fmt:message key="label.authoring.basic.download" />
					</html:link>
					<c:choose>
						<c:when test="${fileTypeFlag==CONS_OFFLINE}">
							<html:link href="#"
								onclick="deleteOfflineFile(${file.fileUuid},${file.fileVersionId})">
								<fmt:message key="label.common.delete" />
							</html:link>
						</c:when>
						<c:otherwise>
							<html:link href="#"
								onclick="deleteOnlineFile(${file.fileUuid},${file.fileVersionId})">
								<fmt:message key="label.common.delete" />
							</html:link>
						</c:otherwise>
					</c:choose>
				</li>
			</c:if>
		</c:forEach>
	</ul>
</c:if>
