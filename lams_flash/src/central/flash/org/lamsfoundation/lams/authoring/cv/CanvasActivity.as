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
import org.lamsfoundation.lams.common.util.*;
import org.lamsfoundation.lams.common.util.ui.*;
import org.lamsfoundation.lams.authoring.*;
import org.lamsfoundation.lams.authoring.cv.*;
import org.lamsfoundation.lams.authoring.br.*;
import org.lamsfoundation.lams.monitoring.mv.*;
import org.lamsfoundation.lams.monitoring.mv.tabviews.LearnerTabView;
import org.lamsfoundation.lams.common.style.*
import mx.controls.*
import com.polymercode.Draw;
import mx.managers.*
import mx.containers.*;
import mx.events.*
import mx.utils.*

/**  
* CanvasActivity - 
*/  
class org.lamsfoundation.lams.authoring.cv.CanvasActivity extends MovieClip implements ICanvasActivity{

	public static var TOOL_ACTIVITY_WIDTH:Number = 123.1;
	public static var TOOL_ACTIVITY_HEIGHT:Number = 50.5;
	
	public static var TOOL_BRANCHING_WIDTH:Number = 166.0;
	public static var TOOL_BRANCHING_HEIGHT:Number = 101.0;
	
	public static var TOOL_MIN_ACTIVITY_WIDTH:Number = 65;
	public static var TOOL_MIN_ACTIVITY_HEIGHT:Number = 44;
	
	public static var GATE_ACTIVITY_HEIGHT:Number =28;
	public static var GATE_ACTIVITY_WIDTH:Number = 28;
	
	public static var BRANCH_ICON_HEIGHT:Number = 30;
	public static var BRANCH_ICON_WIDTH:Number = 30;
	
	public static var ICON_WIDTH:Number = 25;
	public static var ICON_HEIGHT:Number = 25;
	
	public static var HUB_CONNECTOR_MARGIN:Number = 25;
	
	//this is set by the init object
	private var _canvasController:CanvasController;
	private var _canvasView:CanvasView;
	private var _canvasBranchView:CanvasBranchView;
	private var _canvasComplexView:CanvasComplexView;
	
	private var _monitorController:MonitorController;
	private var _monitorView;
	
	private var _controller;
	
	private var mm:MonitorModel; // used only when called from Monitor Environment
	private var _canvasModel:CanvasModel;
	
	private var _tm:ThemeManager;
	private var _ccm:CustomContextMenu;
	
	//TODO:This should be ToolActivity
	private var _activity:Activity;
	
	private var _isSelected:Boolean;
	private var app:ApplicationParent;
	
	//locals
	private var learnerOffset_X:Number = 4;
	private var learnerOffset_Y:Number = 3;
	private var learnerContainer:MovieClip;
	
	private var _module:String;
	private var _branchConnector:Boolean;
	
	private var icon_mc:MovieClip;
	private var icon_mcl:MovieClipLoader;
	
	private var diagram_mc:CanvasBranchingDiagram;
	
	private var bkg_pnl:MovieClip;
	private var act_pnl:MovieClip;
	private var title_lbl:MovieClip;
	
	private var groupIcon_mc:MovieClip;
	private var branchIcon_mc:MovieClip;
	private var optionalIcon_mc:MovieClip;
	private var stopSign_mc:MovieClip;
	private var branchSign_mc:MovieClip;
	
	private var start_branch_icon_mc:MovieClip;
	private var finish_branch_icon_mc:MovieClip;
	private var start_branch_icon_mc_rtl:MovieClip;
	private var finish_branch_icon_mc_rtl:MovieClip;
	
	private var clickTarget_mc:MovieClip;
	
	private var canvasActivity_mc:MovieClip;
	private var canvasActivityGrouped_mc:MovieClip;
	
	private var _dcStartTime:Number = 0;
	private var _doubleClicking:Boolean;
	
	private var _visibleWidth:Number;
	private var _visibleHeight:Number;
	
	private var _base_mc:MovieClip;
	private var _selected_mc:MovieClip;

	private var fade_mc:MovieClip;
	private var bgNegative:String = "original";
	private var authorMenu:ContextMenu;
	
	private var _setupBranchView:Boolean;
	
	private var _sequenceChild:Boolean;
	private var _depthHistory:Number;
	
	private var _ddm:DesignDataModel;
	
