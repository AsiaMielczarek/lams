/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.tool.vote;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;



/**
 * <p> DTO that holds authoring properties for authoring jsps
 * </p>
 * 
 * @author Ozgur Demirtas
 */
public class VoteGeneralAuthoringDTO implements Comparable
{
    protected String activeModule;
    protected String defineLaterInEditMode;
    protected String submissionAttempt;
    protected String sbmtSuccess;
    protected String exceptionMaxNominationInvalid;
    protected String toolContentID;
    protected String defaultContentId;
    protected String defaultContentIdStr;
    protected String isDefineLater;
    protected String activityTitle;
    protected String activityInstructions;
    protected int 	maxOptionIndex;
    protected String defaultOptionContent;
    protected String richTextOfflineInstructions;
    protected String richTextOnlineInstructions;
    
    protected List 	listUploadedOfflineFileNames;
    protected List 	listUploadedOnlineFileNames;
    protected List 	listOfflineFilesMetadata;
    protected List 	listOnlineFilesMetadata;
    
    protected String onlineInstructions;
	protected String offlineInstructions;
	
	protected String allowText;
	protected String showResults;
	protected String lockOnFinish;
	protected String maxNominationCount;
	
	protected String editActivityEditMode;
	protected Map mapOptionsContent;
	protected String userExceptionMaxNominationInvalid;
	protected String userExceptionNoToolSessions;
	protected String userExceptionFilenameEmpty;
	protected String isMonitoredContentInUse;
	protected String validationError;
	protected String userExceptionOptionsDuplicate;
	protected String httpSessionID;

	protected String contentFolderID;
    protected String editableQuestionText;
    protected String editableQuestionFeedback;
    
    protected List attachmentList;
    protected List deletedAttachmentList;
    protected String reflect;
    protected String reflectionSubject;
    
