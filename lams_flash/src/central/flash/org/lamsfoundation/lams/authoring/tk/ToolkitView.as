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
import org.lamsfoundation.lams.common.dict.*;
import org.lamsfoundation.lams.common.util.*;
import org.lamsfoundation.lams.common.style.*;
import org.lamsfoundation.lams.authoring.*;
import org.lamsfoundation.lams.common.ui.*
import org.lamsfoundation.lams.authoring.tk.*;
import org.lamsfoundation.lams.common.mvc.*;
import org.lamsfoundation.lams.authoring.cv.*  

import mx.managers.*;
import mx.controls.*;
import mx.events.*
/**
* Authoring view for the toolkit
* @author DC
*/
class ToolkitView extends AbstractView {
	private var bkg_pnl:MovieClip;
	
	private var toolkitLibraries_sp:MovieClip;
	private var libraryActivityDesc_txa:TextArea;
	private var title_lbl:Label;
	private var title_btn:Button;
	private var toolTip:MovieClip;
	private var _className = "ToolkitView";
	private var _depth:Number;
	
	private var dragIcon_mc:MovieClip;
	private var dragIcon_mcl:MovieClipLoader;
	
	private var dragIconListener:Object;
	private var _dictionary:Dictionary;
	private var _tm:ThemeManager;
	private var _tip:ToolTip;
	
	//sorry mvc guru but i disagree - little state var here
	private var _dragging:Boolean;
	private var _scrollPanelWidthDiff:Number;            //Difference in width between scrollpane and panel

	private var _toolkitLoaded:Boolean;

    //These are defined so that the compiler can 'see' the events that are added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;    

	/**
	* Constructor
	*/
	public function ToolkitView(){	
        //Set up this class to use the Flash event delegation model
        EventDispatcher.initialize(this);     
		_tm = ThemeManager.getInstance();
		_dictionary = Dictionary.getInstance();
		_tip = new ToolTip(_tm);
		_dictionary.addEventListener('init',Proxy.create(this,setupLabels));
		_toolkitLoaded = false;
		//Debugger.log('Running',4,'Constructor','ToolkitView');
	}
	
	/**
	* Initialisation - sets up the mvc relations ship Abstract view.
	* Also creates a doLater handler for createToolkit
	*/
	public function init(m:Observable, c:Controller){
		//Invoke superconstructor, which sets up MVC relationships.
		super (m, c);
		//Debugger.log('Running',4,'init','ToolkitView');
		_dragging = false;
		
		dragIconListener = new Object();
		dragIconListener.cRef = this;
		dragIcon_mcl = new MovieClipLoader();
		dragIcon_mcl.addListener(dragIconListener);
		title_btn.useHandCursor = false;
        title_btn.onRollOver = Proxy.create(this,this['showToolTip'], undefined, null, null, title_btn);
		title_btn.onRollOut = Proxy.create(this,this['hideToolTip']);
		/**  
		* Called by the MovieClip loader that loaded the drag icon.
		* 
		* @param dragIcon_mc The target mc that got the loaded movie
		*/
		dragIconListener.onLoadInit = function(dragIcon_mc){
			//Debugger.log('dragIcon_mc:'+dragIcon_mc,4,'dragIconListener.onLoadInit','ToolkitView');
			dragIcon_mc._visible = false;
			//dragIcon_mc = dragIcon_mc;
			//Proxy.create(this.cRef,this.cRef['setUpDrag'],dragIcon_mc);
			this.cRef.setUpDrag(dragIcon_mc);
			//dragIcon_mc.startDrag(true);
			/*			
			dragIcon_mc.onPress = function(){
				this.startDrag(true);
			}
			*/
		}
		
		
		
		//Application.getInstance().setupAppCM(false)
		
		//Create a clip that will wait a frame before dispatching init to give components time to setup
		this.onEnterFrame = createToolkit;
	}
	
    public function getCopy(){
		Application.getInstance().copy();
	}
	
