/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author Marc.Hoaglin
 */
public class Receiver {

    //static String szOrgFileName;
    static String szSHAFull;
    static ArrayList<String[]> badFiles;
    static String[] szElements;
    static String sep = ",,";
    static int iCurrentChunk;
    static int iTotalChunk;
    static ArrayList<File> alFiles;
    public final static Logger rcvLOG = Logger.getLogger(Receiver.class.getName());
    static String[] badList;
    static boolean blGotAllChunks;

    public static void rcvFile2(String[] szFileInfo, String UID) {

        System.out.println(szFileInfo.length);
        int bytesRead;
        String szOrgFileName = null;
//        boolean blReceive = true;
        if (alFiles == null) {
            alFiles = new ArrayList<>();
        }
        if (badFiles == null) {
            badFiles = new ArrayList<>();
        }
//        while (blReceive) {
//            InputStream in = null;
        try {
            Sender.RemoteCanReceive = false;

            DataInputStream clientData = new DataInputStream(ConnectionHandler.inStreams.get(UID));
            String fileName = szFileInfo[2];
            String szSHA = szFileInfo[3];
            int index = Integer.parseInt(szFileInfo[4]);
            iCurrentChunk = index + 1;
            iTotalChunk = Integer.parseInt(szFileInfo[5]);
            szSHAFull = szFileInfo[6];
            szOrgFileName = szFileInfo[7];
            String szFileOutPath = Config.readProp("receive.tmp", Config.cfgFile) + File.separatorChar + szSHAFull;
            if (!new File(szFileOutPath).exists()) {
                new File(szFileOutPath).mkdirs();
            }
            String szCurrentChunk = szFileOutPath + File.separatorChar + fileName;
            OutputStream output = new FileOutputStream(szCurrentChunk);
            long size = clientData.readLong();
            rcvLOG.info("Receiving: " + szOrgFileName + " Size: " + size + " Chunk#: " + iCurrentChunk + " of " + iTotalChunk);
            System.out.println("Receiving: " + szOrgFileName + " Size: " + size + " Chunk#: " + iCurrentChunk + " of " + iTotalChunk);
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.flush();
            output.close();
            // Add file to received collection, must check for matching source file
            if (Receiver.verifyHash(szSHA, Hasher.getSHA(szCurrentChunk))) {

                alFiles.add(index, new File(szCurrentChunk));

            } else {
                //add logic to re-send corrupt chunk
                System.out.println("Chunk is BAD!");
                rcvLOG.warning("Chunk is BAD!");
                badFiles.ensureCapacity(index);
                badFiles.add(szFileInfo);

            }
//                checkAndAssemble(UID);
//                blReceive = false;
        } catch (IOException ex) {
            rcvLOG.severe(ex.getMessage());
        }
//        }

        if (checkAndAssemble(UID, szOrgFileName)) {
            Sender.putmQ(UID, "COMPLETE" + sep + szSHAFull);
            CleanUp.deleteDir(Config.readProp("receive.tmp", Config.cfgFile) + File.separatorChar + szSHAFull);
        } else if (!blGotAllChunks) {
            //Do nothing just wait for rest of chunks. loop
        } else {
            System.out.println("Failed to assemble: " + szOrgFileName);
            rcvLOG.severe("Failed to assemble: " + szOrgFileName);
        }

    }
//    static void AddToMap(String fullhash, int chunk, String szFile) {
//       
//         Map<String, String> fullhash;
//        if (fullhash-map)
//    }

    static boolean verifyHash(String szOrgData, String szNewData) {
        boolean blCheck = false;
        if (!szNewData.equals(szOrgData)) {
            blCheck = false;
        } else if (szNewData.equals(szOrgData)) {
            blCheck = true;
        }
        return blCheck;
    }

    static boolean checkAndAssemble(String UID, String szOrgFileName) {
        boolean blComplete = false;
        blGotAllChunks = false;
        if (badFiles != null) {
//            badList = (String[]) badFiles.toArray(new String[0]);
//            for (int i = 0; i < badList.length; i++) {
//                System.out.println("Bad Chunk: " + badList[i]);
//            }
            for (int i = 0; i < badFiles.size(); i++) {
                System.out.println(badFiles.get(i));
            }
        }

        if ((iCurrentChunk == iTotalChunk) && iTotalChunk != 0 && iCurrentChunk > 0) {
            blGotAllChunks = true;
            System.out.println("Assembling");


            String szOutFileFinal = null;
            File[] szFileList = null;
            try {
                szFileList = (File[]) alFiles.toArray(new File[0]);
                alFiles.clear();

                String szOutFolder = Config.readProp("output.folder", Config.cfgFile);
                if (!new File(szOutFolder).exists()) {
                    new File(szOutFolder).mkdirs();
                }

                szOutFileFinal = szOutFolder + File.separatorChar + (new File(szOrgFileName).getName());
                rcvLOG.info("Assembling: " + szOutFileFinal);
                SplitMan.FileJoiner(szFileList, szOutFileFinal);
                //                    System.out.println("Back to Listen");
            } catch (Exception ex) {
                rcvLOG.severe(ex.getMessage());
            }
            if (szSHAFull.equals(Hasher.getSHA(szOutFileFinal))) {
                try {
                    System.out.println("CheckSums match");
                    rcvLOG.info("CheckSums match, file good: " + szOutFileFinal);
                    Operator.FinishedFilesQ.put(szOutFileFinal);
                    blComplete = true;

                    szFileList = null;

    
                } catch (InterruptedException ex) {
                    rcvLOG.severe(ex.getMessage());
                }


            } else {
                System.out.println(szSHAFull);
                System.out.println(Hasher.getSHA(szOutFileFinal));
                blGotAllChunks = false;
            }

        }
        return blComplete;
    }

    public static String rcvXLST(String[] szFileInfo, String UID, String szFilePath) {

        System.out.println("Receiving XLST length: " + szFileInfo.length);
        rcvLOG.info("Receiving XLST length: " + szFileInfo.length);
        int bytesRead;
        String szFullFilePath = null;
        boolean blReceive = true;
        if (alFiles == null) {
            alFiles = new ArrayList<>();
        }
        if (badFiles == null) {
            badFiles = new ArrayList<>();
        }
        while (blReceive) {

            try {
                DataInputStream clientData = new DataInputStream(ConnectionHandler.inStreams.get(UID));
                String fileName = szFileInfo[3];
                String szSHA = szFileInfo[4];

                if (!new File(szFilePath).exists()) {
                    new File(szFilePath).mkdirs();
                }
                szFullFilePath = szFilePath + fileName;
                OutputStream output = new FileOutputStream(szFullFilePath);
                long size = clientData.readLong();
                System.out.println("Receiving: " + szFullFilePath + " Size: " + size + " SHA256: " + szSHA);
                rcvLOG.info("Receiving: " + szFullFilePath + " Size: " + size + " SHA256: " + szSHA);
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }

                output.flush();
                output.close();

                System.out.println("Done receiving XBMC db export from: " + szFileInfo[1]);
                rcvLOG.info("Done receiving XBMC db export from: " + szFileInfo[1]);
                blReceive = false;
            } catch (IOException ex) {
                rcvLOG.severe(ex.getMessage());
            }
        }
        return szFullFilePath;
    }
}
