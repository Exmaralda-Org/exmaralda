package org.exmaralda.coma.model;

import org.jdom.Element;

public interface TypeInterface {
    Element toXML();

    String toHTML();

    String toString();

    boolean connectTo(TypeClass t, int type);

}
