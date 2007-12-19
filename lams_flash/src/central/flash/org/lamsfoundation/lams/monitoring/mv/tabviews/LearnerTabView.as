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

import org.lamsfoundation.lams.common.util.*;
import org.lamsfoundation.lams.common.ui.*;
import org.lamsfoundation.lams.common.style.*;
import org.lamsfoundation.lams.monitoring.mv.*;
import org.lamsfoundation.lams.monitoring.mv.tabviews.*;
import org.lamsfoundation.lams.monitoring.*;
import org.lamsfoundation.lams.common.dict.*;
import org.lamsfoundation.lams.common.mvc.*;
import org.lamsfoundation.lams.common.ApplicationParent;
import org.lamsfoundation.lams.authoring.Activity;
import org.lamsfoundation.lams.authoring.ComplexActivity;
import org.lamsfoundation.lams.authoring.cv.CanvasActivity;
import org.lamsfoundation.lams.common.ToolTip;
import org.lamsfoundation.lams.authoring.Transition;

import mx.managers.*;
import mx.containers.*;
import mx.events.*;
import mx.utils.*;
import mx.controls.*;


/**
*Monitoring Tab view for the Monitor
* Reflects changes in the MonitorModel
*/

class org.lamsfoundation.lams.monitoring.mv.tabviews.LearnerTabView extends AbstractView {
	
	public static var _tabID:Number = 2;
	private var _className = "LearnerTabView";
	
	//constants:
	private var GRID_HEIGHT:Number;
	private var GRID_WIDTH:Number;
	private var H_GAP:Number;
	private var V_GAP:Number;
	private var ACT_X:Number = 0;
	private var ACT_Y:Number = 40;
	private var xOffSet:Number = 10;
	private var actWidth:Number = 120;
	private var activeLearner:Number;
	private var prevLearner:Number;
	private var learnersDrawn:Number;
	private var learnerListArr:Array;

	private var _tm:ThemeManager;
	private var _tip:ToolTip;
	
	private var mm:MonitorModel;
	private var _learnerTabView:LearnerTabView;
	private var _learnerTabViewContainer_mc:MovieClip;
	private var learnerMenuBar:MovieClip;
	
	private var _activityLayer_mc:MovieClip;
	private var _activityLayer_mc_clones:Array;
	private var _nameLayer_mc:MovieClip;
	
	private var bkg_pnl:MovieClip;
	private var completed_mc:MovieClip;
	
	private var refresh_btn:Button;
	private var help_btn:Button;
	private var panelLowered:Boolean;
	
	private var drawCount:Number;
	private var maxCount:Number;
	private var evtArr:Array;
	
	private var currentLearnerIndex:Number;
	private var maxLearnerIndex:Number;
	private var learnersDrawnIndex:Number;
	
	private var hAdjustment:Boolean;
	
    //Defined so compiler can 'see' events added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;

	/**
	* Constructor
	*/
	function LearnerTabView(){
		_learnerTabView = this;
		_learnerTabViewContainer_mc = this;
		_tm = ThemeManager.getInstance();
		_tip = new ToolTip();
		
        //Init for event delegation
        mx.events.EventDispatcher.initialize(this);
	}

	/**
	* Called to initialise Canvas. Called by the Canvas container
	*/
	public function init(m:Observable,c:Controller){
		//Invoke superconstructor, which sets up MVC relationships.
		super (m, c);
		mm = MonitorModel(model)
		
		learnerListArr = new Array();
		panelLowered = false;
		hAdjustment = false;

        //Set up parameters for the grid
		H_GAP = 10;
		V_GAP = 10;
		
		MovieClipUtils.doLater(Proxy.create(this,draw)); 
		mm.getMonitor().getMV().getMonitorLearnerScp()._visible = false;
    }
	
