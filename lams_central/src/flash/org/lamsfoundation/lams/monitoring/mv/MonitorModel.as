﻿/***************************************************************************
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
 * ************************************************************************
 */

import org.lamsfoundation.lams.monitoring.*;
import org.lamsfoundation.lams.monitoring.mv.*;
import org.lamsfoundation.lams.authoring.Activity;
import org.lamsfoundation.lams.authoring.Transition
import org.lamsfoundation.lams.authoring.GateActivity;
import org.lamsfoundation.lams.authoring.DesignDataModel;
import org.lamsfoundation.lams.common.Sequence;
import org.lamsfoundation.lams.common.util.Observable;
import org.lamsfoundation.lams.common.util.*;
import org.lamsfoundation.lams.common.*;
import mx.managers.*
import mx.utils.*
import mx.events.*;

/*
* Model for the Monitoring Tabs 
*/
class MonitorModel extends Observable{
	private var _className:String = "MonitorModel";
   
	public var RT_FOLDER:String = "Folder";
	public var RT_ORG:String = "Organisation";
	   
	private static var LEARNER_ROLE:String = "LEARNER";
	private static var MONITOR_ROLE:String = "MONITOR";
	private static var TEACHER_ROLE:String = "TEACHER";
	   
	private var __width:Number;
	private var __height:Number;
	private var __x:Number;
	private var __y:Number;
	private var _isDirty:Boolean;
	private var infoObj:Object;
	private var selectedTab:Number;
	private var _dialogOpen:String;		// the type of dialog currently open
	
	private var _staffLoaded:Boolean;
	private var _learnersLoaded:Boolean;
	private var _isLessonProgressChanged:Boolean;
	private var _isSequenceProgressChanged:Boolean;
	private var _isLearnerProgressChanged:Boolean;
	private var _isSequenceSet:Boolean = false;
	private var _isDragging:Boolean;
	private var _isLocked:Boolean;
	private var monitor_y:Number;
	private var monitor_x:Number;
	private var ttHolder:MovieClip;
	private var _monitor:Monitor;
	private var _selectedItem:Object;  // the currently selected thing - could be activity, transition etc.
	private var _prevSelectedItem:Object;
	
	// add model data
	private var _activeSeq:Sequence;
	private var _lastSelectedSeq:Sequence;
	private var app:Application;
	
	private var _org:Organisation;
	private var _todos:Array;  // Array of ToDo ContributeActivity(s)
	
	// state data
	private var _isDesignDrawn:Boolean;
	private var _showLearners:Boolean;
	private var _endGate:MovieClip;
	
	//these are hashtables of mc refs MOVIECLIPS (like CanvasActivity or CanvasTransition)
	//each on contains a reference to the emelment in the ddm (activity or transition)
	private var _activitiesDisplayed:Hashtable;
	private var _transitionsDisplayed:Hashtable;
	private var _learnersProgress:Hashtable;
	
	//this is the dataprovider for the org tree
	private var _treeDP:XML;
	private var _orgResources:Array;	
	private var learnerTabActArr:Array;
	private var ddmActivity_keys:Array;
	private var ddmTransition_keys:Array;
	private var _orgs:Array;
	private var _resultDTO:Object;
	//private var _selectedTreeNode:XMLNode;
	
	private static var USER_LOAD_CHECK_INTERVAL:Number = 50;
	private static var USER_LOAD_CHECK_TIMEOUT_COUNT:Number = 200;
	private var _UserLoadCheckIntervalID:Number;         //Interval ID for periodic check on User Load status
	private var _userLoadCheckCount = 0;				// instance counter for number of times we have checked to see if users are loaded	
	
	
	private var dispatchEvent:Function;       
    public var addEventListener:Function;  
    public var removeEventListener:Function;
	
	/**
	* Constructor.
	*/
	public function MonitorModel (monitor:Monitor){
		_monitor = monitor;
		
		_showLearners = true;
		isDesignDrawn = true;
		_staffLoaded = false;
		_learnersLoaded = false;
		
		_activitiesDisplayed = new Hashtable("_activitiesDisplayed");
		_transitionsDisplayed = new Hashtable("_transitionsDisplayed");
		_learnersProgress = new Hashtable("_learnersProgress")

		_orgResources = new Array();
		learnerTabActArr = new Array();
		ddmActivity_keys = new Array();
		ddmTransition_keys = new Array();
		
		_resultDTO = new Object();
		ttHolder = Application.tooltip;
		monitor_y = Application.MONITOR_Y;
		monitor_x = Application.MONITOR_X;
		
		mx.events.EventDispatcher.initialize(this);
		app = Application.getInstance();
	}
	
