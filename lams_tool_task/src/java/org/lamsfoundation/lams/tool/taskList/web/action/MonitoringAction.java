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
package org.lamsfoundation.lams.tool.taskList.web.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.taskList.TaskListConstants;
import org.lamsfoundation.lams.tool.taskList.dto.ReflectDTO;
import org.lamsfoundation.lams.tool.taskList.dto.Summary;
import org.lamsfoundation.lams.tool.taskList.dto.TaskSummary;
import org.lamsfoundation.lams.tool.taskList.model.TaskList;
import org.lamsfoundation.lams.tool.taskList.model.TaskListItem;
import org.lamsfoundation.lams.tool.taskList.model.TaskListSession;
import org.lamsfoundation.lams.tool.taskList.model.TaskListUser;
import org.lamsfoundation.lams.tool.taskList.service.ITaskListService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MonitoringAction extends Action {
	public static Logger log = Logger.getLogger(MonitoringAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String param = mapping.getParameter();

		if (param.equals("summary")) {
			return summary(mapping, form, request, response);
		}
		if (param.equals("summaryTask")) {
			return summaryTask(mapping, form, request, response);
		}
		if(param.equals("setVerifiedByMonitor")){
			return setVerifiedByMonitor(mapping, form, request, response);
		}
		if (param.equals("listuser")) {
			return listuser(mapping, form, request, response);
		}
		if (param.equals("viewReflection")) {
			return viewReflection(mapping, form, request, response);
		}
		
		return mapping.findForward(TaskListConstants.ERROR);
	}

	private ActionForward summary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ITaskListService service = getTaskListService();
		
		Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
		request.setAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID, contentId);
		
		//initial Session Map 
		SessionMap sessionMap = new SessionMap();
		request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
		request.setAttribute(TaskListConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
		//save contentFolderID into session
		sessionMap.put(AttributeNames.PARAM_CONTENT_FOLDER_ID,WebUtil.readStrParam(request,AttributeNames.PARAM_CONTENT_FOLDER_ID));

		request.setAttribute("initialTabId",WebUtil.readLongParam(request, AttributeNames.PARAM_CURRENT_TAB,true));

		TaskList taskList = service.getTaskListByContentId(contentId);
		taskList.toDTO();
		
		Summary summary = service.getSummary(contentId);
		
		//cache into sessionMap
		sessionMap.put(TaskListConstants.ATTR_SUMMARY, summary);
		sessionMap.put(TaskListConstants.PAGE_EDITABLE, taskList.isContentInUse());
		sessionMap.put(TaskListConstants.ATTR_RESOURCE, taskList);
		sessionMap.put(TaskListConstants.ATTR_TOOL_CONTENT_ID, contentId);
		
		return mapping.findForward(TaskListConstants.SUCCESS);
	}
	
	private ActionForward summaryTask(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ITaskListService service = getTaskListService();
		
		Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
		Long taskListItemId = WebUtil.readLongParam(request, TaskListConstants.ATTR_TASK_LIST_ITEM_UID);
//		request.setAttribute(TaskListConstants.ATTR_TASK_LIST_ITEM_UID, contentId);
		
		TaskSummary taskSummary = service.getTaskSummary(contentId, taskListItemId);
		request.setAttribute(TaskListConstants.ATTR_TASK_SUMMARY, taskSummary);
		request.setAttribute("taskSummary", taskSummary);
						
		return mapping.findForward(TaskListConstants.SUCCESS);
	}
	
	/**
	 * Mark taskList item as complete status. 
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	private ActionForward setVerifiedByMonitor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Long uid = WebUtil.readLongParam(request, TaskListConstants.ATTR_USER_UID); 
		ITaskListService service = getTaskListService();
		TaskListUser user = service.getUser(uid);
		user.setVerifiedByMonitor(true);
		service.createUser(user);

		Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
		String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
		
		ActionRedirect redirect = new ActionRedirect(mapping.findForwardConfig(TaskListConstants.SUCCESS));
		redirect.addParameter(AttributeNames.PARAM_TOOL_CONTENT_ID, contentId);
		redirect.addParameter(AttributeNames.PARAM_CONTENT_FOLDER_ID, contentFolderID);
		return  redirect;
	}

	private ActionForward listuser(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Long sessionId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
		Long itemUid = WebUtil.readLongParam(request, TaskListConstants.PARAM_RESOURCE_ITEM_UID);

		//get user list by given item uid
		ITaskListService service = getTaskListService();
		List list = service.getUserListBySessionItem(sessionId, itemUid);
		
		//set to request
		request.setAttribute(TaskListConstants.ATTR_USER_LIST, list);
		return mapping.findForward(TaskListConstants.SUCCESS);
	}
	
	private ActionForward viewReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		Long uid = WebUtil.readLongParam(request, TaskListConstants.ATTR_USER_UID); 
		
		ITaskListService service = getTaskListService();
		TaskListUser user = service.getUser(uid);
		Long sessionID = user.getSession().getSessionId();
		NotebookEntry notebookEntry = service.getEntry(sessionID, 
				CoreNotebookConstants.NOTEBOOK_TOOL, 
				TaskListConstants.TOOL_SIGNATURE, user.getUserId().intValue());
		
		TaskListSession session = service.getTaskListSessionBySessionId(sessionID);
		
		ReflectDTO refDTO = new ReflectDTO(user);
		if(notebookEntry == null){
			refDTO.setFinishReflection(false);
			refDTO.setReflect(null);
		}else{
			refDTO.setFinishReflection(true);
			refDTO.setReflect(notebookEntry.getEntry());
		}
		refDTO.setReflectInstrctions(session.getTaskList().getReflectInstructions());
		
		request.setAttribute("userDTO", refDTO);
		return mapping.findForward("success");
	}

	// *************************************************************************************
	// Private method
	// *************************************************************************************
	private ITaskListService getTaskListService() {
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
		return (ITaskListService) wac.getBean(TaskListConstants.RESOURCE_SERVICE);
	}	
	
}
