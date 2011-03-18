/***************************************************************************
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License version 2 as
published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
USA

http://www.gnu.org/licenses/gpl.txt
* ***********************************************************************/

/* $$Id$$ */
package org.lamsfoundation.lams.tool.qa;

import java.util.Date;


/**
 * This test is designed to test all service provided by SurveyQueUsr domain 
 * class
 * @author ozgurd
 * 
 */

/**
 * Test case for TestQaUsrResp
 */

public class TestQaUsrResp extends QaDataAccessTestCase
{
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

    }

    
    public TestQaUsrResp(String name)
    {
        super(name);
    }
    
    
    public void testCreateNewResponse()
    {
//    	QaQuestion qaQueContent = qaQuestionDAO.getQaQueById(new Long(1).longValue());
//    	
//    	QaQueUsr qaQueUsr=qaQueUsrDAO.getQaQueUsrById(new Long(700).longValue()); 
//    	
//		QaUsrResp qaUsrResp= new QaUsrResp("I am from Sydney.",
//											new Date(System.currentTimeMillis()),
//											"",
//											qaQueContent,
//											qaQueUsr);
//		qaUsrRespDAO.createUserResponse(qaUsrResp);
    }
    
}
