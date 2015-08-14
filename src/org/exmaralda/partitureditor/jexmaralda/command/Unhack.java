/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.command;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class Unhack {

    BasicTranscription bt;
    
    public Unhack(String transcriptionFilename) throws SAXException, JexmaraldaException{
        bt = new BasicTranscription(transcriptionFilename);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length!=2 && args.length!=3){
                System.out.println("Usage: Unhack input-file.exb output-file.exb [unhacktier]");
                System.out.println("   where [unhacktier] = position:0 (default)");
                System.out.println("      or [unhacktier] = id:TIE1    ");
                System.out.println("      or [unhacktier] = category:v    ");
                System.exit(1);
            }
            String INPUT_FILE = args[0];
            String OUTPUT_FILE = args[1];
            
            Unhack u = new Unhack(INPUT_FILE);
            String tierID = u.determineTierID(args);
            u.unhack(tierID);
            u.write(OUTPUT_FILE);
        } catch (IOException ex) {
            Logger.getLogger(Unhack.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Unhack.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(Unhack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void unhack(String unhackTierID) throws JexmaraldaException {
        bt.unhack2(unhackTierID);
    }

    private void write(String filename) throws IOException {
        bt.normalize();
        bt.writeXMLToFile(filename, "none");
    }

    private String determineTierID(String[] args) {
        if (args.length<3){
            return bt.getBody().getTierAt(0).getID();
        } else {
            if (args[2].startsWith("position:")){
                int position = Integer.parseInt(args[2].substring(args[2].indexOf(":")+1));
                return bt.getBody().getTierAt(position).getID();
            } else if (args[2].startsWith("id:")){
                String id = args[2].substring(args[2].indexOf(":")+1);                
                return id;
            } else if (args[2].startsWith("category:")){
                String category = args[2].substring(args[2].indexOf(":")+1);                
                for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                    Tier t = bt.getBody().getTierAt(pos);
                    if (t.getCategory().equals(category)) {
                        return t.getID();
                    }
                }
            }
        }
        return bt.getBody().getTierAt(0).getID();
    }
}
