package org.exmaralda.coma.metadata;

public class Communication extends Metadata {

    Description description;
    Id id;
    
    @Override
    public String getName() {
	return "Communication";
    }

}
