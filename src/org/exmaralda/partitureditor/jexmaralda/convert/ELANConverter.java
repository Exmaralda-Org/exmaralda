/*
 * ELANConverter.java
 *
 * Created on 13. Oktober 2003, 15:00
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

// for parsing
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;

// For write operation
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import java.io.*;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import javax.swing.JProgressBar;

/**
 *
 * @author  thomas
 */
public class ELANConverter {
    
    /** the XSLT stylesheet for converting an EAF document to an EXMARaLDA basic transcription */
    static final String ELAN2EX_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/EAF2BasicTranscription.xsl";
    /** the XSLT stylesheet for converting  an EXMARaLDA basic transcription  to an EAF document*/
    static final String EX2ELAN_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/BasicTranscription2EAF.xsl";

    static final String SEP_CHAR = " | ";
    
    Hashtable ELAN_IDs = new Hashtable();
    
    JProgressBar progBar;
    
    /** Creates a new instance of ELANConverter */
    public ELANConverter() {
    }
    
    public void setProgressBar(JProgressBar pb){
        progBar = pb;
    }
    
    public void updateProgressBar(String message, int value){
        if (progBar == null) return;
        progBar.setString(message);
        progBar.setValue(progBar.getMaximum()*value/100);
        progBar.update(progBar.getGraphics());
        //progBar.paintImmediately(progBar.getBounds());
    }

    /** reads the EAF file specified by filename and returns an EXMARaLDA BasicTranscription */
    public BasicTranscription readELANFromFile(String filename) throws JexmaraldaException, 
                                                                       SAXException, 
                                                                       IOException, 
                                                                       ParserConfigurationException, 
                                                                       TransformerConfigurationException, 
                                                                       TransformerException  {
        // create a class for performing a stylesheet transformation                                                                           
        updateProgressBar("Setting up stylesheet factory",0);
        StylesheetFactory ssf = new StylesheetFactory(true);
        // applies the (internal) stylesheet to the specified file
        updateProgressBar("Applying stylesheet",10);
        String basicTrans = ssf.applyInternalStylesheetToExternalXMLFile(ELAN2EX_STYLESHEET, filename);      
        // creates a new (empty) basic transcription 
        BasicTranscription bt = new BasicTranscription();
        // reads the result of the XSLT transformation into that basic transcription 
        bt.BasicTranscriptionFromString(basicTrans);
        String[] originalTierOrder = bt.getBody().getAllTierIDs();
        updateProgressBar("Setting up tier order",50);
        setupTierOrder(bt);
        updateProgressBar("Setting up ID mapping",60);
        setupIDMapping(bt);
        updateProgressBar("Resolving references",65);
        resolveReferences(bt);        
        //bt.writeXMLToFile("/n/shokuji/dc/schmidt/DOCS/Hamburg/Mawng_0.xml", "none");
        updateProgressBar("Handling symbolic associations",70);
        handleSymbolicAssociations(bt);
        //bt.writeXMLToFile("/n/shokuji/dc/schmidt/DOCS/Hamburg/Mawng_0a.xml", "none");
        updateProgressBar("Glueing linked annotations",85);
        glueLinkedAnnotations(bt);
        // "normalizes" that basic transcription, i.e. harmonizes the timeline
        updateProgressBar("Normalizing the timeline",90);
        normalize(bt);        
        // reconstitute the original tier order
        updateProgressBar("Reordering tiers",95);
        bt.getBody().reorderTiers(originalTierOrder);
        // returns the normalized basic transcription
        updateProgressBar("Done.",100);
        return bt;
    }    
    
    /** converts the basic transcription into a string representing an EAF document */
    private String BasicTranscriptionToELAN(BasicTranscription t) throws SAXException, 
                                                                         IOException, 
                                                                         ParserConfigurationException, 
                                                                         TransformerConfigurationException, 
                                                                         TransformerException {
        
        // make a copy of the basic transcription
        BasicTranscription copy_t = t.makeCopy();
        
        // interpolate the timeline, i.e. calculate absoulute time values for timeline items
        // that don't have an absolute time value assigned
        // (is this necessary or can ELAN also handle time slots without absolute time values?)
        copy_t.getBody().getCommonTimeline().completeTimes();
        
        // create a class for performing a stylesheet transformation
        StylesheetFactory ssf = new StylesheetFactory();        
        // applies the (internal) stylesheet to the XML representation of the basic transcription
        String elanTrans = ssf.applyInternalStylesheetToString(EX2ELAN_STYLESHEET, copy_t.toXML());      
        // return the result of the XSLT transformation
        return elanTrans;
    }
    
