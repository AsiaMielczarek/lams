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
package org.lamsfoundation.lams.learning.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.learning.progress.ProgressEngine;
import org.lamsfoundation.lams.learning.progress.ProgressException;
import org.lamsfoundation.lams.learning.web.util.ActivityMapping;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.BranchActivityEntry;
import org.lamsfoundation.lams.learningdesign.BranchCondition;
import org.lamsfoundation.lams.learningdesign.BranchingActivity;
import org.lamsfoundation.lams.learningdesign.GateActivity;
import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.learningdesign.SequenceActivity;
import org.lamsfoundation.lams.learningdesign.ToolActivity;
import org.lamsfoundation.lams.learningdesign.ToolBranchingActivity;
import org.lamsfoundation.lams.learningdesign.dao.IActivityDAO;
import org.lamsfoundation.lams.learningdesign.dao.IGroupingDAO;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.dao.ILearnerProgressDAO;
import org.lamsfoundation.lams.lesson.dao.ILessonDAO;
import org.lamsfoundation.lams.lesson.dto.LearnerProgressDTO;
import org.lamsfoundation.lams.lesson.dto.LessonDTO;
import org.lamsfoundation.lams.lesson.service.ILessonService;
import org.lamsfoundation.lams.lesson.service.LessonServiceException;
import org.lamsfoundation.lams.tool.ToolOutput;
import org.lamsfoundation.lams.tool.ToolSession;
import org.lamsfoundation.lams.tool.dao.IToolSessionDAO;
import org.lamsfoundation.lams.tool.exception.LamsToolServiceException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.service.ILamsCoreToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.web.IndexLessonBean;
/**
 * This class is a facade over the Learning middle tier.
 * @author chris, Jacky Fang
 */
public class LearnerService implements ICoreLearnerService
{
    //---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	private static Logger log = Logger.getLogger(LearnerService.class);
    
    private ILearnerProgressDAO learnerProgressDAO;
    private ILessonDAO lessonDAO;
    private IActivityDAO activityDAO;
    private IGroupingDAO groupingDAO;
    private ProgressEngine progressEngine;
    private IToolSessionDAO toolSessionDAO;
    private ILamsCoreToolService lamsCoreToolService;
    private ActivityMapping activityMapping;
    private IUserManagementService userManagementService;
    private ILessonService lessonService;
    protected MessageService messageService;
    

    //---------------------------------------------------------------------
    // Inversion of Control Methods - Constructor injection
    //---------------------------------------------------------------------
    
    /** Creates a new instance of LearnerService*/
    public LearnerService(ProgressEngine progressEngine) {
        this.progressEngine = progressEngine;
    }

    /** Creates a new instance of LearnerService. To be used by Spring, assuming the Spring 
     * will set up the progress engine via method injection. If you are creating the bean manually
     * then use the other constructor. */
    public LearnerService() {
    }

    //---------------------------------------------------------------------
    // Inversion of Control Methods - Method injection
    //---------------------------------------------------------------------
	/**
	 * Set i18n MessageService
	 */
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

    public MessageService getMessageService() {
		return messageService;
	}
	/**
     * @param toolSessionDAO The toolSessionDAO to set.
     */
    public void setToolSessionDAO(IToolSessionDAO toolSessionDAO)
    {
        this.toolSessionDAO = toolSessionDAO;
    }
    
    /**
     * @param lessonDAO The lessonDAO to set.
     */
	public void setLessonDAO(ILessonDAO lessonDAO) 
	{
		this.lessonDAO = lessonDAO;
	}

    /**
     * @param learnerProgressDAO The learnerProgressDAO to set.
     */
    public void setLearnerProgressDAO(ILearnerProgressDAO learnerProgressDAO)
    {
        this.learnerProgressDAO = learnerProgressDAO;
    }

    /**
     * @param lamsToolService The lamsToolService to set.
     */
    public void setLamsCoreToolService(ILamsCoreToolService lamsToolService)
    {
        this.lamsCoreToolService = lamsToolService;
    }
    
    public void setActivityMapping(ActivityMapping activityMapping) {
		this.activityMapping = activityMapping;
	}
    
    /**
     * @param activityDAO The activityDAO to set.
     */
    public void setActivityDAO(IActivityDAO activityDAO)
    {
        this.activityDAO = activityDAO;
    }
    
    /**
     * @param groupingDAO The groupingDAO to set.
     */
    public void setGroupingDAO(IGroupingDAO groupingDAO)
    {
        this.groupingDAO = groupingDAO;
    }
    /**
     * @return the User Management Service
     */
	public IUserManagementService getUserManagementService() {
		return userManagementService;
	}
	/**
	 * @param userService User Management Service
	 */
	public void setUserManagementService(IUserManagementService userService) {
		this.userManagementService = userService;
	}
	
