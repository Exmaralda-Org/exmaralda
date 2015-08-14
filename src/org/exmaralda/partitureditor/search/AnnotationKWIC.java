/*
 * AnnotationKWIC.java
 *
 * Created on 30. Juni 2003, 09:17
 */

package org.exmaralda.partitureditor.search;

import org.exmaralda.partitureditor.jexmaralda.AbstractSegmentVector;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Speakertable;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.BasicBody;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import java.util.*;
import java.io.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class AnnotationKWIC {
    
    static String corpusFileName;
    static Vector corpusFiles = new Vector();
    static String category;
    static String outputFileName;
    static int maxChars = -1;
    static Vector result = new Vector();
    static String summary = new String();

    /** Creates a new instance of AnnotationKWIC */
    public AnnotationKWIC() {
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ((args.length!=3) && (args.length!=4)){
            System.out.println("Usage: AnnotationKWIC corpus-file category output-file");
            System.out.println("or   : AnnotationKWIC corpus-file category output-file max-characters");            
            return;
        }
        corpusFileName = args[0];
        category = args[1];
        outputFileName = args[2];
        if (args.length==4){
            try{
                maxChars = new Integer(args[3]).intValue();
                if (maxChars<0) throw new NumberFormatException();
            } catch (java.lang.NumberFormatException nfe){
                System.out.println(args[3] + " ist keine positive ganze Zahl.");
                return;
            }
        }
        
        
        // read in the corpus file names        
        try{
            FileReader fr = new FileReader(corpusFileName);
            BufferedReader br = new BufferedReader(fr);
            String nextLine = new String();
            System.out.println("Lese Corpus-Datei");
            while ((nextLine = br.readLine()) != null){
                corpusFiles.addElement(nextLine);
            }
            br.close();
            System.out.println("Corpus-Datei gelesen.");               
        } catch (IOException ioe){
            System.out.println(ioe.getLocalizedMessage());
            return;
        }
        
        int countAll = 0;
        for (int pos=0; pos<corpusFiles.size(); pos++){
            String currentFileName = (String)(corpusFiles.elementAt(pos));
            int countAllInTrans=0;
            String transName = new String();
            try{
                System.out.println("Lese Transkription " + currentFileName);
                BasicTranscription t = new BasicTranscription(currentFileName);
                BasicBody body = t.getBody();
                Timeline timeline = body.getCommonTimeline();
                Speakertable speakertable = t.getHead().getSpeakertable();
                transName = t.getHead().getMetaInformation().getTranscriptionName();
                for (int tierNo=0; tierNo<body.getNumberOfTiers(); tierNo++){
                    Tier tier = body.getTierAt(tierNo);
                    if (tier.getType().equals("a") && tier.getCategory().equals(category)){                        
                        System.out.println("Spur " + tier.getDescription(speakertable) + " wird verarbeitet.");
                        Tier annotatedTier = null;
                        for (int tierNo2=0; tierNo2<body.getNumberOfTiers(); tierNo2++){
                            if ((body.getTierAt(tierNo2).getSpeaker() != null) && 
                                body.getTierAt(tierNo2).getSpeaker().equals(tier.getSpeaker()) &&
                                body.getTierAt(tierNo2).getType().equals("t")){
                                annotatedTier = body.getTierAt(tierNo2);
                            }
                        }
                        if (annotatedTier == null){
                            System.out.println("Keine zugehoerige Transkriptionsspur");
                            break;
                        }            
                        summary += Integer.toString(tier.getNumberOfEvents()) 
                                   + " Annotationen fuer Sprecher " 
                                   + speakertable.getSpeakerWithID(tier.getSpeaker()).getAbbreviation()
                                   + System.getProperty("line.separator");
                        countAllInTrans+=tier.getNumberOfEvents();
                        AbstractSegmentVector asv = annotatedTier.toSegmentVector(timeline);
                        for (int eventNo = 0; eventNo<tier.getNumberOfEvents(); eventNo++){
                            Event annotation = tier.getEventAt(eventNo);
                            //System.out.println(annotation.toXML());
                            TimedSegment ts = (TimedSegment)(asv.getParentSegment(timeline, annotation.getStart(), annotation.getEnd()));
                            Vector events = ts.getLeaves();
                            String before = new String();
                            String after = new String();
                            String self = new String();
                            for (int i=0; i< events.size(); i++){
                                TimedSegment ts2 = (TimedSegment)(events.elementAt(i));
                                if (timeline.lookupID(annotation.getStart()) > timeline.lookupID(ts2.getStart())){
                                    before += ts2.getDescription();
                                } else if (timeline.lookupID(annotation.getEnd()) <= timeline.lookupID(ts2.getStart())){
                                    after += ts2.getDescription();
                                } else{
                                    self +=ts2.getDescription();
                                }
                            }
                            String[] resultLine = { t.getHead().getMetaInformation().getTranscriptionName(),
                                                    speakertable.getSpeakerWithID(tier.getSpeaker()).getAbbreviation(),
                                                    annotation.getDescription(),
                                                    tier.getID(),
                                                    annotation.getStart(),
                                                    Integer.toString(timeline.lookupID(annotation.getStart())),
                                                    before,
                                                    self,
                                                    after };                                                        
                            result.addElement(resultLine);
                        }
                    }
                }
            } catch (JexmaraldaException je){
                System.out.println(je.getMessage());
            } catch (SAXException se){
                System.out.println(se.getMessage());
            }
            summary+= Integer.toString(countAllInTrans)   
                      + " Annotationen in Transkription "
                      + transName                                   
                      + System.getProperty("line.separator"); 
            countAll+=countAllInTrans;
            summary+= "-------------------------------------------" + System.getProperty("line.separator"); 

        }
        summary+= Integer.toString(countAll)   
                  + " Annotationen insgesamt "
                  + System.getProperty("line.separator"); 
        
        // OUTPUT
        StringBuffer sb = new StringBuffer();
        sb.append("<HTML>\n<HEAD>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        sb.append("<BODY>");
        sb.append("<TABLE border=\"1\" rules=\"groups\">");
        // header
        sb.append("<THEAD>");
        sb.append("<TR>");
        sb.append("<TH>" + "<SMALL>" + "Transkription" +  "</SMALL>" + "</TH>");
        sb.append("<TH>" + "<SMALL>" + "Sprecher (TLI)" + "</SMALL>" + "</TH>");
        sb.append("<TH>" + category + "</TH>");
        sb.append("<TH>" + "Vorher" + "</TH>");
        sb.append("<TH>" + "" + "</TH>");
        sb.append("<TH>" + "Nachher" + "</TH>");
        sb.append("</TR>");        
        sb.append("</THEAD>");
        for (int pos=0; pos<result.size(); pos++){
            String[] line = (String[])(result.elementAt(pos));
            sb.append("<TR>");
            sb.append("<TD nowrap>" + "<SMALL>" + line[0] + "</SMALL>" + "</TD>");  // transcription name
            sb.append("<TD nowrap>" + "<SMALL>" + line[1] + " [" + line[5] + "]"  + "</SMALL>" +"</TD>");  // speaker abbrev [timeline item no]
            sb.append("<TD nowrap>" + "<B>" + "<SMALL>" + line[2] + "</SMALL>" + "</B>" + "</TD>");  // anno desc
            String before = line[6];
            if ((maxChars>=0) && (before.length()>maxChars)){
                before = before.substring(before.length()-maxChars);
            }                
            String after = line[8];
            if ((maxChars>=0) && (after.length()>maxChars)){
                after = after.substring(0,maxChars);
            }
            
            sb.append("<TD nowrap align=\"right\">" + before + "</TD>");  // context before
            sb.append("<TD nowrap align=\"center\">" + "<B>" + line[7] + "</B>" + "</TD>");  // annotated
            sb.append("<TD nowrap align=\"left\">" + after + "</TD>");  // context after
            sb.append("</TR>");            
        }
        sb.append("</TABLE>");
        sb.append("</BODY>");
        sb.append("</HTML>");        
        try{
            System.out.println("Schreibe Ausgabe...");
            FileOutputStream fos = new FileOutputStream(new File(outputFileName));
            fos.write(sb.toString().getBytes("UTF-8"));
            fos.close();
            System.out.println("Ausgabe geschrieben.");            
        } catch (IOException ioe){
            System.out.println(ioe.getLocalizedMessage());
        }
        System.out.println("===============");
        System.out.println("Zusammenfassung");
        System.out.println("===============");
        System.out.println(summary);
    }
    
}
