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

package org.lamsfoundation.lams.tool.dimdim.web.actions;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.MappingDispatchAction;
import org.lamsfoundation.lams.tool.dimdim.service.DimdimServiceProxy;
import org.lamsfoundation.lams.tool.dimdim.service.IDimdimService;

/**
 * @struts.action path="/admin/editConfig" name="adminForm"
 *                parameter="editConfig" scope="request" validate="false"
 * @struts.action-forward name="editConfig-success"
 *                        path="tiles:/admin/editConfig"
 * 
 * @struts.action path="/admin/saveConfig" name="adminForm"
 *                parameter="saveConfig" scope="request" validate="false"
 * @struts.action-forward  name="saveConfig" path="tiles:/admin/saveConfig"
 * 
 * @author asukkar
 * 
 */
public class AdminAction extends MappingDispatchAction {

	private IDimdimService dimdimService;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (dimdimService == null) {
			dimdimService = DimdimServiceProxy.getDimdimService(this
					.getServlet().getServletContext());
		}

		return super.execute(mapping, form, request, response);
	}

	public ActionForward editConfig(ActionMapping mapping, ActionForm form,
			ServletRequest request, ServletResponse response) throws Exception {
		
		
		
		return mapping.findForward("editConfig-success");
	}

	public ActionForward saveConfig(ActionMapping mapping, ActionForm form,
			ServletRequest request, ServletResponse response) throws Exception {

		return mapping.findForward("saveConfig-success");
	}

}
