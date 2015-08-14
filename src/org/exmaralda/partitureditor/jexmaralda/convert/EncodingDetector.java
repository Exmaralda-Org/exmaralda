/*
 * EncodingDetector.java
 *
 * Created on 10. Oktober 2007, 14:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
/**
 *
 * @author thomas
 */
public class EncodingDetector {
    
    /** Creates a new instance of EncodingDetector */
    public EncodingDetector() {
    }
    
    public static String detectEncoding(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[3];
        int length = fis.read(byteArray);
        if (length<2) return "";
        System.out.println(byteArray[0]);
        if ((byteArray[0]==(byte)0xfe)
            && (byteArray[1]==(byte)0xff)){
            // UTF-16 Big-Endian
            return "UTF-16BE";
        } 
        if ((byteArray[0]==(byte)0xff)
            && (byteArray[1]==(byte)0xfe)){
            // UTF-16 Little Endian
            return "UTF-16LE";            
        }
        if (length<3) return "";
        if ((byteArray[0]==(byte)0xef) 
            && (byteArray[1]==(byte)0xbb)
            && (byteArray[2]==(byte)0xbf)){
            return "UTF-8";
        }
        return "UTF-8";
    }
    
}
