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
package org.lamsfoundation.lams.monitoring.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.monitoring.MonitoringConstants;
import org.lamsfoundation.lams.monitoring.service.IMonitoringService;
import org.lamsfoundation.lams.monitoring.service.MonitoringServiceProxy;
import org.lamsfoundation.lams.tool.exception.LamsToolServiceException;
import org.lamsfoundation.lams.usermanagement.exception.UserAccessDeniedException;
import org.lamsfoundation.lams.util.DateUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.util.audit.IAuditService;
import org.lamsfoundation.lams.util.wddx.FlashMessage;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * <p>The action servlet that provide all the monitoring functionalities. It
 * interact with the teacher via flash and JSP monitoring interface.</p>
 * 
 * @author Jacky Fang
 * @since  2005-4-15
 * @version 1.1
 * 
 * ----------------XDoclet Tags--------------------
 * 
 * @struts:action path="/monitoring" 
 *                parameter="method" 
 *                validate="false"
 * @struts.action-forward name = "previewdeleted" path = "/previewdeleted.jsp"
 * @struts.action-forward name = "notsupported" path = ".notsupported"
 * 
 * ----------------XDoclet Tags--------------------
 */
public class MonitoringAction extends LamsDispatchAction
{
		
	//---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	
    //---------------------------------------------------------------------
    // Class level constants - Struts forward
    //---------------------------------------------------------------------
	private static final String PREVIEW_DELETED_REPORT_SCREEN = "previewdeleted";
	private static final String NOT_SUPPORTED_SCREEN = "notsupported";
	
	/** See deleteOldPreviewLessons */
	public static final String NUM_DELETED = "numDeleted";
	
	private static IAuditService auditService;

	private Integer getUserId(HttpServletRequest request) {
		return new Integer(WebUtil.readIntParam(request,"userID"));
/*		HttpSession ss = SessionManager.getSession();
		UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		return user != null ? user.getUserID() : null;
	*/
	}
	
 	  private FlashMessage handleException(Exception e, String methodKey, IMonitoringService monitoringService) {
			log.error("Exception thrown "+methodKey,e);
			auditService = getAuditService();
			auditService.log(MonitoringAction.class.getName()+":"+methodKey, e.toString());
			
			if ( e instanceof UserAccessDeniedException ) {
				return new FlashMessage(methodKey,
					monitoringService.getMessageService().getMessage("error.user.noprivilege"),
					FlashMessage.ERROR);
			} else {
				String[] msg = new String[1];
				msg[0] = e.getMessage();
				return new FlashMessage(methodKey,
					monitoringService.getMessageService().getMessage("error.system.error", msg),
					FlashMessage.CRITICAL_ERROR);
			}
	    }
	 
	  private FlashMessage handleCriticalError(String methodKey, String messageKey, IMonitoringService monitoringService) {
		  String message = monitoringService.getMessageService().getMessage(messageKey); 
			log.error("Error occured "+methodKey+" error ");
			auditService = getAuditService();
			auditService.log(MonitoringAction.class.getName()+":"+methodKey, message);
			
			return new FlashMessage(methodKey,
					message,
					FlashMessage.CRITICAL_ERROR);
      }
	 
	  private ActionForward redirectToURL(ActionMapping mapping, HttpServletResponse response, String url) throws IOException {
		  if ( url != null ) {
			  String fullURL = WebUtil.convertToFullURL(url);
			  response.sendRedirect(response.encodeRedirectURL(fullURL));
			  return null;
		  } else {
			  return mapping.findForward(NOT_SUPPORTED_SCREEN);
		  }
	 }

