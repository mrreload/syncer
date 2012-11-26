package syncer;

/**
 *
 * @author mrreload
 */
import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender {

    public static String fullHash;
    public static String OrgFileName;
    public static boolean BadFile;
    static boolean RemoteCanReceive;
    final static String sep = ",,";
    static BlockingQueue<String> Q;// = new PriorityBlockingQueue<>();
public final static Logger sndLOG = Logger.getLogger(Sender.class.getName());
    Sender() {
        Q = new PriorityBlockingQueue<>();
    }

    public static void SndFile(String szUUID, String szType, String szFile, int iCurrentFile, int iTotalFile) {
        FileInputStream fis = null;
        try {
            String sep = ",,";
            System.out.println("Sending file " + szFile + " to " + szUUID);
            Socket sock = ConnectionHandler.sockets.get(szUUID);
            File myFile = new File(szFile);
            String szSHA = Hasher.getSHA(szFile);
            byte[] mybytearray = new byte[(int) myFile.length()];
            fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);
            OutputStream os = sock.getOutputStream();
            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(Node.myUid + sep + szType + sep + myFile.getName() + sep + szSHA + sep + iCurrentFile + sep + iTotalFile + sep + fullHash + sep + OrgFileName);
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
//            dos.close();
        } catch (FileNotFoundException ex) {
            sndLOG.severe(ex.getMessage());
        } catch (IOException ex) {
            sndLOG.severe(ex.getMessage());
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                sndLOG.severe(ex.getMessage());
            }
        }

    }

    public static void SendList(String szUUID, String szType, String[] szList) throws IOException, Exception {

        for (int i = 0; i < szList.length; i++) {
            //senderBusy = true;
//            System.out.println(szList[i]);
//            SndFile(szUUID, szType, szList[i], i, szList.length);
            putFileQ(szUUID, szType, szList[i], i, szList.length);
        }
        //senderBusy = false;

    }

    public static String[] getList(String szDir) {

        File file = new File(szDir);
        File[] files = file.listFiles();
        String[] szFiles = new String[files.length];
        for (int fileInList = 0; fileInList < files.length; fileInList++) {
            szFiles[fileInList] = files[fileInList].toString();
        }
        return szFiles;
    }

    public static void SndMSG(String szMSG, Socket sock) {
        OutputStream os = null;
        try {

            os = sock.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(szMSG);
            dos.flush();

        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void reSender(String szFile, int iCurrentFile, int iTotalFile) throws InterruptedException {
        BadFile = false;
        while (!BadFile) {
            Thread.sleep(5000);
        }

    }

//    public static void servReady() throws IOException, Exception {
//        TimeUnit.SECONDS.sleep(5);
//
//        while (!RemoteCanReceive) {
////            Sender.SndMSG("ACK");
//            TimeUnit.SECONDS.sleep(3);
//        }
//
//    }
    static void Qwatcher() {

        new Thread(new Runnable() {
            public void run() {

                while (true) {

                    try {
//                        System.out.println("In QUEUE " + Q.peek());
                        processQ(Q.take());
//                        System.out.println(Q.take());
                        //MsgParser.parseMSG(PullQ(Q.take()));
//                        Thread.sleep(20000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }


            }
        }).start();
        System.out.println("Q watcher started....");
    }

    public static void putQ(String DestUID, String szMSG) {
        String theMessage = Config.readProp("My.Uid", Config.cfgFile) + Request.sep + DestUID + Request.sep + szMSG;
//        System.out.println("Putting in Q: " + theMessage);
        try {
            Q.put(theMessage);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void processQ(String szQ) {
        String[] szQmsg = szQ.split(",,");
        //test for valid socket connection
        if (ConnectionHandler.sockets.get(szQmsg[1]).isConnected()) {
            if (szQmsg[2].equals("FIL")) {
                SndFile(szQmsg[1], szQmsg[2], szQmsg[3], Integer.parseInt(szQmsg[4]), Integer.parseInt(szQmsg[5]));
            } else {
                SndMSG(szQ, ConnectionHandler.sockets.get(szQmsg[1]));
            }
        } else {
            System.out.println("Socket not connected");
        }

    }

    public static void putFileQ(String DestUID, String szType, String szFile, int iCurrentFile, int iTotalFile) {
        String szMSG = szType + sep + szFile + sep + iCurrentFile + sep + iTotalFile;
        String theMessage = Config.readProp("My.Uid", Config.cfgFile) + Request.sep + DestUID + Request.sep + szMSG;
//        System.out.println("Putting in Q: " + theMessage);
        try {
            Q.put(theMessage);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
