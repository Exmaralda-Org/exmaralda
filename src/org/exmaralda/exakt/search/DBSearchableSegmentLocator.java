/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.exakt.search;

import org.jdom.Element;

/**
 *
 * @author hanna
 */
public class DBSearchableSegmentLocator implements SearchableSegmentLocatorInterface{

    String transcriptionKey;
    String segmentKey;
    String filePath;
    String xPath;

    /** Creates a new instance of DBSearchableSegmentLocator */
    public DBSearchableSegmentLocator(String transcriptionKey, String segmentKey, String fp, String xp) {
        this.transcriptionKey = transcriptionKey;
        this.segmentKey = segmentKey;
        filePath = fp;
        xPath = xp;
    }

    public String getCorpusComponentFilename() {
        return filePath;
    }

    public Object getCorpusComponentLocator() {
        //return filePath;
        return transcriptionKey;
    }

    public void setCorpusComponentLocator(String fp) {
        //filePath = fp;
        transcriptionKey = fp;
    }

    public Object getSearchableSegmentLocator() {
        return xPath;
    }

    public Element toXML() {
        Element l = new Element("locator");
        l.setAttribute("transcriptionGUID", transcriptionKey);
        l.setAttribute("segmentID", segmentKey);
        l.setAttribute("file", filePath);
        l.setAttribute("xpath", xPath);
        return l;
    }

}
