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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */

package org.lamsfoundation.lams.tool.chat.web.actions;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.chat.beans.AuthoringSessionBean;
import org.lamsfoundation.lams.tool.chat.model.Chat;
import org.lamsfoundation.lams.tool.chat.model.ChatAttachment;
import org.lamsfoundation.lams.tool.chat.service.ChatServiceProxy;
import org.lamsfoundation.lams.tool.chat.service.IChatService;
import org.lamsfoundation.lams.tool.chat.util.ChatConstants;
import org.lamsfoundation.lams.tool.chat.web.forms.AuthoringForm;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;

/**
 * @author
 * @version
 * 
 * @struts.action path="/authoring" name="authoringForm" parameter="dispatch"
 *                scope="request" validate="false"
 * 
 * @struts.action-forward name="success" path="tiles:/authoring/main"
 * @struts.action-forward name="message_page" path="tiles:/generic/message"
 */
public class AuthoringAction extends LamsDispatchAction {

	private static Logger logger = Logger.getLogger(AuthoringAction.class);

	public IChatService chatService;

	/**
	 * Default method when no dispatch parameter is specified. It is expected
	 * that the parameter <code>toolContentID</code> will be passed in. This
	 * will be used to retrieve content for this tool.
	 * 
	 */
	protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		// Extract toolContentID from parameters.
		Long toolContentID = new Long(WebUtil.readLongParam(request,
				AttributeNames.PARAM_TOOL_CONTENT_ID));
		if (logger.isDebugEnabled()) {
			logger.debug("entering method unspecified: toolContentID = "
					+ toolContentID);
		}

		// set up chatService
		if (chatService == null) {
			chatService = ChatServiceProxy.getChatService(this.getServlet()
					.getServletContext());
		}

		// retrieving Chat with given toolContentID
		Chat chat = chatService.getChatByContentId(toolContentID);
		if (chat == null) {
			chat = chatService.copyDefaultContent(toolContentID);
			chat.setCreateDate(new Date());
			chatService.saveOrUpdateChat(chat);
			// TODO NOTE: this causes DB orphans when LD not saved.
		}

		// check if content in use is set
		if (chat.getContentInUse()) {
			// Cannot edit while content is in use.
			request.setAttribute(ChatConstants.ATTR_MESSAGE, getResources(request).getMessage(
			"error.content.locked"));
			return mapping.findForward("message_page");
		}

		// set the defineLater flag so that learners cannot use content while we
		// are editing.
		// This flag is released when updateContent is called.
		chat.setDefineLater(true);
		chatService.saveOrUpdateChat(chat);

		// set the access mode.
		ToolAccessMode mode = getAccessMode(request);
		request.setAttribute(AttributeNames.ATTR_MODE, mode.toString());

		// create a new authoringSessionBean and add to session
		AuthoringSessionBean authSession = new AuthoringSessionBean();
		String id = ChatConstants.AUTH_SESSION_ID
				+ createAuthSessionId(request.getSession());
		authSession.setAuthSessionId(id);
		authSession.setMode(mode);
		request.getSession().setAttribute(id, authSession);

		// set up the form.
		AuthoringForm authForm = (AuthoringForm) form;

		// populating the AuthoringForm using Chat content
		populateAuthForm(authForm, chat);
		resetAuthSession(authSession, chat);

		authForm.setAuthSession(authSession);
		authForm.setAuthSessionId(authSession.getAuthSessionId());

