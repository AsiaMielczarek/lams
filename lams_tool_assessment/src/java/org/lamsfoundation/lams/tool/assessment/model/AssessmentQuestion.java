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
package org.lamsfoundation.lams.tool.assessment.model;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.assessment.util.SequencableComparator;

/**
 * Assessment Question
 * 
 * @author Andrey Balan
 * 
 * @hibernate.class table="tl_laasse10_assessment_question"
 * 
 */
public class AssessmentQuestion implements Cloneable, Sequencable {
    private static final Logger log = Logger.getLogger(AssessmentQuestion.class);

    private Long uid;

    private short type;

    private String title;

    private String question;
    
    private int sequenceId;

    private int defaultGrade;
    
    private float penaltyFactor;
    
    private String generalFeedback;
    
    private String feedback;
    
    private boolean multipleAnswersAllowed;
    
    private String feedbackOnCorrect;
    
    private String feedbackOnPartiallyCorrect;
    
    private String feedbackOnIncorrect;

    private boolean shuffle;
    
    private boolean caseSensitive;
    
    private boolean correctAnswer;
    
    private boolean hide;
    private boolean isCreateByAuthor;

    private Date createDate;
    private AssessmentUser createBy;
    
    // ***********************************************
    // Non persistant fields:
    
    private Set<AssessmentQuestionOption> questionOptions;
    
    private Set<AssessmentUnit> units;
    
    // DTO fields:
    private boolean complete;
    
    private String answerString;
    
    private float answerFloat;
    
    private boolean answerBoolean;
    
    private float mark;
    
    private float penalty;
    
    public AssessmentQuestion() {
	questionOptions = new TreeSet<AssessmentQuestionOption>(new SequencableComparator());
	units = new TreeSet<AssessmentUnit>(new SequencableComparator());
    }

    public Object clone() {
	AssessmentQuestion obj = null;
	try {
	    obj = (AssessmentQuestion) super.clone();
	    ((AssessmentQuestion) obj).setUid(null);
	    
	    // clone questionOptions
	    if (questionOptions != null) {
		Iterator<AssessmentQuestionOption> iter = questionOptions.iterator();
		Set<AssessmentQuestionOption> set = new TreeSet<AssessmentQuestionOption>(new SequencableComparator());
		while (iter.hasNext()) {
		    AssessmentQuestionOption answerOption = (AssessmentQuestionOption) iter.next();
		    AssessmentQuestionOption newAnswerOption = (AssessmentQuestionOption) answerOption.clone();
		    set.add(newAnswerOption);
		}
		obj.questionOptions = set;
	    }
	    
	    // clone units
	    if (units != null) {
		Iterator<AssessmentUnit> iter = units.iterator();
		Set<AssessmentUnit> set = new TreeSet<AssessmentUnit>(new SequencableComparator());
		while (iter.hasNext()) {
		    AssessmentUnit unit = (AssessmentUnit) iter.next();
		    AssessmentUnit newUnit = (AssessmentUnit) unit.clone();
		    set.add(newUnit);
		}
		obj.units = set;
	    }
	    
	    // clone AssessmentUser as well
	    if (this.createBy != null) {
		((AssessmentQuestion) obj).setCreateBy((AssessmentUser) this.createBy.clone());
	    }
	} catch (CloneNotSupportedException e) {
	    log.error("When clone " + AssessmentQuestion.class + " failed");
	}

	return obj;
    }

    // **********************************************************
    // Get/Set methods
    // **********************************************************
    /**
     * @hibernate.id generator-class="native" type="java.lang.Long" column="uid"
     * @return Returns the uid.
     */
    public Long getUid() {
	return uid;
    }

    /**
     * @param uid
     *            The uid to set.
     */
    public void setUid(Long userID) {
	this.uid = userID;
    }
    
    /**
     * @hibernate.property column="question_type"
     * @return
     */
    public short getType() {
	return type;
    }

    public void setType(short type) {
	this.type = type;
    }
    
