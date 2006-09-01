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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $$Id$$ */	

package org.lamsfoundation.lams.tool.sbmt.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.lamsfoundation.lams.tool.sbmt.Learner;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;

/**
 * @author Manpreet Minhas
 * @serial 5900249986365640342L
 */
public class LearnerDetailsDTO implements Serializable{
	
	private static final long serialVersionUID = 5900249986365640342L;
	private Long toolSessionID;
	
	//learner personal info dto
	private UserDTO userDto;
	
	//learner uploaded file info
	private String fileName;
	private String fileDescription;
	private String comments;	
	private Long marks;	
	private Date dateOfSubmission;
	private Date dateMarksReleased;
	
	private List filesUploaded;

	private boolean hasRefection;
	private String reflectInstrctions;
	private boolean finishReflection;
	private String reflect;
	
	public LearnerDetailsDTO(){
		
	}
	public LearnerDetailsDTO(UserDTO userDto){
		this.setUserDto(userDto);
	}
	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return Returns the dateMarksReleased.
	 */
	public Date getDateMarksReleased() {
		return dateMarksReleased;
	}
	/**
	 * @param dateMarksReleased The dateMarksReleased to set.
	 */
	public void setDateMarksReleased(Date dateMarksReleased) {
		this.dateMarksReleased = dateMarksReleased;
	}
	/**
	 * @return Returns the dateOfSubmission.
	 */
	public Date getDateOfSubmission() {
		return dateOfSubmission;
	}
	/**
	 * @param dateOfSubmission The dateOfSubmission to set.
	 */
	public void setDateOfSubmission(Date dateOfSubmission) {
		this.dateOfSubmission = dateOfSubmission;
	}
	/**
	 * @return Returns the fileDescription.
	 */
	public String getFileDescription() {
		return fileDescription;
	}
	/**
	 * @param fileDescription The fileDescription to set.
	 */
	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}
	/**
	 * @return Returns the name.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param name The name to set.
	 */
	public void setFileName(String name) {
		this.fileName = name;
	}	
	/**
	 * @return Returns the marks.
	 */
	public Long getMarks() {
		return marks;
	}
	/**
	 * @param marks The marks to set.
	 */
	public void setMarks(Long marks) {
		this.marks = marks;
	}
	/**
	 * @return Returns the toolSessionID.
	 */
	public Long getToolSessionID() {
		return toolSessionID;
	}
	/**
	 * @param toolSessionID The toolSessionID to set.
	 */
	public void setToolSessionID(Long toolSessionID) {
		this.toolSessionID = toolSessionID;
	}

	/**
	 * @return Returns the filesUploaded.
	 */
	public List getFilesUploaded() {
		return filesUploaded;
	}
	/**
	 * @param filesUploaded The filesUploaded to set.
	 */
	public void setFilesUploaded(List filesUploaded) {
		this.filesUploaded = filesUploaded;
	}

	public UserDTO getUserDto() {
		return userDto;
	}
	public void setUserDto(UserDTO userDto) {
		this.userDto = userDto;
	}
	public boolean isFinishReflection() {
		return finishReflection;
	}
	public void setFinishReflection(boolean finishReflection) {
		this.finishReflection = finishReflection;
	}
	public boolean isHasRefection() {
		return hasRefection;
	}
	public void setHasRefection(boolean hasRefection) {
		this.hasRefection = hasRefection;
	}
	public String getReflect() {
		return reflect;
	}
	public void setReflect(String reflect) {
		this.reflect = reflect;
	}
	public String getReflectInstrctions() {
		return reflectInstrctions;
	}
	public void setReflectInstrctions(String reflectInstrctions) {
		this.reflectInstrctions = reflectInstrctions;
	}
}
