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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * @author Fiona Malikoff
 */
public class TestDateUtil extends TestCase {
	
	private Date utcDate = null;
	private Date utcCalendarDate = null;
	private Date localDate = null;
	
	// used to test the UTC conversion
	private static final Date testDate = new Date(System.currentTimeMillis());
	private static final Date testCalendarDate = Calendar.getInstance().getTime();
	
	private static final String FLASH_DATE_STRING_NULL_VALUE = "1970-1-1T0:0:0";
	private static final String FLASH_DATE_STRING_SINGLE_DIGITS = "2005-2-7T1:0:23";
	private static final String FLASH_DATE_STRING_DOUBLE_DIGITS = "2004-12-23T11:50:23";
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// TODO Implement testConvertToUTC test
	public void testConvertToUTC() {
		utcDate = DateUtil.convertToUTC(testDate);
		utcCalendarDate = DateUtil.convertToUTC(testCalendarDate);
		System.out.println("Original Date:" + testDate.toString());
		System.out.println("UTC Date:" + utcDate.toString());
		System.out.println("UTC Date (Calendar):"  + utcCalendarDate.toString());
	
		assertNotNull("Date has converted to UTC as expected", utcDate);
	}

	// TODO Implement testConvertFromUTCToLocal test
	public void testConvertFromUTCToLocal() {
	}

	
	/** Test the conversion of dates given hardcoded date formats.
	 */
	public void testConvertFromStringString() {
		
		Date date;
		
		try {
			date = DateUtil.convertFromString(FLASH_DATE_STRING_SINGLE_DIGITS);
			assertNotNull("Single digit date converted okay", date);
			checkDate(date,2005,Calendar.FEBRUARY,7,1,0,23);
		} catch (ParseException e) {
			fail("Parse exception thrown trying to convert "+FLASH_DATE_STRING_NULL_VALUE);
		}

		try {
			date = DateUtil.convertFromString(FLASH_DATE_STRING_DOUBLE_DIGITS);
			assertNotNull("Double digit date converted okay", date);
			checkDate(date,2004,Calendar.DECEMBER,23,11,50,23);
		} catch (ParseException e) {
			fail("Parse exception thrown trying to convert "+FLASH_DATE_STRING_DOUBLE_DIGITS);
		}

	}

	/**
	 * @param date
	 */
	private void checkDate(Date date, int year, int month, int day, int hour, int minute, int second) {
		Calendar cal;
		cal = new GregorianCalendar();
		cal.setTime(date);
		assertEquals(cal.get(Calendar.YEAR),year);
		assertEquals(cal.get(Calendar.MONTH),month);
		assertEquals(cal.get(Calendar.DAY_OF_MONTH),day);
		assertEquals(cal.get(Calendar.HOUR_OF_DAY),hour);
		assertEquals(cal.get(Calendar.MINUTE),minute);
		assertEquals(cal.get(Calendar.SECOND),second);
	}

	
	/** Test the conversion of dates given supplied date formats. 
	 */
	public void testConvertFromStringStringString() {
		Date date;
		Calendar cal;
		
		try {
			date = DateUtil.convertFromString("31-DEC-1981 23:59:59","dd-MMM-yyyy HH:mm:ss");
			assertNotNull("Manual date converted okay", date);
			checkDate(date,1981,Calendar.DECEMBER,31,23,59,59);
		} catch (ParseException e) {
			fail("Parse exception thrown trying to convert "+FLASH_DATE_STRING_NULL_VALUE);
		}
	}

	public void testFlashConversion() {
		Date date = null;
		String dateString = null;
		
		try {
			dateString = "17/12/2006 11:13 AM";
	        date = DateUtil.convertFromLAMSFlashFormat(dateString);
			checkDate(date, 2006, Calendar.DECEMBER, 17, 11, 13, 0);
	        
			dateString = "17/09/2006 11:13 AM";
			date = DateUtil.convertFromLAMSFlashFormat(dateString);
			checkDate(date, 2006, Calendar.SEPTEMBER, 17, 11, 13, 0);
			
			dateString = "17/9/2006 02:03 PM";
			date = DateUtil.convertFromLAMSFlashFormat(dateString);
			checkDate(date, 2006, Calendar.SEPTEMBER, 17, 14, 3, 0);
			
		} catch (ParseException e) {
			fail("Parse exception thrown trying to convert "+dateString);
		}
	}
}
