/**
 * AJGL, an abstract java game library that provides useful functions for making a game.
 * Copyright (C) 2014 Tyler Bucher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.tylerb318;


/**
 * This class is designed to be a utility class for Legendary Start
 * @author Tyler Bucher
 */
public class LegendaryStartUtil {
    
    /**
     * Converts the provided milliseconds to a string
     * representation of the highest time value.
     * @param ms - Time in milliseconds
     * @return The heights time value
     */
    public static String msToTime(long ms) {
        long cTime = ms/1000; // Seconds
        String time = Long.toString(cTime)+" Seconds";
        
        cTime /= 60; // minutes
        if(cTime>0) time = Long.toString(cTime)+" Minutes";
        else return time;
        cTime /= 60; // Hours
        if(cTime>0) time = Long.toString(cTime)+" Hours";
        else return time;
        cTime /= 24; // Days
        if(cTime>0) time = Long.toString(cTime)+" Days";
        else return time;
        cTime /= 365; // years
        if(cTime>0) time = Long.toString(cTime)+" Years";
        return time;
    }
}