    protected Map mapNominationContent;
    protected String editableNominationText;
    protected String editableNominationFeedback;
    protected String isToolSessionChanged;
    protected String responseId;
    protected String currentUid;
	
    
    /**
     * @return Returns the httpSessionID.
     */
    public String getHttpSessionID() {
        return httpSessionID;
    }
    /**
     * @param httpSessionID The httpSessionID to set.
     */
    public void setHttpSessionID(String httpSessionID) {
        this.httpSessionID = httpSessionID;
    }
    /**
     * @return Returns the maxNominationCount.
     */
    public String getMaxNominationCount() {
        return maxNominationCount;
    }
    /**
     * @param maxNominationCount The maxNominationCount to set.
     */
    public void setMaxNominationCount(String maxNominationCount) {
        this.maxNominationCount = maxNominationCount;
    }
    /**
     * @return Returns the activeModule.
     */
    public String getActiveModule() {
        return activeModule;
    }
    /**
     * @param listOfflineFilesMetadata The listOfflineFilesMetadata to set.
     */
    public void setListOfflineFilesMetadata(List listOfflineFilesMetadata) {
        this.listOfflineFilesMetadata = listOfflineFilesMetadata;
    }
    /**
     * @param listOnlineFilesMetadata The listOnlineFilesMetadata to set.
     */
    public void setListOnlineFilesMetadata(List listOnlineFilesMetadata) {
        this.listOnlineFilesMetadata = listOnlineFilesMetadata;
    }
    /**
     * @param listUploadedOnlineFileNames The listUploadedOnlineFileNames to set.
     */
    public void setListUploadedOnlineFileNames(List listUploadedOnlineFileNames) {
        this.listUploadedOnlineFileNames = listUploadedOnlineFileNames;
    }
    
    
    /**
     * @return Returns the isMonitoredContentInUse.
     */
    public String getIsMonitoredContentInUse() {
        return isMonitoredContentInUse;
    }
    /**
     * @param isMonitoredContentInUse The isMonitoredContentInUse to set.
     */
    public void setIsMonitoredContentInUse(String isMonitoredContentInUse) {
        this.isMonitoredContentInUse = isMonitoredContentInUse;
    }
    /**
     * @param activeModule The activeModule to set.
     */
    public void setActiveModule(String activeModule) {
        this.activeModule = activeModule;
    }
    /**
     * @return Returns the activityInstructions.
     */
    public String getActivityInstructions() {
        return activityInstructions;
    }
    /**
     * @param activityInstructions The activityInstructions to set.
     */
    public void setActivityInstructions(String activityInstructions) {
        this.activityInstructions = activityInstructions;
    }
    /**
     * @return Returns the activityTitle.
     */
    public String getActivityTitle() {
        return activityTitle;
    }
    /**
     * @param activityTitle The activityTitle to set.
     */
    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }
    /**
     * @return Returns the defaultContentId.
     */
    public String getDefaultContentId() {
        return defaultContentId;
    }
    /**
     * @param defaultContentId The defaultContentId to set.
     */
    public void setDefaultContentId(String defaultContentId) {
        this.defaultContentId = defaultContentId;
    }
    /**
     * @return Returns the defaultContentIdStr.
     */
    public String getDefaultContentIdStr() {
        return defaultContentIdStr;
    }
    /**
     * @param defaultContentIdStr The defaultContentIdStr to set.
     */
    public void setDefaultContentIdStr(String defaultContentIdStr) {
        this.defaultContentIdStr = defaultContentIdStr;
    }
    /**
     * @return Returns the defaultOptionContent.
     */
    public String getDefaultOptionContent() {
        return defaultOptionContent;
    }
    /**
     * @param defaultOptionContent The defaultOptionContent to set.
     */
    public void setDefaultOptionContent(String defaultOptionContent) {
        this.defaultOptionContent = defaultOptionContent;
    }
    /**
     * @return Returns the defineLaterInEditMode.
     */
    public String getDefineLaterInEditMode() {
        return defineLaterInEditMode;
    }
    /**
     * @param defineLaterInEditMode The defineLaterInEditMode to set.
     */
    public void setDefineLaterInEditMode(String defineLaterInEditMode) {
        this.defineLaterInEditMode = defineLaterInEditMode;
    }
    /**
     * @return Returns the exceptionMaxNominationInvalid.
     */
    public String getExceptionMaxNominationInvalid() {
        return exceptionMaxNominationInvalid;
    }
    /**
     * @param exceptionMaxNominationInvalid The exceptionMaxNominationInvalid to set.
     */
    public void setExceptionMaxNominationInvalid(
            String exceptionMaxNominationInvalid) {
        this.exceptionMaxNominationInvalid = exceptionMaxNominationInvalid;
    }
    /**
     * @return Returns the isDefineLater.
     */
    public String getIsDefineLater() {
        return isDefineLater;
    }
    /**
     * @param isDefineLater The isDefineLater to set.
     */
    public void setIsDefineLater(String isDefineLater) {
        this.isDefineLater = isDefineLater;
    }
    
    /**
     * @return Returns the listUploadedOfflineFileNames.
     */
    public List getListUploadedOfflineFileNames() {
        return listUploadedOfflineFileNames;
    }
    /**
     * @param listUploadedOfflineFileNames The listUploadedOfflineFileNames to set.
     */
    public void setListUploadedOfflineFileNames(
            List listUploadedOfflineFileNames) {
        this.listUploadedOfflineFileNames = listUploadedOfflineFileNames;
    }
    /**
     * @return Returns the maxOptionIndex.
     */
    public int getMaxOptionIndex() {
        return maxOptionIndex;
    }
    /**
     * @param maxOptionIndex The maxOptionIndex to set.
     */
    public void setMaxOptionIndex(int maxOptionIndex) {
        this.maxOptionIndex = maxOptionIndex;
    }
    /**
     * @return Returns the richTextOfflineInstructions.
     */
    public String getRichTextOfflineInstructions() {
        return richTextOfflineInstructions;
    }
    /**
     * @param richTextOfflineInstructions The richTextOfflineInstructions to set.
     */
    public void setRichTextOfflineInstructions(
            String richTextOfflineInstructions) {
        this.richTextOfflineInstructions = richTextOfflineInstructions;
    }
    /**
     * @return Returns the richTextOnlineInstructions.
     */
    public String getRichTextOnlineInstructions() {
        return richTextOnlineInstructions;
    }
    /**
     * @param richTextOnlineInstructions The richTextOnlineInstructions to set.
     */
    public void setRichTextOnlineInstructions(String richTextOnlineInstructions) {
        this.richTextOnlineInstructions = richTextOnlineInstructions;
    }
    /**
     * @return Returns the sbmtSuccess.
     */
    public String getSbmtSuccess() {
        return sbmtSuccess;
    }
    /**
     * @param sbmtSuccess The sbmtSuccess to set.
     */
    public void setSbmtSuccess(String sbmtSuccess) {
        this.sbmtSuccess = sbmtSuccess;
    }
    /**
     * @return Returns the submissionAttempt.
     */
    public String getSubmissionAttempt() {
        return submissionAttempt;
    }
    /**
     * @param submissionAttempt The submissionAttempt to set.
     */
    public void setSubmissionAttempt(String submissionAttempt) {
        this.submissionAttempt = submissionAttempt;
    }
    /**
     * @return Returns the toolContentID.
     */
    public String getToolContentID() {
        return toolContentID;
    }
    /**
     * @param toolContentID The toolContentID to set.
     */
    public void setToolContentID(String toolContentID) {
        this.toolContentID = toolContentID;
    }
    
    /**
     * @return Returns the offlineInstructions.
     */
    public String getOfflineInstructions() {
        return offlineInstructions;
    }
    /**
     * @param offlineInstructions The offlineInstructions to set.
     */
    public void setOfflineInstructions(String offlineInstructions) {
        this.offlineInstructions = offlineInstructions;
    }
    /**
     * @return Returns the onlineInstructions.
     */
    public String getOnlineInstructions() {
        return onlineInstructions;
    }
    /**
     * @param onlineInstructions The onlineInstructions to set.
     */
    public void setOnlineInstructions(String onlineInstructions) {
        this.onlineInstructions = onlineInstructions;
    }
    /**
     * @return Returns the listOfflineFilesMetadata.
     */
    public List getListOfflineFilesMetadata() {
        return listOfflineFilesMetadata;
    }
    /**
     * @return Returns the listOnlineFilesMetadata.
     */
    public List getListOnlineFilesMetadata() {
        return listOnlineFilesMetadata;
    }
    /**
     * @return Returns the listUploadedOnlineFileNames.
     */
    public List getListUploadedOnlineFileNames() {
        return listUploadedOnlineFileNames;
    }

    
    /**
     * @return Returns the allowText.
     */
    public String getAllowText() {
        return allowText;
    }
    /**
     * @param allowText The allowText to set.
     */
    public void setAllowText(String allowText) {
        this.allowText = allowText;
    }
    /**
     * @return Returns the lockOnFinish.
     */
    public String getLockOnFinish() {
        return lockOnFinish;
    }
    /**
     * @param lockOnFinish The lockOnFinish to set.
     */
    public void setLockOnFinish(String lockOnFinish) {
        this.lockOnFinish = lockOnFinish;
    }

    /**
     * @return Returns the mapOptionsContent.
     */
    public Map getMapOptionsContent() {
        return mapOptionsContent;
    }
    /**
     * @param mapOptionsContent The mapOptionsContent to set.
     */
    public void setMapOptionsContent(Map mapOptionsContent) {
        this.mapOptionsContent = mapOptionsContent;
    }
    
    /**
     * @return Returns the userExceptionMaxNominationInvalid.
     */
    public String getUserExceptionMaxNominationInvalid() {
        return userExceptionMaxNominationInvalid;
    }
    /**
     * @param userExceptionMaxNominationInvalid The userExceptionMaxNominationInvalid to set.
     */
    public void setUserExceptionMaxNominationInvalid(
            String userExceptionMaxNominationInvalid) {
        this.userExceptionMaxNominationInvalid = userExceptionMaxNominationInvalid;
    }
    /**
     * @return Returns the userExceptionOptionsDuplicate.
     */
    public String getUserExceptionOptionsDuplicate() {
        return userExceptionOptionsDuplicate;
    }
    /**
     * @param userExceptionOptionsDuplicate The userExceptionOptionsDuplicate to set.
     */
    public void setUserExceptionOptionsDuplicate(
            String userExceptionOptionsDuplicate) {
        this.userExceptionOptionsDuplicate = userExceptionOptionsDuplicate;
    }
    /**
     * @return Returns the validationError.
     */
    public String getValidationError() {
        return validationError;
    }
    /**
     * @param validationError The validationError to set.
     */
    public void setValidationError(String validationError) {
        this.validationError = validationError;
    }
    
    /**
     * @return Returns the editActivityEditMode.
     */
    public String getEditActivityEditMode() {
        return editActivityEditMode;
    }
    
    /**
     * @param editActivityEditMode The editActivityEditMode to set.
     */
    public void setEditActivityEditMode(String editActivityEditMode) {
        this.editActivityEditMode = editActivityEditMode;
    }
    /**
     * @return Returns the userExceptionNoToolSessions.
     */
    public String getUserExceptionNoToolSessions() {
        return userExceptionNoToolSessions;
    }
    /**
     * @param userExceptionNoToolSessions The userExceptionNoToolSessions to set.
     */
    public void setUserExceptionNoToolSessions(
            String userExceptionNoToolSessions) {
        this.userExceptionNoToolSessions = userExceptionNoToolSessions;
    }

    /**
     * @return Returns the userExceptionFilenameEmpty.
     */
    public String getUserExceptionFilenameEmpty() {
        return userExceptionFilenameEmpty;
    }
    /**
     * @param userExceptionFilenameEmpty The userExceptionFilenameEmpty to set.
     */
    public void setUserExceptionFilenameEmpty(String userExceptionFilenameEmpty) {
        this.userExceptionFilenameEmpty = userExceptionFilenameEmpty;
    }    
	public int compareTo(Object o)
    {
	    VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = (VoteGeneralAuthoringDTO) o;
     
        if (voteGeneralAuthoringDTO == null)
        	return 1;
		else
			return 0;
    }

	public String toString() {
        return new ToStringBuilder(this)
            .append("activeModule: ", activeModule)
            .append("contentFolderID: ", contentFolderID)
            .append("editableQuestionText: ", editableQuestionText)            
            .append("defineLaterInEditMode: ", defineLaterInEditMode)
            .append("reflectionSubject: ", reflectionSubject)            
            .append("submissionAttempt: ", submissionAttempt)
            .append("mapNominationContent: ", mapNominationContent)            
            .append("sbmtSuccess: ", sbmtSuccess)
            .append("exceptionMaxNominationInvalid: ", exceptionMaxNominationInvalid)
            .append("toolContentID: ", toolContentID)
            .append("defaultContentId: ", defaultContentId)
            .append("defaultContentIdStr: ", defaultContentIdStr)
            .append("reflect: ", reflect)            
            .append("isDefineLater: ", isDefineLater)
            .append("allowText: ", allowText)
            .append("showResults: ", showResults)
            .append("lockOnFinish: ", lockOnFinish)
            .append("maxNominationCount: ", maxNominationCount)
            .append("activityTitle: ", activityTitle)
            .append("activityInstructions: ", activityInstructions)
            .append("maxOptionIndex: ", maxOptionIndex)
            .append("defaultOptionContent: ", defaultOptionContent)
            .append("richTextOfflineInstructions: ", richTextOfflineInstructions)
            .append("richTextOnlineInstructions: ", richTextOnlineInstructions)
            .append("listUploadedOfflineFileNames: ", listUploadedOfflineFileNames)            
            .append("listUploadedOnlineFileNames: ", listUploadedOnlineFileNames)
            .append("listOfflineFilesMetadata: ", listOfflineFilesMetadata)
            .append("listOnlineFilesMetadata: ", listOnlineFilesMetadata)
            .append("mapOptionsContent: ", mapOptionsContent)
            .append("onlineInstructions: ", onlineInstructions)
            .append("offlineInstructions: ", offlineInstructions)
			.append("userExceptionMaxNominationInvalid: ", userExceptionMaxNominationInvalid)
			.append("userExceptionFilenameEmpty: ", userExceptionFilenameEmpty)
			.append("isMonitoredContentInUse: ", isMonitoredContentInUse)
			.append("validationError: ", validationError)
			.append("userExceptionOptionsDuplicate: ", userExceptionOptionsDuplicate)
			.append("userExceptionNoToolSessions: ", userExceptionNoToolSessions)
			.append("httpSessionID: ", httpSessionID)
			.append("editActivityEditMode: ", editActivityEditMode)
            .toString();
    }
	
    /**
     * @return Returns the contentFolderID.
     */
    public String getContentFolderID() {
        return contentFolderID;
    }
    /**
     * @param contentFolderID The contentFolderID to set.
     */
    public void setContentFolderID(String contentFolderID) {
        this.contentFolderID = contentFolderID;
    }
    /**
     * @return Returns the editableQuestionFeedback.
     */
    public String getEditableQuestionFeedback() {
        return editableQuestionFeedback;
    }
    /**
     * @param editableQuestionFeedback The editableQuestionFeedback to set.
     */
    public void setEditableQuestionFeedback(String editableQuestionFeedback) {
        this.editableQuestionFeedback = editableQuestionFeedback;
    }
    /**
     * @return Returns the editableQuestionText.
     */
    public String getEditableQuestionText() {
        return editableQuestionText;
    }
    /**
     * @param editableQuestionText The editableQuestionText to set.
     */
    public void setEditableQuestionText(String editableQuestionText) {
        this.editableQuestionText = editableQuestionText;
    }
    
    /**
     * @return Returns the attachmentList.
     */
    public List getAttachmentList() {
        return attachmentList;
    }
    /**
     * @param attachmentList The attachmentList to set.
     */
    public void setAttachmentList(List attachmentList) {
        this.attachmentList = attachmentList;
    }
    /**
     * @return Returns the deletedAttachmentList.
     */
    public List getDeletedAttachmentList() {
        return deletedAttachmentList;
    }
    /**
     * @param deletedAttachmentList The deletedAttachmentList to set.
     */
    public void setDeletedAttachmentList(List deletedAttachmentList) {
        this.deletedAttachmentList = deletedAttachmentList;
    }
    /**
     * @return Returns the reflect.
     */
    public String getReflect() {
        return reflect;
    }
    /**
     * @param reflect The reflect to set.
     */
    public void setReflect(String reflect) {
        this.reflect = reflect;
    }
    /**
     * @return Returns the reflectionSubject.
     */
    public String getReflectionSubject() {
        return reflectionSubject;
    }
    /**
     * @param reflectionSubject The reflectionSubject to set.
     */
    public void setReflectionSubject(String reflectionSubject) {
        this.reflectionSubject = reflectionSubject;
    }
    /**
     * @return Returns the editableNominationFeedback.
     */
    public String getEditableNominationFeedback() {
        return editableNominationFeedback;
    }
    /**
     * @param editableNominationFeedback The editableNominationFeedback to set.
     */
    public void setEditableNominationFeedback(String editableNominationFeedback) {
        this.editableNominationFeedback = editableNominationFeedback;
    }
    /**
     * @return Returns the editableNominationText.
     */
    public String getEditableNominationText() {
        return editableNominationText;
    }
    /**
     * @param editableNominationText The editableNominationText to set.
     */
    public void setEditableNominationText(String editableNominationText) {
        this.editableNominationText = editableNominationText;
    }
    /**
     * @return Returns the mapNominationContent.
     */
    public Map getMapNominationContent() {
        return mapNominationContent;
    }
    /**
     * @param mapNominationContent The mapNominationContent to set.
     */
    public void setMapNominationContent(Map mapNominationContent) {
        this.mapNominationContent = mapNominationContent;
    }
    /**
     * @return Returns the isToolSessionChanged.
     */
    public String getIsToolSessionChanged() {
        return isToolSessionChanged;
    }
    /**
     * @param isToolSessionChanged The isToolSessionChanged to set.
     */
    public void setIsToolSessionChanged(String isToolSessionChanged) {
        this.isToolSessionChanged = isToolSessionChanged;
    }
    /**
     * @return Returns the currentUid.
     */
    public String getCurrentUid() {
        return currentUid;
    }
    /**
     * @param currentUid The currentUid to set.
     */
    public void setCurrentUid(String currentUid) {
        this.currentUid = currentUid;
    }
    /**
     * @return Returns the responseId.
     */
    public String getResponseId() {
        return responseId;
    }
    /**
     * @param responseId The responseId to set.
     */
    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }
	public String getShowResults() {
		return showResults;
	}
	public void setShowResults(String showResults) {
		this.showResults = showResults;
	}
}
