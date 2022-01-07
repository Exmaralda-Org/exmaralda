/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.application;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import org.exmaralda.common.*;
import org.exmaralda.common.helpers.Internationalizer;


/**
 *
 * @author thomas
 */
public class EXMARaLDAOnTheWebAction extends javax.swing.AbstractAction {

    ExmaraldaApplication application;
    String url = "http://www.exmaralda.org";

    public EXMARaLDAOnTheWebAction(String name, ExmaraldaApplication app) {
        super(Internationalizer.getString(name));
        application = app;
    }

    public EXMARaLDAOnTheWebAction(String name, ExmaraldaApplication app, String url) {
        super(Internationalizer.getString(name));
        application = app;
        this.url = url;
    }
    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try{
            
            //org.exmaralda.partitureditor.partiture.BrowserLauncher.openURL(url);
            Desktop.getDesktop().browse(new URI(url.toString()));

        } catch (java.io.IOException ioe){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
            errorDialog.showMessageDialog(  null,
                ioe.getLocalizedMessage(),
                "IO Error",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);                           
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(EXMARaLDAOnTheWebAction.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

}