	// add get/set methods
	
	public function setSequence(activeSeq:Sequence){
		
		Debugger.log("Active seq: " + activeSeq.ID + " ddm: " + activeSeq.getLearningDesignModel(), Debugger.CRITICAL, "setSequence", "MonitorModel");
		_activeSeq = activeSeq;
		
		var seq:Sequence = Sequence(_activeSeq);
		if (seq.getLearningDesignModel() != null){
			setIsSequenceSet(true);
			
			var obj:Object = app.layout.manager.checkAvailability(_activeSeq);
			locked = obj.locked;
			
		} else {
			getMonitor().openLearningDesign(_activeSeq);
		}
		
		setChanged();
		
		// if seq locked for edit TODO
		//send an update
		infoObj = {};
		infoObj.updateType = "SEQUENCE";
		infoObj.tabID = getSelectedTab();
		notifyObservers(infoObj);
	
	}
	
	public function getSequence():Sequence{
		return _activeSeq;
	}
	
	public function setIsSequenceSet(setSeq:Boolean){
		_isSequenceSet = setSeq;
	}

	private function getIsSequenceSet():Boolean{
		return _isSequenceSet;
	}
	
	public function loadSequence(_seq:Object):Boolean{
		// create new Sequence from DTO
		var seq:Sequence = new Sequence(_seq);
		setSequence(seq);
		
		return true;
	}
	
	public function suspendSequence():Void{
		var callback:Function = Proxy.create(_monitor, _monitor.reloadLessonToMonitor);
		Application.getInstance().getComms().getRequest('monitoring/monitoring.do?method=suspendLesson&lessonID=' + String(_activeSeq.ID) + '&userID=' + _root.userID,callback, false);
	}
	
	public function archiveSequence():Void{
		var callback:Function = Proxy.create(_monitor, _monitor.reloadLessonToMonitor);
		Application.getInstance().getComms().getRequest('monitoring/monitoring.do?method=archiveLesson&lessonID=' + String(_activeSeq.ID) + '&userID=' + _root.userID,callback, false);
	}
	
	public function unarchiveSequence():Void{
		var callback:Function = Proxy.create(_monitor, _monitor.reloadLessonToMonitor);
		Application.getInstance().getComms().getRequest('monitoring/monitoring.do?method=unarchiveLesson&lessonID=' + String(_activeSeq.ID) + '&userID=' + _root.userID,callback, false);
	}
	
	public function removeSequence():Void{
		var callback:Function = Proxy.create(_monitor, _monitor.closeAndRefresh);
		Application.getInstance().getComms().getRequest('monitoring/monitoring.do?method=removeLesson&lessonID=' + String(_activeSeq.ID) + '&userID=' + _root.userID,callback, false);
	}

	public function activateSequence():Void{
		var callback:Function = Proxy.create(_monitor, _monitor.reloadLessonToMonitor);
		Application.getInstance().getComms().getRequest('monitoring/monitoring.do?method=unsuspendLesson&lessonID=' + String(_activeSeq.ID) + '&userID=' + _root.userID,callback, false);
	}

	public function setLessonProgressData(learnerProg:Array){
		//clear the old lot of Learner Progress data
		_learnersProgress.clear();
		learnerTabActArr = new Array();
		learnerTabActArr = learnerProg;
		
		for(var i=0; i<learnerProg.length;i++){
			_learnersProgress.put(learnerProg[i].getLearnerId(),learnerProg[i]);
		}
		
		Debugger.log('Added '+learnerProg.length+' Sequences to _lessonSequences',4,'setLessonSequences','LessonModel');
		
		setChanged();
		
		//send an update
		infoObj = {};
		infoObj.updateType = "DRAW_DESIGN";
		infoObj.tabID = getSelectedTab();
		notifyObservers(infoObj);
		
	}
	
	/**
	* Sets last selected Sequence
	*/
	public function setLastSelectedSequence(seq:Sequence):Void{
		_lastSelectedSeq = seq;
	}
	
