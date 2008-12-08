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
/* $$Id$$ */
package org.lamsfoundation.lams.tool.imageGallery.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.contentrepository.AccessDeniedException;
import org.lamsfoundation.lams.contentrepository.ICredentials;
import org.lamsfoundation.lams.contentrepository.ITicket;
import org.lamsfoundation.lams.contentrepository.IVersionedNode;
import org.lamsfoundation.lams.contentrepository.InvalidParameterException;
import org.lamsfoundation.lams.contentrepository.LoginException;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.WorkspaceNotFoundException;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.contentrepository.service.IRepositoryService;
import org.lamsfoundation.lams.contentrepository.service.SimpleCredentials;
import org.lamsfoundation.lams.events.IEventNotificationService;
import org.lamsfoundation.lams.learning.service.ILearnerService;
import org.lamsfoundation.lams.learningdesign.service.ExportToolContentException;
import org.lamsfoundation.lams.learningdesign.service.IExportToolContentService;
import org.lamsfoundation.lams.learningdesign.service.ImportToolContentException;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
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
import org.lamsfoundation.lams.tool.imageGallery.ImageGalleryConstants;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageCommentDAO;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageGalleryAttachmentDAO;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageGalleryConfigItemDAO;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageGalleryDAO;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageGalleryItemDAO;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageGalleryItemVisitDAO;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageGallerySessionDAO;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageGalleryUserDAO;
import org.lamsfoundation.lams.tool.imageGallery.dao.ImageRatingDAO;
import org.lamsfoundation.lams.tool.imageGallery.dto.ReflectDTO;
import org.lamsfoundation.lams.tool.imageGallery.dto.Summary;
import org.lamsfoundation.lams.tool.imageGallery.dto.UserImageContributionDTO;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageComment;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageGallery;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageGalleryAttachment;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageGalleryConfigItem;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageGalleryItem;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageGalleryItemVisitLog;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageGallerySession;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageGalleryUser;
import org.lamsfoundation.lams.tool.imageGallery.model.ImageRating;
import org.lamsfoundation.lams.tool.imageGallery.util.ImageCommentComparator;
import org.lamsfoundation.lams.tool.imageGallery.util.ImageGalleryItemComparator;
import org.lamsfoundation.lams.tool.imageGallery.util.ImageGalleryToolContentHandler;
import org.lamsfoundation.lams.tool.imageGallery.util.ReflectDTOComparator;
import org.lamsfoundation.lams.tool.imageGallery.util.ResizePictureUtil;
import org.lamsfoundation.lams.tool.service.ILamsToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.audit.IAuditService;

/**
 * 
 * @author Dapeng.Ni
 * 
 */
