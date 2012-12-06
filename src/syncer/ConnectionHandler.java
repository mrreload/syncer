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

/**
 *
 * @author mrreload
 */
public class ConnectionHandler extends Thread {

    Socket conn = new Socket();
    static Map<String, Socket> sockets = Collections.synchronizedMap(new HashMap<String, Socket>());
    static Map<String, InputStream> inStreams = Collections.synchronizedMap(new HashMap<String, InputStream>());
//    static Map<String, String> client2UID = Collections.synchronizedMap(new HashMap<String, String>());
    final String separ = ",,";
    String[] szElements;
    static String uid;

    ConnectionHandler(Socket socket) {
        conn = socket;
        
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
            e.printStackTrace();
        }
    }
}//class connedtion

