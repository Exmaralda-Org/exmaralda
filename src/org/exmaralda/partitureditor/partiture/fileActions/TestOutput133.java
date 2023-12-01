/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.partitureditor.partiture.fileActions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestOutput133 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestOutput133().doit();
        } catch (SAXException | JexmaraldaException | FSMException | IOException ex) {
            Logger.getLogger(TestOutput133.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws SAXException, JexmaraldaException, FSMException, IOException {
        BasicTranscription bt = new BasicTranscription("C:\\Users\\bernd\\Dropbox\\work\\2023_Tschannen\\EXAMPLE_1_MONDADA_MOZZARELLA.exb");
        ListTranscription lt = new GATSegmentation().BasicToIntonationUnitList(bt);
        lt.writeXMLToFile("C:\\Users\\bernd\\Dropbox\\work\\2023_Tschannen\\EXAMPLE_2_MONDADA_MOZZARELLA.exl", "none");
    }
    
}