	/**
	* Gets last selected Sequence
	*/
	public function getLastSelectedSequence():Sequence{
		return _lastSelectedSeq;
	}

	public function setIsProgressChangedLesson(isChanged:Boolean):Void{
		_isLessonProgressChanged = isChanged;
	}
	public function setIsProgressChangedSequence(isChanged:Boolean):Void{
		_isSequenceProgressChanged = isChanged;
	}
	public function setIsProgressChangedLearner(isChanged:Boolean):Void{
		_isLearnerProgressChanged = isChanged;
	}
	
	public function getIsProgressChangedLesson():Boolean{
		return _isLessonProgressChanged;
	}
	public function getIsProgressChangedSequence():Boolean{
		return _isSequenceProgressChanged;
	}
	public function getIsProgressChangedLearner():Boolean{
		return _isLearnerProgressChanged;
	}
	public function setOrganisation(org:Organisation){
		_org = org;
		
		setChanged();
		
		//send an update
		infoObj = {};
		infoObj.updateType = "ORGANISATION_UPDATED";
		notifyObservers(infoObj);
	}
	
	public function getOrganisation():Organisation{
		return _org;
	}
	
	public function saveOrgs(orgs:Array){
		_orgs = orgs;
	}
	
	public function getOrgs():Array{
		return _orgs;
	}
	
	public function setToDos(todos:Array){
		_todos = new Array();
		
		for(var i=0; i< todos.length; i++){
			var t:Object = todos[i];
			var todo:ContributeActivity = new ContributeActivity();
			todo.populateFromDTO(t, _activeSeq.contentFolderID);
			_todos.push(todo);
		}
		
		setChanged();
		
		//send an update
		infoObj = {};
		infoObj.updateType = "TODOS";
		notifyObservers(infoObj);
	}
	
	public function getToDos():Array{
		return _todos;
	}
	
	public function showLearners(){
		_showLearners = true;
		
		setChanged();
		
		//send an update
		infoObj = {};
		infoObj.updateType = "SHOW_LEARNERS";
		notifyObservers(infoObj);
	}
	
	public function hideLearners(){
		_showLearners = false;
		
		setChanged();
		
		//send an update
		infoObj = {};
		infoObj.updateType = "HIDE_LEARNERS";
		notifyObservers(infoObj);
	}
	
	public function isShowLearners():Boolean{
		return _showLearners;
	}


	public function activitiesOnCanvas():Array{
		var actAll:Array = new Array();
		var k:Array = _activitiesDisplayed.values();
		for (var i=0; i<k.length; i++){
			actAll.push(k[i]);		
		}
		
		return actAll;
	}
	/**
	 * Compares the design in the CanvasModel (what is displayed on the screen) 
	 * against the design in the DesignDataModel and updates the Canvas Model accordingly.
	 * NOTE: Design elements are added to the DDM here, but removed in the View
	 * 
	 * @usage   
	 * @return  
	 */
	public function clearDesign(tabID:Number, learner:Object){
		
		if (learner != null || learner != undefined){
			var drawLearner:Object = new Object();
			drawLearner = learner;
		}
	
		//porobbably need to get a bit more granular
		Debugger.log('Running',Debugger.GEN,'refreshDesign','MonitorModel');
		var mmActivity_keys:Array = _activitiesDisplayed.keys();
		var longest = mmActivity_keys.length;
		
		//set index array with activity keyys length
		var indexArray:Array;
		indexArray = mmActivity_keys;

		//loop through and remove activities
		for(var i=0;i<longest;i++){
			var keyToCheck:Number = indexArray[i];
			var mm_activity:Activity = _activitiesDisplayed.get(keyToCheck).activity;			
			broadcastViewUpdate("REMOVE_ACTIVITY",mm_activity, getSelectedTab());
		}
		
		/*--------For Clearing Transitions---------*/
		
		var mmTransition_keys:Array = _transitionsDisplayed.keys();
		var transLongest = mmTransition_keys.length;
		//chose which array we are going to loop over
		var transIndexArray:Array;
		transIndexArray = mmTransition_keys;
		
		//loop through and remove transitions
		for(var i=0;i<transLongest;i++){
			var transkeyToCheck:Number = transIndexArray[i];
			var mm_transition:Transition = _transitionsDisplayed.get(transkeyToCheck).transition;
			broadcastViewUpdate("REMOVE_TRANSITION",mm_transition, getSelectedTab());
		}

	}
	
