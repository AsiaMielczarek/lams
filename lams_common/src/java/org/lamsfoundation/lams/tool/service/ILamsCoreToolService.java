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
package org.lamsfoundation.lams.tool.service;


import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.ToolActivity;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.tool.Tool;
import org.lamsfoundation.lams.tool.ToolOutput;
import org.lamsfoundation.lams.tool.ToolOutputDefinition;
import org.lamsfoundation.lams.tool.ToolSession;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.LamsToolServiceException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.usermanagement.User;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

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
     * Creates a LAMS ToolSession for a learner and activity. Checks
     * to see if an appropriate tool session exists for each learner
     * before creating the tool session. 
     * <p>
     * If an appropriate tool session already exists for a learner, then 
     * it returns null.
     * <p>
     * Sets up the tool session based on the groupingSupportType. 
     * @see org.lamsfoundation.lams.learningdesign.ToolActivity#createToolSessionForActivity(org.lamsfoundation.lams.usermanagement.User,org.lamsfoundation.lams.lesson.Lesson)
     *
     * @param learner the learner who is running the activity.
     * @param activity the requested activity.
     * @return toolSession if a new one created, null otherwise.
     */
    public ToolSession createToolSession(User learner, ToolActivity activity,Lesson lesson) throws LamsToolServiceException;

    /**
     * Creates LAMS ToolSessions for a set of learners and activity. Checks
     * to see if an appropriate tool session exists for each learner
     * before creating the tool session.
     * <p>
     * If an appropriate tool session already exists for a learner, then 
     * it does not include the tool session in the returned set.
     * <p>
     * @param learners the learners who are running the activity.
     * @param activity the requested activity.
     * @return toolSessions set of newly created ToolSessions
     */
    public Set createToolSessions(Set learners, ToolActivity activity,Lesson lesson) throws LamsToolServiceException;

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
     * @param toolSession the tool session generated by lams.
     * @param activity the activity correspondent to that tool session.
     */
    public void notifyToolsToCreateSession(ToolSession toolSession, ToolActivity activity)
    	throws ToolException;
    
    /**
     * Calls the tool to copy the content for an activity. Used when copying a learning design.
     * If it is a preview lesson, we don't want to set define later - we will sidestep this in the progress engine.
     * 
     * @param toolActivity the tool activity defined in the design.
     * @param setDefineLater whether or not to set the define later flag.
     * @throws DataMissingException, ToolException
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#notifyToolToCopyContent(org.lamsfoundation.lams.learningdesign.ToolActivity)
     */
    public Long notifyToolToCopyContent(ToolActivity toolActivity, boolean setDefineLater) 
    		throws DataMissingException, ToolException;
    
    /**
     * Calls the tool to set up the define later and run offline flags.
     * 
     * This method is a subset of the functionality of notifyToolToCopyContent(ToolActivity toolActivity, boolean setDefineLater);
     * so if you call notifyToolToCopyContent then you don't need to call this method. It is a separate method used by Live Edit.
     * 
     * @param toolActivity the tool activity defined in the design.
     * @throws DataMissingException, ToolException
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#notifyToolToCopyContent(org.lamsfoundation.lams.learningdesign.ToolActivity)
     */
    public Long notifyToolOfStatusFlags(ToolActivity toolActivity) 
    		throws DataMissingException, ToolException;

    /**
     * Calls the tool to copy the content for an activity. Used when copying an activity in authoring. Can't
     * use the notifyToolToCopyContent(ToolActivity, boolean) version in authoring as the tool activity won't
     * exist if the user hasn't saved the sequence yet. But the tool content (as that is saved by the 
     * tool) may already exist.
     * 
     * This doesn't set the define later and run offline flags from the design - it only copies the content
     * stored in the tool.
     * 
     * @param toolContentId the content to be copied.
     * @throws DataMissingException, ToolException
     * @see org.lamsfoundation.lams.tool.service.ILamsCoreToolService#notifyToolToCopyContent(org.lamsfoundation.lams.learningdesign.ToolActivity)
     */
    public Long notifyToolToCopyContent(Long toolContentId) 
    		throws DataMissingException, ToolException;
    /**
     * Ask a tool to delete a tool content. If any related tool session data exists then it should 
     * be deleted.
     * 
     * @param toolActivity the tool activity defined in the design.
     * @throws ToolException 
     */
    public void notifyToolToDeleteContent(ToolActivity toolActivity) throws ToolException;

    /**
     * Ask a tool for its OutputDefinitions, based on the given toolContentId. If the tool doesn't
     * have any content matching the toolContentId then it should create the OutputDefinitions based
     * on the tool's default content.
     * 
     * This functionality relies on a method added to the Tool Contract in LAMS 2.1, so if the tool
     * doesn't support the required method, it writes out an error to the log but doesn't throw 
     * an exception - just returns an empty map.
	*
     * @param toolContentId
     * @return SortedMap of ToolOutputDefinitions with the key being the name of each definition
     * @throws ToolException 
     */
    public SortedMap<String, ToolOutputDefinition> getOutputDefinitionsFromTool(Long toolContentId) throws ToolException;
   
    /**
     * Ask a tool for one particular ToolOutput, based on the given toolSessionId. If the tool doesn't
     * have any content matching the toolSessionId then should return an "empty" but valid set of data. e.g
     * an empty mark would be 0.
     * 
     * This functionality relies on a method added to the Tool Contract in LAMS 2.1.
     * 
     * @throws ToolException 
     */
    public ToolOutput getOutputFromTool(String conditionName, Long toolSessionId, Integer learnerId) throws ToolException;
    
    /**
     * Ask a tool for one particular ToolOutput, based on the given toolSessionId. If the tool doesn't
     * have any content matching the toolSessionId then should return an "empty" but valid set of data. e.g
     * an empty mark would be 0.
     * 
     * This functionality relies on a method added to the Tool Contract in LAMS 2.1.
     * 
     * @throws ToolException 
     */
    public ToolOutput getOutputFromTool(String conditionName, ToolSession toolSession, Integer learnerId) throws ToolException;
    
    /**
     * Ask a tool for a set of ToolOutputs, based on the given toolSessionId. 
     * 
     * If conditionName array is null, then return all the outputs for the tool, otherwise just restrict the 
     * outputs to the given list. If it is empty, then no outputs will be returned.
     * 
     * If the learnerId is null, then return the outputs based on all learners in that toolSession. If the 
     * output is nonsense for all learners, then return an "empty" but valid answer. For example, for a mark
     * you might return 0.
     * 
     * If there isn't any content matching the toolSessionId then should return an "empty" but valid set of data. e.g
     * an empty mark would be 0.
     * 
     * This functionality relies on a method added to the Tool Contract in LAMS 2.1.
     * 
     * @throws ToolException 
     */
    public SortedMap<String, ToolOutput> getOutputFromTool(List<String> names, Long toolSessionId, Integer learnerId) throws ToolException;
    
    /**
     * Ask a tool for a set of ToolOutputs, based on the given toolSessionId. 
     * 
     * If conditionName array is null, then return all the outputs for the tool, otherwise just restrict the 
     * outputs to the given list. If it is empty, then no outputs will be returned.
     * 
     * If the learnerId is null, then return the outputs based on all learners in that toolSession. If the 
     * output is nonsense for all learners, then return an "empty" but valid answer. For example, for a mark
     * you might return 0.
     * 
     * If there isn't any content matching the toolSessionId then should return an "empty" but valid set of data. e.g
     * an empty mark would be 0.
     * 
     * This functionality relies on a method added to the Tool Contract in LAMS 2.1.
     * 
     * @throws ToolException 
     */
    public SortedMap<String, ToolOutput> getOutputFromTool(List<String> names, ToolSession toolSession, Integer learnerId) throws ToolException;

    /**
     * Update the tool session data.
     * @param toolSession the new tool session object.
     */
    public void updateToolSession(ToolSession toolSession);

    /**
     * Return tool activity url for a learner. See also getToolPreviewURL, getToolLearnerProgressURL
     * @param lesson id  - needed for the SystemToolActivities
     * @param activity the requested activity - should be either a ToolActivity or a SystemToolActivity
     * @param learner the current learner.
     * @return the tool access url with tool session id or activity id
     */
    public String getToolLearnerURL(Long lessonID, Activity activity, User learner) throws LamsToolServiceException;

    /**
     * Return tool activity url for running a tool in preview mode. See also getToolLearnerURL, getToolLearnerProgressURL
     * @param lesson id  - needed for the SystemToolActivities
     * @param activity the requested activity - should be either a ToolActivity or a SystemToolActivity
     * @param learner the current learner.
     * @return the tool access url with tool session id or activity id
     */
    public String getToolLearnerPreviewURL(Long lessonID, Activity activity, User learner) throws LamsToolServiceException;

    /**
     * Return tool activity url for running a tool in preview mode. See also getToolLearnerURL, getToolPreviewURL
     * @param lesson id  - needed for the SystemToolActivities
     * @param activity the requested activity - should be either a ToolActivity or a SystemToolActivity
     * @param learner the current learner.
     * @return the tool access url with tool session id or activity id
     */
    public String getToolLearnerProgressURL(Long lessonID, Activity activity, User learner) throws LamsToolServiceException;

    /**
     * Return tool activity url for monitoring. 
     * @param lesson id  - needed for the SystemToolActivities
     * @param activity the requested activity - should be either a ToolActivity or a SystemToolActivity
     * @return the tool access url with tool session id or activity id
     */
    public String getToolMonitoringURL(Long lessonID, Activity activity) throws LamsToolServiceException;

    /**
     * Return the contribution url for monitoring.
     * @param lesson id  - needed for the SystemToolActivities
     * @param activity the requested activity - should be either a ToolActivity or a SystemToolActivity
     * @return the tool access url with tool session id or activity id
     */
    public String getToolContributionURL(Long lessonID, Activity activity) throws LamsToolServiceException;

    /**
     * Return the define later url for monitoring. 
     * @param activity the requested activity - must be a a ToolActivity. System Activities don't support define later.
     * @return the tool access url with tool content id
     */
    public String getToolDefineLaterURL(ToolActivity activity) throws LamsToolServiceException;

    /**
     * Return the moderate url for monitoring. 
     * @param activity the requested activity - must be a a ToolActivity. System Activities don't support moderation.
     * @return the tool access url with tool content id
     */
    public String getToolModerateURL(ToolActivity activity) throws LamsToolServiceException;

    /**
     * Get all the tool sessions for a lesson. The resulting list is not sorted.
     * @return list of ToolSession objects 
     */
    public List getToolSessionsByLesson(Lesson lesson);
    
    
    /**
     * Delete a tool session. Calls the tool to delete its session details and then
     * deletes the main tool session record. If the tool throws an exception, the main
     * tool session record is still deleted.
     */
    public void deleteToolSession(ToolSession toolSession);

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
