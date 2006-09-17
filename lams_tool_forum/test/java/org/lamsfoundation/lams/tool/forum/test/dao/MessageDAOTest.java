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

package org.lamsfoundation.lams.tool.forum.test.dao;

import java.util.List;

import org.lamsfoundation.lams.tool.forum.persistence.Forum;
import org.lamsfoundation.lams.tool.forum.persistence.ForumToolSession;
import org.lamsfoundation.lams.tool.forum.persistence.ForumUser;
import org.lamsfoundation.lams.tool.forum.persistence.Message;
import org.lamsfoundation.lams.tool.forum.test.DAOBaseTest;
import org.lamsfoundation.lams.tool.forum.test.TestUtils;

public class MessageDAOTest extends DAOBaseTest{

	public MessageDAOTest(String name) {
		super(name);
	}
	
	public void testSave(){
		Message msg = TestUtils.getMessageA();
		messageDao.saveOrUpdate(msg);
		Message tmsg = messageDao.getById(msg.getUid());
		assertEquals(msg,tmsg);
		
		messageDao.delete(msg.getUid());
	}
	public void testDelete(){
		Message msg = TestUtils.getMessageA();
		messageDao.saveOrUpdate(msg);
		messageDao.delete(msg.getUid());
		
		assertNull(messageDao.getById(msg.getUid()));
		
	}

	public void testGetBySession(){
		ForumToolSession sessionA = TestUtils.getSessionA();
		forumToolSessionDao.saveOrUpdate(sessionA);
		
		Message msgA = TestUtils.getMessageA();
		msgA.setToolSession(sessionA);
		messageDao.saveOrUpdate(msgA);
		Message msgB = TestUtils.getMessageB();
		msgB.setToolSession(sessionA);
		messageDao.saveOrUpdate(msgB);
		
		List list = messageDao.getBySession(new Long(1));
		
		assertEquals(2,list.size());
		assertEquals(list.get(0),msgA);
		assertEquals(list.get(1),msgB);
		
		//remove test data
		messageDao.delete(msgA.getUid());
		messageDao.delete(msgB.getUid());
		forumToolSessionDao.delete(sessionA);
	}
	public void testGetBySessionAndUser(){
		ForumToolSession sessionA = TestUtils.getSessionA();
		forumToolSessionDao.saveOrUpdate(sessionA);
		ForumUser userA = TestUtils.getUserA();
		forumUserDao.save(userA);
		
		Message msgA = TestUtils.getMessageA();
		msgA.setToolSession(sessionA);
		msgA.setCreatedBy(userA);
		messageDao.saveOrUpdate(msgA);
		Message msgB = TestUtils.getMessageB();
		msgB.setToolSession(sessionA);
		msgB.setCreatedBy(userA);
		messageDao.saveOrUpdate(msgB);
		
		List list = messageDao.getByUserAndSession(userA.getUid(),sessionA.getSessionId());
		
		assertEquals(2,list.size());
		assertEquals(list.get(0),msgA);
		assertEquals(list.get(1),msgB);
		
		//remove test data
		messageDao.delete(msgA.getUid());
		messageDao.delete(msgB.getUid());
		forumToolSessionDao.delete(sessionA);
		forumUserDao.delete(userA);
	}
	public void testGetFromAuthor(){
		ForumUser userA = TestUtils.getUserA();
		forumUserDao.save(userA);
		Forum forumA = TestUtils.getForumA();
		forumDao.saveOrUpdate(forumA);
		
		Message msgA = TestUtils.getMessageA();
		msgA.setCreatedBy(userA);
		msgA.setForum(forumA);
		msgA.setIsAuthored(true);
		messageDao.saveOrUpdate(msgA);
		Message msgB = TestUtils.getMessageB();
		msgB.setForum(forumA);
		msgB.setCreatedBy(userA);
		msgB.setIsAuthored(false);
		messageDao.saveOrUpdate(msgB);
		
		List list = messageDao.getTopicsFromAuthor(forumA.getUid());
		
		assertEquals(1,list.size());
		assertEquals(list.get(0),msgA);
		
		//remove test data
		messageDao.delete(msgA.getUid());
		messageDao.delete(msgB.getUid());
		forumUserDao.delete(userA);
		forumDao.delete(forumA);
		
	}
	public void testGetRootTopics(){
		ForumToolSession sessionA = TestUtils.getSessionA();
		forumToolSessionDao.saveOrUpdate(sessionA);
		
		Message msgA = TestUtils.getMessageA();
		msgA.setToolSession(sessionA);
		messageDao.saveOrUpdate(msgA);
		
		Message msgB = TestUtils.getMessageB();
		msgB.setParent(msgA);
		msgB.setToolSession(sessionA);
		messageDao.saveOrUpdate(msgB);
		
		List list = messageDao.getRootTopics(sessionA.getSessionId());
		
		assertEquals(1,list.size());
		assertEquals(list.get(0),msgA);
		
		//remove test data
		messageDao.delete(msgB.getUid());
		messageDao.delete(msgA.getUid());
		forumToolSessionDao.delete(sessionA);
	}
	public void testGetChildrenTopics(){
		Message msgA = TestUtils.getMessageA();
		messageDao.saveOrUpdate(msgA);
		
		Message msgB = TestUtils.getMessageB();
		msgB.setParent(msgA);
		messageDao.saveOrUpdate(msgB);
		
		List list = messageDao.getChildrenTopics(msgA.getUid());
		
		assertEquals(1,list.size());
		assertEquals(list.get(0),msgB);
		
		//remove test data
		messageDao.delete(msgB.getUid());
		messageDao.delete(msgA.getUid());
	}
	

}
