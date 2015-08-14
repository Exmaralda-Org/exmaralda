/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class EXB2Praat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=2){
            System.out.println("Usage: EXB2Praat input.exb output.textGrid");
            System.exit(1);
        }
        try {
            new EXB2Praat().doit(args);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
    }

    private void doit(String[] args) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException {
        File f = new File(args[0]);
        BasicTranscription bt = new BasicTranscription(f.getAbsolutePath());
        System.out.println("--- " + args[0] + " read.");
        PraatConverter pc = new PraatConverter();
        pc.writePraatToFile(bt, args[1]);
        System.out.println("--- " + args[1] + " written.");
        System.out.println("--- Done ---");
    }

}
