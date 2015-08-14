/*
 * WizardTemplate.java
 *
 * Created on 20. Februar 2008, 14:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.wizard.newtranscriptionwizard;

import org.exmaralda.exakt.wizard.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import javax.swing.*;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

// MESSAGE TO KAI: I SALUTE YE!

/**
 *
 * @author thomas
 */
public class NewTranscriptionWizard extends AbstractWizardDialog{
    
    String META_DATA_EXPLANATION = "<html><p style='font-family:sans-serif;font-size:11pt;'><b>Metadata</b><br/>You can choose to <span style='color:blue; font-weight:bold'>enter some basic metadata</span> manually or "
            + "<span style='color:blue; font-weight:bold'>import metadata</span> from a communication defined inside a Corpus Manager (Coma) document. The metadata you provide here can later "
            + "be modified or supplemented via <span style='color:green; font-weight:bold'>Transcription > Meta information...</span></p></html>";

    String RECORDINGS_EXPLANATION = "<html><p style='font-family:sans-serif;font-size:11pt;'><b>Recording(s)</b><br/>Specify the audio and/or video recording(s) you want to transcribe. "
            + "A <span style='color:blue; font-weight:bold'>WAV audio recording</span> is needed to display the waveform in the editor. "
            + "An <span style='color:blue; font-weight:bold'>MP3 audio recording</span> is needed to make use of the flash player output options. "
            + "If you have a recording that is neither in WAV nor in MP3 format (e.g. an AVI video file) specify it under <span style='color:blue; font-weight:bold'>Other media file</span>."
            + "Recordings specified here can later be modified or supplemented via <span style='color:green; font-weight:bold'>Transcription > Recordings...</span></p></html>";

    String SPEAKERS_EXPLANATION = "<html><p style='font-family:sans-serif;font-size:11pt;'><b>Speakers</b><br/>Specify the abbreviations for the speakers who participate in this recording. "
            + "Enter the abbreviations separated by a comma, e.g. <br/> "
            + "<span style='color:blue; font-weight:bold'>JIL,TOM,ADA,TIM</span><br/>"
            + "Make sure that abbreviations are unique."
            + "Speakers specified here can later be modified or supplemented via <span style='color:green; font-weight:bold'>Transcription > Speakertable...</span></p></html>";

    String TIERS_EXPLANATION = "<html><p style='font-family:sans-serif;font-size:11pt;'><b>Tiers</b><br/>Specify categories and types for tiers in your transcription. "
            + "Each speaker will get exactly one tier of type T(ranscription) with the category you provide under <span style='color:blue; font-weight:bold'>category for main transcription tier</span>. "
            + "You can add any number of tiers of type A(nnotation) or D(escription) for each speaker. and place those at one of three positions: </p>"
            + "<ul style='font-family:sans-serif;font-size:10pt;'><li><span style='font-family:sans-serif;font-size:10pt;color:blue;'>immediately before the main transcription tier of the speaker</span></li>"
            + "<li><span style='font-family:sans-serif;font-size:10pt;color:blue;'>immediately after the main transcription tier of the speaker</span></li>"
            + "<li><span style='font-family:sans-serif;font-size:10pt;color:blue;'>at the bottom of the partitur</span></li></ul>"
            + "<p style='font-family:sans-serif;font-size:11pt;'>Tiers specified here can later be modified or supplemented via operations in the <span style='color:green; font-weight:bold'>Tier</span> menu.</p></html>";
            
    MetaInfoWizardPanel metaInfoWizardPanel = new MetaInfoWizardPanel();
    RecordingsWizardPanel recordingsWizardPanel = new RecordingsWizardPanel();
    SpeakersWizardPanel speakersWizardPanel = new SpeakersWizardPanel();
    TiersWizardPanel tiersWizardPanel = new TiersWizardPanel();

    /** Creates a new instance of WizardTemplate */
    public NewTranscriptionWizard(java.awt.Frame parent, boolean modal) {        
        //super(parent,modal);
        this.setModal(modal);
        setTitle("New Transcription");
        initPanels();
        initialise();
        loadSettings();
    }

    public Object[] getData(){
        Object[] values = new Object[5];
        values[0] = metaInfoWizardPanel.getData();
        values[1] = recordingsWizardPanel.getPaths();
        values[2] = speakersWizardPanel.getSpeakers();
        values[3] = tiersWizardPanel.getMainCategory();
        values[4] = tiersWizardPanel.getTierSpecifications();
        return values;
    }

    void initPanels(){
        stepPanels = new JPanel[getNumberOfSteps()];
        stepPanels[0] = new AbstractWizardPanel("1. Metadata", META_DATA_EXPLANATION, metaInfoWizardPanel);
        stepPanels[1] = new AbstractWizardPanel("2. Recording(s)", RECORDINGS_EXPLANATION, recordingsWizardPanel);
        stepPanels[2] = new AbstractWizardPanel("3. Speakers", SPEAKERS_EXPLANATION, speakersWizardPanel);
        stepPanels[3] = new AbstractWizardPanel("4. Tiers", TIERS_EXPLANATION, tiersWizardPanel);
    }
    
    public String getStep(int n) {
        switch(n){
            case 0  : return "1. Metadata";
            case 1  : return "2. Recording(s)";
            case 2  : return "3. Speakers";
            case 3  : return "4. Tiers";
            //case 4  : return "5. Timeline";
            //case 5  : return "6. Formatting";
            default : return "";
        }
    }