	  //---------------------------------------------------------------------
    // Struts Dispatch Method
    //---------------------------------------------------------------------
    /**
     * This STRUTS action method will initialize a lesson for specific learning design with the given lesson title
     * and lesson description.<p>
     * If initialization is successed, this method will return a WDDX message which includes the ID of new lesson.
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
    public ActionForward initializeLesson(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws IOException,
                                                                          ServletException
    {

    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
        FlashMessage flashMessage = null;
    	
    	try {
    		String title = WebUtil.readStrParam(request,"lessonName");
    		if ( title == null ) title = "lesson";
    		String desc = WebUtil.readStrParam(request,"lessonDescription", true);
    		if ( desc == null ) desc = "description";
    		Integer organisationId = WebUtil.readIntParam(request,"organisationID",true);
    		long ldId = WebUtil.readLongParam(request, AttributeNames.PARAM_LEARNINGDESIGN_ID);
    		Lesson newLesson = monitoringService.initializeLesson(title,desc,ldId,organisationId,getUserId(request));
    		
    		flashMessage = new FlashMessage("initializeLesson",newLesson.getLessonId());
		} catch (Exception e) {
			flashMessage = handleException(e, "initializeLesson", monitoringService);
		}
		
		String message =  flashMessage.serializeMessage();
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    }

     
    /**
     * The Struts dispatch method that starts a lesson that has been created
     * beforehand. Most likely, the request to start lesson should be triggered
     * by the flash component. This method will delegate to the Spring service
     * bean to complete all the steps for starting a lesson. Finally, a wddx
     * acknowledgement message will be serialized and sent back to the flash
     * component.
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
    public ActionForward startLesson(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	FlashMessage flashMessage = null;
    	
    	try {
            long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
    		monitoringService.startLesson(lessonId, getUserId(request));
    		flashMessage = new FlashMessage("startLesson",Boolean.TRUE);
		} catch (Exception e) {
			flashMessage = handleException(e, "startLesson", monitoringService);
		}
		
		String message =  flashMessage.serializeMessage();
				
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    }
    /**
     * The Struts dispatch method that starts a lesson on schedule that has been created
     * beforehand.
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
     * @throws  
     */
    public ActionForward startOnScheduleLesson(ActionMapping mapping,
    		ActionForm form,
    		HttpServletRequest request,
    		HttpServletResponse response) throws IOException,
    		ServletException 
    		{
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	FlashMessage flashMessage = null;
    	
    	try {
        	long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
        	
        	String dateStr = WebUtil.readStrParam(request, MonitoringConstants.PARAM_LESSON_START_DATE);
    		Date startDate = DateUtil.convertFromLAMSFlashFormat(dateStr);
    		
    		monitoringService.startLessonOnSchedule(lessonId,startDate,getUserId(request));
    		flashMessage = new FlashMessage("startOnScheduleLesson",Boolean.TRUE);
    	}catch (Exception e) {
			flashMessage = handleException(e, "startOnScheduleLesson", monitoringService);
    	}
    	
    	String message =  flashMessage.serializeMessage();
    	
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    }
    
    /**
     * The Struts dispatch method that finsh a lesson on schedule that has been started
     * beforehand.
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
     * @throws  
     */
    public ActionForward finishOnScheduleLesson(ActionMapping mapping,
    		ActionForm form,
    		HttpServletRequest request,
    		HttpServletResponse response) throws IOException,
    		ServletException 
    		{
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	FlashMessage flashMessage = null;
    	
    	try {
        	long lessonId = WebUtil.readLongParam(request, AttributeNames.PARAM_LESSON_ID);
        	String dateStr = WebUtil.readStrParam(request, MonitoringConstants.PARAM_LESSON_FINISH_DATE);
    		Date finishDate = DateFormat.getInstance().parse(dateStr);
    		monitoringService.finishLessonOnSchedule(lessonId,finishDate,getUserId(request));
    		flashMessage = new FlashMessage("finishOnScheduleLesson",Boolean.TRUE);
    	}catch (Exception e) {
			flashMessage = handleException(e, "finishOnScheduleLesson", monitoringService);
    	}
    	
    	String message =  flashMessage.serializeMessage();	
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    }
    
