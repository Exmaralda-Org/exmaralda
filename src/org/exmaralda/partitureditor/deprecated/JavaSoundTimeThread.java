/*
 * JavaSoundTimeThread.java
 *
 * Created on 4. August 2004, 18:03
 */

package org.exmaralda.partitureditor.deprecated;

/**
 *
 * @author  thomas
 */
public class JavaSoundTimeThread extends Thread {
    
    JavaSoundPlayer player;
    /** Creates a new instance of JavaSoundTimeThread */
    public JavaSoundTimeThread(JavaSoundPlayer p) {
        player = p;
    }

    public void run(){
        player.playbackStartTime = System.currentTimeMillis();
        while (player.timeThread != null) {
            if (player.timeThread.isInterrupted()) break;
            //player.firePosition();
            try{
                player.timeThread.sleep(player.UPDATE_INTERVAL);
            } catch (InterruptedException ie){
                player.timeThread.interrupt();
            }
        }

    }
    
    
}
