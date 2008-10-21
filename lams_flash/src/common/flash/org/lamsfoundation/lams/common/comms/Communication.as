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
import org.lamsfoundation.lams.common.comms.*import org.lamsfoundation.lams.common.*;
import org.lamsfoundation.lams.common.ui.*;
import org.lamsfoundation.lams.authoring.*;
import org.lamsfoundation.lams.common.dict.*

/**
* Communication - responsible for server side communication and wddx serialisation/de-serialisation
* @author  DI   05/04/05
* 
*/
class org.lamsfoundation.lams.common.comms.Communication {


	private static var FRIENDLY_ERROR_CODE:Number = 1;  //Server error codes
    private static var SYSTEM_ERROR_CODE:Number = 2;
    
    private static var MAX_REQUESTS:Number = 20;       //Maximum no of simultaneous requests
	
	
    
    private var _serverURL:String;
    private var ignoreWhite:Boolean;  
    private var responseXML:XML;      //XML object for server response
    private var wddx:Wddx;            //WDDX serializer/de-serializer
    
    private var queue:Array;
    private var queueID:Number;

    /**
    * Comms constructor
    */
    function Communication(aServerURL:String){
        //Set up queue
        queue=[];
        queueID=0;
        
        
        ignoreWhite = true;
		
		if(_serverURL == null){
			if(_root.serverURL!=null){
				_serverURL = _root.serverURL;
			}else if(aServerURL != null){
				_serverURL = aServerURL;
			}else{
				Debugger.log("! serverURL is not set, unable to connect to server !!",Debugger.CRITICAL,'Consturcutor','Communication');
			}
			
		}
       
	    wddx = new Wddx();
    }
    
    /**
    * Make a request to the server.  Each request handlers is added to a queue whilst waiting for the (asynchronous) response 
    * from the server. The server returns a wrapped XML packet that is unwrapped and de-serialised on load and passed back
    * to the request handler
    * 
    * @usage -  var myObject.test = function (deserializedObject) {
    *               //Code to process object here....
    *           }
    *           commsInstance.getRequest('http://dolly.uklams.net/lams/lams_authoring/all_library_details.xml',myObject.test,true) //Note: all_library_details.xml has been deleted from cvs
    * 
    * @param requestURL:String  URL on the server for request script
    * @param handler:Function   callback function to call with the response object
  	* @param isFullURL:Boolean  Inicates if the requestURL is fully qualified, if false serverURL will be prepended
    * @returns Void
    */
    public function getRequest(requestURL:String,handler:Function,isFullURL:Boolean):Void{	

		Cursor.showCursor(ApplicationParent.C_HOURGLASS);

		//Create XML response object 
        var responseXML = new XML();
		responseXML.ignoreWhite = ignoreWhite;
        //Assign onLoad handler
        responseXML.onLoad = Proxy.create(this,onServerResponse,queueID);
        //Add handler to queue
        addToQueue(handler);        
        //Assign onData function        
        setOnData(responseXML);
        
        //TODO DI 11/04/05 Stub here for now until we have server implmenting new WDDX structure
        if(isFullURL){
			Debugger.log('Requesting:'+requestURL,Debugger.GEN,'getRequest','Communication');			
			responseXML.load(requestURL);
		}else{
			Debugger.log('Requesting:'+_serverURL+requestURL,Debugger.GEN,'getRequest','Communication');			
			responseXML.load(_serverURL+requestURL);
		}
    }
  
    /**
	* Sends a data object to the server and directs response to handler function.
	* 
    * @param dto:Object 		The flash object to send. Will be WDDX serialised    
	* @param requestURL:String  The url to send and load the data from
	* @param handler:Function   The function that will handle the response (usually an ACK response)
	* @param isFullURL:Boolean  Inicates if the requestURL is fully qualified, if false serverURL will be prepended
	* 
	* @usage: 
	* 
    */
    public function sendAndReceive(rawDto:Object, requestURL:String,handler:Function,isFullURL){
        //Serialise the Data Transfer Object
        var xmlToSend:XML = serializeObj(rawDto);
		xmlToSend.ignoreWhite = ignoreWhite;
		
		//Create XML response object 
        var responseXML = new XML();
		 //Assign onLoad handler
        responseXML.onLoad = Proxy.create(this,onServerResponse,queueID);

        //Assign onLoad handler
        //responseXML.onLoad = Proxy.create(this,onSendACK,queueID);
        //Add handler to queue
        addToQueue(handler);        
        
		//Assign onData function        
        setOnData(responseXML);
        
        //TODO DI 11/04/05 Stub here for now until we have server implmenting new WDDX structure
        if(isFullURL){
            Debugger.log('Posting to:'+requestURL,Debugger.GEN,'sendAndReceive','Communication');			
			Debugger.log('Sending XML:'+xmlToSend.toString(),Debugger.GEN,'sendAndReceive','Communication');			
            xmlToSend.sendAndLoad(requestURL,responseXML);
        }else{
            Debugger.log('Posting to:'+_serverURL+requestURL,Debugger.GEN,'sendAndReceive','Communication');
			Debugger.log('Sending XML:'+xmlToSend.toString(),Debugger.GEN,'sendAndReceive','Communication');			
            xmlToSend.sendAndLoad(_serverURL+requestURL,responseXML);
        }
    }
	
