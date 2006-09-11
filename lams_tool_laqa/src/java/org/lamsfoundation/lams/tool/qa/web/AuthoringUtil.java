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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/

/* $$Id$$ */
package org.lamsfoundation.lams.tool.qa.web;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.tool.qa.QaAppConstants;
import org.lamsfoundation.lams.tool.qa.QaComparator;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaGeneralAuthoringDTO;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.service.IQaService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;

/**
 * 
 * Keeps all operations needed for Authoring mode. 
 * @author Ozgur Demirtas
 *
 */
public class AuthoringUtil implements QaAppConstants {
	static Logger logger = Logger.getLogger(AuthoringUtil.class.getName());
    
	
    /**
     * reconstructQuestionContentMapForAdd(TreeMap mapQuestionContent, HttpServletRequest request)
     * return void
     * adds a new entry to the Map
     */
    protected void reconstructQuestionContentMapForAdd(Map mapQuestionContent, QaGeneralAuthoringDTO qaGeneralAuthoringDTO, 
            HttpServletRequest request)
    {
    	logger.debug("pre-add Map content: " + mapQuestionContent);
    	logger.debug("pre-add Map size: " + mapQuestionContent.size());
    	
    	repopulateMap(mapQuestionContent, request);
    	
    	mapQuestionContent.put(new Long(mapQuestionContent.size()+1).toString(), "");
    	//request.getSession().setAttribute("mapQuestionContent", mapQuestionContent);
    	qaGeneralAuthoringDTO.setMapQuestionContent(mapQuestionContent);
	     
    	logger.debug("post-add Map is: " + mapQuestionContent);    	
	   	logger.debug("post-add count " + mapQuestionContent.size());
    }


    /**
     * reconstructQuestionContentMapForRemove(TreeMap mapQuestionContent, HttpServletRequest request)
     * return void
     * deletes the requested entry from the Map
     */
    protected Map reconstructQuestionContentMapForRemove(Map mapQuestionContent, HttpServletRequest request, 
            QaAuthoringForm qaAuthoringForm, String activeModule)
    {
    		logger.debug("doing reconstructQuestionContentMapForRemove with activeModule: " + activeModule);
    	 	String questionIndex =qaAuthoringForm.getQuestionIndex();
    	 	logger.debug("pre-delete map content:  " + mapQuestionContent);
    	 	logger.debug("questionIndex: " + questionIndex);
    	 	
    	 	// watch here and remove request.getSession().getAttribute(REMOVABLE_QUESTION_INDEX);
    	 	String removableQuestionIndex=null;
    	 	if (activeModule.equals(MONITORING))
    	 	{
       	 		removableQuestionIndex=(String)request.getSession().getAttribute(REMOVABLE_QUESTION_INDEX);
        	 	logger.debug("removableQuestionIndex: " + removableQuestionIndex);
        	 	questionIndex=removableQuestionIndex;
    	 	}
    	 	logger.debug("final removableQuestionIndex: " + questionIndex);
    	 	
    	 	
    	 	long longQuestionIndex= new Long(questionIndex).longValue();
    	 	logger.debug("pre-delete count: " + mapQuestionContent.size());
    	 	
        	repopulateMap(mapQuestionContent, request);
        	logger.debug("post-repopulateMap questionIndex: " + questionIndex);
        	
	 		mapQuestionContent.remove(new Long(longQuestionIndex).toString());	
	 		logger.debug("removed the question content with index: " + longQuestionIndex);
	 		
	 		logger.debug("returning mapQuestionContent:" + mapQuestionContent);
	 		return mapQuestionContent;

    }

    
    /**
     * reconstructQuestionContentMapForSubmit(TreeMap mapQuestionContent, HttpServletRequest request)
     * return void
     * 
    */
    protected  void reconstructQuestionContentMapForSubmit(Map mapQuestionContent, HttpServletRequest request)
    {
    	logger.debug("pre-submit Map:" + mapQuestionContent);
    	logger.debug("pre-submit Map size :" + mapQuestionContent.size());
    	
    	repopulateMap(mapQuestionContent, request);
    	Map mapFinalQuestionContent = new TreeMap(new QaComparator());
    	
    	Iterator itMap = mapQuestionContent.entrySet().iterator();
	    while (itMap.hasNext()) {
	        Map.Entry pairs = (Map.Entry)itMap.next();
	        if ((pairs.getValue() != null) && (!pairs.getValue().equals("")))
    		{
	        	mapFinalQuestionContent.put(pairs.getKey(), pairs.getValue());
	        	logger.debug("adding the  pair: " +  pairs.getKey() + " = " + pairs.getValue());
    		}
	    }
	    
	    mapQuestionContent=(TreeMap)mapFinalQuestionContent;
	    request.getSession().setAttribute("mapQuestionContent", mapQuestionContent);
	    logger.debug("final mapQuestionContent:" + mapQuestionContent);
    }
    
    
    /**
     * repopulateMap(TreeMap mapQuestionContent, HttpServletRequest request)
     * return void 
     * repopulates the user entries into the Map
     */
    protected void repopulateMap(Map mapQuestionContent, HttpServletRequest request)
    {
        logger.debug("starting repopulateMap");
        int intQuestionIndex= mapQuestionContent.size();
        logger.debug("intQuestionIndex: " + intQuestionIndex);

    	/* if there is data in the Map remaining from previous session remove those */
		mapQuestionContent.clear();
		logger.debug("Map got initialized: " + mapQuestionContent);
		
		for (long i=0; i < intQuestionIndex  ; i++)
		{
			String candidateQuestionEntry =request.getParameter("questionContent" + i);
			if (i==0)
    		{
    			logger.debug("defaultQuestionContent set to: " + candidateQuestionEntry);
    		}
			if ((candidateQuestionEntry != null) && (candidateQuestionEntry.length() > 0))
			{
				logger.debug("using key: " + i);
				mapQuestionContent.put(new Long(i+1).toString(), candidateQuestionEntry);
				logger.debug("added new entry.");	
			}
		}
    }


