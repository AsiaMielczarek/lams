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

import org.lamsfoundation.lams.authoring.*
import org.lamsfoundation.lams.authoring.cv.*
import org.lamsfoundation.lams.common.mvc.*
import org.lamsfoundation.lams.common.util.*
import org.lamsfoundation.lams.common.ui.*
import org.lamsfoundation.lams.common.dict.*
import mx.utils.*


/*
* Makes changes to the Canvas Authoring model's data based on user input.
*/
class org.lamsfoundation.lams.authoring.cv.CanvasController extends AbstractController {
	
	private var _canvasModel:CanvasModel;
	private var _canvasView:CanvasView;
	
	/**
	* Constructor
	*
	* @param   cm   The model to modify.
	*/
	public function CanvasController (cm:Observable) {
		super (cm);
		//have to do an upcast
		_canvasModel = CanvasModel(getModel());
		
	}
   
    public function activityClick(ca:Object):Void{
	   Debugger.log('activityClick CanvasActivity:'+ca.activity.activityUIID + ' orderID: ' + ca.activity.orderID,Debugger.GEN,'activityClick','CanvasController');
	    Debugger.log('Check if transition tool active :'+_canvasModel.isTransitionToolActive(),Debugger.GEN,'activityClick','CanvasController');
	   //if transition tool active
	    if(_canvasModel.isTransitionToolActive()){
		   var transitionTarget = createValidTransitionTarget(ca);
		    if(transitionTarget instanceof LFError){
				transitionTarget.showErrorAlert(null); 
				//transitionTarget.showMessageConfirm()
				//TODO: transitionTarget.showErrorAlertCrashDump(null); 
		   }else{
				_canvasModel.addActivityToTransition(transitionTarget);
			}
			/*
			_canvasModel.resetTransitionTool();
			if(ca instanceof CanvasActivity || ca instanceof CanvasParallelActivity ){
				_canvasModel.addActivityToTransition(ca);
			}
			*/
	    }else{
		   //just select the activity
			
			//var parentActTypeID = _canvasModel.getCanvas().ddm.getActivityByUIID(ca.activity.parentUIID).activityTypeID
			trace("parent UIID: "+ ca.activity.parentUIID + " and parent's activity type ID: ")
			 //if (ca.activity.parentUIID > 0 && parentActTypeID == Activity.PARALLEL_ACTIVITY_TYPE){
			//	 _canvasModel.selectedItem = null;
			//	 _canvasModel.isDragging = false;
				 //ca.startDrag(false);
			//	var msg:String = Dictionary.getValue('al_cannot_move_activity');
			//	LFMessage.showMessageAlert(msg);
				
			 //}else {
				 _canvasModel.selectedItem = ca;
				 _canvasModel.isDragging = true;
				 ca.startDrag(false);
			 //}
		}
	   
   }
   
	public function activityDoubleClick(ca:Object):Void{
	   Debugger.log('activityDoubleClick CanvasActivity:'+ca.activity.activityUIID,Debugger.GEN,'activityDoubleClick','CanvasController');
	    _canvasModel.selectedItem = ca;
		if(ca.activity.activityTypeID == Activity.TOOL_ACTIVITY_TYPE){
			_canvasModel.openToolActivityContent(ca.activity);
		}else{
			//TODO: Show the property inspector if its a parralel activity or whatever
		}
    }
   
    public function activityRelease(ca:Object):Void{
	   Debugger.log('activityRelease CanvasActivity:'+ca.activity.activityUIID,Debugger.GEN,'activityRelease','CanvasController');
	   
	    if(_canvasModel.isDragging){
			ca.stopDrag();
			//if we are on the bin - trash it
			
			if (ca.hitTest(_canvasModel.getCanvas().bin)){
				trace("Activity "+ca.activity.title+" has hit the bin")
				if (ca.activity.activityTypeID == Activity.OPTIONAL_ACTIVITY_TYPE || ca.activity.activityTypeID == Activity.PARALLEL_ACTIVITY_TYPE){
					trace("Complex Activity has hit the bin")
					_canvasModel.removeComplexActivity(ca);
				}
				//_canvasModel.removeActivity(ca.activity.activityUIID);
				_canvasModel.getCanvas().removeActivity(ca.activity.activityUIID);
				//_canvasModel.setDirty();
			}
			
			var optionalOnCanvas:Array  = _canvasModel.findOptionalActivities();
			if (ca.activity.parentUIID != null){
				trace ("testing Optional child on Canvas "+ca.activity.activityUIID)
				for (var i=0; i<optionalOnCanvas.length; i++){
				//trace ("testing Optional on Canvas "+i)
					if (ca.activity.parentUIID == optionalOnCanvas[i].activity.activityUIID){
						if (optionalOnCanvas[i].locked == false){
							if (ca._x > 142 || ca._x < -129 || ca._y < -55 || ca._y > optionalOnCanvas[i].getpanelHeight){
								trace (ca.activity.activityUIID+" had a hitTest with canvas.")
						
								_canvasModel.removeOptionalCA(ca, optionalOnCanvas[i].activity.activityUIID);
							}
						}
					}
				}
			}
			//if we are on the optional Activity remove this activity from canvas and assign it a parentID of optional activity and place it in the optional activity window.
			for (var i=0; i<optionalOnCanvas.length; i++){
				//trace ("testing Optional on Canvas "+i)
				if (ca.activity.activityUIID != optionalOnCanvas[i].activity.activityUIID){
					if (ca.hitTest(optionalOnCanvas[i])){
						if (optionalOnCanvas[i].locked == true){
							var msg:String = Dictionary.getValue('act_lock_chk');
							LFMessage.showMessageAlert(msg);
						}else{
							_canvasModel.addParentToActivity(optionalOnCanvas[i].activity.activityUIID, ca)
						}						
					}
				}
			}
						
			//get a view if ther is not one
			if(!_canvasView){
				_canvasView =  CanvasView(getView());
			}
			
			//give it the new co-ords and 'drop' it
			ca.activity.xCoord = ca._x;
			ca.activity.yCoord = ca._y;
			
			//refresh the transitions
			//TODO: refresh the transitions as you drag...
			var myTransitions = _canvasModel.getCanvas().ddm.getTransitionsForActivityUIID(ca.activity.activityUIID);
			//run in a loop ato support branches, maybe more then 2 transitions.
			for (var i=0; i<myTransitions.length;i++){
				Debugger.log('removing transition for redraw:'+myTransitions[i].transitionUIID,Debugger.GEN,'activityRelease','CanvasController');
				var t = _canvasModel.transitionsDisplayed.remove(myTransitions[i].transitionUIID);
				t.removeMovieClip();
				
			}
					
			_canvasModel.setDirty();
			
			Debugger.log('ca.activity.xCoord:'+ca.activity.xCoord,Debugger.GEN,'activityRelease','CanvasController');
			
			
		}
	}
   
