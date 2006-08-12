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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
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
 * <p> DTO that holds learner flow decision properties and some other view-only properties
 * </p>
 * 
 * @author Ozgur Demirtas
 */
public class VoteGeneralLearnerFlowDTO implements Comparable
{
    protected String activityTitle;
    
    protected String activityInstructions;
    
    protected String revisitingUser;
    
    protected String userEntry;
    
    protected String castVoteCount;
    
    protected String maxNominationCountReached;
    
    protected String activityRunOffline;
    
    protected String toolSessionID;
    
    protected String toolContentID;
    
    protected String toolContentUID;
    
    protected String learningMode;
    
    protected String maxNominationCount;
	
    protected String allowTextEntry;
	
    protected String lockOnFinish;
	
    protected String voteChangable;
    
    protected String totalQuestionCount;
    
    protected String previewOnly;
    
    protected String reportViewOnly;
    
    protected String requestLearningReport;
    
    protected String requestLearningReportProgress;
    
    protected String nominationsSubmited;
    
    protected Map mapGeneralCheckedOptionsContent;
    
    protected Map mapStandardNominationsContent;
    
    protected Map mapStandardNominationsHTMLedContent;
    
    protected Map mapStandardRatesContent;
    
    protected Map mapStandardUserCount;
    
    protected Map mapStandardQuestionUid;
    
    protected Map mapStandardToolSessionUid;
    
    protected List listMonitoredAnswersContainerDto;
    
    protected List listUserEntries;
    
