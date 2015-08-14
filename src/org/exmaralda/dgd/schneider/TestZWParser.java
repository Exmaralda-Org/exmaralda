/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.folker.data.ZWParser;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class TestZWParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestZWParser().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(TestZWParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestZWParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        File inputFile = new File("Y:\\thomas\\KN2FLK\\6\\KOA34TRA.flk");
        ZWParser parser = new ZWParser();
        System.out.println("Reading " + inputFile.getName());                        
        Document unparsedDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
        parser.parseDocument(unparsedDocument, 2);            
        FileIO.writeDocumentToLocalFile("Y:\\thomas\\KN2FLK\\KOA34TRA_TEST.xml", unparsedDocument);
        
    }
}
