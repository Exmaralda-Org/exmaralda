/*
 * Speaker.java
 *
 * Created on 7. Mai 2008, 11:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class Speaker {
    
    String identifier;
    String name;
    
    /** Creates a new instance of Speaker */
    public Speaker(String id) {
        setIdentifier(id);
        setName(new String());
    }
    
    public String getIdentifier(){
        return identifier;
    }

    public void setIdentifier(String id) {
        identifier = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    @Override
    public String toString() {
        return identifier;
    }

    Element toJDOMElement() {
        Element speakerElement = new Element("speaker");
        speakerElement.setAttribute("speaker-id", getIdentifier());
        Element nameElement = new Element("name");
        nameElement.setText(getName());
        speakerElement.addContent(nameElement);
        return speakerElement;
    }
    
}
