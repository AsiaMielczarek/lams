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
package org.lamsfoundation.lams.tool.forum.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

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
import org.lamsfoundation.lams.events.IEventNotificationService;
import org.lamsfoundation.lams.gradebook.service.IGradebookService;
import org.lamsfoundation.lams.learning.service.ILearnerService;
import org.lamsfoundation.lams.learningdesign.service.ExportToolContentException;
import org.lamsfoundation.lams.learningdesign.service.IExportToolContentService;
import org.lamsfoundation.lams.learningdesign.service.ImportToolContentException;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.ICoreNotebookService;
import org.lamsfoundation.lams.tool.ToolContentImport102Manager;
import org.lamsfoundation.lams.tool.ToolContentManager;
import org.lamsfoundation.lams.tool.ToolOutput;
import org.lamsfoundation.lams.tool.ToolOutputDefinition;
import org.lamsfoundation.lams.tool.ToolSessionExportOutputData;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.SessionDataExistsException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.forum.dto.MessageDTO;
import org.lamsfoundation.lams.tool.forum.persistence.Attachment;
import org.lamsfoundation.lams.tool.forum.persistence.AttachmentDao;
import org.lamsfoundation.lams.tool.forum.persistence.Forum;
import org.lamsfoundation.lams.tool.forum.persistence.ForumCondition;
import org.lamsfoundation.lams.tool.forum.persistence.ForumDao;
import org.lamsfoundation.lams.tool.forum.persistence.ForumException;
import org.lamsfoundation.lams.tool.forum.persistence.ForumReport;
import org.lamsfoundation.lams.tool.forum.persistence.ForumReportDAO;
import org.lamsfoundation.lams.tool.forum.persistence.ForumToolSession;
import org.lamsfoundation.lams.tool.forum.persistence.ForumToolSessionDao;
import org.lamsfoundation.lams.tool.forum.persistence.ForumUser;
import org.lamsfoundation.lams.tool.forum.persistence.ForumUserDao;
import org.lamsfoundation.lams.tool.forum.persistence.Message;
import org.lamsfoundation.lams.tool.forum.persistence.MessageDao;
import org.lamsfoundation.lams.tool.forum.persistence.MessageSeq;
import org.lamsfoundation.lams.tool.forum.persistence.MessageSeqDao;
import org.lamsfoundation.lams.tool.forum.persistence.PersistenceException;
import org.lamsfoundation.lams.tool.forum.persistence.Timestamp;
import org.lamsfoundation.lams.tool.forum.persistence.TimestampDao;
import org.lamsfoundation.lams.tool.forum.util.DateComparator;
import org.lamsfoundation.lams.tool.forum.util.ForumConstants;
import org.lamsfoundation.lams.tool.forum.util.ForumToolContentHandler;
import org.lamsfoundation.lams.tool.forum.util.TopicComparator;
import org.lamsfoundation.lams.tool.service.ILamsToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.util.audit.IAuditService;
import org.lamsfoundation.lams.util.wddx.WDDXProcessor;
import org.lamsfoundation.lams.util.wddx.WDDXProcessorConversionException;

/**
 * 
 * @author Steve.Ni
 * 
 * @version $Revision$
 */
public class ForumService implements IForumService, ToolContentManager, ToolSessionManager, ToolContentImport102Manager {
    private static final Logger log = Logger.getLogger(ForumService.class);

    // DAO variables
    private ForumDao forumDao;

    private AttachmentDao attachmentDao;

    private MessageDao messageDao;

    private TimestampDao timestampDao;

    private MessageSeqDao messageSeqDao;

    private ForumUserDao forumUserDao;

    private ForumToolSessionDao forumToolSessionDao;

    private ForumReportDAO forumReportDAO;

    // system level handler and service
    private ILamsToolService toolService;

    private ForumToolContentHandler forumToolContentHandler;

    private IRepositoryService repositoryService;

    private ILearnerService learnerService;

    private IAuditService auditService;

    private MessageService messageService;

    private IExportToolContentService exportContentService;

    private IUserManagementService userManagementService;

    private ICoreNotebookService coreNotebookService;

    private ForumOutputFactory forumOutputFactory;

    private IGradebookService gradebookService;

    private IEventNotificationService eventNotificationService;
    private Random generator = new Random();

    // ---------------------------------------------------------------------
    // Inversion of Control Methods - Method injection
    // ---------------------------------------------------------------------
    public void setAuditService(IAuditService auditService) {
	this.auditService = auditService;
    }

    public IAuditService getAuditService() {
	return auditService;
    }

    public void setMessageService(MessageService messageService) {
	this.messageService = messageService;
    }

    public ForumOutputFactory getForumOutputFactory() {
	return forumOutputFactory;
    }

    public void setForumOutputFactory(ForumOutputFactory forumOutputFactory) {
	this.forumOutputFactory = forumOutputFactory;
    }

    public Forum updateForum(Forum forum) throws PersistenceException {
	forumDao.saveOrUpdate(forum);
	return forum;
    }

    public Forum getForum(Long forumUid) throws PersistenceException {
	return forumDao.getById(forumUid);
    }

    public Forum getForumByContentId(Long contentID) throws PersistenceException {
	return forumDao.getByContentId(contentID);
    }

    public void deleteForumAttachment(Long attachmentId) throws PersistenceException {
	Attachment attachment = attachmentDao.getById(attachmentId);
	attachmentDao.delete(attachment);
    }

    public Message createRootTopic(Long forumId, Long sessionId, Message message) throws PersistenceException {
	return createRootTopic(forumId, getSessionBySessionId(sessionId), message);
    }

