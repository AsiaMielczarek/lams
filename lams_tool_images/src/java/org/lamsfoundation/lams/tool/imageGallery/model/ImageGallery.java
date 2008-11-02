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
package org.lamsfoundation.lams.tool.imageGallery.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.tool.imageGallery.util.ImageGalleryToolContentHandler;

/**
 * ImageGallery
 * 
 * @author Dapeng Ni
 * 
 * @hibernate.class table="tl_laimag10_imageGallery"
 * 
 */
public class ImageGallery implements Cloneable {

    private static final Logger log = Logger.getLogger(ImageGallery.class);

    // key
    private Long uid;

    // tool contentID
    private Long contentId;

    private String title;

    private String instructions;
    
    private Long nextImageTitle;

    // advance
    private boolean runOffline;

    private boolean allowVote;

    private int numberColumns;

    private boolean allowCommentImages;

    private boolean allowShareImages;

    private boolean lockWhenFinished;

    private boolean defineLater;

    private boolean contentInUse;

    private boolean allowRank;

    // instructions
    private String onlineInstructions;

    private String offlineInstructions;

    private Set attachments;

    // general infomation
    private Date created;

    private Date updated;

    private ImageGalleryUser createdBy;

    // imageGallery Items
    private Set imageGalleryItems;

    private boolean reflectOnActivity;

    private String reflectInstructions;

    // *************** NON Persist Fields ********************
    private IToolContentHandler toolContentHandler;

    private List<ImageGalleryAttachment> onlineFileList;

    private List<ImageGalleryAttachment> offlineFileList;

    /**
     * Default contruction method.
     * 
     */
    public ImageGallery() {
	nextImageTitle = new Long(1);
	attachments = new HashSet();
	imageGalleryItems = new HashSet();
    }

    // **********************************************************
    // Function method for ImageGallery
    // **********************************************************
    public static ImageGallery newInstance(ImageGallery defaultContent, Long contentId,
	    ImageGalleryToolContentHandler imageGalleryToolContentHandler) {
	ImageGallery toContent = new ImageGallery();
	defaultContent.toolContentHandler = imageGalleryToolContentHandler;
	toContent = (ImageGallery) defaultContent.clone();
	toContent.setContentId(contentId);

	// reset user info as well
	if (toContent.getCreatedBy() != null) {
	    toContent.getCreatedBy().setImageGallery(toContent);
	    Set<ImageGalleryItem> items = toContent.getImageGalleryItems();
	    for (ImageGalleryItem item : items) {
		item.setCreateBy(toContent.getCreatedBy());
	    }
	}
	return toContent;
    }

    @Override
    public Object clone() {

	ImageGallery imageGallery = null;
	try {
	    imageGallery = (ImageGallery) super.clone();
	    imageGallery.setUid(null);
	    if (imageGalleryItems != null) {
		Iterator iter = imageGalleryItems.iterator();
		Set set = new HashSet();
		while (iter.hasNext()) {
		    ImageGalleryItem item = (ImageGalleryItem) iter.next();
		    ImageGalleryItem newItem = (ImageGalleryItem) item.clone();
		    // just clone old file without duplicate it in repository
		    set.add(newItem);
		}
		imageGallery.imageGalleryItems = set;
	    }
	    // clone attachment
	    if (attachments != null) {
		Iterator iter = attachments.iterator();
		Set set = new HashSet();
		while (iter.hasNext()) {
		    ImageGalleryAttachment file = (ImageGalleryAttachment) iter.next();
		    ImageGalleryAttachment newFile = (ImageGalleryAttachment) file.clone();
		    // just clone old file without duplicate it in repository

		    set.add(newFile);
		}
		imageGallery.attachments = set;
	    }
	    // clone ReourceUser as well
	    if (createdBy != null) {
		imageGallery.setCreatedBy((ImageGalleryUser) createdBy.clone());
	    }
	} catch (CloneNotSupportedException e) {
	    ImageGallery.log.error("When clone " + ImageGallery.class + " failed");
	}

	return imageGallery;
    }

    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (!(o instanceof ImageGallery)) {
	    return false;
	}

	final ImageGallery genericEntity = (ImageGallery) o;

