/*
 * ClosedCategoryListAnalysis.java
 *
 * Created on 15. Juni 2007, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.analyses;

import java.util.*;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class ClosedCategoryListAnalysis extends AbstractAnalysis {
    
    Vector<String> categories = new Vector<String>();
    
    /** Creates a new instance of ClosedCategoryListAnalysis */
    public ClosedCategoryListAnalysis(String n, String[] cs) {
        super(n);
        setCategories(cs);
    }
    
    public ClosedCategoryListAnalysis(Element e){
        super(e);
        List l = e.getChildren("category");
        for (Object c : l){
            Element cat = (Element)c;
            categories.add(cat.getText());
        }
    }
    
    public String[] getCategories(){
        String[] r = new String[categories.size()];
        System.arraycopy(categories.toArray(),0,r,0,categories.size());
        return r;
    }
    
    public boolean addCategory(String c){
        return categories.add(c);
    }
    
    public void setCategories(String[] cs){
        categories.clear();
        for (String c : cs){
            categories.add(c);
        }        
    }

    public Element toXML() {
        Element retValue;        
        retValue = super.toXML();
        retValue.setAttribute("type", "ClosedCategoryList");
        for (String c : categories){
            Element ce = new Element("category");
            ce.setText(c);
            retValue.addContent(ce);
        }        
        return retValue;
    }
    
}
