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

package org.lamsfoundation.lams.tool.vote.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;
import org.lamsfoundation.lams.tool.vote.VoteApplicationException;
import org.lamsfoundation.lams.tool.vote.VoteComparator;
import org.lamsfoundation.lams.tool.vote.VoteUtils;
import org.lamsfoundation.lams.tool.vote.pojos.VoteContent;
import org.lamsfoundation.lams.tool.vote.pojos.VoteQueContent;
import org.lamsfoundation.lams.tool.vote.pojos.VoteQueUsr;
import org.lamsfoundation.lams.tool.vote.pojos.VoteSession;
import org.lamsfoundation.lams.tool.vote.pojos.VoteUsrAttempt;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;

/**
 * <p>Action class that controls the logic of tool behavior. </p>
 * 
 * <p>Note that Struts action class only has the responsibility to navigate 
 * page flow. All database operation should go to service layer and data 
 * transformation from domain model to struts form bean should go to form 
 * bean class. This ensure clean and maintainable code.
 * </p>
 * 
 * <code>SystemException</code> is thrown whenever an known error condition is
 * identified. No system exception error handling code should appear in the 
 * Struts action class as all of them are handled in 
 * <code>CustomStrutsExceptionHandler<code>.
 * 
 * @author Ozgur Demirtas
 * 
*/
public class VoteLearningAction extends LamsDispatchAction implements VoteAppConstants
{
	static Logger logger = Logger.getLogger(VoteLearningAction.class.getName());
	
