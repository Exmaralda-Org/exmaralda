/*
 * Formattable.java
 *
 * Created on 26. Februar 2002, 10:00
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  thomas.schmidt@uni-hamburg.de
 * @version 1.0.2
 * interface for all those elements that can have some kind of format, i.e.
 * lines, it-bundles, sync-points, and so forth
 */
public interface Formattable {
    
    /** returns the format of this object */
    public Format getFormat();
    
    /** sets the format of this object */
    public void setFormat(Format f);
    
    /** augments the format by the properties in the given format */
    public void augmentFormat(Format f);
    
    /** returns the width of this object in pixels */
    public double getWidth();
    
    /** returns the height of this object in pixels */
    public double getHeight();
    
    public double getDescent();
    
    /** returns true if this object has a non-null format */
    public boolean hasFormat();
    
    /** propagates this object's format to its children that don't have formats */
    public void propagateFormat();
    
                
}

