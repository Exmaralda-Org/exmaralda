/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda;

import java.io.IOException;
import java.util.List;
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
        new TEST().doit();
    }

    private void doit() throws SAXException, JexmaraldaException {
        BasicTranscription bt = new BasicTranscription("N:\\Workspace\\EXMARaLDA\\EXMARaLDA-DemoKorpus\\AnneWill\\AnneWill.exb");
        Tier tier = bt.getBody().getTierAt(0);
        List<List<Event>> segmentChains = tier.getSegmentChains(bt.getBody().getCommonTimeline());
        for (List<Event> sc : segmentChains){
            for (Event e : sc){
                System.out.println(e.toXML());
            }
            System.out.println("===========");
        }
    }
    
}
