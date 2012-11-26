/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.IOException;
import java.net.Socket;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marc.hoaglin
 */
public class Request {

    static String FILE;
    static String CHUNK;
    static Socket sock;
    
    final static String sep = ",,";
    

    public static void reqFile(String[] szFile, String UID) throws Exception {

        //Format:
        //REQ, FileName, chunk#
        sock = ConnectionHandler.sockets.get(UID);
        System.out.println(szFile[0] + " Client requested file: " + szFile[3] + " chunk: " + szFile[4]);
        final String clientuuid = szFile[0];
        FILE = szFile[3];
        CHUNK = szFile[4];
        if (!CHUNK.equals("all")) {
            reqChunk(szFile, UID);
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    Sender.fullHash = Hasher.getSHA(FILE);
                    Sender.OrgFileName = FILE;
                    Sender.SendList(clientuuid, "FIL", SplitMan.FileSplitter(FILE, Config.readProp("sender.tmp", Config.cfgFile)));
                } catch (IOException ex) {
                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
//        System.out.println("Sending Requested Files...");

    }

    static void reqChunk(String[] szFile, String UID) {
        //logic to find and send a single chunk
    }

    

    class MyReverseComparator implements Comparator {

        public int compare(Object x, Object y) {
            return ((Comparable) y).compareTo(x);
        }
    }

}
