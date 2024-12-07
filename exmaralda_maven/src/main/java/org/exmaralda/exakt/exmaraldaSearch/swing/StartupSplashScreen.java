/*
 * StartupSplashScreen.java
 *
 * Created on 7. November 2001, 14:47
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

//import com.sun.awt.AWTUtilities;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class StartupSplashScreen extends JWindow {

    /** Creates new StartupSplashScreen
     * @param f */
    public StartupSplashScreen(JFrame f) {
        super(f);
        JLabel label = new JLabel();
        label.setOpaque(false);
        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/splashscreen.png")));
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
            public void mousePressed(java.awt.event.MouseEvent e){
                dump();
            }
        });
        final int pause = 8000;
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