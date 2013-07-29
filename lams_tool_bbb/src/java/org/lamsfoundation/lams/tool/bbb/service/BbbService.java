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

package org.lamsfoundation.lams.tool.bbb.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
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
import org.lamsfoundation.lams.contentrepository.service.SimpleCredentials;
import org.lamsfoundation.lams.learning.service.ILearnerService;
import org.lamsfoundation.lams.learningdesign.service.ExportToolContentException;
import org.lamsfoundation.lams.learningdesign.service.IExportToolContentService;
import org.lamsfoundation.lams.learningdesign.service.ImportToolContentException;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.ICoreNotebookService;
import org.lamsfoundation.lams.tool.ToolContentManager;
import org.lamsfoundation.lams.tool.ToolOutput;
import org.lamsfoundation.lams.tool.ToolOutputDefinition;
import org.lamsfoundation.lams.tool.ToolSessionExportOutputData;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.bbb.dao.IBbbAttachmentDAO;
import org.lamsfoundation.lams.tool.bbb.dao.IBbbConfigDAO;
import org.lamsfoundation.lams.tool.bbb.dao.IBbbDAO;
import org.lamsfoundation.lams.tool.bbb.dao.IBbbSessionDAO;
import org.lamsfoundation.lams.tool.bbb.dao.IBbbUserDAO;
import org.lamsfoundation.lams.tool.bbb.model.Bbb;
import org.lamsfoundation.lams.tool.bbb.model.BbbAttachment;
import org.lamsfoundation.lams.tool.bbb.model.BbbConfig;
import org.lamsfoundation.lams.tool.bbb.model.BbbSession;
import org.lamsfoundation.lams.tool.bbb.model.BbbUser;
import org.lamsfoundation.lams.tool.bbb.util.BbbException;
import org.lamsfoundation.lams.tool.bbb.util.BbbToolContentHandler;
import org.lamsfoundation.lams.tool.bbb.util.BbbUtil;
import org.lamsfoundation.lams.tool.bbb.util.Constants;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.SessionDataExistsException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.service.ILamsToolService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.audit.IAuditService;

/**
 * An implementation of the IBbbService interface.
 * 
 * As a requirement, all LAMS tool's service bean must implement ToolContentManager and ToolSessionManager.
 */

public class BbbService implements ToolSessionManager, ToolContentManager, IBbbService {

    private static final Logger logger = Logger.getLogger(BbbService.class);

    private IBbbDAO bbbDAO = null;

    private IBbbSessionDAO bbbSessionDAO = null;

    private IBbbUserDAO bbbUserDAO = null;

    private IBbbAttachmentDAO bbbAttachmentDAO = null;

    private IBbbConfigDAO bbbConfigDAO = null;

    private ILearnerService learnerService;

    private ILamsToolService toolService;

    private IToolContentHandler bbbToolContentHandler = null;

    private IRepositoryService repositoryService = null;

    private IAuditService auditService = null;

    private IExportToolContentService exportContentService;

    private ICoreNotebookService coreNotebookService;

    public BbbService() {
	super();
	// TODO Auto-generated constructor stub
    }

    /* Methods from ToolSessionManager */
    public void createToolSession(Long toolSessionId, String toolSessionName, Long toolContentId) throws ToolException {
	if (logger.isDebugEnabled()) {
	    logger.debug("entering method createToolSession:" + " toolSessionId = " + toolSessionId
		    + " toolSessionName = " + toolSessionName + " toolContentId = " + toolContentId);
	}

	BbbSession session = new BbbSession();
	session.setSessionId(toolSessionId);
	session.setSessionName(toolSessionName);
	// learner starts
	Bbb bbb = getBbbByContentId(toolContentId);
	session.setBbb(bbb);
	bbbSessionDAO.insertOrUpdate(session);
    }

    public String leaveToolSession(Long toolSessionId, Long learnerId) throws DataMissingException, ToolException {
	return learnerService.completeToolSession(toolSessionId, learnerId);
    }

    public ToolSessionExportOutputData exportToolSession(Long toolSessionId) throws DataMissingException, ToolException {
	// TODO Auto-generated method stub
	return null;
    }

