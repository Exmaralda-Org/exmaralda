/*
 * WizardTemplate.java
 *
 * Created on 20. Februar 2008, 14:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.wizard.folkercorpuswizard;

import java.awt.Cursor;
import org.exmaralda.exakt.wizard.*;
import java.io.File;
import java.io.FileFilter;
import java.util.Vector;
import javax.swing.*;

/**
 *
 * @author thomas
 */
public class EAFCorpusWizard extends AbstractWizardDialog{
    
    String CORPUS_PATH_EXPLANATION = "<html><p style='font-family:sans-serif;font-size:11pt;'><b>Corpus File and Directory</b><br/>Specify the <span style='color:blue; font-weight:bold'>corpus file</span> to be "
            + "generated. The wizard will look for ELAN (*.eaf) annotation files in and beneath the directory in which this file is located.</p></html>";
    
    String TRANSCRIPTIONS_EXPLANATION = "<html><p style='font-family:sans-serif;font-size:11pt;'><b>Transcription(s)</b><br/>Select the ELAN annotation files to be included in the corpus. "
            + "Double click on any entry to change its state from 'included' (green check mark) to 'excluded' (red X) or vice versa. ";

    String PARAMETERS_EXPLANATION = "<html><p style='font-family:sans-serif;font-size:11pt;'><b>Parameters</b><br/>Specify parameters for generating the EXMARaLDA corpus.<br/> "
            + "Choose <span style='color:blue; font-weight:bold'>separate folder</span> if you want all generated EXMARaLDA transcriptions to be written to a separate folder"
            + "underneath the top level folder. This makes it easier to remove the generated files later. <br/>"
            + "Choose <span style='color:blue; font-weight:bold'>same folder</span> if you want all generated EXMARaLDA transcriptions to be written to the same folder as the original ELAN annotation file. "
            + "This ensures that relative audio links are kept and remain valid even if you move the corpus folder.<br/>"
            + "Choose <span style='color:blue; font-weight:bold'>Generate Basic Transcriptions</span> if you want to write EXMARaLDA basic transcriptions alongside the EXMARaLDA segemented transcriptions"
            + "This will require additional space, but enables you to edit transcriptions with the EXMARaLDA editor. ";

    
    CorpusNameAndPathPanel corpusNameAndPathPanel = new CorpusNameAndPathPanel();
    FolkerTranscriptionsWizardPanel folkerTranscriptionsWizardPanel = new FolkerTranscriptionsWizardPanel();
    FolkerParametersPanel parametersPanel = new FolkerParametersPanel();

    int dirCount = 0;

    /** Creates a new instance of WizardTemplate */
    public EAFCorpusWizard(java.awt.Frame parent, boolean modal) {        
        //super(parent,modal);
        this.setModal(modal);
        setTitle("Generate Corpus from ELAN annotation files");
        initPanels();
        initialise();
        loadSettings();
    }

    public Object[] getData(){
        Object[] values = new Object[3];
        values[0] = corpusNameAndPathPanel.getPath();
        values[1] = folkerTranscriptionsWizardPanel.getFiles();
        values[2] = parametersPanel.getParameters();
        return values;
    }

    void initPanels(){
        stepPanels = new JPanel[getNumberOfSteps()];
        stepPanels[0] = new AbstractWizardPanel("1. Corpus File", CORPUS_PATH_EXPLANATION, corpusNameAndPathPanel);
        stepPanels[1] = new AbstractWizardPanel("2. Transcription(s)", TRANSCRIPTIONS_EXPLANATION, folkerTranscriptionsWizardPanel);
        stepPanels[2] = new AbstractWizardPanel("3. Parameters", PARAMETERS_EXPLANATION, parametersPanel);
    }
    
    public String getStep(int n) {
        switch(n){
            case 0  : return "1. Corpus File";
            case 1  : return "2. Transcription(s)";
            case 2  : return "3. Parameters";
            default : return "";
        }
    }


    public int getNumberOfSteps() {
        return 3;
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Setting system L&F : " + javax.swing.UIManager.getSystemLookAndFeelClassName());
                try {
                    javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (InstantiationException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
                new EAFCorpusWizard(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }

    @Override
    public void gotoStep(int n) {
        super.gotoStep(n);
        switch(n){
            case 0 : // corpus name panel
                    pack();
                    break;
            case 1 : //transcriptions panel
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                final File directory = new File(corpusNameAndPathPanel.getPath()).getParentFile();
                new Thread(){
                    @Override
                    public void run() {
                        Vector<File> folkerFiles = listEAFFiles(directory);
                        folkerTranscriptionsWizardPanel.setFiles(folkerFiles);
                    }                    
                }.start();
                pack();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
            case 2 : //parameters panel
                break;
            default : break;
        }
    }

    public Vector<File> listEAFFiles(File directory){
        dirCount++;
        /*String text = "Scanning directories";
        for (int pos=0; pos<(dirCount/5)%10; pos++){
            text+=".";
        }*/
        String text = "Scanning " + directory.getName();
        folkerTranscriptionsWizardPanel.countLabel.setText(text);
        Vector<File> result = new Vector<File>();
        File[] folkerFiles = directory.listFiles(new FileFilter(){
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith("eaf");
            }
        });
        for (File f : folkerFiles){result.addElement(f);}
        File[] subDirectories = directory.listFiles(new FileFilter(){
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for (File subd : subDirectories){
            result.addAll(listEAFFiles(subd));
        }
        return result;
    }



    public void loadSettings() {
        java.util.prefs.Preferences preferences =
            java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
        String comaPath = preferences.get("eafwizard.comapath", "");
        corpusNameAndPathPanel.setPath(comaPath);
        corpusNameAndPathPanel.lastDirectory = new File(comaPath);

    }

    public void storeSettings() {
        java.util.prefs.Preferences preferences =
            java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
        String comapath = corpusNameAndPathPanel.getPath();
        preferences.put("eafwizard.comapath", comapath);

    }

    
    
}
