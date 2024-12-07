package org.exmaralda.sextanttagger.types;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.exmaralda.sextanttagger.ui.TaggerUI;

public class TagOptionDialog extends JDialog {

	private JTextField key;
	private JTextField value;
	private JButton colorButton;
	private TagOption option;
	private JLabel shortcutLabel;
	private JToggleButton selectShortcutButon;
	private KeyListener kl;
	private JLabel colorLabel;

	TagOption myOption;
	private JButton okButton;
	private JButton cancelButton;
	private boolean cancelled;

	public TagOptionDialog(TaggerUI ui, TagOption o) {
		super(ui, "edit tagging option", true);
		setOption(o);
		myOption = new TagOption(o.getKey(), o.getValue(), o.getColor(), o
				.getShortcut(), o.getType());
		getContentPane().setLayout(new GridLayout(0, 2));
		key = new JTextField(o.getKey());
		value = new JTextField(o.getValue());
		colorLabel = new JLabel("Color: ");
		colorLabel.setOpaque(true);
		colorLabel.setBackground(getOption().getColor());
		colorLabel.setForeground(new Color(255 - getOption().getColor()
				.getRed(), 255 - getOption().getColor().getGreen(),
				255 - getOption().getColor().getBlue()));
		colorButton = new JButton("choose...");
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chooseColor();
			}
		});
		shortcutLabel = new JLabel("shortcut: "
				+ KeyboardShortcut.getLabel(o.getShortcut()));
		selectShortcutButon = new JToggleButton("select...");
		selectShortcutButon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chooseButton();
			}
		});
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ok();
			}
		});
		cancelButton = new JButton("cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancel();
			}
		});
		getContentPane().add(new JLabel("Key:"));
		getContentPane().add(key);
		getContentPane().add(new JLabel("Value:"));
		getContentPane().add(value);
		getContentPane().add(colorLabel);
		getContentPane().add(colorButton);
		getContentPane().add(shortcutLabel);
		getContentPane().add(selectShortcutButon);
		getContentPane().add(new JLabel(""));
		getContentPane().add(new JLabel(""));
		getContentPane().add(okButton);
		getContentPane().add(cancelButton);
		pack();
		this.setPreferredSize(new Dimension(400, this.getHeight()));
		pack();
	}

	private void chooseColor() {
		Color newColor = JColorChooser.showDialog(this, "choose color",
				getOption().getColor());
		colorLabel.setBackground(newColor);
		colorLabel.setForeground(new Color(255 - newColor.getRed(),
				255 - newColor.getGreen(), 255 - newColor.getBlue()));
		myOption.setColor(newColor);
	}

	private void chooseButton() {
		if (selectShortcutButon.isSelected()) {
			selectShortcutButon.setText("type shortcut...");
			key.setEnabled(false);
			value.setEnabled(false);
			colorButton.setEnabled(false);
			okButton.setEnabled(false);
		} else {
			selectShortcutButon.setText("select schortcut");
			key.setEnabled(true);
			value.setEnabled(true);
			colorButton.setEnabled(true);
			okButton.setEnabled(true);
		}
	}

	public void ok() {
		myOption.setKey(key.getText());
		myOption.setValue(value.getText());
		myOption.setColor(colorLabel.getBackground());
		setOption(myOption);
		this.dispose();
	}

	public void cancel() {
		cancelled = true;
		this.dispose();
	}

	public void keyPressed(KeyEvent e) {
		if (selectShortcutButon.isSelected()) {
			selectShortcutButon.setText("click when done!");
			String outText = "";
			String code = "";
			if ((e.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
					&& (e.isActionKey() == false)) {
				outText = "n/a";
			} else {
				if (e.getModifiers() < 1) {
					outText = e.getKeyText(e.getKeyCode());
				} else {
					outText = e.getKeyModifiersText(e.getModifiers()) + "+"
							+ e.getKeyText(e.getKeyCode());
				}
				myOption.setShortcut(e.getKeyCode() + ";" + e.getModifiers());
			}
			shortcutLabel.setText("shortcut: " + outText);
			e.consume();
		}
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setOption(TagOption option) {
		this.option = option;
	}

	public TagOption getOption() {
		return option;
	}

}
