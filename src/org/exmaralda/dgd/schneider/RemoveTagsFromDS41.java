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
import org.exmaralda.dgd.tagging.TagDirectory;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class RemoveTagsFromDS41 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new RemoveTagsFromDS41().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(RemoveTagsFromDS41.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RemoveTagsFromDS41.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        Document d = FileIO.readDocumentFromLocalFile(new File("Y:\\thomas\\DS2FLK\\Bugfix\\0\\DS--_E_00041_SE_01_T_01_DF_01.fln"));
        List l = XPath.selectNodes(d, "//*[@lemma]");
        for (Object o : l){
            Element e = (Element)o;
            e.removeAttribute("lemma");
            e.removeAttribute("pos");
        }
        FileIO.writeDocumentToLocalFile(new File("Y:\\thomas\\DS2FLK\\Bugfix\\1\\DS--_E_00041_SE_01_T_01_DF_01.fln"), d);
        String[] myArgs = {"Y:\\thomas\\DS2FLK\\Bugfix\\1", "Y:\\thomas\\DS2FLK\\Bugfix\\2"};
        TagDirectory.main(myArgs);
        d = FileIO.readDocumentFromLocalFile(new File("Y:\\thomas\\DS2FLK\\Bugfix\\2\\DS--_E_00041_SE_01_T_01_DF_01.fln"));
        l = XPath.selectNodes(d, "//*[@p-pos]");
        for (Object o : l){
            Element e = (Element)o;
            e.removeAttribute("p-pos");
        }
        FileIO.writeDocumentToLocalFile(new File("Y:\\thomas\\DS2FLK\\Bugfix\\3\\DS--_E_00041_SE_01_T_01_DF_01.fln"), d);
    }
}
