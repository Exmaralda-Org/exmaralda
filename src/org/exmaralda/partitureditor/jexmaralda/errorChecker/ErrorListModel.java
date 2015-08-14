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

/**
 *
 * @author thomas
 */
public class ErrorListModel extends javax.swing.AbstractListModel {
    
    List errorList;
    Document d;
    
    /** Creates a new instance of ErrorListModel */
    public ErrorListModel(Document errorDocument) {
        errorList = errorDocument.getRootElement().getChild("errors").getChildren("error");
        d = errorDocument;
    }

    public Object getElementAt(int index) {
        return errorList.get(index);
    }
    
    public Document getErrorList(){
        return d;
    }

    public int getSize() {
        return errorList.size();
    }
    
    public void setDone(int index){
        Element error = (Element)(getElementAt(index));
        error.setAttribute("done","yes");
        fireContentsChanged(this, index, index);
    }
    
}
