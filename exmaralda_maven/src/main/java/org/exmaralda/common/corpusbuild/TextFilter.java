/*
 * TextFilter.java
 *
 * Created on 28. November 2006, 16:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

/**
 *
 * @author thomas
 */
public class TextFilter implements org.jdom.filter.Filter {
    
    /** Creates a new instance of TextFilter */
    public TextFilter() {
    }

    public boolean matches(Object object) {
        return (object instanceof org.jdom.Text);
    }
    
}
