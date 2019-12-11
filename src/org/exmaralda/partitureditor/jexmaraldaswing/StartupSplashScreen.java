/*
 * StartupSplashScreen.java
 *
 * Created on 7. November 2001, 14:47
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

//import com.sun.awt.AWTUtilities;
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
        JLabel label = new JLabel();
        label.setOpaque(false);
        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/SplashScreen.png")));
        label.repaint();

        Color transparentColor = new Color(1,1,1,Color.TRANSLUCENT);
        this.setBackground(transparentColor);
        /*try {
            AWTUtilities.setWindowOpaque(this, false);
        } catch (IllegalArgumentException iae){
            // happens on shitty systems like lubuntu
            iae.printStackTrace();
        }*/

        getContentPane().add(label, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension panelSize = this.getPreferredSize();
        setLocation(screenSize.width/2 - panelSize.width/2, screenSize.height/2 - panelSize.height/2);
        addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mousePressed(java.awt.event.MouseEvent e){
                dump();
            }
        });
        final int pause = 8000;
        final Runnable closerRunner = new Runnable(){
            @Override
            public void run()
            {
                dump();
            }
        };
        Runnable waitRunner = new Runnable (){
            @Override
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
    
    public static void main(String[] args){
        new StartupSplashScreen(new JFrame());
    }
    
    
    public void go(){
    }
    
    public void dump(){
        setVisible(false);
        dispose();
    }

}