	function CanvasActivity(_connector){
		_visible = false;
		_tm = ThemeManager.getInstance();
		_ccm = CustomContextMenu.getInstance();
		
		//Get reference to application and design data model
		app = ApplicationParent.getInstance();
		
		//let it wait one frame to set up the components.
		//this has to be set b4 the do later :)
		if((_activity.isGateActivity()) && !_sequenceChild){
			_visibleHeight = CanvasActivity.GATE_ACTIVITY_HEIGHT;
			_visibleWidth = CanvasActivity.GATE_ACTIVITY_WIDTH;
		} else if(_branchConnector) {
			_visibleHeight = CanvasActivity.BRANCH_ICON_HEIGHT;
			_visibleWidth = CanvasActivity.BRANCH_ICON_WIDTH;
		} else if(_activity.isGroupActivity()){
			_visibleHeight = (_sequenceChild) ? CanvasActivity.TOOL_MIN_ACTIVITY_HEIGHT : CanvasActivity.TOOL_ACTIVITY_HEIGHT;
			_visibleWidth = (_sequenceChild) ? CanvasActivity.TOOL_MIN_ACTIVITY_WIDTH : CanvasActivity.TOOL_ACTIVITY_WIDTH;
		} else if(_activity.isBranchingActivity()) {
			_visibleHeight = (_sequenceChild) ? CanvasActivity.TOOL_MIN_ACTIVITY_HEIGHT : CanvasActivity.TOOL_BRANCHING_HEIGHT;
			_visibleWidth = (_sequenceChild) ? CanvasActivity.TOOL_MIN_ACTIVITY_WIDTH :CanvasActivity.TOOL_BRANCHING_WIDTH;
		}else{
			_visibleHeight = (_sequenceChild) ? CanvasActivity.TOOL_MIN_ACTIVITY_HEIGHT : CanvasActivity.TOOL_ACTIVITY_HEIGHT;
			_visibleWidth = (_sequenceChild) ? CanvasActivity.TOOL_MIN_ACTIVITY_WIDTH :CanvasActivity.TOOL_ACTIVITY_WIDTH;
		}
		
		_base_mc = this;
		
		//call init if we have passed in the _activity as an initObj in the attach movie,
		//otherwise wait as the class outside will call it
		if(_activity != undefined){
			init();
		}
		
	}
	
	public function init(initObj):Void{
		Debugger.log("init called.", Debugger.CRITICAL, "init", "CanvasActivity");

		if(initObj){
			_module = initObj._module;
			if (_module == "monitoring"){
				_monitorView = initObj._monitorView;
				_monitorController = initObj._monitorController;
				learnerContainer = initObj.learnerContainer;
			}else {
				_canvasView = initObj._canvasView;
				_canvasController = initObj._canvasController;
			}
			
			_activity = initObj.activity;
		}
		
		_canvasModel = CanvasModel(_canvasController.getModel());
		mm = MonitorModel(_monitorController.getModel());
		
		Debugger.log("mm: " + mm, Debugger.CRITICAL, "init", "CanvasActivity");
		Debugger.log("_activity: " + _activity.activityUIID, Debugger.CRITICAL, "init", "CanvasActivity");

		showAssets(false);
		
		if (_activity.selectActivity == "false"){
			_isSelected = false;
			refresh();
		}
		
		if(!_activity.isGateActivity() && !_activity.isGroupActivity() && !_activity.isBranchingActivity() || _branchConnector){
			loadIcon();
		} else if(_activity.isBranchingActivity()) {
			loadDiagram();
		}
		
		setStyles();
		
		MovieClipUtils.doLater(Proxy.create(this,draw));
	}
	
	private function showAssets(isVisible:Boolean){
		groupIcon_mc._visible = isVisible;
		branchIcon_mc._visible = isVisible;
		optionalIcon_mc._visible = isVisible;
		start_branch_icon_mc._visible = isVisible;
		finish_branch_icon_mc._visible = isVisible;
		start_branch_icon_mc_rtl._visible = isVisible;
		finish_branch_icon_mc_rtl._visible = isVisible;
		title_lbl._visible = isVisible;
		icon_mc._visible = isVisible;
		stopSign_mc._visible = isVisible;
		branchSign_mc._visible = isVisible;
		canvasActivity_mc._visible = isVisible;
		clickTarget_mc._visible = isVisible;
		canvasActivityGrouped_mc._visible = isVisible;
		fade_mc._visible = isVisible;
	}
	
