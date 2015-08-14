/*
 * AbstractSegment.java
 *
 * Created on 1. August 2002, 14:23
 */

package org.exmaralda.partitureditor.jexmaralda;

import javax.swing.tree.*;
import java.io.File;
import java.net.URI;

/**
 * abstract parent class of Segment, TimedAnnotation 
 * @author  Thomas
 * @version 
 */
public abstract class AbstractSegment implements Identifiable, Describable, Linkable, javax.swing.tree.MutableTreeNode, Comparable {

    String id;
    String name;
    String description;
    String url;
    String medium;
    
    MutableTreeNode parent;
    
    /** Creates new AbstractSegment */
    public AbstractSegment() {
    }

    public abstract AbstractSegment makeCopy();
    
    /** sets the id of this segment */
    public void setID(String i) {
        id=i;
    }
    
    /** returns the id of this segment */
    public String getID() {
        return id;
    }
    
    /** sets the name of this segment */
    public void setName(String n) {
        name=n;
    }
    
    public String getName() {
        return name;
    }
    
    public void setDescription(String d) {
        description = d;
    }
    
    public String getDescription() {
        return description;
    }

    public boolean getAllowsChildren() {
        return false;
    }
    
    public java.util.Enumeration children() {
        return null;
    }
    
    public javax.swing.tree.TreeNode getChildAt(int param) {
        return null;
    }
    
    public int getIndex(javax.swing.tree.TreeNode treeNode) {
        return -1;
    }
    
    public int getChildCount() {
        return 0;
    }
    
    public boolean isLeaf() {
        return true;
    }
    
    public javax.swing.tree.TreeNode getParent() {
        return parent;
    }
    
    public void setUserObject(java.lang.Object obj) {
    }    
    
    public void remove(javax.swing.tree.MutableTreeNode mutableTreeNode) {
    }
    
    public void removeFromParent() {
    }
    
    public void setParent(javax.swing.tree.MutableTreeNode mutableTreeNode) {
        parent = mutableTreeNode;
    }
    
    public void remove(int param) {
    }
    
    public void insert(javax.swing.tree.MutableTreeNode mutableTreeNode, int param) {
    }
    
    public String toString(){
        return new String(getName() + " : " + getDescription());
    }    
    
    /** returns the link URL of this event  */
    public String getURL() {
        return url;
    }
    
    /** sets the link medium of this event to the specified value  */
    public void setMedium(String m) {
        medium = m;
    }
    
    /** returns the link medium of this event  */
    public String getMedium() {
        return medium;
    }
    
    /** sets the link URL of this event to the specified value  */
    public void setURL(String u) {
        url=u;
    }
    
    public int compareTo(Object obj) {
        Describable d = (Describable)obj;
        return (getDescription().compareToIgnoreCase(d.getDescription()));
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
    
    public boolean resolveLink(String relativeToWhat){
        if ((url==null) || (url.length()==0)) return false;
        try{
            if (new File(url).isAbsolute()) return false;
            URI uri2 = new File(relativeToWhat).getParentFile().toURI();
            URI absoluteURI = uri2.resolve(url);
            url = new File(absoluteURI).getAbsolutePath();
        } catch (Exception e){
            System.out.println("Error resolving: " + url);
            e.printStackTrace();
            // do nothing;
            return false;
        }
        return true;
    }
    

    
    
}
