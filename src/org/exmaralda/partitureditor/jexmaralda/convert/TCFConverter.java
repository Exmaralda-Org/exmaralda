/**
 * @file TCFConverter.java
 *
 * Convert to and from TCF format. This version is based on weblicht wlfxb
 * libraries, you need to have wlfxb.jar and oaipmh-cmdi-bindings.jar on your
 * classpath. I (tommi) used wlfxb-1.3.4-SNAPSHOT.jar and
 * oaipmh-cmdi-bindings-1.0.6-SNAPSHOT.jar from March 2016 github master.
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.jexmaralda.*;

import java.io.*;
import java.util.*;

// using wlfxb for conversions
import eu.clarin.weblicht.wlfxb.io.WLDObjector;
import eu.clarin.weblicht.wlfxb.io.WLFormatException;
import eu.clarin.weblicht.wlfxb.tc.api.Lemma;
import eu.clarin.weblicht.wlfxb.tc.api.LemmasLayer;
import eu.clarin.weblicht.wlfxb.tc.api.PosTagsLayer;
import eu.clarin.weblicht.wlfxb.tc.api.SentencesLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Sentence;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.tc.api.PosTag;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import eu.clarin.weblicht.wlfxb.tc.xb.TextSourceLayerStored;
import eu.clarin.weblicht.wlfxb.xb.WLData;
import eu.clarin.weblicht.wlfxb.tc.api.TokensLayer;

// from old version?
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.common.corpusbuild.TEIMerger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 * @author thomas
 * @author tpirinen
 *
 * Converter for TCF files.
 */
public class TCFConverter {

    TextCorpusStored textCorpus;
    TextSourceLayerStored textSource;
    static String TCF_STYLESHEET_PATH = 
        "/org/exmaralda/partitureditor/jexmaralda/xsl/ISOTEI2TCF.xsl";

    /** Creates a new instance of TCFConverter */
    public TCFConverter() {}

