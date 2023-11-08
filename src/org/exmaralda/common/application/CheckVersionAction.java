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
public class CheckVersionAction extends javax.swing.AbstractAction {

    ExmaraldaApplication application;
    
    public CheckVersionAction(String name, ExmaraldaApplication app) {
        super(Internationalizer.getString(name));
        application = app;
    }

    
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try{
 	    String url = "http://www.exmaralda.org/update/index.php"
			  + "?program="
                          + java.net.URLEncoder.encode(application.getApplicationName().toLowerCase().replace("-", ""))
                          + "&version=" 
                          + java.net.URLEncoder.encode(application.getVersion())
                          + "&java="
                          + java.net.URLEncoder.encode(System.getProperty("java.version"), "UTF-8")
		          + "&os=" + java.net.URLEncoder.encode( System.getProperty("os.name"))
		          + "&osversion=" + java.net.URLEncoder.encode( System.getProperty("os.version"));            
            //org.exmaralda.partitureditor.partiture.BrowserLauncher.openURL(url);
            Desktop.getDesktop().browse(new URI(url));
        } catch (java.io.IOException ioe){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
            errorDialog.showMessageDialog(  null,
                                            ioe.getLocalizedMessage(),
                                            "IO Error",
                                            javax.swing.JOptionPane.INFORMATION_MESSAGE);                           
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(CheckVersionAction.class.getName()).log(Level.SEVERE, null, ex);
        }                        
    }

}
