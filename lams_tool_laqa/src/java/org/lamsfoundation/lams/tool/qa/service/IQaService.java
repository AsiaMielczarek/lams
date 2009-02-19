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
package org.lamsfoundation.lams.tool.qa.service;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.lamsfoundation.lams.contentrepository.ITicket;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.tool.IToolVO;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.qa.QaApplicationException;
import org.lamsfoundation.lams.tool.qa.QaCondition;
import org.lamsfoundation.lams.tool.qa.QaConfigItem;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.QaQueUsr;
import org.lamsfoundation.lams.tool.qa.QaSession;
import org.lamsfoundation.lams.tool.qa.QaUploadedFile;
import org.lamsfoundation.lams.tool.qa.QaUsrResp;
import org.lamsfoundation.lams.tool.qa.QaWizardCategory;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.audit.IAuditService;

/**
 * This interface define the contract that all Q/A service provider must follow.
 * 
 * @author Ozgur Demirtas
 */
public interface IQaService {
    /**
     * Return the qa object according to the requested content id.
     * 
     * @param toolContentId
     *                the tool content id
     * @return the qa object
     */

    public QaContent retrieveQa(long toolContentId) throws QaApplicationException;

    /**
     * Return the qa object according to the requested content id.
     * 
     * @param toolContentId
     *                the tool content id
     * @return the qa object or null
     */

    public QaContent loadQa(long toolContentId) throws QaApplicationException;

    public void saveOrUpdateQa(QaContent qa) throws QaApplicationException;

    public QaContent getQaContentByUID(Long uid) throws QaApplicationException;

    public int getTotalNumberOfUsers(QaContent qa) throws QaApplicationException;

    public int countSessionComplete(QaContent qa) throws QaApplicationException;

    public int countSessionComplete() throws QaApplicationException;

    public void updateQaQueUsr(QaQueUsr qaQueUsr) throws QaApplicationException;

    /**
     * Return the question content object according to the requested question content id.
     * 
     * @param qaQueContentId
     *                qa question content id
     * @return the qa question object
     */
    public QaQueContent retrieveQaQue(long qaQueContentId) throws QaApplicationException;

    public QaQueUsr loadQaQueUsr(Long userId) throws QaApplicationException;

    public List getAttemptsForUserAndQuestionContent(final Long queUsrId, final Long qaQueContentId)
	    throws QaApplicationException;

    public void createQaQue(QaQueContent qaQueContent) throws QaApplicationException;

    public void removeQaQueContent(QaQueContent qaQueContent) throws QaApplicationException;

    public void createQaUsrResp(QaUsrResp qaUsrResp) throws QaApplicationException;

    public void updateUserResponse(QaUsrResp resp) throws QaApplicationException;

    public QaUsrResp retrieveQaUsrResp(long responseId) throws QaApplicationException;

    public void updateQaUsrResp(QaUsrResp qaUsrResp) throws QaApplicationException;

    public QaQueContent getQuestionContentByQuestionText(final String question, Long contentUid)
	    throws QaApplicationException;

    public QaQueContent getQuestionContentByDisplayOrder(Long displayOrder, Long contentUid)
	    throws QaApplicationException;

    public List getAllQuestionEntriesSorted(final long qaContentId) throws QaApplicationException;

    public void saveOrUpdateQaQueContent(QaQueContent qaQueContent) throws QaApplicationException;

    /**
     * Return the qa session object according to the requested session id.
     * 
     * @param qaSessionId
     *                qa session id
     * @return the qa session object
     */
    public QaSession retrieveQaSession(long qaSessionId) throws QaApplicationException;

    public QaSession retrieveQaSessionOrNullById(long qaSessionId) throws QaApplicationException;

    public void createQaSession(QaSession qaSession) throws QaApplicationException;

    public List getSessionNamesFromContent(QaContent qaContent) throws QaApplicationException;

    public String getSessionNameById(long qaSessionId) throws QaApplicationException;

    public void removeToolSession(Long toolSessionId) throws DataMissingException, ToolException;

    public List getSessionsFromContent(QaContent qaContent) throws QaApplicationException;

    public void createQaQueUsr(QaQueUsr qaQueUsr) throws QaApplicationException;

    public void updateQaSession(QaSession qaSession) throws QaApplicationException;

    /**
     * Return the qa que user object according to the requested usr id.
     * 
     * @param qaQaUsrId
     *                qa usr id
     * @return the qa que usr object
     */
    public QaQueUsr retrieveQaQueUsr(long qaQaUsrId) throws QaApplicationException;

    public QaQueUsr getQaQueUsrById(long qaQueUsrId) throws QaApplicationException;

    public void updateQa(QaContent qa) throws QaApplicationException;

    public void createQa(QaContent qa) throws QaApplicationException;

    public QaUsrResp getAttemptByUID(Long uid) throws QaApplicationException;

    public void removeAttemptsForUserAndQuestionContent(final Long queUsrId, final Long qaQueContentId);

    public void hideResponse(QaUsrResp qaUsrResp) throws QaApplicationException;

    public void showResponse(QaUsrResp qaUsrResp) throws QaApplicationException;

    public void deleteQa(QaContent qa) throws QaApplicationException;

    public void deleteQaSession(QaSession QaSession) throws QaApplicationException;

    public QaContent retrieveQaBySession(long qaSessionId) throws QaApplicationException;

    public void deleteUsrRespByQueId(Long qaQueId) throws QaApplicationException;

    public void deleteQaById(Long qaId) throws QaApplicationException;

