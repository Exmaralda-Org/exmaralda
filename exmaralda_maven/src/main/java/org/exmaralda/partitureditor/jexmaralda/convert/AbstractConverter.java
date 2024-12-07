/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thomas.schmidt
 */
public abstract class AbstractConverter {
    
    List<ConverterListener> converterListenerList;

    public AbstractConverter() {
        this.converterListenerList = new ArrayList<>();
    }
    
    public void addConverterListener(ConverterListener converterListener){
        converterListenerList.add(converterListener);
    }
    
    public void fireConverterEvent(ConverterEvent converterEvent){
        for (ConverterListener converterListener : converterListenerList){
            converterListener.processConverterEvent(converterEvent);
        }
    }
    
}
