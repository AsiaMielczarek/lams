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
package org.lamsfoundation.lams.tool.mc.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McApplicationException;
import org.lamsfoundation.lams.tool.mc.McComparator;
import org.lamsfoundation.lams.tool.mc.McGeneralLearnerFlowDTO;
import org.lamsfoundation.lams.tool.mc.McLearnerAnswersDTO;
import org.lamsfoundation.lams.tool.mc.McStringComparator;
import org.lamsfoundation.lams.tool.mc.McTempDataHolderDTO;
import org.lamsfoundation.lams.tool.mc.McUtils;
import org.lamsfoundation.lams.tool.mc.pojos.McContent;
import org.lamsfoundation.lams.tool.mc.pojos.McOptsContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueUsr;
import org.lamsfoundation.lams.tool.mc.pojos.McSession;
import org.lamsfoundation.lams.tool.mc.pojos.McUsrAttempt;
import org.lamsfoundation.lams.tool.mc.service.IMcService;
import org.lamsfoundation.lams.tool.mc.service.McServiceProxy;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;

/**
 * *
 * 
 * @author Ozgur Demirtas
 * 
 * <p>
 * Action class that controls the logic of tool behavior.
 * </p>
 * 
 * <p>
 * Note that Struts action class only has the responsibility to navigate page flow. All database operation should go to
 * service layer and data transformation from domain model to struts form bean should go to form bean class. This ensure
 * clean and maintainable code.
 * </p>
 * 
 * <code>SystemException</code> is thrown whenever an known error condition is identified. No system exception error
 * handling code should appear in the Struts action class as all of them are handled in
 * <code>CustomStrutsExceptionHandler<code>.
 * 
 <!--Learning Main Action: interacts with the Learning module user -->
 <action	path="/learning"
 type="org.lamsfoundation.lams.tool.mc.web.McLearningAction"
 name="McLearningForm"
 scope="request"
 input="/learning/AnswersContent.jsp"
 parameter="method"
 u7nknown="false"
 validate="false">
 <forward
 name="loadLearner"
 path="/learning/AnswersContent.jsp"
 redirect="false"
 />

 <forward
 name="individualReport"
 path="/learning/IndividualLearnerResults.jsp"
 redirect="false"
 />

 <forward
 name="redoQuestions"
 path="/learning/RedoQuestions.jsp"
 redirect="false"
 />

 <forward
 name="viewAnswers"
 path="/learning/ViewAnswers.jsp"
 redirect="false"
 />

 <forward
 name="resultsSummary"
 path="/learning/ResultsSummary.jsp"
 redirect="false"
 />

 <forward
 name="errorList"
 path="/McErrorBox.jsp"
 redirect="false"
 />

 <forward
 name="starter"
 path="/index.jsp"
 redirect="false"
 />

 <forward
 name="learningStarter"
 path="/learningIndex.jsp"
 redirect="false"
 />

 <forward
 name="preview"
 path="/learning/Preview.jsp"
 redirect="false"
 />
 </action>
 *
 */
public class McLearningAction extends LamsDispatchAction implements McAppConstants {
    static Logger logger = Logger.getLogger(McLearningAction.class.getName());

