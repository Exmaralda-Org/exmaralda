/*
 * TimelineViewer.java
 *
 * Created on 20. Maerz 2008, 11:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.timeview;

import java.awt.Color;
import java.awt.Font;
import org.exmaralda.folker.utilities.TimeStringFormatter;

/**
 *
 * @author thomas
 */
public class TimelineViewer extends AbstractTimeProportionalViewer {
    
    /** Creates a new instance of TimelineViewer */
    public TimelineViewer() {
    }

    Color TIMELINE_BACKGROUND_COLOR = new Color(255, 255, 128);

    Color BORDER_COLOR = Color.BLACK;

    Color BIG_TICKS_COLOR = Color.BLACK;

    Color SMALL_TICKS_COLOR = Color.BLUE;

    void drawTimeline() {
        bufferedImageGraphics.setColor(BORDER_COLOR);
        bufferedImageGraphics.drawLine(0, 0, visibleRectangle.width, 0);
        bufferedImageGraphics.drawLine(0, 40, visibleRectangle.width, 40);
        bufferedImageGraphics.setColor(TIMELINE_BACKGROUND_COLOR);
        bufferedImageGraphics.fillRect(0, 1, visibleRectangle.width, 39);
        double startTime = getLeftBoundaryTime();
        double endTime = getRightBoundaryTime();
        int firstVisibleTenthSecond = (int) Math.ceil(startTime / 100.0);
        int lastVisibleTenthSecond = (int) Math.floor(endTime / 100.0);
        bufferedImageGraphics.setColor(SMALL_TICKS_COLOR);
        for (int second = firstVisibleTenthSecond; second < lastVisibleTenthSecond + 1; second++) {
            int x = (int)(getPixelForMilisecond(second * 100) - visibleRectangle.x + 0.5);
            bufferedImageGraphics.drawLine(x, 1, x, 5);
            bufferedImageGraphics.drawLine(x, 35, x, 39);
        }
        int firstVisibleSecond = (int) Math.ceil(startTime / 1000.0);
        int lastVisibleSecond = (int) Math.floor(endTime / 1000.0);
        bufferedImageGraphics.setColor(BIG_TICKS_COLOR);
        int step = 1;
        if (getPixelsPerSecond()<35) step=2;
        if (getPixelsPerSecond()<25) step=3;
        if (getPixelsPerSecond()<15) step=4;
        for (int second = firstVisibleSecond; second < lastVisibleSecond + 1; second+=step) {
            int x = (int)(getPixelForMilisecond(second * 1000) - visibleRectangle.x + 0.5);
            bufferedImageGraphics.drawLine(x, 1, x, 12);
//            if (getPixelsPerSecond() >= 35) {
            int style = Font.PLAIN;
            if (second % 30 == 0) {
                style = Font.BOLD;
                bufferedImageGraphics.drawLine(x, 30, x, 39);
            }
            bufferedImageGraphics.setFont(bufferedImageGraphics.getFont().deriveFont(style));
            bufferedImageGraphics.drawString(TimeStringFormatter.formatSeconds(second), x, 25);
//            }
        }
    }

    public void drawContents() {
        drawTimeline();
    }
    
}
