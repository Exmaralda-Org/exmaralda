/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas.schmidt
 */
public class EXMARaLDATransformationScenarios {
    
    public static String BUILT_IN_SCENARIOS = "/org/exmaralda/partitureditor/partiture/transcriptionActions/TransformationScenarios.xml";

    public static List readScenarios(ExmaraldaApplication app) throws IOException {
        List scenarios = new ArrayList();
        // 1. Load built-in scenarios
        try {
            Document builtInScenarios = new IOUtilities().readDocumentFromResource(BUILT_IN_SCENARIOS);
            scenarios.addAll(builtInScenarios.getRootElement().getChildren());
        } catch (IOException | JDOMException ex) {
            Logger.getLogger(EXMARaLDATransformationScenarios.class.getName()).log(Level.SEVERE, null, ex);     
            //JOptionPane.showMessageDialog(this, "Could not read built-in scenarios: \n" + ex.getLocalizedMessage());
            throw new IOException(ex);
        }

        // 2. Load user scenarios
        if (app!=null){
            java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(app.getPreferencesNode());
            String path = settings.get("TRANSFORMATION_SCENARIOS_FILE", "");
            if (path.length()>0){
                try {
                    Document userScenarios = IOUtilities.readDocumentFromLocalFile(path);
                    scenarios.addAll(userScenarios.getRootElement().getChildren());
                } catch (IOException | JDOMException ex) {
                    //JOptionPane.showMessageDialog(this, "Could not read user scenarios: \n" + ex.getLocalizedMessage());
                    settings.put("TRANSFORMATION_SCENARIOS_FILE", "");
                    Logger.getLogger(EXMARaLDATransformationScenarios.class.getName()).log(Level.SEVERE, null, ex); 
                    throw new IOException(ex);
                }
            }
        }
        return scenarios;
        
    }

    public static ComboBoxModel getComboBoxModel(List scenarios) {
        String[] scenarioNames = new String[scenarios.size()];
        int count=0;
        for (Object o : scenarios){
            Element scenario = (Element)o;
            scenarioNames[count]=scenario.getChildText("name");
            count++;
        }
        return new DefaultComboBoxModel(scenarioNames);        
    }
    
}
