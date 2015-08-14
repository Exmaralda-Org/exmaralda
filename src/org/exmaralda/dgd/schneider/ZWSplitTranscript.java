/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class ZWSplitTranscript {

    public ZWSplitTranscript(File inputFile, File outputFile, String cId, String wID, String tID, double subtractTime) throws JDOMException, IOException{
        Document doc = FileIO.readDocumentFromLocalFile(inputFile);
        
        System.out.println(inputFile.getAbsolutePath() + " read.");
        
        List l1 = XPath.newInstance("//contribution[@id='" + cId + "']/preceding-sibling::contribution").selectNodes(doc);
        for (Object o1 : l1){
            Element e = (Element)o1;
            e.detach();
        }

        List l2 = XPath.newInstance("//timepoint[@timepoint-id='" + tID + "']/preceding-sibling::timepoint").selectNodes(doc);
        for (Object o2 : l2){
            Element e = (Element)o2;
            e.detach();
        }
        
        List l3 = XPath.newInstance("//w[@id='" + wID + "']/preceding-sibling::*").selectNodes(doc);
        for (Object o3 : l3){
            Element e = (Element)o3;
            e.detach();
        }
        
        List l4 = XPath.newInstance("//timepoint").selectNodes(doc);
        for (Object o4 : l4){
            Element e = (Element)o4;
            double newTime = Double.parseDouble(e.getAttributeValue("absolute-time")) - subtractTime;
            e.setAttribute("absolute-time", Double.toString(newTime));
        }

        List l5 = XPath.newInstance("//*[@time]").selectNodes(doc);
        for (Object o5 : l5){
            Element e = (Element)o5;
            double newTime = Double.parseDouble(e.getAttributeValue("time")) - subtractTime;
            e.setAttribute("time", Double.toString(newTime));
        }
        
        Element contribution = (Element) XPath.newInstance("//contribution[@id='" + cId + "']").selectSingleNode(doc);
        contribution.setAttribute("start-reference", tID);
        contribution.setAttribute("time", "0.0");

        FileIO.writeDocumentToLocalFile(outputFile, doc);
        
        System.out.println(outputFile.getAbsolutePath() + " written.");
        
    
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            /*ZWSplitTranscript st = new ZWSplitTranscript(
                    new File("Y:\\thomas\\ZW2FLK\\11\\ZW--_E_00564_SE_01_T_01_DF_01.fln"),
                    new File("Y:\\thomas\\ZW2FLK\\12\\ZW--_E_00564_SE_01_T_01_DF_01.fln"),
                    "c43", "w647", "TLI_646", 113.99
                    );*/
            if (args.length==0){
                ZWSplitTranscript st = new ZWSplitTranscript(
                        new File("Y:\\thomas\\ZW2FLK\\11\\ZW--_E_00566_SE_01_T_01_DF_01.fln"),
                        new File("Y:\\thomas\\ZW2FLK\\12\\ZW--_E_00566_SE_01_T_01_DF_01.fln"),
                        "c45", "w1362", "TLI_1361", 285.01
                        );
            } else {
                ZWSplitTranscript st = new ZWSplitTranscript(
                        new File(args[0]),
                        new File(args[1]),
                        args[2], args[3], args[4], Double.parseDouble(args[5])
                        );
            }
        } catch (JDOMException ex) {
            Logger.getLogger(ZWSplitTranscript.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ZWSplitTranscript.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
