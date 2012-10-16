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
package org.lamsfoundation.lams.tool.scratchie.web.action;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.scratchie.ScratchieConstants;
import org.lamsfoundation.lams.tool.scratchie.model.Scratchie;
import org.lamsfoundation.lams.tool.scratchie.model.ScratchieAnswer;
import org.lamsfoundation.lams.tool.scratchie.model.ScratchieItem;
import org.lamsfoundation.lams.tool.scratchie.model.ScratchieSession;
import org.lamsfoundation.lams.tool.scratchie.model.ScratchieUser;
import org.lamsfoundation.lams.tool.scratchie.service.IScratchieService;
import org.lamsfoundation.lams.tool.scratchie.service.ScratchieApplicationException;
import org.lamsfoundation.lams.tool.scratchie.util.ScratchieItemComparator;
import org.lamsfoundation.lams.tool.scratchie.web.form.ReflectionForm;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Andrey Balan
 */
public class LearningAction extends Action {

    private static Logger log = Logger.getLogger(LearningAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException, JSONException {

	String param = mapping.getParameter();
	// -----------------------Scratchie Learner function ---------------------------
	if (param.equals("start")) {
	    return start(mapping, form, request, response);
	}
	if (param.equals("refreshQuestionList")) {
	    return refreshQuestionList(mapping, form, request, response);
	}
	if (param.equals("becomeLeader")) {
	    return becomeLeader(mapping, form, request, response);
	}
	if (param.equals("scratchItem")) {
	    return scratchItem(mapping, form, request, response);
	}
	if (param.equals("finish")) {
	    return finish(mapping, form, request, response);
	}
	if (param.equals("showResults")) {
	    return showResults(mapping, form, request, response);
	}

	// ================ Reflection =======================
	if (param.equals("newReflection")) {
	    return newReflection(mapping, form, request, response);
	}
	if (param.equals("submitReflection")) {
	    return submitReflection(mapping, form, request, response);
	}

	return mapping.findForward(ScratchieConstants.ERROR);
    }

    /**
     * Read scratchie data from database and put them into HttpSession. It will redirect to init.do directly after this
     * method run successfully.
     * 
     * This method will avoid read database again and lost un-saved resouce item lost when user "refresh page",
     * 
     */
    private ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	// save toolContentID into HTTPSession
	ToolAccessMode mode = WebUtil.readToolAccessModeParam(request, AttributeNames.PARAM_MODE, true);
	Long toolSessionId = new Long(request.getParameter(ScratchieConstants.PARAM_TOOL_SESSION_ID));

	// get back the scratchie and item list and display them on page
	IScratchieService service = getScratchieService();
	Scratchie scratchie = service.getScratchieBySessionId(toolSessionId);
	
	ScratchieUser user = null;
	if (mode != null && mode.isTeacher()) {
	    // monitoring mode - user is specified in URL
	    // scratchieUser may be null if the user was force completed.
	    user = getSpecifiedUser(service, toolSessionId,
		    WebUtil.readIntParam(request, AttributeNames.PARAM_USER_ID, false));
	} else {
	    user = getCurrentUser(service, toolSessionId);
	}
	
	ScratchieUser groupLeader = service.getGroupLeader(toolSessionId);
	//forwards to the leaderSelection page
	if ((groupLeader == null) && !mode.isTeacher()) {
	    List<ScratchieUser> groupUsers = service.getUsersBySession(toolSessionId);
	    request.setAttribute(ScratchieConstants.ATTR_GROUP_USERS, groupUsers);
	    request.setAttribute(ScratchieConstants.PARAM_TOOL_SESSION_ID, toolSessionId);
	    request.setAttribute(ScratchieConstants.ATTR_SCRATCHIE, scratchie);
	    
	    //checks whether to display dialog prompting to become a leader
	    boolean nodialog = WebUtil.readBooleanParam(request, "nodialog", false);
	    request.setAttribute("nodialog", nodialog);
	    return mapping.findForward("leaderSelection");
	}
	
	//in case user joins the lesson after leader has scratched some answers already - we need to make sure he has the same scratches as leader 
	if (!mode.isTeacher()) {
	    service.copyScratchesFromLeader(user, groupLeader);
	}
	
	// initial Session Map
	SessionMap<String, Object> sessionMap = new SessionMap<String, Object>();
	request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);
	request.setAttribute(ScratchieConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());

	
	// get notebook entry
	String entryText = new String();
	if (user != null) {
	    NotebookEntry notebookEntry = service.getEntry(toolSessionId, CoreNotebookConstants.NOTEBOOK_TOOL,
		    ScratchieConstants.TOOL_SIGNATURE, user.getUserId().intValue());
	    if (notebookEntry != null) {
		entryText = notebookEntry.getEntry();
	    }
	}

