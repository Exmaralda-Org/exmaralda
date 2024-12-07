package org.exmaralda.sextanttagger.resources;

import java.io.InputStream;
import java.net.URL;

public class ResourceHandler {

	public URL getResource(String pathToResource) {
		return (getClass().getResource(pathToResource));
	}


	public URL image(String imageFileName) {
		return getResource("images/" + imageFileName);
	}
}
