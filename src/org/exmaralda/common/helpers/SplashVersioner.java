package org.exmaralda.common.helpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SplashVersioner extends Task {

    private String major;
    private String minor;

    public void execute() throws BuildException {
	int x = 328;
	int y = 130;
	BufferedImage img = null;
	try {
	    img = ImageIO
		    .read(new File(
			    "/Users/kai/Java/EXMARaLDA/src/org/exmaralda/coma/resources/images/splash_clean.png"));
	} catch (IOException e) {
	    System.exit(0);
	}
	Graphics g = img.getGraphics();
	g.setColor(Color.BLACK);
	Graphics2D g2d = (Graphics2D) g;
	Font majorFont = new Font("Calibri", Font.BOLD, 46);
	Font minorFont = new Font("Calibri", Font.PLAIN, 20);
	FontMetrics metrics = g2d.getFontMetrics(majorFont);
	g2d.setFont(majorFont);
	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	g2d.drawString(major, x, y);
	if (minor != null) {
	    g2d.setFont(minorFont);
	    g2d.setColor(Color.RED);
	    g2d.drawString(" preview " + minor, x + metrics.stringWidth(major),
		    y);
	}
	try {
	    ImageIO
		    .write(
			    img,
			    "png",
			    new File(
				    "/Users/kai/Java/EXMARaLDA/src/org/exmaralda/coma/resources/images/splash.png"));
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void setMajor(String ver) {
	this.major = ver;
    }

    public void setMinor(String ver) {
	this.minor = ver;
    }

}
