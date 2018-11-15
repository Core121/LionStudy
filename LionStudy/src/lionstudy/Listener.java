/*
 * 
 */
package lionstudy;

import java.net.Socket;

/**
 *
 * @author jmoun
 */
public class Listener extends Thread {
    
    
    String IP = "";// To implement when static IP addressed
    int pIRCport = 6668;
    int liveIRCport = 6667;
    Socket s; //To implement: Basic IRC socket
    
    public Listener(){
    
    };
    
    public void run(){
        
    }
    
}
