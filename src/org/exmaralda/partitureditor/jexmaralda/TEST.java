/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda;

import java.io.IOException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas.schmidt
 */
public class TEST {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SAXException, JexmaraldaException, FSMException, IOException {
        GATSegmentation gs = new GATSegmentation();
        ListTranscription l = gs.BasicToIntonationUnitList(new BasicTranscription("C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\Godhusen\\Probierdatei_GAT_EDIT.exb"));
        l.writeXMLToFile("C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\Godhusen\\Probierdatei_GAT_EDIT_LIST_2.xml", "none");
    }
    
}
