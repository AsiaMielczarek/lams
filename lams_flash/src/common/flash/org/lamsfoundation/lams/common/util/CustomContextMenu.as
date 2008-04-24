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
 
import org.lamsfoundation.lams.authoring.Activity;
import org.lamsfoundation.lams.common.ApplicationParent;
import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.dict.*
/**
* DTO  Generic data transfer obj
*/
class CustomContextMenu {
	//Declarations
	//signifies update or errrr another type
	
	private var rootMenu:ContextMenu;
	private var authorMenu:ContextMenu;
	private var menuArr:Array;
	private var app:ApplicationParent;
	
	//ContextMenu instance is stored as a static in the CustomContextMenu class
    private static var _instance:CustomContextMenu = null;    
	
    //Defined so compiler can 'see' events added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
	
	//Constructor
	public function CustomContextMenu() {
		
		
		//To hide builtin menu for root
		rootMenu = new ContextMenu();
		rootMenu.hideBuiltInItems();
		
		app = ApplicationParent.getInstance();
		
		_root.menu = rootMenu;
		
		authorMenu = new ContextMenu();
		authorMenu.hideBuiltInItems();
		//To show hide menu for activities
		
		
		mx.events.EventDispatcher.initialize(this);
	}
	
	  /**
    * Retrieves an instance of the Application singleton
    */ 
    public static function getInstance():CustomContextMenu{
        if(CustomContextMenu._instance == null){
            CustomContextMenu._instance = new CustomContextMenu();
        }
		
        return CustomContextMenu._instance;
    }
	
	/**
	* Load the dictionary for the language specified
	* @param language (string) language parameter.
	*/
	public function loadMenu(cmType:String, moduleType:String):Array {
	
		var v:Boolean; 
		var monitorC:Boolean;
		var authorC:Boolean;
		
		var monitorCB:Boolean;
		
		var myCopy:Array = new Array();
		var menuArr:Array = new Array();
		
		if (cmType == "activity"){
			v = true;
		} else {
			v = false;
		}
		
		if (moduleType == "authoring"){
			authorC = true;
			monitorC = false
			
			monitorCB = false;
		}else{
			authorC = false;
			monitorC = true;
		
			monitorCB = (org.lamsfoundation.lams.monitoring.Application.getInstance().getMonitor().model.selectedItem.activity.activityTypeID == Activity.TOOL_BRANCHING_ACTIVITY_TYPE) ? true : false; 
		}
		
		menuArr[0] = [Dictionary.getValue('ccm_open_activitycontent'), getOpenEditActivityContent, false, v, authorC];
		menuArr[1] = [Dictionary.getValue('ccm_copy_activity'), getCopy, false, v, authorC];
		menuArr[2] = [Dictionary.getValue('ccm_monitor_activity'), MonitorActivityContent, false, v, monitorC];
		menuArr[3] = [Dictionary.getValue('ccm_monitor_view_mappings'), viewToolOutputConditions, false, true, monitorCB];
		menuArr[4] = [Dictionary.getValue('ccm_monitor_activityhelp'),getMonitorHelp, false, v, monitorC];
		menuArr[5] = [Dictionary.getValue('ccm_paste_activity'),getPaste, false, v, authorC];
		menuArr[6] = [Dictionary.getValue('ccm_pi'),getPI, true, true, authorC];
		menuArr[7] = [Dictionary.getValue('ccm_author_activityhelp'),getHelp, false, v, authorC];
		
		for (var i=0; i<menuArr.length; i++){
			var myObj:Object = new Object();
			myObj.cmlabel = menuArr[i][0];
			myObj.handler = menuArr[i][1]; 
			myObj.isSeparator = menuArr[i][2]; 
			myObj.isEnable= menuArr[i][3]; 
			myObj.isVisible = menuArr[i][4]; 
			
			myCopy[i]= myObj;			
		}
		
		return myCopy;
		
	}
		
	public function showCustomCM(cmItems:Object){
		for (var j=0; j<authorMenu.customItems.length; j++){
			authorMenu.customItems.splice(0)
		}
		for (var i=0; i<cmItems.length; i++){
			var menuItem_cmi:ContextMenuItem = new ContextMenuItem(cmItems[i].cmlabel.toString(), cmItems[i].handler, cmItems[i].isSeparator, cmItems[i].isEnable, cmItems[i].isVisible);
			authorMenu.customItems.push(menuItem_cmi);
		}
	
		_root.menu = authorMenu;
	}
	
	
	//---------------------------------
	
	public function getOpenEditActivityContent(){
		org.lamsfoundation.lams.authoring.Application.getInstance().openEditActivtiyContent();
	}
	
	public function MonitorActivityContent(){
		org.lamsfoundation.lams.monitoring.Application.getInstance().MonitorActivityContent();
		
	}
	
	public function viewToolOutputConditions() {
		org.lamsfoundation.lams.monitoring.Application.getInstance().getMonitor().viewToolOutputConditions();
	}
	
	public function getCopy(){
		org.lamsfoundation.lams.authoring.Application.getInstance().copy();
	}
	
	public function getPaste(){
		org.lamsfoundation.lams.authoring.Application.getInstance().paste();
	}
	
	public function getPI(){
		org.lamsfoundation.lams.authoring.Application.getInstance().expandPI();
		
	}	
	
	public function getMonitorHelp(){
		org.lamsfoundation.lams.monitoring.Application.getInstance().help();
	}
	
	public function getHelp(){
		org.lamsfoundation.lams.authoring.Application.getInstance().help();
	}
}