		return mapping.findForward("success");
	}

	private synchronized Long createAuthSessionId(HttpSession httpSession) {
		Long authSessionId = (Long) httpSession
				.getAttribute(ChatConstants.AUTH_SESSION_ID_COUNTER);
		if (authSessionId == null) {
			authSessionId = 1L;
			httpSession.setAttribute(ChatConstants.AUTH_SESSION_ID_COUNTER,
					authSessionId);
		} else {
			authSessionId++;
		}
		return authSessionId;
	}

	public ActionForward updateContent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		setAccessMode(request, form);

		// TODO need error checking.
		AuthoringForm authForm = (AuthoringForm) form;

		// retrieving authoring session bean
		AuthoringSessionBean authSession = (AuthoringSessionBean) request
				.getSession().getAttribute(authForm.getAuthSessionId());

		// retrieve the content.
		if (chatService == null) {
			chatService = ChatServiceProxy.getChatService(this.getServlet()
					.getServletContext());
		}
		Chat chat = chatService.getChatByContentId(authForm.getToolContentID());

		// copy form inputs to content
		populateChat(chat, authForm);

		// adding unsaved uploaded files.
		Set attachments = chat.getChatAttachments();
		if (attachments == null) {
			attachments = new HashSet();
		}
		attachments.addAll(authSession.getUnsavedOnlineFilesList());
		attachments.addAll(authSession.getUnsavedOfflineFilesList());

		// Removing attachments marked for deletion.
		List<ChatAttachment> deletedAttachments = authSession
				.getDeletedFilesList();
		for (ChatAttachment delAtt : deletedAttachments) {
			// remove from repository
			chatService.deleteFromRepository(delAtt.getFileUuid(), delAtt
					.getFileVersionId());

			// remove from ChatAttachments
			Iterator attIter = attachments.iterator();
			while (attIter.hasNext()) {
				ChatAttachment att = (ChatAttachment) attIter.next();
				if (delAtt.getUid().equals(att.getUid())) {
					attIter.remove();
					break;
				}
			}
		}

		// set attachments in case it didnt exist
		chat.setChatAttachments(attachments);

		// set the update date
		chat.setUpdateDate(new Date());

		// releasing defineLater flag so that learner can start using the tool.
		chat.setDefineLater(false);

		// persist changes.
		chatService.saveOrUpdateChat(chat);

		request.setAttribute(AuthoringConstants.LAMS_AUTHORING_SUCCESS_FLAG,
				Boolean.TRUE);

		return mapping.findForward("success");
	}

	public ActionForward uploadOnline(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		setAccessMode(request, form);

		return uploadFile(mapping, form, IToolContentHandler.TYPE_ONLINE,
				request);
	}

	public ActionForward uploadOffline(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		setAccessMode(request, form);

		return uploadFile(mapping, form, IToolContentHandler.TYPE_OFFLINE,
				request);
	}

	public ActionForward deleteOnline(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		setAccessMode(request, form);

		return deleteFile(mapping, form, IToolContentHandler.TYPE_ONLINE,
				request);
	}

	public ActionForward deleteOffline(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		setAccessMode(request, form);

		return deleteFile(mapping, form, IToolContentHandler.TYPE_OFFLINE,
				request);
	}

	public ActionForward removeUnsavedOnline(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		setAccessMode(request, form);

		return removeUnsaved(mapping, form, IToolContentHandler.TYPE_ONLINE,
				request);
	}

	public ActionForward removeUnsavedOffline(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		setAccessMode(request, form);

		return removeUnsaved(mapping, form, IToolContentHandler.TYPE_OFFLINE,
				request);
	}

	/* ========== Private Methods ********** */

	private ActionForward uploadFile(ActionMapping mapping, ActionForm form,
			String type, HttpServletRequest request) {

		setAccessMode(request, form);

		AuthoringForm authForm = (AuthoringForm) form;

		// retrieving authoring session bean
		AuthoringSessionBean authSession = (AuthoringSessionBean) request
				.getSession().getAttribute(authForm.getAuthSessionId());

		// setting up variable to be used.
		FormFile file;
		List<ChatAttachment> unsavedFilesList;
		List<ChatAttachment> savedFilesList;
		if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
			file = (FormFile) authForm.getOfflineFile();
			unsavedFilesList = authSession.getUnsavedOfflineFilesList();
			savedFilesList = authSession.getOfflineFilesList();
		} else {
			file = (FormFile) authForm.getOnlineFile();
			unsavedFilesList = authSession.getUnsavedOnlineFilesList();
			savedFilesList = authSession.getOnlineFilesList();
		}

		// upload file to repository
		ChatAttachment newAttachment = chatService.uploadFileToContent(authForm
				.getToolContentID(), file, type);

		// Add attachment to unsavedFileList
		// checking to see if file with same name exists
		ChatAttachment currentAttachment;
		Iterator iter = savedFilesList.iterator();
		while (iter.hasNext()) {
			currentAttachment = (ChatAttachment) iter.next();
			if (StringUtils.equals(currentAttachment.getFileName(),
					newAttachment.getFileName())) {
				// move from this this list to deleted list.
				authSession.getDeletedFilesList().add(currentAttachment);
				iter.remove();
				break;
			}
		}
		unsavedFilesList.add(newAttachment);

		authForm.setAuthSession(authSession);
		request.setAttribute("unsavedChanges", new Boolean(true));

		return mapping.findForward("success");
	}

	private ActionForward deleteFile(ActionMapping mapping, ActionForm form,
			String type, HttpServletRequest request) {
		AuthoringForm authForm = (AuthoringForm) form;

		// retrieving authoring session bean
		AuthoringSessionBean authSession = (AuthoringSessionBean) request
				.getSession().getAttribute(authForm.getAuthSessionId());

		List fileList;
		if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
			fileList = authSession.getOfflineFilesList();
		} else {
			fileList = authSession.getOnlineFilesList();
		}

		Iterator iter = fileList.iterator();

		while (iter.hasNext()) {
			ChatAttachment att = (ChatAttachment) iter.next();

			if (att.getFileUuid().equals(authForm.getDeleteFileUuid())) {
				// move to delete file list, at next updateContent it will be
				// deleted
				authSession.getDeletedFilesList().add(att);

				// remove from this list
				iter.remove();
				break;
			}
		}

		authForm.setAuthSession(authSession);
		request.setAttribute("unsavedChanges", new Boolean(true));

		return mapping.findForward("success");
	}

	private ActionForward removeUnsaved(ActionMapping mapping, ActionForm form,
			String type, HttpServletRequest request) {
		AuthoringForm authForm = (AuthoringForm) form;

		// retrieving authoring session bean
		AuthoringSessionBean authSession = (AuthoringSessionBean) request
				.getSession().getAttribute(authForm.getAuthSessionId());

		List unsavedAttachments;

		if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
			unsavedAttachments = authSession.getUnsavedOfflineFilesList();
		} else {
			unsavedAttachments = authSession.getUnsavedOnlineFilesList();
		}

		Iterator iter = unsavedAttachments.iterator();
		while (iter.hasNext()) {
			ChatAttachment remAtt = (ChatAttachment) iter.next();

			if (remAtt.getFileUuid().equals(authForm.getDeleteFileUuid())) {
				// delete from repository
				chatService.deleteFromRepository(remAtt.getFileUuid(), remAtt
						.getFileVersionId());

				// remove from session list
				iter.remove();
				break;
			}
		}

		authForm.setAuthSession(authSession);
		request.setAttribute("unsavedChanges", new Boolean(true));

		return mapping.findForward("success");
	}

	/**
	 * Populates a Chat using inputs in AuthoringForm.
	 * 
	 * @param authForm
	 * @return
	 */
	private void populateChat(Chat chat, AuthoringForm authForm) {
		chat.setTitle(authForm.getTitle());
		chat.setInstructions(authForm.getInstructions());
		chat.setOfflineInstructions(authForm.getOnlineInstruction());
		chat.setOnlineInstructions(authForm.getOfflineInstruction());
		chat.setLockOnFinished(authForm.isLockOnFinished());
		chat.setFilteringEnabled(authForm.isFilteringEnabled());
		chat.setFilterKeywords(authForm.getFilterKeywords());
	}

	/**
	 * Populates the AuthoringForm with content from Chat
	 * 
	 * @param chat
	 * @param authForm
	 * @return
	 */
	private void populateAuthForm(AuthoringForm authForm, Chat chat) {
		authForm.setToolContentID(chat.getToolContentId());
		authForm.setTitle(chat.getTitle());
		authForm.setInstructions(chat.getInstructions());
		authForm.setOnlineInstruction(chat.getOnlineInstructions());
		authForm.setOfflineInstruction(chat.getOfflineInstructions());
		authForm.setLockOnFinished(chat.getLockOnFinished());
		authForm.setFilteringEnabled(chat.getFilteringEnabled());
		authForm.setFilterKeywords(chat.getFilterKeywords());
		// TODO add the rest.
	}

	private void resetAuthSession(AuthoringSessionBean authSession, Chat chat) {
		// clear the lists in session.
		authSession.getUnsavedOfflineFilesList().clear();
		authSession.getUnsavedOnlineFilesList().clear();
		authSession.getDeletedFilesList().clear();
		authSession.getOnlineFilesList().clear();
		authSession.getOfflineFilesList().clear();

		Iterator iter = chat.getChatAttachments().iterator();
		while (iter.hasNext()) {
			ChatAttachment attachment = (ChatAttachment) iter.next();
			String type = attachment.getFileType();
			if (type.equals(IToolContentHandler.TYPE_OFFLINE)) {
				authSession.getOfflineFilesList().add(attachment);
			}
			if (type.equals(IToolContentHandler.TYPE_ONLINE)) {
				authSession.getOnlineFilesList().add(attachment);
			}
		}
	}

	/**
	 * Get ToolAccessMode from HttpRequest parameters. Default value is AUTHOR
	 * mode.
	 * 
	 * @param request
	 * @return
	 */
	private ToolAccessMode getAccessMode(HttpServletRequest request) {
		ToolAccessMode mode;
		String modeStr = request.getParameter(AttributeNames.ATTR_MODE);
		if (StringUtils.equalsIgnoreCase(modeStr, ToolAccessMode.TEACHER
				.toString()))
			mode = ToolAccessMode.TEACHER;
		else
			mode = ToolAccessMode.AUTHOR;
		return mode;
	}

	/**
	 * Set the request attribute 'mode'using value stored in
	 * AuthoringSessionBean.
	 * 
	 * @param request
	 * @param form
	 */
	private void setAccessMode(HttpServletRequest request, ActionForm form) {
		AuthoringForm authForm = (AuthoringForm) form;
		AuthoringSessionBean authSession = (AuthoringSessionBean) request
				.getSession().getAttribute(authForm.getAuthSessionId());
		request.setAttribute(AttributeNames.ATTR_MODE, authSession.getMode()
				.toString());
	}
}