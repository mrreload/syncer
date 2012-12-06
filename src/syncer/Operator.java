/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marc.Hoaglin
 */
public class Operator {

    static BlockingQueue<String> Q;
    static Map<String, String> Clients;
    static Map<String, String> XbmcREQSent;
    public final static Logger opLOG = Logger.getLogger(Operator.class.getName());

    Operator() {
        Q = new LinkedBlockingQueue<>();
        Clients = Collections.synchronizedMap(new HashMap<String, String>());
        XbmcREQSent = Collections.synchronizedMap(new HashMap<String, String>());
    }

    private static void Ops() {

        if (Config.readProp("file.sync", Config.cfgFile).equalsIgnoreCase("true")) {
            // file sync ops here
        }
        //System.out.println(Config.readProp("xbmc.sync", Config.cfgFile));
        if (Config.readProp("xbmc.sync", Config.cfgFile).equalsIgnoreCase("true")) {
            // xbmc sync ops here
            // look for unfinished work for connected node(s) and request remaining files

            //otherwise request new list and sync

            while (Config.readProp("sync.partners.xbmc.csv", Config.cfgFile) != null && !Clients.isEmpty()) {
                String client[] = Config.readProp("sync.partners.xbmc.csv", Config.cfgFile).split(",");
                // find if configured clients are connected
                for (int c = 0; c < client.length; c++) {
                    if (ConnectionHandler.sockets.get(Clients.get(client[c])).isConnected() && !XbmcREQSent.containsKey(client[c])) {
                        Sender.putmQ(Clients.get(client[c]), "REQXLST,," + Config.readProp("local.name", Config.cfgFile));
                        XbmcREQSent.put(client[c], "true");
                    } else {
                        Clients.remove(client[c]);
                        ConnectionHandler.sockets.remove(Clients.get(client[c]));
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    opLOG.severe(ex.getMessage());
                }
            }

        }
    }

    static void Clientwatcher() {

        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    Ops();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Operator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
        System.out.println("Client watcher started....");
    }
}
