package org.exmaralda.coma.model;

import java.util.HashMap;

import org.jdom.Element;

public class Description extends TypeClass implements TypeInterface{
    
    HashMap <String,String> values;

    @Override
    public String toHTML() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Element toXML() {
	Element e = new Element("Description");
	for (String k : values.keySet()) {
	    Element ke = new Element("Key");
	    ke.setAttribute("Name",k);
	    ke.setText(values.get(k));
	}
	return e;
    }

}