public class ImageGalleryServiceImpl implements IImageGalleryService, ToolContentManager, ToolSessionManager,
	ToolContentImport102Manager {
    
    private final static String MEDIUM_FILENAME_PREFIX = "medium_";
    
    private final static String THUMBNAIL_FILENAME_PREFIX = "thumbnail_";
    
    static Logger log = Logger.getLogger(ImageGalleryServiceImpl.class.getName());

    private ImageGalleryDAO imageGalleryDao;

    private ImageGalleryItemDAO imageGalleryItemDao;

    private ImageCommentDAO imageCommentDao;

    private ImageRatingDAO imageRatingDao;

    private ImageGalleryAttachmentDAO imageGalleryAttachmentDao;

    private ImageGalleryUserDAO imageGalleryUserDao;

    private ImageGallerySessionDAO imageGallerySessionDao;

    private ImageGalleryItemVisitDAO imageGalleryItemVisitDao;

    private ImageGalleryConfigItemDAO imageGalleryConfigItemDAO;

    // tool service
    private ImageGalleryToolContentHandler imageGalleryToolContentHandler;

    private MessageService messageService;
    
    private ImageGalleryOutputFactory imageGalleryOutputFactory;

    // system services
    private IRepositoryService repositoryService;

    private ILamsToolService toolService;

    private ILearnerService learnerService;

    private IAuditService auditService;

    private IUserManagementService userManagementService;

    private IExportToolContentService exportContentService;

    private ICoreNotebookService coreNotebookService;

    // *******************************************************************************
    // Service method
    // *******************************************************************************
    /**
     * Try to get the file. If forceLogin = false and an access denied exception occurs, call this method again to get a
     * new ticket and retry file lookup. If forceLogin = true and it then fails then throw exception.
     * 
     * @param uuid
     * @param versionId
     * @param relativePath
     * @param attemptCount
     * @return file node
     * @throws ImscpApplicationException
     */
    private IVersionedNode getFile(Long uuid, Long versionId, String relativePath)
	    throws ImageGalleryException {

	ITicket tic = getRepositoryLoginTicket();

	try {

	    return repositoryService.getFileItem(tic, uuid, versionId, relativePath);

	} catch (AccessDeniedException e) {

	    String error = "Unable to access repository to get file uuid " + uuid + " version id " + versionId
		    + " path " + relativePath + ".";

	    error = error + "AccessDeniedException: " + e.getMessage() + " Unable to retry further.";
	    ImageGalleryServiceImpl.log.error(error);
	    throw new ImageGalleryException(error, e);

	} catch (Exception e) {

	    String error = "Unable to access repository to get file uuid " + uuid + " version id " + versionId
		    + " path " + relativePath + "." + " Exception: " + e.getMessage();
	    ImageGalleryServiceImpl.log.error(error);
	    throw new ImageGalleryException(error, e);

	}
    }

    /**
     * This method verifies the credentials of the ImageGallery Tool and gives it the <code>Ticket</code> to login and
     * access the Content Repository.
     * 
     * A valid ticket is needed in order to access the content from the repository. This method would be called evertime
     * the tool needs to upload/download files from the content repository.
     * 
     * @return ITicket The ticket for repostory access
     * @throws ImageGalleryException
     */
    private ITicket getRepositoryLoginTicket() throws ImageGalleryException {
	ICredentials credentials = new SimpleCredentials(imageGalleryToolContentHandler.getRepositoryUser(),
		imageGalleryToolContentHandler.getRepositoryId());
	try {
	    ITicket ticket = repositoryService.login(credentials, imageGalleryToolContentHandler
		    .getRepositoryWorkspaceName());
	    return ticket;
	} catch (AccessDeniedException ae) {
	    throw new ImageGalleryException("Access Denied to repository." + ae.getMessage());
	} catch (WorkspaceNotFoundException we) {
	    throw new ImageGalleryException("Workspace not found." + we.getMessage());
	} catch (LoginException e) {
	    throw new ImageGalleryException("Login failed." + e.getMessage());
	}
    }

    public ImageGallery getImageGalleryByContentId(Long contentId) {
	ImageGallery rs = imageGalleryDao.getByContentId(contentId);
	if (rs == null) {
	    ImageGalleryServiceImpl.log.error("Could not find the content by given ID:" + contentId);
	}
	return rs;
    }

    public ImageGallery getDefaultContent(Long contentId) throws ImageGalleryException {
	if (contentId == null) {
	    String error = messageService.getMessage("error.msg.default.content.not.find");
	    ImageGalleryServiceImpl.log.error(error);
	    throw new ImageGalleryException(error);
	}

	ImageGallery defaultContent = getDefaultImageGallery();
	// save default content by given ID.
	ImageGallery content = new ImageGallery();
	content = ImageGallery.newInstance(defaultContent, contentId, imageGalleryToolContentHandler);
	// content.setNextImageTitle(new Long(1));
	return content;
    }

    public List getAuthoredItems(Long imageGalleryUid) {
	return imageGalleryItemDao.getAuthoringItems(imageGalleryUid);
    }

    public ImageGalleryAttachment uploadInstructionFile(FormFile uploadFile, String fileType)
	    throws UploadImageGalleryFileException {

	// upload file to repository
	NodeKey nodeKey = uploadFormFile(uploadFile, fileType);

	// create new attachement
	ImageGalleryAttachment file = new ImageGalleryAttachment();
	file.setFileType(fileType);
	file.setFileUuid(nodeKey.getUuid());
	file.setFileVersionId(nodeKey.getVersion());
	file.setFileName(uploadFile.getFileName());
	file.setCreated(new Date());

	return file;
    }

    public void saveUser(ImageGalleryUser imageGalleryUser) {
	imageGalleryUserDao.saveObject(imageGalleryUser);
    }

    public ImageGalleryUser getUserByIDAndContent(Long userId, Long contentId) {
	return imageGalleryUserDao.getUserByUserIDAndContentID(userId, contentId);
    }

    public ImageGalleryUser getUserByIDAndSession(Long userId, Long sessionId) {
	return imageGalleryUserDao.getUserByUserIDAndSessionID(userId, sessionId);
    }

    public void deleteFromRepository(Long fileUuid, Long fileVersionId) throws ImageGalleryException {
	ITicket ticket = getRepositoryLoginTicket();
	try {
	    repositoryService.deleteVersion(ticket, fileUuid, fileVersionId);
	} catch (Exception e) {
	    throw new ImageGalleryException("Exception occured while deleting files from"
		    + " the repository " + e.getMessage());
	}
    }

    public void saveOrUpdateImageGallery(ImageGallery imageGallery) {
	imageGalleryDao.saveObject(imageGallery);
    }

    public void deleteImageGalleryAttachment(Long attachmentUid) {
	imageGalleryAttachmentDao.removeObject(ImageGalleryAttachment.class, attachmentUid);

    }
    
    public ImageGalleryItem getImageGalleryItemByUid(Long itemUid) {
	return imageGalleryItemDao.getByUid(itemUid);
    }    

    public void saveOrUpdateImageGalleryItem(ImageGalleryItem image) {
	imageGalleryItemDao.saveObject(image);
    }
    
    public ImageRating getImageRatingByImageAndUser(Long imageUid, Long userId) {
	return imageRatingDao.getImageRatingByImageAndUser(imageUid, userId);
    }

    public void saveOrUpdateImageRating(ImageRating rating) {
	imageRatingDao.saveObject(rating);
    }
    
    public ImageComment getImageCommentByUid(Long commentUid) {
	return imageCommentDao.getCommentByUid(commentUid);
    }

    public void saveImageComment(ImageComment comment) {
	imageCommentDao.saveObject(comment);
    }
    
    public void deleteImageComment(Long uid) {
	imageCommentDao.removeObject(ImageComment.class, uid);
    }

    public void deleteImageGalleryItem(Long uid) {
	imageGalleryItemDao.removeObject(ImageGalleryItem.class, uid);
    }

    public ImageGallery getImageGalleryBySessionId(Long sessionId) {
	ImageGallerySession session = imageGallerySessionDao.getSessionBySessionId(sessionId);
	// to skip CGLib problem
	Long contentId = session.getImageGallery().getContentId();
	ImageGallery res = imageGalleryDao.getByContentId(contentId);
	return res;
    }

    public ImageGallerySession getImageGallerySessionBySessionId(Long sessionId) {
	return imageGallerySessionDao.getSessionBySessionId(sessionId);
    }

    public void saveOrUpdateImageGallerySession(ImageGallerySession resSession) {
	imageGallerySessionDao.saveObject(resSession);
    }

    public void setItemAccess(Long imageGalleryItemUid, Long userId, Long sessionId) {
	ImageGalleryItemVisitLog log = imageGalleryItemVisitDao.getImageGalleryItemLog(imageGalleryItemUid, userId);
	if (log == null) {
	    log = new ImageGalleryItemVisitLog();
	    ImageGalleryItem item = imageGalleryItemDao.getByUid(imageGalleryItemUid);
	    log.setImageGalleryItem(item);
	    ImageGalleryUser user = imageGalleryUserDao.getUserByUserIDAndSessionID(userId, sessionId);
	    log.setUser(user);
	    log.setComplete(false);
	    log.setSessionId(sessionId);
	    log.setAccessDate(new Timestamp(new Date().getTime()));
	    imageGalleryItemVisitDao.saveObject(log);
	}
    }

    public String finishToolSession(Long toolSessionId, Long userId) throws ImageGalleryException {
	ImageGalleryUser user = imageGalleryUserDao.getUserByUserIDAndSessionID(userId, toolSessionId);
	user.setSessionFinished(true);
	imageGalleryUserDao.saveObject(user);

	// ImageGallerySession session = imageGallerySessionDao.getSessionBySessionId(toolSessionId);
	// session.setStatus(ImageGalleryConstants.COMPLETED);
	// imageGallerySessionDao.saveObject(session);

	String nextUrl = null;
	try {
	    nextUrl = this.leaveToolSession(toolSessionId, userId);
	} catch (DataMissingException e) {
	    throw new ImageGalleryException(e);
	} catch (ToolException e) {
	    throw new ImageGalleryException(e);
	}
	return nextUrl;
    }
    
    public List<ImageGalleryUser> getUserListBySessionId(Long sessionId) {
	return imageGalleryUserDao.getBySessionID(sessionId);
    }

    public void setItemVisible(Long itemUid, boolean visible) {
	ImageGalleryItem item = imageGalleryItemDao.getByUid(itemUid);
	if (item != null) {
	    // createBy should be null for system default value.
	    Long userId = 0L;
	    String loginName = "No user";
	    if (item.getCreateBy() != null) {
		userId = item.getCreateBy().getUserId();
		loginName = item.getCreateBy().getLoginName();
	    }
	    if (visible) {
		auditService.logShowEntry(ImageGalleryConstants.TOOL_SIGNATURE, userId, loginName, item.toString());
	    } else {
		auditService.logHideEntry(ImageGalleryConstants.TOOL_SIGNATURE, userId, loginName, item.toString());
	    }
	    item.setHide(!visible);
	    imageGalleryItemDao.saveObject(item);
	}
    }

    public Long createNotebookEntry(Long sessionId, Integer notebookToolType, String toolSignature, Integer userId,
	    String entryText) {
	return coreNotebookService.createNotebookEntry(sessionId, notebookToolType, toolSignature, userId, "",
		entryText);
    }

    public NotebookEntry getEntry(Long sessionId, Integer idType, String signature, Integer userID) {
	List<NotebookEntry> list = coreNotebookService.getEntry(sessionId, idType, signature, userID);
	if ((list == null) || list.isEmpty()) {
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

    public ImageGalleryUser getUser(Long uid) {
	return (ImageGalleryUser) imageGalleryUserDao.getObject(ImageGalleryUser.class, uid);
    }

    public List<List<Summary>> getSummary(Long contentId) {
	List<List<Summary>> groupList = new ArrayList<List<Summary>>();
	List<Summary> group = new ArrayList<Summary>();

	// get all item which is accessed by user
	ImageGallery imageGallery = imageGalleryDao.getByContentId(contentId);

	List<ImageGallerySession> sessionList = imageGallerySessionDao.getByContentId(contentId);
	for (ImageGallerySession session : sessionList) {
	    // one new group for one session.
	    group = new ArrayList<Summary>();
	    Set<ImageGalleryItem> groupImages = getImagesForGroup(imageGallery, session.getSessionId());
	    
	    for (ImageGalleryItem image : groupImages) {
		Summary sum = new Summary(session.getSessionId(), session.getSessionName(), image);
		
		int numberOfVotes = imageGalleryUserDao.getNumberOfVotes(image.getUid(), session.getUid());
		sum.setNumberOfVotes(numberOfVotes);
		
		Object[] ratingForGroup = getRatingForGroup(image.getUid(),session.getSessionId());
		sum.setNumberRatings(((Long)ratingForGroup[0]).intValue());
		sum.setAverageRating(((Float)ratingForGroup[1]).floatValue());
		
		group.add(sum);
	    }
	    // if there is no any item available, then just put session name into Summary
	    if (group.size() == 0) {
		group.add(new Summary(session.getSessionId(), session.getSessionName(), null));
	    }
	    groupList.add(group);
	}

	return groupList;
    }
    
    public List<List<UserImageContributionDTO>> getImageSummary(Long contentId, Long imageUid) {
	List<List<UserImageContributionDTO>> imageSummary = new ArrayList<List<UserImageContributionDTO>>();
	List<UserImageContributionDTO> group = new ArrayList<UserImageContributionDTO>();
	ImageGalleryItem image = imageGalleryItemDao.getByUid(imageUid);
	
	List<ImageGallerySession> sessionList;
	if (image.isCreateByAuthor()) {
	    sessionList = imageGallerySessionDao.getByContentId(contentId);
	} else {
	    sessionList = new ArrayList<ImageGallerySession>();
	    sessionList.add(image.getCreateBy().getSession());
	}
	
	for (ImageGallerySession session : sessionList) {
	    // one new group for one session.
	    group = new ArrayList<UserImageContributionDTO>();
	    Object[] ratingForGroup = getRatingForGroup(image.getUid(),session.getSessionId());
	    int numberOfVotes = imageGalleryUserDao.getNumberOfVotes(image.getUid(), session.getUid());
	    
	    List<ImageGalleryUser> users = imageGalleryUserDao.getBySessionID(session.getSessionId());
	    for (ImageGalleryUser user : users) {
		UserImageContributionDTO userContribution = createUserContribution(image, user, session, numberOfVotes, ratingForGroup);
		group.add(userContribution);
	    }

	    imageSummary.add(group);
	}

	return imageSummary;

    }

    public Map<Long, Set<ReflectDTO>> getReflectList(Long contentId, boolean setEntry) {
	Map<Long, Set<ReflectDTO>> map = new HashMap<Long, Set<ReflectDTO>>();

	List<ImageGallerySession> sessionList = imageGallerySessionDao.getByContentId(contentId);
	for (ImageGallerySession session : sessionList) {
	    Long sessionId = session.getSessionId();
	    boolean hasRefection = session.getImageGallery().isReflectOnActivity();
	    Set<ReflectDTO> list = new TreeSet<ReflectDTO>(new ReflectDTOComparator());
	    // get all users in this session
	    List<ImageGalleryUser> users = imageGalleryUserDao.getBySessionID(sessionId);
	    for (ImageGalleryUser user : users) {
		ReflectDTO ref = new ReflectDTO(user);

		if (setEntry) {
		    NotebookEntry entry = getEntry(sessionId, CoreNotebookConstants.NOTEBOOK_TOOL,
			    ImageGalleryConstants.TOOL_SIGNATURE, user.getUserId().intValue());
		    if (entry != null) {
			ref.setReflect(entry.getEntry());
		    }
		}

		ref.setHasRefection(hasRefection);
		list.add(ref);
	    }
	    map.put(sessionId, list);
	}

	return map;
    }
    
    public List<List<List<UserImageContributionDTO>>> exportBySessionId(Long sessionId, ImageGalleryUser user, boolean skipHide) {
	ImageGallerySession session = imageGallerySessionDao.getSessionBySessionId(sessionId);
	if (session == null) {
	    ImageGalleryServiceImpl.log.error("Failed get ImageGallerySession by ID [" + sessionId + "]");
	    return null;
	}
	
	//imageList-->sessionList-->contributionList
	ImageGallery imageGallery = session.getImageGallery();
	List<List<List<UserImageContributionDTO>>> imageList = new ArrayList();

	Set<ImageGalleryItem> dbImages = imageGallery.getImageGalleryItems();
	for (ImageGalleryItem image : dbImages) {
	    if (skipHide && image.isHide()) {
		continue;
	    }

	    List<List<UserImageContributionDTO>> sessionList = new ArrayList();
	    
	    List<UserImageContributionDTO> userContributionList = new ArrayList<UserImageContributionDTO>();

	    Object[] ratingForGroup = getRatingForGroup(image.getUid(), session.getSessionId());
	    int numberOfVotes = imageGalleryUserDao.getNumberOfVotes(image.getUid(), session.getUid());	    
	    UserImageContributionDTO userContribution = createUserContribution(image, user, session, numberOfVotes, ratingForGroup);
	    userContribution.setImage(image);
	    userContributionList.add(userContribution);
	    sessionList.add(userContributionList);
	    imageList.add(sessionList);
	}

	return imageList;
    }

    public List<List<List<UserImageContributionDTO>>> exportByContentId(Long contentId) {
	ImageGallery imageGallery = imageGalleryDao.getByContentId(contentId);
	List<List<List<UserImageContributionDTO>>> imageList = new ArrayList();	
	
	Set<ImageGalleryItem> dbImages = imageGallery.getImageGalleryItems();
	for (ImageGalleryItem image : dbImages) {

	    List<List<UserImageContributionDTO>> sessionList = new ArrayList();
	    List<ImageGallerySession> imageGallerySessionList = imageGallerySessionDao.getByContentId(contentId);
	    for(ImageGallerySession imageSession : imageGallerySessionList) {
		List<UserImageContributionDTO> userContributionList = new ArrayList<UserImageContributionDTO>();
		Object[] ratingForGroup = getRatingForGroup(image.getUid(), imageSession.getSessionId());
		int numberOfVotes = imageGalleryUserDao.getNumberOfVotes(image.getUid(), imageSession.getUid());	    
		
		List<ImageGalleryUser> userList = imageGalleryUserDao.getBySessionID(imageSession.getSessionId());
		for (ImageGalleryUser user : userList) {
		    UserImageContributionDTO userContribution = createUserContribution(image, user, imageSession, numberOfVotes, ratingForGroup);
		    userContribution.setImage(image);
		    userContributionList.add(userContribution);		    
		}
		sessionList.add(userContributionList);
		
	    }

	    imageList.add(sessionList);
	}	

	return imageList;
    }

    public void uploadImageGalleryItemFile(ImageGalleryItem image, FormFile file)
	    throws UploadImageGalleryFileException {
	try {
	    // upload file
	    NodeKey nodeKey = uploadFormFile(file, IToolContentHandler.TYPE_ONLINE);
	    image.setFileName(file.getFileName());
	    image.setFileType(file.getContentType());
	    image.setFileVersionId(nodeKey.getVersion());
	    image.setOriginalFileUuid(nodeKey.getUuid());

	    String fileName = file.getFileName();
	    
	    ImageGalleryConfigItem mediumImageDimensionsKey = getConfigItem(ImageGalleryConfigItem.KEY_MEDIUM_IMAGE_DIMENSIONS);
	    int mediumImageDimensions = Integer.parseInt(mediumImageDimensionsKey.getConfigValue());

	    // Read the original image from the repository
	    InputStream originalIS = imageGalleryToolContentHandler.getFileNode(nodeKey.getUuid()).getFile();
	    InputStream mediumIS = ResizePictureUtil.resizePicture(originalIS, mediumImageDimensions);
	    String mediumFileName = MEDIUM_FILENAME_PREFIX + fileName.substring(0, fileName.indexOf('.')) + ".jpg";
	    NodeKey mediumNodeKey = imageGalleryToolContentHandler.uploadFile(mediumIS, mediumFileName,
		    file.getContentType(), IToolContentHandler.TYPE_ONLINE);
	    image.setMediumFileUuid(mediumNodeKey.getUuid());
	    
	    ImageGalleryConfigItem thumbnailImageDimensionsKey = getConfigItem(ImageGalleryConfigItem.KEY_THUMBNAIL_IMAGE_DIMENSIONS);
	    int thumbnailImageDimensions = Integer.parseInt(thumbnailImageDimensionsKey.getConfigValue());

	    // Read the original image from the repository
	    InputStream mediumIS2 = imageGalleryToolContentHandler.getFileNode(mediumNodeKey.getUuid()).getFile();
	    InputStream thumbnailIS = ResizePictureUtil.resizePicture(mediumIS2, thumbnailImageDimensions);
	    String thumbnailFileName = THUMBNAIL_FILENAME_PREFIX + fileName.substring(0, fileName.indexOf('.')) + ".jpg";
	    NodeKey thumbnailNodeKey = imageGalleryToolContentHandler.uploadFile(thumbnailIS, thumbnailFileName,
		    file.getContentType(), IToolContentHandler.TYPE_ONLINE);
	    image.setThumbnailFileUuid(thumbnailNodeKey.getUuid());
	    
	} catch (RepositoryCheckedException e) {
	    ImageGalleryServiceImpl.log.error(messageService.getMessage("error.msg.repository.checked.exception") + ":" + e.toString());
	    throw new UploadImageGalleryFileException(messageService.getMessage("error.msg.repository.checked.exception"));
	} catch (NumberFormatException e) {
	    ImageGalleryServiceImpl.log.error(messageService.getMessage("error.msg.number.format.exception") + ":"  + e.toString());
	    throw new UploadImageGalleryFileException(messageService.getMessage("error.msg.number.format.exception"));
	}
    }

    /**
     * Process an uploaded file.
     * 
     * @throws ImageGalleryException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws RepositoryCheckedException
     * @throws InvalidParameterException
     */
    private NodeKey uploadFormFile(FormFile file, String fileType) throws UploadImageGalleryFileException {
	if ((file == null) || StringUtils.isEmpty(file.getFileName())) {
	    throw new UploadImageGalleryFileException(messageService.getMessage("error.msg.upload.file.not.found",
		    new Object[] { file }));
	}

	NodeKey node = null;
	try {
	    node = imageGalleryToolContentHandler.uploadFile(file.getInputStream(), file.getFileName(), file
		    .getContentType(), fileType);
	} catch (InvalidParameterException e) {
	    throw new UploadImageGalleryFileException(messageService.getMessage("error.msg.invaid.param.upload"));
	} catch (FileNotFoundException e) {
	    throw new UploadImageGalleryFileException(messageService.getMessage("error.msg.file.not.found"));
	} catch (RepositoryCheckedException e) {
	    throw new UploadImageGalleryFileException(messageService.getMessage("error.msg.repository"));
	} catch (IOException e) {
	    throw new UploadImageGalleryFileException(messageService.getMessage("error.msg.io.exception"));
	}
	return node;
    }

    // *****************************************************************************
    // set methods for Spring Bean
    // *****************************************************************************
    public void setAuditService(IAuditService auditService) {
	this.auditService = auditService;
    }

    public void setLearnerService(ILearnerService learnerService) {
	this.learnerService = learnerService;
    }

    public void setMessageService(MessageService messageService) {
	this.messageService = messageService;
    }

    public void setRepositoryService(IRepositoryService repositoryService) {
	this.repositoryService = repositoryService;
    }

    public void setImageGalleryAttachmentDao(ImageGalleryAttachmentDAO imageGalleryAttachmentDao) {
	this.imageGalleryAttachmentDao = imageGalleryAttachmentDao;
    }

    public void setImageGalleryDao(ImageGalleryDAO imageGalleryDao) {
	this.imageGalleryDao = imageGalleryDao;
    }

    public void setImageGalleryItemDao(ImageGalleryItemDAO imageGalleryItemDao) {
	this.imageGalleryItemDao = imageGalleryItemDao;
    }

    public void setImageCommentDao(ImageCommentDAO imageCommentDao) {
	this.imageCommentDao = imageCommentDao;
    }

    public void setImageRatingDao(ImageRatingDAO imageRatingDao) {
	this.imageRatingDao = imageRatingDao;
    }

    public void setImageGallerySessionDao(ImageGallerySessionDAO imageGallerySessionDao) {
	this.imageGallerySessionDao = imageGallerySessionDao;
    }

    public void setImageGalleryToolContentHandler(ImageGalleryToolContentHandler imageGalleryToolContentHandler) {
	this.imageGalleryToolContentHandler = imageGalleryToolContentHandler;
    }

    public void setImageGalleryUserDao(ImageGalleryUserDAO imageGalleryUserDao) {
	this.imageGalleryUserDao = imageGalleryUserDao;
    }

    public void setToolService(ILamsToolService toolService) {
	this.toolService = toolService;
    }

    public ImageGalleryItemVisitDAO getImageGalleryItemVisitDao() {
	return imageGalleryItemVisitDao;
    }

    public void setImageGalleryItemVisitDao(ImageGalleryItemVisitDAO imageGalleryItemVisitDao) {
	this.imageGalleryItemVisitDao = imageGalleryItemVisitDao;
    }

    public ImageGalleryConfigItemDAO getImageGalleryConfigItemDAO() {
	return imageGalleryConfigItemDAO;
    }

    public void setImageGalleryConfigItemDAO(ImageGalleryConfigItemDAO imageGalleryConfigItemDAO) {
	this.imageGalleryConfigItemDAO = imageGalleryConfigItemDAO;
    }

    // *******************************************************************************
    // ToolContentManager, ToolSessionManager methods
    // *******************************************************************************

    public void exportToolContent(Long toolContentId, String rootPath) throws DataMissingException, ToolException {
	ImageGallery toolContentObj = imageGalleryDao.getByContentId(toolContentId);
	if (toolContentObj == null) {
	    try {
		toolContentObj = getDefaultImageGallery();
	    } catch (ImageGalleryException e) {
		throw new DataMissingException(e.getMessage());
	    }
	}
	if (toolContentObj == null) {
	    throw new DataMissingException("Unable to find default content for the imageGallery tool");
	}

	// set ImageGalleryToolContentHandler as null to avoid copy file node in repository again.
	toolContentObj = ImageGallery.newInstance(toolContentObj, toolContentId, null);
	toolContentObj.setToolContentHandler(null);
	toolContentObj.setOfflineFileList(null);
	toolContentObj.setOnlineFileList(null);
	Set<ImageGalleryItem> images = toolContentObj.getImageGalleryItems();
	for (ImageGalleryItem image : images) {
	    image.setComments(null);
	    
	    ImageGalleryAttachment originalFile = new ImageGalleryAttachment();
	    originalFile.setFileUuid(image.getOriginalFileUuid());
	    originalFile.setFileVersionId(image.getFileVersionId());
	    originalFile.setFileName(image.getFileName());
	    originalFile.setFileType(IToolContentHandler.TYPE_ONLINE);
	    image.setOriginalFile(originalFile);
	    
	    ImageGalleryAttachment mediumFile = new ImageGalleryAttachment();
	    mediumFile.setFileUuid(image.getMediumFileUuid());
	    mediumFile.setFileVersionId(image.getFileVersionId());
	    mediumFile.setFileName(MEDIUM_FILENAME_PREFIX + image.getFileName());
	    mediumFile.setFileType(IToolContentHandler.TYPE_ONLINE);	    
	    image.setMediumFile(mediumFile);

	    ImageGalleryAttachment thumbnailFile = new ImageGalleryAttachment();
	    thumbnailFile.setFileUuid(image.getThumbnailFileUuid());
	    thumbnailFile.setFileVersionId(image.getFileVersionId());
	    thumbnailFile.setFileName(THUMBNAIL_FILENAME_PREFIX + image.getFileName());
	    thumbnailFile.setFileType(IToolContentHandler.TYPE_ONLINE);	    
	    image.setThumbnailFile(thumbnailFile);
	}
	
	try {
	    exportContentService.registerFileClassForExport(ImageGalleryAttachment.class.getName(), "fileUuid",
		    "fileVersionId");
	    exportContentService.exportToolContent(toolContentId, toolContentObj, imageGalleryToolContentHandler,
		    rootPath);
	} catch (ExportToolContentException e) {
	    throw new ToolException(e);
	}
    }

    public void importToolContent(Long toolContentId, Integer newUserUid, String toolContentPath, String fromVersion,
	    String toVersion) throws ToolException {

	try {
	    exportContentService.registerFileClassForImport(ImageGalleryAttachment.class.getName(), "fileUuid",
		    "fileVersionId", "fileName", "fileType", null, null);

	    Object toolPOJO = exportContentService.importToolContent(toolContentPath, imageGalleryToolContentHandler,
		    fromVersion, toVersion);
	    if (!(toolPOJO instanceof ImageGallery)) {
		throw new ImportToolContentException(
			"Import ImageGallery tool content failed. Deserialized object is " + toolPOJO);
	    }
	    ImageGallery toolContentObj = (ImageGallery) toolPOJO;

	    // reset it to new toolContentId
	    toolContentObj.setContentId(toolContentId);
	    ImageGalleryUser user = imageGalleryUserDao.getUserByUserIDAndContentID(new Long(newUserUid.longValue()),
		    toolContentId);
	    if (user == null) {
		user = new ImageGalleryUser();
		UserDTO sysUser = ((User) userManagementService.findById(User.class, newUserUid)).getUserDTO();
		user.setFirstName(sysUser.getFirstName());
		user.setLastName(sysUser.getLastName());
		user.setLoginName(sysUser.getLogin());
		user.setUserId(new Long(newUserUid.longValue()));
		user.setImageGallery(toolContentObj);
	    }
	    toolContentObj.setCreatedBy(user);

	    // reset all imageGalleryItem createBy user
	    Set<ImageGalleryItem> images = toolContentObj.getImageGalleryItems();
	    for (ImageGalleryItem image : images) {
		image.setCreateBy(user);
		
		image.setOriginalFileUuid(image.getOriginalFile().getFileUuid());
		image.setMediumFileUuid(image.getMediumFile().getFileUuid());
		image.setThumbnailFileUuid(image.getThumbnailFile().getFileUuid());
		
		image.setOriginalFile(null);
		image.setMediumFile(null);
		image.setThumbnailFile(null);
	    }
	    imageGalleryDao.saveObject(toolContentObj);
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
    public SortedMap<String, ToolOutputDefinition> getToolOutputDefinitions(Long toolContentId) throws ToolException {
	ImageGallery imageGallery = getImageGalleryByContentId(toolContentId);
	if (imageGallery == null) {
	    imageGallery = getDefaultImageGallery();
	}
	return getImageGalleryOutputFactory().getToolOutputDefinitions(imageGallery);
    }

    public void copyToolContent(Long fromContentId, Long toContentId) throws ToolException {
	if (toContentId == null) {
	    throw new ToolException("Failed to create the SharedImageGalleryFiles tool seession");
	}

	ImageGallery imageGallery = null;
	if (fromContentId != null) {
	    imageGallery = imageGalleryDao.getByContentId(fromContentId);
	}
	if (imageGallery == null) {
	    try {
		imageGallery = getDefaultImageGallery();
	    } catch (ImageGalleryException e) {
		throw new ToolException(e);
	    }
	}

	ImageGallery toContent = ImageGallery.newInstance(imageGallery, toContentId, imageGalleryToolContentHandler);
	imageGalleryDao.saveObject(toContent);

	// save imageGallery items as well
	Set items = toContent.getImageGalleryItems();
	if (items != null) {
	    Iterator iter = items.iterator();
	    while (iter.hasNext()) {
		ImageGalleryItem item = (ImageGalleryItem) iter.next();
		// createRootTopic(toContent.getUid(),null,msg);
	    }
	}
    }

    public void setAsDefineLater(Long toolContentId, boolean value) throws DataMissingException, ToolException {
	ImageGallery imageGallery = imageGalleryDao.getByContentId(toolContentId);
	if (imageGallery == null) {
	    throw new ToolException("No found tool content by given content ID:" + toolContentId);
	}
	imageGallery.setDefineLater(value);
    }

    public void setAsRunOffline(Long toolContentId, boolean value) throws DataMissingException, ToolException {
	ImageGallery imageGallery = imageGalleryDao.getByContentId(toolContentId);
	if (imageGallery == null) {
	    throw new ToolException("No found tool content by given content ID:" + toolContentId);
	}
	imageGallery.setRunOffline(value);
    }

    public void removeToolContent(Long toolContentId, boolean removeSessionData) throws SessionDataExistsException,
	    ToolException {
	ImageGallery imageGallery = imageGalleryDao.getByContentId(toolContentId);
	if (removeSessionData) {
	    List list = imageGallerySessionDao.getByContentId(toolContentId);
	    Iterator iter = list.iterator();
	    while (iter.hasNext()) {
		ImageGallerySession session = (ImageGallerySession) iter.next();
		imageGallerySessionDao.delete(session);
	    }
	}
	imageGalleryDao.delete(imageGallery);
    }

    public void createToolSession(Long toolSessionId, String toolSessionName, Long toolContentId) throws ToolException {
	ImageGallerySession session = new ImageGallerySession();
	session.setSessionId(toolSessionId);
	session.setSessionName(toolSessionName);
	ImageGallery imageGallery = imageGalleryDao.getByContentId(toolContentId);
	session.setImageGallery(imageGallery);
	imageGallerySessionDao.saveObject(session);
    }

    public String leaveToolSession(Long toolSessionId, Long learnerId) throws DataMissingException, ToolException {
	if (toolSessionId == null) {
	    ImageGalleryServiceImpl.log.error("Fail to leave tool Session based on null tool session id.");
	    throw new ToolException("Fail to remove tool Session based on null tool session id.");
	}
	if (learnerId == null) {
	    ImageGalleryServiceImpl.log.error("Fail to leave tool Session based on null learner.");
	    throw new ToolException("Fail to remove tool Session based on null learner.");
	}

	ImageGallerySession session = imageGallerySessionDao.getSessionBySessionId(toolSessionId);
	if (session != null) {
	    session.setStatus(ImageGalleryConstants.COMPLETED);
	    imageGallerySessionDao.saveObject(session);
	} else {
	    ImageGalleryServiceImpl.log.error("Fail to leave tool Session.Could not find shared imageGallery "
		    + "session by given session id: " + toolSessionId);
	    throw new DataMissingException("Fail to leave tool Session."
		    + "Could not find shared imageGallery session by given session id: " + toolSessionId);
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
	imageGallerySessionDao.deleteBySessionId(toolSessionId);
    }

    /**
     * Get the tool output for the given tool output names.
     * 
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#getToolOutput(java.util.List<String>, java.lang.Long,
     *      java.lang.Long)
     */
    public SortedMap<String, ToolOutput> getToolOutput(List<String> names, Long toolSessionId, Long learnerId) {
	return imageGalleryOutputFactory.getToolOutput(names, this, toolSessionId, learnerId);
    }

    /**
     * Get the tool output for the given tool output name.
     * 
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#getToolOutput(java.lang.String, java.lang.Long,
     *      java.lang.Long)
     */
    public ToolOutput getToolOutput(String name, Long toolSessionId, Long learnerId) {
	return imageGalleryOutputFactory.getToolOutput(name, this, toolSessionId, learnerId);
    }

    /* ===============Methods implemented from ToolContentImport102Manager =============== */

    /**
     * Import the data for a 1.0.2 Noticeboard or HTMLNoticeboard
     */
    public void import102ToolContent(Long toolContentId, UserDTO user, Hashtable importValues) {
    }

    /** Set the description, throws away the title value as this is not supported in 2.0 */
    public void setReflectiveData(Long toolContentId, String title, String description) throws ToolException,
	    DataMissingException {

	ImageGallery toolContentObj = getImageGalleryByContentId(toolContentId);
	if (toolContentObj == null) {
	    throw new DataMissingException("Unable to set reflective data titled " + title
		    + " on activity toolContentId " + toolContentId + " as the tool content does not exist.");
	}

	toolContentObj.setReflectOnActivity(Boolean.TRUE);
	toolContentObj.setReflectInstructions(description);
    }

    /* =================================================================================== */

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

    public ICoreNotebookService getCoreNotebookService() {
	return coreNotebookService;
    }

    public void setCoreNotebookService(ICoreNotebookService coreNotebookService) {
	this.coreNotebookService = coreNotebookService;
    }

    public String getLocalisedMessage(String key, Object[] args) {
	return messageService.getMessage(key, args);
    }
    
    public ImageGalleryOutputFactory getImageGalleryOutputFactory() {
	return imageGalleryOutputFactory;
    }

    public void setImageGalleryOutputFactory(ImageGalleryOutputFactory imageGalleryOutputFactory) {
	this.imageGalleryOutputFactory = imageGalleryOutputFactory;
    }

    public ImageGalleryConfigItem getConfigItem(String key) {
	return imageGalleryConfigItemDAO.getConfigItemByKey(key);
    }

    public void saveOrUpdateImageGalleryConfigItem(ImageGalleryConfigItem item) {
	imageGalleryConfigItemDAO.saveOrUpdate(item);
    }
    
    public Set<ImageGalleryItem> getImagesForGroup(ImageGallery imageGallery, Long sessionId) {
	TreeSet<ImageGalleryItem> images = new TreeSet<ImageGalleryItem>(new ImageGalleryItemComparator());	
	
	List<ImageGalleryUser> grouppedUsers = getUserListBySessionId(sessionId);
	Set<ImageGalleryItem> allImages = imageGallery.getImageGalleryItems();

	for (ImageGalleryItem image : allImages) {
	    for (ImageGalleryUser grouppedUser : grouppedUsers) {
		if (image.isCreateByAuthor() || grouppedUser.getUserId().equals(image.getCreateBy().getUserId())) {
		    images.add(image);
		}
	    }
	}
	
	return images;
    }
    
    // *****************************************************************************
    // private methods
    // *****************************************************************************
    private ImageGallery getDefaultImageGallery() throws ImageGalleryException {
	Long defaultImageGalleryId = getToolDefaultContentIdBySignature(ImageGalleryConstants.TOOL_SIGNATURE);
	ImageGallery defaultImageGallery = getImageGalleryByContentId(defaultImageGalleryId);
	if (defaultImageGallery == null) {
	    String error = messageService.getMessage("error.msg.default.content.not.find");
	    ImageGalleryServiceImpl.log.error(error);
	    throw new ImageGalleryException(error);
	}

	return defaultImageGallery;
    }

    private Long getToolDefaultContentIdBySignature(String toolSignature) throws ImageGalleryException {
	Long contentId = null;
	contentId = new Long(toolService.getToolDefaultContentIdBySignature(toolSignature));
	if (contentId == null) {
	    String error = messageService.getMessage("error.msg.default.content.not.find");
	    ImageGalleryServiceImpl.log.error(error);
	    throw new ImageGalleryException(error);
	}
	return contentId;
    }    
    
    private UserImageContributionDTO createUserContribution(ImageGalleryItem image, ImageGalleryUser user, ImageGallerySession session, int numberOfVotes, Object[] ratingForGroup) {
	UserImageContributionDTO userContribution = new UserImageContributionDTO(session.getSessionName(), user);
	
	userContribution.setNumberOfVotes(numberOfVotes);
	userContribution.setNumberRatings(((Long)ratingForGroup[0]).intValue());
	userContribution.setAverageRating(((Float)ratingForGroup[1]).floatValue());
	
	ImageRating rating = imageRatingDao.getImageRatingByImageAndUser(image.getUid(), user.getUserId());
	if (rating != null) {
	    userContribution.setRating(rating.getRating());
	}
	
	boolean isVotedForThisImage = (image.getUid().equals(user.getVotedImageUid()));
	userContribution.setVotedForThisImage(isVotedForThisImage);
	
	Set<ImageComment> dbComments = image.getComments();
	TreeSet<ImageComment> comments = new TreeSet<ImageComment>(new ImageCommentComparator());
	for (ImageComment comment : dbComments) {
	    if (comment.getCreateBy().getUserId().equals(user.getUserId())) {
		comments.add(comment);
	    }
	}
	userContribution.setComments(comments);
	
	return userContribution;
    }
    
    /**
     * Calculates average rating and number of rating for the current group.
     * 
     * @param imageUid
     * @param sessionId
     * @return
     */
    private Object[] getRatingForGroup(Long imageUid, Long sessionId) {
	List<ImageGalleryUser> users = imageGalleryUserDao.getBySessionID(sessionId);
	Long numberRatings = new Long(0);
	Float averageRating = new Float(0);
	List<ImageRating> ratings = imageRatingDao.getImageRatingsByImageUid(imageUid);
	for (ImageRating rating : ratings) {
	    for (ImageGalleryUser user : users) {
		if (rating.getCreateBy().getUserId().equals(user.getUserId())) {
		    numberRatings++;
		    averageRating += rating.getRating();
		}
	    }
	}

	
	if (! numberRatings.equals(new Long(0))) {
	    averageRating = averageRating / numberRatings;
	}
	
	return  new Object[] {numberRatings, averageRating};
    }

}
