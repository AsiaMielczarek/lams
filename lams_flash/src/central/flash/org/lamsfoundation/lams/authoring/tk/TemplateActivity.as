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
import org.lamsfoundation.lams.common.style.*;
import org.lamsfoundation.lams.common.util.*;
import org.lamsfoundation.lams.authoring.*;
import org.lamsfoundation.lams.authoring.tk.*;
import mx.controls.*;
import mx.managers.*;
import mx.events.*;

/**  
* TemplateActivity  - 
* these are the visual elements in the toolkit - 
* each one representing a LearningLibrary template activity
*/  
class org.lamsfoundation.lams.authoring.tk.TemplateActivity extends MovieClip{  
     
	private var _tm:ThemeManager;
	//private var Tip:ToolTip;
	//private var ttHolder:MovieClip;
	 //Declarations  
	private var bkg_pnl:MovieClip;
	private var title_lbl:Label;
	private var select_btn:Button;
	private var icon_mc:MovieClip;	private var _instance:TemplateActivity;	private var icon_mcl:MovieClipLoader;
	private var _taPanelStyle:Object;
	//this is set by the init object
	//contains refs to the classInstances of the activities in this TemplateActivity
	private var _activities:Array;
	private var _mainActivity:Activity;
	private var _childActivities:Array;
	private var yPos:Number;
	 
	private var _toolkitView:ToolkitView;
	 
    /**  
    * Constructor - creates an onEnterFrame doLater call to init
    */ 
    function TemplateActivity() {          
		_tm = ThemeManager.getInstance();
		_childActivities = new Array();
		//Tip = new ToolTip();
		//ttHolder = Application.tooltip;
		//let it wait one frame to set up the components.
		MovieClipUtils.doLater(Proxy.create(this,init));
    }  
  
	/**  
	* Initialises the class. set up button handlers
	*/
	function init():Void{		
		_instance = this;
				var tkv = ToolkitView(_toolkitView);
		
		
		//Set up this class to use the Flash event delegation model  
        EventDispatcher.initialize(this);  
		//_global.breakpoint();
		setStyles();
		Debugger.log('_activities.length:'+_activities.length,Debugger.GEN,'init','TemplateActivity');
		//find the 'main'/container activity
		if(_activities.length > 1){
			for (var i=0; i<_activities.length; i++){

				if(_activities[i].activityTypeID == Activity.PARALLEL_ACTIVITY_TYPE){
					_mainActivity = _activities[i];
					//SOMEBODY THINK OF THE CHILDREN!
					setUpComplexActivity();
					
				}
			}
		}else if(_activities.length == 1){
			_mainActivity = _activities[0];
		}else{
			new LFError("The activities array recieved is not valid, maybe undefined","init",this);
			//we must bail entirely now to prevent a crash or loop
			this.removeMovieClip();
		}
		
		
		
		
		icon_mcl = new MovieClipLoader();
		icon_mcl.addListener(this);
				//Debugger.log('_toolkitView:'+_toolkitView.getClassname(),4,'init','TemplateActivity');
		//set up the button handlers
		select_btn.onPress = Proxy.create(this,this['select']);
		select_btn.onRollOver = Proxy.create(this,this['rollOver']);
		select_btn.onRollOut = Proxy.create(this,this['rollOut']);
		//create an mc to hold th icon		icon_mc = this.createEmptyMovieClip("icon_mc",getNextHighestDepth());
		loadIcon();
		
		
	}
	
	/*private function initTT(){
		
		trace("ttHolder to pass: "+ttHolder)
		var ttMessage:String = "<b>"+_mainActivity.title+"</b></br></br>"+_mainActivity.description;
		Tip = new ToolTip(ttHolder, ttMessage);
	}*/
	/**
	 * Populates the _childActivities array if this is a complex activity
	 * @usage   
	 * @return  
	 */
	private function setUpComplexActivity(){
		if(_mainActivity.activityTypeID == Activity.PARALLEL_ACTIVITY_TYPE){
			//populate the _childActivities hash
			for(var i=0; i<_activities.length; i++){
				if(_activities[i].parentActivityID == _mainActivity.activityID){
					//TODO: Check they are tool activities, if not give a warning and bail.
					_childActivities.push(_activities[i]);
				}
			}
		}else{
			new LFError("Cannot handle this activity type yet","setUpComplexActivity",this,'_mainActivity.activityTypeID:'+_mainActivity.activityTypeID);
		}
	}
		/**  
	* Loads the icon for the temopate activity using a movieclip loader
	*
	*/
	private function loadIcon(loadDefaultIcon:Boolean):Void{
		//Debugger.log('Loading icon:'+Config.getInstance().serverUrl+_toolActivity.libraryActivityUIImage,4,'loadIcon','TemplateActivity');
		//TODO: Get URL from packet when its done.
		//icon_mcl.loadClip(Config.getInstance().serverUrl+"images/icon_chat.swf",icon_mc);
		if(loadDefaultIcon){
			Debugger.log('Going to use default icon: images/icon_missing.swf',Debugger.GEN,'loadIcon','TemplateActivity');
			//icon_missing.swf
			_mainActivity.libraryActivityUIImage = "images/icon_missing.swf";
			//icon_mcl.loadClip(Config.getInstance().serverUrl+"images/icon_missing.swf",icon_mc);
			
		}
		var icon_url = Config.getInstance().serverUrl+_mainActivity.libraryActivityUIImage;
		Debugger.log('Loading icon:'+icon_url,4,'loadIcon','TemplateActivity');
		icon_mcl.loadClip(icon_url,icon_mc);
	}
	