    public Message createRootTopic(Long forumId, ForumToolSession session, Message message) throws PersistenceException {
	// get Forum and ForumToolSesion
	if (message.getForum() == null) {
	    Forum forum = forumDao.getById(forumId);
	    message.setForum(forum);
	}
	// if topic created by author, session will be null.
	if (session != null) {
	    message.setToolSession(session);
	}

	if (message.getUid() == null) {
	    // update message sequence
	    MessageSeq msgSeq = new MessageSeq();
	    msgSeq.setMessage(message);
	    msgSeq.setMessageLevel((short) 0);
	    // set itself as root
	    msgSeq.setRootMessage(message);
	    messageSeqDao.save(msgSeq);
	}

	// if this message had any cloned objects, they also need to be changed.
	// this will only happen if an authored topic is changed via monitoring
	if (message.getSessionClones().size() > 0) {
	    Iterator iter = message.getSessionClones().iterator();
	    while (iter.hasNext()) {
		Message clone = (Message) iter.next();
		message.updateClone(clone);
	    }
	}

	// create message in database
	messageDao.saveOrUpdate(message);

	return message;
    }

    public Message updateTopic(Message message) throws PersistenceException {

	// update message
	messageDao.saveOrUpdate(message);

	// udate root message's lastReply date if this message
	// if this message is root message, then actually, it will update itself lastReplayDate
	MessageSeq msgSeq = messageSeqDao.getByTopicId(message.getUid());
	Message root = msgSeq.getRootMessage();
	// update reply date
	// messageDao.saveOrUpdate(root); // do not update date of root posting

	return message;
    }

    public void updateReport(ForumReport report) {
	forumReportDAO.saveObject(report);
    }

    public Message updateMessageHideFlag(Long messageId, boolean hideFlag) {

	Message message = getMessage(messageId);
	if (message != null) {
	    Long userId = 0L;
	    String loginName = "Default";
	    if (message.getCreatedBy() == null) {
		userId = message.getCreatedBy().getUserId();
		loginName = message.getCreatedBy().getLoginName();
	    }
	    if (hideFlag) {
		auditService.logHideEntry(ForumConstants.TOOL_SIGNATURE, userId, loginName, message.toString());
	    } else {
		auditService.logShowEntry(ForumConstants.TOOL_SIGNATURE, userId, loginName, message.toString());
	    }

	    message.setHideFlag(hideFlag);

	    // update message
	    messageDao.update(message);
	}
	return message;
    }

    public Message getMessage(Long messageUid) throws PersistenceException {
	return messageDao.getById(messageUid);
    }

    public void deleteTopic(Long topicUid) throws PersistenceException {
	List children = messageDao.getChildrenTopics(topicUid);
	// cascade delete children topic by recursive
	if (children != null) {
	    Iterator iter = children.iterator();
	    while (iter.hasNext()) {
		Message msg = (Message) iter.next();
		this.deleteTopic(msg.getUid());
	    }
	}
	messageSeqDao.deleteByTopicId(topicUid);
	messageDao.delete(topicUid);
    }

    public void deleteCondition(ForumCondition condition) throws PersistenceException {
	forumDao.deleteCondition(condition);
    }

    public Message replyTopic(Long parentId, Long sessionId, Message replyMessage) throws PersistenceException {
	// set parent
	Message parent = this.getMessage(parentId);
	replyMessage.setParent(parent);
	replyMessage.setForum(parent.getForum());
	// parent sessionID maybe empty if created by author role. So given sessionId is exactly value.
	ForumToolSession session = getSessionBySessionId(sessionId);
	replyMessage.setToolSession(session);
	messageDao.saveOrUpdate(replyMessage);

	// get root topic and create record in MessageSeq table
	MessageSeq parentSeq = messageSeqDao.getByTopicId(parent.getUid());
	if (parentSeq == null) {
	    ForumService.log.error("Message Sequence table is broken becuase topic " + parent
		    + " can not get Sequence Record");
	}
	Message root = parentSeq.getRootMessage();
	MessageSeq msgSeq = new MessageSeq();
	msgSeq.setMessage(replyMessage);
	msgSeq.setMessageLevel((short) (parentSeq.getMessageLevel() + 1));
	msgSeq.setRootMessage(root);
	messageSeqDao.save(msgSeq);

	// update last reply date for root messagegetlas
	root.setLastReplyDate(new Date());
	// update reply message number for root
	root.setReplyNumber(root.getReplyNumber() + 1);
	// messageDao.saveOrUpdate(root); // do not update the date of root posting

	return replyMessage;
    }

    public Attachment uploadInstructionFile(FormFile uploadFile, String fileType) throws PersistenceException {
	if (uploadFile == null || StringUtils.isEmpty(uploadFile.getFileName())) {
	    throw new ForumException("Could not find upload file: " + uploadFile);
	}

	// upload file to repository
	NodeKey nodeKey = processFile(uploadFile, fileType);

	// create new attachement
	Attachment file = new Attachment();
	file.setFileType(fileType);
	file.setFileUuid(nodeKey.getUuid());
	file.setFileVersionId(nodeKey.getVersion());
	file.setFileName(uploadFile.getFileName());
	file.setCreated(new Date());

	return file;

    }

    /**
     * This method deletes the content with the given <code>uuid</code> and <code>versionID</code> from the content
     * repository
     * 
     * @param uuid
     *                The <code>uuid</code> of the node to be deleted
     * @param versionID
     *                The <code>version_id</code> of the node to be deleted.
     * @throws SubmitFilesException
     */
    public void deleteFromRepository(Long uuid, Long versionID) throws ForumException {
	ITicket ticket = getRepositoryLoginTicket();
	try {
	    repositoryService.deleteVersion(ticket, uuid, versionID);
	} catch (Exception e) {
	    throw new ForumException("Exception occured while deleting files from" + " the repository "
		    + e.getMessage());
	}
    }

