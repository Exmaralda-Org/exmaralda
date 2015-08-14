package org.exmaralda.coma.model;

import org.jdom.Element;

public class Communication extends TypeClass implements TypeInterface{
    
    public Communication() {
	super();
	allowedConnections.add(Speaker.class);
	
    }

    Description description;

    @Override
    public String toHTML() {
	return null;
    }

    @Override
    public Element toXML() {
	return null;
    }


}
