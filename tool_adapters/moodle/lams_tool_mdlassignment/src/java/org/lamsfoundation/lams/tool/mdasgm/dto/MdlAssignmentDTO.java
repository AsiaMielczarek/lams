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

package org.lamsfoundation.lams.tool.mdasgm.dto;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.lamsfoundation.lams.tool.mdasgm.model.MdlAssignment;
import org.lamsfoundation.lams.tool.mdasgm.model.MdlAssignmentSession;

public class MdlAssignmentDTO {

    private Long uid;
    private Date createDate;
    private Date updateDate;
    private boolean defineLater;
    private Long toolContentId;
    private Long extToolContentId;
    private String extUsername;
    private String extCourseId;
    private String extSection;

    public Set<MdlAssignmentSessionDTO> sessionDTOs = new TreeSet<MdlAssignmentSessionDTO>();

    public Long currentTab;

    /* Constructors */
    public MdlAssignmentDTO() {
    }

    public MdlAssignmentDTO(MdlAssignment mdlAssignment) {
	uid = mdlAssignment.getUid();
	createDate = mdlAssignment.getCreateDate();
	updateDate = mdlAssignment.getUpdateDate();
	defineLater = mdlAssignment.isDefineLater();
	toolContentId = mdlAssignment.getToolContentId();
	extToolContentId = mdlAssignment.getExtToolContentId();
	extUsername = mdlAssignment.getExtUsername();
	extCourseId = mdlAssignment.getExtCourseId();
	extSection = mdlAssignment.getExtSection();
	for (Iterator<MdlAssignmentSession> iter = mdlAssignment.getMdlAssignmentSessions().iterator(); iter.hasNext();) {
	    MdlAssignmentSession session = (MdlAssignmentSession) iter.next();
	    MdlAssignmentSessionDTO sessionDTO = new MdlAssignmentSessionDTO(session);

	    sessionDTOs.add(sessionDTO);
	}
    }

    /* Getters / Setters */
    public Set<MdlAssignmentSessionDTO> getSessionDTOs() {
	return sessionDTOs;
    }

    public void setSessionDTOs(Set<MdlAssignmentSessionDTO> sessionDTOs) {
	this.sessionDTOs = sessionDTOs;
    }

    public Long getToolContentId() {
	return toolContentId;
    }

    public void setToolContentId(Long toolContentID) {
	this.toolContentId = toolContentID;
    }

    public Long getCurrentTab() {
	return currentTab;
    }

    public void setCurrentTab(Long currentTab) {
	this.currentTab = currentTab;
    }

    public Long getUid() {
	return uid;
    }

    public void setUid(Long uid) {
	this.uid = uid;
    }

    public Date getCreateDate() {
	return createDate;
    }

    public void setCreateDate(Date createDate) {
	this.createDate = createDate;
    }

    public Date getUpdateDate() {
	return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
	this.updateDate = updateDate;
    }

    public boolean isDefineLater() {
	return defineLater;
    }

    public void setDefineLater(boolean defineLater) {
	this.defineLater = defineLater;
    }

    public Long getExtToolContentId() {
	return extToolContentId;
    }

    public void setExtToolContentId(Long extToolContentId) {
	this.extToolContentId = extToolContentId;
    }

    public String getExtUsername() {
	return extUsername;
    }

    public void setExtUsername(String extUsername) {
	this.extUsername = extUsername;
    }

    public String getExtCourseId() {
	return extCourseId;
    }

    public void setExtCourseId(String extCourseId) {
	this.extCourseId = extCourseId;
    }

    public String getExtSection() {
	return extSection;
    }

    public void setExtSection(String extSection) {
	this.extSection = extSection;
    }

}
