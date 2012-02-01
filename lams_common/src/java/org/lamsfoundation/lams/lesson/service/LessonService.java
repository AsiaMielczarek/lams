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
package org.lamsfoundation.lams.lesson.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.dao.IBaseDAO;
import org.lamsfoundation.lams.index.IndexLessonBean;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.Grouper;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.learningdesign.dao.IGroupingDAO;
import org.lamsfoundation.lams.learningdesign.exception.GroupingException;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.LessonClass;
import org.lamsfoundation.lams.lesson.dao.ILearnerProgressDAO;
import org.lamsfoundation.lams.lesson.dao.ILessonClassDAO;
import org.lamsfoundation.lams.lesson.dao.ILessonDAO;
import org.lamsfoundation.lams.lesson.dto.LessonDTO;
import org.lamsfoundation.lams.lesson.dto.LessonDetailsDTO;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.MessageService;

/**
 * Access the general lesson details.
 * 
 * A lesson has three different "lists" of learners.
 * <OL>
 * <LI>The learners who are in the learner group attached to the lesson. This is fixed when the lesson is started and is
 * a list of all the learners who could ever participate in to the lesson. This is available via lesson.getAllLearners()
 * <LI>The learners who have started the lesson. They may or may not be logged in currently, or if they are logged in
 * they may or may not be doing this lesson. This is available via getActiveLessonLearners().
 * </OL>
 * 
 * There used to be a list of all the learners who were logged into a lesson. This has been removed as we do not need
 * the functionality at present. If this is required later it should be combined with the user's shared session logic
 * and will need to purge users who haven't done anything for a while - otherwise a user whose PC has crashed and then
 * never returns to a lesson will staying in the cache forever.
 * 
 */
public class LessonService implements ILessonService {
    private static Logger log = Logger.getLogger(LessonService.class);

    private ILessonDAO lessonDAO;
    private ILessonClassDAO lessonClassDAO;
    private IGroupingDAO groupingDAO;
    private MessageService messageService;
    private IBaseDAO baseDAO;
    private ILearnerProgressDAO learnerProgressDAO;

    /*  Spring injection methods */
    public void setLessonDAO(ILessonDAO lessonDAO) {
	this.lessonDAO = lessonDAO;
    }

    public void setLessonClassDAO(ILessonClassDAO lessonClassDAO) {
	this.lessonClassDAO = lessonClassDAO;
    }

    public void setGroupingDAO(IGroupingDAO groupingDAO) {
	this.groupingDAO = groupingDAO;
    }

    public void setLearnerProgressDAO(ILearnerProgressDAO learnerProgressDAO) {
	this.learnerProgressDAO = learnerProgressDAO;
    }

    public void setMessageService(MessageService messageService) {
	this.messageService = messageService;
    }

    public void setBaseDAO(IBaseDAO baseDAO) {
	this.baseDAO = baseDAO;
    }

