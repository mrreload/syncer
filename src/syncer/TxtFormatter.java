/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

/**
 *
 * @author mrreload
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class TxtFormatter extends Formatter {

	
	private static final String lineSep = System.getProperty("line.separator");
	/**
	 * A Custom format implementation that is designed for brevity.
	 */
	public String format(LogRecord record) {
		String loggerName = record.getLoggerName();
		if(loggerName == null) {
			loggerName = "root";
		}
		StringBuilder output = new StringBuilder()
                        .append(calcDate(record.getMillis())).append('|')
                        .append(record.getLevel()).append('|')
                        .append(Thread.currentThread().getName()).append('|')
			.append(loggerName)
			.append("|")
			.append(record.getMessage())
			.append(lineSep);
		return output.toString();
	}
        private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

}
