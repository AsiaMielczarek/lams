/***************************************************************************
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
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.tool.mc.web;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McComparator;
import org.lamsfoundation.lams.tool.mc.McUtils;
import org.lamsfoundation.lams.tool.mc.pojos.McContent;
import org.lamsfoundation.lams.tool.mc.pojos.McOptsContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueContent;
import org.lamsfoundation.lams.tool.mc.pojos.McQueUsr;
import org.lamsfoundation.lams.tool.mc.pojos.McSession;
import org.lamsfoundation.lams.tool.mc.pojos.McUsrAttempt;
import org.lamsfoundation.lams.tool.mc.service.IMcService;

/**
 * 
 * Keeps all operations needed for Authoring mode. 
 * @author Ozgur Demirtas
 *
 */
public class LearningUtil implements McAppConstants {
	static Logger logger = Logger.getLogger(LearningUtil.class.getName());
	
    /**
     * updates the Map based on learner activity
     * selectOptionsCheckBox(HttpServletRequest request,McLearningForm mcLearningForm, String questionIndex)
     * 
     * @param request
     * @param form
     * @param questionIndex
     */
    public static  void selectOptionsCheckBox(HttpServletRequest request,McLearningForm mcLearningForm, String questionIndex)
    {
    	logger.debug("requested optionCheckBoxSelected...");
    	logger.debug("questionIndex: " + mcLearningForm.getQuestionIndex());
    	logger.debug("optionIndex: " + mcLearningForm.getOptionIndex());
    	logger.debug("optionValue: " + mcLearningForm.getOptionValue());
    	logger.debug("checked: " + mcLearningForm.getChecked());
    	
    	Map mapGeneralCheckedOptionsContent=(Map) request.getSession().getAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT);
    	logger.debug("mapGeneralCheckedOptionsContent: " + mapGeneralCheckedOptionsContent);
    	
