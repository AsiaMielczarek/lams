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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.tool.survey.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.tool.survey.dto.ReflectDTO;
import org.lamsfoundation.lams.tool.survey.model.Survey;
import org.lamsfoundation.lams.tool.survey.model.SurveyAttachment;
import org.lamsfoundation.lams.tool.survey.model.SurveySession;
import org.lamsfoundation.lams.tool.survey.model.SurveyUser;

/**
 * @author Dapeng.Ni
 * 
 * Interface that defines the contract that all Survey service provider must follow.
 */
public interface ISurveyService 
{
	
	/**
	 * Get <code>Survey</code> by toolContentID.
	 * @param contentId
	 * @return
	 */
	Survey getSurveyByContentId(Long contentId);
	/**
	 * Get a cloned copy of  tool default tool content (Survey) and assign the toolContentId of that copy as the 
	 * given <code>contentId</code> 
	 * @param contentId
	 * @return
	 * @throws SurveyApplicationException
	 */
	Survey getDefaultContent(Long contentId) throws SurveyApplicationException;
	

	/**
	 * Upload instruciton file into repository.
	 * @param file
	 * @param type
	 * @return
	 * @throws UploadSurveyFileException
	 */
	SurveyAttachment uploadInstructionFile(FormFile file, String type) throws UploadSurveyFileException;
	
	
	//********** for user methods *************
	/**
	 * Create a new user in database.
	 */
	void createUser(SurveyUser surveyUser);
	/**
	 * Get user by given userID and toolContentID.
	 * @param long1
	 * @return
	 */
	SurveyUser getUserByIDAndContent(Long userID, Long contentId); 

	/**
	 * Get user by sessionID and UserID
	 * @param long1
	 * @param sessionId
	 * @return
	 */
	SurveyUser getUserByIDAndSession(Long long1, Long sessionId); 


	//********** Repository methods ***********************
	/**
	 * Delete file from repository.
	 */
	void deleteFromRepository(Long fileUuid, Long fileVersionId) throws SurveyApplicationException ;

	/**
	 * Save or update survey into database.
	 * @param Survey
	 */
	void saveOrUpdateSurvey(Survey Survey);
	/**
	 * Delete reource attachment(i.e., offline/online instruction file) from database. This method does not
	 * delete the file from repository.
	 * 
	 * @param attachmentUid
	 */
	void deleteSurveyAttachment(Long attachmentUid);
	/**
	 * Delete resoruce item from database.
	 * @param uid
	 */
	void deleteSurveyItem(Long uid);

	/**
	 * Get survey which is relative with the special toolSession.
	 * @param sessionId
	 * @return
	 */
	Survey getSurveyBySessionId(Long sessionId);
	/**
	 * Get survey toolSession by toolSessionId
	 * @param sessionId
	 * @return
	 */
	SurveySession getSurveySessionBySessionId(Long sessionId);

	/**
	 * Save or update survey session.
	 * @param resSession
	 */
	void saveOrUpdateSurveySession(SurveySession resSession);

	/**
	 * If success return next activity's url, otherwise return null.
	 * @param toolSessionId
	 * @param userId
	 * @return
	 */
	String finishToolSession(Long toolSessionId, Long userId)  throws SurveyApplicationException;

//
//	/**
//	 * Return monitoring summary list. The return value is list of survey summaries for each groups.
//	 * @param contentId
//	 * @return
//	 */
//	List<List<Summary>> getSummary(Long contentId);
//	/**
//	 * Get survey item <code>Summary</code> list according to sessionId and skipHide flag.
//	 *  
//	 * @param sessionId
//	 * @param skipHide true, don't get survey item if its <code>isHide</code> flag is true.
//	 * 				  Otherwise, get all survey item  
//	 * @return
//	 */
//	public List<Summary> exportBySessionId(Long sessionId, boolean skipHide);
//	public List<List<Summary>> exportByContentId(Long contentId);
//
//	List<SurveyUser> getUserListBySessionItem(Long sessionId, Long itemUid);

	//******************************************************************************************
	//  	NOTEBOOK Functions
	//******************************************************************************************
	
	/**
	 * Create refection entry into notebook tool.
	 * @param sessionId
	 * @param notebook_tool
	 * @param tool_signature
	 * @param userId
	 * @param entryText
	 */
	public Long createNotebookEntry(Long sessionId, Integer notebookToolType, String toolSignature, Integer userId, String entryText);
	/**
	 * Get reflection entry from notebook tool.
	 * @param sessionId
	 * @param idType
	 * @param signature
	 * @param userID
	 * @return
	 */
	public NotebookEntry getEntry(Long sessionId, Integer idType, String signature, Integer userID);

	/**
	 * Get Reflect DTO list grouped by sessionID.
	 * @param contentId
	 * @return
	 */
	Map<Long, Set<ReflectDTO>> getReflectList(Long contentId);

	/**
	 * Get user by UID
	 * @param uid
	 * @return
	 */
	SurveyUser getUser(Long uid);
}

