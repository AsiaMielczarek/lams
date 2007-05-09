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
/* $$Id$ */

package org.lamsfoundation.lams.tool.scribe.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.contentrepository.AccessDeniedException;
import org.lamsfoundation.lams.contentrepository.ICredentials;
import org.lamsfoundation.lams.contentrepository.ITicket;
import org.lamsfoundation.lams.contentrepository.InvalidParameterException;
import org.lamsfoundation.lams.contentrepository.LoginException;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.WorkspaceNotFoundException;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.contentrepository.service.IRepositoryService;
import org.lamsfoundation.lams.contentrepository.service.RepositoryProxy;
import org.lamsfoundation.lams.contentrepository.service.SimpleCredentials;
import org.lamsfoundation.lams.learning.service.ILearnerService;
import org.lamsfoundation.lams.learningdesign.service.ExportToolContentException;
import org.lamsfoundation.lams.learningdesign.service.IExportToolContentService;
import org.lamsfoundation.lams.learningdesign.service.ImportToolContentException;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.ICoreNotebookService;
import org.lamsfoundation.lams.tool.ToolContentImport102Manager;
import org.lamsfoundation.lams.tool.ToolContentManager;
import org.lamsfoundation.lams.tool.ToolSessionExportOutputData;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.SessionDataExistsException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.scribe.dao.IScribeAttachmentDAO;
import org.lamsfoundation.lams.tool.scribe.dao.IScribeDAO;
import org.lamsfoundation.lams.tool.scribe.dao.IScribeHeadingDAO;
import org.lamsfoundation.lams.tool.scribe.dao.IScribeSessionDAO;
import org.lamsfoundation.lams.tool.scribe.dao.IScribeUserDAO;
import org.lamsfoundation.lams.tool.scribe.model.Scribe;
import org.lamsfoundation.lams.tool.scribe.model.ScribeAttachment;
import org.lamsfoundation.lams.tool.scribe.model.ScribeHeading;
import org.lamsfoundation.lams.tool.scribe.model.ScribeReportEntry;
import org.lamsfoundation.lams.tool.scribe.model.ScribeSession;
import org.lamsfoundation.lams.tool.scribe.model.ScribeUser;
import org.lamsfoundation.lams.tool.scribe.util.ScribeConstants;
import org.lamsfoundation.lams.tool.scribe.util.ScribeException;
import org.lamsfoundation.lams.tool.scribe.util.ScribeToolContentHandler;
import org.lamsfoundation.lams.tool.service.ILamsToolService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.util.audit.IAuditService;
import org.lamsfoundation.lams.util.wddx.WDDXProcessor;
import org.lamsfoundation.lams.util.wddx.WDDXProcessorConversionException;

/**
 * An implementation of the IScribeService interface.
 * 
 * As a requirement, all LAMS tool's service bean must implement
 * ToolContentManager and ToolSessionManager.
 */

