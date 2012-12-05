/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

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
    Operator() {
        Q = new LinkedBlockingQueue<>();
    }

    public static void Ops(String[] msg) {
        System.out.println("Message objects: " + msg.length);
        for (int i = 0; i < msg.length; i++) {
            System.out.print(i + ": " + msg[i] + " ");
        }
        if (Config.readProp("file.sync", Config.cfgFile).equals("true")) {
            // file sync ops here
        }
        if (Config.readProp("xbmc.sync", Config.cfgFile).equals("true")) {
            // xbmc sync ops here
            Sender.putmQ(msg[1], "REQXLST,," + Config.readProp("local.name", Config.cfgFile));
        }
    }
    static void Qwatcher() {
        
        new Thread(new Runnable() {
            public void run() {
                
                while (true) {
                    
                    try {
                        
//                        if (Q.peek() != null) {
////                            processQ(Q.take());
//                        }
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
}
