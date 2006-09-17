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
package org.lamsfoundation.lams.lesson.dao;

import org.lamsfoundation.lams.lesson.LearnerProgress;

/**
 * Inteface defines Lesson DAO Methods
 * @author chris
 */
public interface ILearnerProgressDAO
{
    
    /**
     * Retrieves the Lesson
     * @param lessonId identifies the lesson to get
     * @return the lesson
     */
    public LearnerProgress getLearnerProgress(Long learnerProgressId);
    
    /**
     * Retrieves the learner progress object for user in a lesson.
     * @param learnerId the user who owns the learner progress data.
     * @param lessonId the lesson for which the progress data applies
     * @return the user's progress data
     */
    public LearnerProgress getLearnerProgressByLearner(final Integer learnerId, final Long lessonId);
        
    /**
     * Saves or Updates learner progress data.
     * @param learnerProgress holds the learne progress data
     */
    public void saveLearnerProgress(LearnerProgress learnerProgress);
    
    /**
     * Deletes a LearnerProgress data <b>permanently</b>.
     * @param learnerProgress 
     */
    public void deleteLearnerProgress(LearnerProgress learnerProgress);
    
    /**
     * Update learner progress data.
     * @param learnerProgress
     */
    public void updateLearnerProgress(LearnerProgress learnerProgress);
}
