/*
 * AbstractSearchResultItem.java
 *
 * Created on 17. Juni 2004, 12:07
 */

package org.exmaralda.zecke;

import java.util.*;

/**
 *
 * @author  thomas
 */
public abstract class AbstractSearchResultItem implements XMLable {
    
    public String transcriptionName;
    public String transcriptionPath;
    public String tierID;
    public String speakerID;
    public String speakerAbb;
    public int matchStart;
    public int matchLength;
    public String[][] additionalMetaData;
    
    /** Creates a new instance of AbstractSearchResultItem */
    public AbstractSearchResultItem() {
    }
    
    public abstract String getTliID();
    
    
}
