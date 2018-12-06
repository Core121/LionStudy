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

    String IP = "localhost";// To implement when static IP addressed
    int livePort = 6666;
    int logPort = 6667;
    Socket s; //To implement: Basic IRC socket

    //This will need to be user's first name
    String nickname = "TEST USER 1";
    //This will be the channel or private message to send the message
    String channel = "Test1";
    //This will be the message to send
    String message = "Test message";

    //These three lines of code are the lines used to join a live time IRC channel after socket
    //is set up
    //chatSocket.IRC_nick(nickname);
    //chatSocket.IRC_user("Brad_Naugle", "null", "null", "real name");
    //chatSocket.IRC_channelJoin(channel);
    public Listener() {

    }

    ;
    
    public void run() {

    }

}
