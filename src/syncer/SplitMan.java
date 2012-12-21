/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author marc.hoaglin
 */
public class SplitMan {

    static String[] szFileList;
    public final static Logger splitLOG = Logger.getLogger(SplitMan.class.getName());

    public static String[] FileSplitter(String szFile, String szOutDir, String szHash, String szStartChunk) {
        if (!new File(szOutDir).exists()) {
            new File(szOutDir).mkdirs();
        }
        if (!(calcSize(new File(szOutDir)) == new File(szFile).length())) {
            FileInputStream fis = null;
            try {
                splitLOG.info("Starting FileSplitter for: " + szFile);
                fis = new FileInputStream(szFile);
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

                    fileName = String.format("%s.part%09d", szFile, count);


                    szOutFile = szOutDir + File.separatorChar + new File(fileName).getName();
                    FileOutputStream fos = new FileOutputStream(szOutFile);
                    fos.write(buffer, 0, i);
                    fos.flush();
                    fos.close();
                    splitLOG.severe("Testing count against chunk#" + String.valueOf(count <= Integer.parseInt(szStartChunk)) + szStartChunk);
                    if (count <= Integer.parseInt(szStartChunk)) {
                        splitLOG.info("Adding " + count + " " + szOutFile + " to Outgoing Files list");
                        alFiles.add(count, szOutFile);
                    }

                    ++count;

                }
                szFileList = (String[]) alFiles.toArray(new String[0]);

            } catch (FileNotFoundException ex) {
                splitLOG.severe(ex.getMessage());
            } catch (IOException ex) {
                splitLOG.severe(ex.getMessage());
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    splitLOG.severe(ex.getMessage());
                }
            }
        } else {
            szFileList = getList(szOutDir, szStartChunk);

        }

        return szFileList;
    }

    public static void FileJoiner(File[] szFiles, String szOutFile) {
        try {
            // get number of files in temp folder
            //        System.out.println(new File(szDir).list().length);
            splitLOG.info("OutPut to: " + szOutFile);
            splitLOG.info("Joiner started..");

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
            splitLOG.info("Joiner Ended for: " + szOutFile);
        } catch (IOException ex) {
            splitLOG.severe(ex.getMessage());
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

    public static String[] getList(String szDir, String szStartChunk) {
        List<String> list = new ArrayList<>();
        File file = new File(szDir);
        File[] files = file.listFiles();
        java.util.Arrays.sort(files);
//        String[] szFiles = new String[files.length];
        String[] szCutList = null;
        int iChunk = Integer.parseInt(szStartChunk);
        for (int fileInList = iChunk; fileInList < files.length; fileInList++) {
//            System.out.println(files[fileInList].toString());

//            szFiles[fileInList] = files[fileInList].toString();
list.add(files[fileInList].toString());

        }
        
//        if (iChunk != 0) {
//            for (int i = 0; i < iChunk; i++) {
//                String str = String.format("%9s", Integer.toString(i)).replace(" ", "0");
//                szCutList = removeElements(szFiles, str);
//
//            }
//
//        } else {
//            list.add(szDir)
//        }

//        String[] szArray = Arrays.asList(files).toArray(new String[files.length]);
        Collections.sort(list);
        
        szCutList = list.toArray(new String[0]);
        java.util.Arrays.sort(szCutList);
        
        return szCutList;
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

    public static String[] removeElements(String[] input, String deleteMe) {
        if (input != null) {
            List<String> list = new ArrayList<>(Arrays.asList(input));
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).endsWith(deleteMe)) {
//                    System.out.println(deleteMe + " Removing item from list: " + list.get(i));
                    list.remove(i);
                }
            }


            return list.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }
}
