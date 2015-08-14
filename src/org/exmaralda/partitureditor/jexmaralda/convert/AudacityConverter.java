/*
 * AIFConverter.java
 *
 * Created on 2. Juli 2003, 13:52
 */

package org.exmaralda.partitureditor.jexmaralda.convert;



import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

// For write operation

/**
 *
 * @author  thomas
 */
public class AudacityConverter {

    public static final int ALL_TIERS = 0;
    public static final int SELECTED_TIERS = 1;
    public static final int TIMELINE = 2;
    
    public static final int DUMMY_PAUSE_DESCRIPTOR = 100;
    public static final int GAT_PAUSE_DESCRIPTOR = 101;
    public static final int HIAT_PAUSE_DESCRIPTOR = 102;



    
    
    
    /** Creates a new instance of AIFConverter */
    public AudacityConverter() {
    }
    
    
    public static String BasicTranscriptionToAudacity(BasicTranscription t, int startRow, int endRow) {
        t.getBody().getCommonTimeline().completeTimes();
        StringBuilder result = new StringBuilder();
        for (int pos=startRow; pos<=endRow; pos++){
            Tier tier = t.getBody().getTierAt(pos);
            result.append(tierToLabels(t, tier));
        }
        return result.toString();
    }
    
    public static String TimelineToAudacity(BasicTranscription t){
        t.getBody().getCommonTimeline().completeTimes();
        StringBuilder result = new StringBuilder();
        for (int pos=0; pos<t.getBody().getCommonTimeline().getNumberOfTimelineItems(); pos++){
            TimelineItem tli = t.getBody().getCommonTimeline().getTimelineItemAt(pos);
            result.append(Double.toString(tli.getTime())).append("\t#").append(Integer.toString(pos)).append("\n");
        }
        return result.toString();        
    }


    public static String tierToLabels(BasicTranscription t, Tier tier){
        StringBuilder result = new StringBuilder();
        for (int i=0; i<tier.getNumberOfEvents(); i++){
            try{
                Event e = tier.getEventAt(i);
                double start = t.getBody().getCommonTimeline().getTimelineItemWithID(e.getStart()).getTime();
                double end = t.getBody().getCommonTimeline().getTimelineItemWithID(e.getEnd()).getTime();
                result.append(Double.toString(start)).append("\t");
                result.append(Double.toString(end)).append("\t");
                result.append(e.getDescription()).append("\n");
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    
    /** writes an AIF version of this BasicTranscription to a file */
    public static void writeAudacityToFile(BasicTranscription t, String filename) throws IOException {
        writeAudacityToFile(t, filename, 0, t.getBody().getNumberOfTiers()-1);
    }

    public static void writeAudacityToFile(BasicTranscription trans, String filename, int selectionStartRow, int selectionEndRow) throws IOException {
        if ((selectionStartRow<0) || (selectionEndRow<0) || 
                (selectionStartRow>=trans.getBody().getNumberOfTiers()) || selectionEndRow>=trans.getBody().getNumberOfTiers()){
            throw new IOException("Tier range not permissible.");            
        }
        try {
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(filename));
            String audacityString = BasicTranscriptionToAudacity(trans, selectionStartRow, selectionEndRow);
            fos.write(audacityString.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
        } catch (FileNotFoundException ex) {
            throw new IOException(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public static void writeTimelineToFile(BasicTranscription trans, String filename) throws IOException {
        try {
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(filename));
            String audacityString = TimelineToAudacity(trans);
            fos.write(audacityString.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
        } catch (FileNotFoundException ex) {
            throw new IOException(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    ArrayList<String> lines = new ArrayList<String>();
    
    public void readText(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            lines.add(nextLine);
        }
        br.close();
    }

    public void readText(File file, String encoding) throws FileNotFoundException, IOException, UnsupportedEncodingException{
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader br = new BufferedReader(isr);
        String nextLine = new String();
        System.out.println("Started reading document " + file.getAbsolutePath());
        int lineCount=0;
        while ((nextLine = br.readLine()) != null){
            // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
            if (lineCount==0 && encoding.startsWith("UTF") && nextLine.charAt(0)=='\uFEFF'){
                nextLine = nextLine.substring(1);
            }
            lines.add(nextLine);
            lineCount++;
        }
        br.close();
        System.out.println("Document read.");                
    }
    
    public BasicTranscription readAudacityFromFile(File audacityFile) throws IOException, JexmaraldaException{
        return readAudacityFromFile(audacityFile, DUMMY_PAUSE_DESCRIPTOR);
    }
       
    
    public BasicTranscription readAudacityFromFile(File audacityFile, int pauseDescriptorType) throws IOException, JexmaraldaException{
        readText(audacityFile, "UTF-8");
        BasicTranscription bt = new BasicTranscription();
        Timeline tl = new Timeline();
        Tier tier1 = new Tier("TIE0", null, "pause", "t", "[pause]");
        Tier tier2 = new Tier("TIE1", null, "x", "t", "[x]");
        bt.getBody().addTier(tier1);
        bt.getBody().addTier(tier2);
        String lastID = null;
        for (String line : lines){
            String[] fields = line.split("\\t");
            String thisID = fields[0].replaceAll("[\\.\\,]", "_");
            double startTime = Double.parseDouble(fields[0].replaceAll("\\,", "."));
            double endTime = Double.parseDouble(fields[1].replaceAll("\\,", "."));
            if (lastID!=null){
                Event event1 = new Event();
                event1.setStart(lastID);
                event1.setEnd(thisID);
                event1.setDescription("***");
                tier1.addEvent(event1);
            }
            TimelineItem tli1 = new TimelineItem();
            tli1.setID(thisID);
            tli1.setTime(startTime);
            TimelineItem tli2 = new TimelineItem();
            lastID = fields[1].replaceAll("[\\.\\,]", "_");
            tli2.setID(lastID);
            tli2.setTime(endTime);
            if ((!tl.containsTimelineItemWithID(tli1.getID()))){
                tl.addTimelineItem(tli1);
            }
            if ((!tl.containsTimelineItemWithID(tli2.getID()))){
                tl.addTimelineItem(tli2);
            }

            Event event2 = new Event();
            event2.setStart(thisID);
            event2.setEnd(lastID);            
            if (fields.length>2){
                event2.setDescription(fields[2]);
            } else {
                event2.setDescription("");
            }
            tier2.addEvent(event2);
        }
        switch (pauseDescriptorType){
            case DUMMY_PAUSE_DESCRIPTOR :
                // do nothing
                break;
            case GAT_PAUSE_DESCRIPTOR :
                for (int i=0; i<tier1.getNumberOfEvents(); i++){
                    Event event = tier1.getEventAt(i);
                    double duration = tl.getTimelineItemWithID(event.getEnd()).getTime()
                            - tl.getTimelineItemWithID(event.getStart()).getTime();
                    DecimalFormat df = new DecimalFormat( "##0.00" );
                    event.setDescription("(" + df.format(duration).replaceAll("\\,", ".") + ")");
                }
                break;
            case HIAT_PAUSE_DESCRIPTOR :
                for (int i=0; i<tier1.getNumberOfEvents(); i++){
                    Event event = tier1.getEventAt(i);
                    double duration = tl.getTimelineItemWithID(event.getEnd()).getTime()
                            - tl.getTimelineItemWithID(event.getStart()).getTime();
                    DecimalFormat df2 = new DecimalFormat( "##0.00" );
                    event.setDescription("((" + df2.format(duration) + "s))");
                }
                break;
        }
        bt.getBody().setCommonTimeline(tl);
        bt.normalize();
        return bt;
        
    }


    
}
