package lionstudy;

import java.io.*;
import java.net.*;
import java.util.*;

final class IRC_LiveSocket implements Runnable
{
    Socket lionstudy;
    OutputStream outStream;
    //Replace this with private message?
    String channelJoined;
    //Used to clear field
    final static String CRLF = "\r\n";
    //Message to be sent to output stream
    String msg;
    //GUI receiver;
    
    IRC_LiveSocket(String server, int port /*, GUI receiver*/)
    {
        //this.receiver = receiver;
        try
        {
        //Connects to Lionstudy Server
            lionstudy = new Socket(server, port);
        //Sets output stream to out
            outStream = lionstudy.getOutputStream();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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
    public void IRC_getLog(String logName)
    {
        int i=0;
        do
        {
            IRC_getLogMessage(logName, i);
            
            
            i++;
        }
        while(true);
    }
    private void IRC_getLogMessage(String logName, int line)
    {
        msg="LOG "+logName+" "+line;
        send(msg);
    }
    
    boolean IRC_processMessage(String ircMessage)
    {
        IRC_RecievedMessage RcvMsg = IRC_MessageParser.recieved(ircMessage);
        if(RcvMsg.command.equals("PRIVMSG")||RcvMsg.command.equals("LOGMSG"))
        {
            System.out.println(RcvMsg.source+": "+RcvMsg.content);
            //receiver.receiveMSG(RcvMsg.source, RcvMsg.content);//Calls GUI's receive message function. (MIGHT BE IN WRONG PLACE. HEP)
        }
        else if(RcvMsg.command.equals("CLOSE"))
        {
            return false;
        }
        return true;
    }
       
    public void run()
    {
        do{
        //LOOP TO RECIEVE MESSAGES
            try
            {
                InputStream inStream = lionstudy.getInputStream();
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
    public boolean logLine()
    {
        boolean msgReceived = false;
        do{
        //LOOP TO RECIEVE MESSAGES
            try
            {
                InputStream inStream = lionstudy.getInputStream();
                IRC_MessageBuffer msgBuf = new IRC_MessageBuffer();

                byte[] buffer = new byte[1024];
                int bytes;

                do
                {
                    bytes=inStream.read(buffer);
                    if(bytes!=-1)
                    {
                        msgReceived=true;
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
        }while(!msgReceived);
        return true;
    }
}
