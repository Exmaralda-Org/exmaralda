/*
 * CorpusElement.java
 *
 * Created on 11. Juni 2004, 14:31
 */

package org.exmaralda.zecke;

import java.io.*;
import javax.swing.tree.*;

/**
 *
 * @author  thomas
 */

public class CorpusElement extends DefaultMutableTreeNode implements XMLable {
    
    boolean selected;
    String transcriptionName;
    String transcriptionPath;
    
    /** Creates a new instance of CorpusElement */
    public CorpusElement(boolean s, String tn, String tp) {
        selected = s;
        transcriptionName = tn;
        transcriptionPath = tp;
    }
    
    public boolean isSelected(){
        return selected;
    }
    
    public void setSelected(boolean value){
        selected = value;
    }
    
    public String getTranscriptionName(){
        return transcriptionName;
    }
    
    public String getTranscriptionPath(){
        return transcriptionPath;
    }
    
    public boolean isPathValid(){
        return new File(transcriptionPath).canRead();
    }
    
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<corpus-element");
        sb.append(" name=\"" + transcriptionName + "\" path=\"");
        sb.append(new File(transcriptionPath).getPath() + "\"/>");
        return sb.toString();
    }
        
    
}
