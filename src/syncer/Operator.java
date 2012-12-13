/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    static BlockingQueue<String> FinishedFilesQ;
    static Map<String, String> Clients;
    static Map<String, String> Inprocess;
    static Map<String, String> XbmcREQSent;
    public final static Logger opLOG = Logger.getLogger(Operator.class.getName());
    public static String szREQlogfolderXBMC;
    public static String szREQlogfolderFILE;
    private static StandardCopyOption overWrite = StandardCopyOption.REPLACE_EXISTING;
    private static StandardCopyOption atomicMove = StandardCopyOption.ATOMIC_MOVE;

    Operator() {
        Q = new LinkedBlockingQueue<>();
        FinishedFilesQ = new LinkedBlockingQueue<>();
        Clients = Collections.synchronizedMap(new HashMap<String, String>());
        Inprocess = Collections.synchronizedMap(new HashMap<String, String>());
        XbmcREQSent = Collections.synchronizedMap(new HashMap<String, String>());
        szREQlogfolderXBMC = Config.readProp("receive.tmp", Config.cfgFile) + File.separatorChar + "xbmc" + File.separatorChar;
        szREQlogfolderXBMC = Config.readProp("receive.tmp", Config.cfgFile) + File.separatorChar + "file" + File.separatorChar;
    }

    private static void Ops() {

        if (Config.readProp("file.sync", Config.cfgFile).equalsIgnoreCase("true")) {
//            String cliFile = szREQlogfolderFILE + Clients.get(client) + ".txt";
            // file sync ops here
        }
        //System.out.println(Config.readProp("xbmc.sync", Config.cfgFile));
        if (Config.readProp("xbmc.sync", Config.cfgFile).equalsIgnoreCase("true")) {

            // xbmc sync ops here
            while (Config.readProp("sync.partners.xbmc.csv", Config.cfgFile) != null && !Clients.isEmpty()) {
                String client[] = Config.readProp("sync.partners.xbmc.csv", Config.cfgFile).split(",");
                // find if configured clients are connected and do work
                System.out.println("Checking for connected Clients for Operator to start work");
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

    void Clientwatcher() {

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
        String cliFile = szREQlogfolderXBMC + Clients.get(client) + ".txt";
        if (new File(cliFile).exists()) {
            InitSort(cliFile);
        }
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

    public static void InitSort(final String szLOG) {

        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    try {
                        WatchNsort(FinishedFilesQ.take(), szLOG);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Operator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Operator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
        System.out.println("Complete files Q started....");
    }

    static void WatchNsort(String szFile, String szLOG) {
        //read requested files log for sort decision
        GetOutPutPath(szLOG, szFile);
        nioMove(szFile, "outputfile");
        xbmcHandler.removeLineFromFile(szFile, szLOG);

    }

    static String[] GetOutPutPath(String szXList, String szSearch) {
        String szMatch[] = new String[6];
        String lineArray[] = new String[6];
        try {
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(szXList);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
//                System.out.println(strLine);
                lineArray = strLine.split("\t");
                if (lineArray[4].contains(szSearch)) {
                    szMatch = lineArray;
                }

            }
            //Close the input stream
            in.close();
            System.out.println("Done Reading and querying");
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return szMatch;
    }

    public static void nioMove(String szsrcFile, String szdstFile) {

        Path source = Paths.get(szsrcFile);
        Path destination = Paths.get(szdstFile);
        Path targetPath = destination.getParent();
        try {
            if (!Files.exists(targetPath)) {
                Files.createDirectory(targetPath);
            }
            Files.move(source, destination, overWrite, atomicMove);
//            cpLOG.info("File Moved to:  " + szdstFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
