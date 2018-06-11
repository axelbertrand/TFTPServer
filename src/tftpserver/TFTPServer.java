/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftpserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
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
            Scanner scanner = new Scanner(System.in);
            System.out.println("Entrez l'adresse IP du serveur :");
            String address = scanner.next();
            System.out.println("Entrez le fichier à envoyer :");
            String filename = scanner.next();
            int Cr_em = STF.sendFile(filename, InetAddress.getByName(address)); //"134.214.116.152"
            System.out.println("CR_em = " + Cr_em);
        } catch (UnknownHostException ex) {
            Logger.getLogger(TFTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
