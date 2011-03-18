/****************************************************************
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
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.tool.qa.web.form;

/* ActionForm for the Authoring environment*/
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.tool.qa.QaAppConstants;
import org.lamsfoundation.lams.tool.qa.service.IQaService;

/**
 * @author Ozgur Demirtas
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class QaAuthoringForm extends QaLearningForm implements QaAppConstants {
    protected String addContent;
    protected String removeContent;
    protected String removeAllContent;
    protected String submitAllContent;
    protected String submitTabDone;
    protected String submitOfflineFile;
    protected String submitOnlineFile;
    protected String dispatch;
    protected String currentTab;

    protected String choice;
    protected String choiceBasic;
    protected String choiceAdvanced;
    protected String choiceInstructions;

    protected String activeModule;

    /* basic content */
    protected String title;
    protected String instructions;
    protected String questionIndex;
    protected String isRemoveContent;

    /* instructions content */
    protected String onlineInstructions;
    protected String offlineInstructions;
    protected FormFile theOfflineFile;
    protected FormFile theOnlineFile;

    protected String toolContentID;
    /* advanced content */
    protected String synchInMonitor;
    protected String reportTitle;
    protected String monitoringReportTitle;
    protected String endLearningMessage;
    protected String usernameVisible;
    protected String allowRateAnswers;
    protected String showOtherAnswers;
    protected String questionsSequenced;
    protected String lockWhenFinished;
    protected String reflect;
    protected String reflectionSubject;

    /* proxy controllers for Monitoring tabs */
    protected String summaryMonitoring;
    protected String instructionsMonitoring;
    protected String editActivityMonitoring;
    protected String statsMonitoring;

    protected String edit;
    private String contentFolderID;
    private String addSingleQuestion;
    private String editableQuestionIndex;
    private String feedback;
    private boolean required;
    private String editQuestionBoxRequest;

    protected String defineLaterInEditMode;
    protected String defaultContentIdStr;
    protected IQaService qaService;
    
    protected boolean allowRichEditor;

    public void resetUserAction() {
	this.addContent = null;
	this.removeContent = null;
	this.removeAllContent = null;
	this.submitAllContent = null;
	this.submitTabDone = null;
	this.submitOfflineFile = null;
	this.submitOnlineFile = null;

	this.summaryMonitoring = null;
	this.instructionsMonitoring = null;
	this.editActivityMonitoring = null;
	this.statsMonitoring = null;
	this.edit = null;
	this.allowRichEditor = false;
    }

    public void reset() {
	this.addContent = null;
	this.removeContent = null;
	this.removeAllContent = null;
	this.submitAllContent = null;
	this.submitTabDone = null;
	this.submitOfflineFile = null;
	this.submitOnlineFile = null;

	this.choice = null;
	this.choiceBasic = null;
	this.choiceAdvanced = null;
	this.choiceInstructions = null;

	this.title = null;
	this.instructions = null;
	this.questionIndex = null;
	this.isRemoveContent = null;

	this.onlineInstructions = null;
	this.offlineInstructions = null;

	this.endLearningMessage = null;
	this.synchInMonitor = null;
	this.reportTitle = null;
	this.monitoringReportTitle = null;
	this.questionsSequenced = null;
	this.lockWhenFinished = null;
	this.reflect = null;
	this.allowRichEditor = false;

	this.summaryMonitoring = null;
	this.instructionsMonitoring = null;
	this.editActivityMonitoring = null;
	this.statsMonitoring = null;
	this.edit = null;
	this.toolContentID = null;
	this.currentTab = null;
    }

    public void resetRadioBoxes() {
	this.synchInMonitor = OPTION_OFF;
	this.usernameVisible = OPTION_OFF;
	this.allowRateAnswers = OPTION_OFF;
	this.questionsSequenced = OPTION_OFF;
	this.lockWhenFinished = OPTION_OFF;
	this.reflect = OPTION_OFF;
	this.allowRichEditor = false;
	this.required = false;
    }

    public String toString() {
	return new ToStringBuilder(this).append("Listing current QaAuthoringForm properties: ")
		.append("toolContentID: ", toolContentID).append("currentTab: ", currentTab)
		.append("activeModule: ", activeModule).append("defaultContentIdStr: ", defaultContentIdStr)
		.append("title: ", title).append("instructions: ", instructions).append("reportTitle: ", reportTitle)
		.append("monitoringReportTitle: ", monitoringReportTitle)
		.append("endLearningMessage: ", endLearningMessage).append("onlineInstructions: ", onlineInstructions)
		.append("offlineInstructions: ", offlineInstructions).append("usernameVisible: ", usernameVisible)
		.append("allowRateAnswers: ", allowRateAnswers).append("showOtherAnswers: ", showOtherAnswers)
		.append("synchInMonitor: ", synchInMonitor).append("questionsSequenced: ", questionsSequenced)
		.append("lockWhenFinished: ", lockWhenFinished).append("reflect: ", reflect)
		.append("defineLaterInEditMode: ", defineLaterInEditMode).toString();
    }

    /**
     * @return Returns the isRemoveContent.
     */
    public String getIsRemoveContent() {
	return isRemoveContent;
    }

    /**
     * @param isRemoveContent
     *                The isRemoveContent to set.
     */
    public void setIsRemoveContent(String isRemoveContent) {
	this.isRemoveContent = isRemoveContent;
    }

    /**
     * @return Returns the questionIndex.
     */
    public String getQuestionIndex() {
	return questionIndex;
    }

    /**
     * @param questionIndex
     *                The questionIndex to set.
     */
    public void setQuestionIndex(String questionIndex) {
	this.questionIndex = questionIndex;
    }

    /**
     * @return Returns the addContent.
     */
    public String getAddContent() {
	return addContent;
    }

    /**
     * @param addContent
     *                The addContent to set.
     */
    public void setAddContent(String addContent) {
	this.addContent = addContent;
    }

    /**
     * @return Returns the removeContent.
     */
    public String getRemoveContent() {
	return removeContent;
    }

    /**
     * @param removeContent
     *                The removeContent to set.
     */
    public void setRemoveContent(String removeContent) {
	this.removeContent = removeContent;
    }

    /**
     * @return Returns the removeAllContent.
     */
    public String getRemoveAllContent() {
	return removeAllContent;
    }

    /**
     * @param removeAllContent
     *                The removeAllContent to set.
     */
    public void setRemoveAllContent(String removeAllContent) {
	this.removeAllContent = removeAllContent;
    }

    /**
     * @return Returns the submitAllContent.
     */
    public String getSubmitAllContent() {
	return submitAllContent;
    }

    /**
     * @param submitAllContent
     *                The submitAllContent to set.
     */
    public void setSubmitAllContent(String submitAllContent) {
	this.submitAllContent = submitAllContent;
    }

    /**
     * @return Returns the instructions.
     */
    public String getInstructions() {
	return instructions;
    }

    /**
     * @param instructions
     *                The instructions to set.
     */
    public void setInstructions(String instructions) {
	this.instructions = instructions;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title
     *                The title to set.
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * @return Returns the offlineInstructions.
     */
    public String getOfflineInstructions() {
	return offlineInstructions;
    }

    /**
     * @param offlineInstructions
     *                The offlineInstructions to set.
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
     * @param onlineInstructions
     *                The onlineInstructions to set.
     */
    public void setOnlineInstructions(String onlineInstructions) {
	this.onlineInstructions = onlineInstructions;
    }

    /**
     * @return Returns the syncInMonitor.
     */
    public String getSynchInMonitor() {
	return synchInMonitor;
    }

    /**
     * @param syncInMonitor
     *                The syncInMonitor to set.
     */
    public void setSynchInMonitor(String synchInMonitor) {
	this.synchInMonitor = synchInMonitor;
    }

    /**
     * @return Returns the choiceAdvanced.
     */
    public String getChoiceAdvanced() {
	return choiceAdvanced;
    }

    /**
     * @param choiceAdvanced
     *                The choiceAdvanced to set.
     */
    public void setChoiceAdvanced(String choiceAdvanced) {
	this.choiceAdvanced = choiceAdvanced;
    }

    /**
     * @return Returns the choiceBasic.
     */
    public String getChoiceBasic() {
	return choiceBasic;
    }

    /**
     * @param choiceBasic
     *                The choiceBasic to set.
     */
    public void setChoiceBasic(String choiceBasic) {
	this.choiceBasic = choiceBasic;
    }

    /**
     * @return Returns the choiceInstructions.
     */
    public String getChoiceInstructions() {
	return choiceInstructions;
    }

    /**
     * @param choiceInstructions
     *                The choiceInstructions to set.
     */
    public void setChoiceInstructions(String choiceInstructions) {
	this.choiceInstructions = choiceInstructions;
    }

    /**
     * @return Returns the choice.
     */
    public String getChoice() {
	return choice;
    }

    /**
     * @param choice
     *                The choice to set.
     */
    public void setChoice(String choice) {
	this.choice = choice;
    }

    /**
     * @return Returns the reportTitle.
     */
    public String getReportTitle() {
	return reportTitle;
    }

    /**
     * @param reportTitle
     *                The reportTitle to set.
     */
    public void setReportTitle(String reportTitle) {
	this.reportTitle = reportTitle;
    }

    /**
     * @return Returns the usernameVisible.
     */
    public String getUsernameVisible() {
	return usernameVisible;
    }

    /**
     * @param usernameVisible
     *                The usernameVisible to set.
     */
    public void setUsernameVisible(String usernameVisible) {
	this.usernameVisible = usernameVisible;
    }
    
    /**
     * @return Returns the allowRateAnswers.
     */
    public String getAllowRateAnswers() {
	return allowRateAnswers;
    }

    /**
     * @param allowRateAnswers
     *                The allowRateAnswers to set.
     */
    public void setAllowRateAnswers(String allowRateAnswers) {
	this.allowRateAnswers = allowRateAnswers;
    }

    /**
     * @return Returns the submitTabDone.
     */
    public String getSubmitTabDone() {
	return submitTabDone;
    }

    /**
     * @param submitTabDone
     *                The submitTabDone to set.
     */
    public void setSubmitTabDone(String submitTabDone) {
	this.submitTabDone = submitTabDone;
    }

    /**
     * @return Returns the questionsSequenced.
     */
    public String getQuestionsSequenced() {
	return questionsSequenced;
    }

    /**
     * @param questionsSequenced
     *                The questionsSequenced to set.
     */
    public void setQuestionsSequenced(String questionsSequenced) {
	this.questionsSequenced = questionsSequenced;
    }

    /**
     * @return Returns the endLearningMessage.
     */
    public String getEndLearningMessage() {
	return endLearningMessage;
    }

    /**
     * @param endLearningMessage
     *                The endLearningMessage to set.
     */
    public void setEndLearningMessage(String endLearningMessage) {
	this.endLearningMessage = endLearningMessage;
    }

    /**
     * @return Returns the monitoringReportTitle.
     */
    public String getMonitoringReportTitle() {
	return monitoringReportTitle;
    }

    /**
     * @param monitoringReportTitle
     *                The monitoringReportTitle to set.
     */
    public void setMonitoringReportTitle(String monitoringReportTitle) {
	this.monitoringReportTitle = monitoringReportTitle;
    }

    /**
     * @return Returns the editActivityMonitoring.
     */
    public String getEditActivityMonitoring() {
	return editActivityMonitoring;
    }

    /**
     * @param editActivityMonitoring
     *                The editActivityMonitoring to set.
     */
    public void setEditActivityMonitoring(String editActivityMonitoring) {
	this.editActivityMonitoring = editActivityMonitoring;
    }

    /**
     * @return Returns the instructionsMonitoring.
     */
    public String getInstructionsMonitoring() {
	return instructionsMonitoring;
    }

    /**
     * @param instructionsMonitoring
     *                The instructionsMonitoring to set.
     */
    public void setInstructionsMonitoring(String instructionsMonitoring) {
	this.instructionsMonitoring = instructionsMonitoring;
    }

    /**
     * @return Returns the statsMonitoring.
     */
    public String getStatsMonitoring() {
	return statsMonitoring;
    }

    /**
     * @param statsMonitoring
     *                The statsMonitoring to set.
     */
    public void setStatsMonitoring(String statsMonitoring) {
	this.statsMonitoring = statsMonitoring;
    }

    /**
     * @return Returns the summaryMonitoring.
     */
    public String getSummaryMonitoring() {
	return summaryMonitoring;
    }

    /**
     * @param summaryMonitoring
     *                The summaryMonitoring to set.
     */
    public void setSummaryMonitoring(String summaryMonitoring) {
	this.summaryMonitoring = summaryMonitoring;
    }

    /**
     * @return Returns the edit.
     */
    public String getEdit() {
	return edit;
    }

    /**
     * @param edit
     *                The edit to set.
     */
    public void setEdit(String edit) {
	this.edit = edit;
    }

    /**
     * @return Returns the submitOfflineFile.
     */
    public String getSubmitOfflineFile() {
	return submitOfflineFile;
    }

    /**
     * @param submitOfflineFile
     *                The submitOfflineFile to set.
     */
    public void setSubmitOfflineFile(String submitOfflineFile) {
	this.submitOfflineFile = submitOfflineFile;
    }

    /**
     * @param theOfflineFile
     *                The theOfflineFile to set.
     */
    public void setTheOfflineFile(FormFile theOfflineFile) {
	this.theOfflineFile = theOfflineFile;
    }

    /**
     * @param theOnlineFile
     *                The theOnlineFile to set.
     */
    public void setTheOnlineFile(FormFile theOnlineFile) {
	this.theOnlineFile = theOnlineFile;
    }

    /**
     * @return Returns the theOfflineFile.
     */
    public FormFile getTheOfflineFile() {
	return theOfflineFile;
    }

    /**
     * @return Returns the theOnlineFile.
     */
    public FormFile getTheOnlineFile() {
	return theOnlineFile;
    }

    /**
     * @return Returns the submitOnlineFile.
     */
    public String getSubmitOnlineFile() {
	return submitOnlineFile;
    }

    /**
     * @param submitOnlineFile
     *                The submitOnlineFile to set.
     */
    public void setSubmitOnlineFile(String submitOnlineFile) {
	this.submitOnlineFile = submitOnlineFile;
    }

    /**
     * @return Returns the dispatch.
     */
    public String getDispatch() {
	return dispatch;
    }

    /**
     * @param dispatch
     *                The dispatch to set.
     */
    public void setDispatch(String dispatch) {
	this.dispatch = dispatch;
    }

    /**
     * @return Returns the toolContentID.
     */
    public String getToolContentID() {
	return toolContentID;
    }

    /**
     * @param toolContentID
     *                The toolContentID to set.
     */
    public void setToolContentID(String toolContentID) {
	this.toolContentID = toolContentID;
    }

    /**
     * @return Returns the currentTab.
     */
    public String getCurrentTab() {
	return currentTab;
    }

    /**
     * @param currentTab
     *                The currentTab to set.
     */
    public void setCurrentTab(String currentTab) {
	this.currentTab = currentTab;
    }

    /**
     * @return Returns the activeModule.
     */
    public String getActiveModule() {
	return activeModule;
    }

    /**
     * @param activeModule
     *                The activeModule to set.
     */
    public void setActiveModule(String activeModule) {
	this.activeModule = activeModule;
    }

    /**
     * @return Returns the defaultContentIdStr.
     */
    public String getDefaultContentIdStr() {
	return defaultContentIdStr;
    }

    /**
     * @param defaultContentIdStr
     *                The defaultContentIdStr to set.
     */
    public void setDefaultContentIdStr(String defaultContentIdStr) {
	this.defaultContentIdStr = defaultContentIdStr;
    }

    /**
     * @return Returns the qaService.
     */
    public IQaService getQaService() {
	return qaService;
    }

    /**
     * @param qaService
     *                The qaService to set.
     */
    public void setQaService(IQaService qaService) {
	this.qaService = qaService;
    }

    /**
     * @return Returns the defineLaterInEditMode.
     */
    public String getDefineLaterInEditMode() {
	return defineLaterInEditMode;
    }

    /**
     * @param defineLaterInEditMode
     *                The defineLaterInEditMode to set.
     */
    public void setDefineLaterInEditMode(String defineLaterInEditMode) {
	this.defineLaterInEditMode = defineLaterInEditMode;
    }

    /**
     * @return Returns the reflect.
     */
    public String getReflect() {
	return reflect;
    }

    /**
     * @param reflect
     *                The reflect to set.
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
     * @param reflectionSubject
     *                The reflectionSubject to set.
     */
    public void setReflectionSubject(String reflectionSubject) {
	this.reflectionSubject = reflectionSubject;
    }

    /**
     * @return Returns the contentFolderID.
     */
    public String getContentFolderID() {
	return contentFolderID;
    }

    /**
     * @param contentFolderID
     *                The contentFolderID to set.
     */
    public void setContentFolderID(String contentFolderID) {
	this.contentFolderID = contentFolderID;
    }

    /**
     * @return Returns the addSingleQuestion.
     */
    public String getAddSingleQuestion() {
	return addSingleQuestion;
    }

    /**
     * @param addSingleQuestion
     *                The addSingleQuestion to set.
     */
    public void setAddSingleQuestion(String addSingleQuestion) {
	this.addSingleQuestion = addSingleQuestion;
    }

    /**
     * @return Returns the editableQuestionIndex.
     */
    public String getEditableQuestionIndex() {
	return editableQuestionIndex;
    }

    /**
     * @param editableQuestionIndex
     *                The editableQuestionIndex to set.
     */
    public void setEditableQuestionIndex(String editableQuestionIndex) {
	this.editableQuestionIndex = editableQuestionIndex;
    }

    /**
     * @return Returns the feedback.
     */
    public String getFeedback() {
	return feedback;
    }

    /**
     * @param feedback
     *                The feedback to set.
     */
    public void setFeedback(String feedback) {
	this.feedback = feedback;
    }

    /**
     * @return Returns the editQuestionBoxRequest.
     */
    public String getEditQuestionBoxRequest() {
	return editQuestionBoxRequest;
    }

    /**
     * @param editQuestionBoxRequest
     *                The editQuestionBoxRequest to set.
     */
    public void setEditQuestionBoxRequest(String editQuestionBoxRequest) {
	this.editQuestionBoxRequest = editQuestionBoxRequest;
    }

    /**
     * @return Returns the lockWhenFinished.
     */
    public String getLockWhenFinished() {
	return lockWhenFinished;
    }

    /**
     * @param lockWhenFinished
     *                The lockWhenFinished to set.
     */
    public void setLockWhenFinished(String lockWhenFinished) {
	this.lockWhenFinished = lockWhenFinished;
    }

    /**
     * @return Returns the showOtherAnswers.
     */
    public String getShowOtherAnswers() {
	return showOtherAnswers;
    }

    /**
     * @param showOtherAnswers
     *                The showOtherAnswers to set.
     */
    public void setShowOtherAnswers(String showOtherAnswers) {
	this.showOtherAnswers = showOtherAnswers;
    }

    public boolean isAllowRichEditor() {
        return allowRichEditor;
    }

    public void setAllowRichEditor(boolean allowRichEditor) {
        this.allowRichEditor = allowRichEditor;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
