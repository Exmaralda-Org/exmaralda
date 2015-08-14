/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class RenameFOLK2DGD {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new RenameFOLK2DGD().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(RenameFOLK2DGD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RenameFOLK2DGD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        Document meta = FileIO.readDocumentFromLocalFile("C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\FOLK_Metadata.xml");
        File[] files = new File("C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\1").listFiles();
        int count = 0;
        for (File f : files){            
            String name = f.getName();            
            // FOLK_EKSP_01_A01a_Teil_3.fln
            int index = name.indexOf(".fln");
            if (name.contains("_Teil")){
                index = name.indexOf("_Teil");
            }
            String kennung = name.substring(0, index);
            String xp = "//event[contains(Sonstige_Bezeichnungen, '" + kennung + "')]";
            List l = XPath.newInstance(xp).selectNodes(meta);
            //if (l.size()>1) continue;
            Object o = l.get(0);
            Element e = (Element)o;            
            String dgdKennung = e.getAttributeValue("event-id");

            //System.out.println(name + " " + kennung + " " + dgdKennung);
            
            //FOLK_E_00002_SE_01_T_01_DF_01
            String teil = "01";
            if (name.contains("_Teil_")){
                int index1 = name.indexOf("_Teil_") + 6;
                int index2 = name.indexOf(".fln");
                teil = name.substring(index1, index2);
            }
            if (teil.length()<2){
                teil = "0" + teil;
            }
            
            String newName = dgdKennung + "_SE_01_T_" + teil + "_DF_01.fln";
            System.out.println(name + " --> " + newName);
            
            f.renameTo(new File(f.getParentFile(), newName));
        }
    }
}
