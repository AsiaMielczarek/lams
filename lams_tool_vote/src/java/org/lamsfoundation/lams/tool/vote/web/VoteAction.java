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
import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;
import org.lamsfoundation.lams.tool.vote.VoteApplicationException;
import org.lamsfoundation.lams.tool.vote.VoteAttachmentDTO;
import org.lamsfoundation.lams.tool.vote.VoteComparator;
import org.lamsfoundation.lams.tool.vote.VoteUtils;
import org.lamsfoundation.lams.tool.vote.pojos.VoteContent;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;
import org.lamsfoundation.lams.tool.vote.service.VoteServiceProxy;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;

/**
 * * @author Ozgur Demirtas
 * 
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
*/
public class VoteAction extends LamsDispatchAction implements VoteAppConstants
{
	/*
	 * when to reset define later and synchin monitor etc..
	 * make sure the tool gets called on:
	 * setAsForceComplete(Long userId) throws VoteApplicationException 
	 */
	static Logger logger = Logger.getLogger(VoteAction.class.getName());
	
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
    	VoteUtils.cleanUpUserExceptions(request);
	 	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
	 	IVoteService voteService =VoteUtils.getToolService(request);
	 	VoteUtils.saveInSessionRichText(request);	 	
	 	voteAuthoringForm.resetUserAction();
	 	return null;
    }
    
    
    public boolean isNewNominationAdded(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
		logger.debug("doing isNewNominationAdded");
		VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
	    logger.debug("voteAuthoringForm :" +voteAuthoringForm);
	    voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());
		
	    AuthoringUtil authoringUtil= new AuthoringUtil();
	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	
		String richTextTitle = request.getParameter("title");
	    String richTextInstructions = request.getParameter("instructions");
	    
	    VoteUtils.saveInSessionRichText(request);
	    
	    authoringUtil.reconstructOptionContentMapForAdd(mapOptionsContent, request);
	    
	    mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("final mapOptionsContent: " + mapOptionsContent);

	    int maxIndex=mapOptionsContent.size();
    	request.getSession().setAttribute(MAX_OPTION_INDEX, new Integer(maxIndex));
    	logger.debug("MAX_OPTION_INDEX: " +  request.getSession().getAttribute(MAX_OPTION_INDEX));
    	
    	String firstEntry=(String)mapOptionsContent.get("1");
    	logger.debug("firstEntry: " +  firstEntry);
    	request.getSession().setAttribute(DEFAULT_OPTION_CONTENT, firstEntry);
    	
    	
    	IVoteService voteService = (IVoteService)request.getSession().getAttribute(TOOL_SERVICE);
	    if (voteService == null)        
	    	voteService = VoteServiceProxy.getVoteService(getServlet().getServletContext());
	    logger.debug("voteService :" +voteService);
	    
	    Long toolContentId =(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
	    logger.debug("toolContentId: " + toolContentId);

    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
		/*true means there is at least 1 response*/
    	if (voteContent != null)
    	{
    		if (voteService.studentActivityOccurredStandardAndOpen(voteContent))
    		{
    				request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(false).toString());
    				logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to false");
    		}
    		else
    		{
    			request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(true).toString());
    			logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to true");
    		}
    	}

	    return true;
        
    }
    
    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward addNewNomination(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException 
    {
		logger.debug("dispathcing addNewNomination");
		boolean isNewNominationAdded=isNewNominationAdded(mapping, form, request, response);
		logger.debug("isNewNominationAdded:" + isNewNominationAdded);
		
		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("final mapOptionsContent: " + mapOptionsContent);

	    return (mapping.findForward(destination));
    }


    public boolean isNominationRemoved(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        logger.debug("starting isNominationRemoved");
		VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
	    logger.debug("voteAuthoringForm :" +voteAuthoringForm);
	    voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());
		
		VoteUtils.saveInSessionRichText(request);
	    
		AuthoringUtil authoringUtil= new AuthoringUtil();
	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("mapOptionsContent: " + mapOptionsContent);
	    
	    authoringUtil.reconstructOptionContentMapForRemove(mapOptionsContent, request, voteAuthoringForm);
	    
	    mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("final mapOptionsContent: " + mapOptionsContent);
	    
        int maxIndex=mapOptionsContent.size();
    	request.getSession().setAttribute(MAX_OPTION_INDEX, new Integer(maxIndex));
    	logger.debug("MAX_OPTION_INDEX: " +  request.getSession().getAttribute(MAX_OPTION_INDEX));
    	
    	String firstEntry=(String)mapOptionsContent.get("1");
    	logger.debug("firstEntry: " +  firstEntry);
    	request.getSession().setAttribute(DEFAULT_OPTION_CONTENT, firstEntry);
    	
    	IVoteService voteService = (IVoteService)request.getSession().getAttribute(TOOL_SERVICE);
	    if (voteService == null)        
	    	voteService = VoteServiceProxy.getVoteService(getServlet().getServletContext());
	    logger.debug("voteService :" +voteService);
	    
	    Long toolContentId =(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
	    logger.debug("toolContentId: " + toolContentId);

    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
		/*true means there is at least 1 response*/
    	if (voteContent != null)
    	{
    		if (voteService.studentActivityOccurredStandardAndOpen(voteContent))
    		{
    				request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(false).toString());
    				logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to false");
    		}
    		else
    		{
    			request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(true).toString());
    			logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to true");
    		}
    	}

    	return true;
    }

    
    public ActionForward removeNomination(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException 
    {
		logger.debug("starting removeNomination ");
		boolean isNominationRemoved=isNominationRemoved(mapping, form, request, response);
		logger.debug("isNominationRemoved:" + isNominationRemoved);
		
	    /* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);
		
	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("final mapOptionsContent: " + mapOptionsContent);
		
	    return (mapping.findForward(destination));
    }


    /**
     * persists the content into the database
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public boolean submitContent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        logger.debug("doing submitContent..");

	    VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
	    logger.debug("voteAuthoringForm :" +voteAuthoringForm);
	    voteAuthoringForm.setSubmissionAttempt(new Boolean(true).toString());
	    request.setAttribute(VALIDATION_ERROR, new Boolean(false).toString());
	    
	    IVoteService voteService = (IVoteService)request.getSession().getAttribute(TOOL_SERVICE);
	    if (voteService == null)        
	    	voteService = VoteServiceProxy.getVoteService(getServlet().getServletContext());
	    logger.debug("voteService :" +voteService);
	    
	    
	    Long toolContentId =(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
	    logger.debug("toolContentId: " + toolContentId);

    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
		/*true means there is at least 1 response*/
    	if (voteContent != null)
    	{
    		if (voteService.studentActivityOccurredStandardAndOpen(voteContent))
    		{
    				request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(false).toString());
    				logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to false");
    		}
    		else
    		{
    			request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(true).toString());
    			logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to true");
    		}
    	}
	    
		VoteUtils.saveInSessionRichText(request);
	           	
	    ActionMessages errors= new ActionMessages();
	    errors=validateSubmit(request, errors, voteAuthoringForm);
	    
	    if (errors.size() > 0)  
	    {
	        logger.debug("returning back to from to fix errors:");
	        request.getSession().setAttribute(EDITACTIVITY_EDITMODE, new Boolean(true));
	        request.setAttribute(VALIDATION_ERROR, new Boolean(true).toString());
	        return false;
	    }
	    
	    List attachmentList = (List) request.getSession().getAttribute(ATTACHMENT_LIST);
	    List deletedAttachmentList = (List) request.getSession().getAttribute(DELETED_ATTACHMENT_LIST);
	
	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("mapOptionsContent :" +mapOptionsContent);

	    if (mapOptionsContent == null)
	        mapOptionsContent= new TreeMap(new VoteComparator());

	    logger.debug("mapOptionsContent :" +mapOptionsContent);
	    boolean nominationsDuplicate=AuthoringUtil.verifyDuplicateNominations(mapOptionsContent);
	    logger.debug("nominationsDuplicate :" +nominationsDuplicate);
	    
	    if (nominationsDuplicate == true)
	    {
	        logger.debug("back to user, nominationsDuplicate :" +nominationsDuplicate);
	        request.setAttribute(USER_EXCEPTION_OPTIONS_DUPLICATE, new Boolean(true).toString());
	        return false;
	    }
	    

        int maxIndex=mapOptionsContent.size();
    	request.getSession().setAttribute(MAX_OPTION_INDEX, new Integer(maxIndex));
    	logger.debug("MAX_OPTION_INDEX: " +  request.getSession().getAttribute(MAX_OPTION_INDEX));
    	
    	String firstEntry=(String)mapOptionsContent.get("1");
    	logger.debug("firstEntry: " +  firstEntry);
    	request.getSession().setAttribute(DEFAULT_OPTION_CONTENT, firstEntry);

    	AuthoringUtil authoringUtil= new AuthoringUtil();
	    authoringUtil.reconstructOptionsContentMapForSubmit(mapOptionsContent, request);
	    logger.debug("before saveOrUpdateVoteContent.");
	    
	 	logger.debug("submitting mapOptionsContent:" + mapOptionsContent);
	    
	    /*to remove deleted entries in the questions table based on mapQuestionContent */
	    authoringUtil.removeRedundantOptions(mapOptionsContent, voteService, voteAuthoringForm, request);
	    logger.debug("end of removing unused entries... ");
	    
	    voteContent=authoringUtil.saveOrUpdateVoteContent(mapOptionsContent, voteService, voteAuthoringForm, request);
	    logger.debug("voteContent: " + voteContent);
		
	    String maxNomCount=voteAuthoringForm.getMaxNominationCount();
	    logger.debug("maxNomCount:" + maxNomCount);
	    
	    String activeModule=voteAuthoringForm.getActiveModule();
	    logger.debug("activeModule:" + activeModule);
	    
	    if (activeModule != null)
	    {
		    if (activeModule.equals(AUTHORING))
		    {
			    if (maxNomCount != null)
			    {
				    if (maxNomCount.equals("0"))
				    {
				        request.setAttribute(USER_EXCEPTION_MAXNOMINATION_INVALID, new Boolean(true).toString());
						return false;
				    }
				    
			    	try
					{
			    		int intMaxNomCount=new Integer(maxNomCount).intValue();
				    	logger.debug("intMaxNomCount : " +intMaxNomCount);
					}
			    	catch(NumberFormatException e)
					{
				        request.setAttribute(USER_EXCEPTION_MAXNOMINATION_INVALID, new Boolean(true).toString());
			    		return false;
					}
			    }

			
				logger.debug("start persisting offline files metadata");
				AuthoringUtil.persistFilesMetaData(request, true, voteContent);
				logger.debug("start persisting online files metadata");
				AuthoringUtil.persistFilesMetaData(request, false, voteContent);
		        
			
			/* making sure only the filenames in the session cache are persisted and the others in the db are removed*/ 
			logger.debug("start removing redundant offline files metadata");
			AuthoringUtil.removeRedundantOfflineFileItems(request, voteContent);
			
			logger.debug("start removing redundant online files metadata");
			AuthoringUtil.removeRedundantOnlineFileItems(request, voteContent);
	 		logger.debug("done removing redundant files");
	 		
		    }
	    }
	
	    errors.clear();
	    errors.add(Globals.ERROR_KEY, new ActionMessage("sbmt.successful"));
        request.setAttribute(SUBMIT_SUCCESS, new Boolean(true).toString());
	    logger.debug("setting SUBMIT_SUCCESS to 1.");
	    
	    Long strToolContentId=(Long)request.getSession().getAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID);
	    logger.debug("strToolContentId: " + strToolContentId);
	    VoteUtils.setDefineLater(request, false);
	    
	    saveErrors(request,errors);
	    
	    VoteUtils.setDefineLater(request, false);
	    logger.debug("define later set to false");
	    
	    voteAuthoringForm.resetUserAction();
	    return true;
    }
    
    
    public ActionForward submitAllContent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
	
		logger.debug("starting submitAllContent :" +form);
		
	    VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
	    logger.debug("voteAuthoringForm :" +voteAuthoringForm);
		
		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

		
		boolean isContentSubmitted=submitContent(mapping, form, request, response);
		logger.debug("isContentSubmitted :" +isContentSubmitted);
		
		if (isContentSubmitted == true)
		    voteAuthoringForm.setSbmtSuccess(new Boolean(true).toString());
		    
		logger.debug("final submit status :" +voteAuthoringForm.getSbmtSuccess());
		logger.debug("final duplicate status :" +request.getAttribute(USER_EXCEPTION_OPTIONS_DUPLICATE));
		logger.debug("fwding to destination :" +destination);
		
	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("final mapOptionsContent: " + mapOptionsContent);

		
        request.setAttribute(AuthoringConstants.LAMS_AUTHORING_SUCCESS_FLAG,Boolean.TRUE);
        return (mapping.findForward(destination));	
    }


    public boolean isMoveNominationDown(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) 
    {
    	logger.debug("starting isMoveNominationDown...");
    	VoteUtils.cleanUpUserExceptions(request);
    	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
	    logger.debug("voteAuthoringForm :" +voteAuthoringForm);
	    voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());

    	IVoteService voteService =VoteUtils.getToolService(request);

		VoteUtils.saveInSessionRichText(request);
	 	
    	Map mapOptionsContent=AuthoringUtil.repopulateMap(request, "optionContent");
     	logger.debug("mapOptionsContent before move down: " + mapOptionsContent);
     	logger.debug("mapOptionsContent size move down: " + mapOptionsContent.size());

     	/* perform a move down if there are at least 2 nominations*/
     	if (mapOptionsContent.size() > 1)
     	{
     		String optIndex =voteAuthoringForm.getOptIndex();
        	logger.debug("optIndex:" + optIndex);
        	String movableOptionEntry=(String)mapOptionsContent.get(optIndex);
        	logger.debug("movableOptionEntry:" + movableOptionEntry);
        	
        	if (movableOptionEntry != null && (!movableOptionEntry.equals("")))
        	{
        	    mapOptionsContent= AuthoringUtil.shiftMap(mapOptionsContent, optIndex,movableOptionEntry,  "down");
            	logger.debug("mapOptionsContent after move down: " + mapOptionsContent);
            	request.getSession().setAttribute(MAP_OPTIONS_CONTENT, mapOptionsContent);
            	logger.debug("updated Options Map: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
        	}
     	}
    	
    	voteAuthoringForm.resetUserAction();
        
    	mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
    	logger.debug("mapOptionsContent: " + mapOptionsContent);
        int maxIndex=mapOptionsContent.size();
    	request.getSession().setAttribute(MAX_OPTION_INDEX, new Integer(maxIndex));
    	logger.debug("MAX_OPTION_INDEX: " +  request.getSession().getAttribute(MAX_OPTION_INDEX));
    	
    	String firstEntry=(String)mapOptionsContent.get("1");
    	logger.debug("firstEntry: " +  firstEntry);
    	request.getSession().setAttribute(DEFAULT_OPTION_CONTENT, firstEntry);
    	
	    Long toolContentId =(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
	    logger.debug("toolContentId: " + toolContentId);

    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
		/*true means there is at least 1 response*/
    	if (voteContent != null)
    	{
    		if (voteService.studentActivityOccurredStandardAndOpen(voteContent))
    		{
    				request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(false).toString());
    				logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to false");
    		}
    		else
    		{
    			request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(true).toString());
    			logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to true");
    		}
    	}

    	return true;
    }
    
    
    /**
     * shifts the nominations map for moving down
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward moveNominationDown(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException
    {
		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

		boolean isMoveNominationDown=isMoveNominationDown(mapping, form, request, response);
		logger.debug("isMoveNominationDown:" + isMoveNominationDown);
		
	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("final mapOptionsContent: " + mapOptionsContent);

        return (mapping.findForward(destination));	
    }


    /**
     * shifts the nominations map for moving up
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public boolean isMoveNominationUp(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
    {
        logger.debug("starting isMoveNominationUp...");
    	VoteUtils.cleanUpUserExceptions(request);
    	
    	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;    	
    	
    	logger.debug("voteAuthoringForm :" +voteAuthoringForm);
    	voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());
    	
    	IVoteService voteService =VoteUtils.getToolService(request);

		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

		VoteUtils.saveInSessionRichText(request);
	 	
    	Map mapOptionsContent=AuthoringUtil.repopulateMap(request, "optionContent");
     	logger.debug("mapOptionsContent before move down: " + mapOptionsContent);
     	logger.debug("mapOptionsContent size move down: " + mapOptionsContent.size());

     	/* perform a move down if there are at least 2 nominations */
     	if (mapOptionsContent.size() > 1)
     	{
     		String optIndex =voteAuthoringForm.getOptIndex();
        	logger.debug("optIndex:" + optIndex);
        	String movableOptionEntry=(String)mapOptionsContent.get(optIndex);
        	logger.debug("movableOptionEntry:" + movableOptionEntry);
        	
        	if (movableOptionEntry != null && (!movableOptionEntry.equals("")))
        	{
        	    mapOptionsContent= AuthoringUtil.shiftMap(mapOptionsContent, optIndex,movableOptionEntry,  "up");
            	logger.debug("mapOptionsContent after move down: " + mapOptionsContent);
            	request.getSession().setAttribute(MAP_OPTIONS_CONTENT, mapOptionsContent);
            	logger.debug("updated Options Map: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
        	}
     	}
    	
    	voteAuthoringForm.resetUserAction();
    	
    	mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
    	logger.debug("mapOptionsContent: " + mapOptionsContent);
        int maxIndex=mapOptionsContent.size();
    	request.getSession().setAttribute(MAX_OPTION_INDEX, new Integer(maxIndex));
    	logger.debug("MAX_OPTION_INDEX: " +  request.getSession().getAttribute(MAX_OPTION_INDEX));

    	String firstEntry=(String)mapOptionsContent.get("1");
    	logger.debug("firstEntry: " +  firstEntry);
    	request.getSession().setAttribute(DEFAULT_OPTION_CONTENT, firstEntry);
    	
	    Long toolContentId =(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
	    logger.debug("toolContentId: " + toolContentId);

    	VoteContent voteContent=voteService.retrieveVote(toolContentId);
		/*true means there is at least 1 response*/
    	if (voteContent != null)
    	{
    		if (voteService.studentActivityOccurredStandardAndOpen(voteContent))
    		{
    				request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(false).toString());
    				logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to false");
    		}
    		else
    		{
    			request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(true).toString());
    			logger.debug("USER_EXCEPTION_NO_TOOL_SESSIONS is set to true");
    		}
    	}

        return true;
    }

    
    public ActionForward moveNominationUp(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException
    {
		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

		boolean isMoveNominationUp=isMoveNominationUp(mapping, form, request, response);
		logger.debug("isMoveNominationUp:" + isMoveNominationUp);
		
	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("final mapOptionsContent: " + mapOptionsContent);

        return (mapping.findForward(destination));	
    }
    
    
    /**
     * checks the user entries before submit is performed
     * @param request
     * @param errors
     * @param voteAuthoringForm
     * @return
     */
    protected ActionMessages validateSubmit(HttpServletRequest request, ActionMessages errors, VoteAuthoringForm voteAuthoringForm)
    {
        String title = voteAuthoringForm.getTitle();
        logger.debug("title: " + title);

        String instructions = voteAuthoringForm.getInstructions();
        logger.debug("instructions: " + instructions);
        
        boolean validateSuccess=true;
        if ((title == null) || (title.trim().length() == 0) || title.equalsIgnoreCase(RICHTEXT_BLANK))
        {
            validateSuccess=false;
        }

        if ((instructions == null) || (instructions.trim().length() == 0) || instructions.equalsIgnoreCase(RICHTEXT_BLANK))
        {
            validateSuccess=false;
        }

        /*
         * enforce that the first (default) question entry is not empty
         */
        String defaultOptionEntry =request.getParameter("optionContent0");
        if ((defaultOptionEntry == null) || (defaultOptionEntry.length() == 0))
        {
            validateSuccess=false;        }
        
        if (validateSuccess == false)
        {
            errors.add(Globals.ERROR_KEY, new ActionMessage("error.fields.mandatory"));
            logger.debug("validate success is false");
        }
        
        saveErrors(request,errors);
        return errors;
    }
    
    
    /**
     * persists offline files
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     * @throws RepositoryCheckedException
     */
    public ActionForward submitOfflineFiles(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException,
                                         RepositoryCheckedException
    {
    	VoteUtils.cleanUpUserExceptions(request);
    	logger.debug("dispatching submitOfflineFile...");
    	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
    	logger.debug("voteAuthoringForm :" +voteAuthoringForm);
    	voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());
    	
    	IVoteService voteService =VoteUtils.getToolService(request);

		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

		VoteUtils.saveInSessionRichText(request);
	 	
		logger.debug("will uploadFile for offline file:");
 		VoteAttachmentDTO voteAttachmentDTO=AuthoringUtil.uploadFile(request, voteAuthoringForm, true);
 		logger.debug("returned voteAttachmentDTO:" + voteAttachmentDTO);
 		
 		if (voteAttachmentDTO == null)
 		{
 			ActionMessages errors= new ActionMessages();
 			errors= new ActionMessages();
 			request.getSession().setAttribute(USER_EXCEPTION_FILENAME_EMPTY, new Boolean(true).toString());
 			errors.add(Globals.ERROR_KEY,new ActionMessage("error.fileName.empty"));
 			saveErrors(request,errors);
 			voteAuthoringForm.resetUserAction();
 			persistInRequestError(request,"error.fileName.empty");
 			
 	   	    return (mapping.findForward(destination));	
 		}
 		
 		 		
 		List listOfflineFilesMetaData =(List)request.getSession().getAttribute(LIST_OFFLINEFILES_METADATA);
 		logger.debug("listOfflineFilesMetaData:" + listOfflineFilesMetaData);
 		listOfflineFilesMetaData.add(voteAttachmentDTO);
 		logger.debug("listOfflineFilesMetaData after add:" + listOfflineFilesMetaData);
 		request.getSession().setAttribute(LIST_OFFLINEFILES_METADATA, listOfflineFilesMetaData);
		
 		voteAuthoringForm.resetUserAction();
   	    return (mapping.findForward(destination));    
    }

    
    /**
     * persists online files
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     * @throws RepositoryCheckedException
     */
    public ActionForward submitOnlineFiles(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException,
                                         RepositoryCheckedException
    {
    	VoteUtils.cleanUpUserExceptions(request);
    	logger.debug("dispatching submitOnlineFiles...");
    	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
    	logger.debug("voteAuthoringForm :" +voteAuthoringForm);
    	voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());
    	
	 	IVoteService voteService =VoteUtils.getToolService(request);

		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

		VoteUtils.saveInSessionRichText(request);
	 	
		logger.debug("will uploadFile for online file:");
 		VoteAttachmentDTO voteAttachmentDTO=AuthoringUtil.uploadFile(request, voteAuthoringForm, false);
 		logger.debug("returned voteAttachmentDTO:" + voteAttachmentDTO);
 		
 		if (voteAttachmentDTO == null)
 		{
 			ActionMessages errors= new ActionMessages();
 			errors= new ActionMessages();
 			request.getSession().setAttribute(USER_EXCEPTION_FILENAME_EMPTY, new Boolean(true).toString());
 			errors.add(Globals.ERROR_KEY,new ActionMessage("error.fileName.empty"));
 			saveErrors(request,errors);
 			voteAuthoringForm.resetUserAction();
 			persistInRequestError(request,"error.fileName.empty");
 			
 		    return (mapping.findForward(destination));	
 		}
 		 		
 		List listOnlineFilesMetaData =(List)request.getSession().getAttribute(LIST_ONLINEFILES_METADATA);
 		logger.debug("listOnlineFilesMetaData:" + listOnlineFilesMetaData);
 		listOnlineFilesMetaData.add(voteAttachmentDTO);
 		logger.debug("listOnlineFilesMetaData after add:" + listOnlineFilesMetaData);
 		request.getSession().setAttribute(LIST_ONLINEFILES_METADATA, listOnlineFilesMetaData);
 		
        voteAuthoringForm.resetUserAction();
   	    return (mapping.findForward(destination));
    }


    /**
     * removes an offline file from the jsp
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward deleteOfflineFile(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException

    {
    	VoteUtils.cleanUpUserExceptions(request);
    	logger.debug("dispatching deleteOfflineFile...");
    	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
    	logger.debug("voteAuthoringForm :" +voteAuthoringForm);
    	voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());
    	
	 	IVoteService voteService =VoteUtils.getToolService(request);
	 	
		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);
	 	
		VoteUtils.saveInSessionRichText(request);
	 	
	 	String uuid =voteAuthoringForm.getUuid();
	 	logger.debug("uuid:" + uuid);
	 	
	 	List listOfflineFilesMetaData =(List)request.getSession().getAttribute(LIST_OFFLINEFILES_METADATA);
 		logger.debug("listOfflineFilesMetaData:" + listOfflineFilesMetaData);
 		listOfflineFilesMetaData=AuthoringUtil.removeFileItem(listOfflineFilesMetaData, uuid);
 		logger.debug("listOfflineFilesMetaData after remove:" + listOfflineFilesMetaData);
 		request.getSession().setAttribute(LIST_OFFLINEFILES_METADATA, listOfflineFilesMetaData);
	 	
        voteAuthoringForm.resetUserAction();
    	
        return (mapping.findForward(destination));
    }

	/**
	 * deletes an online file from the jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
    public ActionForward deleteOnlineFile(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException

    {
    	VoteUtils.cleanUpUserExceptions(request);
    	logger.debug("dispatching deleteOnlineFile...");
    	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
    	logger.debug("voteAuthoringForm :" +voteAuthoringForm);
    	voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());
    	
	 	IVoteService voteService =VoteUtils.getToolService(request);

		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

		VoteUtils.saveInSessionRichText(request);
	 	
	 	String uuid =voteAuthoringForm.getUuid();
	 	logger.debug("uuid:" + uuid);
	 	
	 	List listOnlineFilesMetaData =(List)request.getSession().getAttribute(LIST_ONLINEFILES_METADATA);
 		logger.debug("listOnlineFilesMetaData:" + listOnlineFilesMetaData);
 		listOnlineFilesMetaData=AuthoringUtil.removeFileItem(listOnlineFilesMetaData, uuid);
 		logger.debug("listOnlineFilesMetaData after remove:" + listOnlineFilesMetaData);
 		request.getSession().setAttribute(LIST_ONLINEFILES_METADATA, listOnlineFilesMetaData);
	 	
        voteAuthoringForm.resetUserAction();
        
        return (mapping.findForward(destination));
    }

    /**
     * used in define later to switch from view-only to editable mode
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     * @throws ToolException
     */
    public ActionForward editActivityQuestions(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException,
                                         ServletException,
                                         ToolException
    {
    	logger.debug("dispatching editActivityQuestions...");
    	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
    	logger.debug("voteAuthoringForm :" +voteAuthoringForm);
    	voteAuthoringForm.setSubmissionAttempt(new Boolean(false).toString());
    	
		IVoteService voteService = (IVoteService)request.getSession().getAttribute(TOOL_SERVICE);
		logger.debug("voteService: " + voteService);
		if (voteService == null)
		{
			logger.debug("will retrieve voteService");
			voteService = VoteServiceProxy.getVoteService(getServlet().getServletContext());
			logger.debug("retrieving voteService from session: " + voteService);
		}

		/* determine whether the request is from Monitoring url Edit Activity*/
		String sourceVoteStarter = (String) request.getAttribute(SOURCE_VOTE_STARTER);
		logger.debug("sourceVoteStarter: " + sourceVoteStarter);
		String destination=VoteUtils.getDestination(sourceVoteStarter);
		logger.debug("destination: " + destination);

     	request.getSession().setAttribute(DEFINE_LATER_IN_EDIT_MODE, new Boolean(true));
     	String toolContentId=voteAuthoringForm.getToolContentId();
     	logger.debug("toolContentId: " + toolContentId);
     	if ((toolContentId== null) || toolContentId.equals(""))
     	{
     		logger.debug("getting toolContentId from session.");
     		Long longToolContentId =(Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
     		toolContentId=longToolContentId.toString();
     		logger.debug("toolContentId: " + toolContentId);
     	}
     	
     	VoteContent voteContent=voteService.retrieveVote(new Long(toolContentId));
		logger.debug("existing voteContent:" + voteContent);
    	
		boolean isContentInUse=VoteUtils.isContentInUse(voteContent);
		logger.debug("isContentInUse:" + isContentInUse);
		
		request.getSession().setAttribute(IS_MONITORED_CONTENT_IN_USE, new Boolean(false).toString());
		if (isContentInUse == true)
		{
			logger.debug("monitoring url does not allow editActivity since the content is in use.");
	    	persistInRequestError(request,"error.content.inUse");
	    	request.getSession().setAttribute(IS_MONITORED_CONTENT_IN_USE, new Boolean(true).toString());
		}
     	
		VoteUtils.setDefineLater(request, true);

	    Map mapOptionsContent=(Map)request.getSession().getAttribute(MAP_OPTIONS_CONTENT);
	    logger.debug("final mapOptionsContent: " + mapOptionsContent);

		logger.debug("forwarding to : " + destination);
		return mapping.findForward(destination);
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
    