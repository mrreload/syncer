/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

/**
 *
 * @author xbmc
 */
import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SimpleLog {
final static Logger logger = Logger.getLogger("syncer log");
    
    public static void Logit(Level lvl,String daLog) {
        FileHandler fh;
        try {

      // This block configure the logger with handler and formatter
      fh = new FileHandler("c:\\SyncerLog.log", true);
      logger.addHandler(fh);
      logger.setLevel(Level.ALL);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);

      // the following statement is used to log any messages   
      logger.log(lvl,daLog);

    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    }
}
