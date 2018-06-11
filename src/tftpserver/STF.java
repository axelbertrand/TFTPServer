/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author axelb
 */
public class STF {
    public static int sendFile(String localFilename, InetAddress address) {
        try {
            System.out.println("Ouverture du fichier " + localFilename + "...");
            File file = new File(localFilename);
            FileInputStream fis = new FileInputStream(file);
            
            ds = new DatagramSocket();
            ds.setSoTimeout(10000);
            DatagramPacket dp;
            
            System.out.println("Préparation de la requête...");
            byte[] request = createRequestBlock(WRQ, localFilename, "octet");
            dp = new DatagramPacket(request, request.length, address, 69);
            lastDp = dp;
            System.out.println("Envoi de la requête au serveur...");
            ds.send(dp);
            
            byte[] buffer = new byte[512];
            int bytesNumber;
            int blockNum = 1;
            
            do
            {
                dp = new DatagramPacket(buffer, buffer.length);
                
                receive(dp);
                int port = dp.getPort();
                
                buffer = new byte[512];
                System.out.println("Lecture du fichier " + localFilename + "...");
                bytesNumber = fis.read(buffer);
                
                byte[] buffer2 = new byte[bytesNumber];
                System.arraycopy(buffer, 0, buffer2, 0, buffer2.length);
                
                System.out.println("Envoi des données, block n°" + blockNum + "...");
                byte[] data = createDataBlock(blockNum, buffer2);
                
                dp = new DatagramPacket(data, data.length, address, port);
                lastDp = dp;
                ds.send(dp);
                blockNum++;
            }
            while(bytesNumber == 512);
            
            System.out.println("Fin de transfert du fichier");
            
            buffer = new byte[512];
            dp = new DatagramPacket(buffer, buffer.length);
            receive(dp);
            
            System.out.println("Fermeture...");
            fis.close();
            
        } catch (IOException ex) {
            Logger.getLogger(STF.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (Exception ex) { 
            Logger.getLogger(STF.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        
        return 0;
    }
    
    private static byte[] createRequestBlock(int opCode, String filename, String mode) {
        int length = filename.length() + mode.length() + 4;
        byte[] request = new byte[length];
        byte[] opBytes = new byte[] {
            (byte) (opCode >> 8),
            (byte) (opCode)
        };
        
        byte[] filenameBytes = new byte[filename.getBytes().length + 1];
        System.arraycopy(filename.getBytes(), 0, filenameBytes, 0, filenameBytes.length - 1);
        
        byte[] modeBytes = new byte[mode.getBytes().length + 1];
        System.arraycopy(mode.getBytes(), 0, modeBytes, 0, modeBytes.length - 1);
        
        byte[][] arrays = new byte[][] {
            opBytes,
            filenameBytes,
            modeBytes
        };

        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, request, offset, array.length);
            offset += array.length;
        }
        
        return request;
    }
    
    public static byte[] createDataBlock(int blockNum, byte[] data)
    {
        int length = data.length + 4;
        byte[] request = new byte[length];
        byte[] opBytes = new byte[2];
        opBytes[1] = (byte) DATA;
        
        byte[] blockNumBytes = new byte[] {
            (byte) (blockNum >> 8),
            (byte) (blockNum)
        };
        
        byte[][] arrays = new byte[][] {
            opBytes,
            blockNumBytes,
            data
        };
        
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, request, offset, array.length);
            offset += array.length;
        }
        
        return request;
    }
    
    private static void receive(DatagramPacket dp) throws IOException, Exception {
        while(true)
        {
            try {
                System.out.println("Réception de la réponse...");
                ds.receive(dp);
                byte[] response = dp.getData();
                
                if(response[1] != ACK)
                {
                    System.out.println("Erreur lors du transfert du fichier");
                    if(response[1] == ERROR)
                    {
                        int errorCode = response[3];
                        String errorMessage = new String(Arrays.copyOfRange(response, 4, response.length));
                        throw new Exception(String.valueOf(errorCode) + ", " + errorMessage);
                    }
                }
                break;
            } catch (SocketTimeoutException ex) {
                System.out.println("Timeout, renvoi des données...");
                ds.send(lastDp);
            }
        }
    }

    private static DatagramSocket ds = null;
    private static DatagramPacket lastDp = null;
    
    private static final int RRQ = 1;
    private static final int WRQ = 2;
    private static final int DATA = 3;
    private static final int ACK = 4;
    private static final int ERROR = 5;
}
