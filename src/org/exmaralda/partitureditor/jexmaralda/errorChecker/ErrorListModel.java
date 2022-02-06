/*
 * ErrorListModel.java
 *
 * Created on 14. November 2007, 17:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.errorChecker;

import org.jdom.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class ErrorListModel extends javax.swing.AbstractListModel {
    
    List errorList;
    Document d;
    int transcriptCount;
    
    /** Creates a new instance of ErrorListModel */
    public ErrorListModel(Document errorDocument) {
        try {
            errorList = errorDocument.getRootElement().getChild("errors").getChildren("error");
            d = errorDocument;
            List l = XPath.selectNodes(d, "//error");
            Set<String> transcripts = new HashSet<>();
            for (Object o : l){
                String tr = ((Element)o).getAttributeValue("file");
                transcripts.add(tr);
            }
            transcriptCount = transcripts.size();
        } catch (JDOMException ex) {
            Logger.getLogger(ErrorListModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object getElementAt(int index) {
        return errorList.get(index);
    }
    
    public Document getErrorList(){
        return d;
    }

    @Override
    public int getSize() {
        return errorList.size();
    }
    
    public int getTranscriptionCount(){
        return transcriptCount;
    }
    
    public void setDone(int index){
        Element error = (Element)(getElementAt(index));
        error.setAttribute("done","yes");
        fireContentsChanged(this, index, index);
    }
    
}
