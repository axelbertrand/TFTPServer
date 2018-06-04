/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftpserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author axelb
 */
public class STF {
    public static int sendFile(String localFilename, InetAddress address) {
        try {        
            DatagramSocket ds = new DatagramSocket();
            
            byte[] request = createRequest(2, localFilename, "octet");
            DatagramPacket dp = new DatagramPacket(request, request.length, address, 1500);
            ds.send(dp);
            System.out.println("Envoie de la requête au serveur...");
            
            byte[] buffer = new byte[512];
            dp = new DatagramPacket(buffer, buffer.length);
            ds.receive(dp);
            
            
        } catch (SocketException ex) {
            Logger.getLogger(STF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(STF.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
    
    private static byte[] createRequest(int opCode, String filename, String mode) {
        int length = filename.length() + mode.length() + 4;
        byte[] request = new byte[length];
        //request[1] = opCode;
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
}
