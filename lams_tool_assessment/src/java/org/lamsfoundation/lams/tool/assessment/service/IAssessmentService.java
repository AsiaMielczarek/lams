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
/* $$Id$$ */
package org.lamsfoundation.lams.tool.assessment.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.events.IEventNotificationService;
import org.lamsfoundation.lams.tool.assessment.dto.Summary;
import org.lamsfoundation.lams.tool.assessment.dto.UserSummary;
import org.lamsfoundation.lams.tool.assessment.model.Assessment;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentAttachment;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestion;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestionResult;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentResult;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentSession;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentUser;
import org.lamsfoundation.lams.usermanagement.User;

/**
 * @author Andrey Balan
 * 
 *         Interface that defines the contract that all ShareAssessment service provider must follow.
 */
public interface IAssessmentService {

    /**
     * Get <code>Assessment</code> by toolContentID.
     * 
     * @param contentId
     * @return
     */
    Assessment getAssessmentByContentId(Long contentId);

    /**
     * Get a cloned copy of tool default tool content (Assessment) and assign the toolContentId of that copy as the
     * given <code>contentId</code>
     * 
     * @param contentId
     * @return
     * @throws AssessmentApplicationException
     */
    Assessment getDefaultContent(Long contentId) throws AssessmentApplicationException;

    /**
     * Get list of assessment questions by given assessmentUid. These assessment questions must be created by author.
     * 
     * @param assessmentUid
     * @return
     */
    List getAuthoredQuestions(Long assessmentUid);

    /**
     * Upload instruciton file into repository.
     * 
     * @param file
     * @param type
     * @return
     * @throws UploadAssessmentFileException
     */
    AssessmentAttachment uploadInstructionFile(FormFile file, String type) throws UploadAssessmentFileException;

    // ********** for user methods *************
    /**
     * Create a new user in database.
     */
    void createUser(AssessmentUser assessmentUser);

    /**
     * Get user by given userID and toolContentID.
     * 
     * @param long1
     * @return
     */
    AssessmentUser getUserByIDAndContent(Long userID, Long contentId);

    /**
     * Get user by sessionID and UserID
     * 
     * @param long1
     * @param sessionId
     * @return
     */
    AssessmentUser getUserByIDAndSession(Long long1, Long sessionId);

    // ********** Repository methods ***********************
    /**
     * Delete file from repository.
     */
    void deleteFromRepository(Long fileUuid, Long fileVersionId) throws AssessmentApplicationException;

    /**
     * Save or update assessment into database.
     * 
     * @param Assessment
     */
    void saveOrUpdateAssessment(Assessment Assessment);

    /**
     * Delete reource attachment(i.e., offline/online instruction file) from database. This method does not delete the
     * file from repository.
     * 
     * @param attachmentUid
     */
    void deleteAssessmentAttachment(Long attachmentUid);

    /**
     * Delete resoruce question from database.
     * 
     * @param uid
     */
    void deleteAssessmentQuestion(Long uid);

    /**
     * Return all reource questions within the given toolSessionID.
     * 
     * @param sessionId
     * @return
     */
    List<AssessmentQuestion> getAssessmentQuestionsBySessionId(Long sessionId);

    /**
     * Get assessment which is relative with the special toolSession.
     * 
     * @param sessionId
     * @return
     */
    Assessment getAssessmentBySessionId(Long sessionId);

    /**
     * Get assessment toolSession by toolSessionId
     * 
     * @param sessionId
     * @return
     */
    AssessmentSession getAssessmentSessionBySessionId(Long sessionId);

    /**
     * Save or update assessment session.
     * 
     * @param resSession
     */
    void saveOrUpdateAssessmentSession(AssessmentSession resSession);
    
    /**
     * Save or update assessment result.
     * 
     * @param assessmentResult
     */
    void setAttemptStarted(Assessment assessment, AssessmentUser assessmentUser, Long toolSessionId);    

    void processUserAnswers(Long assessmentUid, Long userId, ArrayList<LinkedHashSet<AssessmentQuestion>> pagedQuestions);
    
    AssessmentResult getLastAssessmentResult(Long assessmentUid, Long userId);
    
    int getAssessmentResultCount(Long assessmentUid, Long userId);
    
    List<AssessmentQuestionResult> getAssessmentQuestionResultList(Long assessmentUid, Long userId, Long questionUid);

    /**
     * If success return next activity's url, otherwise return null.
     * 
     * @param toolSessionId
     * @param userId
     * @return
     */
    String finishToolSession(Long toolSessionId, Long userId) throws AssessmentApplicationException;

    AssessmentQuestion getAssessmentQuestionByUid(Long questionUid);

    /**
     * Return monitoring summary list. The return value is list of assessment summaries for each groups.
     * 
     * @param contentId
     * @return
     */
    List<Summary> getSummaryList(Long contentId);
    
    /**
     * Return user summary. This summary contains list of all attempts made by user.
     * 
     * @param contentId
     * @param userId
     * @return
     */
    UserSummary getUserSummary(Long contentId, Long userId);

    /**
     * Set a assessment question visible or not.
     * 
     * @param questionUid
     * @param visible
     *            true, question is visible. False, question is invisible.
     */
    void setQuestionVisible(Long questionUid, boolean visible);

    /**
     * Get assessment question <code>Summary</code> list according to sessionId and skipHide flag.
     * 
     * @param sessionId
     * @param skipHide
     *            true, don't get assessment question if its <code>isHide</code> flag is true. Otherwise, get all assessment
     *            question
     * @return
     */
    public List<Summary> exportBySessionId(Long sessionId, boolean skipHide);

    public List<List<Summary>> exportByContentId(Long contentId);

    /**
     * Get user by UID
     * 
     * @param uid
     * @return
     */
    AssessmentUser getUser(Long uid);

    public IEventNotificationService getEventNotificationService();

    /**
     * Gets a message from assessment bundle. Same as <code><fmt:message></code> in JSP pages.
     * 
     * @param key
     *            key of the message
     * @param args
     *            arguments for the message
     * @return message content
     */
    String getLocalisedMessage(String key, Object[] args);

    /**
     * Finds out which lesson the given tool content belongs to and returns its monitoring users.
     * 
     * @param sessionId
     *            tool session ID
     * @return list of teachers that monitor the lesson which contains the tool with given session ID
     */
    public List<User> getMonitorsByToolSessionId(Long sessionId);
}
