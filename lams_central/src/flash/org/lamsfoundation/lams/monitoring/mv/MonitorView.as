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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ************************************************************************
 */

import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.ui.*
import org.lamsfoundation.lams.common.style.*
import org.lamsfoundation.lams.monitoring.mv.*
import org.lamsfoundation.lams.monitoring.mv.tabviews.*;
import org.lamsfoundation.lams.monitoring.*;
import org.lamsfoundation.lams.common.dict.*
import org.lamsfoundation.lams.common.mvc.*
import mx.managers.*
import mx.containers.*;
import mx.events.*
import mx.utils.*
import mx.controls.*;
//import mx.controls.TabBar;


/**
*Monitoring view for the Monitor
* Relects changes in the MonitorModel
*/

class org.lamsfoundation.lams.monitoring.mv.MonitorView extends AbstractView{
	
	private var _className = "MonitorView";
	//constants:
	private var GRID_HEIGHT:Number;
	private var GRID_WIDTH:Number;
	private var H_GAP:Number;
	private var V_GAP:Number;
	private var Offset_Y_TabLayer_mc:Number;
	private var _tm:ThemeManager;
	
	private var _monitorView_mc:MovieClip;
	
	//Canvas clip
	private var _monitor_mc:MovieClip;
	private var monitor_scp:MovieClip;
	private var monitorTabs_tb:MovieClip;
	private var learnerMenuBar:MovieClip;
    private var bkg_pnl:MovieClip;
	//private var act_pnl:Panel;
	
    private var _gridLayer_mc:MovieClip;
    private var _lessonTabLayer_mc:MovieClip;
	private var _monitorTabLayer_mc:MovieClip;
	private var _learnerTabLayer_mc:MovieClip;
	private var _todoTabLayer_mc:MovieClip;
	private var refresh_btn:Button;
	private var help_btn:Button;
	//private var _activityLayerComplex_mc:MovieClip;
	//private var _activityLayer_mc:MovieClip;
	
	//private var _transitionPropertiesOK:Function;
    private var _monitorView:MonitorView;
	
	//Tab Views Initialisers
	//LessonTabView
	private var lessonTabView:LessonTabView;
	private var lessonTabView_mc:MovieClip;
	//MonitorTabView
	private var monitorTabView:MonitorTabView;
	private var monitorTabView_mc:MovieClip;
	//TodoTabView
	private var todoTabView:TodoTabView;
	private var todoTabView_mc:MovieClip;
	//LearnerTabView
	private var learnerTabView:LearnerTabView;
	private var learnerTabView_mc:MovieClip;
	
	private var _monitorController:MonitorController;
	
    //Defined so compiler can 'see' events added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
	//public var menu:ContextMenu;

	
	/**
	* Constructor
	*/
	function MonitorView(){
		_monitorView = this;
		_tm = ThemeManager.getInstance();
        //Init for event delegation
        mx.events.EventDispatcher.initialize(this);
	}
	
	/**
	* Called to initialise Canvas  . CAlled by the Canvas container
	*/
	public function init(m:Observable,c:Controller,x:Number,y:Number,w:Number,h:Number){
		//Invoke superconstructor, which sets up MVC relationships.
		//if(c==undefined){
		//	c==defaultController();
		//}
		super (m, c);
        //Set up parameters for the grid
		H_GAP = 10;
		V_GAP = 10;
		
		MovieClipUtils.doLater(Proxy.create(this,draw)); 
		
        //setupTabInit(m, c);
	   //register to recive updates form the model
		//MonitorModel(m).addEventListener('update',this);
        
		
    }    
	
	private function tabLoaded(evt:Object){
        Debugger.log('viewLoaded called',Debugger.GEN,'tabLoaded','MonitorView');
		
		if(evt.type=='load') {
            //dispatchEvent({type:'load',target:this});
        }else {
            //Raise error for unrecognized event
        }
    }
	
	/**
 * Recieved update events from the CanvasModel. Dispatches to relevent handler depending on update.Type
 * @usage   
 * @param   event
 */
public function update (o:Observable,infoObj:Object):Void{
		
       var mm:MonitorModel = MonitorModel(o);
	   _monitorController = getController();

	   switch (infoObj.updateType){
		   case 'SEQUENCE' :
				trace("TabID for Selected tab is: "+infoObj.tabID)
				//showData(mm);
				//removeActivity(infoObj.removeData, mm)
                break;
            case 'POSITION' :
				setPosition(mm);
                break;
            case 'SIZE' :
			    setSize(mm);
                break;
			case 'TABCHANGE' :
				showData(mm);
				break;
            default :
                Debugger.log('unknown update type :' + infoObj.updateType,Debugger.CRITICAL,'update','org.lamsfoundation.lams.MonitorView');
		}

	}
	
	/**
    * Sets the size of the canvas on stage, called from update
    */
	private function showData(mm:MonitorModel):Void{
        var s:Object = mm.getSequence();
		//for (var i in s){
			trace("Item Description is : "+s._learningDesignID);
			
		//}
		//monitor_scp.contentPath = lessonTabView;
					
	}
	
