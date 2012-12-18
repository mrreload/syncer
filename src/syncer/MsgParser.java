/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marc.Hoaglin
 */
public class MsgParser {

    final static String sep = ",,";
    public final static Logger msgLOG = Logger.getLogger(Syncer.class.getName());

    static void parseMSG(String[] szMSG) {
//        System.out.println("Message objects:" + szMSG.length);
//        for (int i = 0; i < szMSG.length; i++) {
//            System.out.print(i + ": " + szMSG[i] + " ");
//        }
//        System.out.println();
        String UID = szMSG[0];

        if (szMSG.length > 1) {
//            for (int i = 0; i < szMSG.length; i++) {
//                System.out.println(szMSG[i]);
//            }
            if (szMSG[2].equals("ACK")) {
                ConnectionHandler.uid = szMSG[0];
                System.out.println("Received an ACK from " + szMSG[0] + " " + szMSG[3]);
                msgLOG.info("Received an ACK from " + szMSG[0] + " " + szMSG[3]);
                Config.writeProp(szMSG[3], UID, Config.cfgFile);
                Operator.Clients.put(szMSG[3], szMSG[0]);
                Sender.putmQ(szMSG[0], "READY" + sep + Config.readProp("local.name", Config.cfgFile));
            }

            if (szMSG[2].equals("READY")) {
//                ConnectionHandler.rStatus.put(UID, "READY");
                System.out.println("READY Message received from Remote " + szMSG[3] + " " + szMSG[0]);
                msgLOG.info("READY Message received from Remote " + szMSG[3] + " " + szMSG[0]);
                Config.writeProp(szMSG[3], UID, Config.cfgFile);
                Operator.Clients.put(szMSG[3], szMSG[0]);
            }

            if (szMSG[2].equals("COMPLETE")) {
                System.out.println("COMPLETE Message received from Client " + szMSG[0]);
                for (int i = 0; i < szMSG.length; i++) {
                }
                System.out.println("Deleting: " + Config.readProp("sender.tmp", Config.cfgFile) + File.separatorChar + szMSG[3]);
                msgLOG.info("Deleting: " + Config.readProp("sender.tmp", Config.cfgFile) + File.separatorChar + szMSG[3]);
                CleanUp.deleteDir(Config.readProp("sender.tmp", Config.cfgFile) + File.separatorChar + szMSG[3]);
                
            }

            if (szMSG[2].equals("WAIT")) {
                System.out.println("Received an WAIT, Remote Server is busy Sending already " + szMSG[0]);
            }

            if (szMSG[2].equals("REQ")) {
                System.out.println("Received a REQ from " + szMSG[0]);
                msgLOG.info("Received a REQ from " + szMSG[0]);
                try {
//                    Request.Qmsg(szMSG);
                    Request.reqFile(szMSG, UID);
                } catch (Exception ex) {
                    msgLOG.severe(ex.getMessage());
                }
            }
            if (szMSG[1].equals("FIL")) {
//                System.out.println("Received a FIL from " + szMSG[0]);
                String fileREQ = "Receiving: ";
                for (int i = 0; i < szMSG.length; i++) {
                    fileREQ = fileREQ + " " + szMSG[i];
                }
                msgLOG.info("Receiving: " + fileREQ);
                Receiver.rcvFile2(szMSG, UID);
            }
            if (szMSG[2].equals("LST")) {
                System.out.println("Received a LST " + szMSG[0]);
                for (int i = 0; i < szMSG.length; i++) {
//            System.out.println(szMSG[i]);
                }
            }

            if (szMSG[2].equals("XLST")) {
                System.out.println("Received a XLST " + szMSG[0]);
                msgLOG.info("Received a XLST " + szMSG[0]);
                String fileREQ = "Receiving: ";
                for (int i = 0; i < szMSG.length; i++) {
                    fileREQ = fileREQ + " " + szMSG[i];
                }
                msgLOG.info(fileREQ);

                xbmcHandler.xbmcSyncMain(Receiver.rcvXLST(szMSG, UID, Operator.szCliFileListFolder), UID);
            }

            if (szMSG[2].equals("REQXLST")) {
                System.out.println("Client requested xbmc list " + szMSG[0]);
                msgLOG.info("Client requested xbmc list " + szMSG[0]);
                String fl2Send = Config.getLogFolder() + Config.readProp("local.name", Config.cfgFile) + ".txt";
                if (new File(fl2Send).exists()) {
                    new File(fl2Send).delete();
                }
                try {
                    xbmcHandler.query(fl2Send);
                    Sender.putmQ(szMSG[0], "XLST" + sep + fl2Send + sep + Hasher.getSHA(fl2Send));
                } catch (Exception ex) {
                    msgLOG.severe(ex.getMessage());
                }
            }

        } else if (szMSG.length == 1) {
            System.out.println("Message is 1 object" + szMSG[0]);
            msgLOG.warning("Message is 1 object " + szMSG[0]);

        }
    }
}
