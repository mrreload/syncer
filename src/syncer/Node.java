/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;

/**
 *
 * @author mrreload
 */
public class Node {

    static Socket nodeLocalSocket;
//    static Socket readSocket;
//    static OutputStream os;
//    static InputStream is;
    static String[] szElements;
    static String separ = ",,";
    static String myUid;
//    static String TempUid;
    public final static Logger nodeLOG = Logger.getLogger(Syncer.class.getName());

    static void connect(String szHost, int iPort) throws IOException {
        boolean blMasterAlive = false;
        if (Config.readProp("My.Uid", Config.cfgFile).equals("")) {
            Node.myUid = UUID.randomUUID().toString();
            Config.writeProp("My.Uid", Node.myUid, Config.cfgFile);
        } else {
            Node.myUid = Config.readProp("My.Uid", Config.cfgFile);
        }
        while (!blMasterAlive) {
            nodeLocalSocket = new Socket();
            InetSocketAddress endPoint = new InetSocketAddress(szHost, iPort);
            nodeLocalSocket.setKeepAlive(true);
            if (endPoint.isUnresolved()) {
                nodeLOG.warning("Failure " + endPoint);
                blMasterAlive = false;
            } else {
                try {
                    nodeLocalSocket.connect(endPoint, 10000);
                    System.out.println("Success Connected: " + endPoint);
                    nodeLOG.info("Success Connected: " + endPoint);
                    blMasterAlive = true;

                } catch (IOException ioe) {

                    nodeLOG.severe("Failure Connecting: endPoint" + ioe.getMessage());
                    nodeLocalSocket.close();
                    blMasterAlive = false;
                }
            }
            try {
                Thread.sleep(100);

            } catch (InterruptedException ex) {
                nodeLOG.severe(ex.getMessage());
            }
        }

        Sender.SndMSG(myUid + separ + "ACK" + separ + Config.readProp("local.name", Config.cfgFile), nodeLocalSocket);

        new ConnectionHandler(nodeLocalSocket)
                .start();

    }

    static void NodeMain(Socket conn) {
        InputStream sin = null;
        String uid = null;
        try {
            System.out.println("New Connection detected ");
            nodeLOG.info("New Connection detected ");
            sin = conn.getInputStream();
            DataInputStream clientData = new DataInputStream(sin);
            nodeLOG.info("Connected to Server: " + conn.toString());
            while (true) {

                if (clientData.available() > 0) {
//                    System.out.println(clientData.available());
                    szElements = clientData.readUTF().split(separ);
//                    System.out.println(clientData.readLong());
                    if (szElements.length > 1) {
                        uid = szElements[0];
                        ConnectionHandler.sockets.put(uid, conn);
                        ConnectionHandler.inStreams.put(uid, sin);
                        MsgParser.parseMSG(szElements);
//                        Request.Qmsg(szElements);
                        szElements = null;
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        nodeLOG.severe(ex.getMessage());
                    }
                }
            }

        } catch (IOException ex) {
            nodeLOG.severe(ex.getMessage());
        }
    }
}
