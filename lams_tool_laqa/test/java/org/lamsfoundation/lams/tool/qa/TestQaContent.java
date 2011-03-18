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

import org.lamsfoundation.lams.tool.service.ILamsToolService;



/*
 * 
 * @author ozgurd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * Test case for QaContent
 */

public class TestQaContent extends QaDataAccessTestCase
{
	public org.lamsfoundation.lams.tool.dao.IToolDAO toolDAO;
	public ILamsToolService lamsToolService;
	
	protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public TestQaContent(String name)
    {
        super(name);
    }
    
    
   /* 
    public void testCreateNewQaContent()
    {
    	//create new qa content
    	QaContent qa = new QaContent();
		qa.setQaContentId(new Long(TEST_NEW_CONTENT_ID));
		qa.setTitle("New - Put Title Here");
		qa.setInstructions("New - Put instructions here.");
		qa.setCreationDate(new Date(System.currentTimeMillis()));
		qa.setCreatedBy(0);
	    qa.setUsernameVisible(false);
	    qa.setDefineLater(false);
	    qa.setSynchInMonitor(false);
	    qa.setOnlineInstructions("New- online instructions");
	    qa.setOfflineInstructions("New- offline instructions");
	    qa.setReportTitle("New-Report title");
	    qa.setQaQueContents(new TreeSet());
	    qa.setQaSessions(new TreeSet());
	    
	    //create new qa que content
	    QaQuestion qaQuestion = new QaQuestion("What planet are you from",
	    											4, 
													qa, 
													new TreeSet(), 
													new TreeSet());
	    
	    qa.getQaQueContents().add(qaQuestion);
	    
	    //create the new content
	    qaContentDAO.createQa(qa);
	    
	    
        qaSession = new QaSession(new Long(TEST_NEW_SESSION_ID),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()+ ONE_DAY),
                this.NOT_ATTEMPTED, 
				qa,
                new TreeSet());
        
        qaSession.setQaContent(qa);
        
        //create new qa que response
        qaQuestion.getQaUsrResps().add(getNewResponse("I am from Venus", qaQuestion));
        
	    //create a new session with the  new content
        qaSessionDAO.CreateQaSession(qaSession);
        System.out.println(this.getClass().getName() + " New session created: " + qa);
   }
   */
    
    
/*

    public void testCreateDefaultQaContent()
    {
    	QaContent defaultQaContent = qaContentDAO.getQaById(DEFAULT_CONTENT_ID);	
    	System.out.println(this.getClass().getName() + " Default qa content: " + defaultQaContent.getQaQueContents());
    	Iterator queIterator=defaultQaContent.getQaQueContents().iterator();
    	System.out.println(this.getClass().getName() + " \nqueIterator: " + queIterator);
    	while (queIterator.hasNext())
    	{
    		System.out.println(this.getClass().getName() + " \nin loop");
    		QaQuestion qaQuestion=(QaQuestion) queIterator.next();
    		System.out.println(this.getClass().getName() + " \nquestion: " + qaQuestion.getQuestion());
    	}
    }

*/   
    
    
  /*  
    public void testCreateExistingQaContent()
    {
    	QaContent defaultQaContent = qaContentDAO.getQaById(242453535L);
    	if (defaultQaContent == null)
		{
    		System.out.println(this.getClass().getName() + " Default qa content: " + defaultQaContent);
		}
    	else
    	{
    		System.out.println(this.getClass().getName() + " other content: ");
    	}
    }
  */
    
    
    /*
    public void testIdGeneration()
    {
    	System.out.println(this.getClass().getName() + " generated id: " + QaUtils.generateId());
    }
    */
    
    /*
    public void testRemoveNewQaContent()
    {
    	qaContentDAO.removeQa(new Long(TEST_NEW_CONTENT_ID));   
        System.out.println("New content removed: ");
    }
    */

    /*
    public void testRemoveQaContent()
    {
    	
    	qaContentDAO.removeQa(new Long(DEFAULT_CONTENT_ID));
    	System.out.println(this.getClass().getName() + " DEFAULT_CONTENT_ID removed");
    }
    */
 
    /*
    public void testRemoveQaContent()
    {
    	QaContent qaContent = qaContentDAO.loadQaById(TEST_NONEXISTING_CONTENT_ID);
    	System.out.println(this.getClass().getName() + "qaContent loaded : " + qaContent);
    }
    */
/*    
    public void testTimeZone()
    {
    	TimeZone timeZone=TimeZone.getDefault();
    	System.out.println("timeZone: " + timeZone.getDisplayName());
    }
    
    public void testDateTime()
    {
    	 Date now = new Date();
    	 System.out.println("10. " + DateFormat.getDateTimeInstance(
         DateFormat.LONG, DateFormat.LONG).format(now));	
    }
*/    
    
}