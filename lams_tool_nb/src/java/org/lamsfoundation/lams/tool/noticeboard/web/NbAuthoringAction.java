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

/* $$Id$$ */
package org.lamsfoundation.lams.tool.noticeboard.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.MessageResources;
import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.contentrepository.InvalidParameterException;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.tool.noticeboard.NbApplicationException;
import org.lamsfoundation.lams.tool.noticeboard.NoticeboardAttachment;
import org.lamsfoundation.lams.tool.noticeboard.NoticeboardConstants;
import org.lamsfoundation.lams.tool.noticeboard.NoticeboardContent;
import org.lamsfoundation.lams.tool.noticeboard.service.INoticeboardService;
import org.lamsfoundation.lams.tool.noticeboard.service.NoticeboardServiceProxy;
import org.lamsfoundation.lams.tool.noticeboard.util.NbWebUtil;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.FileValidatorUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;

/**
 * @author mtruong
 * 
 * <p>This class is a simple combination of NbAuthoringStarterAction and NbAuthoringAction.
 * It has been created for the purpose of supporting the new authoring page which is done using 
 * DHTML.</p>
 * 
 * <p>The unspecified method, is the same as the execute method for NbAuthoringStarterAction.
 * It will get called when the method parameter is not specified (that is on first entry
 * into the authoring environment).</p>
 * 
 * <p> The save, upload and delete method is the same as that of NbAuthoringAction, to see its explanation,
 * please see org.lamsfoundation.lams.tool.noticeboard.web.NbAuthoringAction </p>
 *
 * ----------------XDoclet Tags--------------------
 * 
 * @struts:action path="/authoring" name="NbAuthoringForm" scope="request"
 * 				  type="org.lamsfoundation.lams.tool.noticeboard.web.NbAuthoringAction"
 *                parameter="method" validate="true" input="/author_page.jsp"
 *
 * @struts:action-forward name="authoringContent" path="/authoring/authoring.jsp"
 * @struts:action-forward name="displayMessage" path=".message"
 * 
 * ----------------XDoclet Tags--------------------
 */

public class NbAuthoringAction extends LamsDispatchAction {
    static Logger logger = Logger.getLogger(NbAuthoringAction.class.getName());
    public final static String FORM="NbAuthoringForm";
    
