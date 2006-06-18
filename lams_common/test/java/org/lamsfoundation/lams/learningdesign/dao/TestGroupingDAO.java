/***************************************************************************
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
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.learningdesign.dao;

import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.dao.hibernate.GroupingDAO;
import org.lamsfoundation.lams.test.AbstractCommonTestCase;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.dao.IBaseDAO;
import org.lamsfoundation.lams.dao.hibernate.BaseDAO;

/**
 * 
 * @author Jacky Fang
 * @since  2005-3-31
 * @version
 * 
 */
public class TestGroupingDAO extends AbstractCommonTestCase
{
	protected IGroupingDAO groupingDAO;
	protected IBaseDAO baseDAO = null;
	private static Grouping testGrouping =null;	
	private static User testUser = null;
	private final Integer TEST_USER_ID = new Integer(2);
	private final Long TEST_GROUPING_ID = new Long(99);
	
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
		groupingDAO = (GroupingDAO)context.getBean("groupingDAO");
		baseDAO = (BaseDAO)context.getBean("userDAO");
		
		testUser = (User)baseDAO.find(User.class,TEST_USER_ID);
		
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Constructor for TestGroupingDAO.
     * @param arg0
     */
    public TestGroupingDAO(String testName)
    {
        super(testName);
    }

    public void testGetGrouping()
    {
        testGrouping = groupingDAO.getGroupingById(TEST_GROUPING_ID);
        
        assertNotNull("verify grouping",testGrouping);
        assertEquals("verify number of existing groups",1,testGrouping.getGroups().size());
        assertTrue("test learner shouldn't exist",!testGrouping.doesLearnerExist(testUser));
    }
    
    public void testUpdateGrouping()
    {
        testGrouping.getGrouper().doGrouping(testGrouping, "testgroup", testUser);
        
        groupingDAO.update(testGrouping);
        
        testGrouping = groupingDAO.getGroupingById(TEST_GROUPING_ID);
        assertEquals("verify number of groups",2,testGrouping.getGroups().size());
        assertTrue("tet learner should exist now",testGrouping.doesLearnerExist(testUser));
        
    }

}
