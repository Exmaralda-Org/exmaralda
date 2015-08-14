package org.exmaralda.coma.model;

import java.util.HashSet;

public abstract class TypeClass implements TypeInterface {
    String name;
    HashSet <Class> allowedConnections;

    public TypeClass() {
	allowedConnections = new HashSet<Class>();
    }

    public boolean connectTo(TypeClass t, int type) {
	
	return false;
    }
}
