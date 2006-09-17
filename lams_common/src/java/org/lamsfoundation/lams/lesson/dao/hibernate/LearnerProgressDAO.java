/****************************************************************
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
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.lesson.dao.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.lesson.dao.ILearnerProgressDAO;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of ILessonDAO
 * @author chris
 */
public class LearnerProgressDAO extends HibernateDaoSupport implements ILearnerProgressDAO
{
    private final static String LOAD_PROGRESS_BY_LEARNER = 
        "from LearnerProgress p where p.user.id = :learnerId and p.lesson.id = :lessonId";

    /**
     * Retrieves the Lesson
     * @param lessonId identifies the lesson to get
     * @return the lesson
     */
    public LearnerProgress getLearnerProgress(Long learnerProgressId)
    {
        return (LearnerProgress)getHibernateTemplate().get(LearnerProgress.class, learnerProgressId);
    }
    
    /**
     * Saves or Updates learner progress data.
     * @param learnerProgress holds the learne progress data
     */
    public void saveLearnerProgress(LearnerProgress learnerProgress)
    {
        getHibernateTemplate().save(learnerProgress);
    }
    
    /**
     * Deletes a LearnerProgress data <b>permanently</b>.
     * @param learnerProgress
     */
    public void deleteLearnerProgress(LearnerProgress learnerProgress)
    {
        getHibernateTemplate().delete(learnerProgress);
    }

    /**
     * @see org.lamsfoundation.lams.lesson.dao.ILearnerProgressDAO#getLearnerProgressByLeaner(java.lang.Integer,java.lang.Long)
     */
    public LearnerProgress getLearnerProgressByLearner(final Integer learnerId, final Long lessonId)
    {
        HibernateTemplate hibernateTemplate = new HibernateTemplate(this.getSessionFactory());

        return (LearnerProgress)hibernateTemplate.execute(
             new HibernateCallback() 
             {
                 public Object doInHibernate(Session session) throws HibernateException 
                 {
                     return session.createQuery(LOAD_PROGRESS_BY_LEARNER)
                     			   .setInteger("learnerId",learnerId)
                     			   .setLong("lessonId",lessonId)
                     			   .uniqueResult();
                 }
             }
       );     
    }

    /**
     * @see org.lamsfoundation.lams.lesson.dao.ILearnerProgressDAO#updateLearnerProgress(org.lamsfoundation.lams.lesson.LearnerProgress)
     */
    public void updateLearnerProgress(LearnerProgress learnerProgress)
    {
        this.getHibernateTemplate().update(learnerProgress);
    }
}
