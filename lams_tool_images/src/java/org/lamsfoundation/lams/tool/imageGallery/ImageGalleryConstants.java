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
package org.lamsfoundation.lams.tool.imageGallery;

public class ImageGalleryConstants {
    public static final String TOOL_SIGNATURE = "laimag10";
    public static final String RESOURCE_SERVICE = "imageGalleryService";
    public static final String TOOL_CONTENT_HANDLER_NAME = "imageGalleryToolContentHandler";
    public static final int COMPLETED = 1;

    // for action forward name
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String DEFINE_LATER = "definelater";

    // for parameters' name
    public static final String PARAM_TOOL_CONTENT_ID = "toolContentID";
    public static final String PARAM_TOOL_SESSION_ID = "toolSessionID";
    public static final String PARAM_FILE_VERSION_ID = "fileVersionId";
    public static final String PARAM_FILE_UUID = "fileUuid";
    public static final String PARAM_IMAGE_INDEX = "imageIndex";
    public static final String PARAM_IMAGE_UID = "imageUid";
    public static final String PARAM_CURRENT_INSTRUCTION_INDEX = "insIdx";
    public static final String PARAM_RUN_OFFLINE = "runOffline";
    public static final String PARAM_OPEN_URL_POPUP = "popupUrl";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_COMMENTS = "comments";
    public static final String PARAM_CURRENT_IMAGE = "currentImage";
    public static final String PARAM_CURRENT_RATING = "currentRating";
    public static final String PARAM_VOTED_IMAGE_UID = "votedImageUid";

    // for request attribute name
    public static final String ATTR_TOOL_CONTENT_ID = "toolContentID";
    public static final String ATTR_TOOL_SESSION_ID = "toolSessionID";
    public static final String ATTR_RESOURCE_ITEM_LIST = "imageGalleryList";
    public static final String ATT_ATTACHMENT_LIST = "instructionAttachmentList";
    public static final String ATTR_DELETED_RESOURCE_ITEM_LIST = "deleteImageGalleryList";
    public static final String ATTR_DELETED_ATTACHMENT_LIST = "deletedAttachmmentList";
    public static final String ATTR_DELETED_RESOURCE_ITEM_ATTACHMENT_LIST = "deletedItemAttachmmentList";;
    public static final String ATT_LEARNING_OBJECT = "cpPackage";
    public static final String ATTR_RESOURCE_REVIEW_URL = "imageGalleryItemReviewUrl";
    public static final String ATTR_RESOURCE = "imageGallery";
    public static final String ATTR_RESOURCE_ITEM_UID = "itemUid";
    public static final String ATTR_NEXT_ACTIVITY_URL = "nextActivityUrl";
    public static final String ATTR_SUMMARY_LIST = "summaryList";
    public static final String ATTR_USER_LIST = "userList";
    public static final String ATTR_RESOURCE_INSTRUCTION = "instructions";
    public static final String ATTR_FINISH_LOCK = "finishedLock";
    public static final String ATTR_LOCK_ON_FINISH = "lockOnFinish";
    public static final String ATTR_SESSION_MAP_ID = "sessionMapID";
    public static final String ATTR_RESOURCE_FORM = "imageGalleryForm";
    public static final String ATTR_NEXT_IMAGE_TITLE = "nextImageTitle";
    public static final String ATTR_FILE_TYPE_FLAG = "fileTypeFlag";
    public static final String ATTR_TITLE = "title";
    public static final String ATTR_INSTRUCTIONS = "instructions";
    public static final String ATTR_USER_FINISHED = "userFinished";
    public static final String ATTR_COMMENT = "comment";
    public static final String ATTR_CURRENT_IMAGE_UID = "currentImageUid";
    public static final String ATTR_CONTENT_FOLDER_ID = "contentFolderID";
    
    // error message keys
    public static final String ERROR_MSG_TITLE_BLANK = "error.resource.item.title.blank";
    public static final String ERROR_MSG_NOT_ALLOWED_FORMAT = "error.resource.image.not.alowed.format";
    public static final String ERROR_MSG_URL_BLANK = "error.resource.item.url.blank";
    public static final String ERROR_MSG_DESC_BLANK = "error.resource.item.desc.blank";
    public static final String ERROR_MSG_FILE_BLANK = "error.resource.item.file.blank";
    public static final String ERROR_MSG_COMMENT_BLANK = "error.resource.image.comment.blank";
    public static final String ERROR_MSG_INVALID_URL = "error.resource.item.invalid.url";
    public static final String ERROR_MSG_UPLOAD_FAILED = "error.upload.failed";
    public static final String ERROR_MSG_REQUIRED_FIELDS_MISSING = "error.required.fields.missing";
    public static final String ERROR_MSG_ENTERED_VALUES_NOT_INTEGERS = "error.entered.values.not.integers";

    public static final String PAGE_EDITABLE = "isPageEditable";
    public static final String MODE_AUTHOR_SESSION = "author_session";
    public static final String ATTR_REFLECTION_ON = "reflectOn";
    public static final String ATTR_REFLECTION_INSTRUCTION = "reflectInstructions";
    public static final String ATTR_REFLECTION_ENTRY = "reflectEntry";
    public static final String ATTR_REFLECT_LIST = "reflectList";
    public static final String ATTR_USER_UID = "userUid";
    
    public static final String DEFUALT_PROTOCOL_REFIX = "http://";
    public static final String ALLOW_PROTOCOL_REFIX = new String("[http://|https://|ftp://|nntp://]");
    public static final String EVENT_NAME_NOTIFY_TEACHERS_ON_ASSIGMENT_SUBMIT = "notify_teachers_on_assigment_submit";
}
