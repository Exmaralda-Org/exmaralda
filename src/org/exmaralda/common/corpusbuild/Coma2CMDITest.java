/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.common.corpusbuild;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author Z2
 */
public class Coma2CMDITest {

    public static String CORPUS_FILENAME = "S:\\TP-Z2\\DATEN\\MAPTASK\\0.3\\hamatac.coma";
    public static String CMDI_FILENAME = "S:\\TP-Z2\\Z2-Mitarbeiter\\Hanna\\metadata\\imdi_hamatac.cmdi";
    public static String XSLT_FILENAME = "S:\\TP-Z2\\Z2-Mitarbeiter\\Hanna\\metadata\\coma2cmdi_imdi.xsl";

    public static void main(String[] args) {
        try {

            Coma2CMDI transformer = new Coma2CMDI(CORPUS_FILENAME, CMDI_FILENAME, XSLT_FILENAME);
            transformer.transform();
            System.exit(0);

        } catch (JDOMException ex) {
            Logger.getLogger(AddRecordingDurationsInComa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddRecordingDurationsInComa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
