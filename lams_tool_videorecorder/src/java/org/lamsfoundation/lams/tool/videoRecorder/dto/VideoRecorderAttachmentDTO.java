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

/* $Id$ */

package org.lamsfoundation.lams.tool.videoRecorder.dto;

import java.util.Date;

import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderAttachment;

public class VideoRecorderAttachmentDTO implements Comparable {

	public VideoRecorderAttachmentDTO(VideoRecorderAttachment att) {
		this.fileUuid = att.getFileUuid();
		this.fileName = att.getFileName();
		this.fileVersionId = att.getFileVersionId();
		this.createDate = att.getCreateDate();
		this.uid = att.getUid();
	}

	Long uid;

	Long fileUuid;

	Long fileVersionId;

	String fileName;

	Date createDate;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileUuid() {
		return fileUuid;
	}

	public void setFileUuid(Long fileUuid) {
		this.fileUuid = fileUuid;
	}

	public Long getFileVersionId() {
		return fileVersionId;
	}

	public void setFileVersionId(Long fileVersionId) {
		this.fileVersionId = fileVersionId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public int compareTo(Object o) {
		VideoRecorderAttachmentDTO toAttachment = (VideoRecorderAttachmentDTO) o;
		int returnValue = this.createDate.compareTo(((VideoRecorderAttachmentDTO) o)
				.getCreateDate());

		if (returnValue == 0) {
			returnValue = this.uid.compareTo(toAttachment.getUid());
		}
		return returnValue;
	}
}
