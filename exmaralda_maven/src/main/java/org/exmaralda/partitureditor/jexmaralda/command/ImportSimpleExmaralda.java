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
import org.exmaralda.partitureditor.jexmaralda.convert.SimpleExmaraldaReader;

/**
 *
 * @author Schmidt
 */
public class ImportSimpleExmaralda {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=2){
            System.out.println("Usage: ImportSimpleExmaralda input.txt output.exb");
            System.exit(0);
        }
        try {
            new ImportSimpleExmaralda().importText(args[0], args[1]);
        } catch (IOException ex) {
            Logger.getLogger(ImportSimpleExmaralda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(ImportSimpleExmaralda.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void importText(String input, String output) throws IOException, JexmaraldaException {
        SimpleExmaraldaReader reader = new SimpleExmaraldaReader(input, "UTF-8");
        BasicTranscription bt = reader.parseBasicTranscription();
        bt.writeXMLToFile(output, "none");
    }
}