   public function activityReleaseOutside(ca:Object):Void{
	   Debugger.log('activityReleaseOutside CanvasActivity:'+ca.activity.activityUIID,Debugger.GEN,'activityReleaseOutside','CanvasController');
	   Debugger.log('activityReleaseOutside Check if Transition tool active:'+_canvasModel.isTransitionToolActive(),Debugger.GEN,'activityReleaseOutside','CanvasController');
	   if(_canvasModel.isTransitionToolActive()){
				
			
			//get a ref to the CanvasActivity the transition pen is over when released
			var currentCursor:MovieClip = Cursor.getCurrentCursorRef();
			//Debugger.log("currentCursor:"+currentCursor, Debugger.GEN,'activityReleaseOutside','CanvasController');
			//strip the last mc off the path as its the click target
			//_global.breakpoint();
			//Debugger.log("currentCursor._droptarget:"+currentCursor._droptarget, Debugger.GEN,'activityReleaseOutside','CanvasController');
			var dt:String = new String(eval(currentCursor._droptarget));
			var i:Number = dt.lastIndexOf(".");
			dt = dt.substring(0,i);
			//Debugger.log("Subst:"+dt, Debugger.GEN,'activityReleaseOutside','CanvasController');
			var transitionTarget_mc:MovieClip = eval(dt);
			Debugger.log("Transition drop target:"+transitionTarget_mc, Debugger.GEN,'activityReleaseOutside','CanvasController');
			
			var transitionTarget = createValidTransitionTarget(transitionTarget_mc);
			if(transitionTarget instanceof LFError){
			//_canvasModel.resetTransitionTool();
				_canvasModel.getCanvas().stopTransitionTool();
			//if(transitionTarget instanceof LFMessage){
				//transitionTarget.showErrorAlert(null);
				transitionTarget.showMessageConfirm();
			}else{
				_canvasModel.addActivityToTransition(transitionTarget);
				//_canvasModel.resetTransitionTool();
				_canvasModel.getCanvas().stopTransitionTool();
			}
			
		
			
	   }else{
			if(_canvasModel.isDragging){
				ca.stopDrag();
				if (ca.hitTest(_canvasModel.getCanvas().bin)){
					_canvasModel.getCanvas().removeActivity(ca.activity.activityUIID);
				}
			}
		   
		}
   		
   
   
   }
   
   
    public function transitionClick(ct:CanvasTransition):Void{
	   Debugger.log('transitionClick Transition:'+ct.transition.uiID,Debugger.GEN,'transitionClick','CanvasController');
	    _canvasModel.selectedItem = ct;
		_canvasModel.isDragging = true;
		ct.startDrag(false);
	   
	}
   
   public function transitionDoubleClick(ct:CanvasTransition):Void{
	   Debugger.log('transitionDoubleClick CanvasTransition:'+ct.transition.transitionUIID,Debugger.GEN,'transitionDoubleClick','CanvasController');
	   
	   //TODO: fix this, its null
	   _canvasView =  CanvasView(getView());
	   Debugger.log('_canvasView:'+_canvasView,Debugger.GEN,'transitionDoubleClick','CanvasController');
	   _canvasView.createTransitionPropertiesDialog("centre",Delegate.create(this, transitionPropertiesOK));
	   
	    _canvasModel.selectedItem = ct;
   }
   