	/**
    * layout visual elements on the canvas on initialisation
    */
	private function draw(){
		
		var mcontroller = getController();
		//get the content path for the sp
		_monitor_mc = monitor_scp.content;
		//Debugger.log('_canvas_mc'+_canvas_mc,Debugger.GEN,'draw','CanvasView');
		
		trace("Height of learnerMenuBar: "+learnerMenuBar._height)
		//Offset_Y_TabLayer_mc = learnerMenuBar._height
		
		//_monitor_mc._y = Offset_Y_TabLayer_mc;
		_lessonTabLayer_mc = _monitor_mc.createEmptyMovieClip("_lessonTabLayer_mc", _monitor_mc.getNextHighestDepth());
		_monitorTabLayer_mc = _monitor_mc.createEmptyMovieClip("_monitorTabLayer_mc", _monitor_mc.getNextHighestDepth());
		_todoTabLayer_mc = _monitor_mc.createEmptyMovieClip("_todoTabLayer_mc", _monitor_mc.getNextHighestDepth());
		_learnerTabLayer_mc = _monitor_mc.createEmptyMovieClip("_learnerTabLayer_mc", _monitor_mc.getNextHighestDepth());
		//trace('lesson tab view: ' + _tabsLayer_mc);
		//bkg_pnl.useHandCursor = false;
	
		var tab_arr:Array = [{label:"Lesson", data:"lesson"}, {label:"Monitor", data:"monitor"}, {label:"Learners", data:"learners"}, {label:"Todo", data:"todo"}];
		
		monitorTabs_tb.dataProvider = tab_arr;
		monitorTabs_tb.selectedIndex = 0;
		
		//learnerMenuBar = this.attachMovie("RefreshBar", "RefreshBar", this.getNextHighestDepth(), {_x:-30})
		//learnerMenuBar = eval(learnerMenuBar)
		
		//learnerMenuBar.refresh_btn.addEventListener("click",mcontroller);
		refresh_btn.addEventListener("click",mcontroller);
		monitorTabs_tb.addEventListener("change",mcontroller);
		
		
		//setStyles();
		setupTabInit()
	    dispatchEvent({type:'load',target:this});
		
	}
	
	private function setupTabInit(){
		
		
		
		var mm:Observable = getModel();
		// Inititialsation for Lesson Tab View 
		lessonTabView_mc = _lessonTabLayer_mc.attachMovie("LessonTabView", "lessonTabView_mc",DepthManager.kTop)
		lessonTabView_mc._visible = false;
		lessonTabView = LessonTabView(lessonTabView_mc);
		lessonTabView.init(mm, undefined);
		lessonTabView.addEventListener('load',Proxy.create(this,tabLoaded));
			
		// Inititialsation for Monitor Tab View 
		monitorTabView_mc = _monitorTabLayer_mc.attachMovie("MonitorTabView", "monitorTabView_mc",DepthManager.kTop)
		monitorTabView_mc._visible = false;
		monitorTabView = MonitorTabView(monitorTabView_mc);
		monitorTabView.init(mm, undefined);
		monitorTabView.addEventListener('load',Proxy.create(this,tabLoaded));
		
		// Inititialsation for Learner Tab View 
		learnerTabView_mc = _learnerTabLayer_mc.attachMovie("LearnerTabView", "learnerTabView_mc",DepthManager.kTop)
		learnerTabView_mc._visible = false;
		learnerTabView = LearnerTabView(learnerTabView_mc);
		learnerTabView.init(mm, undefined);
		learnerTabView.addEventListener('load',Proxy.create(this,tabLoaded));
		
		// Inititialsation for Todo Tab View 
		todoTabView_mc = _todoTabLayer_mc.attachMovie("TodoTabView", "todoTabView_mc",DepthManager.kTop)
		todoTabView_mc._visible = false;
		todoTabView = TodoTabView(todoTabView_mc);
		todoTabView.init(mm, undefined);
		todoTabView.addEventListener('load',Proxy.create(this,tabLoaded));
		
		//Observers for All the Tab Views
		
		mm.addObserver(lessonTabView);
		mm.addObserver(monitorTabView);
		mm.addObserver(learnerTabView);
		mm.addObserver(todoTabView);
		
	}
	

	
	/**
	 * Event listener for when when tab is clicked
	 * 
	 * @usage   
	 * @param   evt 
	 * @return  
	 */
	//private function clickEvt(evt):Void{
		  // trace(evt.target);
		  // trace("test: "+ String(evt.target.selectedIndex))
					//forClick.text="label is: " + evt.itemIndex.label + " index is: " + evt.index + " capital is: " +               targetComp.dataProvider[evt.index].data;
		//}
	/**
    * Sets the size of the canvas on stage, called from update
    */
	private function setSize(mm:MonitorModel):Void{
        var s:Object = mm.getSize();
		trace("Monitor Tab Widtht: "+s.w+" Monitor Tab Height: "+s.h);
		monitor_scp.setSize(s.w,s.h);
		refresh_btn._x = s.w - 180
		help_btn._x = s.w - 80
				
	}
	
	 /**
    * Sets the position of the canvas on stage, called from update
    * @param cm Canvas model object 
    */
	private function setPosition(mm:MonitorModel):Void{
        var p:Object = mm.getPosition();
		trace("X pos set in Model is: "+p.x+" and Y pos set in Model is "+p.y)
        this._x = p.x;
        this._y = p.y;
	}
	
	public function getLessonTabView():LessonTabView{
		return lessonTabView;
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
	
	public function getMonitorTab():MovieClip{
		return monitorTabs_tb;
	}
	
	public function getMonitorScp():MovieClip{
		trace("Called getMonitorScp")
		return monitor_scp;
	}
	 /*
    * Returns the default controller for this view.
    */
    public function defaultController (model:Observable):Controller {
        return new MonitorController(model);
    }
	
}