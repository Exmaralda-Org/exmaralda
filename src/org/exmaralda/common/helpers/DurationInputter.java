package org.exmaralda.common.helpers;

import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.exmaralda.coma.root.Ui;

public class DurationInputter extends JPanel {
	JTextField yearsTF = new JTextField("", 3);
	JTextField daysTF = new JTextField("", 3);
	JTextField hoursTF = new JTextField("", 3);
	JTextField minutesTF = new JTextField("", 3);
	JTextField secondsTF = new JTextField("", 3);
	JTextField millisTF = new JTextField("", 3);
	private JTextField[] textfields;

	public DurationInputter() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		textfields = new JTextField[] { yearsTF, daysTF, hoursTF, minutesTF,
				secondsTF, millisTF };
		int sub = 0;
		for (JTextField tf : textfields) {
			this.add(new JLabel(Ui.getText("DurationString").substring(sub,
					sub + 1)));
			sub++;
			tf.setDocument(new DigitsDocument());
			this.add(tf);
		}
	}

	public void setText(long ms) {
		long days = TimeUnit.MILLISECONDS.toDays(ms);
		if (days > 0) {

			if (days > 365) {
				long years = days / 365;
				yearsTF.setText("" + years);
				daysTF.setText("" + days % 365);
			} else {
				daysTF.setText("" + days);

			}
			ms = ms - (TimeUnit.DAYS.toMillis(days));
		}

		long hours = TimeUnit.MILLISECONDS.toHours(ms);
		if (hours > 0) {
			hoursTF.setText("" + hours);
			ms -= TimeUnit.HOURS.toMillis(hours);
		}
		long minutes = TimeUnit.MILLISECONDS.toMinutes(ms);
		if (minutes > 0) {
			minutesTF.setText("" + minutes);
			ms -= TimeUnit.MINUTES.toMillis(minutes);
		}
		long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
		if (seconds > 0) {
			secondsTF.setText("" + seconds);
			ms -= TimeUnit.SECONDS.toMillis(seconds);
		}
		millisTF.setText("" + ms);

	}

	private int getNumber(JTextField tf) {
		int i = 0;
		if (tf.getText() != null) {
			if (tf.getText().length() > 0) {
				i = new Integer(tf.getText()).intValue();
			}
		}
		return i;
	}

	public long getDurationInMilliseconds() {
		long ms = 0;
		ms += TimeUnit.DAYS.toMillis(getNumber(yearsTF) * 365);
		System.out.println("years:"+TimeUnit.DAYS.toMillis(getNumber(yearsTF) * 365));
		ms += TimeUnit.DAYS.toMillis(getNumber(daysTF));
		ms += TimeUnit.HOURS.toMillis(getNumber(hoursTF));
		ms += TimeUnit.MINUTES.toMillis(getNumber(minutesTF));
		ms += TimeUnit.SECONDS.toMillis(getNumber(secondsTF));
		ms += getNumber(millisTF);
		return ms;
	}

	public boolean hasDuration() {
		boolean hasDuration = false;
		for (JTextField tf : textfields) {
			if (getNumber(tf) != 0)
				hasDuration = true;
		}
		// TODO Auto-generated method stub
		return hasDuration;
	}
}

class DigitsDocument extends PlainDocument {
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		if (str == null) {
			return;
		}
		char[] addedFigures = str.toCharArray();
		char c;
		for (int i = addedFigures.length; i > 0; i--) {
			c = addedFigures[i - 1];
			if (Character.isDigit(c)) {
				// System.out.println(Inserting digit... + c);
				super.insertString(offs,
						new String(new Character(c).toString()), a);
			}
		}
		// super.insertString(offs, , a);
	}
}
