/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

/**
 *
 * @author marc
 */
import java.io.File;
import java.util.logging.Logger;

public class CleanUp {
    private final static Logger clnLOG = Logger.getLogger(CleanUp.class.getName());

    public static boolean deleteDir(String szDir) {
        File dir = new File(szDir);
        boolean blDeleted = false;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]).toString());
                if (!success) {
                    clnLOG.severe("Failed to delete: " + dir + children[i]);
                    blDeleted = false;
                    //return false;
                } else {
                    blDeleted = true;
                }
            }
        }

        blDeleted = dir.delete();

        return blDeleted;
    }

    
}