	public function getPaste(){
		Application.getInstance().paste();
	}
	public function setupLabels(){
		
		var tkTitle = Dictionary.getValue('tk_title')
		if (tkTitle.length > 25){
			tkTitle = tkTitle.substr(0, 24)+"..."
		} 
		title_lbl.text = "<b>"+tkTitle+"</b>";
		

	}
	/**
	* Sets up the toolkit, its actually attached in toolkit.as
	*/
	public function createToolkit():Void {
        //Work out difference between scrollpane and panel (container) width
        _scrollPanelWidthDiff = bkg_pnl.width - toolkitLibraries_sp.width;
		delete this.onEnterFrame;
		_depth = this.getNextHighestDepth();
		
		
        setStyles();
        //dispatch load event
        dispatchEvent({type:'load',target:this});
		//Debugger.log('_toolkit_mc.desc_panel:'+this.desc_panel,4,'createToolkit','ToolkitView');
		//layoutToolkit();
	}
        
	
	/**
	* Updates state of the tookit, called by the model
	*
	* @param   o   		The model object that is broadcasting an update.
	* @param   infoObj  object with details of changes to model. will contain one field "update type"
	*/
	public function update (o:Observable,infoObj:Object):Void {
        //Update view from info object
        //Debugger.log('Recived an UPDATE!, updateType:'+infoObj.updateType,4,'update','ToolkitView');
        switch (infoObj.updateType) {
            case 'LIBRARIES_UPDATED' :
                updateLibraries(o);
                break;
            case 'TEMPLATE_ACTIVITY_SELECTED' :
                updateSelectedTemplateActivity(o);
                break;
            case 'SIZE' : 
                //set the size
                setSize(o);
                break;
            case 'POSITION' : 
                //set the position of the clip based on the view
                setPosition(o);
                break;
            default :
            //TODO: DI-Deal with default case where button type is not caught
        }
	}

    /**
    * Change toolkit size
    */
    private function setSize(o:Observable):Void{
        //Cast the observable instance into the model and get size info
        var m = ToolkitModel(o);
        var s:Object = m.getSize();
        //Panel
        bkg_pnl.setSize(s.w,s.h);
        //Debugger.log('s.w:' + s.w,Debugger.GEN,'setSize','org.lamsfoundation.lams.ToolkitView');
        //Debugger.log('s.h:' + s.h,Debugger.GEN,'setSize','org.lamsfoundation.lams.ToolkitView');
        //Debugger.log('libraryActivityDesc_txa._y:' + libraryActivityDesc_txa._y,Debugger.GEN,'setSize','org.lamsfoundation.lams.ToolkitView');
        //Debugger.log('libraryActivityDesc_txa.height:' + libraryActivityDesc_txa.height,Debugger.GEN,'setSize','org.lamsfoundation.lams.ToolkitView');

        //Calculate scrollpane size
        var spWidth:Number = s.w-_scrollPanelWidthDiff;
        var spHeight:Number = s.h-(libraryActivityDesc_txa._y+libraryActivityDesc_txa.height)
        
        //Scrollpane
        if(spWidth > 0 && spHeight>0) {
            toolkitLibraries_sp.setSize(spWidth,spHeight);
        }
        
    }
    
    /**
    * Set the toolkit position
    */
    private function setPosition(o:Observable):Void{
        //Cast observable instance into model and get position and set it on clip
        var m = ToolkitModel(o);
        var p:Object = m.getPosition();
        this._x = p.x;
        this._y = p.y;
    }