	/**
    * XML load handler for getRequest()
    * @param success            XML load status
    * @param wrappedPacketXML   The wrapped XML response object 
    * @param queueID:Number     ID of request handler on queue 
    */
    private function onServerResponse(success:Boolean,wrappedPacketXML:XML,queueID:Number){
        Debugger.log('xml recieved is:'+wrappedPacketXML.toString(),Debugger.VERBOSE,'onServerResponse','Communication');
		
		//Load ok?
        if(success){
		   var responseObj:Object = wddx.deserialize(wrappedPacketXML);
            if(responseObj.messageType == null){
				Debugger.log('Message type was:'+responseObj.messageType+' , cannot continue',Debugger.CRITICAL,'onServerResponse','Communication');			
				Debugger.log('xml recieved is:'+wrappedPacketXML.toString(),Debugger.CRITICAL,'onServerResponse','Communication');			
				return -1;
			}
			
            //Check for errors in message type that's returned from server
			if(responseObj.messageType == FRIENDLY_ERROR_CODE){
                //user friendly error
				var e = new LFError(responseObj.messageValue,"onServerResponse",this);
				dispatchToHandlerByID(queueID,e);
				
				var sendMsg:String = Dictionary.getValue('sys_error_msg_start')+"\n\n"+Dictionary.getValue('sys_error_msg_finish')+"\n\n\n";
				
				LFError.showSendErrorRequest(sendMsg, 'sys_error', Debugger.crashDataDump, null);
                
            }else if(responseObj.messageType == SYSTEM_ERROR_CODE){
				var sendMsg:String = Dictionary.getValue('sys_error_msg_start')+"\n\n"+Dictionary.getValue('sys_error_msg_finish');
				LFError.showSendErrorRequest(sendMsg, 'sys_error', Debugger.crashDataDump, null);
			}else{
                //Everything is fine so lookup callback handler on queue 
                if(responseObj.messageValue != null){
					dispatchToHandlerByID(queueID,responseObj.messageValue);
				}else{
					Debugger.log('Message value was null, cannot continue',Debugger.CRITICAL,'onServerResponse','Communication');			
				}
            }
			
			//Now delete the XML 
            delete wrappedPacketXML;
        }else {
            Debugger.log("XML Load failed",Debugger.CRITICAL,'onServerResponse','Communication');
			var e = new LFError("Communication with the server has failed. \nPlease check you are connected to the internet and/or LAMS server","onServerResponse",this,'Server URL:'+_serverURL);
			e.showMessageConfirm();
		}
    }
    
    /**
     * Received Acknowledgement from server, response to sendAndReceive() method
     * 
     * @usage   
     * @param   success     
     * @param   loadedXML   
     * @param   queueID     
     * @param   deserialize 
     * @return  
     */
    private function onSendACK(success:Boolean,loadedXML:XML,queueID:Number,deserialize:Boolean) {
       //TODO DI 06/06/05 find out what is required here, is it enough to assign onLoad handler to onServerResponse?
    }
    
    /**
    * Load Plain XML with optional deserialisation
    */
    public function loadXML(requestURL:String,handler:Function,isFullURL:Boolean,deserialize:Boolean):Void{
        //Create XML response object 
        var responseXML = new XML();
        
		//Assign onLoad handler
        responseXML.onLoad = Proxy.create(this,onXMLLoad,queueID,deserialize);
        
		//Add handler to queue
        addToQueue(handler);        
        
		//Assign onData function        
        setOnData(responseXML);
        
        //TODO DI 11/04/05 Stub here for now until we have server implmenting new WDDX structure
        if(isFullURL){
			Debugger.log('Requesting:'+requestURL,Debugger.GEN,'loadXML','Communication');			
			responseXML.load(requestURL);
		}else{
			Debugger.log('Requesting:'+_serverURL+requestURL,Debugger.GEN,'loadXML','Communication');			
			responseXML.load(_serverURL+requestURL);
		}
    }
    
