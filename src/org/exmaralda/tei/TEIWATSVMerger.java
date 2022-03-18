/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tei;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.*;
import org.xml.sax.SAXException;

/**
 *
 * This class merges an ISO/TEI file, optionally converted from a FLN file, with
 * a WebAnno TSV 3.3 file
 * (https://inception-project.github.io/releases/22.5/docs/user-guide.html#sect_webannotsv)
 * that has the layer
 * #T_SP=de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity|identifier|value,
 * in this case as the second layer after
 * #T_SP=de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS|PosValue|coarseValue,
 * i.e. identifier is at position 6 and value at position 7, and token IDs (for
 * w and p elements in the ISO/TEI) at position 9.
 *
 * It needs an input folder and will by default take all TEI files (.xml) and
 * merge them with TSV files having the same filename, creating new suffixed
 * files in the same folder. The suffix can be set individually to replace the
 * last four characters, i.e. the dot and the extension (but beware of the loop
 * if IN=OUT).
 *
 * NB: This class only cares for the identifier (resource URI) and the value
 * (class) of the Named Entity annotation!
 *
 * It puts both features in one spanGrp with the value as the text of the span
 * element and the identifier as a target attrbute (cf. corpus MEND).
 *
 */
public class TEIWATSVMerger {

    String IN;
    String FORMAT;
    String TSV;
    String OUT;
    String SUFFIX;
    Namespace TEI;
    Namespace TEI4XPATH = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");
    ArrayList<String[]> lines;
    Document teiDocument;

