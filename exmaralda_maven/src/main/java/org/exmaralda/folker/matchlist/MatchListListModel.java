/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.folker.matchlist;

import javax.swing.AbstractListModel;
import org.jdom.Element;

/**
 *
 * @author Schmidt
 */
public class MatchListListModel extends AbstractListModel {

    MatchList matchList;

    public MatchListListModel(MatchList matchList) {
        super();
        this.matchList = matchList;
    }
    
    
    
    @Override
    public int getSize() {
        return matchList.getSize();        
    }

    @Override
    public Object getElementAt(int index) {
        return matchList.getElementAt(index);
    }
    
    public void setChecked(int index){
        Element e = (Element) getElementAt(index);
        e.setAttribute("done", "yes");
        fireContentsChanged(this, index, index);
    }
    
}