    /*  Service methods */
    /*
     * (non-Javadoc)
     * 
     * @see org.lamsfoundation.lams.lesson.service.ILessonService#getActiveLessonLearners(java.lang.Long)
     */
    public List getActiveLessonLearners(Long lessonId) {
	return lessonDAO.getActiveLearnerByLesson(lessonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lamsfoundation.lams.lesson.service.ILessonService#getActiveLessonLearnersByGroup(java.lang.Long,
     * java.lang.Long)
     */
    public List getActiveLessonLearnersByGroup(Long lessonId, Long groupId) {
	return lessonDAO.getActiveLearnerByLessonAndGroup(lessonId, groupId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lamsfoundation.lams.lesson.service.ILessonService#getCountActiveLessonLearners(java.lang.Long)
     */
    public Integer getCountActiveLessonLearners(Long lessonId) {
	return lessonDAO.getCountActiveLearnerByLesson(lessonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lamsfoundation.lams.lesson.service.ILessonService#getLessonDetails(java.lang.Long)
     */
    public LessonDetailsDTO getLessonDetails(Long lessonId) {
	Lesson lesson = lessonDAO.getLesson(lessonId);
	LessonDetailsDTO dto = null;
	if (lesson != null) {
	    dto = lesson.getLessonDetails();
	    Integer active = getCountActiveLessonLearners(lessonId);
	    dto.setNumberStartedLearners(active != null ? active : new Integer(0));
	}
	return dto;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lamsfoundation.lams.lesson.service.ILessonService#getLessonData(java.lang.Long)
     */
    public LessonDTO getLessonData(Long lessonId) {
	Lesson lesson = lessonDAO.getLesson(lessonId);
	LessonDTO dto = null;
	if (lesson != null) {
	    dto = lesson.getLessonData();
	}
	return dto;
    }

    /**
     * Get the lesson object.
     * 
     * @param lessonId
     * @return lesson details
     */
    public Lesson getLesson(Long lessonId) {
	return lessonDAO.getLesson(lessonId);
    }

    /**
     * If the supplied learner is not already in a group, then perform grouping for the learners who have started the
     * lesson, based on the grouping activity. The grouper decides who goes in which group.
     * 
     * Can only be run on a Random Grouping
     * 
     * @param lessonId
     *            lesson id (mandatory)
     * @param groupingActivity
     *            the activity that has create grouping. (mandatory)
     * @param learner
     *            the learner to be check before grouping. (mandatory)
     */
    public void performGrouping(Long lessonId, GroupingActivity groupingActivity, User learner)
	    throws LessonServiceException {
	Grouping grouping = groupingActivity.getCreateGrouping();
	if (grouping != null && grouping.isRandomGrouping()) {
	    // get the real objects, not the CGLIB version
	    grouping = groupingDAO.getGroupingById(grouping.getGroupingId());
	    Grouper grouper = grouping.getGrouper();

	    if (grouper != null) {
		grouper.setCommonMessageService(messageService);
		try {
		    if (grouping.getGroups().size() == 0) {
			// no grouping done yet - do everyone already in the lesson.
			List usersInLesson = getActiveLessonLearners(lessonId);
			grouper.doGrouping(grouping, (String) null, usersInLesson);
		    } else if (!grouping.doesLearnerExist(learner)) {
			// done the others, just do the one user
			grouper.doGrouping(grouping, null, learner);
		    }
		} catch (GroupingException e) {
		    throw new LessonServiceException(e);
		}
		groupingDAO.update(grouping);
	    }

	} else {
	    String error = "The method performGrouping supports only grouping methods where the grouper decides the groups (currently only RandomGrouping). Called with a groupingActivity with the wrong grouper "
		    + groupingActivity.getActivityId();
	    LessonService.log.error(error);
	    throw new LessonServiceException(error);
	}
    }

    /**
     * Perform the grouping, setting the given list of learners as one group.
     * 
     * @param groupingActivity
     *            the activity that has create grouping. (mandatory)
     * @param groupName
     *            (optional)
     * @param learners
     *            to form one group (mandatory)
     */
    public void performGrouping(GroupingActivity groupingActivity, String groupName, List learners)
	    throws LessonServiceException {

	Grouping grouping = groupingActivity.getCreateGrouping();
	performGrouping(grouping, groupName, learners);
    }

    /**
     * Perform the grouping, setting the given list of learners as one group. Used in situations where there is a
     * grouping but no grouping activity (e.g. in branching).
     * 
     * @param groupingActivity
     *            the activity that has create grouping. (mandatory)
     * @param groupName
     *            (optional)
     * @param learners
     *            to form one group (mandatory)
     */
    public void performGrouping(Grouping grouping, String groupName, List learners) throws LessonServiceException {

	if (grouping != null) {
	    // Ensure we have a real grouping object, not just a CGLIB version (LDEV-1817)
	    grouping = groupingDAO.getGroupingById(grouping.getGroupingId());
	    Grouper grouper = grouping.getGrouper();
	    if (grouper != null) {
		grouper.setCommonMessageService(messageService);
		try {
		    grouper.doGrouping(grouping, groupName, learners);
		} catch (GroupingException e) {
		    throw new LessonServiceException(e);
		}
		groupingDAO.update(grouping);
	    }
	}
    }

    /**
     * Perform grouping for the given learner.
     * 
     * @param grouping
     *            the object on which to perform the grouing. (mandatory)
     * @param groupId
     *            group id (mandatory)
     * @param learner
     *            learner to group (mandatory)
     * @throws LessonServiceException
     */
    public void performGrouping(Grouping grouping, Long groupId, User learner) throws LessonServiceException {
	if (grouping != null) {
	    // Ensure we have a real grouping object, not just a CGLIB version (LDEV-1817)
	    grouping = groupingDAO.getGroupingById(grouping.getGroupingId());
	    Grouper grouper = grouping.getGrouper();
	    if (grouper != null) {
		grouper.setCommonMessageService(messageService);
		try {
		    List<User> learners = new ArrayList<User>(1);
		    learners.add(learner);
		    grouper.doGrouping(grouping, groupId, learners);
		} catch (GroupingException e) {
		    throw new LessonServiceException(e);
		}
		groupingDAO.update(grouping);
	    }
	}
    }

    /**
     * Perform the grouping, setting the given list of learners as one group. Currently used for chosen grouping and
     * teacher chosen branching, and for group based branching in preview (when the user selects a branch that would not
     * be their normal branch).
     * 
     * @param grouping
     *            The grouping that needs to have the grouping performed.. (mandatory)
     * @param the
     *            id of the preferred group (optional)
     * @param learners
     *            to form one group (mandatory)
     */
    public void performGrouping(Grouping grouping, Long groupId, List learners) throws LessonServiceException {
	if (grouping != null) {
	    Grouper grouper = grouping.getGrouper();
	    if (grouper != null) {
		grouper.setCommonMessageService(messageService);
		try {
		    grouper.doGrouping(grouping, groupId, learners);
		} catch (GroupingException e) {
		    throw new LessonServiceException(e);
		}
		groupingDAO.update(grouping);
	    }
	} else {
	    String error = "The method performChosenGrouping supports only grouping methods where the supplied list should be used as a single group (currently only ChosenGrouping). Called with a grouping with the wrong grouper "
		    + grouping;
	    LessonService.log.error(error);
	    throw new LessonServiceException(error);
	}
    }

    /**
     * Remove learners from the given group.
     * 
     * @param grouping
     *            the grouping that contains the users to be removed (mandatory)
     * @param groupID
     *            if not null only remove user from this group, if null remove learner from any group.
     * @param learners
     *            the learners to be removed (mandatory)
     */
    public void removeLearnersFromGroup(Grouping grouping, Long groupID, List<User> learners)
	    throws LessonServiceException {
	if (grouping != null) {
	    // get the real objects, not the CGLIB version
	    grouping = groupingDAO.getGroupingById(grouping.getGroupingId());
	    Grouper grouper = grouping.getGrouper();
	    if (grouper != null) {
		try {
		    grouper.removeLearnersFromGroup(grouping, groupID, learners);
		} catch (GroupingException e) {
		    throw new LessonServiceException(e);
		}
	    }
	    groupingDAO.update(grouping);
	}
    }

    /**
     * Create an empty group for the given grouping. Create an empty group for the given grouping. If the group name
     * already exists then it will force the name to be unique.
     * 
     * @param grouping
     *            the grouping. (mandatory)
     * @param groupName
     *            (mandatory)
     * @return the new group
     */
    public Group createGroup(Grouping grouping, String name) throws LessonServiceException {
	Group newGroup = null;
	if (grouping != null) {
	    // get the real objects, not the CGLIB version
	    grouping = groupingDAO.getGroupingById(grouping.getGroupingId());
	    Grouper grouper = grouping.getGrouper();
	    if (grouper != null) {
		try {
		    newGroup = grouper.createGroup(grouping, name);
		} catch (GroupingException e) {
		    throw new LessonServiceException(e);
		}
	    }
	    groupingDAO.update(grouping);
	}
	return newGroup;
    }

    /**
     * Remove a group for the given grouping. If the group is already used (e.g. a tool session exists) then it throws a
     * GroupingException.
     * 
     * @param grouping
     *            the grouping that contains the group to be removed (mandatory)
     * @param groupID
     *            (mandatory)
     */
    public void removeGroup(Grouping grouping, Long groupID) throws LessonServiceException {
	if (grouping != null) {
	    // get the real objects, not the CGLIB version
	    grouping = groupingDAO.getGroupingById(grouping.getGroupingId());
	    Grouper grouper = grouping.getGrouper();
	    if (grouper != null) {
		try {
		    grouper.removeGroup(grouping, groupID);
		} catch (GroupingException e) {
		    throw new LessonServiceException(e);
		}
	    }
	    groupingDAO.update(grouping);
	}
    }

    /**
     * Add a learner to the lesson class. Checks for duplicates.
     * 
     * @param userId
     *            new learner id
     * @return true if added user, returns false if the user already a learner and hence not added.
     */
    public boolean addLearner(Long lessonId, Integer userId) throws LessonServiceException {
	Lesson lesson = lessonDAO.getLesson(lessonId);
	if (lesson == null) {
	    throw new LessonServiceException("Lesson " + lessonId + " does not exist. Unable to add learner to lesson.");
	}

	LessonClass lessonClass = lesson.getLessonClass();
	if (lessonClass == null) {
	    throw new LessonServiceException("Lesson class for " + lessonId
		    + " does not exist. Unable to add learner to lesson.");
	}

	// initialise the lesson group, or we get a lazy loading error when logging in
	// from moodle. Should only be two groups - learner and staff
	// yes this is a bit of a hack!
	Group learnersGroup = lessonClass.getLearnersGroup();
	if (learnersGroup != null) {
	    lessonDAO.initialize(learnersGroup);
	}

	User user = (User) baseDAO.find(User.class, userId);
	boolean ret = lessonClass.addLearner(user);
	if (ret) {
	    lessonClassDAO.updateLessonClass(lessonClass);
	}
	return ret;
    }

    /**
     * Add a set of learners to the lesson class.
     * 
     * If version of the method is designed to be called from Moodle or some other external system, and is less
     * efficient in that it has to look up the user from the user id. If we don't do this, then we may get a a session
     * closed issue if this code is called from the LoginRequestValve (as the users will be from a previous session)
     * 
     * @param lessonId
     * @param userIds
     *            array of new learner ids
     */
    public void addLearners(Long lessonId, Integer[] userIds) throws LessonServiceException {

	Lesson lesson = lessonDAO.getLesson(lessonId);
	if (lesson == null) {
	    throw new LessonServiceException("Lesson " + lessonId + " does not exist. Unable to add learner to lesson.");
	}
	LessonClass lessonClass = lesson.getLessonClass();
	if (lessonClass == null) {
	    throw new LessonServiceException("Lesson class for " + lessonId
		    + " does not exist. Unable to add learner to lesson.");
	}

	// initialise the lesson group, or we might get a lazy loading error in the future
	// when logging in from an external system. Should only be two groups - learner and staff
	// yes this is a bit of a hack!
	Group learnersGroup = lessonClass.getLearnersGroup();
	if (learnersGroup != null) {
	    lessonDAO.initialize(learnersGroup);
	}

	Set<User> users = new HashSet<User>();
	for (Integer userId : userIds) {
	    User user = (User) baseDAO.find(User.class, userId);
	    users.add(user);
	}
	addLearners(lesson, users);
    }

    /**
     * Add a set of learners to the lesson class. To be called within LAMS - see addLearners(Long lessonId, Integer[]
     * userIds) if calling from an external system.
     * 
     * @param lesson
     *            lesson
     * @param users
     *            the users to add as learners
     */
    public void addLearners(Lesson lesson, Collection<User> users) throws LessonServiceException {

	LessonClass lessonClass = lesson.getLessonClass();
	int numAdded = lessonClass.addLearners(users);
	if (numAdded > 0) {
	    lessonClassDAO.updateLessonClass(lessonClass);
	}
	if (LessonService.log.isDebugEnabled()) {
	    LessonService.log.debug("Added " + numAdded + " learners to lessonClass " + lessonClass.getGroupingId());
	}
    }
    
    /**
     * Set the learners in a lesson class. To be called within LAMS.
     * 
     * @param lesson
     *            lesson
     * @param users
     *            the users to set as learners
     */
    public void setLearners(Lesson lesson, Collection<User> users) throws LessonServiceException {
	LessonClass lessonClass = lesson.getLessonClass();
	int numberOfLearners = lessonClass.setLearners(users);
	lessonClassDAO.updateLessonClass(lessonClass);
	if (LessonService.log.isDebugEnabled()) {
	    LessonService.log.debug("Set " + numberOfLearners + " learners in lessonClass "
		    + lessonClass.getGroupingId());
	}
    }

    /**
     * Add a new staff member to the lesson class. Checks for duplicates.
     * 
     * @param userId
     *            new learner id
     * @return true if added user, returns false if the user already a staff member and hence not added.
     */
    public boolean addStaffMember(Long lessonId, Integer userId) {
	Lesson lesson = lessonDAO.getLesson(lessonId);
	if (lesson == null) {
	    throw new LessonServiceException("Lesson " + lessonId
		    + " does not exist. Unable to add staff member to lesson.");
	}

	LessonClass lessonClass = lesson.getLessonClass();

	if (lessonClass == null) {
	    throw new LessonServiceException("Lesson class for " + lessonId
		    + " does not exist. Unable to add staff member to lesson.");
	}

	lessonDAO.initialize(lessonClass.getStaffGroup());
	User user = (User) baseDAO.find(User.class, userId);

	boolean ret = lessonClass.addStaffMember(user);
	if (ret) {
	    lessonClassDAO.updateLessonClass(lessonClass);
	}
	return ret;
    }

    /**
     * Add a set of staff to the lesson class.
     * 
     * If version of the method is designed to be called from Moodle or some other external system, and is less
     * efficient in that it has to look up the user from the user id. If we don't do this, then we may get a a session
     * closed issue if this code is called from the LoginRequestValve (as the users will be from a previous session)
     * 
     * @param lessonId
     * @param userIds
     *            array of new staff ids
     */
    public void addStaffMembers(Long lessonId, Integer[] userIds) throws LessonServiceException {

	Lesson lesson = lessonDAO.getLesson(lessonId);
	if (lesson == null) {
	    throw new LessonServiceException("Lesson " + lessonId + " does not exist. Unable to add learner to lesson.");
	}
	LessonClass lessonClass = lesson.getLessonClass();
	if (lessonClass == null) {
	    throw new LessonServiceException("Lesson class for " + lessonId
		    + " does not exist. Unable to add learner to lesson.");
	}

	// initialise the lesson group, or we might get a lazy loading error in the future
	// when logging in from an external system. Should only be two groups - learner and staff
	// yes this is a bit of a hack!
	lessonDAO.initialize(lessonClass.getStaffGroup());

	Set<User> users = new HashSet<User>();
	for (Integer userId : userIds) {
	    User user = (User) baseDAO.find(User.class, userId);
	    users.add(user);
	}
	addStaffMembers(lesson, users);
    }

    /**
     * Add a set of staff members to the lesson class. To be called within LAMS - see addLearners(Long lessonId,
     * Integer[] userIds) if calling from an external system.
     * 
     * @param lesson
     *            lesson
     * @param users
     *            the users to add as learners
     */
    public void addStaffMembers(Lesson lesson, Collection<User> users) throws LessonServiceException {

	LessonClass lessonClass = lesson.getLessonClass();
	int numAdded = lessonClass.addStaffMembers(users);
	if (numAdded > 0) {
	    lessonClassDAO.updateLessonClass(lessonClass);
	}
	if (LessonService.log.isDebugEnabled()) {
	    LessonService.log.debug("Added " + numAdded + " staff members to lessonClass "
		    + lessonClass.getGroupingId());
	}
    }
    
    /**
     * Set the staff members in a lesson class. To be called within LAMS.
     * 
     * @param lesson
     *            lesson
     * @param users
     *            the users to set as staff
     */
    public void setStaffMembers(Lesson lesson, Collection<User> users) throws LessonServiceException {

	LessonClass lessonClass = lesson.getLessonClass();
	int numberOfStaff = lessonClass.setStaffMembers(users);
	lessonClassDAO.updateLessonClass(lessonClass);
	if (LessonService.log.isDebugEnabled()) {
	    LessonService.log.debug("Set " + numberOfStaff + " staff members in lessonClass "
		    + lessonClass.getGroupingId());
	}
    }

    /**
     * Remove references to an activity from all learner progress entries. Used by Live Edit, to remove any references
     * to the system gates
     * 
     * @param activity
     *            The activity for which learner progress references should be removed.
     */
    public void removeProgressReferencesToActivity(Activity activity) throws LessonServiceException {
	if (activity != null) {
	    LessonService.log.debug("Processing learner progress for activity " + activity.getActivityId());

	    List progresses = learnerProgressDAO.getLearnerProgressReferringToActivity(activity);
	    if (progresses != null && progresses.size() > 0) {
		Iterator iter = progresses.iterator();
		while (iter.hasNext()) {
		    LearnerProgress progress = (LearnerProgress) iter.next();
		    if (removeActivityReference(activity, progress)) {
			;
		    }
		    learnerProgressDAO.updateLearnerProgress(progress);
		}
	    }
	}
    }

    private boolean removeActivityReference(Activity activity, LearnerProgress progress) {

	if (LessonService.log.isDebugEnabled()) {
	    LessonService.log.debug("Processing learner progress learner " + progress.getUser().getUserId());
	}

	boolean recordUpdated = false;

	boolean removed = (progress.getAttemptedActivities().remove(activity) != null);
	if (removed) {
	    recordUpdated = true;
	    LessonService.log.debug("Removed activity from attempted activities");
	}

	removed = (progress.getCompletedActivities().remove(activity) != null);
	if (removed) {
	    recordUpdated = true;
	    LessonService.log.debug("Removed activity from completed activities");
	}

	if (progress.getCurrentActivity() != null && progress.getCurrentActivity().equals(activity)) {
	    progress.setCurrentActivity(null);
	    recordUpdated = true;
	    LessonService.log.debug("Removed activity as current activity");
	}

	if (progress.getNextActivity() != null && progress.getNextActivity().equals(activity)) {
	    progress.setNextActivity(null);
	    recordUpdated = true;
	    LessonService.log.debug("Removed activity as next activity");
	}

	if (progress.getPreviousActivity() != null && progress.getPreviousActivity().equals(activity)) {
	    progress.setPreviousActivity(null);
	    recordUpdated = true;
	    LessonService.log.debug("Removed activity as previous activity");
	}

	return recordUpdated;
    }

    /**
     * Mark any learner progresses for this lesson as not completed. Called when Live Edit ends, to ensure that if there
     * were any completed progress records, and the design was extended, then they are no longer marked as completed.
     * 
     * @param lessonId
     *            The lesson for which learner progress entries should be updated.
     */
    public void performMarkLessonUncompleted(Long lessonId) throws LessonServiceException {
	int count = 0;
	if (lessonId != null) {
	    LessonService.log.debug("Setting learner progress to uncompleted for lesson " + lessonId);

	    List progresses = learnerProgressDAO.getCompletedLearnerProgressForLesson(lessonId);
	    if (progresses != null && progresses.size() > 0) {
		Iterator iter = progresses.iterator();
		while (iter.hasNext()) {
		    LearnerProgress progress = (LearnerProgress) iter.next();
		    if (progress.getLessonComplete() == LearnerProgress.LESSON_END_OF_DESIGN_COMPLETE) {
			progress.setLessonComplete(LearnerProgress.LESSON_NOT_COMPLETE);
		    }
		    count++;
		}
	    }
	}
	if (LessonService.log.isDebugEnabled()) {
	    LessonService.log.debug("Reset completed flag for " + count + " learners for lesson " + lessonId);
	}
    }

    /**
     * Get the list of users who have attempted an activity. This is based on the progress engine records. This will
     * give the users in all tool sessions for an activity (if it is a tool activity) or it will give all the users who
     * have attempted an activity that doesn't have any tool sessions, i.e. system activities such as branching.
     */
    public List<User> getLearnersHaveAttemptedActivity(Activity activity) throws LessonServiceException {
	return learnerProgressDAO.getLearnersHaveAttemptedActivity(activity);
    }

    /**
     * Get the list of users who have completed an activity. This is based on the progress engine records. This will
     * give the users in all tool sessions for an activity (if it is a tool activity) or it will give all the users who
     * have attempted an activity that doesn't have any tool sessions, i.e. system activities such as branching.
     */
    public List<User> getLearnersHaveCompletedActivity(Activity activity) throws LessonServiceException {
	return learnerProgressDAO.getLearnersHaveCompletedActivity(activity);
    }

    /**
     * Gets the count of the users who have attempted an activity. This is based on the progress engine records. This
     * will work on all activities, including ones that don't have any tool sessions, i.e. system activities such as
     * branching.
     */
    public Integer getCountLearnersHaveAttemptedActivity(Activity activity) throws LessonServiceException {
	return learnerProgressDAO.getNumUsersAttemptedActivity(activity);
    }

    /**
     * Gets the count of the users who have completed an activity. This is based on the progress engine records. This
     * will work on all activities, including ones that don't have any tool sessions, i.e. system activities such as
     * branching.
     */
    public Integer getCountLearnersHaveCompletedActivity(Activity activity) throws LessonServiceException {
	return learnerProgressDAO.getNumUsersCompletedActivity(activity);
    }

    public Map<Long, IndexLessonBean> getLessonsByOrgAndUserWithCompletedFlag(Integer userId, Integer orgId,
	    Integer userRole) {
	TreeMap<Long, IndexLessonBean> map = new TreeMap<Long, IndexLessonBean>();
	List list = lessonDAO.getLessonsByOrgAndUserWithCompletedFlag(userId, orgId, userRole);
	if (list != null) {
	    Iterator iterator = list.iterator();
	    while (iterator.hasNext()) {
		Object[] tuple = (Object[]) iterator.next();
		Long lessonId = (Long) tuple[0];
		String lessonName = (String) tuple[1];
		String lessonDescription = (String) tuple[2];
		Integer lessonState = (Integer) tuple[3];
		Boolean lessonCompleted = (Boolean) tuple[4];
		lessonCompleted = lessonCompleted == null ? false : lessonCompleted.booleanValue();
		Boolean enableLessonNotifications = (Boolean) tuple[5];
		enableLessonNotifications = enableLessonNotifications == null ? false : enableLessonNotifications.booleanValue();
		Boolean dependent = (Boolean) tuple[6];
		dependent = dependent == null ? false : dependent.booleanValue();
		IndexLessonBean bean = new IndexLessonBean(lessonId, lessonName, lessonDescription, lessonState,
			lessonCompleted, enableLessonNotifications, dependent);
		map.put(new Long(lessonId), bean);
	    }
	}
	return map;
    }

    public List<Lesson> getLessonsByGroupAndUser(Integer userId, Integer organisationId) {
	List<Lesson> list = lessonDAO.getLessonsByGroupAndUser(userId, organisationId);
	return list;
    }
    
    public List<Lesson> getLessonsByGroup(Integer organisationId) {
	List<Lesson> list = lessonDAO.getLessonsByGroup(organisationId);
	return list;
    }

    /**
     * Gets the learner's progress details for a particular lesson. Will return null if the user has not started the
     * lesson.
     * 
     * @param learnerId
     *            user's id
     * @param lessonId
     *            lesson's id
     * @return learner's progress or null
     */
    public LearnerProgress getUserProgressForLesson(Integer learnerId, Long lessonId) {
	return learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);
    }

    /**
     * Gets list of lessons which are originally based on the given learning design id.
     */
    public List getLessonsByOriginalLearningDesign(Long ldId, Integer orgId) {
	return lessonDAO.getLessonsByOriginalLearningDesign(ldId, orgId);
    }

    public List<User> getMonitorsByToolSessionId(Long sessionId) {
	return lessonDAO.getMonitorsByToolSessionId(sessionId);
    }

    public List<Lesson> getActiveLessonsForLearner(Integer learnerId, Integer organisationId) {
	return lessonDAO.getActiveLessonsForLearner(learnerId, organisationId);
    }
    
    /**
     * @see org.lamsfoundation.lams.lesson.service.ILessonService#getLessonDetailsFromSessionID(java.lang.Long)
     */
    public LessonDetailsDTO getLessonDetailsFromSessionID(Long toolSessionID) {
	Lesson lesson = this.lessonDAO.getLessonFromSessionID(toolSessionID);
	if (lesson != null) {
	    return lesson.getLessonDetails(); 
	}
	return null;
    }
    
    /**
     * Check if preceding lessons have been completed and the given lesson is available to the user.
     */
    public boolean checkLessonReleaseConditions(Long lessonId, Integer learnerId) {
	Lesson lesson = getLesson(lessonId);
	if (lesson != null) {
	    for (Lesson precedingLesson : lesson.getPrecedingLessons()) {
		LearnerProgress progress = getUserProgressForLesson(learnerId, precedingLesson.getLessonId());
		if (progress == null || !progress.isComplete()) {
		    return false;
		}
	    }
	}
	return true;
    }
    
    /**
     * Find lessons which just got available after the given lesson has been completed.
     */
    public Set<Lesson> getReleasedSucceedingLessons(Long completedLessonId, Integer learnerId) {
	Set<Lesson> releasedSucceedingLessons = new HashSet<Lesson>();
	Lesson lesson = getLesson(completedLessonId);
	if (lesson != null) {
	    for (Lesson succeedingLesson : lesson.getSucceedingLessons()) {
		if (checkLessonReleaseConditions(succeedingLesson.getLessonId(), learnerId)) {
		    releasedSucceedingLessons.add(succeedingLesson);
		}
	    }
	}
	return releasedSucceedingLessons;
    }
}
