/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marc.Hoaglin
 */
public class Master {
    
    static ServerSocket serverSock;
    static Socket clientSock;
    static InputStream in;
    static String[] szElements;
    static String separ = ",,";
    public final static Logger masterLOG = Logger.getLogger(Syncer.class.getName());
    
    static void Listen(int iPort) throws IOException {
        masterLOG.info("Starting Master: on " + iPort);
        if (Config.readProp("My.Uid", Config.cfgFile).equals("")) {            
            Node.myUid = UUID.randomUUID().toString();
            Config.writeProp("My.Uid", Node.myUid, Config.cfgFile);
        } else {
            Node.myUid = Config.readProp("My.Uid", Config.cfgFile);
        }
        serverSock = new ServerSocket(iPort);
        Boolean listening = true;
        
        while (listening) {
            masterLOG.info("Waiting for Clients to Connect...");
            clientSock = serverSock.accept();
            new ConnectionHandler(clientSock).start();
        }
    }
    
    static void MasterMain(Socket conn) {
        InputStream sin = null;
        String uid = null;
        try {
//            System.out.println("New Connection detected ");
            masterLOG.info("Connected to Server: " + conn.toString());
            sin = conn.getInputStream();
            DataInputStream clientData = new DataInputStream(sin);
//            System.out.println(clientData.available());

            while (true) {
                szElements = null;
                if (clientData.available() > 0) {
                    szElements = clientData.readUTF().split(separ);
                    if (szElements.length > 0) {
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
                        Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