    /** Get the user from the shared session */
	public UserDTO getUser(HttpServletRequest request) {
		// set up the user details
		HttpSession ss = SessionManager.getSession();
		UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		if ( user == null )
		{
		    MessageResources resources = getResources(request);
		    String error = resources.getMessage(NoticeboardConstants.ERR_MISSING_PARAM, "User");
		    logger.error(error);
			throw new NbApplicationException(error);
		}
		return user;
	}

    
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws NbApplicationException {
        
	    //to ensure that we are working with a new form, not one from previous session
		NbAuthoringForm nbForm = new NbAuthoringForm();		
		
		Long contentId = WebUtil.readLongParam(request, NoticeboardConstants.TOOL_CONTENT_ID);
		String contentFolderId = WebUtil.readStrParam(request, NoticeboardConstants.CONTENT_FOLDER_ID);
		
		nbForm.setToolContentID(contentId.toString());
		
		/* DefineLater is used in the basic screen. If defineLater is set, then in the authoring page,
		 * the two tabs {Advanced, Instructions} are not visible.
		 */
		nbForm.setDefineLater(request.getParameter(NoticeboardConstants.DEFINE_LATER));
							
		/*
		 * Retrieve the Service
		 */
		INoticeboardService nbService = NoticeboardServiceProxy.getNbService(getServlet().getServletContext());
		
		List attachmentList = null;
		if (!contentExists(nbService, contentId))
		{
			//	Pre-fill the form with the default content
			//NoticeboardContent nb =	nbService.retrieveNoticeboard(NoticeboardConstants.DEFAULT_CONTENT_ID);
		    Long defaultToolContentId = nbService.getToolDefaultContentIdBySignature(NoticeboardConstants.TOOL_SIGNATURE);
		  //  logger.debug("Default tool content id is " + defaultToolContentId);
		    NoticeboardContent nb = nbService.retrieveNoticeboard(defaultToolContentId);
			
		    if (nb==null)
		    {
		        String error= "There is data missing in the database";
		        logger.error(error);
		        throw new NbApplicationException(error);
		    }
			
			//initialise the values in the form, so the values will be shown in the jsp
			nbForm.setToolContentID(contentId.toString());
			nbForm.setContentFolderID(contentFolderId);
			nbForm.setTitle(nb.getTitle());
			nbForm.setContent(nb.getContent());
			nbForm.setOnlineInstructions(nb.getOnlineInstructions());
			nbForm.setOfflineInstructions(nb.getOfflineInstructions());
			
			attachmentList = NbWebUtil.setupAttachmentList(nbService,null);
		
		}
		else //content already exists on the database
		{
			//get the values from the database
			NoticeboardContent nb = nbService.retrieveNoticeboard(contentId);
			
			/* If retrieving existing content, check whether the contentInUse flag is set, if set, the
			 * author is not allowed to edit content 
			 */
			
			if (NbWebUtil.isContentEditable(nb))
			{
			    /* Define later set to true when the edit activity tab is brought up 
			     * So that users cannot start using the content while the staff member is editing the content */
			    nbForm.populateFormWithNbContentValues(nb);
			    nbForm.setContentFolderID(contentFolderId);
			    nb.setDefineLater(Boolean.parseBoolean(nbForm.getDefineLater()));
			    nbService.saveNoticeboard(nb);
			    
			    /** TODO: setup values in the instructions map */
			 
			}
			else
			{
			    //The contentInUse flag is set and a user has already reached this activity.
			    saveMessages(request, null); //ensure there are no existing messages
			    ActionMessages message = new ActionMessages();
			    message.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.contentInUseSet"));
			    saveMessages(request, message);
			    return mapping.findForward(NoticeboardConstants.DISPLAY_MESSAGE);
			    
			}
			
			//Setup the map containing the files that have been uploaded for this particular tool content id
			attachmentList = NbWebUtil.setupAttachmentList(nbService,nb);
		
		}
		
		SessionMap map = NbWebUtil.addUploadsToSession(null, request, attachmentList, NbWebUtil.setupDeletedAttachmentList());
		nbForm.setSessionMapID(map.getSessionID());
		
		request.setAttribute(FORM, nbForm);
		return mapping.findForward(NoticeboardConstants.AUTHOR_PAGE);
    }

	/**
	 * Checks the session to see if the title and content session variables exist or not.
	 * 
	 * @param session The HttpSession to check.
	 * @return true if the parameters title and content exists in the session, false otherwise
	 */
	private boolean contentExists(INoticeboardService service, Long id)
	{
		NoticeboardContent nb = service.retrieveNoticeboard(id);
		if (nb == null)
			return false;
		else
			return true;
		
	}
	
	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws NbApplicationException {
			
		NbAuthoringForm nbForm = (NbAuthoringForm)form;
		//copyAuthoringFormValuesIntoFormBean(request, nbForm);
		
		INoticeboardService nbService = NoticeboardServiceProxy.getNbService(getServlet().getServletContext());
		String idAsString = nbForm.getToolContentID();
		if (idAsString == null)
		{
		    MessageResources resources = getResources(request);
		    String error = resources.getMessage(NoticeboardConstants.ERR_MISSING_PARAM, "Tool Content Id");
		    logger.error(error);
			throw new NbApplicationException(error);
		}
		Long content_id = NbWebUtil.convertToLong(nbForm.getToolContentID());
		
		//throws exception if the content id does not exist
		checkContentId(content_id);
		
		NoticeboardContent nbContent = nbService.retrieveNoticeboard(content_id);
		if ( nbContent == null ) {
			//create a new noticeboard object 
			nbContent = new NoticeboardContent();
			nbContent.setNbContentId(content_id);
		}
		
		nbForm.copyValuesIntoNbContent(nbContent);
		if ( nbContent.getDateCreated() == null )
			nbContent.setDateCreated(nbContent.getDateUpdated()); 
		
		UserDTO user = getUser(request);
		nbContent.setCreatorUserId(new Long(user.getUserID().longValue()));
		
		// Author has finished editing the content and mark the defineLater flag to false
		nbContent.setDefineLater(false);
		nbService.saveNoticeboard(nbContent);
		
		// Save the attachments then update the attachment collections in the session.
		SessionMap sessionMap = getSessionMap(request, nbForm);
		List attachmentList = (List) sessionMap.get(NoticeboardConstants.ATTACHMENT_LIST);
		List deletedAttachmentList = (List) sessionMap.get(NoticeboardConstants.DELETED_ATTACHMENT_LIST);
		deletedAttachmentList = saveAttachments(nbService, nbContent, attachmentList, deletedAttachmentList, mapping, request);
		sessionMap = NbWebUtil.addUploadsToSession(sessionMap, request, attachmentList, deletedAttachmentList);
	
		request.setAttribute(AuthoringConstants.LAMS_AUTHORING_SUCCESS_FLAG,Boolean.TRUE);		    	
		return mapping.findForward(NoticeboardConstants.AUTHOR_PAGE);
		
	}
	  
