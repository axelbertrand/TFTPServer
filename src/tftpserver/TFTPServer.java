/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftpserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author axelb
 */
public class TFTPServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            int Cr_em = STF.sendFile("test.txt", InetAddress.getByName("127.0.0.1"));
        } catch (UnknownHostException ex) {
            Logger.getLogger(TFTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
