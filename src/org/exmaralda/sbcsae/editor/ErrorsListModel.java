/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.editor;

import java.util.*;
import org.jdom.*;
import org.jdom.xpath.*;

/**
 *
 * @author thomas
 */
public class ErrorsListModel extends javax.swing.AbstractListModel{
    
    private Vector<Element> errors = new Vector<Element>();

    public ErrorsListModel(Document d) throws JDOMException {
        XPath iu = XPath.newInstance("//error");
        List l = iu.selectNodes(d);
        for (Object o : l){
            Element e = (Element)o;
            errors.addElement(e);
        }        
    }

    public int getSize() {
        return errors.size();
    }

    public Object getElementAt(int index) {
        return errors.elementAt(index);
    }

}
