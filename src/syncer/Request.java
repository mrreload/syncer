/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
    public final static Logger reqLOG = Logger.getLogger(Request.class.getName());
    static BlockingQueue<String[]> requestQ;

    Request() {
        reqLOG.info("Setting up Request Queue..");
        requestQ = new LinkedBlockingQueue<>(5);
    }

    public static void reqFile(String[] szFile, String UID) throws Exception {

        //Format:
        //REQ, FileName, chunk#
        sock = ConnectionHandler.sockets.get(UID);
        reqLOG.info(szFile[0] + " Client requested file: " + szFile[3] + " chunk: " + szFile[4]);
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
                String[] szSnd = {clientuuid, "FIL", szFile[3], Config.readProp("sender.tmp", Config.cfgFile) + File.separatorChar + fullHash, fullHash, new File(szFile[3]).getName(), fullHash};

                requestQ.put(szSnd);

            }


        } catch (Exception ex) {
            reqLOG.severe(ex.getMessage());
        }

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

    void BufferLimit() {
        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    try {

                        String szSnd[] = requestQ.take();
                        Sender.SendList(szSnd[0], szSnd[1], SplitMan.FileSplitter(szSnd[2], szSnd[3], szSnd[4]), szSnd[5], szSnd[6]);

                    } catch (IOException ex) {
                        reqLOG.severe(ex.getMessage());

                    } catch (InterruptedException ex) {
                        reqLOG.severe(ex.getMessage());

                    } catch (Exception ex) {
                        reqLOG.severe(ex.getMessage());
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        reqLOG.severe(ex.getMessage());
                    }
                }
            }
        }).start();
        System.out.println("Buffer Limiter started....");
        reqLOG.info("Buffer Limiter started....");
    }
}
