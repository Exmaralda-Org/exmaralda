/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.folker.data.PFParser;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class TestPFParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Document d = FileIO.readDocumentFromLocalFile(new File("Y:\\thomas\\PF2FLK\\5\\PF202TRA.flk"));
            PFParser parser = new PFParser();
            parser.parseDocument(d, 2);
            FileIO.writeDocumentToLocalFile(new File ("Y:\\thomas\\PF2FLK\\6\\PF202TRA_parsed.xml"), d);
        } catch (JDOMException ex) {
            Logger.getLogger(TestPFParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestPFParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
