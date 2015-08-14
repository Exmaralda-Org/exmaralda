/**
 *
 */
package org.exmaralda.coma.filters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.IconFactory;
import org.exmaralda.coma.root.Ui;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * @author woerner
 * 
 */
public class ComaFilter {
	Coma coma;

	public static final int COMMTYPE = 0;

	public static final int SPKTYPE = 1;
	
	private int filterType;

	protected ChangeEvent changeEvent = null;

	protected EventListenerList listenerList = new EventListenerList();

	private Preferences prefs = Ui.prefs;

	private String xpath;

	private boolean add;

	private boolean active;

	private boolean exclusive;

	private String name;

	private JPanel panel;

	private JTextField filterEditButton;

	private JToggleButton filterAddToggle;

	private JToggleButton filterActiveCheckbox;

	private JToggleButton filterInvertToggle;

	private JToggleButton filterRemoveToggle;

	private JButton filterSaveButton;

	private ComaFilter mySelf;

	public ComaFilter(Coma c, String xpath) {
		coma = c;
		mySelf = this;
		this.xpath = xpath;
		if (xpath.contains("/Communication")) {
			filterType=COMMTYPE;
		} else {
			filterType=SPKTYPE;
		}
		add = prefs.getBoolean("prefs.newFiltersAdd", false);
		active = prefs.getBoolean("prefs.newFiltersActive", true);
		exclusive = true;
	}

	public int getFilterType() {
		return filterType;
	}
	
	public void setEnabled(boolean b) {
		active = b;
	}

	public boolean isEnabled() {
		return active;
	}

	public void setIncluding(boolean b) {
		add = b;
	}

	public boolean isIncluding() {
		return add;
	}

	public boolean isInverted() {
		return !(exclusive);
	}

	public JPanel getPanel() {
		ImageIcon activeIcon = IconFactory.createImageIcon("switch-on.png");

		ImageIcon inactiveIcon = IconFactory.createImageIcon("switch-off.png");

		ImageIcon passIcon = IconFactory.createImageIcon("pass.png");

		ImageIcon blockIcon = IconFactory.createImageIcon("block.png");

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		filterEditButton = new JTextField(getDisplayName());
		filterEditButton.setFont(new Font("Dialog", 0, 12));
		filterEditButton.setHorizontalAlignment(JTextField.CENTER);
		filterEditButton.setToolTipText(this.toString());
		filterEditButton.setOpaque(true);
		filterEditButton
				.setBackground((Color) UIManager.get("Textfield.color"));
		filterEditButton.setBorder((Border) UIManager.get("Textfield.border"));

		filterEditButton.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				filterEditButton.setText(xpath.substring(xpath.indexOf("[")));
				filterEditButton.setBackground(Color.YELLOW);
				filterEditButton.setBorder((Border) UIManager
						.get("Textfield.border"));

			}

			public void focusLost(FocusEvent arg0) {
				filterEditButton.setText(getDisplayName());
				filterEditButton.setBackground((Color) UIManager
						.get("Textfield.color"));
				filterEditButton.setBorder((Border) UIManager
						.get("Textfield.border"));

			}
		});

		filterEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editFilter();
			}
		});

		panel.setBackground((active ? UIManager.getColor("Label.background")
				: new Color(204, 153, 153)));

		filterInvertToggle = new JToggleButton(passIcon);
		filterInvertToggle.setToolTipText(Ui.getText("cmd.filter.invert"));
		filterInvertToggle.setIcon(blockIcon);
		filterInvertToggle.setSelectedIcon(passIcon);
		filterInvertToggle.setSelected(exclusive);
		filterInvertToggle.setOpaque(false);
		filterInvertToggle.putClientProperty("JButton.buttonType", "segmented");
		filterInvertToggle.putClientProperty("JButton.segmentPosition",
				"middle");

		filterInvertToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exclusive = filterInvertToggle.isSelected();
				fireStateChanged();
			}
		});

		filterAddToggle = new JToggleButton("\u2229");
		filterAddToggle.setText(add ? "\u222a" : "\u2229");
		filterAddToggle.setToolTipText(Ui.getText("cmd.filter.andOr"));
		filterAddToggle.setOpaque(false);
		filterAddToggle.setSelected(add);
		filterAddToggle.putClientProperty("JButton.buttonType", "segmented");
		filterAddToggle.putClientProperty("JButton.segmentPosition", "last");
		filterAddToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add = filterAddToggle.isSelected();
				fireStateChanged();
			}
		});

		filterActiveCheckbox = new JToggleButton(activeIcon);
		filterActiveCheckbox.setToolTipText(Ui.getText("cmd.filter.active"));
		filterActiveCheckbox.setIcon(inactiveIcon);
		filterActiveCheckbox.setSelectedIcon(activeIcon);

		filterActiveCheckbox.setOpaque(false);
		filterActiveCheckbox.setSelected(active);
		filterActiveCheckbox.putClientProperty("JButton.buttonType",
				"segmented");
		filterActiveCheckbox.putClientProperty("JButton.segmentPosition",
				"first");

		filterActiveCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				active = filterActiveCheckbox.isSelected();
				fireStateChanged();
			}
		});

		filterSaveButton = new JButton("S");
		filterSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				coma.saveFilter(mySelf);
				active = filterActiveCheckbox.isSelected();
				fireStateChanged();
			}
		});

		JPanel foptions = new JPanel();
		foptions.setLayout(new BoxLayout(foptions, BoxLayout.X_AXIS));
		foptions.setOpaque(false);
		foptions.add(filterActiveCheckbox);
		foptions.add(filterInvertToggle);
		foptions.add(filterAddToggle);
		foptions.add(filterSaveButton);
		panel.add(filterEditButton, BorderLayout.CENTER);
		panel.add(foptions, BorderLayout.EAST);
		return panel;
	}

	/**
	 * 
	 */
	protected void editFilter() {
		filterEditButton.setBackground(Color.WHITE);
		String xpathOld = xpath;
		xpath = xpath.substring(0, xpath.indexOf("["))
				+ filterEditButton.getText();

		try {
			XPath xp = XPath.newInstance(isInverted() ? getXPathInverted()
					: getXPath());
		} catch (JDOMException err) {
			filterEditButton.setBackground(new Color(255, 0, 0));
			xpath = xpathOld;
			return;
		}

		filterEditButton.requestFocus(false);
		fireStateChanged();
	}

	@Override
	public String toString() {
		return xpath.substring(xpath.indexOf("["));
	}

	public String getXPath() {
		return xpath;
	}

	public String getDisplayName() {
		String displayName = exclusive ? "" : "!";
		if (xpath.toLowerCase().contains("count")) {
			return xpath.substring(xpath.indexOf("[") + 1, xpath.indexOf("]"));
		} else if (xpath.equals("//Corpus/CorpusData/Communication[1>0]")) {
			return ("EMPTY FILTER");
		} else {
			String[] splitString = xpath.split("\'");
			boolean out = false;
			for (String s : splitString) {
				if (out) {
					displayName += "[" + s + "]  ";
				}
				out = !out;
			}
		}
		return displayName;
	}

	public void addChangeListener(ChangeListener l) {

		if (listenerList.getListenerCount() == 0)
			listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireStateChanged() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent("FilterChanged");
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	/**
	 * @return
	 */
	public String getXPathInverted() {
		String xp = getXPath();
		String xpi = xp.substring(0, xp.indexOf("[")) + "[not("
				+ xp.substring(xp.indexOf("[") + 1, xp.lastIndexOf("]")) + ")]";
		return xpi;
	}
}
