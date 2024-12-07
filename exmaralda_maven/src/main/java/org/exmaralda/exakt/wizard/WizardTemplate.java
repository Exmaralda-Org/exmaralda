/*
 * WizardTemplate.java
 *
 * Created on 20. Februar 2008, 14:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.wizard;

import org.exmaralda.exakt.wizard.newtranscriptionwizard.CorpusPanel;
import javax.swing.*;

/**
 *
 * @author thomas
 */
public class WizardTemplate extends AbstractWizardDialog{
    
    
    /** Creates a new instance of WizardTemplate */
    public WizardTemplate(java.awt.Frame parent, boolean modal) {        
        //super(parent,modal);
        initPanels();
        initialise();        
    }

    void initPanels(){
        stepPanels = new JPanel[getNumberOfSteps()];
        stepPanels[0] = new CorpusPanel();
        stepPanels[1] = new TranscriptionsPanel();
        for (int pos=2; pos<5; pos++){
            stepPanels[pos] = new JPanel();
        }
    }
    
    public String getStep(int n) {
        switch(n){
            case 0  : return "1. Corpus name and file";
            case 1  : return "2. Transcriptions";
            case 2  : return "3. Segmentation";
            case 3  : return "4. Meta-Data";
            case 4  : return "5. Something";
            default : return "";
        }
    }


    public int getNumberOfSteps() {
        return 5;
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WizardTemplate(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }

    @Override
    public void loadSettings() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void storeSettings() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
