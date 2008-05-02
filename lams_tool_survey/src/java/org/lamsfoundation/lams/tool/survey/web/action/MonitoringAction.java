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
package org.lamsfoundation.lams.tool.survey.web.action;

import static org.lamsfoundation.lams.tool.survey.SurveyConstants.ATTR_ANSWER_LIST;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.ATTR_QUESTION;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.CHART_TYPE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.survey.SurveyConstants;
import org.lamsfoundation.lams.tool.survey.dto.AnswerDTO;
import org.lamsfoundation.lams.tool.survey.dto.ReflectDTO;
import org.lamsfoundation.lams.tool.survey.model.Survey;
import org.lamsfoundation.lams.tool.survey.model.SurveyOption;
import org.lamsfoundation.lams.tool.survey.model.SurveyQuestion;
import org.lamsfoundation.lams.tool.survey.model.SurveySession;
import org.lamsfoundation.lams.tool.survey.model.SurveyUser;
import org.lamsfoundation.lams.tool.survey.service.ISurveyService;
import org.lamsfoundation.lams.tool.survey.util.SurveyUserComparator;
import org.lamsfoundation.lams.tool.survey.util.SurveyWebUtils;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MonitoringAction extends Action {
	private static final String MSG_LABEL_QUESTION = "label.question";
	private static final String MSG_LABEL_OPEN_RESPONSE = "label.open.response";
	private static final String MSG_LABEL_SESSION_NAME = "label.session.name";
	private static final String MSG_LABEL_POSSIBLE_ANSWERS = "message.possible.answers";
	private static final String MSG_LABEL_LEARNER = "label.learner";
	
	public static Logger log = Logger.getLogger(MonitoringAction.class);
	
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		String param = mapping.getParameter();

		if (param.equals("summary")) {
			return summary(mapping, form, request, response);
		}

		if (param.equals("viewChartReport")) {
			return viewChartReport(mapping, form, request, response);
		}

		if (param.equals("listAnswers")) {
			return listAnswers(mapping, form, request, response);
		}

		if (param.equals("viewReflection")) {
			return viewReflection(mapping, form, request, response);
		}
		
		if (param.equals("exportSurvey")) {
			return exportSurvey(mapping, form, request, response);
		}

		return mapping.findForward(SurveyConstants.ERROR);
	}


	/**
	 * Summary page action.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */

	private ActionForward summary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		//initial Session Map 
		SessionMap sessionMap = new SessionMap();
		request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
		request.setAttribute(SurveyConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
		//save contentFolderID into session
		sessionMap.put(AttributeNames.PARAM_CONTENT_FOLDER_ID,WebUtil.readStrParam(request,AttributeNames.PARAM_CONTENT_FOLDER_ID));

		request.setAttribute("initialTabId",WebUtil.readLongParam(request, AttributeNames.PARAM_CURRENT_TAB,true));

		Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
		ISurveyService service = getSurveyService();
		
		//get summary 
		SortedMap<SurveySession,List<AnswerDTO>> summary = service.getSummary(contentId);
		
		//get survey 
		Survey survey = service.getSurveyByContentId(contentId);
		
		//get statistic
		SortedMap<SurveySession,Integer> statis = service.getStatistic(contentId);
		
		//get refection list
		Map<Long,Set<ReflectDTO> >relectList = service.getReflectList(contentId);
		
		//cache into sessionMap
		sessionMap.put(SurveyConstants.ATTR_SUMMARY_LIST, summary);
		sessionMap.put(SurveyConstants.ATTR_STATISTIC_LIST, statis);
		sessionMap.put(SurveyConstants.PAGE_EDITABLE, new Boolean(SurveyWebUtils.isSurveyEditable(survey)));
		sessionMap.put(SurveyConstants.ATTR_SURVEY, survey);
		sessionMap.put(AttributeNames.PARAM_TOOL_CONTENT_ID, contentId);
		sessionMap.put(SurveyConstants.ATTR_REFLECT_LIST, relectList);
		
		return mapping.findForward(SurveyConstants.SUCCESS);
	}
	/**
	 * Display pie chart or bar chart for one question.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward viewChartReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Long sessionId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
		String type= WebUtil.readStrParam(request, CHART_TYPE);
		
		Long questionUid = WebUtil.readLongParam(request, SurveyConstants.ATTR_QUESTION_UID);
		ISurveyService service = getSurveyService();
		//get question
		SurveyQuestion question = service.getQuestion(questionUid);

		//set all attribute to request for show
		request.setAttribute( AttributeNames.PARAM_TOOL_SESSION_ID, sessionId);
		request.setAttribute( CHART_TYPE, type);
		request.setAttribute( ATTR_QUESTION, question);
		
		return mapping.findForward(SurveyConstants.SUCCESS);
	}



	private ActionForward listAnswers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Long sessionId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
		Long questionUid = WebUtil.readLongParam(request, SurveyConstants.ATTR_QUESTION_UID);

		//get user list 
		ISurveyService service = getSurveyService();
		
		SortedMap<SurveyUser,AnswerDTO> userAnswerMap = new TreeMap<SurveyUser, AnswerDTO>(new SurveyUserComparator());
//		get all users with their answers whatever they answer or not
		List<SurveyUser> users = service.getSessionUsers(sessionId);
		for (SurveyUser user : users) {
			List<AnswerDTO> questionAnswers = service.getQuestionAnswers(sessionId, user.getUid());
			for (AnswerDTO questionAnswer : questionAnswers) {
				if(questionUid.equals(questionAnswer.getUid())){
					userAnswerMap.put(user,questionAnswer);
					break;
				}
			}
		}
		//set all attribute to request for show
		request.setAttribute( ATTR_ANSWER_LIST, userAnswerMap);
		
		return mapping.findForward(SurveyConstants.SUCCESS);
	}
	
	private ActionForward viewReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		Long uid = WebUtil.readLongParam(request, SurveyConstants.ATTR_USER_UID); 
		Long sessionID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
		
		ISurveyService service = getSurveyService();
		SurveyUser user = service.getUser(uid);
		NotebookEntry notebookEntry = service.getEntry(sessionID, 
				CoreNotebookConstants.NOTEBOOK_TOOL, 
				SurveyConstants.TOOL_SIGNATURE, user.getUserId().intValue());
		
		SurveySession session = service.getSurveySessionBySessionId(sessionID);
		
		ReflectDTO refDTO = new ReflectDTO(user);
		if(notebookEntry == null){
			refDTO.setFinishReflection(false);
			refDTO.setReflect(null);
		}else{
			refDTO.setFinishReflection(true);
			refDTO.setReflect(notebookEntry.getEntry());
		}
		refDTO.setReflectInstrctions(session.getSurvey().getReflectInstructions());
		
		request.setAttribute("userDTO", refDTO);
		return mapping.findForward("success");
	}	

	/**
	 * Export Excel format survey data.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward exportSurvey(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Long toolSessionID =new Long(WebUtil.readLongParam(request,AttributeNames.PARAM_TOOL_SESSION_ID));
		
		ISurveyService service = getSurveyService();
		
		SortedMap<SurveySession,SortedMap<SurveyQuestion,List<AnswerDTO>>>  groupList = service.exportBySessionId(toolSessionID);
		String errors = null;
		MessageService resource = getMessageService();
		try {
			//create an empty excel file
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Survey");
			sheet.setColumnWidth((short)0,(short)5000);
			HSSFRow row;
			HSSFCell cell;
			int idx = 0;
			Set<Entry<SurveySession,SortedMap<SurveyQuestion,List<AnswerDTO>>>> entries = groupList.entrySet();
			for (Entry<SurveySession,SortedMap<SurveyQuestion,List<AnswerDTO>>> entry : entries) {
				SurveySession session = entry.getKey();
				SortedMap<SurveyQuestion,List<AnswerDTO>> map = entry.getValue();
				//display survey title, instruction and questions
				Survey survey = session.getSurvey();
				//survey title
				row = sheet.createRow(idx++);
				cell = row.createCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(survey.getTitle());
				
				//survey instruction
				row = sheet.createRow(idx++);
				cell = row.createCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(survey.getInstructions());
				
				//display 2 empty row
				row = sheet.createRow(idx++);
				cell = row.createCell((short) 0);
				cell.setCellValue("");
				row = sheet.createRow(idx++);
				cell = row.createCell((short) 0);
				cell.setCellValue("");
				
				//display session name
				row = sheet.createRow(idx++);
				cell = row.createCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(resource.getMessage(MSG_LABEL_SESSION_NAME));
				cell = row.createCell((short) 1);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(session.getSessionName());
				
				//begin to display question and its answers
				Set<Entry<SurveyQuestion,List<AnswerDTO>>> questionEntries =  map.entrySet();
				int questionIdx = 0;
				for (Entry<SurveyQuestion,List<AnswerDTO>> questionEntry : questionEntries ) {
					//display 1 empty row
					row = sheet.createRow(idx++);
					cell = row.createCell((short) 0);
					cell.setCellValue("");
										
					questionIdx++;
					SurveyQuestion question = questionEntry.getKey();
					List<AnswerDTO> answers = questionEntry.getValue();
					
					//display question content
					row = sheet.createRow(idx++);
					cell = row.createCell((short) 0);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(resource.getMessage(MSG_LABEL_QUESTION) + " " + questionIdx);
					cell = row.createCell((short) 1);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(question.getDescription());

					
					//display options content
					Set<SurveyOption> options = question.getOptions();
					
					row = sheet.createRow(idx++);
					cell = row.createCell((short) 0);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(resource.getMessage(MSG_LABEL_POSSIBLE_ANSWERS));
					
					int optionIdx = 0;
					for (SurveyOption option : options) {
						optionIdx++;
						row = sheet.createRow(idx++);
						cell = row.createCell((short) 0);
						cell.setCellValue(SurveyConstants.OPTION_SHORT_HEADER + optionIdx);
						cell = row.createCell((short) 1);
						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(option.getDescription());
					}
					if(question.isAppendText() || question.getType() == SurveyConstants.QUESTION_TYPE_TEXT_ENTRY){
						optionIdx++;
						row = sheet.createRow(idx++);
						cell = row.createCell((short) 0);
						cell.setCellValue(SurveyConstants.OPTION_SHORT_HEADER + optionIdx);
						cell = row.createCell((short) 1);
						cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(resource.getMessage(MSG_LABEL_OPEN_RESPONSE));
					}
					
					//display 1 empty row
					row = sheet.createRow(idx++);
					cell = row.createCell((short) 0);
					cell.setCellValue("");
					
					////////////////////////////
					//display answer list 
					////////////////////////////
					// first display option title : a1 , a2, a3 etc

					int cellIdx = 0;
					row = sheet.createRow(idx++);
					cell = row.createCell((short) cellIdx);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					cell.setCellValue(resource.getMessage(MSG_LABEL_LEARNER));
					//increase one more option number if there are open entry option
					int optionsNum = options.size();
					if(question.isAppendText() || question.getType() == SurveyConstants.QUESTION_TYPE_TEXT_ENTRY){
						optionsNum++;
					}
					for(cellIdx = 1; cellIdx<=optionsNum;cellIdx++){
						cell = row.createCell((short) cellIdx);
						cell.setCellValue(SurveyConstants.OPTION_SHORT_HEADER + cellIdx);
					}
					
					//display all users' answers for this question in multiple rows
					for (AnswerDTO answer: answers) {
						row = sheet.createRow(idx++);
						cellIdx = 0;
						cell = row.createCell((short) cellIdx);
						cell.setCellValue(answer.getReplier().getLoginName());
						//for answer's options
						for (SurveyOption option : options) {
							cellIdx++;
							cell = row.createCell((short) cellIdx);
							if(answer.getAnswer() == null)
								break;
							String[] choices = answer.getAnswer().getChoices();
							for (String choice : choices) {
								if(StringUtils.equals(choice, option.getUid().toString())){
									cell.setCellValue("X");
								}
							}
						}
						//for textEntry option
						if(question.isAppendText() || question.getType() == SurveyConstants.QUESTION_TYPE_TEXT_ENTRY){
							cell = row.createCell((short) ++cellIdx);
							cell.setEncoding(HSSFCell.ENCODING_UTF_16);
							if(answer.getAnswer() != null){
								cell.setCellValue(answer.getAnswer().getAnswerText());
							}
						}
						
					}
				}
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);
			//construct download file response header
			String fileName = "lams_survey_" + toolSessionID+".xls";
			String mineType = "application/vnd.ms-excel";
			String header = "attachment; filename=\"" + fileName + "\";";
			response.setContentType(mineType);
			response.setHeader("Content-Disposition",header);

			byte[] data = bos.toByteArray();
			response.getOutputStream().write(data,0,data.length);
			response.getOutputStream().flush();
		} catch (Exception e) {
			log.error(e);
			errors =new ActionMessage("error.monitoring.export.excel",e.toString()).toString();
		}

		if(errors != null){
			try {
				PrintWriter out = response.getWriter();
				out.write(errors);
				out.flush();
			} catch (IOException e) {
			}
		}
		return null;
	}

	// *************************************************************************************
	// Private method
	// *************************************************************************************
	private ISurveyService getSurveyService() {
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
				.getServletContext());
		return (ISurveyService) wac.getBean(SurveyConstants.SURVEY_SERVICE);
	}
	
	/**
	 * Return ResourceService bean.
	 */
	private MessageService getMessageService() {
	      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
	      return (MessageService) wac.getBean("lasurvMessageService");
	}
}
