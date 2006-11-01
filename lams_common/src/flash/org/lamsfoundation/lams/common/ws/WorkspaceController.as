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

import org.lamsfoundation.lams.authoring.Application;
import org.lamsfoundation.lams.common.ws.*
import org.lamsfoundation.lams.common.mvc.*
import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.ui.*
import org.lamsfoundation.lams.common.dict.*
import mx.utils.*

/*
* Makes changes to the model's data based on user input.
*/
class org.lamsfoundation.lams.common.ws.WorkspaceController extends AbstractController {
	
	private var _workspaceModel:WorkspaceModel;
	private var _workspaceController:WorkspaceController;
	private var _isBusy:Boolean;
	/**
	* Constructor
	*
	* @param   cm   The model to modify.
	*/
	public function WorkspaceController (wm:Observable) {
		super (wm);
		_workspaceController = this;
		_workspaceModel = WorkspaceModel(wm);
		_isBusy = false;
	}
   
   
   

	/**
	 * called when the dialog is loaded, calles methods to set up content in dialogue
	 * also sets up the okClicked event listener
	 * @usage   
	 * @param   evt 
	 * @return  
	 */
    public function openDialogLoaded(evt:Object) {
        Debugger.log('!evt.type:'+evt.type,Debugger.GEN,'openDialogLoaded','org.lamsfoundation.lams.WorkspaceController');
        //Check type is correct
        if(evt.type == 'contentLoaded'){
            //Set up callback for ok button click
            Debugger.log('!evt.target.scrollContent:'+evt.target.scrollContent,Debugger.GEN,'openDialogLoaded','org.lamsfoundation.lams.WorkspaceView');
            evt.target.scrollContent.addEventListener('okClicked',Delegate.create(this,okClicked));
            evt.target.scrollContent.addEventListener('locationTabClick',Delegate.create(this,locationTabClick));
            evt.target.scrollContent.addEventListener('propertiesTabClick',Delegate.create(this,propertiesTabClick));
				
			//set a ref to the view
			evt.target.scrollContent.workspaceView = getView();
			//set a ref to the dia in the view
			getView().workspaceDialogue = evt.target.scrollContent;
			//set up UI
			//note this function registeres the dialog to recieve view updates
			evt.target.scrollContent.setUpContent();		
			//populate the licenses drop down
			_workspaceModel.populateLicenseDetails();			
			
			//select the right tab, dont pass anything to show the default tab
			_workspaceModel.showTab(_workspaceModel.currentTab);
			
			// show details of current saved LD - Authoring only
			if(_workspaceModel.currentMode == 'SAVEAS'){
				_workspaceModel.currentOpenNode = Application.getInstance().getCanvas().ddm.getDesignForWorkspace();
				trace('current open node (ltext): '  + _workspaceModel.currentOpenNode);
				_workspaceModel.showCurrentDetails(_workspaceModel.currentOpenNode);
			}
        }else {
            //TODO DI 25/05/05 raise wrong event type error 
        }
    }

	private function locationTabClick(evt:Object) {
        Debugger.log('locationTabClick:'+evt.type,Debugger.GEN,'locationTabClick','org.lamsfoundation.lams.WorkspaceController');
        _workspaceModel.showTab("LOCATION");
    }
	
	private function propertiesTabClick(evt:Object) {
        Debugger.log('propertiesTabClick:'+evt.type,Debugger.GEN,'propertiesTabClick','org.lamsfoundation.lams.WorkspaceController');
        _workspaceModel.showTab("PROPERTIES");
    }
	
    /**
    * Workspace dialog OK button clicked handler
    */
    private function okClicked(evt:Object) {
        Debugger.log('!okClicked:'+evt.type+', now follows the resultDTO:',Debugger.GEN,'okClicked','org.lamsfoundation.lams.WorkspaceController');
        //Check type is correct
		if(evt.type == 'okClicked'){
            //Call the callback, passing in the design selected designId

			//invalidate the cache of folders
			_workspaceModel.clearWorkspaceCache(evt.target.resultDTO.targetWorkspaceFolderID);
			
			//pass the resultant DTO back to the class that called us.
            _workspaceModel.getWorkspace().onOKCallback(evt.target.resultDTO);
        }else {
            //TODO DI 25/05/05 raise wrong event type error 
        }
    }
	
	/**
    * Workspace dialog OK button clicked handler
    */
    private function clickFromDialog(evt:Object) {
        Debugger.log('clickFromDialog:'+evt.type,Debugger.GEN,'clickFromDialog','org.lamsfoundation.lams.WorkspaceController');
        
    }
	
