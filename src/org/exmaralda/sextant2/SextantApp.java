package org.exmaralda.sextant2;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.helpers.FileSelector;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

public class SextantApp implements ExmaraldaApplication {

    public SextantApp() {
	super();
	annotations = new Vector<File>();
	transcriptions = new Vector<File>();
	concordances = new Vector<File>();
    }

    private SextantUi ui;
    private File exsFile;
    private Document exsDoc;
    private String status;

    private Vector<File> annotations;
    private Vector<File> transcriptions;
    private Vector<File> concordances;
    private String selectedType;
    private File selectedFile;
    private File esaFile;
    private Document esaDoc;

    public void setUi(SextantUi u) {
	ui = u;
    }

    public void open() {
	HashMap<String, String> filetypes = new HashMap();
	filetypes.put("esa", "Standoff-Annotation");
	filetypes.put("exs", "Segmented Transcription");
	filetypes.put("xml", "EXAKT Concordance");
	File file = null;
	// File file = FileSelector.chooseFile(ui, "open annotation", null,
	// null,
	// false, filetypes, getPreferencesNode());
	if (file != null) {
	    Document doc = getDocumentFromXMLFile(file);
	    if (doc != null) {
		Element root = doc.getRootElement();
		if (root.getName().equals("segmented-transcription")) {
		    ui.status("<b>" + file.getName()
			    + "</b> is a transcription.", true);
		    exsFile = file;
		    exsDoc = doc;
		    checkForAnnotations();
		} else if (root.getName().equals("annotation")) {
		    ui.status("<b>" + file.getName()
			    + "</b> is a standoff-annotation file.", true);
		    esaFile = file;
		    esaDoc = doc;
		    checkForTranscriptions();
		} else if (root.getName().equals("search-result-list")) {
		    ui.status(
			    "<b>" + file.getName() + "</b> is a concordance.",
			    true);
		    checkConc();
		}
	    }
	}
    }

    private void checkConc() {
	// TODO Auto-generated method stub

    }

    private void checkForTranscriptions() {
	annotations.add(esaFile);
	ui.updateAnns(annotations);
	File trForAnn = new File(esaFile.getParentFile().getPath()
		+ File.separator
		+ esaDoc.getRootElement().getAttributeValue("target"));
	if (trForAnn.exists()) {
	    ui.status("Annotation <b>" + esaFile.getName()
		    + "</b> annotates transcription <b>" + trForAnn.getName()
		    + "</b> (exists!)", true);
	    transcriptions.add(trForAnn);
	    exsFile = trForAnn;
	    ui.updateTrans(transcriptions);
	} else {
	    ui.status("Annotation <b>" + esaFile.getName()
		    + "</b> annotates transcription <b>" + trForAnn.getPath()
		    + "</b> (doesn't exist!)", false);
	}

    }

    private void checkForAnnotations() {
	transcriptions.add(exsFile);
	ui.updateTrans(transcriptions);

	try {
	    XPath annXPath = XPath
		    .newInstance("//ud-information[@attribute-name='@sextant-annotation']");
	    List anns = annXPath.selectNodes(exsDoc);
	    if (anns.size() > 0) {
		ui.status("transcription has " + anns.size() + " annotation"
			+ ((anns.size() == 0 || anns.size() > 1) ? "s:" : ":"),
			true);
		for (Element e : (List<Element>) anns) {
		    annotations.add(new File(exsFile.getParentFile().getPath()
			    + File.separator + e.getText()));
		    ui.status(e.getText(), true);
		}
		ui.updateAnns(annotations);
	    }
	} catch (JDOMException e) {

	    ui.status("can't determine transcription's annotations.", false);

	}

    }

    public Document getDocumentFromXMLFile(File f) {
	Document doc = null;
	SAXBuilder parser;
	parser = new SAXBuilder();

	try {
	    doc = parser.build(f);
	} catch (JDOMException e) {
	    status = "failed parsing XML";
	    System.err.println("failed building XML from input file");
	    e.printStackTrace();
	} catch (IOException e) {
	    status = "failed opening file";
	    System.err.println("failed opening input file");
	}
	return doc;
    }

