/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hasher {

    public static String getSHA(String szFileIn) {
        StringBuilder sb = null;
        FileInputStream fis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            fis = new FileInputStream(szFileIn.replaceAll("\"", ""));
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

            //        System.out.println("Hex format : " + sb.toString());

            //convert the byte to hex format method 2
            //        StringBuilder hexString = new StringBuilder();
            //        for (int i = 0; i < mdbytes.length; i++) {
            //            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            //        }
            //
            //        System.out.println("Hex format : " + hexString.toString());
        } catch (IOException ex) {
            Logger.getLogger(Hasher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hasher.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(Hasher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sb.toString();
    }
}
