/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
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
/* $$Id$$ */
package org.lamsfoundation.lams.web.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.lamsfoundation.lams.web.util.TokenProcessor;

/**
 * @author daveg
 *
 */
public abstract class LamsAction extends Action {
    
    protected static String className = "Action";
	
    protected static TokenProcessor token = TokenProcessor.getInstance();
    protected static Logger log = Logger.getLogger(LamsAction.class);    

	protected void saveToken(javax.servlet.http.HttpServletRequest request) {
        token.saveToken(request);
	}
	
	protected boolean isTokenValid(javax.servlet.http.HttpServletRequest request) {
        return token.isTokenValid(request, false);
	}
    
	protected boolean isTokenValid(javax.servlet.http.HttpServletRequest request, boolean reset) {
	    return token.isTokenValid(request, reset);
	}
	
    protected void resetToken(HttpServletRequest request) {
        token.resetToken(request);
    }
    
    /*protected void saveForward(javax.servlet.http.HttpServletRequest request, ActionForward forward) {
    	token.saveForward(request, forward);
    }
    
    protected ActionForward getForward(javax.servlet.http.HttpServletRequest request) {
    	return token.getForward(request, true);
    }*/
    
}
