/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.folker.data.PatternReader;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.exmaralda.partitureditor.jexmaralda.TimedSegmentSaxReader;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class GATMinimalSegmentation extends AbstractSegmentation {

    String checkRegex = ".*";

    public GATMinimalSegmentation() {
        super();
        setupPattern();
    }

    public GATMinimalSegmentation(String ptef) {
        super(ptef);
        setupPattern();
    }

    private void setupPattern() {
        try {
            PatternReader pr = new PatternReader("/org/exmaralda/folker/data/Patterns.xml");
            checkRegex = pr.getPattern(2, "GAT_CONTRIBUTION");
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public Vector getSegmentationErrors(BasicTranscription bt) throws SAXException {
         Vector<FSMException> result = new Vector<FSMException>();
         SegmentedTranscription st = bt.toSegmentedTranscription();
         for (String tierID : st.getBody().getAllTierIDs()){
             SegmentedTier tier = st.getBody().getSegmentedTierWithID(tierID);
             if (!tier.getType().equals("t")) continue;
             if (tier.hasSegmentationWithName("SpeakerContribution_Event")){
                Segmentation segmentation = tier.getSegmentationWithName("SpeakerContribution_Event");
                for (int pos=0; pos<segmentation.size(); pos++){
                    Object s = segmentation.elementAt(pos);
                    if (s instanceof TimedSegment){
                        TimedSegment ts = ((TimedSegment)s);
                        String description = ts.getDescription();
                        if (!description.matches(checkRegex)){
                            FSMException fsme = new FSMException("GAT Error", description);
                            fsme.setTierID(tierID);
                            fsme.setTLI(ts.getStart());
                            result.add(fsme);
                        }
                    }
                }
             }
         }
         return result;
    }


    @Override
    public SegmentedTranscription BasicToSegmented(BasicTranscription bt) throws SAXException, FSMException {
        Vector segerr = getSegmentationErrors(bt);
        if (segerr.size()>0){
            FSMException fsme = (FSMException)(segerr.elementAt(0));
            throw fsme;
        }
        
        StylesheetFactory sf = new StylesheetFactory(true);
        TimedSegmentSaxReader tsr = new TimedSegmentSaxReader();


        SegmentedTranscription st = bt.toSegmentedTranscription();
        EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
        elt.setMediaPath("");
        elt.updateContributions();
        Document folkerDocument = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt, new File(System.getProperty("user.dir")));
        GATParser parser = new GATParser();
        parser.parseDocument(folkerDocument, 1);
        parser.parseDocument(folkerDocument, 2);
         for (String tierID : st.getBody().getAllTierIDs()){
             SegmentedTier tier = st.getBody().getSegmentedTierWithID(tierID);
             if (!tier.getType().equals("t")) continue;
             if (tier.hasSegmentationWithName("SpeakerContribution_Event")){
                Segmentation segmentation = tier.getSegmentationWithName("SpeakerContribution_Event");
                Segmentation newSegmentation = new Segmentation();
                newSegmentation.setName("SpeakerContribution_Word");
                newSegmentation.setTierReference(segmentation.getTierReference());
                tier.addSegmentation(newSegmentation);
                String tierSpeaker = null;
                try {
                    if (tier.getSpeaker()!=null){
                        tierSpeaker = st.getHead().getSpeakertable().getSpeakerWithID(tier.getSpeaker()).getAbbreviation();
                    }
                } catch (JexmaraldaException ex) {
                    ex.printStackTrace();
                }
                java.util.Iterator contributionIterator = folkerDocument.getDescendants(new org.jdom.filter.ElementFilter("contribution"));
                java.util.Vector<org.jdom.Element> contributions = new java.util.Vector<org.jdom.Element>();
                while (contributionIterator.hasNext()){
                    Element con = (Element)(contributionIterator.next());
                    contributions.add(con);
                }
                for (Element cont : contributions){
                    String contSpeaker = cont.getAttributeValue("speaker-reference");
                    if ((contSpeaker==null) || (!contSpeaker.equals(tierSpeaker))) continue;
                    // transform it into a timed segment
                    String contString = org.exmaralda.common.jdomutilities.IOUtilities.elementToString(cont);
                    try {
                        String tsString = sf.applyInternalStylesheetToString("/org/exmaralda/folker/data/contribution2timedsegment.xsl", contString);
                        TimedSegment ts = tsr.readFromString(tsString);
                        // time it
                        // and add it to the new segmentation
                        newSegmentation.addSegment(ts);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new FSMException(ex.getLocalizedMessage(), "");
                    }
                }
             } 
         }                
        return st;
    }


}
