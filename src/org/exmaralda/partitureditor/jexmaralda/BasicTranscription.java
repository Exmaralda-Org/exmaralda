
package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import java.io.*;
import org.xml.sax.*;
import org.jdom.*;



/*
 * BasicTranscription.java
 *
 * Created on 7. Maerz 2001, 09:49
 */



/**
 * class corresponding to an EXMARaLDA Basic Transcription
 * @author  Thomas
 * @version 
 */
public class BasicTranscription extends AbstractTranscription {

    private BasicBody body;
    
    private TierFormatTable tierFormatTable;



    public TierFormatTable getTierFormatTable() {
        return tierFormatTable;
    }

    public void setTierFormatTable(TierFormatTable tft) {
        tierFormatTable = tft;
    }
        
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************
    
    /** Creates new BasicTranscription */
    public BasicTranscription() {
        super();
        body = new BasicBody();
    }

    /** reads in a new BasicTranscription from the specified file */
    public BasicTranscription (String inputFileName) throws SAXException, JexmaraldaException{
        this(inputFileName, true);
    }
    
    public BasicTranscription(String inputFileName, boolean check) throws SAXException, JexmaraldaException {
        super();
        body = new BasicBody();
        org.exmaralda.partitureditor.jexmaralda.sax.BasicTranscriptionSaxReader reader = new org.exmaralda.partitureditor.jexmaralda.sax.BasicTranscriptionSaxReader();
        BasicTranscription t = new BasicTranscription();
        t = reader.readFromFile(inputFileName);
        if (check){
            t.check();
        }
        setHead(t.getHead());
        setBody(t.getBody());
        setTierFormatTable(t.getTierFormatTable());
        //if (!new File(getHead().getMetaInformation().getReferencedFile()).isAbsolute()){
            // changed 13-08-2010
            // ".." in relative paths allowed now
            getHead().getMetaInformation().resolveReferencedFile(inputFileName, MetaInformation.NEW_METHOD);
        //}
        resolveLinks(inputFileName);  
        
    }

