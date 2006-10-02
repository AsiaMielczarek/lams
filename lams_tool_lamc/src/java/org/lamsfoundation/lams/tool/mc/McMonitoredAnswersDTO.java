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
package org.lamsfoundation.lams.tool.mc;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * <p> DTO that holds question/candidate answers data as well as user attempts data for jsp purposes
 * </p>
 * 
 * @author Ozgur Demirtas
 */
public class McMonitoredAnswersDTO implements Comparable
{
	private String questionUid;
	
	private String question;
	
	private String weight;
	
	private String mark;
	
	private String sessionId;
	
	private String sessionName;
	
	private List candidateAnswers;
	
	private List candidateAnswersCorrect;
	
	private Map questionAttempts;
	
	
	/**
	 * @return Returns the candidateAnswers.
	 */
	public List getCandidateAnswers() {
		return candidateAnswers;
	}
	/**
	 * @param candidateAnswers The candidateAnswers to set.
	 */
	public void setCandidateAnswers(List candidateAnswers) {
		this.candidateAnswers = candidateAnswers;
	}
	/**
	 * @return Returns the question.
	 */
	public String getQuestion() {
		return question;
	}
	/**
	 * @param question The question to set.
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	/**
	 * @return Returns the questionUid.
	 */
	public String getQuestionUid() {
		return questionUid;
	}
	/**
	 * @param questionUid The questionUid to set.
	 */
	public void setQuestionUid(String questionUid) {
		this.questionUid = questionUid;
	}
	
	/**
	 * @return Returns the questionAttempts.
	 */
	public Map getQuestionAttempts() {
		return questionAttempts;
	}
	/**
	 * @param questionAttempts The questionAttempts to set.
	 */
	public void setQuestionAttempts(Map questionAttempts) {
		this.questionAttempts = questionAttempts;
	}
	

	public String toString() {
        return new ToStringBuilder(this)
            .append("questionUid: ", questionUid)
            .append("question: ", question)
            .append("weight: ", weight)
            .append("candidateAnswers: ", candidateAnswers)            
            .append("questionAttempts: ", questionAttempts)
            .append("candidateAnswersCorrect:", candidateAnswersCorrect)
            .toString();
    }
	
	
	public int compareTo(Object o)
    {
		McMonitoredAnswersDTO mcMonitoredAnswersDTO = (McMonitoredAnswersDTO) o;
     
        if (mcMonitoredAnswersDTO == null)
        	return 1;
		else
			return (int) (new Long(questionUid).longValue() - new Long(mcMonitoredAnswersDTO.questionUid).longValue());
    }
	
    /**
     * @return Returns the weight.
     */
    public String getWeight() {
        return weight;
    }
    /**
     * @param weight The weight to set.
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }
    /**
     * @return Returns the candidateAnswersCorrect.
     */
    public List getCandidateAnswersCorrect() {
        return candidateAnswersCorrect;
    }
    /**
     * @param candidateAnswersCorrect The candidateAnswersCorrect to set.
     */
    public void setCandidateAnswersCorrect(List candidateAnswersCorrect) {
        this.candidateAnswersCorrect = candidateAnswersCorrect;
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
    /**
     * @return Returns the mark.
     */
    public String getMark() {
        return mark;
    }
    /**
     * @param mark The mark to set.
     */
    public void setMark(String mark) {
        this.mark = mark;
    }
}