    	if (mapGeneralCheckedOptionsContent.size() == 0)
    	{
    		logger.debug("mapGeneralCheckedOptionsContent size is 0");
    		Map mapLeanerCheckedOptionsContent= new TreeMap(new McComparator());
    		
    		if (mcLearningForm.getChecked().equals("true"))
    			mapLeanerCheckedOptionsContent.put(mcLearningForm.getOptionIndex(), mcLearningForm.getOptionValue());
    		else
    			mapLeanerCheckedOptionsContent.remove(mcLearningForm.getOptionIndex());
    		
    		mapGeneralCheckedOptionsContent.put(mcLearningForm.getQuestionIndex(),mapLeanerCheckedOptionsContent);
    		request.getSession().setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapGeneralCheckedOptionsContent);
    	}
    	else
    	{
    		Map mapCurrentOptions=(Map) mapGeneralCheckedOptionsContent.get(questionIndex);
    		
    		logger.debug("mapCurrentOptions: " + mapCurrentOptions);
    		if (mapCurrentOptions != null)
    		{
    			if (mcLearningForm.getChecked().equals("true"))
    				mapCurrentOptions.put(mcLearningForm.getOptionIndex(), mcLearningForm.getOptionValue());
    			else
    				mapCurrentOptions.remove(mcLearningForm.getOptionIndex());
    			
    			logger.debug("updated mapCurrentOptions: " + mapCurrentOptions);
    			
    			mapGeneralCheckedOptionsContent.put(mcLearningForm.getQuestionIndex(),mapCurrentOptions);
    			request.getSession().setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapGeneralCheckedOptionsContent);	
    		}
    		else
    		{
    			logger.debug("no options for this questions has been selected yet");
    			Map mapLeanerCheckedOptionsContent= new TreeMap(new McComparator());
    			        			
    			if (mcLearningForm.getChecked().equals("true"))
    				mapLeanerCheckedOptionsContent.put(mcLearningForm.getOptionIndex(), mcLearningForm.getOptionValue());
    			else
    				mapLeanerCheckedOptionsContent.remove(mcLearningForm.getOptionIndex());        			
    			
    			mapGeneralCheckedOptionsContent.put(mcLearningForm.getQuestionIndex(),mapLeanerCheckedOptionsContent);
    			request.getSession().setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapGeneralCheckedOptionsContent);
    		}
    	}
    	
    	mapGeneralCheckedOptionsContent=(Map) request.getSession().getAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT);
    	logger.debug("final mapGeneralCheckedOptionsContent: " + mapGeneralCheckedOptionsContent);
    }
    
    
    /**
     * continueOptions(HttpServletRequest request)
     * 
     * @param request
     * @return boolean
     */
    public static boolean continueOptions(HttpServletRequest request)
    {
	 	IMcService mcService =McUtils.getToolService(request);
	 	
    	logger.debug("continue options requested.");
    	String currentQuestionIndex=(String)request.getSession().getAttribute(CURRENT_QUESTION_INDEX);
    	logger.debug("currentQuestionIndex:" + currentQuestionIndex);
    	
    	int newQuestionIndex=new Integer(currentQuestionIndex).intValue() + 1;
    	request.getSession().setAttribute(CURRENT_QUESTION_INDEX, new Integer(newQuestionIndex).toString());
    	logger.debug("updated questionIndex:" + request.getSession().getAttribute(CURRENT_QUESTION_INDEX));
    	
    	Long toolContentID= (Long) request.getSession().getAttribute(TOOL_CONTENT_ID);
        logger.debug("TOOL_CONTENT_ID: " + toolContentID);
        
        McContent mcContent=mcService.retrieveMc(toolContentID);
        logger.debug("mcContent: " + mcContent);
        
        /*
    	 * fetch question content from content
    	 */
        logger.debug("newQuestionIndex: " + newQuestionIndex);
    	Iterator contentIterator=mcContent.getMcQueContents().iterator();
    	boolean questionFound=false;
    	while (contentIterator.hasNext())
    	{
    		McQueContent mcQueContent=(McQueContent)contentIterator.next();
    		if (mcQueContent != null)
    		{
    			int displayOrder=mcQueContent.getDisplayOrder().intValue();
    			logger.debug("displayOrder: " + displayOrder);
    			
        		/* prepare the next question's candidate answers for presentation*/ 
        		if (newQuestionIndex == displayOrder)
        		{
        			logger.debug("get the next question... ");
        			Long uid=mcQueContent.getUid();
        			logger.debug("uid : " + uid);
        			/* get the options for this question */
        			List listMcOptions=mcService.findMcOptionsContentByQueId(uid);
        			logger.debug("listMcOptions : " + listMcOptions);
        			Map mapOptionsContent=McUtils.generateOptionsMap(listMcOptions);
        			request.getSession().setAttribute(MAP_OPTIONS_CONTENT, mapOptionsContent);
        			logger.debug("updated Options Map: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
        			questionFound=true;
        		}
    		}
    	}
    	logger.debug("questionFound: " + questionFound);
		return questionFound;
    }
    
    
    /**
     * readParameters(HttpServletRequest request, McLearningForm mcLearningForm)
     * 
     * @param request
     * @param mcLearningForm
     */
    public static void readParameters(HttpServletRequest request, McLearningForm mcLearningForm)
    {
    	String optionCheckBoxSelected=request.getParameter("optionCheckBoxSelected");
    	logger.debug("parameter optionCheckBoxSelected: " + optionCheckBoxSelected);
    	if ((optionCheckBoxSelected != null) && optionCheckBoxSelected.equals("1"))
    	{
    		logger.debug("parameter optionCheckBoxSelected is selected " + optionCheckBoxSelected);
    		mcLearningForm.setOptionCheckBoxSelected("1");
    	}
    	
    	String questionIndex=request.getParameter("questionIndex");
    	logger.debug("parameter questionIndex: " + questionIndex);
    	if ((questionIndex != null))
    	{
    		logger.debug("parameter questionIndex is selected " + questionIndex);
    		mcLearningForm.setQuestionIndex(questionIndex);
    	}
    	
    	String optionIndex=request.getParameter("optionIndex");
    	logger.debug("parameter optionIndex: " + optionIndex);
    	if (optionIndex != null)
    	{
    		logger.debug("parameter optionIndex is selected " + optionIndex);
    		mcLearningForm.setOptionIndex(optionIndex);
    	}
    	
    	String optionValue=request.getParameter("optionValue");
    	logger.debug("parameter optionValue: " + optionValue);
    	if (optionValue != null)
    	{
    		mcLearningForm.setOptionValue(optionValue);
    	}
    	
    	String checked=request.getParameter("checked");
    	logger.debug("parameter checked: " + checked);
    	if (checked != null)
    	{
    		logger.debug("parameter checked is selected " + checked);
    		mcLearningForm.setChecked(checked);
    	}
    }
    
    
    /**
     * assess(HttpServletRequest request, Map mapGeneralCheckedOptionsContent)
     * 
     * @param request
     * @param mapGeneralCheckedOptionsContent
     */
    public static Map assess(HttpServletRequest request, Map mapGeneralCheckedOptionsContent, Long toolContentId)
    {
		Map mapGeneralCorrectOptions= new TreeMap(new McComparator());
		
		IMcService mcService =McUtils.getToolService(request);
		
		Map mapQuestionsUidContent=AuthoringUtil.rebuildQuestionUidMapfromDB(request,toolContentId);
		logger.debug("mapQuestionsUidContent : " + mapQuestionsUidContent);
		
		Iterator itMap = mapQuestionsUidContent.entrySet().iterator();
		Long mapIndex=new Long(1);
		while (itMap.hasNext()) {
        	Map.Entry pairs = (Map.Entry)itMap.next();
            logger.debug("using the  pair: " +  pairs.getKey() + " = " + pairs.getValue());
            
            logger.debug("using mcQueContentId: " +  pairs.getValue());
            List correctOptions=(List) mcService.getPersistedSelectedOptions(new Long(pairs.getValue().toString()));
            Map mapCorrectOptions=buildMapCorrectOptions(correctOptions);
            logger.debug("mapCorrectOptions: " +  mapCorrectOptions);
        	mapGeneralCorrectOptions.put(mapIndex.toString(),mapCorrectOptions);
            mapIndex=new Long(mapIndex.longValue()+1);
		}
		logger.debug("mapGeneralCorrectOptions : " + mapGeneralCorrectOptions);
		
		Map mapLeanerAssessmentResults=compare(mapGeneralCorrectOptions,mapGeneralCheckedOptionsContent);
		logger.debug("mapLeanerAssessmentResults : " + mapLeanerAssessmentResults);
		request.getSession().setAttribute(MAP_LEARNER_ASSESSMENT_RESULTS, mapLeanerAssessmentResults);

		return mapLeanerAssessmentResults;
		
    }
    
   
    /**
     * calculateWeights(Map mapLeanerAssessmentResults, Map mapQuestionWeights)
     * 
     * @param mapLeanerAssessmentResults
     * @param mapQuestionWeights
     * @return int
     */
    public static int calculateWeights(Map mapLeanerAssessmentResults, Map mapQuestionWeights)
	{
    	logger.debug("starting calculate weights...");
    	logger.debug("mapLeanerAssessmentResults : " + mapLeanerAssessmentResults);
    	logger.debug("mapQuestionWeights : " + mapQuestionWeights);
    	
    	int totalUserWeight=0;
    	Iterator itLearnerAssessmentMap = mapLeanerAssessmentResults.entrySet().iterator();
		while (itLearnerAssessmentMap.hasNext()) 
		{
			Map.Entry pairs = (Map.Entry)itLearnerAssessmentMap.next();
            logger.debug("using the  pair: " +  pairs.getKey() + " = " + pairs.getValue());
            
            Iterator itWeightsMap = mapQuestionWeights.entrySet().iterator();
            while (itWeightsMap.hasNext())
            {
            	Map.Entry pairsWeight = (Map.Entry)itWeightsMap.next();
            	logger.debug("using the  weight pair: " +  pairsWeight.getKey() + " = " + pairsWeight.getValue());
            	if (pairs.getKey().toString().equals(pairsWeight.getKey().toString()))
            	{
            		logger.debug("equal question found " +  pairsWeight.getKey() + " = " + pairsWeight.getValue() + " and " +  pairs.getValue());
            		
            		if (pairs.getValue().toString().equalsIgnoreCase("true"))
            		{
                		logger.debug("equal question found " +  pairsWeight.getKey() + " is a correct answer.");
                		int weight= new Integer(pairsWeight.getValue().toString()).intValue();
                		logger.debug("weight: " + weight);
                		
                		totalUserWeight=totalUserWeight + weight;  
            		}
            	}
            }
		}
		logger.debug("totalUserWeight: " + totalUserWeight);
		return totalUserWeight;
	}
    
    
    /**
     * calculates the mark of a learner
     * getMark(Map mapLeanerAssessmentResults)
     * 
     * @param mapLeanerAssessmentResults
     * @return int
     */
    public static int getMark(Map mapLeanerAssessmentResults)
    {
    	int totalUserWeight=0;
    	Iterator itLearnerAssessmentMap = mapLeanerAssessmentResults.entrySet().iterator();
    	int correctAnswerCount=0;
		while (itLearnerAssessmentMap.hasNext()) 
		{
			Map.Entry pairs = (Map.Entry)itLearnerAssessmentMap.next();
		    if (pairs.getValue().toString().equalsIgnoreCase("true"))
		    {
		    	++correctAnswerCount;
		    }
		}
		return correctAnswerCount;
    }
  
    /**
     * calculates the max attempt count of a learner
     * getHighestAttemptOrder(HttpServletRequest request, Long queUsrId)
     * 
     * @param request
     * @param queUsrId
     * @return
     */
    public static int getHighestAttemptOrder(HttpServletRequest request, Long queUsrId)
    {
    	IMcService mcService =McUtils.getToolService(request);
    	List listMarks=mcService.getHighestAttemptOrder(queUsrId);
    	
    	Iterator itMarks=listMarks.iterator();
    	int highestAO=0;
    	while (itMarks.hasNext())
		{
    		McUsrAttempt mcUsrAttempt=(McUsrAttempt)itMarks.next();
    		int currentAO=mcUsrAttempt.getAttemptOrder().intValue();
    		if (currentAO > highestAO)
    			highestAO= currentAO;
		}
    	return highestAO;
    }
    
    
    /**
     * returns the highest mark a learner has achieved
     * getHighestMark(HttpServletRequest request, Long queUsrId)
     * 
     * @param request
     * @param queUsrId
     * @return
     */
    public static int getHighestMark(HttpServletRequest request, Long queUsrId)
    {
    	IMcService mcService =McUtils.getToolService(request);
    	List listMarks=mcService.getHighestMark(queUsrId);
    	
    	Iterator itMarks=listMarks.iterator();
    	int highestMark=0;
    	while (itMarks.hasNext())
		{
    		McUsrAttempt mcUsrAttempt=(McUsrAttempt)itMarks.next();
    		int currentMark=mcUsrAttempt.getMark().intValue();
    		if (currentMark > highestMark)
    			highestMark= currentMark;
		}
    	return highestMark;
    }
    
    /**
     * return the top mark for all learners
     * getTopMark(HttpServletRequest request)
     * 
     * @param request
     * @return
     */
    public static int getTopMark(HttpServletRequest request)
    {
    	IMcService mcService =McUtils.getToolService(request);
    	List listMarks=mcService.getMarks();
    	
    	Iterator itMarks=listMarks.iterator();
    	int highestMark=0;
    	while (itMarks.hasNext())
		{
    		McUsrAttempt mcUsrAttempt=(McUsrAttempt)itMarks.next();
    		int currentMark=mcUsrAttempt.getMark().intValue();
    		if (currentMark > highestMark)
    			highestMark= currentMark;
		}
    	return highestMark;
    }

    
    /**
     * return the lowest mark for all learners
     * getTopMark(HttpServletRequest request)
     * 
     * @param request
     * @return
     */
    public static int getLowestMark(HttpServletRequest request)
    {
    	IMcService mcService =McUtils.getToolService(request);
    	List listMarks=mcService.getMarks();
    	
    	Iterator itMarks=listMarks.iterator();
    	int lowestMark=100;
    	while (itMarks.hasNext())
		{
    		McUsrAttempt mcUsrAttempt=(McUsrAttempt)itMarks.next();
    		int currentMark=mcUsrAttempt.getMark().intValue();
    		if (currentMark < lowestMark)
    			lowestMark= currentMark;
		}
    	
    	//in case there was no attempts
    	if (lowestMark == 100)
    		lowestMark=0;
    	
    	return lowestMark;
    }
    
    /**
     * return the average mark for all learners
     * getTopMark(HttpServletRequest request)
     * 
     * @param request
     * @return
     */
    public static int getAverageMark(HttpServletRequest request)
    {
    	IMcService mcService =McUtils.getToolService(request);
    	List listMarks=mcService.getMarks();
    	
    	Iterator itMarks=listMarks.iterator();
    	int marksTotal=0;
    	int count=0;
    	while (itMarks.hasNext())
		{
    		McUsrAttempt mcUsrAttempt=(McUsrAttempt)itMarks.next();
    		int currentMark=mcUsrAttempt.getMark().intValue();
    		marksTotal=marksTotal + currentMark;
			count++;
		}
    	logger.debug("marksTotal: " + marksTotal);
    	logger.debug("count: " + count);
    	
    	int averageMark=0;
    	if (count > 0)
    	{
    		averageMark= (marksTotal / count);
    	}
    	
    	logger.debug("averageMark: " + averageMark);
    	return averageMark;
    }
    
    
    /**
     * conversts correct options list to correct options map
     * buildMapCorrectOptions(List correctOptions)
     * 
     * @param correctOptions
     * @return Map
     */
    public static Map buildMapCorrectOptions(List correctOptions)
	{
    	Map mapCorrectOptions= new TreeMap(new McComparator());
    	Iterator correctOptionsIterator=correctOptions.iterator();
    	Long mapIndex=new Long(1);
    	while (correctOptionsIterator.hasNext())
    	{
    		McOptsContent mcOptsContent=(McOptsContent)correctOptionsIterator.next();
    		if (mcOptsContent != null)
    		{
    			mapCorrectOptions.put(mapIndex.toString(),mcOptsContent.getMcQueOptionText());
        		mapIndex=new Long(mapIndex.longValue()+1);
    		}
    	}
    	logger.debug("mapCorrectOptions : " + mapCorrectOptions);
    	return mapCorrectOptions;
	}
    
    
    public static Map compare(Map mapGeneralCorrectOptions,Map mapGeneralCheckedOptionsContent)
    {
    	logger.debug("incoming mapGeneralCorrectOptions : " + mapGeneralCorrectOptions);
    	logger.debug("incoming mapGeneralCheckedOptionsContent : " + mapGeneralCheckedOptionsContent);
    	
    	Map mapLeanerAssessmentResults= new TreeMap(new McComparator());
    	
    	if (mapGeneralCheckedOptionsContent == null)
    		return mapLeanerAssessmentResults;
		
    	Iterator itMap = mapGeneralCorrectOptions.entrySet().iterator();
    	boolean compareResult= false;
		while (itMap.hasNext()) {
			compareResult= false;
        	Map.Entry pairs = (Map.Entry)itMap.next();
            
            Iterator itCheckedMap = mapGeneralCheckedOptionsContent.entrySet().iterator();
            while (itCheckedMap.hasNext()) 
            {
            	compareResult= false;
            	Map.Entry checkedPairs = (Map.Entry)itCheckedMap.next();
                if (pairs.getKey().toString().equals(checkedPairs.getKey().toString()))
    			{
                    Map mapCorrectOptions=(Map) pairs.getValue();
                    Map mapCheckedOptions=(Map) checkedPairs.getValue();
                    
                    boolean isEqual=compareMaps(mapCorrectOptions, mapCheckedOptions);
                    boolean isEqualCross=compareMapsCross(mapCorrectOptions, mapCheckedOptions);
                    
                    compareResult= isEqual && isEqualCross; 
                    mapLeanerAssessmentResults.put(pairs.getKey(), new Boolean(compareResult).toString());
        		}
            }
		}
		logger.debug("constructed mapLeanerAssessmentResults: " + mapLeanerAssessmentResults);
		return mapLeanerAssessmentResults;
    }
    
    
    public static boolean compareMaps(Map mapCorrectOptions, Map mapCheckedOptions)
	{
	   logger.debug("mapCorrectOptions: " +  mapCorrectOptions);
       logger.debug("mapCheckedOptions: " +  mapCheckedOptions);
       
       Iterator itMap = mapCorrectOptions.entrySet().iterator();
       boolean response=false;
       while (itMap.hasNext()) 
       {
       		Map.Entry pairs = (Map.Entry)itMap.next();
			boolean optionExists=optionExists(pairs.getValue().toString(), mapCheckedOptions);
			
			if (optionExists == false)
			{
				return false;
			}
		}
       return true;
	}
    
    /**
     * compareMapsCross(Map mapCorrectOptions, Map mapCheckedOptions)
     * 
     * @param mapCorrectOptions
     * @param mapCheckedOptions
     * @return boolean
     */
    public static boolean compareMapsCross(Map mapCorrectOptions, Map mapCheckedOptions)
	{
	   logger.debug("mapCorrectOptions: " +  mapCorrectOptions);
       logger.debug("mapCheckedOptions: " +  mapCheckedOptions);
       
       Iterator itMap = mapCheckedOptions.entrySet().iterator();
       boolean response=false;
       while (itMap.hasNext()) 
       {
       		Map.Entry pairs = (Map.Entry)itMap.next();
			boolean optionExists=optionExists(pairs.getValue().toString(), mapCorrectOptions);
			
			if (optionExists == false)
			{
				return false;
			}
		}
       return true;
	}
    
    
    /**
     * optionExists(String optionValue, Map generalMap)
     * 
     * @param optionValue
     * @param generalMap
     * @return boolean
     */
    public static boolean optionExists(String optionValue, Map generalMap)
    {
    	Iterator itMap = generalMap.entrySet().iterator();
    	while (itMap.hasNext()) 
    	{
       		Map.Entry pairsChecked = (Map.Entry)itMap.next();
       		if (pairsChecked.getValue().equals(optionValue))
				return true;
    	}	
    	return false;
    }
	
    public static McQueUsr getUser(HttpServletRequest request)
	{
		IMcService mcService =McUtils.getToolService(request);
	    Long queUsrId=McUtils.getUserId();
	    
    	Long toolSessionId=(Long)request.getSession().getAttribute(TOOL_SESSION_ID);
    	logger.debug("toolSessionId: " + toolSessionId);
    	
    	McSession mcSession=mcService.retrieveMcSession(toolSessionId);
        logger.debug("retrieving mcSession: " + mcSession);
        McQueUsr mcQueUsr=mcService.getMcUserBySession(queUsrId, mcSession.getUid());
		//McQueUsr mcQueUsr=mcService.retrieveMcQueUsr(queUsrId);
		return mcQueUsr;
	}
    
    
    /**
     * creates the user in the db
     * createUser(HttpServletRequest request)
     * 
     * @param request
     */
    public static McQueUsr createUser(HttpServletRequest request)
	{
		IMcService mcService =McUtils.getToolService(request);
	    Long queUsrId=McUtils.getUserId();
		String username=McUtils.getUserName();
		String fullname=McUtils.getUserFullName();
		Long toolSessionId=(Long) request.getSession().getAttribute(TOOL_SESSION_ID);
		
		McSession mcSession=mcService.retrieveMcSession(toolSessionId);
		McQueUsr mcQueUsr= new McQueUsr(queUsrId, 
										username, 
										fullname,  
										mcSession, 
										new TreeSet());		
		mcService.createMcQueUsr(mcQueUsr);
		logger.debug("created mcQueUsr in the db: " + mcQueUsr);
		return mcQueUsr;
	}

    
    /**
     * creates a user attempt in the db
     * createAttempt(HttpServletRequest request)
     * 
     * @param request
     */
    public static void createAttempt(HttpServletRequest request, McQueUsr mcQueUsr, Map mapGeneralCheckedOptionsContent, int mark,  boolean passed, int highestAttemptOrder, Map mapLeanerAssessmentResults)
	{
		IMcService mcService =McUtils.getToolService(request);
		Date attempTime=McUtils.getGMTDateTime();
		String timeZone= McUtils.getCurrentTimeZone();
		logger.debug("timeZone: " + timeZone);
		
		Long toolContentUID= (Long) request.getSession().getAttribute(TOOL_CONTENT_UID);
		logger.debug("toolContentUID: " + toolContentUID);
		 	
		if (toolContentUID != null)
		{
			Iterator itCheckedMap = mapGeneralCheckedOptionsContent.entrySet().iterator();
	        while (itCheckedMap.hasNext()) 
	        {
	        	Map.Entry checkedPairs = (Map.Entry)itCheckedMap.next();
	            Map mapCheckedOptions=(Map) checkedPairs.getValue();
	            Long questionDisplayOrder=new Long(checkedPairs.getKey().toString());
	            
	            logger.debug("questionDisplayOrder: " + questionDisplayOrder);
	            String isAttemptCorrect=(String)mapLeanerAssessmentResults.get(questionDisplayOrder.toString());
	            logger.debug("isAttemptCorrect: " + isAttemptCorrect);
	            
	            McQueContent mcQueContent=mcService.getQuestionContentByDisplayOrder(questionDisplayOrder, toolContentUID);
	            logger.debug("mcQueContent: " + mcQueContent);
	            
	            if (mcQueContent != null)
	            {
	                createIndividualOptions(request, mapCheckedOptions, mcQueContent, mcQueUsr, attempTime, timeZone, mark, passed, new Integer(highestAttemptOrder), isAttemptCorrect);    
	            }
	        }			
		}
	 }
    
    
    public static void createIndividualOptions(HttpServletRequest request, Map mapCheckedOptions, McQueContent mcQueContent, McQueUsr mcQueUsr, Date attempTime, String timeZone, int mark,  boolean passed, Integer highestAttemptOrder, String isAttemptCorrect)
    {
    	IMcService mcService =McUtils.getToolService(request);
    	Integer IntegerMark= new Integer(mark);
		
    	logger.debug("createIndividualOptions-> isAttemptCorrect: " + isAttemptCorrect);
    	logger.debug("mcQueContent: " + mcQueContent);
    	logger.debug("mapCheckedOptions: " + mapCheckedOptions);
    	
    	
    	if (mcQueContent != null)
    	{
    	    if (mapCheckedOptions != null)
        	{
        	    Iterator itCheckedMap = mapCheckedOptions.entrySet().iterator();
                while (itCheckedMap.hasNext()) 
                {
                	Map.Entry checkedPairs = (Map.Entry)itCheckedMap.next();
                	McOptsContent mcOptsContent= mcService.getOptionContentByOptionText(checkedPairs.getValue().toString(), mcQueContent.getUid());
                	logger.debug("mcOptsContent: " + mcOptsContent);
                	if (mcOptsContent != null)
                	{
                	    McUsrAttempt mcUsrAttempt=new McUsrAttempt(attempTime, timeZone, mcQueContent, mcQueUsr, mcOptsContent, IntegerMark, passed, highestAttemptOrder, new Boolean(isAttemptCorrect).booleanValue());
	    			    mcService.createMcUsrAttempt(mcUsrAttempt);
                    	logger.debug("created mcUsrAttempt in the db :" + mcUsrAttempt);
                	}
                }    
        	}    
    	}
    }
    
    
    /**
     * buildWeightsMap(HttpServletRequest request, Long toolContentId)
     * 
     * @param request
     * @param toolContentId
     * @return Map
     */
    public static Map buildWeightsMap(HttpServletRequest request, Long toolContentId)
    {
    	IMcService mcService =McUtils.getToolService(request);
    	Map mapWeights= new TreeMap(new McComparator());
    	McContent mcContent=mcService.retrieveMc(toolContentId);
		
    	List questionsContent=mcService.refreshQuestionContent(mcContent.getUid());
    	
    	Iterator listIterator=questionsContent.iterator();
    	Long mapIndex=new Long(1);
    	while (listIterator.hasNext())
    	{
    		McQueContent mcQueContent=(McQueContent)listIterator.next();
    		mapWeights.put(mapIndex.toString(),mcQueContent.getWeight().toString());
    		mapIndex=new Long(mapIndex.longValue()+1);
    	}
    	return mapWeights;
    }
    
    
    public static Map buildQuestionContentMap(HttpServletRequest request, McContent mcContent)
    {
    	IMcService mcService =McUtils.getToolService(request);
    	Map mapQuestionsContent= new TreeMap(new McComparator());
    	
        Iterator contentIterator=mcContent.getMcQueContents().iterator();
    	while (contentIterator.hasNext())
    	{
    		McQueContent mcQueContent=(McQueContent)contentIterator.next();
    		if (mcQueContent != null)
    		{
    			int displayOrder=mcQueContent.getDisplayOrder().intValue();
        		if (displayOrder != 0)
        		{
        			/* add the question to the questions Map in the displayOrder*/
        			mapQuestionsContent.put(new Integer(displayOrder).toString(),mcQueContent.getQuestion() + "      of weight: " + mcQueContent.getWeight().toString() + "%");
        		}
        		
        		/* prepare the first question's candidate answers for presentation*/ 
        		if (displayOrder == 1)
        		{
        			logger.debug("first question... ");
        			Long uid=mcQueContent.getUid();
        			logger.debug("uid : " + uid);
        			List listMcOptions=mcService.findMcOptionsContentByQueId(uid);
        			logger.debug("listMcOptions : " + listMcOptions);
        			Map mapOptionsContent=McUtils.generateOptionsMap(listMcOptions);
        			request.getSession().setAttribute(MAP_OPTIONS_CONTENT, mapOptionsContent);
        			logger.debug("updated Options Map: " + request.getSession().getAttribute(MAP_OPTIONS_CONTENT));
        		}
    		}
    	}
    	return mapQuestionsContent;
    }
    

    public static int getLearnerMarkAtLeast(Integer passMark, Map mapQuestionWeights)
    {
    	logger.debug("doing getLearnerMarkAtLeast");
    	logger.debug("passMark:" + passMark);
        logger.debug("mapQuestionWeights:" + mapQuestionWeights);
        
    	if ((passMark == null) || (passMark.intValue() == 0))
    	{
    		logger.debug("no passMark..");
    		return 0;
    	}
    	else if ((passMark != null) && (passMark.intValue() != 0))
    	{
    		int minimumQuestionCountToPass=calculateMinimumQuestionCountToPass(passMark, mapQuestionWeights);
    		logger.debug("minimumQuestionCountToPass: " + minimumQuestionCountToPass);
    		return minimumQuestionCountToPass;
    	}
    	return 0;
    }


    public static int calculateMinimumQuestionCountToPass(Integer passMark, Map mapQuestionWeights)
    {
    	logger.debug("calculating minimumQuestionCountToPass: mapQuestionWeights: " + mapQuestionWeights + " passmark: " + passMark);
    	logger.debug("passMark: " + passMark);
    	logger.debug("original mapQuestionWeights: " + mapQuestionWeights);
    	
    	int minimumQuestionCount=0;
    	int totalHighestWeights=0;
    	while (totalHighestWeights <= passMark.intValue())
    	{
    		logger.debug("totalHighestWeights versus passMark: " + totalHighestWeights + " versus" + passMark);
        	int highestWeight=getHighestWeight(mapQuestionWeights);
        	logger.debug("highestWeight: " + highestWeight);
        	totalHighestWeights=totalHighestWeights + highestWeight;
        	logger.debug("totalHighestWeights: " + totalHighestWeights);
        	mapQuestionWeights=rebuildWeightsMapExcludeHighestWeight(mapQuestionWeights, highestWeight);
    		logger.debug("mapQuestionWeights: " + mapQuestionWeights);
    		++minimumQuestionCount;
    		if (mapQuestionWeights.size() == 0)
    		{
    			logger.debug("no more weights: ");
    			break;
    		}
    	}
    	logger.debug("returning minimumQuestionCount: " + minimumQuestionCount);
    	return minimumQuestionCount;
    }
    
    
	public static int getHighestWeight(Map mapQuestionWeights)
	{
	   
		if (mapQuestionWeights.size() == 1)
		{
			logger.debug("using map with 1 question only");
			/*the only alternative is 100*/
			return 100;
		}
		
		logger.debug("using map with > 1 questions");
	   Iterator itMap = mapQuestionWeights.entrySet().iterator();
 	   int highestWeight=0; 	   
       while (itMap.hasNext()) 
       {
       		Map.Entry pair = (Map.Entry)itMap.next();
       		String weight=pair.getValue().toString();
       		int intWeight=new Integer(weight).intValue();
       		
			if (intWeight > highestWeight)
				highestWeight= intWeight;
       }
       return highestWeight;
	}

	
	public static Map rebuildWeightsMapExcludeHighestWeight(Map mapQuestionWeights, int highestWeight)
	{
		logger.debug("doing rebuildWeightsMapExcludeHighestWeight: " + mapQuestionWeights);
		logger.debug("doing highestWeight: " + highestWeight);
		
	   Map mapWeightsExcludeHighestWeight= new TreeMap(new McComparator());
	   
	   Iterator itMap = mapQuestionWeights.entrySet().iterator();
	   Long mapIndex=new Long(1);
       while (itMap.hasNext()) 
       {
       		Map.Entry pair = (Map.Entry)itMap.next();
       		String weight=pair.getValue().toString();
       		int intWeight=new Integer(weight).intValue();
       		logger.debug("intWeight: " + intWeight);
       		logger.debug("intWeight versus highestWeight:" + intWeight + " versus" + highestWeight);
       		if (intWeight != highestWeight)
       		{
           		mapWeightsExcludeHighestWeight.put(mapIndex.toString(),weight);
    	   		mapIndex=new Long(mapIndex.longValue()+1);
       		}
       		else
       		{
       			logger.debug("excluding highest weight from the reconstructed map: " + intWeight);
       		}
       }
       logger.debug("returning mapWeightsExcludeHighestWeight: " + mapWeightsExcludeHighestWeight);
       return mapWeightsExcludeHighestWeight; 
	}

    
    /**
     * removes Learning session attributes
     * cleanUpLearningSession(HttpServletRequest request)
     * 
     * @param request
     */
    public static void cleanUpLearningSession(HttpServletRequest request)
    {
    	request.getSession().removeAttribute(USER_ID);
    	request.getSession().removeAttribute(TOOL_CONTENT_ID);
    	request.getSession().removeAttribute(TOOL_SESSION_ID);
    	request.getSession().removeAttribute(QUESTION_LISTING_MODE);
    	request.getSession().removeAttribute(QUESTION_LISTING_MODE_SEQUENTIAL);
    	request.getSession().removeAttribute(QUESTION_LISTING_MODE_COMBINED);
    	request.getSession().removeAttribute(MAP_OPTIONS_CONTENT);
    	request.getSession().removeAttribute(MAP_QUESTION_CONTENT_LEARNER);
    	request.getSession().removeAttribute(TOTAL_QUESTION_COUNT);
    	request.getSession().removeAttribute(CURRENT_QUESTION_INDEX);
    	request.getSession().removeAttribute(LEARNER_LAST_ATTEMPT_ORDER);
    	request.getSession().removeAttribute(LEARNER_BEST_MARK);
    	request.getSession().removeAttribute(PASSMARK);
    	request.getSession().removeAttribute(REPORT_TITLE_LEARNER);
    	request.getSession().removeAttribute(IS_CONTENT_IN_USE);
    	request.getSession().removeAttribute(IS_TOOL_ACTIVITY_OFFLINE);
    	request.getSession().removeAttribute(IS_USERNAME_VISIBLE);
    	request.getSession().removeAttribute(IS_SHOW_FEEDBACK);
    	request.getSession().removeAttribute(TOTAL_COUNT_REACHED);
    	request.getSession().removeAttribute(COUNT_SESSION_COMPLETE);
    	request.getSession().removeAttribute(CURRENT_QUESTION_INDEX);
    	request.getSession().removeAttribute(TOP_MARK);
    	request.getSession().removeAttribute(LOWEST_MARK);
    	request.getSession().removeAttribute(AVERAGE_MARK);
    	request.getSession().removeAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT);
    	request.getSession().removeAttribute(MAP_LEARNER_ASSESSMENT_RESULTS);
    	request.getSession().removeAttribute(MAP_LEARNER_FEEDBACK_INCORRECT);
    	request.getSession().removeAttribute(MAP_LEARNER_FEEDBACK_CORRECT);
    	request.getSession().removeAttribute(MAP_QUE_ATTEMPTS);
    	request.getSession().removeAttribute(MAP_QUE_CORRECT_ATTEMPTS);
    	request.getSession().removeAttribute(MAP_QUE_INCORRECT_ATTEMPTS);
    	request.getSession().removeAttribute(MAP_QUESTION_WEIGHTS);
    	request.getSession().removeAttribute(USER_EXCEPTION_USERID_NOTAVAILABLE);
    	request.getSession().removeAttribute(USER_EXCEPTION_TOOLSESSIONID_REQUIRED);
    	request.getSession().removeAttribute(USER_EXCEPTION_NUMBERFORMAT);
    	request.getSession().removeAttribute(LEARNING_MODE);
    	request.getSession().removeAttribute(PREVIEW_ONLY);
    	request.getSession().removeAttribute(LEARNER_PROGRESS);
    	request.getSession().removeAttribute(LEARNER_PROGRESS_USERID);
    }
 }
