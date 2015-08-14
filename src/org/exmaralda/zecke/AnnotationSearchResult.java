/*
 * SearchResult.java
 *
 * Created on 11. Juni 2004, 14:28
 */

package org.exmaralda.zecke;

/**
 *
 * @author  thomas
 */
public class AnnotationSearchResult extends AbstractSearchResult {
    
    /** Creates a new instance of SearchResult */
    public AnnotationSearchResult() {
    }
    
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<result search-type=\"annotation\">");
        for (int pos=0; pos<size(); pos++){
            XMLable item = (XMLable)(elementAt(pos));
            sb.append(item.toXML());
        }
        sb.append("</result>");
        return sb.toString();
    }
    
}