	/**
	 * Updates the CanvasActivity display fields with the current data
	 * @usage   
	 * @return  
	 */
	public function refresh(setNegative:Boolean):Void{
		bgNegative = String(setNegative);
		setStyles();
		
		setupBranchView = false;
		
		if(diagram_mc != null)
			diagram_mc.refresh();
		
		draw();
		setSelected(_isSelected);
	}
	
	public function setSelected(isSelected){
		Debugger.log(_activity.title+" isSelected:"+isSelected,4,'setSelected','CanvasActivity');
		var MARGIN = 5;
		
		if(isSelected) {
			//draw a selected border
			var tgt_mc;
			if(_activity.isGateActivity() && !_sequenceChild)
				tgt_mc = stopSign_mc;			
			else if(_activity.groupingUIID > 0)
				tgt_mc = canvasActivityGrouped_mc;
			else
				tgt_mc = canvasActivity_mc;
				
			Debugger.log("tgt_mc:"+tgt_mc,4,'setSelected','CanvasActivity');
				
			//vars
			var tl_x = tgt_mc._x - MARGIN; 											//top left x
			var tl_y = tgt_mc._y - MARGIN;											//top left y
			var tr_x = tgt_mc._x + tgt_mc._width + MARGIN;							//top right x
			var tr_y = tl_y;														//top right y
			var br_x = tr_x;														//bottom right x
			var br_y = tgt_mc._y + tgt_mc._height + MARGIN;							//bottom right y
			var bl_x = tl_x;														//biottom left x															
			var bl_y = br_y;														//bottom left y
				
			
			if(_selected_mc){
				_selected_mc.removeMovieClip();
			}
			
			_selected_mc = _base_mc.createEmptyMovieClip('_selected_mc', _base_mc.getNextHighestDepth());
				
			var dashStyle:mx.styles.CSSStyleDeclaration = _tm.getStyleObject("CAHighlightBorder");
			var color:Number = dashStyle.getStyle("color");
				
			Draw.dashTo(_selected_mc,tl_x,tl_y,tr_x,tr_y,2,3,2,color);
			Draw.dashTo(_selected_mc,tr_x,tr_y,br_x,br_y,2,3,2,color);
			Draw.dashTo(_selected_mc,br_x,br_y,bl_x,bl_y,2,3,2,color);
			Draw.dashTo(_selected_mc,bl_x,bl_y,tl_x,tl_y,2,3,2,color);
						
			_isSelected = isSelected;
					
		} else {
			if(depthHistory != null)
				this.swapDepths(this.depthHistory);

			//hide the selected border
			_selected_mc.removeMovieClip();
		}
		
	}
	
	private function loadDiagram():Void {
		diagram_mc = CanvasBranchingDiagram(this.attachMovie("CanvasBranchingDiagram", "diagram_mc", this.getNextHighestDepth(), {_ddm: getDDM(), _branchingActivity: activity, _visible: false}));

		// swap depths if transparent layer visible
		if(fade_mc._visible) {
			diagram_mc.swapDepths(fade_mc);
		}
	}
	
	private function loadIcon():Void{
		icon_mc = this.createEmptyMovieClip("icon_mc", this.getNextHighestDepth());
		var ml = new MovieLoader(Config.getInstance().serverUrl+_activity.libraryActivityUIImage,setUpActIcon,this,icon_mc);	

		// swap depths if transparent layer visible
		if(fade_mc._visible) {
			icon_mc.swapDepths(fade_mc);
		}
	}
	
	private function setUpActIcon(icon_mc):Void{
		icon_mc._x = (_visibleWidth / 2) - (icon_mc._width / 2);
		icon_mc._y = (_visibleHeight / 2) - (icon_mc._height / 2);
		icon_mc._y -= (!_sequenceChild) ? 6 : 0;
	}
	
