/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 *
 * @author George
 */
public class Timer {

    public Timer kitchenTimer = null;
    private ArrayList<TimerRange> ALTimer = null;
    public final static Logger tmrLOG = Logger.getLogger(Timer.class.getName());

    public Timer() {
        this.ALTimer = new ArrayList<TimerRange>();

    }

    public void addRange(TimerRange TR) {
        this.ALTimer.add(TR);
    }

    public boolean inRange() {
        boolean blMatchFound = false;
        TimerRange TR = null;

        Iterator i = this.ALTimer.iterator();

        while (!blMatchFound && i.hasNext()) {
            TR = (TimerRange) i.next();
            blMatchFound = TR.inRange();
        }

        return blMatchFound;
    }

    public void setTimer(String szParam) {
        if (this.kitchenTimer == null) {
            this.kitchenTimer = new Timer();
        }

        String[] szElement = szParam.split(",");
        tmrLOG.info(szElement[0] + " at " + szElement[1] + " for " + szElement[2] + " hours");

        if (szElement.length != 3) {
            tmrLOG.severe("Error: incorrect number of parameters for TimerRange.");
            System.exit(-1);
        } else {
            String szDOW = szElement[0].trim();
            String[] szStartTimeElement = szElement[1].split(":");
            if (szStartTimeElement.length != 2) {
                tmrLOG.severe("Error: invalid format for start time \"" + szElement[1] + "\".");
                System.exit(-1);
            }
            int iStartTimeHour = Integer.parseInt(szStartTimeElement[0].trim());
            int iStartTimeMinute = Integer.parseInt(szStartTimeElement[1].trim());

            String[] szRunTimeElement = szElement[2].split(":");
            if (szRunTimeElement.length != 2) {
                tmrLOG.severe("Error: invalid format for run time \"" + szElement[1] + "\".");
                System.exit(-1);
            }
            int iRunTimeHours = Integer.parseInt(szRunTimeElement[0].trim());
            int iRunTimeMinutes = Integer.parseInt(szRunTimeElement[1].trim());

            TimerRange tr = new TimerRange();
            tr.setStartTime(szDOW, iStartTimeHour, iStartTimeMinute);
            tr.setRunTime(iRunTimeHours, iRunTimeMinutes);
            this.kitchenTimer.addRange(tr);
        }
    }

    public void TimerInit() {

        readSetSchedules();
        checkTimers();

    }

    public void readSetSchedules() {
        String SUN = Config.readProp("Sun.time", Config.tmrCFG) + "," + Config.readProp("Sun.length", Config.tmrCFG);
        String MON = Config.readProp("Mon.time", Config.tmrCFG) + "," + Config.readProp("Mon.length", Config.tmrCFG);
        String TUE = Config.readProp("Tue.time", Config.tmrCFG) + "," + Config.readProp("Tue.length", Config.tmrCFG);
        String WED = Config.readProp("Wed.time", Config.tmrCFG) + "," + Config.readProp("Wed.length", Config.tmrCFG);
        String THU = Config.readProp("Thu.time", Config.tmrCFG) + "," + Config.readProp("Thu.length", Config.tmrCFG);
        String FRI = Config.readProp("Fri.time", Config.tmrCFG) + "," + Config.readProp("Fri.length", Config.tmrCFG);
        String SAT = Config.readProp("Sat.time", Config.tmrCFG) + "," + Config.readProp("Sat.length", Config.tmrCFG);
        tmrLOG.info("Scheduler set to:");
        setTimer("SUNDAY," + SUN);
        setTimer("MONDAY," + MON);
        setTimer("TUESDAY," + TUE);
        setTimer("WEDNESDAY," + WED);
        setTimer("THURSDAY," + THU);
        setTimer("FRIDAY," + FRI);
        setTimer("SATURDAY," + SAT);
    }

    public void checkTimers() {
        if (!kitchenTimer.inRange()) {
//            qpsLOG.info("Encoder not scheduled to run yet");
            tmrLOG.info("Syncer is sleeping");
        }
        while (!kitchenTimer.inRange()) {
//            System.err.println("It is time to encode: " + kitchenTimer.inRange());            
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {
//                qpsLOG.severe(ex.getLocalizedMessage());
            }
        }
        //System.exit(2);
    }
}