    /**
     * @return Returns the activityRunOffline.
     */
    public String getActivityRunOffline() {
        return activityRunOffline;
    }
    /**
     * @param activityRunOffline The activityRunOffline to set.
     */
    public void setActivityRunOffline(String activityRunOffline) {
        this.activityRunOffline = activityRunOffline;
    }
    /**
     * @return Returns the castVoteCount.
     */
    public String getCastVoteCount() {
        return castVoteCount;
    }
    /**
     * @param castVoteCount The castVoteCount to set.
     */
    public void setCastVoteCount(String castVoteCount) {
        this.castVoteCount = castVoteCount;
    }
    /**
     * @return Returns the maxNominationCountReached.
     */
    public String getMaxNominationCountReached() {
        return maxNominationCountReached;
    }
    /**
     * @param maxNominationCountReached The maxNominationCountReached to set.
     */
    public void setMaxNominationCountReached(String maxNominationCountReached) {
        this.maxNominationCountReached = maxNominationCountReached;
    }
    /**
     * @return Returns the revisitingUser.
     */
    public String getRevisitingUser() {
        return revisitingUser;
    }
    /**
     * @param revisitingUser The revisitingUser to set.
     */
    public void setRevisitingUser(String revisitingUser) {
        this.revisitingUser = revisitingUser;
    }
    /**
     * @return Returns the userEntry.
     */
    public String getUserEntry() {
        return userEntry;
    }
    /**
     * @param userEntry The userEntry to set.
     */
    public void setUserEntry(String userEntry) {
        this.userEntry = userEntry;
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
     * @return Returns the learningMode.
     */
    public String getLearningMode() {
        return learningMode;
    }
    /**
     * @param learningMode The learningMode to set.
     */
    public void setLearningMode(String learningMode) {
        this.learningMode = learningMode;
    }
    /**
     * @return Returns the toolSessionID.
     */
    public String getToolSessionID() {
        return toolSessionID;
    }
    /**
     * @param toolSessionID The toolSessionID to set.
     */
    public void setToolSessionID(String toolSessionID) {
        this.toolSessionID = toolSessionID;
    }
    
    /**
     * @return Returns the allowTextEntry.
     */
    public String getAllowTextEntry() {
        return allowTextEntry;
    }
    /**
     * @param allowTextEntry The allowTextEntry to set.
     */
    public void setAllowTextEntry(String allowTextEntry) {
        this.allowTextEntry = allowTextEntry;
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
     * @return Returns the toolContentUID.
     */
    public String getToolContentUID() {
        return toolContentUID;
    }
    /**
     * @param toolContentUID The toolContentUID to set.
     */
    public void setToolContentUID(String toolContentUID) {
        this.toolContentUID = toolContentUID;
    }    
    
    /**
     * @return Returns the previewOnly.
     */
    public String getPreviewOnly() {
        return previewOnly;
    }
    /**
     * @param previewOnly The previewOnly to set.
     */
    public void setPreviewOnly(String previewOnly) {
        this.previewOnly = previewOnly;
    }
    
    /**
     * @return Returns the totalQuestionCount.
     */
    public String getTotalQuestionCount() {
        return totalQuestionCount;
    }
    /**
     * @param totalQuestionCount The totalQuestionCount to set.
     */
    public void setTotalQuestionCount(String totalQuestionCount) {
        this.totalQuestionCount = totalQuestionCount;
    }

    /**
     * @return Returns the listUserEntries.
     */
    public List getListUserEntries() {
        return listUserEntries;
    }
    /**
     * @param listUserEntries The listUserEntries to set.
     */
    public void setListUserEntries(List listUserEntries) {
        this.listUserEntries = listUserEntries;
    }
    public int compareTo(Object o)
    {
	    VoteGeneralLearnerFlowDTO voteGeneralLearnerFlowDTO = (VoteGeneralLearnerFlowDTO) o;
     
        if (voteGeneralLearnerFlowDTO == null)
        	return 1;
		else
			return 0;
    }

	public String toString() {
        return new ToStringBuilder(this)
            .append("activityInstructions: ", activityInstructions)
            .append("activityTitle: ", activityTitle)
            .append("revisitingUser: ", revisitingUser)
            .append("userEntry: ", userEntry)
            .append("castVoteCount: ", castVoteCount)
            .append("maxNominationCountReached: ", maxNominationCountReached)            
            .append("activityRunOffline: ", activityRunOffline)
            .append("toolSessionID: ", toolSessionID)
            .append("learningMode: ", learningMode)
			.append("maxNominationCount: ", maxNominationCount)
			.append("allowTextEntry: ", allowTextEntry)
			.append("lockOnFinish: ", lockOnFinish)
			.append("voteChangable: ", voteChangable)
			.append("toolContentID: ", toolContentID)
			.append("toolContentUID: ", toolContentUID)
			.append("totalQuestionCount: ", totalQuestionCount)
			.append("requestLearningReport: ", requestLearningReport)
			.append("requestLearningReportProgress: ", requestLearningReportProgress)
			.append("nominationsSubmited: ", nominationsSubmited)
			.append("mapGeneralCheckedOptionsContent: ", mapGeneralCheckedOptionsContent)
			.append("mapStandardNominationsContent: ", mapStandardNominationsContent)
			.append("mapStandardNominationsHTMLedContent: ", mapStandardNominationsHTMLedContent)
			.append("mapStandardRatesContent: ", mapStandardRatesContent)
			.append("mapStandardUserCount: ", mapStandardUserCount)
			.append("mapStandardQuestionUid: ", mapStandardQuestionUid)
			.append("mapStandardToolSessionUid: ", mapStandardToolSessionUid)
			.append("listMonitoredAnswersContainerDto: ", listMonitoredAnswersContainerDto)
			.append("listUserEntries: ", listUserEntries)
			.append("reportViewOnly: ", reportViewOnly)
            .toString();
    }
	
    
    /**
     * @return Returns the reportViewOnly.
     */
    public String getReportViewOnly() {
        return reportViewOnly;
    }
    /**
     * @param reportViewOnly The reportViewOnly to set.
     */
    public void setReportViewOnly(String reportViewOnly) {
        this.reportViewOnly = reportViewOnly;
    }
    /**
     * @return Returns the nominationsSubmited.
     */
    public String getNominationsSubmited() {
        return nominationsSubmited;
    }
    /**
     * @param nominationsSubmited The nominationsSubmited to set.
     */
    public void setNominationsSubmited(String nominationsSubmited) {
        this.nominationsSubmited = nominationsSubmited;
    }
    /**
     * @return Returns the requestLearningReport.
     */
    public String getRequestLearningReport() {
        return requestLearningReport;
    }
    /**
     * @param requestLearningReport The requestLearningReport to set.
     */
    public void setRequestLearningReport(String requestLearningReport) {
        this.requestLearningReport = requestLearningReport;
    }
    /**
     * @return Returns the requestLearningReportProgress.
     */
    public String getRequestLearningReportProgress() {
        return requestLearningReportProgress;
    }
    /**
     * @param requestLearningReportProgress The requestLearningReportProgress to set.
     */
    public void setRequestLearningReportProgress(
            String requestLearningReportProgress) {
        this.requestLearningReportProgress = requestLearningReportProgress;
    }
    /**
     * @return Returns the mapStandardNominationsContent.
     */
    public Map getMapStandardNominationsContent() {
        return mapStandardNominationsContent;
    }
    /**
     * @param mapStandardNominationsContent The mapStandardNominationsContent to set.
     */
    public void setMapStandardNominationsContent(
            Map mapStandardNominationsContent) {
        this.mapStandardNominationsContent = mapStandardNominationsContent;
    }
    /**
     * @return Returns the mapStandardNominationsHTMLedContent.
     */
    public Map getMapStandardNominationsHTMLedContent() {
        return mapStandardNominationsHTMLedContent;
    }
    /**
     * @param mapStandardNominationsHTMLedContent The mapStandardNominationsHTMLedContent to set.
     */
    public void setMapStandardNominationsHTMLedContent(
            Map mapStandardNominationsHTMLedContent) {
        this.mapStandardNominationsHTMLedContent = mapStandardNominationsHTMLedContent;
    }
    /**
     * @return Returns the mapStandardRatesContent.
     */
    public Map getMapStandardRatesContent() {
        return mapStandardRatesContent;
    }
    /**
     * @param mapStandardRatesContent The mapStandardRatesContent to set.
     */
    public void setMapStandardRatesContent(Map mapStandardRatesContent) {
        this.mapStandardRatesContent = mapStandardRatesContent;
    }
    /**
     * @return Returns the mapStandardUserCount.
     */
    public Map getMapStandardUserCount() {
        return mapStandardUserCount;
    }
    /**
     * @param mapStandardUserCount The mapStandardUserCount to set.
     */
    public void setMapStandardUserCount(Map mapStandardUserCount) {
        this.mapStandardUserCount = mapStandardUserCount;
    }
    /**
     * @return Returns the listMonitoredAnswersContainerDto.
     */
    public List getListMonitoredAnswersContainerDto() {
        return listMonitoredAnswersContainerDto;
    }
    /**
     * @param listMonitoredAnswersContainerDto The listMonitoredAnswersContainerDto to set.
     */
    public void setListMonitoredAnswersContainerDto(
            List listMonitoredAnswersContainerDto) {
        this.listMonitoredAnswersContainerDto = listMonitoredAnswersContainerDto;
    }
    /**
     * @return Returns the mapGeneralCheckedOptionsContent.
     */
    public Map getMapGeneralCheckedOptionsContent() {
        return mapGeneralCheckedOptionsContent;
    }
    /**
     * @param mapGeneralCheckedOptionsContent The mapGeneralCheckedOptionsContent to set.
     */
    public void setMapGeneralCheckedOptionsContent(
            Map mapGeneralCheckedOptionsContent) {
        this.mapGeneralCheckedOptionsContent = mapGeneralCheckedOptionsContent;
    }
    /**
     * @return Returns the mapStandardQuestionUid.
     */
    public Map getMapStandardQuestionUid() {
        return mapStandardQuestionUid;
    }
    /**
     * @param mapStandardQuestionUid The mapStandardQuestionUid to set.
     */
    public void setMapStandardQuestionUid(Map mapStandardQuestionUid) {
        this.mapStandardQuestionUid = mapStandardQuestionUid;
    }
    /**
     * @return Returns the mapStandardToolSessionUid.
     */
    public Map getMapStandardToolSessionUid() {
        return mapStandardToolSessionUid;
    }
    /**
     * @param mapStandardToolSessionUid The mapStandardToolSessionUid to set.
     */
    public void setMapStandardToolSessionUid(Map mapStandardToolSessionUid) {
        this.mapStandardToolSessionUid = mapStandardToolSessionUid;
    }
}
