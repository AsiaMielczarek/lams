
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

<%@ include file="/common/taglibs.jsp"%>

<table>

	<tr>
		<td>
			<div class="field-name">
				<fmt:message key="label.onlineInstructions.col"></fmt:message>
			</div>
			<lams:STRUTS-textarea property="onlineInstructions" rows="3" cols="75" />
		</td>
	</tr>

	<tr>
		<td>
			<ul>
				<c:forEach var="attachment"
					items="${voteGeneralAuthoringDTO.attachmentList}">
					<c:if test="${attachment.fileOnline == true }">
						<li>
							<bean:define id="view">/download/?uuid=<bean:write
									name="attachment" property="uuid" />&preferDownload=false</bean:define>
							<bean:define id="download">/download/?uuid=<bean:write
									name="attachment" property="uuid" />&preferDownload=true</bean:define>
							<bean:define id="uuid" name="attachment" property="uuid" />

							<bean:write name="attachment" property="fileName" />

							<a
								href='javascript:launchInstructionsPopup("<html:rewrite page='<%=view%>'/>")'>
								<fmt:message key="label.view" /> </a> &nbsp
							<html:link page="<%=download%>">
								<fmt:message key="label.download" />
							</html:link>
							&nbsp
							<html:link
								page="/authoring.do?dispatch=deleteFile&httpSessionID=${voteGeneralAuthoringDTO.httpSessionID}&toolContentID=${voteGeneralAuthoringDTO.toolContentID}&contentFolderID=${voteGeneralAuthoringDTO.contentFolderID}&activeModule=${voteGeneralAuthoringDTO.activeModule}&defaultContentIdStr=${voteGeneralAuthoringDTO.defaultContentIdStr}&lockOnFinish=${voteGeneralAuthoringDTO.lockOnFinish}&allowText=${voteGeneralAuthoringDTO.allowText}&maxNominationCount=${voteGeneralAuthoringDTO.maxNominationCount}&minNominationCount=${voteGeneralAuthoringDTO.minNominationCount}&reflect=${voteGeneralAuthoringDTO.reflect}&reflectionSubject=${voteGeneralAuthoringDTO.reflectionSubject}&onlineInstructions=${voteGeneralAuthoringDTO.onlineInstructions}&offlineInstructions=${voteGeneralAuthoringDTO.offlineInstructions}"
								paramId="uuid" paramName="attachment" paramProperty="uuid"
								onclick="javascript:return confirm('Are you sure you want to delete this file?')"
								target="_self">
								<fmt:message key="button.delete" />
							</html:link>
						</li>
					</c:if>
				</c:forEach>
			</ul>
		</td>
	</tr>

	<tr>
		<td>
			<div class="field-name">
				<fmt:message key="label.onlineFiles" />
			</div>
			<html:file property="theOnlineFile"></html:file>
			<html:submit property="submitOnlineFile" styleClass="button"
				onclick="submitMethod('addNewFile');">
				<fmt:message key="button.upload" />
			</html:submit>
		</td>
	</tr>

	<tr>
		<td>
			<hr />
		</td>
	</tr>

	<tr>
		<td>
			<div class="field-name-alternative-color">
				<fmt:message key="label.offlineInstructions.col"></fmt:message>
			</div>
			<lams:STRUTS-textarea property="offlineInstructions" rows="3" cols="75" />
		</td>
	</tr>

	<tr>
		<td>
			<ul>
				<c:forEach var="attachment"
					items="${voteGeneralAuthoringDTO.attachmentList}">

					<c:if test="${attachment.fileOnline == false}">
						<li>
							<bean:define id="view">/download/?uuid=<bean:write
									name="attachment" property="uuid" />&preferDownload=false</bean:define>
							<bean:define id="download">/download/?uuid=<bean:write
									name="attachment" property="uuid" />&preferDownload=true</bean:define>
							<bean:define id="uuid" name="attachment" property="uuid" />
							<bean:write name="attachment" property="fileName" />
							<a
								href='javascript:launchInstructionsPopup("<html:rewrite page='<%=view%>'/>")'>
								<fmt:message key="label.view" /> </a> &nbsp
							<html:link page="<%=download%>">
								<fmt:message key="label.download" />
							</html:link>
							&nbsp
							<html:link
								page="/authoring.do?dispatch=deleteFile&httpSessionID=${voteGeneralAuthoringDTO.httpSessionID}&toolContentID=${voteGeneralAuthoringDTO.toolContentID}&contentFolderID=${voteGeneralAuthoringDTO.contentFolderID}&activeModule=${voteGeneralAuthoringDTO.activeModule}&defaultContentIdStr=${voteGeneralAuthoringDTO.defaultContentIdStr}&lockOnFinish=${voteGeneralAuthoringDTO.lockOnFinish}&allowText=${voteGeneralAuthoringDTO.allowText}&maxNominationCount=${voteGeneralAuthoringDTO.maxNominationCount}&minNominationCount=${voteGeneralAuthoringDTO.minNominationCount}&reflect=${voteGeneralAuthoringDTO.reflect}&reflectionSubject=${voteGeneralAuthoringDTO.reflectionSubject}&onlineInstructions=${voteGeneralAuthoringDTO.onlineInstructions}&offlineInstructions=${voteGeneralAuthoringDTO.offlineInstructions}"
								paramId="uuid" paramName="attachment" paramProperty="uuid"
								onclick="javascript:return confirm('Are you sure you want to delete this file?')"
								target="_self">
								<fmt:message key="button.delete" />
							</html:link>
						</li>
					</c:if>
				</c:forEach>
			</ul>
		</td>
	</tr>

	<tr>
		<td>
			<div class="field-name-alternative-color">
				<fmt:message key="label.offlineFiles" />
			</div>
			<html:file property="theOfflineFile"></html:file>
			<html:submit property="submitOfflineFile" styleClass="button"
				onclick="submitMethod('addNewFile');">
				<fmt:message key="button.upload" />
			</html:submit>
		</td>
	</tr>
</table>