    /**
     * <p>
     * Default struts dispatch method.
     * </p>
     * 
     * <p>
     * It is assuming that progress engine should pass in the tool access mode and the tool session id as http
     * parameters.
     * </p>
     * 
     * @param mapping
     *                An ActionMapping class that will be used by the Action class to tell the ActionServlet where to
     *                send the end-user.
     * 
     * @param form
     *                The ActionForm class that will contain any data submitted by the end-user via a form.
     * @param request
     *                A standard Servlet HttpServletRequest class.
     * @param response
     *                A standard Servlet HttpServletResponse class.
     * @return An ActionForward class that will be returned to the ActionServlet indicating where the user is to go
     *         next.
     * @throws IOException
     * @throws ServletException
     * @throws McApplicationException
     *                 the known runtime exception
     * 
     * unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     * throws IOException, ServletException
     * 
     * main content/question content management and workflow logic
     * 
     * if the passed toolContentId exists in the db, we need to get the relevant data into the Map if not, create the
     * default Map
     */
    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McLearningForm mcLearningForm = (McLearningForm) form;
	LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	return null;
    }

    /**
     * displayMc(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     * throws IOException, ServletException
     * 
     * responds to learner activity in learner mode.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward displayMc(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McLearningForm mcLearningForm = (McLearningForm) form;
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);
	mcLearningForm.setToolSessionID(toolSessionID);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	String toolContentId = mcSession.getMcContent().getMcContentId().toString();
	McLearningAction.logger.debug("toolContentId: " + toolContentId);
	mcLearningForm.setToolContentID(toolContentId);

	McLearningAction.logger.debug("mcLearningForm nextQuestionSelected : "
		+ mcLearningForm.getNextQuestionSelected());

	if (mcLearningForm.getNextQuestionSelected() != null && !mcLearningForm.getNextQuestionSelected().equals("")) {
	    McLearningAction.logger.debug("processing getNextQuestionSelected...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    mcLearningForm.resetParameters();
	    setContentInUse(request, toolContentId, mcService);
	    return getNextOptions(mapping, form, request, response);
	}

	if (mcLearningForm.getContinueOptionsCombined() != null) {
	    McLearningAction.logger.debug("processing getContinueOptionsCombined...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    return continueOptionsCombined(mapping, form, request, response);
	} else if (mcLearningForm.getNextOptions() != null) {
	    McLearningAction.logger.debug("processing getNextOptions...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    return getNextOptions(mapping, form, request, response);
	} else if (mcLearningForm.getRedoQuestions() != null) {
	    McLearningAction.logger.debug("processing getRedoQuestions...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    return redoQuestions(mapping, form, request, response);
	} else if (mcLearningForm.getRedoQuestionsOk() != null) {
	    McLearningAction.logger.debug("processing getRedoQuestionsOk...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    McLearningAction.logger.debug("requested redoQuestionsOk, user is sure to redo the questions.");
	    return redoQuestions(request, mcLearningForm, mapping);
	} else if (mcLearningForm.getViewAnswers() != null) {
	    McLearningAction.logger.debug("processing getViewAnswers...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    mcLearningForm.setLearnerProgress(new Boolean(false).toString());
	    return viewAnswers(mapping, mcLearningForm, request, response);
	} else if (mcLearningForm.getViewSummary() != null) {
	    McLearningAction.logger.debug("processing getViewSummary...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    return viewSummary(mapping, form, request, response);
	} else if (mcLearningForm.getSubmitReflection() != null) {
	    McLearningAction.logger.debug("processing getSubmitReflection...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    return submitReflection(mapping, form, request, response);
	} else if (mcLearningForm.getForwardtoReflection() != null) {
	    McLearningAction.logger.debug("processing getForwardtoReflection...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    return forwardtoReflection(mapping, form, request, response);
	} else if (mcLearningForm.getLearnerFinished() != null) {
	    McLearningAction.logger.debug("processing getLearnerFinished...");
	    LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	    setContentInUse(request, toolContentId, mcService);
	    return endLearning(mapping, form, request, response);
	}

	return mapping.findForward(McAppConstants.LOAD_LEARNER);
    }

    /**
     * ActionForward endLearning(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse
     * response)
     * 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward endLearning(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McLearningAction.logger.debug("dispatching endLearning ");
	McLearningForm mcLearningForm = (McLearningForm) form;
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);
	mcLearningForm.setToolSessionID(toolSessionID);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	String toolContentId = mcSession.getMcContent().getMcContentId().toString();
	McLearningAction.logger.debug("toolContentId: " + toolContentId);
	mcLearningForm.setToolContentID(toolContentId);

	McLearningAction.logger.debug("mcLearningForm nextQuestionSelected : "
		+ mcLearningForm.getNextQuestionSelected());

	LearningUtil.saveFormRequestData(request, mcLearningForm, false);
	McLearningAction.logger.debug("requested learner finished, the learner should be directed to next activity.");

	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);

	String userID = "";
	HttpSession ss = SessionManager.getSession();
	McLearningAction.logger.debug("ss: " + ss);

	if (ss != null) {
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    if (user != null && user.getUserID() != null) {
		userID = user.getUserID().toString();
		McLearningAction.logger.debug("retrieved userId: " + userID);
	    }
	}

	McLearningAction.logger.debug("attempting to leave/complete session with toolSessionId:" + toolSessionID
		+ " and userID:" + userID);

	McUtils.cleanUpSessionAbsolute(request);

	String nextUrl = null;
	try {
	    nextUrl = mcService.leaveToolSession(new Long(toolSessionID), new Long(userID));
	    McLearningAction.logger.debug("nextUrl: " + nextUrl);
	} catch (DataMissingException e) {
	    McLearningAction.logger.debug("failure getting nextUrl: " + e);
	    return mapping.findForward(McAppConstants.LEARNING_STARTER);
	} catch (ToolException e) {
	    McLearningAction.logger.debug("failure getting nextUrl: " + e);
	    return mapping.findForward(McAppConstants.LEARNING_STARTER);
	} catch (Exception e) {
	    McLearningAction.logger.debug("unknown exception getting nextUrl: " + e);
	    return mapping.findForward(McAppConstants.LEARNING_STARTER);
	}

	McLearningAction.logger.debug("success getting nextUrl: " + nextUrl);

	McQueUsr mcQueUsr = mcService.getMcUserBySession(new Long(userID), mcSession.getUid());
	McLearningAction.logger.debug("mcQueUsr:" + mcQueUsr);

	/* it is possible that mcQueUsr can be null if the content is set as runoffline and reflection is on */
	if (mcQueUsr == null) {
	    McLearningAction.logger
		    .debug("attempt creating  user record since it must exist for the runOffline + reflection screens");

	    UserDTO toolUser = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    McLearningAction.logger.debug("retrieving toolUser: " + toolUser);
	    McLearningAction.logger.debug("retrieving toolUser userId: " + toolUser.getUserID());
	    McLearningAction.logger.debug("retrieving toolUser username: " + toolUser.getLogin());

	    String userName = toolUser.getLogin();
	    String fullName = toolUser.getFirstName() + " " + toolUser.getLastName();
	    McLearningAction.logger.debug("retrieving toolUser fullname: " + fullName);

	    Long userId = new Long(toolUser.getUserID().longValue());
	    McLearningAction.logger.debug("userId: " + userId);
	    McLearningAction.logger.debug("retrieving toolUser fullname: " + fullName);

	    mcQueUsr = new McQueUsr(userId, userName, fullName, mcSession, new TreeSet());
	    mcService.createMcQueUsr(mcQueUsr);
	    McLearningAction.logger.debug("createMcQueUsr - mcQueUsr: " + mcQueUsr);

	    McLearningAction.logger.debug("session uid: " + mcSession.getUid());
	    McLearningAction.logger.debug("mcQueUsr: " + mcQueUsr);
	    mcService.createMcQueUsr(mcQueUsr);
	    McLearningAction.logger.debug("created mcQueUsr in the db: " + mcQueUsr);
	}

	mcQueUsr.setResponseFinalised(true);
	mcService.updateMcQueUsr(mcQueUsr);
	McLearningAction.logger.debug("response finalised for user:" + mcQueUsr);

	mcQueUsr.setViewSummaryRequested(true);
	mcService.updateMcQueUsr(mcQueUsr);
	McLearningAction.logger.debug("view summary requested by mcQueUsr: " + mcQueUsr);

	List userAttempts = mcService.getLatestAttemptsForAUser(mcQueUsr.getUid());
	McLearningAction.logger.debug("userAttempts:" + userAttempts);

	Iterator itAttempts = userAttempts.iterator();
	while (itAttempts.hasNext()) {
	    McUsrAttempt mcUsrAttempt = (McUsrAttempt) itAttempts.next();
	    mcUsrAttempt.setFinished(true);
	    mcService.updateMcUsrAttempt(mcUsrAttempt);
	}

	McLearningAction.logger.debug("redirecting to the nextUrl: " + nextUrl);
	response.sendRedirect(nextUrl);

	return null;
    }

    /**
     * Set parseLearnerInput(List learnerInput, McContent mcContent, IMcService mcService)
     * 
     * @param learnerInput
     * @param mcContent
     * @param mcService
     * @return
     */
    protected Set parseLearnerInput(List learnerInput, McContent mcContent, IMcService mcService) {
	McLearningAction.logger.debug("starting parseLearnerInput: " + learnerInput);
	McLearningAction.logger.debug("mcContent: " + mcContent);
	McLearningAction.logger.debug("mcContent uid: " + mcContent.getUid());

	Set questionUids = new HashSet();

	Iterator listLearnerInputIterator = learnerInput.iterator();
	while (listLearnerInputIterator.hasNext()) {
	    String input = (String) listLearnerInputIterator.next();
	    McLearningAction.logger.debug("input: " + input);
	    int pos = input.indexOf("-");
	    McLearningAction.logger.debug("pos: " + pos);
	    String questionUid = input.substring(0, pos);
	    McLearningAction.logger.debug("questionUid: " + questionUid);
	    questionUids.add(questionUid);
	}
	McLearningAction.logger.debug("final set questionUid: " + questionUids);

	List questionEntriesOrdered = mcService.getAllQuestionEntries(mcContent.getUid());
	McLearningAction.logger.debug("questionEntriesOrdered: " + questionEntriesOrdered);

	Set questionOrderedUids = new TreeSet(new McComparator());
	Iterator questionEntriesOrderedIterator = questionEntriesOrdered.iterator();
	while (questionEntriesOrderedIterator.hasNext()) {
	    McQueContent mcQueContent = (McQueContent) questionEntriesOrderedIterator.next();
	    McLearningAction.logger.debug("mcQueContent: " + mcQueContent);
	    McLearningAction.logger.debug("mcQueContent text: " + mcQueContent.getQuestion());

	    Iterator questionUidsIterator = questionUids.iterator();
	    while (questionUidsIterator.hasNext()) {
		String questionUid = (String) questionUidsIterator.next();
		McLearningAction.logger.debug("questionUid: " + questionUid);

		McLearningAction.logger.debug("questionUid versus objects uid : " + questionUid + " versus "
			+ mcQueContent.getUid());
		if (questionUid.equals(mcQueContent.getUid().toString())) {
		    questionOrderedUids.add(questionUid);
		}
	    }
	}
	McLearningAction.logger.debug("questionOrderedUids: " + questionOrderedUids);
	return questionOrderedUids;
    }

    /**
     * List buildSelectedQuestionAndCandidateAnswersDTO(List learnerInput, McTempDataHolderDTO mcTempDataHolderDTO,
     * IMcService mcService, McContent mcContent)
     * 
     * @param learnerInput
     * @param mcTempDataHolderDTO
     * @param mcService
     * @param mcContent
     * @return
     */
    protected List buildSelectedQuestionAndCandidateAnswersDTO(List learnerInput,
	    McTempDataHolderDTO mcTempDataHolderDTO, IMcService mcService, McContent mcContent) {
	if (LamsDispatchAction.log.isDebugEnabled()) {
	    McLearningAction.logger.debug("starting buildSelectedQuestionAndCandidateAnswersDTO using learnerInput: "
		    + learnerInput + " and mcContent: " + mcContent);
	}

	int learnerMarks = 0;
	int totalMarksPossible = 0;

	Set questionUids = parseLearnerInput(learnerInput, mcContent, mcService);
	if (LamsDispatchAction.log.isDebugEnabled()) {
	    McLearningAction.logger.debug("set questionUids: " + questionUids);
	}

	List questionAndCandidateAnswersList = new LinkedList();

	Iterator questionIterator = mcContent.getMcQueContents().iterator();
	while (questionIterator.hasNext()) {
	    McQueContent mcQueContent = (McQueContent) questionIterator.next();
	    String currentQuestionUid = mcQueContent.getUid().toString();
	    int currentMark = mcQueContent.getMark().intValue();
	    totalMarksPossible += currentMark;

	    McLearnerAnswersDTO mcLearnerAnswersDTO = new McLearnerAnswersDTO();
	    mcLearnerAnswersDTO.setQuestion(mcQueContent.getQuestion());
	    mcLearnerAnswersDTO.setDisplayOrder(mcQueContent.getDisplayOrder().toString());
	    mcLearnerAnswersDTO.setQuestionUid(mcQueContent.getUid());
	    mcLearnerAnswersDTO.setFeedback(mcQueContent.getFeedback() != null ? mcQueContent.getFeedback() : "");

	    Map<String, String> caMap = new TreeMap<String, String>(new McStringComparator());
	    List<String> caIds = new LinkedList<String>();
	    long mapIndex = new Long(1);

	    Iterator listLearnerInputIterator = learnerInput.iterator();
	    while (listLearnerInputIterator.hasNext()) {
		String input = (String) listLearnerInputIterator.next();
		int pos = input.indexOf("-");
		String localQuestionUid = input.substring(0, pos);

		if (LamsDispatchAction.log.isDebugEnabled()) {
		    McLearningAction.logger.debug("input: " + input);
		    McLearningAction.logger.debug("pos: " + pos);
		    McLearningAction.logger.debug("localQuestionUid: " + localQuestionUid);
		}

		if (currentQuestionUid.equals(localQuestionUid)) {
		    String caUid = input.substring(pos + 1);
		    McOptsContent mcOptsContent = mcQueContent.getOptionsContentByUID(new Long(caUid));
		    String mapIndexAsString = new Long(mapIndex).toString();
		    caMap.put(mapIndexAsString, mcOptsContent.getMcQueOptionText());
		    caIds.add(caUid);
		    mapIndex++;

		    if (LamsDispatchAction.log.isDebugEnabled()) {
			McLearningAction.logger.debug("equal uids found : " + localQuestionUid + " caUid: " + caUid);
			McLearningAction.logger.debug("mcOptsContent: " + mcOptsContent);
			McLearningAction.logger.debug("mcOptsContent text: " + mcOptsContent.getMcQueOptionText());
		    }
		}
	    }
	    mcLearnerAnswersDTO.setCandidateAnswers(caMap);

	    List correctOptions = mcService.getPersistedSelectedOptions(mcQueContent.getUid());
	    boolean compareResult = LearningUtil.isQuestionCorrect(correctOptions, caIds);
	    mcLearnerAnswersDTO.setAttemptCorrect(new Boolean(compareResult).toString());
	    if (compareResult) {
		mcLearnerAnswersDTO.setFeedbackCorrect(mcQueContent.getFeedback());
		mcLearnerAnswersDTO.setMark(new Integer(currentMark));
		learnerMarks += currentMark;
	    } else {
		mcLearnerAnswersDTO.setFeedbackIncorrect(mcQueContent.getFeedback());
		mcLearnerAnswersDTO.setMark(new Integer(0));
	    }

	    if (LamsDispatchAction.log.isDebugEnabled()) {
		McLearningAction.logger.debug("mark:: " + learnerMarks);
		McLearningAction.logger.debug("current mcLearnerAnswersDTO: " + mcLearnerAnswersDTO);
	    }

	    questionAndCandidateAnswersList.add(mcLearnerAnswersDTO);

	}// end question iterator

	mcTempDataHolderDTO.setLearnerMark(new Integer(learnerMarks));
	mcTempDataHolderDTO.setTotalMarksPossible(new Integer(totalMarksPossible));

	if (LamsDispatchAction.log.isDebugEnabled()) {
	    McLearningAction.logger.debug("final questionAndCandidateAnswersList: " + questionAndCandidateAnswersList);
	    McLearningAction.logger.debug("final userMarks: " + learnerMarks);
	    McLearningAction.logger.debug("totalMarksPossible: " + totalMarksPossible);
	    McLearningAction.logger.debug("mcTempDataHolderDTO before return : " + mcTempDataHolderDTO);
	}

	return questionAndCandidateAnswersList;
    }

    /**
     * continueOptionsCombined(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse
     * response)
     * 
     * responses to learner when they answer all the questions on a single page
     * 
     * @param request
     * @param form
     * @param mapping
     * @return ActionForward
     */
    public ActionForward continueOptionsCombined(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McLearningAction.logger.debug("dispatching continueOptionsCombined...");
	McLearningForm mcLearningForm = (McLearningForm) form;
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());
	McLearningAction.logger.debug("retrieving mcService: " + mcService);

	String httpSessionID = mcLearningForm.getHttpSessionID();
	McLearningAction.logger.debug("httpSessionID: " + httpSessionID);

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);
	McLearningAction.logger.debug("sessionMap: " + sessionMap);

	String questionListingMode = mcLearningForm.getQuestionListingMode();
	McLearningAction.logger.debug("questionListingMode: " + questionListingMode);

	List learnerInput = new LinkedList();
	if (questionListingMode.equals(McAppConstants.QUESTION_LISTING_MODE_SEQUENTIAL)) {
	    McLearningAction.logger.debug("listing mode is : " + McAppConstants.QUESTION_LISTING_MODE_SEQUENTIAL);

	    List sequentialCheckedCa = (List) sessionMap.get(McAppConstants.QUESTION_AND_CANDIDATE_ANSWERS_KEY);
	    McLearningAction.logger.debug("pre sequentialCheckedCa: " + sequentialCheckedCa);

	    Iterator sequentialCheckedCaIterator = sequentialCheckedCa.iterator();

	    while (sequentialCheckedCaIterator.hasNext()) {
		String input = (String) sequentialCheckedCaIterator.next();
		McLearningAction.logger.debug("input: " + input);
	    }

	    /* checkedCa refers to candidate answers */
	    String[] checkedCa = mcLearningForm.getCheckedCa();
	    McLearningAction.logger.debug("checkedCa: " + checkedCa);

	    if (checkedCa != null) {
		mcLearningForm.resetCa(mapping, request);

		for (int i = 0; i < checkedCa.length; i++) {
		    String currentCa = checkedCa[i];
		    McLearningAction.logger.debug("currentCa: " + currentCa);
		    sequentialCheckedCa.add(currentCa);
		}
	    }

	    McLearningAction.logger.debug("post sequentialCheckedCa: " + sequentialCheckedCa);
	    sequentialCheckedCaIterator = sequentialCheckedCa.iterator();

	    while (sequentialCheckedCaIterator.hasNext()) {
		String input = (String) sequentialCheckedCaIterator.next();
		McLearningAction.logger.debug("input: " + input);
	    }

	    sessionMap.put(McAppConstants.QUESTION_AND_CANDIDATE_ANSWERS_KEY, sequentialCheckedCa);
	    request.getSession().setAttribute(httpSessionID, sessionMap);

	    learnerInput = sequentialCheckedCa;

	    McLearningAction.logger.debug("end processing for mode : "
		    + McAppConstants.QUESTION_LISTING_MODE_SEQUENTIAL);
	} else {
	    Map parameters = request.getParameterMap();
	    Iterator iter = parameters.keySet().iterator();
	    while (iter.hasNext()) {
		String key = (String) iter.next();
		if (key.startsWith("checkedCa")) {
		    String currentCheckedCa = request.getParameter(key);
		    McLearningAction.logger.debug("Found matching checkedCa: key " + key + " value " + currentCheckedCa
			    + ".");
		    if (currentCheckedCa != null) {
			learnerInput.add(currentCheckedCa);
		    }
		}
	    }

	    mcLearningForm.resetCa(mapping, request);
	}

	McLearningAction.logger.debug("final learnerInput: " + learnerInput);

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	String toolContentId = mcSession.getMcContent().getMcContentId().toString();
	McLearningAction.logger.debug("toolContentId: " + toolContentId);

	/* process the answers */
	McContent mcContent = mcService.retrieveMc(new Long(toolContentId));
	McLearningAction.logger.debug("mcContent: " + mcContent);

	McGeneralLearnerFlowDTO mcGeneralLearnerFlowDTO = LearningUtil.buildMcGeneralLearnerFlowDTO(mcContent);
	McLearningAction.logger.debug("constructed a new mcGeneralLearnerFlowDTO");

	McTempDataHolderDTO mcTempDataHolderDTO = new McTempDataHolderDTO();

	boolean allQuestionsChecked = allQuestionsChecked(mcService, learnerInput, mcContent, mcTempDataHolderDTO);
	McLearningAction.logger.debug("allQuestionsChecked: " + allQuestionsChecked);
	McLearningAction.logger.debug("mcTempDataHolderDTO displayOrder: " + mcTempDataHolderDTO.getDisplayOrder());

	if (!allQuestionsChecked) {
	    McLearningAction.logger.debug("there are no selected answers for any questions: " + learnerInput);
	    ActionMessages errors = new ActionMessages();

	    ActionMessage error = new ActionMessage("answers.submitted.none");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	    McLearningAction.logger.debug("errors: " + errors);
	    saveErrors(request, errors);
	    McLearningAction.logger.debug("errors saved: " + errors);

	    McLearningStarterAction mcLearningStarterAction = new McLearningStarterAction();
	    mcLearningStarterAction.commonContentSetup(request, mcContent, mcService, mcLearningForm, toolSessionID);

	    mcGeneralLearnerFlowDTO.setQuestionIndex(mcTempDataHolderDTO.getDisplayOrder());

	    request.setAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO, mcGeneralLearnerFlowDTO);
	    McLearningAction.logger.debug("MC_GENERAL_LEARNER_FLOW_DTO: "
		    + request.getAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO));

	    McLearningAction.logger.debug("returning to LOAD_LEARNER: " + McAppConstants.LOAD_LEARNER);
	    return mapping.findForward(McAppConstants.LOAD_LEARNER);
	}

	List selectedQuestionAndCandidateAnswersDTO = buildSelectedQuestionAndCandidateAnswersDTO(learnerInput,
		mcTempDataHolderDTO, mcService, mcContent);
	McLearningAction.logger.debug("selectedQuestionAndCandidateAnswersDTO: "
		+ selectedQuestionAndCandidateAnswersDTO);
	request.setAttribute(McAppConstants.LIST_SELECTED_QUESTION_CANDIDATEANSWERS_DTO,
		selectedQuestionAndCandidateAnswersDTO);
	McLearningAction.logger.debug("LIST_SELECTED_QUESTION_CANDIDATEANSWERS_DTO: "
		+ request.getAttribute(McAppConstants.LIST_SELECTED_QUESTION_CANDIDATEANSWERS_DTO));

	McLearningAction.logger.debug("mcTempDataHolderDTO becomes: " + mcTempDataHolderDTO);

	mcGeneralLearnerFlowDTO.setQuestionListingMode(McAppConstants.QUESTION_LISTING_MODE_COMBINED);

	Integer learnerMark = mcTempDataHolderDTO.getLearnerMark();
	mcGeneralLearnerFlowDTO.setLearnerMark(learnerMark);
	int totalQuestionCount = mcContent.getMcQueContents().size();
	mcGeneralLearnerFlowDTO.setTotalQuestionCount(new Integer(totalQuestionCount));
	mcGeneralLearnerFlowDTO.setTotalMarksPossible(mcTempDataHolderDTO.getTotalMarksPossible());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	Long toolSessionUid = mcSession.getUid();
	McLearningAction.logger.debug("toolSessionUid: " + toolSessionUid);

	boolean isUserDefined = false;

	String userID = "";
	HttpSession ss = SessionManager.getSession();
	McLearningAction.logger.debug("ss: " + ss);

	if (ss != null) {
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    if (user != null && user.getUserID() != null) {
		userID = user.getUserID().toString();
		McLearningAction.logger.debug("retrieved userId: " + userID);
	    }
	}

	McLearningAction.logger.debug("userID: " + userID);
	McQueUsr existingMcQueUsr = mcService.getMcUserBySession(new Long(userID), mcSession.getUid());
	McLearningAction.logger.debug("existingMcQueUsr: " + existingMcQueUsr);

	if (existingMcQueUsr != null) {
	    isUserDefined = true;
	}

	McLearningAction.logger.debug("isUserDefined: " + isUserDefined);

	McQueUsr mcQueUsr = null;
	if (isUserDefined == false) {
	    mcQueUsr = LearningUtil.createUser(request, mcService, new Long(toolSessionID));
	    McLearningAction.logger.debug("created user in the db: " + mcQueUsr);
	} else {
	    mcQueUsr = existingMcQueUsr;
	    McLearningAction.logger.debug("assign");
	}

	McLearningAction.logger.debug("final mcQueUsr: " + mcQueUsr);

	Integer currentHighestAttemptOrder = mcQueUsr.getLastAttemptOrder();
	int newHighestAttempOrder = currentHighestAttemptOrder != null ? currentHighestAttemptOrder.intValue() + 1 : 1;
	McLearningAction.logger.debug("newHighestAttempOrder: " + newHighestAttempOrder);

	// Have to work out in advance if passed so that we can store it against the attempts
	boolean passed = mcQueUsr.isMarkPassed(learnerMark);
	mcGeneralLearnerFlowDTO.setUserOverPassMark(new Boolean(passed).toString());
	mcGeneralLearnerFlowDTO.setPassMarkApplicable(new Boolean(mcContent.getPassMark() != null).toString());

	LearningUtil.createLearnerAttempt(request, mcQueUsr, selectedQuestionAndCandidateAnswersDTO, passed,
		newHighestAttempOrder, null, mcService);
	McLearningAction.logger.debug("created user attempt in the db");

	mcQueUsr.setLastAttemptOrder(newHighestAttempOrder);
	mcQueUsr.setLastAttemptTotalMark(learnerMark);
	mcService.updateMcQueUsr(mcQueUsr);

	McLearningAction.logger.debug("displayAnswers: " + mcContent.isDisplayAnswers());
	mcGeneralLearnerFlowDTO.setDisplayAnswers(new Boolean(mcContent.isDisplayAnswers()).toString());

	McLearningAction.logger.debug("showMarks: " + mcContent.isShowMarks());
	mcGeneralLearnerFlowDTO.setShowMarks(new Boolean(mcContent.isShowMarks()).toString());
	if (mcContent.isShowMarks()) {
	    Integer[] markStatistics = mcService.getMarkStatistics(mcSession);
	    mcGeneralLearnerFlowDTO.setTopMark(markStatistics[0]);
	    mcGeneralLearnerFlowDTO.setLowestMark(markStatistics[1]);
	    mcGeneralLearnerFlowDTO.setAverageMark(markStatistics[2]);
	} else {
	    Integer zero = new Integer(0);
	    mcGeneralLearnerFlowDTO.setTopMark(zero);
	    mcGeneralLearnerFlowDTO.setLowestMark(zero);
	    mcGeneralLearnerFlowDTO.setAverageMark(zero);
	}

	Map mapQuestionMarks = LearningUtil.buildMarksMap(request, mcContent.getMcContentId(), mcService);
	McLearningAction.logger.debug("mapQuestionMarks:" + mapQuestionMarks);

	McLearningAction.logger.debug("user over passmark:" + mcGeneralLearnerFlowDTO.getUserOverPassMark());
	McLearningAction.logger.debug("is passmark applicable:" + mcGeneralLearnerFlowDTO.getPassMarkApplicable());

	McLearningAction.logger.debug("is tool reflective: " + mcContent.isReflect());
	mcGeneralLearnerFlowDTO.setReflection(new Boolean(mcContent.isReflect()).toString());
	McLearningAction.logger.debug("reflection subject: " + mcContent.getReflectionSubject());

	String reflectionSubject = McUtils.replaceNewLines(mcContent.getReflectionSubject());
	mcGeneralLearnerFlowDTO.setReflectionSubject(reflectionSubject);

	mcGeneralLearnerFlowDTO.setLatestAttemptMark(mcQueUsr.getLastAttemptTotalMark());

	McLearningAction.logger.debug("mcGeneralLearnerFlowDTO for jsp: " + mcGeneralLearnerFlowDTO);

	request.setAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO, mcGeneralLearnerFlowDTO);
	McLearningAction.logger.debug("MC_GENERAL_LEARNER_FLOW_DTO: "
		+ request.getAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO));

	return mapping.findForward(McAppConstants.INDIVIDUAL_REPORT);
    }

    public boolean allQuestionsChecked(IMcService mcService, List learnerInput, McContent mcContent,
	    McTempDataHolderDTO mcTempDataHolderDTO) {
	McLearningAction.logger.debug("starting allQuestionsChecked learnerInput: " + learnerInput);

	boolean questionSelected = false;
	Iterator listIterator = mcContent.getMcQueContents().iterator();
	while (listIterator.hasNext()) {
	    McQueContent mcQueContent = (McQueContent) listIterator.next();
	    String uid = mcQueContent.getUid().toString();
	    McLearningAction.logger.debug("using uid: " + uid);

	    questionSelected = false;
	    Iterator learnerInputIterator = learnerInput.iterator();
	    while (learnerInputIterator.hasNext() && !questionSelected) {
		String learnerInputLine = (String) learnerInputIterator.next();
		McLearningAction.logger.debug("using learnerInputLine: " + learnerInputLine);

		int sepIndex = learnerInputLine.indexOf("-");
		McLearningAction.logger.debug("having sepIndex: " + sepIndex);

		String selectedUid = learnerInputLine.substring(0, sepIndex);
		McLearningAction.logger.debug("selectedUid: " + selectedUid);

		if (uid.equals(selectedUid)) {
		    McLearningAction.logger.debug("equal uids found: " + selectedUid);
		    questionSelected = true;
		}

	    }
	    McLearningAction.logger.debug("iterated loop questionSelected: " + questionSelected);

	    if (questionSelected == false) {
		if (LamsDispatchAction.log.isDebugEnabled()) {
		    McLearningAction.logger.debug("Question not selected by user mcQueContent :" + mcQueContent);
		    McLearningAction.logger.debug("mcQueContent displayorder:" + mcQueContent.getDisplayOrder());
		}
		mcTempDataHolderDTO.setDisplayOrder(mcQueContent.getDisplayOrder());
		return false;
	    }
	}

	return true;
    }

    /**
     * 
     * continueOptionsCombined(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse
     * response)
     * 
     * takes the learner to the next set of questions
     * 
     * @param request
     * @param form
     * @param mapping
     * @return ActionForward
     */
    public ActionForward getNextOptions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McLearningAction.logger.debug("dispatching getNextOptions...");
	McLearningForm mcLearningForm = (McLearningForm) form;
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());
	McLearningAction.logger.debug("mcService: " + mcService);

	String questionIndex = mcLearningForm.getQuestionIndex();
	McLearningAction.logger.debug("questionIndex: " + questionIndex);

	String httpSessionID = mcLearningForm.getHttpSessionID();
	McLearningAction.logger.debug("httpSessionID: " + httpSessionID);

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);
	McLearningAction.logger.debug("sessionMap: " + sessionMap);

	List sequentialCheckedCa = (List) sessionMap.get(McAppConstants.QUESTION_AND_CANDIDATE_ANSWERS_KEY);

	McLearningAction.logger.debug("pre sequentialCheckedCa: " + sequentialCheckedCa);
	Iterator sequentialCheckedCaIterator = sequentialCheckedCa.iterator();
	while (sequentialCheckedCaIterator.hasNext()) {
	    String input = (String) sequentialCheckedCaIterator.next();
	    McLearningAction.logger.debug("input: " + input);
	}

	/* checkedCa refers to candidate answers */
	String[] checkedCa = mcLearningForm.getCheckedCa();
	McLearningAction.logger.debug("checkedCa: " + checkedCa);

	if (checkedCa != null) {
	    mcLearningForm.resetCa(mapping, request);

	    for (int i = 0; i < checkedCa.length; i++) {
		String currentCa = checkedCa[i];
		McLearningAction.logger.debug("currentCa: " + currentCa);
		sequentialCheckedCa.add(currentCa);
	    }
	}

	McLearningAction.logger.debug("post sequentialCheckedCa: " + sequentialCheckedCa);
	sequentialCheckedCaIterator = sequentialCheckedCa.iterator();
	while (sequentialCheckedCaIterator.hasNext()) {
	    String input = (String) sequentialCheckedCaIterator.next();
	    McLearningAction.logger.debug("input: " + input);
	}

	sessionMap.put(McAppConstants.QUESTION_AND_CANDIDATE_ANSWERS_KEY, sequentialCheckedCa);
	request.getSession().setAttribute(httpSessionID, sessionMap);

	McLearningAction.logger.debug("updated sessionMap : " + sessionMap);

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	String toolContentId = mcSession.getMcContent().getMcContentId().toString();
	McLearningAction.logger.debug("toolContentId: " + toolContentId);

	McContent mcContent = mcService.retrieveMc(new Long(toolContentId));
	McLearningAction.logger.debug("mcContent: " + mcContent);

	boolean randomize = mcContent.isRandomize();
	McLearningAction.logger.debug("randomize: " + randomize);

	List listQuestionAndCandidateAnswersDTO = LearningUtil.buildQuestionAndCandidateAnswersDTO(request, mcContent,
		randomize, mcService);
	McLearningAction.logger.debug("listQuestionAndCandidateAnswersDTO: " + listQuestionAndCandidateAnswersDTO);
	request.setAttribute(McAppConstants.LIST_QUESTION_CANDIDATEANSWERS_DTO, listQuestionAndCandidateAnswersDTO);
	McLearningAction.logger.debug("LIST_QUESTION_CANDIDATEANSWERS_DTO: "
		+ request.getAttribute(McAppConstants.LIST_QUESTION_CANDIDATEANSWERS_DTO));

	McGeneralLearnerFlowDTO mcGeneralLearnerFlowDTO = LearningUtil.buildMcGeneralLearnerFlowDTO(mcContent);

	Integer totalQuestionCount = mcGeneralLearnerFlowDTO.getTotalQuestionCount();
	Integer intQuestionIndex = new Integer(questionIndex);
	McLearningAction.logger.debug("intTotalQuestionCount versus intCurrentQuestionIndex: " + totalQuestionCount
		+ " versus " + intQuestionIndex);
	if (totalQuestionCount.equals(intQuestionIndex)) {
	    McLearningAction.logger.debug("totalQuestionCount has been reached :" + totalQuestionCount);
	    mcGeneralLearnerFlowDTO.setTotalCountReached(new Boolean(true).toString());
	}

	McLearningAction.logger.debug("is tool reflective: " + mcContent.isReflect());
	mcGeneralLearnerFlowDTO.setReflection(new Boolean(mcContent.isReflect()).toString());
	McLearningAction.logger.debug("reflection subject: " + mcContent.getReflectionSubject());

	String reflectionSubject = McUtils.replaceNewLines(mcContent.getReflectionSubject());
	mcGeneralLearnerFlowDTO.setReflectionSubject(reflectionSubject);

	mcGeneralLearnerFlowDTO.setRetries(new Boolean(mcContent.isRetries()).toString());

	mcGeneralLearnerFlowDTO.setTotalMarksPossible(mcContent.getTotalMarksPossible());

	McLearningAction.logger.debug("mcGeneralLearnerFlowDTO for jsp: " + mcGeneralLearnerFlowDTO);
	mcGeneralLearnerFlowDTO.setQuestionIndex(new Integer(questionIndex));
	request.setAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO, mcGeneralLearnerFlowDTO);

	return mapping.findForward(McAppConstants.LOAD_LEARNER);
    }

    /**
     * 
     * redoQuestions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     * 
     * allows the learner to take the activity again
     * 
     * @param request
     * @param form
     * @param mapping
     * @return ActionForward
     */
    public ActionForward redoQuestions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McLearningAction.logger.debug("dispatching redoQuestions...");
	McLearningForm mcLearningForm = (McLearningForm) form;
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	String toolContentId = mcSession.getMcContent().getMcContentId().toString();
	McLearningAction.logger.debug("toolContentId: " + toolContentId);

	McContent mcContent = mcService.retrieveMc(new Long(toolContentId));
	McLearningAction.logger.debug("mcContent: " + mcContent);

	boolean randomize = mcContent.isRandomize();
	McLearningAction.logger.debug("randomize: " + randomize);

	List listQuestionAndCandidateAnswersDTO = LearningUtil.buildQuestionAndCandidateAnswersDTO(request, mcContent,
		randomize, mcService);
	McLearningAction.logger.debug("listQuestionAndCandidateAnswersDTO: " + listQuestionAndCandidateAnswersDTO);
	request.setAttribute(McAppConstants.LIST_QUESTION_CANDIDATEANSWERS_DTO, listQuestionAndCandidateAnswersDTO);
	McLearningAction.logger.debug("LIST_QUESTION_CANDIDATEANSWERS_DTO: "
		+ request.getAttribute(McAppConstants.LIST_QUESTION_CANDIDATEANSWERS_DTO));

	McGeneralLearnerFlowDTO mcGeneralLearnerFlowDTO = LearningUtil.buildMcGeneralLearnerFlowDTO(mcContent);
	mcGeneralLearnerFlowDTO.setCurrentQuestionIndex(new Integer(1));
	mcGeneralLearnerFlowDTO.setTotalCountReached(new Boolean(false).toString());

	/* use existing session to extract PASSMARK_APPLICABLE and USER_OVER_PASSMARK */
	String httpSessionID = mcLearningForm.getHttpSessionID();
	McLearningAction.logger.debug("httpSessionID: " + httpSessionID);

	/* create a new session */
	SessionMap sessionMap = new SessionMap();
	List sequentialCheckedCa = new LinkedList();
	sessionMap.put(McAppConstants.QUESTION_AND_CANDIDATE_ANSWERS_KEY, sequentialCheckedCa);
	request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
	mcLearningForm.setHttpSessionID(sessionMap.getSessionID());

	String userID = "";
	HttpSession ss = SessionManager.getSession();
	McLearningAction.logger.debug("ss: " + ss);

	if (ss != null) {
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    if (user != null && user.getUserID() != null) {
		userID = user.getUserID().toString();
		McLearningAction.logger.debug("retrieved userId: " + userID);
	    }
	}

	McQueUsr mcQueUsr = mcService.getMcUserBySession(new Long(userID), mcSession.getUid());
	McLearningAction.logger.debug("mcQueUsr: " + mcQueUsr);

	mcGeneralLearnerFlowDTO.setLatestAttemptMark(mcQueUsr.getLastAttemptTotalMark());

	String passMarkApplicable = new Boolean(mcContent.isPassMarkApplicable()).toString();
	mcGeneralLearnerFlowDTO.setPassMarkApplicable(passMarkApplicable);
	mcLearningForm.setPassMarkApplicable(passMarkApplicable);

	String userOverPassMark = new Boolean(mcQueUsr.isLastAttemptMarkPassed()).toString();
	mcGeneralLearnerFlowDTO.setUserOverPassMark(userOverPassMark);
	mcLearningForm.setUserOverPassMark(userOverPassMark);

	mcGeneralLearnerFlowDTO.setReflection(new Boolean(mcContent.isReflect()).toString());

	String reflectionSubject = McUtils.replaceNewLines(mcContent.getReflectionSubject());
	mcGeneralLearnerFlowDTO.setReflectionSubject(reflectionSubject);

	mcGeneralLearnerFlowDTO.setRetries(new Boolean(mcContent.isRetries()).toString());

	mcGeneralLearnerFlowDTO.setTotalMarksPossible(mcContent.getTotalMarksPossible());

	request.setAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO, mcGeneralLearnerFlowDTO);

	if (McLearningAction.logger.isDebugEnabled()) {
	    McLearningAction.logger.debug("mcGeneralLearnerFlowDTO for jsp: " + mcGeneralLearnerFlowDTO);
	}

	return mapping.findForward(McAppConstants.REDO_QUESTIONS);
    }

    /**
     * void prepareViewAnswersData(ActionMapping mapping, McLearningForm mcLearningForm, HttpServletRequest request,
     * HttpServletResponse response)
     * 
     * @param mapping
     * @param mcLearningForm
     * @param request
     * @param response
     */
    public void prepareViewAnswersData(ActionMapping mapping, McLearningForm mcLearningForm,
	    HttpServletRequest request, HttpServletResponse response) {
	McLearningAction.logger.debug("running  prepareViewAnswersData..." + mcLearningForm);

	// may have to get service from the form - if class has been created by starter action, rather than by struts
	IMcService mcService = null;
	if (getServlet() != null) {
	    mcService = McServiceProxy.getMcService(getServlet().getServletContext());
	} else {
	    mcService = mcLearningForm.getMcService();
	}

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));

	String toolContentId = mcSession.getMcContent().getMcContentId().toString();
	McContent mcContent = mcService.retrieveMc(new Long(toolContentId));

	if (McLearningAction.logger.isDebugEnabled()) {
	    McLearningAction.logger.debug("retrieving mcSession: " + mcSession);
	    McLearningAction.logger.debug("mcContent: " + mcContent);
	}

	McGeneralLearnerFlowDTO mcGeneralLearnerFlowDTO = LearningUtil.buildMcGeneralLearnerFlowDTO(mcContent);

	Map mapQuestionsUidContent = AuthoringUtil.rebuildQuestionUidMapfromDB(request, new Long(toolContentId),
		mcService);

	Map mapStartupGeneralOptionsContent = AuthoringUtil.rebuildStartupGeneralOptionsContentMapfromDB(request,
		mapQuestionsUidContent, mcService);
	mcGeneralLearnerFlowDTO.setMapGeneralOptionsContent(mapStartupGeneralOptionsContent);

	Map mapQuestionsContent = AuthoringUtil.rebuildQuestionMapfromDB(request, new Long(toolContentId), mcService);
	mcGeneralLearnerFlowDTO.setMapQuestionsContent(mapQuestionsContent);

	Map mapFeedbackContent = AuthoringUtil.rebuildFeedbackMapfromDB(request, new Long(toolContentId), mcService);
	mcGeneralLearnerFlowDTO.setMapFeedbackContent(mapFeedbackContent);

	// Set up the user details. If this is the learner progress screen then we that id,
	// otherwise we use the user from the session.

	String learnerProgress = mcLearningForm.getLearnerProgress();
	String learnerProgressUserId = mcLearningForm.getLearnerProgressUserId();
	mcGeneralLearnerFlowDTO.setLearnerProgressUserId(learnerProgressUserId);
	mcGeneralLearnerFlowDTO.setLearnerProgress(learnerProgress);

	Boolean learnerProgressOn = Boolean.parseBoolean(learnerProgress);
	McLearningAction.logger.debug("learnerProgressOn:" + learnerProgressOn);

	McQueUsr mcQueUsr = null;
	if (learnerProgressOn.equals(Boolean.FALSE)) {
	    McLearningAction.logger.debug("learnerProgress off, using user from session");
	    mcQueUsr = LearningUtil.getUser(request, mcService, toolSessionID);
	} else {
	    McLearningAction.logger.debug("using learnerProgressUserId: " + learnerProgressUserId);
	    mcQueUsr = mcService.getMcUserBySession(new Long(learnerProgressUserId), mcSession.getUid());
	}

	if (McLearningAction.logger.isDebugEnabled()) {
	    McLearningAction.logger.debug("final mcQueUsr: " + mcQueUsr);
	}

	Long toolContentUID = mcContent.getUid();

	Map[] attemptMaps = LearningUtil.getAttemptMapsForUser(mcContent.getMcQueContents().size(), toolContentUID,
		mcContent.isRetries(), mcService, mcQueUsr);
	mcGeneralLearnerFlowDTO.setMapFinalAnswersIsContent(attemptMaps[0]);
	mcGeneralLearnerFlowDTO.setMapFinalAnswersContent(attemptMaps[1]);
	mcGeneralLearnerFlowDTO.setMapQueAttempts(attemptMaps[2]);
	mcGeneralLearnerFlowDTO.setMapQueCorrectAttempts(attemptMaps[3]);
	mcGeneralLearnerFlowDTO.setMapQueIncorrectAttempts(attemptMaps[4]);

	mcGeneralLearnerFlowDTO.setReflection(new Boolean(mcContent.isReflect()).toString());
	String reflectionSubject = McUtils.replaceNewLines(mcContent.getReflectionSubject());
	mcGeneralLearnerFlowDTO.setReflectionSubject(reflectionSubject);

	NotebookEntry notebookEntry = mcService.getEntry(new Long(toolSessionID), CoreNotebookConstants.NOTEBOOK_TOOL,
		McAppConstants.MY_SIGNATURE, new Integer(mcQueUsr.getQueUsrId().intValue()));
	if (notebookEntry != null) {
	    String notebookEntryPresentable = notebookEntry.getEntry();
	    notebookEntryPresentable = McUtils.replaceNewLines(notebookEntryPresentable);
	    mcGeneralLearnerFlowDTO.setNotebookEntry(notebookEntryPresentable);
	}

	mcGeneralLearnerFlowDTO.setReportViewOnly(mcLearningForm.getReportViewOnly());
	mcGeneralLearnerFlowDTO.setRetries(new Boolean(mcContent.isRetries()).toString());
	mcGeneralLearnerFlowDTO.setPassMarkApplicable(new Boolean(mcContent.isPassMarkApplicable()).toString());
	mcGeneralLearnerFlowDTO.setUserOverPassMark(new Boolean(mcQueUsr.isLastAttemptMarkPassed()).toString());
	mcGeneralLearnerFlowDTO.setTotalMarksPossible(mcContent.getTotalMarksPossible());
	mcGeneralLearnerFlowDTO.setShowMarks(new Boolean(mcContent.isShowMarks()).toString());
	mcGeneralLearnerFlowDTO.setDisplayAnswers(new Boolean(mcContent.isDisplayAnswers()).toString());
	if (mcContent.isShowMarks()) {
	    Integer[] markStatistics = mcService.getMarkStatistics(mcSession);
	    mcGeneralLearnerFlowDTO.setTopMark(markStatistics[0]);
	    mcGeneralLearnerFlowDTO.setLowestMark(markStatistics[1]);
	    mcGeneralLearnerFlowDTO.setAverageMark(markStatistics[2]);
	} else {
	    Integer zero = new Integer(0);
	    mcGeneralLearnerFlowDTO.setTopMark(zero);
	    mcGeneralLearnerFlowDTO.setLowestMark(zero);
	    mcGeneralLearnerFlowDTO.setAverageMark(zero);
	}

	McLearningAction.logger.debug("mcGeneralLearnerFlowDTO for jsp: " + mcGeneralLearnerFlowDTO);

	request.setAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO, mcGeneralLearnerFlowDTO);
	McLearningAction.logger.debug("MC_GENERAL_LEARNER_FLOW_DTO: "
		+ request.getAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO));
	McLearningAction.logger.debug("end of prepareViewAnswersData.");
    }

    /**
     * 
     * viewAnswers(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     * 
     * allows the learner to view their answer history
     * 
     * @param request
     * @param form
     * @param mapping
     * @return ActionForward
     */
    public ActionForward viewAnswers(ActionMapping mapping, McLearningForm mcLearningForm, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McLearningAction.logger.debug("dispatching viewLearnerProgress..." + mcLearningForm);
	McLearningAction.logger.debug("mcLearningForm :" + mcLearningForm);

	prepareViewAnswersData(mapping, mcLearningForm, request, response);
	McLearningAction.logger.debug("post prepareViewAnswersData");
	return mapping.findForward(McAppConstants.VIEW_ANSWERS);
    }

    /**
     * 
     * viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
     * 
     * allows the learner to view all the other learners' activity summary
     * 
     * @param request
     * @param form
     * @param mapping
     * @return ActionForward
     */
    public ActionForward viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McLearningAction.logger.debug("dispatching viewSummary...");
	McLearningForm mcLearningForm = (McLearningForm) form;
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());
	McLearningAction.logger.debug("mcService: " + mcService);

	String userID = request.getParameter(AttributeNames.PARAM_USER_ID);
	McLearningAction.logger.debug("userID: " + userID);

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	McQueUsr mcQueUsr = mcService.getMcUserBySession(new Long(userID), mcSession.getUid());
	McLearningAction.logger.debug("mcQueUsr: " + mcQueUsr);

	mcQueUsr.setViewSummaryRequested(true);
	mcService.updateMcQueUsr(mcQueUsr);
	McLearningAction.logger.debug("view summary requested by mcQueUsr: " + mcQueUsr);

	String toolContentId = mcSession.getMcContent().getMcContentId().toString();
	McLearningAction.logger.debug("toolContentId: " + toolContentId);

	McContent mcContent = mcService.retrieveMc(new Long(toolContentId));
	McLearningAction.logger.debug("mcContent: " + mcContent);

	McGeneralLearnerFlowDTO mcGeneralLearnerFlowDTO = LearningUtil.buildMcGeneralLearnerFlowDTO(mcContent);

	mcGeneralLearnerFlowDTO.setReflection(new Boolean(mcContent.isReflect()).toString());
	mcGeneralLearnerFlowDTO.setNotebookEntriesVisible(new Boolean(false).toString());

	int countSessionComplete = 0;
	Iterator sessionsIterator = mcContent.getMcSessions().iterator();
	while (sessionsIterator.hasNext()) {
	    McSession mcSessionLocal = (McSession) sessionsIterator.next();
	    if (mcSession != null) {
		McLearningAction.logger.debug("mcSessionLocal: " + mcSessionLocal);
		if (mcSessionLocal.getSessionStatus().equals(McAppConstants.COMPLETED)) {
		    McLearningAction.logger.debug("COMPLETED session found: " + mcSessionLocal);
		    ++countSessionComplete;
		}
	    }
	}
	McLearningAction.logger.debug("countSessionComplete: " + countSessionComplete);

	Integer[] markStatistics = mcService.getMarkStatistics(mcSession);

	McLearningAction.logger.debug("countSessionComplete: " + countSessionComplete);
	McLearningAction.logger.debug("topMark: " + markStatistics[0]);
	McLearningAction.logger.debug("lowestMark: " + markStatistics[1]);
	McLearningAction.logger.debug("averageMark: " + markStatistics[2]);

	mcGeneralLearnerFlowDTO.setCountSessionComplete(new Integer(countSessionComplete).toString());
	mcGeneralLearnerFlowDTO.setTopMark(markStatistics[0]);
	mcGeneralLearnerFlowDTO.setLowestMark(markStatistics[1]);
	mcGeneralLearnerFlowDTO.setAverageMark(markStatistics[2]);

	McLearningAction.logger.debug("is tool reflective: " + mcContent.isReflect());
	mcGeneralLearnerFlowDTO.setReflection(new Boolean(mcContent.isReflect()).toString());
	McLearningAction.logger.debug("reflection subject: " + mcContent.getReflectionSubject());

	String reflectionSubject = McUtils.replaceNewLines(mcContent.getReflectionSubject());
	mcGeneralLearnerFlowDTO.setReflectionSubject(reflectionSubject);

	McLearningAction.logger.debug("mcContent.isRetries(): " + mcContent.isRetries());
	mcGeneralLearnerFlowDTO.setRetries(new Boolean(mcContent.isRetries()).toString());

	String passMarkApplicable = new Boolean(mcContent.isPassMarkApplicable()).toString();
	mcGeneralLearnerFlowDTO.setPassMarkApplicable(passMarkApplicable);
	mcLearningForm.setPassMarkApplicable(passMarkApplicable);

	String userOverPassMark = new Boolean(mcQueUsr.isLastAttemptMarkPassed()).toString();
	mcGeneralLearnerFlowDTO.setUserOverPassMark(userOverPassMark);
	mcLearningForm.setUserOverPassMark(userOverPassMark);

	String httpSessionID = mcLearningForm.getHttpSessionID();
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);
	request.getSession().setAttribute(httpSessionID, sessionMap);

	mcGeneralLearnerFlowDTO.setTotalMarksPossible(mcContent.getTotalMarksPossible());

	McLearningAction.logger.debug("mcGeneralLearnerFlowDTO for jsp: " + mcGeneralLearnerFlowDTO);

	request.setAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO, mcGeneralLearnerFlowDTO);
	McLearningAction.logger.debug("MC_GENERAL_LEARNER_FLOW_DTO: "
		+ request.getAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO));

	return mapping.findForward(McAppConstants.RESULTS_SUMMARY);
    }

    /**
     * setContentInUse(HttpServletRequest request, String toolContentId, IMcService mcService)
     * 
     * indicates that some learners are using the content
     * 
     * @param request
     * @param toolContentId
     * @param mcService
     */
    protected void setContentInUse(HttpServletRequest request, String toolContentId, IMcService mcService) {
	McLearningAction.logger.debug("starting setContentInUse");
	McLearningAction.logger.debug("toolContentId:" + toolContentId);

	McContent mcContent = mcService.retrieveMc(new Long(toolContentId));
	McLearningAction.logger.debug("mcContent:" + mcContent);
	mcContent.setContentInUse(true);
	McLearningAction.logger.debug("content has been set to inuse");
	mcService.saveMcContent(mcContent);
    }

    /**
     * redoQuestions(HttpServletRequest request, McLearningForm mcLearningForm, ActionMapping mapping)
     * 
     * @param request
     * @param mcLearningForm
     * @param mapping
     * @return
     */
    public ActionForward redoQuestions(HttpServletRequest request, McLearningForm mcLearningForm, ActionMapping mapping) {
	McLearningAction.logger.debug("requested redoQuestions...");
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	/* reset the checked options MAP */
	Map mapGeneralCheckedOptionsContent = new TreeMap(new McComparator());

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	String toolContentId = mcSession.getMcContent().getMcContentId().toString();
	McLearningAction.logger.debug("toolContentId: " + toolContentId);

	McContent mcContent = mcService.retrieveMc(new Long(toolContentId));
	McLearningAction.logger.debug("mcContent: " + mcContent);

	boolean randomize = mcContent.isRandomize();
	McLearningAction.logger.debug("randomize: " + randomize);

	List<McLearnerAnswersDTO> listQuestionAndCandidateAnswersDTO = LearningUtil
		.buildQuestionAndCandidateAnswersDTO(request, mcContent, randomize, mcService);
	McLearningAction.logger.debug("listQuestionAndCandidateAnswersDTO: " + listQuestionAndCandidateAnswersDTO);
	request.setAttribute(McAppConstants.LIST_QUESTION_CANDIDATEANSWERS_DTO, listQuestionAndCandidateAnswersDTO);
	McLearningAction.logger.debug("LIST_QUESTION_CANDIDATEANSWERS_DTO: "
		+ request.getAttribute(McAppConstants.LIST_QUESTION_CANDIDATEANSWERS_DTO));

	McGeneralLearnerFlowDTO mcGeneralLearnerFlowDTO = LearningUtil.buildMcGeneralLearnerFlowDTO(mcContent);
	mcGeneralLearnerFlowDTO.setQuestionIndex(new Integer(1));

	McLearningAction.logger.debug("is tool reflective: " + mcContent.isReflect());
	mcGeneralLearnerFlowDTO.setReflection(new Boolean(mcContent.isReflect()).toString());
	McLearningAction.logger.debug("reflection subject: " + mcContent.getReflectionSubject());

	String reflectionSubject = McUtils.replaceNewLines(mcContent.getReflectionSubject());
	mcGeneralLearnerFlowDTO.setReflectionSubject(reflectionSubject);

	McLearningAction.logger.debug("mcContent.isRetries(): " + mcContent.isRetries());
	mcGeneralLearnerFlowDTO.setRetries(new Boolean(mcContent.isRetries()).toString());

	String passMarkApplicable = new Boolean(mcContent.isPassMarkApplicable()).toString();
	mcGeneralLearnerFlowDTO.setPassMarkApplicable(passMarkApplicable);
	mcLearningForm.setPassMarkApplicable(passMarkApplicable);

	String userOverPassMark = Boolean.FALSE.toString();
	mcGeneralLearnerFlowDTO.setUserOverPassMark(userOverPassMark);
	mcLearningForm.setUserOverPassMark(userOverPassMark);

	mcGeneralLearnerFlowDTO.setTotalMarksPossible(mcContent.getTotalMarksPossible());

	// should we show the marks for each question - we show the marks if any of the questions
	// have a mark > 1.
	Boolean showMarks = LearningUtil.isShowMarksOnQuestion(listQuestionAndCandidateAnswersDTO);
	mcGeneralLearnerFlowDTO.setShowMarks(showMarks.toString());

	McLearningAction.logger.debug("mcGeneralLearnerFlowDTO for jsp: " + mcGeneralLearnerFlowDTO);
	request.setAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO, mcGeneralLearnerFlowDTO);
	McLearningAction.logger.debug("MC_GENERAL_LEARNER_FLOW_DTO: "
		+ request.getAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO));

	McLearningAction.logger.debug("fwding to LOAD_LEARNER: " + McAppConstants.LOAD_LEARNER);
	return mapping.findForward(McAppConstants.LOAD_LEARNER);
    }

    /**
     * persists error messages to request scope
     * 
     * @param request
     * @param message
     */
    public void persistError(HttpServletRequest request, String message) {
	ActionMessages errors = new ActionMessages();
	errors.add(Globals.ERROR_KEY, new ActionMessage(message));
	McLearningAction.logger.debug("add " + message + "  to ActionMessages:");
	saveErrors(request, errors);
    }

    /**
     * submitReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse
     * response)
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
    public ActionForward submitReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException, ToolException {
	McLearningAction.logger.debug("dispatching submitReflection...");
	McLearningForm mcLearningForm = (McLearningForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());
	McLearningAction.logger.debug("mcService: " + mcService);

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);
	mcLearningForm.setToolSessionID(toolSessionID);

	String userID = request.getParameter("userID");
	McLearningAction.logger.debug("userID: " + userID);
	mcLearningForm.setUserID(userID);

	String reflectionEntry = request.getParameter(McAppConstants.ENTRY_TEXT);
	McLearningAction.logger.debug("reflectionEntry: " + reflectionEntry);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	McQueUsr mcQueUsr = mcService.getMcUserBySession(new Long(userID), mcSession.getUid());
	McLearningAction.logger.debug("McQueUsr:" + mcQueUsr);

	/* it is possible that mcQueUsr can be null if the content is set as runoffline and reflection is on */
	if (mcQueUsr == null) {
	    McLearningAction.logger
		    .debug("attempt creating  user record since it must exist for the runOffline + reflection screens");
	    HttpSession ss = SessionManager.getSession();

	    UserDTO toolUser = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    McLearningAction.logger.debug("retrieving toolUser: " + toolUser);
	    McLearningAction.logger.debug("retrieving toolUser userId: " + toolUser.getUserID());
	    McLearningAction.logger.debug("retrieving toolUser username: " + toolUser.getLogin());

	    String userName = toolUser.getLogin();
	    String fullName = toolUser.getFirstName() + " " + toolUser.getLastName();
	    McLearningAction.logger.debug("retrieving toolUser fullname: " + fullName);

	    Long userId = new Long(toolUser.getUserID().longValue());
	    McLearningAction.logger.debug("userId: " + userId);
	    McLearningAction.logger.debug("retrieving toolUser fullname: " + fullName);

	    mcQueUsr = new McQueUsr(userId, userName, fullName, mcSession, new TreeSet());
	    mcService.createMcQueUsr(mcQueUsr);
	    McLearningAction.logger.debug("createMcQueUsr - mcQueUsr: " + mcQueUsr);

	    McLearningAction.logger.debug("session uid: " + mcSession.getUid());
	    McLearningAction.logger.debug("mcQueUsr: " + mcQueUsr);
	    mcService.createMcQueUsr(mcQueUsr);
	    McLearningAction.logger.debug("created mcQueUsr in the db: " + mcQueUsr);
	}

	McLearningAction.logger.debug("McQueUsr:" + mcQueUsr);
	McLearningAction.logger.debug("toolSessionID:" + toolSessionID);
	McLearningAction.logger.debug("CoreNotebookConstants.NOTEBOOK_TOOL:" + CoreNotebookConstants.NOTEBOOK_TOOL);
	McLearningAction.logger.debug("MY_SIGNATURE:" + McAppConstants.MY_SIGNATURE);
	McLearningAction.logger.debug("userID:" + userID);
	McLearningAction.logger.debug("reflectionEntry:" + reflectionEntry);

	McLearningAction.logger.debug("attempt getting notebookEntry: ");
	NotebookEntry notebookEntry = mcService.getEntry(new Long(toolSessionID), CoreNotebookConstants.NOTEBOOK_TOOL,
		McAppConstants.MY_SIGNATURE, new Integer(userID));

	McLearningAction.logger.debug("notebookEntry: " + notebookEntry);

	if (notebookEntry != null) {
	    notebookEntry.setEntry(reflectionEntry);
	    mcService.updateEntry(notebookEntry);
	} else {
	    mcService.createNotebookEntry(new Long(toolSessionID), CoreNotebookConstants.NOTEBOOK_TOOL,
		    McAppConstants.MY_SIGNATURE, new Integer(userID), reflectionEntry);
	}

	return endLearning(mapping, form, request, response);
    }

    /**
     * forwardtoReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse
     * response)
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
    public ActionForward forwardtoReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException, ToolException {
	McLearningAction.logger.debug("dispatching forwardtoReflection...");
	McLearningForm mcLearningForm = (McLearningForm) form;
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());
	McLearningAction.logger.debug("mcService: " + mcService);

	String toolSessionID = request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	McLearningAction.logger.debug("toolSessionID: " + toolSessionID);

	McSession mcSession = mcService.retrieveMcSession(new Long(toolSessionID));
	McLearningAction.logger.debug("retrieving mcSession: " + mcSession);

	McContent mcContent = mcSession.getMcContent();
	McLearningAction.logger.debug("using mcContent: " + mcContent);

	McGeneralLearnerFlowDTO mcGeneralLearnerFlowDTO = new McGeneralLearnerFlowDTO();
	mcGeneralLearnerFlowDTO.setActivityTitle(mcContent.getTitle());
	String reflectionSubject = mcContent.getReflectionSubject();

	reflectionSubject = McUtils.replaceNewLines(reflectionSubject);
	mcGeneralLearnerFlowDTO.setReflectionSubject(reflectionSubject);

	String userID = "";
	HttpSession ss = SessionManager.getSession();
	McLearningAction.logger.debug("ss: " + ss);

	if (ss != null) {
	    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    if (user != null && user.getUserID() != null) {
		userID = user.getUserID().toString();
		McLearningAction.logger.debug("retrieved userId: " + userID);
	    }
	}

	McLearningAction.logger.debug("attempt getting notebookEntry: ");
	NotebookEntry notebookEntry = mcService.getEntry(new Long(toolSessionID), CoreNotebookConstants.NOTEBOOK_TOOL,
		McAppConstants.MY_SIGNATURE, new Integer(userID));

	McLearningAction.logger.debug("notebookEntry: " + notebookEntry);

	if (notebookEntry != null) {
	    String notebookEntryPresentable = notebookEntry.getEntry();
	    mcLearningForm.setEntryText(notebookEntryPresentable);

	}

	request.setAttribute(McAppConstants.MC_GENERAL_LEARNER_FLOW_DTO, mcGeneralLearnerFlowDTO);
	McLearningAction.logger.debug("final mcGeneralLearnerFlowDTO: " + mcGeneralLearnerFlowDTO);

	McLearningAction.logger.debug("fwd'ing to: " + McAppConstants.NOTEBOOK);
	return mapping.findForward(McAppConstants.NOTEBOOK);
    }

}
