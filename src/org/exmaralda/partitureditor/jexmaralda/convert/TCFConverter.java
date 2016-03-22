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
import eu.clarin.weblicht.wlfxb.tc.api.TextCorpus;
import eu.clarin.weblicht.wlfxb.tc.api.TextLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.tc.api.PosTag;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerTag;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import eu.clarin.weblicht.wlfxb.xb.WLData;
import eu.clarin.weblicht.wlfxb.tc.api.TokensLayer;



/**
 * @author thomas
 * @author tpirinen
 *
 * @note TCF converter copied from TreeTaggerConverter.
 */
public class TCFConverter {

    TextCorpusStored textCorpus;

    /** Creates a new instance of TCFConverter */
    public TCFConverter() {}

    /** Read TCF from fis using WLData. */
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

    /** Read TCF from a file. 
     *  @sa readText(FileInputStream)
     */
    public void readText(File file) throws IOException {
        readText(new FileInputStream(file));
    }

    /** Read encoded TCF from a file.
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
     */
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
            String token_id = "t_" + new Integer(pos + 1).toString();
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
