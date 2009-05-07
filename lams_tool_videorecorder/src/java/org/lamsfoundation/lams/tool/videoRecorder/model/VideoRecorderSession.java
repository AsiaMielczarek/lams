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

package org.lamsfoundation.lams.tool.videoRecorder.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.videoRecorder.dto.VideoRecorderSessionDTO;

/**
 * 
 * Represents the tool session.
 * 
 * @hibernate.class table="tl_lavidr10_session"
 */

public class VideoRecorderSession implements java.io.Serializable {

	private static Logger log = Logger.getLogger(VideoRecorderSession.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 4407078136514639026L;

	// Fields
	private Long uid;

	private Date sessionEndDate;

	private Date sessionStartDate;

	private Integer status;

	private Long sessionId;

	private String sessionName;
	
	private String contentFolderId;

	private VideoRecorder videoRecorder;

	private Set videoRecorderUsers;

	// Constructors

	/** default constructor */
	public VideoRecorderSession() {
	}

	/** full constructor */
	public VideoRecorderSession(Date sessionEndDate, Date sessionStartDate,
			Integer status, Long sessionId, String sessionName, String contentFolderId, VideoRecorder videoRecorder,
			Set videoRecorderUsers) {
		this.sessionEndDate = sessionEndDate;
		this.sessionStartDate = sessionStartDate;
		this.status = status;
		this.sessionId = sessionId;
		this.sessionName = sessionName;
		this.contentFolderId = contentFolderId;
		this.videoRecorder = videoRecorder;
		this.videoRecorderUsers = videoRecorderUsers;
	}
	
	public VideoRecorderSession(VideoRecorderSessionDTO videoRecorderSessionDTO) {
		this.sessionId = videoRecorderSessionDTO.getSessionID();
		this.sessionName = videoRecorderSessionDTO.getSessionName();
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
	 * @hibernate.property column="session_end_date"
	 * 
	 */

	public Date getSessionEndDate() {
		return this.sessionEndDate;
	}

	public void setSessionEndDate(Date sessionEndDate) {
		this.sessionEndDate = sessionEndDate;
	}

	/**
	 * @hibernate.property column="session_start_date"
	 * 
	 */

	public Date getSessionStartDate() {
		return this.sessionStartDate;
	}

	public void setSessionStartDate(Date sessionStartDate) {
		this.sessionStartDate = sessionStartDate;
	}

	/**
	 * @hibernate.property column="status" length="11"
	 * 
	 */

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @hibernate.property column="session_id" length="20"
	 * 
	 */

	public Long getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @hibernate.property column="session_name" length="250"
	 * 
	 */

	public String getSessionName() {
		return this.sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	
	/**
	 * @hibernate.property column="content_folder_id" length="32"
	 * 
	 */

	public String getContentFolderId() {
		return this.contentFolderId;
	}

	public void setContentFolderId(String contentFolderId) {
		this.contentFolderId = contentFolderId;
	}

	/**
	 * @hibernate.many-to-one not-null="true"
	 * @hibernate.column name="videoRecorder_uid"
	 * 
	 */

	public VideoRecorder getVideoRecorder() {
		return this.videoRecorder;
	}

	public void setVideoRecorder(VideoRecorder videoRecorder) {
		this.videoRecorder = videoRecorder;
	}

	/**
	 * @hibernate.set lazy="true" inverse="true" cascade="none"
	 * @hibernate.collection-key column="videoRecorder_session_uid"
	 * @hibernate.collection-one-to-many class="org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderUser"
	 * 
	 */

	public Set getVideoRecorderUsers() {
		return this.videoRecorderUsers;
	}

	public void setVideoRecorderUsers(Set videoRecorderUsers) {
		this.videoRecorderUsers = videoRecorderUsers;
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
		buffer.append("sessionEndDate").append("='")
				.append(getSessionEndDate()).append("' ");
		buffer.append("sessionStartDate").append("='").append(
				getSessionStartDate()).append("' ");
		buffer.append("status").append("='").append(getStatus()).append("' ");
		buffer.append("sessionID").append("='").append(getSessionId()).append(
				"' ");
		buffer.append("sessionName").append("='").append(getSessionName())
				.append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof VideoRecorderSession))
			return false;
		VideoRecorderSession castOther = (VideoRecorderSession) other;

		return ((this.getUid() == castOther.getUid()) || (this.getUid() != null
				&& castOther.getUid() != null && this.getUid().equals(
				castOther.getUid())));
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result
				+ (getUid() == null ? 0 : this.getUid().hashCode());
		return result;
	}

  	public Object clone(){
  		
  		VideoRecorderSession session = null;
  		try{
  			session = (VideoRecorderSession) super.clone();
  			session.videoRecorderUsers = new HashSet();
		} catch (CloneNotSupportedException e) {
			log.error("When clone " + VideoRecorderSession.class + " failed");
		}
  		return session;
  	}

}
