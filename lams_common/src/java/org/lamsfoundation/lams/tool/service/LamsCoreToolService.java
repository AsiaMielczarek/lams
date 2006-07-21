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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.tool.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.ToolActivity;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.tool.SystemTool;
import org.lamsfoundation.lams.tool.Tool;
import org.lamsfoundation.lams.tool.ToolContent;
import org.lamsfoundation.lams.tool.ToolContentIDGenerator;
import org.lamsfoundation.lams.tool.ToolContentManager;
import org.lamsfoundation.lams.tool.ToolSession;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.dao.ISystemToolDAO;
import org.lamsfoundation.lams.tool.dao.IToolContentDAO;
import org.lamsfoundation.lams.tool.dao.IToolSessionDAO;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.LamsToolServiceException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.util.wddx.FlashMessage;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * 
 * @author Jacky Fang 
 * @version 1.1
 * @since 2005-2-23
 */
public class LamsCoreToolService implements ILamsCoreToolService,ApplicationContextAware
{
	private static final Logger log = Logger.getLogger(LamsCoreToolService.class);

	//---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
    private ApplicationContext context;
    private IToolSessionDAO toolSessionDAO;
    private ISystemToolDAO systemToolDAO;
    private ToolContentIDGenerator contentIDGenerator;
	protected IToolContentDAO toolContentDAO;
    //---------------------------------------------------------------------
    // Inversion of Control Methods - Method injection
    //---------------------------------------------------------------------
    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.context = context;
    }
    
    /**
     * @param toolSessionDAO The toolSessionDAO to set.
     */
    public void setToolSessionDAO(IToolSessionDAO toolSessionDAO)
    {
        this.toolSessionDAO = toolSessionDAO;
    }

	public ISystemToolDAO getSystemToolDAO() {
		return systemToolDAO;
	}

	public void setSystemToolDAO(ISystemToolDAO systemToolDAO) {
		this.systemToolDAO = systemToolDAO;
	}

    /**
     * @param contentIDGenerator The contentIDGenerator to set.
     */
    public void setContentIDGenerator(ToolContentIDGenerator contentIDGenerator)
    {
        this.contentIDGenerator = contentIDGenerator;
    }

    //---------------------------------------------------------------------
    // Service Methods
    //---------------------------------------------------------------------

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#createToolSession(org.lamsfoundation.lams.usermanagement.User, org.lamsfoundation.lams.learningdesign.Activity)
     */
    public ToolSession createToolSession(User learner, ToolActivity activity,Lesson lesson) throws LamsToolServiceException
    {
        // look for an existing applicable tool session
		// could be either a grouped (class group or standard group) or an individual.
        // more likely to be grouped (more tools work that way!)
        ToolSession toolSession = toolSessionDAO.getToolSessionByLearner(learner, activity);

        // if haven't found an existing tool session then create one
        if( toolSession == null ) {

        	if ( log.isDebugEnabled() ) {
        		log.debug("Creating tool session for ["+activity.getActivityId()+","+activity.getTitle()
        				+"] for learner ["+learner.getLogin()
        				+"] lesson ["+lesson.getLessonId()+","+lesson.getLessonName()+"].");
        	}

        	toolSession = activity.createToolSessionForActivity(learner,lesson);
            toolSessionDAO.saveToolSession(toolSession);
            return toolSession;
        }
        
        // indicate that we found an existing tool session by returning null
        return null;
    }


    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#createToolSession(java.util.Set, org.lamsfoundation.lams.learningdesign.Activity)
     */
    public Set createToolSessions(Set learners, ToolActivity activity,Lesson lesson) throws LamsToolServiceException
    {
		Iterator iter = learners.iterator();
		Set newToolSessions = new HashSet();
		while (iter.hasNext()) {
			// set up the new tool session. createToolSession() will see if it really
			// needs to be created - if not will return an existing session.
			User learner = (User) iter.next();
        	ToolSession toolSession = createToolSession(learner, activity,lesson);
        	if ( toolSession != null )
        		newToolSessions.add(toolSession);
    	}
        
        return newToolSessions;
    }

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolSessionByLearner(org.lamsfoundation.lams.usermanagement.User, org.lamsfoundation.lams.learningdesign.Activity)
     */
    public ToolSession getToolSessionByLearner(User learner, Activity activity) throws LamsToolServiceException
    {
        return toolSessionDAO.getToolSessionByLearner(learner,activity);
    }

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolSessionById(java.lang.Long)
     */
    public ToolSession getToolSessionById(Long toolSessionId)
    {
        return toolSessionDAO.getToolSession(toolSessionId);
    }
    
    /**
     * Get the tool session based on the activity id and the learner.
     * @throws LamsToolServiceException
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolSessionByActivity(org.lamsfoundation.lams.usermanagement.User, ToolActivity)
     */
    public ToolSession getToolSessionByActivity(User learner, ToolActivity toolActivity) throws LamsToolServiceException
    {
    	return this.toolSessionDAO.getToolSessionByLearner(learner,toolActivity);
    }
    
    /**
     * @throws ToolException
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#notifyToolsToCreateSession( org.lamsfoundation.lams.tool.ToolSession, org.lamsfoundation.lams.learningdesign.ToolActivity)
     */
    public void notifyToolsToCreateSession(ToolSession toolSession, ToolActivity activity) throws ToolException
    {
    	try {
	        ToolSessionManager sessionManager = (ToolSessionManager) findToolService(activity.getTool());
	
	        sessionManager.createToolSession(toolSession.getToolSessionId(),toolSession.getToolSessionName(),
	                                         activity.getToolContentId());
    	} catch ( NoSuchBeanDefinitionException e ) {
    		String message = "A tool which is defined in the database appears to missing from the classpath. Unable to create tool session. ToolActivity "+activity;
    		log.error(message,e);
    		throw new ToolException(message,e);
    	}
    }
    /**
     * Calls the tool to copy the content for an activity. Used when copying a learning design.
     * If it is a preview lesson, we don't want to set define later - we will sidestep this in the progress engine.
     * 
     * @param toolActivity the tool activity defined in the design.
     * @throws DataMissingException, ToolException
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#notifyToolToCopyContent(org.lamsfoundation.lams.learningdesign.ToolActivity)
     */
    public Long notifyToolToCopyContent(ToolActivity toolActivity, boolean setDefineLater) 
    		throws DataMissingException, ToolException
    {
        Long newToolcontentID = contentIDGenerator.getNextToolContentIDFor(toolActivity.getTool());
        try {
			ToolContentManager contentManager = (ToolContentManager) findToolService(toolActivity.getTool());
            contentManager.copyToolContent(toolActivity.getToolContentId(),
                                           newToolcontentID);
            
            if ( setDefineLater && toolActivity.getDefineLater() != null &&
                    toolActivity.getDefineLater().booleanValue() ) {
                contentManager.setAsDefineLater(newToolcontentID);
            }
            if ( toolActivity.getRunOffline() != null &&
                    toolActivity.getRunOffline().booleanValue() ) {
            	contentManager.setAsRunOffline(newToolcontentID);
			}
		} catch ( NoSuchBeanDefinitionException e ) {
			String message = "A tool which is defined in the database appears to missing from the classpath. Unable to copy the tool content. ToolActivity "+toolActivity;
			log.error(message,e);
			throw new ToolException(message,e);
		}

        return newToolcontentID;
    }
    
    /**
     * Calls the tool to copy the content for an activity. Used when copying an activity in authoring. Can't
     * use the notifyToolToCopyContent(ToolActivity, boolean) version in authoring as the tool activity won't
     * exist if the user hasn't saved the sequence yet. But the tool content (as that is saved by the 
     * tool) may already exist.
     * 
     * @param toolContentId the content to be copied.
     * @throws DataMissingException, ToolException
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#notifyToolToCopyContent(org.lamsfoundation.lams.learningdesign.ToolActivity)
     */
    public Long notifyToolToCopyContent(Long toolContentId) 
    		throws DataMissingException, ToolException
    {
    	ToolContent toolContent = (ToolContent) toolContentDAO.find(ToolContent.class, toolContentId);
    	if ( toolContent == null ) {
    		String error = "The toolContentID "+ toolContentId + " is not valid. No such record exists on the database.";
 	       log.error(error);
 	       throw new DataMissingException(error);
    	}
    	
		Tool tool = toolContent.getTool();
    	if ( tool == null ) {
    		String error = "The tool for toolContentId "+ toolContentId + " is missing.";
 	       log.error(error);
 	       throw new DataMissingException(error);
    	}

    	Long newToolcontentID = contentIDGenerator.getNextToolContentIDFor(tool);
        try {
			ToolContentManager contentManager = (ToolContentManager) findToolService(tool);
            contentManager.copyToolContent(toolContentId,newToolcontentID);
		} catch ( NoSuchBeanDefinitionException e ) {
			String message = "A tool which is defined in the database appears to missing from the classpath. Unable to copy the tool content. ToolContentId "+toolContentId;
			log.error(message,e);
			throw new ToolException(message,e);
		}

        return newToolcontentID;
    }
    
    /**
     * Ask a tool to delete a tool content. If any related tool session data exists then it should 
     * be deleted.
     * 
     * @param toolActivity the tool activity defined in the design.
     * @throws ToolException 
     */
    public void notifyToolToDeleteContent(ToolActivity toolActivity) throws ToolException
    {
    	try {
			ToolContentManager contentManager = (ToolContentManager) findToolService(toolActivity.getTool());
			contentManager.removeToolContent(toolActivity.getToolContentId(),true);
		} catch ( NoSuchBeanDefinitionException e ) {
			String message = "A tool which is defined in the database appears to missing from the classpath. Unable to delete the tool content. ToolActivity "+toolActivity;
			log.error(message,e);
			throw new ToolException(message,e);
		}
    }
    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#updateToolSession(org.lamsfoundation.lams.tool.ToolSession)
     */
    public void updateToolSession(ToolSession toolSession)
    {
        toolSessionDAO.updateToolSession(toolSession);        
    }
    
    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolSessionsByLesson(org.lamsfoundation.lams.lesson.Lesson)
     */
    public List getToolSessionsByLesson(Lesson lesson)
    {
        return toolSessionDAO.getToolSessionsByLesson(lesson);        
    }
    
    
    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#deleteToolSession(org.lamsfoundation.lams.tool.ToolSession)
     */
    public void deleteToolSession(ToolSession toolSession)
    {
		if ( toolSession == null ) {
			log.error("deleteToolSession: unable to delete tool session as tool session is null.");
			return;		
		}

    	// call the tool to remove the session details
        ToolSessionManager sessionManager = (ToolSessionManager) findToolService(toolSession.getToolActivity().getTool());

        try {
			sessionManager.removeToolSession(toolSession.getToolSessionId());
		} catch (DataMissingException e) {
			log.error("Unable to delete tool data for tool session "+toolSession.getToolSessionId()
					+" as toolSession does not exist",e);
		} catch (ToolException e) {
			log.error("Unable to delete tool data for tool session "+toolSession.getToolSessionId()
					+" as tool threw an exception",e);
		}
    	
    	// now remove the tool session from the core tables.
    	toolSessionDAO.removeToolSession(toolSession);
        
    }

    
    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolLearnerURL(java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity, org.lamsfoundation.lams.usermanagement.User)
     */
    public String getToolLearnerURL(Long lessonID, Activity activity,User learner) throws LamsToolServiceException
    {
    	if ( activity.isToolActivity() ) {
    		ToolActivity toolActivity = (ToolActivity) activity;
        	String toolURL = toolActivity.getTool().getLearnerUrl();
            return setupToolURLWithToolSession(toolActivity, learner, toolURL);
    	} else if ( activity.isSystemToolActivity() ){
    		SystemTool sysTool = systemToolDAO.getSystemToolByActivityTypeId(activity.getActivityTypeId());
    		if ( sysTool != null ) {
                return setupURLWithActivityLessonID(activity, lessonID, sysTool.getLearnerUrl());
    		}
    	}
    	return null;
    }
    
    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolLearnerPreviewURL(java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity, org.lamsfoundation.lams.usermanagement.User)
     */
    public String getToolLearnerPreviewURL(Long lessonID, Activity activity,User authorLearner) throws LamsToolServiceException
    {
    	if ( activity.isToolActivity() ) {
    		ToolActivity toolActivity = (ToolActivity) activity;
	    	String toolURL = toolActivity.getTool().getLearnerPreviewUrl();
	        return setupToolURLWithToolSession(toolActivity, authorLearner, toolURL);
    	} else if ( activity.isSystemToolActivity() ){
    		SystemTool sysTool = systemToolDAO.getSystemToolByActivityTypeId(activity.getActivityTypeId());
    		if ( sysTool != null ) {
                return setupURLWithActivityLessonID(activity, lessonID, sysTool.getLearnerPreviewUrl());
    		}
    	}
    	return null;
    }

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolLearnerProgressURL(java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity, org.lamsfoundation.lams.usermanagement.User)
     */
    public String getToolLearnerProgressURL(Long lessonID, Activity activity,User learner) throws LamsToolServiceException
    {
    	if ( activity.isToolActivity() ) {
    		ToolActivity toolActivity = (ToolActivity) activity;
	    	String toolURL = toolActivity.getTool().getLearnerProgressUrl();
	       	toolURL = appendUserIDToURL(learner, toolURL);
	        return setupToolURLWithToolSession(toolActivity, learner, toolURL);
    	} else if ( activity.isSystemToolActivity() ){
    		SystemTool sysTool = systemToolDAO.getSystemToolByActivityTypeId(activity.getActivityTypeId());
    		if ( sysTool != null ) {
                return setupURLWithActivityLessonID(activity, lessonID, sysTool.getLearnerProgressUrl());
    		}
    	}
    	return null;
    }

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolMonitoringURL(java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity, org.lamsfoundation.lams.usermanagement.User)
     */
    public String getToolMonitoringURL(Long lessonID, Activity activity) throws LamsToolServiceException {

    	if ( activity.isToolActivity() ) {
    		ToolActivity toolActivity = (ToolActivity) activity;
    		String url = toolActivity.getTool().getMonitorUrl();
    		if ( url != null )
    			return setupToolURLWithToolContent(toolActivity, url);
    	} else if ( activity.isSystemToolActivity() ){
    		SystemTool sysTool = systemToolDAO.getSystemToolByActivityTypeId(activity.getActivityTypeId());
    		if ( sysTool != null ) {
                return setupURLWithActivityLessonID(activity, lessonID, sysTool.getMonitorUrl());
    		}
    	}
    	return null;
    }

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getToolContributionURL(java.lang.Long, org.lamsfoundation.lams.learningdesign.Activity, org.lamsfoundation.lams.usermanagement.User)
     */
    public String getToolContributionURL(Long lessonID, Activity activity) throws LamsToolServiceException {

    	if ( activity.isToolActivity() ) {
    		ToolActivity toolActivity = (ToolActivity) activity;
    		String url = toolActivity.getTool().getContributeUrl();
    		if ( url != null )
    			return setupToolURLWithToolContent(toolActivity, url);
    	} else if ( activity.isSystemToolActivity() ){
    		SystemTool sysTool = systemToolDAO.getSystemToolByActivityTypeId(activity.getActivityTypeId());
    		if ( sysTool != null ) {
                return setupURLWithActivityLessonID(activity, lessonID, sysTool.getContributeUrl());
    		}
    	}
    	return null;
    }

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getDefineLaterURL(org.lamsfoundation.lams.learningdesign.ToolActivity, org.lamsfoundation.lams.usermanagement.User)
     */
    public String getToolDefineLaterURL(ToolActivity activity) throws LamsToolServiceException {

    	if ( activity.isToolActivity() ) {
    		ToolActivity toolActivity = (ToolActivity) activity;
    		String url = toolActivity.getTool().getDefineLaterUrl();
    		if ( url != null )
    			return setupToolURLWithToolContent(toolActivity, url);
    	} 
    	return null;
    }

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#getModerateURL(org.lamsfoundation.lams.learningdesign.ToolActivity, org.lamsfoundation.lams.usermanagement.User)
     */
    public String getToolModerateURL(ToolActivity activity) throws LamsToolServiceException {

    	if ( activity.isToolActivity() ) {
    		ToolActivity toolActivity = (ToolActivity) activity;
    		String url = toolActivity.getTool().getModerationUrl();
    		if ( url != null )
    			return setupToolURLWithToolContent(toolActivity, url);
    	} 
    	return null;
    }

    /**
     * Add the user id to the url
     */
    private String appendUserIDToURL(User user, String toolURL) 
    {
        return WebUtil.appendParameterToURL(toolURL,
        		AttributeNames.PARAM_USER_ID,
        		user.getUserId().toString());
    }

    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#setupToolURLWithToolSession(org.lamsfoundation.lams.learningdesign.ToolActivity, org.lamsfoundation.lams.usermanagement.User, java.lang.String)
     */
    public String setupToolURLWithToolSession(ToolActivity activity, 
                                              User learner,
                                              String toolURL) throws LamsToolServiceException
    {
        ToolSession toolSession = this.getToolSessionByActivity(learner,activity);
        
        if ( toolSession == null ) {
        	String error = "Unable to set up url as session does not exist. Activity "
        		+(activity!=null?activity.getActivityId()+":"+activity.getTitle():"null")
				+" learner "
				+(learner!=null?learner.getUserId()+":"+learner.getLogin():"null");
        	log.error(error);
			throw new LamsToolServiceException(error);
        }
        
        return WebUtil.appendParameterToURL(toolURL,
        		AttributeNames.PARAM_TOOL_SESSION_ID,
				toolSession.getToolSessionId().toString());
    }
    
    public String setupURLWithActivityLessonID(Activity activity, Long lessonID, String learnerURL) {
        String url = learnerURL;
        if ( url!=null && activity != null ) {
        	url = WebUtil.appendParameterToURL(url,
        		AttributeNames.PARAM_ACTIVITY_ID,
				activity.getActivityId().toString());
        }
        if ( url!=null && lessonID != null ) {
        	url = WebUtil.appendParameterToURL(url,
               		AttributeNames.PARAM_LESSON_ID,
               		lessonID.toString());
        }
       return url;
    }
    
    /**
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#setupToolURLWithToolContent(org.lamsfoundation.lams.learningdesign.ToolActivity, java.lang.String)
     */
    public String setupToolURLWithToolContent(ToolActivity activity,
                                              String toolURL)
    {
        return WebUtil.appendParameterToURL(toolURL,
        		AttributeNames.PARAM_TOOL_CONTENT_ID,
				activity.getToolContentId().toString());
    }
    //---------------------------------------------------------------------
    // Helper Methods
    //---------------------------------------------------------------------
   
    /**
     * Find a tool's service registered inside lams. It is implemented using
     * Spring now. We might need to extract this method to a proxy class to
     * find different service such as EJB or Web service. 
     * @param toolActivity the tool activity defined in the design.
     * @return the service object from tool.
     * @throws NoSuchBeanDefinitionException if the tool is not the classpath or the supplied service name is wrong.
     */
    private Object findToolService(Tool tool) throws NoSuchBeanDefinitionException
    {
        return context.getBean(tool.getServiceName());
    }

	public void setToolContentDAO(IToolContentDAO toolContentDAO) {
		this.toolContentDAO = toolContentDAO;
	}


}
