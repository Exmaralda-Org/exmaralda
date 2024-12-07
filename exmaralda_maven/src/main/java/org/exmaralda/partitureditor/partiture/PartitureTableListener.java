/*
 * PartitureTableListener.java
 *
 * Created on 4. Dezember 2001, 09:23
 */

package org.exmaralda.partitureditor.partiture;

/**
 * an interface for classes that want to listen to changes of a PartiturTable
 * solely needed to avert the main frame when the filename changes
 * @author  Thomas
 * @version 
 */
public interface PartitureTableListener extends java.util.EventListener {
    
    public void partitureTablePropertyChanged(PartitureTableEvent e);

}

