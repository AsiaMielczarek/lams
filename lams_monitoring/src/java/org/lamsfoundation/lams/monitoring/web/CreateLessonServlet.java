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
package org.lamsfoundation.lams.monitoring.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.monitoring.MonitoringConstants;
import org.lamsfoundation.lams.monitoring.service.IMonitoringService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.util.audit.IAuditService;
import org.lamsfoundation.lams.util.wddx.FlashMessage;
import org.lamsfoundation.lams.web.servlet.AbstractStoreWDDXPacketServlet;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author Steve.Ni
 * 
 * @version $Revision$
 * @web:servlet name="createLessonClass"
 * @web:servlet-mapping url-pattern="/createLessonClass"
 */
public class CreateLessonServlet  extends AbstractStoreWDDXPacketServlet {
    //---------------------------------------------------------------------
    // Instance variables
    //---------------------------------------------------------------------
	private static Logger log = Logger.getLogger(CreateLessonServlet.class);
	private static final long serialVersionUID = 6474150792777819606L;
	private static IAuditService auditService;

	protected String process(String lessonPackage, HttpServletRequest request) throws Exception {
		auditService = getAuditService();
		//get User infomation from shared session.
		HttpSession ss = SessionManager.getSession();
    	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
    	Integer userID = new Integer(WebUtil.readIntParam(request,AttributeNames.PARAM_USER_ID));
    	if(userID == null){
    		log.error("Can not find valid login user information");
    		FlashMessage flashMessage = new FlashMessage("createLesson",
					"Can not find valid login user information",
					FlashMessage.ERROR);
    		return flashMessage.serializeMessage();
    	}
    	
    	if ( log.isDebugEnabled() ) {
    		log.debug("CreateLessonServlet process received packet "+lessonPackage);
    	}
    	
    	try {
			IMonitoringService monitoringService = getMonitoringService();
			return monitoringService.createLessonClassForLessonWDDX(userID,lessonPackage);
    	} catch ( Exception e ) {
    		log.error("Exception thrown while creating lesson class.",e);
    		FlashMessage flashMessage = FlashMessage.getExceptionOccured("createLesson",e.getMessage());
    		auditService.log(CreateLessonServlet.class.getName(), e.getMessage());
    		return flashMessage.serializeMessage();
    	}
	}

	protected String getMessageKey(String packet, HttpServletRequest request) {
		return MonitoringConstants.CREATE_LESSON_MESSAGE_KEY;
	}
	

	public IMonitoringService getMonitoringService(){
		WebApplicationContext webContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		return (IMonitoringService) webContext.getBean(MonitoringConstants.MONITORING_SERVICE_BEAN_NAME);		
	}
	
	/**
	 * Get AuditService bean.
	 * @return
	 */
	private IAuditService getAuditService(){
		if(auditService==null){
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
			auditService = (IAuditService) ctx.getBean("auditService");
		}
		return auditService;
	}

}