public class ScribeService implements ToolSessionManager, ToolContentManager, ToolContentImport102Manager,
		IScribeService {

	static Logger logger = Logger.getLogger(ScribeService.class.getName());

	private IScribeDAO scribeDAO = null;

	private IScribeSessionDAO scribeSessionDAO = null;
	
	private IScribeHeadingDAO scribeHeadingDAO = null;

	private IScribeUserDAO scribeUserDAO = null;

	private IScribeAttachmentDAO scribeAttachmentDAO = null;

	private ILearnerService learnerService;

	private ILamsToolService toolService;

	private IToolContentHandler scribeToolContentHandler = null;

	private IRepositoryService repositoryService = null;

	private IAuditService auditService = null;

	private IExportToolContentService exportContentService;

	private ICoreNotebookService coreNotebookService;
	
	public ScribeService() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* ************ Methods from ToolSessionManager ************* */
	public void createToolSession(Long toolSessionId, String toolSessionName,
			Long toolContentId) throws ToolException {
		if (logger.isDebugEnabled()) {
			logger.debug("entering method createToolSession:"
					+ " toolSessionId = " + toolSessionId
					+ " toolSessionName = " + toolSessionName
					+ " toolContentId = " + toolContentId);
		}

		ScribeSession session = new ScribeSession();
		session.setSessionId(toolSessionId);
		session.setSessionName(toolSessionName);
		Scribe scribe = scribeDAO.getByContentId(toolContentId);
		session.setScribe(scribe);
		
		session.setForceComplete(false);
		session.setReportSubmitted(false);
		scribeSessionDAO.saveOrUpdate(session);
	}

	public String leaveToolSession(Long toolSessionId, Long learnerId)
			throws DataMissingException, ToolException {
		return learnerService.completeToolSession(toolSessionId, learnerId);
	}

	public ToolSessionExportOutputData exportToolSession(Long toolSessionId)
			throws DataMissingException, ToolException {
		// TODO Auto-generated method stub
		return null;
	}

	public ToolSessionExportOutputData exportToolSession(List toolSessionIds)
			throws DataMissingException, ToolException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeToolSession(Long toolSessionId)
			throws DataMissingException, ToolException {
		scribeSessionDAO.deleteBySessionID(toolSessionId);
		//TODO check if cascade worked
	}

	/* ************ Methods from ToolContentManager ************************* */

	public void copyToolContent(Long fromContentId, Long toContentId)
			throws ToolException {

		if (logger.isDebugEnabled()) {
			logger.debug("entering method copyToolContent:" + " fromContentId="
					+ fromContentId + " toContentId=" + toContentId);
		}

		if (toContentId == null) {
			String error = "Failed to copy tool content: toContentID is null";
			throw new ToolException(error);
		}

		Scribe fromContent = null;
		if ( fromContentId != null ) {
			fromContent = scribeDAO.getByContentId(fromContentId);
		}
		if (fromContent == null) {
			// create the fromContent using the default tool content
			fromContent = getDefaultContent();
		}
		Scribe toContent = Scribe.newInstance(fromContent, toContentId,
				scribeToolContentHandler);
		scribeDAO.saveOrUpdate(toContent);
	}

	public void setAsDefineLater(Long toolContentId, boolean value)
			throws DataMissingException, ToolException {
		Scribe scribe = scribeDAO.getByContentId(toolContentId);
		if(scribe == null){
			throw new ToolException("Could not find tool with toolContentID: " + toolContentId);
		}
		scribe.setDefineLater(value);
		scribeDAO.saveOrUpdate(scribe);
	}

	public void setAsRunOffline(Long toolContentId, boolean value)
			throws DataMissingException, ToolException {
		Scribe scribe = scribeDAO.getByContentId(toolContentId);
		if(scribe == null){
			throw new ToolException("Could not find tool with toolContentID: " + toolContentId);
		}
		scribe.setRunOffline(value);
		scribeDAO.saveOrUpdate(scribe);
	}

	public void removeToolContent(Long toolContentId, boolean removeSessionData)
			throws SessionDataExistsException, ToolException {
		// TODO Auto-generated method stub
	}

	/**
	 * Export the XML fragment for the tool's content, along with any files
	 * needed for the content.
	 * 
	 * @throws DataMissingException
	 *             if no tool content matches the toolSessionId
	 * @throws ToolException
	 *             if any other error occurs
	 */

	public void exportToolContent(Long toolContentId, String rootPath)
			throws DataMissingException, ToolException {
		Scribe scribe = scribeDAO.getByContentId(toolContentId);
		if (scribe == null) {
			scribe = getDefaultContent();
		}
		if (scribe == null)
 			throw new DataMissingException("Unable to find default content for the scribe tool");

		// set ResourceToolContentHandler as null to avoid copy file node in
		// repository again.
		scribe = Scribe.newInstance(scribe, toolContentId, null);
		scribe.setToolContentHandler(null);
		scribe.setScribeSessions(null);
		// wipe out the links from ScribeAttachments, ScribeHeading back to Scribe, or it will try to 
		// include the hibernate object version of the Scribe within the XML
		Set<ScribeAttachment> atts = scribe.getScribeAttachments();
		for (ScribeAttachment att : atts) {
			att.setScribe(null);
		}
		Set<ScribeHeading> headings = scribe.getScribeHeadings();
		for (ScribeHeading heading : headings) {
			heading.setScribe(null);
		}
		try {
			exportContentService
					.registerFileClassForExport(ScribeAttachment.class.getName(),
							"fileUuid", "fileVersionId");
			exportContentService.exportToolContent(toolContentId,
					scribe, scribeToolContentHandler, rootPath);
		} catch (ExportToolContentException e) {
			throw new ToolException(e);
		}
	}

	/**
	 * Import the XML fragment for the tool's content, along with any files
	 * needed for the content.
	 * 
	 * @throws ToolException
	 *             if any other error occurs
	 */
	public void importToolContent(Long toolContentId, Integer newUserUid,
			String toolContentPath,String fromVersion,String toVersion) throws ToolException {
		try {
			exportContentService.registerFileClassForImport(
					ScribeAttachment.class.getName(), "fileUuid",
					"fileVersionId", "fileName", "fileType", null, null);

			Object toolPOJO = exportContentService.importToolContent(
					toolContentPath, scribeToolContentHandler,fromVersion,toVersion);
			if (!(toolPOJO instanceof Scribe))
				throw new ImportToolContentException(
						"Import Scribe tool content failed. Deserialized object is "
								+ toolPOJO);
			Scribe scribe = (Scribe) toolPOJO;

			// reset it to new toolContentId
			scribe.setToolContentId(toolContentId);
			scribe.setCreateBy(new Long(newUserUid.longValue()));

			scribeDAO.saveOrUpdate(scribe);
		} catch (ImportToolContentException e) {
			throw new ToolException(e);
		}
	}

	/* ********** IScribeService Methods ************************************** */

	public void createReportEntry(Long toolSessionId){
		// creating scribeReports for each heading and add to session.
		ScribeSession session = scribeSessionDAO.getBySessionId(toolSessionId);
		
		//these heading report already copied from content, the skipit.
		Set entries = session.getScribeReportEntries();
		if(entries != null && entries.size() > 0)
			return;
		
		Scribe scribe = session.getScribe();
		Set reports = session.getScribeReportEntries();
		if(reports == null){
			reports = new HashSet();
			session.setScribeReportEntries(reports);
		}
		for (Iterator iter = scribe.getScribeHeadings().iterator(); iter.hasNext();) {
			ScribeHeading heading = (ScribeHeading) iter.next();
			
			ScribeReportEntry report = new ScribeReportEntry();
			report.setScribeHeading(heading);
			
			reports.add(report);
		}
		scribeSessionDAO.update(session);

	}
	public void deleteHeadingReport(Long uid) {
		scribeHeadingDAO.deleteReport(uid);
		
	}
	public Long getDefaultContentIdBySignature(String toolSignature) {
		Long toolContentId = null;
		toolContentId = new Long(toolService
				.getToolDefaultContentIdBySignature(toolSignature));
		if (toolContentId == null) {
			String error = "Could not retrieve default content id for this tool";
			logger.error(error);
			throw new ScribeException(error);
		}
		return toolContentId;
	}

	public Scribe getDefaultContent() {
		Long defaultContentID = getDefaultContentIdBySignature(ScribeConstants.TOOL_SIGNATURE);
		Scribe defaultContent = getScribeByContentId(defaultContentID);
		if (defaultContent == null) {
			String error = "Could not retrieve default content record for this tool";
			logger.error(error);
			throw new ScribeException(error);
		}
		return defaultContent;
	}

	public Scribe copyDefaultContent(Long newContentID) {

		if (newContentID == null) {
			String error = "Cannot copy the Scribe tools default content: + "
					+ "newContentID is null";
			logger.error(error);
			throw new ScribeException(error);
		}

		Scribe defaultContent = getDefaultContent();
		// create new scribe using the newContentID
		Scribe newContent = new Scribe();
		newContent = Scribe.newInstance(defaultContent, newContentID,
				scribeToolContentHandler);
		scribeDAO.saveOrUpdate(newContent);
		return newContent;
	}

	public Scribe getScribeByContentId(Long toolContentID) {
		Scribe scribe = (Scribe) scribeDAO.getByContentId(toolContentID);
		if (scribe == null) {
			logger.debug("Could not find the content with toolContentID:"
					+ toolContentID);
		}
		return scribe;
	}

	public ScribeSession getSessionBySessionId(Long toolSessionId) {
		ScribeSession scribeSession = scribeSessionDAO.getBySessionId(toolSessionId);
		if (scribeSession == null) {
			logger.debug("Could not find the scribe session with toolSessionID:"
					+ toolSessionId);
		}
		return scribeSession;
	}

	public ScribeUser getUserByUserIdAndSessionId(Long userId, Long toolSessionId) {
		return scribeUserDAO.getByUserIdAndSessionId(userId, toolSessionId);
	}

	public ScribeUser getUserByLoginNameAndSessionId(String loginName,
			Long toolSessionId) {
		return scribeUserDAO.getByLoginNameAndSessionId(loginName, toolSessionId);
	}

	public ScribeUser getUserByUID(Long uid) {
		return scribeUserDAO.getByUID(uid);
	}

	public ScribeAttachment uploadFileToContent(Long toolContentId,
			FormFile file, String type) {
		if (file == null || StringUtils.isEmpty(file.getFileName()))
			throw new ScribeException("Could not find upload file: " + file);

		NodeKey nodeKey = processFile(file, type);

		ScribeAttachment attachment = new ScribeAttachment();
		attachment.setFileType(type);
		attachment.setFileUuid(nodeKey.getUuid());
		attachment.setFileVersionId(nodeKey.getVersion());
		attachment.setFileName(file.getFileName());

		return attachment;
	}

	public void deleteFromRepository(Long uuid, Long versionID)
			throws ScribeException {
		ITicket ticket = getRepositoryLoginTicket();
		try {
			repositoryService.deleteVersion(ticket, uuid, versionID);
		} catch (Exception e) {
			throw new ScribeException(
					"Exception occured while deleting files from"
							+ " the repository " + e.getMessage());
		}
	}

	public void deleteInstructionFile(Long contentID, Long uuid,
			Long versionID, String type) {
		scribeDAO.deleteInstructionFile(contentID, uuid, versionID, type);

	}

	public void saveOrUpdateScribe(Scribe scribe) {
		scribeDAO.saveOrUpdate(scribe);
	}

	public void saveOrUpdateScribeSession(ScribeSession scribeSession) {
		scribeSessionDAO.saveOrUpdate(scribeSession);
	}

	public void saveOrUpdateScribeUser(ScribeUser scribeUser) {
		scribeUserDAO.saveOrUpdate(scribeUser);
	}

	public ScribeUser createScribeUser(UserDTO user,
			ScribeSession scribeSession) {
		ScribeUser scribeUser = new ScribeUser(user, scribeSession);
		saveOrUpdateScribeUser(scribeUser);
		return scribeUser;
	}

	public IAuditService getAuditService() {
		return auditService;
	}

	public void setAuditService(IAuditService auditService) {
		this.auditService = auditService;
	}

	/* ********** Private methods ********** */
	
	private NodeKey processFile(FormFile file, String type) {
		NodeKey node = null;
		if (file != null && !StringUtils.isEmpty(file.getFileName())) {
			String fileName = file.getFileName();
			try {
				node = getScribeToolContentHandler().uploadFile(
						file.getInputStream(), fileName, file.getContentType(),
						type);
			} catch (InvalidParameterException e) {
				throw new ScribeException(
						"InvalidParameterException occured while trying to upload File"
								+ e.getMessage());
			} catch (FileNotFoundException e) {
				throw new ScribeException(
						"FileNotFoundException occured while trying to upload File"
								+ e.getMessage());
			} catch (RepositoryCheckedException e) {
				throw new ScribeException(
						"RepositoryCheckedException occured while trying to upload File"
								+ e.getMessage());
			} catch (IOException e) {
				throw new ScribeException(
						"IOException occured while trying to upload File"
								+ e.getMessage());
			}
		}
		return node;
	}

	/**
	 * This method verifies the credentials of the SubmitFiles Tool and gives it
	 * the <code>Ticket</code> to login and access the Content Repository.
	 * 
	 * A valid ticket is needed in order to access the content from the
	 * repository. This method would be called evertime the tool needs to
	 * upload/download files from the content repository.
	 * 
	 * @return ITicket The ticket for repostory access
	 * @throws SubmitFilesException
	 */
	private ITicket getRepositoryLoginTicket() throws ScribeException {
		repositoryService = RepositoryProxy.getRepositoryService();
		ICredentials credentials = new SimpleCredentials(
				ScribeToolContentHandler.repositoryUser,
				ScribeToolContentHandler.repositoryId);
		try {
			ITicket ticket = repositoryService.login(credentials,
					ScribeToolContentHandler.repositoryWorkspaceName);
			return ticket;
		} catch (AccessDeniedException ae) {
			throw new ScribeException("Access Denied to repository."
					+ ae.getMessage());
		} catch (WorkspaceNotFoundException we) {
			throw new ScribeException("Workspace not found." + we.getMessage());
		} catch (LoginException e) {
			throw new ScribeException("Login failed." + e.getMessage());
		}
	}

	/* ********** Used by Spring to "inject" the linked objects ************* */

	public IScribeAttachmentDAO getScribeAttachmentDAO() {
		return scribeAttachmentDAO;
	}

	public void setScribeAttachmentDAO(IScribeAttachmentDAO attachmentDAO) {
		this.scribeAttachmentDAO = attachmentDAO;
	}

	public IScribeDAO getScribeDAO() {
		return scribeDAO;
	}

	public void setScribeDAO(IScribeDAO scribeDAO) {
		this.scribeDAO = scribeDAO;
	}

	public IToolContentHandler getScribeToolContentHandler() {
		return scribeToolContentHandler;
	}

	public void setScribeToolContentHandler(
			IToolContentHandler scribeToolContentHandler) {
		this.scribeToolContentHandler = scribeToolContentHandler;
	}

	public IScribeSessionDAO getScribeSessionDAO() {
		return scribeSessionDAO;
	}

	public void setScribeSessionDAO(IScribeSessionDAO sessionDAO) {
		this.scribeSessionDAO = sessionDAO;
	}

	public ILamsToolService getToolService() {
		return toolService;
	}

	public void setToolService(ILamsToolService toolService) {
		this.toolService = toolService;
	}

	public IScribeUserDAO getScribeUserDAO() {
		return scribeUserDAO;
	}

	public void setScribeUserDAO(IScribeUserDAO userDAO) {
		this.scribeUserDAO = userDAO;
	}

	public ILearnerService getLearnerService() {
		return learnerService;
	}

	public void setLearnerService(ILearnerService learnerService) {
		this.learnerService = learnerService;
	}

	public IExportToolContentService getExportContentService() {
		return exportContentService;
	}

	public void setExportContentService(
			IExportToolContentService exportContentService) {
		this.exportContentService = exportContentService;
	}
	
	public ICoreNotebookService getCoreNotebookService() {
		return coreNotebookService;
	}

	public void setCoreNotebookService(ICoreNotebookService coreNotebookService) {
		this.coreNotebookService = coreNotebookService;
	}

	public Long createNotebookEntry(Long id, Integer idType, String signature,
			Integer userID, String entry) {
		return coreNotebookService.createNotebookEntry(id, idType, signature, userID, "", entry);
	}

	public NotebookEntry getEntry(Long id, Integer idType, String signature,
			Integer userID) {
		
		List<NotebookEntry> list = coreNotebookService.getEntry(id, idType, signature, userID);
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
	
	/* ===============Methods implemented from ToolContentImport102Manager =============== */
	

    /**
     * Import the data for a 1.0.2 Scribe
     */
    public void import102ToolContent(Long toolContentId, UserDTO user, Hashtable importValues)
    {
    	Date now = new Date();
    	Scribe scribe = new Scribe();
    	scribe.setContentInUse(Boolean.FALSE);
    	scribe.setCreateBy(new Long(user.getUserID().longValue()));
    	scribe.setCreateDate(now);
    	scribe.setDefineLater(Boolean.FALSE);
    	scribe.setInstructions(null);
    	scribe.setOfflineInstructions(null);
    	scribe.setOnlineInstructions(null);
    	scribe.setReflectInstructions(null);
    	scribe.setReflectOnActivity(Boolean.FALSE);
    	scribe.setRunOffline(Boolean.FALSE);
    	scribe.setTitle((String)importValues.get(ToolContentImport102Manager.CONTENT_TITLE));
    	scribe.setToolContentId(toolContentId);
    	scribe.setUpdateDate(now);
    	scribe.setAutoSelectScribe(true);
    	
    	try {
    		Boolean isReusable = WDDXProcessor.convertToBoolean(importValues, ToolContentImport102Manager.CONTENT_REUSABLE);
    		scribe.setLockOnFinished(isReusable != null ? ! isReusable.booleanValue() : true);
       	} catch (WDDXProcessorConversionException e) {
       		logger.error("Unable to content for activity "+scribe.getTitle()+"properly due to a WDDXProcessorConversionException.",e);
    		throw new ToolException("Invalid import data format for activity "+scribe.getTitle()+"- WDDX caused an exception. Some data from the design will have been lost. See log for more details.");
    	}

	    	String headingList = (String)importValues.get(ToolContentImport102Manager.CONTENT_BODY);
    	if (headingList != null && headingList.length()>0) {
    		String[] headings = headingList.split("\\^");
			Set<ScribeHeading> set = new HashSet<ScribeHeading>();
    		for (int i=0; i < headings.length ; i++) {
    			ScribeHeading sHeading = new ScribeHeading();
    			sHeading.setDisplayOrder(i);
    			sHeading.setHeadingText(WebUtil.convertNewlines(headings[i]));
    			sHeading.setScribe(scribe);
				set.add(sHeading);
    		}
			scribe.setScribeHeadings(set);
    	}
    	
    	// leave as empty, no need to set them to anything.
    	//setScribeAttachments(Set scribeAttachments);
    	//setScribeSessions(Set scribeSessions);
    	scribeDAO.saveOrUpdate(scribe);
    }

    /** Set the description, throws away the title value as this is not supported in 2.0 */
    public void setReflectiveData(Long toolContentId, String title, String description) 
    		throws ToolException, DataMissingException {
    	
    	Scribe scribe = getScribeByContentId(toolContentId);
    	if ( scribe == null ) {
    		throw new DataMissingException("Unable to set reflective data titled "+title
	       			+" on activity toolContentId "+toolContentId
	       			+" as the tool content does not exist.");
    	}
    	
    	scribe.setReflectOnActivity(Boolean.TRUE);
    	scribe.setReflectInstructions(description);
    }
    //=========================================================================================

	public void setScribeHeadingDAO(IScribeHeadingDAO scribeHeadingDAO) {
		this.scribeHeadingDAO = scribeHeadingDAO;
	}
}