/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.vote.web;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;

/**
 * <p> ActionForm for the Authoring environment </p>
 * 
 * @author Ozgur Demirtas
 * 
 */
public class VoteAuthoringForm extends VoteLearningForm implements VoteAppConstants {
	/* form controllers */
	protected String addOptionContent;
	protected String currentTab;
	
	protected String questionIndex;
	protected String optIndex;
	protected String optionIndex;
	protected String selectedIndex;
	protected String deletableOptionIndex;
	
	protected String editDefaultQuestion;
	protected String removeOptionContent;
	
	protected String voteChangable;
	protected String lockOnFinish;
	protected String allowText;
	
	protected String activeModule;
	
	protected String maxNominationCount;
	
	protected String fileItem;
	protected String uuid;
	
	protected FormFile receivedFile;
	protected String offlineFile;
	
	protected String addContent;
	protected String removeContent;
	protected String removeAllContent;
	protected String submitAllContent;
	protected String submitTabDone;
	protected String submitOfflineFile;
	protected String submitOnlineFile;
	
	/* tab controller, these may go away once the Flash wraps the jsp */
	protected String choice;
	protected String choiceBasic;
	protected String choiceAdvanced;
	protected String choiceInstructions;
	
	protected String submit;
	
	/* basic content */
	protected String title;
	protected String instructions;
	
	protected String isRemoveContent;
	protected String toolContentID;
	
	
	/* instructions content */
	protected String onlineInstructions;
	protected String offlineInstructions;
	protected FormFile theOfflineFile;
	protected FormFile theOnlineFile;
	
	protected String richTextOfflineInstructions;
	protected String richTextOnlineInstructions;
	
	protected String defineLaterInEditMode;
	
	
	/* proxy controllers for Monitoring tabs */ 
	protected String summaryMonitoring;
	protected String instructionsMonitoring;
	protected String editActivityMonitoring;
	protected String statsMonitoring;

	protected String edit;
	protected String exceptionMaxNominationInvalid;
	protected String defaultContentIdStr;
	protected String defaultContentId;
	protected String isDefineLater;
	protected String submissionAttempt;
	protected String defaultOptionContent;
	protected String httpSessionID;
	protected IVoteService voteService;
	
	public void resetUserAction()
    {
		this.editDefaultQuestion=null;
		this.addOptionContent=null;	
		this.removeOptionContent=null;
		
		this.fileItem=null;
		
    	this.addContent=null;	
    	this.removeContent=null;
    	this.removeAllContent=null;
    	this.submitAllContent=null;
    	this.submitTabDone=null;
    	this.submitOfflineFile=null;
    	this.submitOnlineFile=null;
    	
    	
    	this.summaryMonitoring=null;
    	this.instructionsMonitoring=null;
    	this.editActivityMonitoring=null;
    	this.statsMonitoring=null;
    	this.edit=null;
    	this.submit=null;
    }
	
	public void reset()
	{
		this.editDefaultQuestion=null;
		this.addOptionContent=null;	
		this.removeOptionContent=null;
		
		this.fileItem=null;
		this.uuid=null;
		this.receivedFile=null;
		
		this.addContent=null;
		this.removeContent=null;
		this.removeAllContent=null;
		this.submitAllContent=null;
		this.submitTabDone=null;
		this.submitOfflineFile=null;
		this.submitOnlineFile=null;
		this.offlineFile=null;

    	this.choice=null;
		this.choiceBasic=null;
		this.choiceAdvanced=null;
		this.choiceInstructions=null;
		
		this.title=null;
		this.instructions=null;
		this.questionIndex=null;
		this.optIndex=null;
		this.optionIndex=null;
		this.selectedIndex=null;
		this.deletableOptionIndex=null;
		this.isRemoveContent=null;
		this.toolContentID=null;
		
		this.onlineInstructions=null;
		this.offlineInstructions=null;
		
		this.richTextOfflineInstructions=null;
		this.richTextOnlineInstructions=null;
	
		this.voteChangable=null;
		this.lockOnFinish=null;
		this.allowText=null;
		this.maxNominationCount=null;
		
		this.summaryMonitoring=null;
    	this.instructionsMonitoring=null;
    	this.editActivityMonitoring=null;
    	this.statsMonitoring=null;
    	this.edit=null;
    	this.submit=null;
    	this.submissionAttempt=null;
    	this.sbmtSuccess=null;
	}
	
