/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging.swing;

import java.awt.Frame;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.exmaralda.partitureditor.jexmaraldaswing.OKCancelDialog;

/**
 *
 * @author thomas
 */
public class COMATreeTaggingDialog extends OKCancelDialog {

    public TreeTaggerParametersPanel  treeTaggerParametersPanel = new TreeTaggerParametersPanel();
    public TaggingOptionsPanel  taggingOptionsPanel = new TaggingOptionsPanel();

    public COMATreeTaggingDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle("Tree Tagging");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(treeTaggerParametersPanel);
        mainPanel.add(taggingOptionsPanel);
        getContentPane().add(mainPanel, SwingConstants.CENTER);
        this.pack();
    }



}
