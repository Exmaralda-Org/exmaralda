package org.exmaralda.coma.metadata;

public class Speaker extends Metadata {

    Description description;
    Id id;
    
    @Override
    public String getName() {
	return "Speaker";
    }

}
