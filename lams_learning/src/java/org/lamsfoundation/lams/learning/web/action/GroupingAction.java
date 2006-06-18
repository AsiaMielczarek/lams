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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.lamsfoundation.lams.learning.service.ILearnerService;
import org.lamsfoundation.lams.learning.service.LearnerServiceException;
import org.lamsfoundation.lams.learning.service.LearnerServiceProxy;
import org.lamsfoundation.lams.learning.web.util.LearningWebUtil;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.GroupComparator;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;


/**
 * 
 * <p>The action servlet that triggers the system driven grouping
 * (random grouping) and allows the learner to view the result of the grouping.
 * </p>
 * 
 * <p>Has a special override key - if the parameter force is set and the 
 * lesson is a preview lesson, any chosen grouping will be overridden. 
 * </p>
 * @author Jacky Fang
 * @since  2005-3-29
 * @version 1.1
 * 
 * ----------------XDoclet Tags--------------------
 * 
 * @struts:action name = "GroupingForm"
 * 				  path="/grouping" 
 *                parameter="method" 
 *                validate="false"
 * @struts.action-exception key="error.system.learner" scope="request"
 *                          type="org.lamsfoundation.lams.learning.service.LearnerServiceException"
 *                          path=".systemError"
 * 							handler="org.lamsfoundation.lams.learning.util.CustomStrutsExceptionHandler"
 * @struts:action-forward name="viewGroup" path="/grouping.do?method=viewGrouping"
 * @struts:action-forward name="showGroup" path=".showgroup"
 * @struts:action-forward name="waitGroup" path=".waitgroup"
 * ----------------XDoclet Tags--------------------
 * 
 */
public class GroupingAction extends LamsDispatchAction
{

    /** Input parameter. Boolean value */
    public static final String PARAM_FORCE_GROUPING  = "force";

    //---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	private static Logger log = Logger.getLogger(GroupingAction.class);
    //---------------------------------------------------------------------
    // Class level constants - Session Attributes
    //---------------------------------------------------------------------
	private static final String GROUPS = "groups";
	
    //---------------------------------------------------------------------
    // Class level constants - Struts forward
    //---------------------------------------------------------------------
    private static final String VIEW_GROUP = "viewGroup";
    private static final String WAIT_GROUP = "waitGroup";
    private static final String SHOW_GROUP = "showGroup";
    
    //---------------------------------------------------------------------
    // Struts Dispatch Method
    //---------------------------------------------------------------------    
    /**
     * Perform the grouping for the users who are currently running the lesson.
     * If force is set to true, then we should be in preview mode, and we want to 
     * override the chosen grouping to make it group straight away.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return 
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward performGrouping(ActionMapping mapping,
                                         ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException,
                                                                          ServletException
    {

        boolean forceGroup = WebUtil.readBooleanParam(request,PARAM_FORCE_GROUPING,false);

        //initialize service object
        ILearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());
        LearnerProgress learnerProgress = LearningWebUtil.getLearnerProgress(request, learnerService);
        validateLearnerProgress(learnerProgress);
        
        boolean groupingDone = learnerService.performGrouping(learnerProgress.getLesson().getLessonId(),
        								learnerProgress.getNextActivity().getActivityId(), 
        								LearningWebUtil.getUserId(),forceGroup);

        LearningWebUtil.putActivityInRequest(request, learnerProgress.getNextActivity(), learnerService);
        
        DynaActionForm groupForm = (DynaActionForm)form;
        groupForm.set("previewLesson",learnerProgress.getLesson().isPreviewLesson());
        return mapping.findForward(groupingDone ? VIEW_GROUP : WAIT_GROUP);
    }

    /**
     * Load up the grouping information and forward to the jsp page to display
     * all the groups and members.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward viewGrouping(ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        //initialize service object
        ILearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());
     
        SortedSet groups = new TreeSet(new GroupComparator());

        Activity activity = LearningWebUtil.getActivityFromRequest(request,learnerService);
        Grouping grouping = ((GroupingActivity)activity).getCreateGrouping();
        if ( grouping != null)
        	groups.addAll(grouping.getGroups());
        
        request.getSession().setAttribute(GROUPS,groups);
        request.setAttribute(AttributeNames.PARAM_ACTIVITY_ID,	activity.getActivityId());
        
        return mapping.findForward(SHOW_GROUP);
    }

    /**
     * Complete the current tool activity and forward to the url of next activity
     * in the learning design.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward completeActivity(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws IOException,
                                                                          ServletException
    {
        //initialize service object
        ILearnerService learnerService = LearnerServiceProxy.getLearnerService(getServlet().getServletContext());
        LearnerProgress learnerProgress = LearningWebUtil.getLearnerProgress(request,learnerService);
        Activity groupingActivity = LearningWebUtil.getActivityFromRequest(request,learnerService);

        String nextActivityUrl = learnerService.completeActivity(learnerProgress.getUser().getUserId(),
                                                                  groupingActivity);
		response.sendRedirect(nextActivityUrl);
        return null;
    }
    //---------------------------------------------------------------------
    // Helper method
    //---------------------------------------------------------------------
    /**
     * @param learnerProgress
     */
    private void validateLearnerProgress(LearnerProgress learnerProgress)
    {
        if(learnerProgress ==null)
            throw new LearnerServiceException("Can't perform grouping without knowing" +
            		" the learner progress.");
        
        if(!isNextActivityValid(learnerProgress))
            throw new LearnerServiceException("Error in progress engine. Getting "
                                              +learnerProgress.getNextActivity().toString()
                                              +" where it should be grouping activity");
    }


    /**
     * Validate the next activity within the learner progress. It should not
     * be null and it should be the grouping activity.
     * @param learnerProgress the learner progress for current learner.
     * @return whether the next activity is valid.
     */
    private boolean isNextActivityValid(LearnerProgress learnerProgress)
    {
        return learnerProgress.getNextActivity()!=null&&(learnerProgress.getNextActivity().isGroupingActivity());
    }

}
