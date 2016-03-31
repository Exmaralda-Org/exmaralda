/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class TransanaConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TransanaConverter().readTransanaFromTextFile(new File("C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\PILOT\\ZF2-D-EK-150522-P1-V1-001328-F2A2.txt"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TransanaConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TransanaConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String XSL_1 = "/org/exmaralda/partitureditor/jexmaralda/xsl/Transana2SimplifiedXML.xsl";
    public static String XSL_2 = "/org/exmaralda/partitureditor/jexmaralda/xsl/TransanaSimplifiedXML2EXB.xsl";
    
    public BasicTranscription readTransanaFromXMLFile(File inputFile) throws SAXException, ParserConfigurationException, IOException, TransformerException, JexmaraldaException {
        StylesheetFactory ssf = new StylesheetFactory(true);
        String simplifiedXML = ssf.applyInternalStylesheetToExternalXMLFile(XSL_1, inputFile.getAbsolutePath());
        String exbXML = ssf.applyInternalStylesheetToString(XSL_2, simplifiedXML);
        BasicTranscription bt = new BasicTranscription();
        bt.BasicTranscriptionFromString(exbXML);
        return bt;
    }
    
    public BasicTranscription readTransanaFromTextFile(File inputFile) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        ArrayList<String> allLines = new ArrayList<String>();
        FileInputStream fis = new FileInputStream(inputFile);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String nextLine = "";
        while ((nextLine = br.readLine()) != null){
            allLines.add(nextLine);
            System.out.println(nextLine);
        }
        br.close();
        
        ArrayList<ParsedLine> allParsedLines = new ArrayList<ParsedLine>();
        for (String line : allLines){
            ParsedLine parsedLine = new ParsedLine(line);
            allParsedLines.add(parsedLine);
        }
        
        // TODO...
        return null;
    }

    private static class ParsedLine {

        double startTime;
        double endTime;
        
        // ¤ (0:13:28.2)Leh [Lehrer teilt die Arbeitsblätter aus und geht weiter zu den Dokumenten] Jetzt wollen wir mal überprüfen, wer Recht hat. Oder was stimmt.
        // ¤<812073> (0:13:32.1) Hal [mit Hakan Schnick- Schnack- Schnuck spielend] ... [lachend] Drei Null. 
        public ParsedLine(String unparsedLine) {
            // startTime
            if (unparsedLine.matches("^\\u00A4<\\d+> .*")){
                int i1=unparsedLine.indexOf("<");
                int i2=unparsedLine.indexOf(">");
                String timeString = unparsedLine.substring(i1+1,i2);
                String secondsString = timeString.substring(0, timeString.length()-3) + "." + timeString.substring(timeString.length()-3);
                startTime = Double.parseDouble(secondsString);
            } else if (unparsedLine.matches("^ ?\\u00A4 \\(\\d:\\d\\d:\\d\\d\\.\\d\\).*")){
                int i1=unparsedLine.indexOf("(");
                int i2=unparsedLine.indexOf(")");
                String timeString = unparsedLine.substring(i1+1,i2);
                startTime = Integer.parseInt(timeString.substring(0,1))*3600
                        + Integer.parseInt(timeString.substring(2, 4))*60
                        + Double.parseDouble(timeString.substring(5));
            }
            System.out.println(unparsedLine + "==>" + startTime);
        }
        
    }
    
}