    @SuppressWarnings("unchecked")
    public ToolSessionExportOutputData exportToolSession(List toolSessionIds) throws DataMissingException,
	    ToolException {
	// TODO Auto-generated method stub
	return null;
    }

    public void removeToolSession(Long toolSessionId) throws DataMissingException, ToolException {
	bbbSessionDAO.deleteByProperty(BbbSession.class, "sessionId", toolSessionId);
	// TODO check if cascade worked
    }

    /**
     * Get the tool output for the given tool output names.
     * 
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#getToolOutput(java.util .List<String>, java.lang.Long,
     *      java.lang.Long)
     */
    public SortedMap<String, ToolOutput> getToolOutput(List<String> names, Long toolSessionId, Long learnerId) {
	return new TreeMap<String, ToolOutput>();
    }

    /**
     * Get the tool output for the given tool output name.
     * 
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#getToolOutput(java.lang.String, java.lang.Long,
     *      java.lang.Long)
     */
    public ToolOutput getToolOutput(String name, Long toolSessionId, Long learnerId) {
	return null;
    }

    /* Methods from ToolContentManager */

    public void copyToolContent(Long fromContentId, Long toContentId) throws ToolException {

	if (logger.isDebugEnabled()) {
	    logger.debug("entering method copyToolContent:" + " fromContentId=" + fromContentId + " toContentId="
		    + toContentId);
	}

	if (toContentId == null) {
	    String error = "Failed to copy tool content: toContentID is null";
	    throw new ToolException(error);
	}

	Bbb fromContent = null;
	if (fromContentId != null) {
	    fromContent = getBbbByContentId(fromContentId);
	}
	if (fromContent == null) {
	    // create the fromContent using the default tool content
	    fromContent = getDefaultContent();
	}
	Bbb toContent = Bbb.newInstance(fromContent, toContentId, bbbToolContentHandler);
	saveOrUpdateBbb(toContent);
    }

    public void setAsDefineLater(Long toolContentId, boolean value) throws DataMissingException, ToolException {
	Bbb bbb = getBbbByContentId(toolContentId);
	if (bbb == null) {
	    throw new ToolException("Could not find tool with toolContentID: " + toolContentId);
	}
	bbb.setDefineLater(value);
	saveOrUpdateBbb(bbb);
    }

    public void setAsRunOffline(Long toolContentId, boolean value) throws DataMissingException, ToolException {
	Bbb bbb = getBbbByContentId(toolContentId);
	if (bbb == null) {
	    throw new ToolException("Could not find tool with toolContentID: " + toolContentId);
	}
	bbb.setRunOffline(value);
	saveOrUpdateBbb(bbb);
    }

    public void removeToolContent(Long toolContentId, boolean removeSessionData) throws SessionDataExistsException,
	    ToolException {
	// TODO Auto-generated method stub
    }

    /**
     * Export the XML fragment for the tool's content, along with any files needed for the content.
     * 
     * @throws DataMissingException
     *             if no tool content matches the toolSessionId
     * @throws ToolException
     *             if any other error occurs
     */

    public void exportToolContent(Long toolContentId, String rootPath) throws DataMissingException, ToolException {
	Bbb bbb = getBbbByContentId(toolContentId);
	if (bbb == null) {
	    bbb = getDefaultContent();
	}
	if (bbb == null)
	    throw new DataMissingException("Unable to find default content for the bbb tool");

	// set ResourceToolContentHandler as null to avoid copy file node in
	// repository again.
	bbb = Bbb.newInstance(bbb, toolContentId, null);
	bbb.setToolContentHandler(null);
	bbb.setBbbSessions(null);
	Set<BbbAttachment> atts = bbb.getBbbAttachments();
	for (BbbAttachment att : atts) {
	    att.setBbb(null);
	}
	try {
	    exportContentService.registerFileClassForExport(BbbAttachment.class.getName(), "fileUuid",
		    "fileVersionId");
	    exportContentService.exportToolContent(toolContentId, bbb, bbbToolContentHandler, rootPath);
	} catch (ExportToolContentException e) {
	    throw new ToolException(e);
	}
    }

