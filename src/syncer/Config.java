/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author mrreload
 */
public class Config {

    private final static Logger CFGLOG = Logger.getLogger(Config.class.getName());
//    static String cfgFile;
    private static String LogFolder;
     public static String home;
    public static String cfgFile;
    public static String tmrCFG;

    public static String readProp(String prop, String daFile) {
        String propval = null;
        Properties configFl = new Properties();
        try {
            FileInputStream fin = new FileInputStream(daFile);
            configFl.load(fin);

            propval = configFl.getProperty(prop);
            fin.close();
        } catch (FileNotFoundException noFile) {
            CFGLOG.severe(noFile.getMessage());
        } catch (IOException io) {
            CFGLOG.severe(io.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propval;

    }

    public static void writeProp(String property, String szs, String cfgFile) {
        boolean blSet = false;
        Properties configFile = new Properties();
        try {
            FileInputStream fin = new FileInputStream(cfgFile);
            configFile.load(fin);
            configFile.setProperty(property, szs);
            FileOutputStream fout = new FileOutputStream(cfgFile);
            configFile.store(fout, null);
            fin.close();
            fout.close();
            blSet = true;
        } catch (FileNotFoundException noFile) {
            CFGLOG.severe(noFile.getMessage());
            blSet = false;
        } catch (IOException io) {
            CFGLOG.severe(io.getMessage());
            blSet = false;
        }

    }

    public static String getLogFolder() {
        return LogFolder;
    }

    public String getLogLevel() {
        String szLogLevel = readProp("log.level", cfgFile);
        return szLogLevel;
    }

    public static void setHome(String szHome) {
        home = szHome;

        File hm = new File(home);
        if (!hm.exists()) {
            CFGLOG.severe(home + " folder NOT Existing creating folder");
            hm.mkdirs();

        }
        LogFolder = home + "logs" + File.separatorChar;
        File log = new File(LogFolder);
        if (!log.exists()) {
            CFGLOG.severe(LogFolder + " folder NOT Existing creating folder");
            log.mkdirs();

        }

        String ConfigFolder = home + "config" + File.separatorChar;
        File cfg = new File(ConfigFolder);
        if (!cfg.exists()) {
            CFGLOG.severe(ConfigFolder + " folder NOT Existing creating folder");
            cfg.mkdirs();

        }
        setCfgFL(ConfigFolder);


    }

    static void setCfgFL(String szConfigFolder) {
        cfgFile = szConfigFolder + "syncer.conf";
        tmrCFG = szConfigFolder + "timers.conf";
        File cf = new File(cfgFile);
        File tcf = new File(tmrCFG);
        if (!cf.exists()) {
            CFGLOG.severe("Properties file missing or first run creating file");
            try {
                // Create file 
                FileWriter fstream = new FileWriter(cfgFile);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write("### Syncer Properties File");
                //Close the output stream
                out.close();
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
                CFGLOG.severe("Error: " + e.getMessage() + " " + e.getClass() + " Cause: " + e.getCause());
            }
        }
        CFGLOG.info("Config File at: " + cfgFile + " :INFO Only NOT an Error!");
        if (!tcf.exists()) {
            CFGLOG.severe("Timers config missing or first run creating file");
            try {
                // Create file 
                FileWriter fstream = new FileWriter(tmrCFG);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write("### Syncer Timers File");
                //Close the output stream
                out.close();
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
                CFGLOG.severe("Error: " + e.getMessage() + " " + e.getClass() + " Cause: " + e.getCause());
            }
        }
        CFGLOG.info("Timers config at: " + tmrCFG + " :INFO Only NOT an Error!");
        
    }

    private static void dSet(String prop, String defVal) {


        if (readProp(prop, cfgFile) == null) {

            CFGLOG.warning(prop + " Property not found in config file setting default value of: " + defVal);
            writeProp(prop, defVal, cfgFile);
        }

        if (readProp(prop, cfgFile).isEmpty()) {
            CFGLOG.warning("Empty Value for: " + prop + " setting default value of: " + readProp(prop, "syncer.conf"));
            writeProp(prop, defVal, cfgFile);
        }
    }
    private static void dSetTimers(String prop, String defVal) {


        if (readProp(prop, tmrCFG) == null) {

            CFGLOG.warning(prop + " Property not found in config file setting default value of: " + defVal);
            writeProp(prop, defVal, tmrCFG);
        }

        if (readProp(prop, tmrCFG).isEmpty()) {
            CFGLOG.warning("Empty Value for: " + prop + " setting default value of: " + defVal);
            writeProp(prop, defVal, tmrCFG);
        }
    }

    public static void checkDefaults() {

        File mkvbcfg = new File(cfgFile);
        if (!mkvbcfg.exists()) {

            try {
                FileWriter fstream = new FileWriter(cfgFile);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write("#Syncer Properties File");
                out.close();

            } catch (Exception e) {
                CFGLOG.severe("Error Writing Properties File: /n" + e.getMessage());

            }
        }

        dSet("output.folder", System.getProperty("user.home") + File.separatorChar + "Syncer" + File.separatorChar + "Finsished");
        dSet("sender.tmp", System.getProperty("user.home") + File.separatorChar + "Syncer" + File.separatorChar + "SendTemp");
        dSet("receive.tmp", System.getProperty("user.home") + File.separatorChar + "Syncer" + File.separatorChar + "ReceiveTemp");
        dSet("server.mode", "node");
        dSet("remote.host", "mister-wizard.com");
        dSet("remote.port", "13267");
        dSet("local.port", "13267");
        dSet("local.name", "dummy");
        dSet("My.Uid", "");
        dSet("file.sync", "false");
        dSet("local.watch.folders", "none");
        dSet("local.archive.point", "none");
        dSet("xbmc.sync", "false");
        dSet("xbmc-db.user", "xbmc");
        dSet("xbmc-db.pass", "xbmc");
        dSet("xbmc-db.host", "localhost");
        dSet("xbmc-db.port", "3306");
        dSet("xbmc-db.name", "MyVideos75");
        dSet("sync.partners.xbmc.csv", "none");
        dSet("sync.partners.file.csv", "none");
        dSet("xbmc.sync.use.timer", "true");
        dSetTimers("Sun.time", "2:00");
        dSetTimers("Sun.length", "10:00");
        dSetTimers("Mon.time", "2:00");
        dSetTimers("Mon.length", "10:00");
        dSetTimers("Tue.time", "2:00");
        dSetTimers("Tue.length", "10:00");
        dSetTimers("Wed.time", "2:00");
        dSetTimers("Wed.length", "10:00");
        dSetTimers("Thu.time", "2:00");
        dSetTimers("Thu.length", "10:00");
        dSetTimers("Fri.time", "2:00");
        dSetTimers("Fri.length", "10:00");
        dSetTimers("Sat.time", "2:00");
        dSetTimers("Sat.length", "10:00");
        dSet("log.level", "4");

    }
}
