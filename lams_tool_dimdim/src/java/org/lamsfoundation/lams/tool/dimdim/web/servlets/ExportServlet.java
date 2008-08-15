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

package org.lamsfoundation.lams.tool.dimdim.web.servlets;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.dimdim.dto.ContentDTO;
import org.lamsfoundation.lams.tool.dimdim.dto.SessionDTO;
import org.lamsfoundation.lams.tool.dimdim.dto.DimdimUserDTO;
import org.lamsfoundation.lams.tool.dimdim.dto.NotebookEntryDTO;
import org.lamsfoundation.lams.tool.dimdim.model.Dimdim;
import org.lamsfoundation.lams.tool.dimdim.model.DimdimSession;
import org.lamsfoundation.lams.tool.dimdim.model.DimdimUser;
import org.lamsfoundation.lams.tool.dimdim.service.DimdimServiceProxy;
import org.lamsfoundation.lams.tool.dimdim.service.IDimdimService;
import org.lamsfoundation.lams.tool.dimdim.util.DimdimException;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.servlet.AbstractExportPortfolioServlet;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;

public class ExportServlet extends AbstractExportPortfolioServlet {

	private static final long serialVersionUID = -2829707715037631881L;

	private static Logger logger = Logger.getLogger(ExportServlet.class);

	private final String FILENAME = "dimdim_main.html";

	private IDimdimService dimdimService;

	protected String doExport(HttpServletRequest request,
			HttpServletResponse response, String directoryName, Cookie[] cookies) {

		if (dimdimService == null) {
			dimdimService = DimdimServiceProxy
					.getDimdimService(getServletContext());
		}

		try {
			if (StringUtils.equals(mode, ToolAccessMode.LEARNER.toString())) {
				request.getSession().setAttribute(AttributeNames.ATTR_MODE,
						ToolAccessMode.LEARNER);
				doLearnerExport(request, response, directoryName, cookies);
			} else if (StringUtils.equals(mode, ToolAccessMode.TEACHER
					.toString())) {
				request.getSession().setAttribute(AttributeNames.ATTR_MODE,
						ToolAccessMode.TEACHER);
				doTeacherExport(request, response, directoryName, cookies);
			}
		} catch (DimdimException e) {
			logger.error("Cannot perform export for dimdim tool.");
		}

		String basePath = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath();
		writeResponseToFile(basePath + "/pages/export/exportPortfolio.jsp",
				directoryName, FILENAME, cookies);

		return FILENAME;
	}

	protected String doOfflineExport(HttpServletRequest request,
			HttpServletResponse response, String directoryName, Cookie[] cookies) {
		if (toolContentID == null && toolSessionID == null) {
			logger
					.error("Tool content Id or and session Id are null. Unable to activity title");
		} else {
			if (dimdimService == null) {
				dimdimService = DimdimServiceProxy
						.getDimdimService(getServletContext());
			}

			Dimdim content = null;
			if (toolContentID != null) {
				content = dimdimService.getDimdimByContentId(toolContentID);
			} else {
				DimdimSession session = dimdimService
						.getSessionBySessionId(toolSessionID);
				if (session != null)
					content = session.getDimdim();
			}
			if (content != null) {
				activityTitle = content.getTitle();
			}
		}
		return super.doOfflineExport(request, response, directoryName, cookies);
	}

	private void doLearnerExport(HttpServletRequest request,
			HttpServletResponse response, String directoryName, Cookie[] cookies)
			throws DimdimException {

		logger.debug("doExportLearner: toolContentID:" + toolSessionID);

		// check if toolContentID available
		if (toolSessionID == null) {
			String error = "Tool Session ID is missing. Unable to continue";
			logger.error(error);
			throw new DimdimException(error);
		}

		DimdimSession dimdimSession = dimdimService
				.getSessionBySessionId(toolSessionID);

		Dimdim dimdim = dimdimSession.getDimdim();

		UserDTO lamsUserDTO = (UserDTO) SessionManager.getSession()
				.getAttribute(AttributeNames.USER);

		DimdimUser dimdimUser = dimdimService.getUserByUserIdAndSessionId(
				new Long(lamsUserDTO.getUserID()), toolSessionID);

		NotebookEntry dimdimEntry = dimdimService.getEntry(dimdimUser
				.getEntryUID());

		// construct dto's
		ContentDTO contentDTO = new ContentDTO();
		contentDTO.setTitle(dimdim.getTitle());
		contentDTO.setInstructions(dimdim.getInstructions());

		SessionDTO sessionDTO = new SessionDTO();
		sessionDTO.setSessionName(dimdimSession.getSessionName());
		sessionDTO.setSessionID(dimdimSession.getSessionId());

		// If the user hasn't put in their entry yet, dimdimEntry will be null;
		DimdimUserDTO userDTO = dimdimEntry != null ? new DimdimUserDTO(
				dimdimUser, dimdimEntry) : new DimdimUserDTO(dimdimUser);

		sessionDTO.getUserDTOs().add(userDTO);
		contentDTO.getSessionDTOs().add(sessionDTO);

		request.getSession().setAttribute("dimdimDTO", contentDTO);
	}

	private void doTeacherExport(HttpServletRequest request,
			HttpServletResponse response, String directoryName, Cookie[] cookies)
			throws DimdimException {

		logger.debug("doExportTeacher: toolContentID:" + toolContentID);

		// check if toolContentID available
		if (toolContentID == null) {
			String error = "Tool Content ID is missing. Unable to continue";
			logger.error(error);
			throw new DimdimException(error);
		}

		Dimdim dimdim = dimdimService.getDimdimByContentId(toolContentID);

		ContentDTO contentDTO = new ContentDTO(dimdim);

		// add the dimdimEntry for each user in each session

		for (SessionDTO session : contentDTO.getSessionDTOs()) {
			for (DimdimUserDTO user : session.getUserDTOs()) {
				NotebookEntry entry = dimdimService
						.getEntry(user.getEntryUID());
				if (entry != null) {
					NotebookEntryDTO entryDTO = new NotebookEntryDTO(entry);
					user.setEntryDTO(entryDTO);
				}
			}
		}

		request.getSession().setAttribute("dimdimDTO", contentDTO);
	}

}
