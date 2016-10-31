/*
 * PartiturEditorWithDIDAExtensions.java
 *
 * Created on 22. Oktober 2003, 12:10
 */

package org.exmaralda.partitureditor.DIDA;

import javax.swing.*;
/**
 *
 * @author  thomas
 */
public class PartiturEditorWithDIDAExtensions extends org.exmaralda.partitureditor.partiture.PartiturEditor implements java.awt.event.MouseListener {
    
    // Ein zusaetzliches Menue fuer DIDA-spezifische Aktionen
    javax.swing.JMenu didaMenu;
    // Ein oder mehrere DIDA-spezifische Aktionen
    javax.swing.AbstractAction someAction;
    javax.swing.AbstractAction someOtherAction;
    
    org.exmaralda.partitureditor.praatPanel.PraatControl praatControl = new org.exmaralda.partitureditor.praatPanel.PraatControl();
    
    /** Creates a new instance of PartiturEditorWithDIDAExtensions */
    public PartiturEditorWithDIDAExtensions() {
        // Initialisierung der Superklasse
        super();
        //getPartitur().openAction.setEnabled(false);
        getPartitur().getModel().getTranscription().getHead().getMetaInformation().getUDMetaInformation().getValueOfAttribute("Mein Attribut");
        /*try{
            praatControl.startPraat();
            javax.swing.JOptionPane.showConfirmDialog(this, "PRAAT started");
            praatControl.openSoundFile("D:\\AAA_Beispiele\\PaulMcCartney\\PaulMcCartney.wav");
        } catch (Exception e){
            e.printStackTrace();
        }*/
        // Initialisierung der DIDA-spezifischen Aktionen
        // Diese werden in der Regel auf der Partitur operieren, deshalb
        // wird fuer die Initialisierung ein Pointer auf die Partitur uebergeben
        // Hier wurde einfach exemplarisch die Aktion zum Oeffnen
        // einer neuen Transkription eingesetzt - vgl. dort.
        // Eine Aktion zum "Abholen" einer Transkription aus der Datenbank und
        // fuer den umgekehrten Prozess wuerde wahrscheinlich sehr aehnlich aussehen
        someAction = new org.exmaralda.partitureditor.partiture.fileActions.OpenAction(getPartitur(), null);
        someOtherAction = new javax.swing.AbstractAction("Eine andere Aktion"){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performSomeOtherAction();
            }
            
        };
            
        
        getJMenuBar().getMenu(0).getMenuComponent(3).setEnabled(false);
        getJMenuBar().getMenu(0).getMenuComponent(17).setEnabled(false);
        getJMenuBar().getMenu(0).getMenuComponent(18).setEnabled(false);
        
        this.getVersion();

        // Initialisierung des DIDA-Men�s
        didaMenu = new javax.swing.JMenu();
        didaMenu.setText("DIDA");
        // Hinzuf�gen der DIDA-spezifischen Aktionen zum DIDA-Men�
        didaMenu.add(someAction);
        didaMenu.add(someOtherAction);
        
        // Hinzuf�gen des DIDA-Men�s zur Men�-Bar
        getJMenuBar().add(didaMenu);
        System.out.println(getName());
        getPartitur().setLocked(true);

        
        this.getPartitur().editTierAction.setEnabled(false);
    
        this.addMouseListener(this);
    }
    
    // Wenn der Benutzer die Auswahl in der Partitur �ndert wird diese Methode aufgerufen
    // startTime ist dann die der Auswahl n�chste vorhergehende absolute Zeitangabe
    // endTime ist die der Auswahl n�chste folgende Zeitangabe
    // in der Superklasse ist diese Methode leer
    // hier m�sste sie so overridden werden, dass sie die startTime und endTime
    // (wahrscheinlich �ber Aufruf einer System-Routine)
    // an XWaves bzw. Praat weitergibt, damit diese Programme entsprechend reagieren,
    // also z.B. in der zugeh�rigen Aufnahme den betreffenden Abschnitt selektieren
    public void processMediaTimeChanged(double startTime, double endTime){
        
        // Hier werden zu Demozwecken Start- und Endzeit einfach in
        // die Standardausgabe geschrieben (Vorsicht! Die Superklasse
        // leitet die Standardausgabe in die Datei "EXMARaLDA_log.txt" im
        // jeweils aktuellen Benutzerverzeichnis um. Die Ausgabe "Startzeit..."
        // befindet sich also dort.
        String text = "Startzeit ist jetzt " + Double.toString(startTime);
        text += System.getProperty("line.separator");
        text += "Endzeit ist jetzt " + Double.toString(endTime);
        System.out.println(text);
        try{
            praatControl.selectSound(startTime, endTime);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void performSomeOtherAction(){
        System.out.println("Some other action performed.");
        if (getPartitur().transcriptionChanged){
            javax.swing.JOptionPane.showConfirmDialog(this, "Ge�������ndert!!!!");
        } else {
            javax.swing.JOptionPane.showConfirmDialog(this, "Nixe ge�ndert.");
        }
        try {
            double time = praatControl.getCursorTime();
            System.out.println("If i wanted, I could now set the time to " + time);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {       
        new PartiturEditorWithDIDAExtensions().show();                
    }

    public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger()) {
            getPartitur().editTierAction.setEnabled(false);
        }        
    }

    public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
    }

    public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
    }

    public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
    }

    public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
    }
    
    
}
