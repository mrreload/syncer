/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
    static String szFile2 = "C:" + File.separatorChar + "temp" + File.separatorChar + "test.zip";
    static String szFile3 = "/home/media/test/test.zip";

    public static void main(String[] args) throws IOException, InterruptedException {
        //Config.cfgFile = "syncer.conf";
        
        Config.setHome();
        Config.checkDefaults();
        if (args.length > 0) {
            Config.cfgFile = args[0];
            //szFile = args[1];
            
        }
        LogMan.setup();
        Sender sendQ = new Sender();
        Operator op = new Operator();
        Sender.Qwatcher();
        Operator.Qwatcher();
        if (Config.readProp("server.mode", Config.cfgFile).equalsIgnoreCase("master")) {

            try {
                Master.Listen(Integer.parseInt(Config.readProp("local.port", Config.cfgFile)));
                TimeUnit.SECONDS.sleep(1);

            } catch (IOException ex) {
                mainLOG.severe(ex.getMessage());
            }

        } else if (Config.readProp("server.mode", Config.cfgFile).equalsIgnoreCase("node")) {
            mainLOG.info("Starting Node");
            Node.connect(Config.readProp("remote.host", Config.cfgFile), Integer.parseInt(Config.readProp("remote.port", Config.cfgFile)));

//            TimeUnit.SECONDS.sleep(3);
//            Sender.putQ(Config.readProp("marctv", Config.cfgFile), "REQXLST,," + Config.readProp("local.name", Config.cfgFile));
//            Sender.putQ(Config.readProp("dummy", Config.cfgFile), "ACK,," + Config.readProp("local.name", Config.cfgFile));


//            Sender.SndMSG(Node.myUid + ",,REQ,," + szFile + ",,0", Node.nodeLocalSocket);
        }
//        TimeUnit.SECONDS.sleep(2);
//        
//System.out.println("Got from HashMap " + ConnectionHandler.client2UID.get("marctv"));
//        Sender.connect(Config.readProp("remote.host", "syncer.conf"), Integer.parseInt(Config.readProp("remote.port", "syncer.conf")));
    }
}