	/**
	 * Recieved update events from the CanvasModel. Dispatches to relevent handler depending on update.Type
	 * @usage   
	 * @param   event
	 */
	public function update (o:Observable,infoObj:Object):Void{
			Debugger.log("update has been called", Debugger.CRITICAL, "update", "LearnerTabView");
			var mm:MonitorModel = MonitorModel(o);
		   
			switch (infoObj.updateType){
				case 'POSITION' :
					setPosition(mm);
					break;
				case 'SIZE' :
					setSize(mm);
					break;
				case 'TABCHANGE' :
					if (infoObj.tabID == _tabID && !mm.locked){
						hideMainExp(mm);
						mm.broadcastViewUpdate("JOURNALSSHOWHIDE", true);
						adjustLearnerPanel(mm);
						
						if (mm.activitiesDisplayed.isEmpty()){
							mm.getMonitor().openLearningDesign(mm.getSequence());
						} else if(mm.getIsProgressChangedLearner()) {
							reloadProgress(mm, false);
						} else if(learnersDrawn != mm.allLearnersProgress.length){ //could be error here with learnersDrawn
							reloadProgress(mm, true);
						}
						
						mm.getMonitor().getMV().getMonitorLearnerScp()._visible = true;
						LFMenuBar.getInstance().setDefaults();
						
					} else {
						mm.getMonitor().getMV().getMonitorLearnerScp()._visible = false;
					}
					break;
				case 'PROGRESS' :
					Debugger.log("Progress event received", Debugger.CRITICAL, "update", "LearnerTabView");
					if (infoObj.tabID == _tabID){
						if(!mm.locked){
							mm.getMonitor().getProgressData(mm.getSequence());
						} else {
							ApplicationParent.extCall("reloadWindow", null);
						}
					}
					break;
				case 'RELOADPROGRESS' :	
					Debugger.log("Reload Progress event received", Debugger.CRITICAL, "update", "LearnerTabView");
					if (infoObj.tabID == _tabID && !mm.locked){
						reloadProgress(mm, true);
					}
					break;	
				case 'DRAW_ACTIVITY' :
					if (infoObj.tabID == _tabID && !mm.locked){
						drawActivity(infoObj.data, mm, infoObj.learner);
					}
					break;
				case 'CLONE_ACTIVITY' :
					if (infoObj.tabID == _tabID && !mm.locked){
						cloneActivity(infoObj.data, mm, infoObj.learner);
					}
					break;
				case 'REMOVE_ACTIVITY' :
					if (infoObj.tabID == _tabID && !mm.locked){
						removeActivity(infoObj.data, mm);
					}
					break;
				case 'DRAW_DESIGN' :
					Debugger.log("DRAW_DESIGN received",Debugger.CRITICAL,"update","LearnerTabView");
					adjustLearnerPanel(mm);
					if (infoObj.tabID == _tabID && !mm.locked){
						if (mm.isDesignDrawn) {
							clearCanvas(mm);
						}
						drawAllLearnersDesign(mm, infoObj.tabID);
					}
					break;
				case 'DRAW_ALL' :
					adjustLearnerPanel(mm);
					if (infoObj.tabID == _tabID && !mm.locked){
						evtArr = infoObj.data;
						drawAll();
					}
					setSize(mm);
					break;
				default :
					Debugger.log('unknown update type :' + infoObj.updateType,Debugger.CRITICAL,'update','org.lamsfoundation.lams.LearnerTabView');
			}
	}
	
	private function drawAll(){
		drawCount = 0;
		
		maxCount = evtArr.length;
		
		Debugger.log("drawing all activities: " + maxCount, Debugger.CRITICAL, "drawAll", "LearnerTabView");
		
		drawNext();
	}
	
	public function drawNext():Void {
		if(drawCount < maxCount) {
			Debugger.log("drawing: " + evtArr[drawCount].updateType, Debugger.CRITICAL, "drawNext", "LearnerTabView");
			update(mm, evtArr[drawCount])
		} else {
			drawNextLearner();
			return;
		}
		
		drawCount++;
	}
	
	/**
    * layout visual elements on the MonitorTabView on initialisation
    */
	private function draw(){
		//set up the Movie Clips to load relevant  
		
		this._nameLayer_mc = this.createEmptyMovieClip("_nameLayer_mc", this.getNextHighestDepth(),{_y:learnerMenuBar._height});
		this._activityLayer_mc = this.createEmptyMovieClip("_activityLayer_mc", this.getNextHighestDepth(),{_y:learnerMenuBar._height});
		
		setStyles();
		
		dispatchEvent({type:'load',target:this});
	}
	
	private function hideMainExp(mm:MonitorModel):Void{
		mm.broadcastViewUpdate("EXPORTSHOWHIDE", false);
		mm.broadcastViewUpdate("EDITFLYSHOWHIDE", false);
	}
	
