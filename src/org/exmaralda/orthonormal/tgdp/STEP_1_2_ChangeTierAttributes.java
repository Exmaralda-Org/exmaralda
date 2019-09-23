/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.tgdp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Thomas_Schmidt
 */
public class STEP_1_2_ChangeTierAttributes extends AbstractEAFProcessor {

    File OUT = new File("D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\2-CHANGE_TIER_ATTRIBUTES");
    File OUT_MOVE = new File("D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\2-CHANGE_TIER_ATTRIBUTES-PROBLEMS");
    
    File LINGUISTIC_TYPES_DEF = new File("D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\LINGUISTIC_TYPES_DEFINITION.xml");
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            STEP_1_2_ChangeTierAttributes x = new STEP_1_2_ChangeTierAttributes();
            x.IN_DIR = "D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\1-REMOVED_XSI";                
            x.doit();
        } catch (IOException ex) {
            Logger.getLogger(STEP_1_2_ChangeTierAttributes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Override
    public void processFile(File eafFile) throws IOException {
        try {
            org.jdom.Document doc = FileIO.readDocumentFromLocalFile(eafFile);
            java.util.List tiers = XPath.selectNodes(doc, "//TIER");
            System.out.println(tiers.size() + " tiers");
            boolean moveIntoProblemFolder = false;
            boolean atLeastOneSpeakerMatches = false;
            boolean atLeastOneInterviewerMatches = false;
            String[] numbers = eafFile.getName().split("-");
            String interviewerNumberByFilename = numbers[0];
            String speakerNumberByFilename = numbers[1];
            
            StringBuilder problemLog = new StringBuilder();
            
            for (Object o : tiers){
                /*<TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="Default" PARTICIPANT="001"
                    TIER_ID="Interviewer 1">*/
                Element tier = (Element)o;
                String tierID = tier.getAttributeValue("TIER_ID");
                
                // Remove empty default tiers
                if (isEmptyDefaultTier(tier)){
                    tier.detach();
                    System.out.println("   Removed empty default tier");
                    continue;
                }
                
                // Remove IPA tiers
                if (tierID.startsWith("IPA")){
                    tier.detach();
                    System.out.println("   Removed IPA tier");
                    continue;                    
                }
                
                // Remove Codeswitching tiers
                if (tierID.startsWith("Codeswitching")){
                    tier.detach();
                    System.out.println("   Removed Codeswitching tier");
                    continue;                    
                }

                // Remove Comments tiers
                if (tierID.startsWith("Comments")){
                    tier.detach();
                    System.out.println("   Removed Comments tier");
                    continue;                    
                }

                System.out.println(tierID);
                
                String checkRegex = "^(Translation (- )?)?(Speaker|Interviewer)( #?\\d{0,3})?$";
                if (!(tierID.matches(checkRegex))){
                    System.out.println("   Tier ID not parsable");
                    problemLog.append("Tier ID ").append(tierID).append(" is not parsable.\n");
                    moveIntoProblemFolder = true;
                    break;
                }
                
                // now we're sure it is either a speaker or an interviewer tier
                boolean isSpeakerTier = tierID.contains("Speaker");
                boolean isMainTier = (!(tierID.contains("Translation") || tierID.contains("Comments")));
                

                String digregex = "\\d{1,3}";
                Pattern p = Pattern.compile(digregex);
                Matcher m = p.matcher(tierID);
                String numberByTierID = null;
                if (m.find()){
                    numberByTierID = m.group();
                } 
                
                atLeastOneSpeakerMatches = atLeastOneSpeakerMatches ||
                        (isSpeakerTier && isMainTier && (numberByTierID==null || Integer.parseInt(numberByTierID)==Integer.parseInt(speakerNumberByFilename)));
                
                atLeastOneInterviewerMatches = atLeastOneInterviewerMatches || 
                        ((!isSpeakerTier) && isMainTier && (numberByTierID==null || (Integer.parseInt(numberByTierID)==Integer.parseInt(interviewerNumberByFilename))));

                String harmonizedSpeakerID = "";
                
                if (numberByTierID!=null){
                    harmonizedSpeakerID = numberByTierID;
                } else {
                    if (isSpeakerTier){
                        harmonizedSpeakerID = speakerNumberByFilename;
                    } else {
                        harmonizedSpeakerID = interviewerNumberByFilename;
                    }
                }
                while (harmonizedSpeakerID.length()<3){
                    harmonizedSpeakerID = "0" + harmonizedSpeakerID;
                }
                if (isSpeakerTier){
                    harmonizedSpeakerID = "Speaker_" + harmonizedSpeakerID;
                } else {
                    harmonizedSpeakerID = "Interviewer_" + harmonizedSpeakerID;                    
                }
                
                tier.setAttribute("PARTICIPANT", harmonizedSpeakerID);
                System.out.println("  ==> Participant ID: " + harmonizedSpeakerID);
                
                String linguisticType = "TRANSCRIPTION";
                if (tierID.contains("Translation")){
                    linguisticType = "TRANSLATION";
                }
                tier.setAttribute("LINGUISTIC_TYPE_REF", linguisticType);
                System.out.println("  ==> Linguistic Type: " + linguisticType);
                
                tier.setAttribute("TIER_ID", linguisticType + "_" + harmonizedSpeakerID);
                System.out.println("  ==> Tier ID: " + linguisticType + "_" + harmonizedSpeakerID);
                
                Document lingTypeDoc = FileIO.readDocumentFromLocalFile(LINGUISTIC_TYPES_DEF);
                List l = XPath.selectNodes(doc, "//*[self::LINGUISTIC_TYPE or self::LOCALE  or self::CONSTRAINT]");
                for (Object o2 : l){
                    ((Element)o2).detach();
                }
                
                List l2 = lingTypeDoc.getRootElement().removeContent();
                for (Object o3 : l2){
                    doc.getRootElement().addContent((Content)o3);
                }
                
                
                if (isMainTier){
                    tier.removeAttribute("PARENT_REF");
                } else {
                    tier.setAttribute("PARENT_REF", "TRANSCRIPTION_" + harmonizedSpeakerID);
                }


            }
            if (!atLeastOneSpeakerMatches){
                problemLog.append("The speaker ").append(speakerNumberByFilename).append(" should be there according to the filename, but there is no main tier for that speaker.\n");
            }
            if (!atLeastOneInterviewerMatches){
                problemLog.append("The interviwer ").append(interviewerNumberByFilename).append(" should be there according to the filename, but there is no main tier for that interviewer.\n");
            }
            
            List transL = XPath.selectNodes(doc, "//TIER[@LINGUISTIC_TYPE_REF='TRANSLATION']");
            for (Object o : transL){
                Element tier = (Element)o;
                String parentRef = tier.getAttributeValue("PARENT_REF");
                Element parentTier = ((Element)XPath.selectSingleNode(doc, "//TIER[@TIER_ID='" + parentRef + "']"));
                if (parentTier==null){
                    moveIntoProblemFolder = true;
                    problemLog.append("No parent tier for tier ").append(tier.getAttributeValue("TIER_ID")).append("\n")                      ;
                }
                int countTranslations = tier.getChildren("ANNOTATION").size();
                int countTranscriptions = parentTier.getChildren("ANNOTATION").size();
                if (countTranslations>countTranscriptions){
                    moveIntoProblemFolder = true;
                    problemLog.append("Number of translations in tier ").append(tier.getAttributeValue("TIER_ID")).append(" is greater than number of transcribed utterances in tier ").append(parentRef)
                            .append("\n");
                    
                }
            }
            
            
            
            moveIntoProblemFolder = moveIntoProblemFolder || (!atLeastOneSpeakerMatches) || (!atLeastOneInterviewerMatches);
            if (moveIntoProblemFolder){
                System.out.println("Moving into problem folder : " + eafFile.getName());
                System.out.println(problemLog.toString());
                Comment comment = new Comment(problemLog.toString());
                doc.getRootElement().addContent(0, comment);
                OUT_MOVE.mkdir();
                FileIO.writeDocumentToLocalFile(new File(OUT_MOVE, eafFile.getName()), doc);                
                
            } else {
                OUT.mkdir();
                FileIO.writeDocumentToLocalFile(new File(OUT, eafFile.getName()), doc);                
            }
        } catch (JDOMException ex) {
            throw new IOException(ex);
        }
            
        
    }

    private boolean isEmptyDefaultTier(Element tier) throws JDOMException {
            /*<TIER LINGUISTIC_TYPE_REF="default-lt" TIER_ID="default">
                <ANNOTATION>
                    <ALIGNABLE_ANNOTATION ANNOTATION_ID="a1" TIME_SLOT_REF1="ts1" TIME_SLOT_REF2="ts3">
                        <ANNOTATION_VALUE/>
                    </ALIGNABLE_ANNOTATION>
                </ANNOTATION>
            </TIER>*/
            
            return (
                    (tier.getAttributeValue("TIER_ID").equals("default")) &&
                    /*(tier.getChildren().size()<=1) &&*/
                    (XPath.selectSingleNode(tier, "descendant::ANNOTATION_VALUE[string-length()>0]")==null)
                   );
        
    }
    
}
