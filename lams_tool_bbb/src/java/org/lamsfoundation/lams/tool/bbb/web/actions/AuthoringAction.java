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
/* $Id$ */

package org.lamsfoundation.lams.tool.bbb.web.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.bbb.model.Bbb;
import org.lamsfoundation.lams.tool.bbb.model.BbbAttachment;
import org.lamsfoundation.lams.tool.bbb.service.BbbServiceProxy;
import org.lamsfoundation.lams.tool.bbb.service.IBbbService;
import org.lamsfoundation.lams.tool.bbb.util.Constants;
import org.lamsfoundation.lams.tool.bbb.web.forms.AuthoringForm;
import org.lamsfoundation.lams.util.FileValidatorUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;

/**
 * @author
 * @version
 * 
 * @struts.action path="/authoring" name="authoringForm" parameter="dispatch" scope="request" validate="false"
 * 
 * @struts.action-forward name="success" path="tiles:/authoring/main"
 */
public class AuthoringAction extends DispatchAction {

    // private static final Logger logger =
    // Logger.getLogger(AuthoringAction.class);

    private IBbbService bbbService;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	// set up bbbService
	bbbService = BbbServiceProxy.getBbbService(this.getServlet().getServletContext());

	return super.execute(mapping, form, request, response);
    }

    /**
     * Default method when no dispatch parameter is specified. It is expected that the parameter
     * <code>toolContentID</code> will be passed in. This will be used to retrieve content for this tool.
     * 
     * @throws ServletException
     * 
     */
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws ServletException {

	// Extract toolContentID from parameters.
	Long toolContentID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID));

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);

	ToolAccessMode mode = WebUtil.readToolAccessModeParam(request, AttributeNames.PARAM_MODE, true);

	// retrieving Bbb with given toolContentID
	Bbb bbb = bbbService.getBbbByContentId(toolContentID);
	if (bbb == null) {
	    bbb = bbbService.copyDefaultContent(toolContentID);
	    bbb.setCreateDate(new Date());
	    bbbService.saveOrUpdateBbb(bbb);
	    // TODO NOTE: this causes DB orphans when LD not saved.
	}

	if (mode != null && mode.isTeacher()) {
	    // Set the defineLater flag so that learners cannot use content
	    // while we are editing. This flag is released when updateContent is
	    // called.
	    bbb.setDefineLater(true);
	    bbbService.saveOrUpdateBbb(bbb);
	}

	// Set up the authForm.
	AuthoringForm authForm = (AuthoringForm) form;
	copyProperties(authForm, bbb);

	// Set up sessionMap
	SessionMap<String, Object> map = createSessionMap(bbb, getAccessMode(request), contentFolderID,
		toolContentID);
	authForm.setSessionMapID(map.getSessionID());

	// add the sessionMap to HTTPSession.
	request.getSession().setAttribute(map.getSessionID(), map);
	request.setAttribute(Constants.ATTR_SESSION_MAP, map);

	return mapping.findForward("success");
    }

    public ActionForward updateContent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	// get authForm and session map.
	AuthoringForm authForm = (AuthoringForm) form;
	SessionMap<String, Object> map = getSessionMap(request, authForm);

	// get bbb content.
	Bbb bbb = bbbService.getBbbByContentId((Long) map.get(Constants.KEY_TOOL_CONTENT_ID));

	// update bbb content using form inputs.
	ToolAccessMode mode = (ToolAccessMode) map.get(Constants.KEY_MODE);
	copyProperties(bbb, authForm, mode);

	// remove attachments marked for deletion.
	Set<BbbAttachment> attachments = bbb.getBbbAttachments();
	if (attachments == null) {
	    attachments = new HashSet<BbbAttachment>();
	}

	for (BbbAttachment att : getAttList(Constants.KEY_DELETED_FILES, map)) {
	    // remove from db, leave in repository
	    attachments.remove(att);
	}

	// add unsaved attachments
	attachments.addAll(getAttList(Constants.KEY_UNSAVED_ONLINE_FILES, map));
	attachments.addAll(getAttList(Constants.KEY_UNSAVED_OFFLINE_FILES, map));

	// set attachments in case it didn't exist
	bbb.setBbbAttachments(attachments);

	// set the update date
	bbb.setUpdateDate(new Date());

	// releasing defineLater flag so that learner can start using the tool.
	bbb.setDefineLater(false);

	bbbService.saveOrUpdateBbb(bbb);

	request.setAttribute(AuthoringConstants.LAMS_AUTHORING_SUCCESS_FLAG, Boolean.TRUE);

	// add the sessionMapID to form
	authForm.setSessionMapID(map.getSessionID());

	request.setAttribute(Constants.ATTR_SESSION_MAP, map);

	return mapping.findForward("success");
    }

    public ActionForward uploadOnline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return uploadFile(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_ONLINE, request);
    }

    public ActionForward uploadOffline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return uploadFile(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_OFFLINE, request);
    }

    public ActionForward deleteOnline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return deleteFile(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_ONLINE, request);
    }

    public ActionForward deleteOffline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return deleteFile(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_OFFLINE, request);
    }

    public ActionForward removeUnsavedOnline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return removeUnsaved(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_ONLINE, request);
    }

    public ActionForward removeUnsavedOffline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return removeUnsaved(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_OFFLINE, request);
    }

    /* ========== Private Methods */

    private ActionForward uploadFile(ActionMapping mapping, AuthoringForm authForm, String type,
	    HttpServletRequest request) {
	SessionMap<String, Object> map = getSessionMap(request, authForm);

	FormFile file;
	List<BbbAttachment> unsavedFiles;
	List<BbbAttachment> savedFiles;
	if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
	    file = (FormFile) authForm.getOfflineFile();
	    unsavedFiles = getAttList(Constants.KEY_UNSAVED_OFFLINE_FILES, map);

	    savedFiles = getAttList(Constants.KEY_OFFLINE_FILES, map);
	} else {
	    file = (FormFile) authForm.getOnlineFile();
	    unsavedFiles = getAttList(Constants.KEY_UNSAVED_ONLINE_FILES, map);

	    savedFiles = getAttList(Constants.KEY_ONLINE_FILES, map);
	}

	// validate file max size
	ActionMessages errors = new ActionMessages();
	FileValidatorUtil.validateFileSize(file, true, errors);
	if (!errors.isEmpty()) {
	    request.setAttribute(Constants.ATTR_SESSION_MAP, map);
	    this.saveErrors(request, errors);
	    return mapping.findForward("success");
	}

	if (file.getFileName().length() != 0) {

	    // upload file to repository
	    BbbAttachment newAtt = bbbService.uploadFileToContent((Long) map.get(Constants.KEY_TOOL_CONTENT_ID),
		    file, type);

	    // Add attachment to unsavedFiles
	    // check to see if file with same name exists
	    BbbAttachment currAtt;
	    Iterator<BbbAttachment> iter = savedFiles.iterator();
	    while (iter.hasNext()) {
		currAtt = (BbbAttachment) iter.next();
		if (StringUtils.equals(currAtt.getFileName(), newAtt.getFileName())
			&& StringUtils.equals(currAtt.getFileType(), newAtt.getFileType())) {
		    // move from this this list to deleted list.
		    getAttList(Constants.KEY_DELETED_FILES, map).add(currAtt);
		    iter.remove();
		    break;
		}
	    }
	    unsavedFiles.add(newAtt);

	    request.setAttribute(Constants.ATTR_SESSION_MAP, map);
	}
	return mapping.findForward("success");
    }

    private ActionForward deleteFile(ActionMapping mapping, AuthoringForm authForm, String type,
	    HttpServletRequest request) {
	SessionMap<String, Object> map = getSessionMap(request, authForm);

	List<BbbAttachment> fileList;
	if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
	    fileList = getAttList(Constants.KEY_OFFLINE_FILES, map);
	} else {
	    fileList = getAttList(Constants.KEY_ONLINE_FILES, map);
	}

	Iterator<BbbAttachment> iter = fileList.iterator();

	while (iter.hasNext()) {
	    BbbAttachment att = (BbbAttachment) iter.next();

	    if (att.getFileUuid().equals(authForm.getDeleteFileUuid())) {
		// move to delete file list, deleted at next updateContent
		getAttList(Constants.KEY_DELETED_FILES, map).add(att);

		// remove from this list
		iter.remove();
		break;
	    }
	}

	request.setAttribute(Constants.ATTR_SESSION_MAP, map);

	return mapping.findForward("success");
    }

    private ActionForward removeUnsaved(ActionMapping mapping, AuthoringForm authForm, String type,
	    HttpServletRequest request) {
	SessionMap<String, Object> map = getSessionMap(request, authForm);

	List<BbbAttachment> unsavedFiles;

	if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
	    unsavedFiles = getAttList(Constants.KEY_UNSAVED_OFFLINE_FILES, map);
	} else {
	    unsavedFiles = getAttList(Constants.KEY_UNSAVED_ONLINE_FILES, map);
	}

	Iterator<BbbAttachment> iter = unsavedFiles.iterator();
	while (iter.hasNext()) {
	    BbbAttachment att = (BbbAttachment) iter.next();

	    if (att.getFileUuid().equals(authForm.getDeleteFileUuid())) {
		// delete from repository and list
		bbbService.deleteFromRepository(att.getFileUuid(), att.getFileVersionId());
		iter.remove();
		break;
	    }
	}

	request.setAttribute(Constants.ATTR_SESSION_MAP, map);

	return mapping.findForward("success");
    }

    /**
     * Updates Bbb content using AuthoringForm inputs.
     * 
     * @param authForm
     * @param mode
     * @return
     */
    private void copyProperties(Bbb bbb, AuthoringForm authForm, ToolAccessMode mode) {
	bbb.setTitle(authForm.getTitle());
	bbb.setInstructions(authForm.getInstructions());
	if (mode.isAuthor()) { // Teacher cannot modify following
	    bbb.setOfflineInstructions(authForm.getOfflineInstructions());
	    bbb.setOnlineInstructions(authForm.getOnlineInstructions());
	    bbb.setReflectOnActivity(authForm.isReflectOnActivity());
	    bbb.setReflectInstructions(authForm.getReflectInstructions());
	    bbb.setLockOnFinished(authForm.isLockOnFinished());
	    
	}
    }

    /**
     * Updates AuthoringForm using Bbb content.
     * 
     * @param bbb
     * @param authForm
     * @return
     * @throws ServletException
     */
    private void copyProperties(AuthoringForm authForm, Bbb bbb) throws ServletException {
	try {
	    BeanUtils.copyProperties(authForm, bbb);
	} catch (IllegalAccessException e) {
	    throw new ServletException(e);
	} catch (InvocationTargetException e) {
	    throw new ServletException(e);
	}
    }

    /**
     * Updates SessionMap using Bbb content.
     * 
     * @param bbb
     * @param mode
     */
    private SessionMap<String, Object> createSessionMap(Bbb bbb, ToolAccessMode mode, String contentFolderID,
	    Long toolContentID) {

	SessionMap<String, Object> map = new SessionMap<String, Object>();

	map.put(Constants.KEY_MODE, mode);
	map.put(Constants.KEY_CONTENT_FOLDER_ID, contentFolderID);
	map.put(Constants.KEY_TOOL_CONTENT_ID, toolContentID);
	map.put(Constants.KEY_ONLINE_FILES, new LinkedList<BbbAttachment>());
	map.put(Constants.KEY_OFFLINE_FILES, new LinkedList<BbbAttachment>());
	map.put(Constants.KEY_UNSAVED_ONLINE_FILES, new LinkedList<BbbAttachment>());
	map.put(Constants.KEY_UNSAVED_OFFLINE_FILES, new LinkedList<BbbAttachment>());
	map.put(Constants.KEY_DELETED_FILES, new LinkedList<BbbAttachment>());

	for (BbbAttachment attachment : bbb.getBbbAttachments()) {
	    String type = attachment.getFileType();
	    if (type.equals(IToolContentHandler.TYPE_OFFLINE)) {
		getAttList(Constants.KEY_OFFLINE_FILES, map).add(attachment);
	    }
	    if (type.equals(IToolContentHandler.TYPE_ONLINE)) {
		getAttList(Constants.KEY_ONLINE_FILES, map).add(attachment);
	    }
	}

	return map;
    }

    /**
     * Get ToolAccessMode from HttpRequest parameters. Default value is AUTHOR mode.
     * 
     * @param request
     * @return
     */
    private ToolAccessMode getAccessMode(HttpServletRequest request) {
	ToolAccessMode mode;
	String modeStr = request.getParameter(AttributeNames.ATTR_MODE);
	if (StringUtils.equalsIgnoreCase(modeStr, ToolAccessMode.TEACHER.toString())) {
	    mode = ToolAccessMode.TEACHER;
	} else {
	    mode = ToolAccessMode.AUTHOR;
	}
	return mode;
    }

    /**
     * Retrieves a List of attachments from the map using the key.
     * 
     * @param key
     * @param map
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<BbbAttachment> getAttList(String key, SessionMap<String, Object> map) {
	List<BbbAttachment> list = (List<BbbAttachment>) map.get(key);
	return list;
    }

    /**
     * Retrieve the SessionMap from the HttpSession.
     * 
     * @param request
     * @param authForm
     * @return
     */
    @SuppressWarnings("unchecked")
    private SessionMap<String, Object> getSessionMap(HttpServletRequest request, AuthoringForm authForm) {
	return (SessionMap<String, Object>) request.getSession().getAttribute(authForm.getSessionMapID());
    }
}