	/**
	* Sets last selected Sequence
	*/
	private function setPrevLearner(learner:Number):Void{
		prevLearner = learner;
	}
	
	/**
	* Gets last selected Sequence
	*/
	private function getPrevLearner():Number{
		return prevLearner;
	}
	
	/*
	* Clear Method to clear movies from scrollpane
	* 
	*/
	private function clearLearnersData(array:Array):Array{
		if(array != null){
			for (var i=0; i <array.length; i++){
				array[i].removeMovieClip();
			}
		}
		
		array = new Array();
		return array;
	}
	
	/**
	 * Reloads the learner Progress and 
	 * @Param isChanged Boolean Value to pass it to setIsProgressChanged in monitor model so that it sets it to true if refresh button is clicked and sets it to fasle as soon as latest data is loaded and design is redrawn.
	 * @usage   
	 * @return  nothing
	 */
	private function reloadProgress(mm:MonitorModel, isChanged:Boolean){
			clearCanvas(mm, isChanged);
			
			mm.getMonitor().getProgressData(mm.getSequence());
	}
	
	public function adjustLearnerPanel(mm:MonitorModel):Void {
		
		Debugger.log("adjustLearnerPanel called", Debugger.CRITICAL, "adjustLearnerPanel", "LearnerTabView");
		var _scroll:ScrollPane = mm.getMonitor().getMV().getMonitorLearnerScp();
		var s:Object = mm.getSize();
		
		if (mm.numIndexButtons > 1 && !panelLowered) {
			if (hAdjustment) {
				_scroll.setSize(s.w-_scroll._x, s.h - 20);
				//_scroll.setSize(s.w-_scroll._x, s.h);
				hAdjustment = false;
			}
			
			_scroll._y += 20;
			
			panelLowered = true;	
		}
		else if (mm.numIndexButtons <= 1 && !hAdjustment) {
			_scroll.setSize(s.w-_scroll._x, s.h);
			hAdjustment = true;
		}
	
	}
	
	public function clearCanvas(mm:MonitorModel, isChanged:Boolean):Void {
		Debugger.log("in clearCanvas", Debugger.CRITICAL, "clearCanvas", "LearnerTabView");
		learnersDrawn = 0;
		learnersDrawnIndex = 0;
		
		ACT_X = 0;
		ACT_Y = 35;
		
		if (learnerListArr == null || learnerListArr == undefined){ 
			learnerListArr = new Array();
		}
		this._activityLayer_mc.removeMovieClip();
		this._nameLayer_mc.removeMovieClip();
			
		this._nameLayer_mc = this.createEmptyMovieClip("_nameLayer_mc", this.getNextHighestDepth(),{_y:learnerMenuBar._height});
		this._activityLayer_mc = this.createEmptyMovieClip("_activityLayer_mc", this.getNextHighestDepth(),{_y:learnerMenuBar._height});
		
		if (isChanged == false){
			mm.setIsProgressChangedLearner(false);
			mm.setIsProgressChangedSequence(true);
		} else {
			mm.setIsProgressChangedLesson(true);
			mm.setIsProgressChangedSequence(true);
		}
			
		mm.transitionsDisplayed.clear();
		mm.activitiesDisplayed.clear();
		
		mm.isDesignDrawn = false;
	}
	
	private function drawAllLearnersDesign(mm:MonitorModel, tabID:Number){
		var learnersPerPage:Number = mm.learnersPerPage;
		currentLearnerIndex = (mm.currentLearnerIndex-1)*learnersPerPage;
		maxLearnerIndex = (learnersPerPage*mm.currentLearnerIndex)-1;
		learnersDrawnIndex = 0;
		
		drawNextLearner();

	}
	
	private function drawNextLearner(){
		Debugger.log("drawing next learner: " + currentLearnerIndex, Debugger.CRITICAL, "drawNextLearner", "LearnerTabView")
		
		if(currentLearnerIndex <= maxLearnerIndex) {
			Debugger.log("learner progress: " + mm.allLearnersProgress[currentLearnerIndex], Debugger.CRITICAL, "drawNextLearner", "LearnerTabView")
		
			if (mm.allLearnersProgress[currentLearnerIndex] != undefined) {
				learnersDrawn = currentLearnerIndex+1;
				
				ACT_X = 0;
				ACT_Y = (learnersDrawnIndex*80)+35;
				
				learnersDrawnIndex++;
				mm.drawDesign(_tabID, mm.allLearnersProgress[currentLearnerIndex]);
			}
		} else {
			mm.isDesignDrawn = true;
		}
		
		currentLearnerIndex++;
	}
	