	public function getlearnerTabActArr():Array{
		return learnerTabActArr;
	}
	
	
	private function orderDesign(activity:Activity, order:Array):Void{
		order.push(activity);
		
		for(var i=0;i<ddmTransition_keys.length;i++){
			var transitionKeyToCheck:Number = ddmTransition_keys[i];
			var ddmTransition:Transition = _activeSeq.getLearningDesignModel().transitions.get(transitionKeyToCheck);
			
				if (ddmTransition.fromUIID == activity.activityUIID){
					var ddm_activity:Activity = _activeSeq.getLearningDesignModel().activities.get(ddmTransition.toUIID);
					orderDesign(ddm_activity, order);
				}
				
		}
		
	}
	
	public function setDesignOrder():Array{
		ddmActivity_keys = _activeSeq.getLearningDesignModel().activities.keys();
		ddmTransition_keys = _activeSeq.getLearningDesignModel().transitions.keys();
		
		var orderedActivityArr:Array = new Array();
		var trIndexArray:Array;
		var dataObj:Object;
		var ddmfirstActivity_key:Number = _activeSeq.getLearningDesignModel().firstActivityID;
		var learnerFirstActivity:Activity = _activeSeq.getLearningDesignModel().activities.get(ddmfirstActivity_key);
		
		// recursive method to order design
		orderDesign(learnerFirstActivity, orderedActivityArr);
		
		return orderedActivityArr;
		
	}
	
	/**
	 * get the design in the DesignDataModel and update the Monitor Model accordingly.
	 * NOTE: Design elements are added to the DDM here.
	 * 
	 * @usage   
	 * @return  
	 */
	public function drawDesign(tabID:Number, learner:Object){
		var indexArray:Array = setDesignOrder();
		
		if (learner != null || learner != undefined){
			var drawLearner:Object = new Object();
			drawLearner = learner;
		}
		
		//go through the design and get the activities and transitions 
		var dataObj:Object;
		ddmActivity_keys = _activeSeq.getLearningDesignModel().activities.keys();
		
		//loop through 
		for(var i=0;i<indexArray.length;i++){
			var keyToCheck:Number = indexArray[i].activityUIID;
			var ddm_activity:Activity = _activeSeq.getLearningDesignModel().activities.get(keyToCheck);
			
			if(!(ddm_activity.parentActivityID > 0 || ddm_activity.parentUIID > 0) && !isDesignDrawn){
				broadcastViewUpdate("DRAW_ACTIVITY", ddm_activity, tabID, drawLearner);
			} else {
				broadcastViewUpdate("CLONE_ACTIVITY", ddm_activity, tabID, drawLearner);
			}
		}
		
		//now check the transitions:
		ddmTransition_keys = _activeSeq.getLearningDesignModel().transitions.keys();
				
		//chose which array we are going to loop over
		var trIndexArray:Array;
		trIndexArray = ddmTransition_keys;
		
		//loop through 
		for(var i=0;i<trIndexArray.length;i++){
			var transitionKeyToCheck:Number = trIndexArray[i];
			var ddmTransition:Transition = _activeSeq.getLearningDesignModel().transitions.get(transitionKeyToCheck);
			broadcastViewUpdate("DRAW_TRANSITION", ddmTransition, tabID);
		}
		
		isDesignDrawn = true;
	}
	
	public function setDialogOpen(dialogOpen:String){
		_dialogOpen = dialogOpen;
		broadcastViewUpdate(_dialogOpen, null, null);
	}
	
	public function broadcastViewUpdate(updateType, data, tabID, learner){
		setChanged();
		
		//send an update
		infoObj = {};
		infoObj.updateType = updateType;
		infoObj.data = data;
		infoObj.tabID = tabID;
		infoObj.learner = learner
		notifyObservers(infoObj);
		
	}
	
	public function changeTab(tabID:Number){
		selectedTab = tabID;
		setChanged();
		
		//send an update
		infoObj = {};
		infoObj.updateType = "TABCHANGE";
		infoObj.tabID = tabID;
		notifyObservers(infoObj);
		
	}
	
