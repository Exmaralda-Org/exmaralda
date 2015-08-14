package org.exmaralda.coma.metadata;

import java.util.HashMap;
import java.util.Map.Entry;

public class Corpus {
    HashMap<String, Metadata> data;

    public HashMap<String, Metadata> getMetadata(String type) {
	HashMap<String, Metadata> metadata = new HashMap<String, Metadata>();
	for (Entry<String, Metadata> e : data.entrySet()) {
	    if (e.getValue().getName().equals(type)) {
		metadata.put(e.getKey(), e.getValue());
	    }
	}
	return metadata;
    }

    public Communication addCommunication() {
	Communication c = new Communication();
	data.put(c.getId(), c);
	return c;
    }

    public Speaker addSpeaker() {
	Speaker s = new Speaker();
	data.put(s.getId(), s);
	return s;
    }

    public Link link(Metadata source, Metadata target, int type,
	    Direction direction) {
	Link l = new Link(source, target, type, direction, null); // change
								  // 'dat!
	return l;
    }

    public void addMetadata(Metadata m) {
	data.put(m.getId(), m);
    }
}
