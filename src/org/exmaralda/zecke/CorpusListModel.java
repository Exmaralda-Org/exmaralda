/*
 * CorpusListModel.java
 *
 * Created on 7. Juni 2005, 10:51
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.zecke;

import javax.swing.*;
/**
 *
 * @author thomas
 */
public class CorpusListModel extends AbstractListModel {
    
    CorpusTree corpusTree;
    
    /** Creates a new instance of CorpusListModel */
    public CorpusListModel() {
        corpusTree = new CorpusTree("Corpus");
    }

    public CorpusTree getCorpusTree(){
        return corpusTree;
    }
    
    public void setCorpusTree(CorpusTree ct){
        int formerSize = getSize();
        corpusTree = ct;
        if (formerSize<ct.getChildCount()){
            super.fireIntervalAdded(this, formerSize, ct.getChildCount()-1);
        }
        super.fireContentsChanged(this, 0, ct.getChildCount()-1);
    }
    
    public Object getElementAt(int param) {
        CorpusElement ce = ((CorpusElement)(corpusTree.getChildAt(param)));
        return ce;
    }

    public int getSize() {
        return corpusTree.getChildCount();
    }
    
    public void addElement(CorpusElement ce){
        corpusTree.addCorpusElement(ce);
        int newSize = corpusTree.getChildCount();
        super.fireIntervalAdded(this, newSize-1, newSize -1);
    }
    
    public void removeElementAt(int index){
        corpusTree.remove(index);
        this.fireIntervalRemoved(this, index, index);
    }
    
}