	// basic information
	sessionMap.put(ScratchieConstants.ATTR_TITLE, scratchie.getTitle());
	sessionMap.put(ScratchieConstants.ATTR_RESOURCE_INSTRUCTION, scratchie.getInstructions());
	sessionMap.put(ScratchieConstants.ATTR_USER_ID, user.getUserId());
	sessionMap.put(ScratchieConstants.ATTR_USER, user);
	sessionMap.put(ScratchieConstants.ATTR_GROUP_LEADER, groupLeader);
	boolean isUserFinished = user != null && user.isSessionFinished();
	sessionMap.put(ScratchieConstants.ATTR_USER_FINISHED, isUserFinished);
	sessionMap.put(ScratchieConstants.ATTR_IS_SHOW_RESULTS_PAGE, scratchie.isShowResultsPage());
	sessionMap.put(AttributeNames.PARAM_TOOL_SESSION_ID, toolSessionId);
	sessionMap.put(AttributeNames.ATTR_MODE, mode);
	// reflection information
	sessionMap.put(ScratchieConstants.ATTR_REFLECTION_ON, scratchie.isReflectOnActivity());
	sessionMap.put(ScratchieConstants.ATTR_REFLECTION_INSTRUCTION, scratchie.getReflectInstructions());
	sessionMap.put(ScratchieConstants.ATTR_REFLECTION_ENTRY, entryText);

	// add define later support
	if (scratchie.isDefineLater()) {
	    return mapping.findForward("defineLater");
	}

	// set contentInUse flag to true!
	scratchie.setContentInUse(true);
	scratchie.setDefineLater(false);
	service.saveOrUpdateScratchie(scratchie);

	// add run offline support
	if (scratchie.getRunOffline()) {
	    sessionMap.put(ScratchieConstants.PARAM_RUN_OFFLINE, true);
	    return mapping.findForward("runOffline");
	} else {
	    sessionMap.put(ScratchieConstants.PARAM_RUN_OFFLINE, false);
	}

	Set<ScratchieItem> initialItems = scratchie.getScratchieItems();
	// becuase in webpage will use this login name. Here is just initialize it to avoid session close error in proxy object.
	for (ScratchieItem item : initialItems) {
	    if (item.getCreateBy() != null) {
		item.getCreateBy().getLoginName();
	    }
	}
	
	// for teacher in monitoring display the number of attempt.
	if (mode.isTeacher()) {
	    service.retrieveScratchesOrder(initialItems, user);
	}

	// set scratched flag for display purpose
	if (user != null) {
	    service.retrieveScratched(initialItems, user);
	}
	
	Collection<ScratchieItem> items = new TreeSet<ScratchieItem>(new ScratchieItemComparator());
	items.addAll(initialItems);
	sessionMap.put(ScratchieConstants.ATTR_ITEM_LIST, items);
	sessionMap.put(ScratchieConstants.ATTR_SCRATCHIE, scratchie);
	boolean isScratchingFinished = user != null && user.isScratchingFinished();
	sessionMap.put(ScratchieConstants.ATTR_IS_SCRATCHING_FINISHED, isScratchingFinished);
	
	//decide whether to show results page or learning one
	if (scratchie.isShowResultsPage() && isScratchingFinished && !mode.isTeacher()) {
	    ActionRedirect redirect = new ActionRedirect(mapping.findForwardConfig("showResults"));
	    redirect.addParameter(ScratchieConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
	    redirect.addParameter(AttributeNames.ATTR_MODE, mode);
	    return redirect;
	} else {
	    return mapping.findForward(ScratchieConstants.SUCCESS);
	}
	
    }
    
    /**
     * Refresh
     * 
     */
    private ActionForward refreshQuestionList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	
	String sessionMapID = request.getParameter(ScratchieConstants.ATTR_SESSION_MAP_ID);
	SessionMap<String, Object> sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
	request.setAttribute(ScratchieConstants.ATTR_SESSION_MAP_ID, sessionMapID);
	IScratchieService service = getScratchieService();

