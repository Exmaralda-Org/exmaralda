package org.exmaralda.partitureditor.svgPanel;

import java.util.Observable;

public class XPointerObservable extends Observable {

	public final static XPointerObservable _instance = new XPointerObservable();
	
	private XPointerObservable() {}
	
	private String currentXPointer;
	
	public void setCurrentXPointer(String xpointer) {
		setChanged();
		this.currentXPointer = xpointer;
		notifyObservers(xpointer);
	}
	
	public String getCurrentXPointer() {
		return currentXPointer;
	}
}
