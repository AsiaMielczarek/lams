/****************************************************************
 * Copyright (C) 2006 LAMS Foundation (http://lamsfoundation.org)
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
 * ****************************************************************
 */
package org.lamsfoundation.testharness.author;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.lamsfoundation.testharness.Call;
import org.lamsfoundation.testharness.MockUser;
import org.lamsfoundation.testharness.TestHarnessException;
import org.xml.sax.SAXException;

import com.meterware.httpunit.WebResponse;

/**
 * @version
 *
 * <p>
 * <a href="MockAuthor.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:fyang@melcoe.mq.edu.au">Fei Yang</a>
 */
public class MockAuthor extends MockUser {
	
	private static final Logger log = Logger.getLogger(MockAuthor.class);
	
	public static final String DEFAULT_NAME = "Author";
	
	private static final String IMPORT_FORM_FLAG = "UPLOAD_FILE";
	private static final String UPLOAD_FILE_PARAM = "UPLOAD_FILE";
	private static final String IMPORT_SUCCESS_FLAG = "movie";
	private static final String LD_START_TAG = "learningDesignID=";
	private static final char LD_END_TAG = '&';

	/**
	 * MockAuthor Constructor
	 *
	 * @param 
	 */
	public MockAuthor(AuthorTest test, String username, String password, String userId) {
		super(test, username, password, userId);
	}

	public String importLearningDesign(String learningDesignUploadURL, File file){
		try{
			delay();
			WebResponse resp = (WebResponse)new Call(wc, test,"Import Learning Design", learningDesignUploadURL).execute();
			if(!checkPageContains(resp,IMPORT_FORM_FLAG)){
				log.debug(resp.getText());
				throw new TestHarnessException(username+" did not get learning design import page with the url:"+learningDesignUploadURL);
			}
			Map<String,Object> params = new HashMap<String,Object>();
			params.put(UPLOAD_FILE_PARAM,file);
			resp = (WebResponse)new Call(wc, test,"Submit Learning Design Import Form", fillForm(resp,0,params)).execute();
			if(!checkPageContains(resp,IMPORT_SUCCESS_FLAG)){
				log.debug(resp.getText());
				throw new TestHarnessException(username+" failed to upload file:"+file.getAbsolutePath());
			}
			String text = resp.getText();
			int startIndex = text.indexOf(LD_START_TAG);
			int endIndex = text.indexOf(LD_END_TAG,startIndex);
			String idAsString = text.substring(startIndex+LD_START_TAG.length(),endIndex); 
			log.info(username + " imported learning design " + file.getName()+" and the id is " + idAsString);
			return idAsString;
		}catch(IOException e){
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

}
