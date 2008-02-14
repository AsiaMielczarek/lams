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


import org.lamsfoundation.lams.common.*
import org.lamsfoundation.lams.common.ws.*
import org.lamsfoundation.lams.common.ui.*
import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.dict.*
import org.lamsfoundation.lams.common.style.*

import org.lamsfoundation.lams.authoring.*
import org.lamsfoundation.lams.authoring.br.*

import mx.controls.*
import mx.controls.gridclasses.DataGridColumn;
import mx.utils.*
import mx.managers.*
import mx.events.*

/*
* Group Matching Dialog window for editing user preferences
* @author  DI
*/
class GroupNamingDialog extends MovieClip implements Dialog {

    //References to components + clips 
    private var _container:MovieClip;  //The container window that holds the dialog
    
	private var _groups:Array;
	private var _grouping:Grouping;
	private var _changedGroups:Hashtable;
	
    private var close_btn:Button;         // Close button
    
	private var instructions_lbl:Label;
	private var _group_naming_dgd:DataGrid;
	
    private var _bgpanel:MovieClip;       //The underlaying panel base
    
    private var fm:FocusManager;            //Reference to focus manager
    private var themeManager:ThemeManager;  //Theme manager
	
    //Dimensions for resizing
    private var xOkOffset:Number;
    private var yOkOffset:Number;
    private var xCancelOffset:Number;
    private var yCancelOffset:Number;

    //These are defined so that the compiler can 'see' the events that are added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
    
    /**
    * constructor
    */
    function GroupNamingDialog(){
        //Set up this class to use the Flash event delegation model
        EventDispatcher.initialize(this);
		
		_changedGroups = new Hashtable("_changedGroups");
		
        //Create a clip that will wait a frame before dispatching init to give components time to setup
        this.onEnterFrame = init;
    }

    /**
    * Called a frame after movie attached to allow components to initialise
    */
    private function init():Void{
        //Delete the enterframe dispatcher
        delete this.onEnterFrame;
        
        //set the reference to the StyleManager
        themeManager = ThemeManager.getInstance();
        
        //Set the text for buttons
        close_btn.label = Dictionary.getValue('al_ok');
        instructions_lbl.text = Dictionary.getValue('groupnaming_dialog_instructions_lbl');
		
        //get focus manager + set focus to OK button, focus manager is available to all components through getFocusManager
        fm = _container.getFocusManager();
        fm.enabled = true;
        
        //EVENTS
        //Add event listeners for ok, cancel and close buttons
        close_btn.addEventListener('click',Delegate.create(this, close));
		
		_group_naming_dgd.addEventListener('cellEdit', Delegate.create(this, itemEdited));
		_group_naming_dgd.addEventListener('cellFocusIn', Delegate.create(this, itemSelected));


		//Assign Click (close button) and resize handlers
        _container.addEventListener('click',this);
        _container.addEventListener('size',this);
        
        //work out offsets from bottom RHS of panel
        xOkOffset = _bgpanel._width - close_btn._x;
        yOkOffset = _bgpanel._height - close_btn._y;
        
        //Register as listener with StyleManager and set Styles
        themeManager.addEventListener('themeChanged',this);
        setStyles();
		
        //fire event to say we have loaded
		_container.contentLoaded();
	}
    
    /**
    * Event fired by StyleManager class to notify listeners that Theme has changed
    * it is up to listeners to then query Style Manager for relevant style info
    */
    public function themeChanged(evt:Object):Void{
        if(evt.type=='themeChanged') {
            //Theme has changed so update objects to reflect new styles
            setStyles();
        }else {
            Debugger.log('themeChanged event broadcast with an object.type not equal to "themeChanged"',Debugger.CRITICAL,'themeChanged','org.lamsfoundation.lams.WorkspaceDialog');
        }
    }
	
	/**
	 * Event fired by DataGrid class when an item has been edited i.e. its value changes
	 * 
	 * @usage   
	 * @param   evt 
	 * @return  
	 */
	
	public function itemEdited(evt:Object):Void {
		var group = _group_naming_dgd.getItemAt(evt.itemIndex);
		
		_changedGroups.put(group.groupUIID, group);
		
		Debugger.log("item changed: " + group.groupUIID, Debugger.CRITICAL, "itemChanged", "GroupNamingDialog");
	}
	
	public function itemSelected(evt:Object):Void {
		// TODO: Highlight the text to be changed
		var item = _group_naming_dgd.getItemAt(evt.itemIndex);
		Debugger.log("current selection: " + Selection.getFocus(), Debugger.CRITICAL, "itemSelected", "GroupNamingDialog");
	}
	
	public function setupGrid():Void {
		var column_name:DataGridColumn = new DataGridColumn("groupName");
		column_name.headerText = Dictionary.getValue("groupnaming_dialog_col_groupName_lbl");
		column_name.editable = true;
		
		_group_naming_dgd.editable = true;
		_group_naming_dgd.addColumn(column_name);
		
		for(var i=0; i < _groups.length; i++) {
			_group_naming_dgd.addItem(_groups[i]);
		}
	}
    
    /**
    * Called on initialisation and themeChanged event handler
    */
    private function setStyles(){
        //LFWindow, goes first to prevent being overwritten with inherited styles.
        var styleObj = themeManager.getStyleObject('LFWindow');
        _container.setStyle('styleName', styleObj);

        //Get the button style from the style manager and apply to both buttons
        styleObj = themeManager.getStyleObject('button');
        close_btn.setStyle('styleName', styleObj);
       
		styleObj = themeManager.getStyleObject('CanvasPanel');
		_bgpanel.setStyle('styleName', styleObj);
		
        //Apply label style 
        styleObj = themeManager.getStyleObject('label');
        instructions_lbl.setStyle('styleName', styleObj);
    }
	
    /**
    * Called by the OK button 
    */
    private function close(){
		_grouping.updateGroups(_changedGroups.values());
        _container.deletePopUp();
    }
	
    /**
    * Event dispatched by parent container when close button clicked
    */
    public function click(e:Object):Void{
        e.target.deletePopUp();
    }
    
    /**
    * Main resize method, called by scrollpane container/parent
    */
    public function setSize(w:Number,h:Number):Void{
		//Size the panel
        _bgpanel.setSize(w,h);

        //Buttons
        close_btn.move(w-xOkOffset,h-yOkOffset);
		
    }
    
    //Gets+Sets
    /**
    * set the container refernce to the window holding the dialog
    */
    function set container(value:MovieClip){
        _container = value;
    }
	
	public function set groups(a:Array){
		_groups = a;
	}

	public function set grouping(a:Grouping) {
		_grouping = a;
	}

}