	public function refreshAllData(learningDesignDTO:Object){
		var ddm:DesignDataModel = new DesignDataModel();
		ddm.setDesign(learningDesignDTO);
		if(!app.layout.manager.checkAvailabilityOnDDM(ddm).locked) {
		
			selectedTab = getSelectedTab();
			setChanged();
			
			//send an update
			infoObj = {};
			infoObj.updateType = "RELOADPROGRESS";
			infoObj.tabID = selectedTab;
			notifyObservers(infoObj);
		} else {
			ApplicationParent.extCall("reloadWindow", null);
		}
		
	}
	
	public function tabHelp(){
		var callback:Function = Proxy.create(this, openTabHelp);
		app.getHelpURL(callback)
	}
	
	private function selectedTabName():String{
		selectedTab = getSelectedTab();
		var tabName:String
		switch (String(selectedTab)){
			case '0' :
				tabName = "lesson"
                break;
            case '1' :
			    tabName = "sequence"
                break;
			case '2' :
				tabName = "learners"
				break;
            default :
		}
		return tabName;
		
		
	}
	
	private function openTabHelp(url:String){
		var tabName:String = selectedTabName();
		var locale:String = _root.lang + _root.country;
		var target:String = app.module +tabName+ '#' + app.module +tabName+ '-' + locale;
		
		ApplicationParent.extCall("openURL", url + target);
	}
	/**
	* Periodically checks if users have been loaded
	*/
	private function checkUsersLoaded() {
		// first time through set interval for method polling
		if(!_UserLoadCheckIntervalID) {
			_UserLoadCheckIntervalID = setInterval(Proxy.create(this, checkUsersLoaded), USER_LOAD_CHECK_INTERVAL);
		} else {
			_userLoadCheckCount++;
			// if dictionary and theme data loaded setup UI
			if(_staffLoaded && _learnersLoaded) {
				clearInterval(_UserLoadCheckIntervalID);
				
				trace('ALL USERS LOADED -CONTINUE');
				// populate learner/staff scrollpanes
				broadcastViewUpdate("USERS_LOADED", null, null);
				
				
			} else if(_userLoadCheckCount >= USER_LOAD_CHECK_TIMEOUT_COUNT) {
				Debugger.log('reached timeout waiting for data to load.',Debugger.CRITICAL,'checkUsersLoaded','MonitorModel');
				clearInterval(_UserLoadCheckIntervalID);		
			}
		}
	}
	
	private function resetUserFlags():Void{
		staffLoaded = false;
		learnersLoaded = false;
		_userLoadCheckCount = 0;
		_UserLoadCheckIntervalID = null;
	}
	
	public function requestLearners(data:Object, callback:Function){
		
		_monitor.requestUsers(LEARNER_ROLE, data.organisationID, callback);
	}

	
	public function requestStaff(data:Object, callback:Function){
		
		_monitor.requestUsers(MONITOR_ROLE, data.organisationID, callback);
	}
	
	public function saveLearners(users:Array){
		saveUsers(users, LEARNER_ROLE);
		
		broadcastViewUpdate("LEARNERS_LOADED", null, null);
	}
	
	public function saveStaff(users:Array){
		saveUsers(users, MONITOR_ROLE);
		
		broadcastViewUpdate("STAFF_LOADED", null, null);
	}


	private function saveUsers(users:Array, role:String):Void{
		
		for(var i=0; i< users.length; i++){
			var u:Object = users[i];
			
			var user:User = User(organisation.getUser(u.userID));
			if(user != null){
				trace('adding role to existing user: ' + user.getFirstName() + ' ' + user.getLastName());
				user.addRole(role);
			} else {
				user = new User();
				user.populateFromDTO(u);
				user.addRole(role);
				
				trace('adding user: ' + user.getFirstName() + ' ' + user.getLastName() + ' ' + user.getUserId());
				organisation.addUser(user);
			}
		}
	}
	
