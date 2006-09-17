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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.vote;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * <p> DTO that holds summary data for all the sessions </p>
 * 
 * @author Ozgur Demirtas
 */
public class VoteAllSessionsDTO implements Comparable
{
	private String sessionUserCount;
	private String completedSessionUserCount;
	private String completedSessionUserPercent;
	private String sessionId;
	private String sessionName;
	private Map mapStandardNominationsContent;
	private Map mapStandardNominationsHTMLedContent;
	
	private Map mapStandardQuestionUid;
	private Map mapStandardToolSessionUid;
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
	private Map mapStandardUserCount;
	private Map mapStandardRatesContent;
	private List listUserEntries;
	private String existsOpenVote;
	
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
	public String toString() {
        return new ToStringBuilder(this)
            .append("sessionId", getSessionId())
            .toString();
    }
	
	public int compareTo(Object o)
    {
	    VoteAllSessionsDTO voteAllSessionsDTO = (VoteAllSessionsDTO) o;
     
        if (voteAllSessionsDTO == null)
        	return 1;
		else
			return (int) (new Long(sessionId).longValue() - new Long(voteAllSessionsDTO.sessionId).longValue());
    }

    /**
     * @return Returns the completedSessionUserCount.
     */
    public String getCompletedSessionUserCount() {
        return completedSessionUserCount;
    }
    /**
     * @param completedSessionUserCount The completedSessionUserCount to set.
     */
    public void setCompletedSessionUserCount(String completedSessionUserCount) {
        this.completedSessionUserCount = completedSessionUserCount;
    }
    /**
     * @return Returns the sessionId.
     */
    public String getSessionId() {
        return sessionId;
    }
    /**
     * @param sessionId The sessionId to set.
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    /**
     * @return Returns the sessionUserCount.
     */
    public String getSessionUserCount() {
        return sessionUserCount;
    }
    /**
     * @param sessionUserCount The sessionUserCount to set.
     */
    public void setSessionUserCount(String sessionUserCount) {
        this.sessionUserCount = sessionUserCount;
    }
    
    /**
     * @return Returns the completedSessionUserPercent.
     */
    public String getCompletedSessionUserPercent() {
        return completedSessionUserPercent;
    }
    /**
     * @param completedSessionUserPercent The completedSessionUserPercent to set.
     */
    public void setCompletedSessionUserPercent(
            String completedSessionUserPercent) {
        this.completedSessionUserPercent = completedSessionUserPercent;
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
     * @return Returns the existsOpenVote.
     */
    public String getExistsOpenVote() {
        return existsOpenVote;
    }
    /**
     * @param existsOpenVote The existsOpenVote to set.
     */
    public void setExistsOpenVote(String existsOpenVote) {
        this.existsOpenVote = existsOpenVote;
    }
    /**
     * @return Returns the sessionName.
     */
    public String getSessionName() {
        return sessionName;
    }
    /**
     * @param sessionName The sessionName to set.
     */
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
}