    /** converts the basic transcription to an EAF document and 
     *  writes it to the specified filename */
    public void writeELANToFile(BasicTranscription t, String filename) throws SAXException,
                                                                              IOException, 
                                                                              ParserConfigurationException, 
                                                                              TransformerConfigurationException, 
                                                                              TransformerException {
        System.out.println("started writing document...");
        java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File(filename));
        fos.write(BasicTranscriptionToELAN(t).getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }
    
    
    /** removes duplicate timeline items from the timeline
     * and changes the start and end points of events 
     * accordingly */
    public void normalize(BasicTranscription bt){
        Hashtable TLIReplacements = new Hashtable();
        Timeline tl = bt.getBody().getCommonTimeline();
        // some nonsense value
        double lastTime = -1.2345678;
        // an empty ID
        String lastID = "";
        // go through the timeline
        for (int pos=0; pos<tl.getNumberOfTimelineItems(); /*pos++*/){
            // get the timeline item at index 'pos'
            TimelineItem tli = tl.getTimelineItemAt(pos);
            // if it has the same absolute time value as the last one...
            if ((tli.getTime()>=0) && (tli.getTime()==lastTime)){
                //... take a note of it, i.e. associate it with the
                // last timeline item which has the same absolute time value
                TLIReplacements.put(tli.getID(), lastID);
                // and remove it from the timeline
                tl.removeTimelineItemAt(pos);
                //pos++;
                //System.out.println("Got one: " + pos);
            } else {
                lastTime = tli.getTime();
                lastID = tli.getID();
                pos++;
            }            
        }
        // go through all the tiers of the basic transcription
        for (int tierNo=0; tierNo<bt.getBody().getNumberOfTiers(); tierNo++){
            // get the tier at index 'tierNo'
            Tier tier = bt.getBody().getTierAt(tierNo);
            // go through all the events of the current tier
            for (int eventNo=0; eventNo<tier.getNumberOfEvents(); eventNo++){
                // get the ebent at index 'eventNo'
                Event event = tier.getEventAt(eventNo);
                // if the start point is one of the removed timeline items...
                if (TLIReplacements.containsKey(event.getStart())){
                    //... replace it with the one that has the same absolute time value
                    event.setStart((String)(TLIReplacements.get(event.getStart())));
                }
                // likewise for the end point
                if (TLIReplacements.containsKey(event.getEnd())){
                    event.setEnd((String)(TLIReplacements.get(event.getEnd())));
                }
            }
        }
    }
    
    void setupTierOrder (BasicTranscription bt) throws JexmaraldaException {
        BasicBody bb = bt.getBody();
        for (int tierNo=0; tierNo<bb.getNumberOfTiers(); tierNo++){
            Tier tier = bb.getTierAt(tierNo);
            System.out.println("Determining dependency level of tier " + tier.getID());
            Tier refTier = tier;
            int depLevel = 0;
            while (refTier.getUDTierInformation().getValueOfAttribute("ELAN-TimeAlignable").equals("false")){
               refTier = bb.getTierWithID(refTier.getUDTierInformation().getValueOfAttribute("ELAN-ParentRef"));
               depLevel++;
            }
            tier.getUDTierInformation().setAttribute("ELAN-DependencyLevel",  Integer.toString(depLevel));
        }        
        String[] newOrder = new String[bb.getNumberOfTiers()];
        int tiersAccountedFor = 0;
        int currentDepLevel = 0;
        while (tiersAccountedFor < newOrder.length){
            for (int tierNo=0; tierNo<bb.getNumberOfTiers(); tierNo++){
                Tier tier = bb.getTierAt(tierNo);
                int depLevelOfThisTier = Integer.parseInt(tier.getUDTierInformation().getValueOfAttribute("ELAN-DependencyLevel"));
                if (currentDepLevel == depLevelOfThisTier){
                    newOrder[tiersAccountedFor] = tier.getID();
                    tiersAccountedFor++;
                }
            }
            currentDepLevel++;
        }
        bb.reorderTiers(newOrder);

    }
    
