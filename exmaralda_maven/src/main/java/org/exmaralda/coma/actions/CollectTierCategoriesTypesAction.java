/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.exmaralda.coma.dialogs.CategoriesPlusTypesDialog;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.corpusbuild.CategoryPlusType;
import org.exmaralda.common.corpusbuild.CollectTierCategoriesTypes;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 * 
 * @author thomas 
 */

// 26-04-2024
// as a side-effect of issue #475

public class CollectTierCategoriesTypesAction extends ComaAction {

	ProgressBarDialog pbd;

	public CollectTierCategoriesTypesAction(String text, ImageIcon icon, Coma c) {
            super(text, icon, c);
	}

        @Override
	public void actionPerformed(ActionEvent e) {
            final File file = coma.getData().getOpenFile();
            if (file == null) {
		JOptionPane.showMessageDialog(coma, Ui.getText("err.noCorpusLoaded"));
		return;
            }
            final CollectTierCategoriesTypes collector = new CollectTierCategoriesTypes();

            pbd = new ProgressBarDialog(coma, false);
            pbd.setLocationRelativeTo(coma);
            pbd.setTitle(Ui.getText("progress.collectTierCategoriesTypes") + coma.getData().getOpenFile().getName());			
            collector.addSearchListener(pbd);
            
            pbd.setVisible(true);

            final Runnable doDisplaySaveDialog = new Runnable() {
                @Override
                public void run() {
                    displayEditDialog(collector);
                }
            };
            Thread checkThread = new Thread() {
                @Override
                public void run() {
                    try {
                        collector.checkCorpus(coma.getData().getDocument(), file.getParent());
                        javax.swing.SwingUtilities.invokeLater(doDisplaySaveDialog);
                    } catch (URISyntaxException | JexmaraldaException | JDOMException | SAXException ex) {
                        JOptionPane.showMessageDialog(coma, ex);
                        System.out.println(ex.getMessage());
                        pbd.setVisible(false);
                    }
                }
            };
            checkThread.start();
	}

	private void displayEditDialog(CollectTierCategoriesTypes collector) {
		pbd.setVisible(false);
            Map<CategoryPlusType, Integer> categoryPlusTypeMap = collector.getCategoryPlusTypeMap();
            CategoriesPlusTypesDialog dialog = new CategoriesPlusTypesDialog(coma, true, categoryPlusTypeMap);
            dialog.setLocationRelativeTo(coma);
            dialog.setVisible(true);
	}

}
