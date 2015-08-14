/**
 *
 */
package org.exmaralda.teide.ui;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;

/**
 * @author woerner
 * 
 */
public class HTMLDisplayer extends JPanel {
	private String text;

	static final int TYPE_EDITORKIT = 0;

	static final int TYPE_COBRA = 1;

	private int myType;

	private JEditorPane jEditorPane;

	private HtmlPanel htmlPanel;

	private LocalHtmlRendererContext rendererContext;

	public HTMLDisplayer(int type) {
		super(new BorderLayout());
		myType = type;
		if (myType == TYPE_COBRA) {
			System.out.println("FaIl");
			htmlPanel = new HtmlPanel();
			rendererContext = new LocalHtmlRendererContext(htmlPanel);
			System.out.println("FaIlx");
			htmlPanel.setPreferredWidth(800);
			//			this.add(new JScrollPane(htmlPanel));
			this.add(htmlPanel);
		}
		if (myType == TYPE_EDITORKIT) {
			jEditorPane = new JEditorPane();
			jEditorPane.setEditorKit(new HTMLEditorKit());
		}
		this.setVisible(true);
	}

	public void setText(String text) {
		this.text = text;
		if (myType == TYPE_COBRA) {
			htmlPanel.setHtml(text, null, rendererContext);
		}
		if (myType == TYPE_EDITORKIT) {
			jEditorPane.setText(text);
		}
	}

	public String getText() {
		return text;
	}

	private static class LocalHtmlRendererContext extends
			SimpleHtmlRendererContext {
		// Override methods here to implement browser functionality
		public LocalHtmlRendererContext(HtmlPanel contextComponent) {
			super(contextComponent);
		}
	}

}
