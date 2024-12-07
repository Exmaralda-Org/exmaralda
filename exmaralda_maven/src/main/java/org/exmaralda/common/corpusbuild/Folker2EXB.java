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
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class Folker2EXB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=3){
            System.out.println("Usage: Folker2EXB input.flk output.exb smooth-threshhold");
            System.exit(1);
        }
        try {
            new Folker2EXB().doit(args);
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
        EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(f);
        BasicTranscription bt = org.exmaralda.folker.io.EventListTranscriptionConverter.exportBasicTranscription(elt);
        Double d = Double.parseDouble(args[2]);
        if (d.doubleValue()>0.0){
            bt.getBody().smoothTimeline(d);
        }
        bt.writeXMLToFile(args[1], "none");
    }

}
