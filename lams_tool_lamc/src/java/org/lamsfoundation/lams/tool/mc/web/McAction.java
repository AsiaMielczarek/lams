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
package org.lamsfoundation.lams.tool.mc.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.questions.Answer;
import org.lamsfoundation.lams.questions.Question;
import org.lamsfoundation.lams.questions.QuestionParser;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.mc.EditActivityDTO;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McCandidateAnswersDTO;
import org.lamsfoundation.lams.tool.mc.McComparator;
import org.lamsfoundation.lams.tool.mc.McGeneralAuthoringDTO;
import org.lamsfoundation.lams.tool.mc.McQuestionContentDTO;
import org.lamsfoundation.lams.tool.mc.McUtils;
import org.lamsfoundation.lams.tool.mc.pojos.McContent;
import org.lamsfoundation.lams.tool.mc.pojos.McUploadedFile;
import org.lamsfoundation.lams.tool.mc.service.IMcService;
import org.lamsfoundation.lams.tool.mc.service.McServiceProxy;
import org.lamsfoundation.lams.tool.mc.util.McToolContentHandler;
import org.lamsfoundation.lams.util.FileValidatorUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * Action class that controls the logic of tool behavior.
 * </p>
 * 
 * @author Ozgur Demirtas
 * 
 *         <action path="/authoring" type="org.lamsfoundation.lams.tool.mc.web.McAction" name="McAuthoringForm"
 *         input="/AuthoringMaincontent.jsp" parameter="dispatch" scope="request" unknown="false" validate="false" >
 * 
 *         <forward name="load" path="/AuthoringMaincontent.jsp" redirect="false" />
 * 
 *         <forward name="loadMonitoring" path="/monitoring/MonitoringMaincontent.jsp" redirect="false" />
 * 
 *         <forward name="refreshMonitoring" path="/monitoring/MonitoringMaincontent.jsp" redirect="false" />
 * 
 *         <forward name="loadViewOnly" path="/authoring/AuthoringTabsHolder.jsp" redirect="false" />
 * 
 *         <forward name="newQuestionBox" path="/authoring/newQuestionBox.jsp" redirect="false" />
 * 
 *         <forward name="editQuestionBox" path="/authoring/editQuestionBox.jsp" redirect="false" />
 * 
 * 
 *         <forward name="starter" path="/index.jsp" redirect="false" /> </action>
 * 
 */
public class McAction extends LamsDispatchAction implements McAppConstants {
    static Logger logger = Logger.getLogger(McAction.class.getName());

