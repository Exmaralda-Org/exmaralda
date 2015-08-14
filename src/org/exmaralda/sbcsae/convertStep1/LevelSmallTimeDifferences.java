/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;
/**
 *
 * @author thomas
 */
public class LevelSmallTimeDifferences extends AbstractTranscriptionProcessor {

    double THE_DIFFERENCE = 0.1;
    String INPUT_DIR = "T:\\TP-Z2\\DATEN\\SBCSAE\\0.2\\";
    String OUTPUT_DIR = "T:\\TP-Z2\\DATEN\\SBCSAE\\0.2\\";
    
    int count = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LevelSmallTimeDifferences lstd = new LevelSmallTimeDifferences();
        lstd.TRANSCRIPTION_PATH = lstd.INPUT_DIR;
        try {
            lstd.process();
            System.out.println("Overall: " + lstd.count);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LevelSmallTimeDifferences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LevelSmallTimeDifferences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(LevelSmallTimeDifferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Override
    public void processTranscription(Document transcription) throws JDOMException {
        try {
            List starts = XPath.newInstance("//@startTime").selectNodes(transcription);
            List ends = XPath.newInstance("//@endTime").selectNodes(transcription);
            HashSet<Double> allAsASet = new HashSet<Double>();
            for (Object o : starts) {
                Attribute a = (Attribute) o;
                allAsASet.add(new Double(a.getDoubleValue()));
            }
            for (Object o : ends) {
                Attribute a = (Attribute) o;
                allAsASet.add(new Double(a.getDoubleValue()));
            }

            Vector<Double> allAsAVector = new Vector<Double>();
            allAsAVector.addAll(allAsASet);
            java.util.Collections.sort(allAsAVector);

            int countThis = 0;

            Hashtable<Double, Double> mappings = new Hashtable<Double, Double>();

            for (int i = 0; i < allAsAVector.size() - 1; i++) {
                Double t1 = allAsAVector.elementAt(i);
                Double t2 = allAsAVector.elementAt(i + 1);

                if (Math.abs(t1.doubleValue() - t2.doubleValue()) <= THE_DIFFERENCE) {
                    double m = (t1.doubleValue() + t2.doubleValue()) / 2.0;
                    Double M = new Double(Math.round(m*1000.0)/1000.0);
                    mappings.put(t1, M);
                    mappings.put(t2, M);
                    i++;
                    countThis++;
                }
            }

            System.out.println(countThis + " of " + allAsAVector.size() + " here.");
            count += countThis;

            // here do it: replace the old time values with the mapped ones
            List l = XPath.newInstance("//intonation-unit").selectNodes(transcription);
            for (Object o : l){
                Element e = (Element)o;
                Double startTime = new Double(e.getAttribute("startTime").getDoubleValue());
                if (mappings.containsKey(startTime)){
                    e.setAttribute("startTime", mappings.get(startTime).toString());
                }
                Double endTime = new Double(e.getAttribute("endTime").getDoubleValue());
                if (mappings.containsKey(endTime)){
                    e.setAttribute("endTime", mappings.get(endTime).toString());
                }
            }
            
            String out = new org.exmaralda.sbcsae.convertStep1.TRN2XML().writeTRN(transcription);
            java.io.FileOutputStream fos = null;
            String outfilename = this.OUTPUT_DIR + this.filename + ".trn";
            System.out.println("Saving " + outfilename);
            System.out.println("started writing document...");
            fos = new java.io.FileOutputStream(new java.io.File(outfilename));
            fos.write(out.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
        } catch (Exception ex) {
            Logger.getLogger(LevelSmallTimeDifferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
