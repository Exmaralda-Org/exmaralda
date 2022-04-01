/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import javax.swing.AbstractListModel;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author thomas.schmidt
 */
public class ListEventsListModel extends AbstractListModel {

    Tier tier;

    public ListEventsListModel(Tier tier) {
        this.tier = tier;
    }
    

    @Override
    public int getSize() {
        return tier.getNumberOfEvents();
    }

    @Override
    public Object getElementAt(int index) {
        return tier.getEventAt(index).getDescription();
    }
    
}
