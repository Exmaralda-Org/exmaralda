/*
 * RTFSelection.java
 *
 * Created on 16. Februar 2007, 12:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.interlinearText;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 *
 * @author thomas
 */

// renamed for issue #338
public class ClipboardSelection implements Transferable {

    DataFlavor rtfFlavor;
    DataFlavor[] supportedFlavors;
    private String content;
    
    public ClipboardSelection(String s) {
        content = s;
        try {
            rtfFlavor = new DataFlavor("text/rtf; class=java.io.InputStream");
            supportedFlavors = new DataFlavor[]{rtfFlavor, DataFlavor.stringFlavor, DataFlavor.allHtmlFlavor};
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(rtfFlavor) || flavor.equals(DataFlavor.stringFlavor);
    }

    @Override
    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

        if (flavor.equals(DataFlavor.stringFlavor)) {
            return content;
        } else if (flavor.equals(rtfFlavor)) {
            byte[] byteArray = content.getBytes();
            return new ByteArrayInputStream(byteArray);
        } else if (flavor.equals(DataFlavor.allHtmlFlavor)) {
          // to do issue #338   
        }
        throw new UnsupportedFlavorException(flavor);
     }
}