	Long toolSessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);
	ScratchieUser user = getCurrentUser(service, toolSessionId);
	
	// get back the scratchie and item list and display them on page
	Scratchie scratchie = service.getScratchieBySessionId(toolSessionId);

	Collection<ScratchieItem> items = new TreeSet<ScratchieItem>(new ScratchieItemComparator());
	items.addAll(scratchie.getScratchieItems());
	
	// set scratched flag for display purpose
	if (user != null) {
	    service.retrieveScratched(items, user);
	}
	
	sessionMap.put(ScratchieConstants.ATTR_ITEM_LIST, items);
	//refresh leadership status
	sessionMap.put(ScratchieConstants.ATTR_USER, user);
	//refresh ScratchingFinished status
	boolean isScratchingFinished = user != null && user.isScratchingFinished();
	sessionMap.put(ScratchieConstants.ATTR_IS_SCRATCHING_FINISHED, isScratchingFinished);
	
	return mapping.findForward(ScratchieConstants.SUCCESS);
	
    }
    
    /**
     * Sets current user as a leader of a group.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws JSONException 
     * @throws IOException 
     */
    private ActionForward becomeLeader(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws JSONException, IOException {
	IScratchieService service = getScratchieService();
	Long toolSessionId = new Long(request.getParameter(ScratchieConstants.PARAM_TOOL_SESSION_ID));
	
	ScratchieUser groupLeader = service.getGroupLeader(toolSessionId);
	//check there is no leader yet. Just in case somebody has pressed "Yes" button faster
	if (groupLeader == null) {
	    ScratchieUser user = getCurrentUser(service, toolSessionId);
	    service.setGroupLeader(user.getUserId(), toolSessionId);
	}
	
	return null;
    }
    
    /**
     * Scratch specified answer.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws JSONException 
     * @throws IOException 
     */
    private ActionForward scratchItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws JSONException, IOException {
	
	IScratchieService service = getScratchieService();

	String sessionMapID = WebUtil.readStrParam(request, ScratchieConstants.ATTR_SESSION_MAP_ID);
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
	Long toolSessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);
	
	ScratchieUser user = getCurrentUser(service, toolSessionId);
	//only leaders are allowed to scratch answers
	if (!user.isLeader()) {
	    return null;
	}

	Long answerUid = NumberUtils.createLong(request.getParameter(ScratchieConstants.PARAM_ANSWER_UID));
	ScratchieAnswer answer = service.getScratchieAnswerById(answerUid);
	if (answer == null) {
	    return mapping.findForward(ScratchieConstants.ERROR);
	}

	service.setAnswerAccess(answer.getUid(), toolSessionId);

	JSONObject JSONObject = new JSONObject();  
	JSONObject.put(ScratchieConstants.ATTR_ANSWER_CORRECT, answer.isCorrect());
	response.setContentType("application/x-json");
	response.getWriter().print(JSONObject);
	return null;

    }
    
    /**
     * Leader presses button show results. All the users set scratchingFinished to true.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    private ActionForward showResults(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	// get back SessionMap
	String sessionMapID = request.getParameter(ScratchieConstants.ATTR_SESSION_MAP_ID);
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
	request.setAttribute(ScratchieConstants.ATTR_SESSION_MAP_ID, sessionMapID);
	Long toolSessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);
	ToolAccessMode mode = WebUtil.readToolAccessModeParam(request, AttributeNames.PARAM_MODE, true);

	IScratchieService service = getScratchieService();
	Long userId = (Long) sessionMap.get(ScratchieConstants.ATTR_USER_ID);
	
	//only leaders should get to here to finalize scratching
	service.setScratchingFinished(toolSessionId);

	Set<ScratchieItem> items = service.populateItemsResults(toolSessionId, userId);
	request.setAttribute(ScratchieConstants.ATTR_ITEM_LIST, items);

	return mapping.findForward(ScratchieConstants.SUCCESS);
    }

    /**
     * Finish learning session.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    private ActionForward finish(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	// get back SessionMap
	String sessionMapID = request.getParameter(ScratchieConstants.ATTR_SESSION_MAP_ID);
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
	Long toolSessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);
	ScratchieUser user = (ScratchieUser) sessionMap.get(AttributeNames.USER);
	IScratchieService service = getScratchieService();
	
	//in case of the leader we should let all other learners see Next Activity button
	if (user.isLeader()) {
	    service.setScratchingFinished(toolSessionId);
	}
	
	String nextActivityUrl = null;
	try {
	    nextActivityUrl = service.finishToolSession(toolSessionId, user.getUserId());
	    request.setAttribute(ScratchieConstants.ATTR_NEXT_ACTIVITY_URL, nextActivityUrl);
	} catch (ScratchieApplicationException e) {
	    LearningAction.log.error("Failed get next activity url:" + e.getMessage());
	}

	return mapping.findForward(ScratchieConstants.SUCCESS);
    }

    /**
     * Display empty reflection form.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    private ActionForward newReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	// get session value
	String sessionMapID = WebUtil.readStrParam(request, ScratchieConstants.ATTR_SESSION_MAP_ID);

	ReflectionForm refForm = (ReflectionForm) form;
	HttpSession ss = SessionManager.getSession();
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);

	refForm.setUserID(user.getUserID());
	refForm.setSessionMapID(sessionMapID);

	// get the existing reflection entry
	IScratchieService submitFilesService = getScratchieService();

	SessionMap map = (SessionMap) request.getSession().getAttribute(sessionMapID);
	Long toolSessionID = (Long) map.get(AttributeNames.PARAM_TOOL_SESSION_ID);
	NotebookEntry entry = submitFilesService.getEntry(toolSessionID, CoreNotebookConstants.NOTEBOOK_TOOL,
		ScratchieConstants.TOOL_SIGNATURE, user.getUserID());

	if (entry != null) {
	    refForm.setEntryText(entry.getEntry());
	}

	return mapping.findForward(ScratchieConstants.SUCCESS);
    }

    /**
     * Submit reflection form input database.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    private ActionForward submitReflection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	ReflectionForm refForm = (ReflectionForm) form;
	Integer userId = refForm.getUserID();

	String sessionMapID = WebUtil.readStrParam(request, ScratchieConstants.ATTR_SESSION_MAP_ID);
	SessionMap sessionMap = (SessionMap) request.getSession().getAttribute(sessionMapID);
	Long sessionId = (Long) sessionMap.get(AttributeNames.PARAM_TOOL_SESSION_ID);

	IScratchieService service = getScratchieService();

	// check for existing notebook entry
	NotebookEntry entry = service.getEntry(sessionId, CoreNotebookConstants.NOTEBOOK_TOOL,
		ScratchieConstants.TOOL_SIGNATURE, userId);

	if (entry == null) {
	    // create new entry
	    service.createNotebookEntry(sessionId, CoreNotebookConstants.NOTEBOOK_TOOL,
		    ScratchieConstants.TOOL_SIGNATURE, userId, refForm.getEntryText());
	} else {
	    // update existing entry
	    entry.setEntry(refForm.getEntryText());
	    entry.setLastModified(new Date());
	    service.updateEntry(entry);
	}

	return finish(mapping, form, request, response);
    }

    // *************************************************************************************
    // Private method
    // *************************************************************************************
    
    private IScratchieService getScratchieService() {
	WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
		.getServletContext());
	return (IScratchieService) wac.getBean(ScratchieConstants.RESOURCE_SERVICE);
    }

    private ScratchieUser getCurrentUser(IScratchieService service, Long sessionId) {
	// try to get form system session
	HttpSession ss = SessionManager.getSession();
	// get back login user DTO
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	ScratchieUser scratchieUser = service.getUserByIDAndSession(new Long(user.getUserID().intValue()), sessionId);

	if (scratchieUser == null) {
	    ScratchieSession session = service.getScratchieSessionBySessionId(sessionId);
	    scratchieUser = new ScratchieUser(user, session);
	    service.createUser(scratchieUser);
	}
	return scratchieUser;
    }

    private ScratchieUser getSpecifiedUser(IScratchieService service, Long sessionId, Integer userId) {
	ScratchieUser scratchieUser = service.getUserByIDAndSession(new Long(userId.intValue()), sessionId);
	if (scratchieUser == null) {
	    log.error("Unable to find specified user for scratchie activity. Screens are likely to fail. SessionId="
		    + sessionId + " UserId=" + userId);
	}
	return scratchieUser;
    }

}