    /**
    * Load XML load handler for loadXML() - 
    * 
    * @param success:Boolean        XML load status
    * @param loadedXML:XML          unwrapped WDDX XML 
    * @param queueID:Number         ID of request handler on queue 
    * @param deserialize:Boolean    flag to determin whether object should be deserialised before being passed back to handler 
    */
    private function onXMLLoad(success:Boolean,loadedXML:XML,queueID:Number,deserialize:Boolean) {
        //Deserialize
        if(deserialize){
            //Deserialize and pass back to handler
            var responseObj:Object = deserializeObj(loadedXML);
            dispatchToHandlerByID(queueID,responseObj);
        }else {
            //Call handler, passing in XML Object
            dispatchToHandlerByID(queueID,loadedXML);
        }
    }
    
    /**
    * Assign the onData method for the XML object
    * 
    * @param xmlObject:XML  the xml object to assign the onData method
    */
    private function setOnData(xmlObject:XML){
        //Set ondata handler to validate data returned in XML object
        xmlObject.onData = function(src){
			Cursor.showCursor(ApplicationParent.C_DEFAULT);
           
			if (src != undefined) {
                //Check for login page
                if(src.indexOf("j_security_login_page") != -1){
                    //TODO DI 12/04/05 deal with error from session timeout/server error
                    trace('j_security_login_page received');
                    this.onLoad(false);
                }else {
                    //Data alright, must be a packet, allow onLoad event
                    this.parseXML(src);
                    this.loaded=true;
                    this.onLoad(true,this);
                }
            } else {
                this.onLoad(false);
            }            
        }
    }    
	
	/**
	 * Serializes an object into WDDX XML
	 * @usage  	var wddxXML:XML = commsInstance.serializeObj(obj); 
	 * @param   dto 	The object to be serialized	
	 * @return  sXML	WDDX Serialized XML
	 */
	public function serializeObj(dto:Object):XML{
		var sXML:XML = new XML();
		sXML = wddx.serialize(dto);
		Debugger.log('monitor data send to Server:'+sXML.toString(),Debugger.GEN,' serializeObj ','Communication');
		return sXML;
	}
    
	/**
	 * Deserializes WDDX XML to produce an object
	 * @usage  	var wddxXML:XML = commsInstance.serializeObj(obj); 
	 * @param   wddx 	The XML to be de-serialized	
	 * @return  Object	de-serialized object
	 */
	public function deserializeObj(xmlObj:XML):Object{
        var dto:Object = wddx.deserialize(xmlObj);
		Debugger.log('monitor data recieved from Server:'+xmlObj.toString(),Debugger.GEN,' deserializeObj ','Communication');
		return dto;
	}
	
	
    
    /**
    * Finds handler in queue, dispatches object to it and deletes item from queue
    */
    private function dispatchToHandlerByID (ID:Number,o:Object) {
        var index:Number = getIndexByQueueID(ID);
        //Dispatch handler passing in object stored under messageValue
        queue[index].handler(o);
        //Now that request has been dealt with remove item from queue
        removeFromQueue(ID);
    }
    
    /**
    * Adds a key handler pair to the queue
    */
    private function addToQueue(handler:Function){
        //Add to the queue and increment the queue ID
        queue.push({queueID:queueID++,handler:handler});
        
        //Reset queueID?
        if(queueID>=MAX_REQUESTS) {
            queueID=0;
        }
    }
    
    /**
    * removes a key handler pair from the queue
    * @returns boolean indicating success
    */
    private function removeFromQueue(queueID:Number):Boolean{
        //find item and delete it
        var index:Number = getIndexByQueueID(queueID);
        if(index!=-1) {
            //Remove the item from the queue
            queue.splice(index,1);
            return true;
        }else {
            return false;
            Debugger.log('Item not found in queue :' + queueID,Debugger.GEN,'removeFromQueue','Communication');
        }
    }
    
    /**
    * searches queue for item by key 
    * @returns Array index value, -1 if key not found
    */
    private function getIndexByQueueID(queueID):Number {
        //Go through handlers and remove key
        for(var i=0;i<queue.length;i++) {
            //If key found, delete key/handler object from array 
            if(queue[i].queueID==queueID) {
                return i;
            }
        }
        //If we're here then we didn't find it return an error
        return -1;
    }    
    
    /**
    * returns current server URL  
    */
    function get serverUrl():String{
        return _serverURL;
    }

    /**
    * sets current server URL  
    */
    function set serverUrl(val:String){
        _serverURL = val;
    }
    
    /**
    * @returns Number - length of the queue
    */
    function get queueLength():Number {
        return queue.length;
    }
}