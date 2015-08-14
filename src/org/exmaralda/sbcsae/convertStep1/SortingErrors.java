/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class SortingErrors extends AbstractXPathErrorFinder{

    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            SortingErrors x = new SortingErrors();
            x.process();
            x.writeErrorDocument("T:\\TP-Z2\\DATEN\\SBCSAE\\SortingErrors.xml");
        } catch (JDOMException ex) {
            Logger.getLogger(SortingErrors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SortingErrors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SortingErrors.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public XPath getXPath() {
        try {
            XPath r = XPath.newInstance("//intonation-unit[(@startTime + 0.0) > (following-sibling::*[1]/@startTime + 0.0)]");
            return r;
        } catch (JDOMException ex) {
            Logger.getLogger(SortingErrors.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getErrorMessage() {
        return "IU is not at the correct position";
    }

}
