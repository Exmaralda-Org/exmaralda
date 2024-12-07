/*
 * Test.java
 *
 * Created on 13. Juni 2003, 13:35
 */

package org.exmaralda.partitureditor.search;

import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
/**
 *
 * @author  thomas
 */
public class Test {
    
    /** Creates a new instance of Test */
    public Test() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            BasicTranscription bt = new BasicTranscription("D:\\AAA_Beispiele\\Helge_neu\\Helge_Basic.xml");
            Vector v = EventSearcher.search("was", bt, false);
            for (int pos=0; pos<v.size(); pos++){
                EventSearchResult esr = (EventSearchResult)(v.elementAt(pos));
                System.out.println(esr.tierID + " / " + esr.offset + " / " + esr.event.getDescription());
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }
    
}