	return new EqualsBuilder().append(uid, genericEntity.uid).append(title, genericEntity.title).append(
		instructions, genericEntity.instructions).append(onlineInstructions, genericEntity.onlineInstructions)
		.append(offlineInstructions, genericEntity.offlineInstructions).append(created, genericEntity.created)
		.append(updated, genericEntity.updated).append(createdBy, genericEntity.createdBy).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(uid).append(title).append(instructions).append(onlineInstructions).append(
		offlineInstructions).append(created).append(updated).append(createdBy).toHashCode();
    }

    /**
     * Updates the modification data for this entity.
     */
    public void updateModificationData() {

	long now = System.currentTimeMillis();
	if (created == null) {
	    this.setCreated(new Date(now));
	}
	this.setUpdated(new Date(now));
    }

    public void toDTO() {
	onlineFileList = new ArrayList<ImageGalleryAttachment>();
	offlineFileList = new ArrayList<ImageGalleryAttachment>();
	Set<ImageGalleryAttachment> fileSet = this.getAttachments();
	if (fileSet != null) {
	    for (ImageGalleryAttachment file : fileSet) {
		if (StringUtils.equalsIgnoreCase(file.getFileType(), IToolContentHandler.TYPE_OFFLINE)) {
		    offlineFileList.add(file);
		} else {
		    onlineFileList.add(file);
		}
	    }
	}
    }

    // **********************************************************
    // get/set methods
    // **********************************************************
    /**
     * Returns the object's creation date
     * 
     * @return date
     * @hibernate.property column="create_date"
     */
    public Date getCreated() {
	return created;
    }

    /**
     * Sets the object's creation date
     * 
     * @param created
     */
    public void setCreated(Date created) {
	this.created = created;
    }

    /**
     * Returns the object's date of last update
     * 
     * @return date updated
     * @hibernate.property column="update_date"
     */
    public Date getUpdated() {
	return updated;
    }

    /**
     * Sets the object's date of last update
     * 
     * @param updated
     */
    public void setUpdated(Date updated) {
	this.updated = updated;
    }

    /**
     * @return Returns the userid of the user who created the Share imageGallery.
     * 
     * @hibernate.many-to-one cascade="save-update" column="create_by"
     * 
     */
    public ImageGalleryUser getCreatedBy() {
	return createdBy;
    }

    /**
     * @param createdBy
     *                The userid of the user who created this Share imageGallery.
     */
    public void setCreatedBy(ImageGalleryUser createdBy) {
	this.createdBy = createdBy;
    }

    /**
     * @hibernate.id column="uid" generator-class="native"
     */
    public Long getUid() {
	return uid;
    }

    public void setUid(Long uid) {
	this.uid = uid;
    }

    /**
     * @return Returns the title.
     * 
     * @hibernate.property column="title"
     * 
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title
     *                The title to set.
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * @return Returns the runOffline.
     * 
     * @hibernate.property column="run_offline"
     * 
     */
    public boolean getRunOffline() {
	return runOffline;
    }

    /**
     * @param runOffline
     *                The forceOffLine to set.
     * 
     * 
     */
    public void setRunOffline(boolean forceOffline) {
	runOffline = forceOffline;
    }

    /**
     * @return Returns the lockWhenFinish.
     * 
     * @hibernate.property column="lock_on_finished"
     * 
     */
    public boolean getLockWhenFinished() {
	return lockWhenFinished;
    }

    /**
     * @param lockWhenFinished
     *                Set to true to lock the imageGallery for finished users.
     */
    public void setLockWhenFinished(boolean lockWhenFinished) {
	this.lockWhenFinished = lockWhenFinished;
    }

    /**
     * @return Returns the instructions set by the teacher.
     * 
     * @hibernate.property column="instructions" type="text"
     */
    public String getInstructions() {
	return instructions;
    }

    public void setInstructions(String instructions) {
	this.instructions = instructions;
    }
    
    /**
     * @return Returns the next condecutive integer for constructing image title.
     * 
     * @hibernate.property column="next_image_title"
     */
    public Long getNextImageTitle() {
	return nextImageTitle;
    }

    public void setNextImageTitle(Long nextImageTitle) {
	this.nextImageTitle = nextImageTitle;
    }
    
    /**
     * @return Returns the onlineInstructions set by the teacher.
     * 
     * @hibernate.property column="online_instructions" type="text"
     */
    public String getOnlineInstructions() {
	return onlineInstructions;
    }

    public void setOnlineInstructions(String onlineInstructions) {
	this.onlineInstructions = onlineInstructions;
    }

    /**
     * @return Returns the onlineInstructions set by the teacher.
     * 
     * @hibernate.property column="offline_instructions" type="text"
     */
    public String getOfflineInstructions() {
	return offlineInstructions;
    }

    public void setOfflineInstructions(String offlineInstructions) {
	this.offlineInstructions = offlineInstructions;
    }

    /**
     * 
     * @hibernate.set lazy="true" cascade="all" inverse="false" order-by="create_date desc"
     * @hibernate.collection-key column="imageGallery_uid"
     * @hibernate.collection-one-to-many class="org.lamsfoundation.lams.tool.imageGallery.model.ImageGalleryAttachment"
     * 
     * @return a set of Attachments to this Message.
     */
    public Set getAttachments() {
	return attachments;
    }

    /*
     * @param attachments The attachments to set.
     */
    public void setAttachments(Set attachments) {
	this.attachments = attachments;
    }

    /**
     * 
     * 
     * @hibernate.set lazy="true" inverse="false" cascade="all" order-by="create_date desc"
     * @hibernate.collection-key column="imageGallery_uid"
     * @hibernate.collection-one-to-many class="org.lamsfoundation.lams.tool.imageGallery.model.ImageGalleryItem"
     * 
     * @return
     */
    public Set getImageGalleryItems() {
	return imageGalleryItems;
    }

    public void setImageGalleryItems(Set imageGalleryItems) {
	this.imageGalleryItems = imageGalleryItems;
    }

    /**
     * @hibernate.property column="content_in_use"
     * @return
     */
    public boolean isContentInUse() {
	return contentInUse;
    }

    public void setContentInUse(boolean contentInUse) {
	this.contentInUse = contentInUse;
    }

    /**
     * @hibernate.property column="define_later"
     * @return
     */
    public boolean isDefineLater() {
	return defineLater;
    }

    public void setDefineLater(boolean defineLater) {
	this.defineLater = defineLater;
    }

    /**
     * @hibernate.property column="content_id" unique="true"
     * @return
     */
    public Long getContentId() {
	return contentId;
    }

    public void setContentId(Long contentId) {
	this.contentId = contentId;
    }

    /**
     * @hibernate.property column="allow_comment_images"
     * @return
     */
    public boolean isAllowCommentImages() {
	return allowCommentImages;
    }

    public void setAllowCommentImages(boolean allowCommentImages) {
	this.allowCommentImages = allowCommentImages;
    }

    /**
     * @hibernate.property column="allow_share_images"
     * @return
     */
    public boolean isAllowShareImages() {
	return allowShareImages;
    }

    public void setAllowShareImages(boolean allowShareImages) {
	this.allowShareImages = allowShareImages;
    }

    /**
     * @hibernate.property column="number_columns"
     * @return
     */
    public int getNumberColumns() {
	return numberColumns;
    }

    public void setNumberColumns(int numberColumns) {
	this.numberColumns = numberColumns;
    }

    /**
     * @hibernate.property column="allow_vote"
     * @return
     */
    public boolean isAllowVote() {
	return allowVote;
    }

    public void setAllowVote(boolean allowVote) {
	this.allowVote = allowVote;
    }

    public List<ImageGalleryAttachment> getOfflineFileList() {
	return offlineFileList;
    }

    public void setOfflineFileList(List<ImageGalleryAttachment> offlineFileList) {
	this.offlineFileList = offlineFileList;
    }

    public List<ImageGalleryAttachment> getOnlineFileList() {
	return onlineFileList;
    }

    public void setOnlineFileList(List<ImageGalleryAttachment> onlineFileList) {
	this.onlineFileList = onlineFileList;
    }

    public void setToolContentHandler(IToolContentHandler toolContentHandler) {
	this.toolContentHandler = toolContentHandler;
    }

    /**
     * @hibernate.property column="reflect_instructions"
     * @return
     */
    public String getReflectInstructions() {
	return reflectInstructions;
    }

    public void setReflectInstructions(String reflectInstructions) {
	this.reflectInstructions = reflectInstructions;
    }

    /**
     * @hibernate.property column="reflect_on_activity"
     * @return
     */
    public boolean isReflectOnActivity() {
	return reflectOnActivity;
    }

    public void setReflectOnActivity(boolean reflectOnActivity) {
	this.reflectOnActivity = reflectOnActivity;
    }

    /**
     * @hibernate.property column="allow_rank"
     * @return
     */
    public boolean isAllowRank() {
	return allowRank;
    }

    public void setAllowRank(boolean allowRank) {
	this.allowRank = allowRank;
    }
}
