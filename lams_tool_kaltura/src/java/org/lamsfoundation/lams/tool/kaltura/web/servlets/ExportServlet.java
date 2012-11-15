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

package org.lamsfoundation.lams.tool.kaltura.web.servlets;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.kaltura.model.Kaltura;
import org.lamsfoundation.lams.tool.kaltura.model.KalturaSession;
import org.lamsfoundation.lams.tool.kaltura.service.IKalturaService;
import org.lamsfoundation.lams.tool.kaltura.service.KalturaServiceProxy;
import org.lamsfoundation.lams.tool.kaltura.util.KalturaException;
import org.lamsfoundation.lams.web.servlet.AbstractExportPortfolioServlet;
import org.lamsfoundation.lams.web.util.AttributeNames;

public class ExportServlet extends AbstractExportPortfolioServlet {

    private static final long serialVersionUID = -2829707715037631881L;

    private static Logger logger = Logger.getLogger(ExportServlet.class);

    private final String FILENAME = "kaltura_main.html";

    private IKalturaService kalturaService;

    protected String doExport(HttpServletRequest request, HttpServletResponse response, String directoryName,
	    Cookie[] cookies) {

	if (kalturaService == null) {
	    kalturaService = KalturaServiceProxy.getKalturaService(getServletContext());
	}

	try {
	    if (StringUtils.equals(mode, ToolAccessMode.LEARNER.toString())) {
		request.getSession().setAttribute(AttributeNames.ATTR_MODE, ToolAccessMode.LEARNER);
		doLearnerExport(request, response, directoryName, cookies);
	    } else if (StringUtils.equals(mode, ToolAccessMode.TEACHER.toString())) {
		request.getSession().setAttribute(AttributeNames.ATTR_MODE, ToolAccessMode.TEACHER);
		doTeacherExport(request, response, directoryName, cookies);
	    }
	} catch (KalturaException e) {
	    logger.error("Cannot perform export for kaltura tool.");
	}

	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
		+ request.getContextPath();
	writeResponseToFile(basePath + "/pages/export/exportPortfolio.jsp", directoryName, FILENAME, cookies);

	return FILENAME;
    }

    protected String doOfflineExport(HttpServletRequest request, HttpServletResponse response, String directoryName,
	    Cookie[] cookies) {
	if (toolContentID == null && toolSessionID == null) {
	    logger.error("Tool content Id or and session Id are null. Unable to activity title");
	} else {
	    if (kalturaService == null) {
		kalturaService = KalturaServiceProxy.getKalturaService(getServletContext());
	    }

	    Kaltura content = null;
	    if (toolContentID != null) {
		content = kalturaService.getKalturaByContentId(toolContentID);
	    } else {
		KalturaSession session = kalturaService.getSessionBySessionId(toolSessionID);
		if (session != null)
		    content = session.getKaltura();
	    }
	    if (content != null) {
		activityTitle = content.getTitle();
	    }
	}
	return super.doOfflineExport(request, response, directoryName, cookies);
    }

    private void doLearnerExport(HttpServletRequest request, HttpServletResponse response, String directoryName,
	    Cookie[] cookies) throws KalturaException {

	logger.debug("doExportLearner: toolContentID:" + toolSessionID);
    }

    private void doTeacherExport(HttpServletRequest request, HttpServletResponse response, String directoryName,
	    Cookie[] cookies) throws KalturaException {

	logger.debug("doExportTeacher: toolContentID:" + toolContentID);
    }

}
