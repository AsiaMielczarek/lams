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

import org.lamsfoundation.lams.common.*;

import org.lamsfoundation.lams.common.util.Observable;
import org.lamsfoundation.lams.common.util.*;
import org.lamsfoundation.lams.authoring.DesignDataModel;


/*
* Model for the Sequence
*/
class Sequence {
	private var _className:String = "Sequence";
	
	private static var _instance:Sequence = null;
	
	public static var ACTIVE_STATE_ID:Number = 1;
	public static var NOTSTARTED_STATE_ID:Number = 2;
	public static var STARTED_STATE_ID:Number = 3;
	public static var SUSPENDED_STATE_ID:Number = 4;
	public static var FINISHED_STATE_ID:Number = 5;
	public static var ARCHIVED_STATE_ID:Number = 6;
	public static var REMOVED_STATE_ID:Number = 7;
	
	/**
	* View state data
	*/
	// sequence data
	private var _seqName:String;
	private var _seqDescription:String;
	private var _seqStateID:Number;
	private var _seqID:Number;
	
	// sequence dates
	private var _seqCreatedDate:Date;
	private var _seqStartDate:Date;
	private var _seqStartDateStr:String;
	private var _seqScheduleStartDate:Date;
	private var _seqScheduleStartDateStr:String;
	
	// organisation data
	private var _organisationID:Number;
	private var _organisationName:String;
	private var _organisationDescription:String;
	
	// LD and workspace data
	private var _learningDesignID:Number;
	private var _workspaceFolderID:Number;
	private var _contentFolderID:Number;
	
	// statistics data
	private var _noPossibleLearners:Number;
	private var _noStartedLearners:Number;
	private var _duration:Number;
	
	private var _licenseID:Number;
	private var _licenseText:String;
	
	private var _learningDesignModel:DesignDataModel;
	private var _progress:Progress;
	private var _active:Boolean;
	private var _learnerExportAvailable:Boolean;	
	private var _locked_for_edit:Boolean;
	
	//Defined so compiler can 'see' events added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
	
	/**
	* Constructor.
	*/
	
	public function Sequence(dto:Object){
		_active = false;
		_learningDesignModel = null;
		_progress = null;
		if(dto != null){
			populateFromDTO(dto);
		}
		
		mx.events.EventDispatcher.initialize(this);
	}
	
	/**
	* event broadcast when a new language is loaded 
	*/ 
	public function broadcastLoad(){
		dispatchEvent({type:'load',target:this});		
	}
	
	/**
	 * 
	 * @return the Sequence 
	 */
	public static function getInstance():Sequence{
		if(Sequence._instance == null){
            Sequence._instance = new Sequence();
        }
        return Sequence._instance;
	}
	
	public function populateFromDTO(dto:Object){
		trace('populating seq object for start date:' + dto.scheduleStartDate);
		Debugger.log('populating seq schedule date:'+dto.scheduleStartDate,Debugger.CRITICAL,'populateFromDTO','Sequence');
		Debugger.log('populating seq start date:'+dto.startDateTime,Debugger.CRITICAL,'populateFromDTO','Sequence');
		Debugger.log('populating seq locked for eidt:'+dto.lockedForEdit,Debugger.CRITICAL,'populateFromDTO','Sequence');
		
		_seqID = dto.lessonID;
		_seqName = dto.lessonName;
		_seqDescription = dto.lessonDescription;
		_seqStateID = dto.lessonStateID;
		
		_learningDesignID = dto.learningDesignID;
		
		_seqCreatedDate = dto.createDateTime;
		_seqStartDate = dto.startDateTime;
		_seqStartDateStr = dto.startDateTimeStr;
		_seqScheduleStartDate = dto.scheduleStartDate;
		_seqScheduleStartDateStr = dto.scheduleStartDateStr;
		
		_organisationID = dto.organisationID;
		_organisationName = dto.organisationName;
		_organisationDescription = dto.organisationDescription;
		
		_workspaceFolderID = dto.workspaceFolderID;
		_contentFolderID = dto.contentFolderID;
		
		_noPossibleLearners = dto.numberPossibleLearners;
		_noStartedLearners = dto.numberStartedLearners;
		_duration = dto.duration;
		
		_licenseID = dto.licenseID;
		_licenseText = dto.licenseText;
		
		_learnerExportAvailable = dto.learnerExportAvailable;
		
		_locked_for_edit = dto.lockedForEdit;
	}
	
	
	/**
	 * Set seq's unique ID
	 * 
	 * @param   seqID 
	 */
	
	public function setSequenceID(seqID:Number){
		_seqID = seqID;
	}
	
	/**
	 * Get Sequence's unique ID
	 *   
	 * @return  Sequence ID
	 */
	
	public function getSequenceID():Number {
		return _seqID;
	}
	
	public function get ID():Number{
		return _seqID;
	}

    /**
	 * Set User Organisation ID
	 * 
	 * @param   organisationIDID 
	 */
	
	public function setOrganisationID(organisationID:Number){
		_organisationID = organisationID;
	}
	
	/**
	 * Get User Organisation ID
	 *   
	 * @return  Organisation ID
	 */
	
	public function getOrganisationID():Number {
		return _organisationID;
	}

	/**
	 * Set the seq's name
	 * 
	 * @param   seqName 
	 */
	
	public function setSequenceName(seqName:String){
		_seqName = seqName;
		
	}
	
	/**
	 * Get the seq's name
	 * 
	 * @return Sequence Name
	 */
	
	public function getSequenceName():String {
		return _seqName;
	}
	
	public function get name():String{
		return _seqName;
	}
	
	/**
	 * Set the seq's description
	 *
	 * @param   seqDescription  
	 */
	
	public function setSequenceDescription(seqDescription:String){
		_seqDescription = seqDescription;
		
	}
	