    public Attachment uploadAttachment(FormFile uploadFile) throws PersistenceException {
	if (uploadFile == null || StringUtils.isEmpty(uploadFile.getFileName())) {
	    throw new ForumException("Could not find upload file: " + uploadFile);
	}

	NodeKey nodeKey = processFile(uploadFile, IToolContentHandler.TYPE_ONLINE);
	Attachment file = new Attachment();
	file.setFileType(IToolContentHandler.TYPE_ONLINE);
	file.setFileUuid(nodeKey.getUuid());
	file.setFileVersionId(nodeKey.getVersion());
	file.setFileName(uploadFile.getFileName());

	return file;
    }

    public List getTopicThread(Long rootTopicId) {

	List unsortedThread = messageSeqDao.getTopicThread(rootTopicId);
	Iterator iter = unsortedThread.iterator();
	MessageSeq msgSeq;
	SortedMap<MessageSeq, Message> map = new TreeMap<MessageSeq, Message>(new TopicComparator());
	while (iter.hasNext()) {
	    msgSeq = (MessageSeq) iter.next();
	    map.put(msgSeq, msgSeq.getMessage());
	}
	return getSortedMessageDTO(map);
    }

    public List getRootTopics(Long sessionId) {
	List topicsBySession = messageDao.getRootTopics(sessionId);
	ForumToolSession session = getSessionBySessionId(sessionId);
	if (session == null || session.getForum() == null) {
	    ForumService.log.error("Failed on getting session by given sessionID:" + sessionId);
	    throw new ForumException("Failed on getting session by given sessionID:" + sessionId);
	}

	// sorted by last post date
	Message msg;
	SortedMap<Date, Message> map = new TreeMap<Date, Message>(new DateComparator());
	Iterator iter = topicsBySession.iterator();
	while (iter.hasNext()) {
	    msg = (Message) iter.next();
	    if (ForumConstants.OLD_FORUM_STYLE) {
		map.put(msg.getCreated(), msg);
	    } else {
		map.put(msg.getLastReplyDate(), msg);
	    }
	}
	return MessageDTO.getMessageDTO(new ArrayList<Message>(map.values()));

    }

    public int getTopicsNum(Long userID, Long sessionId) {
	return messageDao.getTopicsNum(userID, sessionId);
    }

    public int getNumOfPostsByTopic(Long userId, Long topicId) {
	return messageSeqDao.getNumOfPostsByTopic(userId, topicId);
    }

    public ForumUser getUserByID(Long userId) {
	return forumUserDao.getByUserId(userId);
    }

    public ForumUser getUserByUserAndSession(Long userId, Long sessionId) {
	return forumUserDao.getByUserIdAndSessionId(userId, sessionId);
    }

    public void createUser(ForumUser forumUser) {
	forumUserDao.save(forumUser);
    }

    public ForumToolSession getSessionBySessionId(Long sessionId) {
	return forumToolSessionDao.getBySessionId(sessionId);
    }

    public Long getRootTopicId(Long topicId) {
	MessageSeq seq = messageSeqDao.getByTopicId(topicId);
	if (seq == null || seq.getRootMessage() == null) {
	    ForumService.log.error("A sequence information can not be found for topic ID:" + topicId);
	    return null;
	}
	return seq.getRootMessage().getUid();
    }

    public List getAuthoredTopics(Long forumUid) {
	List list = messageDao.getTopicsFromAuthor(forumUid);

	TreeMap<Date, Message> map = new TreeMap<Date, Message>(new DateComparator());
	// get all the topics skipping ones with a tool session (we may be editing in monitor) and sort by create date
	Iterator iter = list.iterator();
	while (iter.hasNext()) {
	    Message topic = (Message) iter.next();
	    if (topic.getToolSession() == null) {
		map.put(topic.getCreated(), topic);
	    }
	}
	return MessageDTO.getMessageDTO(new ArrayList<Message>(map.values()));
    }

    public Long getToolDefaultContentIdBySignature(String toolSignature) {
	Long contentId = null;
	contentId = new Long(toolService.getToolDefaultContentIdBySignature(toolSignature));
	if (contentId == null) {
	    String error = "Could not retrieve default content id for this tool";
	    ForumService.log.error(error);
	    throw new ForumException(error);
	}
	return contentId;
    }

    public List getSessionsByContentId(Long contentID) {
	return forumToolSessionDao.getByContentId(contentID);
    }

    public List getUsersBySessionId(Long sessionID) {
	return forumUserDao.getBySessionId(sessionID);
    }

    public List getMessagesByUserUid(Long userId, Long sessionId) {
	List list = messageDao.getByUserAndSession(userId, sessionId);

	return MessageDTO.getMessageDTO(list);
    }

    public ForumUser getUser(Long userUid) {
	return forumUserDao.getByUid(userUid);
    }