	/**
    * Invoked when the node is opened.  it must be a folder
    */
    public function onTreeNodeOpen (evt:Object){
		var treeview = evt.target;
		var nodeToOpen:XMLNode = evt.node;
		Debugger.log('nodeToOpen workspaceFolderID:'+nodeToOpen.attributes.data.workspaceFolderID,Debugger.GEN,'onTreeNodeOpen','org.lamsfoundation.lams.WorkspaceController');
		Debugger.log('nodeToOpen resourceID:'+nodeToOpen.attributes.data.resourceID,Debugger.GEN,'onTreeNodeOpen','org.lamsfoundation.lams.WorkspaceController');
		//if this ndoe has children then the 
		//data has already been got, nothing to do
		
		if(!nodeToOpen.hasChildNodes()){
			// DC24-01-06 this resource ID must refer to a folder as its been marked as a branch
			var resourceToOpen = nodeToOpen.attributes.data.resourceID;
			//must be a folder ID, depoends if this event is fired for an "open" reousrce click
			_workspaceModel.openFolderInTree(resourceToOpen, false);
		}else{
			Debugger.log('nodeToOpen already has children in cache',Debugger.GEN,'onTreeNodeOpen','org.lamsfoundation.lams.WorkspaceController');
			
		}
    }
	
	public function forceNodeOpen(nodeToOpen:XMLNode){
		if(!nodeToOpen.hasChildNodes()){
			// DC24-01-06 this resource ID must refer to a folder as its been marked as a branch
			var resourceToOpen = nodeToOpen.attributes.data.resourceID;
			//must be a folder ID, depoends if this event is fired for an "open" reousrce click
			_workspaceModel.openFolderInTree(resourceToOpen, true);
		}else{
			Debugger.log('nodeToOpen already has children in cache',Debugger.GEN,'forceNodeOpen','org.lamsfoundation.lams.WorkspaceController');
			_workspaceModel.forced = true;
			_workspaceModel.broadcastViewUpdate('OPEN_FOLDER',nodeToOpen);
		}
	}
	
	/**
    * Treeview data changed event handler
    */
    public function onTreeNodeClose (evt:Object){
		Debugger.log('type::'+evt.type,Debugger.GEN,'onTreeNodeClose','org.lamsfoundation.lams.WorkspaceController');
		var treeview = evt.target;
    }
	
	public function onTreeNodeChange (evt:Object){
		Debugger.log('type::'+evt.type,Debugger.GEN,'onTreeNodeChange','org.lamsfoundation.lams.WorkspaceController');
		var treeview = evt.target;
		_workspaceModel.setSelectedTreeNode(treeview.selectedNode);
		if(treeview.selectedNode.attributes.data.resourceType == _workspaceModel.RT_FOLDER) {
			if(treeview.selectedNode.attributes.isOpen) { _workspaceModel.broadcastViewUpdate('CLOSE_FOLDER', treeview.selectedNode); }
			else { forceNodeOpen(treeview.selectedNode); }
		}
	}
	
