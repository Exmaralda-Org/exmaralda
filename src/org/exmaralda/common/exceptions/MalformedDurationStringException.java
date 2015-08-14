/**
 * 
 */
package org.exmaralda.common.exceptions;

import org.exmaralda.common.helpers.DurationHelper;

/**
 * @author woerner
 * 
 */
public class MalformedDurationStringException extends ExmaraldaException {
	public MalformedDurationStringException(String durationString) {
		super("malformed duration string:\n" + durationString + "\nplease use format \n" + DurationHelper.FORMAT_STRING);
	}

}
