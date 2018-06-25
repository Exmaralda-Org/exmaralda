/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tei;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.sbcsae.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 *
 * @author Thomas_Schmidt
 */
public class XPathTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new XPathTest().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(XPathTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XPathTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        Document doc = FileIO.readDocumentFromLocalFile(new File("F:\\AGD-DATA\\dgd2_data\\iso-transcripts\\FOLK\\FOLK_E_00001_SE_01_T_01_DF_01.xml"));
        XPath xp1 = XPath.newInstance("//tei:spanGrp[@type='lemma']/tei:span[1]"); 
        xp1.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        Element e1 = (Element) xp1.selectSingleNode(doc);
        if(e1==null){
            System.out.println("NULLINGER");
        }
        
        XPath xp2 = XPath.newInstance("ancestor-or-self::tei:span[@from][1]/@from"); 
        xp2.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        Element e2 = (Element) xp2.selectSingleNode(e1);
        String s = IOUtilities.elementToString(e2);
        System.out.println(s);
    }
    
}