	private function drawLearners():Void {
		mm = MonitorModel(_monitorController.getModel());
		
		var learner_X = _activity.xCoord + learnerOffset_X;
		var learner_Y = _activity.yCoord + learnerOffset_Y;
		
		var parentAct:Activity = mm.getMonitor().ddm.getActivityByUIID(_activity.parentUIID)
		
		var xCoord = _activity.xCoord;
		var yCoord;
				
		if (_activity.parentUIID != null) {
			var actX = _activity.xCoord;
			var parentX = (mm.activeView instanceof CanvasComplexView) ? mm.activeView.openActivity._x : parentAct.xCoord;
			
			xCoord = (parentAct.activityTypeID == Activity.SEQUENCE_ACTIVITY_TYPE) ? actX : parentX;

			if(parentAct.activityTypeID != Activity.PARALLEL_ACTIVITY_TYPE 
				&& parentAct.activityTypeID != Activity.SEQUENCE_ACTIVITY_TYPE) {
				
				xCoord = parentX + actX;
				
				learner_X = (learner_X != null) ? learner_X + parentX : null;
				learner_Y = (mm.activeView instanceof CanvasComplexView) ? learner_Y + mm.activeView.openActivity._y : learner_Y + parentAct.yCoord;

				
			} else {
				xCoord = parentX;
				yCoord = (mm.activeView instanceof CanvasComplexView) ? mm.activeView.openActivity._y : parentAct.yCoord;
				
				var gparentAct:Activity = mm.getMonitor().ddm.getActivityByUIID(parentAct.parentUIID);
				
				if(gparentAct.isOptionsWithSequencesActivity()) {
					xCoord = (mm.activeView instanceof CanvasComplexView) ? xCoord + parentAct.xCoord : gparentAct.xCoord + xCoord;
					
					learner_X = (learner_X != null) ? learner_X + xCoord : null;
					learner_Y = (mm.activeView instanceof CanvasComplexView) ? learner_Y + parentAct.yCoord + yCoord : learner_Y + gparentAct.yCoord + yCoord;

				}
				
			}
					
		}
		
		// get the length of learners from the Monitor Model and run a for loop.
		for (var j=0; j<mm.allLearnersProgress.length; j++){
				
			var learner:Object = new Object();
			learner = mm.allLearnersProgress[j]
					
			//Gets a true if learner's currect activityID matches this activityID else false.
			var isLearnerCurrentAct:Boolean = Progress.isLearnerCurrentActivity(learner, _activity.activityID);
			
			if (isLearnerCurrentAct){
				
				// Add + icon to indicate that more users are currently at the Activity. 
				// We are unable to display all the users across the Activity's panel.
				Debugger.log("learner_X: " + learner_X + " ref: " + learnerContainer + " xcoord: " + xCoord, Debugger.CRITICAL, "drawLearners", "CanvasActivity");
				
				if(learner_X > (xCoord + getVisibleWidth() - 10)) {
					learnerContainer.attachMovie("learnerIcon", "learnerIcon"+learner.getUserName()+activity.activityUIID, learnerContainer.getNextHighestDepth(), {_activity:_activity, learner:learner, _monitorController:_monitorController, _x:learner_X, _y:learner_Y, _hasPlus:true, _clone:false });
					return;
				}

				// attach icon
				learnerContainer.attachMovie("learnerIcon", "learnerIcon"+learner.getUserName()+activity.activityUIID, learnerContainer.getNextHighestDepth(), {_activity:_activity, learner:learner, _monitorController:_monitorController, _x:learner_X, _y:learner_Y, _hasPlus:false, _clone:false });
						
				//  space icons
				learner_X += 10;
			}
		}
	}
	
	
	/**
	 * Add + icon to indicate that more users are currently at the Activity. 
	 * We are unable to display all the users across the Activity's panel.
	 * 
	 * @usage   
	 * @param   target  The target reference, this class OR a parent
	 * @param   x_pos  	The X position of the icon
	 * @return  
	 */

	
	/**
	 * Does the work of laying out the screen assets.
	 * Depending on type of Activity different bits will be shown
	 * @usage   
	 * @return  
	 */
	private function draw(){
		
		// Drawing learner on the activty. 
		if(_module == "monitoring")
			drawLearners();
		
		Debugger.log(_activity.title+',_activity.isGateActivity():'+_activity.isGateActivity(),4,'draw','CanvasActivity');
		
		setStyles();
		
		var theIcon_mc:MovieClip;
		title_lbl._visible = true;
		clickTarget_mc._visible = true;
		fade_mc._visible = false;
			
		if(_activity.isReadOnly() && getDDM().editOverrideLock == 1){
			Debugger.log("Making transparent layer visible. ", Debugger.CRITICAL, 'draw', 'CanvasActivity');
			fade_mc._visible = true;
		}

		if(_activity.isGateActivity()){
			stopSign_mc._visible = true;
			stopSign_mc._x = 0;
			stopSign_mc._y = 0;
			
			if(_sequenceChild) {
				theIcon_mc = stopSign_mc;
				setUpActIcon(theIcon_mc);
				
				theIcon_mc._visible = true;
				canvasActivity_mc._visible = true;
				
				clickTarget_mc._width = CanvasActivity.TOOL_MIN_ACTIVITY_WIDTH;
				clickTarget_mc._height = CanvasActivity.TOOL_MIN_ACTIVITY_HEIGHT;
			}
			
		} else if(_branchConnector) {
			start_branch_icon_mc._visible = (!ApplicationParent.isRTL());
			start_branch_icon_mc._x = 1;
			start_branch_icon_mc._y = 1;
			
			start_branch_icon_mc_rtl._visible = (ApplicationParent.isRTL());
			start_branch_icon_mc_rtl._x = 31;
			start_branch_icon_mc_rtl._y = 1;
			
			finish_branch_icon_mc._visible = (!ApplicationParent.isRTL());
			finish_branch_icon_mc._x = 1;
			finish_branch_icon_mc._y = 1;
			
			finish_branch_icon_mc_rtl._visible = (ApplicationParent.isRTL());
			finish_branch_icon_mc_rtl._x = 31;
			finish_branch_icon_mc_rtl._y = 1;
			
		} else {
			
			//chose the icon:
			if(_activity.isGroupActivity()){
				groupIcon_mc._visible = true;
				optionalIcon_mc.visible = false;
				icon_mc._visible = false;
				theIcon_mc = groupIcon_mc;
			} else if(_activity.isBranchingActivity()){
				Debugger.log("empty: " + diagram_mc.empty, Debugger.CRITICAL, "draw", "CanvasActivity");
				
				branchIcon_mc._visible = diagram_mc.empty;
				groupIcon_mc._visible = false;
				optionalIcon_mc.visible = false;
				icon_mc._visible = false;
				
				if(!diagram_mc.empty)
					diagram_mc._visible = true;
				else
					theIcon_mc = branchIcon_mc;
					
			} else if(_activity.isOptionalActivity()){
				branchIcon_mc._visible = false;
				groupIcon_mc._visible = false;
				optionalIcon_mc.visible = true;
				icon_mc._visible = false;
				theIcon_mc = optionalIcon_mc;
			} else if(_activity.isOptionsWithSequencesActivity()){
				branchIcon_mc._visible = false;
				groupIcon_mc._visible = false;
				optionalIcon_mc.visible = true;
				icon_mc._visible = false;
				theIcon_mc = optionalIcon_mc;
			} else {
				groupIcon_mc._visible = false;
				branchIcon_mc._visible = false;
				optionalIcon_mc.visible = false;
				icon_mc._visible = true;
				theIcon_mc = icon_mc;
			}
			
			setUpActIcon(theIcon_mc);
			theIcon_mc._visible = true;
		
			//chose the background mc
			if(_activity.groupingUIID != null && _activity.groupingUIID > 0){
				canvasActivityGrouped_mc._visible = true;
				canvasActivity_mc._visible = false;
			}else{
				canvasActivity_mc._visible = true;
				canvasActivityGrouped_mc._visible = false;
			}
			
			title_lbl.visible = true;
			stopSign_mc._visible = false;
			branchSign_mc._visible = false;
			
			//write text
			title_lbl.text = _activity.title;
			
			clickTarget_mc._width = (_sequenceChild) ? CanvasActivity.TOOL_MIN_ACTIVITY_WIDTH : TOOL_ACTIVITY_WIDTH;
			clickTarget_mc._height = (_sequenceChild) ? CanvasActivity.TOOL_MIN_ACTIVITY_HEIGHT : TOOL_ACTIVITY_HEIGHT;
			
			if(activity.isBranchingActivity()) {
				clickTarget_mc._width = TOOL_BRANCHING_WIDTH;
				clickTarget_mc._height = TOOL_BRANCHING_HEIGHT;
			}
		}

		//position
		Debugger.log('setting position:',Debugger.CRITICAL,'draw','CanvasActivity');
		
		if(!_branchConnector) {
			_x = _activity.xCoord;
			_y = _activity.yCoord;
		} else {
			var _canvasSize:Object;
			if(_module == "monitoring")
				_canvasSize = mm.getSize();
			else
				_canvasSize = _canvasModel.getSize();
			
			if(_canvasBranchView.isStart(this)) {
				Debugger.log('start branch:' + _canvasBranchView.activity.startXCoord + ":" + _canvasBranchView.activity.startYCoord, Debugger.CRITICAL,'draw','CanvasActivity');
				_x = (_canvasBranchView.activity.startXCoord != null) ? _canvasBranchView.activity.startXCoord : _x;
				_y = (_canvasBranchView.activity.startYCoord != null) ? _canvasBranchView.activity.startYCoord : _y;
			} else if(_canvasBranchView.isEnd(this)) {
				Debugger.log('end branch:' + _canvasBranchView.activity.endXCoord + ":" + _canvasBranchView.activity.endYCoord,Debugger.CRITICAL,'draw','CanvasActivity');
				_x = (_canvasBranchView.activity.endXCoord != null) ? _canvasBranchView.activity.endXCoord : _x;
				_y = (_canvasBranchView.activity.endYCoord != null) ? _canvasBranchView.activity.endYCoord : _y;
			}
		}
		
		_visible = true;
		
		if (_activity.runOffline){
			bgNegative = "true"
			setStyles();
		}
		
		if(setupBranchView)
			if(_module == "monitoring")
				mm.openBranchActivityContent(this, false);
			else
				_canvasModel.openBranchActivityContent(this, false);
		
	}

	
	private function onRollOver():Void{
		if (_module == "monitoring"){
			_ccm.showCustomCM(_ccm.loadMenu("activity", "monitoring"))
		}else {
			_ccm.showCustomCM(_ccm.loadMenu("activity", "authoring"))
		}
	}
	
