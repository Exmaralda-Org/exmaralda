/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.application;


import org.exmaralda.common.*;
import org.exmaralda.common.helpers.Internationalizer;
/**
 *
 * @author thomas
 */
public class HelpMenu extends javax.swing.JMenu {

    public AboutAction aboutAction;

    boolean autoInternationalize = true;

    public HelpMenu(String s, ExmaraldaApplication app) {
        super(s);
        add(new EXMARaLDAOnTheWebAction("EXMARaLDA on the web...", app));
        aboutAction = new AboutAction("About...", app);
        add(aboutAction);
        add(new CheckVersionAction("Check version...", app));
    }
    
    public HelpMenu(String s, ExmaraldaApplication app, String[] menuTitles, String url) {
        super(s);
        add(new EXMARaLDAOnTheWebAction(menuTitles[0], app, url));
        aboutAction = new AboutAction(menuTitles[1], app);
        add(aboutAction);
        if (menuTitles[2].length()>0){
            add(new CheckVersionAction(menuTitles[2], app));
        }
        autoInternationalize = false;
    }

    @Override
    public void setText(String text){
        if (autoInternationalize) {
            super.setText(Internationalizer.getString(text));
        } else {
            super.setText(text);
        }
    }
    

}