   public function transitionRelease(ct:CanvasTransition):Void{
	if(_canvasModel.isDragging){
			ct.stopDrag();
			
				if (ct.hitTest(_canvasModel.getCanvas().bin)){
					_canvasModel.getCanvas().removeTransition(ct.transition.transitionUIID);
				}
			
			
		}
	
   }

	public function transitionReleaseOutside(ct:CanvasTransition):Void{
		transitionRelease(ct);
		
	}
   
	
	
	/**
	 * Transition Properties OK Handler
	 * @usage   
	 * @return  
	 */
	public function transitionPropertiesOK(evt:Object):Void{
		Debugger.log(evt.gate,Debugger.GEN,'transitionPropertiesOK','CanvasController');
		//cm creates gate act and another transition to make [trans][gate][trans] seq
		//sets in ddm
		//flags dirty to refresh view
		_canvasModel.createGateTransition(_canvasModel.selectedItem.transition.transitionUIID,evt.gate);
		
		
		
	}
		
	/**
	* Clled when we get a click on the canvas, if its in craete gate mode then create a gate
	* 
	 * public static var SYNCH_GATE_ACTIVITY_TYPE:Number = 3;
	 * public static var SCHEDULE_GATE_ACTIVITY_TYPE:Number = 4;
	 * public static var PERMISSION_GATE_ACTIVITY_TYPE:Number = 5;
	 * @usage   
	 * @param   canvas_mc 
	 * @return  
	 */
	public function canvasRelease(canvas_mc:MovieClip){
		Debugger.log(canvas_mc,Debugger.GEN,'canvasRelease','CanvasController');
		Debugger.log('_canvasModel.activeTool:'+_canvasModel.activeTool,Debugger.GEN,'canvasRelease','CanvasController');
		_canvasModel.selectedItem = null;
		if(_canvasModel.activeTool == CanvasModel.GATE_TOOL){
			var p = new Point(canvas_mc._xmouse, canvas_mc._ymouse); 
			_canvasModel.createNewGate(Activity.PERMISSION_GATE_ACTIVITY_TYPE,p);
			_canvasModel.getCanvas().stopGateTool();
			
		}
		if(_canvasModel.activeTool == CanvasModel.OPTIONAL_TOOL){
			var p = new Point(canvas_mc._xmouse, canvas_mc._ymouse); 
			_canvasModel.createNewOptionalActivity(Activity.OPTIONAL_ACTIVITY_TYPE,p);
			//_canvasModel.createNewOptionalActivity(p);
			_canvasModel.getCanvas().stopOptionalActivity();
			
		}
		if(_canvasModel.activeTool == CanvasModel.GROUP_TOOL){
			var p = new Point(canvas_mc._xmouse, canvas_mc._ymouse); 
			_canvasModel.createNewGroupActivity(p);
			_canvasModel.getCanvas().stopGroupTool();
			
		}
		
	}
	
	private function createValidTransitionTarget(transitionTargetObj:Object):Object{
			var targetCA:Object;
			Debugger.log("My transitionTargetObj is :"+transitionTargetObj.activity.activityUIID, Debugger.GEN,'createValidTransitionTarget','CanvasController');
			//see what we can cast to
			if(CanvasActivity(transitionTargetObj)!=null){
				Debugger.log("Casting to CanvasActivity", Debugger.GEN,'createValidTransitionTarget','CanvasController');
				targetCA = CanvasActivity(transitionTargetObj);
				return targetCA;
			}else if(CanvasParallelActivity(transitionTargetObj)!=null){
				Debugger.log("Casting to CanvasParallelActivity", Debugger.GEN,'createValidTransitionTarget','CanvasController');
				targetCA = CanvasParallelActivity(transitionTargetObj);
				return targetCA;
			}else if(CanvasOptionalActivity(transitionTargetObj)!=null){
				Debugger.log("Casting to CanvasOptionalActivity", Debugger.GEN,'createValidTransitionTarget','CanvasController');
				targetCA = CanvasOptionalActivity(transitionTargetObj);
				return targetCA;
			}else{
				var e = new LFError(Dictionary.getValue('cv_invalid_trans_target'),"createValidTransitionTarget",this,String(transitionTargetObj));
				//bail
				return e;
			}
			
			/*
			//if(ca instanceof CanvasActivity){
			if(ICanvasActivity(ca) != null){
				Debugger.log("Target implements ICanvasActivity", Debugger.GEN,'activityReleaseOutside','CanvasController');
				var r:Object = _canvasModel.addActivityToTransition(targetCA);
				if(r instanceof LFError){
				//Debugger.error(r);
					r.showErrorAlert(null);
				}
				//TODO: Check on status of try catch bug
				*//*
				try{
					_canvasModel.addActivityToTransition(ca);
				//}catch(e:org.lamsfoundation.lams.common.util.LFError){
				}catch(e:LFError){
					trace('in catch');
					Debugger.error(e);
				}
				*/
				
			
			
			/*
			}else{
				Debugger.log("Target does NOT implement ICanvasActivity", Debugger.CRITICAL,'activityReleaseOutside','CanvasController');
				//released over something other than a CanvasActivity so reset the t tool
				
			}
			*/
	}
	
	
   

   
}
