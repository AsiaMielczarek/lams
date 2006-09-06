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
package org.lamsfoundation.lams.tool.noticeboard.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.tool.noticeboard.NoticeboardContent;
import org.lamsfoundation.lams.util.UploadFileUtil;


//import org.lamsfoundation.lams.tool.noticeboard.NoticeboardConstants;

/**
 * <p>ActionForm which holds the state of the noticeboard form content in the Authoring 
 * environment. Stores all values in the session scope.</p>
 * 
 * <p>The validate method does not check whether any of the input from
 * title, content, onlineInstructions, offlineInstructions are empty or not.
 * This is because I have encountered a situation where even though the field is
 * empty, the FCKEditor places a <br/> tag and so the validate method doesnt work.
 * However, the validate method checks the length of the file that has been uploaded,
 * the maximum filesize that can be uploaded is a property that is in the UploadFileUtil. </p>
 * 
 */

/**
 * Creation Date: 19-05-05
 *  
 * ----------------XDoclet Tags--------------------
 * 
 * @struts:form name="NbAuthoringForm" type="org.lamsfoundation.lams.tool.noticeboard.web.NbAuthoringForm"
 *
 * ----------------XDoclet Tags--------------------
 */

public class NbAuthoringForm extends ActionForm {
	
	private static final long serialVersionUID = -8425012664714570196L;

	static Logger logger = Logger.getLogger(NbAuthoringForm.class.getName());

	private String title;
	private String content;
	private String onlineInstructions;
	private String offlineInstructions;
	
	private String method;
	private String toolContentID;
	private String contentFolderID;
	private String defineLater;
	
	private FormFile onlineFile;
	private FormFile offlineFile;

	private String currentTab;
	private String sessionMapID;
	
	private Long deleteFileUuid;

	/**
     * @return Returns the offlineFile.
     */
    public FormFile getOfflineFile() {
        return offlineFile;
    }
    /**
     * @param offlineFile The offlineFile to set.
     */
    public void setOfflineFile(FormFile offlineFile) {
        this.offlineFile = offlineFile;
    }
    /**
     * @return Returns the onlineFile.
     */
    public FormFile getOnlineFile() {
        return onlineFile;
    }
    /**
     * @param onlineFile The onlineFile to set.
     */
    public void setOnlineFile(FormFile onlineFile) {
        this.onlineFile = onlineFile;
    }
    /**
     * @return Returns the defineLater.
     */
    public String getDefineLater() {
        return defineLater;
    }
    /**
     * @param defineLater The defineLater to set.
     */
    public void setDefineLater(String defineLater) {
        this.defineLater = defineLater;
    }
	/**
	 * @return Returns the content.
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content The content to set.
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return Returns the online instructions
	 */
	public String getOnlineInstructions()
	{
		return onlineInstructions;
	}
	
	/**
	 * @param onlineInstructions The online instructions to set
	 */
	public void setOnlineInstructions(String onlineInstructions)
	{
		this.onlineInstructions = onlineInstructions;
	}
	
	/**
	 * @return Returns the offline instructions
	 */
	public String getOfflineInstructions()
	{
		return offlineInstructions;
	}
	
	/**
	 * @param offlineInstructions The offline instructions to set
	 */
	public void setOfflineInstructions(String offlineInstructions)
	{
		this.offlineInstructions = offlineInstructions;
	}
	
	/**
	 * @return Returns the method.
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method The method to set.
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return Returns the toolContentID.
	 */
	public String getToolContentID() {
		return toolContentID;
	}
	/**
	 * @param toolContentID The toolContentID to set.
	 */
	public void setToolContentID(String toolContentID) {
		this.toolContentID = toolContentID;
	}

	/**
	 * 
	 * @return Returns the contentFolderID
	 */
	public String getContentFolderID() {
		return contentFolderID;
	}
	
	/**
	 * 
	 * @param contentFolderID The contentFolderID is set
	 */
	public void setContentFolderID(String contentFolderID) {
		this.contentFolderID = contentFolderID;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
	//	this.content = null;
		//this.title = null;
		//this.onlineInstructions = null;
		//this.offlineInstructions = null;
	    this.method= null;
	    this.defineLater = null;
	    this.onlineFile = null;
	    this.offlineFile = null;
	    this.deleteFileUuid = null;
	}
	
	/**
	 * This method is to prepopulate the form with values from a noticeboard content
	 * object. Used in AuthoringStarterAction
	 * @param nbContent
	 */
	public void populateFormWithNbContentValues(NoticeboardContent nbContent)
	{
		setTitle(nbContent.getTitle());
		setContent(nbContent.getContent());
		setOnlineInstructions(nbContent.getOnlineInstructions());
		setOfflineInstructions(nbContent.getOfflineInstructions());
	}
	
	public void copyValuesIntoNbContent(NoticeboardContent nbContent)
	{
		nbContent.setTitle(getTitle());
	    nbContent.setContent(getContent());
		nbContent.setOnlineInstructions(getOnlineInstructions());
		nbContent.setOfflineInstructions(getOfflineInstructions());
		nbContent.setDateUpdated(new Date(System.currentTimeMillis()));
	}
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
	    ActionErrors errors = new ActionErrors();
	    //check the tool content id
	    //check the title and instructions
	    
	    float maxFileSize = UploadFileUtil.getMaxFileSize();
	  
	   if (this.offlineFile != null && offlineFile.getFileSize()!= 0) 
	    {
	        float sizeOfOfflineUpload = offlineFile.getFileSize() / 1024 / 1024;
	        //check the file size
	       if (sizeOfOfflineUpload  > maxFileSize)
	        {
	            logger.error("file size " + sizeOfOfflineUpload + " Max File Size allowed: " + maxFileSize);
	            errors.add("filesize", new ActionMessage("error.exceedMaxFileSize"));
	            this.setMethod("Instructions");
	        }
	       
	    } 
	   else if ( this.onlineFile != null && onlineFile.getFileSize() != 0)
	   {
	       float sizeOfOnlineUpload = onlineFile.getFileSize() / 1024 / 1024; //getFileSize() returns the file size in bytes, but we are comparing the filesize using units MBs
		   
	     if (sizeOfOnlineUpload > maxFileSize) 
	     {

	         	logger.error("file size " + sizeOfOnlineUpload + " Max File Size allowed: " + maxFileSize);
	            errors.add("filesize", new ActionMessage("error.exceedMaxFileSize"));
	            this.setMethod("Instructions");
	     }
	   }
	   
	    return errors; 
	    
	}
	public String getCurrentTab() {
		return currentTab;
	}
	public void setCurrentTab(String currentTab) {
		this.currentTab = currentTab;
	}
	public String getSessionMapID() {
		return sessionMapID;
	}
	public void setSessionMapID(String sessionMapID) {
		this.sessionMapID = sessionMapID;
	}
	public Long getDeleteFileUuid() {
		return deleteFileUuid;
	}
	public void setDeleteFileUuid(Long deleteFileUuid) {
		this.deleteFileUuid = deleteFileUuid;
	}	
  
}
