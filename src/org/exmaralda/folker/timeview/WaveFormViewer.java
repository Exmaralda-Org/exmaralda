/*
 * WaveFormViewer.java
 *
 * Created on 10. Maerz 2008, 12:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.timeview;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;




/**
 *
 * @author thomas
 */
public class WaveFormViewer extends TimelineViewer{
    
    ELANWaveSamplerWrapper waveSamplerWrapper;
    
    Color WAVEFORM_COLOR = Color.GRAY;
    
    short numberOfChannels;
    
    /** Creates a new instance of WaveFormViewer */
    public WaveFormViewer() {
    }
    
    public void setSoundFile(String soundFilePath) throws IOException{
        ELANWaveSampler ews = new ELANWaveSampler(soundFilePath);
        waveSamplerWrapper = new ELANWaveSamplerWrapper(ews);
        soundDuration = ews.getDuration();
        numberOfChannels = ews.getWavHeader().getNumberOfChannels();
        pixelWidth = (int)Math.ceil(soundDuration/1000.f*getPixelsPerSecond());
        reset();
        repaint();        
    }

    
    public void drawContents() {
        super.drawContents();
        int x1 = visibleRectangle.x;
        int x2 = visibleRectangle.x + visibleRectangle.width;        

        // changed this in case we have a mono file...
        int NR_OF_CHANNELS = (int)numberOfChannels;
        byte[][] values = waveSamplerWrapper.getValuesForPixels(x1,x2,pixelWidth,NR_OF_CHANNELS);

        drawWaveform(values);
    }
    
    public int getHeightOfAdditionalViews(){
        return 0;
    }
    
    void drawWaveform(byte[][] values){
        bufferedImageGraphics.setColor(WAVEFORM_COLOR);
        int height = this.getHeight()- 40 - getHeightOfAdditionalViews();        
        if (values.length==2){
            int baseline = 40 + height/2;
            int lastY = baseline;
            int count=0;
            for (byte s : values[0]){
                double perc = (double)(s)/100.0;
                //int y = baseline + (int)(getVerticalMagnify()*Math.round(height/2.0*perc));
                int y = baseline + (int)Math.round(height/getVerticalMagnify()/2.0*perc);
                double perc2 = (double)(values[1][count])/100.0;
                //int y2 = baseline + (int)(getVerticalMagnify()*Math.round(height/2.0*perc2));
                int y2 = baseline + (int)Math.round(height/getVerticalMagnify()/2.0*perc2);
                
                bufferedImageGraphics.drawLine(count,lastY,count+1,y);
                bufferedImageGraphics.drawLine(count+1,y,count+1,y2);
                lastY=y2;
                count++;
            }
        } else if (values.length==4){
            int baseline1 = 40 + height/4;
            int baseline2 = 40 + 3*height/4;
            int lastY1 = baseline1;
            int lastY2 = baseline2;
            int count=0;
            for (byte s : values[0]){
                double perc = (double)(s)/100.0;
                //int y = baseline1 + (int)(getVerticalMagnify()*Math.round(height/4.0*perc));
                int y = baseline1 + (int)Math.round(height/getVerticalMagnify()/4.0*perc);
                double perc2 = (double)(values[1][count])/100.0;
                //int y2 = baseline1 + (int)(getVerticalMagnify()*Math.round(height/4.0*perc2));
                int y2 = baseline1 + (int)Math.round(height/getVerticalMagnify()/4.0*perc2);
                bufferedImageGraphics.drawLine(count,lastY1,count+1,y);
                bufferedImageGraphics.drawLine(count+1,y,count+1,y2);
                lastY1=y2;
                
                double perc3 = (double)(values[2][count])/100.0;
                //int y3 = baseline2 + (int)(getVerticalMagnify()*Math.round(height/4.0*perc3));
                int y3 = baseline2 + (int)Math.round(height/getVerticalMagnify()/4.0*perc3);
                double perc4 = (double)(values[3][count])/100.0;
                //int y4 = baseline2 + (int)(getVerticalMagnify()*Math.round(height/4.0*perc4));
                int y4 = baseline2 + (int)Math.round(height/getVerticalMagnify()/4.0*perc4);
                bufferedImageGraphics.drawLine(count,lastY2,count+1,y3);
                bufferedImageGraphics.drawLine(count+1,y3,count+1,y4);
                lastY2=y4;
                
                
                count++;
            }
        }

    }

    
   
    
    
    
    
    
}