    void setupIDMapping (BasicTranscription bt){
        BasicBody bb = bt.getBody();
        for (int tierNo=0; tierNo<bb.getNumberOfTiers(); tierNo++){
            Tier tier = bb.getTierAt(tierNo);
            for (int eventNo=0; eventNo<tier.getNumberOfEvents(); eventNo++){
                Event event = tier.getEventAt(eventNo);
                UDInformationHashtable udInfo = event.getUDEventInformation();
                String id = udInfo.getValueOfAttribute("ELAN-ID");
                ELAN_IDs.put(id, event);
            }
        }
    }
    
    void resolveReferences(BasicTranscription bt){
        BasicBody bb = bt.getBody();
        for (int tierNo=0; tierNo<bb.getNumberOfTiers(); tierNo++){
            Tier tier = bb.getTierAt(tierNo);
            UDInformationHashtable udTierInfo = tier.getUDTierInformation();
            if (udTierInfo.getValueOfAttribute("ELAN-TimeAlignable").equalsIgnoreCase("true")) continue;
            for (int eventNo=0; eventNo<tier.getNumberOfEvents(); eventNo++){
                Event event = tier.getEventAt(eventNo);
                UDInformationHashtable udInfo = event.getUDEventInformation();
                String refID = udInfo.getValueOfAttribute("ELAN-REF");
                Event referencedEvent = (Event)(ELAN_IDs.get(refID));
                while (referencedEvent.getStart().equals("TO_BE_INFERRED")){
                    refID = referencedEvent.getUDEventInformation().getValueOfAttribute("ELAN-REF");
                    referencedEvent = (Event)(ELAN_IDs.get(refID));                    
                }
                event.setStart(referencedEvent.getStart());
                event.setEnd(referencedEvent.getEnd());
            }
        }        
    }
    
    void handleSymbolicAssociations(BasicTranscription bt){
        BasicBody bb = bt.getBody();
        for (int tierNo=0; tierNo<bb.getNumberOfTiers(); tierNo++){
            Tier tier = bb.getTierAt(tierNo);
            UDInformationHashtable udTierInfo = tier.getUDTierInformation();            
            String parentRef = udTierInfo.getValueOfAttribute("ELAN-ParentRef");
            Tier refTier = null;
            if (parentRef.length()>0){
                try {refTier = bb.getTierWithID(parentRef);} catch (JexmaraldaException je){}
            }
            //boolean isSubDivisionOfSubdivision = false;
            boolean isSubDivisionOfSubdivision = 
                (udTierInfo.getValueOfAttribute("ELAN-Constraints").equalsIgnoreCase("Symbolic_Subdivision")) &&
                (refTier!=null) &&
                (refTier.getUDTierInformation().getValueOfAttribute("ELAN-Constraints").equalsIgnoreCase("Symbolic_Subdivision"));
                
            if ((!udTierInfo.getValueOfAttribute("ELAN-Constraints").equalsIgnoreCase("Symbolic_Association")) 
                && (!isSubDivisionOfSubdivision)) continue;
            
            for (int eventNo=0; eventNo<tier.getNumberOfEvents(); eventNo++){
                Event event = tier.getEventAt(eventNo);
                // changed for 1.3.3.
                if ((event.getUDEventInformation().getValueOfAttribute("ELAN-PREV")!=null) &&
                    (event.getUDEventInformation().getValueOfAttribute("ELAN-PREV").length()>0)) continue;    
                UDInformationHashtable udInfo = event.getUDEventInformation();
                String refID = udInfo.getValueOfAttribute("ELAN-REF");
                Event referencedEvent = (Event)(ELAN_IDs.get(refID));
                String prevID = referencedEvent.getUDEventInformation().getValueOfAttribute("ELAN-PREV");
                
                if ((prevID!=null) && (prevID.length()>0)){
                    for (int pos=0; pos<tier.getNumberOfEvents(); pos++){
                        Event otherEvent = tier.getEventAt(pos);
                        if (otherEvent.getUDEventInformation().getValueOfAttribute("ELAN-REF").equals(prevID)){
                            event.getUDEventInformation().setAttribute("ELAN-PREV", otherEvent.getUDEventInformation().getValueOfAttribute("ELAN-ID"));
                            break;
                        }
                    }
                }
                
            }            
            
            updateProgressBar("Handling symbolic associations",70 + 15 * tierNo/bb.getNumberOfTiers());
        }
    }
    
