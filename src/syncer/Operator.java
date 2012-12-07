/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.File;
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
    static Map<String, String> Inprocess;
    static Map<String, String> XbmcREQSent;
    public final static Logger opLOG = Logger.getLogger(Operator.class.getName());
    public static String szREQlogfolder;

    Operator() {
        Q = new LinkedBlockingQueue<>();
        Clients = Collections.synchronizedMap(new HashMap<String, String>());
        Inprocess = Collections.synchronizedMap(new HashMap<String, String>());
        XbmcREQSent = Collections.synchronizedMap(new HashMap<String, String>());
        szREQlogfolder = Config.readProp("receive.tmp", Config.cfgFile) + File.separatorChar + "xbmc" + File.separatorChar;
    }

    private static void Ops() {

        if (Config.readProp("file.sync", Config.cfgFile).equalsIgnoreCase("true")) {
            // file sync ops here
        }
        //System.out.println(Config.readProp("xbmc.sync", Config.cfgFile));
        if (Config.readProp("xbmc.sync", Config.cfgFile).equalsIgnoreCase("true")) {
            // xbmc sync ops here
            while (Config.readProp("sync.partners.xbmc.csv", Config.cfgFile) != null && !Clients.isEmpty()) {
                String client[] = Config.readProp("sync.partners.xbmc.csv", Config.cfgFile).split(",");
                // find if configured clients are connected and do work
                for (int c = 0; c < client.length; c++) {
                    System.out.println("Client: " + client[c] + " is connected: " + ConnectionHandler.sockets.get(Clients.get(client[c])).isConnected() + " request sent: " + XbmcREQSent.containsKey(client[c]));
                    if (ConnectionHandler.sockets.get(Clients.get(client[c])).isConnected() && !XbmcREQSent.containsKey(client[c])) {
                        worker(client[c]);
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

    static void worker(String client) {
        // look for unfinished work for connected node(s) and request remaining files
        String cliFile = szREQlogfolder + Clients.get(client) + ".txt";
        if (new File(cliFile).exists() && !Inprocess.containsKey(client)) {
            //read file and send requests
            xbmcHandler.ReadFile(cliFile, Clients.get(client));
            //let system know file is being processed
            Inprocess.put(client, "true");
        } else {
            //otherwise request new list and sync
            Sender.putmQ(Clients.get(client), "REQXLST,," + Config.readProp("local.name", Config.cfgFile));
            XbmcREQSent.put(client, "true");
        }

    }
}
