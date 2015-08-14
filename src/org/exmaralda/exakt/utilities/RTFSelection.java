/*
 * RTFSelection.java
 *
 * Created on 16. Februar 2007, 12:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 *
 * @author thomas
 */
public class RTFSelection implements Transferable {

    DataFlavor rtfFlavor;
    DataFlavor[] supportedFlavors;
    private String content;
    
    public RTFSelection(String s) {
        content = s;
        try {
            rtfFlavor = new DataFlavor("text/rtf; class=java.io.InputStream");
            supportedFlavors = new DataFlavor[]{rtfFlavor, DataFlavor.stringFlavor};
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(rtfFlavor) || flavor.equals(DataFlavor.stringFlavor);
    }

    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

        if (flavor.equals(DataFlavor.stringFlavor)) {
            return content;
        } else if (flavor.equals(rtfFlavor)) {
            byte[] byteArray = content.getBytes();
            return new ByteArrayInputStream(byteArray);
        } 
        throw new UnsupportedFlavorException(flavor);
     }
}