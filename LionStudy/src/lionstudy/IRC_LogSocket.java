package lionstudy;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

final class IRC_LogSocket implements Runnable
{
    Socket lionstudy;
    OutputStream outStream;
    //Replace this with private message?
    String channelJoined;
    //Used to clear field
    final static String CRLF = "\r\n";
    //Message to be sent to output stream
    String msg;
    String serv;
    int prt;
    
    IRC_LogSocket(String server, int port, String log)
    {
        serv=server;
        prt=port;
        try
        {
        //Connects to Lionstudy Server
            lionstudy = new Socket(server, port);
        //Sets output stream to out
            outStream = lionstudy.getOutputStream();
        //Sets channelJoined to log
            channelJoined=log;
        }
        catch(Exception e)
        {
            System.out.print(e);
        }
    }
    
    private void IRC_reset()
    {
        try
        {
            lionstudy.close();
            outStream.close();

            lionstudy = new Socket(serv,prt);
            outStream=lionstudy.getOutputStream();
        }
        catch(Exception e)
        {
            System.out.println(e);
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
            System.out.println(e);
        }
    }
    
    public void IRC_getLog(String logName)
    {
        boolean close=false;
        for(int i=1;!close;i++)
        {
            IRC_getLogMessage(logName, i);
            listen();
            close=IRC_processMessage(msg);
            IRC_reset();
            if(i>10)
                close=true;
        }
    }
    
    private void IRC_getLogMessage(String logName, int line)
    {
        msg="LOG "+logName+" "+line;
        send(msg);
    }
    
    //NOTE, we need to edit this to probably send message to output
    boolean IRC_processMessage(String ircMessage)
    {
        IRC_RecievedMessage RcvMsg = IRC_MessageParser.recieved(ircMessage);
        if(RcvMsg.command.equals("LOGMSG"))
        {
            System.out.println(RcvMsg.source+": "+RcvMsg.content);
        }
        else if(RcvMsg.command.equals("CLOSE"))
        {
            return true;
        }
        return false;
    }
       
    
    
    public void run()
    {
        IRC_getLog(channelJoined);
    }
    
    private void listen()
    {
        boolean msgReceived=false;
        do{
        //LOOP TO RECIEVE MESSAGE
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
                        msgReceived=true;
                    }
                    break;
                }while(bytes!=-1);
            }
            catch(Exception e)
            {
                System.out.print(e);
            }
        }while(!msgReceived);
    }
    
    
    
}