    private McToolContentHandler toolContentHandler;

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
    }

    /**
     * 
     * submits content into the tool database
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward submitAllContent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {

	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	Map mapQuestionContent = AuthoringUtil.extractMapQuestionContent(listQuestionContentDTO);

	Map mapFeedback = AuthoringUtil.extractMapFeedback(listQuestionContentDTO);

	Map mapWeights = new TreeMap(new McComparator());

	String totalMarks = request.getParameter("totalMarks");

	Map mapMarks = AuthoringUtil.extractMapMarks(listQuestionContentDTO);

	Map mapCandidatesList = AuthoringUtil.extractMapCandidatesList(listQuestionContentDTO);

	ActionMessages errors = new ActionMessages();

	if (mapQuestionContent.size() == 0) {
	    ActionMessage error = new ActionMessage("questions.none.submitted");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	AuthoringUtil authoringUtil = new AuthoringUtil();

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    List attachmentListBackup = new ArrayList();
	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    attachmentListBackup = attachmentList;

	    List deletedAttachmentListBackup = new ArrayList();
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);
	    deletedAttachmentListBackup = deletedAttachmentList;

	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);

	}

	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);
	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	mcGeneralAuthoringDTO.setMapQuestionContent(mapQuestionContent);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	// there are no issues with input, continue and submit data

	McContent mcContentTest = mcService.retrieveMc(new Long(strToolContentID));

	if (!errors.isEmpty()) {
	    saveErrors(request, errors);
	    McAction.logger.debug("errors saved: " + errors);
	}

	McContent mcContent = mcContentTest;
	if (errors.isEmpty()) {
	    authoringUtil.removeRedundantQuestions(mapQuestionContent, mcService, mcAuthoringForm, request,
		    strToolContentID);
	    // end of removing unused entries

	    mcContent = authoringUtil.saveOrUpdateMcContent(mapQuestionContent, mapFeedback, mapWeights, mapMarks,
		    mapCandidatesList, mcService, mcAuthoringForm, request, mcContentTest, strToolContentID);

	    long defaultContentID = 0;
	    // attempt retrieving tool with signatute McAppConstants.MY_SIGNATURE
	    defaultContentID = mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE);

	    if (mcContent != null) {
		mcGeneralAuthoringDTO.setDefaultContentIdStr(new Long(defaultContentID).toString());
	    }

	    authoringUtil.reOrganizeDisplayOrder(mapQuestionContent, mcService, mcAuthoringForm, mcContent);

	    if (activeModule.equals(McAppConstants.AUTHORING)) {

		List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);

		List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

		List attachments = saveAttachments(mcContent, attachmentList, deletedAttachmentList, mapping, request);
	    }

	    McUtils.setDefineLater(request, false, strToolContentID, mcService);
	    // define later set to false

	    McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		    defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	    if (activeModule.equals(McAppConstants.AUTHORING)) {
		// standard authoring close
		request.setAttribute(AuthoringConstants.LAMS_AUTHORING_SUCCESS_FLAG, Boolean.TRUE);
		mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());
	    } else {
		// go back to view only screen
		mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(false).toString());
	    }

	} else {
	    // errors is not empty

	    if (mcContent != null) {
		long defaultContentID = 0;
		defaultContentID = mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE);

		if (mcContent != null) {
		    mcGeneralAuthoringDTO.setDefaultContentIdStr(new Long(defaultContentID).toString());
		}

		McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
			defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	    }
	}

	mcGeneralAuthoringDTO.setSbmtSuccess(new Integer(1).toString());

	mcAuthoringForm.resetUserAction();
	mcGeneralAuthoringDTO.setMapQuestionContent(mapQuestionContent);

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);
	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);
	request.getSession().setAttribute(httpSessionID, sessionMap);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	// generating dyn pass map using listQuestionContentDTO
	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);

	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	return mapping.findForward(McAppConstants.LOAD_QUESTIONS);
    }

    public ActionForward saveSingleQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {

	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	String totalMarks = request.getParameter("totalMarks");

	String mark = request.getParameter("mark");

	String passmark = request.getParameter("passmark");

	AuthoringUtil authoringUtil = new AuthoringUtil();

	boolean validateCandidateAnswersNotBlank = authoringUtil.validateCandidateAnswersNotBlank(request);

	List caList = authoringUtil.repopulateCandidateAnswersBox(request, false);

	caList = AuthoringUtil.removeBlankEntries(caList);

	boolean validateSingleCorrectCandidate = authoringUtil.validateSingleCorrectCandidate(caList);

	boolean validateOnlyOneCorrectCandidate = authoringUtil.validateOnlyOneCorrectCandidate(caList);

	ActionMessages errors = new ActionMessages();

	if (!validateCandidateAnswersNotBlank) {
	    ActionMessage error = new ActionMessage("candidates.blank");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!validateSingleCorrectCandidate) {
	    ActionMessage error = new ActionMessage("candidates.none.correct");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!validateOnlyOneCorrectCandidate) {
	    ActionMessage error = new ActionMessage("candidates.duplicate.correct");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!errors.isEmpty()) {
	    saveErrors(request, errors);
	    McAction.logger.debug("errors saved: " + errors);
	}

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setMarkValue(mark);
	mcGeneralAuthoringDTO.setPassMarkValue(passmark);

	if (errors.isEmpty()) {
	    McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	    mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	    mcGeneralAuthoringDTO.setSbmtSuccess(new Integer(0).toString());

	    String newQuestion = request.getParameter("newQuestion");

	    String feedback = request.getParameter("feedback");

	    String editableQuestionIndex = request.getParameter("editableQuestionIndex");
	    mcAuthoringForm.setQuestionIndex(editableQuestionIndex);

	    if ((newQuestion != null) && (newQuestion.length() > 0)) {
		if ((editQuestionBoxRequest != null) && (editQuestionBoxRequest.equals("false"))) {
		    // request for add and save
		    boolean duplicates = AuthoringUtil.checkDuplicateQuestions(listQuestionContentDTO, newQuestion);

		    if (!duplicates) {
			McQuestionContentDTO mcQuestionContentDTO = null;
			Iterator listIterator = listQuestionContentDTO.iterator();
			while (listIterator.hasNext()) {
			    mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

			    String question = mcQuestionContentDTO.getQuestion();
			    String displayOrder = mcQuestionContentDTO.getDisplayOrder();

			    if ((displayOrder != null) && (!displayOrder.equals(""))) {
				if (displayOrder.equals(editableQuestionIndex)) {
				    break;
				}

			    }
			}

			mcQuestionContentDTO.setQuestion(newQuestion);
			mcQuestionContentDTO.setFeedback(feedback);
			mcQuestionContentDTO.setDisplayOrder(editableQuestionIndex);
			mcQuestionContentDTO.setListCandidateAnswersDTO(caList);
			mcQuestionContentDTO.setMark(mark);

			mcQuestionContentDTO.setCaCount(new Integer(mcQuestionContentDTO.getListCandidateAnswersDTO()
				.size()).toString());

			listQuestionContentDTO = AuthoringUtil.reorderUpdateListQuestionContentDTO(
				listQuestionContentDTO, mcQuestionContentDTO, editableQuestionIndex);
			// post reorderUpdateListQuestionContentDTO listQuestionContentDTO
		    } else {
			// duplicate question entry, not adding
		    }
		} else {
		    // request for edit and save
		    McQuestionContentDTO mcQuestionContentDTO = null;
		    Iterator listIterator = listQuestionContentDTO.iterator();
		    while (listIterator.hasNext()) {
			mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

			String question = mcQuestionContentDTO.getQuestion();
			String displayOrder = mcQuestionContentDTO.getDisplayOrder();

			if ((displayOrder != null) && (!displayOrder.equals(""))) {
			    if (displayOrder.equals(editableQuestionIndex)) {
				break;
			    }

			}
		    }

		    mcQuestionContentDTO.setQuestion(newQuestion);
		    mcQuestionContentDTO.setFeedback(feedback);
		    mcQuestionContentDTO.setDisplayOrder(editableQuestionIndex);
		    mcQuestionContentDTO.setListCandidateAnswersDTO(caList);
		    mcQuestionContentDTO.setMark(mark);

		    mcQuestionContentDTO.setCaCount(new Integer(mcQuestionContentDTO.getListCandidateAnswersDTO()
			    .size()).toString());

		    listQuestionContentDTO = AuthoringUtil.reorderUpdateListQuestionContentDTO(listQuestionContentDTO,
			    mcQuestionContentDTO, editableQuestionIndex);
		}
	    } else {
		// entry blank, not adding
	    }

	    mcGeneralAuthoringDTO.setMarkValue(mark);

	    request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);
	    sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	    commonSaveCode(request, mcGeneralAuthoringDTO, mcAuthoringForm, sessionMap, activeModule, strToolContentID,
		    defaultContentIdStr, mcService, httpSessionID, listQuestionContentDTO);

	    request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	    return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
	} else {
	    // errors is not empty

	    commonSaveCode(request, mcGeneralAuthoringDTO, mcAuthoringForm, sessionMap, activeModule, strToolContentID,
		    defaultContentIdStr, mcService, httpSessionID, listQuestionContentDTO);

	    // generating dyn pass map using listQuestionContentDTO
	    Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	    mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	    String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	    mcAuthoringForm.setTotalMarks(totalMark);
	    mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	    request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	    request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);
	    sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	    request.setAttribute("requestNewEditableQuestionBox", new Boolean(true).toString());

	    return newEditableQuestionBox(mapping, form, request, response);
	}

    }

    /**
     * Parses questions extracted from IMS QTI file and adds them to currently edited question.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionForward saveQTI(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	// big part of code was taken from addSingleQuestion() and saveSingleQuestion() methods
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = request.getParameter("httpSessionID");
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);
	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);
	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);
	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);
	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);
	mcGeneralAuthoringDTO.setSbmtSuccess(new Integer(0).toString());

	List<McQuestionContentDTO> listQuestionContentDTO = (List<McQuestionContentDTO>) sessionMap
		.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);
	// proper parsing
	Question[] questions = QuestionParser.parseQuestionChoiceForm(request);

	for (Question question : questions) {
	    // quietly do same verification as in other question-adding methods
	    String questionText = question.getText();
	    if (StringUtils.isBlank(questionText)) {
		LamsDispatchAction.log.warn("Skipping a blank question.");
		continue;
	    }

	    questionText = QuestionParser.processHTMLField(questionText, false, contentFolderID,
		    question.getResourcesFolderPath());

	    if (AuthoringUtil.checkDuplicateQuestions(listQuestionContentDTO, questionText)) {
		LamsDispatchAction.log.warn("Skipping duplicate question: " + questionText);
		continue;
	    }

	    List<McCandidateAnswersDTO> caList = new ArrayList<McCandidateAnswersDTO>();
	    String correctAnswer = null;
	    Integer correctAnswerScore = 1;

	    if (question.getAnswers() != null) {
		for (Answer answer : question.getAnswers()) {
		    McCandidateAnswersDTO mcCandidateAnswersDTO = new McCandidateAnswersDTO();
		    String answerText = QuestionParser.processHTMLField(answer.getText(), true, null, null);
		    if (answerText == null) {
			LamsDispatchAction.log.warn("Skipping a blank answer");
			continue;
		    }
		    if (correctAnswer != null && correctAnswer.equals(answerText)) {
			LamsDispatchAction.log.warn("Skipping an answer with same text as the correct answer: "
				+ answerText);

			continue;
		    }

		    mcCandidateAnswersDTO.setCandidateAnswer(answerText);

		    if ((answer.getScore() != null) && (answer.getScore() > 0)) {
			if (correctAnswer == null) {
			    mcCandidateAnswersDTO.setCorrect("Correct");
			    correctAnswer = mcCandidateAnswersDTO.getCandidateAnswer();
			    // marks are integer numbers
			    correctAnswerScore = Math.min(new Double(Math.ceil(answer.getScore())).intValue(), 10);
			} else {
			    // there can be only one correct answer in a MCQ question
			    LamsDispatchAction.log
				    .warn("Choosing only first correct answer, despite another one was found: "
					    + answerText);
			    mcCandidateAnswersDTO.setCorrect("Incorrect");
			}
		    } else {
			mcCandidateAnswersDTO.setCorrect("Incorrect");
		    }

		    caList.add(mcCandidateAnswersDTO);
		}
	    }

	    if (correctAnswer == null) {
		LamsDispatchAction.log.warn("No correct answer found for question: " + questionText);
		continue;
	    }

	    McQuestionContentDTO mcQuestionContentDTO = new McQuestionContentDTO();
	    mcQuestionContentDTO.setDisplayOrder(String.valueOf(listQuestionContentDTO.size() + 1));
	    mcQuestionContentDTO.setQuestion(questionText);
	    mcQuestionContentDTO.setFeedback(QuestionParser.processHTMLField(question.getFeedback(), true, null, null));
	    mcQuestionContentDTO.setListCandidateAnswersDTO(caList);
	    mcQuestionContentDTO.setMark(correctAnswerScore.toString());
	    mcQuestionContentDTO.setCaCount(String.valueOf(caList.size()));

	    listQuestionContentDTO.add(mcQuestionContentDTO);

	    if (LamsDispatchAction.log.isDebugEnabled()) {
		LamsDispatchAction.log.debug("Added question: " + questionText);
	    }
	}

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);
	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	commonSaveCode(request, mcGeneralAuthoringDTO, mcAuthoringForm, sessionMap, activeModule, strToolContentID,
		defaultContentIdStr, mcService, httpSessionID, listQuestionContentDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));
	return (mapping.findForward(McAppConstants.LOAD));
    }

    protected void commonSaveCode(HttpServletRequest request, McGeneralAuthoringDTO mcGeneralAuthoringDTO,
	    McAuthoringForm mcAuthoringForm, SessionMap sessionMap, String activeModule, String strToolContentID,
	    String defaultContentIdStr, IMcService mcService, String httpSessionID, List listQuestionContentDTO) {
	String richTextTitle = request.getParameter(McAppConstants.TITLE);
	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);

	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);

	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);
	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);

	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	AuthoringUtil authoringUtil = new AuthoringUtil();
	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);
	request.getSession().setAttribute(httpSessionID, sessionMap);
    }

    public ActionForward addSingleQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {

	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String totalMarks = request.getParameter("totalMarks");

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setSbmtSuccess(new Integer(0).toString());

	AuthoringUtil authoringUtil = new AuthoringUtil();

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	List listAddableQuestionContentDTO = (List) sessionMap.get(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY);

	McQuestionContentDTO mcQuestionContentDTONew = null;

	int listSize = listQuestionContentDTO.size();

	request.setAttribute(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_LIST, listAddableQuestionContentDTO);

	String newQuestion = request.getParameter("newQuestion");

	String feedback = request.getParameter("feedback");

	String mark = request.getParameter("mark");
	mcGeneralAuthoringDTO.setMarkValue(mark);

	String passmark = request.getParameter("passmark");
	mcGeneralAuthoringDTO.setPassMarkValue(passmark);

	boolean validateCandidateAnswersNotBlank = authoringUtil.validateCandidateAnswersNotBlank(request);

	List caList = authoringUtil.repopulateCandidateAnswersBox(request, false);

	caList = AuthoringUtil.removeBlankEntries(caList);

	boolean validateSingleCorrectCandidate = authoringUtil.validateSingleCorrectCandidate(caList);

	boolean validateOnlyOneCorrectCandidate = authoringUtil.validateOnlyOneCorrectCandidate(caList);

	ActionMessages errors = new ActionMessages();

	if (!validateCandidateAnswersNotBlank) {
	    ActionMessage error = new ActionMessage("candidates.blank");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!validateSingleCorrectCandidate) {
	    ActionMessage error = new ActionMessage("candidates.none.correct");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!validateOnlyOneCorrectCandidate) {
	    ActionMessage error = new ActionMessage("candidates.duplicate.correct");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!errors.isEmpty()) {
	    saveErrors(request, errors);
	    McAction.logger.debug("errors saved: " + errors);
	}

	if (errors.isEmpty()) {
	    if ((newQuestion != null) && (newQuestion.length() > 0)) {
		boolean duplicates = AuthoringUtil.checkDuplicateQuestions(listQuestionContentDTO, newQuestion);

		if (!duplicates) {
		    McQuestionContentDTO mcQuestionContentDTO = new McQuestionContentDTO();
		    mcQuestionContentDTO.setDisplayOrder(new Long(listSize + 1).toString());
		    mcQuestionContentDTO.setFeedback(feedback);
		    mcQuestionContentDTO.setQuestion(newQuestion);
		    mcQuestionContentDTO.setMark(mark);

		    mcQuestionContentDTO.setListCandidateAnswersDTO(caList);
		    mcQuestionContentDTO.setCaCount(new Integer(mcQuestionContentDTO.getListCandidateAnswersDTO()
			    .size()).toString());

		    listQuestionContentDTO.add(mcQuestionContentDTO);
		} else {
		    // entry duplicate, not adding
		}
	    } else {
		// entry blank, not adding
	    }
	} else {
	    // errors, not adding

	    commonSaveCode(request, mcGeneralAuthoringDTO, mcAuthoringForm, sessionMap, activeModule, strToolContentID,
		    defaultContentIdStr, mcService, httpSessionID, listQuestionContentDTO);

	    Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	    mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	    String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	    mcAuthoringForm.setTotalMarks(totalMark);
	    mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	    request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	    request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);
	    sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	    return newQuestionBox(mapping, form, request, response);
	}

	mcGeneralAuthoringDTO.setMarkValue(mark);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);
	mcAuthoringForm.setFeedback(feedback);

	mcGeneralAuthoringDTO.setMarkValue(mark);
	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);
	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	commonSaveCode(request, mcGeneralAuthoringDTO, mcAuthoringForm, sessionMap, activeModule, strToolContentID,
		defaultContentIdStr, mcService, httpSessionID, listQuestionContentDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
    }

    /**
     * opens up an new screen within the current page for adding a new question
     * 
     * newQuestionBox
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward newQuestionBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);
	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String totalMarks = request.getParameter("totalMarks");

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	/* create default mcContent object */
	McContent mcContent = mcService.retrieveMc(new Long(defaultContentIdStr));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);
	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);
	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	AuthoringUtil authoringUtil = new AuthoringUtil();
	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	String requestType = request.getParameter("requestType");

	List listAddableQuestionContentDTO = (List) sessionMap.get(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY);

	if ((requestType != null) && (requestType.equals("direct"))) {
	    // requestType is direct
	    listAddableQuestionContentDTO = authoringUtil.buildDefaultQuestionContent(mcContent, mcService);
	}

	request.setAttribute(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_LIST, listAddableQuestionContentDTO);
	sessionMap.put(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY, listAddableQuestionContentDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String newQuestion = request.getParameter("newQuestion");
	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	String feedback = request.getParameter("feedback");
	mcAuthoringForm.setFeedback(feedback);

	String mark = request.getParameter("mark");
	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);

	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);

	return (mapping.findForward("newQuestionBox"));
    }

    /**
     * opens up an new screen within the current page for editing a question
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward newEditableQuestionBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	String totalMarks = request.getParameter("totalMarks");

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	request.setAttribute(McAppConstants.CURRENT_EDITABLE_QUESTION_INDEX, questionIndex);

	mcAuthoringForm.setEditableQuestionIndex(questionIndex);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	String editableQuestion = "";
	String editableFeedback = "";
	String editableMark = "";
	Iterator listIterator = listQuestionContentDTO.iterator();
	while (listIterator.hasNext()) {
	    McQuestionContentDTO mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();
	    String question = mcQuestionContentDTO.getQuestion();
	    String displayOrder = mcQuestionContentDTO.getDisplayOrder();

	    if ((displayOrder != null) && (!displayOrder.equals(""))) {
		if (displayOrder.equals(questionIndex)) {
		    editableFeedback = mcQuestionContentDTO.getFeedback();
		    editableQuestion = mcQuestionContentDTO.getQuestion();
		    editableMark = mcQuestionContentDTO.getMark();

		    List candidates = mcQuestionContentDTO.getListCandidateAnswersDTO();

		    break;
		}

	    }
	}

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);
	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);
	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = (McGeneralAuthoringDTO) request
		.getAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO);

	if (mcGeneralAuthoringDTO == null) {
	    mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	}

	mcGeneralAuthoringDTO.setMarkValue(editableMark);
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);
	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);
	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setEditableQuestionText(editableQuestion);
	mcGeneralAuthoringDTO.setEditableQuestionFeedback(editableFeedback);
	mcAuthoringForm.setFeedback(editableFeedback);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	AuthoringUtil authoringUtil = new AuthoringUtil();
	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);
	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	String requestNewEditableQuestionBox = (String) request.getAttribute("requestNewEditableQuestionBox");

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	String newQuestion = request.getParameter("newQuestion");

	// if ((editQuestionBoxRequest != null) && (editQuestionBoxRequest.equals("false")))
	if ((requestNewEditableQuestionBox != null) && requestNewEditableQuestionBox.equals("true")) {
	    // String newQuestion=request.getParameter("newQuestion");
	    // logger.debug("newQuestion: " + newQuestion);
	    mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	    String feedback = request.getParameter("feedback");
	    mcAuthoringForm.setFeedback(feedback);
	}

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);
	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);

	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	return (mapping.findForward("editQuestionBox"));
    }

    /**
     * removes a question from the questions map
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward removeQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	String totalMarks = request.getParameter("totalMarks");

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	McQuestionContentDTO mcQuestionContentDTO = null;
	Iterator listIterator = listQuestionContentDTO.iterator();
	while (listIterator.hasNext()) {
	    mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

	    String question = mcQuestionContentDTO.getQuestion();
	    String displayOrder = mcQuestionContentDTO.getDisplayOrder();

	    if ((displayOrder != null) && (!displayOrder.equals(""))) {
		if (displayOrder.equals(questionIndex)) {
		    break;
		}

	    }
	}

	mcQuestionContentDTO.setQuestion("");

	listQuestionContentDTO = AuthoringUtil.reorderListQuestionContentDTO(listQuestionContentDTO, questionIndex);

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);
	String richTextTitle = request.getParameter(McAppConstants.TITLE);
	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);
	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);
	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	if (mcContent == null) {
	    mcContent = mcService.retrieveMc(new Long(defaultContentIdStr));
	}

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	AuthoringUtil authoringUtil = new AuthoringUtil();

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);
	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
    }

    /**
     * moves a question down in the list
     * 
     * moveQuestionDown
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward moveQuestionDown(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	String totalMarks = request.getParameter("totalMarks");

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	listQuestionContentDTO = AuthoringUtil.swapNodes(listQuestionContentDTO, questionIndex, "down");

	listQuestionContentDTO = AuthoringUtil.reorderSimpleListQuestionContentDTO(listQuestionContentDTO);

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	AuthoringUtil authoringUtil = new AuthoringUtil();

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);
	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);
	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);
	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);
	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));
	return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
    }

    public ActionForward moveQuestionUp(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	listQuestionContentDTO = AuthoringUtil.swapNodes(listQuestionContentDTO, questionIndex, "up");

	listQuestionContentDTO = AuthoringUtil.reorderSimpleListQuestionContentDTO(listQuestionContentDTO);

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String totalMarks = request.getParameter("totalMarks");

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);
	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	AuthoringUtil authoringUtil = new AuthoringUtil();

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
    }

    /**
     * 
     * adds a new file to content repository
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward addNewFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String totalMarks = request.getParameter("totalMarks");

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String onlineInstructions = request.getParameter(McAppConstants.ONLINE_INSTRUCTIONS);

	String offlineInstructions = request.getParameter(McAppConstants.OFFLINE_INSTRUCTIONS);

	sessionMap.put(McAppConstants.ONLINE_INSTRUCTIONS_KEY, onlineInstructions);
	sessionMap.put(McAppConstants.OFFLINE_INSTRUCTIONS, offlineInstructions);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);
	mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	mcGeneralAuthoringDTO.setSbmtSuccess(new Integer(0).toString());

	String richTextTitle = request.getParameter(McAppConstants.TITLE);
	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	addFileToContentRepository(request, mcAuthoringForm, attachmentList, deletedAttachmentList, sessionMap,
		mcGeneralAuthoringDTO);

	sessionMap.put(McAppConstants.ATTACHMENT_LIST_KEY, attachmentList);
	sessionMap.put(McAppConstants.DELETED_ATTACHMENT_LIST_KEY, deletedAttachmentList);

	mcGeneralAuthoringDTO.setAttachmentList(attachmentList);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("3");

	AuthoringUtil authoringUtil = new AuthoringUtil();
	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	mcAuthoringForm.resetUserAction();

	String strOnlineInstructions = request.getParameter("onlineInstructions");
	String strOfflineInstructions = request.getParameter("offlineInstructions");
	mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
    }

    /**
     * 
     * deletes a file from the content repository
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward deleteFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String totalMarks = request.getParameter("totalMarks");

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setSbmtSuccess(new Integer(0).toString());

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);

	String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);

	mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);
	mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);
	mcAuthoringForm.setOnlineInstructions(onlineInstructions);
	mcAuthoringForm.setOfflineInstructions(offlineInstructions);

	String richTextTitle = (String) sessionMap.get(McAppConstants.ACTIVITY_TITLE_KEY);
	String richTextInstructions = (String) sessionMap.get(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY);
	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	long uuid = WebUtil.readLongParam(request, McAppConstants.UUID);

	List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);

	if (attachmentList == null) {
	    attachmentList = new ArrayList();
	}

	List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	if (deletedAttachmentList == null) {
	    deletedAttachmentList = new ArrayList();
	}

	/*
	 * move the file's details from the attachment collection to the deleted attachments collection the attachment
	 * will be delete on saving.
	 */

	deletedAttachmentList = McUtils.moveToDelete(Long.toString(uuid), attachmentList, deletedAttachmentList);

	sessionMap.put(McAppConstants.ATTACHMENT_LIST_KEY, attachmentList);
	sessionMap.put(McAppConstants.DELETED_ATTACHMENT_LIST_KEY, deletedAttachmentList);

	mcGeneralAuthoringDTO.setAttachmentList(attachmentList);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("3");

	AuthoringUtil authoringUtil = new AuthoringUtil();
	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	mcAuthoringForm.resetUserAction();
	return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
    }

    /**
     * 
     * persists error messages to request scope
     * 
     * @param request
     * @param message
     */
    public void persistError(HttpServletRequest request, String message) {
	ActionMessages errors = new ActionMessages();
	errors.add(Globals.ERROR_KEY, new ActionMessage(message));
	saveErrors(request, errors);
    }

    /**
     * addFileToContentRepository(HttpServletRequest request, McAuthoringForm mcAuthoringForm, List attachmentList, List
     * deletedAttachmentList, SessionMap sessionMap, McGeneralAuthoringDTO mcGeneralAuthoringDTO)
     * 
     * @param request
     * @param mcAuthoringForm
     */
    public void addFileToContentRepository(HttpServletRequest request, McAuthoringForm mcAuthoringForm,
	    List attachmentList, List deletedAttachmentList, SessionMap sessionMap,
	    McGeneralAuthoringDTO mcGeneralAuthoringDTO) {
	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	if (attachmentList == null) {
	    attachmentList = new ArrayList();
	}

	if (deletedAttachmentList == null) {
	    deletedAttachmentList = new ArrayList();
	}

	FormFile uploadedFile = null;
	boolean isOnlineFile = false;
	String fileType = null;
	if ((mcAuthoringForm.getTheOfflineFile() != null) && (mcAuthoringForm.getTheOfflineFile().getFileSize() > 0)) {
	    // theOfflineFile is available
	    uploadedFile = mcAuthoringForm.getTheOfflineFile();
	    fileType = IToolContentHandler.TYPE_OFFLINE;
	} else if ((mcAuthoringForm.getTheOnlineFile() != null)
		&& (mcAuthoringForm.getTheOnlineFile().getFileSize() > 0)) {
	    // theOnlineFile is available
	    uploadedFile = mcAuthoringForm.getTheOnlineFile();
	    isOnlineFile = true;
	    fileType = IToolContentHandler.TYPE_ONLINE;
	} else {
	    /* no file uploaded */
	    return;
	}

	// validate upload file size.
	ActionMessages errors = new ActionMessages();
	FileValidatorUtil.validateFileSize(uploadedFile, true, errors);
	if (!errors.isEmpty()) {
	    this.saveErrors(request, errors);
	    return;
	}

	/* if a file with the same name already exists then move the old one to deleted */
	deletedAttachmentList = McUtils.moveToDelete(uploadedFile.getFileName(), isOnlineFile, attachmentList,
		deletedAttachmentList);

	try {
	    /*
	     * This is a new file and so is saved to the content repository. Add it to the attachments collection, but
	     * don't add it to the tool's tables yet.
	     */
	    NodeKey node = getToolContentHandler().uploadFile(uploadedFile.getInputStream(),
		    uploadedFile.getFileName(), uploadedFile.getContentType(), fileType);
	    McUploadedFile file = new McUploadedFile();
	    String fileName = uploadedFile.getFileName();

	    if ((fileName != null) && (fileName.length() > 30)) {
		fileName = fileName.substring(0, 31);
	    }

	    file.setFileName(fileName);
	    file.setFileOnline(isOnlineFile);
	    file.setUuid(node.getUuid().toString());
	    /* file.setVersionId(node.getVersion()); */

	    /* add the files to the attachment collection - if one existed, it should have already been removed. */
	    attachmentList.add(file);

	    /* reset the fields so that more files can be uploaded */
	    mcAuthoringForm.setTheOfflineFile(null);
	    mcAuthoringForm.setTheOnlineFile(null);
	} catch (FileNotFoundException e) {
	    McAction.logger.error("Unable to uploadfile", e);
	    throw new RuntimeException("Unable to upload file, exception was " + e.getMessage());
	} catch (IOException e) {
	    McAction.logger.error("Unable to uploadfile", e);
	    throw new RuntimeException("Unable to upload file, exception was " + e.getMessage());
	} catch (RepositoryCheckedException e) {
	    McAction.logger.error("Unable to uploadfile", e);
	    throw new RuntimeException("Unable to upload file, exception was " + e.getMessage());
	}
    }

    private McToolContentHandler getToolContentHandler() {
	if (toolContentHandler == null) {
	    WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
		    .getServletContext());
	    toolContentHandler = (McToolContentHandler) wac.getBean("mcToolContentHandler");
	}
	return toolContentHandler;
    }

    /**
     * 
     * Go through the attachments collections. Remove any content repository or tool objects matching entries in the the
     * deletedAttachments collection, add any new attachments in the attachments collection. Clear the
     * deletedAttachments collection, ready for new editing.
     * 
     * @param mcContent
     * @param attachmentList
     * @param deletedAttachmentList
     * @param mapping
     * @param request
     * @return
     */
    private List saveAttachments(McContent mcContent, List attachmentList, List deletedAttachmentList,
	    ActionMapping mapping, HttpServletRequest request) {

	if ((attachmentList == null) || (deletedAttachmentList == null)) {
	    return null;
	}

	IMcService voteService = McServiceProxy.getMcService(getServlet().getServletContext());

	if (deletedAttachmentList != null) {
	    Iterator iter = deletedAttachmentList.iterator();
	    while (iter.hasNext()) {
		McUploadedFile attachment = (McUploadedFile) iter.next();

		if (attachment.getSubmissionId() != null) {
		    voteService.removeFile(attachment.getSubmissionId());
		}
	    }
	    deletedAttachmentList.clear();
	}

	if (attachmentList != null) {
	    Iterator iter = attachmentList.iterator();
	    while (iter.hasNext()) {
		McUploadedFile attachment = (McUploadedFile) iter.next();

		if (attachment.getSubmissionId() == null) {
		    /* add entry to tool table - file already in content repository */
		    voteService.persistFile(mcContent, attachment);
		}
	    }
	}

	return deletedAttachmentList;
    }

    public ActionForward editActivity(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	return null;
    }

    /**
     * 
     * generates Edit Activity screen
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
    public ActionForward editActivityQuestions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException, ToolException {

	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String totalMarks = request.getParameter("totalMarks");

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(mcContent.getTitle());
	mcAuthoringForm.setTitle(mcContent.getTitle());

	mcGeneralAuthoringDTO.setActivityInstructions(mcContent.getInstructions());

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, mcContent.getTitle());
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, mcContent.getInstructions());

	/* determine whether the request is from Monitoring url Edit Activity */
	String sourceMcStarter = (String) request.getAttribute(McAppConstants.SOURCE_MC_STARTER);

	mcAuthoringForm.setDefineLaterInEditMode(new Boolean(true).toString());
	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	boolean isContentInUse = McUtils.isContentInUse(mcContent);

	mcGeneralAuthoringDTO.setMonitoredContentInUse(new Boolean(false).toString());
	if (isContentInUse == true) {
	    // monitoring url does not allow editActivity since the content is in use
	    persistError(request, "error.content.inUse");
	    mcGeneralAuthoringDTO.setMonitoredContentInUse(new Boolean(true).toString());
	}

	EditActivityDTO editActivityDTO = new EditActivityDTO();
	if (isContentInUse == true) {
	    editActivityDTO.setMonitoredContentInUse(new Boolean(true).toString());
	}
	request.setAttribute(McAppConstants.EDIT_ACTIVITY_DTO, editActivityDTO);

	McUtils.setDefineLater(request, true, strToolContentID, mcService);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	AuthoringUtil authoringUtil = new AuthoringUtil();
	List listQuestionContentDTO = authoringUtil.buildDefaultQuestionContent(mcContent, mcService);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));
	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);
	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));
	request.getSession().setAttribute(httpSessionID, sessionMap);

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);
	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	return mapping.findForward(McAppConstants.LOAD_QUESTIONS);
    }

    /**
     * moves a candidate dwn in the list
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward moveCandidateDown(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	String candidateIndex = request.getParameter("candidateIndex");
	mcAuthoringForm.setCandidateIndex(candidateIndex);

	String totalMarks = request.getParameter("totalMarks");

	AuthoringUtil authoringUtil = new AuthoringUtil();

	boolean validateCandidateAnswersNotBlank = authoringUtil.validateCandidateAnswersNotBlank(request);

	ActionMessages errors = new ActionMessages();

	if (!validateCandidateAnswersNotBlank) {
	    ActionMessage error = new ActionMessage("candidates.blank");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!errors.isEmpty()) {
	    saveErrors(request, errors);
	}

	List caList = authoringUtil.repopulateCandidateAnswersBox(request, false);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	if (errors.isEmpty()) {
	    List candidates = new LinkedList();
	    List listCandidates = new LinkedList();
	    String editableQuestion = "";
	    Iterator listIterator = listQuestionContentDTO.iterator();
	    while (listIterator.hasNext()) {
		McQuestionContentDTO mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

		String question = mcQuestionContentDTO.getQuestion();
		String displayOrder = mcQuestionContentDTO.getDisplayOrder();

		if ((displayOrder != null) && (!displayOrder.equals(""))) {
		    if (displayOrder.equals(questionIndex)) {
			editableQuestion = mcQuestionContentDTO.getQuestion();

			candidates = mcQuestionContentDTO.getListCandidateAnswersDTO();
			// candidates found
			// but we are using the repopulated caList here

			listCandidates = AuthoringUtil.swapCandidateNodes(caList, candidateIndex, "down");

			mcQuestionContentDTO.setListCandidateAnswersDTO(listCandidates);

			break;
		    }

		}
	    }
	}

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	String newQuestion = request.getParameter("newQuestion");
	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	String feedback = request.getParameter("feedback");
	mcAuthoringForm.setFeedback(feedback);

	String mark = request.getParameter("mark");
	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	request.setAttribute("requestNewEditableQuestionBox", new Boolean(true).toString());
	return newEditableQuestionBox(mapping, form, request, response);
    }

    /**
     * moves a candidate up in the list
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward moveCandidateUp(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	String totalMarks = request.getParameter("totalMarks");

	String candidateIndex = request.getParameter("candidateIndex");
	mcAuthoringForm.setCandidateIndex(candidateIndex);

	AuthoringUtil authoringUtil = new AuthoringUtil();

	boolean validateCandidateAnswersNotBlank = authoringUtil.validateCandidateAnswersNotBlank(request);

	ActionMessages errors = new ActionMessages();

	if (!validateCandidateAnswersNotBlank) {
	    ActionMessage error = new ActionMessage("candidates.blank");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!errors.isEmpty()) {
	    saveErrors(request, errors);
	}

	List caList = authoringUtil.repopulateCandidateAnswersBox(request, false);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	if (errors.isEmpty()) {
	    List candidates = new LinkedList();
	    List listCandidates = new LinkedList();
	    String editableQuestion = "";
	    Iterator listIterator = listQuestionContentDTO.iterator();
	    while (listIterator.hasNext()) {
		McQuestionContentDTO mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

		String question = mcQuestionContentDTO.getQuestion();
		String displayOrder = mcQuestionContentDTO.getDisplayOrder();

		if ((displayOrder != null) && (!displayOrder.equals(""))) {
		    if (displayOrder.equals(questionIndex)) {
			editableQuestion = mcQuestionContentDTO.getQuestion();

			candidates = mcQuestionContentDTO.getListCandidateAnswersDTO();

			listCandidates = AuthoringUtil.swapCandidateNodes(caList, candidateIndex, "up");

			mcQuestionContentDTO.setListCandidateAnswersDTO(listCandidates);
			mcQuestionContentDTO.setCaCount(new Integer(listCandidates.size()).toString());

			break;
		    }

		}
	    }
	}

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();
	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	String newQuestion = request.getParameter("newQuestion");
	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	String feedback = request.getParameter("feedback");
	mcAuthoringForm.setFeedback(feedback);

	String mark = request.getParameter("mark");
	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	request.setAttribute("requestNewEditableQuestionBox", new Boolean(true).toString());
	return newEditableQuestionBox(mapping, form, request, response);
    }

    /**
     * removes a candidate from the list
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward removeCandidate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	String candidateIndex = request.getParameter("candidateIndex");
	mcAuthoringForm.setCandidateIndex(candidateIndex);

	String totalMarks = request.getParameter("totalMarks");

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	AuthoringUtil authoringUtil = new AuthoringUtil();
	List caList = authoringUtil.repopulateCandidateAnswersBox(request, false);

	McQuestionContentDTO mcQuestionContentDTO = null;
	Iterator listIterator = listQuestionContentDTO.iterator();
	while (listIterator.hasNext()) {
	    mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

	    String question = mcQuestionContentDTO.getQuestion();
	    String displayOrder = mcQuestionContentDTO.getDisplayOrder();

	    if ((displayOrder != null) && (!displayOrder.equals(""))) {
		if (displayOrder.equals(questionIndex)) {
		    break;
		}

	    }
	}

	mcQuestionContentDTO.setListCandidateAnswersDTO(caList);

	List candidateAnswers = mcQuestionContentDTO.getListCandidateAnswersDTO();

	McCandidateAnswersDTO mcCandidateAnswersDTO = null;
	Iterator listCaIterator = candidateAnswers.iterator();
	int caIndex = 0;
	while (listCaIterator.hasNext()) {
	    caIndex++;
	    mcCandidateAnswersDTO = (McCandidateAnswersDTO) listCaIterator.next();

	    if (caIndex == new Integer(candidateIndex).intValue()) {
		mcCandidateAnswersDTO.setCandidateAnswer("");

		break;
	    }
	}

	candidateAnswers = AuthoringUtil.reorderListCandidatesDTO(candidateAnswers);

	mcQuestionContentDTO.setListCandidateAnswersDTO(candidateAnswers);
	mcQuestionContentDTO.setCaCount(new Integer(candidateAnswers.size()).toString());

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	if (mcContent == null) {
	    mcContent = mcService.retrieveMc(new Long(defaultContentIdStr));
	}

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	String newQuestion = request.getParameter("newQuestion");
	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	String feedback = request.getParameter("feedback");
	mcAuthoringForm.setFeedback(feedback);

	String mark = request.getParameter("mark");
	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	request.setAttribute("requestNewEditableQuestionBox", new Boolean(true).toString());
	return newEditableQuestionBox(mapping, form, request, response);
    }

    /**
     * enables adding a new candidate answer
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public ActionForward newCandidateBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	String candidateIndex = request.getParameter("candidateIndex");
	mcAuthoringForm.setCandidateIndex(candidateIndex);

	String totalMarks = request.getParameter("totalMarks");

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	AuthoringUtil authoringUtil = new AuthoringUtil();
	List caList = authoringUtil.repopulateCandidateAnswersBox(request, true);

	int caCount = caList.size();

	String newQuestion = request.getParameter("newQuestion");

	String mark = request.getParameter("mark");

	String passmark = request.getParameter("passmark");

	String feedback = request.getParameter("feedback");

	int currentQuestionCount = listQuestionContentDTO.size();

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	McQuestionContentDTO mcQuestionContentDTOLocal = null;
	Iterator listIterator = listQuestionContentDTO.iterator();
	while (listIterator.hasNext()) {
	    mcQuestionContentDTOLocal = (McQuestionContentDTO) listIterator.next();

	    String question = mcQuestionContentDTOLocal.getQuestion();
	    String displayOrder = mcQuestionContentDTOLocal.getDisplayOrder();

	    if ((displayOrder != null) && (!displayOrder.equals(""))) {
		if (displayOrder.equals(questionIndex)) {
		    break;
		}

	    }
	}

	if (mcQuestionContentDTOLocal != null) {
	    mcQuestionContentDTOLocal.setListCandidateAnswersDTO(caList);
	    mcQuestionContentDTOLocal.setCaCount(new Integer(caList.size()).toString());
	}

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);
	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	mcAuthoringForm.setFeedback(feedback);

	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	request.setAttribute("requestNewEditableQuestionBox", new Boolean(true).toString());
	return newEditableQuestionBox(mapping, form, request, response);
    }

    public ActionForward updateMarksList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");
	mcAuthoringForm.setQuestionIndex(questionIndex);

	String totalMarks = request.getParameter("totalMarks");

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	AuthoringUtil authoringUtil = new AuthoringUtil();

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("2");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	return (mapping.findForward(McAppConstants.LOAD_QUESTIONS));
    }

    public ActionForward moveAddedCandidateUp(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String totalMarks = request.getParameter("totalMarks");

	String candidateIndex = request.getParameter("candidateIndex");
	mcAuthoringForm.setCandidateIndex(candidateIndex);

	AuthoringUtil authoringUtil = new AuthoringUtil();

	boolean validateCandidateAnswersNotBlank = authoringUtil.validateCandidateAnswersNotBlank(request);

	ActionMessages errors = new ActionMessages();

	if (!validateCandidateAnswersNotBlank) {
	    ActionMessage error = new ActionMessage("candidates.blank");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!errors.isEmpty()) {
	    saveErrors(request, errors);
	}

	List caList = authoringUtil.repopulateCandidateAnswersBox(request, false);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	List listAddableQuestionContentDTO = (List) sessionMap.get(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY);

	if (errors.isEmpty()) {
	    List candidates = new LinkedList();
	    List listCandidates = new LinkedList();

	    Iterator listIterator = listAddableQuestionContentDTO.iterator();
	    /* there is only 1 question dto */
	    while (listIterator.hasNext()) {
		McQuestionContentDTO mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

		candidates = mcQuestionContentDTO.getListCandidateAnswersDTO();

		listCandidates = AuthoringUtil.swapCandidateNodes(caList, candidateIndex, "up");

		mcQuestionContentDTO.setListCandidateAnswersDTO(listCandidates);
	    }
	}

	request.setAttribute(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_LIST, listAddableQuestionContentDTO);
	sessionMap.put(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY, listAddableQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	String newQuestion = request.getParameter("newQuestion");
	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	String feedback = request.getParameter("feedback");
	mcAuthoringForm.setFeedback(feedback);

	String mark = request.getParameter("mark");
	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	return newQuestionBox(mapping, form, request, response);

    }

    public ActionForward moveAddedCandidateDown(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String candidateIndex = request.getParameter("candidateIndex");
	mcAuthoringForm.setCandidateIndex(candidateIndex);

	String totalMarks = request.getParameter("totalMarks");

	AuthoringUtil authoringUtil = new AuthoringUtil();

	boolean validateCandidateAnswersNotBlank = authoringUtil.validateCandidateAnswersNotBlank(request);

	ActionMessages errors = new ActionMessages();

	if (!validateCandidateAnswersNotBlank) {
	    ActionMessage error = new ActionMessage("candidates.blank");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	if (!errors.isEmpty()) {
	    saveErrors(request, errors);
	}

	List caList = authoringUtil.repopulateCandidateAnswersBox(request, false);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);
	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	List listAddableQuestionContentDTO = (List) sessionMap.get(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY);

	if (errors.isEmpty()) {
	    List candidates = new LinkedList();
	    List listCandidates = new LinkedList();

	    Iterator listIterator = listAddableQuestionContentDTO.iterator();
	    /* there is only 1 question dto */
	    while (listIterator.hasNext()) {
		McQuestionContentDTO mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

		candidates = mcQuestionContentDTO.getListCandidateAnswersDTO();

		listCandidates = AuthoringUtil.swapCandidateNodes(caList, candidateIndex, "down");

		mcQuestionContentDTO.setListCandidateAnswersDTO(listCandidates);
	    }
	}

	request.setAttribute(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_LIST, listAddableQuestionContentDTO);
	sessionMap.put(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY, listAddableQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	String newQuestion = request.getParameter("newQuestion");
	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	String feedback = request.getParameter("feedback");
	mcAuthoringForm.setFeedback(feedback);

	String mark = request.getParameter("mark");
	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	return newQuestionBox(mapping, form, request, response);
    }

    public ActionForward removeAddedCandidate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String candidateIndex = request.getParameter("candidateIndex");
	mcAuthoringForm.setCandidateIndex(candidateIndex);

	String totalMarks = request.getParameter("totalMarks");

	AuthoringUtil authoringUtil = new AuthoringUtil();
	List caList = authoringUtil.repopulateCandidateAnswersBox(request, false);

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);
	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	List listAddableQuestionContentDTO = (List) sessionMap.get(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY);

	List candidates = new LinkedList();
	List listCandidates = new LinkedList();

	Iterator listIterator = listAddableQuestionContentDTO.iterator();
	/* there is only 1 question dto */
	while (listIterator.hasNext()) {
	    McQuestionContentDTO mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

	    candidates = mcQuestionContentDTO.getListCandidateAnswersDTO();
	    mcQuestionContentDTO.setListCandidateAnswersDTO(caList);

	    List candidateAnswers = mcQuestionContentDTO.getListCandidateAnswersDTO();

	    McCandidateAnswersDTO mcCandidateAnswersDTO = null;
	    Iterator listCaIterator = candidateAnswers.iterator();
	    int caIndex = 0;
	    while (listCaIterator.hasNext()) {
		caIndex++;
		mcCandidateAnswersDTO = (McCandidateAnswersDTO) listCaIterator.next();

		if (caIndex == new Integer(candidateIndex).intValue()) {
		    mcCandidateAnswersDTO.setCandidateAnswer("");

		    break;
		}
	    }

	    candidateAnswers = AuthoringUtil.reorderListCandidatesDTO(candidateAnswers);

	    mcQuestionContentDTO.setListCandidateAnswersDTO(candidateAnswers);
	    mcQuestionContentDTO.setCaCount(new Integer(candidateAnswers.size()).toString());
	}

	request.setAttribute(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_LIST, listAddableQuestionContentDTO);
	sessionMap.put(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY, listAddableQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McContent mcContent = mcService.retrieveMc(new Long(strToolContentID));

	if (mcContent == null) {
	    mcContent = mcService.retrieveMc(new Long(defaultContentIdStr));
	}

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	String newQuestion = request.getParameter("newQuestion");
	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	String feedback = request.getParameter("feedback");
	mcAuthoringForm.setFeedback(feedback);

	String mark = request.getParameter("mark");
	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	return newQuestionBox(mapping, form, request, response);

    }

    public ActionForward newAddedCandidateBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	McAuthoringForm mcAuthoringForm = (McAuthoringForm) form;

	IMcService mcService = McServiceProxy.getMcService(getServlet().getServletContext());

	String httpSessionID = mcAuthoringForm.getHttpSessionID();

	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(httpSessionID);

	String candidateIndex = request.getParameter("candidateIndex");
	mcAuthoringForm.setCandidateIndex(candidateIndex);

	String totalMarks = request.getParameter("totalMarks");

	List listQuestionContentDTO = (List) sessionMap.get(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY);

	AuthoringUtil authoringUtil = new AuthoringUtil();
	List caList = authoringUtil.repopulateCandidateAnswersBox(request, true);

	int caCount = caList.size();

	String newQuestion = request.getParameter("newQuestion");

	String mark = request.getParameter("mark");

	String passmark = request.getParameter("passmark");

	String feedback = request.getParameter("feedback");

	int currentQuestionCount = listQuestionContentDTO.size();

	String editQuestionBoxRequest = request.getParameter("editQuestionBoxRequest");

	List listAddableQuestionContentDTO = (List) sessionMap.get(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY);

	List candidates = new LinkedList();
	List listCandidates = new LinkedList();

	Iterator listIterator = listAddableQuestionContentDTO.iterator();
	/* there is only 1 question dto */
	while (listIterator.hasNext()) {
	    McQuestionContentDTO mcQuestionContentDTO = (McQuestionContentDTO) listIterator.next();

	    mcQuestionContentDTO.setListCandidateAnswersDTO(caList);
	    mcQuestionContentDTO.setCaCount(new Integer(caList.size()).toString());
	}

	request.setAttribute(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_LIST, listAddableQuestionContentDTO);
	sessionMap.put(McAppConstants.NEW_ADDABLE_QUESTION_CONTENT_KEY, listAddableQuestionContentDTO);

	sessionMap.put(McAppConstants.LIST_QUESTION_CONTENT_DTO_KEY, listQuestionContentDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	mcAuthoringForm.setContentFolderID(contentFolderID);

	String activeModule = request.getParameter(McAppConstants.ACTIVE_MODULE);

	String richTextTitle = request.getParameter(McAppConstants.TITLE);

	String richTextInstructions = request.getParameter(McAppConstants.INSTRUCTIONS);

	sessionMap.put(McAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(McAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String defaultContentIdStr = new Long(mcService.getToolDefaultContentIdBySignature(McAppConstants.MY_SIGNATURE))
		.toString();

	McGeneralAuthoringDTO mcGeneralAuthoringDTO = new McGeneralAuthoringDTO();
	mcGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	mcGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	mcAuthoringForm.setTitle(richTextTitle);

	mcGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	if (activeModule.equals(McAppConstants.AUTHORING)) {
	    String onlineInstructions = (String) sessionMap.get(McAppConstants.ONLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOnlineInstructions(onlineInstructions);

	    String offlineInstructions = (String) sessionMap.get(McAppConstants.OFFLINE_INSTRUCTIONS_KEY);
	    mcGeneralAuthoringDTO.setOfflineInstructions(offlineInstructions);

	    List attachmentList = (List) sessionMap.get(McAppConstants.ATTACHMENT_LIST_KEY);
	    List deletedAttachmentList = (List) sessionMap.get(McAppConstants.DELETED_ATTACHMENT_LIST_KEY);

	    mcGeneralAuthoringDTO.setAttachmentList(attachmentList);
	    mcGeneralAuthoringDTO.setDeletedAttachmentList(deletedAttachmentList);

	    String strOnlineInstructions = request.getParameter("onlineInstructions");
	    String strOfflineInstructions = request.getParameter("offlineInstructions");
	    mcAuthoringForm.setOnlineInstructions(strOnlineInstructions);
	    mcAuthoringForm.setOfflineInstructions(strOfflineInstructions);
	}

	mcGeneralAuthoringDTO.setEditActivityEditMode(new Boolean(true).toString());

	request.getSession().setAttribute(httpSessionID, sessionMap);

	McUtils.setFormProperties(request, mcService, mcAuthoringForm, mcGeneralAuthoringDTO, strToolContentID,
		defaultContentIdStr, activeModule, sessionMap, httpSessionID);

	mcGeneralAuthoringDTO.setToolContentID(strToolContentID);
	mcGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	mcGeneralAuthoringDTO.setActiveModule(activeModule);
	mcGeneralAuthoringDTO.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setToolContentID(strToolContentID);
	mcAuthoringForm.setHttpSessionID(httpSessionID);
	mcAuthoringForm.setActiveModule(activeModule);
	mcAuthoringForm.setDefaultContentIdStr(defaultContentIdStr);
	mcAuthoringForm.setCurrentTab("1");

	request.setAttribute(McAppConstants.LIST_QUESTION_CONTENT_DTO, listQuestionContentDTO);

	mcGeneralAuthoringDTO.setDefineLaterInEditMode(new Boolean(true).toString());

	Map marksMap = authoringUtil.buildMarksMap();
	mcGeneralAuthoringDTO.setMarksMap(marksMap);

	Map passMarksMap = authoringUtil.buildDynamicPassMarkMap(listQuestionContentDTO, false);
	mcGeneralAuthoringDTO.setPassMarksMap(passMarksMap);

	String totalMark = AuthoringUtil.getTotalMark(listQuestionContentDTO);
	mcAuthoringForm.setTotalMarks(totalMark);
	mcGeneralAuthoringDTO.setTotalMarks(totalMark);

	Map correctMap = authoringUtil.buildCorrectMap();
	mcGeneralAuthoringDTO.setCorrectMap(correctMap);

	mcGeneralAuthoringDTO.setEditableQuestionText(newQuestion);

	mcAuthoringForm.setFeedback(feedback);

	mcGeneralAuthoringDTO.setMarkValue(mark);

	request.setAttribute(McAppConstants.MC_GENERAL_AUTHORING_DTO, mcGeneralAuthoringDTO);

	request.setAttribute(McAppConstants.TOTAL_QUESTION_COUNT, new Integer(listQuestionContentDTO.size()));

	return newQuestionBox(mapping, form, request, response);

    }

    protected boolean existsContent(long toolContentID, IMcService mcService) {
	McContent mcContent = mcService.retrieveMc(new Long(toolContentID));
	if (mcContent == null) {
	    return false;
	}

	return true;
    }

}