    public int getNumberOfSteps() {
        return 4;
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
                new NewTranscriptionWizard(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }

    @Override
    public void gotoStep(int n) {
        super.gotoStep(n);
        Element comaComm = metaInfoWizardPanel.getSelectedComaCommunication();
        switch(n){
            case 0 : // meta panel
                    pack();
                    break;
            case 1 : //recordings panel
                if (comaComm!=null){
                    Iterator i = comaComm.getDescendants(new ElementFilter("Media"));
                    while (i.hasNext()){
                        Element media = (Element)(i.next());
                        String nsLink = media.getChildText("NSLink");
                        //System.out.println(nsLink);
                        String fullPath = "";
                        //System.out.println("***" + metaInfoWizardPanel.getComaPath());
                        URI uri2 = new File(metaInfoWizardPanel.getComaPath()).getParentFile().toURI();
                        //System.out.println("Resolving " + nsLink + " relative to " + metaInfoWizardPanel.getComaPath());
                        URI absoluteURI = uri2.resolve(nsLink);
                        fullPath = new File(absoluteURI).getAbsolutePath();
                        if (nsLink.toUpperCase().endsWith("MP3")){
                            recordingsWizardPanel.mp3TextField.setText(fullPath);
                        } else if (nsLink.toUpperCase().endsWith("WAV")){
                            recordingsWizardPanel.wavTextField.setText(fullPath);
                        } else {
                            recordingsWizardPanel.otherTextField.setText(fullPath);
                        }
                    }
                }
                String cp = metaInfoWizardPanel.getComaPath();
                if (cp!=null){
                    recordingsWizardPanel.lastDirectory = new File(cp).getParentFile();
                }
                break;
            case 2 : //speakers panel
                if (comaComm!=null){
                    String speakers = "";
                    Iterator i = comaComm.getDescendants(new ElementFilter("Person"));
                    while (i.hasNext()){
                        Element person = (Element)(i.next());
                        String id = person.getText();
                        String xp = "//Speaker[@Id='" + id + "']";
                        try {
                            Element speaker = (Element) (XPath.selectSingleNode(metaInfoWizardPanel.getComaDocument().getRootElement(), xp));
                            String sigle = speaker.getChildText("Sigle");
                            if (speakers.length()>0){
                                speakers+=",";
                            }
                            speakers+=sigle;
                        } catch (JDOMException ex) {
                            ex.printStackTrace();
                        }
                    }
                    speakersWizardPanel.speakersTextArea.setText(speakers);
                    speakersWizardPanel.parse();
                }
                break;
            case 4 : break;
            default : break;
        }
    }



    public void loadSettings() {
        java.util.prefs.Preferences preferences =
            java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        boolean useComa = preferences.getBoolean("wizard.usecomafile", false);
        if (useComa){
            metaInfoWizardPanel.setComaUse();
            String comapath = preferences.get("wizard.comapath", "");
            if (comapath.length()>0){
                try {
                    metaInfoWizardPanel.setComaPath(comapath);
                } catch (JDOMException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        String beforeTiers = preferences.get("wizard.beforeTiers", "");
        String[][] beforeTiersSpec = parseTierSpec(beforeTiers);
        String afterTiers = preferences.get("wizard.afterTiers", "");
        String[][] afterTiersSpec = parseTierSpec(afterTiers);
        String bottomSpeakerTiers = preferences.get("wizard.bottomSpeakerTiers", "");
        String[][] bottomSpeakerTiersSpec = parseTierSpec(bottomSpeakerTiers);
        String bottomNoSpeakerTiers = preferences.get("wizard.bottomNoSpeakerTiers", "");
        String[][] bottomNoSpeakerTiersSpec = parseTierSpec(bottomNoSpeakerTiers);

        tiersWizardPanel.setTierSpecifications(beforeTiersSpec,afterTiersSpec,bottomSpeakerTiersSpec,bottomNoSpeakerTiersSpec);

        String mainCat = preferences.get("wizard.mainTierCategory", "v");
        tiersWizardPanel.setMainCategory(mainCat);
    }

    public void storeSettings() {
        java.util.prefs.Preferences preferences =
            java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        preferences.putBoolean("wizard.usecomafile", metaInfoWizardPanel.getSelectedComaCommunication()!=null);
        String comapath = "";
        if (metaInfoWizardPanel.getComaPath()!=null){
            comapath = metaInfoWizardPanel.getComaPath();
        }
        preferences.put("wizard.comapath", comapath);

        String[][][] tierSpecs = tiersWizardPanel.getTierSpecifications();
        preferences.put("wizard.beforeTiers", encodeTierSpec(tierSpecs[0]));
        preferences.put("wizard.afterTiers", encodeTierSpec(tierSpecs[1]));
        preferences.put("wizard.bottomSpeakerTiers", encodeTierSpec(tierSpecs[2]));
        preferences.put("wizard.bottomNoSpeakerTiers", encodeTierSpec(tierSpecs[3]));
        preferences.put("wizard.mainTierCategory", tiersWizardPanel.getMainCategory());
    }

    String[][] parseTierSpec(String string){
        if (string.length()==0){return new String[0][0];}
        String[] individualSpecs = string.split("§");
        String[][] result = new String[individualSpecs.length][2];
        int pos=0;
        for (String individualSpec : individualSpecs){
            String[] indSpec = individualSpec.split("\\|");
            result[pos] = indSpec;
            pos++;
        }
        return result;
    }

    private String encodeTierSpec(String[][] specs) {
        String result = "";
        int pos=0;
        for (String[] spec : specs){
            result+=spec[0] + "|" + spec[1];
            if (pos<specs.length-1){
                result+="§";
            }
            pos++;
        }
        return result;
    }

    
    
}