    /**
     * Import the XML fragment for the tool's content, along with any files needed for the content.
     * 
     * @throws ToolException
     *             if any other error occurs
     */
    public void importToolContent(Long toolContentId, Integer newUserUid, String toolContentPath, String fromVersion,
	    String toVersion) throws ToolException {
	try {
	    exportContentService.registerFileClassForImport(BbbAttachment.class.getName(), "fileUuid",
		    "fileVersionId", "fileName", "fileType", null, null);

	    Object toolPOJO = exportContentService.importToolContent(toolContentPath, bbbToolContentHandler,
		    fromVersion, toVersion);
	    if (!(toolPOJO instanceof Bbb))
		throw new ImportToolContentException("Import Bbb tool content failed. Deserialized object is "
			+ toolPOJO);
	    Bbb bbb = (Bbb) toolPOJO;

	    // reset it to new toolContentId
	    bbb.setToolContentId(toolContentId);
	    bbb.setCreateBy(new Long(newUserUid.longValue()));

	    saveOrUpdateBbb(bbb);
	} catch (ImportToolContentException e) {
	    throw new ToolException(e);
	}
    }

    public Class[] getSupportedToolOutputDefinitionClasses(int definitionType) {
	return null;
    }
    
    /**
     * Get the definitions for possible output for an activity, based on the toolContentId. These may be definitions
     * that are always available for the tool (e.g. number of marks for Multiple Choice) or a custom definition created
     * for a particular activity such as the answer to the third question contains the word Koala and hence the need for
     * the toolContentId
     * 
     * @return SortedMap of ToolOutputDefinitions with the key being the name of each definition
     */
    public SortedMap<String, ToolOutputDefinition> getToolOutputDefinitions(Long toolContentId, int definitionType)
	    throws ToolException {
	return new TreeMap<String, ToolOutputDefinition>();
    }

    public String getToolContentTitle(Long toolContentId) {
	return getBbbByContentId(toolContentId).getTitle();
    }
    
    /* IBbbService Methods */

    public Long createNotebookEntry(Long id, Integer idType, String signature, Integer userID, String entry) {
	return coreNotebookService.createNotebookEntry(id, idType, signature, userID, "", entry);
    }

