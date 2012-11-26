/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
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

        String url = "jdbc:mysql://192.168.1.3:3306/MyVideos72";
        String user = "xbmc";
        String password = "xbmc";

         try {
            
            con = (Connection) DriverManager.getConnection(url, user, password);
            pst = con.prepareStatement("SELECT movie.idFile, movie.c00 as Title, movie.c07 as Year, movie.c09 as imdb_id, concat(path.strPath, files.strFilename) as fullpath FROM MyVideos72.movie join path, files where path.idPath = files.idPath and movie.idfile = files.idfile;");
            rs = pst.executeQuery();

            while (rs.next()) { 
                System.out.print(rs.getInt("idFile"));                
                System.out.print(rs.getString("Title"));
                System.out.print(rs.getString("Year"));
                System.out.print(rs.getString("imdb_id"));
                System.out.println(rs.getString("fullpath"));
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
}
