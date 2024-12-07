package org.exmaralda.coma.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class TimeSpinner extends JPanel {
	GregorianCalendar calendar;
	private JSpinner spinner;
	private SpinnerDateModel sm;

	public TimeSpinner() {
		calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		sm = new SpinnerDateModel(calendar.getTime(), null, null,
				Calendar.HOUR_OF_DAY);
		spinner = new JSpinner(sm);
		JSpinner.DateEditor de = new JSpinner.DateEditor(spinner, "hh:mm");
		spinner.setEditor(de);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(spinner);
	}

	public TimeSpinner(GregorianCalendar cal) {
		this();
		calendar.setTime(cal.getTime());

	}

	public TimeSpinner(Date date) {
		this();
		calendar.setTime(date);
		sm.setValue(date);
	}
	
	public Date getTime() {
		return calendar.getTime();
	}
	
	public void setTime (Date date) {
		calendar.setTime(date);
		sm.setValue(date);
	}
	
	public void setEnabled (boolean e) {
		spinner.setEnabled(e);
	}
}
