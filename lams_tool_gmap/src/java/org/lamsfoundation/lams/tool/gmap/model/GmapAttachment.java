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

package org.lamsfoundation.lams.tool.gmap.model;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;

/**
 * 
 * The details of files attached to the tool. In most cases this will be the
 * online/offline instruction files.
 * 
 * @hibernate.class table="tl_lagmap10_attachment"
 * 
 */

public class GmapAttachment implements java.io.Serializable, Cloneable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 47452859888067500L;
	
	private static final Logger log = Logger.getLogger(GmapAttachment.class);

	/**
	 * 
	 */
	private Long uid;

	private Long fileVersionId;

	private String fileType;

	private String fileName;

	private Long fileUuid;

	private Date createDate;

	private Gmap gmap;

	// Constructors

	/** default constructor */
	public GmapAttachment() {
	}

	/** full constructor */
	public GmapAttachment(Long fileVersionId, String fileType, String fileName,
			Long fileUuid, Date createDate, Gmap gmap) {
		this.fileVersionId = fileVersionId;
		this.fileType = fileType;
		this.fileName = fileName;
		this.fileUuid = fileUuid;
		this.createDate = createDate;
		this.gmap = gmap;
	}

	// Property accessors
	/**
	 * @hibernate.id generator-class="native" type="java.lang.Long" column="uid"
	 * 
	 */

	public Long getUid() {
		return this.uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	/**
	 * @hibernate.property column="file_version_id" length="20"
	 * 
	 */

	public Long getFileVersionId() {
		return this.fileVersionId;
	}

	public void setFileVersionId(Long fileVersionId) {
		this.fileVersionId = fileVersionId;
	}

	/**
	 * @hibernate.property column="file_type" length="255"
	 * 
	 */

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @hibernate.property column="file_name" length="255"
	 * 
	 */

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @hibernate.property column="file_uuid" length="20"
	 * 
	 */

	public Long getFileUuid() {
		return this.fileUuid;
	}

	public void setFileUuid(Long fileUuid) {
		this.fileUuid = fileUuid;
	}

	/**
	 * @hibernate.property column="create_date"
	 * 
	 */

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @hibernate.many-to-one not-null="true"
	 * @hibernate.column name="gmap_uid"
	 * 
	 */

	public Gmap getGmap() {
		return this.gmap;
	}

	public void setGmap(Gmap gmap) {
		this.gmap = gmap;
	}

	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(
				Integer.toHexString(hashCode())).append(" [");
		buffer.append("fileVersionId").append("='").append(getFileVersionId())
				.append("' ");
		buffer.append("fileName").append("='").append(getFileName()).append(
				"' ");
		buffer.append("fileUuid").append("='").append(getFileUuid()).append(
				"' ");
		buffer.append("]");

		return buffer.toString();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof GmapAttachment))
			return false;

		final GmapAttachment genericEntity = (GmapAttachment) o;

      	return new EqualsBuilder()
      	.append(this.uid,genericEntity.uid)
      	.append(this.fileVersionId,genericEntity.fileVersionId)
      	.append(this.fileName,genericEntity.fileName)
      	.append(this.fileType,genericEntity.fileType)
      	.append(this.createDate,genericEntity.createDate)
      	.isEquals();
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result
				+ (getUid() == null ? 0 : this.getUid().hashCode());
		return result;
	}

	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
			((GmapAttachment) obj).setUid(null);
		} catch (CloneNotSupportedException e) {
			log.error("Failed to clone " + GmapAttachment.class);
		}

		return obj;
	}
}