	/**
	 * Get the seq's description
	 * 
	 * @return  seq description
	 */
	public function getSequenceDescription():String {
		return _seqDescription;
	}
	
	public function get description():String{
		return _seqDescription;
	}
	
	public function setSequenceStateID(seqStateID:Number) {
		_seqStateID = seqStateID;
		
	}
	
	public function getSequenceStateID():Number {
		return _seqStateID;
	}
	
	public function get state():Number{
		return _seqStateID;
	}
	
	public function setLearningDesignID(learningDesignID:Number){
		_learningDesignID = learningDesignID;
		
	}
	
	public function get learningDesignID():Number{
		return _learningDesignID;
	}
	
	public function setLearningDesignModel(learningDesignModel:DesignDataModel){
		_learningDesignModel = learningDesignModel;
		
        broadcastLoad();
	}
	
	public function getLearningDesignModel():DesignDataModel{
		return _learningDesignModel;
	}
	
	public function setCreatedDateTime(seqCreatedDate:Date){
		_seqCreatedDate = seqCreatedDate;
	}
	
	public function getCreatedDateTime():Date{
		return _seqCreatedDate;
	}
	
	public function get createddate():Date{
		return _seqCreatedDate;
	}
	
	
	
	public function setStartDateTime(seqStartDate:Date){
		_seqStartDate = seqStartDate;
	}
	
	public function getStartDateTime():String{
		/**var dateFormated:String
		if (_seqStartDate.getDate() == undefined || _seqStartDate.getDate() == null){
			//dateFormated = "Not Started"
			return null;
		}else{
			var dateFormated:String = (_seqStartDate.getDate()+" "+(StringUtils.getMonthAsString(_seqStartDate.getMonth()))+" "+_seqStartDate.getFullYear());;
		}
		
		return dateFormated;
		*/
		return this.startdatestr;
	}
	
	public function setStartDateTimeStr(seqStartDateStr:String){
		_seqStartDateStr = seqStartDateStr;
	}
	
	public function get startdate():Date{
		return _seqStartDate;
	}
	
	public function get startdatestr():String {
		return _seqStartDateStr;
	}
	
	public function get isStarted():Boolean{
		if (_seqStartDate.getDate() == undefined || _seqStartDate.getDate() == null || _seqStartDate.getFullYear() == 1970){
			return false;
		}
		
		return true;
	}
	
	public function setScheduleDateTime(seqScheduleDate:Date){
		_seqScheduleStartDate = seqScheduleDate;
	}
	
	public function setScheduleDateTimeStr(seqScheduleDateStr:String){
		_seqScheduleStartDateStr = seqScheduleDateStr;
	}
	
	public function get scheduledatestr():String {
		return _seqScheduleStartDateStr;
	}
	
	public function getScheduleDateTime():String{
		
		
		/**var dateFormated:String;
		var timeFormated:String;
		if (_seqScheduleStartDate.getDate() == undefined || _seqScheduleStartDate.getDate() == null){
			//dateFormated = "Not Scheduled"
			return null;
		}else{
			var hours = _seqScheduleStartDate.getHours();
			var mins = _seqScheduleStartDate.getMinutes();
			var mins_str;
			if(mins < 10){
				mins_str = '0' + mins.toString();
			} else {
				mins_str = mins.toString();
			}
			
			timeFormated = hours + ":" + mins_str;
			dateFormated = (_seqScheduleStartDate.getDate()+" "+(StringUtils.getMonthAsString(_seqScheduleStartDate.getMonth()))+" "+_seqScheduleStartDate.getFullYear());;
		}
		return timeFormated + " " + dateFormated;
		
		*/
		
		return this.scheduledatestr;
	}
	
	public function get scheduledate():Date{
		return _seqScheduleStartDate;
	}
	
	public function get isScheduled():Boolean{
		if (_seqScheduleStartDate.getDate() == undefined || _seqScheduleStartDate.getDate() == null){
			return false;
		}
		
		return true;
	}
	
	public function setActive() {
		_active = true;
		trace('setting seq active...');
		
	}
	
	public function setInactive() {
		_active = false;
		trace('setting seq inactive...');
		
	}
	
	public function getStatus():Boolean {
		return _active;
	}
	
	public function setProgress(progress:Progress){
		_progress = progress;
	}
	
	public function getProgress():Progress{
		return _progress;
	}

	public function checkState(stateID:Number):Boolean {
		if(getSequenceStateID()==stateID){
			return true
		} else {
			return false;
		}
	}
	
	public function isFinished():Boolean {
		return checkState(FINISHED_STATE_ID);
	}
	
	public function get organisationID():Number{
		return _organisationID;
	}
	
	public function get organisationName():String{
		return _organisationName;
	}
	
	public function get organisationDescription():String{
		return _organisationDescription;
	}
	
	public function get workspaceFolderID():Number{
		return _workspaceFolderID;
	}
	
	public function get contentFolderID():Number{
		return _contentFolderID;
	}
	
	public function get duration():Number{
		return _duration;
	}
	
	public function get noPossibleLearners():Number{
		return _noPossibleLearners;
	}
	
	public function get noStartedLearners():Number{
		return _noStartedLearners;
	}
	
	public function get licenseID():Number{
		return _licenseID;
	}
	
	public function get licenseText():String{
		return _licenseText;
	}
	
	public function set learnerExportAvailable(b:Boolean) {
		_learnerExportAvailable = b;
	}
	
	public function get learnerExportAvailable():Boolean {
		return _learnerExportAvailable;
	}
	
	public function set locked_for_edit(b:Boolean) {
		_locked_for_edit = b;
	}
	
	public function get locked_for_edit():Boolean {
		return _locked_for_edit;
	}
	
	function get className():String{
        return 'Sequence';
    }

}