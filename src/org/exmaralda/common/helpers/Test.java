/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.helpers;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class Test {

    static String COMA_IN = "C:\\Dokumente und Einstellungen\\thomas\\Desktop\\EXMARaLDA_DemoKorpus\\EXMARaLDA_DemoKorpus.coma";
    static String COMA_OUT = "C:\\Dokumente und Einstellungen\\thomas\\Desktop\\EXMARaLDA_DemoKorpus\\EXMARaLDA_DemoKorpus_Plus_Oettinger.coma";
    static String TRANS_IN = "C:\\Dokumente und Einstellungen\\thomas\\Desktop\\EXMARaLDA_DemoKorpus\\Oettinger\\Oettinger_Quatsch.exb";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new Test().doit();
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException {
        System.out.println(Internationalizer.getString("Search string:   "));
        /*Document comadoc = IOUtilities.readDocumentFromLocalFile(COMA_IN);
        BasicTranscription2COMA.importBasicTranscription(new File(TRANS_IN), comadoc, null);
        IOUtilities.writeDocumentToLocalFile(COMA_OUT, comadoc);
        System.out.println("Done!");*/
    }


}
