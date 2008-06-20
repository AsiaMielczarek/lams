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

import org.lamsfoundation.lams.common.util.*

/**
* HashTable
*/
class org.lamsfoundation.lams.common.util.Hashtable {

    //Declarations
    private var elements:Array;
    private var _name:String;
    private var _watch:Boolean;
    private var _watchFunction:Function;
	private var _watchFunctionLocation:Object;
	private var _isBusy:Boolean;
	
    //Constructor
    function Hashtable(n) {
        _name=n;        elements=[];
		clearBusy();
    }
    
	/**
	 * Adds element to the hashtable
	 * @usage   
	 * @param   key 
	 * @param   val 
	 * @return  r	The value that was replaced
	 */
    public function put(key, val) {		setBusy();
       //Debugger.debug('called','Hashtable.prototype.put');
        var i = this._getIndex(key);
        var r = this.elements[i].val;
    
       //Debugger.debug('watch update..','Hashtable.prototype.put');
        if(this._watch){
            this._watchFunctionLocation[this._watchFunction](this._name, r, val);
        }
        
        if(i == this.elements.length){
			this.elements.length++;
		}
		
        //this.elements.length += i == this.elements.length;
        
        this.elements[i] = new HashtableElement(key, val);
        clearBusy();
		return r;
    }
    
    public function putAll(anArray) {	        for (var i = 0; i < anArray.length; i++) {
            this.put(i, anArray[i]);
            if(this._watch){
                this._watchFunctionLocation[this._watchFunction](this._name, null, null);
            }
        }
    }
    
    
    public function get (o) {
		setBusy();
        //trace('Hashtable.prototype.get called');
        var i = this._getIndex(o);
        if (i != this.elements.length) {
			clearBusy();
            return this.elements[i].val;
        }
		
    }
    
     public function keys() {		setBusy();
        var r = new Array();
        for (var i = 0; i < this.elements.length; i++) {
            r[i] = this.elements[i].key;
        }
		clearBusy();
        return r;
    }
    
    public function values() {
		setBusy();
        var r = new Array();
        for (var i = 0; i < this.elements.length; i++) {
            r[i] = this.elements[i].val;
        }
		clearBusy();
        return r;
    }
    
	/**
	 * Removes an element from the hashtable
	 * @usage   
	 * @param   o 
	 * @return  the element being removed or null if its not found
	 */
    public function remove(o) {
		setBusy();

        var i = this._getIndex(o);
		
		//we gotta clear the busy flag as keys needs to be called
		clearBusy();
        var l = this.keys().length;
		setBusy();
		
		if(i < l){
            // o found
            var r = this.elements[i].val;
    
            if(this._watch){
                this._watchFunctionLocation[this._watchFunction](this._name, r, null);
            }
            
            this.elements.splice(i, 1);
			clearBusy();
			
            return r;
        }else{
			clearBusy();
            return null;
        }	
    }
    
    public function clear() {		setBusy();
        this.elements = new Array();
        if(this._watch){
            this._watchFunctionLocation[this._watchFunction](this._name, null, null);
        }		clearBusy();
        
    }
    
    public function clone(newName) {
		setBusy();
        var r = new Hashtable(newName);
        for (var i = 0; i < this.elements.length; i++) {
            r.elements[i] = this.elements[i];
        }
		clearBusy();
        return r;
    }
    
    public function containsKey(o:Number) {
		setBusy();
		var r = this._getIndex(o) != this.elements.length;
		clearBusy();
        return r;
    }
    
    public function containsValue(o) {		setBusy();
		var r = this._getIndex(o, "val") != this.elements.length;
		clearBusy();
        return r;
    }
    
    public function size() {
        return this.elements.length;
    }
    
    public function isEmpty() {
        return this.elements.length == 0;
    }
    
    public function startWatch(newWatchFunction,loc) {
        this._watch = true;
        this._watchFunction = newWatchFunction;
        this._watchFunctionLocation = loc;
        Debugger.debug("\nwatch "+this._watch+" for:"+this._name);
        trace("\nwatch "+this._watch+" for:"+this._name);
    }
    
    public function stopWatch () {
        this._watch =false;
        this._watchFunction = null;
        this._watchFunctionLocation = null;
    }   
    
    /*
    *
    * Internal method to get index of an element
    * o = search term, f is name of field, can be 'val' or blank is key
    */
    private function _getIndex (o, f) {
        var r;
        f = f == undefined ? "key" : f;
        for (r = 0; r < this.elements.length && this.elements[r][f] != o; r++);
        return r;
    }    	private function setBusy():Void{
		if(_isBusy){
			//Debugger.log('!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!',1,'checkBusy','org.lamsfoundation.lams.common.util.Hashtable');
			//Debugger.log('!!!!!!!!!!!!!!!!!!!! HASHTABLE ACCESED WHILE BUSY !!!!!!!!!!!!!!!!',1,'checkBusy','org.lamsfoundation.lams.common.util.Hashtable');
			//Debugger.log('!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!',1,'checkBusy','org.lamsfoundation.lams.common.util.Hashtable');
		}
		_isBusy=true;
	}
	
	private function clearBusy():Void{
		_isBusy=false;
	}
	
}