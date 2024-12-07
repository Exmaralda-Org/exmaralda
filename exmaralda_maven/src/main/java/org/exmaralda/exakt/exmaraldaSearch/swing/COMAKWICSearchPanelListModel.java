/*
 * COMAKWICSearchPanelListModel.java
 *
 * Created on 20. Juni 2007, 10:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.util.*;

/**
 *
 * @author thomas
 */
public class COMAKWICSearchPanelListModel extends javax.swing.AbstractListModel {
    
    Vector content = new Vector();
    
    /** Creates a new instance of COMAKWICSearchPanelListModel */
    public COMAKWICSearchPanelListModel() {
    }

    public Object getElementAt(int index) {
        return content.elementAt(index);
    }
    
    public void addElement(Object o){
        content.addElement(o);
        int i = content.size()-1;
        fireIntervalAdded(this,i,i);
    }

    public int getSize() {
        return content.size();
    }
    
    public void removeElement(Object o){
        int i = content.indexOf(o);
        content.removeElement(o);
        fireIntervalRemoved(this,i,i);
    }

    protected void fireContentsChanged(Object source, int index0, int index1) {
        super.fireContentsChanged(source, index0, index1);
    }
    
}
