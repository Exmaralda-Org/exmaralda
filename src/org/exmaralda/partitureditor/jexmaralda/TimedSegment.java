/*
 * TimedSegment.java
 *
 * Created on 1. August 2002, 14:31
 */

package org.exmaralda.partitureditor.jexmaralda;

import javax.swing.tree.*;
import java.util.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class TimedSegment extends DefaultMutableTreeNode implements Identifiable, Timeable, Describable, XMLable, Linkable, Comparable {

    String id;
    String name;
    String start;
    String end;
    String description = "";;
    String medium;
    String url;
    
    /** Creates new TimedSegment */
    public TimedSegment() {
    }    
    
    public TimedSegment makeCopy(){
        TimedSegment result = new TimedSegment();
        copyAttributes(result);
        result.setDescription(this.getDescription());
        for (int pos=0; pos<this.getChildCount(); pos++){
            Object o = getChildAt(pos);
            if (o instanceof TimedSegment){
                TimedSegment t = (TimedSegment)o;
                add(t.makeCopy());
            } else {
                AbstractSegment a = (AbstractSegment)o;
                add(a.makeCopy());
            }
        }
        return result;
    }
    
    void copyAttributes(TimedSegment t){
        t.setName(this.getName());
        t.setStart(this.getStart());
        t.setEnd(this.getEnd());
        t.setMedium(this.getMedium());
        t.setURL(this.getURL());
    }
    
    public void setDescription(String d) {
        description = d;
    }
    
    public void setEnd(String e) {
        end = e;
    }
    
    public void setID(String i) {
        id = i;
    }
    
    public String getID() {
        return id;
    }
    
    public String getStart() {
        return start;
    }
    
    public void setName(String n) {
        name = n;
    }
    
    public String getDescription() {
        if (this.getChildCount()>0){
            StringBuffer sb = new StringBuffer();
            for (int pos=0; pos<this.getChildCount(); pos++){
                sb.append(((Describable)getChildAt(pos)).getDescription());
            }
            return sb.toString();
        } else {
            return description;
        }                
    }
    
    public String getEnd() {
        return end;
    }
    
    public void setStart(String s) {
        start = s;
    }
    
    public String getName() {
        return name;
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
        url = u;
    }
    
    public boolean getAllowsChildren(){
        return true;
    }
    
    public String toString(){
        return new String(getName() + " : " + getDescription());
    }
    
    public Vector getLeaves(){
        Vector result = new Vector();
        for (int pos=0; pos<this.getChildCount(); pos++){
            TreeNode tn = getChildAt(pos);
            if (tn.isLeaf()){
                result.add(tn);
            } else {
                Vector v = ((TimedSegment)tn).getLeaves();
                result.addAll(v);
            }
        }
        return result;
    }
// ************************************************************************************

    public Identifiable getSegmentWithID(String id){
        for (int pos=0; pos<this.getChildCount(); pos++){
            Identifiable i = (Identifiable)this.getChildAt(pos);
            if (i.getID().equals(id)){
                return i;
            } else if (((TreeNode)i).getAllowsChildren()){
                Identifiable i2 = ((TimedSegment)i).getSegmentWithID(id);
                if (i2!=null) {
                    return i2;
                }
            }
        }
        return null;
    }
    
    public Vector getAllSegmentsWithName(String name){
        Vector result = new Vector();
        for (int pos=0; pos<this.getChildCount(); pos++){
            Identifiable i = (Identifiable)this.getChildAt(pos);
            if (i.getName().equals(name)){
                result.addElement(i);
            }
            if (i instanceof TimedSegment){
                result.addAll(((TimedSegment)i).getAllSegmentsWithName(name));
            }
        }        
        return result;
    }
    
    public HashSet getAllSegmentNames(){
        HashSet names = new HashSet();
        for (int pos=0; pos<this.getChildCount(); pos++){
            Identifiable i = (Identifiable)this.getChildAt(pos);
            names.add(i.getName());
            if (i instanceof TimedSegment){
                names.addAll(((TimedSegment)i).getAllSegmentNames());
            }
        }        
        return names;
    }
    
    public Hashtable indexTLIs(){
        Hashtable result = new Hashtable();
        result.put(getStart(),this);
        result.put(getEnd(),this);
        String[] thisOne = {getStart(), getEnd()};
        result.put(thisOne, this);
        for (int pos=0; pos<this.getChildCount(); pos++){
            Object o = this.getChildAt(pos);
            if (o instanceof Timeable){
                Hashtable indexesOfChildren = (((Timeable)o).indexTLIs());
                Enumeration e = indexesOfChildren.keys();
                while (e.hasMoreElements()){
                    Object key = e.nextElement();
                    result.put(key, this);
                }
            }
        }        
        return result;        
    }
    
    public TimedSegment hierarchicalMerge(  TimedSegment segmentToBeMerged, 
                                            String nameOfMergeMother,
                                            String nameOfMergeChild)throws JexmaraldaException{
        TimedSegment result = new TimedSegment();
        copyAttributes(result);        
        if (getName().equals(nameOfMergeMother)){
            Vector v = segmentToBeMerged.getAllSegmentsWithName(nameOfMergeChild);
            int first = -1;
            int last = -1;
            for (int pos=0; pos< v.size(); pos++){
                Object o = v.elementAt(pos);
                if (!(o instanceof TimedSegment)){
                    throw new JexmaraldaException(114, "Merge child is no TimedSegment: " + ((XMLable)o).toXML());
                }
                TimedSegment t = (TimedSegment)o;
                if (t.getStart().equals(this.getStart())){first=pos;}
                if (t.getEnd().equals(this.getEnd())){last=pos;}
            }
            if ((first<0) || (last<0)){
                throw new JexmaraldaException(111, "Start/End Mismatch: " + this.toXML() + " " + segmentToBeMerged.toXML());
            }
            String lastEnd = ((TimedSegment)(v.elementAt(first))).getStart();
            for (int pos=first; pos<=last; pos++){
                TimedSegment t = (TimedSegment)(v.elementAt(pos));
                if (!t.getStart().equals(lastEnd)){
                    throw new JexmaraldaException(114, "Discontinous segment sequence.");
                }
                result.add(t.makeCopy());
                lastEnd = t.getEnd();
            }
        } else { //recursion
            for (int pos=0; pos<this.getChildCount(); pos++){
                Object o = this.getChildAt(pos);
                if (!(o instanceof TimedSegment)){
                    throw new JexmaraldaException(113, "<nts> or <ats> intervening");
                }
                TimedSegment t = (TimedSegment)o;
                TimedSegment mergedT = t.hierarchicalMerge(segmentToBeMerged, nameOfMergeMother, nameOfMergeChild);
                result.add(mergedT);
            }
        }
        return result;
    }
    
// ************************************************************************************

    void timeDown(){
        if (getChildCount()==0) {return;}
        int first = 0;
        int last = 0;
        for (int pos=0; pos<this.getChildCount(); pos++){
            if (getChildAt(pos) instanceof Timeable){
                ((Timeable)getChildAt(pos)).setStart(this.getStart());
                first = pos;
                break;
            }
        }                    

        for (int pos=this.getChildCount()-1; pos>=0; pos--){
            if (getChildAt(pos) instanceof Timeable){
                ((Timeable)getChildAt(pos)).setEnd(this.getEnd());
                last = pos;
                break;
            }
        }        

        if (getChildAt(first) instanceof TimedSegment){((TimedSegment)getChildAt(first)).timeDown();}
        if ((first!=last) && (getChildAt(last) instanceof TimedSegment)){((TimedSegment)getChildAt(last)).timeDown();}                        
    }
    
    public void timeUp(){
        if (getParent()==null) {return;}
        TimedSegment parent = (TimedSegment)getParent();
        for (int pos=0; pos<parent.getChildCount(); pos++){
            if (parent.getChildAt(pos) instanceof Timeable){
                if (parent.getChildAt(pos)==this){
                    parent.setStart(this.getStart());
                }
                break;
            }
        }
        for (int pos=parent.getChildCount()-1; pos>=0; pos--){
            if (parent.getChildAt(pos) instanceof Timeable){
                if (parent.getChildAt(pos)==this){
                    parent.setEnd(this.getEnd());
                }
                break;
            }
        }        
        parent.timeUp();
    }
// ************************************************************************************
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        String [][] atts = {{"n", getName()},
                                {"id", getID()},
                                {"s", getStart()},
                                {"e", getEnd()},
                                {"medium", getMedium()},
                                {"url", getURL()}};
        sb.append(StringUtilities.makeXMLOpenElement("ts", atts));
        if (getChildCount()>0){
            for (int pos=0; pos<getChildCount(); pos++){
                sb.append(((XMLable)getChildAt(pos)).toXML());
            }
        } else {
            sb.append(StringUtilities.checkCDATA(getDescription()));
        }
        sb.append(StringUtilities.makeXMLCloseElement("ts"));
        return sb.toString();
    }
    
    public boolean isTimed() {
        return ((start!=null) && (end!=null) && (start.length()>0) && (end.length()>0));        
    }    

    public int compareTo(Object obj) {
        Describable d = (Describable)obj;
        return (getDescription().compareToIgnoreCase(d.getDescription()));
    }    

    // ************************************************************************************
    public Vector makeSegmentList(){
        Vector result = new Vector();
        String name;
        String desc;
        String start;
        String end;
        String id;
        String parentid;
        String pos_in_parent;
        if (getParent()==null){
            name = this.getName();
            desc = this.getDescription();
            start = this.getStart();
            end = this.getEnd();
            id = this.getID();
            parentid = "nil";
            pos_in_parent = "0";
            String[] nameDesc = {name, desc, start, end, id, parentid, pos_in_parent, "s"};
            result.add(nameDesc);
        }
        for (int pos=0; pos<this.getChildCount(); pos++){
            Object o = this.getChildAt(pos);            
            name = ((Identifiable)o).getName();
            desc = ((Describable)o).getDescription();
            id = ((Identifiable)o).getID();
            parentid = this.getID();
            pos_in_parent = Integer.toString(pos+1);
            if (o instanceof Timeable){
                start = ((Timeable)o).getStart();
                end = ((Timeable)o).getEnd();
            } else {
                start = "nil";
                end = "nil";
            }                
            String[] nameDesc2 = {name, desc, start, end, id, parentid, pos_in_parent, "s"};
            result.add(nameDesc2);
            if (o instanceof TimedSegment){
                result.addAll(((TimedSegment)o).makeSegmentList());
            }
        }        
        return result;
        
    }
    
    public int makeIDs(int startIDno){
        int idno = startIDno;
        this.setID("Seg_" + Integer.toString(idno));
        idno++;
        for (int pos=0; pos<this.getChildCount(); pos++){
            Object o = this.getChildAt(pos);           
            ((Identifiable)o).setID("Seg_" + Integer.toString(idno));
            idno++;
            if (o instanceof TimedSegment){
                idno = ((TimedSegment)o).makeIDs(idno);
            }
        }        
        return idno;
    }
    
    void mapStartPoints(String[] mappings, String[] segmentNames, int charPos){
        int localCharPos = charPos;
        if (mappings[charPos]==null){
            for (int i=0; i<segmentNames.length; i++){
                if (this.getName().equals(segmentNames[i])){
                    mappings[charPos]=this.getStart();
                    break;
                }
            }
        }        
        for (int pos=0; pos<this.getChildCount(); pos++){
            Object o = this.getChildAt(pos);
            if (o instanceof AtomicTimedSegment || o instanceof NonTimedSegment){
                Describable d = (Describable)o;
                localCharPos+=d.getDescription().length();
            } else if (o instanceof TimedSegment){
                TimedSegment ts = (TimedSegment)o;
                ts.mapStartPoints(mappings, segmentNames, localCharPos);
                localCharPos+=ts.getDescription().length();
            }
        }
    }
    
    public String getTLIByCharacterOffset(int offset){
        Vector leaves = this.getLeaves();
        if (offset>=this.getDescription().length()){
            // return start of the last timeable leave
            System.out.println("Halolalodri!");
            for (int pos=leaves.size()-1; pos>=0; pos--){
                if (leaves.elementAt(pos) instanceof Timeable){
                    return ((Timeable)(leaves.elementAt(pos))).getStart();
                }
            }
        }
        int count = 0;
        for (int pos=0; pos<leaves.size(); pos++){
            Describable d = (Describable)(leaves.elementAt(pos));
            if ((count<=offset) && (count+d.getDescription().length()>offset)){
                while ((pos>0) && (!(d instanceof Timeable))){
                    pos--;
                    d = (Describable)(leaves.elementAt(pos));
                }
                if (d instanceof Timeable){
                    Timeable t = (Timeable)d;
                    return t.getStart();
                } else {
                    return this.getStart();
                }
            } 
            count+=d.getDescription().length();
        }        
        return getStart();
    }
    

    
}
