/*
 * HTMLSelection.java
 *
 * Created on 16. Februar 2007, 11:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.io.Reader;
import java.io.StringReader;
import java.io.InputStream;

/**
 *
 * @author thomas
 */

public class HTMLSelection implements Transferable {
    
    private static ArrayList htmlFlavors = new ArrayList();

    static {
        try {
            htmlFlavors.add(new DataFlavor("text/html;class=java.lang.String"));
            htmlFlavors.add(new DataFlavor("text/html;class=java.io.Reader"));
            htmlFlavors.add(new DataFlavor("text/html;charset=unicode;class=java.io.InputStream"));
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private String html;

    public HTMLSelection(String html) {
        this.html = html;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return (DataFlavor[]) htmlFlavors.toArray(new DataFlavor[htmlFlavors.size()]);
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return htmlFlavors.contains(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (String.class.equals(flavor.getRepresentationClass())) {
            return html;
        } else if (Reader.class.equals(flavor.getRepresentationClass())) {
            return new StringReader(html);
        } 
        throw new UnsupportedFlavorException(flavor);
    }
}