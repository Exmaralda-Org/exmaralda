/*
 * TierEventViewer.java
 *
 * Created on 20. Maerz 2008, 11:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.timeview;

import org.exmaralda.folker.experiment.transcription.EventInterface;
import org.exmaralda.folker.experiment.transcription.TierEventInterface;
import java.awt.*;
import java.util.*;

/**
 *
 * @author thomas
 */
public class TierEventViewer extends WaveFormViewer {
    
    int TIER_HEIGHT = 20;
    java.awt.Color EVENT_BORDER_COLOR = java.awt.Color.DARK_GRAY;
    java.awt.Color EVENT_STRING_COLOR = java.awt.Color.BLACK;
    java.awt.Font EVENT_STRING_FONT = new Font("Dialog", Font.PLAIN, 11);
    
    TierEventInterface tierEventInterface;
    
    /** Creates a new instance of TierEventViewer */
    public TierEventViewer(TierEventInterface tei) {
        tierEventInterface = tei;
    }

    public void drawContents() {
        super.drawContents();
        drawTiers();
    }
    
    void drawTiers(){
        int x1 = visibleRectangle.x;
        int x2 = visibleRectangle.x + visibleRectangle.width;        
        
        double time1 = getMilisecondForPixel(x1);
        double time2 = getMilisecondForPixel(x2);

        //draw the bounding box
        bufferedImageGraphics.setColor(super.TIMELINE_BACKGROUND_COLOR);
        bufferedImageGraphics.fillRect(0, getBufferHeight() - getHeightOfAdditionalViews(), x2-x1, getHeightOfAdditionalViews());

        // draw the boxes
        Vector<EventInterface[]> v = tierEventInterface.getTierEventData(time1/1000.0, time2/1000.0);
        int y = getBufferHeight() - getHeightOfAdditionalViews();
        for (EventInterface[] eis : v){
            for (EventInterface ei : eis){                
                double start = ei.getStartTime();
                double end = ei.getEndTime();
                int start_x = (int)(getPixelForMilisecond(start*1000.0) + 0.5);
                int end_x = (int)(getPixelForMilisecond(end*1000.0) + 0.5);
                bufferedImageGraphics.setColor(EVENT_BORDER_COLOR);
                bufferedImageGraphics.drawRect(start_x-x1, y, end_x-start_x, TIER_HEIGHT-2);
                bufferedImageGraphics.setColor(java.awt.Color.WHITE);
                bufferedImageGraphics.fillRect(start_x-x1+1, y+1, end_x-start_x-1, TIER_HEIGHT-3);
            }
            y+=TIER_HEIGHT;
        }
        
        
        // draw the strings
        y = this.getBufferHeight() - getHeightOfAdditionalViews();
        bufferedImageGraphics.setColor(EVENT_STRING_COLOR);
        bufferedImageGraphics.setFont(EVENT_STRING_FONT);        
        for (EventInterface[] eis : v){
            for (EventInterface ei : eis){
                double start = ei.getStartTime();
                double end = ei.getEndTime();
                int start_x = (int)(getPixelForMilisecond(start*1000.0) + 0.5);
                int end_x = (int)(getPixelForMilisecond(end*1000.0) + 0.5);
                java.awt.Shape theEventBox = new java.awt.Rectangle(start_x-x1, y, end_x-start_x, TIER_HEIGHT-2);
                bufferedImageGraphics.setClip(theEventBox);
                bufferedImageGraphics.drawString(ei.getDescription(), start_x-x1+2, y+TIER_HEIGHT-4);
            }
            y+=TIER_HEIGHT;
        }
    }

    public int getHeightOfAdditionalViews() {
        return super.getHeightOfAdditionalViews();
        //return TIER_HEIGHT*tierEventInterface.getNumberOfTiers();
    }

    
    
    
}
