package org.exmaralda.aggronym.resources;

import java.net.URL;

public class ResourceHandler {

	public URL getResource(String pathToResource) {
		return (getClass().getResource(pathToResource));
	}

	public URL dict() {
		return getResource("de-en.txt");
	}
}
