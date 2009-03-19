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
package org.lamsfoundation.lams.tool.assessment.dao.hibernate;

import java.util.List;

import org.lamsfoundation.lams.tool.assessment.dao.AssessmentQuestionResultDAO;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestionResult;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentResult;

public class AssessmentQuestionResultDAOHibernate extends BaseDAOHibernate implements AssessmentQuestionResultDAO {

    private static final String FIND_BY_UID = "from " + AssessmentQuestionResult.class.getName()
	    + " as r where r.uid = ?";

    private static final String FIND_BY_ASSESSMENT_QUESTION_AND_USER = "from "
	    + AssessmentQuestionResult.class.getName()
	    + " as q, "
	    + AssessmentResult.class.getName()
	    + " as r "
	    + " where q.resultUid = r.uid and r.assessment.uid = ? and r.user.userId =? and q.assessmentQuestion.uid =? order by r.startDate asc";

    private static final String FIND_WRONG_ANSWERS_NUMBER = "select count(q) from  "
	    + AssessmentQuestionResult.class.getName()
	    + " as q, "
	    + AssessmentResult.class.getName()
	    + " as r "
	    + " where q.resultUid = r.uid and r.assessment.uid = ? and r.user.userId =? and q.assessmentQuestion.uid =? and q.mark < q.assessmentQuestion.defaultGrade";

    public int getNumberWrongAnswersDoneBefore(Long assessmentUid, Long userId, Long questionUid) {
	List list = getHibernateTemplate().find(FIND_WRONG_ANSWERS_NUMBER, new Object[] { assessmentUid, userId, questionUid });
	if (list == null || list.size() == 0) {
	    return 0;
	} else {
	    return ((Number) list.get(0)).intValue();
	}
    }

    public List<AssessmentQuestionResult> getAssessmentQuestionResultList(Long assessmentUid, Long userId, Long questionUid) {
	return getHibernateTemplate().find(FIND_BY_ASSESSMENT_QUESTION_AND_USER, new Object[] { assessmentUid, userId, questionUid });
    }
    
    public AssessmentQuestionResult getAssessmentQuestionResultByUid(Long questionResultUid) {
	List list = getHibernateTemplate().find(FIND_BY_UID, new Object[] { questionResultUid });
	if (list == null || list.size() == 0)
	    return null;
	return (AssessmentQuestionResult) list.get(0);
    }

}
