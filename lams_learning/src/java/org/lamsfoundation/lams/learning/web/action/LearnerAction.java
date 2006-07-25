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
package org.lamsfoundation.lams.learning.web.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.learning.service.ICoreLearnerService;
import org.lamsfoundation.lams.learning.service.LearnerServiceProxy;
import org.lamsfoundation.lams.learning.web.util.ActivityMapping;
import org.lamsfoundation.lams.learning.web.util.LearningWebUtil;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.dto.ProgressActivityDTO;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.dto.LearnerProgressDTO;
import org.lamsfoundation.lams.lesson.dto.LessonDTO;
import org.lamsfoundation.lams.monitoring.MonitoringConstants;
import org.lamsfoundation.lams.monitoring.service.IMonitoringService;
import org.lamsfoundation.lams.monitoring.service.MonitoringServiceProxy;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.util.audit.IAuditService;
import org.lamsfoundation.lams.util.wddx.FlashMessage;
import org.lamsfoundation.lams.util.wddx.WDDXProcessor;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/** 
 * 
 * <p>The action servlet that interacts with learner to start a lams learner
 * module, join a user to the lesson and allows a user to exit a lesson.</p>
 * 
 * <p>It is also responsible for the interaction between lams server and 
 * flash. Flash will call method implemented in this class to get progress
 * data or trigger a lams server calculation here</p>
 * 
 * <b>Note:</b>It needs to extend the <code>LamsDispatchAction</code> which has
 * been customized to accomodate struts features to solve duplicate 
 * submission problem.
 * 
 * @author Jacky Fang
 * @since 3/03/2005
 * @version 1.1
 * 
 * ----------------XDoclet Tags--------------------
 * 
 * @struts:action path="/learner" 
 *                parameter="method" 
 *                validate="false"
 * @struts:action-forward name="displayActivity" path="/DisplayActivity.do" 
 * ----------------XDoclet Tags--------------------
 * 
 */
public class LearnerAction extends LamsDispatchAction 
{
    //---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	private static Logger log = Logger.getLogger(LearnerAction.class);
	
    //---------------------------------------------------------------------
    // Class level constants - Struts forward
    //---------------------------------------------------------------------
    private static final String DISPLAY_ACTIVITY = "displayActivity";
	
	private static IAuditService auditService;
	
	private ActionForward redirectToURL(ActionMapping mapping, HttpServletResponse response, String url) throws IOException, ServletException {
		if ( url != null ) {
			  String fullURL = WebUtil.convertToFullURL(url);
			  response.sendRedirect(response.encodeRedirectURL(fullURL));
		} else {
			  throw new ServletException("Tried to redirect to url but url is null");
		}
		return null;
	 }

	  /** Handle an exception - either thrown by the service or by the web layer. Allows the exception
	 * to be logged properly and ensure that an actual message goes back to Flash.
	 * 
	 * @param e
	 * @param methodKey
	 * @param learnerService
	 * @return
	 */
	protected FlashMessage handleException(Exception e, String methodKey, ICoreLearnerService learnerService) {
		log.error("Exception thrown "+methodKey,e);
		String[] msg = new String[1];
		msg[0] = e.getMessage();
		
		getAuditService().log(LearnerAction.class.getName()+":"+methodKey, e.toString());		
		
		return new FlashMessage(methodKey,
				learnerService.getMessageService().getMessage("error.system.learner", msg),
				FlashMessage.CRITICAL_ERROR);
	}