	/**
	 * Initiates a move request for a folder or resource
	 * @usage   
	 * @param   evt //this event should contain: {type:"drag_complete", target: this.tree, sourceNode: node, targetNode: this.__targetNode.item}
	 * @return  
	 */
	public function onDragComplete(evt:Object){
		Debugger.log('type::'+evt.type,Debugger.GEN,'onDragComplete','org.lamsfoundation.lams.WorkspaceController');
		
		var treeview = evt.target;
		//var snodeData = treeview.selectedNode.attributes.data;
		
		//targetNode is the folder or resource that the source was dropped onto
		var targetNodeData = evt.targetNode.attributes.data;
		//source node is the ndie that was picked up.
		var sourceNodeData = evt.sourceNode.attributes.data;
		var targetFolderID:Number;
		
		//Find where we are to copy to
		//if its a folder then the resourceID is the selected folder ID, otherwise its the parent
		if(targetNodeData.resourceType == _workspaceModel.RT_FOLDER){
			targetFolderID= targetNodeData.resourceID;
		}else{
			targetFolderID= targetNodeData.workspaceFolderID;
		}
		//check the permission code for that folder
		var isWritable = _workspaceModel.isWritableResource(_workspaceModel.RT_FOLDER,targetFolderID);
		var sourceFolderID:Number;
		/*
		* actually its always the folderID , even if its the folder, 
		* cos we are looking to refresh the containing fodlerID, 
		* not the resource being moved
		if(sourceNodeData.resourceType == _workspaceModel.RT_FOLDER){
			sourceFolderID= sourceNodeData.resourceID;
		}else{
			sourceFolderID= sourceNodeData.workspaceFolderID;
		}
		*/
		sourceFolderID= sourceNodeData.workspaceFolderID;
		
		Debugger.log('sourceFolderID:'+sourceFolderID+', targetFolderID:'+targetFolderID,Debugger.GEN,'onDragComplete','org.lamsfoundation.lams.WorkspaceController');
		//are we trying to copy to the same folder?
		if(sourceFolderID == targetFolderID){
			
			LFMessage.showMessageAlert(Dictionary.getValue('ws_copy_same_folder'),null,null);
			//we still have to refresh the folders as the DnD tree will be showing wrong info
			_workspaceModel.clearWorkspaceCacheMultiple();
		}else{
			
			//must clear the entire cache as both the source and target folders need to be refreshed
			_workspaceModel.folderIDPendingRefresh = null;
			_workspaceModel.folderIDPendingRefreshList = new Array(targetFolderID,sourceFolderID);
			
			//Debugger.log('SourceNode:\n'+ObjectUtils.toString(sourceNodeData),Debugger.GEN,'onDragComplete','org.lamsfoundation.lams.WorkspaceController');
			//Debugger.log('TargetNode:\n'+ObjectUtils.toString(targetNodeData),Debugger.GEN,'onDragComplete','org.lamsfoundation.lams.WorkspaceController');
			
			//ok we are going to do a move:
			if(isWritable){
				_workspaceModel.getWorkspace().requestMoveResource(sourceNodeData.resourceID, targetFolderID, sourceNodeData.resourceType);
			}else{
				//show an alert();
				LFMessage.showMessageAlert(Dictionary.getValue('ws_no_permission'),null,null);
				//we still have to refresh the folders as the DnD tree will be showing wrong info
				_workspaceModel.clearWorkspaceCacheMultiple();
			}
		
		}
		
		
		
	}
	
	
	
		/**
	 * Handles the events from the cut, copy, paste n delete buttons
	 * @usage   
	 * @param   e 
	 * @return  
	 */
	public function fileOperationRequest(e:Object){
		setBusy()
		var tgt:String = new String(e.target);
		var workspaceDialogue = getView().workspaceDialogue;
		Debugger.log('type:'+e.type+',target:'+tgt,Debugger.GEN,'fileOperationRequest','org.lamsfoundation.lams.WorkspaceController');
		//get the selected node:
		var snode = workspaceDialogue.treeview.selectedNode;
		
		//Number(snode.attributes.data.resourceID);
		//check target for button name
		/* TODO: Add cut implementation, for now just scrap it :-)
		if(tgt.indexOf("cut_btn") != -1){
			_workspaceModel.setClipboardItem(snode,"CUT");
			//TODO: Be nice to dim the branch in the tree
			
		}else 
		*/
		_global.breakpoint();
		if(tgt.indexOf("copy_btn") != -1){
			_workspaceModel.setClipboardItem(snode.attributes.data);
			
		}else if(tgt.indexOf("paste_btn") != -1){
			var itemToPaste = _workspaceModel.getClipboardItem();
			if(itemToPaste != null){
				//find out the selected folderID:
				//get the selected node:
				var snodeData = workspaceDialogue.treeview.selectedNode.attributes.data;
				var selectedFolderID:Number;
				
				//if its a folder then the resourceID is the selected folder ID, otherwise its the parent
				if(snodeData.resourceType == _workspaceModel.RT_FOLDER){
					selectedFolderID = snodeData.resourceID;
					
				}else{
					selectedFolderID = snodeData.workspaceFolderID;
				}
				
				//check if we can write to this folder
				if(_workspaceModel.isWritableResource(snodeData.resourceType,selectedFolderID)){
					_workspaceModel.folderIDPendingRefresh = selectedFolderID;
					Debugger.log('Selected (target) folder ID=:'+selectedFolderID,Debugger.GEN,'fileOperationRequest','org.lamsfoundation.lams.WorkspaceController');
					_workspaceModel.getWorkspace().requestCopyResource(itemToPaste.resourceID,selectedFolderID,itemToPaste.resourceType);
					
				}else{
					LFMessage.showMessageAlert(Dictionary.getValue('ws_no_permission'),null,null);
				}
				
			}else{
				//nothing to paste..
			}

		}else if(tgt.indexOf("delete_btn") != -1){
			//find out the selected folderID:
			//get the selected node:
			var snodeData = workspaceDialogue.treeview.selectedNode.attributes.data;
			if(snodeData != null){
				//TODO Check permission code to make sure we can do this!
				//check if we can write to this folder
				if(_workspaceModel.isWritableResource(snodeData.resourceType,snodeData.resourceID)){
					_workspaceModel.folderIDPendingRefresh = snodeData.workspaceFolderID;
					_workspaceModel.getWorkspace().requestDeleteResource(snodeData.resourceID,snodeData.resourceType, false);	
				}else{
					LFMessage.showMessageAlert(Dictionary.getValue('ws_no_permission'),null,null);
				}
				
				
			}else{
				//nothing to delete
			}
		}else if(tgt.indexOf("new_btn") != -1){
			//check we can create a folder here
			var snodeData = workspaceDialogue.treeview.selectedNode.attributes.data;
			if(snodeData != null){
				
				
				//check if we can write to this folder
				if(_workspaceModel.isWritableResource(snodeData.resourceType,snodeData.resourceID)){
					Dialog.createInputDialog(Dictionary.getValue('ws_newfolder_ins'), Dictionary.getValue('ws_newfolder_ok'), Dictionary.getValue('ws_newfolder_cancel'), Delegate.create(_workspaceController ,setNewFolderName),null);
				}else{
					LFMessage.showMessageAlert(Dictionary.getValue('ws_no_permission'),null,null);
				}
				
			}else{
				//no where to make new folder
			}
			
			//_workspaceModel.getWorkspace().requestCreateFolder();
			
		}else if(tgt.indexOf("rename_btn") != -1){
			//check we can create a folder here
			var snodeData = workspaceDialogue.treeview.selectedNode.attributes.data;
			if(snodeData != null){
				//check if we can write to this folder
				if(_workspaceModel.isWritableResource(snodeData.resourceType,snodeData.resourceID)){
					Dialog.createInputDialog(Dictionary.getValue('ws_rename_ins'), Dictionary.getValue('ws_newfolder_ok'), Dictionary.getValue('ws_newfolder_cancel'), Delegate.create(_workspaceController ,setNewResourceName),null);
				}else{
					LFMessage.showMessageAlert(Dictionary.getValue('ws_no_permission'),null,null);
				}

			}else{
				//nothing to rename
			}
			
			//_workspaceModel.getWorkspace().requestCreateFolder();
			
		}
		clearBusy()
		
		//TODO: integrate with key listener for canvas!! CTRL-C is handels by the canvas at the mo... need to set somethign in application.#
		
	}
	
