/***************************************************************************
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
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.tool.mc.dao;

import java.util.List;

import org.lamsfoundation.lams.tool.mc.pojos.McUsrAttempt;


/**
 * 
 * @author Ozgur Demirtas
 * * <p>Interface for the McUsrAttempt DAO, defines methods needed to access/modify user attempt data</p>
 *
 */
public interface IMcUsrAttemptDAO
{
    
    /**
	 *  * <p>Return the persistent instance of a McUsrAttempt  
	 * with the given identifier <code>uid</code>, returns null if not found. </p>
	 * 
	 * @param uid
	 * @return McQueContent
	 */
	public McUsrAttempt getMcUserAttemptByUID(Long uid);

	/**
	 *  * <p>saves McUsrAttempt  
	 * with the given identifier <code>mcUsrAttempt</code> </p>
	 * 
	 * @param uid
	 * @return 
	 */
	public void saveMcUsrAttempt(McUsrAttempt mcUsrAttempt);
    
	/**
	 *  * <p>updates McUsrAttempt  
	 * with the given identifier <code>mcUsrAttempt</code> </p>
	 * 
	 * @param mcUsrAttempt
	 * @return 
	 */	
	public void updateMcUsrAttempt(McUsrAttempt mcUsrAttempt);
	
	/**
	 *  * <p>removes McUsrAttempt  
	 * with the given identifier <code>uid</code> </p>
	 * 
	 * @param uid
	 * @return 
	 */	
	public void removeMcUsrAttemptByUID(Long uid);

	/**
	 *  * <p>removes McUsrAttempt  
	 * with the given identifier <code>mcUsrAttempt</code> </p>
	 * 
	 * @param mcUsrAttempt
	 * @return 
	 */
	public void removeMcUsrAttempt(McUsrAttempt mcUsrAttempt);
	
	/**
	 *  * <p>returns the highest mark of a learner  
	 * with the given identifier <code>queUsrId</code> </p>
	 * 
	 * @param queUsrId
	 * @return 
	 */
	public List getHighestMark(Long queUsrId);

	/**
	 *  * <p>returns the highest mark of a learner  
	 * with the given identifier <code>queUsrId</code> </p>
	 * 
	 * @param queUsrId
	 * @return 
	 */	
	public List getHighestAttemptOrder(Long queUsrId);
	
	/**
	 *  * <p>returns a list of attempts  
	 * with the given identifiers <code>queUsrId</code> and <code>mcQueContentId</code> </p>
	 * 
	 * @param queUsrId
	 * @param mcQueContentId
	 * @return 
	 */
	public List getAttemptForQueContent(final Long queUsrId, final Long mcQueContentId);
	
	/**
	 *  * <p>returns a list of attempts  
	 * with the given identifiers <code>queUsrId</code> and <code>mcQueContentId</code> and <code>attemptOrder</code> </p>
	 * 
	 * @param queUsrId
	 * @param mcQueContentId
	 * @param attemptOrder
	 * @return 
	 */
	public List getAttemptByAttemptOrder(final Long queUsrId, final Long mcQueContentId, final Integer attemptOrder);
	
	/**
	 *  * <p>returns a list of marks
	 * 
	 * @return 
	 */
	
	public List getAttemptsForUser(final Long queUsrId);
	
	public List getAttemptsForUserAndQuestionContent(final Long queUsrId, final Long mcQueContentId);
	
	public List getUserAttemptsForQuestionContentAndSessionUid(final Long queUsrUid,  final Long mcQueContentId, final Long mcSessionUid);
	
	public McUsrAttempt getAttemptWithLastAttemptOrderForUserInSession(Long queUsrUid, final Long mcSessionUid);
	
	public List getAttemptsForUserInSession(final Long queUsrUid, final Long mcSessionUid);
	
	public List getAttemptsForUserOnHighestAttemptOrderInSession(final Long queUsrUid, final Long mcSessionUid, final Integer attemptOrder);
	
	public List getAttemptsOnHighestAttemptOrder(final Long queUsrUid,  final Long mcQueContentId, final Long mcSessionUid, final Integer attemptOrder);
	
	public boolean getUserAttemptCorrectForQuestionContentAndSessionUid(final Long queUsrUid,  final Long mcQueContentId, final Long mcSessionUid, final Integer attemptOrder);
	
	public List getMarks();
}


