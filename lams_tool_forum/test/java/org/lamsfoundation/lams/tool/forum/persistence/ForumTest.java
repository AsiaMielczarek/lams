package org.lamsfoundation.lams.tool.forum.persistence;

import org.lamsfoundation.lams.tool.forum.core.FactoryException;
import org.lamsfoundation.lams.tool.forum.core.GenericObjectFactoryImpl;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: conradb
 * Date: 7/06/2005
 * Time: 10:38:21
 * To change this template use File | Settings | File Templates.
 */
public class ForumTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateAndDeleteForum() throws FactoryException {
        //Populate a Forum entity for test purposes
        Forum entity = new Forum();
        entity.setTitle("Lams Forum");
        entity.setLockWhenFinished(false);
        entity.setForceOffline(true);
        entity.setAllowAnnomity(true);
        entity.setCreatedBy(new Long("1000"));

        Set attachments = new HashSet();

        AttachmentDao attachmentDao = (AttachmentDao) GenericObjectFactoryImpl.getInstance().lookup(AttachmentDao.class);
        Attachment instructions = new Attachment();
        instructions.setName("instructions");
        instructions.setType(true);
        attachments.add(instructions);
        attachmentDao.saveOrUpdate(instructions);

        /*
        Attachment onLineInstructions = new Attachment();
        onLineInstructions.setData("on line instructions byte array".getBytes());
        instructions.setType("ONLINEINSTRUCTIONS");
        attachments.add(onLineInstructions);
        attachmentDao.saveOrUpdate(onLineInstructions);

        Attachment offLineInstructions = new Attachment();
        offLineInstructions.setData("off line instructions byte array".getBytes());
        instructions.setType("OFFLINEINSTRUCTIONS");
        attachments.add(offLineInstructions);
        attachmentDao.saveOrUpdate(offLineInstructions);*/

        entity.setAttachments(attachments);

        //save
        ForumDao dao = (ForumDao) GenericObjectFactoryImpl.getInstance().lookup(ForumDao.class);
        dao.saveOrUpdate(entity);
        assertNotNull(entity.getId());
        assertNotNull("date created is null", entity.getCreated());
        assertNotNull("date updated is null", entity.getUpdated());
        assertEquals("date created and updated are different for first save", entity.getCreated(), entity.getUpdated());

        //load
        Forum reloaded = (Forum) dao.getById(entity.getId());
        assertEquals("reloaded object not equal", entity, reloaded);
        assertEquals("date difference in database and memory", entity.getCreated().getTime()/1000, reloaded.getCreated().getTime()/1000);
        assertEquals("date difference in database and memory", entity.getUpdated().getTime()/1000, reloaded.getUpdated().getTime()/1000);
         assertEquals("title should be Lams Forum", "Lams Forum", reloaded.getTitle());
        assertEquals("lockWhenFinished should be false", false, reloaded.getLockWhenFinished());
        assertEquals("forceOffline should be true", true, reloaded.getForceOffline());
        assertEquals("allowAnnomity should be true", true, reloaded.getAllowAnnomity());
            //validate attachment relations
        assertEquals("should have 1 attachments", 1, reloaded.getAttachments().size());
        Set reloadedSet = reloaded.getAttachments();

        assertTrue("reloaded set does not contain instructions attachment", reloadedSet.contains(instructions));
        Attachment[] child = (Attachment[]) reloadedSet.toArray(new Attachment[0]);
        assertEquals("attachments type should be same", instructions.getType(), child[0].getType());
        assertEquals("attachment bytes should be the same", new String(instructions.getName()), new String(child[0].getName()));

        //find
        List values = dao.findByNamedQuery("allForums");
        assertTrue("find all result not containing object", values.contains(entity));

        //delete
        dao.delete(reloaded);
        assertNull("object not deleted", dao.getById(entity.getId()) );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