	public void setLessonService(ILessonService lessonService) {
		this.lessonService = lessonService;
	}

	
    //---------------------------------------------------------------------
    // Service Methods
    //---------------------------------------------------------------------
    /**
     * Delegate to lesson dao to load up the lessons.
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getActiveLessonsFor(org.lamsfoundation.lams.usermanagement.User)
     */
    public LessonDTO[] getActiveLessonsFor(Integer learnerId)
    {
    	User learner = (User)userManagementService.findById(User.class,learnerId);
        List activeLessons = this.lessonDAO.getActiveLessonsForLearner(learner);
        return getLessonDataFor(activeLessons);
    }
    
    public Lesson getLesson(Long lessonId)
    {
        return lessonDAO.getLesson(lessonId);
    }
    
    /**
     * Get the lesson data for a particular lesson. In a DTO format suitable for sending to the client.
     */
    public LessonDTO getLessonData(Long lessonId)
    {
    	Lesson lesson = getLesson(lessonId);
    	return ( lesson != null ? lesson.getLessonData() : null );
    }
 
    /**
     * <p>Joins a User to a lesson as a learner. It could either be a new lesson
     * or a lesson that has been started.</p>
     * 
     * <p>In terms of new lesson, a new learner progress would be initialized.
     * Tool session for the next activity will be initialized if necessary.</p>
     * 
     * <p>In terms of an started lesson, the learner progress will be returned
     * without calculation. Tool session will be initialized if necessary.
     * Note that we won't initialize tool session for current activity because
     * we assume tool session will always initialize before it becomes a 
     * current activity.</p
     * 
     * 
     * @param learnerId the Learner's userID
     * @param lessionID identifies the Lesson to start
     * @throws LamsToolServiceException
     * @throws LearnerServiceException in case of problems.
     */
    public LearnerProgress joinLesson(Integer learnerId, Long lessonID)  
    {
    	User learner = (User)userManagementService.findById(User.class,learnerId);
    	
    	Lesson lesson = getLesson(lessonID);
    	
    	if ( lesson == null || ! lesson.isLessonStarted() ) {
			log.error("joinLesson: Learner "+learner.getLogin()+" joining lesson "+lesson+" but lesson has not started");
			throw new LearnerServiceException("Cannot join lesson as lesson has not started");
    	}
    		
        LearnerProgress learnerProgress = learnerProgressDAO.getLearnerProgressByLearner(learner.getUserId(),lessonID);
    	
        if(learnerProgress==null)
        {
            //create a new learner progress for new learner
            learnerProgress = new LearnerProgress(learner,lesson);
            
            try
            {
                progressEngine.setUpStartPoint(learnerProgress);
            }
            catch (ProgressException e)
            {
                log.error("error occurred in 'setUpStartPoint':"+e.getMessage());
        		throw new LearnerServiceException(e.getMessage());
            }
        	//Use TimeStamp rather than Date directly to keep consistent with Hibnerate persiste object.
        	learnerProgress.setStartDate(new Timestamp(new Date().getTime()));
            learnerProgressDAO.saveLearnerProgress(learnerProgress);
        } else {
        	
        	Activity currentActivity = learnerProgress.getCurrentActivity();
        	if ( currentActivity == null ) {
               	// something may have gone wrong and we need to recalculate the current activity
        		try
                {
                    progressEngine.setUpStartPoint(learnerProgress);
                }
                catch (ProgressException e)
                {
                    log.error("error occurred in 'setUpStartPoint':"+e.getMessage());
            		throw new LearnerServiceException(e.getMessage());
                }
        	}
        	
	        //The restarting flag should be setup when the learner hit the exit
	        //button. But it is possible that user exit by closing the browser,
	        //In this case, we set the restarting flag again.
	        if(!learnerProgress.isRestarting())
	        {
	            learnerProgress.setRestarting(true);
	            learnerProgressDAO.updateLearnerProgress(learnerProgress);
	        }
        }
        
        return learnerProgress;
    }
    
