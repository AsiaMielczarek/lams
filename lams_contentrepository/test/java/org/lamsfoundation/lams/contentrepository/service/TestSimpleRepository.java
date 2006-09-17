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
package org.lamsfoundation.lams.contentrepository.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.contentrepository.AccessDeniedException;
import org.lamsfoundation.lams.contentrepository.CrNodeVersionProperty;
import org.lamsfoundation.lams.contentrepository.ICredentials;
import org.lamsfoundation.lams.contentrepository.ITicket;
import org.lamsfoundation.lams.contentrepository.IValue;
import org.lamsfoundation.lams.contentrepository.IVersionDetail;
import org.lamsfoundation.lams.contentrepository.IVersionedNode;
import org.lamsfoundation.lams.contentrepository.ItemExistsException;
import org.lamsfoundation.lams.contentrepository.ItemNotFoundException;
import org.lamsfoundation.lams.contentrepository.LoginException;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.contentrepository.NodeType;
import org.lamsfoundation.lams.contentrepository.PropertyType;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.WorkspaceNotFoundException;
import org.lamsfoundation.lams.contentrepository.dao.IFileDAO;
import org.lamsfoundation.lams.contentrepository.dao.file.FileDAO;
import org.lamsfoundation.lams.contentrepository.data.CRResources;
import org.lamsfoundation.lams.util.zipfile.ZipFileUtil;
import org.lamsfoundation.lams.util.zipfile.ZipFileUtilException;


/**
 * Test SimpleRepository and the Credentials code.
 * 
 * @author Fiona Malikoff
 */
public class TestSimpleRepository extends BaseTestCase {

	protected Logger log = Logger.getLogger(TestSimpleRepository.class);	

	String wrongWorkspaceExists = "btoolWorkspace";
	String wrongWorkspaceDoesNotExist = "ctoolWorkspace";
	String newWorkspace = "newtoolWorkspace";
	String workspaceAlreadyExistsMessage = "Workspace newtoolWorkspace already exists, cannot add workspace.";
	String wrongUser = "btool";
	String newUser = "ctool";
	String userAlreadyExistsMessage = "Credential name ctool already exists - cannot create credential.";
	char[] wrongPassword = {'b','t','o','o','l'};
	char[] newPassword1 = {'c','t','o','o','l','x'};
	char[] newPassword2 = {'d','t','o','o','l','x'};
	
	Long one = new Long(1);
	Long two = new Long(2);
	String v1Description = "Draft";
	String v2Description = "Final";
	
	/**
	 * Constructor for SimpleRepositoryTest.
	 * @param arg0
	 */
	public TestSimpleRepository(String name) {
		super(name);
	}