	private function onRollOut():Void{
		if (_module == "monitoring"){
			_ccm.showCustomCM(_ccm.loadMenu("canvas", "monitoring"))
		}else {
			_ccm.showCustomCM(_ccm.loadMenu("canvas", "authoring"))
		}
	}	
	
	private function onPress():Void{
			// check double-click
			var now:Number = new Date().getTime();
			Debugger.log('_module:'+_module,Debugger.GEN,'onPress','CanvasActivity');
			Debugger.log('_controller:'+_monitorController,Debugger.GEN,'onPress','CanvasActivity');
			
			if((now - _dcStartTime) <= Config.DOUBLE_CLICK_DELAY && !branchConnector){
				if (app.controlKeyPressed != "transition"){
					_doubleClicking = true;
					if (_module == "monitoring"){
						Debugger.log('DoubleClicking: '+this.activity.activityID,Debugger.GEN,'onPress','CanvasActivity For Monitoring');
						_monitorController.activityDoubleClick(this, "MonitorTabView");
					}else {
						Debugger.log('DoubleClicking: '+this,Debugger.CRITICAL,'onPress','CanvasActivity');
						_canvasController.activityDoubleClick(this);
					}
				}
				
				app.controlKeyPressed = "";
				
			}else{
				
				_doubleClicking = false;
				
				if (_module == "monitoring"){
					Debugger.log('SingleClicking1:+'+this,Debugger.GEN,'onPress','CanvasActivity for monitoring');
					_monitorController.activityClick(this);
				}else {
					Debugger.log('SingleClicking2:+'+this,Debugger.GEN,'onPress','CanvasActivity');
					_canvasController.activityClick(this);
				}			
				
			}
			
			_dcStartTime = now;
	
	}
	