    public void releaseMarksForSession(Long sessionID) {
	// udate release mark date for each message.
	List list = messageDao.getBySession(sessionID);
	Iterator iter = list.iterator();
	ForumToolSession session = forumToolSessionDao.getBySessionId(sessionID);
	Forum forum = session.getForum();
	boolean notifyLearnersOnMarkRelease = getEventNotificationService().eventExists(ForumConstants.TOOL_SIGNATURE,
		ForumConstants.EVENT_NAME_NOTIFY_LEARNERS_ON_MARK_RELEASE, forum.getContentId());
	Map<Long, StringBuilder> notificationMessages = null;
	Object[] notificationMessageParameters = null;
	if (notifyLearnersOnMarkRelease) {
	    notificationMessages = new TreeMap<Long, StringBuilder>();
	    notificationMessageParameters = new Object[3];
	}

	while (iter.hasNext()) {
	    Message msg = (Message) iter.next();
	    ForumReport report = msg.getReport();
	    if (report != null) {
		report.setDateMarksReleased(new Date());
		if (notifyLearnersOnMarkRelease) {
		    ForumUser user = msg.getCreatedBy();
		    StringBuilder notificationMessage = notificationMessages.get(user.getUserId());
		    if (notificationMessage == null) {
			notificationMessage = new StringBuilder();
		    }
		    notificationMessageParameters[0] = msg.getSubject();
		    notificationMessageParameters[1] = msg.getUpdated();
		    notificationMessageParameters[2] = report.getMark();
		    notificationMessage.append(getLocalisedMessage("event.mark.release.mark",
			    notificationMessageParameters));
		    notificationMessages.put(user.getUserId(), notificationMessage);
		}
	    }
	    messageDao.saveOrUpdate(msg);

	}
	if (notifyLearnersOnMarkRelease) {
	    notificationMessageParameters = new Object[1];
	    for (Long userID : notificationMessages.keySet()) {
		notificationMessageParameters[0] = notificationMessages.get(userID).toString();
		getEventNotificationService().triggerForSingleUser(ForumConstants.TOOL_SIGNATURE,
			ForumConstants.EVENT_NAME_NOTIFY_LEARNERS_ON_MARK_RELEASE, forum.getContentId(), userID,
			notificationMessageParameters);

	    }
	}

	List<ForumUser> users = getUsersBySessionId(sessionID);
	if (users != null) {
	    for (ForumUser user : users) {
		// send marks to gradebook where applicable
		sendMarksToGradebook(user, sessionID);
	    }
	}

	// update session to set MarkRelease flag.
	session.setMarkReleased(true);
	forumToolSessionDao.saveOrUpdate(session);

    }

    public void finishUserSession(ForumUser currentUser) {
	currentUser.setSessionFinished(true);
	forumUserDao.save(currentUser);
    }

    // ***************************************************************************************************************
    // Private methods
    // ***************************************************************************************************************
    /**
     * @param map
     * @return
     */
    private List getSortedMessageDTO(SortedMap<MessageSeq, Message> map) {
	Iterator iter;
	MessageSeq msgSeq;
	List<MessageDTO> msgDtoList = new ArrayList<MessageDTO>();
	iter = map.entrySet().iterator();
	while (iter.hasNext()) {
	    Map.Entry entry = (Entry) iter.next();
	    msgSeq = (MessageSeq) entry.getKey();
	    MessageDTO dto = MessageDTO.getMessageDTO((Message) entry.getValue());
	    dto.setLevel(msgSeq.getMessageLevel());
	    msgDtoList.add(dto);
	}
	return msgDtoList;
    }

    /**
     * Process an uploaded file.
     * 
     * @param forumForm
     * @throws FileNotFoundException
     * @throws IOException
     * @throws RepositoryCheckedException
     * @throws InvalidParameterException
     */
    private NodeKey processFile(FormFile file, String fileType) {
	NodeKey node = null;
	if (file != null && !StringUtils.isEmpty(file.getFileName())) {
	    String fileName = file.getFileName();
	    try {
		node = getForumToolContentHandler().uploadFile(file.getInputStream(), fileName, file.getContentType(),
			fileType);
	    } catch (InvalidParameterException e) {
		throw new ForumException("FileNotFoundException occured while trying to upload File" + e.getMessage());
	    } catch (FileNotFoundException e) {
		throw new ForumException("FileNotFoundException occured while trying to upload File" + e.getMessage());
	    } catch (RepositoryCheckedException e) {
		throw new ForumException("FileNotFoundException occured while trying to upload File" + e.getMessage());
	    } catch (IOException e) {
		throw new ForumException("FileNotFoundException occured while trying to upload File" + e.getMessage());
	    }
	}
	return node;
    }

    /**
     * This method verifies the credentials of the SubmitFiles Tool and gives it the <code>Ticket</code> to login and
     * access the Content Repository.
     * 
     * A valid ticket is needed in order to access the content from the repository. This method would be called evertime
     * the tool needs to upload/download files from the content repository.
     * 
     * @return ITicket The ticket for repostory access
     * @throws SubmitFilesException
     */
    private ITicket getRepositoryLoginTicket() throws ForumException {
	ICredentials credentials = new SimpleCredentials(forumToolContentHandler.getRepositoryUser(),
		forumToolContentHandler.getRepositoryId());
	try {
	    ITicket ticket = repositoryService.login(credentials, forumToolContentHandler.getRepositoryWorkspaceName());
	    return ticket;
	} catch (AccessDeniedException ae) {
	    throw new ForumException("Access Denied to repository." + ae.getMessage());
	} catch (WorkspaceNotFoundException we) {
	    throw new ForumException("Workspace not found." + we.getMessage());
	} catch (LoginException e) {
	    throw new ForumException("Login failed." + e.getMessage());
	}
    }

    private Forum getDefaultForum() {
	Long defaultForumId = getToolDefaultContentIdBySignature(ForumConstants.TOOL_SIGNATURE);
	Forum defaultForum = getForumByContentId(defaultForumId);
	if (defaultForum == null) {
	    String error = "Could not retrieve default content record for this tool";
	    ForumService.log.error(error);
	    throw new ForumException(error);
	}

	return defaultForum;

    }

    public Long createNotebookEntry(Long sessionId, Integer notebookToolType, String toolSignature, Integer userId,
	    String entryText) {
	return coreNotebookService.createNotebookEntry(sessionId, notebookToolType, toolSignature, userId, "",
		entryText);
    }