	/** 
	* Go through the attachments collections. Remove any content repository or tool objects
	* matching entries in the the deletedAttachments collection, add any new attachments in the
	* attachments collection. Clear the deletedAttachments collection, ready for new editing.
	*/ 
  	private List saveAttachments (INoticeboardService nbService, NoticeboardContent nbContent, 
  			List attachmentList, List deletedAttachmentList,
  			ActionMapping mapping, HttpServletRequest request) {

  		if ( deletedAttachmentList != null ) {
  			Iterator iter = deletedAttachmentList.iterator();
  			while (iter.hasNext()) {
				NoticeboardAttachment attachment = (NoticeboardAttachment) iter.next();
				
		    	try
		    	{
		    	// remove tool entry from db, does not removing entry from the content repository
		    	// deleting a non-existent entry shouldn't cause any errors.
					if ( attachment.getAttachmentId() != null ) {
						nbService.removeAttachment(nbContent, attachment);
					}
		    	} catch (RepositoryCheckedException e) {
		            logger.error("Unable to delete file",e);
		    		ActionMessages am = new ActionMessages(); 
		    		am.add( ActionMessages.GLOBAL_MESSAGE,  
		    	           new ActionMessage( NoticeboardConstants.ERROR_FILE_UPLOAD_CONTENT_REPOSITORY , 
		    	        		   			  attachment.getFilename())); 
		    		saveErrors( request, am );
		    	}
  			}
  			deletedAttachmentList.clear();
  		}
  		
  		if ( attachmentList != null ) {
  			Iterator iter = attachmentList.iterator();
  			while (iter.hasNext()) {
				NoticeboardAttachment attachment = (NoticeboardAttachment) iter.next();

				if ( attachment.getAttachmentId() == null ) {
					// add entry to tool table - file already in content repository
					nbService.saveAttachment(nbContent, attachment);
				}
  			}
  		}
  			
  		return deletedAttachmentList;
  	}
	  	
