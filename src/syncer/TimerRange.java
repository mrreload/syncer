/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package syncer;
import java.util.Calendar;

/**
 *
 * @author George
 */
public class TimerRange {
    private long lSecondInMillis = 1000l;
    private long lMinuteInMillis = lSecondInMillis * 60;
    private long lHourInMillis = lMinuteInMillis * 60;
    private long lDayInMillis = lHourInMillis * 24;
    static int DOW_SUNDAY = 0;
    static int DOW_MONDAY = 1;
    static int DOW_TUESDAY = 2;
    static int DOW_WEDNESDAY = 3;
    static int DOW_THURSDAY = 4;
    static int DOW_FRIDAY = 5;
    static int DOW_SATURDAY = 6;


    public long lStartTime = -1;
    public long lRunTime = -1;

    private int parseDayOfWeek( String szDOW ){
        int iReturn = -1;

        String szDOWUC = szDOW.toUpperCase();
        switch (szDOWUC) {
            case "SUNDAY":
                iReturn = 0;
                break;
            case "MONDAY":
                iReturn = 1;
                break;
            case "TUESDAY":
                iReturn = 2;
                break;
            case "WEDNESDAY":
                iReturn = 3;
                break;
            case "THURSDAY":
                iReturn = 4;
                break;
            case "FRIDAY":
                iReturn = 5;
                break;
            case "SATURDAY":
                iReturn = 6;
                break;
            default:
                System.out.println("Error: \"" + szDOW + "\" not recognized as a day of the week.");
                System.exit(-1);
                break;
        }

        return iReturn;
    }

    public void setStartTime(int iDOW, int iHour, int iMinute){
        lStartTime = (iDOW * lDayInMillis) + (iHour * lHourInMillis) + (iMinute * lMinuteInMillis);
    }

    public void setStartTime( String szDOW, int iHour, int iMinute){
        int iDOW = parseDayOfWeek(szDOW);
    
        setStartTime(iDOW, iHour, iMinute);
    }

    public void setRunTime(int iHours, int iMinutes){
        lRunTime = (iHours * lHourInMillis) + (iMinutes * lMinuteInMillis);
    }
    private int translateDayOfWeek(int iCalendarDayOfWeek){
        int iReturnDayOfWeek = 0;

        switch  (iCalendarDayOfWeek){
            case Calendar.SUNDAY:
                iReturnDayOfWeek = TimerRange.DOW_SUNDAY;
                break;
            case Calendar.MONDAY:
                iReturnDayOfWeek = TimerRange.DOW_MONDAY;
                break;
            case Calendar.TUESDAY:
                iReturnDayOfWeek = TimerRange.DOW_TUESDAY;
                break;
            case Calendar.WEDNESDAY:
                iReturnDayOfWeek = TimerRange.DOW_WEDNESDAY;
                break;
            case Calendar.THURSDAY:
                iReturnDayOfWeek = TimerRange.DOW_THURSDAY;
                break;
            case Calendar.FRIDAY:
                iReturnDayOfWeek = TimerRange.DOW_FRIDAY;
                break;
            case Calendar.SATURDAY:
                iReturnDayOfWeek = TimerRange.DOW_SATURDAY;
                break;
            default:
                System.out.println("Undefined Calendar Day!");
                System.exit(-1);
                break;
        }

        return iReturnDayOfWeek;
    }

    public boolean inRange(){
        Calendar cNow = Calendar.getInstance();
        long lMillisInDaysSinceSunday = translateDayOfWeek(cNow.get(Calendar.DAY_OF_WEEK)) * this.lDayInMillis;
        long lMillisInHoursSinceStartOfDay = cNow.get(Calendar.HOUR_OF_DAY) * this.lHourInMillis;
        long lMillisInMinutesSinceStartOfHour = cNow.get(Calendar.MINUTE) * this.lMinuteInMillis;
        long lMillisInSecondsSinceStartOfMinute = cNow.get(Calendar.SECOND) * this.lSecondInMillis;
        long lMillisSinceStartOfSecond = cNow.get(Calendar.MILLISECOND);
        long lMillisSinceSunday = lMillisInDaysSinceSunday + lMillisInHoursSinceStartOfDay +
                lMillisInMinutesSinceStartOfHour + lMillisInSecondsSinceStartOfMinute +
                lMillisSinceStartOfSecond;

        long lEndTime = this.lStartTime + this.lRunTime;
        boolean blInRange = lMillisSinceSunday >= this.lStartTime;
        blInRange = blInRange && (lMillisSinceSunday <= lEndTime);

        return blInRange;
    }
}
