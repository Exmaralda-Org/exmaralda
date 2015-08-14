/*
 * UdInformable.java
 *
 * Created on 26. Februar 2002, 11:42
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public interface UdInformable {
    
    public UdInformation getUdInformation();
    public void setUdInformation(UdInformation udi);
    public boolean hasUdInformation();
    public void addUdInformation(String propertyName, String propertyValue);

}

