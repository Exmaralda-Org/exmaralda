/*
 * TRN2XML.java
 *
 * Created on 7. Juni 2007, 09:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import org.jdom.*;
import org.jdom.xpath.*;
import java.io.*;
import org.exmaralda.sbcsae.utilities.FileIO;
import java.util.regex.*;
import java.util.*;
/**
 *
 * @author thomas
 */
public class TRN2XML {
    
    static String BASE_DIRECTORY = "T:\\TP-Z2\\DATEN\\SBCSAE\\";
    
    String lastSpeaker;
    int lineCount;
    
    /** Creates a new instance of TRN2XML */
    public TRN2XML() {
    }
    
    public String writeTRN(Document d) throws JDOMException{
        StringBuffer sb = new StringBuffer();
        XPath iu = XPath.newInstance("//intonation-unit");
        List l = iu.selectNodes(d);
        Element lastLine = null;
        for (Object o : l){
            Element e = (Element)o;
            sb.append(e.getAttributeValue("startTime"));
            sb.append(" ");
            sb.append(e.getAttributeValue("endTime"));
            sb.append("\t");
            String thisSpeaker = e.getAttributeValue("speaker");
            if ((lastLine!=null) && (lastLine.getAttributeValue("speaker").equals(thisSpeaker))){
                sb.append(thisSpeaker.replaceAll(".", " "));
            } else {
                sb.append(thisSpeaker + ": ");
            }
            sb.append("\t");
            sb.append(e.getText());
            sb.append("\n");
            lastLine = e;
        }
        
        return sb.toString();
    }
    
    public Document readTRN(File trnFile) throws FileNotFoundException, IOException {
       Document resultDocument = new Document();
       Element rootElement = new Element("DT1-Transcription");
       resultDocument.setRootElement(rootElement);
       FileReader fr =  new FileReader(trnFile);
       BufferedReader myInput = new BufferedReader(fr);
       String nextLine = new String();
       System.out.println("Started reading document " + trnFile.getAbsoluteFile() + "...");
       lastSpeaker = "";
       lineCount = 0;
       // read in the document line for line
       while ((nextLine = myInput.readLine()) != null) {
           lineCount++;
           nextLine = nextLine.replaceAll("\\u0000","");
           Element intonationUnitElement = parseLine(nextLine);
           if (intonationUnitElement!=null){
            rootElement.addContent(intonationUnitElement);
           }
       }
       myInput.close();
       fr.close();
       
       return resultDocument; 
    }
    
    public Document readTRN(String trans) throws IOException {
       Document resultDocument = new Document();
       Element rootElement = new Element("DT1-Transcription");
       resultDocument.setRootElement(rootElement);
       StringReader fr =  new StringReader(trans);
       BufferedReader myInput = new BufferedReader(fr);
       String nextLine = new String();
       lastSpeaker = "";
       lineCount = 0;
       // read in the document line for line
       while ((nextLine = myInput.readLine()) != null) {
           lineCount++;
           nextLine = nextLine.replaceAll("\\u0000","");
           Element intonationUnitElement = parseLine(nextLine);
           if (intonationUnitElement!=null){
            rootElement.addContent(intonationUnitElement);
           }
       }
       myInput.close();
       fr.close();
       
       return resultDocument; 
    }

    public Element parseLine(String nextLine){
       if (nextLine.startsWith("000000000") || nextLine.startsWith("$")){
           Element commentElement = new Element("comment");
           commentElement.setText(nextLine);
           commentElement.setAttribute("line",Integer.toString(lineCount));
           return commentElement;
       }

       if (nextLine.equals("\u001A") || nextLine.trim().length()==0) return null;


       String numberRegex = "\\d+\\.\\d+";
       Pattern numberPattern = Pattern.compile(numberRegex);
       Matcher numberMatcher = numberPattern.matcher(nextLine);
       if (!numberMatcher.find()){
           System.out.println("No first match found in line " + lineCount);
           System.exit(1);
       }
       String startTime = numberMatcher.group();
       if (!numberMatcher.find()){
           System.out.println("No second match found in line " + lineCount);
           System.exit(1);
       }
       String endTime = numberMatcher.group();
       int index1 = numberMatcher.end();

       String rest = nextLine.substring(index1+1);
       int index2 = rest.indexOf(":");
       String speaker = lastSpeaker;
       if (index2>=0){
           speaker = rest.substring(0, index2).trim();
           lastSpeaker = speaker;
           rest = rest.substring(index2+1);
       }
       String transcribedText = rest.trim();

       Element intonationUnitElement = new Element("intonation-unit");
       intonationUnitElement.setAttribute("startTime", startTime);
       intonationUnitElement.setAttribute("endTime", endTime);
       intonationUnitElement.setAttribute("speaker", speaker);
       intonationUnitElement.setAttribute("line",Integer.toString(lineCount));          
       intonationUnitElement.setText(transcribedText); 
       
       return intonationUnitElement;
    }
    
    public static void main(String[] args){
        for (int i=1; i<=60; i++){
            String filename = "SBC0";
            if (i<10) filename+="0";
            filename+=Integer.toString(i);
            String inputPath = BASE_DIRECTORY + "\\0.2\\" + filename + ".trn";
            String outputPath = BASE_DIRECTORY + "\\1\\" + filename + ".xml";
            File f = new File(inputPath);
            try {
                Document d = new TRN2XML().readTRN(f);
                FileIO.assignStylesheet("Step1_To_HTML.xsl", d);
                FileIO.writeDocumentToLocalFile(outputPath,d);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }        
    }
    
}
