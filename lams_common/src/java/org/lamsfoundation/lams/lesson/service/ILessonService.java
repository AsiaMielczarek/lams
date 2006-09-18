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
/* $Id$ */

package org.lamsfoundation.lams.lesson.service;

import java.util.Collection;
import java.util.List;

import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.dto.LessonDTO;
import org.lamsfoundation.lams.lesson.dto.LessonDetailsDTO;
import org.lamsfoundation.lams.usermanagement.User;

/**
 * Access the general lesson details and access to grouping.
 * 
 * A lesson has two different "lists" of learners.
 * <OL>
 * <LI>The learners who are in the learner group attached to the lesson. This is fixed 
 * when the lesson is started and is a list of all the learners who could ever participate in
 * to the lesson. This is available via lesson.getAllLearners()
 * <LI>The learners who have started the lesson. They may or may not be logged in currently,
 * or if they are logged in they may or may not be doing this lesson. This is available
 * via getActiveLessonLearners().
 * </OL>
 * 
 * There used to be a list of all the learners who were logged into a lesson. This has been
 * removed as we do not need the functionality at present. If this is required later it should
 * be combined with the user's shared session logic and will need to purge users who haven't
 * done anything for a while - otherwise a user whose PC has crashed and then never returns
 * to a lesson will staying in the cache forever.
 */
public interface ILessonService {

	/** Get all the learners who have started the lesson. They may not be currently online.*/
	public abstract List getActiveLessonLearners(Long lessonId);

	/** 
	 * Get the count of all the learners who have started the lesson. They may not be currently online.
	 */
	public Integer getCountActiveLessonLearners(Long lessonId);
    
	/** Get the lesson details for the LAMS client. Suitable for the monitoring client.
	 * Contains a count of the total number of learners in the lesson and the number of active learners.
	 * This is a pretty intensive call as it counts all the learners in the 
	 * lessons' learner group, and determines the number of active learners.
	 * @param lessonId
	 * @return lesson details
	 */
	public abstract LessonDetailsDTO getLessonDetails(Long lessonId);

	/** Get the lesson details for the LAMS client. Suitable for the learner client.
	 * Contains a reduced number of fields compared to getLessonDetails.
	 * @param lessonId
	 * @return lesson details
	 */
	public abstract LessonDTO getLessonData(Long lessonId);

	/**
     * If the supplied learner is not already in a group, then perform grouping for 
     * the learners who have started the lesson, based on the grouping activity. 
     * Currently used for random grouping.
     * This method should be used when we do have an grouping activity and learner that is 
     * already part of the Hibernate session. (e.g. from the ForceComplete)
     * 
     * @param lessonId lesson id (mandatory)
     * @param groupingActivity the activity that has create grouping. (mandatory)
     * @param learner the learner to be check before grouping. (mandatory)
     */
    public void performGrouping(Long lessonId, GroupingActivity groupingActivity, User learner) throws LessonServiceException;

	/**
     * Perform grouping for all the learners who have started the lesson, based on the grouping activity. 
     * Currently used for chosen grouping.
     * @param lessonId lesson id (mandatory)
     * @param groupName group name (mandatory)
     * @param groupingActivityId the activity that has create grouping. (mandatory)
     */
    public void performGrouping(GroupingActivity groupingActivity, String groupName, List learners) throws LessonServiceException;

   /**
    * Perform grouping for all the learners who have started the lesson, based on the grouping activity. 
    * Currently used for chosen grouping.
    * @param lessonId lesson id (mandatory)
    * @param groupId group id (mandatory)
    * @param groupingActivityId the activity that has create grouping. (mandatory)
    */
   public void performGrouping(GroupingActivity groupingActivity, Long groupId, List learners) throws LessonServiceException;

    /**
     * Remove learners from the given group. 
     * @param groupingActivity the activity that has create grouping. (mandatory)
     * @param groupName if not null only remove user from this group, if null remove learner from any group.
     * @param learners the learners to be removed (mandatory)
     */
    public void removeLearnersFromGroup(GroupingActivity groupingActivity, Long groupId, List<User> learners) throws LessonServiceException;
    
    
    /** Create an empty group for the given grouping. If the group name is not supplied
     * or the group name already exists then nothing happens.
     * 
     * @param groupingActivity the activity that has create grouping. (mandatory)
     * @param groupName (mandatory)
     */
    public void createGroup(GroupingActivity groupingActivity, String name) throws LessonServiceException; 

    /** 
     * Remove a group for the given grouping. If the group is already used (e.g. a tool session exists)
     * then it throws a GroupingException.
     *  
     * @param groupingActivity the activity that has create grouping. (mandatory)
     * @param groupName (mandatory)
     */
    public void removeGroup(GroupingActivity groupingActivity, Long groupId) throws LessonServiceException;
    
    /** 
     * Add a learner to the lesson class. Checks for duplicates.
     * @paran user new learner 
     * @return true if added user, returns false if the user already a learner and hence not added.
     */ 
    public boolean addLearner(Long lessonId, User user);

    /** 
     * Add a new staff member to the lesson class. Checks for duplicates.
     * @paran user new learner 
     * @return true if added user, returns false if the user already a staff member and hence not added.
     */ 
    public boolean addStaffMember(Long lessonId, User user);
 	
}