/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * ***********************************************************************/


package org.lamsfoundation.lams.tool.service;


import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.ToolActivity;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.ToolSession;
import org.lamsfoundation.lams.usermanagement.User;

/**
 * <p>This interface defines the service that lams tool package offers to other
 * lams core modules, such as, lams_learning, lams_authoring, lams_monitoring.</p>
 * 
 * <p>It doesn't have the tool service that will be called by the tool.</p>
 * 
 * 
 * @author Jacky Fang
 * @since  2005-3-17
 * @version 1.1
 */
public interface ILamsCoreToolService
{
    /**
     * Creates a Lams ToolSession for a learner and activity.
     * @param learner the learner who is running the activity.
     * @param activity the requested activity.
     */
    public ToolSession createToolSession(User learner, ToolActivity activity,Lesson lesson) throws LamsToolServiceException;
    
    /**
     * Returns the previously created ToolSession for a learner and activity.
     * It is queried base on learner.
     * @param learner the learner who owns the tool session.
     * @param activity the activity that associate with the tool session.
     * @return the requested tool session.
     * @throws LamsToolServiceException the known error condition when we 
     * 									are getting the tool session
     */
    public ToolSession getToolSessionByLearner(User learner, Activity activity) throws LamsToolServiceException;
    
    /**
     * Returns the tool session according to tool session id.
     * @param toolSessionId the requested tool session id.
     * @return the tool session object
     */
    public ToolSession getToolSessionById(Long toolSessionId);
    
    /**
     * Get the lams tool session based on activity. It search through all
     * the tool sessions that linked to the requested activity and return
     * the tool session with requested learner information.
     * 
     * @param learner the requested learner
     * @param toolActivity the requested activity.
     * @return the tool session.
     * @throws LamsToolServiceException the known error condition when we 
     * 									are getting the tool session
     */
    public ToolSession getToolSessionByActivity(User learner, ToolActivity toolActivity)throws LamsToolServiceException;
    
    /**
     * Notify tools to create their tool sessions in their own tables. 
     * @param toolSessionId the tool session generated by lams.
     * @param activity the activity correspondent to that tool session.
     */
    public void notifyToolsToCreateSession(Long toolSessionId, ToolActivity activity);
    
    /**
     * Notify a tool to make a copy of its own content. Lams needs to dynamically
     * load tool's service by request and invoke the copy method from tool
     * content manager. 
     * @param toolActivity the requested tool activity.
     * @return new tool content id.
     */
    public Long notifyToolToCopyContent(ToolActivity toolActivity);
    
    /**
     * Update the tool session data.
     * @param toolSession the new tool session object.
     */
    public void updateToolSession(ToolSession toolSession);

    /**
     * Return tool activity url for a learner according to the access mode.
     * For example, the monitor side sould passed in the teacher mode and 
     * learning side sould pass in the learner mode.
     * @param activity the requested activity.
     * @param learner the current learner.
     * @param accessMode tool access mode.
     * @return the tool access url with tool session id and access mode.
     */
    public String getLearnerToolURLByMode(ToolActivity activity, User learner, ToolAccessMode accessMode) throws LamsToolServiceException;

    /**
     * <p>Setup target tool url with tool session id parameter based on the tool
     * activity and learner.</p>
     * 
     * @param activity the activity that requested tool session belongs to.
     * @param learner the user who invloved the tool session.
     * @param toolURL the target url.
     * @throws LamsToolServiceException
     * @return the url with tool session id.
     */
    public String setupToolURLWithToolSession(ToolActivity activity,User learner,String toolURL) throws LamsToolServiceException;
    
    /**
     * <p>Setup target tool url with tool content id parameter based on the tool
     * activity and learner.</p>
     * @param activity the requested activity.
     * @param toolURL the target url
     * @return the url with tool content id.
     */
    public String setupToolURLWithToolContent(ToolActivity activity,String toolURL);
}
