/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author Marc.Hoaglin
 */
public class Syncer {

    /**
     * @param args the command line arguments
     */
    public final static Logger mainLOG = Logger.getLogger(Syncer.class.getName());
    static String szFile = "/home/xbmc/test/hsqldb-2.2.8.zip";
//    static String szFile2 = "C:" + File.separatorChar + "temp";
    static String szFile3 = "/home/media/test/test.zip";
    

    public static void main(String[] args) throws IOException, InterruptedException {
        String home2 = new File(".").getCanonicalPath();
        
        //Config.cfgFile = "syncer.conf";
//        WatchDir.Watcher(szFile2);
        
        
        if (args.length > 0) {
            Config.setHome(new File(".").getCanonicalPath() + File.separatorChar);
            //szFile = args[1];

        } else {
            Config.setHome(System.getProperty("user.home") + File.separatorChar + ".syncer" + File.separatorChar);
        }
        
        
        Config.checkDefaults();
        LogMan.setup();
        //xbmcHandler.query("/home/xbmc/testexport.txt");
        Sender sendQ = new Sender();
        Operator op = new Operator();
        Request req = new Request();
        req.BufferLimit();
        sendQ.Qwatcher();
        op.Clientwatcher();
        
        if (Config.readProp("server.mode", Config.cfgFile).equalsIgnoreCase("master")) {

            try {
                mainLOG.fine("Starting Master " + Config.readProp("local.port", Config.cfgFile));
                Master.Listen(Integer.parseInt(Config.readProp("local.port", Config.cfgFile)));
//                TimeUnit.SECONDS.sleep(1);

            } catch (IOException ex) {
                mainLOG.severe(ex.getMessage());
            }

        } else if (Config.readProp("server.mode", Config.cfgFile).equalsIgnoreCase("node")) {
            mainLOG.info("Starting Node " + Config.readProp("remote.host", Config.cfgFile) + Integer.parseInt(Config.readProp("remote.port", Config.cfgFile)));
            Node.connect(Config.readProp("remote.host", Config.cfgFile), Integer.parseInt(Config.readProp("remote.port", Config.cfgFile)));

        }

    }
}
