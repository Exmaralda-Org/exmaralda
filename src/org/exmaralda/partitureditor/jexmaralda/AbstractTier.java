/*
 * AbstractTier.java
 *
 * Created on 1. August 2002, 10:30
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;

/**
 * Abstract parent class of abstract event tier 
 * @author  Thomas
 * @version 
 */
public class AbstractTier extends Vector {

    private String id;
    private String speaker;
    private String category;
    private String type;
    private String displayName;
 
    /** Creates new AbstractTier */
    public AbstractTier() {
        id = new String();
        speaker = null;
        category = new String();
        type = new String();        
    }
    
    /** Creates new AbstractTier with id i, speaker s, category c and type t*/
    public AbstractTier(String i, String s, String c, String t) {
        id = i;
        speaker = s;
        category = c;
        type = t;        
    }
    
    /** Creates new AbstractTier with id i, speaker s, category c, type t and display-name d */
    public AbstractTier(String i, String s, String c, String t, String d) {
        id = i;
        speaker = s;
        category = c;
        type = t;        
        displayName = d;
    }
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns id */
    public String getID(){
        return id;
    }
  
    /** returns speakerID */
    public String getSpeaker(){
        return speaker;
    }
    
    /** returns type */
    public String getType(){
        return type;
    }
    
    /** returns category */
    public String getCategory(){
        return category;
    }
    
    /** returns displayName */
    public String getDisplayName(){
        return displayName;
    }
    
    // returns name|type|category
    public String getPraatName() {
        String name = "";
        if (getSpeaker()!=null){
            name+=getSpeaker();
        } else {
            name+="NO_SPEAKER";
        }
        name+="|";
        name+=getType();
        name+="|";
        name+=getCategory();
        return name;
    }
    
    
    /** sets id to value of i */
    public void setID(String i){
        id=i;
    }
    
    /** sets speaker id to value of i */
    public void setSpeaker(String s){
        speaker=s;
    }
    
    /** sets type to value of t */
    public void setType(String t){
        type=t;
    }
    
    /** sets category to value of c */
    public void setCategory(String c){
        category=c;
    }
    
    /** sets display name to value of dn */
    public void setDisplayName(String dn){
        displayName=dn;
    }
    /** returns a description for, e.g. a label, of this tier */
    public String getDescription(Speakertable st){
        StringBuilder sb = new StringBuilder();
        if (getSpeaker()!=null){
            try {
                sb.append(st.getSpeakerWithID(this.getSpeaker()).getAbbreviation());
            } catch (JexmaraldaException je) {}
            if (getCategory().length()>0){
                sb.append(" ");
            }
        }
        if (getCategory().length()>0){
            sb.append("[").append(getCategory()).append("]");
        }
        return sb.toString();        
    }
    
    

}
