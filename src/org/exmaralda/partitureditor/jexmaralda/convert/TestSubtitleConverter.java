/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
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
        BasicTranscription bt = SubtitleConverter.readVTT(new File("D:\\Dropbox\\BASEL\\LEHRE_FJS_2022\\SEMINAR_MAP_TASKS--edited.vtt"));
        System.out.println(bt.toXML());
    }
    
}
