/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.IOException;
import org.exmaralda.common.corpusbuild.TEIMerger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas.schmidt
 */
public class TESTTEIMerge {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JDOMException, IOException {
        new TESTTEIMerge().doit();
    }

    private void doit() throws JDOMException, IOException {
        Document doc = FileIO.readDocumentFromLocalFile(new File("N:\\Workspace\\HMAT\\HAMATAC\\Elisa_Hitomi\\MT_180410_Elisa\\MT_180410_Elisa_s_DEBUG.exs"));
        
        Element el1 = (Element) XPath.selectSingleNode(doc, "//ts[@id='Seg_17024']");
        Element el2 = (Element) XPath.selectSingleNode(doc, "//ts[@id='Seg_19718']");
        Element merged = TEIMerger.merge(el1, el2);
        
        System.out.println(IOUtilities.elementToString(merged));
    }
    
}
