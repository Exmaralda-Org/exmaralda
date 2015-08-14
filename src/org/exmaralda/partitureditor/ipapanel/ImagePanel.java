/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.ipapanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.*;

/**
 *
 * @author Thomas
 */
class ImagePanel extends JPanel {

  private Image img;

      public ImagePanel(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
      }

      public ImagePanel(ImageIcon img) {
        this.img = img.getImage();
        Dimension size = new Dimension(img.getImage().getWidth(null), img.getImage().getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
      }

      @Override
      public void paintComponent(Graphics g) {
         g.drawImage(img, 0, 0, null);
      }

}