    /**
     * The Struts dispatch method to archive a lesson.  A wddx
     * acknowledgement message will be serialized and sent back to the flash
     * component.
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
    public ActionForward archiveLesson(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
    	FlashMessage flashMessage = null;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	
    	try {
        	long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);
    		monitoringService.archiveLesson(lessonId, getUserId(request));
    		flashMessage = new FlashMessage("archiveLesson",Boolean.TRUE);
		} catch (Exception e) {
			flashMessage = handleException(e, "archiveLesson", monitoringService);
		}
		
		String message =  flashMessage.serializeMessage();
		
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    }
    /**
     * The Struts dispatch method to "unarchive" a lesson. Returns it back to 
     * its previous state. A wddx acknowledgement message will be serialized and sent back to the flash
     * component.
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
    public ActionForward unarchiveLesson(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
    	FlashMessage flashMessage = null;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	
    	try {
        	long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);
    		monitoringService.unarchiveLesson(lessonId, getUserId(request));
    		flashMessage = new FlashMessage("unarchiveLesson",Boolean.TRUE);
		} catch (Exception e) {
			flashMessage = handleException(e, "unarchiveLesson", monitoringService);
		}
		
		String message =  flashMessage.serializeMessage();
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    }
    /**
     * The purpose of suspending is to hide the lesson from learners temporarily. 
     * It doesn't make any sense to suspend a created or a not started (ie scheduled) 
     * lesson as they will not be shown on the learner interface anyway! If the teacher 
     * tries to suspend a lesson that is not in the STARTED_STATE, then an error should 
     * be returned to Flash. 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward suspendLesson(ActionMapping mapping,
    		ActionForm form,
    		HttpServletRequest request,
    		HttpServletResponse response) throws IOException,
    		ServletException
    		{
    	FlashMessage flashMessage = null;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	
    	try {
        	long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);
    		monitoringService.suspendLesson(lessonId, getUserId(request));
    		flashMessage = new FlashMessage("suspendLesson",Boolean.TRUE);
    	} catch (Exception e) {
			flashMessage = handleException(e, "suspendLesson", monitoringService);
    	}
    	
    	String message =  flashMessage.serializeMessage();
    	
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
   }
    /**
     * Unsuspend a lesson which state must be Lesson.SUPSENDED_STATE. Otherwise a error message will return to 
     * flash client.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward unsuspendLesson(ActionMapping mapping,
    		ActionForm form,
    		HttpServletRequest request,
    		HttpServletResponse response) throws IOException,
    		ServletException
    		{
    	FlashMessage flashMessage = null;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	
    	try {
        	long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);
    		monitoringService.unsuspendLesson(lessonId, getUserId(request));
    		flashMessage = new FlashMessage("unsuspendLesson",Boolean.TRUE);
    	} catch (Exception e) {
			flashMessage = handleException(e, "unsuspendLesson", monitoringService);
    	}
    	
    	String message =  flashMessage.serializeMessage();
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    		}
    /**
     * <P>
     * The STRUTS action will send back a WDDX message after marking the lesson by the given lesson ID
     * as <code>Lesson.REMOVED_STATE</code> status.
     * </P>
     * <P>
     * This action need a lession ID as input.
     * </P>
     * @param form The ActionForm class that will contain any data submitted
     * by the end-user via a form.
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where
     *         the user is to go next.
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward removeLesson(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                                 ServletException{
    	FlashMessage flashMessage = null;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	
    	try {
        	long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);
    		monitoringService.removeLesson(lessonId, getUserId(request));
    		flashMessage = new FlashMessage("removeLesson",Boolean.TRUE);
		} catch (Exception e) {
			flashMessage = handleException(e, "removeLesson", monitoringService);
		}
		String message =  flashMessage.serializeMessage();
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    	
    }
    /**
     * <P>
     * </P>
     * <P>
     * This action need a lession ID, Learner ID and Activity ID as input. Activity ID is optional, if it is 
     * null, all activities for this learner will complete to as end as possible.
     * </P>
     * @param form 
     * @param request A standard Servlet HttpServletRequest class.
     * @param response A standard Servlet HttpServletResponse class.
     * @return An ActionForward
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward forceComplete(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                                 ServletException{
    	FlashMessage flashMessage = null;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	//get parameters
    	Long activityId = null;
    	String actId = request.getParameter(AttributeNames.PARAM_ACTIVITY_ID);
    	if(actId != null)
    		try{
    			activityId = new Long(Long.parseLong(actId));
    		}catch(Exception e){
    			activityId = null;
    		}
    	
    	//force complete
    	try {
        	long lessonId = WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID);
        	Integer learnerId = new Integer(WebUtil.readIntParam(request,MonitoringConstants.PARAM_LEARNER_ID));
    		String message = monitoringService.forceCompleteLessonByUser(learnerId,lessonId,activityId);
    		if ( log.isDebugEnabled() ) {
    			log.debug("Force complete for learner "+learnerId+" lesson "+lessonId+". "+message);
    		}
    		flashMessage = new FlashMessage("forceComplete",message);
		} catch (Exception e) {
			flashMessage = handleException(e, "forceComplete", monitoringService);
		}
		String message =  flashMessage.serializeMessage();
		
        PrintWriter writer = response.getWriter();
        writer.println(message);
        return null;
    	
    }
    
     public ActionForward getLessonDetails(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException{
    	 
    	String wddxPacket;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	try{
	    	Long lessonID = new Long(WebUtil.readLongParam(request,"lessonID"));
	    	wddxPacket = monitoringService.getLessonDetails(lessonID, getUserId(request));
     	}catch (Exception e) {
     		wddxPacket = handleException(e, "getLessonDetails", monitoringService).serializeMessage();
    	}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }
    
    public ActionForward getLessonLearners(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException{
    	String wddxPacket;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	try{
	    	Long lessonID = new Long(WebUtil.readLongParam(request,"lessonID"));
	    	wddxPacket = monitoringService.getLessonLearners(lessonID);
    	}catch (Exception e) {
     		wddxPacket = handleException(e, "getLessonLearners", monitoringService).serializeMessage();
    	}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }
    
    public ActionForward getLessonStaff(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException{
    	String wddxPacket;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	try{
    		Long lessonID = new Long(WebUtil.readLongParam(request,"lessonID"));
    		wddxPacket = monitoringService.getLessonStaff(lessonID);
    	}catch (Exception e) {
     		wddxPacket = handleException(e, "getLessonStaff", monitoringService).serializeMessage();
    	}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }
    
    public ActionForward getLearningDesignDetails(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException{
    	String wddxPacket;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	try{
    		Long lessonID = new Long(WebUtil.readLongParam(request,"lessonID"));
    		wddxPacket = monitoringService.getLearningDesignDetails(lessonID);
     	}catch (Exception e) {
     		wddxPacket = handleException(e, "getLearningDesignDetails", monitoringService).serializeMessage();
    	}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }
    public ActionForward getAllLearnersProgress(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException{
    	String wddxPacket;
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	try{
    		Long lessonID = new Long(WebUtil.readLongParam(request,"lessonID"));
    		wddxPacket = monitoringService.getAllLearnersProgress(lessonID);
     	}catch (Exception e) {
     		wddxPacket = handleException(e, "getAllLearnersProgress", monitoringService).serializeMessage();
    	}
    		
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }
    public ActionForward getAllContributeActivities(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException{
    	
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	String wddxPacket = null;
    	try {
	    	Long lessonID = new Long(WebUtil.readLongParam(request,"lessonID"));
	    	wddxPacket = monitoringService.getAllContributeActivities(lessonID);
    	} catch (Exception e) {
			FlashMessage flashMessage = handleException(e, "getAllContributeActivities", monitoringService);
			wddxPacket = flashMessage.serializeMessage();
		}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }
    
    /** Calls the server to bring up the learner progress page. Assumes destination is a new window */
    public ActionForward getLearnerActivityURL(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException,LamsToolServiceException{
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	Integer userID = new Integer(WebUtil.readIntParam(request,"userID"));
    	Long activityID = new Long(WebUtil.readLongParam(request,"activityID"));
    	Long lessonID = new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID));
    	
