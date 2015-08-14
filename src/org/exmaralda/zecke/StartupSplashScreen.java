/*
 * StartupSplashScreen.java
 *
 * Created on 7. November 2001, 14:47
 */

package org.exmaralda.zecke;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class StartupSplashScreen extends JWindow {

    /** Creates new StartupSplashScreen */
    public StartupSplashScreen(JFrame f) {
        super(f);        
        WelcomeMessagePanel welcomeMessage = new WelcomeMessagePanel();
        getContentPane().add(welcomeMessage, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension panelSize = welcomeMessage.getPreferredSize();
        setLocation(screenSize.width/2 - panelSize.width/2, screenSize.height/2 - panelSize.height/2);
        addMouseListener(new java.awt.event.MouseAdapter(){
            public void mousePressed(java.awt.event.MouseEvent e){
                dump();
            }
        });
        final int pause = 5000;
        final Runnable closerRunner = new Runnable(){
            public void run()
            {
                dump();
            }
        };
        Runnable waitRunner = new Runnable (){
            public void run(){
                try{
                    Thread.sleep(pause);
                    SwingUtilities.invokeAndWait(closerRunner);
                }
                catch (Exception e){}
            }
        };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
    }
    
    public void go(){
    }
    
    public void dump(){
        setVisible(false);
        dispose();
    }

}