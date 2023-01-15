/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas.schmidt
 */
public class TestSubtitleConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException {
        new TestSubtitleConverter().doit();
    }

    private void doit() throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException {
        File[] vttFiles = new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_01_13_ISSUE_119").listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".vtt");
            }            
        });
        for (File vttFile : vttFiles){
            BasicTranscription bt = SubtitleConverter.readVTT(vttFile);
            String outFilename = vttFile.getName().replace(".vtt", ".exb");
            bt.writeXMLToFile(new File(vttFile.getParent(), outFilename).getAbsolutePath(), "none");
            //System.out.println(bt.toXML());            
        }
        
        
    }
    
}