	/**
	* Updates learning library activities.  Creates a templateActivity mc / class for each one.
	* NOTE: Each library element contains an array of templateActivities.
	* templateActivities array may contain just one ToolActivity, or a container activity
	* like Parralel and corresponding child activities
	*
	* @param   o   		The model object that is broadcasting an update.
	*/
	private function updateLibraries(o:Observable){
		Debugger.log('Running:: toolkitLoaded: ' + _toolkitLoaded, Debugger.CRITICAL, 'updateLibraries', 'ToolkitView');
		if(_toolkitLoaded) return;

		var yPos:Number = 0;
		
		//set SP the content path:
		toolkitLibraries_sp.contentPath = "empty_mc";
		
		var tkv = ToolkitView(this);
		var tkm:ToolkitModel = getModel();
		//get the hashtable
		var myLibs:Hashtable = tkm.getToolkitLibraries();
	
		//loop through the libraries
		var keys:Array = myLibs.keys();
		//keys.sortOn("title", Array.CASEINSENSITIVE);
		for(var i=0; i<keys.length; i++){
			//NOTE: Each library element contains an array of templateActivities.  
			
			var learningLib:Object = myLibs.get(keys[i]);
			if(learningLib.templateActivities.length > 1){
				Debugger.log('Template activities library length is greater than 1, may be complex activity',Debugger.GEN,'updateLibraries','ToolkitView');
			}
			
			//NOW we pass in the whole array, as complex activities are supprted in this way
			var activities:Array = learningLib.classInstanceRefs;
			//Debugger.log('toolActivity '+ta.title+'('+ta.activityID+')',4,'updateLibraries','ToolkitView');
			
			// First condition true if is a native lams tool
			var toolAct:ToolActivity = ToolActivity(activities[0]);
			if (toolAct.extLmsId == null || toolAct.extLmsId == undefined) { 
				Debugger.log('ToolActivity->Native tool: ' + toolAct.title, Debugger.CRITICAL, 'updateLibraries', 'ToolView');
			}
			// Second condition true if it is an external tool adapter tool
			else if (_root.extlmsid != undefined && _root.extlmsid != null) {				
				var mappedServers:Array = toolAct.mappedServers;
				var found:Boolean = false;
				
				Debugger.log('FOR LUKE mappedServers length ' + mappedServers.length, Debugger.CRITICAL, 'updateLibraries', 'ToolView');
				
				for (var j = 0; j < mappedServers.length; j++) {
					Debugger.log('FOR LUKE mappedServers entry ' + mappedServers[j], Debugger.CRITICAL, 'updateLibraries', 'ToolView');
					if (String(mappedServers[j]) == _root.extlmsid) {
						found = true;
					}
				}
				
				// If the extlmsid is not matched against one of the mapped servers, the tool is invalid
				if (!found) {
					Debugger.log('ToolActivity->External tool, internal request: ' + toolAct.title, Debugger.CRITICAL, 'updateLibraries', 'ToolView');
					continue;
				}
				// Else the extlmsid is matched against one of the mapped servers, the tool is valid
				else {
					Debugger.log('ToolActivity->External tool, external request: ' + toolAct.title, Debugger.CRITICAL, 'updateLibraries', 'ToolView');
				}
			}
			// Else is a tool adapter tool, but the call was not from external server, therefore disable
			else {
				Debugger.log('ToolActivity->External tool, internal request: ' + toolAct.title, Debugger.CRITICAL, 'updateLibraries', 'ToolView');
				continue;
			}
			
			var templateActivity_mc = toolkitLibraries_sp.content.attachMovie("TemplateActivity","ta_"+learningLib.learningLibraryID,_depth++,{_activities:activities,_toolkitView:tkv, yPos:yPos});
			
			//position it
			templateActivity_mc._y = yPos;
			yPos += templateActivity_mc._height;
						
			
		}
		_toolkitLoaded = true;

		//toolkitLibraries_sp.refreshPane();
		
		
	}
	
	public function showToolTip(ttMsg:String, xpos:Number, ypos:Number, btnObj):Void{
		var ttHolder = ApplicationParent.tooltip;
		var ttMessage:String;
		var Xpos:Number;
		var Ypos:Number;
		if (btnObj != undefined){
			Xpos = Application.TOOLKIT_X+ libraryActivityDesc_txa._x;
			Ypos = Application.TOOLKIT_Y+ libraryActivityDesc_txa._y;
			ttMessage = title_lbl.text;
		}else {
			Xpos = Application.TOOLKIT_X+toolkitLibraries_sp._x + xpos
			Ypos = Application.TOOLKIT_Y+toolkitLibraries_sp._y + ypos
			ttMessage = ttMsg;
		}
		_tip.DisplayToolTip(ttHolder, ttMessage, Xpos, Ypos);
		
	}
	
	public function hideToolTip():Void{
		_tip.CloseToolTip();
	}

