/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.interlinearText.HTMLParameters;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestOutputter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestOutputter().doit();
        } catch (SAXException | JexmaraldaException | IOException | ParserConfigurationException | TransformerException | JDOMException ex) {
            Logger.getLogger(TestOutputter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    String IN = "C:\\Users\\bernd\\Dropbox\\EXMARaLDA-Demokorpus\\RudiVoellerWutausbruch\\RudiVoellerWutausbruch.exb";
    String OUT = "C:\\Users\\bernd\\OneDrive\\Desktop\\out.html";



    private void doit() throws SAXException, JexmaraldaException, IOException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JDOMException {
        BasicTranscription bt = new BasicTranscription(IN);
        Outputter outputter = new Outputter(bt);
        File outputFile = new File(OUT);
        Map<String, Object> parameters = new HashMap<>();
        HTMLParameters htmlParameters = new HTMLParameters();
        htmlParameters.setWidth(600);
        parameters.put("HTMLParameters", htmlParameters);
        outputter.output(outputFile, Outputter.OUTPUT_VARIANT.HTML_PARTITUR_WITH_AUDIO, parameters);
    }
    
}
