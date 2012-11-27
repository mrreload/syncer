/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.File;
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
        String UID = szMSG[0];

        if (szMSG.length > 1) {
//            for (int i = 0; i < szMSG.length; i++) {
//                System.out.println(szMSG[i]);
//            }
            if (szMSG[1].equals("ACK")) {
                ConnectionHandler.uid = szMSG[0];
                System.out.println("Received an ACK from " + szMSG[0] + " " + szMSG[2]);
                Config.writeProp(szMSG[2], UID, Config.cfgFile);
                ConnectionHandler.client2UID.put(szMSG[2], szMSG[0]);
                Sender.putQ(szMSG[0], "READY" + sep + Config.readProp("local.name", Config.cfgFile));
//                Sender.putQ(ConnectionHandler.client2UID.get(szMSG[2]), "REQ,," + Syncer.szFile2 + ",,0");

            }

            if (szMSG[2].equals("READY")) {
//                ConnectionHandler.rStatus.put(UID, "READY");
                System.out.println("READY Message received from Server " + szMSG[3] + " " + szMSG[0]);
                Config.writeProp(szMSG[3], UID, Config.cfgFile);
                ConnectionHandler.client2UID.put(szMSG[3], szMSG[0]);
//                Sender.putQ(ConnectionHandler.client2UID.get("marctv"), "REQ,," + Syncer.szFile + ",,0");

            }

            if (szMSG[2].equals("COMPLETE")) {
                System.out.println("COMPLETE Message received from Client " + szMSG[0]);
//            RemoteCanSend = false;
                //REQinProgress = false;
                for (int i = 0; i < szMSG.length; i++) {
//            System.out.println(szMSG[i]);
                }
            }

            if (szMSG[2].equals("WAIT")) {
                System.out.println("Received an WAIT, Remote Server is busy Sending already " + szMSG[0]);
            }

            if (szMSG[2].equals("REQ")) {
                System.out.println("Received a REQ from " + szMSG[0]);
                try {
//                    Request.Qmsg(szMSG);
                    Request.reqFile(szMSG, UID);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (szMSG[1].equals("FIL")) {
//                System.out.println("Received a FIL from " + szMSG[0]);
                String fileREQ = "Receiving: ";
                for (int i = 0; i < szMSG.length; i++) {
                    fileREQ = fileREQ + " " + szMSG[i];
                }
                msgLOG.info(fileREQ);
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
                String fileREQ = "Receiving: ";
                for (int i = 0; i < szMSG.length; i++) {
                    fileREQ = fileREQ + " " + szMSG[i];
                }
                msgLOG.info(fileREQ);
                Receiver.rcvXLST(szMSG, UID);
            }
            if (szMSG[2].equals("REQXLST")) {
                System.out.println("Client requested xbmc list " + szMSG[0]);
                try {
                    xbmcHandler.query();
                    Sender.putQ(szMSG[0], "XLST,," + Config.getLogFolder() + Config.readProp("local.name", Config.cfgFile) + ".csv" + ",,0");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } else if (szMSG.length == 1) {
            System.out.println(szMSG[0]);

        }
    }
}