	/**
	*The currently selected Template Activity
	* 
	* @param   o   		The model object that is broadcasting an update.
	*/
	private function updateSelectedTemplateActivity(o:Observable):Void{
		//_global.breakpoint();
		//gett the model
		var tkm = ToolkitModel(o);
		//set the states of TKActs
		var l = tkm.getLastSelectedTemplateActivity();
		l.setState(false);
		var c = tkm.getSelectedTemplateActivity();
		c.setState(true);
		//Update the descripotion panel
		libraryActivityDesc_txa.text = "<p><b>"+c.toolActivity.title+"</b></p><p>"+c.toolActivity.description+"</p>";
		
		//set up the drag
		initDrag(c);
		
	}
	
	
	private function initDrag(selectedTA):Void{		
		//TODO: change myRoot to in application
		//dragIcon_mc = _root.createChildAtDepth('dummy_mc',DepthManager.kTop);
		
		//dragIcon_mc.loadMovie('http://dolly.uklams.net/lams/lams_central/icons/icon_chat.swf');
		
		//dragIcon_mc = _root.createObjectAtDepth("dummy_mc",DepthManager.kCursor);
		
		dragIcon_mc = Application.cursor.createEmptyMovieClip("dragIcon_mc",Application.cursor.getNextHighestDepth());

		//dragIcon_mc = _root.createObjectAtDepth("dummy_mc",DepthManager.kCursor);
		//dragIcon_mc = Application.root.createObjectAtDepth("dummy_mc",DepthManager.kCursor);
		
		//Debugger.log('dragIcon_mc:'+dragIcon_mc,4,'initDrag','ToolkitView');
		//TODO: Here we need to load the right icon.

		dragIcon_mcl.loadClip(Config.getInstance().serverUrl+selectedTA.toolActivity.libraryActivityUIImage,dragIcon_mc);
		//dragIcon_mc = _global.myRoot.duplicateMovieClip('dragIcon_mc',DepthManager.kTopmost);
		//Debugger.log('dragIcon_mc:'+dragIcon_mc,4,'initDrag','ToolkitView');
		
	}
	/*
	function onMouseUp(){
		//Debugger.log('hiya',4,'onMouseUp','TemplateActivity');
		Mouse.removeListener(this);
		//check if we are selected
		
		
		//TODO:hitTest against the canvas
		
	}
	*/
	
	private function setUpDrag(aDragIcon_mc):Void{
		//Debugger.log('aDragIcon_mc:'+aDragIcon_mc,4,'setUpDrag','TemplateActivity');
		//Debugger.log('this:'+this,4,'setUpDrag','TemplateActivity');
		Cursor.showCursor(Application.C_DEFAULT);
		dragIcon_mc = aDragIcon_mc;
		Application.getInstance().getCanvas().model.activeTool = null;
		//canvasModel = new CanvasModel(this);
		_dragging = true;
		Application.cursor.onMouseMove = Proxy.create(this,this['dragIcon']);
		Application.cursor.onMouseUp = Proxy.create(this,this['dropIcon']);
		/*
		Application.cursor.onMouseMove = function(){
			dragIcon_mc._visible = true;
			dragIcon_mc.startDrag(true);
			
			Mouse.addListener(this);
		}
		*/
		
		/*
		Application.cursor.onMouseUp = function(){
			Debugger.log('this:'+this,4,'dragIcon_mc.onRelease','TemplateActivity');
			broadcastToolkitDrop();
		}
		*/
		
	}
	
	private function dragIcon():Void{
		//Debugger.log('_dragging:'+_dragging,4,'dragIcon','TemplateActivity');
		if(_dragging){
			dragIcon_mc._visible = true;
			dragIcon_mc._x = Application.cursor._xmouse-(dragIcon_mc._width/2);
			dragIcon_mc._y = Application.cursor._ymouse-(dragIcon_mc._height/2);
		}
		
	}
	
	private function dropIcon():Void{
		_dragging = false;
		delete Application.cursor.onMouseMove;
		delete Application.cursor.onMouseUp;
		
		ToolkitController(getController()).iconDrop(dragIcon_mc);
		
		
		
	}
	
	private function setStyles():Void{
		var styleObj = _tm.getStyleObject('BGPanel');
		bkg_pnl.setStyle('styleName',styleObj);
		styleObj = _tm.getStyleObject('scrollpane');
		toolkitLibraries_sp.setStyle('styleName',styleObj);
		styleObj = _tm.getStyleObject('textarea');
		libraryActivityDesc_txa.setStyle('styleName',styleObj);
		styleObj = _tm.getStyleObject('label');
		title_lbl.setStyle('styleName',styleObj);
		
	}
	
	public function get className():String{
			return _className;
	}
	
	/**
	* Gets the ToolkitModel
	*
	* @returns model 
	*/
	public function getModel():ToolkitModel{
			return ToolkitModel(model);
	}
    /**
    * Returns the default controller for this view (ToolkitController).
	* Overrides AbstractView.defaultController()
    */
    public function defaultController (model:Observable):Controller {
        return new ToolkitController(model);
    }
}
