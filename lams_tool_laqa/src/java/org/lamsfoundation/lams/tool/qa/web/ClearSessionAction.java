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
package org.lamsfoundation.lams.tool.qa.web;

import javax.servlet.http.HttpSession;

import org.lamsfoundation.lams.authoring.web.LamsAuthoringFinishAction;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.web.util.AttributeNames;

/**
 * This class give a chance to clear HttpSession when user save/close authoring page.
 * @author Steve.Ni
 * ----------------XDoclet Tags--------------------
 * 
 * @struts:action path="/clearsession"   validate="false"
 *                
 * @version $Revision$
 */
public class ClearSessionAction extends LamsAuthoringFinishAction {

	@Override
	public void clearSession(String customiseSessionID,HttpSession session, ToolAccessMode mode) {
		if(mode.isAuthor()){
			session.removeAttribute(QaAction.SUBMIT_SUCCESS);
			session.removeAttribute(QaAction.TOOL_SERVICE);
			session.removeAttribute(QaAction.MAP_QUESTION_CONTENT);
			session.removeAttribute(QaAction.EDITACTIVITY_EDITMODE);
			session.removeAttribute(QaAction.RENDER_MONITORING_EDITACTIVITY);
			session.removeAttribute(QaAction.ATTACHMENT_LIST);
			session.removeAttribute(QaAction.DELETED_ATTACHMENT_LIST);
			session.removeAttribute(QaAction.USER_EXCEPTION_QUESTIONS_DUPLICATE);
			session.removeAttribute(QaAction.ACTIVITY_TITLE);
			session.removeAttribute(QaAction.ACTIVITY_INSTRUCTIONS);
			session.removeAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID);
			session.removeAttribute(QaAction.DEFINE_LATER_IN_EDIT_MODE);
			session.removeAttribute(QaAction.TARGET_MODE);
			session.removeAttribute(QaAction.REQUESTED_MODULE);
			session.removeAttribute(QaAction.ACTIVE_MODULE);
			session.removeAttribute(QaAction.IS_DEFINE_LATER);
			session.removeAttribute(QaAction.SHOW_AUTHORING_TABS);
			session.removeAttribute("queIndex");
		}
	}

		
}