    	String url = monitoringService.getLearnerActivityURL(lessonID,activityID,userID);
    	return redirectToURL(mapping, response, url);
    }
    /** Calls the server to bring up the activity's monitoring page. Assumes destination is a new window */
    public ActionForward getActivityMonitorURL(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException,LamsToolServiceException{
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	Long activityID = new Long(WebUtil.readLongParam(request,"activityID"));
    	Long lessonID = new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID));
    	String contentFolderID = WebUtil.readStrParam(request, "contentFolderID");
    	String url = monitoringService.getActivityMonitorURL(lessonID,activityID,contentFolderID);
    	
    	return redirectToURL(mapping, response, url);
    }
    /** Calls the server to bring up the activity's define later page. Assumes destination is a new window */
    public ActionForward getActivityDefineLaterURL(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException,LamsToolServiceException{
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	Long activityID = new Long(WebUtil.readLongParam(request,"activityID"));
    	Long lessonID = new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_LESSON_ID));
    	
    	String url = monitoringService.getActivityDefineLaterURL(lessonID,activityID);
    	return redirectToURL(mapping, response, url);
    }

    public ActionForward moveLesson(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException{
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	String wddxPacket = null;
    	try {
    		Long lessonID = new Long(WebUtil.readLongParam(request,"lessonID"));
	    	Integer userID = new Integer(WebUtil.readIntParam(request,"userID"));
	    	Integer targetWorkspaceFolderID = new Integer(WebUtil.readIntParam(request,"folderID"));
	    	wddxPacket = monitoringService.moveLesson(lessonID,targetWorkspaceFolderID,userID);
    	} catch (Exception e) {
			FlashMessage flashMessage = handleException(e, "moveLesson", monitoringService);
			wddxPacket = flashMessage.serializeMessage();
		}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }
    public ActionForward renameLesson(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)throws IOException{
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	String wddxPacket = null;
    	try {
    		Long lessonID = new Long(WebUtil.readLongParam(request,"lessonID"));
	    	Integer userID = new Integer(WebUtil.readIntParam(request,"userID"));
	    	String name = WebUtil.readStrParam(request,"name"); 
	    	wddxPacket = monitoringService.renameLesson(lessonID,name,userID);
    	} catch (Exception e) {
			FlashMessage flashMessage = handleException(e, "renameLesson", monitoringService);
			wddxPacket = flashMessage.serializeMessage();
		}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }
    
    public ActionForward checkGateStatus(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	String wddxPacket = null;
    	try {
    		Long activityID = new Long(WebUtil.readLongParam(request, "activityID"));
    		Long lessonID = new Long(WebUtil.readLongParam(request, "lessonID"));
    		wddxPacket = monitoringService.checkGateStatus(activityID, lessonID);
    	} catch (Exception e) {
			FlashMessage flashMessage = handleException(e, "checkGateStatus", monitoringService);
			wddxPacket = flashMessage.serializeMessage();
		}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
        
    }
    
    public ActionForward releaseGate(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
    	IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
    	String wddxPacket = null;
    	try {
    		Long activityID = new Long(WebUtil.readLongParam(request, "activityID"));
    		wddxPacket = monitoringService.releaseGate(activityID);
    	} catch (Exception e) {
			FlashMessage flashMessage = handleException(e, "releaseGate", monitoringService);
			wddxPacket = flashMessage.serializeMessage();
		}
        PrintWriter writer = response.getWriter();
        writer.println(wddxPacket);
        return null;
    }

	public ActionForward startPreviewLesson(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
		IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
		FlashMessage flashMessage = null;
		
		try {
		
			int userID = WebUtil.readIntParam(request,AttributeNames.PARAM_USER_ID); 
			long learningDesignID = WebUtil.readLongParam(request,AttributeNames.PARAM_LEARNINGDESIGN_ID);
			String title = WebUtil.readStrParam(request,"title");
			String desc = WebUtil.readStrParam(request,"description"); 
			
	        // initialize the lesson
	        Lesson previewLesson = monitoringService.initializeLessonForPreview(title,desc,learningDesignID,new Integer(userID));
	        if ( previewLesson != null ) {

	        	long lessonID = previewLesson.getLessonId().longValue();
				
	        	monitoringService.createPreviewClassForLesson(userID, lessonID);
		        monitoringService.startLesson(lessonID, getUserId(request));
		
				flashMessage = new FlashMessage("startPreviewSession",new Long(lessonID));
				
	        } else {
				flashMessage = handleCriticalError("startPreviewSession", "error.system.error", monitoringService);
	        }
			
		} catch (Exception e) {
			flashMessage = handleException(e, "startPreviewSession", monitoringService);
		}
		
		PrintWriter writer = response.getWriter();
		writer.println(flashMessage.serializeMessage());
		return null;
	}

	/** Delete all old preview lessons and their related data, across all
	 * organisations.
	 *  Should go to a monitoring webservice maybe ? */
	public ActionForward deleteOldPreviewLessons(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		IMonitoringService monitoringService = MonitoringServiceProxy.getMonitoringService(getServlet().getServletContext());
		int numDeleted = monitoringService.deleteAllOldPreviewLessons();
		request.setAttribute(NUM_DELETED, Integer.toString(numDeleted));
		return mapping.findForward(PREVIEW_DELETED_REPORT_SCREEN);
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
