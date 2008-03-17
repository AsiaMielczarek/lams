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
package org.lamsfoundation.lams.tool.survey.web.action;

import static org.lamsfoundation.lams.tool.survey.SurveyConstants.ATTR_QUESTION_UID;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.CHART_TYPE;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.ERROR_MSG_CHART_ERROR;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.MSG_BARCHART_CATEGORY_AXIS_LABEL;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.MSG_BARCHART_TITLE;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.MSG_BARCHART_VALUE_AXIS_LABEL;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.MSG_OPEN_RESPONSE;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.MSG_PIECHART_TITLE;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.QUESTION_TYPE_TEXT_ENTRY;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.SURVEY_SERVICE;
import static org.lamsfoundation.lams.tool.survey.SurveyConstants.OPTION_SHORT_HEADER;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.lamsfoundation.lams.tool.survey.dto.AnswerDTO;
import org.lamsfoundation.lams.tool.survey.model.SurveyOption;
import org.lamsfoundation.lams.tool.survey.model.SurveyQuestion;
import org.lamsfoundation.lams.tool.survey.service.ISurveyService;
import org.lamsfoundation.lams.util.ChartUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
/**
 * Display chart image by request.
 * 
 * @author Steve.Ni
 * 
 * @version $Revision$
 */
public class ChartAction extends  Action {
    
	static Logger logger = Logger.getLogger(ChartAction.class);
	
	private MessageResources resource;
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		resource = getResources(request);
        OutputStream out= response.getOutputStream(); 
        
        String type= WebUtil.readStrParam(request, CHART_TYPE);
        Long sessionId= WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
        Long questionUid= WebUtil.readLongParam(request, ATTR_QUESTION_UID);
        
    	ISurveyService service = getSurveyService();
		
		AnswerDTO answer =service.getQuestionResponse(sessionId,questionUid);
		if(answer.getType() == QUESTION_TYPE_TEXT_ENTRY){
			logger.error("Error question type : Text entry can not generate chart.");
			response.getWriter().print(resource.getMessage(ERROR_MSG_CHART_ERROR));
			return null;
		}
			
		//Try to create chart
		try {
	    	if (type.equals(ChartUtil.CHART_TYPE_PIE)){
	    		DefaultPieDataset data = createPieDataset (answer);
	    	    ChartUtil.outputPieChart(response, out, resource.getMessage(MSG_PIECHART_TITLE,answer.getSequenceId()), data);
	    	}else if (type.equals(ChartUtil.CHART_TYPE_BAR)){
	    		DefaultCategoryDataset data = createBarDataset(answer);    
	    		ChartUtil.outputBarChart( response, out, resource.getMessage(MSG_BARCHART_TITLE,answer.getSequenceId()), data, 
	    				resource.getMessage(MSG_BARCHART_CATEGORY_AXIS_LABEL), resource.getMessage(MSG_BARCHART_VALUE_AXIS_LABEL) ); 
	    	}
		} catch ( IOException e ) {
			logger.error("Error creating chart for sessionId "+sessionId,e);
			response.getWriter().print(resource.getMessage(ERROR_MSG_CHART_ERROR));
		}
		
        return null;
    }


    public DefaultPieDataset createPieDataset(AnswerDTO answer){
    
        DefaultPieDataset data= new DefaultPieDataset();
        
        Set<SurveyOption> options = answer.getOptions();
		int optIdx = 1;
		for (SurveyOption option : options) {
			data.setValue(OPTION_SHORT_HEADER + optIdx, (Number) option.getResponse());
			optIdx++;
  		}
          
        if(answer.isAppendText())
          	 data.setValue(resource.getMessage(MSG_OPEN_RESPONSE), (Number)answer.getOpenResponse());
          
        return data;
    }
    
    public DefaultCategoryDataset createBarDataset(AnswerDTO answer){
        
    	DefaultCategoryDataset data= new DefaultCategoryDataset();
      
        Set<SurveyOption> options = answer.getOptions();
        int optIdx = 1;
        for (SurveyOption option : options) {
            data.setValue((Number)option.getResponse(), OPTION_SHORT_HEADER + optIdx, OPTION_SHORT_HEADER + optIdx);
            optIdx++;
		}
        
        if(answer.isAppendText())
        	 data.setValue((Number)answer.getOpenResponse(), resource.getMessage(MSG_OPEN_RESPONSE), resource.getMessage(MSG_OPEN_RESPONSE));
        
   	    return data;
    }
	//*************************************************************************************
	// Private method 
	//*************************************************************************************
	/**
	 * Return SurveyService bean.
	 */
	private ISurveyService getSurveyService() {
	      WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
	      return (ISurveyService) wac.getBean(SURVEY_SERVICE);
	}
    
}
