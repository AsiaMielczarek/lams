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
package org.lamsfoundation.lams.web.tag;

import java.io.IOException;
import java.lang.NullPointerException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.tool.service.*;
import org.lamsfoundation.lams.tool.IToolVO;
import org.lamsfoundation.lams.web.filter.LocaleFilter;
import org.lamsfoundation.lams.util.Configuration;
import org.lamsfoundation.lams.util.ConfigurationKeys;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Generates a help link to a contextualised tool help page.
 * 
 * @jsp.tag name="help"
 * 			body-content="empty"
 * 			display-name="Help tag"
 * 			description="Help tag"
 * 	
 * @author Fiona Malikoff
 */
public class HelpTag extends TagSupport {

    private static final String LAMSFOUNDATION_TEXT = "lamsfoundation";
    private static final String LAMSDOCS = "lamsdocs";
    private static final String ENGLISH_LANGUAGE = "en";

	private static final Logger log = Logger.getLogger(HelpTag.class);
	private String module = null;
	private String page = null;
	private String toolSignature = null;
	private String style = null;
	
	/**
	 * 
	 */
	public HelpTag() {
		super();
	}
	
	public int doStartTag() throws JspException {
		try {
		    String helpURL = null;
		    String fullURL = null;
		    
        	JspWriter writer = pageContext.getOut();
        	if (StringUtils.equals(style, "no-tabs")) {
        		writer.println("<div class='help-no-tabs'>");
        	} else {
        		writer.println("<div class='help'>");
        	}
        	try {
        		
        		if(toolSignature != null && module != null) {
	        		// retrieve help URL for tool
		        	ILamsToolService toolService = (ILamsToolService) getContext().getBean(AuthoringConstants.TOOL_SERVICE_BEAN_NAME);
					IToolVO tool = toolService.getToolBySignature(toolSignature);
					
					helpURL = tool.getHelpUrl();
					
					if(helpURL == null)
						return SKIP_BODY;
					
					// construct link
					helpURL = addLanguageToURL(helpURL);
				    fullURL = helpURL + module + "#" + toolSignature + module;
					
					writer.println("<img src=\"" + Configuration.get(ConfigurationKeys.SERVER_URL) + "images/help.jpg\" border=\"0\" width=\"25\" height=\"25\" onclick=\"window.open('" + fullURL + "', 'help')\"/>");

	        	
	        	} else if(page != null){
	        		
	        		helpURL =  addLanguageToURL(Configuration.get(ConfigurationKeys.HELP_URL));
	        		fullURL = helpURL+page;

	        		writer.println("<img src=\"" + Configuration.get(ConfigurationKeys.SERVER_URL) + "images/help.jpg\" border=\"0\" width=\"25\" height=\"25\" onclick=\"window.open('" + fullURL + "', 'help')\"/>");

	        	} else {
	        		log.error("HelpTag unable to write out due to unspecified values.");
	        		writer.println("<img src=\"" + Configuration.get(ConfigurationKeys.SERVER_URL) + "images/css/warning.gif\" border=\"0\" width=\"20\" height=\"20\"/>");
	        	}
        	} catch (NullPointerException npe) {
    			log.error("HelpTag unable to write out due to NullPointerException. Most likely a required paramater was unspecified or incorrect.", npe);
    			// don't throw a JSPException as we want the system to still function.

    		}
        	
        	writer.println("</div>");
        	
		} catch (IOException e) {
			log.error("HelpTag unable to write out due to IOException.", e);
			// don't throw a JSPException as we want the system to still function.
		}
    	return SKIP_BODY;
	}

	private String addLanguageToURL(String helpURL) {

		HttpSession session = ((HttpServletRequest) this.pageContext.getRequest()).getSession();
		Locale locale = (Locale) session.getAttribute(LocaleFilter.PREFERRED_LOCALE_KEY);
	    if ( locale != null ) {
		    String language = locale.getLanguage();
		    
		    if ( ! ENGLISH_LANGUAGE.equals(language) && helpURL.indexOf(LAMSFOUNDATION_TEXT) != -1 ) {
		    	// points to the LAMS Foundation site, so add the language to the path.
		    	return helpURL.replace(LAMSDOCS, LAMSDOCS+language);
		    }
	    }

	    return helpURL;
	}
	
	public int doEndTag() {
		return EVAL_PAGE;
	}
	/**
	 * @return module
	 * 
	 * @jsp.attribute required="false"
	 * 				  rtexprvalue="true"
	 * 				  description="Module Name"
	 */
	public String getModule() {
		return module;
	}
	
	/**
	 * 
	 * @param module
	 */
	public void setModule(String module) {
		this.module = module;
	}
	
	private WebApplicationContext getContext() {
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext( pageContext.getServletContext());
		return ctx;
	}
	
	/**
	 * @return
	 * 
	  * @jsp.attribute required="false"
	 * 				   rtexprvalue="true"
	 * 				   description="Tool Signature"
	 */
	public String getToolSignature() {
		return toolSignature;
	}
	
	/**
	 * 
	 * @param toolSignature
	 */
	public void setToolSignature(String toolSignature) {
		this.toolSignature = toolSignature;
	}
	
	/**
	 * @return page
	 * 
	 * @jsp.attribute required="false"
	 * 				  rtexprvalue="true"
	 * 				  description="Page Name"
	 */
	public String getPage() {
		return module;
	}
	
	/**
	 * 
	 * @param page
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * @return style
	 * 
	 * @jsp.attribute required="false"
	 *                rtexprvalue="true"
	 *                description="Style"
	 */
	public String getStyle() {
		return style;
	}
	
	/**
	 * 
	 * @param style
	 */
	public void setStyle(String style) {
		this.style = style;
	}
	
}
