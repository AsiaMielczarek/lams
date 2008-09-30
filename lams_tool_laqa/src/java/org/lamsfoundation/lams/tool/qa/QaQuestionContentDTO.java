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
package org.lamsfoundation.lams.tool.qa;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>
 * DTO that holds users attempt history data for jsp purposes
 * </p>
 * 
 * @author Ozgur Demirtas
 */
public class QaQuestionContentDTO implements Comparable {
    private String question;
    private String displayOrder;
    private String feedback;

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("question:", question).append("feedback:", feedback).append(
		"displayOrder:", displayOrder).toString();
    }

    public int compareTo(Object o) {
	QaMonitoredUserDTO qaMonitoredUserDTO = (QaMonitoredUserDTO) o;

	if (qaMonitoredUserDTO == null) {
	    return 1;
	} else {
	    return 0;
	}
    }

    /**
     * @return Returns the displayOrder.
     */
    public String getDisplayOrder() {
	return displayOrder;
    }

    /**
     * @param displayOrder
     *                The displayOrder to set.
     */
    public void setDisplayOrder(String displayOrder) {
	this.displayOrder = displayOrder;
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
     * @return Returns the question.
     */
    public String getQuestion() {
	return question;
    }

    /**
     * @param question
     *                The question to set.
     */
    public void setQuestion(String question) {
	this.question = question;
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof QaQuestionContentDTO) {
	    QaQuestionContentDTO compare = (QaQuestionContentDTO) o;
	    return compare.getDisplayOrder().equals(getDisplayOrder());
	}
	return false;
    }

    @Override
    public int hashCode() {
	return new Integer(getDisplayOrder());
    }
}
