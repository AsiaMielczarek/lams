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
/*
 * 
 * @author ozgurd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * Test case for QaQuestion
 */

public class TestQaQueContent extends QaDataAccessTestCase
{
	protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public TestQaQueContent(String name)
    {
        super(name);
    }
    
/*    
    public void testCreateQaQueContent()
    {
    	QaContent qaContent = qaContentDAO.getQaById(DEFAULT_CONTENT_ID);	
    	System.out.println(this.getClass().getName() + "qa content: " + qaContent);
    	
    	QaQuestion qaQuestion=  new QaQuestion("ozgur's new question test", 
    													1, 
														qaContent,
														null,
														null);
    	   	
    	System.out.println(this.getClass().getName() + " qaQuestion: " + qaQuestion);
    	qaQuestionDAO.createQueContent(qaQuestion);
    	System.out.println(this.getClass().getName() + " qaQuestion created: ");
    }
 */
 
   /* 
    public void testCreateQaQueContentMap()
    {
    	Map mapQuestionContent= new TreeMap();
    	mapQuestionContent.put(new Long(1).toString(), "questionEntry -1");
    	mapQuestionContent.put(new Long(2).toString(), "questionEntry -2");
    	
    	QaContent qaContent = qaContentDAO.getQaById(DEFAULT_CONTENT_ID);	
    	System.out.println(this.getClass().getName() + "qa content: " + qaContent);
    	
    	
    	Iterator itMap = mapQuestionContent.entrySet().iterator();
	    while (itMap.hasNext()) 
	    {
	        Map.Entry pairs = (Map.Entry)itMap.next();
	        
	        QaQuestion queContent=  new QaQuestion(pairs.getValue().toString(), 
	        											new Long(pairs.getKey().toString()).intValue(),
	        											qaContent,
														null,
														null);
		
	        queContent.setQaContent(qaContent);
	        qaContent.getQaQueContents().add(queContent);
	        qaContentDAO.createQa(qaContent);
	        System.out.println(this.getClass().getName() + " queContent: " + queContent);
	    }
    }
  */  
    public void testCreateDefaultQaQueContent()
    {
//    	QaQuestion defaultQaQueContent = qaQuestionDAO.getQaQueById(TEST_EXISTING_QUE_CONTENT_ID);	
//    	System.out.println(this.getClass().getName() + " Default qa que content: " + defaultQaQueContent);
    }
    
    
    
    
    
}