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
                    + "order by movie.idfile;");
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
            System.out.println(sztext);
            bw.write(sztext);
            bw.newLine();
            bw.close();;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