    public NotebookEntry getEntry(Long id, Integer idType, String signature, Integer userID) {

	List<NotebookEntry> list = coreNotebookService.getEntry(id, idType, signature, userID);
	if (list == null || list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0);
	}
    }

    public NotebookEntry getNotebookEntry(Long uid) {
	return coreNotebookService.getEntry(uid);
    }

    public void updateNotebookEntry(Long uid, String entry) {
	coreNotebookService.updateEntry(uid, "", entry);
    }

    public void updateNotebookEntry(NotebookEntry notebookEntry) {
	coreNotebookService.updateEntry(notebookEntry);
    }

    public Long getDefaultContentIdBySignature(String toolSignature) {
	Long toolContentId = null;
	toolContentId = new Long(toolService.getToolDefaultContentIdBySignature(toolSignature));
	if (toolContentId == null) {
	    String error = "Could not retrieve default content id for this tool";
	    logger.error(error);
	    throw new BbbException(error);
	}
	return toolContentId;
    }

    public Bbb getDefaultContent() {
	Long defaultContentID = getDefaultContentIdBySignature(Constants.TOOL_SIGNATURE);
	Bbb defaultContent = getBbbByContentId(defaultContentID);
	if (defaultContent == null) {
	    String error = "Could not retrieve default content record for this tool";
	    logger.error(error);
	    throw new BbbException(error);
	}
	return defaultContent;
    }

    public Bbb copyDefaultContent(Long newContentID) {

	if (newContentID == null) {
	    String error = "Cannot copy the Bbb tools default content: + " + "newContentID is null";
	    logger.error(error);
	    throw new BbbException(error);
	}

	Bbb defaultContent = getDefaultContent();
	// create new bbb using the newContentID
	Bbb newContent = new Bbb();
	newContent = Bbb.newInstance(defaultContent, newContentID, bbbToolContentHandler);
	saveOrUpdateBbb(newContent);
	return newContent;
    }

    @SuppressWarnings("unchecked")
    public Bbb getBbbByContentId(Long toolContentID) {
	List<Bbb> list = bbbDAO.findByProperty(Bbb.class, "toolContentId", toolContentID);
	if (list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0);
	}
    }

    @SuppressWarnings("unchecked")
    public BbbSession getSessionBySessionId(Long toolSessionId) {
	List<BbbSession> list = bbbSessionDAO.findByProperty(BbbSession.class, "sessionId", toolSessionId);
	if (list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0);
	}
    }

    @SuppressWarnings("unchecked")
    public BbbUser getUserByUserIdAndSessionId(Long userId, Long toolSessionId) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("userId", userId);
	map.put("bbbSession.sessionId", toolSessionId);
	List<BbbUser> list = bbbUserDAO.findByProperties(BbbUser.class, map);
	if (list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0);
	}
    }

    @SuppressWarnings("unchecked")
    public BbbUser getUserByUID(Long uid) {
	List<BbbUser> list = bbbUserDAO.findByProperty(BbbUser.class, "uid", uid);
	if (list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0);
	}
    }

    public BbbAttachment uploadFileToContent(Long toolContentId, FormFile file, String type) {
	if (file == null || StringUtils.isEmpty(file.getFileName()))
	    throw new BbbException("Could not find upload file: " + file);

	NodeKey nodeKey = processFile(file, type);

	BbbAttachment attachment = new BbbAttachment(nodeKey.getVersion(), type, file.getFileName(), nodeKey
		.getUuid(), new Date());
	return attachment;
    }

    public void deleteFromRepository(Long uuid, Long versionID) throws BbbException {
	ITicket ticket = getRepositoryLoginTicket();
	try {
	    repositoryService.deleteVersion(ticket, uuid, versionID);
	} catch (Exception e) {
	    throw new BbbException("Exception occured while deleting files from" + " the repository "
		    + e.getMessage());
	}
    }

    public String getJoinMeetingURL(UserDTO userDTO, String meetingKey, String password) throws Exception {

	// Get Bbb details
	String serverURL = getConfigValue(Constants.CFG_SERVER_URL);
	String securitySalt = getConfigValue(Constants.CFG_SECURITYSALT); 
	// Get Join parameter
	String joinParam = Constants.BBB_JOIN_PARAM;
	
	if (serverURL == null) {
	    logger.error("Config item : '" + Constants.CFG_SERVER_URL + "' not defined");
	    throw new BbbException("Server url not defined");
	}

	String queryString = "fullName=" 
		+ URLEncoder.encode(userDTO.getFirstName() + " " + userDTO.getLastName(), "UTF8")
		+ "&meetingID=" 
		+ URLEncoder.encode(meetingKey, "UTF8") 
		+ "&password="
		+ URLEncoder.encode(password, "UTF8"); 
	
	String checkSum = DigestUtils.shaHex("join" + queryString + securitySalt);	
	
	String url = serverURL + joinParam + queryString + "&checksum=" + checkSum;

	return url;
    } 
    
   public Boolean isMeetingRunning(String meetingKey) throws Exception {
		String serverURL = getConfigValue(Constants.CFG_SERVER_URL);
		String securitySalt = getConfigValue(Constants.CFG_SECURITYSALT);
		String meetingRunning = Constants.BBB_MEETING_RUNNING_PARAM;
		
		String queryString = "meetingID=" 
			+ URLEncoder.encode(meetingKey, "UTF8");
		
		String checkSum = DigestUtils.shaHex("isMeetingRunning" + queryString + securitySalt);
		
		URL url;
		url = new URL(serverURL 
				+ meetingRunning 
				+ queryString 
				+ "&checksum=" 
				+ URLEncoder.encode(checkSum, "UTF8"));

		logger.debug("isMeetingRunningURL=" + url);
		
		String response;
		response = sendRequest(url);
		
		if (response.contains("true")) {
			return true;
		} else {
			return false;
		}
    	
    }
   
    
    public String startConference(String meetingKey, String atendeePassword, 
    		String moderatorPassword, String returnURL,
    		String welcomeMessage)
	    throws Exception {

	String serverURL = getConfigValue(Constants.CFG_SERVER_URL);
	String securitySalt = getConfigValue(Constants.CFG_SECURITYSALT);
	String createParam = Constants.BBB_CREATE_PARAM;
	
	if (serverURL == null) {
	    logger.error("Config item : '" + Constants.CFG_SERVER_URL + "' not defined");
	    throw new BbbException("Standard server url not defined");
	}
	
	String queryString = "name=" 
	+ URLEncoder.encode(meetingKey, "UTF8") + "&meetingID=" 
	+ URLEncoder.encode(meetingKey, "UTF8") + "&attendeePW="
	+ URLEncoder.encode(atendeePassword, "UTF8") + "&moderatorPW=" 
	+ URLEncoder.encode(moderatorPassword, "UTF8") + "&logoutURL="
	+ URLEncoder.encode(returnURL, "UTF8") + "&welcome="
	+ URLEncoder.encode(welcomeMessage, "UTF8");

	logger.debug("queryString = " + queryString);
	
	String checkSum = DigestUtils.shaHex("create" + queryString + securitySalt);

	logger.debug("checksum = " + checkSum);
		
	URL url;
	    url = new URL(serverURL 
	    		+ createParam 
	    		+ queryString 
	    		+ "&checksum="
	    		+ URLEncoder.encode(checkSum, "UTF8"));

	    logger.info("url = " + url);
   	    
	String response;
	response = sendRequest(url);
	
	if (BbbUtil.getResponse(response) == Constants.RESPONSE_SUCCESS) {
		return Constants.RESPONSE_SUCCESS;
	} else {
	    logger.error("BBB returns fail when creating a meeting room");
	    throw new BbbException("Standard server url not defined");

	}
    }
    
    public void saveOrUpdateBbb(Bbb bbb) {
	bbbDAO.insertOrUpdate(bbb);
    }

    public void saveOrUpdateBbbSession(BbbSession bbbSession) {
	bbbSessionDAO.insertOrUpdate(bbbSession);
    }

    public void saveOrUpdateBbbUser(BbbUser bbbUser) {
	bbbUserDAO.insertOrUpdate(bbbUser);
    }

    public BbbUser createBbbUser(UserDTO user, BbbSession bbbSession) {
	BbbUser bbbUser = new BbbUser(user, bbbSession);
	saveOrUpdateBbbUser(bbbUser);
	return bbbUser;
    }

    @SuppressWarnings("unchecked")
    public BbbConfig getConfig(String key) {
	List<BbbConfig> list = (List<BbbConfig>) bbbConfigDAO.findByProperty(BbbConfig.class, "key", key);
	if (list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0);
	}
    }

    @SuppressWarnings("unchecked")
    public String getConfigValue(String key) {
	List<BbbConfig> list = (List<BbbConfig>) bbbConfigDAO.findByProperty(BbbConfig.class, "key", key);
	if (list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0).getValue();
	}
    }

    public void saveOrUpdateConfigEntry(BbbConfig bbbConfig) {
	bbbConfigDAO.insertOrUpdate(bbbConfig);
    }

    public IAuditService getAuditService() {
	return auditService;
    }

    public void setAuditService(IAuditService auditService) {
	this.auditService = auditService;
    }

    private String sendRequest(URL url) throws IOException {

	if (logger.isDebugEnabled()) {
	    logger.debug("request = " + url);
	}

	URLConnection connection = url.openConnection();

	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	String response = "";
	String line = "";

	while ((line = in.readLine()) != null)
	    response += line;
	in.close();

	if (logger.isDebugEnabled()) {
	    logger.debug("response = " + response);
	}

	return response;
    }

    private NodeKey processFile(FormFile file, String type) {
	NodeKey node = null;
	if (file != null && !StringUtils.isEmpty(file.getFileName())) {
	    String fileName = file.getFileName();
	    try {
		node = getBbbToolContentHandler().uploadFile(file.getInputStream(), fileName, file.getContentType(),
			type);
	    } catch (InvalidParameterException e) {
		throw new BbbException("InvalidParameterException occured while trying to upload File"
			+ e.getMessage());
	    } catch (FileNotFoundException e) {
		throw new BbbException("FileNotFoundException occured while trying to upload File" + e.getMessage());
	    } catch (RepositoryCheckedException e) {
		throw new BbbException("RepositoryCheckedException occured while trying to upload File"
			+ e.getMessage());
	    } catch (IOException e) {
		throw new BbbException("IOException occured while trying to upload File" + e.getMessage());
	    }
	}
	return node;
    }

    /**
     * This method verifies the credentials of the Tool and gives it the <code>Ticket</code> to login and
     * access the Content Repository.
     * 
     * A valid ticket is needed in order to access the content from the repository. This method would be called every time
     * the tool needs to upload/download files from the content repository.
     * 
     * @return ITicket The ticket for repository access
     * @throws SubmitFilesException
     */
    private ITicket getRepositoryLoginTicket() throws BbbException {
	ICredentials credentials = new SimpleCredentials(BbbToolContentHandler.repositoryUser,
		BbbToolContentHandler.repositoryId);
	try {
	    ITicket ticket = repositoryService.login(credentials, BbbToolContentHandler.repositoryWorkspaceName);
	    return ticket;
	} catch (AccessDeniedException ae) {
	    throw new BbbException("Access Denied to repository." + ae.getMessage());
	} catch (WorkspaceNotFoundException we) {
	    throw new BbbException("Workspace not found." + we.getMessage());
	} catch (LoginException e) {
	    throw new BbbException("Login failed." + e.getMessage());
	}
    }

    /**
     * Set the description, throws away the title value as this is not supported in 2.0
     */
    public void setReflectiveData(Long toolContentId, String title, String description) throws ToolException,
	    DataMissingException {

	logger
		.warn("Setting the reflective field on a bbb. This doesn't make sense as the bbb is for reflection and we don't reflect on reflection!");
	Bbb bbb = getBbbByContentId(toolContentId);
	if (bbb == null) {
	    throw new DataMissingException("Unable to set reflective data titled " + title
		    + " on activity toolContentId " + toolContentId + " as the tool content does not exist.");
	}

	bbb.setReflectOnActivity(Boolean.TRUE);
	bbb.setReflectInstructions(description);
    }

    // =========================================================================================
    /* Used by Spring to "inject" the linked objects */

    public IBbbAttachmentDAO getBbbAttachmentDAO() {
	return bbbAttachmentDAO;
    }

    public void setBbbAttachmentDAO(IBbbAttachmentDAO attachmentDAO) {
	this.bbbAttachmentDAO = attachmentDAO;
    }

    public IBbbDAO getBbbDAO() {
	return bbbDAO;
    }

    public void setBbbDAO(IBbbDAO bbbDAO) {
	this.bbbDAO = bbbDAO;
    }

    public IToolContentHandler getBbbToolContentHandler() {
	return bbbToolContentHandler;
    }

    public void setBbbToolContentHandler(IToolContentHandler bbbToolContentHandler) {
	this.bbbToolContentHandler = bbbToolContentHandler;
    }

    public IBbbSessionDAO getBbbSessionDAO() {
	return bbbSessionDAO;
    }

    public void setBbbSessionDAO(IBbbSessionDAO sessionDAO) {
	this.bbbSessionDAO = sessionDAO;
    }

    public IBbbConfigDAO getBbbConfigDAO() {
	return bbbConfigDAO;
    }

    public void setBbbConfigDAO(IBbbConfigDAO bbbConfigDAO) {
	this.bbbConfigDAO = bbbConfigDAO;
    }

    public ILamsToolService getToolService() {
	return toolService;
    }

    public void setToolService(ILamsToolService toolService) {
	this.toolService = toolService;
    }

    public IBbbUserDAO getBbbUserDAO() {
	return bbbUserDAO;
    }

    public void setBbbUserDAO(IBbbUserDAO userDAO) {
	this.bbbUserDAO = userDAO;
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

    public void setExportContentService(IExportToolContentService exportContentService) {
	this.exportContentService = exportContentService;
    }

    public ICoreNotebookService getCoreNotebookService() {
	return coreNotebookService;
    }

    public void setCoreNotebookService(ICoreNotebookService coreNotebookService) {
	this.coreNotebookService = coreNotebookService;
    }
    
    public IRepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(IRepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }


}
