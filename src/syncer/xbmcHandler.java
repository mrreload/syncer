/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

    public static void query() {

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
            pst = con.prepareStatement("SELECT movie.idFile, movie.c00 as Title, movie.c07 as Year, movie.c09 as imdb_id, concat(path.strPath, files.strFilename) as fullpath FROM MyVideos72.movie join path, files where path.idPath = files.idPath and movie.idfile = files.idfile order by movie.idfile;");
            rs = pst.executeQuery();

            while (rs.next()) {

                csvWrite(rs.getInt("idFile") + "," + rs.getString("Title").replaceAll(",", "") + "," + rs.getString("Year") + "," + rs.getString("imdb_id") + "," + rs.getString("fullpath"), Config.getLogFolder() + Config.readProp("local.name", Config.cfgFile) + ".csv");
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
            bw.write(sztext);
            bw.newLine();
            bw.close();;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
