/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.common.helpers;

import java.io.IOException;
import java.io.StringReader;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author bernd
 */
public class XMLFormatter {
    
    
    public static String formatXML(String unformattedXML, boolean omitDeclaration) throws JDOMException, IOException{
        Format prettyFormat = Format.getPrettyFormat();
        // very important, issue#340
        prettyFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE);
        prettyFormat.setOmitDeclaration(omitDeclaration);
        String prettyXml = new XMLOutputter(prettyFormat).
                         outputString(new SAXBuilder().build(new StringReader(unformattedXML)));
        return prettyXml;
    }
    
}
