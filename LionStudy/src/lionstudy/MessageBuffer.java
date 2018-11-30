/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lionstudy;

/**
 *
 * @author jjh5954
 */
public class MessageBuffer
{
    String buffer ="";
    String message="";
    
    public void addToBuffer (byte[] bytes)
    {
        buffer+=new String(bytes);
    }
    
    public boolean hasMessage()
    {
        if (buffer.contains("\r\n"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public String getMessage()
    {
        if(buffer.indexOf("\r\n")>-1)
        {
            message = buffer.substring(0,buffer.indexOf("\r\n"));
            buffer = buffer.substring(buffer.indexOf("\r\n")+2);
        }
        return message;
    }
    
    
    
    
    
}