    /**
     * <p>The structs dispatch action that joins a learner into a lesson. The
     * learner could either start a lesson or resume a lesson.</p>
     * 
     * @param mapping An ActionMapping class that will be used by the Action class to tell
     * the ActionServlet where to send the end-user.
     * 
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * 
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward joinLesson(ActionMapping mapping,
                                    ActionForm form,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        //initialize service object
        ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());

        FlashMessage message = null;
    	try {
	
	        //get user and lesson based on request.
	        Integer learner = LearningWebUtil.getUserId();
	        long lessonID = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);
	
	        
	        if(log.isDebugEnabled())
	            log.debug("The learner ["+learner+"] is joining the lesson ["+lessonID+"]");
	
	        //join user to the lesson on the server
	        LearnerProgress learnerProgress = learnerService.joinLesson(learner,lessonID);
	        
	        if(log.isDebugEnabled())
	            log.debug("The learner ["+learner+"] joined lesson. The"
	                      +"porgress data is:"+learnerProgress.toString());
	        
			LearningWebUtil.putLearnerProgressInRequest(request,learnerProgress);
	
	        //serialize a acknowledgement flash message with the path of display next
	        //activity
	        message = new FlashMessage("joinLesson", mapping.findForward(DISPLAY_ACTIVITY).getPath());
	
    	} catch (Exception e ) {
    		message = handleException(e, "joinLesson", learnerService);
    	}
    	
        String wddxPacket = WDDXProcessor.serialize(message);
        if(log.isDebugEnabled())
            log.debug("Sending Lesson joined acknowledge message to flash:"+wddxPacket);
        
        //we hand over the control to flash. 
        response.getWriter().print(wddxPacket);
        return null;
    }
    


    /**
     * <p>Exit the current lesson that is running in the leaner window. It 
     * expects lesson id passed as parameter from flash component.
     * 
     * @param mapping An ActionMapping class that will be used by the Action class to tell
     * the ActionServlet where to send the end-user.
     * 
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * 
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward exitLesson(ActionMapping mapping,
                                    ActionForm form,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        //initialize service object
        ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());

        FlashMessage message = null;
    	try {
	
	        LearnerProgress learnerProgress = LearningWebUtil.getLearnerProgress(request,learnerService);
	        
	        if(log.isDebugEnabled())
	            log.debug("Exiting lesson, lesson id is: "+learnerProgress.getLesson().getLessonId());
	        
	        learnerService.exitLesson(learnerProgress.getLearnerProgressId());
	        
	        //send acknowledgment to flash as it is triggered by flash
	        message = new FlashMessage("exitLesson",true);

    	} catch (Exception e ) {
    		message = handleException(e, "exitLesson", learnerService);
    	}
    	
        String wddxPacket = WDDXProcessor.serialize(message);
        if(log.isDebugEnabled())
            log.debug("Sending Exit Lesson acknowledge message to flash:"+wddxPacket);
        response.getWriter().print(wddxPacket);
        return null;
    }
    
    /**
     * Gets the basic lesson details (name, descripton, etc) for a lesson. Contains a LessonDTO.
     * Takes a single parameter lessonID
     */
    public ActionForward getLesson(ActionMapping mapping,
                                    ActionForm form,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException,
                                                                          ServletException
    {

        //initialize service object
        ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());

        FlashMessage message = null;
    	try {

    		Long lessonID = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);

            if(log.isDebugEnabled())
                log.debug("get lesson..."+lessonID);

            LessonDTO dto = learnerService.getLessonData(lessonID);
	        
	        //send acknowledgment to flash as it is triggerred by flash
	        message = new FlashMessage("getLesson",dto);

    	} catch (Exception e ) {
    		message = handleException(e, "getLesson", learnerService);
    	}
    	
