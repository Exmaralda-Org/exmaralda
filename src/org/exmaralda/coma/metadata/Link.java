package org.exmaralda.coma.metadata;


public class Link extends Metadata {
    
    public static final int COMMUNICATION_SPEAKER = 0;
    public static final int COMMUNICATION_TRANSCRIPTION = 1;
    public static final int RECORDING_TRANSCRIPTION = 2;

    
    Metadata source;
    Metadata target;
    int type;
    Description description;
    Direction direction;

    public Link(Metadata s,Metadata t,int ty, Direction dir, Description d) {
	type = ty;
	source = s;
	target = t;
	direction = dir;
	description = d;
    }
    
    @Override
    public String getName() {
	return "Link";
    }

}
