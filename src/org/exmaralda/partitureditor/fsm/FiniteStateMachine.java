/*
 * FiniteStateMachine.java
 *
 * Created on 29. Juli 2002, 15:14
 */

package org.exmaralda.partitureditor.fsm;

import java.util.*;
/**
 *
 * @author  Thomas
 * @version 
 */

public class FiniteStateMachine extends Hashtable {

    String start;
    String end;
    public String name = new String();

    // set to true only for debugging, slows down everything!
    boolean VERBOSE = false;
    
    /** Creates new FiniteStateMachine */
    public FiniteStateMachine() {
    }
               
    public void setStartState(String s){
        start = s;
    }

    public boolean isVERBOSE() {
        return VERBOSE;
    }

    public void setVERBOSE(boolean VERBOSE) {
        this.VERBOSE = VERBOSE;
    }
    
  
    
    public void setEndState(String e){
        end = e;
    }
    
    public void putTransition(String source, String target, String input, Output output){
        if (!containsKey(source)){
            put(source, new Hashtable());
        }
        Hashtable transitions = (Hashtable)get(source);
        transitions.put(input, output);        
        transitions.put("#" + input, target);
    }
    
    public void putForbidden(String source, String forbidden){
        if (!containsKey(source)){
            put(source, new Hashtable());
        }
        Hashtable transitions = (Hashtable)get(source);
        transitions.put("###FORBIDDEN###", forbidden);        
    }
    
    public String process(String input) throws FSMException {
        return process(input, true);
    }

    public String process(String input, boolean escapeXML) throws FSMException {
        if (input.length()<1){
            String message = "Empty input string. ";
            throw new FSMException(message, new String());
        }
        StringBuffer result = new StringBuffer();
        String currentState = start;
        String currentForbidden = new String();
        for (int pos=0; pos<input.length(); pos++){
            if (VERBOSE){
                System.out.println("source state : " + currentState);
                System.out.println("current character: " + Character.toString(input.charAt(pos)));
                //System.out.println("Result: " + result.toString());
                //System.out.println("result : " + result.toString());
            }
            
            Character c = new Character(input.charAt(pos));
            Hashtable transitions = (Hashtable)get(currentState);
            if (transitions.containsKey("###FORBIDDEN###")){
                currentForbidden = (String)(transitions.get("###FORBIDDEN###"));
                //System.out.println("current forbidden: " + currentForbidden);
            }
            if (transitions.containsKey(c.toString())){
                Output output = (Output)(transitions.get(c.toString()));
                String outputString = output.process(c.charValue(), escapeXML);
                result.append(outputString);                
                currentState = (String)transitions.get("#" + c.toString());
            } else if (transitions.containsKey("OTH")) {
                Output output = (Output)(transitions.get("OTH"));
                String outputString = output.process(c.charValue(), escapeXML);
                result.append(outputString);                
                currentState = (String)transitions.get("#OTH");
            } else {    // no appropriate transition defined
                String message = "No Transition defined for current state " + currentState + " and character \"" + c.toString() + "\".";
                throw new FSMException(message, result.toString());
            }
            //System.out.println("target state : " + currentState);
            //System.out.println("====================");
        }

        Hashtable transitions = (Hashtable)get(currentState);

        // Added 27-01-2010
        if (transitions.containsKey("###FORBIDDEN###")){
            currentForbidden = (String)(transitions.get("###FORBIDDEN###"));
            //System.out.println("current forbidden: " + currentForbidden);
        }
        // END ADDED
        
        if (!transitions.containsKey("END")){
            String message = "No final Transition defined for state " + currentState;
            throw new FSMException(message, result.toString());
        }
        Output output = (Output)(transitions.get("END"));
        String outputString = output.process(' ', escapeXML);
        result.append(outputString);
        String finalState = (String)transitions.get("#END");

        if (!finalState.equals(end)){
            String message = "Error: " + currentForbidden;
            throw new FSMException(message, result.toString());
        }        
        return result.toString();
    }

}
