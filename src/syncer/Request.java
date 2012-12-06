/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.File;
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

//    static String FILE;
    static String CHUNK;
    static Socket sock;
    
    final static String sep = ",,";
    

    public static void reqFile(String[] szFile, String UID) throws Exception {

        //Format:
        //REQ, FileName, chunk#
        sock = ConnectionHandler.sockets.get(UID);
        System.out.println(szFile[0] + " Client requested file: " + szFile[3] + " chunk: " + szFile[4]);
        final String clientuuid = szFile[0];
//        String FileName = new File(szFile[3]).getName();
        
        CHUNK = szFile[4];
        if (!CHUNK.equals("all")) {
            reqChunk(szFile, UID);
        }

//        new Thread(new Runnable() {
//            public void run() {
                try {
                    
//                    Sender.OrgFileName = FILE;
                    if (new File(szFile[3]).exists()) {
                         String fullHash = Hasher.getSHA(szFile[3]);
                        Sender.SendList(clientuuid, "FIL", SplitMan.FileSplitter(szFile[3], Config.readProp("sender.tmp", Config.cfgFile) + File.separatorChar + fullHash, fullHash), new File(szFile[3]).getName(), fullHash);
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                }
//            }
//        }).start();
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
    static void reqXlist(String[] szFile, String UID) {
        //logic to find and send a single chunk
    }


}