    /**
     * @param args the command line arguments
     *
     */
    public static void main(String[] args) {
        try {
            TEIWATSVMerger t = new TEIWATSVMerger("C:\\Users\\Hedeland\\Nextcloud\\EDMTestCase\\NER_transcripts\\", "FLN");
            if (t.FORMAT.equalsIgnoreCase("FLN")) {
                t.convert();
            }
            t.doit();

        } catch (JDOMException | IOException | SAXException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(FLN_TEIConversion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TEIWATSVMerger(String inputDir) {

        this(inputDir, "TEI", inputDir, inputDir, "_NE.xml");

    }

    public TEIWATSVMerger(String inputDir, String inputFormat) {

        this(inputDir, inputFormat, inputDir, inputDir, "_NE.xml");

    }

    public TEIWATSVMerger(String inputDir, String inputFormat, String tsvDir) {

        this(inputDir, inputFormat, tsvDir, inputDir, "_NE.xml");

    }

    public TEIWATSVMerger(String inputDir, String inputFormat, String tsvDir, String outputDir) {

        this(inputDir, inputFormat, tsvDir, outputDir, "_NE.xml");

    }

    /**
     * @param inputDir the input folder for the source TEI or FLN files
     * @param inputFormat the input format: TEI (.xml) or FLN (.fln), defaults
     * to TEI
     * @param tsvDir the input folder for WebAnno TSV files, defaults to
     * inputDir
     * @param outputDir the output folder for enriched TEI files, defaults to
     * inputDir
     * @param suffix the suffix for the target files, replaces last for chars,
     * defaults to _NE.xml
     */
    public TEIWATSVMerger(String inputDir, String inputFormat, String tsvDir, String outputDir, String suffix) {

        IN = inputDir;
        FORMAT = inputFormat;
        TSV = tsvDir;
        OUT = outputDir;
        SUFFIX = suffix;

    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {

        File[] teiFiles = new File(IN).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                //some extra stuff might help avoiding silly looping when IN=OUT
                return (name.toUpperCase().replaceAll("_NE\\.XML", "").endsWith(".XML"));
            }

        });

        for (File teiFile : teiFiles) {

            SAXBuilder builder = new SAXBuilder();
            teiDocument = (Document) builder.build(teiFile);
            TEI = teiDocument.getRootElement().getNamespace();

            File tsvFile = new File(TSV + teiFile.getName().replaceAll("\\.xml", ".tsv"));

            lines = new ArrayList<>();

            // we read the file and add all lines with annotations as String[] to a list 
            // to have the values ready and some context for multi-unit annotations
            try (BufferedReader br = new BufferedReader(new FileReader(tsvFile))) {

                String line;

                while ((line = br.readLine()) != null) {

                    // we only want to use lines with annotations
                    if (Pattern.compile("^[0-9]").matcher(line).find()) {

                        String[] items = line.split("\\t");

                        // items[6] holds the value, like ORG, PLACE etc.
                        // if there is no annotation, there is an underscore
                        if (items[6].length() > 1) {
                            lines.add(items);
                        }
                    }
                }
            }

            System.out.println(tsvFile.getAbsolutePath() + " read.");

            addNELayer();

            System.out.println(OUT + teiFile.getName().replaceAll("\\.xml", SUFFIX) + " written.");

            IOUtilities.writeDocumentToLocalFile(OUT + teiFile.getName().replaceAll("\\.xml", SUFFIX), teiDocument);
        }
    }

    private void addNELayer() throws JDOMException {

        for (int j = 0; j < lines.size(); j++) {

            String[] items = lines.get(j);

            // We have Multi-token span annotations and stacked span annotations for Named Entity annotation, format spec says:
            // The disambiguation ID is attached as a suffix [N] to the annotation value. Stacked annotations are separated by | character.
            // 
            // Example: Multi-token span annotations and stacked span annotations
            // 1-1	0-3	Ms.	NNP	PER[1]|PERpart[2]
            // 1-2	4-8	Haag	NNP	PER[1]
            // 
            // Here it can look like this:
            // 313-31	41969-41980	Universität	NN	NOUN	*[93]|http://www.wikidata.org/entity/Q319239[94]	ORG[93]|ORG[94]	Universität	w6569	
            // 313-32	41981-41984	Tel	NE	PROPN	*[93]|http://www.wikidata.org/entity/Q319239[94]|http://www.wikidata.org/entity/Q33935[95]	ORG[93]|ORG[94]|GPE[95]	Tel	w6570
            // 313-33	41985-41989	Aviv	NE	PROPN	*[93]|http://www.wikidata.org/entity/Q319239[94]|http://www.wikidata.org/entity/Q33935[95]	ORG[93]|ORG[94]|GPE[95]	Aviv	w6571
            //
            // items[5] holds the identifier, the wikidata ref, 
            // sometimes there are stacked annotations
            // items[6] holds the value, like ORG, PLACE etc.,
            // these are also stacked aligned with the identifiers
            // items[8] holds the token id
            String[] identifiers = items[5].split("\\|");
            String[] values = items[6].split("\\|");
            String id = items[8];

            for (int i = 0; i < identifiers.length; i++) {

                String identifier = identifiers[i];
                String value = values[i];

                // handle multi-unit annotations
                if (identifier.contains("[")) {

                    // disambiguator IDs are used to chain together multi-unit annotations
                    String disambiguator = identifier.split("[\\[\\]]")[1];
                    identifier = identifier.split("[\\[\\]]")[0];
                    //we also have to strip this from the value
                    value = value.split("[\\[\\]]")[0];

                    // we check if there is a preceding part-annotation, then we already handled that one
                    // if it's the first identifier with a disambiguation id, we create the correct span now
                    if (lines.get(j - 1)[5].contains("[" + disambiguator + "]")) {
                        //System.out.println("Found another one of " + disambiguator + ", not doing anything.");
                    } else {
                        //System.out.println("Now creating mutliple-token annotation");
                        Element span = new Element("span", TEI);
                        String to = findToId(j, disambiguator);

                        span.setAttribute("from", id);
                        span.setAttribute("to", to);
                        // if there is no WD link, we don't want the attribute?
                        if (!identifier.contains("*")) {
                            span.setAttribute("target", identifier);
                        }
                        span.setText(value);
                        //System.out.println("Created annotation span from " + id + " to " + to);

                        addSpan(id, span, "NE");
                    }

                } else {
                    // System.out.println("Now creating single-token annotation");
                    Element span = new Element("span", TEI);

                    span.setAttribute("from", id);
                    span.setAttribute("to", id);
                    // if there is no WD link, we don't want the attribute?
                    if (!identifier.contains("*")) {
                        span.setAttribute("target", identifier);
                    }
                    span.setText(value);
                    //  System.out.println("Created annotation span from " + id + " to " + id);
                    addSpan(id, span, "NE");
                }
            }
        }
    }

    private String findToId(int position, String disambiguator) {

        //System.out.println("Looking for Id in " + lines.get(position + 1)[5]);
        if (!lines.get(position + 1)[5].contains("[" + disambiguator + "]")) {

            return lines.get(position)[8];
        }

        //   System.out.println("Disambiguator found: " + disambiguator + ", position: " + position);
        return findToId(position + 1, disambiguator);

    }

    /**
     * @param id the id of the start/from token element
     * @param span the span element for the anntotation
     * @param type the annotation layer (attribute type)
     *
     * The method adds a span to the identifier and value sublayers of the NE
     * annotation layer
     */
    private void addSpan(String id, Element span, String type) throws JDOMException {

        //this is where we want to attach the span element
        Element spanGrp;

        // we try to find already inserted <spanGrp> element to only add a <span>
        String pathToSpanGrp = "//tei:annotationBlock[descendant::tei:*[@xml:id='" + id + "']]/tei:spanGrp[@type='" + type + "']";
        XPath xp2 = XPath.newInstance(pathToSpanGrp);
        xp2.addNamespace(TEI4XPATH);
        spanGrp = (Element) xp2.selectSingleNode(teiDocument);

        // if we could retrieve the spanGrp elemnt, we can add to it
        if (spanGrp == null) {

            // if there was none, we fetch the annotationBlock and add the annotations
            // to a new spanGrp child element of the annotationBlock
            String pathToAB = "//tei:annotationBlock[descendant::tei:*[@xml:id='" + id + "']]";
            XPath xp = XPath.newInstance(pathToAB);
            xp.addNamespace(TEI4XPATH);
            Element annotationBlock = (Element) xp.selectSingleNode(teiDocument);

            spanGrp = new Element("spanGrp", TEI);
            spanGrp.setAttribute("type", "NE");
            annotationBlock.addContent(spanGrp);
        }

        spanGrp.addContent(span);
    }

    private void convert() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {

        File[] FLNfiles = new File(IN).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".FLN");
            }

        });
        StylesheetFactory sf = new StylesheetFactory(true);

        for (File file : FLNfiles) {
            File out = new File(new File(IN), file.getName().replaceAll("\\.fln", ".xml"));
            String[][] parameters = {
                {"LANGUAGE", "de"},
                {"MAKE_INLINE_ATTRIBUTES", "TRUE"}
            };
            String result = sf.applyInternalStylesheetToExternalXMLFile("/org/exmaralda/tei/xml/folker2isotei.xsl", file.getAbsolutePath(), parameters);
            Document d = IOUtilities.readDocumentFromString(result);
            IOUtilities.writeDocumentToLocalFile(out.getAbsolutePath(), d);
            System.out.println(out.getAbsolutePath() + " written.");
        }
    }
}