    public QaContent  saveOrUpdateQaContent(Map mapQuestionContent, IQaService qaService, QaAuthoringForm qaAuthoringForm, 
            HttpServletRequest request, QaContent qaContent, String strToolContentID)
    {
        UserDTO toolUser = (UserDTO) SessionManager.getSession().getAttribute(AttributeNames.USER);
        
        boolean isQuestionsSequenced=false;
        boolean isSynchInMonitor=false;
        boolean isUsernameVisible=false;

        String richTextTitle = request.getParameter(TITLE);
        String richTextInstructions = request.getParameter(INSTRUCTIONS);

        logger.debug("richTextTitle: " + richTextTitle);
        logger.debug("richTextInstructions: " + richTextInstructions);

		String synchInMonitor=request.getParameter(SYNC_IN_MONITOR);
		logger.debug("synchInMonitor: " + synchInMonitor);
		String usernameVisible=request.getParameter(USERNAME_VISIBLE);
		logger.debug("usernameVisible: " + usernameVisible);
		String questionsSequenced=request.getParameter(QUESTIONS_SEQUENCED);
		logger.debug("questionsSequenced: " + questionsSequenced);
		String reportTitle=request.getParameter(REPORT_TITLE);
		String monitoringReportTitle=request.getParameter(MONITORING_REPORT_TITLE);
		String richTextOfflineInstructions=request.getParameter(OFFLINE_INSTRUCTIONS);
		String richTextOnlineInstructions=request.getParameter(ONLINE_INSTRUCTIONS);
		String reflect=request.getParameter(REFLECT);
		logger.debug("reflect: " + reflect);
		
		String reflectionSubject=request.getParameter(REFLECTION_SUBJECT);
		logger.debug("reflectionSubject: " + reflectionSubject);
		
		String activeModule=request.getParameter(ACTIVE_MODULE);
		logger.debug("activeModule: " + activeModule);
        
        
        boolean setCommonContent=true; 
        if ((questionsSequenced == null) || (synchInMonitor == null) || 
             (usernameVisible == null) || (reflect == null))
        {
        	setCommonContent=false;
        }
        logger.debug("setCommonContent: " + setCommonContent);
		
        boolean questionsSequencedBoolean=false;
        boolean synchInMonitorBoolean=false;
        boolean usernameVisibleBoolean=false;
        boolean reflectBoolean=false;
        //if (setCommonContent)
        //{
            if ((questionsSequenced != null) && (questionsSequenced.equalsIgnoreCase("1")))
                questionsSequencedBoolean=true;            
            
            if ((synchInMonitor != null) && (synchInMonitor.equalsIgnoreCase("1")))
                synchInMonitorBoolean=true;            
            
            if ((usernameVisible != null) && (usernameVisible.equalsIgnoreCase("1")))
                usernameVisibleBoolean=true;            

            if ((reflect != null) && (reflect.equalsIgnoreCase("1")))
                reflectBoolean=true;
        //}
            
            
        logger.debug("questionsSequencedBoolean: " + questionsSequencedBoolean);
        logger.debug("synchInMonitorBoolean: " + synchInMonitorBoolean);
        logger.debug("usernameVisibleBoolean: " + usernameVisibleBoolean);
        logger.debug("reflectBoolean: " + reflectBoolean);
        
        long userId=0;
        if (toolUser != null)
        {
        	userId = toolUser.getUserID().longValue();	
        }
        else
        {
    		HttpSession ss = SessionManager.getSession();
    		logger.debug("ss: " + ss);
    		UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
    		logger.debug("user" + user);
    		if (user != null)
    		{
    			userId = user.getUserID().longValue();	
    		}
    		else
    		{
    			logger.debug("should not reach here");
    			userId=0;
    		}
        }
        logger.debug("userId: " + userId);
     	logger.debug("qaContent: " + qaContent);
     	
     	boolean newContent=false;
        if(qaContent == null)
        {
        	qaContent = new QaContent();
        	newContent=true;
        }


    	logger.debug("setting common content values..." + richTextTitle + " " + richTextInstructions);
    	qaContent.setQaContentId(new Long(strToolContentID));
     	qaContent.setTitle(richTextTitle);
     	qaContent.setInstructions(richTextInstructions);
     	qaContent.setUpdateDate(new Date(System.currentTimeMillis())); /**keep updating this one*/
     	logger.debug("userId: " + userId);
     	qaContent.setCreatedBy(userId); /**make sure we are setting the userId from the User object above*/
     	logger.debug("end of setting common content values...");

     	
        //if ((!activeModule.equals(DEFINE_LATER)) && (setCommonContent))
     	if (activeModule.equals(AUTHORING))
		{
        	logger.debug("setting other content values...");
         	qaContent.setQuestionsSequenced(isQuestionsSequenced); /**the default question listing in learner mode will be all in the same page*/
         	qaContent.setSynchInMonitor(isSynchInMonitor);
         	qaContent.setOnlineInstructions(richTextOnlineInstructions);
         	qaContent.setOfflineInstructions(richTextOfflineInstructions);
         	qaContent.setReportTitle(reportTitle);
         	qaContent.setMonitoringReportTitle(monitoringReportTitle);
         	qaContent.setUsernameVisible(usernameVisibleBoolean);
         	qaContent.setQuestionsSequenced(questionsSequencedBoolean);
         	qaContent.setSynchInMonitor(synchInMonitorBoolean);	
         	qaContent.setReflect(reflectBoolean);
         	qaContent.setReflectionSubject(reflectionSubject);
		}
        	
 
        if (newContent)
        {
        	logger.debug("will create: " + qaContent);
         	qaService.createQa(qaContent);
        }
        else
        {
        	logger.debug("will update: " + qaContent);
            qaService.updateQa(qaContent);
        }
        
        qaContent=qaService.loadQa(new Long(strToolContentID).longValue());
     	logger.debug("qaContent: " + qaContent);
        
        qaContent=createQuestionContent(mapQuestionContent, qaService, qaContent);
        
        return qaContent;
    }
    
    
    /**
     * removes unused question entries from db
     * removeRedundantQuestions (Map mapQuestionContent, IQaService qaService, QaAuthoringForm qaAuthoringForm)
     * 
     * @param mapQuestionContent
     * @param qaService
     * @param qaAuthoringForm
     */
    public void removeRedundantQuestions (Map mapQuestionContent, IQaService qaService, QaAuthoringForm qaAuthoringForm, 
            HttpServletRequest request, String toolContentID)
	{
    	logger.debug("removing unused entries... ");
    	logger.debug("mapQuestionContent:  " + mapQuestionContent);
    	logger.debug("toolContentID:  " + toolContentID);
    	
    	QaContent qaContent=qaService.loadQa( new Long(toolContentID).longValue());
    	logger.debug("qaContent:  " + qaContent);
    	
    	if (qaContent != null)
    	{
        	logger.debug("qaContent uid: " + qaContent.getUid());
        	List allQuestions=qaService.getAllQuestionEntries(qaContent.getUid());
        	logger.debug("allQuestions: " + allQuestions);
        	
        	Iterator listIterator=allQuestions.iterator();
    		Long mapIndex=new Long(1);
    		boolean entryUsed=false;
    		while (listIterator.hasNext())
    		{
    			QaQueContent queContent=(QaQueContent)listIterator.next();
    			logger.debug("queContent data: " + queContent);
    			logger.debug("queContent: " + queContent.getQuestion() + " " + queContent.getDisplayOrder());
    			
    			entryUsed=false;
    	        Iterator itMap = mapQuestionContent.entrySet().iterator();
    	        int diplayOrder=0;
    	        while (itMap.hasNext()) 
    		    {
    	        	entryUsed=false;
    		        Map.Entry pairs = (Map.Entry)itMap.next();
    		        logger.debug("using the pair: " +  pairs.getKey() + " = " + pairs.getValue());
    		        if (pairs.getValue().toString().length() != 0)
    		        {
    		        	logger.debug("text from map:" + pairs.getValue().toString());
    		        	logger.debug("text from db:" + queContent.getQuestion());
    		        	if (pairs.getValue().toString().equals(queContent.getQuestion()))
    		        	{
    		        		logger.debug("used entry in db:" + queContent.getQuestion());
    		        		entryUsed=true;
    		        		break;
    		        	}
    		        }
    		    }
    	        
    	        if (entryUsed == false)
    	        {
    	        	logger.debug("removing unused entry in db:" + queContent.getQuestion());
    	        	
    	        	QaQueContent removeableQaQueContent=qaService.getQuestionContentByQuestionText(queContent.getQuestion(), qaContent.getUid());
        			logger.debug("removeableQaQueContent"  + removeableQaQueContent);
        			if (removeableQaQueContent != null)
        			{
        				qaService.removeQaQueContent(removeableQaQueContent);
            			logger.debug("removed removeableQaQueContent from the db: " + removeableQaQueContent);	
        			}
    	        	
    	        }
    		}    		
    	}
	
	}
    
