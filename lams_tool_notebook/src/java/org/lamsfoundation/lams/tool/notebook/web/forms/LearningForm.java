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

/* $Id$ */

package org.lamsfoundation.lams.tool.notebook.web.forms;

import org.apache.struts.action.ActionForm;

/**
 * 
 * @author Anthony Sukkar
 * 
 * @struts.form name="learningForm"
 */
public class LearningForm extends ActionForm {

	private static final long serialVersionUID = -4728946254882237144L;
	
	String title;
	String instructions;
	
	String dispatch;
	Long notebookUserUID;
	Long toolSessionID;
	String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Long getNotebookUserUID() {
		return notebookUserUID;
	}

	public void setNotebookUserUID(Long notebookUserUID) {
		this.notebookUserUID = notebookUserUID;
	}

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public Long getToolSessionID() {
		return toolSessionID;
	}

	public void setToolSessionID(Long toolSessionID) {
		this.toolSessionID = toolSessionID;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
