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
 * @author Hedeland, master of c/p :)
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
 * It needs an input folder and will by default take all TEI files and merge
 * them with TSV files having the same filename, creating new suffixed files in
 * the same folder.
 *
 * NB: It only cares for the identifier (resource URI) and the value (class) of
 * the Named Entity annotation!
 *
 */
public class TEIWATSVMerger {

    String IN;
    String FORMAT;
    String TSV;
    String OUT;
    Namespace TEI;
    ArrayList<String[]> lines;
    Document teiDocument;

    /**
     * @param args the command line arguments
     *
     * args[1] the input folder for the source TEI or FLN files args[0] the
     * input format: TEI (.tei) or FLN (.fln), defaults to TEI args[1] the input
     * folder for WebAnno TSV files, defaults to args[1] args[2] the output
     * folder for enriched TEI files, defaults to args[1] args[3] the suffix for
     * the target TEI files, defaults to _NE
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

    public TEIWATSVMerger(String inDir) {

        IN = inDir;
        FORMAT = "TEI";
        TSV = inDir;
        OUT = inDir;
        TEI = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");

    }

    public TEIWATSVMerger(String inDir, String inputFormat) {

        IN = inDir;
        FORMAT = inputFormat;
        TSV = inDir;
        OUT = inDir;
        TEI = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");

    }

    public TEIWATSVMerger(String inputDir, String inputFormat, String tsvDir, String outputDir) {

        IN = inputDir;
        FORMAT = inputFormat;
        TSV = tsvDir;
        OUT = outputDir;
        TEI = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");

    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {

        File[] teiFiles = new File(IN).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                //return name.toUpperCase().endsWith(".TEI"); won't work for the same in/out dir...
                return name.toUpperCase().replaceAll("_NE\\.TEI", ".NOTEI").endsWith(".TEI");
            }

        });

        for (File teiFile : teiFiles) {

            SAXBuilder builder = new SAXBuilder();
            teiDocument = (Document) builder.build(teiFile);

            File tsvFile = new File(TSV + teiFile.getName().replaceAll("\\.tei", ".tsv"));

            lines = new ArrayList<>();

            // we read the file and add all relevant lines as String[] to a list 
            // to have the values ready and some context for multi-unit annotations
            try (BufferedReader br = new BufferedReader(new FileReader(tsvFile))) {

                String line;

                while ((line = br.readLine()) != null) {

                    if (line.contains("wikidata")) {

                        String[] items = line.split("\\t");
                        lines.add(items);

                    }
                }
            }

            System.out.println("Done reading " + tsvFile.getAbsolutePath() + ", " + lines.size() + " entries found.");

            addNELayer();

            System.out.println("Done with TEI file: " + OUT + teiFile.getName().replaceAll("\\.tei", "_NE.tei"));

            IOUtilities.writeDocumentToLocalFile(OUT + teiFile.getName().replaceAll("\\.tei", "_NE.tei"), teiDocument);
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
                        Element spanIdentifier = new Element("span", TEI);
                        Element spanValue = new Element("span", TEI);
                        String to = findToId(j, disambiguator);

                        spanIdentifier.setAttribute("from", id);
                        spanIdentifier.setAttribute("to", to);
                        spanIdentifier.setText(identifier);

                        //System.out.println("Created annotation span from " + id + " to " + to);
                        spanValue.setAttribute("from", id);
                        spanValue.setAttribute("to", to);
                        spanValue.setText(value);

                        addSpans(id, spanIdentifier, spanValue);
                    }

                } else {
                    // System.out.println("Now creating single-token annotation");
                    Element spanIdentifier = new Element("span", TEI);
                    Element spanValue = new Element("span", TEI);

                    spanIdentifier.setAttribute("from", id);
                    spanIdentifier.setAttribute("to", id);
                    spanIdentifier.setText(identifier);

                    //  System.out.println("Created annotation span from " + id + " to " + id);
                    spanValue.setAttribute("from", id);
                    spanValue.setAttribute("to", id);
                    spanValue.setText(value);

                    addSpans(id, spanIdentifier, spanValue);
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
     * @param spanIdentifier the span for the identifier sublayer
     * @param spanValue the span for the value sublayer
     *
     * The method adds spans to the identifier and value sublayers of the NE
     * annotation layer
     */
    private void addSpans(String id, Element spanIdentifier, Element spanValue) throws JDOMException {

        //this is where we want to attach the span elements
        Element spanGrpIdentifier;
        Element spanGrpValue;

        // we try to find already inserted <spanGrp> elements to only add a <span>
        String pathToSpanGrpIdentifier = "//tei:annotationBlock[descendant::tei:*[@xml:id='" + id + "']]/tei:spanGrp[@type='NE' and @subtype='identifier']";
        XPath xp2 = XPath.newInstance(pathToSpanGrpIdentifier);
        xp2.addNamespace(TEI);
        spanGrpIdentifier = (Element) xp2.selectSingleNode(teiDocument);

        // if we could retrieve the spanGrp, we have both already and can add to them
        if (spanGrpIdentifier != null) {
            String pathToSpanGrpValue = "//tei:annotationBlock[descendant::tei:*[@xml:id='" + id + "']]/tei:spanGrp[@type='NE' and @subtype='value']";
            XPath xp3 = XPath.newInstance(pathToSpanGrpValue);
            xp3.addNamespace(TEI);
            spanGrpValue = (Element) xp3.selectSingleNode(teiDocument);

        } else {
            // if there were none, we fetch the annotationBlock and add the annotations as <span> elements
            // in the <spanGrp type="NE" suptype="identifier"> child element of the annotationBlock
            String pathToAB = "//tei:annotationBlock[descendant::tei:*[@xml:id='" + id + "']]";
            XPath xp = XPath.newInstance(pathToAB);
            xp.addNamespace(TEI);
            Element annotationBlock = (Element) xp.selectSingleNode(teiDocument);

            spanGrpIdentifier = new Element("spanGrp", TEI);
            spanGrpIdentifier.setAttribute("type", "NE");
            spanGrpIdentifier.setAttribute("subtype", "identifier");
            annotationBlock.addContent(spanGrpIdentifier);
            spanGrpValue = new Element("spanGrp", TEI);
            spanGrpValue.setAttribute("type", "NE");
            spanGrpValue.setAttribute("subtype", "value");
            annotationBlock.addContent(spanGrpValue);

        }

        spanGrpIdentifier.addContent(spanIdentifier);
        spanGrpValue.addContent(spanValue);

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
            File out = new File(new File(IN), file.getName().replaceAll("\\.fln", ".tei"));
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