	/**
	 * Remove the activityies from screen on selection of new lesson
	 * 
	 * @usage   
	 * @param   activityUIID 
	 * @return  
	 */
	private function removeActivity(a:Activity,mm:MonitorModel){
		//dispatch an event to show the design  has changed
		var r = mm.activitiesDisplayed.remove(a.activityUIID);
		r.removeMovieClip();
		var s:Boolean = (r==null) ? false : true;
		
	}
	
	private function printLearner(mm:MonitorModel, learner:Object){
		var z:Object = mm.getSize();

		var styleObj = _tm.getStyleObject('button');
		var EP_btn_label:String = Dictionary.getValue('learner_exportPortfolio_btn')
		
		var nameTextFormat = new TextFormat();
		
		var exp_url = _root.serverURL+"learning/exportWaitingPage.jsp?mode=learner&role=teacher&lessonID="+_root.lessonID+"&userID="+learner.getLearnerId();
		
		if(_nameLayer_mc["learnerName"+learner.getLearnerId()] != undefined) {
			_nameLayer_mc["learnerName"+learner.getLearnerId()+"_btn"].removeTextField();
		}
		
		_nameLayer_mc.createTextField("learnerName"+learner.getLearnerId(), _nameLayer_mc.getNextHighestDepth(), ACT_X+2, ACT_Y, z.w-22, 20);
		
		if(_nameLayer_mc["learnerName"+learner.getLearnerId()+"_btn"] != undefined) {
			_nameLayer_mc["learnerName"+learner.getLearnerId()+"_btn"].removeMovieClip();
		}
		
		_nameLayer_mc.attachMovie("Button", "learnerName"+learner.getLearnerId()+"_btn", _nameLayer_mc.getNextHighestDepth(),{label:EP_btn_label, _x:z.w-110, _y:ACT_Y+2, styleName:styleObj} )
		
		var learnerName_txt = _nameLayer_mc["learnerName"+learner.getLearnerId()];
		var learnerExp_btn = _nameLayer_mc["learnerName"+learner.getLearnerId()+"_btn"];
		
		learnerExp_btn.setSize(90, 17);
		learnerExp_btn.onRelease = function (){
			JsPopup.getInstance().launchPopupWindow(exp_url, 'ExportPortfolio', 410, 640, true, true, false, false, false);
		}
		
		learnerExp_btn.onRollOver = Proxy.create(this,this['showToolTip'], learnerExp_btn, "learner_exportPortfolio_btn_tooltip");
		learnerExp_btn.onRollOut = Proxy.create(this,this['hideToolTip']);
		
		var sLearner:mx.styles.CSSStyleDeclaration = _tm.getStyleObject("LTVLearnerText");
		
		nameTextFormat.bold = (sLearner.getStyle("fontWeight")=="bold") ? true : false;
		nameTextFormat.font = sLearner.getStyle("fontFamily");
		nameTextFormat.size = sLearner.getStyle("fontSize");
		learnerName_txt.border = (sLearner.getStyle("borderStyle") != "none") ? true : false;
		learnerName_txt.background = true;
		learnerName_txt.backgroundColor = sLearner.getStyle("backgroundColor");
		learnerName_txt.textColor = sLearner.getStyle("color");
		learnerName_txt.setNewTextFormat(nameTextFormat);
		learnerName_txt.selectable = false;
		learnerName_txt.text = "\t"+learner.getLearnerFirstName() + " "+learner.getLearnerLastName();
		
		var tempObj = new Object();
		tempObj.learnerName = learnerName_txt;
		tempObj.learnerButton = learnerExp_btn;
		learnerListArr.push(tempObj);
		Debugger.log("learnerListArr.length: "+learnerListArr.length, Debugger.GEN, "printLearner", "LearnerTabView");
	}