    public Hashtable<String, String[]> getAnnotationMismatches() {
        Hashtable<String, String[]> allMismatches = new Hashtable<String, String[]>();
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            Tier t = getBody().getTierAt(pos);
            String[] theseMismatches = t.getAnnotationMismatches(this);
            if (theseMismatches!=null){
                allMismatches.put(t.getID(), theseMismatches);
            }
        }
        return allMismatches;
    }

    public String[] getDuplicateTranscriptionTiers() {
        Vector<String> allDuplicates = new Vector<String>();
        HashSet<String> idMemo = new HashSet<String>();
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            Tier t = getBody().getTierAt(pos);
            if (!(t.getType().equals("t"))) continue;
            if (idMemo.contains(t.getSpeaker())){
                allDuplicates.add(t.getID());
            }
            idMemo.add(t.getSpeaker());
        }
        return allDuplicates.toArray(new String[allDuplicates.size()]);

    }

    public String[] getOrphanedTranscriptionTiers(){
        Vector<String> allOrphans = new Vector<String>();
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            Tier t = getBody().getTierAt(pos);
            if ((t.getType().equals("t") && (t.getSpeaker()==null))){
                    allOrphans.addElement(t.getID());
            }
        }
        return allOrphans.toArray(new String[allOrphans.size()]);
    }

     public String[] getOrphanedAnnotationTiers() {
        Vector<String> allOrphans = new Vector<String>();
        HashSet<String> idMemo = new HashSet<String>();
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            Tier t = getBody().getTierAt(pos);
            if (t.getType().equals("t")){
                idMemo.add(t.getSpeaker());
            }
        }
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            Tier t = getBody().getTierAt(pos);
            if (t.getType().equals("a")){
                if ((t.getSpeaker()==null) || (!(idMemo.contains(t.getSpeaker())))){
                    allOrphans.addElement(t.getID());
                }
            }
        }
        return allOrphans.toArray(new String[allOrphans.size()]);

    }

    
    /**
     * creates BasicTranscription from a given String containing an exmaralda instance
     *
     * added by JTM, 23.11.2001
     *
     **/
    public void BasicTranscriptionFromString (String inputString) throws SAXException, JexmaraldaException {
        body = new BasicBody();
        org.exmaralda.partitureditor.jexmaralda.sax.BasicTranscriptionSaxReader reader = new org.exmaralda.partitureditor.jexmaralda.sax.BasicTranscriptionSaxReader();
        BasicTranscription t = new BasicTranscription();
        t = reader.readFromString(inputString);
        //t.check();
        setHead(t.getHead());
        setBody(t.getBody());
        setTierFormatTable(t.getTierFormatTable());
    }
    
    public void BasicTranscriptionFromJDOMDocument(Document d) throws SAXException, JexmaraldaException {
        String string = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(d);
        BasicTranscriptionFromString(string);
    }
    

    /** returns a copy of this BasicTranscription */
    public BasicTranscription makeCopy(){
        BasicTranscription result = new BasicTranscription();
        result.setHead(this.getHead().makeCopy());
        result.setBody(this.getBody().makeCopy());
        return result;
    }
    
    /** returns a copy of this BasicTranscription */
    public BasicTranscription makeEmptyCopy(){
        BasicTranscription result = new BasicTranscription();
        result.setHead(this.getHead().makeCopy());
        result.setBody(this.getBody().makeEmptyCopy());
        return result;
    }
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    
    /** returns the body of the transcription */
    public BasicBody getBody(){
        return body;
    }
    
    /** sets the body of the transcription to the specified value */
    public void setBody(BasicBody b){
        body = b;
    }
           
    // ********************************************
    // ********** BASIC MANIPULATION **************
    // ********************************************

    /** checks the well-formedness of this transcription, i.e.
     * everything that is not covered by the DTD */
    public void check() throws JexmaraldaException {
        getBody().check();
    }

    /** returns a part of this transcription: all events from the specified tiers that
     * lie between the specified start and end point */
    public BasicTranscription getPartOfTranscription(String[] tierIDs, String startTLI, String endTLI){
        return getPartOfTranscription(tierIDs, startTLI, endTLI, false);
    }
    
    public BasicTranscription getPartOfTranscription(String[] tierIDs, String startTLI, String endTLI, boolean anchor){
        BasicTranscription result = new BasicTranscription();
        result.setHead(this.getHead().makeCopy());
        try {
            result.setBody(getBody().getPartOfBody(tierIDs, startTLI, endTLI, anchor));
        } catch (JexmaraldaException je){
        }
        return result;
    }



    /** checks if all the speakers referred to from the tiers appear in the speakertable *
     * if not, sets the speaker of the tiers in question to null 
     * returns true if such a change has been made, false otherwise */
    public boolean checkSpeakers(){
        boolean result = false;
        for (int pos=0; pos<getBody().getNumberOfTiers();pos++){
            Tier t = getBody().getTierAt(pos);
            if (t.getSpeaker()!=null){
                if (!getHead().getSpeakertable().containsSpeakerWithID(t.getSpeaker())){
                    t.setSpeaker(null);
                    result = true;
                }
            }
        }
        return result;
    }
    
    /** checks if there is a parent tier for every annotation tier *
     * for each tier: sets the corresponding return value to -1 if *
     * the check is not applicable (i.e. if the respective tier is not of type 'a') *
     * sets the return value to 0 if there is no parent tier, sets *
     * the return value to 1 if there is a parent tier */
    public int[] checkAnnotationTierDependencies(){
        int tiercount = getBody().getNumberOfTiers();
        int[] returnvalue = new int[tiercount];
        for (int pos=0; pos<tiercount; pos++){
            Tier t = getBody().getTierAt(pos);
            if (!t.getType().equals("a")){
                returnvalue[pos]=-1;
                continue;
            }
            boolean foundMatch = false;
            for (int pos2=0; pos2<tiercount; pos2++){
                Tier t2 = getBody().getTierAt(pos2);
                // changed 03-11-2009: there may be t-tiers without a speaker
                if ((t2.getType().equals("t")) && (t2.getSpeaker()!=null) && (t2.getSpeaker().equals(t.getSpeaker()))){
                    foundMatch = true;
                    break;
                }
            }
            if (foundMatch) {
                returnvalue[pos] = 1;
            } else {
                returnvalue[pos] = 0;
            }
        }
        return returnvalue;
    }
    
    public int[] checkAnnotationMismatches(){
        int tiercount = getBody().getNumberOfTiers();
        int[] returnvalue = new int[tiercount];
        for (int pos=0; pos<tiercount; pos++){
            Tier t = getBody().getTierAt(pos);
            String[] mm = t.getAnnotationMismatches(this);
            if (mm==null){
                returnvalue[pos]=-1;                
            } else {
                returnvalue[pos]=mm.length;                                
            }
        }
        return returnvalue;        
    }

     /** returns the positions of those tiers whose display name
     * corresponds to the name generated from speaker abbreviation and 
     * category */
    public int[] getTierNumbersWithAutoDisplayName(){
        Vector resultVector = new Vector();
        for (int pos=0; pos<getBody().getNumberOfTiers();pos++){
            Tier t = getBody().getTierAt(pos);
            if (t.getDescription(getHead().getSpeakertable()).equals(t.getDisplayName())){
                resultVector.add(new Integer(pos));
            }
        }
        int[] result = new int[resultVector.size()];
        for (int pos=0; pos<resultVector.size(); pos++){
            result[pos] = ((Integer)(resultVector.elementAt(pos))).intValue();
        }
        return result;
    }
    
    /** generates display names automatically (this is for compatability with
     * transcriptions before version 1.2.5.) */
    public void makeAutoDisplayName(int[] tierNos){
        for (int pos=0; pos<tierNos.length; pos++){
            Tier t = getBody().getTierAt(tierNos[pos]);
            t.setDisplayName(t.getDescription(getHead().getSpeakertable()));
        }
    }
    
    /** makes display names where none are given (this is for compatability with
     * transcriptions before version 1.2.5.) */
    public void makeDisplayNames(){
        for (int pos=0; pos<getBody().getNumberOfTiers();pos++){
            Tier t = getBody().getTierAt(pos);
            if (t.getDisplayName()==null){
                t.setDisplayName(t.getDescription(getHead().getSpeakertable()));
            }
        }
    }
    
    /**
     * Maps the tiers of this transcription to the tiers of the other transcription.
     * Returns a vector with string arrays [ID1, ID2] that contain matching tier IDs.
     */    
    public Vector makeTierIDMappings(BasicTranscription otherTrans){
        Vector resultVector = new Vector();
        for (int pos1=0; pos1<getBody().getNumberOfTiers(); pos1++){
            Tier tier = getBody().getTierAt(pos1);
            String speakerAbb = "";
            try {
                speakerAbb = getHead().getSpeakertable().getSpeakerWithID(tier.getSpeaker()).getAbbreviation();
            } catch (Exception e1) {}
            String category = tier.getCategory();
            String type = tier.getType();
            for (int pos2=0; pos2<otherTrans.getBody().getNumberOfTiers(); pos2++){
                Tier otherTier = otherTrans.getBody().getTierAt(pos2);
                String otherSpeakerAbb = "";
                try {
                    otherSpeakerAbb = otherTrans.getHead().getSpeakertable().getSpeakerWithID(otherTier.getSpeaker()).getAbbreviation();
                } catch (Exception e2) {}
                String otherCategory = otherTier.getCategory();
                String otherType = otherTier.getType();
                if (speakerAbb.equals(otherSpeakerAbb) &&
                    category.equals(otherCategory) &&
                    type.equals(otherType)){
                        String id1 = tier.getID();
                        String id2 = otherTier.getID();
                        String[] mapping = {id1, id2};
                        resultVector.add(mapping);
                        break;
                }
            }
        }
        return resultVector;
    }
    
    /**
     * Glues the other transcription to the end of this transcription
     * using the given tier mappings
     */    
    public void glue(BasicTranscription otherTrans, String[][] tierIDMappings) throws JexmaraldaException {
        glue(otherTrans,tierIDMappings,false);
    }
    
    public void glue(BasicTranscription otherTrans, String[][] tierIDMappings, boolean merge) throws JexmaraldaException {
        getBody().glue(otherTrans.getBody(), tierIDMappings, merge);
    }

    public void merge(BasicTranscription otherTrans) throws JexmaraldaException{
        
        // merge speakertables
        Hashtable<String,String> speakerMappings = new Hashtable<String,String>();
        Speakertable thisSpeakertable = getHead().getSpeakertable();
        Speakertable otherSpeakertable = otherTrans.getHead().getSpeakertable();
        for (int pos=0; pos<otherSpeakertable.getNumberOfSpeakers(); pos++){
            Speaker speaker = otherSpeakertable.getSpeakerAt(pos);
            String oldID = speaker.getID();
            String newID = thisSpeakertable.getFreeID();
            speaker.setID(newID);
            thisSpeakertable.addSpeaker(speaker);
            speakerMappings.put(oldID, newID);
        }
        
        //merge timelines
        Hashtable<String,String> tliMappings = new Hashtable<String,String>();
        Timeline thisTimeline = getBody().getCommonTimeline();
        Timeline otherTimeline = otherTrans.getBody().getCommonTimeline();
        thisTimeline.completeTimes();
        otherTimeline.completeTimes();
        for (int pos=0; pos<otherTimeline.getNumberOfTimelineItems(); pos++){
            TimelineItem tli = otherTimeline.getTimelineItemAt(pos);
            String oldID = tli.getID();
            // does this timeline contain a timeline item which has 
            // (nearly) the same time as the current tli from the other timeline?
            int foundPosition = thisTimeline.findTimelineItem(tli.getTime(), 0.001);
            if (foundPosition>=0){
                TimelineItem matchingTLI = thisTimeline.getTimelineItemAt(foundPosition);
                tliMappings.put(oldID,matchingTLI.getID());                
            } else {
                String newID = thisTimeline.getFreeID();
                tli.setID(newID);
                thisTimeline.insertAccordingToTime(tli);
                tliMappings.put(oldID,newID);
            }
        }
        
        //merge tiers
        for (int pos=0; pos<otherTrans.getBody().getNumberOfTiers(); pos++){
            Tier tier = otherTrans.getBody().getTierAt(pos);
            String newID = getBody().getFreeID();
            tier.setID(newID);
            //change speaker assigment
            if (tier.getSpeaker()!=null){
                tier.setSpeaker(speakerMappings.get(tier.getSpeaker()));
            }
            //change timeline assigments of events
            for (int i=0; i<tier.getNumberOfEvents(); i++){
                Event event = tier.getEventAt(i);
                event.setStart(tliMappings.get(event.getStart()));
                event.setEnd(tliMappings.get(event.getEnd()));
            }
            getBody().addTier(tier);
        }
    }
    
    /**
     * Chops this transcription into several smaller Basic Transcriptions.
     * The parameter determines the minimum number of timeline items
     * that the resulting transcriptions will contain.
     * Returns the resulting transcriptions as an array.
     */    
    public BasicTranscription[] chop(int minNumberOfTimelineItems){
        BasicBody[] bodies = getBody().chop(minNumberOfTimelineItems);
        BasicTranscription[] result = new BasicTranscription[bodies.length];
        for (int pos=0; pos<bodies.length; pos++){
            result[pos] = new BasicTranscription();
            result[pos].setHead(this.getHead());
            result[pos].setBody(bodies[pos]);
        }
        return result;
    }
    
    
    /** second attempt, more generic, no reference tier */
    public void unhack2(String unhackTierID) throws JexmaraldaException{
        
        HashSet<String> tliIDsUsedOnOtherTiers = new HashSet<String>();
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            Tier thisTier = getBody().getTierAt(pos);
            if (thisTier.getID().equals(unhackTierID)) continue;
            for (int i=0;i<thisTier.getNumberOfEvents();i++){
                Event e = thisTier.getEventAt(i);
                tliIDsUsedOnOtherTiers.add(e.getStart());
                tliIDsUsedOnOtherTiers.add(e.getEnd());
            }
        }
        
        Tier unhackTier = getBody().getTierWithID(unhackTierID);
        Timeline tl = getBody().getCommonTimeline();
        unhackTier.sort(tl);
        
        ArrayList<ArrayList<Event>> scs = new ArrayList<ArrayList<Event>>();        
        ArrayList<Event> currentSc = new ArrayList<Event>();
        
        String lastEnd="";        
        for (int pos=0; pos<unhackTier.getNumberOfEvents(); pos++){
            Event event = unhackTier.getEventAt(pos);
            if (!(lastEnd.equals(event.getStart()))){
                if (!currentSc.isEmpty()){
                    scs.add(currentSc);
                }
                currentSc = new ArrayList<Event>();
            }
            currentSc.add(event);
            lastEnd = event.getEnd();
        }
        scs.add(currentSc);
        
        ArrayList<Event> newEvents = new ArrayList<Event>();
        for (ArrayList<Event> sc : scs){
            Event currentEvent = null;
            for (Event event : sc){
                if (currentEvent==null || tliIDsUsedOnOtherTiers.contains(event.getStart())){
                    if (currentEvent!=null){
                        newEvents.add(currentEvent);
                    }
                    currentEvent = event;
                } else {
                    currentEvent.setDescription(currentEvent.getDescription() + event.getDescription());
                    currentEvent.setEnd(event.getEnd());
                }
                if (sc.indexOf(event)==sc.size()-1){
                    newEvents.add(currentEvent);                    
                }
            }
        }
        
        unhackTier.removeAllEvents();
        for (Event e : newEvents){
            unhackTier.addEvent(e);
        }
        //System.out.println(unhackTier.toXML());
        getBody().removeUnusedTimelineItems();
        

    
    }
    
    /** new 30-10-2012, for SiN data, remove superfluous event boundaries */
    public void unhack(String unhackTierID, String referenceTierID) throws JexmaraldaException{
        Tier unhackTier = getBody().getTierWithID(unhackTierID);
        Tier referenceTier = getBody().getTierWithID(referenceTierID);
        Timeline tl = getBody().getCommonTimeline();
        referenceTier.sort(tl);
        ArrayList<Event> unhackedEvents = new ArrayList<Event>();
        for (int pos=0; pos<referenceTier.getNumberOfEvents(); pos++){
              Event e = referenceTier.getEventAt(pos);              
              String startTLI = e.getStart();
              String endTLI = e.getEnd();
              String currentText = "";
              for (int i=tl.lookupID(startTLI); i<tl.lookupID(endTLI); i++){
                  TimelineItem tli = tl.getTimelineItemAt(i);
                  if (!(unhackTier.containsEventAtStartPoint(tli.getID()))) continue;
                  Event currentEvent = unhackTier.getEventAtStartPoint(tli.getID());
                  //System.out.println(currentEvent.getDescription());
                  boolean otherTierHasEvent = false;
                  Event otherEvent = null;
                  for (int j=0; j<getBody().getNumberOfTiers(); j++){
                      Tier checkTier = getBody().getTierAt(j);
                      if (checkTier.getID().equals(unhackTierID) || checkTier.getID().equals(referenceTierID)) continue;
                      if (checkTier.containsEventAtStartPoint(tli.getID())){
                        otherEvent = checkTier.getEventAtStartPoint(tli.getID());
                        if (
                                tl.lookupID(otherEvent.getEnd())<tl.lookupID(endTLI) ||
                                (tl.lookupID(otherEvent.getEnd())==tl.lookupID(endTLI) &&
                                (tl.lookupID(otherEvent.getStart())>tl.lookupID(e.getStart())))
                           ){
                            otherTierHasEvent = true;
                            break;
                        }
                      }
                  }
                  if (otherTierHasEvent){
                      if (!(startTLI.equals(otherEvent.getStart()))){
                          Event newEvent = new Event();
                          newEvent.setStart(startTLI);
                          newEvent.setEnd(otherEvent.getStart());
                          newEvent.setDescription(currentText);
                          unhackedEvents.add(newEvent);
                      }
                      // -----
                      startTLI = otherEvent.getEnd();
                      i = tl.lookupID(startTLI) - 1;
                      for (int k=tl.lookupID(otherEvent.getStart()); k<tl.lookupID(otherEvent.getEnd()); k++){
                          String betweenTLI = tl.getTimelineItemAt(k).getID();
                          if (unhackTier.containsEventAtStartPoint(betweenTLI)){
                              unhackedEvents.add(unhackTier.getEventAtStartPoint(betweenTLI));
                          }
                      }
                      // -----
                      currentText = "";
                  } else {
                      currentText+=currentEvent.getDescription();
                  }
              }
              if (!(startTLI.equals(endTLI))){
                  Event newEvent = new Event();
                  newEvent.setStart(startTLI);
                  newEvent.setEnd(endTLI);
                  newEvent.setDescription(currentText);
                  unhackedEvents.add(newEvent);              
              }
        }
        unhackTier.removeAllEvents();
        for (Event e : unhackedEvents){
            unhackTier.addEvent(e);
        }
        //System.out.println(unhackTier.toXML());
        getBody().removeUnusedTimelineItems();
    }
    
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns the transcription as an XML element &lt;basic-transcription&gt; as
     *  specified in the corresponding dtd */
    public String toXML(TierFormatTable tft) {
        StringBuilder sb=new StringBuilder();
        sb.append("<basic-transcription>\n");
        sb.append(super.toXML());
        sb.append(getBody().toXML());
        if (tft!=null){
            sb.append(tft.toXML());
        }
        sb.append("</basic-transcription>\n");
        return sb.toString();
    }

    public String toXML() {
        return toXML(null);
    }

    
    /** writes an XML file with the specified file name and the specified path to
     *  the dtd */
    public void writeXMLToFile(String filename, String pathToDTD, TierFormatTable tft)throws IOException {
        // changed 11-05-2010: new method can also produce relative paths that
        // go via parent folders, i.e. including one or more ..s
        getHead().getMetaInformation().relativizeReferencedFile(filename, MetaInformation.NEW_METHOD);
        // change rolled back, can't find a decent method to resolve such a path
        // 13-08-2010: change reintroduced
        //getHead().getMetaInformation().relativizeReferencedFile(filename);
        relativizeLinks(filename);
        System.out.println("started writing document" + filename + "...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(StringConstants.XML_HEADER.getBytes("UTF-8"));        
        fos.write(StringUtilities.makeXMLDoctypeBasicTranscription(pathToDTD).getBytes("UTF-8"));
        fos.write(StringConstants.XML_COPYRIGHT_COMMENT.getBytes("UTF-8"));        
        fos.write(toXML(tft).getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
        // ".." in relative paths allowed now
        getHead().getMetaInformation().resolveReferencedFile(filename, MetaInformation.NEW_METHOD);
        resolveLinks(filename);
    }

    public void writeXMLToFile(String filename, String pathToDTD) throws IOException {
        writeXMLToFile(filename, pathToDTD, null);
    }
    
    public org.jdom.Document toJDOMDocument() throws JDOMException, IOException{
        return org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString(this.toXML());
    }
    
    
    // ********************************************
    // ********** EXMARALDA CONVERSIONS ***********
    // ********************************************

    /** converts this BasicTranscription to a SegmentedTranscription */
    public SegmentedTranscription toSegmentedTranscription(){
        SegmentedTranscription result = new SegmentedTranscription();
        result.setHead(this.getHead());
        result.setBody(this.getBody().toSegmentedBody());
        result.setConversionInfo(this.generateStandardConversionInfo());
        return result;
    }
    
    public ConversionInfo generateStandardConversionInfo(){
        ConversionInfo result = new ConversionInfo();
        BasicBody body = getBody();
        result.setTimeline(body.getCommonTimeline().makeCopy());
        for (int pos=0; pos<body.getNumberOfTiers(); pos++){
            Tier t = body.getTierAt(pos);
            if (t.getType().equals("t")){
                String[] info = { t.getID(), "SpeakerContribution_Event", t.getCategory(),
                                  t.getDisplayName(), t.getType()};
                result.addTierConversionInfo(info);
            } else if (t.getType().equals("d")){
                String[] info = { t.getID(), "Event", t.getCategory(),
                                  t.getDisplayName(), t.getType()};
                result.addTierConversionInfo(info);                
            } else if (t.getType().equals("a") && (t.getSpeaker()!=null)){
                // determine the parent ID for this annotation tier
                for (int pos2=0; pos2<body.getNumberOfTiers(); pos2++){
                    Tier t2 = body.getTierAt(pos2);
                    if (t2.getSpeaker()==null) continue;
                    if (t2.getSpeaker().equals(t.getSpeaker()) && t2.getType().equals("t")){
                        String[] info = { t2.getID(), t.getCategory(), t.getCategory(),
                                          t.getDisplayName(), t.getType()};
                        result.addTierConversionInfo(info);
                        break;
                    }
                }                
                // annotation with speaker but without parent ID will be ignored!!!!!
            } else if (t.getType().equals("a")) {    // i.e. annotation with null speaker
                 String[] info = { t.getID(), t.getCategory(), t.getCategory(),
                                   t.getDisplayName(), t.getType()};                
                 result.addTierConversionInfo(info);                
            } else {    // i.e. other type (l or u)
            }
        }
        return result;
    }
    
    public void relativizeLinks(String relativeToWhat){
        for (int pos=0; pos<body.getNumberOfTiers(); pos++){
            Tier t = body.getTierAt(pos);
            for (int i=0; i<t.getNumberOfEvents(); i++){
                t.getEventAt(i).relativizeLink(relativeToWhat);
            }
        }
    }

    public void resolveLinks(String relativeToWhat){
        for (int pos=0; pos<body.getNumberOfTiers(); pos++){
            Tier t = body.getTierAt(pos);
            for (int i=0; i<t.getNumberOfEvents(); i++){
                t.getEventAt(i).resolveLink(relativeToWhat);
            }
        }
    }
    
    /** makes uniform speaker, tier and timeline IDs */
    public void normalize(){
        Hashtable speakerMappings = getHead().getSpeakertable().normalize();
        getBody().normalize(speakerMappings);
    }
    
    /** replaces multiple whitespace inside T tier events by single whitespace */
    public void normalizeWhiteSpace(){
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            Tier t = getBody().getTierAt(pos);
            if (!(t.getType().equals("t"))) continue;
            for (int i=0; i<t.getNumberOfEvents(); i++){
                Event e = t.getEventAt(i);
                String newDescription = e.getDescription().replaceAll(" {2,}", " ");
                e.setDescription(newDescription);
            }
        }
    }

    public int replaceAllInEvents(String searchExpression, String replaceExpression) {
        int count = 0;
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            Tier tier = getBody().getTierAt(pos);
            for (int i=0; i<tier.getNumberOfEvents(); i++){
                Event e = tier.getEventAt(i);
                String original = e.getDescription();
                String changed = original.replaceAll(searchExpression, replaceExpression);
                if (!(changed.equals(original))){
                    count++;
                    e.setDescription(changed);
                }

            }
        }
        return count;
    }



    

}