    public NotebookEntry getEntry(Long sessionId, Integer idType, String signature, Integer userID) {
	List<NotebookEntry> list = coreNotebookService.getEntry(sessionId, idType, signature, userID);
	if (list == null || list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0);
	}
    }

    /**
     * @param notebookEntry
     */
    public void updateEntry(NotebookEntry notebookEntry) {
	coreNotebookService.updateEntry(notebookEntry);
    }

    // ***************************************************************************************************************
    // ToolContentManager and ToolSessionManager methods
    // ***************************************************************************************************************
    public void copyToolContent(Long fromContentId, Long toContentId) throws ToolException {
	if (toContentId == null) {
	    throw new ToolException("Failed to create the ForumFiles tool seession");
	}

	Forum fromContent = null;
	if (fromContentId != null) {
	    fromContent = forumDao.getByContentId(fromContentId);
	}
	if (fromContent == null) {
	    fromContent = getDefaultForum();
	}

	Forum toContent = Forum.newInstance(fromContent, toContentId, forumToolContentHandler);
	forumDao.saveOrUpdate(toContent);

	// save topics in this forum, only save the author created topic!!! and reset its reply number to zero.
	Set topics = toContent.getMessages();
	if (topics != null) {
	    Iterator iter = topics.iterator();
	    while (iter.hasNext()) {
		Message msg = (Message) iter.next();
		// set this message forum Uid as toContent
		if (!msg.getIsAuthored()) {
		    continue;
		}
		msg.setReplyNumber(0);
		// msg.setCreated(new Date()); // need to keep the original create date to maintain correct order
		msg.setUpdated(new Date());
		msg.setLastReplyDate(new Date());
		msg.setHideFlag(false);
		msg.setForum(toContent);
		createRootTopic(toContent.getUid(), (ForumToolSession) null, msg);
	    }
	}

    }

    public void setAsDefineLater(Long toolContentId, boolean value) throws DataMissingException, ToolException {
	Forum forum = forumDao.getByContentId(toolContentId);
	if (forum == null) {
	    throw new ToolException("No found tool content by given content ID:" + toolContentId);
	}
	forum.setDefineLater(value);
	forum.setContentInUse(false);
    }

    public void setAsRunOffline(Long toolContentId, boolean value) throws DataMissingException, ToolException {
	Forum forum = forumDao.getByContentId(toolContentId);
	if (forum == null) {
	    throw new ToolException("No found tool content by given content ID:" + toolContentId);
	}
	forum.setRunOffline(value);

    }

    public void removeToolContent(Long toolContentId, boolean removeSessionData) throws SessionDataExistsException,
	    ToolException {
	Forum forum = forumDao.getByContentId(toolContentId);
	if (removeSessionData) {
	    List list = forumToolSessionDao.getByContentId(toolContentId);
	    Iterator iter = list.iterator();
	    while (iter.hasNext()) {
		ForumToolSession session = (ForumToolSession) iter.next();
		forumToolSessionDao.delete(session);
	    }
	}
	forumDao.delete(forum);
    }

    /**
     * Export the XML fragment for the tool's content, along with any files needed for the content.
     * 
     * @throws DataMissingException
     *                 if no tool content matches the toolSessionId
     * @throws ToolException
     *                 if any other error occurs
     */

    public void exportToolContent(Long toolContentId, String rootPath) throws DataMissingException, ToolException {
	Forum toolContentObj = forumDao.getByContentId(toolContentId);
	if (toolContentObj == null) {
	    toolContentObj = getDefaultForum();
	}
	if (toolContentObj == null) {
	    throw new DataMissingException("Unable to find default content for the forum tool");
	}

	// set ResourceToolContentHandler as null to avoid copy file node in repository again.
	toolContentObj = Forum.newInstance(toolContentObj, toolContentId, null);
	toolContentObj.setToolContentHandler(null);
	toolContentObj.setCreatedBy(null);
	Set<Message> items = toolContentObj.getMessages();
	Set<Message> authorItems = new HashSet<Message>();
	for (Message item : items) {
	    if (item.getIsAuthored()) {
		authorItems.add(item);
		item.setCreatedBy(null);
		item.setModifiedBy(null);
		item.setToolSession(null);
		item.setForum(null);
		item.setToolContentHandler(null);
		item.setReport(null);
		item.setReplyNumber(0);
		item.setParent(null);
		item.setSessionClones(null);
	    }
	}
	toolContentObj.setMessages(authorItems);
	try {
	    exportContentService.registerFileClassForExport(Attachment.class.getName(), "fileUuid", "fileVersionId");
	    exportContentService.exportToolContent(toolContentId, toolContentObj, forumToolContentHandler, rootPath);
	} catch (ExportToolContentException e) {
	    throw new ToolException(e);
	}
    }

    /**
     * Import the XML fragment for the tool's content, along with any files needed for the content.
     * 
     * @throws ToolException
     *                 if any other error occurs
     */
    public void importToolContent(Long toolContentId, Integer newUserUid, String toolContentPath, String fromVersion,
	    String toVersion) throws ToolException {

	try {
	    exportContentService.registerFileClassForImport(Attachment.class.getName(), "fileUuid", "fileVersionId",
		    "fileName", "fileType", null, null);

	    Object toolPOJO = exportContentService.importToolContent(toolContentPath, forumToolContentHandler,
		    fromVersion, toVersion);
	    if (!(toolPOJO instanceof Forum)) {
		throw new ImportToolContentException("Import Forum tool content failed. Deserialized object is "
			+ toolPOJO);
	    }
	    Forum toolContentObj = (Forum) toolPOJO;

	    // reset it to new toolContentId
	    toolContentObj.setContentId(toolContentId);
	    ForumUser user = forumUserDao.getByUserId(new Long(newUserUid.longValue()));
	    if (user == null) {
		user = new ForumUser();
		UserDTO sysUser = ((User) userManagementService.findById(User.class, newUserUid)).getUserDTO();
		user.setFirstName(sysUser.getFirstName());
		user.setLastName(sysUser.getLastName());
		user.setLoginName(sysUser.getLogin());
		user.setUserId(new Long(newUserUid.longValue()));
		this.createUser(user);
	    }
	    toolContentObj.setCreatedBy(user);
	    // save forum first
	    forumDao.saveOrUpdate(toolContentObj);

	    // save all authoring message one by one.
	    // reset all resourceItem createBy user
	    Set<Message> items = toolContentObj.getMessages();
	    for (Message item : items) {
		item.setCreatedBy(user);
		item.setIsAuthored(true);
		item.setForum(toolContentObj);
		item.setSessionClones(new HashSet());
		createRootTopic(toolContentObj.getUid(), (ForumToolSession) null, item);
	    }
	} catch (ImportToolContentException e) {
	    throw new ToolException(e);
	}
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
	Forum forum = getForumByContentId(toolContentId);
	if (forum == null) {
	    forum = getDefaultForum();
	}
	return getForumOutputFactory().getToolOutputDefinitions(forum, definitionType);
    }

    /**
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#createToolSession(java.lang.Long, java.lang.String,
     *      java.lang.Long)
     */
    public void createToolSession(Long toolSessionId, String toolSessionName, Long toolContentId) throws ToolException {
	ForumToolSession session = new ForumToolSession();
	session.setSessionId(toolSessionId);
	session.setSessionName(toolSessionName);
	Forum forum = forumDao.getByContentId(toolContentId);
	session.setForum(forum);

	// also clone author created topic from this forum tool content!!!
	// this can avoid topic record information conflict when multiple sessions are against same tool content
	// for example, the reply number maybe various for different sessions.
	ForumService.log.debug("Clone tool content [" + forum.getContentId() + "] topics for session ["
		+ session.getSessionId() + "]");
	Set<Message> contentTopics = forum.getMessages();
	if (contentTopics != null && contentTopics.size() > 0) {
	    for (Message msg : contentTopics) {
		if (msg.getIsAuthored() && msg.getToolSession() == null) {
		    Message newMsg = Message.newInstance(msg, forumToolContentHandler);
		    msg.getSessionClones().add(newMsg);
		    createRootTopic(forum.getContentId(), session, newMsg);
		}
	    }
	}
	session.setStatus(ForumConstants.STATUS_CONTENT_COPYED);

	forumToolSessionDao.saveOrUpdate(session);
	ForumService.log.debug("tool session [" + session.getSessionId() + "] created.");
    }

    public String leaveToolSession(Long toolSessionId, Long learnerId) throws DataMissingException, ToolException {
	if (toolSessionId == null) {
	    ForumService.log.error("Fail to leave tool Session based on null tool session id.");
	    throw new ToolException("Fail to remove tool Session based on null tool session id.");
	}
	if (learnerId == null) {
	    ForumService.log.error("Fail to leave tool Session based on null learner.");
	    throw new ToolException("Fail to remove tool Session based on null learner.");
	}

	ForumToolSession session = forumToolSessionDao.getBySessionId(toolSessionId);
	if (session != null) {
	    forumToolSessionDao.saveOrUpdate(session);
	} else {
	    ForumService.log.error("Fail to leave tool Session.Could not find submit file "
		    + "session by given session id: " + toolSessionId);
	    throw new DataMissingException("Fail to leave tool Session."
		    + "Could not find submit file session by given session id: " + toolSessionId);
	}
	return learnerService.completeToolSession(toolSessionId, learnerId);
    }

    public ToolSessionExportOutputData exportToolSession(Long toolSessionId) throws DataMissingException, ToolException {
	return null;
    }

    public ToolSessionExportOutputData exportToolSession(List toolSessionIds) throws DataMissingException,
	    ToolException {
	return null;
    }

    public void removeToolSession(Long toolSessionId) throws DataMissingException, ToolException {
	forumToolSessionDao.delete(toolSessionId);
    }

    /**
     * Get the tool output for the given tool output names.
     * 
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#getToolOutput(java.util.List<String>, java.lang.Long,
     *      java.lang.Long)
     */
    public SortedMap<String, ToolOutput> getToolOutput(List<String> names, Long toolSessionId, Long learnerId) {

	return forumOutputFactory.getToolOutput(names, this, toolSessionId, learnerId);

    }

    /**
     * Get the tool output for the given tool output name.
     * 
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#getToolOutput(java.lang.String, java.lang.Long,
     *      java.lang.Long)
     */
    public ToolOutput getToolOutput(String name, Long toolSessionId, Long learnerId) {
	return forumOutputFactory.getToolOutput(name, this, toolSessionId, learnerId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lamsfoundation.lams.tool.sbmt.service.ISubmitFilesService#getDefaultContent(java.lang.Long)
     */
    public Forum getDefaultContent(Long contentID) {
	if (contentID == null) {
	    String error = "Could not retrieve default content id for Forum tool";
	    ForumService.log.error(error);
	    throw new ForumException(error);
	}

	Forum defaultContent = getDefaultForum();
	if (defaultContent.getConditions().isEmpty()) {
	    defaultContent.getConditions().add(getForumOutputFactory().createDefaultComplexCondition(defaultContent));
	}
	// get default content by given ID.
	Forum content = new Forum();
	content = Forum.newInstance(defaultContent, contentID, forumToolContentHandler);

	Set topics = content.getMessages();
	if (topics != null) {
	    Iterator iter = topics.iterator();
	    while (iter.hasNext()) {
		Message msg = (Message) iter.next();
		// clear message forum so that they can be saved when persistent happens
		msg.setForum(null);
	    }
	}

	return content;
    }

    public List<MessageDTO> getAllTopicsFromSession(Long sessionID) {
	return MessageDTO.getMessageDTO(messageDao.getBySession(sessionID));
    }

    /* ===============Methods implemented from ToolContentImport102Manager =============== */

    /**
     * Import the data for a 1.0.2 Forum
     */
    public void import102ToolContent(Long toolContentId, UserDTO user, Hashtable importValues) {
	Date now = new Date();
	Forum toolContentObj = new Forum();

	try {

	    toolContentObj.setTitle((String) importValues.get(ToolContentImport102Manager.CONTENT_TITLE));

	    toolContentObj.setAllowAnonym(Boolean.FALSE);
	    toolContentObj.setAllowEdit(Boolean.TRUE); // this is the default value
	    toolContentObj.setAllowNewTopic(Boolean.TRUE);
	    toolContentObj.setAllowRichEditor(Boolean.FALSE);
	    toolContentObj.setAllowUpload(Boolean.TRUE); // this is the default value
	    toolContentObj.setContentId(toolContentId);
	    toolContentObj.setContentInUse(Boolean.FALSE);
	    toolContentObj.setCreated(now);
	    toolContentObj.setDefineLater(Boolean.FALSE);
	    toolContentObj.setInstructions(WebUtil.convertNewlines((String) importValues
		    .get(ToolContentImport102Manager.CONTENT_BODY)));
	    toolContentObj.setLimitedChar(5000); // this is the default value
	    toolContentObj.setReflectOnActivity(Boolean.FALSE);
	    toolContentObj.setReflectInstructions(null);

	    // lockOnFinsh = ! isReusable
	    Boolean bool = WDDXProcessor.convertToBoolean(importValues, ToolContentImport102Manager.CONTENT_REUSABLE);
	    toolContentObj.setLockWhenFinished(bool != null ? !bool.booleanValue() : false);
	    toolContentObj.setMaximumReply(0);
	    toolContentObj.setMinimumReply(0);
	    toolContentObj.setOfflineInstructions(null);
	    toolContentObj.setOnlineInstructions(null);
	    toolContentObj.setRunOffline(Boolean.FALSE);
	    toolContentObj.setUpdated(now);

	    // unused entries from 1.0.2
	    // isNewTopicAllowed - not actually used in 1.0.2
	    // durationInDays - no equivalent in 2.0
	    // isPostingModerated - no equivalent in 2.0
	    // isPostingNotified - no equivalent in 2.0
	    // contentShowUser - no equivalent in 2.0
	    // isHTML - no equivalent in 2.0
	    // terminationType=moderator - no equivalent in 2.0

	    ForumUser forumUser = new ForumUser();
	    forumUser.setUserId(new Long(user.getUserID().longValue()));
	    forumUser.setFirstName(user.getFirstName());
	    forumUser.setLastName(user.getLastName());
	    forumUser.setLoginName(user.getLogin());
	    createUser(forumUser);
	    toolContentObj.setCreatedBy(forumUser);

	    // leave as empty, no need to set them to anything.
	    // toolContentObj.setAttachments(attachments);
	    forumDao.saveOrUpdate(toolContentObj);

	    // topics in the XML file are ordered using the "number" field, not in their order in the vector.
	    TreeMap<Integer, Map> messageMaps = new TreeMap<Integer, Map>();
	    Vector topics = (Vector) importValues.get(ToolContentImport102Manager.CONTENT_MB_TOPICS);
	    Date msgDate = null;
	    if (topics != null) {
		Iterator iter = topics.iterator();
		while (iter.hasNext()) {
		    Hashtable messageMap = (Hashtable) iter.next();
		    Integer order = WDDXProcessor.convertToInteger(messageMap,
			    ToolContentImport102Manager.CONTENT_MB_TOPIC_NUMBER);
		    messageMaps.put(order, messageMap);
		}

		iter = messageMaps.values().iterator();
		while (iter.hasNext()) {

		    Map messageMap = (Map) iter.next();

		    Message message = new Message();
		    message.setIsAuthored(true);

		    // topics are ordered by date, so I need to try to assign each entry a different date. Won't work if
		    // this is too quick.
		    if (msgDate != null) {
			try {
			    Thread.sleep(1000);
			} catch (Exception e) {
			}
		    }
		    msgDate = new Date();

		    message.setCreated(msgDate);
		    message.setCreatedBy(forumUser);
		    message.setUpdated(msgDate);
		    message.setLastReplyDate(msgDate);
		    message.setSubject((String) messageMap.get(ToolContentImport102Manager.CONTENT_TITLE));
		    message.setBody(WebUtil.convertNewlines((String) messageMap
			    .get(ToolContentImport102Manager.CONTENT_MB_TOPIC_MESSAGE)));
		    // ignore the old subject field - it wasn't updated by the old interface.
		    message.setHideFlag(Boolean.FALSE);
		    message.setIsAnonymous(Boolean.FALSE);

		    createRootTopic(toolContentObj.getUid(), (ForumToolSession) null, message);

		}
	    }

	} catch (WDDXProcessorConversionException e) {
	    ForumService.log.error("Unable to content for activity " + toolContentObj.getTitle()
		    + "properly due to a WDDXProcessorConversionException.", e);
	    throw new ToolException(
		    "Invalid import data format for activity "
			    + toolContentObj.getTitle()
			    + "- WDDX caused an exception. Some data from the design will have been lost. See log for more details.");
	}

    }

    /**
     * Set the description, throws away the title value as this is not supported in 2.0
     */
    public void setReflectiveData(Long toolContentId, String title, String description) throws ToolException,
	    DataMissingException {

	Forum toolContentObj = getForumByContentId(toolContentId);
	if (toolContentObj == null) {
	    throw new DataMissingException("Unable to set reflective data titled " + title
		    + " on activity toolContentId " + toolContentId + " as the tool content does not exist.");
	}

	toolContentObj.setReflectOnActivity(Boolean.TRUE);
	toolContentObj.setReflectInstructions(description);
    }

    /**
     * Sends marks straight to gradebook from a forum report
     * 
     * @param user
     * @param toolSessionID
     */
    @SuppressWarnings("unchecked")
    public void sendMarksToGradebook(ForumUser user, Long toolSessionID) {

	List<MessageDTO> messages = getMessagesByUserUid(user.getUid(), toolSessionID);
	if (messages != null) {
	    Float totalMark = null;
	    for (MessageDTO message : messages) {
		if (totalMark == null) {
		    totalMark = message.getMark();
		} else if (message.getMark() != null) {
		    totalMark += message.getMark();
		}
	    }
	    if (totalMark != null) {
		Double mark = new Double(totalMark);
		gradebookService.updateActivityMark(mark, null, user.getUserId().intValue(), toolSessionID, false);
	    }
	}

    }

    // ***************************************************************************************************************
    // Get / Set methods
    // ***************************************************************************************************************
    public ILamsToolService getToolService() {
	return toolService;
    }

    public void setToolService(ILamsToolService toolService) {
	this.toolService = toolService;
    }

    public AttachmentDao getAttachmentDao() {
	return attachmentDao;
    }

    public void setAttachmentDao(AttachmentDao attachmentDao) {
	this.attachmentDao = attachmentDao;
    }

    public ForumDao getForumDao() {
	return forumDao;
    }

    public void setForumDao(ForumDao forumDao) {
	this.forumDao = forumDao;
    }

    public TimestampDao getTimestampDao() {
	return timestampDao;
    }

    public void setTimestampDao(TimestampDao timestampDao) {
	this.timestampDao = timestampDao;
    }

    public MessageDao getMessageDao() {
	return messageDao;
    }

    public void setMessageDao(MessageDao messageDao) {
	this.messageDao = messageDao;
    }

    public MessageSeqDao getMessageSeqDao() {
	return messageSeqDao;
    }

    public void setMessageSeqDao(MessageSeqDao messageSeqDao) {
	this.messageSeqDao = messageSeqDao;
    }

    public ForumToolSessionDao getForumToolSessionDao() {
	return forumToolSessionDao;
    }

    public void setForumToolSessionDao(ForumToolSessionDao forumToolSessionDao) {
	this.forumToolSessionDao = forumToolSessionDao;
    }

    public ForumUserDao getForumUserDao() {
	return forumUserDao;
    }

    public void setForumUserDao(ForumUserDao forumUserDao) {
	this.forumUserDao = forumUserDao;
    }

    public IRepositoryService getRepositoryService() {
	return repositoryService;
    }

    public void setRepositoryService(IRepositoryService repositoryService) {
	this.repositoryService = repositoryService;
    }

    public ForumToolContentHandler getForumToolContentHandler() {
	return forumToolContentHandler;
    }

    public void setForumToolContentHandler(ForumToolContentHandler toolContentHandler) {
	forumToolContentHandler = toolContentHandler;
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

    public IUserManagementService getUserManagementService() {
	return userManagementService;
    }

    public void setUserManagementService(IUserManagementService userManagementService) {
	this.userManagementService = userManagementService;
    }

    public ForumReportDAO getForumReportDAO() {
	return forumReportDAO;
    }

    public void setForumReportDAO(ForumReportDAO forumReportDAO) {
	this.forumReportDAO = forumReportDAO;
    }

    public ICoreNotebookService getCoreNotebookService() {
	return coreNotebookService;
    }

    public void setCoreNotebookService(ICoreNotebookService coreNotebookService) {
	this.coreNotebookService = coreNotebookService;
    }

    public IEventNotificationService getEventNotificationService() {
	return eventNotificationService;
    }

    public void setEventNotificationService(IEventNotificationService eventNotificationService) {
	this.eventNotificationService = eventNotificationService;
    }

    public String getLocalisedMessage(String key, Object[] args) {
	return messageService.getMessage(key, args);
    }

    public void setGradebookService(IGradebookService gradebookService) {
	this.gradebookService = gradebookService;
    }

    /**
     * {@inheritDoc}
     */
    public String createTextSearchConditionName(Collection<ForumCondition> existingConditions) {
	String uniqueNumber = null;
	do {
	    uniqueNumber = String.valueOf(Math.abs(generator.nextInt()));
	    for (ForumCondition condition : existingConditions) {
		String[] splittedName = getForumOutputFactory().splitConditionName(condition.getName());
		if (uniqueNumber.equals(splittedName[1])) {
		    uniqueNumber = null;
		}
	    }
	} while (uniqueNumber == null);
	return getForumOutputFactory().buildTextSearchConditionName(uniqueNumber);
    }

    /**
     * Get number of new postings.
     * 
     * @param messageId
     * @param userId
     * @return
     */
    public int getNewMessagesNum(Long messageId, Long userId) {
	return timestampDao.getNewMessagesNum(messageId, userId);
    }

    /**
     * Get last topic date.
     * 
     * @param messageId
     * @return
     */
    public Date getLastTopicDate(Long messageId) {
	return messageDao.getLastTopicDate(messageId);
    }

    /**
     * Get timestamp.
     * 
     * @param messageId
     * @param forumUserId
     * @return
     */
    public Timestamp getTimestamp(Long MessageId, Long forumUserId) throws PersistenceException {
	return timestampDao.getTimestamp(MessageId, forumUserId);
    }

    /**
     * Save timestamp.
     * 
     * @param timestamp
     * @return
     */
    public void saveTimestamp(Timestamp timestamp) {
	timestampDao.saveOrUpdate(timestamp);
    }

}