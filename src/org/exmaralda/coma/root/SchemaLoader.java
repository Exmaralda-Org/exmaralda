package org.exmaralda.coma.root;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SchemaLoader implements EntityResolver {
	public static final String FILE_SCHEME = "file://";

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		if (systemId.startsWith(FILE_SCHEME)) {
			String filename = systemId.substring(FILE_SCHEME.length());
			InputStream stream = SchemaLoader.class.getClassLoader()
					.getResourceAsStream(filename);
			System.out.println("I was invoked!"+filename);
			return new InputSource(stream);
		} else {
			System.out.println("null!");
			return null;
		}
	}
}