        String wddxPacket = WDDXProcessor.serialize(message);
        if(log.isDebugEnabled())
            log.debug("Sending getLesson data message to flash:"+wddxPacket);
        response.getWriter().print(wddxPacket);
        return null;
    }
    
    /**
     * <p>The struts dispatch action to retrieve the progress data from the 
     * server and tailor it into the object struture that expected by flash.
     * A wddx packet with object data struture is sent back in the end of this 
     * call. It is used to construct or restore the flash learner progress
     * bar</p>
     * 
     * <p>Gets the most recent copy from the database - not the cached version.
     * That way if the cached version has problems, at least we start off right!
     * </p>
     * 
     * <p>As this process is expensive, the server is only expecting this call
     * whenever is necessary. For example, starting, resuming and restoring
     * a new lesson. And it should not happen every time that learner is
     * progressing to next activity.</p>
     * 
     * @param mapping An ActionMapping class that will be used by the Action class to tell
     * the ActionServlet where to send the end-user.
     * 
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward getFlashProgressData(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        if(log.isDebugEnabled())
            log.debug("Getting Flash progress data...");
        
        FlashMessage message = null;
    	try {
	
	        //SessionBean sessionBean = LearningWebUtil.getSessionBean(request,getServlet().getServletContext());
    		
	        Integer learnerId = LearningWebUtil.getUserId();
	        ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());
	        
		    Long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID );
		    LearnerProgressDTO learnerProgress = learnerService.getProgressDTOByLessonId(lessonId, learnerId);
	        
	        message = new FlashMessage("getFlashProgressData",learnerProgress);

    	} catch (Exception e ) {
    		message = handleException(e, "getFlashProgressData", LearnerServiceProxy.getLearnerService(getServlet().getServletContext()));
    	}
    	
        String wddxPacket = WDDXProcessor.serialize(message);
        if(log.isDebugEnabled())
            log.debug("Sending learner progress data to flash:"+wddxPacket);
        response.getWriter().print(wddxPacket);

        //don't need to return a action forward because it sent the wddx packet
        //back already.
        return null;
    }
   
    /**
     * <p>The struts dispatch action to view the activity. This will be called 
     * by flash progress bar to check up the activity component. The lams side 
     * will calculate the url and send a flash message back to the 
     * flash component.</p>
     * 
     * @param mapping An ActionMapping class that will be used by the Action class to tell
     * the ActionServlet where to send the end-user.
     * 
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward getLearnerActivityURL(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws IOException,
                                                                          	ServletException
    {
        if(log.isDebugEnabled())
            log.debug("Getting url for learner activity...");

        FlashMessage message = null;
    	try {

    		// get the activity id and calculate the url for this activity.
	        long activityId = WebUtil.readLongParam(request,AttributeNames.PARAM_ACTIVITY_ID);
	        String url = getLearnerActivityURL(request, activityId); 
	        
	        //send data back to flash.
	        ProgressActivityDTO activityDTO = new ProgressActivityDTO(new Long(activityId),url);
	        message = new FlashMessage("getLearnerActivityURL",activityDTO);

    	} catch (Exception e ) {
    		message = handleException(e, "getLearnerActivityURL", LearnerServiceProxy.getLearnerService(getServlet().getServletContext()));
    	}
    	
        String wddxPacket = WDDXProcessor.serialize(message);
        if(log.isDebugEnabled())
            log.debug("Sending learner activity url data to flash:"+wddxPacket);
        
        response.getWriter().print(wddxPacket);
        return null;
    }

	/**
	 * @param request
	 * @param activityId
	 * @return
	 */
	private String getLearnerActivityURL(HttpServletRequest request, long activityId) {
		//initialize service object
		ActivityMapping activityMapping = LearnerServiceProxy.getActivityMapping(this.getServlet().getServletContext());
		ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());

		//getting requested object according to coming parameters
		Integer learnerId = LearningWebUtil.getUserId();
		User learner = (User)LearnerServiceProxy.getUserManagementService(getServlet().getServletContext()).findById(User.class,learnerId);

		Activity requestedActivity = learnerService.getActivity(new Long(activityId));
		Lesson lesson = learnerService.getLessonByActivity(requestedActivity);
		String url = activityMapping.calculateActivityURLForProgressView(lesson,learner,requestedActivity);
		return url;
	}
    
    /**
     * Gets the same url as getLearnerActivityURL() but forwards directly to the url, rather than 
     * returning the url in a Flash packet.
     * 
     * @param mapping An ActionMapping class that will be used by the Action class to tell
     * the ActionServlet where to send the end-user.
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward forwardToLearnerActivityURL(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws IOException,
                                                                          	ServletException
    {
        long activityId = WebUtil.readLongParam(request,AttributeNames.PARAM_ACTIVITY_ID);
        if(log.isDebugEnabled())
            log.debug("Forwarding to the url for learner activity..."+activityId);
        
        String url = getLearnerActivityURL(request, activityId);
       	return redirectToURL(mapping, response, url);
    }
	
    /**
     * Forces a move to a destination Activity in the learning sequence.
     * 
     * @param mapping An ActionMapping class that will be used by the Action class to tell
     * the ActionServlet where to send the end-user.
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward forceMove(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                                 ServletException {
    	FlashMessage flashMessage = null;
    	
    	//initialize service object
		ActivityMapping activityMapping = LearnerServiceProxy.getActivityMapping(this.getServlet().getServletContext());
		ICoreLearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());

		//getting requested object according to coming parameters
		Integer learnerId = LearningWebUtil.getUserId();
		User learner = (User)LearnerServiceProxy.getUserManagementService(getServlet().getServletContext()).findById(User.class,learnerId);

		//get parameters
    	Long fromActivityId = null;
    	Long toActivityId = null;
    	
    	String fromActId = request.getParameter(AttributeNames.PARAM_CURRENT_ACTIVITY_ID);
    	String toActId = request.getParameter(AttributeNames.PARAM_DEST_ACTIVITY_ID);
    	if(fromActId != null)
    		try{
    			fromActivityId = new Long(Long.parseLong(fromActId));
    		}catch(Exception e){
    			fromActivityId = null;
    		}
    	
    	if(toActId != null)
        	try{
        		toActivityId = new Long(Long.parseLong(toActId));
        	}catch(Exception e){
        		toActivityId = null;
        	}
    		
    	//force complete
    	try {
        	long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);
            
        	Activity fromActivity = null;
        	Activity toActivity = null;
        	
        	if(fromActivityId != null)
        		fromActivity = learnerService.getActivity(fromActivityId);
        	
        	if(toActivityId != null)
        		toActivity = learnerService.getActivity(toActivityId);
        	 
        	learnerService.moveToActivity(learnerId, new Long(lessonId), fromActivity, toActivity);
        	
    		if ( log.isDebugEnabled() ) {
    			log.debug("Force move for learner "+learnerId+" lesson "+lessonId+". ");
    		}
    		flashMessage = new FlashMessage("forceMove", mapping.findForward(DISPLAY_ACTIVITY).getPath());
		} catch (Exception e) {
			flashMessage = handleException(e, "forceMove", learnerService);
		}
		String message =  flashMessage.serializeMessage();
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    	
    }
    
	/**
	 * Get AuditService bean.
	 * @return
	 */
	private IAuditService getAuditService(){
		if(auditService==null){
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
			auditService = (IAuditService) ctx.getBean("auditService");
		}
		return auditService;
	}
    
}