/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.AmberscriptJSONConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.WhisperJSONConverter;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */

// issue #357
public class TestASRJSONConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException {
        new TestASRJSONConverter().doit();
    }

    private void doit() throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException {
        /*File[] jsonFiles = new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_01_13_ISSUE_119").listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("_WHISPER.json");
            }            
        });*/
        File[] jsonFiles = {new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_04_23_ISSUE_357\\tagesschau-tagesschau_06_00_Uhr__04_07_2024-1444422770.json")};
        for (File jsonFile : jsonFiles){
            BasicTranscription bt = WhisperJSONConverter.readWhisperJSON(jsonFile);
            String outFilename = jsonFile.getName().replace(".json", ".exb");
            bt.writeXMLToFile(new File(jsonFile.getParent(), outFilename).getAbsolutePath(), "none");
            //System.out.println(bt.toXML());            
        }
        
        System.exit(0);

        jsonFiles = new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_01_13_ISSUE_119").listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("_AMBERSCRIPT.json");
            }            
        });
        for (File jsonFile : jsonFiles){
            BasicTranscription bt = AmberscriptJSONConverter.readAmberscriptJSON(jsonFile);
            String outFilename = jsonFile.getName().replace(".json", ".exb");
            bt.writeXMLToFile(new File(jsonFile.getParent(), outFilename).getAbsolutePath(), "none");
            //System.out.println(bt.toXML());            
        }

    }
    
}
