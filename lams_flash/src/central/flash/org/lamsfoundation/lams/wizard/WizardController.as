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

import org.lamsfoundation.lams.common.mvc.*;
import org.lamsfoundation.lams.common.util.*;

import org.lamsfoundation.lams.wizard.*;
import org.lamsfoundation.lams.wizard.steps.*;
import org.lamsfoundation.lams.common.ws.*;
import org.lamsfoundation.lams.common.ApplicationParent;

import mx.utils.*

/**
* Controller for the sequence library
*/
class WizardController extends AbstractController {
	private var _wizardModel:WizardModel;
	private var _wizardController:WizardController;
	private var _wizardView:WizardView;
	private var _resultDTO:Object;
	private var _isBusy:Boolean;
	
	/**
	* Constructor
	*
	* @param   wm   The model to modify.
	*/
	public function WizardController (wm:Observable) {
		super (wm);
		_wizardModel = WizardModel(getModel());
		_wizardController = this;
		_wizardView = WizardView(getView());
		_isBusy = false;
	}
	
	// add control methods
	
	/**
	 * Event listener for when when tab is clicked
	 * 
	 * @usage   
	 * @param   evt 
	 * @return  
	 */
	public function change(evt):Void{
		Debugger.log("hitting change: " + evt.target.selectedIndex, Debugger.CRITICAL, "change", "WizardController");
		Debugger.log("wizard model: " + _wizardModel, Debugger.CRITICAL, "change", "WizardController");
		
		_wizardModel.setLocationTab(evt.target.selectedIndex);
	}
	
	public function click(evt):Void{
		Debugger.log('click evt.target.label:'+evt.target.label,Debugger.CRITICAL,'click','WizardController');
		var tgt:String = new String(evt.target);
		// button click event handler - next, prev, finish, cancel
		
		if(tgt.indexOf("next_btn") != -1){
			gonext();
		
		}else if(tgt.indexOf("prev_btn") != -1){
			goprev();
		}else if(tgt.indexOf("finish_btn") != -1){
			gofinish();
		}else if(tgt.indexOf("start_btn") != -1){
			gostart();
		}else if(tgt.indexOf("close_btn") != -1){
			goclose();
		}else if(tgt.indexOf("cancel_btn") != -1){
			gocancel();
		}else if(tgt.indexOf("addmore_btn") != -1){
			goaddmore();
		}
		
	}
	
	private function gonext(evt:Object){
        Debugger.log('I am in goNext:',Debugger.CRITICAL,'click','gonext');
		_global.breakpoint();
		var wizView:WizardView = getView();
		if(wizView.validateStep(_wizardModel)){
			_wizardModel.stepID++;
		}
    }
	
	private function gocancel(evt:Object){
		ApplicationParent.extCall("closeWindow", null);
	}
	
	private function goclose(evt:Object){
		ApplicationParent.extCall("closeWizard", null);
	}
	
	private function goprev(evt:Object){
		_wizardModel.stepID--;
	}
	
	private function gofinish(evt:Object){
		var wizView:WizardView = getView();
		if(wizView.validateStep(_wizardModel)){
			wizView.resultDTO.mode = WizardView.FINISH_MODE;
			wizView.disableButtons();
			initializeLesson(wizView.resultDTO);
		}
	}
	
	private function gostart(evt:Object){
		var wizView:WizardView = getView();
		if(wizView.validateStep(_wizardModel)){
			wizView.resultDTO.mode = WizardView.START_MODE;
			wizView.disableButtons();
			initializeLesson(wizView.resultDTO);
		}
	}
	
	private function goaddmore(evt:Object){
		// go back to step 1
		_wizardModel.stepID = 1;
	}
	
	/**
    * Workspace dialog OK button clicked handler
    */
    private function okClicked(evt:Object) {
		if(evt.type == 'okClicked'){
			//invalidate the cache of folders
			//getView().workspaceView.getModel().clearWorkspaceCache(evt.target.resultDTO.targetWorkspaceFolderID);
			
			//pass the resultant DTO back to the class that called us.
            Application.getInstance().getWorkspace().onOKCallback(evt.target.resultDTO);
		}
	}
	
	/**
    * Invoked when the node is opened.  it must be a folder
    */
    public function onTreeNodeOpen (evt:Object){
		var treeview = evt.target;
		var nodeToOpen:XMLNode = evt.node;
		Debugger.log('nodeToOpen organisationID:'+nodeToOpen.attributes.data.organisationID,Debugger.GEN,'onTreeNodeOpen','org.lamsfoundation.lams.MonitorController');
		Debugger.log('nodeToOpen org name:'+nodeToOpen.attributes.data.name,Debugger.GEN,'onTreeNodeOpen','org.lamsfoundation.lams.MonitorController');
		//if this ndoe has children then the 
		//data has already been got, nothing to do
		
    }
	
	/**
    * Treeview data changed event handler
    */
    public function onTreeNodeClose (evt:Object){
		Debugger.log('type::'+evt.type,Debugger.GEN,'onTreeNodeClose','org.lamsfoundation.lams.MonitorController');
		var treeview = evt.target;
    }
	
	public function onTreeNodeChange (evt:Object){
		Debugger.log('type::'+evt.type,Debugger.GEN,'onTreeNodeChange','org.lamsfoundation.lams.MonitorController');
		var treeview = evt.target;
		if(!_isBusy){
			setBusy();
			_wizardModel.setSelectedTreeNode(treeview.selectedNode);
		} else {
			treeview.selectedNode = _wizardModel.getSelectedTreeNode();
		}
	}
	
	public function selectTreeNode(node:XMLNode){
		if(node!=null){
			if(!_isBusy){
				setBusy();
				_wizardModel.setSelectedTreeNode(node);
			}
		}
	}
	
	/**
	 * Initialize lesson returning new LessonID
	 *   
	 * @param   resultDTO Wizard data
	 *  
	 */
	
	public function initializeLesson(resultDTO:Object){
		_wizardModel.resultDTO = resultDTO;
		var callback:Function = Proxy.create(this,saveLessonClass);
		_wizardModel.getWizard().initializeLesson(resultDTO, callback);
	}
	
	/**
	 * Save Lesson Class after Lesson is initialized
	 *  
	 * @param   lessonID 
	 * @return  
	 */
	
	public function saveLessonClass(r){
		if(r instanceof LFError) {
			r.showMessageConfirm();
		} else {
			_wizardModel.lessonID = r;
			_wizardModel.getWizard().createLessonClass();
		}
	}
	
	private function getView():WizardView{
		return WizardView(super.getView());
	}
	
	public function setBusy(){
		_isBusy = true;
		WizardView(getView()).disableButtons();
	}
	
	public function clearBusy(){
		_isBusy = false;
		WizardView(getView()).enableButtons(_wizardModel);
	}
	
}