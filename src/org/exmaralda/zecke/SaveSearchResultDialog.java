/*
 * SaveSearchResultDialog.java
 *
 * Created on 9. Dezember 2004, 13:45
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.HTMLFileFilter;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ExmaraldaFileFilter;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.TextFileFilter;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import javax.swing.*;
import java.io.*;
/**
 *
 * @author  thomas
 */
public class SaveSearchResultDialog extends JFileChooser {
    
    AbstractSearchResult result;
    JFrame parent;
    
    /** Creates a new instance of SaveSearchResultDialog */
    public SaveSearchResultDialog(JFrame p, AbstractSearchResult r) {
        result = r;
        parent = p;
        addChoosableFileFilter(new HTMLFileFilter());
        addChoosableFileFilter(new TextFileFilter());
        addChoosableFileFilter(new ExmaraldaFileFilter());
    }
    
    public void saveSearchResult(){
        int returnValue=showSaveDialog(parent);
        if (returnValue!=JFileChooser.APPROVE_OPTION) return;
        try{
            String outputfilepath = this.getSelectedFile().getAbsolutePath();
            FileOutputStream fos = new FileOutputStream(new File(outputfilepath));
            StringBuffer xmlStringBuffer = new StringBuffer();
            xmlStringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            //xmlStringBuffer.append("<?xml-stylesheet type=\"text/xsl\" href=\"SR2HTML.xsl\"?>");
            xmlStringBuffer.append("<result>");
            for (int pos=0; pos<result.size(); pos++){
                XMLable item = (XMLable)(result.elementAt(pos));
                xmlStringBuffer.append(item.toXML());
            }
            xmlStringBuffer.append("</result>");
            javax.swing.filechooser.FileFilter ff = getFileFilter();
            if (ff instanceof HTMLFileFilter){
                StylesheetFactory sf = new StylesheetFactory();
                String html = sf.applyInternalStylesheetToString("/org/exmaralda/zecke/SR2HTML.xsl", xmlStringBuffer.toString());
                fos.write(html.getBytes("UTF-8"));            
            } else if (ff instanceof ExmaraldaFileFilter){
                fos.write(xmlStringBuffer.toString().getBytes("UTF-8"));            
            } else {
                StylesheetFactory sf = new StylesheetFactory();
                String html = sf.applyInternalStylesheetToString("/org/exmaralda/zecke/SR2TXT.xsl", xmlStringBuffer.toString());
                fos.write(html.getBytes("UTF-8"));                    
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            new JOptionPane().showMessageDialog(parent, e.getLocalizedMessage());
        }
    }
    
    
    
}
