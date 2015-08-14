package org.exmaralda.coma.resources;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import org.xml.sax.InputSource;

public class ResourceHandler {

	public URL getResource(String pathToResource) {
		return (getClass().getResource(pathToResource));
	}

	public URL schemaURL() {
		System.out.println(getResource("comacorpus.xsd"));
		return getResource("comacorpus.xsd");
	}

	public URL languageFileURL() {
		return getResource("languages.xml");
	}

	public URL image(String imageFileName) {
		return getResource("images/" + imageFileName);
	}

	public URL propertiesURL() {
		return getResource("version.properties");
	}

	public InputStream fedoraCredentialsStream() {
		return this.getClass().getResourceAsStream(
				"fedoraCredentials.properties");
	}

	public InputStream printStylesheetStream() {
		return this.getClass().getResourceAsStream("print.css");
	}

	public InputStream OutputXslStream(String whichStylesheet) {
		return this.getClass().getResourceAsStream(
				"outputxsl/" + whichStylesheet);
	}

	public Vector<String> outputStylesheets() {
		Vector<String> v = new Vector<String>();
		File xslDir = new File(this.getClass().getResource("outputxsl")
				.getFile());
		for (File f : xslDir.listFiles()) {
			v.add(f.getName());
		}
		return v;
	}

	public InputStream screenStylesheetStream(String whichOne) {
		return this.getClass().getResourceAsStream(whichOne + ".css");
	}

	public URL helpFileURL(String helpFileName) {
		System.out.println(getResource("documentation/" + helpFileName));
		return getResource("documentation/" + helpFileName);
	}

	public InputStream languageCodes() {
		return this.getClass().getResourceAsStream("iso-639-3.tab");
	}

	public URL defaultHokusCorpusFileURL() {
		return getResource("hokuscorpus_default.xml");
	}

	public URL stylesheetsList() {
		return getResource("outputxsl/stylesheets.xml");
	}
}
