/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices.swing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas.schmidt
 */
public class WebLichtChainComboBoxModel extends javax.swing.DefaultComboBoxModel<ChainDefinition> {

    String BUILT_IN_DEFINTIONS = "/org/exmaralda/webservices/weblicht-chains/CHAIN-DEFINITIONS.xml";
    List<ChainDefinition> chainDefinitions = new ArrayList<>();
    
    public WebLichtChainComboBoxModel(){
        try {
            Document builtInDefinitions = new IOUtilities().readDocumentFromResource(BUILT_IN_DEFINTIONS);
            for (Object o : builtInDefinitions.getRootElement().getChildren()){
                Element e = (Element)o;
                ChainDefinition chainDefinition = new ChainDefinition(e);
                chainDefinitions.add(chainDefinition);
            }
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(WebLichtChainComboBoxModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    
    @Override
    public int getSize() {
        return chainDefinitions.size();
    }

    @Override
    public ChainDefinition getElementAt(int index) {
        return chainDefinitions.get(index);
    }

    
}