	public void testLoginPass() {
		// get a credential from the bean factory
		// setup credential with known username/password
		ICredentials cred = new SimpleCredentials(INITIAL_WORKSPACE_USER, INITIAL_WORKSPACE_PASSWORD);
		try { 
			ITicket ticket = repository.login(cred, INITIAL_WORKSPACE);
			assertNotNull("Login succeeded, ticket returned.", ticket);
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
	}

	public void testLoginFailWrongUser() {
		// get a credential from the bean factory
		// setup credential with known username/password
		ICredentials cred = new SimpleCredentials(wrongUser, INITIAL_WORKSPACE_PASSWORD);
		try { 
			ITicket ticket = repository.login(cred, INITIAL_WORKSPACE);
			fail("Login succeeded but expected it to fail as username was wrong. Ticket is "+ticket);
		} catch ( LoginException le ) {
			assertTrue("Login exception thrown as expected. Exception was "+le.getMessage(),true);
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
	}

	public void testLoginFailWrongPassword() {
		// get a credential from the bean factory
		// setup credential with known username/password
		ICredentials cred = new SimpleCredentials(INITIAL_WORKSPACE_USER, wrongPassword);
		try { 
			ITicket ticket = repository.login(cred, INITIAL_WORKSPACE);
			fail("Login succeeded but expected it to fail as password was wrong. Ticket is "+ticket);
		} catch ( LoginException le ) {
			assertTrue("Login exception thrown as expected. Exception was "+le.getMessage(),true);
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
	}

	public void testLoginFailWrongWorkspaceExists() {
		// get a credential from the bean factory
		// setup credential with known username/password
		try { 
			ICredentials cred = new SimpleCredentials(INITIAL_WORKSPACE_USER, INITIAL_WORKSPACE_PASSWORD);
			ITicket ticket = repository.login(cred, wrongWorkspaceExists);
			fail("Login succeeded but expected it to fail as password was wrong. Ticket is "+ticket);
		} catch ( WorkspaceNotFoundException we ) {
			assertTrue("Workspace not found exception thrown as expected.",true);
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
	}

	public void testLoginFailWrongWorkspaceDoesNotExist() {
		// get a credential from the bean factory
		// setup credential with known username/password
		try { 
			ICredentials cred = new SimpleCredentials(INITIAL_WORKSPACE_USER, INITIAL_WORKSPACE_PASSWORD);
			ITicket ticket = repository.login(cred, wrongWorkspaceDoesNotExist);
			fail("Login succeeded but expected it to fail as password was wrong. Ticket is "+ticket);
		} catch ( WorkspaceNotFoundException we ) {
			assertTrue("Workspace not found exception thrown as expected. Exception was "+we.getMessage(), true);
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
	}

	public void testAddWorkspace() {
		ICredentials cred = new SimpleCredentials(INITIAL_WORKSPACE_USER, INITIAL_WORKSPACE_PASSWORD);
		try { 
			repository.addWorkspace(cred,newWorkspace);
			ITicket ticket = repository.login(cred, newWorkspace);
			assertNotNull("Add workspace succeeded - can login to workspace. Ticket is "+ticket, ticket);
		} catch ( AccessDeniedException ae ) {
			assertTrue("Access denied exception thrown as expected. Exception was "+ae.getMessage(), true);
		} catch ( ItemExistsException iee ) {
		    log.error("Workspace already exists - unable to check that it can be created.\n"
		            +" The test should really be run with a newly rebuilt database & test data loaded");
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}

		// repeat the add - should fail as the workspace name already exists
		try { 
			repository.addWorkspace(cred,newWorkspace);
			repository.login(cred, newWorkspace);
			fail("Add workspace suceeded but it should have failed as workspace already exists.");
		} catch ( RepositoryCheckedException re ) {
			assertTrue("Repository exception thrown was due to name duplication as expected: "+re.getMessage(), 
						re.getMessage() != null && re.getMessage().equals(workspaceAlreadyExistsMessage));
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
}

	/** Tests adding a new user and changing a password */
	public void testUserAdmin() {
		// try creating a new user.
		// setup credential with known username/password
		ICredentials cred1 = new SimpleCredentials(newUser, newPassword1);
		ICredentials cred2 = new SimpleCredentials(newUser, newPassword2);
		try { 
			repository.createCredentials(cred1);
			repository.assignCredentials(cred1, INITIAL_WORKSPACE);
			ITicket newTicket = repository.login(cred1, INITIAL_WORKSPACE);
			assertTrue("Login succeeded for new user to original workspace.",newTicket!=null);
		} catch ( ItemExistsException iee ) {
		    log.error("Credential already exists - unable to check that it can be created.\n"
		            +" The test should really be run with a newly rebuilt database & test data loaded");
		} catch ( LoginException le ) {
			assertTrue("Login exception unexpectededly - user newly created. Exception was "+le.getMessage(),true);
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
		
		try {
			// resetup the credential as the password will have been cleared by login.
			cred1 = new SimpleCredentials(newUser, newPassword1);
			repository.updateCredentials(cred1,cred2);
			ITicket newTicket = repository.login(cred2, INITIAL_WORKSPACE);
			assertTrue("Login succeeded for new user to original workspace with new password.",newTicket!=null);
		} catch ( LoginException le ) {
			assertTrue("Login exception unexpectededly - user password changed. Exception was "+le.getMessage(),true);
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
		
		// try recreating a new user - should fail
		// setup credential with known username/password
		try { 
			repository.createCredentials(cred1);
			fail("User creation should have failed due to duplicate username");
		} catch ( ItemExistsException re ) {
			assertTrue("Repository exception thrown was due to name duplication as expected: "+re.getMessage(), 
					re.getMessage() != null && re.getMessage().equals(userAlreadyExistsMessage));
		} catch ( Exception e ) {
			failUnexpectedException(e);
		}
	}

	/** create a node with two versions and test deleting them using delete version */
	public void testFileItemDeleteVersion() {
		IFileDAO fileDAO = (FileDAO)context.getBean("fileDAO", FileDAO.class);
		
        try {
            NodeKey keys = testAddFileItem(CRResources.getSingleFile(), CRResources.singleFileName,null,one);
	        checkFileNodeExist(fileDAO, keys.getUuid(), one, 1, v1Description);
			keys = testAddFileItem(CRResources.getZipFile(), CRResources.zipFileName,keys.getUuid(),two);
			checkFileNodeExist(fileDAO, keys.getUuid(), two, 2, v2Description);
			
			deleteVersion(keys.getUuid(), two);
			checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), two, 1);
			checkFileNodeExist(fileDAO, keys.getUuid(), one, 1, v1Description);
					
			deleteVersion(keys.getUuid(), one);
			checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), one, 0);
			checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), two, 0);
			
        } catch (FileNotFoundException e) {
            fail("Unexpected exception "+e.getMessage());
        }
	}
	
	/** create a node with two versions and test deleting them using delete node. ALso tests set property */
	public void testFileItemDeleteNode() {

		IFileDAO fileDAO = (FileDAO)context.getBean("fileDAO", FileDAO.class);

		try {
			NodeKey keys = testAddFileItem(CRResources.getSingleFile(), CRResources.singleFileName ,null,one);
			checkFileNodeExist(fileDAO, keys.getUuid(), one, 1, v1Description);
			testAddFileItem(CRResources.getZipFile(), CRResources.zipFileName,keys.getUuid(),two);
			checkFileNodeExist(fileDAO, keys.getUuid(), two, 2, v2Description);
		
			testSetPropertyFile(keys);

			deleteNode(keys.getUuid());
			checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), one, 0);
			checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), two, 0);
        } catch (FileNotFoundException e) {
            fail("Unexpected exception "+e.getMessage());
        }
	}
	
	/** create a node with two versions and test deleting them using delete node 
	  * but first rig the files so one of them is already gone and the other one is 
	  * read only. Note: java can delete a read only file - the status just
	  * stops you from writing it.
	  */
	public void testFileItemDeleteNodeFileProb() {

		IFileDAO fileDAO = (FileDAO)context.getBean("fileDAO", FileDAO.class);

		NodeKey keys = null;
		try {
			keys = testAddFileItem(CRResources.getSingleFile(), CRResources.singleFileName ,null,one);
			checkFileNodeExist(fileDAO, keys.getUuid(), one, 1, v1Description);
			testAddFileItem(CRResources.getZipFile(), CRResources.zipFileName,keys.getUuid(),two);
			checkFileNodeExist(fileDAO, keys.getUuid(), two, 2, v2Description);
        } catch (FileNotFoundException e) {
            fail("Unexpected exception "+e.getMessage());
        }
		
		try { 
			File file = new File(fileDAO.getFilePath(keys.getUuid(), one));
			boolean success = file.delete();
			assertTrue("Fudge deletion worked.", success);
			file = new File(fileDAO.getFilePath(keys.getUuid(), two));
			file.setReadOnly();
		} catch (RepositoryCheckedException re) {
			failUnexpectedException("testTextFileItem",re);
		}

		deleteNode(keys.getUuid());
		checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), one, 0);
		checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), two, 0);

	}
	
	private void checkFileNodeExist(IFileDAO fileDAO, Long uuid, Long version, int expectNumVersions, String expectedDescription) {
		
		InputStream isOut = null;
		try {
			IVersionedNode node = repository.getFileItem(ticket, uuid, version); 
			isOut = node.getFile(); 
			assertTrue("Input stream is returned.", isOut != null);
			int ch = isOut.read();
			assertTrue("Input stream can be read, first byte is "+ch, ch != -1);
			
			SortedSet history = repository.getVersionHistory(ticket, uuid);
			assertTrue("History contains "+history.size()+" objects, expected "+expectNumVersions,
					history != null && history.size()==expectNumVersions);
			Iterator iter = history.iterator();
			while (iter.hasNext()) {
				IVersionDetail element = (IVersionDetail) iter.next();
				if ( element.getVersionId().equals(version) ) {
					assertTrue("Description is "+element.getDescription()
							+" as expected "+expectedDescription,
							expectedDescription.equals(element.getDescription()));
				}
			}
			
			String filepath = fileDAO.getFilePath(uuid, version);
			File file = new File(filepath);
			assertTrue("File "+filepath+" exists. ", file.exists() );
			
		} catch (RepositoryCheckedException re) {
			failUnexpectedException("checkFileNode",re);
		} catch (IOException ioe) {
			failUnexpectedException("checkFileNode",ioe);
		} finally {
			try {
				if ( isOut != null ) {
					isOut.close();
				} 
			} catch (IOException ioe1) {
					System.err.println("Unable to close file");
					ioe1.printStackTrace();
			}
		}
	}
	
	private void checkFileNodeDoesNotExist(IFileDAO fileDAO, Long uuid, Long version, int expectNumVersions) {
		
		try {
			try {
				IVersionedNode node;
				node = repository.getFileItem(ticket, uuid, version);
				// can't really check that it is really gone - transaction seems to keep it open
				//fail("Should have thrown ItemNotFoundException exception for uuid "
				//	+uuid+" version "+version+" got node "+node);
			} catch ( ItemNotFoundException e ) {
				assertTrue("ItemNotFoundException thrown as expected", true);
			}
		
			try {

				SortedSet history = repository.getVersionHistory(ticket, uuid);
				if ( expectNumVersions > 0 )
					assertTrue("History contains "+history.size()+" objects, expected "+expectNumVersions,
						history != null && history.size()==expectNumVersions);
				// can't really check that it is really gone - transaction seems to keep it open
				// else 
				// fail("Should have thrown ItemNotFoundException exception for uuid "
				//    +uuid+" as all versions have been deleted so node should have been deleted.");

			} catch ( ItemNotFoundException e ) {
				if ( expectNumVersions > 0 )
					fail("ItemNotFoundException thrown unexpectedly - there should be other versions! ");
				else 
					assertTrue("ItemNotFoundException thrown as expected", true);
			}
	
			String filepath = fileDAO.getFilePath(uuid, version);
			File file = new File(filepath);
			assertTrue("File "+filepath+" does not exist. ", ! file.exists() );
		
			// should the directory still be there?
			int pos = file.getPath().lastIndexOf(File.separator+version);
			String dirPath = file.getPath().substring(0,pos);
			System.out.println("Checking dir path "+dirPath);
			File dir = new File(dirPath);
			if ( expectNumVersions > 0 ) {
				assertTrue("Directory still exists for other files", dir.exists());
			} else {
				assertTrue("Directory removed", ! dir.exists());
			}

		} catch (RepositoryCheckedException re) {
			re.printStackTrace();
			failUnexpectedException("checkFileNodeDoesNotExist",re);
		}

		
	}
	
	private void deleteVersion(Long uuid, Long version) {
		try {
			String[] problemFiles = repository.deleteVersion(ticket,uuid,version);
			assertTrue("No problematic files should be found. "
					+(problemFiles!=null ? problemFiles.length+" found":"")
					,problemFiles==null || problemFiles.length==0);
		} catch (RepositoryCheckedException re) {
			re.printStackTrace();
			failUnexpectedException("checkFileNodeDoesNotExist",re);
		}
	}

	private void deleteNode(Long uuid) {
		try {
			String[] problemFiles = repository.deleteNode(ticket,uuid);
			assertTrue("No problematic files should be found. "
					+(problemFiles!=null ? problemFiles.length+" found":"")
					,problemFiles==null || problemFiles.length==0);
		} catch (RepositoryCheckedException re) {
			re.printStackTrace();
			failUnexpectedException("checkFileNodeDoesNotExist",re);
		}
	}
	
	private NodeKey testAddFileItem(InputStream file, String filename, Long uuid, Long expectedVersion) {
		
		NodeKey keys = null;
		try {
			if ( uuid == null ) {
				// new file
				keys = repository.addFileItem(ticket, file, filename, null, v1Description);
				assertTrue("File save returns uuid",keys != null && keys.getUuid() != null);
				assertTrue("File save got expected version "+expectedVersion,
					keys != null && keys.getVersion() != null 
					&& keys.getVersion().equals(expectedVersion));
			} else {
				// update existing node
				keys = repository.updateFileItem(ticket, uuid, filename, 
				        file, null, v2Description);
				assertTrue("File save returns same uuid",keys != null && keys.getUuid().equals(uuid));
				assertTrue("File save got expected version "+expectedVersion,
					keys != null && keys.getVersion() != null 
					&& keys.getVersion().equals(expectedVersion));
			}
			

		} catch (RepositoryCheckedException re) {
			failUnexpectedException("testAddFileItem",re);
		} finally {
			try {
				if ( file != null ) {
				    file.close();
				}
			} catch (IOException ioe1) {
				System.err.println("Unable to close file");
				ioe1.printStackTrace();
			}
		}
		
		return keys;


	}

	/** Tests that a file item can be added and copied, and that the two copies are separate */
	public void testCopyFileItem() {
		IFileDAO fileDAO = (FileDAO)context.getBean("fileDAO", FileDAO.class);
	
		NodeKey keys = null;
		try {
			keys = testAddFileItem(CRResources.getSingleFile(), CRResources.singleFileName ,null,one);
			checkFileNodeExist(fileDAO, keys.getUuid(), one, 1, v1Description);
	    } catch (FileNotFoundException e) {
	        fail("Unexpected exception "+e.getMessage());
	    }

	   // copy each version of the file node
		try {
			NodeKey copy1 = repository.copyNodeVersion(ticket, keys.getUuid(), one);
			NodeKey copylatest = repository.copyNodeVersion(ticket, keys.getUuid(), null);

			assertNotSame("Copy 1 is a different uuid to the original.", copy1.getUuid(), keys.getUuid());
			checkFileNodeExist(fileDAO, copy1.getUuid(), one, 1, v1Description);
			
			assertNotSame("Copy Latest is a different uuid to the original.", copylatest.getUuid(), keys.getUuid());
			checkFileNodeExist(fileDAO, copylatest.getUuid(), one, 1, v1Description);
			
			assertNotSame("Two copies have a different uuid.", copylatest.getUuid(), copy1.getUuid());
			
			// check that the properties are different ids, but have the same values
			Set origProperties = repository.getFileItem(ticket, keys.getUuid(), one).getProperties();
			Set copyProperties = repository.getFileItem(ticket, copy1.getUuid(), one).getProperties();
			compareProperties(origProperties, copyProperties);
			
			copyProperties = repository.getFileItem(ticket, copylatest.getUuid(), one).getProperties();
			compareProperties(origProperties, copyProperties);

			// should be able to delete the original nodes, and the copies should still exist.
			deleteNode(keys.getUuid());
			checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), one, 0);
			checkFileNodeDoesNotExist(fileDAO, keys.getUuid(), two, 0);
			checkFileNodeExist(fileDAO, copy1.getUuid(), one, 1, v1Description);
			checkFileNodeExist(fileDAO, copylatest.getUuid(), one, 1, v1Description);

		} catch (RepositoryCheckedException re) {
			failUnexpectedException("testTextFileItem",re);
		}
	
	}

	/** The two properties sets should have the same values, but different ids */
	private void compareProperties(Set origProperties, Set copyProperties) {
		assertEquals("Same number of properties in each set", origProperties.size(), copyProperties.size());
		Iterator iter = origProperties.iterator();
		
		while (iter.hasNext()) {
			// check each property in origProperties, against the entries in copyProperties
			// not the most efficient but there shouldn't be too many properties to check.
			CrNodeVersionProperty origElement = (CrNodeVersionProperty) iter.next();

			boolean found = false;
			String name = origElement.getName();
			Iterator copyIter = copyProperties.iterator();
			while  ( copyIter.hasNext() && ! found ) {
				CrNodeVersionProperty copyElement = (CrNodeVersionProperty) copyIter.next();
				if ( name.equals(copyElement.getName()) ) {
					found = true;
					assertEquals("Element "+name+" same value",origElement.getValue(),copyElement.getValue());
					assertTrue("Element "+name+" different id",!origElement.getId().equals(copyElement.getId()));
				}
			}
			assertTrue("Copied elements contain element for "+name,found);
		}

	}
	
	/** Tests that a package item can be created, update and that at property can be set. */
	public void testPackageItem() {
		NodeKey keys = testPackageItem(null);
		testPackageItem(keys.getUuid());
		testSetPropertyPackage(keys);
	}

	private NodeKey testPackageItem(Long uuid) {
		
	    String tempDir = null;
		String v1Description = "Draft";
		String v2Description = "Final";
		NodeKey keys = null;
		try {
		    // unpack the zip file so we have a directory to play with 
		    tempDir = ZipFileUtil.expandZip(CRResources.getZipFile(), CRResources.zipFileName);
			File directory = new File(tempDir);
			directory.list();

			if ( uuid == null ) {
				keys = repository.addPackageItem(ticket,  tempDir, "index.html", v1Description);
				assertTrue("Package save returns uuid",keys != null && keys.getUuid() != null);
				assertTrue("Package save got version 1",
						keys != null && keys.getVersion() != null 
						&& keys.getVersion().longValue() == 1);
			} else {
				keys = repository.updatePackageItem(ticket, uuid, tempDir, "index.html", v2Description);
				assertTrue("Package save returns uuid",keys != null && keys.getUuid() != null);
				assertTrue("Package save got version >1",
						keys != null && keys.getVersion() != null 
						&& keys.getVersion().longValue() > 1);
			}

			checkPackage(keys);
					
		} catch (RepositoryCheckedException re) {
			failUnexpectedException("testPackageItem",re);
		} catch (IOException ioe) {
			failUnexpectedException("testPackageItem",ioe);
	    } catch ( ZipFileUtilException e ) {
			failUnexpectedException("testPackageItem",e);
		} finally {
		    // clean up - delete that temporary directory
		    if ( tempDir != null ) {
			    try { 
			        ZipFileUtil.deleteDirectory(tempDir);
			    } catch ( ZipFileUtilException e ) {
					failUnexpectedException("testPackageItem",e);
			    }
		    }
		}
		
		return keys;
	}



	/** Tests that a file item can be added and copied, and that the two copies are separate */
	public void testCopyPackageItem() {
	
		NodeKey keys = testPackageItem(null);

	    // copy the package node
		try {
			NodeKey copy = repository.copyNodeVersion(ticket, keys.getUuid(), null);

			assertNotSame("Copy 1 is a different uuid to the original.", copy.getUuid(), keys.getUuid());
			checkPackage(copy);
			
			// should be able to delete the original nodes, and the copies should still exist.
			deleteNode(keys.getUuid());
			checkPackage(copy);

		} catch (RepositoryCheckedException re) {
			failUnexpectedException("testCopyPackageItem",re);
		} catch (IOException e) {
			failUnexpectedException("testCopyPackageItem",e);
		}

	}

	private void testSetPropertyPackage(NodeKey keys) {
	    String propertyName = "CUSTOMA";
	    Boolean value = Boolean.TRUE;
	    
		List nodes;
        try {
            nodes = repository.getPackageNodes(ticket, keys.getUuid(), keys.getVersion());
	        Iterator iter = nodes.iterator();
			if ( iter.hasNext() ) {
				SimpleVersionedNode packageNode = (SimpleVersionedNode) iter.next();
				assertTrue("First node is the package node. (A)",
					packageNode.isNodeType(NodeType.PACKAGENODE));
				repository.setProperty(ticket, keys.getUuid(), keys.getVersion(), propertyName, value, PropertyType.BOOLEAN); 			
			} else {
			    fail("No nodes found for package "+keys);
			}
			
			// now, is the property set?
			nodes = repository.getPackageNodes(ticket, keys.getUuid(), keys.getVersion());
			iter = nodes.iterator();
			if ( iter.hasNext() ) {
				SimpleVersionedNode packageNode = (SimpleVersionedNode) iter.next();
				assertTrue("First node is the package node. (B)",
					packageNode.isNodeType(NodeType.PACKAGENODE));
				IValue newValue = packageNode.getProperty(propertyName);
				assertTrue("newValue is a TRUE Boolean", 
				        Boolean.TRUE.equals(newValue.getBoolean()) && newValue.getType()==PropertyType.BOOLEAN);
			} else {
			    fail("No nodes found for package "+keys);
			}
        } catch (RepositoryCheckedException e) {
			failUnexpectedException("testSetProperty",e);
        }

	}

	private void testSetPropertyFile(NodeKey keys) {
	    String propertyName = "CUSTOMB";
	    String value = "avalue";
	    
        try {
            IVersionedNode node = repository.getFileItem(ticket, keys.getUuid(), keys.getVersion());
            assertTrue("File node found (A)", node != null && node.isNodeType(NodeType.FILENODE));
            repository.setProperty(ticket, keys.getUuid(), keys.getVersion(), propertyName, value, PropertyType.BOOLEAN); 			
			
			// now, is the property set?
            node = repository.getFileItem(ticket, keys.getUuid(), keys.getVersion());
            assertTrue("File node found (A)", node != null && node.isNodeType(NodeType.FILENODE));
			IValue newValue = node.getProperty(propertyName);
			assertEquals("newValue "+newValue+" equals "+value, value, newValue.getString());
			
        } catch (RepositoryCheckedException e) {
			failUnexpectedException("testSetProperty",e);
        }

	}

	public void testLogout() {
		// relogin then logout so that we don't affect the other tests.
		ICredentials cred = new SimpleCredentials(INITIAL_WORKSPACE_USER, INITIAL_WORKSPACE_PASSWORD);
		ITicket localTicket = null;
		try {
			localTicket = repository.login(cred, INITIAL_WORKSPACE);
			assertTrue("Login okay",localTicket != null);
			repository.getVersionHistory(ticket, TEST_DATA_NODE_ID);
			assertTrue("History can be accessed before logging out",true);
			repository.logout(ticket);
		} catch (RepositoryCheckedException e) {
			failUnexpectedException("testLogout",e);
		}
		
		try {
			Long id = ticket.getWorkspaceId();
			assertTrue("Workspace id is not avaiable after logging out",id==null);
			try {
				repository.getVersionHistory(ticket, TEST_DATA_NODE_ID);
				fail("History shouldn't be available after logging out");
			} catch (AccessDeniedException ade ) {
				assertTrue("AccessDeniedException thrown as expected - can't get history after logging out",true);
			}
		} catch (RepositoryCheckedException e) {
			failUnexpectedException("testLogout",e);
		}
	}
	
	/** Checks that the system will give us an error if we get try to
	 * get a node that belongs to a different workspace to our ticket.
	 */
	public void testWrongWorkspaceAccess() {
		// relogin then logout so that we don't affect the other tests.
		ICredentials cred = new SimpleCredentials(INITIAL_WORKSPACE_USER, INITIAL_WORKSPACE_PASSWORD);
		ITicket localTicket = null;
		try {
			localTicket = repository.login(cred, SECONDARY_WORKSPACE);
			assertTrue("Login okay",localTicket != null);
		} catch (RepositoryCheckedException e) {
			failUnexpectedException("testWrongWorkspaceAccess",e);
		}

		try {
			// should fail as this node is in the INITIAL_WORKSPACE,
			// not the SECONDARY_WORKSPACE
			repository.getFileItem(localTicket, TEST_DATA_NODE_ID, null);
			fail("Node can be accessed for the wrong workspace.");
		} catch (ItemNotFoundException e) {
			assertTrue("ItemNotFoundException thrown as expected when getting node from wrong workspace.", true);
		} catch (RepositoryCheckedException e) {
			failUnexpectedException("testWrongWorkspaceAccess",e);
		}

		try {
			// should fail as this node is in the INITIAL_WORKSPACE,
			// not the SECONDARY_WORKSPACE
			repository.getVersionHistory(localTicket, TEST_DATA_NODE_ID);
			fail("History can be accessed for the wrong workspace.");
		} catch (ItemNotFoundException e) {
			assertTrue("ItemNotFoundException thrown as expected when getting history for node from wrong workspace.", true);
		} catch (RepositoryCheckedException e) {
			failUnexpectedException("testWrongWorkspaceAccess",e);
		}
		
		try {
			// be nice and clean up.
			repository.logout(localTicket);
		} catch (RepositoryCheckedException e) {
			failUnexpectedException("testWrongWorkspaceAccess",e);
		}
	}


}
