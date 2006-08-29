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

import mx.controls.*
import mx.utils.*
import mx.managers.*
import mx.events.*
import org.lamsfoundation.lams.common.ws.*
import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.dict.*
import org.lamsfoundation.lams.common.style.*
import org.lamsfoundation.lams.common.ui.*
import it.sephiroth.TreeDnd;
/**
* @author      DI & DC
*/
class WorkspaceDialog extends MovieClip{
 
    //private static var OK_OFFSET:Number = 50;
    //private static var CANCEL_OFFSET:Number = 50;

    //References to components + clips 
    private var _container:MovieClip;       //The container window that holds the dialog
    private var ok_btn:Button;              //OK+Cancel buttons
    private var cancel_btn:Button;
    private var panel:MovieClip;            //The underlaying panel base
    
	private var switchView_tab:MovieClip;
	
	//location tab elements
	private var treeview:Tree;              //Treeview for navigation through workspace folder structure
	private var location_dnd:TreeDnd;
	private var input_txt:TextInput;
	private var currentPath_lbl:Label;
	private var name_lbl:Label;
	private var resourceTitle_txi:TextInput;
	private var new_btn:Button;
	//private var cut_btn:Button;
	private var copy_btn:Button;
	private var paste_btn:Button;
	private var delete_btn:Button;
	private var rename_btn:Button;
		
	
	//properties
	private var description_lbl:Label;
	private var license_lbl:Label;
	private var license_comment_lbl:Label;
    private var resourceDesc_txa:TextArea;
    private var license_txa:TextArea;
    private var licenseID_cmb:ComboBox;
    private var licenseImg_pnl:MovieClip;
    private var viewLicense_btn:Button;
	
	
	
    private var fm:FocusManager;            //Reference to focus manager
    private var themeManager:ThemeManager;  //Theme manager
	
	private var _workspaceView:WorkspaceView;
	private var _workspaceModel:WorkspaceModel;
	private var _workspaceController:WorkspaceController;

    
    //Dimensions for resizing
    private var xOkOffset:Number;
    private var yOkOffset:Number;
    private var xCancelOffset:Number;
    private var yCancelOffset:Number;
    
	private var _resultDTO:Object;			//This is an object to contain whatever the user has selected / set - will be passed back to the calling function
	

    private var _selectedDesignId:Number;
    private var _currentTab:Number;
	
	private static var OTHER_LICENSE_ID:Number = 6;
	private static var LOCATION_TAB:Number = 0;
	private static var PROP_TAB:Number = 1;
	
    //These are defined so that the compiler can 'see' the events that are added at runtime by EventDispatcher
    private var dispatchEvent:Function;     
    public var addEventListener:Function;
    public var removeEventListener:Function;
    
    
    /**
    * constructor
    */
    function WorkspaceDialog(){
        //trace('WorkSpaceDialog.constructor');
        //Set up this class to use the Flash event delegation model
        EventDispatcher.initialize(this);
        _resultDTO = new Object();
		this.tabEnabled = true
        //Create a clip that will wait a frame before dispatching init to give components time to setup
        this.onEnterFrame = init;
    }

    /**
    * Called a frame after movie attached to allow components to initialise
    */
	private function init(){
        //Delete the enterframe dispatcher
        delete this.onEnterFrame;
		//TODO: DC apply the themes here
        

        //set the reference to the StyleManager
        themeManager = ThemeManager.getInstance();
        
        //Set the container reference
        Debugger.log('container=' + _container,Debugger.GEN,'init','org.lamsfoundation.lams.WorkspaceDialog');

        //set up the tab bar:
		
		switchView_tab.addItem({label:Dictionary.getValue('ws_dlg_location_button'), data:'LOCATION'});
		switchView_tab.addItem({label:Dictionary.getValue('ws_dlg_properties_button'), data:'PROPERTIES'});

		
		//Set the text on the labels
        currentPath_lbl.text = "<b>"+Dictionary.getValue('ws_dlg_location_button')+"</b>:"
        license_lbl.text = Dictionary.getValue('ws_license_lbl');
		license_comment_lbl.text = Dictionary.getValue('ws_license_comment_lbl');
		
		name_lbl.text = Dictionary.getValue('ws_dlg_filename');
        
		//Set the text for buttons
		ok_btn.label = Dictionary.getValue('ws_dlg_ok_button');
        cancel_btn.label = Dictionary.getValue('ws_dlg_cancel_button');
		viewLicense_btn.label = Dictionary.getValue('ws_view_license_button');
		
		new_btn.label = Dictionary.getValue('new_btn');
		copy_btn.label = Dictionary.getValue('copy_btn');
		paste_btn.label = Dictionary.getValue('paste_btn');
		delete_btn.label = Dictionary.getValue('delete_btn');
		rename_btn.label = Dictionary.getValue('rename_btn');
		//TODO: Dictionary calls for all the rest of the buttons
		
		//TODO: Make setStyles more efficient
		setStyles();

        //get focus manager + set focus to OK button, focus manager is available to all components through getFocusManager
        fm = _container.getFocusManager();
        fm.enabled = true;
        ok_btn.setFocus();
        //fm.defaultPushButton = ok_btn;
        
        Debugger.log('ok_btn.tabIndex: '+ok_btn.tabIndex,Debugger.GEN,'init','org.lamsfoundation.lams.WorkspaceDialog');
        
       
        //Tie parent click event (generated on clicking close button) to this instance
        _container.addEventListener('click',this);
        //Register for LFWindow size events
        _container.addEventListener('size',this);
		
		//panel.setStyle('backgroundColor',0xFFFFFF);
        
        //Debugger.log('setting offsets',Debugger.GEN,'init','org.lamsfoundation.lams.common.ws.WorkspaceDialog');

        //work out offsets from bottom RHS of panel
        xOkOffset = panel._width - ok_btn._x;
        yOkOffset = panel._height - ok_btn._y;
        xCancelOffset = panel._width - cancel_btn._x;
        yCancelOffset = panel._height - cancel_btn._y;
        
        //Register as listener with StyleManager and set Styles
        themeManager.addEventListener('themeChanged',this);

        treeview = location_dnd.getTree();
		//Fire contentLoaded event, this is required by all dialogs so that creator of LFWindow can know content loaded
        resourceTitle_txi.setFocus();
		_container.contentLoaded();
		this.tabChildren = true;
		setTabIndex();
    }
	
