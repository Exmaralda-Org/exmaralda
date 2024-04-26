/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.exmaralda.coma.dialogs.CategoriesPlusTypesDialog;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.corpusbuild.CategoryPlusType;
import org.exmaralda.common.corpusbuild.CollectTierCategoriesTypes;
import org.exmaralda.common.corpusbuild.CollectTypesInCategory;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaraldaswing.TypesDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 * 
 * @author thomas 
 */

// 26-04-2024
// as a side-effect of issue #475

public class CollectTypesAction extends ComaAction {

	ProgressBarDialog pbd;

	public CollectTypesAction(String text, ImageIcon icon, Coma c) {
            super(text, icon, c);
	}

        @Override
	public void actionPerformed(ActionEvent e) {
            try {
                final File file = coma.getData().getOpenFile();
                if (file == null) {
                    JOptionPane.showMessageDialog(coma, Ui.getText("err.noCorpusLoaded"));
                    return;
                }
                
                CollectTierCategoriesTypes preCollector = new CollectTierCategoriesTypes();
                preCollector.checkCorpus(coma.getData().getDocument(), file.getParent());
                Map<CategoryPlusType, Integer> categoryPlusTypeMap = preCollector.getCategoryPlusTypeMap();
                CategoriesPlusTypesDialog dialog = new CategoriesPlusTypesDialog(coma, true, categoryPlusTypeMap);
                dialog.setLocationRelativeTo(coma);
                dialog.setVisible(true);
                
                final String category = dialog.getSelectedCategory();                
                if (category==null){
                    JOptionPane.showMessageDialog(coma, "No category selected");
                    return;
                }
                final String type = dialog.getSelectedType();
                
                final CollectTypesInCategory collector = new CollectTypesInCategory(category, type);
                
                pbd = new ProgressBarDialog(coma, false);
                pbd.setLocationRelativeTo(coma);
                pbd.setTitle(Ui.getText("progress.collectTypes") + coma.getData().getOpenFile().getName());
                collector.addSearchListener(pbd);                
                pbd.setVisible(true);
                
                final Runnable doDisplaySaveDialog = new Runnable() {
                    @Override
                    public void run() {
                        displayEditDialog(collector, category, type);
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
            } catch (JDOMException | SAXException | JexmaraldaException | URISyntaxException ex) {
                Logger.getLogger(CollectTypesAction.class.getName()).log(Level.SEVERE, null, ex);
            }
	}

	private void displayEditDialog(CollectTypesInCategory collector, String category, String type) {
            pbd.setVisible(false);
            Map<String, Integer> typesTable = collector.getTypesTable();
            Tier dummyTier = new Tier("idx", null, category, type);
            TypesDialog dialog = new TypesDialog(coma, true);
            dialog.setData(typesTable, dummyTier);
            dialog.setLocationRelativeTo(coma);
            dialog.setVisible(true);
	}

}