	 /** 
     * <p>Default struts dispatch method.</p> 
     * 
     * <p>It is assuming that progress engine should pass in the tool access
     * mode and the tool session id as http parameters.</p>
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
     * @throws VoteApplicationException the known runtime exception 
     * 
	 * unspecified(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException,
                                                            ServletException
                                                            
	 * main content/question content management and workflow logic
	 * 
	 * if the passed toolContentId exists in the db, we need to get the relevant data into the Map 
	 * if not, create the default Map 
	*/
    public ActionForward unspecified(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException,
                                                            ServletException
    {
        VoteLearningForm voteLearningForm = (VoteLearningForm) form;
        voteLearningForm.setNominationsSubmited(new Boolean(false).toString());
    	
        VoteUtils.cleanUpUserExceptions(request);
    	voteLearningForm.setMaxNominationCountReached(new Boolean(false).toString());
    	VoteUtils.persistInSessionRichText(request);	 	
	 	return null;
    }

    
    public ActionForward viewAllResults(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException
	{   
        VoteUtils.cleanUpUserExceptions(request);
		logger.debug("dispatching viewAllResults...");
		VoteLearningForm voteLearningForm = (VoteLearningForm) form;
		voteLearningForm.setNominationsSubmited(new Boolean(false).toString());
		voteLearningForm.setMaxNominationCountReached(new Boolean(false).toString());
		
		setContentInUse(request);
		IVoteService voteService =VoteUtils.getToolService(request);
	 	
		Collection<String> voteDisplayOrderIds = voteLearningForm.votesAsCollection();
		logger.debug("Checkbox votes "+voteDisplayOrderIds);
		
    	Long toolContentId=(Long)request.getSession().getAttribute(TOOL_CONTENT_ID);
    	logger.debug("toolContentId:" + toolContentId);
    	
    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
    	logger.debug("voteContent:" + voteContent);

		Map mapGeneralCheckedOptionsContent = LearningUtil.buildQuestionContentMap(request, voteContent, voteDisplayOrderIds);
		logger.debug("mapGeneralCheckedOptionsContent: "+ mapGeneralCheckedOptionsContent);
		request.setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapGeneralCheckedOptionsContent);

    	voteLearningForm.resetCommands();
	    return (mapping.findForward(ALL_NOMINATIONS));
    }
    
    
    public ActionForward viewAnswers(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException
	{   
        VoteUtils.cleanUpUserExceptions(request);
		logger.debug("dispatching viewAnswers...");
		
		VoteLearningForm voteLearningForm = (VoteLearningForm) form;
		voteLearningForm.setNominationsSubmited(new Boolean(false).toString());
		voteLearningForm.setMaxNominationCountReached(new Boolean(false).toString());
		
	 	IVoteService voteService =VoteUtils.getToolService(request);
	 	setContentInUse(request);
	 	
	 	String isRevisitingUser=voteLearningForm.getRevisitingUser();
	 	logger.debug("isRevisitingUser: " + isRevisitingUser);
	 	
	 	if (isRevisitingUser.equals("true"))
	 	{
	 	    logger.debug("this is a revisiting user, get the nominations from the db: " + isRevisitingUser);
		 	Long toolContentId=(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
	    	logger.debug("toolContentId: " + toolContentId);

	    	VoteQueUsr voteQueUsr=LearningUtil.getUser(request);
	    	logger.debug("voteQueUsr: " + voteQueUsr);

	    	List attempts=voteService.getAttemptsForUser(voteQueUsr.getUid());
	    	logger.debug("attempts: " + attempts);
	    	
	    	Map mapQuestionsContent= new TreeMap(new VoteComparator());
			Iterator listIterator=attempts.iterator();
			int order=0;
	    	while (listIterator.hasNext())
	    	{
	    	    VoteUsrAttempt attempt=(VoteUsrAttempt)listIterator.next();
	        	logger.debug("attempt: " + attempt);
	        	VoteQueContent voteQueContent=attempt.getVoteQueContent();
	        	logger.debug("voteQueContent: " + voteQueContent);        	
	        	order++;
	    		if (voteQueContent != null)
	    		{
	            	mapQuestionsContent.put(new Integer(order).toString(),voteQueContent.getQuestion());
	    		}
	    	}
	    	request.setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapQuestionsContent);
	 	}
	 	else
	 	{
	 	   logger.debug("this is not a revisiting user: " + isRevisitingUser);
	 	}
	 	
	 	voteLearningForm.resetCommands();
    	logger.debug("fwd'ing to : " + VIEW_ANSWERS);
		return (mapping.findForward(VIEW_ANSWERS));
    }
    

    public ActionForward redoQuestionsOk(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException
	{   
        VoteUtils.cleanUpUserExceptions(request);
		logger.debug("dispatching redoQuestionsOk...");
		
		VoteLearningForm voteLearningForm = (VoteLearningForm) form;
		voteLearningForm.setNominationsSubmited(new Boolean(false).toString());
		voteLearningForm.setMaxNominationCountReached(new Boolean(false).toString());

	 	setContentInUse(request);
		logger.debug("requested redoQuestionsOk, user is sure to redo the questions.");
		voteLearningForm.resetCommands();
		return redoQuestions(mapping, form, request, response);
	}

    
    public ActionForward learnerFinished(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException
	{   
        logger.debug("dispatching learnerFinished");
		logger.debug("requested learner finished, the learner should be directed to next activity.");

		VoteLearningForm voteLearningForm = (VoteLearningForm) form;
		voteLearningForm.setNominationsSubmited(new Boolean(false).toString());
		voteLearningForm.setMaxNominationCountReached(new Boolean(false).toString());
	 	
		IVoteService voteService =VoteUtils.getToolService(request);
	 	
		Long toolSessionId = (Long) request.getSession().getAttribute(TOOL_SESSION_ID);
		String userID=(String) request.getSession().getAttribute(USER_ID);
		logger.debug("attempting to leave/complete session with toolSessionId:" + toolSessionId + " and userID:"+userID);
		
		VoteUtils.cleanUpSessionAbsolute(request);
		
		String nextUrl=null;
		try
		{
			nextUrl=voteService.leaveToolSession(toolSessionId, new Long(userID));
			logger.debug("nextUrl: "+ nextUrl);
		}
		catch (DataMissingException e)
		{
			logger.debug("failure getting nextUrl: "+ e);
    		voteLearningForm.resetCommands();
			//throw new ServletException(e);
    		return (mapping.findForward(LEARNING_STARTER));
		}
		catch (ToolException e)
		{
			logger.debug("failure getting nextUrl: "+ e);
    		voteLearningForm.resetCommands();
			//throw new ServletException(e);
    		return (mapping.findForward(LEARNING_STARTER));        		
		}
		catch (Exception e)
		{
			logger.debug("unknown exception getting nextUrl: "+ e);
    		voteLearningForm.resetCommands();
			//throw new ServletException(e);
    		return (mapping.findForward(LEARNING_STARTER));        		
		}

		logger.debug("success getting nextUrl: "+ nextUrl);
		voteLearningForm.resetCommands();
		
		/* pay attention here*/
		logger.debug("redirecting to the nextUrl: "+ nextUrl);
		response.sendRedirect(nextUrl);
		
		return null;

    }

    public ActionForward continueOptionsCombined(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException
	{   
        VoteUtils.cleanUpUserExceptions(request);
		logger.debug("dispatching continueOptionsCombined...");
		
		VoteLearningForm voteLearningForm = (VoteLearningForm) form;
		voteLearningForm.setNominationsSubmited(new Boolean(false).toString());
		voteLearningForm.setMaxNominationCountReached(new Boolean(false).toString());

		Collection<String> voteDisplayOrderIds = voteLearningForm.votesAsCollection();
		logger.debug("Checkbox votes "+voteDisplayOrderIds);
		
		// check number of votes
		int castVoteCount= voteDisplayOrderIds!=null ? voteDisplayOrderIds.size() : 0;
    	String userEntry=voteLearningForm.getUserEntry();
    	logger.debug("userEntry: " + userEntry);
    	
    	if ((userEntry != null) && (userEntry.length() > 0))
    	{
    	    logger.debug("userEntry available: " + userEntry);
    	    ++castVoteCount;
    	}
    	logger.debug("castVoteCount post user entry count: " + castVoteCount);
    	

		String maxNominationCount=voteLearningForm.getMaxNominationCount();
    	logger.debug("maxNominationCount: " + maxNominationCount);
    	
    	int intMaxNominationCount=0;
    	if (maxNominationCount != null)
    	    intMaxNominationCount=new Integer(maxNominationCount).intValue();
    	logger.debug("intMaxNominationCount: " + intMaxNominationCount);
    	logger.debug("intMaxNominationCount versus current voting count: " + intMaxNominationCount + " versus " + castVoteCount );

    	if (castVoteCount > intMaxNominationCount )
    	{
    	    voteLearningForm.setMaxNominationCountReached(new Boolean(true).toString());
    	    persistInRequestError(request, "error.maxNominationCount.reached");
	        logger.debug("give warning,  max nom count reached...");
    	    logger.debug("fwd'ing to: " + LOAD_LEARNER);
    	    return (mapping.findForward(LOAD_LEARNER));
    	}

		IVoteService voteService =VoteUtils.getToolService(request);
	 	setContentInUse(request);
    	
    	Long toolContentId=(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
    	logger.debug("toolContentId: " + toolContentId);
    	logger.debug("userEntry: " + userEntry);
    	
    	boolean userEntryAvailable=false;
    	if ((userEntry != null) && (userEntry.length() > 0))
    	{
    	    logger.debug("userEntry available: " + userEntry);
    	    userEntryAvailable=true;
    	}
    	logger.debug("userEntryAvailable " + userEntryAvailable);
    	
    	Long toolSessionId=(Long)request.getSession().getAttribute(TOOL_SESSION_ID);
    	logger.debug("toolSessionId: " + toolSessionId);
    	
    	VoteSession voteSession=voteService.retrieveVoteSession(toolSessionId);
        logger.debug("retrieving voteSession: " + voteSession);
        
        Long toolSessionUid=voteSession.getUid();
        logger.debug("toolSessionUid: " + toolSessionUid);

    	
    	boolean isUserDefined=false;
    	String userID=(String)request.getSession().getAttribute(USER_ID);
    	logger.debug("userID: " + userID);
    	VoteQueUsr existingVoteQueUsr=voteService.getVoteUserBySession(new Long(userID), voteSession.getUid());
    	logger.debug("existingVoteQueUsr: " + existingVoteQueUsr);
    	
    	if (existingVoteQueUsr != null)
    	    isUserDefined=true;
    	
    	logger.debug("isUserDefined: " + isUserDefined);
    	
    	
    	VoteQueUsr voteQueUsr=null;
    	if (isUserDefined == false)
    	{
    	    voteQueUsr=LearningUtil.createUser(request);
    		logger.debug("created user in the db");
    		logger.debug("new create");
    	}
    	else
    	{
    	    voteQueUsr=existingVoteQueUsr;
    	    logger.debug("assign");
    	}
    	
    	logger.debug("voteQueUsr: " + voteQueUsr);
    	logger.debug("voteQueUsr is : " + voteQueUsr);
    	
    	if (existingVoteQueUsr != null)
    	{
    	    logger.debug("attempt removing attempts for user id and session id:" + existingVoteQueUsr.getUid() + " " + voteSession.getUid() );
    	    voteService.removeAttemptsForUserandSession(existingVoteQueUsr.getUid(), voteSession.getUid() );
        	logger.debug("votes deleted for user: " + voteQueUsr.getUid());
    	}
    	
    	// To mimize changes to working code, convert the String[] array to the mapGeneralCheckedOptionsContent structure 
    	VoteContent voteContent=voteSession.getVoteContent();
    	Map mapGeneralCheckedOptionsContent = LearningUtil.buildQuestionContentMap(request, voteContent, voteDisplayOrderIds);
    	
    	logger.debug("mapGeneralCheckedOptionsContent size: " + mapGeneralCheckedOptionsContent.size());
    	
    	if (mapGeneralCheckedOptionsContent.size() > 0)
    	{
    	    LearningUtil.createAttempt(request, voteQueUsr, mapGeneralCheckedOptionsContent, userEntry, false, voteSession);    
    	}
    	
    	
    	if ((mapGeneralCheckedOptionsContent.size() == 0  && (userEntryAvailable == true)))
    	{
    		logger.debug("mapGeneralCheckedOptionsContent size is 0");
    		Map mapLeanerCheckedOptionsContent= new TreeMap(new VoteComparator());
    		mapLeanerCheckedOptionsContent.put("101", userEntry);

			logger.debug("after mapsize check  mapLeanerCheckedOptionsContent " + mapLeanerCheckedOptionsContent);
			if (userEntry.length() > 0)
			{
			    logger.debug("creating entry for: " + userEntry);
			    LearningUtil.createAttempt(request, voteQueUsr, mapLeanerCheckedOptionsContent, userEntry, true, voteSession);    
			}
    	}
    	if ((mapGeneralCheckedOptionsContent.size() > 0) && (userEntryAvailable == true))
    	{
    		logger.debug("mapGeneralCheckedOptionsContent size is > 0" + userEntry);
    		Map mapLeanerCheckedOptionsContent= new TreeMap(new VoteComparator());
    		mapLeanerCheckedOptionsContent.put("102", userEntry);

			logger.debug("after mapsize check  mapLeanerCheckedOptionsContent " + mapLeanerCheckedOptionsContent);
			if (userEntry.length() > 0)
			{
			    logger.debug("creating entry for: " + userEntry);
			    LearningUtil.createAttempt(request, voteQueUsr, mapLeanerCheckedOptionsContent, userEntry, false, voteSession);    
			}
    	}
    
    	
    	logger.debug("created user attempt in the db");
    	
    	// Put the map in the request ready for the next screen
    	request.setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapGeneralCheckedOptionsContent);
    	logger.debug("final mapGeneralCheckedOptionsContent: " + mapGeneralCheckedOptionsContent);
    	
    	voteLearningForm.setNominationsSubmited(new Boolean(true).toString());
    	
    	logger.debug("calling  prepareChartData: " + toolContentId);
    	MonitoringUtil.prepareChartData(request, voteService, null, toolContentId, toolSessionUid);
    	
    	logger.debug("fwding to INDIVIDUAL_REPORT: " + INDIVIDUAL_REPORT);
    	voteLearningForm.resetCommands();
    	return (mapping.findForward(INDIVIDUAL_REPORT));
    }

     public ActionForward redoQuestions(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                      ServletException
   {
        logger.debug("dispatching redoQuestions...");
    	VoteUtils.cleanUpUserExceptions(request);
    	VoteLearningForm voteLearningForm = (VoteLearningForm) form;
    	voteLearningForm.setNominationsSubmited(new Boolean(false).toString());
    	voteLearningForm.setMaxNominationCountReached(new Boolean(false).toString());
    	
	 	IVoteService voteService =VoteUtils.getToolService(request);
	 	
    	Long toolContentId=(Long)request.getSession().getAttribute(TOOL_CONTENT_ID);
    	logger.debug("toolContentId:" + toolContentId);
    	
    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
    	logger.debug("voteContent:" + voteContent);

    	Map mapQuestionsContent= new TreeMap(new VoteComparator());
    	mapQuestionsContent=LearningUtil.buildQuestionContentMap(request,voteContent,null);
	    logger.debug("mapQuestionsContent: " + mapQuestionsContent);
		
		request.setAttribute(MAP_QUESTION_CONTENT_LEARNER, mapQuestionsContent);
		logger.debug("MAP_QUESTION_CONTENT_LEARNER: " +  request.getAttribute(MAP_QUESTION_CONTENT_LEARNER));
		logger.debug("voteContent has : " + mapQuestionsContent.size() + " entries.");
		
		Map mapGeneralCheckedOptionsContent= new TreeMap(new VoteComparator());
	    request.setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapGeneralCheckedOptionsContent);
	    
	    voteLearningForm.setUserEntry("");
	    
	    String previewOnly=(String)request.getSession().getAttribute(PREVIEW_ONLY);
	    logger.debug("previewOnly : " + previewOnly);
	    logger.debug("fwd'ing to LOAD_LEARNER : " + LOAD_LEARNER);
	    voteLearningForm.resetCommands();
	    return (mapping.findForward(LOAD_LEARNER));
   }

    
    
    protected void setContentInUse(HttpServletRequest request)
    {
    	IVoteService voteService =VoteUtils.getToolService(request);
    	Long toolContentId=(Long)request.getSession().getAttribute(TOOL_CONTENT_ID);
    	logger.debug("toolContentId:" + toolContentId);
    	
    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
    	logger.debug("voteContent:" + voteContent);
    	voteContent.setContentInUse(true);
    	logger.debug("content has been set to inuse");
    	voteService.saveVoteContent(voteContent);
    }

    
    
    /**
     * persists error messages to request scope
     * @param request
     * @param message
     */
    public void persistInRequestError(HttpServletRequest request, String message)
	{
		ActionMessages errors= new ActionMessages();
		errors.add(Globals.ERROR_KEY, new ActionMessage(message));
		logger.debug("add " + message +"  to ActionMessages:");
		saveErrors(request,errors);	    	    
	}
    
}
    