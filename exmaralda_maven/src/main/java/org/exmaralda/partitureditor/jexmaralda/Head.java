//package jexmaralda;

package org.exmaralda.partitureditor.jexmaralda;

import java.io.*;

/*
 * Head.java
 *
 * Created on 6. Februar 2001, 12:26
 */


/* Revision History
 *  0   06-Feb-2001 Creation according to revision 0 of 'exmaralda-time-transcription.dtd'
 *                  and 'exmaralda-segment-transcription.dtd'
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

public class Head extends Object {

    private MetaInformation metaInformation;
    private Speakertable speakertable;   
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new Head */
    public Head() {
        metaInformation = new MetaInformation();
        speakertable = new Speakertable();
    }
    
    /** returns a copy of this head */
    public Head makeCopy(){
        Head result = new Head();
        result.setMetaInformation(this.getMetaInformation().makeCopy());
        result.setSpeakertable(this.getSpeakertable().makeCopy());
        return result;
    }
    
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns the meta information of this head */
    public MetaInformation getMetaInformation(){
        return metaInformation;
    }
    
    /** sets the meta information to the specified value */
    public void setMetaInformation(MetaInformation mi){
        metaInformation=mi;
    }
    
    /** returns the speakertable of this head */
    public Speakertable getSpeakertable(){
        return speakertable;
    }
    
    /** sets the speakertable to the specified value */
    public void setSpeakertable(Speakertable st){
        speakertable=st;
    }
   
    

    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns the head as an XML element &lt;head&gt; as
     *  specified in the corresponding dtds */
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<head>\n");
        sb.append(metaInformation.toXML());
        sb.append(speakertable.toXML());
        sb.append("</head>\n");
        return sb.toString();
    }
    

    // ********************************************
    // ********** HTML OUTPUT *********************
    // ********************************************
    
    public String toHTML(){
        try{
            return new org.exmaralda.partitureditor.jexmaralda.convert.HTMLConverter().HeadToHTML(this);
        } catch (Throwable t){
            t.printStackTrace();
            System.out.println(t.getMessage());
            return new String();
        }
        /*StringBuffer sb = new StringBuffer();
        sb.append(getMetaInformation().toHTML());
        sb.append(getSpeakertable().toHTML());
        return sb.toString();*/
    }
    
    public void writeHTMLToFile(String filename){
        try{
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(filename));        
            fos.write(toHTML().getBytes("UTF8"));
            fos.close();
            System.out.println("document written.");
        } catch (IOException ioe){}
    }    
    
    // ********************************************
    // ********** RTF OUTPUT *********************
    // ********************************************
    
    public String toRTF(){
        StringBuffer sb = new StringBuffer();
        sb.append(getMetaInformation().toRTF());
        sb.append(getSpeakertable().toRTF());
        return sb.toString();
    }
    


    

}