/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import com.mysql.jdbc.Connection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marc.Hoaglin
 */
public class xbmcHandler {

    public static void query(String szExport) {
        if (new File(szExport).exists()) {
            new File(szExport).delete();
        }

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String dbhost = Config.readProp("xbmc-db.host", Config.cfgFile);
        String dbport = Config.readProp("xbmc-db.port", Config.cfgFile);
        String dbuser = Config.readProp("xbmc-db.user", Config.cfgFile);
        String dbpass = Config.readProp("xbmc-db.pass", Config.cfgFile);
        String dbname = Config.readProp("xbmc-db.name", Config.cfgFile);

        String url = "jdbc:mysql://" + dbhost + ":" + dbport + "/" + dbname;
        try {

            con = (Connection) DriverManager.getConnection(url, dbuser, dbpass);
            pst = con.prepareStatement("SELECT movie.idFile, movie.c00 as Title, movie.c07 as Year, "
                    + "movie.c09 as imdb_id, concat(path.strPath, files.strFilename) "
                    + "as fullpath, streamdetails.iVideoWidth as videoQ FROM movie "
                    + "left join files on movie.idfile = files.idfile "
                    + "left join path on files.idPath = path.idPath "
                    + "left join streamdetails on movie.idfile = streamdetails.idfile and streamdetails.iVideoWidth is not null "
                    + "order by movie.idfile desc limit 1;");
            rs = pst.executeQuery();

            while (rs.next()) {

                csvWrite(rs.getInt("idFile") + "\t" + rs.getString("Title") + "\t" + rs.getString("Year") + "\t" + rs.getString("imdb_id") + "\t" + rs.getString("fullpath") + "\t" + rs.getString("videoQ"), szExport);
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(xbmcHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {

            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(xbmcHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    private static void csvWrite(String sztext, String szFile) {
        try {
            FileWriter fwrite = new FileWriter(szFile, true);
            BufferedWriter bw = new BufferedWriter(fwrite);
//            System.out.println(sztext);
            bw.write(sztext);
            bw.newLine();
            bw.close();;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private static String RemoteList;
    private static String RemoteUID;

    public static void xbmcSyncMain(String szXList, String szRemoteUID) {
        RemoteList = szXList;
        RemoteUID = szRemoteUID;
        new Thread(new Runnable() {
            public void run() {
                xbmcSync(RemoteList, RemoteUID);
            }
        }).start();
    }

    private static void xbmcSync(String szXList, String szUID) {
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
                if (!queryimdb(lineArray[3])) {
                    System.out.println("Not in local db " + lineArray[3]);
                    
                    String szREQlog = Config.readProp("receive.tmp", Config.cfgFile) + File.separatorChar + "xbmc" + File.separatorChar;
                    if (!new File(szREQlog).exists()) {
                        new File(szREQlog).mkdirs();
                    }
                    csvWrite(strLine,  szREQlog + szUID + ".txt");
                    Sender.putmQ(szUID, "REQ,," + lineArray[4] + ",,0");
                }
//                System.out.println(queryimdb(lineArray[3]));
//                System.out.println(lineArray[3]);
                
            }
            //Close the input stream
            in.close();
            System.out.println("Done Reading and querying");
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    private static boolean queryimdb(String szImDb) {
        boolean blInDb = true;
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String dbhost = Config.readProp("xbmc-db.host", Config.cfgFile);
        String dbport = Config.readProp("xbmc-db.port", Config.cfgFile);
        String dbuser = Config.readProp("xbmc-db.user", Config.cfgFile);
        String dbpass = Config.readProp("xbmc-db.pass", Config.cfgFile);
        String dbname = Config.readProp("xbmc-db.name", Config.cfgFile);

        String url = "jdbc:mysql://" + dbhost + ":" + dbport + "/" + dbname;
        try {

            con = (Connection) DriverManager.getConnection(url, dbuser, dbpass);
            pst = con.prepareStatement("SELECT c00 as title FROM movie where c09 =" + "\'" + szImDb + "\';");
            rs = pst.executeQuery();

            blInDb = rs.next();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(xbmcHandler.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {

            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(xbmcHandler.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return blInDb;
    }
}
