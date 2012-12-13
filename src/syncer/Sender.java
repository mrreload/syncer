package syncer;

/**
 *
 * @author mrreload
 */
import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender {

//    public static String fullHash;
    public static String OrgFileName;
    public static boolean BadFile;
    static boolean RemoteCanReceive;
    final static String sep = ",,";
    static BlockingQueue<String> Q;
    static BlockingQueue<String> mQ;
    static BlockingQueue<String> pQ;
    public final static Logger sndLOG = Logger.getLogger(Sender.class.getName());
    
    Sender() {
        Q = new LinkedBlockingQueue<>();
        mQ = new LinkedBlockingQueue<>();
        pQ = new LinkedBlockingQueue<>();
    }
    
    public static void SndFile(String szUUID, String szType, String szFile, int iCurrentFile, int iTotalFile, String szOrgFile, String fullHash) {
        FileInputStream fis = null;
        try {
            
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
            dos.writeUTF(Node.myUid + sep + szType + sep + myFile.getName() + sep + szSHA + sep + iCurrentFile + sep + iTotalFile + sep + fullHash + sep + szOrgFile);
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
    
    public static void SendList(String szUUID, String szType, String[] szList, String OrgFileName, String theHash) throws IOException, Exception {
        
        for (int i = 0; i < szList.length; i++) {
            //senderBusy = true;
//            System.out.println(szList[i]);
//            SndFile(szUUID, szType, szList[i], i, szList.length);
            putFileQ(szUUID, szType, szList[i], i, szList.length, OrgFileName, theHash);
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
    void Qwatcher() {
        
        new Thread(new Runnable() {
            public void run() {
                
                while (true) {
                    
                    try {
                        if (pQ.peek() != null) {
                            processQ(pQ.take());
                        }
                        if (mQ.peek() != null) {
                            processQ(mQ.take());
                        }
                        if (Q.peek() != null) {
                            processQ(Q.take());
                        }
                        Thread.sleep(500);

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
    
    public static void putmQ(String DestUID, String szMSG) {
        String theMessage = Config.readProp("My.Uid", Config.cfgFile) + sep + DestUID + sep + szMSG;
        System.out.println("Putting in Q: " + theMessage);
        try {
            mQ.put(theMessage);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void processQ(String szQ) {
        String[] szQmsg = szQ.split(sep);
//        for (int i = 0; i < szQmsg.length; i++) {
//            System.out.println(szQmsg[i]);
//        }
        
        //test for valid socket connection
        if (ConnectionHandler.sockets.get(szQmsg[1]).isConnected()) {
            switch (szQmsg[2]) {
                case "FIL":
                    SndFile(szQmsg[1], szQmsg[2], szQmsg[3], Integer.parseInt(szQmsg[4]), Integer.parseInt(szQmsg[5]), szQmsg[6], szQmsg[7]);
                    break;
                case "XLST":
                    SndXFile(szQmsg[1], szQmsg[2], szQmsg[3]);
                    break;
                default:
                    SndMSG(szQ, ConnectionHandler.sockets.get(szQmsg[1]));
                    break;
            }
        } else {
            System.out.println("Socket not connected");
        }
        
    }
    
    public static void putFileQ(String DestUID, String szType, String szFile, int iCurrentFile, int iTotalFile, String szOrgFileName, String szFullHash) {
        String szMSG = szType + sep + szFile + sep + iCurrentFile + sep + iTotalFile + sep + szOrgFileName + sep + szFullHash;
        String theMessage = Config.readProp("My.Uid", Config.cfgFile) + Request.sep + DestUID + Request.sep + szMSG;
//        System.out.println("Putting in Q: " + theMessage);
        try {
            Q.put(theMessage);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void SndXFile(String szUUID, String szType, String szFile) {
        FileInputStream fis = null;
        try {
            String sep = ",,";
            System.out.println("Sending XLST " + szFile + " to " + szUUID);
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
            dos.writeUTF(Node.myUid + sep + szUUID + sep + szType + sep + myFile.getName() + sep + szSHA);
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
}
