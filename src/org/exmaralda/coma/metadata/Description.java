package org.exmaralda.coma.metadata;

import java.util.HashMap;

import org.jdom.Element;

public class Description extends Metadata {
    Metadata descriptionParent;
    HashMap<String, String> descriptionMap;

    public String toString() {
	String out = "Description from " + descriptionParent.getName() + "\n";
	for (String key : descriptionMap.keySet()) {
	    out += key + "=" + descriptionMap.get(key) + "\n";
	}
	return out;
    }

    public boolean containsKey(String key) {
	return descriptionMap.containsKey(key);
    }

    public boolean containsValue(String value) {
	return descriptionMap.containsValue(value);
    }

    @Override
    public String getName() {
	return "Description";
    }

    public Element toElement() {
	Element e = new Element("Description");
	for (String key : descriptionMap.keySet()) {
	    e.addContent(new Element("Key").setAttribute("name", key).setText(
		    descriptionMap.get(key)));
	}
	return e;
    }
}