    @Override
    public String getApplicationName() {
	return "Sextant";
    }

    @Override
    public String getPreferencesNode() {
	return "org.exmaralda.sextant";
    }

    @Override
    public String getVersion() {
	return "0.2";
    }

    @Override
    public ImageIcon getWelcomeScreen() {
	return null;
    }

    public String getStatus() {
	return status;
    }

    public void setSelectedItem(String type, File file) {
	selectedType = type;
	selectedFile = file;
    }

    public void visualize() {
	if (selectedType.equals("annotation")) {
	    ui
		    .status(
			    "visualizing annotation: " + selectedFile.getName(),
			    true);
	} else if (selectedType.equals("transcription")) {
	    ui.status("visualizing transcription: " + selectedFile.getName(),
		    true);
	} else {
	    ui.status("no visualizing for concordances", true);
	}
    }

    public void convert() {
	if (selectedType.equals("concordance")) {
	    ui
		    .status(
			    "converting concordance: " + selectedFile.getName(),
			    true);
	} else {
	    ui.status("no conversion for annotations or concordances", true);
	}
    }

    public void validate() {
	if (selectedType == null) {
	    ui
		    .status(
			    "please select either a transcription or an annotation for validation",
			    false);

	} else {
	    if (selectedType.equals("annotation")) {
		ui.status("validating annotation: " + selectedFile.getName(),
			true);
		validateAnnotation(selectedFile);
	    } else if (selectedType.equals("transcription")) {
		ui.status(
			"validating transcription: " + selectedFile.getName(),
			true);

	    } else {
		ui.status("no validation for concordances", true);
	    }
	}

    }

    private void validateAnnotation(File f) {
	SchemaFactory factory = SchemaFactory
		.newInstance("http://www.w3.org/2001/XMLSchema");

	URL schemaLocation;
	try {
	    schemaLocation = new URL("http://xml.exmaralda.org/sextant.xsd");
	    Schema schema;
	    schema = factory.newSchema(schemaLocation);
	    Validator validator = schema.newValidator();
	    Source source = new StreamSource(f);
	    validator.validate(source);
	    ui.status("xml is valid!", true);
	    Document anDoc = getDocumentFromXMLFile(f);
	    Document trDoc = getDocumentFromXMLFile(exsFile);
	    Namespace xlink = Namespace.getNamespace("xlink",
		    "http://www.w3.org/1999/xlink");
	    HashSet<String> trSegments = new HashSet<String>();
	    String error = "";
	    try {
		XPath segments = XPath.newInstance("//ts|//nts");
		List<Element> segs = segments.selectNodes(trDoc);
		for (Element e : segs) {
		    trSegments.add(e.getAttributeValue("id"));
		}
		segments = XPath.newInstance("//ann");
		segs = segments.selectNodes(anDoc);
		for (Element e : segs) {
		    if (trSegments.contains(e.getAttributeValue("href", xlink)
			    .substring(1))) {
		    } else {
			error += "annotatated segment "
				+ e.getAttributeValue("href", xlink).substring(
					1) + " does not exist in "
				+ exsFile.getName() + ".<br/>";
		    }
		}
		if (error.length() == 0) {
		    ui.status("All annotated segments accounted for!", true);
		} else {
		    ui.status(error, false);
		}

	    } catch (JDOMException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} catch (MalformedURLException e) {
	    ui
		    .status(
			    "cannot verify against schema. maybe you are not connected to the internet.",
			    false);
	} catch (SAXException e) {
	    ui.status("is not valid: " + e.getMessage(), false);
	} catch (IOException e) {
	    ui.status("cannot open annotation. maybe the file does not exist.",
		    false);
	}
    }
}
