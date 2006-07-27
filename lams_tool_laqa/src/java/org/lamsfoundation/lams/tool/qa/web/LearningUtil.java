/***************************************************************************
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
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.tool.qa.web;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.qa.QaAppConstants;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.QaQueUsr;
import org.lamsfoundation.lams.tool.qa.QaSession;
import org.lamsfoundation.lams.tool.qa.QaUsrResp;
import org.lamsfoundation.lams.tool.qa.QaUtils;
import org.lamsfoundation.lams.tool.qa.service.IQaService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;

/**
 * 
 * Keeps all operations needed for Learning mode. 
 * @author Ozgur Demirtas
 *
 */
public class LearningUtil implements QaAppConstants{
	static Logger logger = Logger.getLogger(LearningUtil.class.getName());
	
	public static void saveFormRequestData(HttpServletRequest request, QaLearningForm qaLearningForm)
	{
	 	String toolSessionID=request.getParameter("toolSessionID");
	 	logger.debug("toolSessionID: " + toolSessionID);
	 	qaLearningForm.setToolSessionID(toolSessionID);

	 	logger.debug("done saving form request data.");
	}


    /**
     * createUsersAndResponses(Map mapAnswers, HttpServletRequest request)
     * create users of the responses
     * @param mapAnswers, request
     * return void
     *
     */
    protected void createUsersAndResponses(Map mapAnswers, HttpServletRequest request, IQaService qaService)
    {
        logger.debug("createUsers-retrieving qaService: " + qaService);
        
	    HttpSession ss = SessionManager.getSession();
	    /* get back login user DTO */
	    UserDTO toolUser = (UserDTO) ss.getAttribute(AttributeNames.USER);
    	logger.debug("retrieving toolUser: " + toolUser);
    	logger.debug("retrieving toolUser userId: " + toolUser.getUserID());
    	logger.debug("retrieving toolUser username: " + toolUser.getLogin());

    	String userName=toolUser.getLogin(); 
    	String fullName= toolUser.getFirstName() + " " + toolUser.getLastName();
    	logger.debug("retrieving toolUser fullname: " + fullName);
    	
    	Long userId=new Long(toolUser.getUserID().longValue());
    	
        /*
         * retrive contentId from the http session
         */
        logger.debug("createUsers-attempt retrieving toolContentId: " + request.getSession().getAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID));
        Long toolContentId=(Long)request.getSession().getAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID);
        /*
         * obtain QaContent to be used in creating QaQueUsr
         */  
        QaContent qaContent=qaService.retrieveQa(toolContentId.longValue());
        logger.debug("createUsers-retrieving qaContent: " + qaContent);

        /*
         * get QaSession to be used in creating QaQueUsr
         */
        Long toolSessionId=(Long)request.getSession().getAttribute(AttributeNames.PARAM_TOOL_SESSION_ID);
        logger.debug("createUsers-retrieving toolSessionId: " + toolSessionId);
        QaSession qaSession = qaService.retrieveQaSessionOrNullById(toolSessionId.longValue()); 
        logger.debug("createUsers-retrieving qaSession: " + qaSession);
        
        Iterator contentIterator=qaContent.getQaQueContents().iterator();
    	logger.debug("createUsers-attempt iteration questions");
    	
    	QaQueUsr qaQueUsr= new QaQueUsr(userId,
										userName,
										fullName,
										null, 
										qaSession, 
										new TreeSet());
						    	
    	logger.debug("createQaQueUsr - qaQueUsr: " + qaQueUsr);
    	
    	logger.debug("session uid: " + qaSession.getUid());
    	/*note that it is possible for a user to already exist from another tool session. In this case don't add any more user record*/
    	QaQueUsr qaQueUsrLocal=qaService.getQaUserBySession(userId, qaSession.getUid());
    	logger.debug("qaQueUsrLocal: " + qaQueUsrLocal);
    	
    	if ((qaQueUsr != null) && (qaQueUsrLocal == null)) 
        {
    	    qaQueUsr=createUser(request);
            logger.debug("created qaQueUsr: " + qaQueUsr);	
        }
    	else
    	{
    		logger.debug("assign user");
    		qaQueUsr=qaQueUsrLocal;
    	}
    	
    	logger.debug("qaQueUsr uid:" + qaQueUsr.getUid());
    	
    	
    	boolean isResponseFinalized=qaQueUsr.isResponseFinalized();
    	logger.debug("isResponseFinalized: " + isResponseFinalized);
    	
    	if (!isResponseFinalized)
    	{
    	  	logger.debug("isResponseFinalized is false, so creating the responses: ");
            while (contentIterator.hasNext())
        	{
        		QaQueContent qaQueContent=(QaQueContent)contentIterator.next();
        		if (qaQueContent != null)
        		{
        		    logger.debug("qaQueContent uid:" + qaQueContent.getUid());
        		    
            		String question=qaQueContent.getQuestion();
            		String displayOrder=new Long(qaQueContent.getDisplayOrder()).toString();
            		String answer=(String)mapAnswers.get(displayOrder);
            		
                    logger.debug("iterationg question-answers: displayOrder: " + displayOrder + 
             													 " question: " + question + " answer: " + answer);
            		
                    String timezoneId=(String)request.getSession().getAttribute(TIMEZONE_ID);
                    if (timezoneId == null) timezoneId="";
                    
                    List attempts=qaService.getAttemptsForUserAndQuestionContent(qaQueUsr.getUid(), qaQueContent.getUid());
                    logger.debug("attempts:" + attempts);
                    
                    if ((attempts != null) && (attempts.size() > 0))
                    {
                        logger.debug("this user already responsed to q/a in this session:");
                    }
                    else
                    {
                        logger.debug("creating response.");
                    	QaUsrResp qaUsrResp= new QaUsrResp(answer,false,
        						new Date(System.currentTimeMillis()),
        						timezoneId,
        						qaQueContent,
        						qaQueUsr,
        						true); 

        				logger.debug("iterationg qaUsrResp: " + qaUsrResp);
        				if (qaUsrResp != null)
        				{
        					qaService.createQaUsrResp(qaUsrResp);
        					logger.debug("created qaUsrResp in the db");	
        				}
                    }
        		}
            }

    	}
    	
		logger.debug("current user is: " + qaQueUsr);
		if (qaQueUsr != null)
		{
			qaQueUsr.setResponseFinalized(true);
			logger.debug("finalized user input");
			qaService.updateQaQueUsr(qaQueUsr);
		}
    }

    
    public static QaQueUsr createUser(HttpServletRequest request)
	{
        logger.debug("creating a new user in the tool db");
		IQaService qaService =QaUtils.getToolService(request);
	    Long queUsrId=QaUtils.getUserId();
		String username=QaUtils.getUserName();
		String fullname=QaUtils.getUserFullName();
		Long toolSessionId=(Long) request.getSession().getAttribute(TOOL_SESSION_ID);
		
		QaSession qaSession=qaService.retrieveQaSessionOrNullById(toolSessionId.longValue());
		logger.debug("qaSession: " + qaSession);
		
		QaQueUsr qaQueUsr= new QaQueUsr(queUsrId,
		        username,
		        fullname,
				null, 
				qaSession, 
				new TreeSet());

		qaService.createQaQueUsr(qaQueUsr);
		logger.debug("created qaQueUsr in the db: " + qaQueUsr);
		return qaQueUsr;
	}

    
    /**
	 * feedBackAnswersProgress(HttpServletRequest request, int currentQuestionIndex)
	 * give user feedback on the remaining questions
	 * @param qaLearningForm
	 * return void
	 */
    protected void feedBackAnswersProgress(HttpServletRequest request, int currentQuestionIndex)
    {
    	String totalQuestionCount=(String)request.getSession().getAttribute(TOTAL_QUESTION_COUNT);
    	logger.debug("totalQuestionCount: " + totalQuestionCount);
    	int remainingQuestionCount=new Long(totalQuestionCount).intValue() - currentQuestionIndex +1;
    	logger.debug("remainingQuestionCount: " + remainingQuestionCount);
    	String userFeedback="";
    	if (remainingQuestionCount != 0)
    		userFeedback= "Remaining question count: " + remainingQuestionCount;
    	else
    		userFeedback= "End of the questions.";
    			
    	request.getSession().setAttribute(USER_FEEDBACK, userFeedback);
    }
    
    public void setContentInUse(long toolContentId, IQaService qaService)
    {
    	QaContent qaContent=qaService.loadQa(toolContentId);
    	logger.debug("retrieve qaContent: " + qaContent);
    	
        qaContent.setContentLocked(true);
        logger.debug("content with id : " + toolContentId + "has been marked LOCKED");
        qaService.updateQa(qaContent);
        logger.debug("content with id : " + toolContentId + "has been marked LOCKED and updated in the db");
    }
}
