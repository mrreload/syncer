/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Hasher {
public final static Logger hsLOG = Logger.getLogger(Hasher.class.getName());
    public static String getSHA(String szFileIn) {
        StringBuilder sb = null;
        FileInputStream fis = null;
//        System.out.println("Hashing: " + szFileIn);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            fis = new FileInputStream(szFileIn);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();

            //convert the byte to hex format method 1
            sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (IOException ex) {
            hsLOG.severe(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            hsLOG.severe(ex.getMessage());
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                hsLOG.severe(ex.getMessage());
            }
        }
        return sb.toString();
    }
    public static int getPercent(int subtotal, int total) {
        return (int) (((float) subtotal / (float) total) * 100);
    }
}
