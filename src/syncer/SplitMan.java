/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marc.hoaglin
 */
public class SplitMan {

    static String[] szFileList;

    public static String[] FileSplitter(String szFile, String szOutDir, String szHash) throws FileNotFoundException, IOException {
        if (!new File(szOutDir).exists()) {
            new File(szOutDir).mkdirs();
        }
        if (!(calcSize(new File(szOutDir)) == new File(szFile).length())) {
            System.out.println("Starting FileSplitter for: " + szFile);
            FileInputStream fis = new FileInputStream(szFile);
            String fileName;
            String szOutFile;
            ArrayList<String> alFiles = new ArrayList<>();
            int size = 1024 * 1024;
            byte buffer[] = new byte[size];

            int count = 0;
            while (true) {

                int i = fis.read(buffer, 0, size);
                if (i == -1) {
                    break;
                }
//            Sender.senderBusy = true;
                fileName = String.format("%s.part%09d", szFile, count);
//            System.out.println(new File(fileName).getName());
                szOutFile = szOutDir + File.separatorChar + new File(fileName).getName();
                FileOutputStream fos = new FileOutputStream(szOutFile);
                fos.write(buffer, 0, i);
                fos.flush();
                fos.close();
//            System.out.println(szOutFile);
                alFiles.add(count, szOutFile);
                ++count;

            }
            szFileList = (String[]) alFiles.toArray(new String[0]);
            System.out.println(count);
        } else {
            szFileList = getList(szOutDir);
//            for (int i = 0; i < szFileList.length; i++) {
//                System.out.println(szFileList[i]);
//            }
        }


//        Sender.senderBusy = false;
        
        return szFileList;
    }

    public static void FileJoiner(File[] szFiles, String szOutFile) {
        try {
            // get number of files in temp folder
            //        System.out.println(new File(szDir).list().length);
            System.out.println("OutPut to: " + szOutFile);
            System.out.println("Joiner started..");

            File ofile = new File(szOutFile);
            if (ofile.exists()) {
                ofile.delete();
            }
            FileOutputStream fos;
            FileInputStream fis;
            byte[] fileBytes;
            int bytesRead = 0;

            fos = new FileOutputStream(ofile, true);
            for (File file : szFiles) {
                fis = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fis.read(fileBytes, 0, (int) file.length());
                assert (bytesRead == fileBytes.length);
                assert (bytesRead == (int) file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }
            fos.close();
            fos = null;
            System.out.println("Joiner Ended..");
        } catch (IOException ex) {
            Logger.getLogger(SplitMan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                length += file.length();
            } else {
                length += folderSize(file);
            }
        }
        return length;
    }

    public static String[] getList(String szDir) {

        File file = new File(szDir);
        File[] files = file.listFiles();
        String[] szFiles = new String[files.length];
        for (int fileInList = 0; fileInList < files.length; fileInList++) {
//            System.out.println(files[fileInList].toString());
            szFiles[fileInList] = files[fileInList].toString();
        }
//        String[] szArray = Arrays.asList(files).toArray(new String[files.length]);
        java.util.Arrays.sort(szFiles);
        return szFiles;
    }

    private static long calcSize(File dir) {
        if (dir.isFile() && dir.canRead()) {
            return dir.length();
        }
        long size = 0;
        if (dir.exists() && dir.isDirectory() && dir.canRead()) {
            for (File file : dir.listFiles()) { //Here NPE
                if (file.isFile() && dir.canRead()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += calcSize(file);
                } else {
                    throw new Error("What is this: " + file);
                }
            }
        }
        return size;
    }
}
