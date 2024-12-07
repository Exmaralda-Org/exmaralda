/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author Thomas_Schmidt
 */
public class TEST_GLUE {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TEST_GLUE().doit();
        } catch (SAXException ex) {
            Logger.getLogger(TEST_GLUE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(TEST_GLUE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TEST_GLUE.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws SAXException, JexmaraldaException, IOException {
        File[] allFilesToBeGlued = new File("C:\\Users\\Thomas_Schmidt\\Desktop\\DEBUG\\GLUE_JAN_DH\\TEST").listFiles();
        BasicTranscription firstTrans = new BasicTranscription(allFilesToBeGlued[0].toString());
        for (int i = 1; i < allFilesToBeGlued.length; i++) {
            System.out.println("File to be glued: " + allFilesToBeGlued[i]);
            BasicTranscription glueTrans = new BasicTranscription(allFilesToBeGlued[i].toString());
            Vector myTierMappings = new Vector();
            //myTierMappings = BasicTranscription.makeTierIDMappings(firstTrans glueTrans);
            myTierMappings = firstTrans.makeTierIDMappings(glueTrans);
            //firstTrans.merge(glueTrans);
            //firstTrans.glue(glueTrans myTierMappings);
            String[][] myTierMappingsAsArray = new String[myTierMappings.size()][2];
            for (int pos = 0; pos < myTierMappings.size(); pos++) {
                myTierMappingsAsArray[pos] = (String[]) (myTierMappings.elementAt(pos));
            }
            firstTrans.glue(glueTrans, myTierMappingsAsArray, true);
        }
        File outFile = new File("C:\\Users\\Thomas_Schmidt\\Desktop\\DEBUG\\GLUE_JAN_DH\\TEST_OUT.exb");
        firstTrans.writeXMLToFile(outFile.getAbsolutePath(), "none");
    }
    
}
