/*
 * TimeUtilities.java
 *
 * Created on 8. Januar 2002, 09:58
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

/**
 *
 * @author  Thomas
 * @version 
 */
public class TimeUtilities {

    /** Creates new TimeUtilities */
    public TimeUtilities() {
    }
    
    /** parses a string and returns the number it represents
     *  possible format: xxxx.yyyyyy and
     *                   hh:mm:ss.xxxx */
    static double parseTimeString(String text) throws NumberFormatException {
        if (text.length()==0){return -0.1;}   // string is empty
        for (int pos=0; pos<text.length(); pos++){  // check permissible characters
            char c = text.charAt(pos);
            if ((c!=':') && (c!='.') && (c!='0') && (c!='1') && (c!='2') && 
                (c!='3') && (c!='4') && (c!='5') && (c!='6') && (c!='7') && (c!='8') && (c!='9')) {
                    throw new NumberFormatException();
            }           
        }

        if (text.lastIndexOf(':')>text.lastIndexOf('.')){
            throw new NumberFormatException();
        }

        if (text.lastIndexOf(':')<0){   // i.e. format MUST BE xxxx.yyyyyy
            Double.parseDouble(text);
            return new Double(text).doubleValue();
        }
        
        // format is hh:mm:ss.xxxx
        double result = 0;
        int index = text.lastIndexOf(':');
        String secondsText = text.substring(index+1);
        double seconds = Double.parseDouble(secondsText);
        if (seconds >= 60) {
            throw new NumberFormatException("Value of seconds greater than 59. ");
        }
        result+=seconds;
        
        if (text.equals(":")){return result;}
        
        text=text.substring(0,index);
        
        index = text.lastIndexOf(':');
        if (index<0){   // only minutes left
            int minutes=Integer.parseInt(text);
            if (minutes>59){
                throw new NumberFormatException("Value of minutes greater than 59. ");
            }
            result+=minutes*60;
            return result;
        }
        String minutesText = text.substring(index+1);
        int minutes = Integer.parseInt(minutesText);
        if (minutes >= 60) {
            throw new NumberFormatException("Value of minutes greater than 59. ");
        }
        result+=minutes*60;
        
        if (text.equals(":")){return result;}
        
        text=text.substring(0,index);
        
        int hours = Integer.parseInt(text);
        result+=hours*60*60;        
        
        return result;
    }
    
    static String makeTimeString (double time, String format){

        int hourDig=0;
        int minDig=0;
        int secDig=0;
        int miliDig=0;
        
        for (int pos=0; pos<format.length(); pos++){
            char c = format.charAt(pos);
            if (c=='h') {hourDig++;}
            if (c=='m') {minDig++;}
            if (c=='s') {secDig++;}
            if (c=='x') {miliDig++;}
        }
        
        if ((hourDig>0) || (minDig>2)) {minDig=2;}
        if ((minDig>0) || (secDig>2)) {secDig=2;}
        
        String result = new String();

        long hours = Math.round(Math.floor(time/3600));
        time-=hours*3600;
        long minutes = Math.round(Math.floor(time/60));
        time-=minutes*60;
        long seconds = Math.round(Math.floor(time));
        time-=seconds;
        long miliseconds = Math.round(time*1000);
        
        if ((hours>0) || (hourDig)>0){
            String hoursString = Long.toString(hours);
            if (hoursString.length()<hourDig){
                for (int i=0; i<hourDig-hoursString.length(); i++){result+="0";}
            }
            result+=hoursString + ":";
        }
        
        if ((minutes>0) || (minDig)>0){
            String minutesString = Long.toString(minutes);
            if (minutesString.length()<minDig){
                result+="0";
            }
            result+=minutesString + ":";
        }
        
        String secondsString = Long.toString(seconds);
        if (secondsString.length()<secDig){
            result+="0";
        }
        result+=secondsString + ".";
        
        String milisecondsString = Long.toString(miliseconds);
        for (int i=0; i<miliDig; i++){
            if (i<milisecondsString.length()){
                result+=milisecondsString.substring(i,i+1);
            }
            else {
                result+="0";
            }
        }
        
        return result;
    }
    
}
