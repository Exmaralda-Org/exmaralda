/*
 * SegmentList.java
 *
 * Created on 27. September 2002, 13:12
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import java.io.*;



/**
 *
 * @author  Thomas
 */
public class SegmentList extends Vector{
    
    Hashtable tlis;
    
    /** Creates a new instance of SegmentList */
    public SegmentList() {
        tlis = new Hashtable();
    }
    
    public void setTlis(Hashtable t){
        tlis = t;
    }
    
    public void writeHTMLToFile(String filename, String targetFrameName, String targetFileName) throws IOException {
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(toHTML(targetFrameName, targetFileName).getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");                
    }
    
    public String toHTML(String targetFrameName, String targetFileName){
        Collections.sort(this);
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        sb.append("<base target=\"" + targetFrameName + "\"/></head>");
        sb.append("<style>");
        sb.append("a {font-size:8pt; color:rgb(150,150,150); padding-right:10px}");
        sb.append("td { vertical-align:top}");
        sb.append("td.Lemma { font-size:10pt; font-weight:bold}");
        sb.append("</style>");
        sb.append("<body>");
        String nakedFileName = new File("file:///" + targetFileName).getName();
        /*for (int pos=0; pos<size(); pos++){
            Describable d = (Describable)(elementAt(pos));
            String start = ((String)tlis.get((String)((Timeable)d).getStart()));
            sb.append("<a href=\"" + "./" + nakedFileName + "#" + start + "\">" + d.getDescription() + "</a><br>");                    
            //sb.append("<a href=\"" + "file:///" + targetFileName + "#" + start + "\">" + d.getDescription() + "</a><br>");                    
        } */       
        // NEW OUTPUT in version 1.3.3.
        sb.append("<table>");
        for (int pos=0; pos<size(); pos++){
            Describable d = (Describable)(elementAt(pos));
            String description = d.getDescription();
            sb.append("<tr><td class=\"Lemma\">");
            sb.append(d.getDescription().toLowerCase());
            sb.append("</td><td>");
            String d2 = ((Describable)elementAt(pos)).getDescription();
            while ((pos<size()) && (description.equalsIgnoreCase(d2))){
                String start = ((String)tlis.get((String)((Timeable)elementAt(pos)).getStart()));
                sb.append("<a href=\"" + "./" + nakedFileName + "#" + start + "\">" + start + "</a> ");                    
                pos++;
                if (pos<size()){
                    d2 = ((Describable)elementAt(pos)).getDescription();
                }
            }
            sb.append("</td></tr>");
            pos--;
        }
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString();        
    }
    
    public String toText(Timeline timeline){
        Collections.sort(this);
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
        sb.append("<body>");
        sb.append("<table>");
        for (int pos=0; pos<size(); pos++){
            sb.append("<tr>");
            Describable d = (Describable)(elementAt(pos));
            sb.append("<td>" + d.getDescription() + "</td>");
            //sb.append(d.getDescription() + "\u0009");
            String start = ((String)tlis.get((String)((Timeable)d).getStart()));
            int startPosition = timeline.lookupID(start);
            //sb.append(startPosition + "\u0009" + start + System.getProperty("line.separator"));
            sb.append("<td>" + startPosition + "</td>");
            sb.append("<td>" + start + "</td>");
            sb.append("</tr>");
        }        
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString();                
    }
    
    public void writeTextToFile(String filename, Timeline timeline) throws IOException {
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(toText(timeline).getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");                
    }
    
    
}