	public function showToolTip(btnObj, btnTT:String):Void{
		var scpWidth:Number = mm.getMonitor().getMV().getMonitorLearnerScp().width;
		var btnLabel = btnObj.label;
		var xpos:Number = scpWidth - 190;
		var Xpos = xpos;
		var Ypos = (Application.MONITOR_Y+ btnObj._y+btnObj.height)+5;
		var ttHolder = Application.tooltip;
		var ttMessage = Dictionary.getValue(btnTT);
		_tip.DisplayToolTip(ttHolder, ttMessage, Xpos, Ypos);
		
	}
	
	public function hideToolTip():Void{
		_tip.CloseToolTip();
	}
	
	/**
	 * Draws new activity to monitor tab view stage.
	 * @usage   
	 * @param   a  - Activity to be drawn
	 * @param   cm - Refernce to the model
	 * @return  Boolean - successfullit
	 */
	private function drawActivity(a:Activity, mm:MonitorModel, learner:Object):Boolean{
		
		var ltv = LearnerTabView(this);
		var mc = getController();
		var newActivity_mc:MovieClip = null;
		
		Debugger.log('The activity:'+a.title+','+a.activityTypeID+' is now be drawn',Debugger.CRITICAL,'drawActivity','LearnerTabView');
		
		if (ACT_X == 0) {
			printLearner(mm, learner);
		}
		
		//take action depending on act type
		if(a.activityTypeID==Activity.TOOL_ACTIVITY_TYPE || a.isGroupActivity()) {
			newActivity_mc = _activityLayer_mc.createChildAtDepth("LearnerActivity", _activityLayer_mc.getNextHighestDepth(),{_activity:a,_controller:mc,_view:ltv, _x:ACT_X, _y:ACT_Y+40, learner:learner});
			
			ACT_X = newActivity_mc._x + newActivity_mc._width;
			
		} else if(a.isGateActivity()){
			var actLabel:String = gateTitle(a);
			newActivity_mc = _activityLayer_mc.createChildAtDepth("LearnerGateActivity", _activityLayer_mc.getNextHighestDepth(),{_activity:a,_controller:mc,_view:ltv, _x:ACT_X, _y:ACT_Y+40, actLabel:actLabel, learner:learner});
			
			ACT_X = newActivity_mc._x + newActivity_mc._width;
			
		} else if(a.activityTypeID==Activity.PARALLEL_ACTIVITY_TYPE || a.activityTypeID==Activity.OPTIONAL_ACTIVITY_TYPE || a.activityTypeID==Activity.OPTIONS_WITH_SEQUENCES_TYPE || a.isBranchingActivity()){
			//get the children
			var children:Array = mm.getMonitor().ddm.getComplexActivityChildren(a.activityUIID);
			newActivity_mc = _activityLayer_mc.createChildAtDepth("LearnerComplexActivity", _activityLayer_mc.getNextHighestDepth(),{_activity:a,_children:children,_controller:mc,_view:ltv, _x:ACT_X, _y:ACT_Y+40, learner:learner});
			
			ACT_X = newActivity_mc._x + newActivity_mc._width;
			
		} else{
			Debugger.log('The activity:'+a.title+','+a.activityUIID+' is of unknown type, it cannot be drawn',Debugger.CRITICAL,'drawActivity','LearnerTabView');
		}
		
		var actItems:Number = mm.activitiesDisplayed.size();
		
		if (actItems < mm.getActivityKeys().length) 
			mm.activitiesDisplayed.put(a.activityUIID,newActivity_mc); 
		
		return true;
	}
	
	/**
	 * Create a clone of the Learning Design with the learner progress data applied to the cloned instance.
	 * 
	 * @usage   
	 * @param   learner 	Learner's progress data
	 * @return  
	 */
	