    /**
     * This method navigate through all the tool activities for the given
     * activity. For each tool activity, we look up the database
     * to check up the existance of correspondent tool session. If the tool 
     * session doesn't exist, we create a new tool session instance.
     * 
     * @param learnerProgress the learner progress we are processing.
     * @throws LamsToolServiceException
     */
    public void createToolSessionsIfNecessary(Activity activity, LearnerProgress learnerProgress) 
    {
        if(activity!=null) {
        
	        try
	        {
	            for(Iterator i = activity.getAllToolActivities().iterator();i.hasNext();)
	            {
	            	ToolActivity toolActivity =  (ToolActivity)i.next();
	           		createToolSessionFor(toolActivity, learnerProgress.getUser(),learnerProgress.getLesson());
	            }
	        }
	        catch (LamsToolServiceException e)
	        {
	            log.error("error occurred in 'createToolSessionFor':"+e.getMessage());
	    		throw new LearnerServiceException(e.getMessage());
	        }
	        catch (ToolException e)
	        {
	            log.error("error occurred in 'createToolSessionFor':"+e.getMessage());
	    		throw new LearnerServiceException(e.getMessage());
	        }
        }
    }
    

    /**
     * Returns the current progress data of the User. 
     * @param learnerId the Learner's userID
     * @param lessonId the Lesson to get progress from.
     * @return LearnerProgess contains the learner's progress for the lesson.
     * @throws LearnerServiceException in case of problems.
     */
    public LearnerProgress getProgress(Integer learnerId, Long lessonId)
    {
        return learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);
    }
    
    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getProgressById(java.lang.Long)
     */
    public LearnerProgress getProgressById(Long progressId)
    {
        return learnerProgressDAO.getLearnerProgress(progressId);
    }
    
    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getProgressDTOByLessonId(java.lang.Long, org.lamsfoundation.lams.usermanagement.User)
     */
    public LearnerProgressDTO getProgressDTOByLessonId(Long lessonId, Integer learnerId)
    {
        return learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId).getLearnerProgressData();
    }

    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#chooseActivity(org.lamsfoundation.lams.usermanagement.User, java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity)
     */
    public LearnerProgress chooseActivity(Integer learnerId, Long lessonId, Activity activity) 
    {
    	LearnerProgress progress = learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);
    	progressEngine.setActivityAttempted(progress, activity);
    	progress.setCurrentActivity(activity);
       	progress.setNextActivity(activity);

    	learnerProgressDAO.saveLearnerProgress(progress);
    	return progress;
    }
    
    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#moveToActivity(java.lang.Integer, java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity, org.lamsfoundation.lams.learningdesign.Activity)
     */
    public LearnerProgress moveToActivity(Integer learnerId, Long lessonId, Activity fromActivity, Activity toActivity){
    	LearnerProgress progress = learnerProgressDAO.getLearnerProgressByLearner(learnerId, lessonId);
    	
    	if(fromActivity != null)
    		progress.setProgressState(fromActivity, LearnerProgress.ACTIVITY_ATTEMPTED, activityDAO);
    	
    	if(toActivity != null) { 
	    	progress.setProgressState(toActivity, LearnerProgress.ACTIVITY_ATTEMPTED, activityDAO);
	    	progress.setCurrentActivity(toActivity);
	    	progress.setNextActivity(toActivity);
    	}
    	
    	learnerProgressDAO.updateLearnerProgress(progress);
    	return progress;
    }
    
    /**
     * Calculates learner progress and returns the data required to be displayed 
     * to the learner.
     * @param completedActivity the activity just completed
     * @param learner the Learner
     * @param learnerProgress the current progress
     * @return the bean containing the display data for the Learner
     * @throws LamsToolServiceException
     * @throws LearnerServiceException in case of problems.
     */
    public LearnerProgress calculateProgress(Activity completedActivity,Integer learnerId, LearnerProgress currentLearnerProgress) 
    {
        try
        {
        	LearnerProgress learnerProgress = progressEngine.calculateProgress(currentLearnerProgress.getUser(), completedActivity,currentLearnerProgress);
            learnerProgressDAO.updateLearnerProgress(learnerProgress);
            return learnerProgress;
        }
        catch (ProgressException e)
        {
            throw new LearnerServiceException(e.getMessage());
        }

    }
    
    /**
     * @see org.lamsfoundation.lams.learning.service.ILearnerService#completeToolSession(java.lang.Long, java.lang.Long)
     */
    public String completeToolSession(Long toolSessionId, Long learnerId) 
    {
    	// this method is called by tools, so it mustn't do anything that relies on all the tools' Spring beans
    	// being available in the context. Hence it is defined in the ILearnerService interface, not the IFullLearnerService
    	// interface. If it calls any other methods then it mustn't use anything on the ICoreLearnerService interface.
    	
    	String returnURL = null;
    	
        ToolSession toolSession = lamsCoreToolService.getToolSessionById(toolSessionId);	
        if ( toolSession == null ) {
        	// something has gone wrong - maybe due to Live Edit. The tool session supplied by the tool doesn't exist.
        	// have to go to a "can't do anything" screen and the user will have to hit resume.
        	returnURL = activityMapping.getProgressBrokenURL();

        } else {
        	Long lessonId = toolSession.getLesson().getLessonId();
            LearnerProgress currentProgress = getProgress(new Integer(learnerId.intValue()), lessonId);
            // TODO Cache the learner progress in the session, but mark it with the progress id. Then get the progress out of the session
            // for ActivityAction.java.completeActivity(). Update LearningWebUtil to look under the progress id, so we don't get
            // a conflict in Preview & Learner.
	        returnURL = activityMapping.getCompleteActivityURL(toolSession.getToolActivity().getActivityId(), currentProgress.getLearnerProgressId());
        	
        }
        
        if ( log.isDebugEnabled() ) { 
        	log.debug("CompleteToolSession() for tool session id "+toolSessionId+" learnerId "+learnerId+" url is "+returnURL);
        }
        
        return returnURL;
        
    }
    
    /**
     * Complete the activity in the progress engine and delegate to the progress 
     * engine to calculate the next activity in the learning design. 
     * It is currently triggered by various progress engine related action classes,
     * which then calculate the url to go to next, based on the ActivityMapping
     * class.
     * 
     * @param learnerId the learner who are running this activity in the design.
     * @param activity the activity is being run.
     * @param lessonId lesson id
     * @return the updated learner progress
     */
    public LearnerProgress completeActivity(Integer learnerId,Activity activity,LearnerProgress progress) 
    {
        LearnerProgress nextLearnerProgress = null;
        
        // Need to synchronise the next bit of code so that if the tool calls
        // this twice in quick succession, with the same parameters, it won't update
        // the database twice! This may happen if a tool has a double submission problem.
        // I don't want to synchronise on (this), as this could cause too much of a bottleneck,
        // but if its not synchronised, we get db errors if the same tool session is completed twice
        // (invalid index). I can'tfind another object on which to synchronise - Hibernate does not give me the 
        // same object for tool session or current progress and user is cached via login, not userid.

        //  bottleneck synchronized (this) {
        	if (activity==null ) {
		    	try {
	        		nextLearnerProgress = progressEngine.setUpStartPoint(progress);
				} catch (ProgressException e) {
		            log.error("error occurred in 'setUpStartPoint':"+e.getMessage(),e);
		    		throw new LearnerServiceException(e);
				}
        		
        	} else {
        		
		    	nextLearnerProgress = calculateProgress(activity, learnerId, progress);
        	}
        //}
       	return nextLearnerProgress;
    }
    
   /**
     * @throws  
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#completeActivity(java.lang.Integer, org.lamsfoundation.lams.learningdesign.Activity, java.lang.Long )
     */
    public LearnerProgress completeActivity(Integer learnerId,Activity activity,Long lessonId) 
    {
        LearnerProgress currentProgress = getProgress(new Integer(learnerId.intValue()), lessonId);
        return completeActivity(learnerId, activity, currentProgress);
    }    
    
    /**
     * Exit a lesson.
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#exitLesson(org.lamsfoundation.lams.lesson.LearnerProgress)
     */
    public void exitLesson(Integer learnerId, Long lessonId)
    {
    	
       User learner = (User)userManagementService.findById(User.class,learnerId);
    	
       LearnerProgress progress = learnerProgressDAO.getLearnerProgressByLearner(learner.getUserId(),lessonId);
    	
       if ( progress != null ) {
    	   progress.setRestarting(true);
    	   learnerProgressDAO.updateLearnerProgress(progress);
       } else { 
    	   String error = "Learner Progress "+lessonId+" does not exist. Cannot exit lesson successfully.";
    	   log.error(error);
    	   throw new LearnerServiceException(error);
       }
    }
    
    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getActivity(java.lang.Long)
     */
    public Activity getActivity(Long activityId)
    {
        return activityDAO.getActivityByActivityId(activityId);
    }
    
    /**
     * @throws LearnerServiceException 
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#performGrouping(java.lang.Long, java.lang.Long, java.lang.Integer)
     */
    public boolean performGrouping(Long lessonId, Long groupingActivityId, Integer learnerId, boolean forceGrouping) throws LearnerServiceException
    {
    	GroupingActivity groupingActivity = (GroupingActivity) activityDAO.getActivityByActivityId(groupingActivityId, GroupingActivity.class);
    	User learner = (User)userManagementService.findById(User.class,learnerId);
    	
    	boolean groupingDone = false;
    	try {
	    	if ( groupingActivity != null && groupingActivity.getCreateGrouping()!=null && learner != null ) {
	    		Grouping grouping = groupingActivity.getCreateGrouping();
	    		
	    		// first check if the grouping already done for the user. If done, then skip the processing.
    			groupingDone = grouping.doesLearnerExist(learner);

    			if ( ! groupingDone ) {
    				if ( grouping.isRandomGrouping() ) {
		    			// normal and preview cases for random grouping 
		    			lessonService.performGrouping(lessonId, groupingActivity, learner);
		    			groupingDone = true;
		    			
		    		} else if ( forceGrouping ) {
		    			// preview case for chosen grouping 
		    			Lesson lesson = getLesson(lessonId);
		            	groupingDone = forceGrouping(lesson, grouping, null, learner);
		        	} 
    			}
	    		
	    	} else {
	    		String error = "Grouping activity "+groupingActivity+" learner "+learnerId+" does not exist. Cannot perform grouping.";
	            log.error(error);
	            throw new LearnerServiceException(error);
	    	}
    	} catch ( LessonServiceException e ) {
    		throw new LearnerServiceException("performGrouping failed due to "+e.getMessage(), e);
    	}
    	return groupingDone;
    }
    
	private boolean forceGrouping(Lesson lesson, Grouping grouping, Group group, User learner) {
		boolean groupingDone = false;
		if ( lesson.isPreviewLesson() ) {
			ArrayList<User> learnerList = new ArrayList<User>();
			learnerList.add(learner);
			lessonService.performGrouping(grouping, group!=null?group.getGroupId():null, learnerList);
			groupingDone = true;
		}
		return groupingDone;
	}
    	
    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#knockGate(java.lang.Long, org.lamsfoundation.lams.usermanagement.User)
     */
    public boolean knockGate(Long gateActivityId, User knocker, boolean forceGate) {
    	GateActivity gate = (GateActivity) activityDAO.getActivityByActivityId(gateActivityId, GateActivity.class);
    	if ( gate != null ) {
    		return knockGate(gate,knocker, forceGate);
    	} 
    	
		String error = "Gate activity "+gateActivityId+" does not exist. Cannot knock on gate.";
        log.error(error);
		throw new LearnerServiceException(error);
    }
    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#knockGate(org.lamsfoundation.lams.learningdesign.GateActivity, org.lamsfoundation.lams.usermanagement.User)
     */
    public boolean knockGate(GateActivity gate, User knocker, boolean forceGate)
    {
    		Lesson lesson = getLessonByActivity(gate);
    		
    		//get all learners who have started the lesson
        	List lessonLearners = getActiveLearnersByLesson(lesson.getLessonId());
        	
        	boolean gateOpen = false;
        	
        	if ( forceGate ) {
            	if ( lesson.isPreviewLesson() ) {
            		// special case for preview - if forceGate is true then brute force open the gate
            		gateOpen = gate.forceGateOpen();
            	}
        	} 
        	
        	if ( ! gateOpen ) {
        		// normal case - knock the gate.
        		gateOpen = gate.shouldOpenGateFor(knocker,lessonLearners);
        	}
	        
	        //update gate including updating the waiting list and gate status in
	        //the database.
	        activityDAO.update(gate);
	        return gateOpen;
	        
    }
    
    /**
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getLearnerActivityURL(java.lang.Integer, java.lang.Long)
     */
   public String getLearnerActivityURL(Integer learnerId, Long activityId) {    
    	User learner = (User)userManagementService.findById(User.class,learnerId);
    	Activity requestedActivity = getActivity(activityId);
    	Lesson lesson = getLessonByActivity(requestedActivity);
    	return activityMapping.calculateActivityURLForProgressView(lesson,learner,requestedActivity);
    }
    
   /**
    * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getActiveLearnersByLesson(long)
    */
   public List getActiveLearnersByLesson(long lessonId)
   {
   	return lessonService.getActiveLessonLearners(lessonId);
   }
   
   /**
    * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#getCountActiveLessonLearners(long)
    */
   public Integer getCountActiveLearnersByLesson(long lessonId)
   {
   	return lessonService.getCountActiveLessonLearners(lessonId);
   }
   
   /** 
    * Get the lesson for this activity. If the activity is not part of a lesson (ie is from an authoring 
    * design then it will return null.
    */
   public Lesson getLessonByActivity(Activity activity) {
	   Lesson lesson = lessonDAO.getLessonForActivity(activity.getActivityId());
		if ( lesson == null ) {
			log.warn("Tried to get lesson id for a non-lesson based activity. An error is likely to be thrown soon. Activity was "+activity);
		}
		return lesson;
   }
   
    //---------------------------------------------------------------------
    // Helper Methods
    //---------------------------------------------------------------------

    /**
     * <p>Create a lams tool session for learner against a tool activity. This will
     * have concurrency issues interms of grouped tool session because it might 
     * be inserting some tool session that has already been inserted by other
     * member in the group. If the unique_check is broken, we need to query
     * the database to get the instance instead of inserting it. It should be
     * done in the Spring rollback strategy. </p>
     *
     * Once lams tool session is inserted, we need to notify the tool to its
     * own session. 
     * 
     * @param toolActivity
     * @param learner
     * @throws LamsToolServiceException
     */
    private void createToolSessionFor(ToolActivity toolActivity,User learner,Lesson lesson) throws LamsToolServiceException, ToolException
    {
        // if the tool session already exists, createToolSession() will return null
        ToolSession toolSession = lamsCoreToolService.createToolSession(learner,toolActivity,lesson);
        if ( toolSession !=null ) {
	        toolActivity.getToolSessions().add(toolSession);
	        lamsCoreToolService.notifyToolsToCreateSession(toolSession, toolActivity);
        }
    }
    
    
    /**
     * Create an array of lesson dto based a list of lessons.
     * @param lessons the list of lessons.
     * @return the lesson dto array.
     */
    private LessonDTO[] getLessonDataFor(List lessons)
    {
        List<LessonDTO> lessonDTOList = new ArrayList<LessonDTO>();
        for(Iterator i=lessons.iterator();i.hasNext();)
        {
            Lesson currentLesson = (Lesson)i.next();
            lessonDTOList.add(currentLesson.getLessonData());
        }
        return lessonDTOList.toArray(new LessonDTO[lessonDTOList.size()]);   
    }

    /**
     * @throws LearnerServiceException 
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#determineBranch(org.lamsfoundation.lams.lesson.Lesson, org.lamsfoundation.lams.learningdesign.BranchingActivity, java.lang.Integer)
     */
    public SequenceActivity determineBranch(Lesson lesson, BranchingActivity branchingActivity, Integer learnerId) throws LearnerServiceException
    {
    	User learner = (User)userManagementService.findById(User.class,learnerId);
    	if ( learner == null ) {
    		String error = "determineBranch: learner "+learnerId+" does not exist. Cannot determine branch.";
    		log.error(error);
    		throw new LearnerServiceException(error);
    	}

    	try {
	        if ( branchingActivity.isToolBranchingActivity() ) {
	        	return determineToolBasedBranch(lesson, (ToolBranchingActivity)branchingActivity, learner);
	 
	    	} else {
	    		// assume either isGroupBranchingActivity() || isChosenBranchingActivity() ) 
	    		// in both cases, the branch is based on the group the learner is in.
        		return determineGroupBasedBranch(lesson, branchingActivity, learner);

	    	}
    	} catch ( LessonServiceException e ) {
    		String message = "determineBranch failed due to "+e.getMessage();
    		log.error(message,e);
    		throw new LearnerServiceException("determineBranch failed due to "+e.getMessage(), e);
    	}
    }
    
	/** Get all the conditions for this branching activity, ordered by order id.
	 * Go through each condition until we find one that passes and that is the required branch.
	 * If no conditions match, use the branch that is the "default" branch for this branching activity.
	 */
	private SequenceActivity determineToolBasedBranch(Lesson lesson, ToolBranchingActivity branchingActivity, User learner) 
    {
    	Activity defaultBranch = (Activity) branchingActivity.getDefaultActivity();
    	SequenceActivity matchedBranch = null;
    	
    	// Work out the tool session appropriate for this user and branching activity. We expect there to be only one at this point.
    	ToolSession toolSession = null;
    	for ( Activity inputActivity :  (Set<Activity>) branchingActivity.getInputActivities() ) {
    		toolSession = lamsCoreToolService.getToolSessionByLearner(learner, inputActivity);
    	}

    	if ( toolSession != null ) {
    	
	    	// Get all the conditions for this branching activity, ordered by order id.
	    	Map<BranchCondition,SequenceActivity> conditionsMap = new TreeMap<BranchCondition,SequenceActivity>();
	    	Iterator branchIterator = branchingActivity.getActivities().iterator();
	    	while ( branchIterator.hasNext() ) {
	    		Activity branchActivity = (Activity) branchIterator.next();
	    		SequenceActivity branchSequence = (SequenceActivity) activityDAO.getActivityByActivityId(branchActivity.getActivityId(), SequenceActivity.class);
	    		Iterator<BranchActivityEntry> entryIterator = branchSequence.getBranchEntries().iterator();
	    		while (entryIterator.hasNext()) {
					BranchActivityEntry entry = entryIterator.next();
					if ( entry.getCondition() != null ) {
						conditionsMap.put(entry.getCondition(), branchSequence);
					} 
				}
	    	}
	
	    	// Go through each condition until we find one that passes and that is the required branch.
	    	// Cache the tool output so that we aren't calling it over an over again.
	    	Map<String, ToolOutput> toolOutputMap = new HashMap<String, ToolOutput>();
	    	Iterator<BranchCondition> conditionIterator = conditionsMap.keySet().iterator();
	
	    	while (matchedBranch==null && conditionIterator.hasNext()) {
	    		BranchCondition condition = conditionIterator.next();
	    		String conditionName = condition.getName();
	    		ToolOutput toolOutput = toolOutputMap.get(conditionName);
	    		if ( toolOutput == null ) {
	    			toolOutput = lamsCoreToolService.getOutputFromTool(conditionName, toolSession, learner.getUserId());
	    			if ( toolOutput == null ) {
	    				log.warn("Condition "+condition+" refers to a tool output "+conditionName+" but tool doesn't return any tool output for that name. Skipping this condition.");
	    			} else {
	    				toolOutputMap.put(conditionName, toolOutput);
	    			}
	    		}
	    		
	    		if ( toolOutput != null && condition.isMet(toolOutput) ) {
						matchedBranch = conditionsMap.get(condition);
	    		}
	    	}
    	}
    	
   	 	// If no conditions match, use the branch that is the "default" branch for this branching activity.
    	if ( matchedBranch != null ) {
			if ( log.isDebugEnabled()) {
				log.debug("Found branch "+matchedBranch.getActivityId()+":"+ matchedBranch.getTitle()
						+" for branching activity "+branchingActivity.getActivityId()+":"+ branchingActivity.getTitle()
						+" for learner "+learner.getUserId()+":"+learner.getLogin());
			}
    		return matchedBranch;
    		
    	} else if ( defaultBranch != null) {
			if ( log.isDebugEnabled() ) {
				log.debug("Using default branch "+defaultBranch.getActivityId()+":"+ defaultBranch.getTitle()
						+" for branching activity "+branchingActivity.getActivityId()+":"+ branchingActivity.getTitle()
						+" for learner "+learner.getUserId()+":"+learner.getLogin());
			}
			// have to convert it to a real activity of the correct type, as it could be a cglib value
    		return (SequenceActivity) activityDAO.getActivityByActivityId(defaultBranch.getActivityId(), SequenceActivity.class);
    	} else {
			if ( log.isDebugEnabled() ) {
				log.debug("No branches match and no default branch exists. Uable to allocate learner to a branch for the branching activity"
						+branchingActivity.getActivityId()+":"+ branchingActivity.getTitle()
						+" for learner "+learner.getUserId()+":"+learner.getLogin());
			}
			return null;
    	}
    }

    private SequenceActivity determineGroupBasedBranch(Lesson lesson, BranchingActivity branchingActivity, User learner) 
    {
    	SequenceActivity sequenceActivity = null;

    	if ( branchingActivity.getGrouping()!=null ) {
    		Grouping grouping = branchingActivity.getGrouping();
	    		
	    	// If the user is in a group, then check if the group is assigned to a sequence activity. If it
    		// is then we are done and we return the sequence
    		Group group = grouping.getGroupBy(learner);
    		if ( group != null ) {
    			if ( group.getBranchActivities() != null ) {
	    			Iterator branchesIterator = group.getBranchActivities().iterator();
	    			while (sequenceActivity==null && branchesIterator.hasNext()) {
	    				BranchActivityEntry branchActivityEntry = (BranchActivityEntry) branchesIterator.next();
	    				if ( branchActivityEntry.getBranchingActivity().equals(branchingActivity) )
	    					sequenceActivity = branchActivityEntry.getBranchSequenceActivity();
	    			}
    			}
    		} 
    		
    		if ( sequenceActivity != null ) {
    			if ( log.isDebugEnabled()) {
    				log.debug("Found branch "+sequenceActivity.getActivityId()+":"+ sequenceActivity.getTitle()
    						+" for branching activity "+branchingActivity.getActivityId()+":"+ branchingActivity.getTitle()
    						+" for learner "+learner.getUserId()+":"+learner.getLogin());
    			}
    		}

    	}
    	
    	return sequenceActivity;
    }

    /**
     * Select a particular branch - we are in preview mode and the author has selected a particular activity.
     * 
     * @throws LearnerServiceException 
     * @see org.lamsfoundation.lams.learning.service.ICoreLearnerService#determineBranch(org.lamsfoundation.lams.lesson.Lesson, org.lamsfoundation.lams.learningdesign.BranchingActivity, java.lang.Integer)
     */
    public SequenceActivity selectBranch(Lesson lesson, BranchingActivity branchingActivity, Integer learnerId, Long branchId) throws LearnerServiceException {

    	User learner = (User)userManagementService.findById(User.class,learnerId);
    	if ( learner == null ) {
    		String error = "selectBranch: learner "+learnerId+" does not exist. Cannot determine branch.";
    		log.error(error);
    		throw new LearnerServiceException(error);
    	}

    	SequenceActivity selectedBranch = (SequenceActivity) activityDAO.getActivityByActivityId(branchId, SequenceActivity.class);
    	if ( selectedBranch !=null ) {

    		if ( selectedBranch.getParentActivity() == null ||  !selectedBranch.getParentActivity().equals(branchingActivity) ) {
        		String error = "selectBranch: activity "+selectedBranch+" is not a branch within the branching activity "+branchingActivity+". Unable to branch.";
        		log.error(error);
        		throw new LearnerServiceException(error);
    		} 
    		
    		Set<Group> groups = selectedBranch.getGroupsForBranch();
    		Grouping grouping = branchingActivity.getGrouping();
    		
    		// Does this matching branch have any groups? If so, see if the learner is in 
    		// the appropriate group and add them if necessary.
    		if ( groups != null && groups.size() > 0) {
	    		boolean isInGroup = false;
	    		Group aGroup = null;
	    		Iterator<Group> groupIter = groups.iterator();
	    		while (!isInGroup && groupIter.hasNext()) {
					aGroup = groupIter.next();
					isInGroup =  aGroup.hasLearner(learner);
				}
	    		
	    		// If the learner is not in the appropriate group, then force the learner in the 
	    		// last group we checked. this will only work if the user is in preview.
				if (!isInGroup) {
					if ( ! forceGrouping(lesson, grouping, aGroup, learner) ) {
			    		String error = "selectBranch: learner "+learnerId+" cannot be added to the group "+aGroup+" for the branch "
			    			+selectedBranch+" for the lesson "+lesson.getLessonName()+" preview is "+lesson.isPreviewLesson()
			    			+". This will only work if preview is true.";
			    		log.error(error);
			    		throw new LearnerServiceException(error);
					}
				}
				
			// if no groups exist, then create one and assign it to the branch.
    		} else {
				Group group = lessonService.createGroup(grouping, selectedBranch.getTitle());
				group.allocateBranchToGroup(null, selectedBranch, branchingActivity);
    		}
			groupingDAO.update(grouping);
			
   			if ( log.isDebugEnabled()) {
   				log.debug("Found branch "+selectedBranch.getActivityId()+":"+ selectedBranch.getTitle()
   						+" for branching activity "+branchingActivity.getActivityId()+":"+ branchingActivity.getTitle()
   						+" for learner "+learner.getUserId()+":"+learner.getLogin());
   			}

   	    	return selectedBranch;

    	} else {
       		String error = "selectBranch: Unable to find branch for branch id "+branchId;
    		log.error(error);
    		throw new LearnerServiceException(error);
    	}

    }
    

    public ProgressEngine getProgressEngine() {
		return progressEngine;
	}

	public void setProgressEngine(ProgressEngine progressEngine) {
		this.progressEngine = progressEngine;
	}
	
	
	public Map<Long, IndexLessonBean> getLessonsByOrgAndUserWithCompletedFlag(Integer userId, Integer orgId, boolean isStaff)
    {
		HashMap<Long, IndexLessonBean> map = new HashMap<Long, IndexLessonBean>();
        List list = this.lessonDAO.getLessonsByOrgAndUserWithCompletedFlag(userId, orgId, isStaff);
        if (list != null) {
        	Iterator iterator = list.iterator();
        	while (iterator.hasNext()) {
        		Object[] tuple = (Object[])iterator.next();
        		Long lessonId = (Long)tuple[0];
        		String lessonName = (String)tuple[1];
        		String lessonDescription = (String)tuple[2];
        		Integer lessonState = (Integer)tuple[3];
        		Boolean lessonCompleted = (Boolean)tuple[4];
    			IndexLessonBean bean = new IndexLessonBean(
    				lessonId, lessonName, lessonDescription, lessonState, (
    					lessonCompleted == null ? false : lessonCompleted.booleanValue()
    				)
    			);
    			map.put(new Long(lessonId), bean);
    		}
        }
        return map;	
    }

}
