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

/* $Id$ */
package org.lamsfoundation.lams.tool.assessment.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.JXLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.lamsfoundation.lams.tool.assessment.AssessmentConstants;
import org.lamsfoundation.lams.tool.assessment.dto.ExcelCell;
import org.lamsfoundation.lams.tool.assessment.dto.QuestionSummary;
import org.lamsfoundation.lams.tool.assessment.dto.Summary;
import org.lamsfoundation.lams.tool.assessment.dto.UserSummary;
import org.lamsfoundation.lams.tool.assessment.model.Assessment;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentOptionAnswer;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestion;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestionOption;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestionResult;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentResult;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentSession;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentUser;
import org.lamsfoundation.lams.tool.assessment.service.IAssessmentService;
import org.lamsfoundation.lams.tool.assessment.util.AssessmentExportXLSUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MonitoringAction extends Action {
    public static Logger log = Logger.getLogger(MonitoringAction.class);

    public static final ExcelCell[] EMPTY_ROW = new ExcelCell[0];

    private IAssessmentService service;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	request.setAttribute("initialTabId", WebUtil.readLongParam(request, AttributeNames.PARAM_CURRENT_TAB, true));

	String param = mapping.getParameter();
	if (param.equals("summary")) {
	    return summary(mapping, form, request, response);
	}
	if (param.equals("userMasterDetail")) {
	    return userMasterDetail(mapping, form, request, response);
	}
	if (param.equals("questionSummary")) {
	    return questionSummary(mapping, form, request, response);
	}
	if (param.equals("userSummary")) {
	    return userSummary(mapping, form, request, response);
	}
	if (param.equals("saveUserGrade")) {
	    return saveUserGrade(mapping, form, request, response);
	}
	if (param.equals("exportSummary")) {
	    return exportSummary(mapping, form, request, response);
	}

	return mapping.findForward(AssessmentConstants.ERROR);
    }

    private ActionForward summary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	// initialize Session Map
	SessionMap sessionMap = new SessionMap();
	request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
	request.setAttribute(AssessmentConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());

	Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	service = getAssessmentService();
	List<Summary> summaryList = service.getSummaryList(contentId);

	Assessment assessment = service.getAssessmentByContentId(contentId);
	assessment.toDTO();

	// cache into sessionMap
	sessionMap.put(AssessmentConstants.ATTR_SUMMARY_LIST, summaryList);
	sessionMap.put(AssessmentConstants.PAGE_EDITABLE, assessment.isContentInUse());
	sessionMap.put(AssessmentConstants.ATTR_ASSESSMENT, assessment);
	sessionMap.put(AssessmentConstants.ATTR_TOOL_CONTENT_ID, contentId);
	sessionMap.put(AttributeNames.PARAM_CONTENT_FOLDER_ID, WebUtil.readStrParam(request,
		AttributeNames.PARAM_CONTENT_FOLDER_ID));
	return mapping.findForward(AssessmentConstants.SUCCESS);
    }

    private ActionForward userMasterDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	Long userId = WebUtil.readLongParam(request, AttributeNames.PARAM_USER_ID);
	Long sessionId = WebUtil.readLongParam(request, AssessmentConstants.PARAM_SESSION_ID);
	service = getAssessmentService();
	AssessmentResult result = service.getUserMasterDetail(sessionId, userId);

	request.setAttribute(AssessmentConstants.ATTR_ASSESSMENT_RESULT, result);
	return (result == null) ? null : mapping.findForward(AssessmentConstants.SUCCESS);
    }

    private ActionForward questionSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	String sessionMapID = request.getParameter(AssessmentConstants.ATTR_SESSION_MAP_ID);
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
	request.setAttribute(AssessmentConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());

	Long questionUid = WebUtil.readLongParam(request, AssessmentConstants.PARAM_QUESTION_UID);
	if (questionUid.equals(-1)) {
	    return null;
	}
	Long contentId = (Long) sessionMap.get(AssessmentConstants.ATTR_TOOL_CONTENT_ID);
	service = getAssessmentService();
	QuestionSummary questionSummary = service.getQuestionSummary(contentId, questionUid);

	request.setAttribute(AssessmentConstants.ATTR_QUESTION_SUMMARY, questionSummary);
	return mapping.findForward(AssessmentConstants.SUCCESS);
    }

    private ActionForward userSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	String sessionMapID = request.getParameter(AssessmentConstants.ATTR_SESSION_MAP_ID);
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
	request.setAttribute(AssessmentConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());

	Long userId = WebUtil.readLongParam(request, AttributeNames.PARAM_USER_ID);
	Long sessionId = WebUtil.readLongParam(request, AssessmentConstants.PARAM_SESSION_ID);
	Long contentId = (Long) sessionMap.get(AssessmentConstants.ATTR_TOOL_CONTENT_ID);
	service = getAssessmentService();
	UserSummary userSummary = service.getUserSummary(contentId, userId, sessionId);

	request.setAttribute(AssessmentConstants.ATTR_USER_SUMMARY, userSummary);
	return mapping.findForward(AssessmentConstants.SUCCESS);
    }

    private ActionForward saveUserGrade(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	if ((request.getParameter(AssessmentConstants.PARAM_NOT_A_NUMBER) == null)
		&& !StringUtils.isEmpty(request.getParameter(AssessmentConstants.PARAM_QUESTION_RESULT_UID))) {
	    Long questionResultUid = WebUtil.readLongParam(request, AssessmentConstants.PARAM_QUESTION_RESULT_UID);
	    float newGrade = Float.valueOf(request.getParameter(AssessmentConstants.PARAM_GRADE));
	    service = getAssessmentService();
	    service.changeQuestionResultMark(questionResultUid, newGrade);
	}

	return null;
    }

    /**
     * Export Excel format survey data.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    private ActionForward exportSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	String sessionMapID = request.getParameter(AssessmentConstants.ATTR_SESSION_MAP_ID);
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
	String fileName = null;
	boolean showUserNames = true;

	Long contentId = null;
	if (sessionMap != null) {
	    request.setAttribute(AssessmentConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
	    contentId = (Long) sessionMap.get(AssessmentConstants.ATTR_TOOL_CONTENT_ID);
	    showUserNames = true;
	} else {
	    contentId = WebUtil.readLongParam(request, "toolContentID");
	    fileName = WebUtil.readStrParam(request, "fileName");
	    showUserNames = false;
	}

	service = getAssessmentService();
	Assessment assessment = service.getAssessmentByContentId(contentId);

	String errors = null;
	if (assessment != null) {
	    try {
		LinkedHashMap<String, ExcelCell[][]> dataToExport = new LinkedHashMap<String, ExcelCell[][]>();

		ExcelCell[][] questionSummaryData = getQuestionSummaryData(assessment, showUserNames);
		dataToExport.put(service.getMessage("lable.export.summary.by.question"), questionSummaryData);

		ExcelCell[][] userSummaryData = getUserSummaryData(assessment, showUserNames);
		dataToExport.put(service.getMessage("label.export.summary.by.user"), userSummaryData);

		// Setting the filename if it wasn't passed in the request
		if (fileName == null && assessment.getTitle() != null) {
		    fileName = assessment.getTitle().replaceAll(" ", "_") + "_export.xls";
		} else if (fileName == null) {
		    fileName = "assessment_export.xls";
		}

		response.setContentType("application/x-download");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		log.debug("Exporting assessment to a spreadsheet: " + assessment.getContentId());
		ServletOutputStream out = response.getOutputStream();

		AssessmentExportXLSUtil.exportAssessmentToExcel(out, service.getMessage("label.export.exported.on"),
			dataToExport);

	    } catch (IOException e) {
		MonitoringAction.log.error(e);
		errors = new ActionMessage("error.monitoring.export.excel", e.toString()).toString();
	    } catch (JXLException e) {
		MonitoringAction.log.error(e);
		errors = new ActionMessage("error.monitoring.export.excel", e.toString()).toString();
	    }

	}
	if (errors != null) {
	    try {
		PrintWriter out = response.getWriter();
		out.write(errors);
		out.flush();
	    } catch (IOException e) {
	    }
	}
	return null;
    }

    @SuppressWarnings("unchecked")
    private ExcelCell[][] getUserSummaryData(Assessment assessment, boolean showUserNames) {
	ArrayList<ExcelCell[]> data = new ArrayList<ExcelCell[]>();
	service = getAssessmentService();

	if (assessment != null) {
	    // Create the question summary
	    ExcelCell[] summaryTitle = new ExcelCell[1];
	    summaryTitle[0] = new ExcelCell(service.getMessage("label.export.user.summary"), true);
	    data.add(summaryTitle);

	    // Adding the question summary -------------------------------------
	    ExcelCell[] summaryRowTitle = new ExcelCell[5];
	    summaryRowTitle[0] = new ExcelCell(service.getMessage("label.monitoring.question.summary.question"), true);
	    summaryRowTitle[1] = new ExcelCell(service.getMessage("label.authoring.basic.list.header.type"), true);
	    summaryRowTitle[2] = new ExcelCell(service.getMessage("label.authoring.basic.penalty.factor"), true);
	    summaryRowTitle[3] = new ExcelCell(service.getMessage("label.monitoring.question.summary.default.mark"),
		    true);
	    summaryRowTitle[4] = new ExcelCell(service.getMessage("label.monitoring.question.summary.average.mark"),
		    true);
	    data.add(summaryRowTitle);
	    Float totalGradesPossible = new Float(0);
	    Float totalAverage = new Float(0);
	    if (assessment.getQuestions() != null) {
		Set<AssessmentQuestion> questions = (Set<AssessmentQuestion>) assessment.getQuestions();

		for (AssessmentQuestion question : questions) {

		    QuestionSummary questionSummary = service.getQuestionSummary(assessment.getContentId(), question
			    .getUid());

		    ExcelCell[] questCell = new ExcelCell[5];
		    questCell[0] = new ExcelCell(question.getTitle(), false);
		    questCell[1] = new ExcelCell(getQuestionTypeLanguageLabel(question.getType()), false);
		    questCell[2] = new ExcelCell(question.getPenaltyFactor(), false);
		    questCell[3] = new ExcelCell(new Long(question.getDefaultGrade()), false);
		    totalGradesPossible += question.getDefaultGrade();
		    if (questionSummary != null) {
			questCell[4] = new ExcelCell(questionSummary.getAverageMark(), false);
			totalAverage += questionSummary.getAverageMark();
		    }
		    data.add(questCell);

		}
		if (totalGradesPossible.floatValue() > 0) {
		    ExcelCell[] totalCell = new ExcelCell[5];
		    totalCell[2] = new ExcelCell(service.getMessage("label.monitoring.summary.total"), true);
		    totalCell[3] = new ExcelCell(totalGradesPossible, false);
		    totalCell[4] = new ExcelCell(totalAverage, false);
		    data.add(totalCell);
		}
		data.add(EMPTY_ROW);
	    }
	    //------------------------------------------------------------------

	    // Adding the user results/marks summary ---------------------------

	    List<Summary> summaryList = service.getSummaryList(assessment.getContentId());
	    if (summaryList != null) {
		for (Summary summary : summaryList) {

		    data.add(EMPTY_ROW);

		    ExcelCell[] sessionTitle = new ExcelCell[1];
		    sessionTitle[0] = new ExcelCell(summary.getSessionName(), true);
		    data.add(sessionTitle);

		    //List<AssessmentResult> assessmentResults = summary.getAssessmentResults();

		    AssessmentSession assessmentSession = service.getAssessmentSessionBySessionId(summary
			    .getSessionId());

		    Set<AssessmentUser> assessmentUsers = assessmentSession.getAssessmentUsers();

		    if (assessmentUsers != null) {

			for (AssessmentUser assessmentUser : assessmentUsers) {

			    if (showUserNames) {
				ExcelCell[] userTitleRow = new ExcelCell[6];
				userTitleRow[0] = new ExcelCell(service.getMessage("label.export.user.id"), true);
				userTitleRow[1] = new ExcelCell(service
					.getMessage("label.monitoring.user.summary.user.name"), true);
				userTitleRow[2] = new ExcelCell(service.getMessage("label.export.date.attempted"), true);
				userTitleRow[3] = new ExcelCell(service
					.getMessage("label.monitoring.question.summary.question"), true);
				userTitleRow[4] = new ExcelCell(service
					.getMessage("label.authoring.basic.option.answer"), true);
				userTitleRow[5] = new ExcelCell(service.getMessage("label.export.mark"), true);
				data.add(userTitleRow);
			    } else {
				ExcelCell[] userTitleRow = new ExcelCell[5];
				userTitleRow[0] = new ExcelCell(service.getMessage("label.export.user.id"), true);
				userTitleRow[1] = new ExcelCell(service.getMessage("label.export.date.attempted"), true);
				userTitleRow[2] = new ExcelCell(service
					.getMessage("label.monitoring.question.summary.question"), true);
				userTitleRow[3] = new ExcelCell(service
					.getMessage("label.authoring.basic.option.answer"), true);
				userTitleRow[4] = new ExcelCell(service.getMessage("label.export.mark"), true);
				data.add(userTitleRow);
			    }

			    AssessmentResult assessmentResult = service.getLastAssessmentResult(assessment.getUid(),
				    assessmentUser.getUserId());

			    if (assessmentResult != null) {
				Set<AssessmentQuestionResult> assessmentQuestionResults = assessmentResult
					.getQuestionResults();

				if (assessmentQuestionResults != null) {

				    for (AssessmentQuestionResult assessmentQuestionResult : assessmentQuestionResults) {

					if (showUserNames) {
					    ExcelCell[] userResultRow = new ExcelCell[6];
					    userResultRow[0] = new ExcelCell(assessmentUser.getUserId(), false);
					    userResultRow[1] = new ExcelCell(assessmentUser.getFullName(), false);
					    userResultRow[2] = new ExcelCell(assessmentResult.getStartDate(), false);
					    userResultRow[3] = new ExcelCell(assessmentQuestionResult
						    .getAssessmentQuestion().getTitle(), false);
					    userResultRow[4] = new ExcelCell(getAnswerObject(assessmentQuestionResult),
						    false);
					    userResultRow[5] = new ExcelCell(assessmentQuestionResult.getMark(), false);
					    data.add(userResultRow);
					} else {
					    ExcelCell[] userResultRow = new ExcelCell[5];
					    userResultRow[0] = new ExcelCell(assessmentUser.getUserId(), false);
					    userResultRow[1] = new ExcelCell(assessmentResult.getStartDate(), false);
					    userResultRow[2] = new ExcelCell(assessmentQuestionResult
						    .getAssessmentQuestion().getTitle(), false);
					    userResultRow[3] = new ExcelCell(getAnswerObject(assessmentQuestionResult),
						    false);
					    userResultRow[4] = new ExcelCell(assessmentQuestionResult.getMark(), false);
					    data.add(userResultRow);
					}
				    }
				}

				ExcelCell[] userTotalRow;
				if (showUserNames) {
				    userTotalRow = new ExcelCell[6];
				    userTotalRow[4] = new ExcelCell(service
					    .getMessage("label.monitoring.summary.total"), true);
				    userTotalRow[5] = new ExcelCell(assessmentResult.getGrade(), false);
				} else {
				    userTotalRow = new ExcelCell[5];
				    userTotalRow[3] = new ExcelCell(service
					    .getMessage("label.monitoring.summary.total"), true);
				    userTotalRow[4] = new ExcelCell(assessmentResult.getGrade(), false);
				}

				data.add(userTotalRow);
				data.add(EMPTY_ROW);
			    }
			}
		    }
		}
	    }

	    //------------------------------------------------------------------

	}

	return data.toArray(new ExcelCell[][] {});
    }

    @SuppressWarnings("unchecked")
    private ExcelCell[][] getQuestionSummaryData(Assessment assessment, boolean showUserNames) {
	ArrayList<ExcelCell[]> data = new ArrayList<ExcelCell[]>();
	service = getAssessmentService();

	if (assessment != null) {
	    // Create the question summary
	    ExcelCell[] summaryTitle = new ExcelCell[1];
	    summaryTitle[0] = new ExcelCell(service.getMessage("label.export.question.summary"), true);
	    data.add(summaryTitle);

	    if (assessment.getQuestions() != null) {
		Set<AssessmentQuestion> questions = (Set<AssessmentQuestion>) assessment.getQuestions();

		for (AssessmentQuestion question : questions) {

		    // Adding the question summary 
		    if (showUserNames) {
			ExcelCell[] summaryRowTitle = new ExcelCell[10];
			summaryRowTitle[0] = new ExcelCell(service
				.getMessage("label.monitoring.question.summary.question"), true);
			summaryRowTitle[1] = new ExcelCell(
				service.getMessage("label.authoring.basic.list.header.type"), true);
			summaryRowTitle[2] = new ExcelCell(service.getMessage("label.authoring.basic.penalty.factor"),
				true);
			summaryRowTitle[3] = new ExcelCell(service
				.getMessage("label.monitoring.question.summary.default.mark"), true);
			summaryRowTitle[4] = new ExcelCell(service.getMessage("label.export.user.id"), true);
			summaryRowTitle[5] = new ExcelCell(service
				.getMessage("label.monitoring.user.summary.user.name"), true);
			summaryRowTitle[6] = new ExcelCell(service.getMessage("label.export.date.attempted"), true);
			summaryRowTitle[7] = new ExcelCell(service.getMessage("label.authoring.basic.option.answer"),
				true);
			summaryRowTitle[8] = new ExcelCell(service.getMessage("label.export.time.taken"), true);
			summaryRowTitle[9] = new ExcelCell(service.getMessage("label.export.mark"), true);
			data.add(summaryRowTitle);
		    } else {
			ExcelCell[] summaryRowTitle = new ExcelCell[9];
			summaryRowTitle[0] = new ExcelCell(service
				.getMessage("label.monitoring.question.summary.question"), true);
			summaryRowTitle[1] = new ExcelCell(
				service.getMessage("label.authoring.basic.list.header.type"), true);
			summaryRowTitle[2] = new ExcelCell(service.getMessage("label.authoring.basic.penalty.factor"),
				true);
			summaryRowTitle[3] = new ExcelCell(service
				.getMessage("label.monitoring.question.summary.default.mark"), true);
			summaryRowTitle[4] = new ExcelCell(service.getMessage("label.export.user.id"), true);
			summaryRowTitle[5] = new ExcelCell(service.getMessage("label.export.date.attempted"), true);
			summaryRowTitle[6] = new ExcelCell(service.getMessage("label.authoring.basic.option.answer"),
				true);
			summaryRowTitle[7] = new ExcelCell(service.getMessage("label.export.time.taken"), true);
			summaryRowTitle[8] = new ExcelCell(service.getMessage("label.export.mark"), true);
			data.add(summaryRowTitle);
		    }

		    QuestionSummary questionSummary = service.getQuestionSummary(assessment.getContentId(), question
			    .getUid());

		    List<List<AssessmentQuestionResult>> allResultsForQuestion = questionSummary
			    .getQuestionResultsPerSession();

		    int markCount = 0;
		    Float markTotal = new Float(0.0);
		    int timeTakenCount = 0;
		    int timeTakenTotal = 0;
		    for (List<AssessmentQuestionResult> resultList : allResultsForQuestion) {
			for (AssessmentQuestionResult assessmentQuestionResult : resultList) {

			    if (showUserNames) {
				ExcelCell[] userResultRow = new ExcelCell[10];
				userResultRow[0] = new ExcelCell(assessmentQuestionResult.getAssessmentQuestion()
					.getTitle(), false);
				userResultRow[1] = new ExcelCell(getQuestionTypeLanguageLabel(assessmentQuestionResult
					.getAssessmentQuestion().getType()), false);
				userResultRow[2] = new ExcelCell(new Float(assessmentQuestionResult
					.getAssessmentQuestion().getPenaltyFactor()), false);
				userResultRow[3] = new ExcelCell(new Long(assessmentQuestionResult
					.getAssessmentQuestion().getDefaultGrade()), false);
				userResultRow[4] = new ExcelCell(assessmentQuestionResult.getUser().getUserId(), false);
				userResultRow[5] = new ExcelCell(assessmentQuestionResult.getUser().getFullName(),
					false);
				userResultRow[6] = new ExcelCell(assessmentQuestionResult.getFinishDate(), false);
				userResultRow[7] = new ExcelCell(getAnswerObject(assessmentQuestionResult), false);

				AssessmentResult assessmentResult = assessmentQuestionResult.getAssessmentResult();
				Date finishDate = assessmentQuestionResult.getFinishDate();
				if (assessmentResult != null && finishDate != null) {
				    Date startDate = assessmentResult.getStartDate();
				    if (startDate != null) {
					Long seconds = (finishDate.getTime() - startDate.getTime()) / 1000;
					userResultRow[8] = new ExcelCell(seconds, false);
					timeTakenCount++;
					timeTakenTotal += seconds;
				    }
				}

				Float mark = assessmentQuestionResult.getMark();
				if (mark != null) {
				    userResultRow[9] = new ExcelCell(assessmentQuestionResult.getMark(), false);
				    markCount++;
				    markTotal += assessmentQuestionResult.getMark();
				}

				data.add(userResultRow);
			    } else {
				ExcelCell[] userResultRow = new ExcelCell[9];
				userResultRow[0] = new ExcelCell(assessmentQuestionResult.getAssessmentQuestion()
					.getTitle(), false);
				userResultRow[1] = new ExcelCell(getQuestionTypeLanguageLabel(assessmentQuestionResult
					.getAssessmentQuestion().getType()), false);
				userResultRow[2] = new ExcelCell(new Float(assessmentQuestionResult
					.getAssessmentQuestion().getPenaltyFactor()), false);
				userResultRow[3] = new ExcelCell(new Long(assessmentQuestionResult
					.getAssessmentQuestion().getDefaultGrade()), false);
				userResultRow[4] = new ExcelCell(assessmentQuestionResult.getUser().getUserId(), false);
				userResultRow[5] = new ExcelCell(assessmentQuestionResult.getFinishDate(), false);
				userResultRow[6] = new ExcelCell(getAnswerObject(assessmentQuestionResult), false);

				if (assessmentQuestionResult.getAssessmentResult() != null) {
				    Date startDate = assessmentQuestionResult.getAssessmentResult().getStartDate();
				    Date finishDate = assessmentQuestionResult.getFinishDate();
				    if (startDate != null && finishDate != null) {
					Long seconds = (finishDate.getTime() - startDate.getTime()) / 1000;
					userResultRow[7] = new ExcelCell(seconds, false);
					timeTakenCount++;
					timeTakenTotal += seconds;
				    }
				}

				userResultRow[8] = new ExcelCell(assessmentQuestionResult.getMark(), false);

				if (assessmentQuestionResult.getMark() != null) {
				    markCount++;
				    markTotal += assessmentQuestionResult.getMark();
				}

				data.add(userResultRow);
			    }

			}
		    }

		    // Calculating the averages
		    ExcelCell[] averageRow;

		    if (showUserNames) {
			averageRow = new ExcelCell[10];

			averageRow[7] = new ExcelCell(service.getMessage("label.export.average"), true);

			if (timeTakenTotal > 0) {
			    averageRow[8] = new ExcelCell(new Long(timeTakenTotal / timeTakenCount), false);
			}
			if (markTotal > 0) {
			    Float averageMark = new Float(markTotal / markCount);
			    averageRow[9] = new ExcelCell(averageMark, false);
			} else {
			    averageRow[9] = new ExcelCell(new Float(0.0), false);
			}
		    } else {
			averageRow = new ExcelCell[9];
			averageRow[6] = new ExcelCell(service.getMessage("label.export.average"), true);

			if (timeTakenTotal > 0) {
			    averageRow[7] = new ExcelCell(new Long(timeTakenTotal / timeTakenCount), false);
			}
			if (markTotal > 0) {
			    Float averageMark = new Float(markTotal / markCount);
			    averageRow[8] = new ExcelCell(averageMark, false);
			} else {
			    averageRow[8] = new ExcelCell(new Float(0.0), false);
			}
		    }

		    data.add(averageRow);
		    data.add(EMPTY_ROW);
		}

	    }
	}

	return data.toArray(new ExcelCell[][] {});
    }

    private String getQuestionTypeLanguageLabel(short type) {
	switch (type) {
	case AssessmentConstants.QUESTION_TYPE_ESSAY:
	    return "Essay";
	case AssessmentConstants.QUESTION_TYPE_MATCHING_PAIRS:
	    return "Matching Pairs";
	case AssessmentConstants.QUESTION_TYPE_MULTIPLE_CHOICE:
	    return "Multiple Choice";
	case AssessmentConstants.QUESTION_TYPE_NUMERICAL:
	    return "Numerical";
	case AssessmentConstants.QUESTION_TYPE_ORDERING:
	    return "Ordering";
	case AssessmentConstants.QUESTION_TYPE_SHORT_ANSWER:
	    return "Short Answer";
	case AssessmentConstants.QUESTION_TYPE_TRUE_FALSE:
	    return "True/False";
	default:
	    return null;
	}
    }

    private Object getAnswerObject(AssessmentQuestionResult assessmentQuestionResult) {
	Object ret = null;

	if (assessmentQuestionResult != null) {
	    switch (assessmentQuestionResult.getAssessmentQuestion().getType()) {
	    case AssessmentConstants.QUESTION_TYPE_ESSAY:
		return removeHTMLTags(assessmentQuestionResult.getAnswerString());
	    case AssessmentConstants.QUESTION_TYPE_MATCHING_PAIRS:
		return getOptionResponse(assessmentQuestionResult, AssessmentConstants.QUESTION_TYPE_MATCHING_PAIRS);
	    case AssessmentConstants.QUESTION_TYPE_MULTIPLE_CHOICE:
		return getOptionResponse(assessmentQuestionResult, AssessmentConstants.QUESTION_TYPE_MULTIPLE_CHOICE);
	    case AssessmentConstants.QUESTION_TYPE_NUMERICAL:
		return assessmentQuestionResult.getAnswerString();
	    case AssessmentConstants.QUESTION_TYPE_ORDERING:
		return getOptionResponse(assessmentQuestionResult, AssessmentConstants.QUESTION_TYPE_ORDERING);
	    case AssessmentConstants.QUESTION_TYPE_SHORT_ANSWER:
		return assessmentQuestionResult.getAnswerString();
	    case AssessmentConstants.QUESTION_TYPE_TRUE_FALSE:
		return assessmentQuestionResult.getAnswerBoolean();
	    default:
		return null;
	    }
	}
	return ret;
    }

    public String getOptionResponse(AssessmentQuestionResult assessmentQuestionResult, short type) {

	StringBuilder sb = new StringBuilder();
	Set<AssessmentOptionAnswer> optionAnswers = assessmentQuestionResult.getOptionAnswers();
	boolean trimComma = false;
	if (optionAnswers != null) {

	    if (type == AssessmentConstants.QUESTION_TYPE_MULTIPLE_CHOICE) {
		for (AssessmentOptionAnswer optionAnswer : optionAnswers) {
		    if (optionAnswer.getAnswerBoolean() == true) {
			for (AssessmentQuestionOption questionOption : assessmentQuestionResult.getAssessmentQuestion()
				.getQuestionOptions()) {
			    if (questionOption.getUid().equals(optionAnswer.getQuestionOptionUid())) {
				sb.append(questionOption.getOptionString() + ", ");
				trimComma = true;
			    }
			}
		    }
		}
	    } else if (type == AssessmentConstants.QUESTION_TYPE_ORDERING) {
		for (int i = 0; i < optionAnswers.size(); i++) {
		    for (AssessmentOptionAnswer optionAnswer : optionAnswers) {
			if (optionAnswer.getAnswerInt() == i) {
			    for (AssessmentQuestionOption questionOption : assessmentQuestionResult
				    .getAssessmentQuestion().getQuestionOptions()) {
				if (questionOption.getUid().equals(optionAnswer.getQuestionOptionUid())) {
				    sb.append(questionOption.getOptionString() + ", ");
				    trimComma = true;
				}
			    }
			}
		    }
		}

	    } else if (type == AssessmentConstants.QUESTION_TYPE_MATCHING_PAIRS) {

		for (AssessmentQuestionOption questionOption : assessmentQuestionResult.getAssessmentQuestion()
			.getQuestionOptions()) {
		    sb.append("[" + questionOption.getOptionString() + ", ");
		    for (AssessmentOptionAnswer optionAnswer : optionAnswers) {
			if (questionOption.getUid().equals(optionAnswer.getQuestionOptionUid())) {
			    for (AssessmentQuestionOption questionOption2 : assessmentQuestionResult
				    .getAssessmentQuestion().getQuestionOptions()) {
				if (questionOption2.getUid() == optionAnswer.getAnswerInt()) {
				    sb.append(questionOption2.getOptionString() + "] ");
				}
			    }
			}
		    }

		}
	    }

	}
	String ret = sb.toString().replaceAll("\\<.*?\\>", "");

	if (trimComma) {
	    ret = ret.substring(0, ret.lastIndexOf(","));
	}

	return ret;
    }

    // *************************************************************************************
    // Private method
    // *************************************************************************************
    private IAssessmentService getAssessmentService() {
	if (service == null) {
	    WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
		    .getServletContext());
	    service = (IAssessmentService) wac.getBean(AssessmentConstants.ASSESSMENT_SERVICE);
	}
	return service;
    }

    /**
     * Removes all the html tags from a string
     * 
     * @param string
     * @return
     */
    private String removeHTMLTags(String string) {
	return string.replaceAll("\\<.*?>", "").replaceAll("&nbsp;", " ");
    }

}
