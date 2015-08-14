/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

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
public class SelfoverlapErrors extends AbstractErrorFinder {

    XPath speakerXPath;
    
    public SelfoverlapErrors() throws JDOMException {
        speakerXPath = XPath.newInstance("//@speaker");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            SelfoverlapErrors x = new SelfoverlapErrors();
            x.TRANSCRIPTION_PATH = "T:\\TP-Z2\\DATEN\\SBCSAE\\0.2\\";
            x.process();
            List l = x.errors.getRootElement().getChildren();
            Vector<Element> toBeDetached = new Vector<Element>();
            for (Object o : l){
                toBeDetached.addElement((Element)o);
            }
            for (Element e : toBeDetached){
                e.detach();
            }
            Collections.sort(
                toBeDetached ,
                new java.util.Comparator<Element>() {


                public int compare(Element o1, Element o2) {
                    String t1 = o1.getText();
                    String t2 = o2.getText();
                    Double d1 = new Double(t1.substring(14));
                    Double d2 = new Double(t2.substring(14));
                    return d1.compareTo(d2);
                }
            });
            x.errors.getRootElement().addContent(toBeDetached);
            x.writeErrorDocument("T:\\TP-Z2\\DATEN\\SBCSAE\\SelfoverlapErrors.xml");
        } catch (IOException ex) {
            Logger.getLogger(SelfoverlapErrors.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(SelfoverlapErrors.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processTranscription(Document transcription) throws JDOMException {
        HashSet<String> speakers = new HashSet<String>();
        List l = speakerXPath.selectNodes(transcription);
        for (Object o : l){
            Attribute a = (Attribute)o;
            speakers.add(a.getValue());
        }
        for (String speaker : speakers){
            String xp = "//intonation-unit[@speaker='" + speaker + "']";
            List l2 = XPath.newInstance(xp).selectNodes(transcription);
            double lastStart = -1;
            for (Object o : l2){
                Element e = (Element)o;
                double thisStart = Double.parseDouble(e.getAttributeValue("startTime"));
                if ((lastStart!=-1) && (thisStart<lastStart)){
                    String difference = Double.toString(Math.round((lastStart-thisStart)*1000.0)/1000.0);
                    addError(Integer.toString(currentTranscriptionNumber),e.getAttributeValue("line"), 
                            "Self-overlap: " + difference);//.substring(0,Math.min(difference.length()-1, 5)));
                }
                lastStart = Double.parseDouble(e.getAttributeValue("endTime"));;
            }
        }
    }

}