	private function onRelease():Void{
		if(!_doubleClicking){
			Debugger.log('Releasing:'+this,Debugger.GEN,'onRelease','CanvasActivity');
			Debugger.log('_module:'+_module,Debugger.GEN,'onRelease','CanvasActivity');
			
			
			if (_module == "monitoring"){
				_monitorController.activityRelease(this);
			}else {
				_canvasController.activityRelease(this);
			}
			
		}
		
	}
	
	private function onReleaseOutside():Void{
		Debugger.log('ReleasingOutside:'+this,Debugger.GEN,'onReleaseOutside','CanvasActivity');
		if (_module == "monitoring"){
			_monitorController.activityReleaseOutside(this);
		}else {
			_canvasController.activityReleaseOutside(this);
		}
	}
	
	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function getVisibleWidth ():Number {
		return _visibleWidth;
	}

	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function getVisibleHeight ():Number {
		return _visibleHeight;
	}
	
	public function setPosition(x:Number, y:Number):Void {
		_x = x;
		_y = y;
		
		if(activity.branchView != null) {
			activity.branchView._x = _x + getVisibleWidth()/2;
			activity.branchView._y = _y + getVisibleHeight()/2;
		}
		
	}

	
	public function set isSetSelected(v:Boolean):Void{
		_isSelected = v;
	}
	
