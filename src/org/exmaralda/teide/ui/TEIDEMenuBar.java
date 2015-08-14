/**
 *
 */
package org.exmaralda.teide.ui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.exmaralda.teide.actions.PublishCorpusAction;
import org.exmaralda.teide.actions.PublishCorpusAction2;

/**
 * @author woerner
 *
 */
public class TEIDEMenuBar extends JMenuBar {
	public TEIDEMenuBar(TeideUI teide) {
		super();
		JMenu fileMenu = new JMenu(Loc.getText("menu.file"));
		fileMenu.setEnabled(false);
		JMenu editMenu = new JMenu(Loc.getText("menu.edit"));
		editMenu.setEnabled(false);
		JMenu toolsMenu = new JMenu(Loc.getText("menu.tools"));
		add(fileMenu);
		add(editMenu);
		add(toolsMenu);
		toolsMenu.add(new PublishCorpusAction2(teide));
		toolsMenu.add(new PublishCorpusAction(teide));
	}

	/**
	 * @return
	 */
}
