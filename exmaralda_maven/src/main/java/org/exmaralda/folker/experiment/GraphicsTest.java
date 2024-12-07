/*
 * GraphicsTest.java
 *
 * Created on 4. Maerz 2008, 14:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment;

import java.awt.Graphics;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import org.exmaralda.folker.timeview.ELANWaveSampler;
import org.exmaralda.folker.timeview.ELANWaveSamplerWrapper;
import java.io.*;
import javax.sound.sampled.*;


/**
 *
 * @author thomas
 */
public class GraphicsTest extends JComponent implements MouseListener, java.awt.event.MouseMotionListener{
    
    //String fn = "T:\\TP-E5\\Mitarbeiter\\Hatice\\Digitalisierungsarbeit\\auch auf der externen Festplatte\\EFE10\\EFE10tk_Akin_0663\\EFE10tk_Akin_0663a_f_SKO_240201.wav";
    String fn = "T:\\TP-Z2\\DATEN\\BEISPIELE\\EXMARaLDA_DemoKorpus\\Arbeitsamt\\Helge_Schneider_Arbeitsamt.wav";
    ELANWaveSampler ews;
    ELANWaveSamplerWrapper ewsw;    
    Rectangle lastVisibleRectangle = null;
    
    java.awt.image.BufferedImage bufferedImage;
    
    int SIZE=1000000;
    
    int lastX1 =-1;
    int lastX2 =-1;
    
    int selectionStart = -1;
    int selectionEnd = -1;

    /** Creates a new instance of GraphicsTest */
    public GraphicsTest() {
        try {
            ews = new ELANWaveSampler(fn);
            ewsw = new ELANWaveSamplerWrapper(ews);
            SIZE = (int)Math.ceil(ews.getDuration()/10.0);
            System.out.println("SIZE: " + SIZE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //ews.seekTime(1.0f);
        //ews.readInterval(1000000,2);
        setPreferredSize(new Dimension(SIZE,400));
        setMinimumSize(new Dimension(SIZE,400));
        setMaximumSize(new Dimension(SIZE,400));
        addMouseListener(this);
        addMouseMotionListener(this);
        this.setOpaque(true);
    }

    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        
        Rectangle visibleRectangle = getVisibleRect();
        Rectangle vr = visibleRectangle;        
        int x1 = vr.x;
        int x2 = vr.x + vr.width;

        System.out.println("Visible - X1: " + x1 + " X2: " + x2);
        
        //x1 = Math.max(0,x1-200);
        //x2 = Math.min(SIZE,x2+200);
        
        int x1_copy = x1;
        int x2_copy = x2;
        
        System.out.println("Interesting - X1: " + x1 + " X2: " + x2);
        
        /*bufferedImage = new BufferedImage(x2-x1,400,BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();*/
        
        g.setColor(Color.BLACK);
        /*if (lastX1>=0){
            if ((x2>lastX1) && (x2<lastX2)) x2 = lastX1;
            if ((x1>lastX1) && (x1<lastX2)) x1 = lastX2;
        }*/
        
        System.out.println("Drawing - X1: " + x1 + " X2: " + x2);
        System.out.println("=========================");
            
        float ms1 = (float)((float)x1/(float)SIZE) * ews.getDuration();
        float ms2 = (float)((float)x2/(float)SIZE) * ews.getDuration();
        long str = Math.round((double)((double)(x2-x1)/(double)SIZE) * ews.getNrOfSamples());        
        
        g.setColor(Color.WHITE);
        g.fillRect(x1,0, x2, getHeight());
        
        byte[][] values = ewsw.getValuesForPixels(x1,x2,SIZE,2);
        
        g.setColor(java.awt.Color.BLUE);        
        drawWaveform(values[0], values[1], x1, x2, 150, g);
        
        g.setColor(java.awt.Color.GREEN);
        drawWaveform(values[2], values[3], x1, x2, 350, g);
                

        for (int x=(int)Math.floor(x1/60000.0)*60000; x<(int)Math.ceil(x2/60000.0)*60000; x+=60000){
            g.drawString(Integer.toString(x/60000), x, 50);
            g.drawLine(x,50,x,200);
        }

        for (int x=(int)Math.floor(x1/1000.0)*1000; x<(int)Math.ceil(x2/1000.0)*1000; x+=1000){
            //g.drawString(Integer.toString(x), x, 50);
            g.drawLine(x,50,x,80);
        }
        
        g.setColor(java.awt.Color.RED);
        for (int x=(int)Math.floor(x1/100.0)*100; x<(int)Math.ceil(x2/100.0)*100; x+=100){
            g.drawLine(x,50,x,70);            
        }
        
        g.setColor(java.awt.Color.GREEN);
        for (int x=(int)Math.floor(x1/10.0)*10; x<(int)Math.ceil(x2/10.0)*10; x+=10){
            g.drawLine(x,50,x,60);            
        }
        
        if ((selectionStart>=0) && (selectionStart!=selectionEnd)){
            g.fillRect(selectionStart, 100, selectionEnd-selectionStart, 200);
        }
            

        lastX1 = x1_copy;
        lastX2 = x2_copy;        
        
    }

    
    synchronized void drawWaveform(byte[] samples1, byte[] samples2, int x1, int x2, int Y, Graphics g){
        int lastY = Y;
        int count=0;
        for (byte s : samples1){
            double perc = (double)(s)/100.0;
            int y = Y + (int)Math.round(50.0*perc);

            double perc2 = (double)(samples2[count])/100.0;
            int y2 = Y + (int)Math.round(50.0*perc2);

            g.drawLine(x1+count,lastY,x1+count+1,y);
            g.drawLine(x2+count+1,y,x2+count+1,y2);
            lastY=y2;
            count++;
        }
                
        g.setColor(java.awt.Color.BLACK);
        g.drawLine(x1,Y,x2,Y);

    }
    
    public void repaint(Rectangle r) {
        super.repaint(r);
    }

    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
    }

    public void repaint(int x, int y, int width, int height) {
        super.repaint(x, y, width, height);
    }

    public void repaint(long tm) {
        super.repaint(tm);
    }

    public void repaint() {
        super.repaint();
    }

    public void mousePressed(MouseEvent e) {
        System.out.println("Mouse pressed at " + e.getX() + " / " + e.getY());      
        selectionStart = e.getX();
        selectionEnd = e.getX();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {        
        System.out.println("Mouse dragged at " + e.getX() + " / " + e.getY());      
        selectionEnd = e.getX();
        this.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }
    
}
