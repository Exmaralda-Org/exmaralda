/*
 * SearchResultInterface.java
 *
 * Created on 5. Januar 2007, 17:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

/**
 *
 * @author thomas
 */
public interface SearchResultInterface {
    
    public String getLeftContextAsString();
    
    public String getRightContextAsString();
    
    public String getMatchTextAsString();
    
    public String getKWICAsHTML();
    
    public SearchableSegmentLocatorInterface getSearchableSegmentLocator();
    
    public org.jdom.Element toXML();
    
    public org.jdom.Element toXML(String baseDirectory);
    
    public String toHTML();
    
    public boolean isSelected();
    
    public void setSelected(boolean isSelected);

    String[] getAdditionalData();
    
    void setAdditionalData(int index, String value);

    public void setAdditionalData(String[] newAdditionalData);

    
    
    
    
}