	/**
	 * Called when the user finishes the editing the name in the pop up dialog
	 * @usage   
	 * @param   newName 
	 * @return  
	 */
	public function setNewResourceName(newName:String){
		Debugger.log('newName:'+newName,Debugger.GEN,'setNewResourceName','org.lamsfoundation.lams.WorkspaceController');
		var workspaceDialogue = getView().workspaceDialogue;
		var snodeData = workspaceDialogue.treeview.selectedNode.attributes.data;
		_workspaceModel.folderIDPendingRefresh = snodeData.workspaceFolderID;
		_workspaceModel.getWorkspace().requestRenameResource(snodeData.resourceID,snodeData.resourceType,newName);
	}
	
	/**
	 * Called when the user finishes the editing the name in the pop up dialog
	 * @usage   
	 * @param   newName 
	 * @return  
	 */
	public function setNewFolderName(newName:String){
		Debugger.log('newName:'+newName,Debugger.GEN,'setNewFolderName','org.lamsfoundation.lams.WorkspaceController');
		var workspaceDialogue = getView().workspaceDialogue;
		var snodeData = workspaceDialogue.treeview.selectedNode.attributes.data;
		var selectedFolderID:Number;
		//if its a folder then the resourceID is the selected folder ID, otherwise its the parent
		if(snodeData.resourceType == _workspaceModel.RT_FOLDER){
			selectedFolderID = snodeData.resourceID;
		}else{
			selectedFolderID = snodeData.workspaceFolderID;
		}
		_workspaceModel.folderIDPendingRefresh = selectedFolderID;
		//TODO: Validate is allowed name
		_workspaceModel.getWorkspace().requestNewFolder(selectedFolderID,newName);
	}
	
	public function setBusy(){
		_isBusy = true;
		getView().workspaceDialogue.showHideBtn(false);
	}
	
	public function clearBusy(){
		_isBusy = false;
		getView().workspaceDialogue.showHideBtn(true);
	}
	
	public function getWSModel():WorkspaceModel{
		return _workspaceModel;
	}
	
	//override the super version
	public function getView(){
		var v = super.getView();
		return WorkspaceView(v);
		
	}
}
