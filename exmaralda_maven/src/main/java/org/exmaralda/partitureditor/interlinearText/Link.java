/*
 * Link.java
 *
 * Created on 26. Februar 2002, 12:16
 */

package org.exmaralda.partitureditor.interlinearText;

import java.io.File;
import java.net.URI;

/**
 *
 * @author  Thomas
 * @version 
 */
public class Link implements XMLElement {

    private String url;
    private String type;
    
    /** Creates new Link */
    public Link() {
    }
    
    public Link(String u, String t){
        url = u;
        type = t;
    }
    
    public String getURL(){
        return url;
    }
    
    public void setURL(String u){
        url = u;
    }
    
    public String getType(){
        return type;
    }
    
    public void setType(String t){
        type = t;
    }

    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<link url=\"");
        buffer.append(getURL());
        buffer.append("\" type=\"");
        buffer.append(getType());            
        buffer.append("\"/>");        
        return buffer.toString();
    }
    
    public String toHTML(HTMLParameters param){
        StringBuffer sb = new StringBuffer();
        sb.append("<a href=\"");
        sb.append(getURL());
        sb.append("\">");
        return sb.toString();
    }
    
    public boolean relativizeLink(String relativeToWhat){
        if ((url==null) || (url.length()==0)) return false;
        try{
            URI uri1 = new File(url).toURI();
            URI uri2 = new File(relativeToWhat).getParentFile().toURI();
            URI relativeURI = uri2.relativize(uri1);
            url = relativeURI.toString();
        } catch (Exception e){
            System.out.println("Error relativizing: " + url);
            e.printStackTrace();
            // do nothing;
            return false;            
        }
        return true;
    }
    
    
    
}