    /**
     * createQuestionContent(TreeMap mapQuestionContent, HttpServletRequest request)
     * return void
     * 
     * persist the questions in the Map the user has submitted 
     */
    protected QaContent createQuestionContent(Map mapQuestionContent, IQaService qaService, QaContent qaContent)
    {    
        logger.debug("content uid is: " + qaContent.getUid());
        List questions=qaService.retrieveQaQueContentsByToolContentId(qaContent.getUid().longValue());
        logger.debug("questions: " + questions);

        
        Iterator itMap = mapQuestionContent.entrySet().iterator();
        int diplayOrder=0;
        while (itMap.hasNext()) 
	    {
	        Map.Entry pairs = (Map.Entry)itMap.next();
	        logger.debug("using the pair: " +  pairs.getKey() + " = " + pairs.getValue());
	        
	        if (pairs.getValue().toString().length() != 0)
	        {
	        	logger.debug("starting createQuestionContent: pairs.getValue().toString():" + pairs.getValue().toString());
	        	logger.debug("starting createQuestionContent: qaContent: " + qaContent);
	        	logger.debug("starting createQuestionContent: diplayOrder: " + diplayOrder);
	        	
	        	
	        	QaQueContent queContent=  new QaQueContent(pairs.getValue().toString(), 
		        											++diplayOrder, 
															qaContent,
															null,
															null);
		        
		        
			       /* checks if the question is already recorded*/
			       logger.debug("question text is: " + pairs.getValue().toString());
			       logger.debug("content uid is: " + qaContent.getUid());
			       logger.debug("question display order is: " + diplayOrder);
			       QaQueContent existingQaQueContent=qaService.getQuestionContentByDisplayOrder(new Long(diplayOrder), qaContent.getUid());
			       logger.debug("existingQaQueContent: " + existingQaQueContent);
			       if (existingQaQueContent == null)
			       {
			       	/*make sure a question with the same question text is not already saved*/
			    	QaQueContent duplicateQaQueContent=qaService.getQuestionContentByQuestionText(pairs.getValue().toString(), qaContent.getUid());
			    	logger.debug("duplicateQaQueContent: " + duplicateQaQueContent);
				       	if (duplicateQaQueContent == null)
				       	{
				       		logger.debug("adding a new question to content: " + queContent);
				       		qaContent.getQaQueContents().add(queContent);
				       		queContent.setQaContent(qaContent);
		
				       		qaService.createQaQue(queContent);
				       	}
			       }
			       else
			       {
			       		existingQaQueContent.setQuestion(pairs.getValue().toString());
			       		logger.debug("updating the existing question content: " + existingQaQueContent);
			       		qaService.updateQaQueContent(existingQaQueContent);
			       }
	        }      
	    }
        return qaContent;
    }
    
    
    /**
     * sorts the questions by the display order
     * reOrganizeDisplayOrder(Map mapQuestionContent, IQaService qaService, QaAuthoringForm qaAuthoringForm, QaContent qaContent)
     * 
     * @param mapQuestionContent
     * @param qaService
     * @param qaAuthoringForm
     * @param qaContent
     */
    public void reOrganizeDisplayOrder(Map mapQuestionContent, IQaService qaService, QaAuthoringForm qaAuthoringForm, QaContent qaContent)
    {    
    	logger.debug("content uid: " + qaContent.getUid());
    	List sortedQuestions=qaService.getAllQuestionEntriesSorted(qaContent.getUid().longValue());
    	logger.debug("sortedQuestions: " + sortedQuestions);
    	
		Iterator listIterator=sortedQuestions.iterator();
		int displayOrder=1;
		while (listIterator.hasNext())
		{
			QaQueContent queContent=(QaQueContent)listIterator.next();
			logger.debug("queContent data: " + queContent);
			logger.debug("queContent: " + queContent.getQuestion() + " " + queContent.getDisplayOrder());
			
			QaQueContent existingQaQueContent=qaService.getQuestionContentByQuestionText(queContent.getQuestion(), qaContent.getUid());
	    	logger.debug("existingQaQueContent: " + existingQaQueContent);
	    	existingQaQueContent.setDisplayOrder(displayOrder);
	    	logger.debug("updating the existing question content for displayOrder: " + existingQaQueContent);
       		qaService.updateQaQueContent(existingQaQueContent);
			displayOrder++;
		}
		logger.debug("done with reOrganizeDisplayOrder...");
    }
    
    
    /**
     * checks if any entry is duplicate 
     * verifyDuplicatesOptionsMap(Map mapQuestions)
     * 
     * @param mapQuestions
     * @return
     */
    public static boolean verifyDuplicatesOptionsMap(Map mapQuestions)
	{
    	Map originalMap=mapQuestions;
    	Map backupMap=mapQuestions;
    	
    	int optionCount=0;
    	for (long i=1; i <= MAX_QUESTION_COUNT.longValue()  ; i++)
		{
    		String currentOption=(String)originalMap.get(new Long(i).toString());
    		
    		optionCount=0;
    		for (long j=1; j <= MAX_QUESTION_COUNT.longValue() ; j++)
    		{
        		String backedOption=(String)backupMap.get(new Long(j).toString());
        		
        		if ((currentOption != null) && (backedOption !=null))
        		{
        			if (currentOption.equals(backedOption))
    				{
    					optionCount++;
    				}
    				
            		if (optionCount > 1)
            			return false;	
        		}
    		}	
		}
    	return true;
	}
}