	public function get activity():Activity{
		return getActivity();
	}
	
	public function set activity(a:Activity){
		setActivity(a);
	}
	
	
	public function getActivity():Activity{
		return _activity;

	}
	
	public function setActivity(a:Activity){
		_activity = a;
	}
	
	public function get branchConnector():Boolean {
		if(_branchConnector != null) return _branchConnector;
		else return false;
	}
	
	public function set branchConnector(a:Boolean):Void {
		_branchConnector = a;
	}
	
	private function getAssociatedStyle():Object{
		var styleObj:Object = new Object();
		
		if(_root.actColour == "true") {
			switch (String(_activity.activityCategoryID)){
				case '0' :
					styleObj = _tm.getStyleObject('ACTPanel0')
					break;
				case '1' :
					styleObj = _tm.getStyleObject('ACTPanel1')
					break;
				case '2' :
					styleObj = _tm.getStyleObject('ACTPanel2')
					break;
				case '3' :
					styleObj = _tm.getStyleObject('ACTPanel5')
					break;
				case '4' :
					styleObj = _tm.getStyleObject('ACTPanel4')
					break;
				case '5' :
					styleObj = _tm.getStyleObject('ACTPanel1')
					break;
				case '6' :
					styleObj = _tm.getStyleObject('ACTPanel3')
					break;
				default :
					styleObj = _tm.getStyleObject('ACTPanel0')
			}
		} else {
			styleObj = _tm.getStyleObject('ACTPanel');
		}
		
		return styleObj;
	}


	/**
	 * Get the CSSStyleDeclaration objects for each component and applies them
	 * directly to the instanced
	 * @usage   
	 * @return  
	 */
	private function setStyles() {
		var my_color:Color = new Color(this);
		var transNegative = {ra:-100, ga:-100, ba:-100, rb:255, gb:255, bb:255};
		var transPositive = {ra:100, ga:100, ba:100, rb:0, gb:0, bb:0};
		
		var styleObj = _tm.getStyleObject('CALabel');
		
		title_lbl.setStyle('styleName',styleObj);
		title_lbl.setStyle("textAlign", "center")
		
		if (bgNegative == "true"){
			my_color.setTransform(transNegative);
		}else if(bgNegative == "false"){
			my_color.setTransform(transPositive);
		}else if(bgNegative == "original"){
			
			if (this.activity.parentUIID != null || this.activity.parentUIID != undefined){
				if (_module != "monitoring"){
					var parentAct = _canvasModel.getCanvas().ddm.getActivityByUIID(this.activity.parentUIID)
				}else {
					var parentAct = mm.getMonitor().ddm.getActivityByUIID(this.activity.parentUIID)
				}
					
				styleObj = getAssociatedStyle();
				act_pnl.setStyle('styleName',styleObj);
				
			} else {
				styleObj = getAssociatedStyle();
				act_pnl.setStyle('styleName',styleObj);
			}
			
		}
		
    }
	
	private function destroy() {
		activity.branchView.removeMovieClip();
	}
	
	public function get setupBranchView():Boolean {
		return _setupBranchView;
	}
	
	public function set setupBranchView(a:Boolean):Void {
		_setupBranchView = a;
	}
	
	public function get depthHistory():Number {
		return _depthHistory;
	}
	
	public function set depthHistory(a:Number):Void {
		_depthHistory = a;
	}
	
	public function get actChildren():Array {
		return getDDM().getComplexActivityChildren(activity.activityUIID)
	}
	
	private function getDDM():DesignDataModel {
		if(_module == "monitoring") {
			return _monitorView.ddm;
		} else if(_canvasBranchView != null){
			return _canvasBranchView.ddm;
		} else {
			return _canvasView.ddm;
		}
	}
	
	public function hit():Void {
		onPress();
		onRelease();
	}

}