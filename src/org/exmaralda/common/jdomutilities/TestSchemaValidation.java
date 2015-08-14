/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.common.jdomutilities;

import java.io.File;
import java.io.IOException;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class TestSchemaValidation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            IOUtilities.schemaValidateLocalFile(new File("S:\\TP-Z2\\IDS\\GAT\\Demokorpus\\HART_ABER_FAIR\\HART_ABER_FAIR_Not_Valid.flk"), 
                    "S:\\TP-Z2\\IDS\\GAT\\Datenmodell\\Folker_Schema.xsd");
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
