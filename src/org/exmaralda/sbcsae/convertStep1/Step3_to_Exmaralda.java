/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class Step3_to_Exmaralda extends AbstractStylesheetTransformer{

    public Step3_to_Exmaralda() {
    }

    @Override
    public String getStylesheetPath() {
        return "T:\\TP-Z2\\DATEN\\SBCSAE\\Step3_to_Exmaralda.xsl";
    }

    @Override
    public String getSourceDirectoryPath() {
        return "T:\\TP-Z2\\DATEN\\SBCSAE\\3";
    }

    @Override
    public String getTargetDirectoryPath() {
        return "T:\\TP-Z2\\DATEN\\SBCSAE\\4";
    }
    
    public static void main(String[] args){
        try {
            Step3_to_Exmaralda x = new Step3_to_Exmaralda();
            x.transform(true);
        } catch (SAXException ex) {
            Logger.getLogger(Step3_to_Exmaralda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Step3_to_Exmaralda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Step3_to_Exmaralda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(Step3_to_Exmaralda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Step3_to_Exmaralda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(Step3_to_Exmaralda.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

}
