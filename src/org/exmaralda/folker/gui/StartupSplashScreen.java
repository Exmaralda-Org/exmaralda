/*
 * StartupSplashScreen.java
 *
 * Created on 7. November 2001, 14:47
 */

package org.exmaralda.folker.gui;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class StartupSplashScreen extends JWindow {

    public StartupSplashScreen(JFrame f, String pathToImage){
        super(f);
        JLabel label = new JLabel();
        label.setIcon(new javax.swing.ImageIcon(getClass().getResource(pathToImage)));
        getContentPane().add(label, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension panelSize = this.getPreferredSize();
        setLocation(screenSize.width/2 - panelSize.width/2, screenSize.height/2 - panelSize.height/2);
        addMouseListener(new java.awt.event.MouseAdapter(){
            public void mousePressed(java.awt.event.MouseEvent e){
                dump();
            }
        });
        final int pause = 4000;
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

    /** Creates new StartupSplashScreen */
    public StartupSplashScreen(JFrame f) {
        this(f, "/org/exmaralda/folker/gui/SplashScreen.png");
    }
    
    
    public void go(){
    }
    
    public void dump(){
        setVisible(false);
        dispose();
    }

}