/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tei;

import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas.schmidt
 */
public class DEUBUG_BAS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SAXException, JexmaraldaException {
        new DEUBUG_BAS().doit();
    }

    String PATH = "C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\BAS_CIRCLE_12_2021\\S15M.exb";
    private void doit() throws SAXException, JexmaraldaException {
        BasicTranscription bt = new BasicTranscription(PATH);
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier tier = bt.getBody().getTierAt(pos);
            if (!(tier.getType().equals("t"))) continue;
            System.out.println("Analysing tier " + tier.getDisplayName());
            int count = 0;
            String lastEnd = "";
            for (int i=0; i<tier.getNumberOfEvents(); i++){
                Event currentEvent = tier.getEventAt(i);
                String start = currentEvent.getStart();
                String end = currentEvent.getEnd();
                
                if (!(start.equals(lastEnd))){
                    count++;
                }
                
                lastEnd = end;
                
                
            }
            
            System.out.println(count + " segment chains and " + tier.getNumberOfEvents() + " events");
        }
    }
    
}
