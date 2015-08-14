//package jexmaralda;

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import org.exmaralda.partitureditor.interlinearText.RTFUtilities;
/*
 * UDInformationHashtable.java
 *
 * Created on 2. Februar 2001, 14:50
 */

/* Revision History
 *  0   02-Feb-2001 Creation according to revision 0 of 'exmaralda-time-transcription.dtd'
 *                  and 'exmaralda-segment-transcription.dtd'
 *  1   07-Aug-2001 changed implementation : now extends Vector instead of Hashtable
 *                  in order to ensure that attributes are kept in order
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */
public class UDInformationHashtable extends Vector {

    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    private Hashtable values;
    
    /** Creates new empty UDInformationHashtable */
    public UDInformationHashtable() {
        super();
        values = new Hashtable();        
    }
    
    /** Creates new UDInformationHashtable with the specified Attributes */
    public UDInformationHashtable(String[] attributeNames) {
        super();
        values = new Hashtable();
        for (int i=0; i<attributeNames.length; i++){
            addElement(attributeNames[i]);
            values.put(attributeNames[i],new String());
        }
    }
    
    /** Creates new UDInformationHashtable with the attributes and values specified in udi*/
    public UDInformationHashtable(String[][] udi){
        super();
        values = new Hashtable();
        for (int pos=0; pos<udi[0].length; pos++){
            addElement(udi[0][pos]); 
            values.put(udi[0][pos],udi[1][pos]);
       }        
    }
    
    /** returns a copy of this UDInformationHashtable */
    public UDInformationHashtable makeCopy(){
        UDInformationHashtable result = new UDInformationHashtable();
        for (int pos=0; pos<getNumberOfAttributes(); pos++){
            result.setAttribute(this.getAttributeAt(pos),this.getValueOfAttribute(this.getAttributeAt(pos)));
        }
        return result;    
    }
    
    public void clear(){
        super.clear();
        values.clear();
    }
    
    public String getAttributeAt(int pos) {
        return(String)elementAt(pos);
    }
    
    public void renameAttribute(int pos, String newName){
        String oldName = getAttributeAt(pos);
        String value = getValueOfAttribute(oldName);
        values.remove(oldName);
        values.put(newName,value);
        this.setElementAt(newName, pos);
    }
    
    
    /** Returns the value of the attribute with the specified name.
     *  Returns null if the specified attribute does not exist.
     *  @param attributeName    the name of the attribute
     *  @return                 the corresponding value or null
     */
    public String getValueOfAttribute(String attributeName){
        if (values.containsKey(attributeName)) {
            return (String)values.get(attributeName);
        }
        return null;
    }

    
    public void removeAttribute(String attributeName){
        removeElement(attributeName);
        values.remove(attributeName);
    }
    
    public boolean containsAttribute(String attributeName){
        return values.containsKey(attributeName);
    }
    /** Sets the attribute with the specified name to the specified value.
     *  @param attributeName    the name of the attribute
     *  @param value            the value this attribute is to be set to
     */  
    public void setAttribute(String attributeName, String value){
        if (!containsAttribute(attributeName)){
            addElement(attributeName);
        }
        values.put(attributeName, value);
    }
    
    public void moveElementUp(int position){
        Object element = set(position, this.get(position-1));
        set(position-1, element);
    }
    
    /** returns number of user defined attributes 
      * @return                 the number of user defined attributes
      */
    public int getNumberOfAttributes(){
        return size();
    }
    
    /** returns a string array with all attribute names, null if there are none 
      * @return                 array with all attribute names or null
      */
    public String[] getAllAttributes(){
        if (getNumberOfAttributes()>0){
            String[] result = new String[getNumberOfAttributes()];
            int i=0;
            for (int pos=0; pos<getNumberOfAttributes(); pos++){
                result[i] = (String)getAttributeAt(pos);     
                i++;
            }           
            return result;
        } else {
            return null;
        }
    }
    
    
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns a string corresponding to XML-elements &lt;ud-information&gt;
     *  as specified in the corresponding dtds */
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        String[] allAttributes = getAllAttributes();
        if (allAttributes!=null){
            for (int i=0; i< allAttributes.length; i++){
                sb.append("<ud-information attribute-name=\"");
                sb.append(StringUtilities.toXMLString(allAttributes[i]));
                sb.append("\">");
                sb.append(StringUtilities.checkCDATA(getValueOfAttribute(allAttributes[i])));
                sb.append("</ud-information>");            
            }
        }
        return sb.toString();
    }

    
    // ********************************************
    // ********** RTF OUTPUT *********************
    // ********************************************
    
    public String toRTF(){
        StringBuffer sb = new StringBuffer();
        String[] allAttributes = getAllAttributes();
        if (allAttributes!=null){
            for (int i=0; i< allAttributes.length; i++){
                //sb.append("{\\fs20\\tab\\b " + StringUtilities.toANSI(allAttributes[i]) + ": \\plain\\fs20 ");
                // changed 01-02-2010: need to escape characters for RTF output
                sb.append("{\\fs20\\tab\\b " + RTFUtilities.toEscapedRTFString(allAttributes[i]) + ": \\plain\\fs20 ");
                //sb.append(StringUtilities.toANSI(getValueOfAttribute(allAttributes[i])));
                sb.append(RTFUtilities.toEscapedRTFString(getValueOfAttribute(allAttributes[i])));
                sb.append("}\\par");
            }
        }
        return sb.toString();        
    }
    

            

    
}