    void glueLinkedAnnotations(BasicTranscription bt){
        HashSet prevIDs = new HashSet();
        BasicBody bb = bt.getBody();
        for (int tierNo=0; tierNo<bb.getNumberOfTiers(); tierNo++){
            Tier tier = bb.getTierAt(tierNo);
            UDInformationHashtable udTierInfo = tier.getUDTierInformation();
            if (udTierInfo.getValueOfAttribute("ELAN-TimeAlignable").equalsIgnoreCase("true")) continue;
            // put all annotation ids that have a previous id into the Hashset
            for (int eventNo=0; eventNo<tier.getNumberOfEvents(); eventNo++){
                Event event = tier.getEventAt(eventNo);
                UDInformationHashtable udInfo = event.getUDEventInformation();
                String prevID = udInfo.getValueOfAttribute("ELAN-PREV");
                if (prevID.length()>0) {prevIDs.add(prevID);}
            }
            for (int eventNo=0; eventNo<tier.getNumberOfEvents(); eventNo++){
//            for (int eventNo=tier.getNumberOfEvents()-1; eventNo>=0; eventNo--){
                Event event = tier.getEventAt(eventNo);
                HashSet visitedIDs = new HashSet();
                ///System.out.println("Event: " + event.toXML());
                UDInformationHashtable udInfo = event.getUDEventInformation();
                String refID = udInfo.getValueOfAttribute("ELAN-PREV");
                String ownID = udInfo.getValueOfAttribute("ELAN-ID");
                // the event has a predecessor and it is itself not somebody else's predecessor!
                // i.e. it is the last one in a chain of successions!
                if ((refID.length()>0) && (!prevIDs.contains(ownID))){
                    visitedIDs.add(ownID);
                    Event previousEvent = (Event)(ELAN_IDs.get(refID));
                    // Version 1.3.3. - changed on 03-Nov-2005
                    // String prevID = event.getUDEventInformation().getValueOfAttribute("ELAN-PREV");
                    String prevID = previousEvent.getUDEventInformation().getValueOfAttribute("ELAN-PREV");
                    String concatenatedDescription = event.getDescription();
                    
                    //do {
                    while (prevID.length()>0) {
                        visitedIDs.add(prevID);
                        System.out.println(concatenatedDescription);
                        System.out.println("ID of concatenated: " + previousEvent.getUDEventInformation().getValueOfAttribute("ELAN-ID"));                        
                        concatenatedDescription = previousEvent.getDescription() + SEP_CHAR + concatenatedDescription;
                        previousEvent = (Event)(ELAN_IDs.get(prevID));
                        //System.out.println("Previous event(2): " + previousEvent.toXML());
                        prevID = previousEvent.getUDEventInformation().getValueOfAttribute("ELAN-PREV");
                    }
                    //} while (prevID.length()>0);
                    // Version 1.3.3. - changed on 03-Nov-2005
                    // previousEvent.setDescription(concatenatedDescription);
                    concatenatedDescription = previousEvent.getDescription() + SEP_CHAR +  concatenatedDescription;
                    previousEvent.setDescription(concatenatedDescription);
                    //event.setDescription(concatenatedDescription);
                    for (Iterator i = visitedIDs.iterator(); i.hasNext();){
                        String s = (String)(i.next());
                        System.out.println(s);
                        ELAN_IDs.put(s, previousEvent);
                    }
                }
            }
            for (int eventNo=0; eventNo<tier.getNumberOfEvents(); eventNo++){
                Event event = tier.getEventAt(eventNo);
                UDInformationHashtable udInfo = event.getUDEventInformation();
                String prevID = udInfo.getValueOfAttribute("ELAN-PREV");
                if (prevID.length()>0){
                    tier.removeElementAt(eventNo);
                    eventNo--;
                }
            }
        }         
        
    }
}