	public function getLessonClassData():Object{
		var classData:Object = new Object();
		var r:Object = resultDTO;
		var staff:Object = new Object();
		var learners:Object = new Object();
		if(r){
			trace('getting lesson class data...');
			trace('org resource id: ' + r.organisationID);
			if(_root.lessonID){classData.lessonID = _root.lessonID;}
			if(r.organisationID){classData.organisationID = r.organisationID;}
			classData.staff = staff;
			classData.learners = learners;
			if(r.staffGroupName){classData.staff.groupName = r.staffGroupName;}
			if(r.selectedStaff){staff.users = r.selectedStaff;}
			if(r.learnersGroupName){classData.learners.groupName = r.learnersGroupName;}
			if(r.selectedLearners){classData.learners.users = r.selectedLearners;}
			return classData;
		} else {
			return null;
		}
	}


	///////////////////////       OPEN ACTIVITY                /////////////////////////////
	/**
	 * Called on double clicking an activity
	 * @usage   
	 * @return  
	 */
	public function openToolActivityContent(ca:Activity):Void{
		Debugger.log('ta:'+ca.title+'toolContentID:'+ca.activityUIID,Debugger.GEN,'openToolActivityContent','MonitorModel');

		//if we have a good toolID lets open it
		var cfg = Config.getInstance();
		var URLToSend:String = cfg.serverUrl+_root.monitoringURL+'getLearnerActivityURL&activityID='+ca.activityID+'&userID='+_root.userID+'&lessonID='+_root.lessonID;
	
		Debugger.log('Opening url:'+URLToSend,Debugger.GEN,'openToolActivityContent','CanvasModel');
		getURL(URLToSend,"_blank");		
	}
	

	public function setDirty(){
		_isDirty = true;
		clearDesign();
	}

	public function setSize(width:Number,height:Number) {
		__width = width;
		__height = height;
		
		setChanged();
		
		//send an update
		infoObj = {};
		infoObj.updateType = "SIZE";
		notifyObservers(infoObj);
    }
	
	/**
	* Used by View to get the size
	* @returns Object containing width(w) & height(h).  obj.w & obj.h
	*/
	public function getSize():Object{
		var s:Object = {};
		s.w = __width;
		s.h = __height;
		return s;
	}  
	
	/**
    * sets the model x + y vars
	*/
	public function setPosition(x:Number,y:Number):Void{
        //Set state variables
		__x = x;
		__y = y;
        //Set flag for notify observers
		setChanged();
        
		//build and send update object
		infoObj = {};
		infoObj.updateType = "POSITION";
		notifyObservers(infoObj);
	}  

	/**
	* Used by View to get the size
	* @returns Object containing width(w) & height(h).  obj.w & obj.h
	*/
	public function getPosition():Object{
		var p:Object = {};
		p.x = __x;
		p.y = __y;
		return p;
	}  
	
	/**
	 * Sets up the tree for the 1st time
	 * 
	 * @usage   
	 * @return  
	 */
	public function initOrganisationTree(){
		_treeDP = new XML();
		_orgResources = new Array();
	}
	
	/**
	 * 
	 * @usage   
	 * @param   neworgResources 
	 * @return  
	 */
	public function setOrganisationResource(key:String,neworgResources:XMLNode):Void {
		Debugger.log(key+'='+neworgResources,Debugger.GEN,'setOrganisationResource','org.lamsfoundation.lams.monitoring.mv.MonitorModel');
		_orgResources[key] = neworgResources;
	}
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function getOrganisationResource(key:String):XMLNode{
		Debugger.log(key+' is returning '+_orgResources[key],Debugger.GEN,'getOrganisationResource','org.lamsfoundation.lams.monitoring.mv.MonitorModel');
		return _orgResources[key];
		
	}

	
	public function get treeDP():XML{
		return _treeDP;
	}

	public function get organisation():Organisation{
		return _org;
	}
	
	/**
	 * 
	 * @usage   
	 * @param   newselectedTreeNode 
	 * @return  
	 
	public function setSelectedTreeNode (newselectedTreeNode:XMLNode):Void {
		_selectedTreeNode = newselectedTreeNode;
		trace('branch: ' + _selectedTreeNode.attributes.isBranch);
		if(!_selectedTreeNode.attributes.isBranch){
			// get the organisations (node) users by role
			//var roles:Array = new Array(LEARNER_ROLE, MONITOR_ROLE, TEACHER_ROLE);
			setOrganisation(new Organisation(_selectedTreeNode.attributes.data));
			resetUserFlags();
			// polling method - waiting for all users to load before displaying users in UI
			checkUsersLoaded();
			
			// load users
			requestLearners(_selectedTreeNode.attributes.data);
			requestStaff(_selectedTreeNode.attributes.data);
			
			trace(staffLoaded);
			trace(learnersLoaded);
		}
		
		//dispatch an update to the view
		//broadcastViewUpdate('ITEM_SELECTED',_selectedTreeNode);
	}
	*/