	/**  
	* Called by the MovieClip loader that loaded thie icon.
	* 
	* @param icon_mc The target mc that got the loaded movie
	*/
	private function onLoadInit(icon_mc:MovieClip):Void{
		Debugger.log('icon_mc:'+icon_mc,4,'onLoadInit','TemplateActivity');	
		//now icon is loaded lets call draw to display the stuff
		draw();
	}
	
	private function onLoadError(icon_mc:MovieClip,errorCode:String):Void{
		switch(errorCode){
			
			case 'URLNotFound' :
				Debugger.log('TemplateActivity icon failed to load - URL is not found:'+icon_mc._url,Debugger.CRITICAL,'onLoadError','TemplateActivity');	
				break;
			case 'LoadNeverCompleted' :
				Debugger.log('TemplateActivity icon failed to load - Load never completed:'+icon_mc,Debugger.CRITICAL,'onLoadError','TemplateActivity');	
				break;
		}
		
		//if there was an error - try and load the missing.swf
		loadIcon(true);
		//draw();
		
	}
	
	/**  
	* Does the visual rendering work of this TemplateActivity
	*/
	private function draw():Void{
		var toolTitle:String = _mainActivity.title
		Debugger.log("_mainActivity.title: "+_mainActivity.title, Debugger.CRITICAL, "draw", "TemplateActivity");
		if (toolTitle.length > 15){
			toolTitle = toolTitle.substr(0, 15)+"..."
		}
		
		title_lbl.text= toolTitle;
		//attach the icon now...
		var ICON_OFFSETX = 3;
		var ICON_OFFSETY = 6;		icon_mc._width = 20;
		icon_mc._height = 20;
		icon_mc._x = ICON_OFFSETX;
		icon_mc._y = ICON_OFFSETY;
		
		//initTT()
		//toolTip.text = _mainActivity.title;
		//Debugger.log('icon_mc._width:'+icon_mc._width,4,'draw','TemplateActivity');
		//Debugger.log('icon_mc._height:'+icon_mc._height,4,'draw','TemplateActivity');
	}
	
	private function getAssociatedStyle():Object{
		trace("Category ID for Activity "+_mainActivity.title +": "+_mainActivity.activityCategoryID)
		var styleObj:Object = new Object();
		switch (String(_mainActivity.activityCategoryID)){
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
				styleObj = _tm.getStyleObject('ACTPanel3')
				break;
			case '4' :
				styleObj = _tm.getStyleObject('ACTPanel4')
				break;
			case '5' :
				styleObj = _tm.getStyleObject('ACTPanel5')
				break;
            default :
                styleObj = _tm.getStyleObject('ACTPanel0')
		}
		return styleObj;
	}
	
	private function setStyles():Void{
		Debugger.log('Running....',Debugger.GEN,'setStyles','TemplateActivity');
		var styleObj;
		_taPanelStyle = _tm.getStyleObject('TAPanel');  //getAssociatedStyle()
		bkg_pnl.setStyle('styleName',_taPanelStyle);
		styleObj = _tm.getStyleObject('label');
		title_lbl.setStyle('styleName',styleObj);
		
	}
	
	/**  
	* Gets this TemplateActivity's data
	*/
	public function get toolActivity():Object{
		/*
		//if we only have one element then return that cos it must be a single toolActiivity
		if(_activities.length ==1){
			return _mainActivity;
		}else{
			return new LFError("There is more than one item in the activities array, may be a complex activity - cant return a ToolActitiy","get toolActivity",this);
		}
		*/
		Debugger.log('This function is deprecated, use mainActivity instead',Debugger.MED,'getToolActivity','TemplateActivity');
		return _mainActivity;
		
	}
	
	public function get mainActivity():Activity{
		return _mainActivity;
	}
	
	/**
	 * 
	 * @usage   
	 * @param   newactivities 
	 * @return  
	 */
	public function set activities (newactivities:Array):Void {
		_activities = newactivities;
	}
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function get activities ():Array {
		return _activities;
	}

	

	public function setState(selected:Boolean):Void{
		if(selected){
			var _taSelectedPanelStyle = _tm.getStyleObject("TAPanelSelected");
			bkg_pnl.setStyle("styleName", _taSelectedPanelStyle);
		}else{
			rollOut();
		}	}
	
	private function select():Void{
		_toolkitView.hideToolTip();
		var toolkitController = _toolkitView.getController();
		toolkitController.selectTemplateActivity(this);			
	}
	
	private function rollOver():Void{
		
		var _taRolloverPanelStyle = _tm.getStyleObject("TAPanelRollover");
		
		bkg_pnl.setStyle("styleName", _taRolloverPanelStyle);
		
		var ttMessage:String = "<b>"+_mainActivity.title+"</b> \n"+_mainActivity.description;
		var ttypos = yPos + select_btn._height
		var ttxpos = 2
		_toolkitView.showToolTip(ttMessage, ttxpos, ttypos);
		
		
		
	}
	
	private function rollOut():Void{
		bkg_pnl.setStyle("styleName",_taPanelStyle);
		_toolkitView.hideToolTip();
	}
	
	
	/**
	 * 
	 * @usage   
	 * @param   newchildActivities 
	 * @return  
	 */
	public function set childActivities (newchildActivities:Array):Void {
		_childActivities = newchildActivities;
	}
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function get childActivities ():Array {
		return _childActivities;
	}
	
	public function get title():String {
		return title_lbl.text;
	}
	
}