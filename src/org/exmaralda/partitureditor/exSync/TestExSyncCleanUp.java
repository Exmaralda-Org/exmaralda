/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.exSync;

import java.io.IOException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class TestExSyncCleanUp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String INPUT = "S:\\TP-Z2\\DATEN\\K1\\Transkripte\\AQUA GLAS\\AQUA GLAS 7.exb";
            String OUTPUT = "S:\\TP-Z2\\DATEN\\K1\\Transkripte\\AQUA GLAS\\AQUA GLAS 7_MOD.exb";
            BasicTranscription bt = new BasicTranscription(INPUT);
            ExSyncCleanup esc = new ExSyncCleanup(bt);
            int i1 = esc.replaceNonBreakingSpace();
            int i2 = esc.replaceEllipsisDots();
            int i4 = esc.moveLigature();
            int i3 = esc.moveIsolatedPunctuation();
            int i5 = esc.moveInitialColons();
            int i6 = esc.moveInitialSpaces();
            int i7 = esc.replaceIncomprehensible();
            int i8 = esc.splitAtUtteranceEndSymbols();
            int i9 = esc.normalizeWhitespace();

            System.out.println(i1 + " non-breaking spaces");
            System.out.println(i2 + " ellipsis dots");
            System.out.println(i3 + " isolated punctuation");
            System.out.println(i4 + " ligatures");
            System.out.println(i5 + " initial colons");
            System.out.println(i6 + " initial spaces");
            System.out.println(i7 + " incomprehensible");
            System.out.println(i8 + " utterance end splits");
            System.out.println(i9 + " whitespace normalizations");
            bt.writeXMLToFile(OUTPUT, "none");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }

    }

}