	/**
	 * 
	 * @usage   
	 * @return 
 
	 
	public function getSelectedTreeNode ():XMLNode {
		return _selectedTreeNode;
	}
	*/
	
	private function setSelectedItem(newselectItem:Object){
		prevSelectedItem = _selectedItem;
		_selectedItem = newselectItem;
		broadcastViewUpdate("SELECTED_ITEM");
	}
	
	/**
	 * 
	 * @usage   
	 * @param   newselectItem 
	 * @return  
	 */
	public function set selectedItem (newselectItem:Object):Void {
		setSelectedItem(newselectItem);
	}
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function get selectedItem ():Object {
		return _selectedItem;
	}
	
	public function setSelectedTab(tabID:Number){
		selectedTab = tabID;
	}
	public function getSelectedTab():Number{
		return selectedTab;
	}
	
	public function set prevSelectedItem (oldselectItem:Object):Void {
		_prevSelectedItem = oldselectItem;
	}
	
	public function get prevSelectedItem():Object {
		return _prevSelectedItem;
	}
	
	public function get learnersLoaded():Boolean{
		return _learnersLoaded;
	}
	
	public function set learnersLoaded(a:Boolean):Void{
		_learnersLoaded = a;
	}
	
	public function get staffLoaded():Boolean{
		return _staffLoaded;
	}
	
	public function set staffLoaded(a:Boolean):Void{
		_staffLoaded = a;
	}
	
	//Accessors for x + y coordinates
    public function get x():Number{
        return __x;
    }
    
    public function get y():Number{
        return __y;
    }

    //Accessors for x + y coordinates
    public function get width():Number{
        return __width;
    }
    
    public function get height():Number{
        return __height;
    }
	
	public function get className():String{
        return 'MonitorModel';
    }

	public function get resultDTO():Object{
		return _resultDTO;
	}
	
	public function set resultDTO(a:Object){
		_resultDTO = a;
	}
	
	public function set endGate(a:MovieClip){
		_endGate = a;
	}
	
	public function get endGate():MovieClip{
		return _endGate;
	}

	public function set locked(a:Boolean){
		_isLocked = a;
	}
	
	public function get locked():Boolean{
		return _isLocked;
	}
	
	public function set isDesignDrawn(a:Boolean){
		_isDesignDrawn = a;
	}
	
	public function get isDesignDrawn():Boolean{
		return _isDesignDrawn;
	}

	/**
	 * Returns a reference to the Activity Movieclip for the UIID passed in.  Gets from _activitiesDisplayed Hashable
	 * @usage   
	 * @param   UIID 
	 * @return  Activity Movie clip
	 */
	public function getActivityMCByUIID(UIID:Number):MovieClip{
		
		var a_mc:MovieClip = _activitiesDisplayed.get(UIID);
		//Debugger.log('UIID:'+UIID+'='+a_mc,Debugger.GEN,'getActivityMCByUIID','CanvasModel');
		return a_mc;
	}
	
	public function get activitiesDisplayed():Hashtable{
		return _activitiesDisplayed;
	}
	
	public function get transitionsDisplayed():Hashtable{
		return _transitionsDisplayed;
	}	
	
	public function get allLearnersProgress():Array{
		return learnerTabActArr;
	}
	
	public function getActivityKeys():Array{
		trace("ddmActivity_keys length: "+ ddmActivity_keys.length)
		return ddmActivity_keys;
	}
	
	public function getTransitionKeys():Array{
		return ddmTransition_keys;
	}

	public function getMonitor():Monitor{
		return _monitor;
	}
	
	public function getTTData():Object{
		var myObj:Object = new Object();
		myObj.monitorX = monitor_x;
		myObj.monitorY = monitor_y;
		myObj.ttHolderMC = ttHolder;
		
		return myObj;
	}
	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function get isDragging ():Boolean {
		return _isDragging;
	}
	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function set isDragging (newisDragging:Boolean):Void{
		_isDragging = newisDragging;
	}
	
	
}