    /** Read by filename
     * @param path
     * @return  */
    public BasicTranscription readTCFFromFile(String path) {
        try {
            readText(new FileInputStream(path));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        return importText();
    }


    /** Read TCF from fis using WLData.
     * @param fis */
    public void readText(FileInputStream fis)
    {
        try {
            WLDObjector wldo = new WLDObjector();
            WLData data = wldo.read(fis);
            textCorpus = data.getTextCorpus();
            // FIXME: if there's hidden exmeralda tei merger data in
            // text-source, just slurp that in
            textSource = textCorpus.getTextSourceLayer();
        } catch (WLFormatException wlfe) {
            wlfe.printStackTrace();
        }
    }

    /** Read TCF from a file.
     * @param file
     *  @sa readText(FileInputStream)
     */
    public void readText(File file) throws IOException {
        readText(new FileInputStream(file));
    }

    /** Read encoded TCF from a file.
     * @param file
     *  @fixme doesn't actually decode.
     */
    public void readText(File file, String encoding) throws 
        FileNotFoundException, IOException, UnsupportedEncodingException
    {
        // XXX: encoding
        readText(new FileInputStream(file));
    }

    /** 
     * Imports some parts of TCF file into tiers.
     * The timeline is based on the tokens and rests are aligned on their
     * bounding points. Text is assumed to stretch over all tokens.
     * @return 
     */
    public BasicTranscription importText(){
        BasicTranscription bt;
        if (textSource != null) {
            try {
                File tempfile = File.createTempFile("exmaralda-tei", ".xml");
                tempfile.deleteOnExit();
                Writer tempwriter = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(tempfile), "utf-8"));
                tempwriter.write(textSource.getText());
                TEIConverter teireader = new TEIConverter();
                System.err.println("Wrote temp TEI to " + 
                        tempfile.getAbsolutePath());
                bt = teireader.readTEIFromFile(tempfile.getAbsolutePath());
                if (bt != null) {
                    return bt;
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            System.err.println("Could not use TEI in textsource for importing, "
                    + "falling back to TCF data");
        }
        bt = new BasicTranscription();
        Speakertable st = bt.getHead().getSpeakertable();
        Speaker speaker = new Speaker();
        speaker.setID("SPK0");
        speaker.setAbbreviation("X");
        try {
            st.addSpeaker(speaker);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        Timeline timeline = bt.getBody().getCommonTimeline();
        String tliStart = timeline.addTimelineItem();
        String tliEnd = tliStart;

        // do we always have tokens, right?
        TokensLayer tokensLayer = textCorpus.getTokensLayer();
        Tier tokenTier = null;
        Map<String, String> tokenStartTimelineItems =
                new HashMap<String, String>();
        Map<String, String> tokenEndTimelineItems =
                new HashMap<String, String>();
        if (tokensLayer != null) {
            tokenTier = new Tier();
            tokenTier.setID("TIE0");
            tokenTier.setCategory("txt");
            tokenTier.setType("t");
            tokenTier.setDisplayName("X [token]");
            tokenTier.setSpeaker("SPK0");


            // collect tokens for later use, as main id points
            String tli = tliStart;
            for (int i = 0; i < textCorpus.getTokensLayer().size(); i++) {
                Token token = textCorpus.getTokensLayer().getToken(i);
                Event e = new Event();
                e.setDescription(token.getString());
                tokenStartTimelineItems.put(token.getID(), tli);
                e.setStart(tli);
                tli = timeline.addTimelineItem();
                e.setEnd(tli);
                tokenEndTimelineItems.put(token.getID(), tli);
                tokenTier.addEvent(e);
            }
            tliEnd = tli;
        }

        // text layer covers whole timeline?
        /*TextLayer text = textCorpus.getTextLayer();
        Tier textTier = null;
        if (text != null) {
            textTier = new Tier();
            textTier.setID("TIE1");
            textTier.setCategory("txt");
            textTier.setType("t");
            textTier.setDisplayName("X [txt]");
            textTier.setSpeaker("SPK0");
            Event e = new Event();
            e.setStart(tliStart);
            e.setDescription(text.getText());
            e.setEnd(tliEnd);
            textTier.addEvent(e);
        }*/

        // sentences cover some subsets
        SentencesLayer sents = textCorpus.getSentencesLayer();
        Tier sentenceTier = new Tier();
        if (sents != null) {
            sentenceTier = new Tier();
            sentenceTier.setID("TIE4");
            sentenceTier.setCategory("txt");
            sentenceTier.setType("t");
            sentenceTier.setDisplayName("X [sent]");
            sentenceTier.setSpeaker("SPK0");
            for (int i = 0; i < sents.size(); i++) {
                Sentence sentence = sents.getSentence(i);
                Token[] tokenses = sents.getTokens(sentence);
                String descr = "";
                for (int j = 0; j < tokenses.length; j++) {
                    descr = descr + " " + tokenses[j].getString();
                }
                Event e = new Event();
                String tliStartNow = 
                    tokenStartTimelineItems.get(tokenses[0].getID());
                e.setStart(tliStartNow);
                e.setDescription(descr);
                String tliEndNow = tokenEndTimelineItems.get(
                    tokenses[tokenses.length - 1].getID());
                e.setEnd(tliEndNow);
                sentenceTier.addEvent(e);
            }
        }

        // other layers?  (this starts to be a bit of copy-pasta)
        // please to be refactoring next time you see this...
        Tier posTier = null;
        PosTagsLayer poses = textCorpus.getPosTagsLayer();
        if (poses != null) {
            posTier = new Tier();
            posTier.setID("TIE2");
            posTier.setCategory("pos");
            posTier.setType("a");
            posTier.setDisplayName("X [pos]");
            posTier.setSpeaker("SPK0");
            for (int i = 0; i < poses.size(); i++) {
                PosTag pos = poses.getTag(i);
                Event e = new Event();
                e.setDescription(pos.getString());
                Token[] tokenses = poses.getTokens(pos);
                String tliStartNow = 
                    tokenStartTimelineItems.get(tokenses[0].getID());
                e.setStart(tliStartNow);
                String tliEndNow = tokenEndTimelineItems.get(
                    tokenses[tokenses.length - 1].getID());
                e.setEnd(tliEndNow);
                posTier.addEvent(e);
            }
        }
        // Lemmas
        LemmasLayer lemmas = textCorpus.getLemmasLayer();
        Tier lemmaTier = null;
        if (lemmas != null) {
            lemmaTier = new Tier();
            lemmaTier.setID("TIE3");
            lemmaTier.setCategory("lemma");
            lemmaTier.setType("a");
            lemmaTier.setDisplayName("X [lemma]");
            lemmaTier.setSpeaker("SPK0");
            for (int i = 0; i < lemmas.size(); i++) {
                Lemma lemma = lemmas.getLemma(i);
                Event e = new Event();
                e.setDescription(lemma.getString());
                Token[] tokenses = lemmas.getTokens(lemma);
                String tliStartNow = 
                    tokenStartTimelineItems.get(tokenses[0].getID());
                e.setStart(tliStartNow);
                String tliEndNow = tokenEndTimelineItems.get(
                        tokenses[tokenses.length - 1].getID());
                e.setEnd(tliEndNow);
                lemmaTier.addEvent(e);
            }
        }

        try {
            if (tokenTier != null) {
                bt.getBody().addTier(tokenTier);
            }
            /*if (textTier != null) {
                bt.getBody().addTier(textTier);
            }*/
            if (sentenceTier != null) {
                bt.getBody().addTier(sentenceTier);
            }
            if (posTier != null) {
                bt.getBody().addTier(posTier);
            }
            if (lemmaTier != null) {
                bt.getBody().addTier(lemmaTier);
            }
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        return bt;
    }
    public void writeHIATTCFToFile(BasicTranscription bt, String filename)
        throws SAXException, FSMException, XSLTransformException, 
                          JDOMException, IOException, Exception {
        writeHIATTCFToFile(bt, filename, "de");
    }

    public void writeHIATTCFToFile(BasicTranscription bt, String filename,
            String language) throws SAXException, FSMException, 
           XSLTransformException, JDOMException, IOException, Exception {
        writeTCFToFile(bt, filename, language, "HIAT");
    }

    public void writeTCFToFile(BasicTranscription bt, String filename,
            String language, String segmentationName) throws SAXException,
            FSMException, XSLTransformException, JDOMException, IOException,
            Exception {
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();
        System.out.println("started writing document...");
        AbstractSegmentation segmentation = null;
        if ("HIAT".equals(segmentationName)){
            segmentation = new HIATSegmentation();
        } else if ("cGAT Minimal".equals(segmentationName)){
            segmentation = new cGATMinimalSegmentation();
        } else {
            segmentation = new GenericSegmentation();
        }
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        System.out.println("Segmented transcription created");
        String nameOfDeepSegmentation = "SpeakerContribution_Word";
        if ("HIAT".equals(segmentationName)){
            nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        }
        TEIMerger teiMerger = new TEIMerger(true);
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc =
            teiMerger.SegmentedTranscriptionToTEITranscription(stdoc,
                    nameOfDeepSegmentation, "SpeakerContribution_Event", true);
        System.out.println("Merged");
        generateWordIDs(teiDoc);
        
        StylesheetFactory ssf = new StylesheetFactory(true);
        String tcf = ssf.applyInternalStylesheetToString(TCF_STYLESHEET_PATH,
                IOUtilities.documentToString(teiDoc));
        Document tcfDoc = FileIO.readDocumentFromString(tcf);

        // set the language (not too elegant...)
        XPath xp = XPath.newInstance("//tcf:TextCorpus");
        xp.addNamespace("tcf", "http://www.dspin.de/data/textcorpus");        
        Element textCorpusElement = (Element) xp.selectSingleNode(tcfDoc);
        textCorpusElement.setAttribute("lang", language);
        FileIO.writeDocumentToLocalFile(filename, tcfDoc);
        System.out.println("document written.");
    }

    public void writeFOLKERTCFToFile(Document flnDoc, String absolutePath) 
        throws SAXException, ParserConfigurationException, IOException,
                          TransformerConfigurationException,
                          TransformerException, JDOMException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = 
            sf.applyInternalStylesheetToString(
                    "/org/exmaralda/tei/xml/folker2isotei.xsl",
                    IOUtilities.documentToString(flnDoc));
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        generateWordIDs(teiDoc);
        String tcf = sf.applyInternalStylesheetToString(TCF_STYLESHEET_PATH,
                IOUtilities.documentToString(teiDoc));
        Document tcfDoc = FileIO.readDocumentFromString(tcf);
        IOUtilities.writeDocumentToLocalFile(absolutePath, tcfDoc);
    }

    private void generateWordIDs(Document document) throws JDOMException{
        XPath wordXPath = XPath.newInstance("//tei:w"); 
        wordXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        List words = wordXPath.selectNodes(document);
        int count=0;
        for (Object o : words){
            count++;
            Element word = (Element)o;
            String wordID = "w" + Integer.toString(count);
            //System.out.println("*** " + wordID);
            word.setAttribute("id", wordID, Namespace.XML_NAMESPACE);
        }
        // new 02-12-2014
        XPath pcXPath = XPath.newInstance("//tei:pc"); 
        pcXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        List pcs = pcXPath.selectNodes(document);
        count=0;
        for (Object o : pcs){
            count++;
            Element pc = (Element)o;
            String pcID = "pc" + Integer.toString(count);
            //System.out.println("*** " + wordID);
            pc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }
    }

    public static void main(String[] args) {
        try {
            File f = new File("F:\\KORPORA\\EXMARaLDA-Demokorpus\\AnneWill\\AnneWill.exb");
            BasicTranscription bt = new BasicTranscription(f.getAbsolutePath());
            TCFConverter tc = new TCFConverter();
            tc.writeTCFToFile(bt, "F:\\KORPORA\\EXMARaLDA-Demokorpus\\AnneWill\\AnneWill_TCF.xml", "de", "Generic");
            /*tc.readText(f);
            bt.writeXMLToFile("tcf04-karin-wl.exb", "none");
            System.out.println(tc.importText().toXML());*/
        } catch (SAXException ex) {
            Logger.getLogger(TCFConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(TCFConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TCFConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TCFConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TCFConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String exportBasicTranscription(BasicTranscription bt) 
            throws IOException {
        Timeline commonTimeline = bt.getBody().getCommonTimeline();
        // XXX: can I get metadatas
        TextCorpusStored textCorpus = new TextCorpusStored("unk");
        // find token tier first, to bind all others to those boundaries
        Tier tokenTier = null;
        for (int pos = 0; pos < bt.getBody().getNumberOfTiers(); pos++) {
            Tier thisTier = bt.getBody().getTierAt(pos);
            if ((thisTier.getSpeaker() != null) && 
                    (thisTier.getType().equals("t")) &&
                    (thisTier.getDisplayName().contains("token"))) {
                tokenTier = thisTier;
                break;
            }
        }
        if (tokenTier == null) {
            throw new
                IOException("No token tier found for this transcription?");
        }
        TokensLayer tokensLayer = textCorpus.createTokensLayer();
        Map <String, Token> tokenStarts = new HashMap<String, Token>();
        Map <String, Token> tokenEnds = new HashMap<String, Token>();
        for (int pos = 0; pos < tokenTier.getNumberOfEvents(); pos++) {
            Event e = tokenTier.getEventAt(pos);
            String token_id = "t_" + Integer.toString(pos + 1);
            long tokenstart = 0;
            long tokenend = 0;
            try {
                TimelineItem tli = 
                    commonTimeline.getTimelineItemWithID(e.getStart());
                tokenstart = Math.round(tli.getTime());
                tli = commonTimeline.getTimelineItemWithID(e.getEnd());
                tokenend = Math.round(tli.getTime());
            }
            catch (JexmaraldaException je) {
                je.printStackTrace();
            }
            tokensLayer.addToken(e.getDescription(),
                   tokenstart, tokenend, token_id);
            Token token = tokensLayer.getToken(token_id);
            tokenStarts.put(e.getStart(), token);
            tokenEnds.put(e.getEnd(), token);
        }
        // add other tiers back
        for (int i = 0; i < bt.getBody().getNumberOfTiers(); i++) {
            Tier thisTier = bt.getBody().getTierAt(i);
            if (thisTier.getSpeaker() == null) {
                // XXX: skip no speaker tiers?
                continue;
            } else if ((thisTier.getType().equals("t")) &&
                    (thisTier.getDisplayName().contains("text"))) {
                // handle text layer
                Event e = thisTier.getEventAt(0);
                textCorpus.createTextLayer().addText(e.getDescription());
            } else if ((thisTier.getType().equals("t")) &&
                    (thisTier.getDisplayName().contains("sentence"))) {
                SentencesLayer sentencesLayer = 
                    textCorpus.createSentencesLayer();
                for (int j = 0; j < thisTier.getNumberOfEvents(); j++) {
                    Event e = thisTier.getEventAt(j);
                    // TODO: calculate span and reconstruct tokens :-///
                    int sentStart = 
                        tokenStarts.get(e.getStart()).getOrder();
                    int sentEnd =
                        tokenEnds.get(e.getEnd()).getOrder();
//                    sentencesLayer.addSentence(words, sentStart, sentEnd);
                }
            // XXX: Lemmas and poses are 1:1 to tokens for now½!
            } else if ((thisTier.getType().equals("a")) &&
                    (thisTier.getDisplayName().contains("lemma"))) {
                LemmasLayer lemmasLayer = textCorpus.createLemmasLayer();
                for (int j = 0; j < thisTier.getNumberOfEvents(); j++) {
                    Event e = thisTier.getEventAt(j);
                    Token t = tokenStarts.get(e.getStart());
                    lemmasLayer.addLemma(e.getDescription(), t);
                }
            } else if ((thisTier.getType().equals("a")) &&
                    (thisTier.getDisplayName().contains("POS"))) {
                // XXX: restore somewhereabouts
                PosTagsLayer posTagsLayer = 
                    textCorpus.createPosTagsLayer("UNKNOWN");
                for (int j = 0; j < thisTier.getNumberOfEvents(); j++) {
                    Event e = thisTier.getEventAt(j);
                    Token t = tokenStarts.get(e.getStart());
                    posTagsLayer.addTag(e.getDescription(), t);
                }
            } else {
                // handle unk tier
                System.out.println("XXX: missing unk layer handler");
            }
        }
        OutputStream bytes = new ByteArrayOutputStream();
        WLData wlData = new WLData(textCorpus);
        WLDObjector.write(wlData, bytes);
        return bytes.toString();
    }

    public void writeText(BasicTranscription bt, File file) throws IOException {
        String text = exportBasicTranscription(bt);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes("UTF-8"));
        fos.close();
    }
}
