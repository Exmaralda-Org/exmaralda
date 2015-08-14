/*
 * TimeProportionalViewer.java
 *
 * Created on 10. Maerz 2008, 12:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.timeview;

import java.awt.*;
import java.awt.event.*; 
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import org.exmaralda.partitureditor.sound.*;
import java.util.*;
import org.exmaralda.folker.gui.TimeviewNavigationDialog;
import org.exmaralda.folker.utilities.TimeStringFormatter;

/**
 *
 * @author thomas
 */
public abstract class AbstractTimeProportionalViewer extends JComponent 
                                                implements  MouseListener,
                                                            MouseMotionListener,
                                                            MouseWheelListener,
                                                            PlayableListener {
    
    double tolerance = 50;

    boolean BUFFER_REDRAW_NECESSARY = true;
    
    
    BufferedImage bufferedImage;
    protected Graphics2D bufferedImageGraphics;
    protected Rectangle visibleRectangle = new Rectangle(0,0,0,0);
    
    private double pixelsPerSecond = 10.0;
    private double verticalMagnify = 1.0;

    int pixelWidth = 0;
    float soundDuration = 0.0f;
    int scrollAmount = 100;
    float MAX_SELECTION_SIZE_FOR_SHIFT = 5000.0f;
            
    final Object paintlock = new Object();
    
    Color BACKGROUND_COLOR = Color.WHITE;
    Color SELECTION_COLOR = new Color(128,255,255);
    Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    Cursor MOVE_LEFT_CURSOR = new Cursor(Cursor.W_RESIZE_CURSOR);
    Cursor MOVE_RIGHT_CURSOR = new Cursor(Cursor.E_RESIZE_CURSOR);
    float[] dash = { 3.0f };
    Stroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 3.0f, dash, 0.0f);
    Stroke PLAIN_STROKE = new BasicStroke(1.0f);
    
    AlphaComposite alpha04 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
    AlphaComposite alpha07 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);    
        
    private float selectionStartPixel = -1;
    private float selectionEndPixel = -1;
    private float cursorPositionPixel = -1;
    private float markPixel = -1;
    private float leftDragBoundaryPixel = -1;
    private float rightDragBoundaryPixel = -1;
    
    boolean leftDrag;
    boolean rightDrag;
    public boolean selectionAttached = false;
            
    Vector<TimeSelectionListener> timeSelectionListeners = new Vector<TimeSelectionListener>();

    public TimeviewNavigationDialog navigationDialog;

    
    /** Creates a new instance of TimeProportionalViewer */
    public AbstractTimeProportionalViewer() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setAutoscrolls(true);
        setToolTipText("00:00.00");
        navigationDialog = new TimeviewNavigationDialog((JFrame)(getTopLevelAncestor()), false, this);
    }

            
    public void setSoundFile(String soundFilePath) throws IOException{
        // changed 28-04-2009
        AbstractPlayer player = null;

        try {
            player = new JMFPlayer();
            player.setSoundFile(soundFilePath);
        } catch (Exception ioe){
            String os = System.getProperty("os.name").substring(0,3);
            if (os.equalsIgnoreCase("mac")) {
                try {
                    //player = new QuicktimePlayer();
                    player = new CocoaQTPlayer();
                    player.setSoundFile(soundFilePath);
                } catch (Exception ex) {
                    throw new IOException("No available player can open this file");
                }
            } else if (os.equalsIgnoreCase("win")){
                try {
                    //player = new ELANDSPlayer();
                    player = new JDSPlayer();
                    player.setSoundFile(soundFilePath);
                } catch (IOException ex) {
                    //if (QuicktimePlayer.isQuicktimeAvailable()){
                            //player = new QuicktimePlayer();
                    try {
                        player = new MMFPlayer();
                        player.setSoundFile(soundFilePath);
                    } catch (Exception ex2){
                        throw new IOException("No available player can open this file");                        
                    }
                   //}
                }
            } else {
                throw new IOException(ioe);
            }
        }
        soundDuration = (float)(player.getTotalLength() * 1000.0f);
        pixelWidth = (int)Math.ceil(soundDuration/1000.f*getPixelsPerSecond());
        player = null;
        reset();
        repaint();
    }
    
    public void reset(){
        selectionStartPixel = -1;
        selectionEndPixel = -1;
        cursorPositionPixel = -1;        
        resetDragBoundaries();
        scrollRectToVisible(new java.awt.Rectangle(0,0,1,1));
    }
    
    /***************************/
    /* TIME <-> PIXEL METHODS **/
    /***************************/       
    
    public double getMilisecondForPixel(float pixel){
        if (this.soundDuration<=0) return -1;
        return ((double)pixel/pixelWidth)*soundDuration;
    }

    public float getPixelForMilisecond(double milisecond){
        if (this.soundDuration<=0) return -1;
        return (float)(milisecond/soundDuration*pixelWidth);
    }
    
    public double getLeftBoundaryTime(){
        return getMilisecondForPixel(visibleRectangle.x);
    }
    
    public double getRightBoundaryTime(){
        return getMilisecondForPixel(visibleRectangle.x + visibleRectangle.width);
    }
    
    public double getSelectionStartTime(){
        return getMilisecondForPixel(getSelectionStartPixel());
    }
    
    public double getSelectionEndTime(){
        return getMilisecondForPixel(getSelectionEndPixel());
    }
    
    public double getCursorTime(){
        return getMilisecondForPixel(getCursorPositionPixel());
    }

    public double getMarkTime(){
        return getMilisecondForPixel(getMarkPixel());
    }

    /************************/
    /**** DRAWING METHODS **/
    /***********************/    

    public int getBufferHeight(){
        return visibleRectangle.height;
    }
    
    public void paintBuffer(){
        synchronized (paintlock) {
            bufferedImage = new BufferedImage(visibleRectangle.width, getBufferHeight(), BufferedImage.TYPE_INT_RGB);
            bufferedImageGraphics = bufferedImage.createGraphics();
            
            bufferedImageGraphics.setColor(BACKGROUND_COLOR);
            bufferedImageGraphics.fillRect( 0, 0, visibleRectangle.width, getBufferHeight());
            
            if (soundDuration > 0){
                drawContents();
            }
        }        
    }
    
    public abstract void drawContents();

    public boolean isBufferRedrawNecessary(){
        return BUFFER_REDRAW_NECESSARY;
    }
    
    /************************/
    /* SUPERCLASS METHODS **/
    /***********************/    
    
    @Override
    protected void paintComponent(Graphics g) {
        visibleRectangle = getVisibleRect();
        if (isBufferRedrawNecessary()){
            paintBuffer();
        }
        
        g.drawImage(bufferedImage, visibleRectangle.x, visibleRectangle.y, this);
        
        // draw the selection
        ((Graphics2D)g).setComposite(alpha04);
        g.setColor(SELECTION_COLOR);
        if (getSelectionEndPixel()>getSelectionStartPixel()){
            g.fillRect((int)(getSelectionStartPixel()+0.5), 0, (int)(getSelectionEndPixel()-getSelectionStartPixel()+0.5), getHeight());
        } else if (getSelectionEndPixel()<getSelectionStartPixel()){
            g.fillRect((int)(getSelectionEndPixel()+0.5), 0, (int)(getSelectionStartPixel()-getSelectionEndPixel()+0.5), getHeight());
        }
                
        // draw boundaries
        if (getSelectionStartPixel()!=getSelectionEndPixel()){            
            if (selectionAttached){
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.BLUE);
            }
            g.fillRect((int)(getSelectionStartPixel()-2+0.5),0,5,getHeight());
            if (selectionAttached){
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.BLUE);
            }
            g.fillRect((int)(getSelectionEndPixel()-2+0.5),0,5,getHeight());
            
            g.setColor(Color.BLACK);
            g.drawLine((int)(getSelectionStartPixel()+0.5), 0, (int)(getSelectionStartPixel()+0.5), getHeight());
            g.drawLine((int)(getSelectionEndPixel()+0.5), 0, (int)(getSelectionEndPixel()+0.5), getHeight());
        }
        
        // draw drag boundaries
        g.setColor(java.awt.Color.DARK_GRAY);
        ((Graphics2D)g).setStroke(DOTTED_STROKE);
        g.drawLine((int)(leftDragBoundaryPixel+0.5),0, (int)(leftDragBoundaryPixel+0.5),getHeight());
        g.drawLine((int)(rightDragBoundaryPixel+0.5),0, (int)(rightDragBoundaryPixel+0.5),getHeight());
        
        ((Graphics2D)g).setStroke(PLAIN_STROKE);

        // draw the cursor
        if (getCursorPositionPixel()>=0){
            g.setColor(Color.BLACK);
            g.drawLine((int)(getCursorPositionPixel()+0.5), 0, (int)(getCursorPositionPixel()+0.5), getHeight());            
        }
        
        // draw a nupsi in the middle of the screen
        g.setColor(Color.GREEN);
        g.fillRect(visibleRectangle.x + visibleRectangle.width/2 -2, 0, 4, 5);

        // draw the mark
        if (getMarkPixel()>=0){
            g.setColor(Color.RED);
            Polygon markTriangle = new Polygon();
            markTriangle.addPoint((int)(getMarkPixel() - 5.5), 0);
            markTriangle.addPoint((int)(getMarkPixel() + 5.5), 0);
            markTriangle.addPoint((int)(getMarkPixel()), 6);
            g.fillPolygon(markTriangle);
        }
    }        
        
    @Override
    public Dimension getPreferredSize() {
        Dimension retValue;        
        retValue = super.getPreferredSize();
        return new java.awt.Dimension(pixelWidth, retValue.height);
    }
    
    @Override
    public String getToolTipText(MouseEvent event) {
        if (event.getY()>40) return null;
        int pixelLocation = event.getX();        
        double miliseconds = getMilisecondForPixel(pixelLocation);        
        return TimeStringFormatter.formatMiliseconds(miliseconds,2);
    }

    /****************************/
    /* MOUSE LISTENER METHODS **/
    /***************************/
    
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.requestFocus();

        if (SwingUtilities.isLeftMouseButton(e)){
            // check if a boundary is being dragged
            Rectangle leftMover = new Rectangle((int)(getSelectionStartPixel()-2+0.5),0,5,getHeight());
            Rectangle rightMover = new Rectangle((int)(getSelectionEndPixel()-2+0.5),0,5,getHeight());
            if (leftMover.contains(e.getPoint())){
                leftDrag = true;
                rightDrag = false;
                return;
            } else if (rightMover.contains(e.getPoint())){
                leftDrag = false;
                rightDrag = true;
                return;
            }

            //if no boundary is dragged, remove current selection
            leftDrag = false;
            rightDrag = false;
            selectionAttached = false;
            resetDragBoundaries();
            setSelectionStartPixel(e.getX());
            setSelectionEndPixel(selectionStartPixel);
            BUFFER_REDRAW_NECESSARY = false;
            repaint();
            BUFFER_REDRAW_NECESSARY = true;
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        float selectionWidth = selectionEndPixel - selectionStartPixel;
        Rectangle leftMover = new Rectangle((int)(getSelectionStartPixel()-2+0.5),0,5,getHeight());
        Rectangle rightMover = new Rectangle((int)(getSelectionEndPixel()-2+0.5),0,5,getHeight());
        if (leftMover.contains(e.getPoint())){
            setCursor(MOVE_LEFT_CURSOR);
        } else if (rightMover.contains(e.getPoint())){
            setCursor(MOVE_RIGHT_CURSOR);
        } else {
            if (selectionStartPixel!=selectionEndPixel){
                Rectangle selectionCenter = new Rectangle((int)(selectionStartPixel+selectionWidth/3),0, (int)(selectionWidth/3), getHeight());
                if (selectionCenter.contains(e.getPoint())){
                    setCursor(HAND_CURSOR);
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //setCursor(MOVE_CURSOR);
        int x = e.getX();
        if (SwingUtilities.isLeftMouseButton(e)){
            // change 03-02-2009: Do not let the selection become smaller than the tolerance
            // do not come closer to the drag boundaries than the tolerance
            float tolerancePixel = getPixelForMilisecond(tolerance) + 1;
            if (!leftDrag){
                if ((x > selectionStartPixel + tolerancePixel) && (x<rightDragBoundaryPixel - tolerancePixel)){
                    moveSelectionEndPixel(x);
                }
            } else {
                if ((x < selectionEndPixel - tolerancePixel) && (x>leftDragBoundaryPixel + tolerancePixel)){
                    moveSelectionStartPixel(x);
                }
            }
            if (x>=visibleRectangle.x && x<=visibleRectangle.x+visibleRectangle.width){
                BUFFER_REDRAW_NECESSARY = false;
                repaint();
                BUFFER_REDRAW_NECESSARY = true;
            } else {
                Rectangle r = new Rectangle(e.getX(), 1, 1, 1);
                scrollRectToVisible(r);
                repaint();
            }
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getClickCount()==1) && (SwingUtilities.isLeftMouseButton(e))){
            setCursorPositionPixel(e.getX());
        } else if ((e.getClickCount()==2) && (SwingUtilities.isRightMouseButton(e))){
            shiftSelection();
        } else if ((e.getClickCount()==2)  && (SwingUtilities.isLeftMouseButton(e))){
            if (getMarkPixel()<0){
                setMarkPixel(e.getX());
            } else {
                double markPixelMs = getMilisecondForPixel(getMarkPixel());
                double otherMs = getMilisecondForPixel(e.getX());
                if (markPixelMs<otherMs){
                    setSelectionInterval(markPixelMs, otherMs, false);
                } else if (otherMs<markPixelMs){
                    setSelectionInterval(otherMs, markPixelMs, false);
                }
                setMarkPixel(-1);
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int delta = e.getWheelRotation();
        if (e.isControlDown() && e.isShiftDown()){
            //System.out.println("Changing Magnify");
            changeMagnify(-delta);
        } else if (e.isControlDown()){
            changeZoom(-delta);
        } else {
            if (selectionStartPixel==selectionEndPixel) return;
            int x = e.getX();
            float a = selectionStartPixel + (selectionEndPixel - selectionStartPixel)/3;
            float b = selectionEndPixel - (selectionEndPixel - selectionStartPixel)/3;
            if ((a<=x) && (x<=b)){
                moveSelection(delta);
            } else if (x<a){
                fineTuneSelectionStart(delta);
            } else {
                fineTuneSelectionEnd(delta);
            }
        }
    }


    /************************/
    /* GETTERS AND SETTERS **/
    /***********************/
    
    public double getVerticalMagnify(){
        return verticalMagnify;
    }
    
    public void setVerticalMagnify(double vm){
        verticalMagnify = vm;
        System.out.println(vm + " is new VM");
        repaint();
    }

    public double getPixelsPerSecond() {
        return pixelsPerSecond;
    }

    public double getSecondsPerPixel() {
        return 1.0/pixelsPerSecond;
    }

    public void setPixelsPerSecond(double pps) {
        double timeAtScreenCenter = getMilisecondForPixel(visibleRectangle.x + visibleRectangle.width/2);        
        double selectionStartTime = getMilisecondForPixel(getSelectionStartPixel());
        double selectionEndTime = getMilisecondForPixel(getSelectionEndPixel());
        double leftDragBoundaryTime = getMilisecondForPixel(leftDragBoundaryPixel);
        double rightDragBoundaryTime = getMilisecondForPixel(rightDragBoundaryPixel);
        double cursorTime = getMilisecondForPixel(cursorPositionPixel);
        
        pixelsPerSecond = pps;
        pixelWidth = (int)Math.ceil(soundDuration/1000.f*getPixelsPerSecond());             
        setSize(getPreferredSize());        
        float newPixelAtScreenCenter = getPixelForMilisecond(timeAtScreenCenter);
        if (selectionStartPixel>=0) selectionStartPixel = getPixelForMilisecond(selectionStartTime);
        if (selectionEndPixel>=0) selectionEndPixel = getPixelForMilisecond(selectionEndTime);
        if (cursorPositionPixel>=0) cursorPositionPixel = getPixelForMilisecond(cursorTime);
        leftDragBoundaryPixel = getPixelForMilisecond(leftDragBoundaryTime);
        rightDragBoundaryPixel = getPixelForMilisecond(rightDragBoundaryTime);
        
        repaint();
        if (selectionStartPixel==selectionEndPixel){
            Rectangle r = new Rectangle((int)(newPixelAtScreenCenter - visibleRectangle.width/2 + 0.5), 
                                        0, 
                                        visibleRectangle.width,1);        

            scrollRectToVisible(r);                    
        } else {
            makeSelectionVisible();
        }

        fireZoomChanged();
    }

    private void changeZoom(int delta) {
        double newPPS = Math.max(10,getPixelsPerSecond() + delta * 10);
        setPixelsPerSecond(newPPS);
        fireZoomChanged();
    }

    private void changeMagnify(int delta){
        //double newMag = Math.max(0.7, getVerticalMagnify() + delta * 0.1);
        double newMag = Math.max(0.25, getVerticalMagnify() + delta * 0.1);
        setVerticalMagnify(newMag);
    }

    public boolean isSelectionAttached() {
        return selectionAttached;
    }

    public void setSelectionAttached(boolean selectionAttached) {
        this.selectionAttached = selectionAttached;
    }

    public float getSelectionStartPixel() {
        return selectionStartPixel;
    }

    public void setSelectionStartPixel(float ssp) {
        selectionStartPixel = ssp;
        cursorPositionPixel = ssp;
        fireTimeSelectionEvent(makeTimeSelectionEvent());
    }

    public void moveSelectionStartPixel(float ssp) {
        selectionStartPixel = ssp;
        cursorPositionPixel = ssp;
        fireStartTimeMoved();
    }

    public float getSelectionEndPixel() {
        return selectionEndPixel;
    }

    public void setSelectionEndPixel(float sep) {
        if (sep>=pixelWidth){
            selectionEndPixel = pixelWidth;
        } else {
            selectionEndPixel = sep;
        }
        fireTimeSelectionEvent(makeTimeSelectionEvent());
    }
    
    public void moveSelectionEndPixel(float sep) {
        selectionEndPixel = sep;
        fireEndTimeMoved();
    }
    
    public float getCursorPositionPixel() {
        return cursorPositionPixel;
    }

    public void setCursorPositionPixel(float cpp) {
        cursorPositionPixel = cpp;
        // i.e. cursor has moved outside the visible area
        if ((cpp>=0) && ((cpp<visibleRectangle.x) || (cpp>visibleRectangle.x+visibleRectangle.width))){
            Rectangle r = new Rectangle((int)(cpp-scrollAmount+0.5), 1, scrollAmount*2, 1);
            scrollRectToVisible(r);
            repaint();            
        } else {
            BUFFER_REDRAW_NECESSARY = false;
            repaint();
            BUFFER_REDRAW_NECESSARY = true;            
        }        
    }

    public float getMarkPixel() {
        return markPixel;
    }


    public void setMarkPixel(int x) {
        markPixel = x;
        BUFFER_REDRAW_NECESSARY = false;
        repaint();
        BUFFER_REDRAW_NECESSARY = true;
        if (markPixel<0) {
            fireMarkRemoved();
        } else {
            fireMarkSet();
        }
    }

    
    public void setCursorTime(double time){
        setCursorPositionPixel(getPixelForMilisecond(time));
    }
    
    public void setLeftDragBoundary(double miliSeconds){
        leftDragBoundaryPixel = this.getPixelForMilisecond(miliSeconds);
    }
    
    public void setRightDragBoundary(double miliSeconds){
        rightDragBoundaryPixel = this.getPixelForMilisecond(miliSeconds);
    }

    public void resetDragBoundaries(){
        leftDragBoundaryPixel = 0;
        rightDragBoundaryPixel = getWidth();
        //rightDragBoundaryPixel = this.getPixelForMilisecond(this.getPreferredSize().width);
        //System.out.println("Reset: " + leftDragBoundaryPixel + " " + rightDragBoundaryPixel);
    }
    
    public void setSelectionInterval(double startMiliseconds, double endMiliseconds, boolean attached){
        this.setSelectionInterval(startMiliseconds, endMiliseconds, attached, true);
    }
    
    public void setSelectionInterval(double startMiliseconds, double endMiliseconds, boolean attached, boolean makeVisible){
        selectionStartPixel = getPixelForMilisecond(startMiliseconds);
        cursorPositionPixel = selectionStartPixel;
        selectionAttached = attached;
        //selectionEndPixel = getPixelForMilisecond(endMiliseconds);
        setSelectionEndPixel(getPixelForMilisecond(endMiliseconds));
        if (makeVisible){
            makeSelectionVisible();
        }
    }
    
    public void removeSelection(){
        selectionStartPixel = -1.0f;
        selectionEndPixel = -1.0f;
        this.resetDragBoundaries();
        selectionAttached = false;
        repaint();
    }
    
    public void shiftSelection(){
        if ((getSelectionEndPixel()<0)||(getSelectionStartPixel()<0)) return;
        float selectionSize = getSelectionEndPixel() - getSelectionStartPixel();
        selectionAttached = false;
        selectionStartPixel=selectionEndPixel;
        setSelectionEndPixel(selectionEndPixel+selectionSize);
        makeSelectionVisible();
        repaint();
    }
    
    public void detachSelection(){
        selectionAttached = false;
        repaint();
        fireSelectionDetached();
    }

    public void fineTuneSelectionStart(int pixel){
        float x = Math.max(0, getSelectionStartPixel()+pixel);
        if ((x < selectionEndPixel) && (x>leftDragBoundaryPixel)){
            moveSelectionStartPixel(x);
            repaint();
            fireStartTimeMoved();
        }
    }
    
    public void fineTuneSelectionEnd(int pixel){
        float x = Math.min(getWidth(), getSelectionEndPixel()+pixel);
        if ((x > selectionStartPixel) && (x<rightDragBoundaryPixel)){
            moveSelectionEndPixel(x);
            repaint();
            fireEndTimeMoved();
        }
    }

    public void moveSelection(int pixel) {
        float x1 = Math.max(0, getSelectionStartPixel()+pixel);
        float x2 = Math.min(getWidth(), getSelectionEndPixel()+pixel);
        if ((x1>leftDragBoundaryPixel) && (x2<rightDragBoundaryPixel)){
            moveSelectionStartPixel(Math.max(0, getSelectionStartPixel()+pixel));
            moveSelectionEndPixel(Math.min(getWidth(), getSelectionEndPixel()+pixel));
            repaint();
        }
    }




    
    /************************/
    /* CONVENIENCE METHODS **/
    /***********************/
    
    public void centerTime(double time) {
        float selectionCentre = getPixelForMilisecond(time);
        Rectangle r = new Rectangle((int)(selectionCentre-visibleRectangle.width/2+0.5),0, visibleRectangle.width, 0);
        scrollRectToVisible(r);
        repaint();
    }

    private void makeSelectionVisible(){
        /*int selectionSize = selectionEndPixel - selectionStartPixel;
        if (selectionSize<visibleRectangle.width){
            selectionSize+=Math.min(50, visibleRectangle.width-selectionSize);
        }
        Rectangle r = new Rectangle(selectionStartPixel,0,selectionSize,0);*/
        float selectionCentre = (selectionEndPixel + selectionStartPixel)/2;
        Rectangle r = new Rectangle((int)(selectionCentre-visibleRectangle.width/2+0.5),0, visibleRectangle.width, 0);
        scrollRectToVisible(r);        
        repaint();                
    }
    
    private TimeSelectionEvent makeTimeSelectionEvent(){
        double start = getMilisecondForPixel(selectionStartPixel);
        double end = getMilisecondForPixel(selectionEndPixel);
        if (end<start){
            double m = start;
            start = end;
            end = m;
        }
        return new TimeSelectionEvent(start,end, TimeSelectionEvent.SELECTION_CHANGED, selectionAttached);
    }
    

    /********************/
    /* LISTENER METHODS */
    /********************/
    
    
    public void addTimeSelectionListener(TimeSelectionListener tsl){
        timeSelectionListeners.add(tsl);
    }
    
    public void removeTimeSelectionListener(TimeSelectionListener tsl){
        timeSelectionListeners.remove(tsl);
    }
    
    public void fireTimeSelectionEvent(TimeSelectionEvent tse){
        for (TimeSelectionListener tsl : timeSelectionListeners){
            tsl.processTimeSelectionEvent(tse);
        }
    }
    
    public void fireMarkSet(){
        double time = getMilisecondForPixel(getMarkPixel());
        TimeSelectionEvent ev = new TimeSelectionEvent(time, time, TimeSelectionEvent.MARK_SET, false);
        fireTimeSelectionEvent(ev);
    }

    public void fireMarkRemoved(){
        TimeSelectionEvent ev = new TimeSelectionEvent(-1, -1, TimeSelectionEvent.MARK_REMOVED, false);
        fireTimeSelectionEvent(ev);
    }

    public void fireStartTimeMoved(){
        TimeSelectionEvent ev = makeTimeSelectionEvent();
        ev.setType(TimeSelectionEvent.START_TIME_CHANGED);
        fireTimeSelectionEvent(ev);
    }

    public void fireSelectionDetached(){
        TimeSelectionEvent ev = makeTimeSelectionEvent();
        ev.setType(TimeSelectionEvent.SELECTION_DETACHED);
        fireTimeSelectionEvent(ev);
    }

    public void fireZoomChanged() {
        TimeSelectionEvent ev = makeTimeSelectionEvent();
        ev.setType(TimeSelectionEvent.ZOOM_CHANGED);
        fireTimeSelectionEvent(ev);
    }

    
    public void fireEndTimeMoved(){
        TimeSelectionEvent ev = makeTimeSelectionEvent();
        ev.setType(TimeSelectionEvent.END_TIME_CHANGED);
        fireTimeSelectionEvent(ev);        
    }

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        if (e.getType() == PlayableEvent.POSITION_UPDATE){
            double seconds = e.getPosition();
            float pixel = getPixelForMilisecond(1000*seconds);
            setCursorPositionPixel(pixel);
            BUFFER_REDRAW_NECESSARY = false;
            repaint();
            BUFFER_REDRAW_NECESSARY = true;            
        } else if ((e.getType() == PlayableEvent.PLAYBACK_STOPPED)){
            /*if (this.getSelectionStartPixel()>=0){
                setCursorPositionPixel(getSelectionStartPixel());
            } else {
                setCursorPositionPixel(0);
            }*/
            BUFFER_REDRAW_NECESSARY = false;
            repaint();
            BUFFER_REDRAW_NECESSARY = true;                        
        }
    }






    
    
}
