/*
 * Timestamps2Timeline.java
 *
 * Created on 23. Juli 2007, 16:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.IOException;
import java.util.*;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class Timestamps2Timeline extends AbstractIUProcessor {
    
    Hashtable<String, Integer> allTimes = new Hashtable<String, Integer>();
    int count=0;
    
    /** Creates a new instance of Timestamps2Timeline */
    public Timestamps2Timeline() {
        super(true);
        INPUT_DIRECTORY = "2";
        OUTPUT_DIRECTORY = "3";
        STYLESHEET_PATH = "Step3_To_HTML.xsl";        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Timestamps2Timeline tstl = new Timestamps2Timeline();
        try {
            tstl.doIt();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void processIntonationUnit(Element iu) {
        try {
            Double startTime = iu.getAttribute("startTime").getDoubleValue();
            if (!(allTimes.containsKey(startTime.toString()))){
                allTimes.put(startTime.toString(), new Integer(count));
                count++;
            }
            Double endTime = iu.getAttribute("endTime").getDoubleValue();
            if (!(allTimes.containsKey(endTime.toString()))){
                allTimes.put(endTime.toString(), new Integer(count));
                count++;
            }
            Integer startID = allTimes.get(startTime.toString());
            iu.setAttribute("startTime", "T" + startID.toString());
            Integer endID = allTimes.get(endTime.toString());
            iu.setAttribute("endTime", "T" + endID.toString());
        } catch (DataConversionException ex) {
            ex.printStackTrace();
        }
    }

    public void beginDocument(Document d) {
        count=0;
        allTimes = new Hashtable<String,Integer>();
    }
    
    public void endDocument(Document d) {
        Object[] o = allTimes.keySet().toArray();
        java.util.Arrays.sort(o, new java.util.Comparator() {
            public int compare(Object o1, Object o2) {
                double d1 = Double.parseDouble((String)o1);
                double d2 = Double.parseDouble((String)o2);
                if (d1<d2) return -1;
                else if (d1>d2) return 1;
                return 0;
            }
            public boolean equals(Object obj) {
                return false;
            }
        });
        Element timeline = new Element("timeline");
        for (Object key : o){
            Integer i = allTimes.get(key);
            Element when = new Element("when");
            when.setAttribute("id", "T" + i.toString());
            when.setAttribute("absolute", key.toString());
            timeline.addContent(when);
        }
        d.getRootElement().addContent(0,timeline);
    }
    
}
