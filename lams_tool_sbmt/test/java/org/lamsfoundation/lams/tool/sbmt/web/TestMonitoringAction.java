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

/* $$Id$$ */	

package org.lamsfoundation.lams.tool.sbmt.web;

import servletunit.struts.MockStrutsTestCase;

/**
 * None of these tests are valid - the getStatus method 
 * doesn't exist and markFiles should be markFile and take 
 * different parameters. FM Sept 05
 * 
 * @author Manpreet Minhas
 */
public class TestMonitoringAction extends MockStrutsTestCase {
	
	public TestMonitoringAction(String name){
		super(name);
	}
	public void testGetStatus(){
		setConfigFile("/WEB-INF/struts/struts-config.xml");
		setRequestPathInfo("/monitoring");
		addRequestParameter("method","getStatus");
		addRequestParameter("contentID","1");
		actionPerform();
		verifyForward("status");
		verifyNoActionErrors();
	}
	public void testMarkFiles(){
		setConfigFile("/WEB-INF/struts/struts-config.xml");
		setRequestPathInfo("/monitoring");
		addRequestParameter("method","markFiles");
		addRequestParameter("contentID","1");
		addRequestParameter("userID","1");
		actionPerform();
		verifyForward("userReport");
		verifyNoActionErrors();
	}

}
