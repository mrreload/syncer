package syncer;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogMan {

    static private FileHandler fileTxt;
    static private Formatter formatterTxt;
    static private FileHandler fileHTML;
    static private Formatter formatterHTML;
    public static FileHandler JobLogTxt;
    static Config config;

    static public void setup() {
        try {
            // Create Logger
            Logger logger = Logger.getLogger("");

            setLogLevel();
    //        System.out.println("Log Level: " + logger.getLevel() + Configuration.readProp("log.level", Configuration.getCfgFL()));
            fileTxt = new FileHandler(Config.getLogFolder() + "syncer.log");
//            fileHTML = new FileHandler(Config.getLogFolder() + "mkvb-log.html");

            // Create txt Formatter
            formatterTxt = new TxtFormatter();
            fileTxt.setFormatter(formatterTxt);
            logger.addHandler(fileTxt);

            // Create HTML Formatter
//            formatterHTML = new HtmlFormat();
//            fileHTML.setFormatter(formatterHTML);
//            logger.addHandler(fileHTML);
        } catch (IOException ex) {
            Logger.getLogger(LogMan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(LogMan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void setLogLevel() {
        Logger logger = Logger.getLogger("");
        // 0=FINEST, 1=FINER, 2=FINE, 3=CONFIG, 4=INFO, 5=WARNING, 6=SEVERE, 7=ALL, 8=OFF
        try {
            int lvl = Integer.parseInt(Config.readProp("log.level", Config.cfgFile));
            if (lvl == 0) {
                logger.setLevel(Level.FINEST);
            }
            if (lvl == 1) {
                logger.setLevel(Level.FINER);
            }
            if (lvl == 2) {
                logger.setLevel(Level.FINE);
            }
            if (lvl == 3) {
                logger.setLevel(Level.CONFIG);
            }
            if (lvl == 4) {
                logger.setLevel(Level.INFO);
            }
            if (lvl == 5) {
                logger.setLevel(Level.WARNING);
            }
            if (lvl == 6) {
                logger.setLevel(Level.SEVERE);
            }
            if (lvl == 7) {
                logger.setLevel(Level.ALL);
            }
            if (lvl == 8) {
                logger.setLevel(Level.OFF);
            }
        } catch (NullPointerException ne) {
            logger.setLevel(Level.INFO);
        }
        System.out.println(Config.readProp("log.level", Config.cfgFile));
        System.out.println("Log Level set to: " + logger.getLevel());

    }
    public static void setupJobLog(String szLogFile) throws IOException {
        // Create Logger
        Logger logger = Logger.getLogger("");

        //setLogLevel();
//        System.out.println("Log Level: " + logger.getLevel() + Configuration.readProp("log.level", Configuration.getCfgFL()));
        JobLogTxt = new FileHandler(Config.getLogFolder() + szLogFile);
        

        // Create txt Formatter
        formatterTxt = new TxtFormatter();
        JobLogTxt.setFormatter(formatterTxt);
        logger.addHandler(JobLogTxt);
        
        

        
    }
    public static void StopJobLog() {
        Logger logger = Logger.getLogger("");
        logger.removeHandler(JobLogTxt);
    }
}
