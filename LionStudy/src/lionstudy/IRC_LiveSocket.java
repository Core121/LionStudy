package lionstudy;

import java.io.*;
import java.net.*;
import java.util.*;

final class IRC_LiveSocket
{
    Socket lionsocket;
    OutputStream outStream;
    //Replace this with private message?
    String channelJoined;
    //Used to clear field
    final static String CRLF = "\r\n";
    //Message to be sent to output stream
    String msg;
    
    String server;
    int port;
    
    
    IRC_LiveSocket(String channel, String fName, String uName)
    {
        
        try
        {
        //Connects to Lionstudy Server
            lionsocket = new Socket(server, port);
        //Sets output stream to out
            outStream = lionsocket.getOutputStream();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        IRC_nick(fName);
        IRC_user(uName, "null","null","real name");
        IRC_channelJoin(channel);
        
        
        
    }
    
    private void send(String text)
    {
        byte[] bytes = (text + CRLF).getBytes();
        
        try
        {
            outStream.write(bytes);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void IRC_nick(String nick)
    {
        msg="NICK "+nick;
        send(msg);
    }
    
    public void IRC_user(String username, String host, String server, String realname)
    {
        msg="USER "+username+" "+host+" "+server+" :"+realname;
        send(msg);
    }
    
    public void IRC_channelJoin(String channel)
    {
        channelJoined=channel;
        msg="JOIN "+channelJoined;
        send(msg);
    }
    
    public void IRC_channelLeave(String channel)
    {
        msg="PART "+channel;
    }
    
    public void IRC_privMSG(String reciever, String text)
    {
        msg="PRIVMSG "+reciever+" :"+text;
        send(msg);
    }
    
    void IRC_processMessage(String ircMessage)
    {
        IRC_RecievedMessage RcvMsg = IRC_MessageParser.recieved(ircMessage);
        if(RcvMsg.command.equals("PRIVMSG"))
        {
            System.out.println(RcvMsg.source+": "+RcvMsg.content);
        }
        else if(RcvMsg.command.equals("CLOSE"))
        {

        }

    }
       
    public void run()
    {
        do{
        //LOOP TO RECIEVE MESSAGES
            try
            {
                InputStream inStream = lionsocket.getInputStream();
                IRC_MessageBuffer msgBuf = new IRC_MessageBuffer();

                byte[] buffer = new byte[1024];
                int bytes;

                do
                {
                    bytes=inStream.read(buffer);
                    if(bytes!=-1)
                    {
                        msgBuf.addToBuffer(Arrays.copyOfRange(buffer,0, bytes));
                        while(msgBuf.hasMessage())
                        {
                            msg = msgBuf.getMessage();
                        }
                        IRC_processMessage(msg);
                    }
                }while(bytes!=-1);

            }
            catch(Exception e)
            {
                System.out.print(e);
            }
        }while(true);
        
    }

}