	public void resetRadioBoxes()
	{
    	this.voteChangable		 =OPTION_OFF;
		this.lockOnFinish		 =OPTION_OFF;
		this.allowText			 =OPTION_OFF;
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
	 * @return Returns the isRemoveContent.
	 */
	public String getIsRemoveContent() {
		return isRemoveContent;
	}
	/**
	 * @param isRemoveContent The isRemoveContent to set.
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
	 * @param questionIndex The questionIndex to set.
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
	 * @param addContent The addContent to set.
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
	 * @param removeContent The removeContent to set.
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
	 * @param removeAllContent The removeAllContent to set.
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
	 * @param submitAllContent The submitAllContent to set.
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
	 * @param instructions The instructions to set.
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
	 * @param title The title to set.
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
	 * @return Returns the choiceAdvanced.
	 */
	public String getChoiceAdvanced() {
		return choiceAdvanced;
	}
	/**
	 * @param choiceAdvanced The choiceAdvanced to set.
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
	 * @param choiceBasic The choiceBasic to set.
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
	 * @param choiceInstructions The choiceInstructions to set.
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
	 * @param choice The choice to set.
	 */
	public void setChoice(String choice) {
		this.choice = choice;
	}

	/**
	 * @return Returns the submitTabDone.
	 */
	public String getSubmitTabDone() {
		return submitTabDone;
	}
	/**
	 * @param submitTabDone The submitTabDone to set.
	 */
	public void setSubmitTabDone(String submitTabDone) {
		this.submitTabDone = submitTabDone;
	}
	
	/**
	 * @return Returns the editActivityMonitoring.
	 */
	public String getEditActivityMonitoring() {
		return editActivityMonitoring;
	}
	/**
	 * @param editActivityMonitoring The editActivityMonitoring to set.
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
	 * @param instructionsMonitoring The instructionsMonitoring to set.
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
	 * @param statsMonitoring The statsMonitoring to set.
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
	 * @param summaryMonitoring The summaryMonitoring to set.
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
	 * @param edit The edit to set.
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
	 * @param submitOfflineFile The submitOfflineFile to set.
	 */
	public void setSubmitOfflineFile(String submitOfflineFile) {
		this.submitOfflineFile = submitOfflineFile;
	}
	/**
	 * @param theOfflineFile The theOfflineFile to set.
	 */
	public void setTheOfflineFile(FormFile theOfflineFile) {
		this.theOfflineFile = theOfflineFile;
	}
	/**
	 * @param theOnlineFile The theOnlineFile to set.
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
	 * @param submitOnlineFile The submitOnlineFile to set.
	 */
	public void setSubmitOnlineFile(String submitOnlineFile) {
		this.submitOnlineFile = submitOnlineFile;
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
	 * @return Returns the editDefaultQuestion.
	 */
	public String getEditDefaultQuestion() {
		return editDefaultQuestion;
	}
	/**
	 * @param editDefaultQuestion The editDefaultQuestion to set.
	 */
	public void setEditDefaultQuestion(String editDefaultQuestion) {
		this.editDefaultQuestion = editDefaultQuestion;
	}
	/**
	 * @return Returns the addOptionContent.
	 */
	public String getAddOptionContent() {
		return addOptionContent;
	}
	/**
	 * @param addOptionContent The addOptionContent to set.
	 */
	public void setAddOptionContent(String addOptionContent) {
		this.addOptionContent = addOptionContent;
	}
	/**
	 * @return Returns the removeOptionContent.
	 */
	public String getRemoveOptionContent() {
		return removeOptionContent;
	}
	/**
	 * @param removeOptionContent The removeOptionContent to set.
	 */
	public void setRemoveOptionContent(String removeOptionContent) {
		this.removeOptionContent = removeOptionContent;
	}
	/**
	 * @return Returns the optionIndex.
	 */
	public String getOptionIndex() {
		return optionIndex;
	}
	/**
	 * @param optionIndex The optionIndex to set.
	 */
	public void setOptionIndex(String optionIndex) {
		this.optionIndex = optionIndex;
	}

	/**
	 * @return Returns the selectedIndex.
	 */
	public String getSelectedIndex() {
		return selectedIndex;
	}
	/**
	 * @param selectedIndex The selectedIndex to set.
	 */
	public void setSelectedIndex(String selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	/**
	 * @return Returns the deletableOptionIndex.
	 */
	public String getDeletableOptionIndex() {
		return deletableOptionIndex;
	}
	/**
	 * @param deletableOptionIndex The deletableOptionIndex to set.
	 */
	public void setDeletableOptionIndex(String deletableOptionIndex) {
		this.deletableOptionIndex = deletableOptionIndex;
	}

	/**
	 * @return Returns the fileItem.
	 */
	public String getFileItem() {
		return fileItem;
	}
	/**
	 * @param fileItem The fileItem to set.
	 */
	public void setFileItem(String fileItem) {
		this.fileItem = fileItem;
	}
	/**
	 * @return Returns the receivedFile.
	 */
	public FormFile getReceivedFile() {
		return receivedFile;
	}
	/**
	 * @param receivedFile The receivedFile to set.
	 */
	public void setReceivedFile(FormFile receivedFile) {
		this.receivedFile = receivedFile;
	}

	
	/**
	 * @return Returns the offlineFile.
	 */
	public String getOfflineFile() {
		return offlineFile;
	}
	/**
	 * @param offlineFile The offlineFile to set.
	 */
	public void setOfflineFile(String offlineFile) {
		this.offlineFile = offlineFile;
	}
	/**
	 * @return Returns the uuid.
	 */
	public String getUuid() {
		return uuid;
	}
	/**
	 * @param uuid The uuid to set.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * @return Returns the currentTab.
	 */
	public String getCurrentTab() {
		return currentTab;
	}
	/**
	 * @param currentTab The currentTab to set.
	 */
	public void setCurrentTab(String currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 * @return Returns the submit.
	 */
	public String getSubmit() {
		return submit;
	}
	/**
	 * @param submit The submit to set.
	 */
	public void setSubmit(String submit) {
		this.submit = submit;
	}

    /**
     * @return Returns the optIndex.
     */
    public String getOptIndex() {
        return optIndex;
    }
    /**
     * @param optIndex The optIndex to set.
     */
    public void setOptIndex(String optIndex) {
        this.optIndex = optIndex;
    }
    
    /**
     * @return Returns the voteChangable.
     */
    public String getVoteChangable() {
        return voteChangable;
    }
    /**
     * @param voteChangable The voteChangable to set.
     */
    public void setVoteChangable(String voteChangable) {
        this.voteChangable = voteChangable;
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
     * @return Returns the activeModule.
     */
    public String getActiveModule() {
        return activeModule;
    }
    /**
     * @param activeModule The activeModule to set.
     */
    public void setActiveModule(String activeModule) {
        this.activeModule = activeModule;
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
     * @return Returns the voteService.
     */
    public IVoteService getVoteService() {
        return voteService;
    }
    /**
     * @param voteService The voteService to set.
     */
    public void setVoteService(IVoteService voteService) {
        this.voteService = voteService;
    }
    
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
    
	public String toString() {
        return new ToStringBuilder(this)
            .append("activeModule: ", activeModule)
            .append("defineLaterInEditMode: ", defineLaterInEditMode)
            .append("submissionAttempt: ", submissionAttempt)
            .append("sbmtSuccess: ", sbmtSuccess)
            .append("exceptionMaxNominationInvalid: ", exceptionMaxNominationInvalid)
            .append("isDefineLater: ", isDefineLater)
            .append("toolContentID: ", toolContentID)
            .append("allowText: ", allowText)
            .append("voteChangable: ", voteChangable)
            .append("lockOnFinish: ", lockOnFinish)
            .append("defaultContentId: ", defaultContentId)
            .append("defaultContentIdStr: ", defaultContentIdStr)
            .append("maxNominationCount: ", maxNominationCount)
            .append("defaultOptionContent: ", defaultOptionContent)
            .append("activityTitle: ", activityTitle)
            .append("activityInstructions: ", activityInstructions)
            .append("richTextOfflineInstructions: ", richTextOfflineInstructions)
            .append("richTextOnlineInstructions: ", richTextOnlineInstructions)
            .append("onlineInstructions: ", onlineInstructions)
            .append("offlineInstructions: ", offlineInstructions)
            .toString();
    }

}
