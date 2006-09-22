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
package org.lamsfoundation.lams.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;
import org.lamsfoundation.lams.usermanagement.SupportedLocale;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author jliew
 *
 */

/**
 * @struts:action path="/saveprofile"
 *                name="UserForm"
 *                input=".editprofile"
 * 	              scope="request"
 * 				  validate="false"
 * 
 * @struts:action-forward name="profile" path="/profile.do?method=view"
 */
public class ProfileSaveAction extends Action {
	
	private static Logger log = Logger.getLogger(ProfileSaveAction.class);
	private static IUserManagementService service;

	public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		if(isCancelled(request)){
			return mapping.findForward("profile");
		}
		
		ActionMessages errors = new ActionMessages();
		User requestor = (User)getService().getUserByLogin(request.getRemoteUser());
		DynaActionForm userForm = (DynaActionForm)form;
		
		// check requestor is same as user being edited
		log.debug("requestor: "+requestor.getLogin()+", form login: "+userForm.get("login"));
		if(!requestor.getLogin().equals(userForm.get("login"))){
			errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("error.authorisation"));
			saveErrors(request,errors);
			return (mapping.getInputForward());
		}
		
		// (dyna)form validation
		if ((userForm.get("firstName") == null) || (userForm.getString("firstName").trim().length() == 0)) {
			errors.add("firstName", new ActionMessage("error.firstname.required"));
		}
		if ((userForm.get("lastName") == null) || (userForm.getString("lastName").trim().length() == 0)) {
			errors.add("lastName", new ActionMessage("error.lastname.required"));
		}
		if ((userForm.get("email") == null) || (userForm.getString("email").trim().length() == 0)) {
			errors.add("email", new ActionMessage("error.email.required"));
		} else {
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher m = p.matcher(userForm.getString("email"));
			if (!m.matches()) {
				errors.add("email", new ActionMessage("error.valid.email.required"));
			}
		}
		
		BeanUtils.copyProperties(requestor,userForm);
		SupportedLocale locale = (SupportedLocale) getService().findById(SupportedLocale.class, (Byte)userForm.get("localeId"));
		requestor.setLocale(locale);
		getService().save(requestor);
		log.debug("profile edited: "+requestor);
		
		return mapping.findForward("profile");
	}
	
	private IUserManagementService getService(){
		if(service==null){
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
			service = (IUserManagementService) ctx.getBean("userManagementServiceTarget");
		}
		return service;
	}

}