    /**
	 * This method will either upload an online instructions file or an offline instructions file. 
	 * It will upload an online file if the bean property onlineFile is not null and similarly,
	 * will upload an offline file if the bean property offlineFile is not null.
	 * By using the term "upload", we are saving the file information on the local database (?)
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws NbApplicationException
	 */
	public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
		throws InvalidParameterException, FileNotFoundException, RepositoryCheckedException, IOException, NbApplicationException {
	    		
	    
	    	//set up the values in the map
	    	//call the uploadFile method from toolContentHandler
	    	NbAuthoringForm nbForm = (NbAuthoringForm)form;
	    	//copyAuthoringFormValuesIntoFormBean(request, nbForm);
	    	FormFile theFile;
	    	INoticeboardService nbService = NoticeboardServiceProxy.getNbService(getServlet().getServletContext());
			
	    	Long content_id = NbWebUtil.convertToLong(nbForm.getToolContentID());
			//throws exception if the content id does not exist
			checkContentId(content_id);
			NoticeboardContent nbContent = nbService.retrieveNoticeboard(content_id);
			
			//validate file max size
			ActionMessages errors = new ActionMessages();
			FileValidatorUtil.validateFileSize(nbForm.getOnlineFile(), true, errors );
			FileValidatorUtil.validateFileSize(nbForm.getOfflineFile(), true, errors );
			if(!errors.isEmpty()){
				this.saveErrors(request, errors);
				return mapping.findForward(NoticeboardConstants.AUTHOR_PAGE);
			}
			
	    	//check if the file uploaded is an online instructions file or offline instructions file.
	    	//if one of the types is null, then the other one must have been uploaded. 
	    	//here we check if the file is an online one
			
			if ((nbForm.getOnlineFile() != null && (nbForm.getOnlineFile().getFileName().trim().length() != 0)) ||
			        (nbForm.getOfflineFile() != null && (nbForm.getOfflineFile().getFileName().trim().length() != 0)))
			{
		    	boolean isOnlineFile = ((nbForm.getOnlineFile() != null && (nbForm.getOnlineFile().getFileName().trim().length() != 0)) ? true: false );
		    	theFile = (isOnlineFile ? nbForm.getOnlineFile() : nbForm.getOfflineFile());
		    	String fileType = isOnlineFile ? IToolContentHandler.TYPE_ONLINE : IToolContentHandler.TYPE_OFFLINE;

		    	SessionMap map = getSessionMap(request, nbForm);
				List attachmentList = (List) map.get(NoticeboardConstants.ATTACHMENT_LIST);
		    	List deletedAttachmentList = (List) map.get(NoticeboardConstants.DELETED_ATTACHMENT_LIST);
		    	
		    	// if a file with the same name already exists then move the old one to deleted
		    	deletedAttachmentList = moveToDelete(theFile.getFileName(), isOnlineFile, attachmentList, deletedAttachmentList );
		    	
		    	try
		    	{
	    	        // This is a new file and so is saved to the content repository. Add it to the 
		    		// attachments collection, but don't add it to the tool's tables yet.
		    		NodeKey node = nbService.uploadFile(theFile.getInputStream(), theFile.getFileName(), theFile.getContentType(), fileType);
	    	        NoticeboardAttachment file = new NoticeboardAttachment();
		    	    file.setFilename(theFile.getFileName());
		    	   	file.setOnlineFile(isOnlineFile);
			    	file.setNbContent(nbContent);
			    	file.setUuid(node.getUuid());
			    	file.setVersionId(node.getVersion()); 
			    	
			    	// add the files to the attachment collection - if one existed, it should have already been removed.
		    	    attachmentList.add(file);
			    	map = NbWebUtil.addUploadsToSession(map, request, attachmentList, deletedAttachmentList);
			    	
			    	//reset the fields so that more files can be uploaded
			    	nbForm.setOfflineFile(null);
			    	nbForm.setOnlineFile(null);
		    	}
		    	catch (FileNotFoundException e) {
		            logger.error("Unable to uploadfile",e);
		            throw new NbApplicationException("Unable to upload file, exception was "+e.getMessage());
		    	} catch (IOException e) {
		            logger.error("Unable to uploadfile",e);
		            throw new NbApplicationException("Unable to upload file, exception was "+e.getMessage());
		    	} catch (RepositoryCheckedException e) {
		            logger.error("Unable to uploadfile",e);
		            throw new NbApplicationException("Unable to upload file, exception was "+e.getMessage());
		    	}			    	
			}
	  
			nbForm.setMethod(NoticeboardConstants.INSTRUCTIONS);
			
			return mapping.findForward(NoticeboardConstants.AUTHOR_PAGE);
	}
		
		/** If this file exists in attachments list, move it to the deleted attachments list.
		 * Returns the updated deletedAttachments list, creating a new one if needed. Uses the filename 
		 * and isOnline flag to match up the attachment entry */
		private List moveToDelete(String filename, boolean isOnline, List attachmentsList, List deletedAttachmentsList ) {
			return moveToDelete(filename, isOnline, null, attachmentsList, deletedAttachmentsList);
		}
		/** If this file exists in attachments list, move it to the deleted attachments list.
		 * Returns the updated deletedAttachments list, creating a new one if needed. Uses the uuid of the
		 * file to match up the attachment entry */
		private List moveToDelete(Long uuid, List attachmentsList, List deletedAttachmentsList ) {
			return moveToDelete(null, false, uuid, attachmentsList, deletedAttachmentsList);
		}
		
		/** If this file exists in attachments map, move it to the deleted attachments map.
		 * Returns the updated deletedAttachments map, creating a new one if needed. If uuid supplied
		 * then tries to match on that, otherwise uses filename and isOnline. */
		private List moveToDelete(String filename, boolean isOnline, Long uuid, List attachmentsList, List deletedAttachmentsList ) {

			List deletedList = deletedAttachmentsList != null ? deletedAttachmentsList : NbWebUtil.setupDeletedAttachmentList();
			
			if ( attachmentsList != null ) {
				Iterator iter = attachmentsList.iterator();
				NoticeboardAttachment attachment = null;
				while ( iter.hasNext() && attachment == null ) {
					NoticeboardAttachment value = (NoticeboardAttachment) iter.next();
					
					if ( uuid != null ) {
						// compare using uuid
						if ( uuid.equals(value.getUuid()) ) {
							attachment = value;
						}
					} else {
						// compare using filename and online/offline flag
						if ( value.isOnlineFile() == isOnline && value.getFilename().equals(filename) ) {
							attachment = value;
						}
					}
				}
				if ( attachment != null ) {
					deletedList.add(attachment);
					attachmentsList.remove(attachment);
				}
			}
			
			return deletedList;
		}
		
