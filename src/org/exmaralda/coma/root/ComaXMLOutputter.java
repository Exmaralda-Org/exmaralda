/*
 * Created on 21.06.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * coma2/org.sfb538.coma2/ComaXMLOutputter.java
 * 
 * @author woerner
 *  
 */
public class ComaXMLOutputter extends XMLOutputter {

	/**
	 * only used constructor. sets utf8-pretty-format for the outputter
	 */
	public ComaXMLOutputter() {
		super();
		//changed for #340
                //Format format = Format.getRawFormat();
                Format format = Format.getPrettyFormat();
                // need to be really conservative here
                // because this class is used to write segmented transcriptions?
                format.setTextMode(Format.TextMode.PRESERVE);
		format.setEncoding("UTF-8");
		this.setFormat(format);
	}

    // 26-05-2023: new for issue #340, make sure that whitespace is removed before
    // pretty printing, this will make empty lines go away
    @Override
    public void output(Document doc, Writer out) throws IOException {
            try {
                List l = XPath.selectNodes(doc, "//*[*]/text()");
                for (Object o : l){
                    Text text = (Text)o;
                    String spacesStripped = text.getText().replaceAll("[\\r\\n\\s\\t]","");
                    if (spacesStripped.trim().length()==0){
                        text.detach();
                    }
                }
            } catch (JDOMException ex) {
                Logger.getLogger(ComaXMLOutputter.class.getName()).log(Level.SEVERE, null, ex);
            }
            super.output(doc, out); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
}