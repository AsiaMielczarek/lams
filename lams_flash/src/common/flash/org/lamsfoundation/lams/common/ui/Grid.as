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

import org.lamsfoundation.lams.common.ui.*
import com.polymercode.Draw;

/**
* Grid  - For positioning and calulcating relative to a grid in canvas
* @author   DI
*
*/ 

class Grid {
	//constants
	private static var LINE_THICKNESS:Number = 1;								//lines
	private static var LINE_ALPHA:Number = 30;
	private static var LINE_COLOR:Number = 0x000000;

	public static var SMALL_GRID_H_SPACING:Number = 21;				        //Small grid dimensions
	public static var SMALL_GRID_V_SPACING:Number = 28;
	public static var LARGE_GRID_H_SPACING:Number = 5 * SMALL_GRID_H_SPACING;  //Large grid dimensions
	public static var LARGE_GRID_V_SPACING:Number = 5 * SMALL_GRID_V_SPACING;
	
	public static var GRID_STYLE:String = 'dots';
		 
	//Vars
    private var gridWidth:Number;
    private var gridHeight:Number
	
	/**
    * Constructor
    */
	function Grid(){
	}
    
	
	/**
    * Calculates screen x&y from grid coords
    */
	public static function pixelsToLargeGrid(x:Number,y:Number):Point{
		var point:Point = new Point(x,y);
		point.x *= LARGE_GRID_H_SPACING;
		point.y *= LARGE_GRID_V_SPACING;
		return point;
	}
	
	/**
    * Calculates screen x&y from grid coords
    */ 
	public static function largeGridToPixels(x:Number,y:Number):Point{
		var point:Point = new Point(x,y);
		point.x /= LARGE_GRID_H_SPACING;
		point.y /= LARGE_GRID_V_SPACING;
		return point;
	}
	
	/**
    * Calculate pixels for small grid coords
    */
	public static function smallGridToPixels(x:Number,y:Number):Point{
		var point:Point = new Point(x,y);
		point.x *= SMALL_GRID_H_SPACING;
		point.y *= SMALL_GRID_V_SPACING;
		return point;
	}

	/**
    * Calculate small grid coords from pixels
    */
	public static function pixelsToSmallGrid(x:Number,y:Number):Point{
		var point:Point = new Point(x,y);
		point.x *= SMALL_GRID_H_SPACING;
		point.y *= SMALL_GRID_V_SPACING;
		return point;
	}
	 
	/**
    * Draw the grid
    */
	public static function drawGrid(target_mc,gridWidth,gridHeight,hGap,vGap):Object{
		//if the gird already exists - remove it
		if(target_mc.grid != null){
			target_mc.grid.removeMovieClip();
		}
        //If the grid already exists remove it
        if(target_mc.grid) {
            target_mc.grid.removeMovieClip();
        }
  		//Create the clip
		var _mc:MovieClip = target_mc.createEmptyMovieClip('grid',target_mc.getNextHighestDepth());

		if (target_mc==undefined){
			//first make sure that the reference is valid
			return false;
		}
		//draw horizontal lines
		var hLines:Number = Math.floor(gridWidth/hGap);
		_mc.lineStyle(LINE_THICKNESS,LINE_COLOR,LINE_ALPHA);
		for (var i=0; i<= hLines;i++){
			var x:Number = i*hGap;
			
			if(GRID_STYLE == 'dots'){
				Draw.dashTo(_mc,x,0,x,gridHeight,1,hGap,1,0xCCCCCC);
			}else{
				_mc.moveTo(x,0);
				_mc.lineTo(x,gridHeight);
			}
			
			
		}
		
		return _mc;
		
	}

}
 
 