	private function cloneActivity(a:Activity, mm:MonitorModel, learner:Object) {
		var ltv = LearnerTabView(this);
		var mc = getController();
		
		var mc_to_clone:MovieClip = MovieClip(mm.activitiesDisplayed.get(a.activityUIID));
		var _activityLayer_mc_clone:MovieClip = null;
		
		if (ACT_X == 0) {
			printLearner(mm, learner);
		}
		
		//take action depending on act type
		if(a.isGateActivity()){
			var actLabel:String = gateTitle(a);
			_activityLayer_mc_clone = ApplicationParent.cloneMovieClip(mc_to_clone, "_activityLayer_mc_clone_" + learner.getLearnerId() + "_" + a.activityUIID, _activityLayer_mc.getNextHighestDepth(), {_activity:a, _controller:mc, _view:ltv, _x:ACT_X, _y:ACT_Y+40, actLabel:actLabel, learner:learner});
		} else if(a.activityTypeID==Activity.PARALLEL_ACTIVITY_TYPE || a.activityTypeID==Activity.OPTIONAL_ACTIVITY_TYPE){
			var children:Array = mm.getMonitor().ddm.getComplexActivityChildren(a.activityUIID);
			_activityLayer_mc_clone = ApplicationParent.cloneMovieClip(mc_to_clone, "_activityLayer_mc_clone_" + learner.getLearnerId() + "_" + a.activityUIID, _activityLayer_mc.getNextHighestDepth(), {_activity:a, _children:children, _controller:mc,_view:ltv, _x:ACT_X, _y:ACT_Y+40, learner:learner});
		} else {
			_activityLayer_mc_clone = ApplicationParent.cloneMovieClip(mc_to_clone, "_activityLayer_mc_clone_" + learner.getLearnerId() + "_" + a.activityUIID, _activityLayer_mc.getNextHighestDepth(), {_activity:a, _controller:mc, _view:ltv, _x:ACT_X, _y:ACT_Y+40, learner:learner});
		}
		
		ACT_X = (_activityLayer_mc_clone != null) ? _activityLayer_mc_clone._x + _activityLayer_mc_clone._width : 0;
			
		Debugger.log("_clone:" + _activityLayer_mc_clone + " xPOS=" + mc_to_clone._x + " yPOS=" + mc_to_clone._y + " _x=" + _activityLayer_mc_clone._x + " _y=" + _activityLayer_mc_clone._y, Debugger.CRITICAL, "cloneDesign", "LTV");

	}

	/**
	 * Get the CSSStyleDeclaration objects for each component and apply them
	 * directly to the instance
	 */
	private function setStyles():Void{
		var styleObj = _tm.getStyleObject('BGPanel');
		bkg_pnl.setStyle('styleName',styleObj);
	}

	private function gateTitle(a:Activity):String{
		var titleToReturn:String
		switch(String(a.activityTypeID)){
			case '3' :
				titleToReturn = "Synchronise Gate"
				return titleToReturn;
				break;
			case '4' :
				titleToReturn = "Schedule Gate"
				return titleToReturn;
				break;
			case '5' :
				titleToReturn = "Permission Gate"
				return titleToReturn;
				break;
			default:
			Debugger.log('not defined yet',Debugger.GEN,'drawActivity','LearnerTabView');
		}
	}
	/**
    * Sets the size of the canvas on stage, called from update
    */
	private function setSize(mm:MonitorModel):Void{
		var s:Object = mm.getSize();
		
		var actkeys = mm.getSequence().getLearningDesignModel().activities.keys();
		var ddmBranch_keys:Array = mm.getSequence().getLearningDesignModel().branches.keys();
		var scpWidth:Number = Math.max((actkeys.length)*130, s.w);
		
		var newWidth:Number = (_activityLayer_mc._width < scpWidth) ? scpWidth - 6 : _activityLayer_mc._width;
		
		for (var i=0; i<learnerListArr.length; i++){
			learnerListArr[i].learnerName._width = newWidth;
			learnerListArr[i].learnerButton._x = newWidth-110;
		}

		bkg_pnl._visible = false;
		bkg_pnl.setSize(_activityLayer_mc._width + 6, 0); // fixes scroll display issue though 0 height is questionable
		
		mm.getMonitor().getMV().getMonitorLearnerScp().redraw(true);
}
	
	 /**
    * Sets the position of the canvas on stage, called from update
    * @param cm Canvas model object 
    */
	private function setPosition(mm:MonitorModel):Void{
        var p:Object = mm.getPosition();
        this._x = p.x;
        this._y = p.y;
	}
	
	
	/**
	 * Overrides method in abstract view to ensure cortect type of controller is returned
	 * @usage   
	 * @return  CanvasController
	 */
	public function getController():MonitorController{
		var c:Controller = super.getController();
		return MonitorController(c);
	}
	
	/*
    * Returns the default controller for this view.
    */
    public function defaultController (model:Observable):Controller {
        return new MonitorController(model);
    }
	
	
}