    public QaQueUsr getQaUserBySession(final Long queUsrId, final Long qaSessionId) throws QaApplicationException;

    public void deleteQaQueUsr(QaQueUsr qaQueUsr) throws QaApplicationException;

    public void removeUserResponse(QaUsrResp resp) throws QaApplicationException;

    public List getAllQuestionEntries(final Long uid) throws QaApplicationException;

    public User getCurrentUserData(String username) throws QaApplicationException;

    public List getUserBySessionOnly(final QaSession qaSession) throws QaApplicationException;

    /**
     * copyToolContent(Long fromContentId, Long toContentId) return void
     * 
     * @param fromContentId
     * @param toContentId
     */
    public void copyToolContent(Long fromContentId, Long toContentId) throws ToolException;

    public void setAsDefineLater(Long toolContentId, boolean value) throws DataMissingException, ToolException;

    public void setAsRunOffline(Long toolContentId, boolean value) throws DataMissingException, ToolException;

    public void setAsForceComplete(Long userId) throws QaApplicationException;

    public void setAsForceCompleteSession(Long toolSessionId) throws QaApplicationException;

    public boolean studentActivityOccurred(QaContent qa) throws QaApplicationException;

    public boolean studentActivityOccurredGlobal(QaContent qaContent) throws QaApplicationException;

    /**
     * removeToolContent(Long toolContentId) return void
     * 
     * @param toolContentId
     */
    public void removeToolContent(Long toolContentId) throws QaApplicationException;

    /**
     * createToolSession(Long toolSessionId,String toolSessionName, Long toolContentId)
     * 
     * It is also defined here since in development we want to be able call it directly from the web-layer instead of it
     * being called by the container.
     * 
     * @param toolSessionId
     * @param toolContentId
     */
    public void createToolSession(Long toolSessionId, String toolSessionName, Long toolContentId) throws ToolException;

    /**
     * leaveToolSession(Long toolSessionId, Long learnerId)
     * 
     * It is also defined here since in development we want to be able call it directly from our web-layer instead of it
     * being called by the container.
     * 
     * @param toolSessionId
     * @param toolContentId
     */
    public String leaveToolSession(Long toolSessionId, Long learnerId) throws DataMissingException, ToolException;

    public IToolVO getToolBySignature(String toolSignature) throws QaApplicationException;

    public long getToolDefaultContentIdBySignature(String toolSignature) throws QaApplicationException;

    public int countSessionUser(QaSession qaSession) throws QaApplicationException;

    public List getToolSessionsForContent(QaContent qa) throws QaApplicationException;

    public QaQueContent getToolDefaultQuestionContent(long contentId) throws QaApplicationException;

    public void configureContentRepository() throws QaApplicationException;

    public ITicket getRepositoryLoginTicket() throws QaApplicationException;

    public void deleteFromRepository(Long uuid, Long versionID) throws QaApplicationException;

    public InputStream downloadFile(Long uuid, Long versionID) throws QaApplicationException;

    public void persistFile(String uuid, boolean isOnlineFile, String fileName, QaContent qaContent)
	    throws QaApplicationException;

    public void persistFile(QaContent content, QaUploadedFile file) throws QaApplicationException;

    public void removeFile(Long submissionId) throws QaApplicationException;

    public List retrieveQaUploadedFiles(QaContent qa) throws QaApplicationException;

    public List retrieveQaQueContentsByToolContentId(long toolContentId);

    public Long createNotebookEntry(Long id, Integer idType, String signature, Integer userID, String entry);

    public NotebookEntry getEntry(Long id, Integer idType, String signature, Integer userID);

    /**
     * Get the LAMS audit service. Needed as the web layer controls the staff updating of an answer, so the log entry
     * must be made by the web layer.
     */
    public IAuditService getAuditService();

    public void updateEntry(NotebookEntry notebookEntry);

    public QaContent getQaContentBySessionId(Long sessionId);

    /**
     * Creates an unique name for a QaCondition. It consists of the tool output definition name and a unique positive
     * integer number.
     * 
     * @param existingConditions
     *                existing conditions; required to check if a condition with the same name does not exist.
     * @return unique QaCondition name
     */
    public String createConditionName(Collection<QaCondition> existingConditions);

    public void deleteCondition(QaCondition condition);

    public QaCondition createDefaultComplexCondition(QaContent qaContent);

    /**
     * Gets the qa config item with the given key
     * 
     * @param configKey
     * @return
     */
    public QaConfigItem getConfigItem(String configKey);

    /**
     * Saves or updates a qa config item
     * 
     * @param configItem
     */
    public void saveOrUpdateConfigItem(QaConfigItem configItem);

    /**
     * Gets the set of wizard categories from the database
     * 
     * @return
     */
    public SortedSet<QaWizardCategory> getWizardCategories();

    /**
     * Saves the entire set of QaWizardCategories (including the child cognitive skills and questions)
     * 
     * @param categories
     */
    public void saveOrUpdateQaWizardCategories(SortedSet<QaWizardCategory> categories);

    /**
     * Deletes a wizard category from the db
     * 
     * @param uid
     */
    public void deleteWizardCategoryByUID(Long uid);

    /**
     * Deletes a wizard cognitive skill from the db
     * 
     * @param uid
     */
    public void deleteWizardSkillByUID(Long uid);

    /**
     * Deletes a wizard question from the db
     * 
     * @param uid
     */
    public void deleteWizardQuestionByUID(Long uid);

    /**
     * Deletes all categories, sub skills and sub questions
     */
    public void deleteAllWizardCategories();

    public void removeQuestionsFromCache(QaContent qaContent);
}
