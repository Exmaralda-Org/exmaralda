/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import javax.swing.AbstractListModel;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;

/**
 *
 * @author bernd
 */
public class TierListModel extends AbstractListModel {

    BasicTranscription basicTranscription;

    public TierListModel(BasicTranscription basicTranscription) {
        this.basicTranscription = basicTranscription;
    }
    
    
    
    
    
    @Override
    public int getSize() {
        return basicTranscription.getBody().getNumberOfTiers();       
    }

    @Override
    public Object getElementAt(int index) {
        return basicTranscription.getBody().getTierAt(index);
    }

    void swap(int i, int j) {
        basicTranscription.getBody().swapTiers(i, j);
        this.fireContentsChanged(this, i, j);
    }
    
}
