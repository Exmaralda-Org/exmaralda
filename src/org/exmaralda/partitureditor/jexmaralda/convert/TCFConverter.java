/**
 * @file TCFConverter.java
 *
 * 
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

// using wlfxb for conversions
import eu.clarin.weblicht.wlfxb.io.TextCorpusStreamed;
import eu.clarin.weblicht.wlfxb.io.WLDObjector;
import eu.clarin.weblicht.wlfxb.io.WLFormatException;
import eu.clarin.weblicht.wlfxb.tc.api.Lemma;
import eu.clarin.weblicht.wlfxb.tc.api.LemmasLayer;
import eu.clarin.weblicht.wlfxb.tc.api.PosTagsLayer;
import eu.clarin.weblicht.wlfxb.tc.api.SentencesLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Sentence;
import eu.clarin.weblicht.wlfxb.tc.api.TextLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.tc.api.PosTag;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerTag;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import eu.clarin.weblicht.wlfxb.xb.WLData;


/**
 * @author thomas
 * @author tpirinen
 *
 * @note TCF converter copied from TreeTaggerConverter.
 */
public class TCFConverter {

    TextCorpusStored textCorpus;

    /** Creates a new instance of TextConverter */
    public TCFConverter() {}

    public void readText(FileInputStream fis)
    {
        try {
            WLDObjector wldo = new WLDObjector();
            WLData data = wldo.read(fis);
            textCorpus = data.getTextCorpus();
        } catch (WLFormatException wlfe) {
            wlfe.printStackTrace();
        }
    }

    public void readText(File file) throws IOException {
        readText(new FileInputStream(file));
    }

    public void readText(File file, String encoding) throws 
        FileNotFoundException, IOException, UnsupportedEncodingException
    {
        // XXX: encoding
        readText(new FileInputStream(file));
    }

    public BasicTranscription importText(){
        BasicTranscription bt = new BasicTranscription();
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

        // do we always have tokens, right?
        Tier tokenTier = new Tier();
        tokenTier.setID("TIE0");
        tokenTier.setCategory("txt");
        tokenTier.setType("t");
        tokenTier.setDisplayName("X [token]");
        tokenTier.setSpeaker("SPK0");

        Map<String, String> tokenStartTimelineItems =
            new HashMap<String, String>();
        Map<String, String> tokenEndTimelineItems =
            new HashMap<String, String>();

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
        String tliEnd = tli;

        // text layer covers whole timeline?
        Tier textTier = new Tier();
        textTier.setID("TIE1");
        textTier.setCategory("txt");
        textTier.setType("t");
        textTier.setDisplayName("X [txt]");
        textTier.setSpeaker("SPK0");
        TextLayer text = textCorpus.getTextLayer();
        Event e = new Event();
        e.setStart(tliStart);
        e.setDescription(text.getText());
        e.setEnd(tliEnd);
        textTier.addEvent(e);

        // sentences cover some subsets
        Tier sentenceTier = new Tier();
        sentenceTier.setID("TIE4");
        sentenceTier.setCategory("txt");
        sentenceTier.setType("t");
        sentenceTier.setDisplayName("X [sent]");
        sentenceTier.setSpeaker("SPK0");
        SentencesLayer sents = textCorpus.getSentencesLayer();
        for (int i = 0; i < sents.size(); i++) {
            Sentence sentence = sents.getSentence(i);
            Token[] tokenses = sents.getTokens(sentence);
            String descr = "";
            for (int j = 0; j < tokenses.length; j++) {
                descr = descr + " " + tokenses[j].getString();
            }
            e = new Event();
            String tliStartNow = 
                tokenStartTimelineItems.get(tokenses[0].getID());
            e.setStart(tliStartNow);
            e.setDescription(descr);
            String tliEndNow = tokenEndTimelineItems.get(
                tokenses[tokenses.length - 1].getID());
            e.setEnd(tliEndNow);
            sentenceTier.addEvent(e);
        }

        // other layers?  (this starts to be a bit of copy-pasta)
        // please to be refactoring next time you see this...
        Tier posTier = new Tier();
        posTier.setID("TIE2");
        posTier.setCategory("pos");
        posTier.setType("a");
        posTier.setDisplayName("X [pos]");
        posTier.setSpeaker("SPK0");
        PosTagsLayer poses = textCorpus.getPosTagsLayer();
        for (int i = 0; i < poses.size(); i++) {
            PosTag pos = poses.getTag(i);
            e = new Event();
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
        // Lemmas
        Tier lemmaTier = new Tier();
        lemmaTier.setID("TIE3");
        lemmaTier.setCategory("lemma");
        lemmaTier.setType("a");
        lemmaTier.setDisplayName("X [lemma]");
        lemmaTier.setSpeaker("SPK0");
        LemmasLayer lemmas = textCorpus.getLemmasLayer();
        for (int i = 0; i < lemmas.size(); i++) {
            Lemma lemma = lemmas.getLemma(i);
            e = new Event();
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


        try {
            bt.getBody().addTier(tokenTier);
            bt.getBody().addTier(textTier);
            bt.getBody().addTier(sentenceTier);
            bt.getBody().addTier(posTier);
            bt.getBody().addTier(lemmaTier);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        return bt;        
    }

    public static void main(String[] args){
        try {
            File f = new File("tcf04-karin-wl.xml");
            TCFConverter tc = new TCFConverter();
            tc.readText(f);
            BasicTranscription bt = tc.importText();
            bt.writeXMLToFile("tcf04-karin-wl.exb", "none");
            System.out.println(tc.importText().toXML());
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String exportBasicTranscription(BasicTranscription bt) 
            throws IOException {
       // should create document using WLBLeach
       return "DANGER TERROR HORROR !!!!!!";
    }

    public void writeText(BasicTranscription bt, File file) throws IOException {
        String text = exportBasicTranscription(bt);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes("UTF-8"));
        fos.close();
        
    }
    
    
}
