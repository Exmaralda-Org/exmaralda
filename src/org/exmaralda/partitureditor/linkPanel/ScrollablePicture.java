/*
 * ScrollablePicture.java
 *
 * Created on 13. November 2003, 14:51
 */

package org.exmaralda.partitureditor.linkPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* ScrollablePicture.java is used by ScrollDemo.java. */

public class ScrollablePicture extends JLabel
                               implements Scrollable,
                                          MouseMotionListener {

    private int maxUnitIncrement = 1;
    private boolean missingPicture = false;

    public ScrollablePicture(ImageIcon i, int m) {
        super();
        //setPreferredSize(new Dimension(200,200));
        setText("");
        if (i!=null){
            this.setIcon(i);        
        }
        if (i == null) {
            missingPicture = true;
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            setBackground(Color.white);
        }
        maxUnitIncrement = m;

        //Let the user scroll by dragging to outside the window.
        addMouseMotionListener(this); //handle mouse drags
    }

    //Methods required by the MouseMotionListener interface:
    @Override
    public void mouseMoved(MouseEvent e) { }
    @Override
    public void mouseDragged(MouseEvent e) {
        //The user is dragging us, so scroll!
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
    }

    @Override
    public Dimension getPreferredSize() {
        if (missingPicture) {
            return new Dimension(20, 20);
        } else {
            return new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight());
            //return super.getPreferredSize();
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(200,200);
        //return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
        //Get the current position.
        int currentPosition;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                             (currentPosition / maxUnitIncrement)
                              * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                   * maxUnitIncrement
                   - currentPosition;
        }
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation,
                                           int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }
}

    