	/**
	 * Called by the worspaceView after the content has loaded
	 * @usage   
	 * @return  
	 */
	public function setUpContent():Void{
		
		//register to recive updates form the model
		WorkspaceModel(_workspaceView.getModel()).addEventListener('viewUpdate',this);
		
		Debugger.log('_workspaceView:'+_workspaceView,Debugger.GEN,'setUpContent','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		//get a ref to the controller and kkep it here to listen for events:
		_workspaceController = _workspaceView.getController();
		Debugger.log('_workspaceController:'+_workspaceController,Debugger.GEN,'setUpContent','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		
		
		 //Add event listeners for ok, cancel and close buttons
        ok_btn.addEventListener('click',Delegate.create(this, ok));
        cancel_btn.addEventListener('click',Delegate.create(this, cancel));
		switchView_tab.addEventListener("change",Delegate.create(this, switchTab));
		//think this is failing....
		switchView_tab.setSelectedIndex(0); 
		
		new_btn.addEventListener('click',Delegate.create(_workspaceController, _workspaceController.fileOperationRequest));
		//cut_btn.addEventListener('click',Delegate.create(_workspaceController, _workspaceController.fileOperationRequest));
		copy_btn.addEventListener('click',Delegate.create(_workspaceController, _workspaceController.fileOperationRequest));
		paste_btn.addEventListener('click',Delegate.create(_workspaceController, _workspaceController.fileOperationRequest));
		delete_btn.addEventListener('click',Delegate.create(_workspaceController, _workspaceController.fileOperationRequest));
		rename_btn.addEventListener('click',Delegate.create(_workspaceController, _workspaceController.fileOperationRequest));
		
		viewLicense_btn.addEventListener('click',Delegate.create(this, openLicenseURL));
		licenseID_cmb.addEventListener('change',Delegate.create(this, onLicenseComboSelect));
		
		//Set up the treeview
        setUpTreeview();
		
		itemSelected(treeview.selectedNode);
		
	}
	
	
	/**
	 * Recieved update events from the WorkspaceModel. Dispatches to relevent handler depending on update.Type
	 * @usage   
	 * @param   event
	 */
	public function viewUpdate(event:Object):Void{
		Debugger.log('Recived an Event dispather UPDATE!, updateType:'+event.updateType+', target'+event.target,4,'viewUpdate','org.lamsfoundation.lams.ws.WorkspaceDialog');
		//Update view from info object
        //Debugger.log('Recived an UPDATE!, updateType:'+infoObj.updateType,4,'update','CanvasView');
       var wm:WorkspaceModel = event.target;
	   //set a permenent ref to the model for ease (sorry mvc guru)
	   _workspaceModel = wm;
	  
	   switch (event.updateType){
			case 'POPULATE_LICENSE_DETAILS' :
				populateAvailableLicenses(event.data, wm);
				break;
			case 'REFRESH_TREE' :
                refreshTree(wm);
                break;
			case 'UPDATE_CHILD_FOLDER' :
				updateChildFolderBranches(event.data,wm);
				openFolder(event.data, wm);
				break;
			case 'UPDATE_CHILD_FOLDER_NOOPEN' :
				updateChildFolderBranches(event.data,wm);
				break;
			case 'ITEM_SELECTED' :
				itemSelected(event.data,wm);
				break;
			case 'OPEN_FOLDER' :
				openFolder(event.data, wm, false);
				break;
			case 'CLOSE_FOLDER' :
				closeFolder(event.data, wm);
				break;
			case 'REFRESH_FOLDER' :
				refreshFolder(event.data, wm);
				break;
			case 'SHOW_TAB' :
				showTab(event.data,wm);
				break;
			case 'UPDATED_PROP_DETAILS' :
				populatePropDetails(event.data, wm);
				break;
			case 'SET_UP_BRANCHES_INIT' :
				setUpBranchesInit();
				break;
				
            default :
                Debugger.log('unknown update type :' + event.updateType,Debugger.GEN,'viewUpdate','org.lamsfoundation.lams.ws.WorkspaceDialog');
		}

	}
	
	
	
	/**
	 * called witht he result when a child folder is opened..
	 * updates the tree branch satus, then refreshes.
	 * @usage   
	 * @param   changedNode 
	 * @param   wm          
	 * @return  
	 */
	private function updateChildFolderBranches(changedNode:XMLNode,wm:WorkspaceModel){
		 Debugger.log('updateChildFolder....:' ,Debugger.GEN,'updateChildFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		 //we have to set the new nodes to be branches, if they are branches
		if(changedNode.attributes.isBranch){
			treeview.setIsBranch(changedNode,true);
			//do its kids
			for(var i=0; i<changedNode.childNodes.length; i++){
				var cNode:XMLNode = changedNode.getTreeNodeAt(i);
				if(cNode.attributes.isBranch){
					treeview.setIsBranch(cNode,true);
				}
			}
		}
	}
	
	private function refreshTree(){
		 Debugger.log('Refreshing tree....:' ,Debugger.GEN,'refreshTree','org.lamsfoundation.lams.ws.WorkspaceDialog');
		
		
		 treeview.refresh();// this is USELESS

		//var oBackupDP = treeview.dataProvider;
		//treeview.dataProvider = null; // clear
		//treeview.dataProvider = oBackupDP;
		
		//treeview.setIsOpen(treeview.getNodeDisplayedAt(0),false);
		//treeview.setIsOpen(treeview.getNodeDisplayedAt(0),true);

	}
	
	/**
	 * Just opens the fodler node - DOES NOT FIRE EVENT - so is used after updatting the child folder
	 * @usage   
	 * @param   nodeToOpen 
	 * @param   wm       
	 * @param 	isForced
	 * @return  
	 */
	private function openFolder(nodeToOpen:XMLNode, wm:WorkspaceModel){
		Debugger.log('openFolder:'+nodeToOpen ,Debugger.GEN,'openFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		//open the node
		nodeToOpen.attributes.isOpen = true;
		treeview.setIsOpen(nodeToOpen,true);
		
		Debugger.log('openFolder forced:'+wm.isForced() ,Debugger.GEN,'openFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		
		if(wm.isForced() && nodeToOpen.attributes.data.resourceID == WorkspaceModel.ROOT_VFOLDER){
			// select users root workspace folder
			treeview.selectedNode = nodeToOpen.firstChild;
			dispatchEvent({type:'change', target:this.treeview});
			
			var virNode:XMLNode = nodeToOpen.firstChild.nextSibling;
			if(virNode.attributes.data.resourceID == WorkspaceModel.ORG_VFOLDER && !treeview.getIsOpen(virNode)){
				openFolder(virNode, wm);
			}
		}
		
		refreshTree();

	}
	
	/**
	 * Closes the folder node
	 * 
	 * @usage   
	 * @param   nodeToClose 
	 * @param   wm          
	 * @return  
	 */
	
	private function closeFolder(nodeToClose:XMLNode, wm:WorkspaceModel){
		Debugger.log('closeFolder:'+nodeToClose ,Debugger.GEN,'closeFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		
		// close the node
		nodeToClose.attributes.isOpen = false;
		treeview.setIsOpen(nodeToClose, false);
		
		refreshTree();
	}
	
	/**
	 * Closes folder, then sends openEvent to controller
	 * @usage   
	 * @param   nodeToOpen 
	 * @param   wm         
	 * @return  
	 */
	private function refreshFolder(nodeToOpen:XMLNode, wm:WorkspaceModel){		Debugger.log('refreshFolder:'+nodeToOpen ,Debugger.GEN,'refreshFolder','org.lamsfoundation.lams.ws.WorkspaceDialog');
		//close the node
		treeview.setIsOpen(nodeToOpen,false);		
		//we are gonna need to fire the event manually for some stupid reason the tree is not firing it.
		//dispatchEvent({type:'nodeOpen',target:treeview,node:nodeToOpen});
		_workspaceController = _workspaceView.getController();
		_workspaceController.onTreeNodeOpen({type:'nodeOpen',target:treeview,node:nodeToOpen});
	}
	
	
	private function itemSelected(newSelectedNode:XMLNode,wm:WorkspaceModel){
		//update the UI with the new info:
		//_global.breakpoint();

		//Only update the details if the node if its a resource:a
		var nodeData = newSelectedNode.attributes.data;
				
		if(nodeData.resourceType == _workspaceModel.RT_FOLDER){
			
			resourceTitle_txi.text = "";
			
			if(wm.currentMode != Workspace.MODE_SAVEAS || wm.currentMode != Workspace.MODE_SAVE){	
				resourceDesc_txa.text = "";
				license_txa.text = "";
				licenseID_cmb.selectedIndex = 0;
			}
			
		}else{
			
			resourceTitle_txi.text = nodeData.name;
			if(StringUtils.isNull(nodeData.description)){
				resourceDesc_txa.text = "";
			} else {
				resourceDesc_txa.text = nodeData.description;
			}
			
			Debugger.log('nodeData.licenseID:'+nodeData.licenseID,Debugger.GEN,'itemSelected','org.lamsfoundation.lams.ws.WorkspaceDialog');
			//find the SI of the license we need:
			//check if a license ID has been selected:
			if(nodeData.licenseID > 0){
				for(var i=0; i<licenseID_cmb.dataProvider.length; i++){
					if(licenseID_cmb.dataProvider[i].data.licenseID == nodeData.licenseID){
						licenseID_cmb.selectedIndex = i;
					}
				}
				onLicenseComboSelect();
				
			}else{
				licenseID_cmb.selectedIndex = 0;
			}
			
			if(StringUtils.isNull(nodeData.licenseText)){
				license_txa.text = "";
			} else {
				license_txa.text = nodeData.licenseText;
			}
		
		}
		
	}
	
	private function populatePropDetails(details:Object){
		Debugger.log('Populating properties details:'+details,Debugger.GEN,'populatePropDetails','org.lamsfoundation.lams.ws.WorkspaceDialog');
		
		if(details == null){
			Debugger.log('Error - empty details:'+details,Debugger.GEN,'populatePropDetails','org.lamsfoundation.lams.ws.WorkspaceDialog');
		
		}
		
		if(StringUtils.isNull(resourceDesc_txa.text)){
			resourceDesc_txa.text = "";
		} else {
			resourceDesc_txa.text = details.description;
		}
			
		if(details.licenseID > 0){
			
			for(var i=0; i<licenseID_cmb.dataProvider.length; i++){
				if(licenseID_cmb.dataProvider[i].data.licenseID == details.licenseID){
					licenseID_cmb.selectedIndex = i;
				}
			}
			onLicenseComboSelect();
		
		}else{
			licenseID_cmb.selectedIndex = 0;
		}
			
		if(details.licenseText == undefined){
			license_txa.text = "";
		} else {
			license_txa.text = details.licenseText;
		}
		
	}
	
	private function populateAvailableLicenses(licenses:Array, wm:WorkspaceModel){
		Debugger.log('Got this many:'+licenses.length,Debugger.GEN,'populateAvailableLicenses','org.lamsfoundation.lams.ws.WorkspaceDialog');
		//add the blank one
		
		var _licenses = licenses;
		_licenses.sortOn("licenseID", Array.NUMERIC);
		var lic_dp = new Array();
		lic_dp.addItem({label:Dictionary.getValue('license_not_selected'),data:""});
		licenseID_cmb.dataProvider = lic_dp;
		for (var i=0;i<_licenses.length;i++){
			lic_dp.addItem({label:_licenses[i].name,data:_licenses[i]});
		}
		
	}
	
	public function onLicenseComboSelect(evt:Object){
		
		//load the picture into the panel
		licenseImg_pnl.createEmptyMovieClip("image_mc", this.getNextHighestDepth());
		licenseImg_pnl.image_mc.loadMovie(licenseID_cmb.value.pictureURL);

		//license_txa.text = StringUtils.cleanNull(evt.target.data.
		
		if(licenseID_cmb.value.url == undefined){
			viewLicense_btn.enabled = false;
		}else{
			viewLicense_btn.enabled = true;
		}
		
		// if Other Licensing Agreement is selected then show the textarea box for addition comments
		if((licenseID_cmb.value.licenseID == OTHER_LICENSE_ID) && (_currentTab == PROP_TAB)){
			license_txa.visible = true;
			license_comment_lbl.visible = true;
		} else {
			license_txa.visible = false;
			license_comment_lbl.visible = false;
		}
	}
		
	public function openLicenseURL(evt:Object){
		var urlToOpen:String = licenseID_cmb.value.url;
		if(urlToOpen != undefined){
			getURL(urlToOpen,'_blank');
		}
	}
	
	
	
	private function setLocationContentVisible(v:Boolean){
		Debugger.log('v:'+v,Debugger.GEN,'setLocationContentVisible','org.lamsfoundation.lams.ws.WorkspaceDialog');
		treeview.visible = v;
		input_txt.visible = v;
		currentPath_lbl.visible = v;
		name_lbl.visible = v;
		resourceTitle_txi.visible = v;
		new_btn.visible = v;
		//cut_btn.visible = v;
		copy_btn.visible = v;
		paste_btn.visible = v;
		delete_btn.visible = v;
		rename_btn.visible = v;
	
	}
	
	private function setPropertiesContentVisible(v:Boolean){
		Debugger.log('v:'+v,Debugger.GEN,'setPropertiesContentVisible','org.lamsfoundation.lams.ws.WorkspaceDialog');
		description_lbl.visible = v;
		license_lbl.visible = v;
		resourceDesc_txa.visible = v;
		licenseImg_pnl.visible = v;
		viewLicense_btn.visible = v;
		licenseID_cmb.visible = v;
		if(licenseID_cmb.value.licenseID == OTHER_LICENSE_ID) {
			license_comment_lbl.visible = v;
			license_txa.visible = v;
		} else {
			license_comment_lbl.visible = false;
			license_txa.visible = false;
		}

	}
	
		
	/**
	 * updates the view to show the right controls for the tab
	 * @usage   
	 * @param   tabToSelect 
	 * @param   wm          
	 * @return  
	 */
	private function showTab(tabToSelect:String,wm:WorkspaceModel){
		Debugger.log('tabToSelect:'+tabToSelect,Debugger.GEN,'showTab','org.lamsfoundation.lams.ws.WorkspaceDialog');
		if(tabToSelect == "LOCATION"){
			setLocationContentVisible(true);
			setPropertiesContentVisible(false);
			//setTabIndex("LOCATION");
			_currentTab = LOCATION_TAB;
						
		}else if(tabToSelect == "PROPERTIES"){
			setLocationContentVisible(false);
			setPropertiesContentVisible(true);
			//setTabIndex("PROPERTIES");
			_currentTab = PROP_TAB;
		}
		
		//set the right label on the 'doit' button
		if(wm.currentMode=="OPEN"){
			ok_btn.label = Dictionary.getValue('ws_dlg_open_btn');
		}else if(wm.currentMode=="SAVE" || wm.currentMode=="SAVEAS"){
			ok_btn.label = Dictionary.getValue('ws_dlg_save_btn');
		}else if(wm.currentMode=="READONLY"){
			ok_btn.label = "Create" //Dictionary.getValue('ws_dlg_create_btn');
		}else{
			Debugger.log('Dont know what mode the Workspace is in!',Debugger.CRITICAL,'showTab','org.lamsfoundation.lams.ws.WorkspaceDialog');
			ok_btn.label = Dictionary.getValue('ws_dlg_ok_btn');
		}
		
	}
	
	private function setTabIndex(selectedTab:String){
		
		ok_btn.tabIndex = 25
        cancel_btn.tabIndex = 26
		switchView_tab.tabIndex = 1
		//think this is failing....
		//if(tabToSelect == "LOCATION"){
			new_btn.tabIndex = 2
			//cut_btn.addEventListener('click',Delegate.create(_workspaceController, _workspaceController.fileOperationRequest));
			copy_btn.tabIndex = 3
			paste_btn.tabIndex = 4
			delete_btn.tabIndex = 5
			rename_btn.tabIndex = 6
			location_dnd.tabIndex = 7
			treeview.tabIndex = 7
		//}else {
			resourceDesc_txa.tabIndex = 8
			licenseID_cmb.tabIndex = 9
			viewLicense_btn.tabIndex = 10
		//}
		
	}
    
    /**
    * Event fired by StyleManager class to notify listeners that Theme has changed
    * it is up to listeners to then query Style Manager for relevant style info
    */
    public function themeChanged(event:Object){
        if(event.type=='themeChanged') {
            //Theme has changed so update objects to reflect new styles
            setStyles();
        }else {
            Debugger.log('themeChanged event broadcast with an object.type not equal to "themeChanged"',Debugger.CRITICAL,'themeChanged','org.lamsfoundation.lams.WorkspaceDialog');
        }
    }
    
    /**
    * Called on initialisation and themeChanged event handler
    */
    private function setStyles(){
        //LFWindow, goes first to prevent being overwritten with inherited styles.
        //var styleObj = themeManager.getStyleObject('LFWindow');
        //_container.setStyle('styleName',styleObj);

        //Get the button style from the style manager
       // styleObj = themeManager.getStyleObject('button');
        
        //apply to both buttons
       // Debugger.log('styleObject : ' + styleObj,Debugger.GEN,'setStyles','org.lamsfoundation.lams.WorkspaceDialog');
       // ok_btn.setStyle('styleName',styleObj);
        //cancel_btn.setStyle('styleName',styleObj);
        
        //Get label style and apply to label
       var styleObj = themeManager.getStyleObject('label');
        name_lbl.setStyle('styleName',styleObj);

        //Apply treeview style 
       // styleObj = themeManager.getStyleObject('treeview');
        //treeview.setStyle('styleName',styleObj);

        //Apply datagrid style 
      //  styleObj = themeManager.getStyleObject('datagrid');
        //datagrid.setStyle('styleName',styleObj);

/*
        //Apply combo style 
        styleObj = themeManager.getStyleObject('combo');
        combo.setStyle('styleName',styleObj);
  */
  }

    /**
    * Called by the cancel button 
    */
    private function cancel(){
        trace('Cancel');
        //close parent window
        _container.deletePopUp();
    }
    
   /**
    * Called by the OK button
	* Dispatches the okClicked event and passes a result DTO containing:
	* <code>
	*	_resultDTO.selectedResourceID 	//The ID of the resource that was selected when the dialogue closed
	*	_resultDTO.resourceName 		//The contents of the Name text field
	*	_resultDTO.resourceDescription 	//The contents of the description field on the propertirs tab
	*	_resultDTO.resourceLicenseText 	//The contents of the license text field
	*	_resultDTO.resourceLicenseID 	//The ID of the selected license from the drop down.
    *</code>
	*/
    private function ok(){
        trace('OK');
		_global.breakpoint();
	
		//TODO: Validate you are allowed to use the name etc... Are you overwriting - NOTE Same names are nto allowed in this version
		
		var snode = treeview.selectedNode;
		
		if(isVirtualFolder(snode)){
			LFMessage.showMessageAlert(Dictionary.getValue('ws_click_virtual_folder'),null);
			return;
		}
		
		Debugger.log('_workspaceModel.currentMode: ' + _workspaceModel.currentMode,Debugger.GEN,'ok','org.lamsfoundation.lams.WorkspaceDialog');
		var tempTitle = StringUtils.replace(resourceTitle_txi.text, " ", "");
		if (tempTitle == "" || tempTitle == undefined){
			var sendMsg:String = Dictionary.getValue('ws_file_name_empty')+"\n"+Dictionary.getValue('ws_entre_file_name')+"\n\n";
			LFMessage.showMessageAlert(sendMsg,null);
			resourceTitle_txi.setFocus();
		}else{
			if(_workspaceModel.currentMode=="SAVE" || _workspaceModel.currentMode=="SAVEAS"){
				saveFile(snode);
			} else {
				openFile(snode);
			}
		}
			
    }
	
	/**
	 * Open file from Workspace
	 * 
	 * @param   snode file to open or folder to open from
	 */
	
	private function openFile(snode:XMLNode):Void{
		Debugger.log('Opening a file.',Debugger.GEN,'openFile','org.lamsfoundation.lams.WorkspaceDialog');
		if (snode.attributes.data.resourceType==_workspaceModel.RT_FOLDER){
			if(resourceTitle_txi.text == null){
				LFMessage.showMessageAlert(Dictionary.getValue('ws_click_file_open'),null);
			} else {
				if(!searchForFile(snode, resourceTitle_txi.text)){
					LFMessage.showMessageAlert(Dictionary.getValue('ws_no_file_open'),null);
				}
			}
		} else {
				doWorkspaceDispatch(true);
		}
	}
	
	/**
	 * Save file to Workspace
	 *   
	 * @param   snode folder to save to
	 */
	
	private function saveFile(snode:XMLNode):Void{
		Debugger.log('Saving a file.',Debugger.GEN,'saveFile','org.lamsfoundation.lams.WorkspaceDialog');
		if(snode == treeview.dataProvider.firstChild){
			LFMessage.showMessageAlert(Dictionary.getValue('ws_save_folder_invalid'),null);
		} else if(snode.attributes.data.resourceType==_workspaceModel.RT_LD){
			//run a confirm dialogue as user is about to overwrite a design!
			LFMessage.showMessageConfirm(Dictionary.getValue('ws_chk_overwrite_resource'), Proxy.create(this,doWorkspaceDispatch,true), Proxy.create(this,closeThisDialogue));
		} else if(snode.attributes.data.resourceType==_workspaceModel.RT_FOLDER){
			if(snode.attributes.data.resourceID < 0){	
				LFMessage.showMessageAlert(Dictionary.getValue('ws_save_folder_invalid'),null);
			} else if(searchForFile(snode, resourceTitle_txi.text)){
				//run a alert dialogue as user is using the same name as an existing design!
				LFMessage.showMessageAlert(Dictionary.getValue('ws_chk_overwrite_existing', [resourceTitle_txi.text]), null);
			}
		} else {
			LFMessage.showMessageAlert(Dictionary.getValue('ws_click_folder_file'),null);
		}
	}
	
	private function searchForFile(snode:XMLNode, filename:String){
		Debugger.log('Searching for file (' + snode.childNodes.length + '): ' + filename,Debugger.GEN,'openFile','org.lamsfoundation.lams.WorkspaceDialog');
		var cnode:XMLNode;
		
		if(snode.hasChildNodes() || snode.attributes.isEmpty){
		
			cnode = snode.firstChild;
			do {
				Debugger.log('matching file: ' + cnode.attributes.data.name + ' to: ' + filename,Debugger.GEN,'openFile','org.lamsfoundation.lams.WorkspaceDialog');
				
				// look for matching Learning Design file in the folder 
				var _filename:String = cnode.attributes.data.name;
				var _filetype:String = cnode.attributes.data.resourceType;
				if(_filename == filename && _filetype == _workspaceModel.RT_LD){
					if(_workspaceModel.currentMode == Workspace.MODE_OPEN){
						treeview.selectedNode = null;
						_resultDTO.file = cnode;
						doWorkspaceDispatch(true);
					}
					
					return true;
				}
				
				cnode = cnode.nextSibling;
			
			} while(cnode != null);
			
			if(_workspaceModel.currentMode == Workspace.MODE_SAVE || _workspaceModel.currentMode == Workspace.MODE_SAVEAS){
				doWorkspaceDispatch(false);
			}
			return false;
			
		} else {
			// save filename value
			_resultDTO.resourceName = filename;
			
			// get folder contents
			var callback:Function = Proxy.create(this,receivedFolderContents);
			_workspaceModel.getWorkspace().requestFolderContents(snode.attributes.data.resourceID, callback);
			
			if(_workspaceModel.currentMode == Workspace.MODE_SAVE || _workspaceModel.currentMode == Workspace.MODE_SAVEAS){
				return false;
			} else {
				return true;
			}
		}
	}
	
	
	public function receivedFolderContents(dto:Object){
		_workspaceModel.setFolderContents(dto, false);
		
		if(_workspaceModel.getWorkspaceResource('Folder_'+dto.workspaceFolderID)!=null){
			if(_workspaceModel.currentMode == Workspace.MODE_SAVE || _workspaceModel.currentMode == Workspace.MODE_SAVEAS){
				if(searchForFile(_workspaceModel.getWorkspaceResource('Folder_'+dto.workspaceFolderID), _resultDTO.resourceName)){
					//run a alert dialogue as user is using the same name as an existing design!
					LFMessage.showMessageAlert(Dictionary.getValue('ws_chk_overwrite_existing', [_resultDTO.resourceName]), null);
				}
			} else {
				if(!searchForFile(_workspaceModel.getWorkspaceResource('Folder_'+dto.workspaceFolderID), _resultDTO.resourceName)){
					LFMessage.showMessageAlert(Dictionary.getValue('ws_no_file_open'),null);
				}
			}
		}
	}
	
	/**
	 * Check if the folder node against a resource ID
	 * 
	 * @usage   
	 * @param   snode      
	 * @param   resourceID  
	 * @return  
	 */
	
	private function isVirtualFolder(folder:XMLNode){
		return folder.attributes.data.resourceID < 0;
	}
	
	/**
	 * Dispatches an event - picked up by the canvas in authoring
	 * sends paramter containing:
	 * _resultDTO.selectedResourceID 
	 * _resultDTO.targetWorkspaceFolderID
	 * 	_resultDTO.resourceName 
		_resultDTO.resourceDescription 
		_resultDTO.resourceLicenseText 
		_resultDTO.resourceLicenseID 
	 * @usage   
	 * @param   useResourceID //if its true then we will send the resorceID of teh item selected in the tree - usually this means we are overwriting something
	 * @return  
	 */
	public function doWorkspaceDispatch(useResourceID:Boolean){
		//ObjectUtils.printObject();
		
		var snode = treeview.selectedNode;		// item selected in tree
		
		if(snode == null){
			// set to file item found in search
			snode = _resultDTO.file;
		}
		
		if(useResourceID){
			//its an LD
			_resultDTO.selectedResourceID = Number(snode.attributes.data.resourceID);
			_resultDTO.targetWorkspaceFolderID = Number(snode.attributes.data.workspaceFolderID);
		}else{
			//its a folder
			_resultDTO.selectedResourceID  = null;
			_resultDTO.targetWorkspaceFolderID = Number(snode.attributes.data.resourceID);
			
		}
		
		_resultDTO.resourceName = resourceTitle_txi.text;
		_resultDTO.resourceDescription = resourceDesc_txa.text;
		_resultDTO.resourceLicenseText = license_txa.text;
		_resultDTO.resourceLicenseID = licenseID_cmb.value.licenseID;

        dispatchEvent({type:'okClicked',target:this});
	   
        closeThisDialogue();
		
	}
	
	public function closeThisDialogue(){
		 _container.deletePopUp();
	}

	
	/**
	 * Called when the tabs are clicked
	 * @usage   
	 * @param   e 
	 * @return  
	 */
	private function switchTab(e){
		Debugger.log('Switch tab called!',Debugger.GEN,'switchTab','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		if(e.newIndex == 0){			
			dispatchEvent({type:'locationTabClick',target:this});
		}else if(e.newIndex ==1){
			dispatchEvent({type:'propertiesTabClick',target:this});
		}
		/*
		for (var item:String in e) {	
			trace("Item: " + item + "=" + e[item]);
		}
		*/
	}
    
    /**
    * Event dispatched by parent container when close button clicked
    */
    private function click(e:Object){
        trace('WorkspaceDialog.click');
        e.target.deletePopUp();
    }
	

	/**
	 * Recursive function to set any folder with children to be a branch
	 * TODO: Might / will have to change this behaviour once designs are being returned into the mix
	 * @usage   
	 * @param   node 
	 * @return  
	 */
    private function setBranches(node:XMLNode){
		if(node.hasChildNodes() || node.attributes.isBranch){
			treeview.setIsBranch(node, true);
			for (var i = 0; i<node.childNodes.length; i++) {
				var cNode = node.getTreeNodeAt(i);
				setBranches(cNode);
				/*
				if(cNode.hasChildNodes()){
					treeview.setIsBranch(cNode, true);
					setBranches(cNode);
				}
				*/
				
			}
		}
	}
	

	/**
	 * Sets up the inital branch detials
	 * @usage   
	 * @return  
	 */
	private function setUpBranchesInit(){
		Debugger.log('Running...',Debugger.GEN,'setUpBranchesInit','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		//get the 1st child
		treeview.dataProvider = WorkspaceModel(_workspaceView.getModel()).treeDP;
		var fNode = treeview.dataProvider.firstChild;
		setBranches(fNode);
		treeview.refresh();
	}
	
	
	/**
	 * Sets up the treeview with whatever datya is in the treeDP
	 * TODO - extend this to make it recurse all the way down the tree
	 * @usage   
	 * @return  
	 */
	private function setUpTreeview(){
			
		//Debugger.log('_workspaceView:'+_workspaceView,Debugger.GEN,'setUpTreeview','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		
		setUpBranchesInit();
		Debugger.log('WorkspaceModel(_workspaceView.getModel()).treeDP:'+WorkspaceModel(_workspaceView.getModel()).treeDP.toString(),Debugger.GEN,'setUpTreeview','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		
		//Debugger.log('_workspaceController.onTreeNodeOpen:'+_workspaceController.onTreeNodeOpen,Debugger.GEN,'setUpTreeview','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
		
		
		
		treeview.addEventListener("nodeOpen", Delegate.create(_workspaceController, _workspaceController.onTreeNodeOpen));
		treeview.addEventListener("nodeClose", Delegate.create(_workspaceController, _workspaceController.onTreeNodeClose));
		treeview.addEventListener("change", Delegate.create(_workspaceController, _workspaceController.onTreeNodeChange));
		
		//location_dnd.addEventListener('double_click', dndList);
		//location_dnd.addEventListener('drag_start', dndList);
		//location_dnd.addEventListener('drag_fail', dndList);
		
		//location_dnd.addEventListener('drag_target', dndList);
		location_dnd.addEventListener("drag_complete", Delegate.create(_workspaceController, _workspaceController.onDragComplete));
		//location_dnd.addEventListener('drag_complete', dndList);
		//use the above event, on comlete the drop, send the request to do the move to the server (evt.targetNode);
		//then immediatly invlaidate the cache.  then server may return error if therrte is a problem, else new details willbe shown
		
		// open My Workspace virtual folder
		var wsNode:XMLNode = treeview.firstVisibleNode;
		//treeview.setIsOpen(wsNode, true);
		_workspaceController.forceNodeOpen(wsNode);
		
    }
    
    /**
    * XML onLoad handler for treeview data
 */
    private function tvXMLLoaded (ok:Boolean,rootXML:XML){
        if(ok){
            /*
			//Set the XML as the data provider for the tree
            treeview.dataProvider = rootXML.firstChild;
            treeview.addEventListener("change", Delegate.create(this, onTvChange));
            
            //Add this function to prevent displaying [type function],[type function] when label attribute missing from XML
            treeview.labelFunction = function(node) {
                    return node.nodeType == 1 ? node.nodeName : node.nodeValue;
            };
            */
        }
    }
    
     
    /**
    * Main resize method, called by scrollpane container/parent
    */
    public function setSize(w:Number,h:Number){
        //Debugger.log('setSize',Debugger.GEN,'setSize','org.lamsfoundation.lams.common.ws.WorkspaceDialog');
        //Size the panel
        panel.setSize(w,h);

        //Buttons
        ok_btn.move(w-xOkOffset,h-yOkOffset);
        cancel_btn.move(w-xCancelOffset,h-yCancelOffset);
    }
    
    //Gets+Sets
    /**
    * set the container refernce to the window holding the dialog
    */
    function set container(value:MovieClip){
        _container = value;
    }
	
	/**
	 * 
	 * @usage   
	 * @param   newworkspaceView 
	 * @return  
	 */
	public function set workspaceView (newworkspaceView:WorkspaceView):Void {
		_workspaceView = newworkspaceView;
	}
	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function get workspaceView ():WorkspaceView {
		return _workspaceView;
	}
	
    
    function get selectedDesignId():Number { 
        return _selectedDesignId;
    }
	
	
	/**
	 * 
	 * @usage   
	 * @return  
	 */
	public function get resultDTO():Object {
		return _resultDTO;
	}
}