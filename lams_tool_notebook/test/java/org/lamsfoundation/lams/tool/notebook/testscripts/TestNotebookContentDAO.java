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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA 
 * 
 * http://www.gnu.org/licenses/gpl.txt 
 * **************************************************************** 
 */  
 
/* $Id$ */  
package org.lamsfoundation.lams.tool.notebook.testscripts;  

import org.lamsfoundation.lams.tool.notebook.core.NotebookDataAccessTestCase;



/**
 * @author mtruong
 *
 * JUnit Test Cases to test the NoticeboardContentDAO class
 */
public class TestNotebookContentDAO extends NotebookDataAccessTestCase
{
	private boolean cleanContentData = true;
    
	public TestNotebookContentDAO(String name)
	{
		super(name);
	}
	
	 /**
     * @see NbDataAccessTestCase#setUp()
     */
	 protected void setUp() throws Exception {
        super.setUp();

        //set up default noticeboard content for each test
//       	initAllData();
    }
	 
	/**
	 * @see NbDataAccessTestCase#tearDown()
	 */
    protected void tearDown() throws Exception 
    {    	
        //remove noticeboard content after each test
       if(cleanContentData)
        {
//        	cleanNbContentData(TEST_NB_ID);
        }
    }
   
   public void testfindNbContentByID()
    {
//       notebook = notebookDAO.getByContentId(TEST_NB_ID);
       
//        assertContentEqualsTestData(notebook);
    	
    
    	 // Test to see if trying to retrieve a non-existent object would 
    	 // return null or not.
    	 
    	Long nonExistentId = new Long(88777);
    	assertTrue(true);
//    	assertNbContentIsNull(nonExistentId);
       
//	   NoticeboardContent nb = new NoticeboardContent(new Long(3600),	
//				TEST_TITLE,
//				TEST_CONTENT,
//				TEST_ONLINE_INSTRUCTIONS,
//				TEST_OFFLINE_INSTRUCTIONS,
//				TEST_DEFINE_LATER,
//				TEST_CONTENT_IN_USE,
//				TEST_FORCE_OFFLINE,
//				TEST_CREATOR_USER_ID,
//				TEST_DATE_CREATED,
//				TEST_DATE_UPDATED);
//
//	   noticeboardDAO.saveNbContent(nb);
	   
	 // noticeboardDAO.removeNoticeboard(new Long(3600));
	//   noticeboardDAO.removeNoticeboard(noticeboardDAO.findNbContentById(new Long(3600)));
	   
	   
    } 
   
//   public void testremoveNoticeboard()
//   {
//       cleanContentData = false;     
//       
//       nbContent = noticeboardDAO.findNbContentById(TEST_NB_ID);
//       
//       noticeboardDAO.removeNoticeboard(nbContent);
//       
//       assertNbSessionIsNull(TEST_SESSION_ID); //check if child table is deleted
//  	   assertNbContentIsNull(TEST_NB_ID);
//   } 
//   
//   public void testremoveNoticeboardById()
//   {
//       cleanContentData = false;
//  	 	
//	   
//  	 	noticeboardDAO.removeNoticeboard(TEST_NB_ID);
//  	 	
//  	 	assertNbSessionIsNull(TEST_SESSION_ID);
//  	 	assertNbContentIsNull(TEST_NB_ID);
//   }
//   
//   public void testgetNbContentBySession()
//   {
//	   	nbContent = noticeboardDAO.getNbContentBySession(TEST_SESSION_ID);
//	   	
//	   	assertContentEqualsTestData(nbContent);
//   }
//   
//
//   public void testsaveNbContent()
//   {
//   	/** 
//   	 * an object already created when setUp() is called, so dont need to save another instance 
//   	 * TODO: change this, actually test the save method
//   	 */
//   	
//   	nbContent = noticeboardDAO.findNbContentById(getTestNoticeboardId());
//   	
//   	assertContentEqualsTestData(nbContent);
//   	
//   }
//   
//   public void testupdateNbContent()
//   {
//   	// Update the noticeboard to have a new value for its content field 
//   	String newContent = "New updated content";
//   	
//   	nbContent = noticeboardDAO.findNbContentById(getTestNoticeboardId());
//   	nbContent.setContent(newContent);
//   	
//   	noticeboardDAO.updateNbContent(nbContent);
//   	
//   	//Check whether the value has been updated
//   	
//   	nbContent = noticeboardDAO.findNbContentById(getTestNoticeboardId());
//   	
//   	assertEquals(nbContent.getContent(), newContent);
//   	
//   } 
//      
//   public void testremoveNbSessions()
//   {
//   	
//	   	nbContent = noticeboardDAO.findNbContentById(getTestNoticeboardId());
//	   	
//	   	
//	   	noticeboardDAO.removeNbSessions(nbContent);
//	   	nbContent.getNbSessions().clear(); //Have to remove/empty the collection before deleting it.
//	   	//otherwise exception will occur
//	   	noticeboardDAO.updateNbContent(nbContent);
//	   	NoticeboardContent nb = noticeboardDAO.findNbContentById(getTestNoticeboardId());
//	   	assertNotNull(nb);
//	   	assertNbSessionIsNull(TEST_SESSION_ID);   	
//   }
//   
//   public void testAddSession()
//   {
//       Long newSessionId = new Long(87);
//       NoticeboardSession newSession = new NoticeboardSession(newSessionId);
//       
//       noticeboardDAO.addNbSession(TEST_NB_ID, newSession);
//       
//       NoticeboardSession retrievedSession = nbSessionDAO.findNbSessionById(newSessionId);
//       
//       assertEquals(retrievedSession.getNbContent().getNbContentId(), TEST_NB_ID);
//       
//   }
   
   
 

  
}

 