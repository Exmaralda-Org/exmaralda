/*
 * TimeStringFormatter.java
 *
 * Created on 10. März 2008, 14:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.utilities;

/**
 *
 * @author thomas
 */
public class TimeStringFormatter {

    
    static String REGEX = "(\\d{0,3}:)?(\\d{1,2})(\\.\\d{1,3})?";
    
    /** Creates a new instance of TimeStringFormatter */
    public TimeStringFormatter() {
    }
    
    /** Creates a new instance of TimeStringFormatter */
    public TimeStringFormatter(String regex) {
    }

    
    public static double parseString(String text) throws Exception {
        return parseString(text, REGEX);
    }
    
    public static double parseString(String text, String regex) throws Exception {

        if (text.length()==0){
            return -1;
        }

        if (!(text.matches(regex))){
            throw new Exception("Wrong syntax");
        }

        try {
            double d = Double.parseDouble(text);
            return d * 1000.0;
        } catch (NumberFormatException nfe){
            // go on...
        }
        
        
        String miliseconds = "0";
        String textWithoutMiliseconds = text;
        if (text.contains(".")) {
            int index = text.lastIndexOf(".");
            if (index<text.length()-1){
                miliseconds = text.substring(index+1);
            }
            textWithoutMiliseconds = text.substring(0,index);
        }
        
        int negative = +1;
        if (text.startsWith("-")){
            negative = -1;
            textWithoutMiliseconds = textWithoutMiliseconds.substring(1);
        }

        String textWithoutSeconds = "";
        String seconds = "0";
        if (textWithoutMiliseconds.contains(":")){
            int index = textWithoutMiliseconds.lastIndexOf(":");
            seconds = textWithoutMiliseconds.substring(index+1);
            textWithoutSeconds = textWithoutMiliseconds.substring(0,index);
        } else {
            seconds = textWithoutMiliseconds;
            textWithoutSeconds = "";
        }

        String textWithoutMinutes = "";
        String minutes = "0";
        if (textWithoutSeconds.contains(":")){
            int index = textWithoutSeconds.lastIndexOf(":");
            seconds = textWithoutSeconds.substring(index+1);
            textWithoutMinutes = textWithoutSeconds.substring(0,index);
        } else {
            minutes = textWithoutSeconds;
            textWithoutMinutes = "";
        }

        try {
            double allMiliseconds = Double.parseDouble(miliseconds);
            allMiliseconds += Double.parseDouble(seconds) * 1000.0;
            if (textWithoutSeconds.length()>0){
                allMiliseconds += Double.parseDouble(textWithoutSeconds) * 60000.0;
            }
            if (textWithoutMinutes.length()>0){
                allMiliseconds += Double.parseDouble(textWithoutMinutes) * 60.0 * 60000.0;
            }
            return allMiliseconds * negative;
        } catch (NumberFormatException numberFormatException) {
            throw numberFormatException;
        }
    }

    public static String formatSeconds(int seconds){
        
        int minutes = seconds/60;
        String formatted = "";
        if (minutes<10) formatted+="0";
        formatted+=Integer.toString(minutes) + ":";
        
        int remainingSeconds = seconds - 60*minutes;
        if (remainingSeconds<10) formatted+="0";
        formatted+=Integer.toString(remainingSeconds);
        
        return formatted;
    }
    
    public static String formatMiliseconds(double miliseconds){
        return formatMiliseconds(miliseconds, false);
    }
    
    public static String formatMiliseconds(double miliseconds, boolean doHours){
        
        int seconds = (int)(miliseconds / 1000);
        //System.out.println(seconds);
        int minutes = seconds/60;
        //System.out.println(minutes);
        String formatted = "";
        int hours = 0;
        if (doHours){
            hours = minutes/60;
            if (hours<10) formatted+="0";
            formatted+=Integer.toString(hours) + ":";
            minutes-=hours*60;
            //System.out.println(minutes);
        }
        
        if (minutes<10) formatted+="0";
        formatted+=Integer.toString(minutes) + ":";
        
        double remainingMiliseconds = miliseconds - 60000*minutes - 60*60000*hours;
        //System.out.println(remainingMiliseconds);
        if (remainingMiliseconds<10000) formatted+="0";
        formatted+=Double.toString(remainingMiliseconds/1000.0);

        return formatted;
    }

    public static String formatMiliseconds(double miliseconds, int digitsAfterComma){
        return formatMiliseconds(false, miliseconds, digitsAfterComma);
    }
    
    public static String formatMiliseconds(boolean doHours, double miliseconds, int digitsAfterComma){
        
        String formatted = formatMiliseconds(miliseconds, doHours);
        int index = formatted.lastIndexOf('.');
        if (digitsAfterComma>0){
            return formatted.substring(0,Math.min(index+digitsAfterComma+1,formatted.length()));
        } else {
            if (index<0){
                return formatted;
            } else {
                return formatted.substring(0,Math.min(index, formatted.length()));
            }
        }
    }
    
    public static String formatMiliseconds(double miliseconds, int digitsAfterComma, boolean shorten){

        String formatted = formatMiliseconds(miliseconds, digitsAfterComma);
        if ((miliseconds>59999) && (formatted.startsWith("00:"))){
            return formatted.substring(3);
        }
        return formatted;
    }
    
    public static void main(String[] args){
        System.out.println(formatMiliseconds(true, 60*60*1000, 0));
    }

}