		public ActionForward deleteAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws InvalidParameterException, RepositoryCheckedException, NbApplicationException {
		
	    	//set up the values in the map
	    	NbAuthoringForm nbForm = (NbAuthoringForm)form;

	    	Long uuid = nbForm.getDeleteFileUuid();
	    	if (uuid == null)
	    	{
	    	    String error = "Unable to continue. The file uuid is missing.";
				logger.error(error);
				throw new NbApplicationException(error);
	    	}
	    	
	    	// move the file's details from the attachment collection to the deleted attachments collection
	    	// the attachment will be delete on saving.
		    SessionMap map = getSessionMap(request, nbForm);
			List attachmentList = (List) map.get(NoticeboardConstants.ATTACHMENT_LIST);
	    	List deletedAttachmentList = (List) map.get(NoticeboardConstants.DELETED_ATTACHMENT_LIST);
	    	deletedAttachmentList = moveToDelete(uuid, attachmentList, deletedAttachmentList );
	    	
	    	nbForm.setMethod(NoticeboardConstants.INSTRUCTIONS);
		
		   	return mapping.findForward(NoticeboardConstants.AUTHOR_PAGE);
		}
		

		/**
		 * It is assumed that the contentId is passed as a http parameter
		 * if the contentId is null, an exception is thrown, otherwise proceed as normal
		 * 
		 * @param contentId the <code>toolContentId</code> to check
		 */
		private void checkContentId(Long contentId)
		{
		    if (contentId == null)
			{
				String error = "Unable to continue. Tool content id missing.";
				
				throw new NbApplicationException(error);
			}
		}
		
		/**
		 * Retrieve the SessionMap from the HttpSession.
		 * 
		 * @param request
		 * @param authForm
		 * @return
		 */
		private SessionMap getSessionMap(HttpServletRequest request, NbAuthoringForm nbForm) {
			return (SessionMap) request.getSession().getAttribute(nbForm.getSessionMapID());
		}
		
		/**
		 * This method copies the values of the request parameters <code>richTextOnlineInstructions</code>
		 * <code>richTextOfflineInstructions</code> <code>richTextContent</code> into the form properties
		 * onlineInstructions, offlineInstructions and content respectively.
		 * 
		 * @param request HttpServlet request
		 * @param form The ActionForm class containing data submitted by the forms.
		 */
	/*	private void copyAuthoringFormValuesIntoFormBean(HttpServletRequest request, NbAuthoringForm form)
		{
		    String onlineInstruction = WebUtil.readStrParam(request, NoticeboardConstants.RICH_TEXT_ONLINE_INSTRN, true);
		    String offlineInstruction = WebUtil.readStrParam(request, NoticeboardConstants.RICH_TEXT_OFFLINE_INSTRN, true);
		  	String content = WebUtil.readStrParam(request, NoticeboardConstants.RICH_TEXT_CONTENT, true);
		  	String title = WebUtil.readStrParam(request, NoticeboardConstants.RICH_TEXT_TITLE, true);

		  	
		  	    form.setTitle(title);
		  	
		  	    form.setContent(content);
		  	
		        form.setOnlineInstructions(onlineInstruction);
		  	
		  	    form.setOfflineInstructions(offlineInstruction);
		  
		}  */
		
	/*	private void copyFormValuesIntoNbContent(HttpServletRequest request, NoticeboardContent nbContent)
		{
		    nbContent.setTitle((String)request.getParameter(NoticeboardConstants.RICH_TEXT_TITLE));
		    nbContent.setContent((String)request.getParameter(NoticeboardConstants.RICH_TEXT_CONTENT));
		    nbContent.setOnlineInstructions((String)request.getParameter(NoticeboardConstants.RICH_TEXT_ONLINE_INSTRN));
		    nbContent.setOfflineInstructions((String)request.getParameter(NoticeboardConstants.RICH_TEXT_OFFLINE_INSTRN));
		    
		} */
		
}	
	
