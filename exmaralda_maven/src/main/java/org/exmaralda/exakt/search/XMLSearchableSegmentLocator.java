/*
 * XMLSearchableSegmentLocator.java
 *
 * Created on 8. Januar 2007, 14:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class XMLSearchableSegmentLocator implements SearchableSegmentLocatorInterface{
    
    String filePath;
    String xPath;
    
    /** Creates a new instance of XMLSearchableSegmentLocator */
    public XMLSearchableSegmentLocator(String fp, String xp) {
        filePath = fp;
        xPath = xp;
    }

    public Object getCorpusComponentLocator() {
        return filePath;
    }

    public void setCorpusComponentLocator(String fp) {
        filePath = fp;
    }

    public Object getSearchableSegmentLocator() {
        return xPath;
    }

    public String getCorpusComponentFilename() {
        return filePath;
    }

    public Element toXML() {
        Element l = new Element("locator");
        l.setAttribute("file",  filePath);
        l.setAttribute("xpath", xPath);
        return l;
    }

    
}
