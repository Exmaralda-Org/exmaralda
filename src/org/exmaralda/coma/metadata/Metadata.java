package org.exmaralda.coma.metadata;


public abstract class Metadata {

    Id id;

    public Metadata() {

    }

    /*
     * please override!
     */
    public String getName() {
	return "Metadata (should not appear!)";
    }

    public String getId() {
	return id.getIdString();
    }
}
