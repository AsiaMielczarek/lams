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
package org.lamsfoundation.testharness;

/**
 * @version
 *
 * <p>
 * <a href="TestHarnessException.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:fyang@melcoe.mq.edu.au">Fei Yang</a>
 */
public class TestHarnessException extends RuntimeException {

    private static final long serialVersionUID = 6481839981681761094L;

	/**
	 * Constructor for TestHarnessException.
	 */
	public TestHarnessException() {
		super();
	}

	/**
	 * Constructor for TestHarnessException.
	 * @param message
	 */
	public TestHarnessException(String message) {
		super(message);
	}

	/**
	 * Constructor for TestHarnessException.
	 * @param message
	 * @param cause
	 */
	public TestHarnessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for TestHarnessException.
	 * @param cause
	 */
	public TestHarnessException(Throwable cause) {
		super(cause);
	}

}
