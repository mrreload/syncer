/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.InputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author mrreload
 */
public class ConnectionHandler extends Thread {

    Socket conn = new Socket();
    public static Map<String, Socket> sockets;
    public static Map<String, InputStream> inStreams;
//    static Map<String, String> client2UID = Collections.synchronizedMap(new HashMap<String, String>());
    final String separ = ",,";
    String[] szElements;
    static String uid;
    public final static Logger chLOG = Logger.getLogger(ConnectionHandler.class.getName());

    ConnectionHandler(Socket socket) {
        chLOG.info("Connection Handler Initializing");
        conn = socket;
        sockets = Collections.synchronizedMap(new HashMap<String, Socket>());
        inStreams = Collections.synchronizedMap(new HashMap<String, InputStream>());
    }

    public void run() {

        try {
            if (Config.readProp("server.mode", Config.cfgFile).equalsIgnoreCase("master")) {

                Master.MasterMain(conn);


            } else if (Config.readProp("server.mode", Config.cfgFile).equalsIgnoreCase("node")) {
                Node.NodeMain(conn);

            }
        }//try
        catch (Exception e) {
            chLOG.severe(e.getMessage());
        }
    }
}//class connedtion