    /**
     * @hibernate.property column="title" length="255"
     * @return
     */
    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }
    
    /**
     * @hibernate.property column="question" type="text"
     * @return
     */
    public String getQuestion() {
	return question;
    }

    public void setQuestion(String question) {
	this.question = question;
    }
    
    /**
     * Returns image sequence number.
     * 
     * @return image sequence number
     * 
     * @hibernate.property column="sequence_id"
     */
    public int getSequenceId() {
	return sequenceId;
    }

    /**
     * Sets image sequence number.
     * 
     * @param sequenceId
     *                image sequence number
     */
    public void setSequenceId(int sequenceId) {
	this.sequenceId = sequenceId;
    }

    /**
     * @hibernate.property column="default_grade"
     * 
     * @return
     */
    public int getDefaultGrade() {
	return defaultGrade;
    }

    public void setDefaultGrade(int defaultGrade) {
	this.defaultGrade = defaultGrade;
    }

    /**
     * @hibernate.property column="penalty_factor"
     * @return
     */
    public float getPenaltyFactor() {
	return penaltyFactor;
    }

    public void setPenaltyFactor(float penaltyFactor) {
	this.penaltyFactor = penaltyFactor;
    }

    /**
     * @hibernate.property column="general_feedback" type="text"
     * @return
     */
    public String getGeneralFeedback() {
	return generalFeedback;
    }

    public void setGeneralFeedback(String generalFeedback) {
	this.generalFeedback = generalFeedback;
    }
    
    /**
     * @hibernate.property column="feedback" type="text"
     * @return
     */
    public String getFeedback() {
	return feedback;
    }

    public void setFeedback(String feedback) {
	this.feedback = feedback;
    }
    
    /**
     * @hibernate.property column="multiple_answers_allowed"
     * @return
     */
    public boolean isMultipleAnswersAllowed() {
	return multipleAnswersAllowed;
    }

    public void setMultipleAnswersAllowed(boolean multipleAnswersAllowed) {
	this.multipleAnswersAllowed = multipleAnswersAllowed;
    }

    /**
     * @hibernate.property column="feedback_on_correct"  type="text"
     * @return
     */
    public String getFeedbackOnCorrect() {
	return feedbackOnCorrect;
    }

    public void setFeedbackOnCorrect(String feedbackOnCorrect) {
	this.feedbackOnCorrect = feedbackOnCorrect;
    }
    
    /**
     * @hibernate.property column="feedback_on_partially_correct"  type="text"
     * @return
     */
    public String getFeedbackOnPartiallyCorrect() {
	return feedbackOnPartiallyCorrect;
    }

    public void setFeedbackOnPartiallyCorrect(String feedbackOnPartiallyCorrect) {
	this.feedbackOnPartiallyCorrect = feedbackOnPartiallyCorrect;
    }
    
    /**
     * @hibernate.property column="feedback_on_incorrect"  type="text"
     * @return
     */
    public String getFeedbackOnIncorrect() {
	return feedbackOnIncorrect;
    }

    public void setFeedbackOnIncorrect(String feedbackOnIncorrect) {
	this.feedbackOnIncorrect = feedbackOnIncorrect;
    }
    
    /**
     * @hibernate.property column="shuffle"
     * @return
     */
    public boolean isShuffle() {
	return shuffle;
    }

    public void setShuffle(boolean shuffle) {
	this.shuffle = shuffle;
    }
    
    /**
     * @hibernate.property column="case_sensitive"
     * @return
     */
    public boolean isCaseSensitive() {
	return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
	this.caseSensitive = caseSensitive;
    }
    
    /**
     * @hibernate.property column="correct_answer"
     * @return
     */
    public boolean getCorrectAnswer() {
	return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
	this.correctAnswer = correctAnswer;
    }
    
    /**
     * @hibernate.property column="hide"
     * @return
     */
    public boolean isHide() {
	return hide;
    }

    public void setHide(boolean hide) {
	this.hide = hide;
    }

    /**
     * @hibernate.property column="create_by_author"
     * @return
     */
    public boolean isCreateByAuthor() {
	return isCreateByAuthor;
    }

    public void setCreateByAuthor(boolean isCreateByAuthor) {
	this.isCreateByAuthor = isCreateByAuthor;
    }

    /**
     * @hibernate.property column="create_date"
     * @return
     */
    public Date getCreateDate() {
	return createDate;
    }

    public void setCreateDate(Date createDate) {
	this.createDate = createDate;
    }
    
    /**
     * @hibernate.many-to-one cascade="none" column="create_by"
     * 
     * @return
     */
    public AssessmentUser getCreateBy() {
	return createBy;
    }

    public void setCreateBy(AssessmentUser createBy) {
	this.createBy = createBy;
    }
    
    /**
     * 
     * @hibernate.set cascade="all" order-by="sequence_id asc"
     * @hibernate.collection-key column="question_uid"
     * @hibernate.collection-one-to-many class="org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestionOption"
     * 
     * @return a set of questionOptions to this AssessmentQuestion.
     */
    public Set<AssessmentQuestionOption> getQuestionOptions() {
	return questionOptions;
    }

    /**
     * @param questionOptions questionOptions to set.
     */
    public void setQuestionOptions(Set<AssessmentQuestionOption> questionOptions) {
	this.questionOptions = questionOptions;
    }
    
    /**
     * 
     * @hibernate.set cascade="all" order-by="sequence_id asc"
     * @hibernate.collection-key column="question_uid"
     * @hibernate.collection-one-to-many class="org.lamsfoundation.lams.tool.assessment.model.AssessmentUnit"
     * 
     * @return a set of units to this AssessmentQuestion.
     */
    public Set<AssessmentUnit> getUnits() {
	return units;
    }

    /**
     * @param questionOptions units to set.
     */
    public void setUnits(Set<AssessmentUnit> units) {
	this.units = units;
    }
    
    public void setComplete(boolean complete) {
	this.complete = complete;
    }
    public boolean isComplete() {
	return complete;
    }
    
    public String getAnswerString() {
	return answerString;
    }
    public void setAnswerString(String answerString) {
	this.answerString = answerString;
    }
    
    public float getAnswerFloat() {
	return answerFloat;
    }
    public void setAnswerFloat(float answerFloat) {
	this.answerFloat = answerFloat;
    }
    
    public boolean getAnswerBoolean() {
	return answerBoolean;
    }
    public void setAnswerBoolean(boolean answerBoolean) {
	this.answerBoolean = answerBoolean;
    }   
    
    public Float getMark() {
	return mark;
    }
    public void setMark(Float mark) {
	this.mark = mark;
    }
    
    public Float getPenalty() {
	return penalty;
    }
    public void setPenalty(Float penalty) {
	this.penalty = penalty;
    }
}
