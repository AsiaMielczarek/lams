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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
package org.lamsfoundation.testharness;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.allaire.wddx.WddxDeserializationException;
import com.allaire.wddx.WddxDeserializer;
import com.allaire.wddx.WddxSerializer;

/**
 * @version
 *
 * <p>
 * <a href="TestUtil.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:fyang@melcoe.mq.edu.au">Fei Yang</a>
 */
public class TestUtil {

	private static final Logger log = Logger.getLogger(TestUtil.class);
	
	private static String machineName;

	protected static final char NAME_SEPERATOR = '_';

	public static String getMachineName() {
		try {
			if (machineName == null)
				return machineName = InetAddress.getLocalHost().getHostName();
			else
				return machineName;
		} catch (UnknownHostException e) {
			return "UnknownHost";
		}

	}

	public static String serialize(Object data) throws IOException {
		WddxSerializer tempws = new WddxSerializer();
		StringWriter tempsw = new StringWriter();
		tempws.serialize(data, tempsw);
		return tempsw.toString();
	}

	public static Object deserialize(String wddxPacket) throws WddxDeserializationException {
		
		log.debug("WDDX packet from the server:"+wddxPacket);
		
		// Create an input source (org.xml.sax.InputSource) bound to the packet
		InputSource tempSource = new InputSource(new StringReader(wddxPacket));

		// Create a WDDX deserializer (com.allaire.wddx.WddxDeserializer)
		WddxDeserializer tempDeserializer = new WddxDeserializer("org.apache.xerces.parsers.SAXParser");

		// Deserialize the WDDX packet
		Object result;
		try {
			result = tempDeserializer.deserialize(tempSource);
			log.debug("Object deserialized from the WDDX packet:"+result);
		} catch (IOException e) {
			throw new WddxDeserializationException(e);
		}

		return result;
	}

	public static String buildName(String testName, String simpleName) {
		return TestUtil.getMachineName() + NAME_SEPERATOR + testName + NAME_SEPERATOR + simpleName;
	}

	public static String buildName(String testName, String simpleName, int maxLength) {
		return truncate(buildName(truncate(testName,1,true),simpleName), maxLength, true);
	}

	private static String truncate(String name, int length, boolean leftToRight) {
		if(name.length()<=length)
			return name;
		if(leftToRight)
			return name.substring(name.length()-length);
		else
			return name.substring(0,length);
	}

	public static String extractString(String text, String startFlag, char endFlag){
		String target = null;
		try{
			int index = text.indexOf(startFlag);
			if(index!=-1){
				int startIndex = index + startFlag.length();
				int endIndex = text.indexOf(endFlag, startIndex);
				target = text.substring(startIndex, endIndex);
			}
		}catch(IndexOutOfBoundsException e){
			log.debug(e.getMessage());
			log.debug("startFlag: "+startFlag+" endFlag: "+endFlag);
			log.debug(text);
		}
		return target;
	}

	public static int generateRandomIndex(int length){
		return new Random().nextInt(length);
	}
	

}